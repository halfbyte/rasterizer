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

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.Date;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import gnu.getopt.*;

public class Rasterizer {

	private int rowsPerPage = 80;
	private int colsPerPage = 56;
	private float dotSize = 10f;
	
	private String inputFilename = new String("");
	private String outputFilename = new String("");
	
	private int xPages = 4;
	
	private boolean landscape = false;
	private boolean verbose = false;
	
	private com.lowagie.text.Rectangle pageSize = null;


	// 	
	public Rasterizer(String[] args) {
		
		System.out.println("Rasterizer V0.2 (c) 2004 JanKrutisch");
		// parse options using getOpt
		pageSize = null;
		if (parseOptions(args) == false) return;
		
		// get Time for performance measurement
		long startTime = new Date().getTime();
		try {
			// Load original image.
			BufferedImage img = loadImage(inputFilename);
			if (img == null) {
				// Something went wrong
				System.out.println("Image file couldn't be opened. aborting.");
				return;
			} 
			// Verbose: output some image information
			verboseOut("-Source image opened-");
			verboseOut("width:"+img.getWidth());
			verboseOut("height:"+img.getHeight());
			
			/*
			 * scale image. note that we probably need to expand
			 * the parameter set if we want the user to be able to
			 * scale by X OR Y.
			 *
			*/
			
			
			if (pageSize == null) {
				pageSize = PageSize.A4;
			}
			if (landscape) pageSize = pageSize.rotate();
			
			colsPerPage = (int)((pageSize.width()-72) / dotSize);
			rowsPerPage = (int)((pageSize.height()-72) / dotSize);
			
			verboseOut("cols_per_page: " + colsPerPage);
			verboseOut("rows_per_page: " + rowsPerPage);
			
			BufferedImage dimg = scaleImage(img,xPages);
			//saveImage("test.jpg",dimg);
			verboseOut("-Converting-");
			
			// Map image global function.
			int pages = mapImage(dimg);
			
			// performance measurements again.
			
			long fullTime = new Date().getTime() - startTime;
			// final output
			System.out.println( (pages) + " Pages (" + (dimg.getWidth()*dimg.getHeight()) + " Rasterdots) written to " + outputFilename + "\ndone in " + fullTime + " Milliseconds.");
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
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
	
	private BufferedImage scaleImage(BufferedImage img,int xPages) {
		
		// calculate new width and height
		
		int w2 = colsPerPage * xPages;
		// scaling by keeping aspect ratio intact.
		int h2 = (int)((float)img.getHeight()/((float)img.getWidth()/(float)w2));

		verboseOut("-Rescaling image-");
		verboseOut("width:"+w2);
		verboseOut("height:"+h2);
		
		// creating destination image space.
		BufferedImage dimg = new BufferedImage(w2,h2,BufferedImage.TYPE_INT_RGB);
		// get 2d reference.
		Graphics2D graphics2D = dimg.createGraphics();
		// Set rendering mode to bilinear interpolation
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// scale image by drawing into the new image
		graphics2D.drawImage(img, 0, 0, w2, h2, null);
		// returning the new image in the correct size
		return dimg;
	}


	/*
	 * Map Image. The core function.
	 */
	private int mapImage(BufferedImage dimg) {
		try {
			// calculate number of vertical pages. Note that this
			// needs to change when the user should be able to
			// specify both X and Y pages 
						
			int yPages = (int)Math.ceil((double)dimg.getHeight() / (double)rowsPerPage);	

			
			// Get iText document
			Document document = new Document(pageSize);
			// Get Writer. Can cause filenotfound exception 
			// if document is in use (by, e.g. acrobat reader)
			
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilename));
			
			// Signalling opening.
			document.open();
			
			// Get Stream for raw gfx.
			PdfContentByte cb = writer.getDirectContent();
		
			/*
			 * loop through all pages. Behaviour is like
			 * Rasterbator:
			 * 0 1 2 3
			 * 4 5 6 7
			 * 8 9 ...
			 */  
			
			for(int yPage=0;yPage<yPages;yPage++) {
				for(int xPage=0;xPage<xPages;xPage++) {
					verboseOut("-Page: " + (xPage + yPage*xPages) + "-");
					// for heavens sake, document needs something on 
					// the page (not done by ContentByte)
					document.add(new Paragraph(""));
					// map one page.
					mapPage(cb,dimg,document,xPage,yPage);
					// new page					
					document.newPage();
				}
				// new Page
				document.newPage();
			}
			verboseOut("writing file...");
			// closing document and writing content to disk
			document.close();
			// Return number of pages written.
			return xPages * yPages;
		} catch(Exception e) {
			System.out.println(e);
			return 0;
		}

	}

	/*
	 * Mapping one page.
	 */
	private void mapPage(PdfContentByte cb, BufferedImage img, Document document, int xPage, int yPage) {
		
		ColorModel cm = img.getColorModel();
		int xStart = xPage * colsPerPage;
		int yStart = yPage * rowsPerPage;
		int xEnd = (xPage+1) * colsPerPage;
		if (xEnd > (img.getWidth())) xEnd = (img.getWidth());
		int yEnd = (yPage+1) * rowsPerPage;
		if (yEnd >= (img.getHeight()-1)) yEnd = (img.getHeight()-1);
		//System.out.println("xStart:" + xStart + " yStart:" + yStart + " xEnd:" + xEnd + " yEnd:" + yEnd);

		// Looping over all Pixels of the page
		for(int x=0;x<(xEnd-xStart);x++) {
			for (int y=0;y<(yEnd-yStart);y++) {
				// Get color value of the pixel
				int color = img.getRGB(x+xStart,y+yStart);
				// calculating gray value
				int value = cm.getRed(color);
				value += cm.getGreen(color);
				value += cm.getBlue(color);
				value /= 3;
				/*
				
				To trace possible errors, one can generate a
				grayscale image. This is setting the pixel to
				a grayscale value:

					int rgb = value + (value*0x100) + (value*0x10000);
					img.setRGB(x,y,rgb);
 				*/
 				// value trickstery for calculating the circle 
 				// radius. should be done in a method to be able
 				// to create different shapes (that need different values)
 				value *= 120;
				value /= 0xFF;
				value = 120 - value;
				// range check.	
				if (value<0) value = 0;
				
				
				float r = (float)Math.sqrt(value / Math.PI);
				r /= 10f;
				r *= dotSize;
				
				// if radius is >0 then draw circle. should be changed
				// to allow different shapes.
				if (r>0f) {
					// Set color
					cb.setRGBColorFillF(0f,0f,0f);
					// create circle path
					cb.circle((dotSize*x)+document.left(),document.top()-dotSize*y,r);
					// fill path
					cb.fill();
				}
			}		
		}
	}
	

	/*
	 * Parse Commandline Options using GNU Getopt.
	 */	
	private boolean parseOptions(String[] args) {
		Getopt g = new Getopt("Rasterizer", args, "p:s:d:lvh");
		int c;
		String arg;
		while((c= g.getopt()) != -1) {
			switch(c) {
				case 'l':
					// NOT IMPLEMENTED, PUT PARSED
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
		System.out.println("-h : The stuff you are reading right now. No Action.");
		System.out.println("-v : Verbose output");
		System.out.println("inputfile  : Input file (.jpeg, .gif, .png)");
		System.out.println("outputfile : Output file (.pdf) defaults to out.pdf. '.pdf' is added if filename doesn't end on .pdf");
	}
	
	/*
	 * Loading Image 
	 */
	private BufferedImage loadImage(String filename) {
		try {
			File f = new File(filename);
			BufferedImage bi = ImageIO.read(f);
			return bi;			
		} catch(Exception e) {
			return null;
		}
	}
	/*
	 * Save Image. Can be used to output preview image of the
	 * scaled, grayed temp-image.
	 * 
	 */
	private void saveImage(String filename,BufferedImage img) {
		// Use a MediaTracker to fully load the image.
		try {
			File f = new File(filename);
			ImageIO.write(img,"jpg",f);
			return;
		} catch(Exception e) {
			return;
		}
	}
	/*
	 * Wrapper for outputs that should only be shown 
	 * when the user wants verbose output.
	 */
	private void verboseOut(String line) {
		if (verbose) {
			System.out.println(line);
		}
		
	}
	/*
	 * main - static start method. Only calls constructor.
	 */
	public static void main(String[] args) {
		Rasterizer myRasterizer = new Rasterizer(args);	
	}

}
