package edu.cornell.cs5300.servers.rpc;

import java.net.DatagramPacket;


import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver;
import edu.cornell.cs5300.project1b.rpc.send.RPCSender;
import edu.cornell.cs5300.project1b.servlet.session.Session;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.servlet.session.UserData;

import edu.cornell.cs5300.project1b.servlet.State;
import edu.cornell.cs5300.project1b.util.Pair;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A thread for handling {@link 
 * edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type DATA_REQUEST} 
 * messages received and stored in 
 * {@link edu.cornell.cs5300.rpc.receive.RPCReceiver RPCReceiver}.
 * 
 * @see #run()
 * 
 * @author gus
 *
 */
public class DataRequestHandlerThread extends Thread {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.handle.RPCDataRequestHandlerThread";
	
	/**
	 * Waits for {@link 
	 * edu.cornell.cs5300.project1b.rpc.message.RPCMessage.Type DATA_REQUEST} 
	 * messages from {@link edu.cornell.cs5300.rpc.receive.RPCReceiver 
	 * RPCReceiver}, retrieves the data from the 
	 * {@link edu.cornell.cs5300.project1b.servlet.State State}, and responds
	 * to the requesting server with that data. Does not respond if this
	 * server does not have the requested data.
	 */
	public void run () {
		while (true) {
			//wait for a data request to come in
			DatagramPacket packet = null;
			
			
			
			
			while (packet == null){
				packet = Server.data_requests.poll();
			}
			
			RPCMessage message = new RPCMessage(packet.getData());
			RPCMessageInterpreter interpreter = new RPCMessageInterpreter(message);
			
			SessionId sid = interpreter.sessionId();
			
			Session session = edu.cornell.cs5300.project1b.servlet.State.data.get(sid.toStringWithoutVersion());
			
			
			
			
			//RPCReceiver.data_request.acquireUninterruptibly();
			
			Pair<IPAddress, RPCMessageInterpreter> p;
			synchronized(RPCReceiver.mutex) {
				p = RPCReceiver.data_request_messages.poll();
			}

			//IP address of the requesting server (where we send the response)
			IPAddress server = p.left();
			//RPCMessageInterpreter interpreter = p.right();
			
			/*//get the requested data from our State
			Session session = 
				edu.cornell.cs5300.project1b.servlet.State.data.get
					(interpreter.sessionId().toStringWithoutVersion());*/
			
			//respond with that data if we have it; log an error if we don't
			//this should never happen barring exceptional circumstances
			if (session != null) {
				RPCSender.sendDataResponse
					(server, session.sessionId(), session.userData()); 
			} else {
				Logger.error(fname + "#run: session with id '" + 
					p.right().sessionId().toString() + 
					"' not found on this server");
				
			}
		}
	}

}
