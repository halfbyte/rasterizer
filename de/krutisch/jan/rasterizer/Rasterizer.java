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

	static final int ROWS_PER_PAGE = 80;
	static final int COLS_PER_PAGE = 56;
	
	private String inputFilename = new String("");
	private String outputFilename = new String("");
	
	private int xPages = 4;
	
	private boolean landscape = false;
	private boolean verbose = false;


	// 	
	public Rasterizer(String[] args) {
		
		System.out.println("Rasterizer V0.2 (c) 2004 JanKrutisch");
		// parse options using getOpt
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
			
			BufferedImage dimg = scaleImage(img,xPages);
			
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

	/*
	 * Image scaling method.
	 */
	
	private BufferedImage scaleImage(BufferedImage img,int xPages) {
		
		// calculate new width and height
		
		int w2 = COLS_PER_PAGE * xPages;
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
						
			int yPages = (int)Math.ceil((double)dimg.getHeight() / (double)ROWS_PER_PAGE);	

			// Get iText document
			Document document = new Document();
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
		int xStart = xPage * COLS_PER_PAGE;
		int yStart = yPage * ROWS_PER_PAGE;
		int xEnd = (xPage+1) * COLS_PER_PAGE;
		if (xEnd >= (img.getWidth()-1)) xEnd = (img.getWidth()-1);
		int yEnd = (yPage+1) * ROWS_PER_PAGE;
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
				
				// if radius is >0 then draw circle. should be changed
				// to allow different shapes.
				if (r>0f) {
					// Set color
					cb.setRGBColorFillF(0f,0f,0f);
					// create circle path
					cb.circle((10*x)+20,820-10*y,r);
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
		Getopt g = new Getopt("Rasterizer", args, "p:lvh");
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
		System.out.println("-l : Use Pages in Landscape (not implemented)");
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
