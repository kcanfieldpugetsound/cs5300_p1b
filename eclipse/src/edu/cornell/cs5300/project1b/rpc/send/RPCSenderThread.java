package edu.cornell.cs5300.project1b.rpc.send;

import java.net.DatagramPacket;

import edu.cornell.cs5300.project1b.rpc.util.DatagramService;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A thread for sending packets to the 
 * {@link edu.cornell.cs5300.project1b.rpc.util.DatagramService 
 * DatagramService} from the sending queue in 
 * {@link edu.cornell.cs5300.project1b.rpc.send.RPCSender RPCSender}.
 * 
 * @see #run()
 * 
 * @author gus
 *
 */
public class RPCSenderThread extends Thread {
	

	private static final int DATAGRAM_SEND_ATTEMPTS = 10;
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.rpc.send.RPCSenderThread";
	
	/**
	 * Takes {@link DatagramPacket}s from the send queue in 
	 * {@link edu.cornell.cs5300.project1b.rpc.send.RPCSender RPCSender}
	 * as they become available, and sends them using the 
	 * {@link DatagramService}.
	 * <br>
	 * A total of {@link #DATAGRAM_SEND_ATTEMPTS} send attempts will be made to
	 * send the packet in case of send failure. Note that a successful send 
	 * does not indicate that the recipient has received the packet.
	 */
	public void run () {
		while (true) {
			//wait for something to send
			RPCSender.send_ready.acquireUninterruptibly();
			Logger.debug(fname + "#run: found a packet to send");
			
			synchronized(RPCSender.mutex) {
				//grab the packet
				DatagramPacket packet = RPCSender.send_ready_packets.poll();
				
				//try to send it up to DATAGRAM_SEND_ATTEMPTS times
				boolean success = false;
				for (int i = 0; i < DATAGRAM_SEND_ATTEMPTS; i++) {
					Logger.debug(fname + "#run: send attempt " + i + " of " + DATAGRAM_SEND_ATTEMPTS);
					success = DatagramService.sendDatagramPacket(packet);
					if (success) {
						Logger.debug(fname + "#run: successfully sent packet");
						break;
					}
				}
				
				//log an error if we failed to send it!
				if (!success) {
					Logger.warn(fname + "#run: failed to send packet after " + 
						DATAGRAM_SEND_ATTEMPTS + " attempts");
				}
			}
		}
	}
}
