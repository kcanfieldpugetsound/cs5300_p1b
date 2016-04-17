package edu.cornell.cs5300.servers.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.util.log.Logger;

/*
 * from https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
 *
 */

// sends a request to the QuoteServer
// waits for the response
//and, when the response is received, displays the response

public class ClientThread extends Thread{


	private static final String fname = "edu.cornell.cs5300.servers.rpc";
			
	public static void init(){

	}
	
	/*
	 * recv responses, and add them to a Queue and process them to figure out which sessions they go to
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while (true){
			DatagramPacket packet = new DatagramPacket(new byte[Constants.MAX_MESSAGE_SIZE], Constants.MAX_MESSAGE_SIZE);
			try {
				Client.clientSocket.receive(packet);
				Logger.debug(fname + "#receiveDatagramPacket: received packet");
				//force payload to be only as large as necessary
				byte[] payload = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), 0, payload, 0, packet.getLength());
				//we know this is a response
				
				RPCMessage msg = new RPCMessage(payload);
				RPCMessageInterpreter interpreter = new RPCMessageInterpreter(msg);
				
				switch(interpreter.type()){
				case DATA_RESPONSE:
					Client.data_responses.add(packet);
					break;
				case PUSH_RESPONSE:
					Client.push_responses.add(packet);
					break;
				default:
					continue;
				
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	
}
