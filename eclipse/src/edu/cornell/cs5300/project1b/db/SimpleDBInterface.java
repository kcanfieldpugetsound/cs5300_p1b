package edu.cornell.cs5300.project1b.db;

import edu.cornell.cs5300.project1b.*;
import java.util.List;
import java.util.ArrayList;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.Attribute;


import edu.cornell.cs5300.project1b.IPAddress;
import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * Provides methods for interfacing with the SimpleDB Server at 
 * {@link edu.cornell.cs5300.project1b.Constants#SIMPLEDB_ADDRESS
 * Constants.SIMPLEDB_ADDRESS}.
 * 
 * @author gus
 *
 */
public class SimpleDBInterface {
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.db.SimpleDBInterface";
	
	/**
	 * Registers this server with the SimpleDB Server at
	 * {@link edu.cornell.cs5300.project1b.Constants#SIMPLEDB_ADDRESS 
	 * Constants.SIMPLEDB_ADDRESS}.
	 */
	
	public static BasicAWSCredentials awsCredentials;
	public static AmazonSimpleDBClient client;
	//public static final String DB_DOMAIN_NAME = "Project1b.ktc36jf446gmd68";
	
	
	public static void registerServer () {
		Logger.debug(fname + "#registerServer: called");
		

//		CreateDomainRequest createDomainRequest = new CreateDomainRequest(SimpleDBInterface.DB_DOMAIN_NAME);
//		client.createDomain(createDomainRequest);
//		
		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		
		String privateIP = System.getenv("PRIVATE_IP");
		
		awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		client = new AmazonSimpleDBClient(awsCredentials);
		
		List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
		String name = "private_IP"; //the name of the column in the table: the private (internal) IP of the server
		String value = Constants.OUR_ADDRESS.toString();//the private (internal) IP of the server
		boolean replace = true;
		ReplaceableAttribute a = new ReplaceableAttribute(name,value,replace);
		attributes.add(a);
		String itemName = "" + Constants.SERVER_INDEX;// AMI small integer for row in table - the AMI gives a small integer value for each running ec2 instance so we can use this integer as the serverID in the cookie instead of the IP address
		PutAttributesRequest req = new PutAttributesRequest();
		req.setDomainName("Project1b.ktc36jf446gmd68");
		req.setAttributes(attributes);
		req.setItemName(itemName);
		client.putAttributes(req);
	}
	
	public static GetAttributesResult getIPAddress(String itemName){
		GetAttributesRequest getAttributesRequest = new GetAttributesRequest("Project1b.ktc36jf446gmd68",itemName);
		ArrayList<String> attributeNames = new ArrayList<String>();
		attributeNames.add("private_IP");
		getAttributesRequest.setAttributeNames(attributeNames);
		return client.getAttributes(getAttributesRequest);
	}
	
	/**
	 * Retrieves a list of the IP Addresses of all servers in the system
	 * from the SimpleDB Server.
	 * @return list of all servers' IP addresses
	 */
	public static List<IPAddress> getServerList() {
		Logger.debug(fname + "#getServerList: attempting to get available "
				+ "servers from database");
		ArrayList<IPAddress> addressList = new ArrayList<IPAddress>();
		
		for(int item = 0; item<Constants.N; item++)
		{
			String itemName = ""+item;
			GetAttributesResult result = getIPAddress(itemName);
			String ipAddressString = "";
			
			Attribute a = result.getAttributes().get(0);
			if(a.getName().equals("private_IP")) {
				ipAddressString = a.getValue();
			}
			if(!ipAddressString.equals("")) {
				IPAddress toAdd = new IPAddress(ipAddressString);
				addressList.add(toAdd);
			}
		}	
		Logger.debug(fname + "#getServerList: found " + addressList.size() + 
			" servers");
		return addressList;
	}
	
	

}
