package edu.cornell.cs5300.project1b.file;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cornell.cs5300.project1b.util.log.Logger;

/**
 * Provides methods for interacting with {@code config.xml}
 * 
 * @author gus
 *
 */
public class ConfigFileInterface {
	
	private static final String path = "server_config.xml";
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.file.ConfigFileInterface";
	
	private static final int KEY_INDEX = 0;
	private static final int VALUE_INDEX = 1;
	
	/**
	 * Gets the value from the key in the xml file at {@code path} 
	 * <br>
	 * Assumes the following file structure:
	 * <br>
	 * &lt;configuration&gt; <br>
	 * &lt;property&gt; <br>
	 * &lt;key&gt;...&lt;/key&gt; <br>
	 * &lt;value&gt;...&lt;/value&gt; <br>
	 * &lt;/property&gt; <br>
	 * ... <br>
	 * &lt;/configuration&gt;
	 * 
	 * @param key
	 * @return
	 */
	private static String getProperty (String key) {
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			Document doc = b.parse(new File(path));
			
			NodeList configurationHead = doc.getElementsByTagName("configuration");
			//NodeList elems = doc.getElementsByTagName("property");

			
			for (int i = 0; i < configurationHead.getLength(); i++) {
				NodeList props = configurationHead.item(i).getChildNodes();
				if (props.item(KEY_INDEX).getNodeValue().equals(key)) {
					return props.item(VALUE_INDEX).getNodeValue();
				}
			}
			
			
			Logger.fatal(fname + "#getProperty: no value found for key='" + key + "'");
			throw new RuntimeException("config read failed");
			
		} catch (ParserConfigurationException pce) {
			System.err.println(fname + "#getProperty: failed");
			pce.printStackTrace();
			throw new RuntimeException("config read failed");
		} catch (IOException ioe) {
			System.err.println(fname + "#getProperty: failed");
			ioe.printStackTrace();
			throw new RuntimeException("config read failed");
		} catch (SAXException saxe) {
			System.err.println(fname + "#getProperty: failed");
			saxe.printStackTrace();
			throw new RuntimeException("config read failed");
		}
		
	} 
	
	/**
	 * Sets the value with the key in the xml file at {@code path}
	 * <br>
	 * Assumes the following file structure:
	 * <br>
	 * &lt;configuration&gt; <br>
	 * &lt;property&gt; <br>
	 * &lt;key&gt;...&lt;/key&gt; <br>
	 * &lt;value&gt;...&lt;/value&gt; <br>
	 * &lt;/property&gt; <br>
	 * ... <br>
	 * &lt;/configuration&gt;
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private static void setProperty (String key, String value) {
		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			Document doc = b.parse(new File(path));
			
			NodeList elems = doc.getElementsByTagName("configuration");
			
			for (int i = 0; i < elems.getLength(); i++) {
				NodeList props = elems.item(i).getChildNodes();
				if (props.item(KEY_INDEX).getNodeValue().equals(key)) {
					props.item(VALUE_INDEX).setTextContent(value);
				}
			}
			
			//TODO: WRITE MODIFIED FILE
			// USING ENVIRONMENT VARS INSTEAD
			
			
			throw new RuntimeException("unimplemented");
			
		} catch (ParserConfigurationException pce) {
			Logger.fatal(fname + "#setProperty: failed on input key='" + key + 
				"', value='" + value + "', exception: " + pce.toString());
			throw new RuntimeException("config write failed");
		} catch (IOException ioe) {
			Logger.fatal(fname + "#setProperty: failed on input key='" + key + 
					"', value='" + value + "', exception: " + ioe.toString());
			throw new RuntimeException("config write failed");
		} catch (SAXException saxe) {
			Logger.fatal(fname + "#setProperty: failed on input key='" + key + 
					"', value='" + value + "', exception: " + saxe.toString());
			saxe.printStackTrace();
			throw new RuntimeException("config write failed");
		}
	}
	
	/*
	 * SETTERS
	 */
	
	public static void incrementReboot () {
		String next = String.valueOf(getRebootId() + 1);		
		setProperty("REBOOT_ID", next);
	}
	
	/*
	 * GETTERS
	 */
	
	public static int getW () {
		return Integer.parseInt(getProperty("W"));
	}
	
	public static int getWQ () {
		return Integer.parseInt(getProperty("WQ"));
	}
	
	public static int getR () {
		return Integer.parseInt(getProperty("R"));
	}
	
	public static int getN () {
		return Integer.parseInt(getProperty("N"));
	}
	
	public static int getF () {
		return Integer.parseInt(getProperty("F"));
	}
	
	public static int getRPCPort () {
		return Integer.parseInt(getProperty("RPC_PORT"));
	}
	
	public static int getMaxMessageSize () {
		return Integer.parseInt(getProperty("MAX_MESSAGE_SIZE"));
	}
	
	public static int getSessionTimeoutMilliseconds () {
		return Integer.parseInt(getProperty("SESSION_TIMEOUT_MILLISECONDS"));
	}
	
	public static int getAckTimeoutMilliseconds () {
		return Integer.parseInt(getProperty("ACK_TIMEOUT_MILLISECONDS"));
	}
	
	public static int getRebootId () {
		return Integer.parseInt(getProperty("REBOOT_ID"));
	}
	
	public static String getServerFilePath () {
		return getProperty("SERVER_FILEPATH");
	}
	
	public static String getSimpleDBAddress () {
		return getProperty("SIMPLEDB_ADDRESS");
	}
	
	

}
