package edu.cornell.cs5300.project1b.rpc.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A class which provides {@link DatagramPacket} sending and receiving
 * capabilities for the RPC service by abstracting away the methods of 
 * {@link DatagramSocket}.
 * <br>
 * Particularly, {@code DatagramService} provides blocking 
 * {@link #sendDatagramPacket(DatagramPacket)} and 
 * {@link #receiveDatagramPacket()} methods.
 * <br><br>
 * {@code DatagramService} listens on port 
 * {@link edu.cornell.cs5300.project1b.Constants#RPC_PORT Constants.RPC_PORT}
 * <br><br>
 * DatagramService must be initialized using {@link #init()}.
 * 
 * @author gus
 *
 */
public class DatagramService {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.util.DatagramService";
	
	private static DatagramSocket socket;
	
	/**
	 * Fully initializes the {@code DatagramService}.
	 */
	public static void init () {
		Logger.debug(fname + "#init: called");
		try {
			socket = new DatagramSocket(Constants.RPC_PORT);
		} catch (SocketException e) {
			Logger.fatal(fname + "#init: failed to open socket on port " + 
				Constants.RPC_PORT + ", exception: " + e.toString());
			throw new RuntimeException("socket initialization failed");
		}
	}
	
	/**
	 * Attempts to send the given {@link DatagramPacket}. Returns whether or
	 * not the send was successful. NOTE: a successful send does not indicate
	 * that the recipient has received it.
	 * 
	 * @param packet what to send
	 * @return whether or not the send was successful
	 */
	public static boolean sendDatagramPacket (DatagramPacket packet) {
		Logger.debug(fname + "#sendDatagramPacket: called");
		//reopen if necessary
//		if (socket.isClosed() || !socket.isConnected()) {
//			Logger.warn(fname + "#sendDatagramPacket: "
//				+ "had to restart DatagramService");
//			socket.close();
//			init();
//		}
		try {
			socket.send(packet);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Receives a {@link DatagramPacket} on port 
	 * {@link edu.cornell.cs5300.project1b.Constants#RPC_PORT 
	 * Constants.RPC_PORT}.
	 * <br>
	 * Blocks until a {@code DatagramPacket} is received. 
	 * 
	 * @return the received packet
	 */
	public static DatagramPacket receiveDatagramPacket () {
		Logger.debug(fname + "#receiveDatagramPacket: called on thread " + Thread.currentThread().getId());
		//reopen if necessary
//		if (socket.isClosed() || !socket.isConnected()) {
//			Logger.warn(fname + "#receiveDatagramPacket: "
//					+ "had to restart DatagramService");
//			socket.close();
//			init();
//		}
		try {
			DatagramPacket received = new DatagramPacket(new byte[Constants.MAX_MESSAGE_SIZE], Constants.MAX_MESSAGE_SIZE);
			socket.receive(received);
			Logger.debug(fname + "#receiveDatagramPacket: received packet");
			
			//force payload to be only as large as necessary
			byte[] payload = new byte[received.getLength()];
			System.arraycopy(received.getData(), 0, payload, 0, received.getLength());
			
			return new DatagramPacket(payload, payload.length, received.getAddress(), Constants.RPC_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
