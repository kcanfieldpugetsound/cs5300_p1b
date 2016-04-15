package edu.cornell.cs5300.project1b.servlet;

import java.util.concurrent.ConcurrentHashMap;

import edu.cornell.cs5300.project1b.servlet.session.Session;

/**
 * Stores this server's volatile state.
 * 
 * @author gus
 *
 */
public class State {
	
	/**
	 * The next session number to be assigned.
	 */
	public static int next_session_number = 0;
	
	/**
	 * Thread-safe UserData store, which uses globally unique SessionIds 
	 * as keys.
	 */
	public static ConcurrentHashMap<String, Session> data =
		new ConcurrentHashMap<String, Session>();

}
