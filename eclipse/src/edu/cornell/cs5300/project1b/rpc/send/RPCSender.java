package edu.cornell.cs5300.project1b.rpc.send;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageFactory;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.servlet.session.UserData;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A class for sending messages via the RPC service. 
 * RPCSender's methods do not immediately send packets over the network;
 * rather, they are queued for sending. RPCSender employs a helper thread, 
 * {@link edu.cornell.cs5300.project1b.rpc.send.RPCSenderThread 
 * RPCSenderThread}, to actually send packets through the
 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
 * DatagramService}.
 * <br>
 * Holds the {@code Queue} of packets ready to be sent 
 * ({@link #send_ready_packets}) and the {@code Semaphore} ({@link #send_ready}
 * to coordinate the sending of those packets with its helper thread.
 * <br><br>
 * RPCSender must be initialized using {@link #init()}
 * 
 * @author gus
 *
 */
public class RPCSender {
	
	public static final String mutex = "";
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.send.RPCSender";
	
	/**
	 * Holds all of the packets that are ready to be sent via the 
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService}.
	 */
	public static Queue<DatagramPacket> send_ready_packets;
	
	/**
	 * The number of packets ready to be sent by the
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService}.
	 */
	public static Semaphore send_ready;
	
	/**
	 * Fully initializes RPCSender, and spawns its helper thread, 
	 * {@code edu.cornell.cs5300.project1b.rpc.send.RPCSenderThread 
	 * RPCSenderThread}.
	 */
	public static void init () {
		Logger.debug(fname + "#init: called");
		send_ready_packets = new LinkedList<DatagramPacket>();
		send_ready = new Semaphore(0);
		(new RPCSenderThread()).start();
		Logger.debug(fname + "#init: RPCSenderThread started");
		Logger.debug(fname + "#init: completed");
	}
	
	/**
	 * Sends a request to {@code server} to store the {@code Session} with
	 * the given {@code SessionId} and {@code UserData}. 
	 * <br><br>
	 * More accurately, constructs the appropriate {@code DatagramPacket} and 
	 * queues it to be sent by the 
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService} at a later time.
	 * 
	 * @param server where to send the request
	 * @param id the id of the session to be stored
	 * @param ud the user data of the session to be stored
	 */
	public static void sendPushRequest (IPAddress server, SessionId id, UserData ud) {
		
		try {
			
			RPCMessage message = 
				RPCMessageFactory.createRPCMessage(RPCMessage.Type.PUSH_REQUEST, id, ud);
			InetAddress addr = InetAddress.getByAddress(server.getBytes());
			
			byte[] payload = message.serialize();
			
			DatagramPacket packet = 
				new DatagramPacket(payload, payload.length, addr, 5697);
			
			synchronized (mutex) {
				send_ready_packets.add(packet);
				send_ready.release();
			}
			
		} catch (UnknownHostException e) { //cannot create InetAdddress from IPAddress
			e.printStackTrace();
			Logger.fatal(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
			throw new RuntimeException
				(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
		}
		
		
	}
	
	/**
	 * Sends a response to {@code server} acknowledging that it has stored the
	 * {@code Session} data with the given {@code id}, as requested.
	 * <br><br>
	 * More accurately, constructs the appropriate {@code DatagramPacket} and 
	 * queues it to be sent by the 
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService} at a later time.
	 * 
	 * @param server where to send the acknowledgment
	 * @param id what {@code SessionId} has been stored
	 */
	public static void sendPushResponse (IPAddress server, SessionId id) {
		
		try {
			
			RPCMessage message = 
				RPCMessageFactory.createRPCMessage(RPCMessage.Type.PUSH_RESPONSE, id);
			InetAddress addr = InetAddress.getByAddress(server.getBytes());
			
			byte[] payload = message.serialize();
			
			DatagramPacket packet = 
				new DatagramPacket(payload, payload.length, addr, 5697);

			synchronized (mutex) {
				send_ready_packets.add(packet);
				send_ready.release();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Logger.fatal(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
			throw new RuntimeException
				(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
		}
	}
	
	/**
	 * Sends a request to {@code server} for the {@code Session} data with the 
	 * given {@code SessionId}
	 * <br><br>
	 * More accurately, constructs the appropriate {@code DatagramPacket} and 
	 * queues it to be sent by the 
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService} at a later time.
	 * <br><br>
	 * NOTE: When requesting {@code Session} data, the version number will not
	 * be known. Send a request with version -1; the response will contain the 
	 * most recent version number.
	 * 
	 * @param server where to send the request
	 * @param id what {@code SessionId} is requested
	 */
	public static void sendDataRequest (IPAddress server, SessionId id) {
		
		try {
			
			RPCMessage message = 
				RPCMessageFactory.createRPCMessage(RPCMessage.Type.DATA_REQUEST, id);
			InetAddress addr = InetAddress.getByAddress(server.getBytes());
			
			byte[] payload = message.serialize();
			
			DatagramPacket packet = 
				new DatagramPacket(payload, payload.length, addr, 5697);

			synchronized (mutex) {
				send_ready_packets.add(packet);
				send_ready.release();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Logger.fatal(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
			throw new RuntimeException
				(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
		}
	}
	
	/**
	 * Sends a response to {@code server} with the requested {@code Session} data,
	 * given by {@code SessionId} and {@code UserData}. 
	 * <br><br>
	 * More accurately, constructs the appropriate {@code DatagramPacket} and 
	 * queues it to be sent by the 
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService} at a later time.
	 * 
	 * @param server where to send the response
	 * @param id the id of the requested session
	 * @param ud the user data of the requested session
	 */
	public static void sendDataResponse (IPAddress server, SessionId id, UserData ud) {
		
		try {
			
			RPCMessage message = 
				RPCMessageFactory.createRPCMessage(RPCMessage.Type.DATA_RESPONSE, id, ud);
			InetAddress addr = InetAddress.getByAddress(server.getBytes());
			
			byte[] payload = message.serialize();
			
			DatagramPacket packet = 
				new DatagramPacket(payload, payload.length, addr, 5697);

			synchronized (mutex) {
				send_ready_packets.add(packet);
				send_ready.release();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Logger.fatal(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
			throw new RuntimeException
				(fname + "#sendPushRequest: could not "
					+ "translate IPAddress to InetAddress");
		}
	}
}
