package edu.cornell.cs5300.project1b.rpc.receive;

import java.net.DatagramPacket;

import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.rpc.util.DatagramService;
import edu.cornell.cs5300.project1b.util.Pair;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A thread for receiving packets from the 
 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
 * DatagramService} and queueing them in 
 * {@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver RPCReceiver}
 * for later processing.
 * 
 * @see #run()
 * 
 * @author gus
 *
 */
public class RPCReceiveThread extends Thread {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.receive.RPCReceiveThread";
	
	/**
	 * Blocks on receiving a packet through the 
	 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
	 * DatagramService}, redirects that packet to the
	 * proper proper message queue in {@link 
	 * edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver RPCReceiver},
	 * and indicates that a packet is available by releasing the corresponding
	 * {@code Semaphore}.
	 * <br><br>
	 * If a received packet's {@code SessionId} is in 
	 * {@link 
	 * edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver#ignorable_responses
	 * RPCReceiver.ignorable_responses}, then the packet will be thrown out,
	 * and the entry in {@code ignorable_responses} will be removed.
	 * 
	 */
	public void run () {
		
		while (true) {
			//block until receive packet
			DatagramPacket packet = DatagramService.receiveDatagramPacket();
			
			//interpret packet
			RPCMessage message = new RPCMessage(packet.getData());
			IPAddress address = 
				new IPAddress(packet.getAddress().getAddress());
			RPCMessageInterpreter interpreter = 
				new RPCMessageInterpreter(message);
			
			synchronized(RPCReceiver.mutex) {
				
				//are we supposed to ignore this packet?
				Long key_to_remove = null;
				for (Long l : RPCReceiver.ignorable_responses.keySet()) {
					if (RPCReceiver.ignorable_responses.get(l)
							.equals(interpreter.sessionId())) {
						key_to_remove = l;
						break;
					}
				}
				
				//we are supposed to ignore this message
				if (key_to_remove != null) {
					if (RPCReceiver.ignorable_responses.remove(key_to_remove)
							== null) {
						Logger.fatal(fname + "#run: key error, not actually "
								+ "removing from ignorable_responses");
						throw new RuntimeException("key error");
					}
					return;
				}
				
				//we are not supposed to ignore this message, so send 
				//it to the proper RPCReceiver queue	
				Pair<IPAddress, RPCMessageInterpreter> p = 
					new Pair<IPAddress, RPCMessageInterpreter>(address, interpreter);
				switch (interpreter.type()) {
				case DATA_REQUEST:
					RPCReceiver.data_request_messages.add(p);
					RPCReceiver.data_request.release();
					break;
				case DATA_RESPONSE:
					RPCReceiver.data_response_messages.add(p);
					RPCReceiver.data_response.release();
					break;
				case PUSH_REQUEST:
					RPCReceiver.push_request_messages.add(p);
					RPCReceiver.push_request.release();
					break;
				case PUSH_RESPONSE:
					RPCReceiver.push_response_messages.add(p);
					RPCReceiver.push_response.release();
					break;
				default:
					Logger.fatal(fname + "#run: unknown Type: " + 
						interpreter.type().toString());
					throw new IllegalStateException("unknown type");
				}
			}
		}
	}
}
