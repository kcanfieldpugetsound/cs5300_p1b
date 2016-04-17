package edu.cornell.cs5300.project1b.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.cs5300.project1b.init.Initializer;

/**
 * Application Lifecycle Listener implementation class Config
 *
 */
public class Config implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public Config() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         Initializer.init();
    }
	
}
