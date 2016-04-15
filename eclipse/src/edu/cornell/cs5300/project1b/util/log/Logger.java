package edu.cornell.cs5300.project1b.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cornell.cs5300.project1b.Constants;

public class Logger {
	
	public static String mutex;
	
	private static final String fname = 
		"edu.cornell.cs5300.project1b.util.Logger";
	
	
	private static void log (Level l, String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		System.out.println(l.toString() + 
			" [ " + sdf.format(new Date()) + " ] (thread " + 
				Thread.currentThread().getId() + "): " + msg);
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
