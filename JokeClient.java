/*--------------------------------------------------------
1. Name / Date: Nathan Mack / 9-25-2022

2. Java version used (Amazon Corretto\jdk17.0.1)

3. Precise command-line compilation examples / instructions:

> javac JokeClient.java

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

6. Notes: Did not create a true UUID. I generated a number between 0 and 2 million.
----------------------------------------------------------*/
import java.io.*;
import java.net.*;

public class JokeClient {
	public static void main (String[] args) {
		String userName;
		int uniqueId = (int)(Math.random()*2000000);
		String serverName;
		//If no alternative server is given on command line, default to local host
		if (args.length < 1) serverName = "localhost";
		else serverName = args[0];

		System.out.println("Nathan Mack's Joke Client, 1.17.\n");
		System.out.println("Using server: " + serverName + ", Port: 4545");
		
		//create buffer to read user input
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please enter your name: ");
		try {
			//gets name from user
			userName = input.readLine ();
			//flushes stream
			System.out.flush();
			String name;
			while (true) {
				System.out.println("\nPress <enter> key or type \"quit\"");
				//flushes the stream
				System.out.flush();
				//takes user input
				name = input.readLine ();
				//Quits the loop if "quit" is typed
				if (name.contains("quit")) 
					break;
				contactServer(serverName, userName, uniqueId);
				}
			System.out.println ("Cancelled by user request.");
		} catch (IOException x) {x.printStackTrace ();}
	}

	private static void contactServer (String serverName, String userName, int uniqueId){
		Socket sock;
		BufferedReader fromServer; //Buffer allows us to read from JokeServer
		PrintStream toServer; //Allows us to print to JokeServer
		String textFromServer;
		try{
			// Establishes a connection to the server
			sock = new Socket(serverName, 4545);
			// Input output for sockets
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
			toServer.println(userName); 
			toServer.println(uniqueId);
			toServer.flush();
			
			for (int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine();

				//if string not empty, print
				if (textFromServer != null) {
					System.out.println(textFromServer);
				}
			}
			//close socket
			sock.close();
		} catch (IOException x) {
			System.out.println ("Socket error.");
			x.printStackTrace ();
		}
	}
	
}

