/*--------------------------------------------------------
1. Name / Date: Nathan Mack / 9-25-2022

2. Java version used (Amazon Corretto\jdk17.0.1)

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java

4. Precise examples / instructions to run this program:

In separate shell windows run:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. JokeServer.java
 b. JokeClient.java
 c. JokeClientAdmin.java
----------------------------------------------------------*/
import java.io.*; //Input and Output library using streams
import java.net.*; // Library for making threads
import java.util.*; //Library needed for dictionary

//establishes connection with clients, then creates a thread to handle client request
public class JokeServer {
	public static void main(String a[]) throws IOException {
		//creates thread that will later be used to connect to JokeClientAdmin
		adminHandler admin = new adminHandler();
		Thread handler = new Thread(admin);
		handler.start();
		
		int q_len = 6; //Number of requests held in queue while waiting for a connection
		int port = 4545; //Port number required by assignment for JokeClients
		Socket sock;
		//creates an initial socket that will be used to connect with JokeClient
		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("Nathan Mack's Joke server 1.17 starting up, listening at port 4545.\n");
		//continually listens for clients, creates a new thread for each new socket
		while (true) {
			sock = servsock.accept();
			new clientHandler(sock).start(); 
		}
	}
}

//Assists adminThreadHandler
class adminHandler implements Runnable {
	public void run() {
		int port = 5050; //Port number require by assignment for JokeClientServer
		int q_len = 6; //Number of requests held in queue while waiting for a connection
		Socket sock;
		try {
			//Creates a socket that will be used to connect with JokeClientAdmin
			ServerSocket servsock = new ServerSocket(port, q_len);
			//continually listen for JokeClientAdmin creates a new thread for each new socket
			while (true) {
				sock = servsock.accept();
				new adminThreadHandler(sock).start(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//Handles JokeClientAdmin thread
class adminThreadHandler extends Thread {
	Socket sock; // Class variable
	adminThreadHandler (Socket s) {sock = s;} // Constructor method
	public void run() {
		//Input output for socket
		PrintStream out = null;
		BufferedReader in = null;
		try {
			//Read from client 
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//Write to client 
			out = new PrintStream(sock.getOutputStream());

			//toggles between jokeMode and !jokeMode(proverbMode)
			global.inJokeMode = !global.inJokeMode;
			
			//Informs JokeClientAdmin of the state of the server (joke or proverb)
			if (global.inJokeMode) {
				out.println("true");
			}
			else {
				out.println("false");
			}
			sock.close(); 
		} catch (IOException ioe) {System.out.println(ioe);}
	}
}

//Client Thread Handler
class clientHandler extends Thread { 
	Socket sock; // Class variable
	clientHandler (Socket s) {sock = s;} // Constructor method
	
	//This method performs the bulk of the program
	public void run() {
		// input and output for socket
		PrintStream out = null;
		BufferedReader in = null;
		try {
			//Read from client 
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//Write to client 
			out = new PrintStream(sock.getOutputStream());

			try {
				//reads from buffer, stores in "userName"
				String userName = in.readLine ();
				//reads from buffer, stores in "id"
				String id = in.readLine ();
				//converts string to int
				int uniqueId = Integer.parseInt(id);
				/*create an array that will be used to determine if a joke or proverb has been seen 
				 using 0 to represent not seen and 1 to represent seen. First four indices are for
				 jokes and the last four indices are for proverbs. 
				*/
				int[] startState = {0,0,0,0,0,0,0,0};
				
				//Creates a new dictionary entry if id is new
				if (!(global.idDict.containsKey(uniqueId))) {
					global.idDict.put(uniqueId, startState );
				}
				
				//extract the saved state from dictionary into ArrayList
				int[] state = global.idDict.get(uniqueId);
				
				//Toggling between joke and proverb mode using if/else and global class variable
				//Code block for Joke Mode
				if (global.inJokeMode) {

					//Check if saved state is already full of 1's (have seen all jokes), reset if this is the case
					int numberOfOnes = 0;
					for (int i = 0; i < 4; i++) {
						if (state[i] == 1) 
							numberOfOnes += 1;
					}
					//determines when all jokes have been told in a given cycle. If so, reset Joke cycle.
					if (numberOfOnes == 4) { 
						for (int i = 0; i < 4; i++) {
							state[i] = 0; 
							}
						//informs user that cycle has completed
						out.println("JOKE CYCLE COMPLETED \n");
						}
					//generate random number between 0 and 3
					Random rand = new Random();
					int index = rand.nextInt(4);
					//use random number to index "state", keep looping until we get a 0 which means the joke hasn't been seen yet
					while (state[index] == 1) {
						index = rand.nextInt(4);
					}
					state[index] = 1;
					//set joke to seen
					global.idDict.put(uniqueId, state);
					//invoke static method that will tell jokes or proverbs depending on what was determined above
					String output = jokeAndProverbTeller(userName, index);
					//print output to client
					out.println(output);
				}
				//Code block for Proverb Mode
				else {
					//Check if saved state is already full of 1's (have seen all proverbs), reset if this is the case
					int numberOfOnes = 0;
					for (int i = 4; i < 8; i++) {
						if (state[i] == 1) 
							numberOfOnes += 1;
					}
					//determines when all proverbs have been told in a given cycle, if so, reset proverb cycle.
					if (numberOfOnes == 4) {
						for (int i = 4; i < 8; i++) {
							state[i] = 0; 
						}
						//informs user that cycle has completed
						out.println("PROVERB CYCLE COMPLETED \n");
					}
					//generate random number between 4 and 8
					Random rand = new Random();
					int index = rand.nextInt(4) + 4;
					//use random number to index "state", keep looping until we get a 0 which means the joke hasn't been seen yet
					while (state[index] == 1) {
						index = rand.nextInt(4) + 4;
					}
					state[index] = 1;
					//set joke to seen
					global.idDict.put(uniqueId, state);
					//invoke static method that will tell jokes or proverbs depending on what was determined above
					String output = jokeAndProverbTeller(userName, index);
					//print output to client
					out.println(output);	
				}
				
			} catch (IOException x) {
				System.out.println("Server read error");
				x.printStackTrace ();
			}
			//close socket
			sock.close(); 
		} catch (IOException ioe) {System.out.println(ioe);}
	}
	//This method returns jokes and proverbs depending on value of the index passed to it
	public static String jokeAndProverbTeller (String userName, int index) {
		//Array of Jokes
		String[] Jokes = 
			{
			"JA <" + userName + ">: There are 10 types of people: those who understand binary, and those who don't.", 
			"JB <" + userName + ">: Why can't you trust an atom? They make up everything.",
			"JC <" + userName + ">: What do you call an organic compound with an attitude? A-mean-o acid.",
			"JD <" + userName + ">: How do you keep warm in a cold room? You go to the corner because it's always 90 degrees."
			};
		//Array of Proverbs
		String[] Proverbs = 
			{
			"PA <" + userName + ">: The supreme art of war is to subdue the enemy without fighting.", 
			"PB <" + userName + ">: Opportunities multiply as they are seized.",
			"PC <" + userName + ">: Half of seeming clever is keeping your mouth shut at the right times.",
			"PD <" + userName + ">: Love your Enemies, for they tell you your Faults."
			};
		//Index Joke or Proverb array depending on value of index 
		if (index < 4) {
			return Jokes[index];
		}
		return Proverbs[index - 4];
	}
}

/*Array keeps the state of all clients inside a dictionary that is visible to all threads
 * Keys are the unique id for each client. The values are an array of size 8. Each index in the 
 * array hold either a 0 or 1 depending on if the joke/proverb has been seen or not.
 * 0 indicates joke/proverb has not yet been seen. 1 indicates joke/proverb has been seen.
 */
class global{
	public static Map<Integer, int[]> idDict = new HashMap<>();
	public static Boolean inJokeMode = true;
	}

	

