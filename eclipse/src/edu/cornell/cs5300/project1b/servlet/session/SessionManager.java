package edu.cornell.cs5300.project1b.servlet.session;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.file.ServerFileInterface;
import edu.cornell.cs5300.project1b.rpc.RPC;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessage;
import edu.cornell.cs5300.project1b.rpc.message.RPCMessageFactory;
import edu.cornell.cs5300.project1b.servlet.State;
import edu.cornell.cs5300.project1b.util.log.Logger;
import edu.cornell.cs5300.servers.rpc.Client;

/**
 * A class for managing user sessions for the entire system, across
 * multiple servers.
 * 
 * @author gus
 *
 */
public class SessionManager {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.servlet.session.SessionManager";
	
	/**
	 * Queries all of the {@code servers} for the Session with the given 
	 * {@code SessionId}, returning after the first server's response.
	 * 
	 * @param servers
	 * @param id
	 * @return
	 */
	public static Session getSession (List<IPAddress> servers, SessionId id) {
		//sanity check
		
		if (servers == null ) {
			Logger.error(fname + 
				"#getSession: called with null servers list");
			return null;
		}
		if (servers.size() == 0 ) {
			Logger.error(fname + 
				"#getSession: called with zero servers");
			return null;
		}
		if ( id == null) {
			Logger.error(fname + 
				"#getSession: called with null id");
			return null;
		}
		
		//make sure we are satisfying the invariants of this distributed system
		if (servers.size() < Constants.R) {
			Logger.warn(fname + "#getSession: provided server list length (" + 
				servers.size() + ") smaller than R (" + Constants.R + ")");
		}
		
		//if we have the data, that's a lot faster, so use it
		if (servers.contains(Constants.OUR_ADDRESS)) {
			Logger.debug(fname + "#getSession: using local data for id " + id);
			Session session = State.data.get(id.toStringWithoutVersion());
			if (session != null)
				return session;
		}
			
		//else we have to query each server, one at a time
		loop:
		for (int i = 0; i < Math.min(Constants.R, servers.size()); i++) {
			Logger.debug(fname + "#getSession: requesting data for id " + id + 
				" from server " + servers.get(i));
			
			RPCMessage message = 
					RPCMessageFactory.createRPCMessage(RPCMessage.Type.DATA_REQUEST, id);
				InetAddress addr = null;
				try {
					addr = InetAddress.getByAddress(servers.get(i).getBytes());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				byte[] payload = message.serialize();
				
				DatagramPacket packet = 
					new DatagramPacket(payload, payload.length, addr, Constants.RPC_PORT);
				boolean sent = Client.sendPacket(packet);
				
				if (!sent)
					continue loop;
				
				long startTime = System.currentTimeMillis();
				long elapsed = System.currentTimeMillis() - startTime;
				Session result = null;
				
				while (elapsed < (long)Constants.ACK_TIMEOUT_MILLISECONDS && result == null){
					result = Client.sessionSearch(id, servers.get(i)); 
					elapsed = System.currentTimeMillis() - startTime; 
				}
				
			//RPC.requestData(servers.get(i), id);
			
			
			if (result != null) {
				Logger.debug(fname + "#getSession: got data for id " + id + 
					" from server " + servers.get(i));
				return result;
			}
		}
		
		//we failed to get the data -- we suffered more than F failures
		Logger.error(fname + 
			"#getSession: failed to retrieve session from all " +
			Math.min(Constants.R, servers.size()) + " servers");
		return null;
	}
	
	/**
	 * Stores the given {@code Session} locally, and at 
	 * {@link edu.cornell.cs5300.project1b.Constants#WQ Constants.WQ} - 1
	 * of the servers in the rest of the system. Returns a list of all of the
	 * IP addresses of the servers where the {@code Session} is stored.
	 * <br>
	 * Push requests are sent serially.
	 * 
	 * @param session what to store
	 * @return where the session has been stored
	 */
	public static List<IPAddress> setSession (Session session) {
		
		List<IPAddress> available_servers = ServerFileInterface.getServers();
		List<IPAddress> stored_servers = new ArrayList<IPAddress>();
		
		//the number of available servers must be greater than or equal to W
		//to satisfy system requirements and F tolerance
		if (available_servers.size() < Constants.W) {
			Logger.warn(fname + "#setSession: total available servers (" + 
				available_servers.size() + ") smaller than W (" + 
				Constants.W + ")");
		}
		
		//store locally
		State.data.put(session.sessionId().toStringWithoutVersion(), session);
		available_servers.remove(Constants.OUR_ADDRESS);
		stored_servers.add(Constants.OUR_ADDRESS);
		
		//store at WQ - 1 other servers
		int num_attempts = Math.min(available_servers.size(), Constants.W - 1);
		loop:
		for (int i = 0; i < num_attempts; i++) {
			//we've stored at enough servers
			if (stored_servers.size() >= Constants.WQ) {
				Logger.debug
					(fname + "#setSession: session successfully stored in WQ=" 
						+ Constants.WQ + " servers");
				break;
			}
			
			//store at a random server from the list
			int index = 
				(int) Math.floor(Math.random() * available_servers.size());
			IPAddress server = available_servers.remove(index);
			
			RPCMessage message = 
					RPCMessageFactory.createRPCMessage(RPCMessage.Type.PUSH_REQUEST, session.sessionId(), session.userData());
				InetAddress addr = null;
				try {
					addr = InetAddress.getByAddress(server.getBytes());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue loop;
				}
				
				byte[] payload = message.serialize();
				
				DatagramPacket packet = 
					new DatagramPacket(payload, payload.length, addr, Constants.RPC_PORT);
				
				boolean sent = Client.sendPacket(packet);
				boolean success = false;
				
				if (!sent)
					continue loop;
				else{
					long startTime = System.currentTimeMillis();
					long elapsed = System.currentTimeMillis() - startTime;
					
					while (elapsed < (long)Constants.ACK_TIMEOUT_MILLISECONDS && success == false){
						success = Client.getACK(session.sessionId(), server); 
						elapsed = System.currentTimeMillis() - startTime; 
					}
				}
				
				
			
			
			if (success) {
				Logger.debug
					(fname + "#setSession: successfully stored data for id " + 
						session.sessionId() + " at server " + server);
				stored_servers.add(server);
			} else {
				Logger.debug
					(fname + "#setSession: failed to store data for id " + 
						session.sessionId() + " at server " + server);
			}
		}
		
		if (stored_servers.size() < Constants.WQ) {
			Logger.error(fname + "#setSession: failed to write session to WQ=" + 
				Constants.WQ + " servers, could only write to " + 
				stored_servers.size() + " servers");
		}
		
		return stored_servers;
	}

}
