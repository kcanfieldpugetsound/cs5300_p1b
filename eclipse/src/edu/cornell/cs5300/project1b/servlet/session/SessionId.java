package edu.cornell.cs5300.project1b.servlet.session;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.util.SerializeUtils;

/**
 * Representation of a globally unique {@code Session} identifier.
 * The ID of the server, the number of times the server has rebooted, the 
 * local session number, and a version number are combined to produce such a
 * globally unique ID.
 * <br>
 * Sessions timeout {@link Constants#SESSION_TIMEOUT_MILLISECONDS}
 * milliseconds after creation. A {@code Session} is only guaranteed to be
 * available before its timeout, but is not guaranteed to be removed
 * at exactly its timeout.
 * 
 * @author gus
 *
 */
public class SessionId {
	
	private IPAddress serverId;
	private int sessionNum;
	private int rebootId;
	private int versionNum;
	
	/**
	 * The time, in milliseconds, at which this session may be discarded.
	 */
	private long timeout;
	
	/**
	 * The number of bytes in the serialized representation of this SessionId.
	 * 
	 * @see #serialize()
	 */
	public static final int SERIALIZED_LENGTH = 16;
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.SessionId";
	
	/**
	 * Constructs a SessionId with the given components.
	 * 
	 * @param server IPAddress of this SessionId's server
	 * @param reboot the number of times this SessionId's server has rebooted
	 * @param session the local session number of this SessionId's server
	 * @param verison the version number of this SessionId
	 */
	public SessionId (IPAddress server, int reboot, int session, int version) {
		serverId = server;
		rebootId = reboot;
		sessionNum = session;
		versionNum = version;
		timeout = Constants.SESSION_TIMEOUT_MILLISECONDS + System.currentTimeMillis();
	}
	
	/**
	 * Constructs a SessionId from the provided byte stream, which must be 
	 * {@link #SERIALIZED_LENGTH} bytes and formatted in 4-byte chunks 
	 * as follows.
	 * <br> <br>
	 * Chunk 1: serverId <br>
	 * Chunk 2: rebootId <br>
	 * Chunk 3: sessNum <br>
	 * Chunk 4: versionNum <br>
	 * 
	 * @param in
	 * @see #serialize()
	 */
	public SessionId (byte[] in) {
		if (in.length != SERIALIZED_LENGTH) {
			throw new IllegalArgumentException(fname + "#init: in must be " + 
				SERIALIZED_LENGTH + " bytes long");
		}
		
		byte[] b_serverId = new byte[4];
		byte[] b_rebootId = new byte[4];
		byte[] b_sessNum = new byte[4];
		byte[] b_versionNum = new byte[4];
		
		System.arraycopy(in, 0, b_serverId, 0, 4);
		System.arraycopy(in, 4, b_rebootId, 0, 4);
		System.arraycopy(in, 8, b_sessNum, 0, 4);
		System.arraycopy(in, 12, b_versionNum, 0, 4);
		
		serverId = new IPAddress(b_serverId);
		rebootId = SerializeUtils.byteArrayToInt(b_rebootId);
		sessionNum = SerializeUtils.byteArrayToInt(b_sessNum);
		versionNum = SerializeUtils.byteArrayToInt(b_versionNum);
		
		timeout = Constants.SESSION_TIMEOUT_MILLISECONDS + System.currentTimeMillis();
	}
	
	/**
	 * 
	 * @return the long value of the timeout time of the session
	 */
	public long timeout () {
		return timeout;
	}
	
	/**
	 * @return IPAddress of this SessionId's server
	 */
	public IPAddress serverId () {
		return serverId;
	}
	
	/**
	 * @return session number of this SessionId
	 */
	public int sessNum () {
		return sessionNum;
	}
	
	/**
	 * @return number of times this SessionId's server has rebooted
	 */
	public int rebootId () {
		return rebootId;
	}
	
	/**
	 * @return the version number of this SessionId
	 */
	public int versionNum () {
		return versionNum;
	}
	
	/**
	 * @return whether or not this session has expired
	 */
	public boolean isExpired () {
		return System.currentTimeMillis() > timeout;
	}
	
	/**
	 * Constructs a {@link #SERIALIZED_LENGTH}-byte serialized representation of this SessionId.
	 * 
	 * The byte stream consists of four 4-byte fields in this order:
	 * <ul>
	 * <li>serverId</li>
	 * <li>rebootId</li>
	 * <li>sessNum</li>
	 * <li>versionNum</li>
	 * </ul>
	 * 
	 * @return serialized representation of this SessionId
	 */
	public byte[] serialize () {
		byte[] result = new byte[SERIALIZED_LENGTH];

		byte[] b_serverId = serverId().getBytes();
		byte[] b_rebootId = SerializeUtils.intToByteArray(rebootId());
		byte[] b_sessNum = SerializeUtils.intToByteArray(sessNum());
		byte[] b_versionNum = SerializeUtils.intToByteArray(versionNum());
		
		System.arraycopy(b_serverId, 0, result, 0, 4);
		System.arraycopy(b_rebootId, 0, result, 4, 4);
		System.arraycopy(b_sessNum, 0, result, 8, 4);
		System.arraycopy(b_versionNum, 0, result, 12, 4);
		
		return result;
	}
	
	/**
	 * Returns a String representation of this SessionId.
	 * Strings are formatted as follows: <br>
	 * <code>
	 * &lt;SessionId [serverId().toString(),sessNum(),rebootId(),versionNum()]&gt;
	 * </code>
	 * 
	 * @return formatted String representation of this SessionId
	 */
	public String toString () {
		return "<SessionId [" + serverId().toString() + "," + 
				sessNum() + "," + rebootId() + "," + versionNum() + "]>";
	}
	
	/**
	 * Returns a String representation of this SessionId without version number.
	 * Strings are formatted as follows: <br>
	 * <code>
	 * &lt;SessionId [serverId().toString(),sessNum(),rebootId()]&gt;
	 * </code>
	 * 
	 * @return formatted String representation of this SessionId
	 */
	public String toStringWithoutVersion () {
		return "<SessionId [" + serverId().toString() + "," + 
				sessNum() + "," + rebootId() + "]>";
	}


	/**
	 * Compares this SessionId to {@code other}. Returns whether or not
	 * {@code this} is an earlier version of {@code other}.
	 * 
	 * @param other session to compare to
	 * @return whether or not {@code this} is an earlier version of 
	 * {@code other}
	 */
	public boolean predates (SessionId other) {
		return 
			serverId().equals(other.serverId()) && 
			sessNum() == other.sessNum() &&
			rebootId () == other.rebootId() &&
			versionNum () < other.versionNum();
			
	}
	
	/**
	 * Compares this SessionId to {@code other}.
	 * Two SessionIds are equal iff all three of their components are equal. 
	 * Note that the serverId()s are checked for structural equality.
	 * 
	 * @param other object to compare {@code this} to.
	 * @return whether or not {@code this} equals {@code other}
	 */
	public boolean equals (Object o) {
		if (!(o instanceof SessionId)) return false;
		SessionId other = (SessionId) o;
		return 
			serverId().equals(other.serverId()) &&
			sessNum() == other.sessNum() &&
			rebootId() == other.rebootId() && 
			versionNum() == other.versionNum();
	}

}
