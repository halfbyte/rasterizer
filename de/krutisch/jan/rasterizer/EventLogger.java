/*
 * Created on Sep 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.krutisch.jan.rasterizer;

/**
 * @author jan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EventLogger {
	static EventLogger me;
	public final static int DEBUG=0,VERBOSE=1,TERSE=2,ERROR=3; 
	private int currentLogLevel;
	
	EventLogger() {
		currentLogLevel = TERSE;
	}
	public synchronized void log(int logLevel,String message) {
		if (logLevel >= this.currentLogLevel) {
			log(message);
		}
	}
	public void setLogLevel(int logLevel) {
		currentLogLevel = logLevel;
	}
	public abstract void log(String message);
}
