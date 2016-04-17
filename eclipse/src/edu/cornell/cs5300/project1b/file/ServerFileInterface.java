package edu.cornell.cs5300.project1b.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.cornell.cs5300.project1b.Constants;
import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.db.SimpleDBInterface;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * Provides methods for interacting with the servers file at
 * {@link edu.cornell.cs5300.project1b.Constants#SERVER_FILEPATH 
 * Constants.SERVER_FILEPATH}
 * 
 * @author gus
 *
 */
public class ServerFileInterface {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.file.ServerFileInterface";
	
	/**
	 * Reads from the servers file at 
	 * {@link edu.cornell.cs5300.project1b.Constants#SERVER_FILEPATH 
	 * Constants.SERVER_FILEPATH}
	 * 
	 * Assumes a file format in which each server's IP address is stored on its
	 * own line in the file, as in this example:
	 * <br><br>
	 * 10.0.0.1 <br>
	 * 10.0.0.2 <br>
	 * 10.0.0.3 <br>
	 * 
	 * @return a List of the IPAddresses of the servers in the servers file
	 */
	public static List<IPAddress> getServers () {
		Logger.debug(fname + "#getServers: attempting to get server list");
		
		List<IPAddress> newServers = SimpleDBInterface.getServerList();
		
		try {
			List<IPAddress> servers = new ArrayList<IPAddress>();
			Scanner sc = new Scanner(new File(Constants.SERVER_FILEPATH));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.matches(Constants.IP_REGEX)){ 
					System.out.println("serverfileline is " + line);
					servers.add(new IPAddress(line));
				}
			}
			sc.close();
			for (IPAddress ip : newServers){
				if (!servers.contains(ip))
					servers.add(ip);
			}
			return servers;
		} catch (FileNotFoundException e) {
			Logger.error
				(fname + "#getServers: could not find servers file at '" + 
					Constants.SERVER_FILEPATH + "'");
			System.err.println("Failed to read from servers file at '" 
				+ Constants.SERVER_FILEPATH + "'");
			e.printStackTrace();
			return new ArrayList<IPAddress>();
		}
	}
	
	/**
	 * Overwrites the servers file at 
	 * {@link edu.cornell.cs5300.project1b.Constants#SERVER_FILEPATH 
	 * Constants.SERVER_FILEPATH}, 
	 * replacing its contents with the servers contained within {@code servers}
	 * with the following format: <br>
	 * <p>
	 * servers.get(0) <br>
	 * servers.get(1) <br>
	 * ... <br>
	 * servers.get(servers.size() - 1)
	 * </p>
	 * 
	 * @param servers
	 */
	public static void setServers (List<IPAddress> servers) {
		Logger.debug(fname + "#setServers: overwriting servers file");
		try {
			FileWriter fw = //false -> overwrite
				new FileWriter(new File(Constants.SERVER_FILEPATH), false);
			
			for (IPAddress addr : servers) {
				fw.write(addr.toString() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			System.err.println("Failed to write to servers file at '" 
				+ Constants.SERVER_FILEPATH + "'");
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the given IP address to the end of the servers file 
	 * at {@link edu.cornell.cs5300.project1b.Constants#SERVER_FILEPATH 
	 * Constants.SERVER_FILEPATH} as long as it does not already
	 * exist in the file.
	 * 
	 * @param server the IPAddress to add to the file
	 */
	public static void addServer (IPAddress server) {
		Logger.debug(fname + "#addServer: called");
		if (!getServers().contains(server)) {
			Logger.debug(fname + "#setServers: appending server " + server + 
				" to file");
			try {
				FileWriter fw = //true -> append
					new FileWriter(new File(Constants.SERVER_FILEPATH), true);
				
				fw.write(server.toString() + "\n");
				fw.close();
			} catch (IOException e) {
				Logger.error(fname + "#addServer: failed to write '" + server +
					"' to servers file at '" + Constants.SERVER_FILEPATH + 
					"'");
				e.printStackTrace();
			}
		} else {
			Logger.debug(fname + "#addServer: server " + server + 
				" already in file");
		}
	}

}
