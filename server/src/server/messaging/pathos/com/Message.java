package server.messaging.pathos.com;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String sender;
	public String receiver;
	public String msg;
	
	public Message(String sender, String receiver, String msg) {
		this.sender = sender;
		this.receiver = receiver;
		this.msg = msg;
	}
	
	public String toString(){
		String s = sender  + " " + receiver  + " " + msg;
		return s;
	}

}