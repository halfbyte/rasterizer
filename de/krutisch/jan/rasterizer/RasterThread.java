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
public class RasterThread extends Thread {
	private RasterizerImage ri;
	private RasterizerPdf rp;
	private EventLogger logger;
	RasterThread(RasterizerImage i, RasterizerPdf p, EventLogger l) {
		this.ri = i;
		this.rp = p;
		this.logger = l;
	}
	public void run() {
		if (ri==null) return;
		if (rp==null) return;
		if (logger==null) return;
		logger.log(EventLogger.VERBOSE,"Starting RasterThread");
		rp.rasterizeImage(ri);
		logger.log(EventLogger.VERBOSE,"RasterThread finished");
	}
}
