package de.krutisch.jan.rasterizer;
/*
 * Created on 21.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.Date;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import gnu.getopt.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Rasterizer {

	static final int ROWS_PER_PAGE = 80;
	static final int COLS_PER_PAGE = 56;
	
	private static String inputFilename = new String("");
	private static String outputFilename = new String("");
	
	private static int xPages = 4;
	
	private static boolean landscape = false;
	private static boolean verbose = false;
	
	public static void main(String[] args) {
				
		if (parseOptions(args) == false) return;
		
		
		System.out.println("Rasterizer V0.1 (c) 2004 JanKrutisch");

		long startTime = new Date().getTime();

		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilename));
			
			
			
			document.open();
			
			
			
			/*
			PdfPageEventHelper helper = new PdfPageEventHelper();
			writer.setPageEvent(helper);
			*/			
			PdfContentByte cb = writer.getDirectContent();
			
			BufferedImage img = loadImage(inputFilename);
			if (img == null) {
				System.out.println("image couldn't be opened");
				return;
			} 
			
			verboseOut("-Source image opened-");
			verboseOut("width:"+img.getWidth());
			verboseOut("height:"+img.getHeight());
			
			
			
			int w2 = COLS_PER_PAGE * xPages;
			int h2 = (int)((float)img.getHeight()/((float)img.getWidth()/(float)w2));
			
			int yPages = (int)Math.ceil((double)h2 / (double)ROWS_PER_PAGE);

			verboseOut("-Rescaling image-");
			verboseOut("width:"+w2);
			verboseOut("height:"+h2);
			
			
			
			BufferedImage dimg = new BufferedImage(w2,h2,BufferedImage.TYPE_INT_RGB);
			
			Graphics2D graphics2D = dimg.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(img, 0, 0, w2, h2, null);
			
			
			ColorModel cm = dimg.getColorModel();
			
			verboseOut("-Converting-");
			
			for(int yPage=0;yPage<yPages;yPage++) {
				for(int xPage=0;xPage<xPages;xPage++) {
					verboseOut("-Page: " + (xPage + yPage*xPages) + "-");
					document.add(new Paragraph(" "));
					int xStart = xPage * COLS_PER_PAGE;
					int yStart = yPage * ROWS_PER_PAGE;
					int xEnd = (xPage+1) * COLS_PER_PAGE;
					if (xEnd >= (w2-1)) xEnd = (w2-1);
					int yEnd = (yPage+1) * ROWS_PER_PAGE;
					if (yEnd >= (h2-1)) yEnd = (h2-1);
					//System.out.println("xStart:" + xStart + " yStart:" + yStart + " xEnd:" + xEnd + " yEnd:" + yEnd);

					for(int x=0;x<(xEnd-xStart);x++) {
						for (int y=0;y<(yEnd-yStart);y++) {
							int color = dimg.getRGB(x+xStart,y+yStart);
							int value = cm.getRed(color);
							value += cm.getGreen(color);
							value += cm.getBlue(color);
							value /= 3;
							int rgb = value + (value*0x100) + (value*0x10000);
							dimg.setRGB(x,y,rgb);
							value *= 120;
							value /= 0xFF;
							value = 120 - value;
					
							if (value<0) value = 0;

							float r = (float)Math.sqrt(value / Math.PI);
							if (r>0f) {
								cb.setRGBColorFillF(0f,0f,0f);
								cb.circle((10*x)+20,820-10*y,r);
								cb.fill();
							} else {
						
							}
					
						}		
					}


				document.newPage();
				}
				document.newPage();
			}
			verboseOut("write file...");
			document.close();
			long fullTime = new Date().getTime() - startTime;
			System.out.println( (xPages * yPages) + " Pages (" + (w2*h2) + " Rasterdots) written to " + outputFilename + "\ndone in " + fullTime + " Milliseconds.");
			
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	private static boolean parseOptions(String[] args) {
		Getopt g = new Getopt("Rasterizer", args, "p:lvh");
		int c;
		String arg;
		while((c= g.getopt()) != -1) {
			switch(c) {
				case 'l':
					landscape = true;
					break;
				case 'v':
					verbose = true;
					break;
				case 'h':
					printHelp();
					return false;
				case 'p':
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
		// Finally: Da elements.
		int no = g.getOptind();
		if (no < args.length) {
			inputFilename = args[no];
		}
		no++;
		if (no < args.length) {
			outputFilename = args[no];
		}
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
	
	private static void printHelp() {
		System.out.println("Usage: java Rasterizer [-p pages] [-l] [-h] [-v] inputfile [outputfile]");
		System.out.println("-p : Number of horizontal pages (vertical will be chosen according to aspect ratio of source image)");
		System.out.println("-l : Use Pages in Landscape (not implemented)");
		System.out.println("-h : The stuff you are reading right now. No Action.");
		System.out.println("-v : Verbose output");
		System.out.println("inputfile  : Input file (.jpeg, .gif, .png)");
		System.out.println("outputfile : Output file (.pdf) defaults to out.pdf. '.pdf' is added if filename doesn't end on .pdf");
	}
	
	private static BufferedImage loadImage(String filename) {
		// Use a MediaTracker to fully load the image.
		try {
			File f = new File(filename);
			BufferedImage bi = ImageIO.read(f);
			return bi;			
		} catch(Exception e) {
			return null;
		}
	}
	private static void saveImage(String filename,BufferedImage img) {
		// Use a MediaTracker to fully load the image.
		try {
			File f = new File(filename);
			ImageIO.write(img,"jpg",f);
			return;
		} catch(Exception e) {
			return;
		}
	}
	private static void verboseOut(String line) {
		if (verbose) {
			System.out.println(line);
		}
		
	}
}
