package edu.cornell.cs5300.project1b.util.log;

import java.io.File;
import java.io.FileWriter;

import edu.cornell.cs5300.project1b.Constants;

public class LoggerThread extends Thread {

	public void run () {
		while (true) {
			//wait for message to write
			Logger.available_log.acquireUninterruptibly();
			
			synchronized(Logger.mutex) {
				
				try {
					//true -> append to file
					//FileWriter w = new FileWriter(new File(Constants.LOG_FILEPATH), true);
					//w.write(Logger.available_log_messages.poll());
					System.out.println(Logger.available_log_messages.poll()); //FIXME
					
					//w.close();
					
				} catch (Exception e) {} //if we can't write it, oh well
			}
		}
	}
}
