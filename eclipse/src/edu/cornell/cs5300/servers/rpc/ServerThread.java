package edu.cornell.cs5300.servers.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cornell.cs5300.project1b.Constants;

public class ServerThread extends Thread{
	
	protected static DatagramSocket socket = null;
	public static ConcurrentLinkedQueue<DatagramPacket> push_requests = new ConcurrentLinkedQueue<DatagramPacket>();
	public static ConcurrentLinkedQueue<DatagramPacket> data_requests = new ConcurrentLinkedQueue<DatagramPacket>();
	
	public static void init(){
		try {
			socket = new DatagramSocket(Constants.RPC_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	public void run(){
		while (true){
			byte[] payload = new byte[Constants.MAX_MESSAGE_SIZE];
			DatagramPacket packet = new DatagramPacket(payload, payload.length);
			
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			
		}
	}

}
