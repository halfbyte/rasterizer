package de.krutisch.jan.rasterizer;
/*
 * The Great Rasterizer. Styled after the Rasterbator:
 * http://homokaasu.org/rasterbator/
 *
 * This is the Commandline version.
 * 
 * (c) 2004 Jan Krutisch (http://jan.krutisch.de)
 * This program is released under the GPL
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
 * $Log$
 * Revision 1.7  2004/09/11 12:24:09  halfbyte
 * Working Phase, only CLI
 *
 * Revision 1.6  2004/09/09 18:05:36  halfbyte
 * Modified -c, now stands for experimental color support (using
 * the rasterbator approach).
 * old -c is now -m (Cropmarks)
 * This is version 0.5
 *
 * Revision 1.5  2004/06/13 00:32:10  halfbyte
 * Added -c for cropmarks. This is 0.4
 *
 * Revision 1.4  2004/05/28 23:43:50  halfbyte
 * 0.3 versioning
 *
 * Revision 1.3  2004/05/28 23:42:38  halfbyte
 * This is Version 0.3
 * Changelog:
 * 
 * - Parameter -d for dotSize
 * - Parameter -s for pageSize (A4,A3,LETTER,LEGAL)
 * - Enabled Parameter -l for Landscape pages
 * - Automatic scaling to page size (creates ugly margins sometimes)
 *
 * Revision 1.2  2004/05/23 18:58:20  halfbyte
 * Cleanup. This is Version 0.2. Same features as 0.1 but cleaner code.
 *
 *  
 * 
 */

import gnu.getopt.Getopt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

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

