package server.messaging.pathos.com;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	public String userName;
	public String password;
	
	public User(String username, String password) {
		this.userName = username;
		this.password = password;
	}
	
	public String toString(){
		String s = userName  + " " + password;
		return s;
	}

}