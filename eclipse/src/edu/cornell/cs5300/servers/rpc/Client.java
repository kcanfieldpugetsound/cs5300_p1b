package edu.cornell.cs5300.servers.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;
import edu.cornell.cs5300.project1b.servlet.session.Session;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;

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
	
	public static boolean sendPacket(DatagramPacket packet)
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
	
	//search data responses
	public Session sessionSearch(SessionId id, IPAddress addr)
	{
		Object[] responses= data_responses.toArray();
		for(int i=0; i<data_responses.size(); i++)
		{
			RPCMessage m = new RPCMessage(((DatagramPacket)responses[i]).getData());
			RPCMessageInterpreter interpreter = new RPCMessageInterpreter(m);
			SessionId responseSessionID = interpreter.sessionId();
			
			if(id.toStringWithoutVersion().equals(responseSessionID))
			{
				return new Session(id,interpreter.userData());
			}
		}
		return null;
	}
	
	
	//search push responses
	public boolean getACK(SessionId id, IPAddress addr)
	{
		Object[] acks= push_responses.toArray();
		for(int j=0; j<acks.length; j++)
		{
			
		}
		
		return true;
	}
	
	
}
