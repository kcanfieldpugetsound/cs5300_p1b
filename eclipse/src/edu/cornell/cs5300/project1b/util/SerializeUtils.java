package edu.cornell.cs5300.project1b.util;

import java.nio.charset.StandardCharsets;

/**
 * A class containing methods useful for serializing data.
 * @author gus
 *
 */
public class SerializeUtils {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.utils.SerializeUtils";

	/**
	 * Returns the LITTLE ENDIAN byte stream representation of the given int.
	 * 
	 * @param i int to convert to LITTLE ENDIAN byte stream
	 * @return LITTLE ENDIAN byte stream representation of {@code i}
	 */
	public static byte[] intToByteArray (int i) {
		byte[] result = new byte[4];
		
		int mask = 0xff;
		
		for (int j = 0; j < 4; j++) {
			result[j] = (byte) (mask & (i >> (8 * j)));
		}
		
		return result;
	}
	
	/**
	 * Constructs an int from the provided LITTLE ENDIAN byte stream.
	 * 
	 * {@code b} must have length of 4, for 32 bits.
	 * 
	 * @param b LITTLE ENDIAN byte stream
	 * @return int constructed from LITTLE ENDIAN byte stream
	 */
	public static int byteArrayToInt (byte[] b) {
		if (b.length != 4) {
			throw new IllegalArgumentException
				(fname + "#byteArrayToInt: b must have length 4");
		}

		int result = 0;
		for (int i = b.length - 1; i >= 0; i--) {
			result = result << 8;
			result = (result | Byte.toUnsignedInt(b[i]));
		}
		return result;
		
	}
	
	/**
	 * @param s the String to serialize
	 * @return byte stream representation of the given String
	 */
	public static byte[] stringToByteArray (String s) {
		
		return s.getBytes(StandardCharsets.UTF_16);
	}
	
	/**
	 * @param b byte stream to convert to a String
	 * @return constructed Stream from the given byte stream
	 */
	public static String byteArrayToString (byte[] b) {
		
		return new String(b, StandardCharsets.UTF_16);
	}
}
