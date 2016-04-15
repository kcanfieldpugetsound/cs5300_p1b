package edu.cornell.cs5300.project1b.rpc.receive.reap;

import java.util.SortedMap;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;

/**
 * A thread for cleaning up {@code ignorable_responses} in 
 * {@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver RPCReceiver}.
 * Entries from {@code ignorable_responses} are removed after
 * {@link 
 * edu.cornell.cs5300.project1b.Constants#MAX_NETWORK_TIME_MILLISECONDS 
 * Constants.MAX_NETWORK_TIME_MILLISECONDS} milliseconds.
 * <br><br>
 * The thread executes once every
 * {@link edu.cornell.cs5300.project1b.Constants#REAPER_PERIOD 
 * Constants.REAPER_PERIOD} seconds.
 * 
 * @see #run()
 * 
 * @author gus
 *
 */
public class RPCReceiveReaperThread extends Thread {
	
	/**
	 * Finds all of the entries in {@code ignorable_responses} in
	 * {@link edu.cornell.cs5300.project1b.rpc.receive.RPCReceiver RPCReceiver}
	 * that have existed for at least
	 * {@link 
	 * edu.cornell.cs5300.project1b.Constants#MAX_NETWORK_TIME_MILLISECONDS
	 * Constants.MAX_NETWORK_TIME_MILLISECONDS} milliseconds, and removes
	 * them.
	 * <br><br>
	 * Executes once every 
	 * {@link edu.cornell.cs5300.project1b.Constants#REAPER_PERIOD 
	 * Constants.REAPER_PERIOD} seconds.
	 */
	public void run () {
		while (true) {
			synchronized(RPCReceiver.mutex) {
				
				long expireTime = System.currentTimeMillis();
				
				//get all of the entries whose expiration times
				//have already passed (they're less than the current time)
				SortedMap<Long, SessionId> to_remove = 
					RPCReceiver.ignorable_responses.headMap(expireTime);
				
				//remove those responses
				for (Long key : to_remove.keySet()) {
					RPCReceiver.ignorable_responses.remove(key);
				}
			}
				
			//run every REAPER_PERIOD seconds
			try {
				Thread.sleep(1000 * Constants.REAPER_PERIOD);
			} catch (InterruptedException e) {}
		}
	}
	
}
