package edu.cornell.cs5300.project1b.servlet.session;

import edu.cornell.cs5300.project1b.util.SerializeUtils;

/**
 * A class for holding a user's data.
 * 
 * @author gus
 *
 */
public class UserData {

	String message; //the user message
	byte[] msgToBytes; //the user message converted to a byte[]
	
	/**
	 * Constructs a UserData object containing the given message.
	 * 
	 * @param message the user message as a String
	 * @see #serialize()
	 */
	public UserData (String message) {
		this.message = message;
		msgToBytes = SerializeUtils.stringToByteArray(message);
		
	}
	
	/**
	 * Constructs a UserData object from its serialized representation.
	 * 
	 * @param data serialized representation of UserData object
	 * @see #serialize()
	 */
	public UserData (byte[] data) {
		msgToBytes = data;
		message = SerializeUtils.byteArrayToString(data);
	}
	
	/**
	 * @return serialized representation of this UserData object
	 */
	public byte[] serialize () {
		return msgToBytes;
	}
	
	/**
	 * @return the user message
	 */
	public String getMessage(){
		return message;
	}
	
	

}
