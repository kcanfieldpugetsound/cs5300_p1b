package edu.cornell.cs5300.servers.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cornell.cs5300.project1b.Constants;

public class Server {

	public static DatagramSocket socket = null;
	public static ConcurrentLinkedQueue<DatagramPacket> push_requests = new ConcurrentLinkedQueue<DatagramPacket>();
	public static ConcurrentLinkedQueue<DatagramPacket> data_requests = new ConcurrentLinkedQueue<DatagramPacket>();
	public static ConcurrentLinkedQueue<DatagramPacket> responses = new ConcurrentLinkedQueue<DatagramPacket>();
	
	public static void init(){
		try {
			socket = new DatagramSocket(Constants.RPC_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new ServerThread().start();
		new PushRequestHandlerThread().start();
		new DataRequestHandlerThread().start();
		
	}
	
}
