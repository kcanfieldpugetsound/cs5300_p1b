package edu.cornell.cs5300.project1b.servlet.session;

/**
 * An immutable class containing all information necessary to store a 
 * user's data for one session.
 * <br><br>
 * Contains both 
 * {@link edu.cornell.cs5300.project1b.servlet.session.SessionId SessionId}
 * and
 * {@link edu.cornell.cs5300.project1b.servlet.session.UserData UserData}
 * objects, accessible through {@link #userData()} and {@link #sessionId()}.
 * 
 * @author gus
 *
 */
public class Session {
	
	private SessionId sessionId;
	private UserData userData;
	
	/**
	 * Constructs a {@code Session} object with the given 
	 * {@code SessionId} and {@code UserData}
	 * 
	 * @param id 
	 * @param ud
	 */
	public Session (SessionId id, UserData ud) {
		sessionId = id;
		userData = ud;
	}
	
	/**
	 * @return the {@code UserData} object contained in this {@code Session}.
	 */
	public UserData userData () {
		return userData;
	}
	
	/**
	 * @return the {@code SessionId} object contained in this {@code Session}.
	 */
	public SessionId sessionId () {
		return sessionId;
	}

}
