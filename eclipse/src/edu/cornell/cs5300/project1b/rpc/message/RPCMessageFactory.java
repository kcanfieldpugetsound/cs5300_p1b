package edu.cornell.cs5300.project1b.rpc.message;

import edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.servlet.session.UserData;

/**
 * A collection of methods for producing {@link RPCMessage}s.
 * 
 * @author gus
 *
 */
public class RPCMessageFactory {
	
	private static final String fname = "edu.cornell.cs5300.project1b.rpc.RPCMessageFactory";
	
	/**
	 * Creates a RPCMessage of type {@code DATA_RESPONSE} and 
	 * {@code PUSH_REQUEST}, both of which contain both a SessionId and UserData.
	 * <br>
	 * See {@link RPCMessage.Type} for RPCMessage semantics.
	 * 
	 * @param t type of this message
	 * @param id session information to include in this message
	 * @param data user data to include in this message
	 * @return message constructed from the given information
	 */
	public static RPCMessage createRPCMessage (RPCMessage.Type t, SessionId id, UserData data) {
		if (t != Type.DATA_RESPONSE && t != Type.PUSH_REQUEST) {
			throw new IllegalArgumentException
				(fname + "#init: only DATA_RESPONSE and PUSH_REQUEST take "
					+ "both SessionId and UserData");
		}
		
		RPCMessage.Type type = t;
		
		//both fields required for these message Types
		byte[] b_id = id.serialize();
		byte[] b_data = data.serialize();
		
		byte[] payload = new byte[b_id.length + b_data.length];
		
		System.arraycopy(b_id, 0, payload, 0, b_id.length);
		System.arraycopy(b_data, 0, payload, b_id.length, b_data.length);
		
		return new RPCMessage(type, payload);
	}
	
	/**
	 * Constructs a RPCMessage of type {@code DATA_REQUEST} and 
	 * {@code PUSH_RESPONSE}, both of which only contain a SessionId.
	 * <br>
	 * See {@link RPCMessage.Type} for RPCMessage semantics.
	 * 
	 * @param t type of this message
	 * @param id session information to include in this message
	 * @return message constructed from the given information
	 */
	public static RPCMessage createRPCMessage (RPCMessage.Type t, SessionId id) {
		if (t != Type.DATA_REQUEST && t != Type.PUSH_RESPONSE) {
			throw new IllegalArgumentException
				(fname + "#init: only DATA_REQUEST and PUSH_RESPONSE take "
					+ "only a SessionId");
		}
		
		RPCMessage.Type type = t;
		
		//only SessionId required for these message Types
		byte[] payload = id.serialize();
		
		return new RPCMessage(type, payload);
	}

}
