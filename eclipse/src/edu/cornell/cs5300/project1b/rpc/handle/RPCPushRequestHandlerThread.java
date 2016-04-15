package edu.cornell.cs5300.project1b.rpc.handle;

import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver;
import edu.cornell.cs5300.project1b.rpc.send.RPCSender;
import edu.cornell.cs5300.project1b.servlet.session.Session;
import edu.cornell.cs5300.project1b.util.Pair;

/**
 * A thread for handling {@link 
 * edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type PUSH_REQUEST} 
 * messages received and stored in 
 * {@link edu.cornell.cs5300.rpc.receive.RPCReceiver RPCReceiver}.
 * 
 * @see #run()
 * 
 * @author gus
 *
 */
public class RPCPushRequestHandlerThread extends Thread {

	/**
	 * Waits for {@link 
	 * edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type PUSH_REQUEST} 
	 * messages from {@link edu.cornell.cs5300.rpc.receive.RPCReceiver 
	 * RPCReceiver}, stores the data in the 
	 * {@link edu.cornell.cs5300.project1b.servlet.State State}, and responds
	 * to the requesting server to let it know that the data has been pushed.
	 */
	public void run () {
		while (true) {
			//wait for push requests
			RPCReceiver.push_request.acquireUninterruptibly();
			
			Pair<IPAddress, RPCMessageInterpreter> p;
			synchronized(RPCReceiver.mutex) {
				p = RPCReceiver.push_request_messages.poll();
			}
			
			//server of the requesting server (where we send the response)
			IPAddress server = p.left();
			RPCMessageInterpreter interpreter = p.right();
			
			//extract the data
			Session session = new Session(interpreter.sessionId(), interpreter.userData());
			
			//and store it in our State
			edu.cornell.cs5300.project1b.servlet.State.data.put
				(interpreter.sessionId().toStringWithoutVersion(), session);
			
			//let the requesting server know that we've stored it
			RPCSender.sendPushResponse(server, interpreter.sessionId());
		}
	}
	
}
