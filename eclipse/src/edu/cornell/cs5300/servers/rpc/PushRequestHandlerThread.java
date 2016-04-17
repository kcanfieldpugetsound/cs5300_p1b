package edu.cornell.cs5300.servers.rpc;

import java.io.IOException;
import java.net.DatagramPacket;


import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageFactory;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver;
import edu.cornell.cs5300.project1b.rpc.send.RPCSender;
import edu.cornell.cs5300.project1b.servlet.session.Session;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.util.Pair;
import edu.cornell.cs5300.project1b.util.log.Logger;
import edu.cornell.cs5300.project1b.servlet.session.UserData;

public class PushRequestHandlerThread extends Thread{
	
	private static final String fname = 
			"edu.cornell.cs5300.servers.rpc.DataRequestHandlerThread";
	public void run () {
		while (true) {
			//wait for a data request to come in
			DatagramPacket packet = null;
			
			
			
			
			while (packet == null){
				packet = Server.push_requests.poll();
			}
			
			RPCMessage message = new RPCMessage(packet.getData());
			RPCMessageInterpreter interpreter = new RPCMessageInterpreter(message);
			
			SessionId sid = interpreter.sessionId();
			UserData userData = interpreter.userData();
			
			Session session = new Session(sid, userData);
			
			edu.cornell.cs5300.project1b.servlet.State.data.put(sid.toStringWithoutVersion(), session);
			
			
			
			//RPCReceiver.data_request.acquireUninterruptibly();
			
			/*Pair<IPAddress, RPCMessageInterpreter> p;
			synchronized(RPCReceiver.mutex) {
				p = RPCReceiver.data_request_messages.poll();
			}*/

			//IP address of the requesting server (where we send the response)
			//IPAddress server = p.left();
			//RPCMessageInterpreter interpreter = p.right();
			
			/*//get the requested data from our State
			Session session = 
				edu.cornell.cs5300.project1b.servlet.State.data.get
					(interpreter.sessionId().toStringWithoutVersion());*/
			
			//respond with that data if we have it; log an error if we don't
			//this should never happen barring exceptional circumstances
			
				RPCMessage msg = 
						RPCMessageFactory.createRPCMessage(RPCMessage.Type.PUSH_RESPONSE, session.sessionId());
				byte[] payload = msg.serialize();
				packet = new DatagramPacket(payload, payload.length, packet.getAddress(), packet.getPort());
				
				try {
					Server.socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Logger.error(fname + "#error: IOerror on this server when trying to store data and return acknowledgement.");
					continue;
				}
				
				/*RPCSender.sendDataResponse
					(server, session.sessionId(), session.userData()); */
			
				Logger.debug(fname + "#run: session with id '" + 
					sid.toString() + 
					"' stored on this server");
				
			
		}
	}
}
