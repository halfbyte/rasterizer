package de.krutisch.jan.rasterizer;
/*
 * The Great Rasterizer. Styled after the Rasterbator:
 * http://homokaasu.org/rasterbator/
 *
 * This is the Commandline version.
 * 
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * The Great Rasterizer uses the following libraries: 
 * (apart from the obvious JRE SL)
 * 
 * * iText for pdf generation by Bruno Lowagie  and Paulo Soares
 * (http://www.lowagie.com/iText/)
 * 
 * * GNU getOpt, to have Unix style command line options by Aaron M. Renn
 * (http://www.urbanophile.com/arenn/hacking/download.html)
 * 
 * $Id$
 * 
 * 
 */

import gnu.getopt.Getopt;

import java.util.Date;
import com.lowagie.text.PageSize;


public class Rasterizer {

	private int rowsPerPage = 80;
	private int colsPerPage = 56;
	private float dotSize = 10f;
	
	private String inputFilename = new String("");
	private String outputFilename = new String("");
	
	private int xPages = 4;
	
	private boolean landscape = false;
	private boolean verbose = false;
	private boolean autoMargins = false;
	private boolean printCropmarks = false;
	private boolean printAllCropmarks = false;
	private boolean printColor = false;
	private com.lowagie.text.Rectangle pageSize = null;
	private static final String VERSION = "0.5";

	// 	
	public Rasterizer(String[] args) {
		
		System.out.println("Rasterizer " + VERSION + "(c) JanKrutisch");
		// parse options using getOpt
		pageSize = PageSize.A4;
		if (parseOptions(args) == false) return;
		
		// get Time for performance measurement
		long startTime = new Date().getTime();
		
		CliLogger logger = new CliLogger();
		if (verbose) logger.setLogLevel(EventLogger.VERBOSE);
		
		RasterizerImage ri = RasterizerImage.getInstance(logger);
		RasterizerPdf rp = RasterizerPdf.getInstance(logger);
		
		if (!ri.loadImageFromFile(inputFilename)) {
			System.out.println("Image file '" + inputFilename + "' couldn't be opened. aborting.");
			return;
		}
		rp.setOutputFile(outputFilename);
		if (printColor) {
			rp.setColorMode(RasterizerPdf.SIMPLECOLOR);
		}
		rp.setPageSize(pageSize);
		rp.setDotSize(dotSize);
		rp.setMargins(36,36,36,36);
		rp.setLandscape(landscape);
		
		if (printAllCropmarks) {
			rp.setCropmarks(RasterizerPdf.ALLCROPMARKS);
		} else if (printCropmarks) rp.setCropmarks(RasterizerPdf.CROPMARKS);
		rp.setHorizontalPages(xPages);
		RasterThread rt = new RasterThread(ri,rp,logger);
		rt.start();
		logger.log(EventLogger.VERBOSE,"detached");
		
		long fullTime = new Date().getTime() - startTime;
		
	}


	private void setDotSize(String p) {
		
		try {
			dotSize = Float.parseFloat(p);
		} catch(Exception e) {
			System.out.println("Illegal Dot Size. Defaulting to 10pt");
			dotSize = 10f;
		}
	}

	private void setPageSize(String p) {
		if ("A4".equals(p)) {
			pageSize = PageSize.A4;
		} else if ("A3".equals(p)) {
			pageSize = PageSize.A3;
		} else if ("LETTER".equals(p)) {
			pageSize = PageSize.LETTER;
		} else if ("LEGAL".equals(p)) {
					pageSize = PageSize.LEGAL;
		}
		if (pageSize == null) pageSize = PageSize.A4;
	}

	/*
	 * Image scaling method.
	 */
	

	/*
	 * Parse Commandline Options using GNU Getopt.
	 */	
	private boolean parseOptions(String[] args) {
		Getopt g = new Getopt("Rasterizer", args, "p:s:d:m::lvhc");
		int c;
		String arg;
		while((c= g.getopt()) != -1) {
			switch(c) {
				case 'm':
					printCropmarks = true;
					if ("all".equals(g.getOptarg())) {
						printAllCropmarks = true;
					}
				break;
				case 'l':
					landscape = true;
					break;
				case 'v':
					// DO VERBOSE OUTPUT
					verbose = true;
					break;
				case 'h':
					// PRINT HELP
					printHelp();
					return false;
				case 's':
					// Page size
					setPageSize(g.getOptarg());
				break;
				case 'd':
					// Page size
					setDotSize(g.getOptarg());
				break;
				case 'p':
					// NUMBER OF X-PAGES
					arg = g.getOptarg();
					try {
						xPages = Integer.parseInt(arg);
					}catch(Exception e) {
						xPages = 4;
					}
					break;
				case 'c':
					//Color mode
					printColor = true;
				case '?':
					break;
				default:
				
			}
		}
		// Finally: Parse non-GetOpt parameters (input/output filenames)
		int no = g.getOptind();
		if (no < args.length) {
			inputFilename = args[no];
		}
		no++;
		if (no < args.length) {
			outputFilename = args[no];
		}
		
		// Sanity Check for parameters
		if (inputFilename.length()==0) {
			printHelp();
			return false;
		}
		if (outputFilename.length() == 0) {
			outputFilename = "out.pdf";
		} else {
			if (!outputFilename.toLowerCase().endsWith(".pdf")) {
				outputFilename += ".pdf";
			}
		}
		return true;
		   
	}
	
	/*
	 * Print Help Message, showing commandline parameters.
	 */
	private void printHelp() {
		System.out.println("Usage: java Rasterizer [-p pages] [-l] [-h] [-v] inputfile [outputfile]");
		System.out.println("-p : Number of horizontal pages (vertical will be chosen according to aspect ratio of source image)");
		System.out.println("-l : Use Pages in Landscape");
		System.out.println("-s : Paper Size. Allowed Values: A4, A3, LETTER, LEGAL");
		System.out.println("-m : Print Cropmarks intelligently. use -mall for forced cropmarks");				
		System.out.println("-d : Dotsize in pt. Defaults to 10pt");				
		System.out.println("-h : The stuff you are reading right now. No Action.");
		System.out.println("-c : Experimental color output (the Rasterbator solution)");
		System.out.println("-v : Verbose output");
		System.out.println("inputfile  : Input file (.jpeg, .gif, .png)");
		System.out.println("outputfile : Output file (.pdf) defaults to out.pdf. '.pdf' is added if filename doesn't end on .pdf");
	}
	
	/*
	 * main - static start method. Only calls constructor.
	 */
	public static void main(String[] args) {
		Rasterizer myRasterizer = new Rasterizer(args);	
	}
	
	public class CliLogger extends EventLogger {
		public void log(String msg) {
			System.out.println(msg);
		}
	}
}

