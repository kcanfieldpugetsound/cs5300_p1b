package edu.cornell.cs5300.project1b.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.init.Initializer;
import edu.cornell.cs5300.project1b.servlet.session.Session;
import edu.cornell.cs5300.project1b.servlet.session.SessionId;
import edu.cornell.cs5300.project1b.servlet.session.SessionManager;
import edu.cornell.cs5300.project1b.servlet.session.UserData;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * Servlet implementation class Project1bServlet
 */
public class Project1bServlet extends HttpServlet {

	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.servlet.Servlet";
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	private int sessionId = 1; //sessionid, version always starts at 1 for a new instance
	private String defaultHeader = "Hello User"; 
	private String mutexSessionId = ""; //lock on the session id
	private String cookieName = "CS5300Project1BSession"; //cookie name
	
    public Project1bServlet() {
        super();
    }
    
    @Override
	public void init() {
    	Logger.info(fname + "#init: performing server initialization");
		Initializer.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.info(fname + "#doGet: handling get request");
		
		response.setContentType("text/html"); //make HTML page
		PrintWriter out = response.getWriter();
		String cookieStuff = "no cookie"; 
		String expiryDate = "";
		
		//variables that will be used in the response
		
		Cookie[] cookieList =  request.getCookies();
		
		synchronized(mutexSessionId){ //get a lock on the sessionId, so we don't clash on it
				if (cookieList != null){ //we have cookies, get information from cookie
					
					List<IPAddress> dataStore = new ArrayList<IPAddress>();
					SessionId sid = null;
					for (Cookie cookie : cookieList){
						if (cookie.getName().equals(cookieName) && cookie.getMaxAge() != 0){
							cookieStuff = cookie.getValue();
							String ips = cookieStuff.split(":")[0];
							String sessId = cookieStuff.split(":")[1];
							sessId = sessId.substring(12, sessId.length() - 2);
							String[] sidInfo = sessId.split(",");
							
							String [] ipAddresses = ips.split("_");
							//I get the information on session, version, the message, and the expiry date to be printed
							//I also get the whole message
							
							for (int i = 0; i < ipAddresses.length; i++)
								dataStore.add(new IPAddress(ipAddresses[i]));
							
							
							sid = new SessionId(new IPAddress(sidInfo[0]), (new Integer(sidInfo[2])).intValue(), 
									(new Integer(sidInfo[1])).intValue(), (new Integer(sidInfo[3])).intValue());
							
						}
					}
					
					Session session = SessionManager.getSession(dataStore, sid);
					
					if (session == null)
						response.sendRedirect("http://localhost:8080/cs5300project1b/crash.html");
					
					else{
						
							Date d = new Date(session.sessionId().timeout());
							
							out.println
						("<!DOCTYPE html>\n" +
								"<html>\n" +
								"<head><title>CS 5300 Project 1a</title></head>\n" +
								"<body bgcolor=\"#fdf5e6\">\n" +
								"<p> NetID: jf446\tSession: " + session.sessionId().sessNum() + "\tVersion: " + session.sessionId().versionNum() + "\tDate: " + (new Date().toString()) +
								"<h1>" + session.userData().getMessage() + "</h1>\n" +
								"<form action=\"http://localhost:8080/cs5300project1b/Project1bServlet\" method=\"post\">New Message (No Underscores Allowed): "
								+ "<input type = \"text\" name=\"message\"><br>"
								+ "<INPUT TYPE=\"submit\" name=\"Load New Message\" value=\"Load New Message\">"
								+ "<br><br><input type=\"submit\" name=\"refresh\" value=\"Refresh\">"
								+ "<br><br><input type=\"submit\" name=\"logout\" value=\"Logout\">"
								+"</form>" 
								+ "<p>" + cookieStuff + "</p>" 
								+ "<p>" + "Expiration: " + d.toString() + "</p>" 
								+ "</body></html>"
						); //make the HTML page
					}
				}
				else{
					//there is no cookie, so this is a new instance
					
					SessionId sid = new SessionId(Constants.OUR_ADDRESS, Constants.REBOOT_ID, sessionId, 0);
					UserData userData = new UserData(defaultHeader);
					Session session = new Session(sid, userData);
					sessionId++; //increment the sessionId for uniqueness between sessions
					long expirationTime = (long) Constants.SESSION_TIMEOUT_MILLISECONDS;
					Date date = new Date(expirationTime); //this sets the Java expiry date
					expiryDate = date.toString();
					Integer key = new Integer(sessionId); //create the key for the hashtable
					String tableKey = sid.toStringWithoutVersion();
					State.data.put(tableKey, session);
					List<IPAddress> dataStore = SessionManager.setSession(session);
					dataStore.add(Constants.OUR_ADDRESS);
					String cookieValue = stringify(dataStore, sid);
					
					Cookie cookie = new Cookie(cookieName, cookieValue);
					cookie.setMaxAge(Constants.SESSION_TIMEOUT_MILLISECONDS);//make the cookie and set it to expire in 1 day
					response.addCookie(cookie);//return cookie to browser
					out.println
					("<!DOCTYPE html>\n" +
							"<html>\n" +
							"<head><title>CS 5300 Project 1b</title></head>\n" +
							"<body bgcolor=\"#fdf5e6\">\n" +
							"<p> NetIDs: jf446, ktc36, gmd68\tSession: " + sid.sessNum() + "\tVersion: " + sid.versionNum() + "\tDate: " + (new Date().toString()) +
							"<h1>" + userData.getMessage() + "</h1>\n" +
							"<form action=\"http://localhost:8080/cs5300project1b/Project1bServlet\" method=\"post\">New Message (No Underscores Allowed): "
							+ "<input type = \"text\" name=\"message\"><br>"
							+ "<INPUT TYPE=\"submit\" name=\"Load New Message\" value=\"Load New Message\">"
							+ "<br><br><input type=\"submit\" name=\"refresh\" value=\"Refresh\">"
							+ "<br><br><input type=\"submit\" name=\"logout\" value=\"Logout\">"
							+"</form>" 
							+ "<p>" + "New Cookie Expiry: " + expiryDate + "</p>"
							+ "</body></html>"
					);//return the page
				
			}	
			
		}
		
		
		//I got the CSS from Marty's web tutorial site. 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.info(fname + "#doPost: handling post request");
		
		//cleanUp(); //this does hashtable clean up, removing old values
		Cookie c = null;
		if 	(request.getCookies() != null)	
			for (Cookie cookie : request.getCookies()){
				if (cookie.getName().equals(cookieName))
					c = cookie; //fetch the right cookie
				else{
					cookie.setMaxAge(0);
					response.addCookie(cookie); //remove extraneous cookies
				}
			}
		if (c != null){ //check if the cookie exists, to avoid null pointers
			List<IPAddress> dataStore = new ArrayList<IPAddress>();
			SessionId sid = null;
			String cookieValue = c.getValue();
			String ips = cookieValue.split(":")[0];
			String sessId = cookieValue.split(":")[1];
			sessId = sessId.substring(12, sessId.length() - 2);
			String[] sidInfo = sessId.split(",");
			String [] ipAddresses = ips.split("_");
			
			for (int i = 0; i < ipAddresses.length; i++)
				dataStore.add(new IPAddress(ipAddresses[i]));
			
			
			sid = new SessionId(new IPAddress(sidInfo[0]), (new Integer(sidInfo[2])).intValue(), 
					(new Integer(sidInfo[1])).intValue(), (new Integer(sidInfo[3])).intValue());
			
			Session session = SessionManager.getSession(dataStore, sid);
			
			//only one of the three buttons will be pressed, so only one if block will execute
			if (session != null){
			//handle new message
			
				if (request.getParameter("Load New Message") != null){  //get our key to change the value
					String msg = request.getParameter("message"); //get the new message
					c.setValue(newMessage(session, msg)); //reset the session message
					c.setMaxAge(Constants.SESSION_TIMEOUT_MILLISECONDS / 1000); //reset the cookie expiration
					response.addCookie(c); //readd the cookie
					
					
				}
				if (request.getParameter("refresh") != null){ // get key so we can manipulate correct session obj
					c.setValue(refresh(session)); //we need to reset the expiration time in the session
					c.setMaxAge(Constants.SESSION_TIMEOUT_MILLISECONDS / 1000); //reset cookie expiration
					response.addCookie(c); //readd
				}
				if (request.getParameter("logout") != null){ //get key so we can delete this session object
					logout(session); //delete object
					c.setMaxAge(0); //invalidate cookie
					response.addCookie(c);
					response.sendRedirect("http://localhost:8080/cs5300project1b/logout.html"); //redirect to logout page
				}
			}
		}
		doGet(request, response); //get HTML response. Logout will not get here. 
	}
	
	protected String newMessage(Session session, String message){
		//Session_OLD s = chm.get(key); //get the correct session
		
		
		
		SessionId newSid = new SessionId(Constants.OUR_ADDRESS, Constants.REBOOT_ID, session.sessionId().sessNum(), session.sessionId().versionNum() + 1);
		UserData newData = new UserData(message);
		
		Session newSession = new Session(newSid, newData);
		
		List<IPAddress> ipList = SessionManager.setSession(newSession);
		
		if (State.data.containsKey(session.sessionId().toStringWithoutVersion()))
			State.data.remove(session.sessionId().toStringWithoutVersion());
			
		
		State.data.put(newSid.toStringWithoutVersion(), newSession);
		
		
		
		return stringify(ipList, newSid); //return the new value for the cookie
	}
	
	protected String refresh(Session session){
		
		SessionId newSid = new SessionId(Constants.OUR_ADDRESS, Constants.REBOOT_ID, session.sessionId().sessNum(), session.sessionId().versionNum() + 1);
		
		Session newSession = new Session(newSid, session.userData());
		List<IPAddress> ipList = SessionManager.setSession(newSession);
		
		if (State.data.containsKey(session.sessionId().toStringWithoutVersion()))
			State.data.remove(session.sessionId().toStringWithoutVersion());
			
		
		State.data.put(newSid.toStringWithoutVersion(), newSession);
		
		
		return stringify(ipList, newSid); //return new value for cookie
	}
	
	protected void logout(Session session){
		if (State.data.containsKey(session.sessionId().toStringWithoutVersion()))
			State.data.remove(session.sessionId().toStringWithoutVersion());
			
		 //remove this key, since the session no longer exists
	}
	
	protected String stringify(Integer key, Session_OLD val){
		return key.toString() + "_" + val.getVersion() + "_" + val.getMessage() + "_" + val.getExpiration();
		//the cookie value is <sessionId>_<version>_<message>_<expiration date string>
		
		//the delimiter is the underscore(_) character
	}
	
	/*protected long getNewExpiry(){
		return (new Date()).getTime() + expTime;
		//timestamp of now + 1 day
	}*/
	//TODO: we'll still need to cleanup our data map in State
	/*protected void cleanUp(){
		 Iterator<Map.Entry<Integer,Session_OLD>> entries = chm.entrySet().iterator();//iterate through all entries in hashmap
		 while (entries.hasNext()){
			 Map.Entry<Integer, Session_OLD> entry = entries.next(); //get item
			 SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //get the expiration of the value
			 try{
				 Date entryDate = sdf.parse(entry.getValue().getExpiration()); //set up parsing
				 if ((new Date()).compareTo(entryDate) > 0){ //if the date right now is past the date in the entry, 
					 chm.remove(entry.getKey(), entry.getValue()); //remove this key,entry pair
				 }
			 }
			 catch(Exception e){
				 continue; //parseException required handler
			 }
		 }
	}*/
	
	protected String stringify(List<IPAddress> data, SessionId sid){
		String result = "";
		
		for (IPAddress ip : data){
			result += ip.toString() + "_";
		}
		
		if (result.length() > 0)
			result = result.substring(0, result.length() - 1);
		
		result += ":";
		result += sid.toString();
		
		
		return result;
	}


}
