package edu.cornell.cs5300.project1b;

import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * Contains constant values necessary for the system's operation.
 * 
 * @author gus
 *
 */
public class Constants {

	/**
	 * Tolerance to failure of the system.
	 * 
	 * For example, F = 3 means that the system can tolerate the failure
	 * of 3 servers.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * <br>
	 * {@link #WQ} >= F + 1
	 */
	public static int F;
	
	/**
	 * Maximum number of servers to send updates to.
	 * 
	 * For example, W = 3 means that every update will be sent to up to 3
	 * different servers.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * <br>
	 * 1 <= {@link #R} <= {@link #WQ} <= W <= {@link #N}
	 */
	public static int W;
	
	/**
	 * Minimum number of servers at which each user's data must be stored.
	 * 
	 * Number of servers that each user knows have its data.
	 * 
	 * For example, WQ = 3 means that at any time, at least 3 servers will 
	 * contain user U's data, and U knows the names of 3 of these servers.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * <br>
	 * 1 <= {@link #R} <= WQ <= {@link #W} <=  {@link #N}
	 */
	public static int WQ;
	
	/**
	 * The maximum number of servers that will be queried for a user's data.
	 * 
	 * For example, R = 3 means that when looking for a user's data, a server
	 * will send requests to at most 3 other servers.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * <br>
	 * 1 <= R <= {@link #WQ} <= {@link #W} <= {@link #N}
	 */
	public static int R;
	
	/**
	 * The number of servers in the system.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * <br>
	 * 1 <= {@link #R} <= {@link #WQ} <= {@link #W} <= N
	 */
	public static int N;
	
	/**
	 * The port on which the RPC protocol operates.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.rpc.send.RPCSender RPCSender
	 */
	public static int RPC_PORT;
	
	/**
	 * The minimum number of milliseconds a session must remain active after
	 * the last user interaction.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.servlet.session.SessionId SessionId
	 */
	public static int SESSION_TIMEOUT_MILLISECONDS;
	
	/**
	 * The number of milliseconds to wait before timing out on a request 
	 * acknowledgment.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 */
	public static int ACK_TIMEOUT_MILLISECONDS;
	
	/**
	 * The maximum number of milliseconds that a packet can be expected
	 * to be latent in the network. That is, after 
	 * MAX_NETWORK_TIME_MILLISECONDS, a packet has certainly been dropped.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 */
	public static int MAX_NETWORK_TIME_MILLISECONDS;
	
	/**
	 * The period in seconds at which the {@link 
	 * edu.cornell.cs5300.project1b.rpc.receive.reap.RPCReceiveReaperThread
	 * RPCReceiveReaperThread} operates.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 */
	public static int REAPER_PERIOD;
	
	/**
	 * The maximum allowed message size to be used as payload in a 
	 * {@code DatagramPacket}.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.rpc.message.RPCMessage RPCMessage
	 */
	public static int MAX_MESSAGE_SIZE;
	
	/**
	 * The address of the SimpleDB Server used for server coordination.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.db.SimpleDBInterface 
	 * SimpleDBInterface
	 */
	public static String SIMPLEDB_ADDRESS;
	
	/**
	 * The IPAddress of this server.
	 */
	public static IPAddress OUR_ADDRESS;
	
	/**
	 * The AMI Index of this server.
	 */
	public static int SERVER_INDEX;
	
	public static String PUBLIC_IP;
	
	
	
	/**
	 * The relative path to the servers file, which contains a list of all 
	 * servers in the system.
	 * <br>
	 * Loaded from {@code server_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.file.ServerFileInterface 
	 * ServerFileInterface
	 */
	public static String SERVER_FILEPATH;
	
	/**
	 * The relative path to the log file, which contains all error logs for
	 * the system.
	 * <br>
	 * Loaded from {@code log_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.util.log.Logger Logger
	 */
	public static String LOG_FILEPATH;
	
	/**
	 * The lowest level of logs that should be displayed. For example, if
	 * LOG_LEVEL is {@code Logger.Level.WARNING}, then {@code TRACE}
	 * and {@code DEBUG} messages will be suppressed in the logger output.
	 * <br>
	 * Loaded from {@code log_config.xml}
	 * 
	 * @see edu.cornell.cs5300.project1b.util.log.Logger Logger
	 */
	public static Logger.Level LOG_LEVEL;
	
	/**
	 * The reboot ID of this system. Incremented every time the system is shut
	 * down and reboots.
	 * <br>
	 * Loaded from {@code config.xml}
	 * 
	 * @see {@link edu.cornell.cs5300.project1b.servlet.session.SessionId 
	 * SessionId}
	 */
	public static int REBOOT_ID;
	
	/**
	 * Regular expression for matching IP Addresses
	 * 
	 * @see IPAddress
	 */
	public static String IP_REGEX = 
		"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
		+ "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
}
