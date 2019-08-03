// File Name GreetingServer.java
package server.messaging.pathos.com;

import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.io.*;

public class MessagingServer {
	final static int portNum = 7001; 
	final static int numThreads = 20;
	static ExecutorService pool;
	static Semaphore sem = new Semaphore(10);
	
	public static ArrayList<Message> messagePool = new ArrayList<Message>();
	
	static void loadUsers(ArrayList<User> us) {
		String s; 
		StringTokenizer t;
		try {
			String curPath = System.getProperty("user.dir");
			FileReader fr = new FileReader(curPath + "\\password.txt");
			BufferedReader in = new BufferedReader(fr);
			s = in.readLine();
			
			while (s != null) {
				t = new StringTokenizer(s);
				User user = new User(t.nextToken(), t.nextToken());
				us.add(user);
				s = in.readLine();
			}
			
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// Read user list from file
		ArrayList<User> us = new ArrayList<User>();
		loadUsers(us);
		pool = Executors.newFixedThreadPool(numThreads);
		try {
			ServerSocket sockServer = new ServerSocket(portNum);
			while (true) {  
				sem.acquire();
				Socket socket = sockServer.accept();
				pool.submit(new ClientThread(socket, us));
			}
			
		} catch(IOException e) {}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}