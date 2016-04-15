package edu.cornell.cs5300.project1b;

import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A wrapper for an IP Address, useful for storing server IP addresses.
 * 
 * @author gus
 *
 */
public class IPAddress {
	
	private byte[] bytes;
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.IPAddress";
	
	/**
	 * Constructs an IPAddress from a source byte array {@code ip}.
	 * {@code ip} must be of length 4.
	 * @param ip source byte array
	 */
	public IPAddress (byte[] ip) {
		if (ip.length != 4) {
			throw new IllegalArgumentException
				(fname + "#init: input array must have length 4");
		}
		System.arraycopy(ip, 0, bytes, 0, 4);
	}
	
	/**
	 * Constructs an IPAddress from a source String {@code s}.
	 * {@code s} must match the regex {@link Constants#IP_REGEX}.
	 * @param s source String 
	 */
	public IPAddress (String s) {
		if (!s.matches(Constants.IP_REGEX)) {
			Logger.fatal(fname + "#init: input string '" + s + 
				"' does not match regex /" + Constants.IP_REGEX + "/");
			throw new IllegalArgumentException
				(fname + "#init: input string '" + s + 
					"' does not match regex /" + Constants.IP_REGEX + "/");
		}
		String[] components = s.split("\\.");
		System.out.println("The input parameter is " + s);
		System.out.println("components length is " + components.length);
		System.out.println("Components[0] is " + components[0]);
		
		bytes[0] = Byte.parseByte(components[0]);
		bytes[1] = Byte.parseByte(components[1]);
		bytes[2] = Byte.parseByte(components[2]);
		bytes[3] = Byte.parseByte(components[3]);
	}
	
	/**
	 * @return a copy of the byte representation of this IPAddress
	 */
	public byte[] getBytes () {
		byte[] result = new byte[4];
		System.arraycopy(bytes, 0, result, 0, 4);
		
		return result;
	}
	
	/**
	 * Returns a String representation of this IPAddress. Strings will match 
	 * the regex {@link Constants#IP_REGEX}.
	 * @return the String representation of this IPAddress
	 */
	public String toString () {
		return Byte.toUnsignedInt(bytes[0]) + "." + 
				Byte.toUnsignedInt(bytes[1]) + "." + 
				Byte.toUnsignedInt(bytes[2]) + "." + 
				Byte.toUnsignedInt(bytes[3]);
	}
	
	/**
	 * Compares this IPAddress to another IPAddress.
	 * 
	 * @param other the IPAddress to compare to
	 * @return whether or not both {@code this} and {@code other} represent 
	 * the same IP Address
	 */
	public boolean equals (IPAddress other) {
		return toString().equals(other.toString());
	}
}
