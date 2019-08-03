// File Name GreetingClient.java
package client.messaging.pathos.com;

import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.io.*;

import server.messaging.pathos.com.Message;
import server.messaging.pathos.com.User;

public class MessagingClient {
	private static boolean exit;
	
	class RecvThread extends Thread {
		private Socket sockRecv;
		public RecvThread(Socket sock) {
			sockRecv = sock;
		}
	   
		public void run() {
			while (!exit) {
				try {
					// Receive messages from server
					ObjectInputStream ois = new ObjectInputStream(sockRecv.getInputStream());
					
					@SuppressWarnings("unchecked")
					ArrayList<Message> msgList = (ArrayList<Message>) ois.readObject();
					
					// Display received messages
					Iterator<Message> i = msgList.iterator();
					while (i.hasNext()) {
//						System.out.println(i.next());
						JOptionPane.showMessageDialog(null, i.next());
					}
		 
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		            
			}
			
			System.out.println(" Stopped."); 
		}
		
		public void terminate() {
	        exit = true;
	    }
	}

	class InputThread extends Thread {
		private Socket sockSend;
		private String userName;
		
		public InputThread(Socket sock, String user) {
			sockSend = sock;
			userName = user;
		}
	   
		public void run() {
			// Loop for sending message
			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
					
			while (!exit) {
				System.out.println("Message to:");
				String receiver = keyboard.nextLine();
				System.out.println("Enter message");
				String msg = keyboard.nextLine();
				Message message = new Message(userName, receiver, msg);
				
				// Send messages
				OutputStream outToServer;
				try {
					outToServer = sockSend.getOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(outToServer);
					out.writeObject(message);
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				// Terminate if user typed in 'exit'
//				if (msg.equals("exit")) {
//					t.terminate();
//					Thread.sleep(4000);
//					break;
//				} 				
			}
		}
		
		public void terminate() {
	        exit = true;
	    }
	}
	

	
	@SuppressWarnings("unchecked")
	public static void main(String [] args) {
		if (args.length < 2) {
			System.out.println("Host and port are required!");
			return;
		}
		
		MessagingClient p = new MessagingClient();
		
		String serverName = args[0];
		int port = Integer.parseInt(args[1]);
		
		System.out.println("Sever Name:" + serverName);
		System.out.println("Port Number:" + port);
		
		String userName;
		
		try {
			System.out.println("Connecting to " + serverName + " on port " + port);
			Socket sockServer = new Socket(serverName, port);
			System.out.println("Just connected to " + sockServer.getRemoteSocketAddress());
			
			while (true) {
				@SuppressWarnings("resource")
				Scanner in = new Scanner(System.in);
				System.out.println("User Name:");
				userName = in.nextLine();
				System.out.println("Password:");
				String password = in.nextLine();
				User user = new User(userName, password);
				
				// Send user info (name / password) to sever.
				OutputStream os = sockServer.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(user);
				oos.flush();
				
				// Receive login result
				InputStream is = sockServer.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				boolean rv = dis.readBoolean();
				
				if (rv) {
					// Receive offline messages
					ObjectInputStream ois = new ObjectInputStream(sockServer.getInputStream());
					
					ArrayList<Message> msgList;
					try {
						msgList = (ArrayList<Message>) ois.readObject();
						
						// Display received messages
						
						Iterator<Message> i = msgList.iterator();
						while (i.hasNext()) {
							//System.out.println(i.next());
							JOptionPane.showMessageDialog(null, i.next());
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
			}

			// Spawn a thread for receiving messages
			RecvThread t = p.new RecvThread(sockServer);
			t.start();

			InputThread t1 = p.new InputThread(sockServer, userName);
			t1.start();
			
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}