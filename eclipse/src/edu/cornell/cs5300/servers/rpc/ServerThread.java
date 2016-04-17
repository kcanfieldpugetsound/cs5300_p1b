package edu.cornell.cs5300.servers.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageInterpreter;

public class ServerThread extends Thread{
	
	
	
	
	
	public void run(){
		while (true){
			byte[] buf = new byte[Constants.MAX_MESSAGE_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			try {
				Server.socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			buf = packet.getData();
			
			RPCMessage message = new RPCMessage(buf);
			RPCMessageInterpreter interpreter = new RPCMessageInterpreter(message);
			
			switch(interpreter.type()){
			case DATA_REQUEST:
				Server.data_requests.add(packet);
				break;
			case PUSH_REQUEST:
				Server.push_requests.add(packet);
				break;
				default:
					continue;
			
			}
			
			
			
		}
	}
	
	

}
