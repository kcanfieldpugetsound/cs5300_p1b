package edu.cornell.cs5300.servers.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
	
	public static ConcurrentLinkedQueue<DatagramPacket> push_responses = new ConcurrentLinkedQueue<DatagramPacket>();
	public static ConcurrentLinkedQueue<DatagramPacket> data_responses = new ConcurrentLinkedQueue<DatagramPacket>();
	public static DatagramSocket clientSocket;

	public static void init(){
		
		try {
			clientSocket = new DatagramSocket(6300);
			} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		new ClientThread().start();
	}
	
	
	
	public boolean sendPacket(DatagramPacket packet)
	{
		try {
			Client.clientSocket.send(packet);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
