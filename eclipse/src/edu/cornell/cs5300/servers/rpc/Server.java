package edu.cornell.cs5300.servers.rpc;

public class Server {

	public static void init(){
		new ServerThread().start();
	}
	
}
