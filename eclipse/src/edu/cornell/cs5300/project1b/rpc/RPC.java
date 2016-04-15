package edu.cornell.cs5300.project1b.rpc;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver;
import edu.cornell.cs5300.project1b.rpc.send.RPCSender;
import edu.cornell.cs5300.project1b.rpc.util.DatagramService;
import edu.cornell.cs5300.project1b.servlet.session.Session;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.util.Pair;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * Defines the Remote Procedure Call (RPC) Service, which is used for sending
 * and receiving {@link edu.cornell.cs5300.project1b.servlet.session.Session 
 * Session} data to and from other servers.
 * <br>
 * Particularly, the RPC service supports two methods:
 * <ul>
 * <li>{@link #pushData(IPAddress, Session)}</li>
 * <li>{@link #requestData(IPAddress, SessionId)}</li>
 * </ul>
 * 
 * The RPC service must be initialized using {@link #init()}.
 * 
 * @author gus
 *
 */
public class RPC {
	
	private static final String fname = "edu.cornell.cs5300.project1b.rpc.RPC";
	
	/**
	 * Fully initializes the Remote Procedure Call Service. Particularly, it
	 * <ul>
	 * <li>{@link edu.cornell.cs5300.project1b.rpc.util.DatagramService#init()
	 * initializes the DatagramService}</li>
	 * <li>{@link edu.cornell.cs5300.project1b.rpc.send.RPCSender#init()
	 * initializes the RPC Sender}</li>
	 * <li>{@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver#init()
	 * initializes the RPC Receiver}</li>
	 * </ul>
	 */
	public static void init () {
		DatagramService.init();
		RPCSender.init();
		RPCReceiver.init();
		Logger.debug(fname + "#init: complete");
	}
	
	/**
	 * Pushes the given {@code Session} information to the given server. Waits 
	 * {@link edu.cornell.cs5300.project1b.Constants#ACK_TIMEOUT_MILLISECONDS 
	 * Constants.ACK_TIMEOUT_MILLISECONDS} milliseconds for an
	 * acknowledgment from the server that it received the data, and returns 
	 * whether or not it received that acknowledgment.
	 * <br>
	 * Note: after timeout, any response that may come in from the server is
	 * ignored by RPC.
	 * 
	 * @param server where to push the data
	 * @param session what data to push
	 * @return whether or not the server successfully stored the data
	 */
	public static boolean pushData (IPAddress server, Session session) {
		Logger.debug(fname + "#pushData: pushing session with id " + 
			session.sessionId() + " to server " + server);
		
		/* SEND REQUEST */
		RPCSender.sendPushRequest
			(server, session.sessionId(), session.userData());
		Logger.debug(fname + "#pushData: request for session with id " + 
			session.sessionId() + " to server " + server + " sent");
		
		/* WAIT FOR RESPONSE */
		
		/*
		 * How we recognize timeout:
		 * We spawn a new thread here to let the main thread (us) know when the
		 * timeout period has passed. Two possible cases exist:
		 *  - no responses are received, and we keep blocking
		 *    -> semaphore.acquire() throws InterruptedException
		 *       if interrupted; we can quit on this exception
		 *  - responses are received, but none of them are ours
		 *    -> the while loop ensures that we are not interrupted before
		 *       every iteration; we can quit looping once interrupted
		 * where quit means stop waiting for a response, and return false
		 */
		
		//keep track of the main thread
		final Thread mainThread = Thread.currentThread();
		//spawn a new thread to interrupt us after timeout has passed
		final Thread timeoutThread = (new Thread () {
			public void run () {
				try {
					Logger.debug(fname + "#pushData (timeoutThread): starting");
					Thread.sleep(Constants.ACK_TIMEOUT_MILLISECONDS);
					Logger.debug(fname + "#pushData (timeoutThread): timeout; "
						+ "interrupting main thrad");
					mainThread.interrupt();
				} catch (InterruptedException e) {
					//abort mission!
					return;
				} 
			}
		});
		timeoutThread.start();
		
		//wait for an ack or timeout
		//note that it's possible for us to spin here if there are responses
		//in the queue that aren't ours, but we won't do so for more than
		//ACK_TIMEOUT_MILLISECONDS milliseconds, as we'll be interrupted
		while (!Thread.interrupted()) {
			try {
				RPCReceiver.push_response.acquire();
				
				Pair<IPAddress, RPCMessageInterpreter> p;
				synchronized(RPCReceiver.mutex) {
					p = RPCReceiver.push_response_messages.poll();
				}
				
				//the ack is for the packet we sent (SessionIds match)
				if (p.right().sessionId().equals(session.sessionId())) {
					//we don't need to be interrupted anymore
					timeoutThread.interrupt();
					//in the rare case that we were interrupted in the last
					//couple of instructions, clear the interrupt flag
					Thread.interrupted();
					return true;
				} 
				//*this is not the ack you're looking for*
				else {
					synchronized(RPCReceiver.mutex) {
						RPCReceiver.push_response_messages.add(p);
					}
					RPCReceiver.push_response.release();
				}
			} catch (InterruptedException ie) { //timeout!
				//we'll catch this in a second
				Thread.currentThread().interrupt();
			}
		}
		Logger.debug(fname + "#pushData: push request timed out; sessionId = " 
			+ session.sessionId() + ", server = " + server);
		return false;
	}
	
	/**
	 * Requests the {@code Session} data with the given {@code SessionId} 
	 * from the given server. Waits 
	 * {@link edu.cornell.cs5300.project1b.Constants#ACK_TIMEOUT_MILLISECONDS 
	 * Constants.ACK_TIMEOUT_MILLISECONDS} for a response, and returns that
	 * response if it receives one. Otherwise, returns {@code null}.
	 * <br>
	 * Note: after timeout, any response that may come in from the server is 
	 * ignored by RPC.
	 * 
	 * @param server
	 * @param id
	 * @return received data, else {@code null} if none received
	 */
	public static Session requestData (IPAddress server, SessionId id) {
		Logger.debug(fname + "#requestData: requesting SessionId " + id + " from server " + server);
		
		/* SEND REQUEST */
		RPCSender.sendDataRequest(server, id);
		Logger.debug(fname + "#requestData: request for session with id " + 
				id + " to server " + server + " sent");
		
		/* WAIT FOR RESPONSE */
		
		/*
		 * How we recognize timeout:
		 * We spawn a new thread here to let the main thread (us) know when the
		 * timeout period has passed. Two possible cases exist:
		 *  - no responses are received, and we keep blocking
		 *    -> semaphore.acquire() throws InterruptedException
		 *       if interrupted; we can quit on this exception
		 *  - responses are received, but none of them are ours
		 *    -> the while loop ensures that we are not interrupted before
		 *       every iteration; we can quit looping once interrupted
		 * where quit means stop waiting for a response, and return null
		 */
		
		//keep track of the main thread
		final Thread mainThread = Thread.currentThread();
		//spawn a new thread to interrupt us after timeout has passed
		final Thread timeoutThread = (new Thread () {
			public void run () {
				try {
					Thread.sleep(Constants.ACK_TIMEOUT_MILLISECONDS);
					Logger.debug(fname + "#sendDataRequest (timeoutThread): ");
					mainThread.interrupt();
				} catch (InterruptedException e) {
					//abort mission!
					return;
				} 
			}
		});
		timeoutThread.start();
		
		//wait for an ack or timeout
		//note that it's possible for us to spin here if there are responses
		//in the queue that aren't ours, but we won't do so for more than
		//ACK_TIMEOUT_MILLISECONDS milliseconds, as we'll be interrupted
		while (!Thread.interrupted()) {
			try {
				RPCReceiver.data_response.acquire();
				
				Pair<IPAddress, RPCMessageInterpreter> p;
				synchronized(RPCReceiver.mutex) {
					p = RPCReceiver.data_response_messages.poll();
				}
				
				//the data is for the SessionId we requested
				if (p.right().sessionId().equals(id)) {
					//we don't need to be interrupted anymore
					timeoutThread.interrupt();
					//clear the interrupt flag in case we were
					//interrupted in the last few instructions
					Thread.interrupted();
					return new Session
						(p.right().sessionId(), p.right().userData());
				} 
				//*this is not the data you're looking for*
				else {
					synchronized(RPCReceiver.mutex) {
						RPCReceiver.data_response_messages.add(p);
					}
					RPCReceiver.data_response.release();
				}
			} catch (InterruptedException ie) { //timeout!
				//we'll catch this in a second
				Thread.currentThread().interrupt();
			}
		}
		return null;
	}

}
