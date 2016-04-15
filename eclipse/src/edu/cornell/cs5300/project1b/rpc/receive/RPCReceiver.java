package edu.cornell.cs5300.project1b.rpc.receive;

import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.handle.RPCDataRequestHandlerThread;
import edu.cornell.cs5300.project1b.rpc.handle.RPCPushRequestHandlerThread;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.rpc.receive.reap.RPCReceiveReaperThread;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.util.Pair;

/**
 * A class for handling received messages from the RPC service.
 * The RPCReceiver employs three helper threads to perform all of its work:
 * {@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiveThread 
 * RPCReceiveThread}, 
 * {@link edu.cornell.cs5300.project1b.rpc.handle.RPCDataRequestHandlerThread
 * RPCDataRequestHandlerThread}, and
 * {@link edu.cornell.cs5300.project1b.rpc.handle.RPCPushRequestHandlerThread
 * RPCPushRequestHandlerThread}.
 * <br><br>
 * The RPCReceiver class itself contains the following 
 * ({@code Semaphore}s) and message queues to coordinate its helper threads.
 * <ul>
 * <li>{@link #data_request}  -> {@link #data_request_messages}</li>
 * <li>{@link #data_response} -> {@link #data_response_messages}</li>
 * <li>{@link #push_request}  -> {@link #push_request_messages}</li>
 * <li>{@link #push_response} -> {@link #push_response_messages}</li>
 * </ul>
 * Responses to
 * {@link edu.cornell.cs5300.project1b.rpc.send.RPCSender RPCSender} requests 
 * that arrive too late are ignored. RPCReceiver keeps track of which responses
 * should be ignored in {@link #ignorable_responses}. It employs another helper 
 * thread,
 * {@link edu.cornell.cs5300.project1b.rpc.receive.reap.RPCReceiveReaperThread
 * RPCReceiveReaperThread}, to delete responses from this collection that 
 * couldn't possibly be received anymore.
 * <br><br>
 * RPCReceiver must be initialized using {@link #init()}
 * @author gus
 *
 */
public class RPCReceiver {
	
	public static String mutex;
	
	/**
	 * Number of pending received 
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type 
	 * DATA_REQUEST} messages.
	 * @see #data_request_messages
	 */
	public static Semaphore data_request;
	
	/**
	 * Number of pending received  
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type 
	 * DATA_RESPONSE} messages.
	 * @see #data_response_messages
	 */
	public static Semaphore data_response;
	
	/**
	 * Number of pending received  
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type 
	 * PUSH_REQUEST} messages.
	 * @see #push_request_messages
	 */
	public static Semaphore push_request;
	
	/**
	 * Number of pending received  
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type 
	 * PUSH_RESPONSE} messages.
	 * @see #push_response_messages
	 */
	public static Semaphore push_response;

	/**
	 * Contains all 
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type
	 * DATA_REQUEST} messages that have yet to be processed.
	 */
	public static Queue<Pair<IPAddress,RPCMessageInterpreter>> 
		data_request_messages;
	
	/**
	 * Contains all 
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type
	 * DATA_RESPONSE} messages that have yet to be processed.
	 */
	public static Queue<Pair<IPAddress,RPCMessageInterpreter>> 
		data_response_messages;
	
	/**
	 * Contains all 
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type
	 * PUSH_REQUEST} messages that have yet to be processed.
	 */
	public static Queue<Pair<IPAddress,RPCMessageInterpreter>> 
		push_request_messages;
	
	/**
	 * Contains all 
	 * {@link edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type
	 * PUSH_RESPONSE} messages that have yet to be processed.
	 */
	public static Queue<Pair<IPAddress,RPCMessageInterpreter>> 
		push_response_messages;
	
	/**
	 * If a received message's {@code SessionId} is contained in this map,
	 * then that received message will be ignored by the
	 * {@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiveThread 
	 * RPCReceiveThread} and not placed in a message queue.
	 * <br><br>
	 * An entry {@code <L,S>} in {@code ignorable_responses} indicates that 
	 * {@code SessionId S} should be ignored, and that the entry may be
	 * removed after time {@code L}.
	 */
	public static SortedMap<Long, SessionId> ignorable_responses;
	
	/**
	 * Fully initializes the RPCReceiver, and spawns all of its helper threads:
	 * <ul>
	 * <li>{@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiveThread
	 * RPCReceiveThread}</li>
	 * <li>{@link 
	 * edu.cornell.cs5300.project1b.rpc.receive.reap.RPCReceiveReaperThread
	 * RPCReceiveReaperThread}</li>
	 * <li>{@link 
	 * edu.cornell.cs5300.project1b.rpc.handle.RPCDataRequestHandlerThread
	 * RPCDataRequestHandlerThread}</li>
	 * <li>{@link
	 * edu.cornell.cs5300.project1b.rpc.handle.RPCPushRequestHandlerThread
	 * RPCPushRequestHandlerThread}</li>
	 * </ul>
	 */
	public static void init () {
		mutex = "";
		
		data_request = new Semaphore(0);
		data_response = new Semaphore(0);
		push_request = new Semaphore(0);
		push_response = new Semaphore(0);

		data_request_messages = new LinkedList<Pair<IPAddress,RPCMessageInterpreter>>();
		data_response_messages = new LinkedList<Pair<IPAddress,RPCMessageInterpreter>>();
		push_request_messages = new LinkedList<Pair<IPAddress,RPCMessageInterpreter>>();
		push_response_messages = new LinkedList<Pair<IPAddress,RPCMessageInterpreter>>();
		
		ignorable_responses = new TreeMap<Long, SessionId>();
		
		(new RPCReceiveThread()).start();
		(new RPCReceiveReaperThread()).start();
		(new RPCDataRequestHandlerThread()).start();
		(new RPCPushRequestHandlerThread()).start();
	}

}
