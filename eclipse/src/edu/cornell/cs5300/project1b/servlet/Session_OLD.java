package edu.cornell.cs5300.project1b.servlet;

public class Session_OLD {
	private int version;
	private String message, expiration;
	
	//The value of the hashtable is called a session
	//which has 3 attributes, <version, message, expiration date>
	//these are indexed by sessionId, which is unique to a session (a browser with two tabs open seem to share cookies, even in private mode, so I haven't handled
	//for that). But two separate browsers don't have the same sessionId
	
	//there is a toString to help with print statements
	
	
	public Session_OLD(int v, String m, String e){
		version = v;
		message = m;
		expiration = e;
	}
	
	public void setMessage(String m){
		message = m;
	}
	
	public void setExpiration(String e){
		expiration = e;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getExpiration(){
		return expiration;
	}
	
	public int getVersion(){
		return version;
	}
	
	public void setVersion(int v){
		version = v;
	}
	
	public String toString(){
		return "\tversion: " + version + "\tmessage: " + message + "\texpiration: " + expiration;
	}
}
