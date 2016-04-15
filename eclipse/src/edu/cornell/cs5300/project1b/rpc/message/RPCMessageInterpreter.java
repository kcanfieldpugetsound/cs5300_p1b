package edu.cornell.cs5300.project1b.rpc.message;

import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.servlet.session.UserData;

/**
 * A class for interpreting {@link RPCMessage}s and extracting information 
 * from them.
 * 
 * @author gus
 *
 */
public class RPCMessageInterpreter {
	
	private RPCMessage message;
	
	private SessionId sessionId;
	private UserData userData;
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.RPCMessageInterpreter";
	
	/**
	 * Constructs a RPCMessageInterpreter object for interpreting {@code m}.
	 * <br>
	 * DATA_REQUEST and PUSH_RESPONSE messages have only a {@link SessionId},
	 * whereas 
	 * <br>
	 * DATA_RESPONSE and PUSH_REQUEST messages have both a
	 * {@link SessionId} and {@link UserData}
	 * <br>
	 * See {@link RPCMessage.Type} for message semantics.
	 * 
	 * @param m the message to be interpreted
	 * 
	 */
	public RPCMessageInterpreter (RPCMessage m) {
		message = m;
		
		//messages with only a SessionId
		if (message.type() == RPCMessage.Type.DATA_REQUEST ||
			message.type() == RPCMessage.Type.PUSH_RESPONSE) {
			
			byte[] b_sessionId = m.payload();
			sessionId = new SessionId(b_sessionId);
			
		} 
		//messages with both SessionId and UserData
		else if (message.type() == RPCMessage.Type.DATA_RESPONSE ||
				message.type() == RPCMessage.Type.PUSH_REQUEST) {
			
			byte[] payload = message.payload();
			
			int b_userData_length = 
				payload.length - SessionId.SERIALIZED_LENGTH;
			
			byte[] b_sessionId = new byte[SessionId.SERIALIZED_LENGTH];
			byte[] b_userData = new byte[b_userData_length];
			
			System.arraycopy(payload, 0, 
				b_sessionId, 0, SessionId.SERIALIZED_LENGTH);
			System.arraycopy(payload, SessionId.SERIALIZED_LENGTH, 
				b_userData, 0, b_userData_length);
			
			sessionId = new SessionId(b_sessionId);
			userData = new UserData(b_userData);
			
		} 
		//what is this madness...
		else {
			throw new IllegalStateException
				(fname + "#init: unknown RPCMessage.Type");
		}
		
	}
	
	/**
	 * @return the type of the interpreted message
	 */
	public RPCMessage.Type type () {
		return message.type();
	}
	
	/**
	 * Returns the interpreted SessionId from the RPCMessage. 
	 * All types of RPCMessages have SessionIds.
	 * 
	 * @return the SessionId from the interpreted message
	 */
	public SessionId sessionId () {
		return sessionId;
	}
	
	/**
	 * Returns the interpreted UserData from the RPCMessage.
	 * Only DATA_RESPONSE and PUSH_REQUEST messages have UserData, so this
	 * method must only be called with those types of messages.
	 * <br>
	 * See {@link RPCMessage.Type} for message semantics.
	 * 
	 * @return
	 */
	public UserData userData () {
		//these types of messages don't have UserData!
		if (message.type() == RPCMessage.Type.DATA_REQUEST ||
			message.type() == RPCMessage.Type.PUSH_RESPONSE) {
			throw new UnsupportedOperationException
				(fname + "#userData: " + type() + 
					" messages do not contain UserData");
		}
		
		return userData;
	}

}
