package edu.cornell.cs5300.project1b.rpc.message;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.util.SerializeUtils;

/**
 * A message sent through the Remote Procedure Call (RPC) service.
 * <br>
 * See {@link RPCMessage.Type} for message semantics.
 * @author gus
 *
 */
public class RPCMessage {
	
	/*
	 * RPC MESSAGE SCHEMA
	 * 
	 * [1 byte | message_type]
	 * [4 bytes | payload_length]
	 * [remainder | message_payload]
	 * 
	 */
	
	private Type type;
	private byte[] payload;
	
	public static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.RPCMessage";
	
	/**
	 * Constructs a {@code RPCMessage} with the given type and payload.
	 * 
	 * @param t type of this {@code RPCMessage}
	 * @param p payload for this {@code RPCMessage}
	 */
	public RPCMessage (Type t, byte[] p) {
		type = t;
		payload = p;
	}
	
	/**
	 * Constructs a RPCMessage from the given byte stream.
	 * 
	 * The byte stream must have the following format:
	 * <ul>
	 * <li>1 byte: RPCMessage.Type (see {@link Type#byteValue()})</li>
	 * <li>4 bytes: payload length</li>
	 * <li>remainder: payload</li>
	 * </ul>
	 * 
	 * @param b
	 * @see #serialize()
	 */
	public RPCMessage (byte[] b) {
		//one byte for Type
		byte b_type = b[0];
		type = Type.parse(b_type);
		
		//four bytes for payload length
		byte[] b_length = new byte[4];
		System.arraycopy(b, 1, b_length, 0, 4);
		int length = SerializeUtils.byteArrayToInt(b_length);
		
		//remaining bytes for payload
		payload = new byte[length];
		System.arraycopy(b, 5, payload, 0, length);
	}
	
	/**
	 * Constructs a serialized representation of this RPCMessage. 
	 * 
	 * The byte stream consists of three fields, in this order:
	 * <ul>
	 * <li>1 byte: RPCMessage.Type (see {@link Type#byteValue()})</li>
	 * <li>4 bytes: payload length</li>
	 * <li>remainder: payload</li>
	 * </ul>
	 * 
	 * @return byte stream representation of this RPCMessage
	 */
	public byte[] serialize () {
		//need enough space for Type (1 byte), length (4 bytes), and payload
		int stream_length = 1 + 4 + payload.length;
		
		//enforce maximum message size
		if (payload.length + 5 > Constants.MAX_MESSAGE_SIZE) {
			throw new IllegalStateException
				(fname + "#serialize: message length exceeds "
						+ Constants.MAX_MESSAGE_SIZE + "bytes");
		}
				
		byte[] stream = new byte[stream_length];
		
		byte[] b_length = SerializeUtils.intToByteArray(payload.length);
		
		stream[0] = type.byteValue();
		System.arraycopy(b_length, 0, stream, 1, 4);
		System.arraycopy(payload, 0, stream, 5, payload.length);
		
		return stream;
	}
	
	/**
	 * @return the Type of this RPCMessage.
	 */
	public Type type () {
		return type;
	}
	
	/**
	 * @return this RPCMessage's payload
	 */
	public byte[] payload () {
		return payload;
	}
	
	/**
	 * The four types of RPCMessages that can be sent.
	 * <ul>
	 * <li>DATA_REQUEST(SessionId): requests the user data for 
	 * the given SessionId from the recipient</li>
	 * <li>DATA_RESPONSE(SessionId, UserData): gives the UserData for the 
	 * requested SessionId</li>
	 * <li>PUSH_REQUEST(SessionId, UserData): requests that the recipient 
	 * store the UserData for the given SessionId</li>
	 * <li>PUSH_RESPONSE(SessionId): indicates that the recipient has stored 
	 * the given information for SessionId</li>
	 * </ul>
	 * 
	 * @author gus
	 *
	 */
	public enum Type {
		DATA_REQUEST,
		DATA_RESPONSE,
		PUSH_REQUEST,
		PUSH_RESPONSE;
		
		/**
		 * Returns a single byte representation of this Type.
		 * <br><br>
		 * The byte representations are as follows:
		 * <ul>
		 * <li>DATA_REQUEST: 0</li>
		 * <li>DATA_RESPONSE: 1</li>
		 * <li>PUSH_REQUEST: 2</li>
		 * <li>PUSH_RESPONSE: 3</li>
		 * </ul>
		 * 
		 * @return byte representation of this Type
		 * @see #parse(byte)
		 */
		public byte byteValue () {
			switch (this) {
			case DATA_REQUEST:
				return (byte) 0;
			case DATA_RESPONSE:
				return (byte) 1;
			case PUSH_REQUEST:
				return (byte) 2;
			case PUSH_RESPONSE:
				return (byte) 3;
			default:
				throw new IllegalStateException
					(fname + ".Type#byteValue: unrecognized type");
			}
		}
		
		/**
		 * Parses the given byte Type representation into the appropriate Type.
		 * 
		 * The byte representations follow:
		 * <ul>
		 * <li>0: DATA_REQUEST</li>
		 * <li>1: DATA_RESPONSE</li>
		 * <li>2: PUSH_REQUEST</li>
		 * <li>3: PUSH_RESPONSE</li>
		 * </ul>
		 * 
		 * @param b to be parsed
		 * @return the parsed Type
		 * @see #byteValue()
		 */
		public static Type parse (byte b) {
			if (b == (byte) 0) 
				return DATA_REQUEST;
			if (b == (byte) 1)
				return DATA_RESPONSE;
			if (b == (byte) 2)
				return PUSH_REQUEST;
			if (b == (byte) 3)
				return PUSH_RESPONSE;
			throw new IllegalArgumentException
				(fname + ".Type#parseType: unrecognized byte '" + b + "'");
		}
		
		public String toString () {
			switch (this) {
			case DATA_REQUEST:
				return "DATA_REQUEST";
			case DATA_RESPONSE:
				return "DATA_RESPONSE";
			case PUSH_REQUEST:
				return "PUSH_REQUEST";
			case PUSH_RESPONSE:
				return "PUSH_RESPONSE";
			default:
				throw new IllegalStateException
					(fname + ".Type#toString: unrecognized type");
			}
		}
	}

}
