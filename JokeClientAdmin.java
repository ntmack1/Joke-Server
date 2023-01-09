/*--------------------------------------------------------
1. Name / Date: Nathan Mack / 9-25-2022

2. Java version used (Amazon Corretto\jdk17.0.1)

3. Precise command-line compilation examples / instructions:

> javac JokeClientAdmin.java

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
import java.io.*;
import java.net.*;

public class JokeClientAdmin {
	public static void main (String[] args) {
		String serverName;
		//If no alternative server is given on command line, default to local host
		if (args.length < 1) serverName = "localhost";
		else serverName = args[0];

		System.out.println("Nathan Mack's Joke Admin Client, 1.17.\n");
		System.out.println("Using server: " + serverName + ", Port: 5050");
		
		//create buffer to read user input
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		try {
			String name;
			while (true) {
				//flushes the stream
				System.out.flush();
				//takes user input
				name = input.readLine ();
				//Quits the loop if "quit" is typed
				if (name.contains("quit")) 
					break;
				toggleServer(serverName);
				}
			System.out.println ("Cancelled by user request.");
		} catch (IOException x) {x.printStackTrace ();}
	}

	private static void toggleServer (String serverName){
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		//create buffer to read user input
		try{
			// Establishes a connection to the server
			sock = new Socket(serverName, 5050);
			// Input output for sockets
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
			toServer.flush();
			
			
			String inJokeMode = fromServer.readLine();
			if (inJokeMode.contains("true")) {
				System.out.println("Currently in Joke mode. \nPress <enter> to toggle to Proverb mode or type \"quit\"");
				}
				
			else {
				System.out.println("Currently in Proverb mode. \nPress  <enter> to toggle to Joke mode or type \"quit\"");
				}
	
			for (int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine();

				//if string not empty, print
				if (textFromServer != null) {
					System.out.println(textFromServer);
					}
			}
			sock.close();
		} catch (IOException x) {
			System.out.println ("Socket error.");
			x.printStackTrace ();
		}
	}
	
}