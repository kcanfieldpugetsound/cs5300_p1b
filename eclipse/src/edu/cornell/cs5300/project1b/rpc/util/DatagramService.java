package edu.cornell.cs5300.project1b.rpc.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
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
	private static DatagramSocket senderSocket;
	private static DatagramSocket firstSocket;//catches all received packets
	
	/**
	 * Fully initializes the {@code DatagramService}.
	 */
	public static void init () {
		Logger.debug(fname + "#init: called");
		try {
			socket = new DatagramSocket(Constants.RPC_PORT);
			senderSocket = new DatagramSocket();//the sender has to be on a different socket than the receiver, use any empty port
			firstSocket = new DatagramSocket(5697);
		} catch (SocketException e) {
			Logger.fatal(fname + "#init: failed to open socket on port " + 
				Constants.RPC_PORT + ", exception: " + e.toString() + " OR senderSocket at"+senderSocket.getPort());
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

			byte[] payload = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), 0, payload, 0, packet.getLength());
			
			byte typeOfMsgByte = payload[0];
			switch (typeOfMsgByte){
				case (byte)0://DATA_REQUEST
				case (byte)2://PUSH_REQUEST	
					senderSocket.send(packet);
					break;
				case (byte)1://DATA_RESPONSE
				case (byte)3://PUSH_RESPONSE	
					socket.send(packet);
					break;
				default:
					return false;
						
			}
		
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
			
			firstSocket.receive(received);
			
			Logger.debug(fname + "#receiveDatagramPacket: received packet");
			
			//force payload to be only as large as necessary
			byte[] payload = new byte[received.getLength()];
			System.arraycopy(received.getData(), 0, payload, 0, received.getLength());
			
//			case DATA_REQUEST:
//			return (byte) 0;
//		case DATA_RESPONSE:
//			return (byte) 1;
//		case PUSH_REQUEST:
//			return (byte) 2;
//		case PUSH_RESPONSE:
//			return (byte) 3;
			
			//find out the message type for port-forwarding
			byte typeOfMsgByte = payload[0];
			switch (typeOfMsgByte){
				case (byte)0://DATA_REQUEST
				case (byte)2://PUSH_REQUEST	
					return new DatagramPacket(payload, payload.length, received.getAddress(), Constants.RPC_PORT);
				case (byte)1://DATA_RESPONSE
				case (byte)3://PUSH_RESPONSE	
					return new DatagramPacket(payload, payload.length, received.getAddress(), senderSocket.getPort());
				default:
					return null;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
