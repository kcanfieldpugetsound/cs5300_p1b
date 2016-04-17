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
import edu.cornell.cs5300.project1b.util.log.Logger;

/*
 * from https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
 *
 */

// sends a request to the QuoteServer
// waits for the response
//and, when the response is received, displays the response

public class ClientThread extends Thread{

	private static DatagramSocket clientSocket;
	private static final String fname = "edu.cornell.cs5300.servers.rpc";
	private static ConcurrentLinkedQueue<DatagramPacket> packetQueue;
		
	public static void init(){
		try {
			clientSocket = new DatagramSocket(6300);
			packetQueue = new ConcurrentLinkedQueue<DatagramPacket>();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	/*
	 * recv responses, and add them to a Queue and process them to figure out which sessions they go to
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while (true){
			DatagramPacket newPacket = new DatagramPacket(new byte[Constants.MAX_MESSAGE_SIZE], Constants.MAX_MESSAGE_SIZE);
			try {
				clientSocket.receive(newPacket);
				Logger.debug(fname + "#receiveDatagramPacket: received packet");
				//force payload to be only as large as necessary
				byte[] payload = new byte[newPacket.getLength()];
				System.arraycopy(newPacket.getData(), 0, payload, 0, newPacket.getLength());
				//we know this is a response
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private boolean sendPacket(DatagramPacket packet)
	{
		return true;
	}
	
}
