package server.messaging.pathos.com;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ClientThread extends Thread {
	private Socket clientSocket;
	private ArrayList<User> alUsers;
	private boolean login;
	

	public ClientThread(Socket client, ArrayList<User> al) {
		clientSocket = client;
		alUsers = al;
	}
   
	boolean checkPassword(User user) {
		for (int i = 0; i < alUsers.size(); i++) {
			User selected = alUsers.get(i);
			if (selected.userName.equals(user.userName)) {
				if (selected.password.equals(user.password)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void run() {
		User user;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		DataOutputStream dos;		
		
		while (true) {
			try {
				if (!login) {
					// Receive user/password from client.
					ois = new ObjectInputStream(clientSocket.getInputStream());
					user = (User) ois.readObject();
					
					// Check password
					if (checkPassword(user)) {						
						// Set thread's name with user's
						this.setName(user.userName);
						
						// Set login flag to true
						login = true;
						
						// Send true to client
						dos = new DataOutputStream(clientSocket.getOutputStream());
						dos.writeBoolean(true);
						
						// Send offline messages
						ArrayList<Message> msgList = new ArrayList<Message>();
						
						for (int i = 0; i < MessagingServer.messagePool.size(); i++) {
							Message msg = MessagingServer.messagePool.get(i);
							String receiver = msg.receiver;
							
							if (receiver.equals(this.getName())) {
								msgList.add(msg);
								MessagingServer.messagePool.remove(i); // Remove it from pool
							}
						}
						
						oos = new ObjectOutputStream(clientSocket.getOutputStream());
						oos.writeObject(msgList);
						oos.flush();
					}                     
	                else {
	                	dos = new DataOutputStream(clientSocket.getOutputStream());
	                	dos.writeBoolean(false);
	    				continue;
	                }
				}
				
				// Receive message from client
				ois = new ObjectInputStream(clientSocket.getInputStream());
				Message message = (Message) ois.readObject();
				
				// Check if it's exit message
				if (message.msg.equals("exit")) {
					clientSocket.close();
					System.out.println("I quit.");
					break;
				}
				
				// Add the message to pool
				MessagingServer.messagePool.add(message);
				
				// Send messages if they are in pool
				ArrayList<Message> msgList = new ArrayList<Message>();
				
				for (int i = 0; i < MessagingServer.messagePool.size(); i++) {
					Message msg = MessagingServer.messagePool.get(i);
					String receiver = msg.receiver;
					
					if (receiver.equals(this.getName())) {
						msgList.add(msg);
						MessagingServer.messagePool.remove(i); // Remove it from pool
					}
				}
				
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.writeObject(msgList);
				oos.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
}