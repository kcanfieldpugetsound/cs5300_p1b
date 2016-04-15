package edu.cornell.cs5300.project1b.servlet.session;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.file.ServerFileInterface;
import edu.cornell.cs5300.project1b.rpc.RPC;
import edu.cornell.cs5300.project1b.servlet.State;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * A class for managing user sessions for the entire system, across
 * multiple servers.
 * 
 * @author gus
 *
 */
public class SessionManager {
	
	private static final String fname = 
		"edu.cornell.cs5300.projet1b.servlet.session.SessionManager";
	
	/**
	 * Queries all of the {@code servers} for the Session with the given 
	 * {@code SessionId}, returning after the first 
	 * @param servers
	 * @param id
	 * @return
	 */
	public static Session getSession (List<IPAddress> servers, SessionId id) {
		//sanity check
		if (servers == null || servers.size() == 0 || id == null) {
			Logger.error(fname + 
				"#getSession: called with zero or null servers, or null id");
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
			return State.data.get(id.toStringWithoutVersion());
		}
			
		//else we have to query each server, one at a time
		for (int i = 0; i < Math.min(Constants.R, servers.size()); i++) {
			Logger.debug(fname + "#getSession: requesting data for id " + id + 
				" from server " + servers.get(i));
			Session result = RPC.requestData(servers.get(i), id);
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
		boolean removed = available_servers.remove(Constants.OUR_ADDRESS);
		if (!removed) {
			Logger.error(fname + "#setSession: did not remove our address"
				+ " from the list of addresses; available_servers = " + available_servers.toString() + ", our address = " + Constants.OUR_ADDRESS);
			
		}
		stored_servers.add(Constants.OUR_ADDRESS);
		
		//store at WQ - 1 other servers
		int num_attempts = Math.min(available_servers.size(), Constants.W - 1);
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
			
			boolean success = RPC.pushData(server, session);
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
