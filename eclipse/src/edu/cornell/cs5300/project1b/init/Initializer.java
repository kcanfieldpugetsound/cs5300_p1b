package edu.cornell.cs5300.project1b.init;

import java.util.List;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.db.SimpleDBInterface;
import edu.cornell.cs5300.project1b.file.ServerFileInterface;
import edu.cornell.cs5300.project1b.rpc.RPC;
import edu.cornell.cs5300.project1b.util.log.Logger;
import edu.cornell.cs5300.servers.rpc.Client;
import edu.cornell.cs5300.servers.rpc.Server;

/**
 * Class for server initialization methods.
 * 
 * @see #init()
 * @author gus
 *
 */
public class Initializer {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.init.Initializer";
	
	private static boolean initialized = false;

	/**
	 * Performs all necessary initialization for a server upon startup, 
	 * specifically by:
	 * <ul>
	 * <li>initializing the logger</li>
	 * <li>reading from system environment variables and setting all values in
	 * {@link Constants}</li>
	 * <li>registering the server with the SimpleDB</li>
	 * <li>downloading a list of all servers from the SimpleDB to 
	 * {@code servers.txt}</li>
	 * <li>initializing the RPC service</li>
	 * </ul>
	 * 
	 */
	public static void init () {
		
		//guard against accidental double initialization
		if (initialized) return;
		initialized = true;
		
		/*
		 * Initialize the Logger
		 */
		//Logger.init();
		
		
		
		/*
		 * Load server configuration parameters from System environmental variables (see bootstrap.sh) into Constants.java
		 */
		Constants.W = Integer.parseInt(System.getenv("W"));
		Constants.WQ = Integer.parseInt(System.getenv("WQ"));
		Constants.R = Integer.parseInt(System.getenv("R"));
		Constants.F = Integer.parseInt(System.getenv("F"));
		Constants.N = Integer.parseInt(System.getenv("N"));
		Constants.REBOOT_ID = Integer.parseInt(System.getenv("N"));
		Constants.RPC_PORT = Integer.parseInt(System.getenv("RPC_PORT"));
		Constants.SESSION_TIMEOUT_MILLISECONDS = Integer.parseInt(System.getenv("SESSION_TIMEOUT_MILLISECONDS"));
		Constants.ACK_TIMEOUT_MILLISECONDS = Integer.parseInt(System.getenv("ACK_TIMEOUT_MILLISECONDS"));
		Constants.MAX_MESSAGE_SIZE = Integer.parseInt(System.getenv("MAX_MESSAGE_SIZE"));
		Constants.SERVER_FILEPATH = System.getenv("SERVER_FILEPATH");
		Constants.SIMPLEDB_ADDRESS = System.getenv("SIMPLEDB_ADDRESS");
		Constants.OUR_ADDRESS = new IPAddress(System.getenv("PRIVATE_IP"));
		Constants.SERVER_INDEX = Integer.parseInt(System.getenv("SERVER_INDEX"));
		Constants.PUBLIC_IP = System.getenv("PUBLIC_IP");
		
		/*
		 * Register with SimpleDB
		 */
		SimpleDBInterface.registerServer();
		
		/*
		 * Download list of servers from SimpleDB, store in servers file
		 */
		List<IPAddress> servers = SimpleDBInterface.getServerList();
		ServerFileInterface.setServers(servers);
		
		/*
		 * Set logger configuration parameters
		 */
		Constants.LOG_LEVEL = Logger.Level.DEBUG; //FIXME: load from somewher
		Constants.LOG_FILEPATH = "log.txt"; //FIXME: where to store me???
		
		
		/*
		 * Initialize Remote Procedure Call service
		 */
		Logger.debug(fname + "#init: running RPC.init() on thread " + Thread.currentThread().getId());
		//RPC.init();
		Server.init();
		Client.init();
		
		Logger.info(fname + "#init: complete");
		
	}
	
}
