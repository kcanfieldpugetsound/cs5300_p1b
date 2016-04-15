package edu.cornell.cs5300.project1b.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import edu.cornell.cs5300.project1b.Constants;

public class Logger {
	
	public static String mutex;
	
	public static Queue<String> available_log_messages;
	public static Semaphore available_log;
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.util.Logger";
	
	public static void init () {
		available_log_messages = new LinkedList<String>();
		available_log = new Semaphore(0);
		
		mutex = "";
		
		(new LoggerThread()).start();
	}
	
	private static void log (Level l, String msg) {
		synchronized(mutex) {
			//create log entry with level, date and time, and message, and add
			//to queue of log messages to be pushed to disk
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			available_log_messages.add(l.toString() + 
				" [ " + sdf.format(new Date()) + " ]: " + msg + " GUS_LOG");
			
			//indicate that log entry is available to be written
			available_log.release();
		}
	}
	
	public static void debug (String msg) {
		if (Constants.LOG_LEVEL == Level.DEBUG) {
			log(Level.DEBUG, msg);
		}
	}
	
	public static void info (String msg) {
		if (Constants.LOG_LEVEL == Level.INFO ||
			Constants.LOG_LEVEL == Level.DEBUG) {
			log(Level.INFO, msg);
		}
	}
	
	public static void warn (String msg) {
		if (Constants.LOG_LEVEL != Level.ERROR &&
			Constants.LOG_LEVEL != Level.FATAL) {
			log(Level.WARNING, msg);
		}
	}
	
	public static void error (String msg) {
		if (Constants.LOG_LEVEL != Level.FATAL) {
			log(Level.ERROR, msg);
		}
	}
	
	public static void fatal (String msg) {
		log(Level.FATAL, msg);
	}
	
	public enum Level {
		DEBUG,
		INFO,
		WARNING,
		ERROR,
		FATAL;
		
		public static Level parse (String s) {
			switch (s) {
			case "DEBUG":
				return DEBUG;
			case "INFO":
				return INFO;
			case "WARNING":
			case "WARN":
				return WARNING;
			case "ERROR":
				return ERROR;
			case "FATAL":
				return FATAL;
			default:
				throw new IllegalArgumentException
					(fname + ".Level#parse: unknown input string '" + s + "'");
			}
		}
		
		public String toString () {
			switch (this) {
			case DEBUG:
				return "DEBUG";
			case INFO:
				return "INFO ";
			case WARNING:
				return "WARN ";
			case ERROR:
				return "ERROR";
			case FATAL:
				return "FATAL";
			default:
				throw new IllegalStateException
					(fname + ".Level#toString: unknown Level");	
			}
		}
	}
}
