/*
 * Created on Sep 11, 2004
 *
 * $Id$
 *
 */
package de.krutisch.jan.rasterizer;
import java.awt.Color;
import javax.swing.JProgressBar;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author jan
 *
 */
public class RasterizerPdf {
	static RasterizerPdf me;
	static FileOutputStream file;
	static int cropmarks;
	static Rectangle pageSize;
	static int colorMode;
	static float dotSize;
	static float rasterHeight,rasterWidth;
	static int pages;
	static EventLogger logger;
	static boolean landscape;
	public static final int NOCOLOR=0,SIMPLECOLOR=1;
	public static final int NOCROPMARKS=0,CROPMARKS=1,ALLCROPMARKS=2;
	static JProgressBar progressBar;
	static float marginTop=0,marginBottom=0,marginLeft=0,marginRight=0;
	
	
	RasterizerPdf() {
		file =null;
		cropmarks = NOCROPMARKS;
		colorMode = NOCOLOR;
		pageSize = PageSize.A4;
		pages = 3;
		dotSize = 10f;
		rasterHeight = 10f;
		rasterWidth = 10f;
	}
	
	public static RasterizerPdf getInstance(EventLogger l) {
		if (me==null)
			me = new RasterizerPdf();
			logger = l;
		return me;
	}

	public void setProgressBar(JProgressBar pb) {
		progressBar = pb; 
	}
	
	public boolean setOutputFile(String filename) {
		try {
			file = new FileOutputStream(filename);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public void setCropmarks(int c) {
		cropmarks = c;
	}
	public void setLandscape(boolean b) {
		landscape = b;
	}
	
	public void setPageSize(Rectangle ps) {
		pageSize = ps;
	}
	public void setColorMode (int cm) {
		colorMode = cm;
	}
	
	public void setDotSize (float ds) {
		dotSize = ds;
	}
	public void setHorizontalPages(int p) {
		pages = p;
	}
	
	public void setMargins(float a, float b, float c, float d) {
		marginTop = c;
		marginRight = b;
		marginBottom = d;
		marginLeft = a;
	}
	
	private void mapPage(PdfContentByte cb, RasterizerImage ri, Document document, int xPage, int yPage, int colsPerPage, int rowsPerPage) {
		
		float right, bottom;
		boolean doRight = true;
		boolean doBottom = true;
		boolean doTop = true;
		boolean doLeft = true;
	
		
		right= document.left() + (colsPerPage * rasterWidth);

		if (xPage == 0) doLeft  = false;
		if (yPage == 0) doTop	= false;
		
		int xStart = xPage * colsPerPage;
		int yStart = yPage * rowsPerPage;
		int xEnd = (xPage+1) * colsPerPage;
		if (xEnd > (ri.getRasterImageSize(RasterizerImage.WIDTH))) {
			xEnd = (ri.getRasterImageSize(RasterizerImage.WIDTH));
		}
		if (xEnd >= (ri.getRasterImageSize(RasterizerImage.WIDTH))) {
			doRight=false;
		}
		 
		int yEnd = (yPage+1) * rowsPerPage;

		if (yEnd > (ri.getRasterImageSize(RasterizerImage.HEIGHT))) {
			yEnd = (ri.getRasterImageSize(RasterizerImage.HEIGHT));
		} 
		if (yEnd >= (ri.getRasterImageSize(RasterizerImage.HEIGHT))) {
			doBottom = false;
		} 

		if (cropmarks >= CROPMARKS) {
		bottom = document.top() - ((yEnd-yStart) * rasterHeight);
		
		cb.setLineWidth(0.2f);
	   	
	   	
	   	if (doTop || cropmarks == ALLCROPMARKS) {
			cb.moveTo(document.left(),document.top());
			cb.lineTo(document.left()+20f,document.top());
			cb.stroke();
			cb.moveTo(right-20f,document.top());
			cb.lineTo(right,document.top());
			cb.stroke(); 
	   	}
	   	if (doBottom || cropmarks == ALLCROPMARKS) {
			cb.moveTo(document.left(),bottom);
			cb.lineTo(document.left()+20f,bottom);
			cb.stroke();
			cb.moveTo(right-20f,bottom);
			cb.lineTo(right,bottom);
			cb.stroke();			
	   			
	   	}
		if (doLeft || cropmarks == ALLCROPMARKS) {
			cb.moveTo(document.left(),document.top());
			cb.lineTo(document.left(),document.top()-20f);
			cb.stroke();
			cb.moveTo(document.left(),bottom+20f);
			cb.lineTo(document.left(),bottom);
			cb.stroke();

		}
		if (doRight || cropmarks == ALLCROPMARKS) {
			cb.moveTo(right,document.top());
			cb.lineTo(right,document.top()-20f);
			cb.stroke();
			cb.moveTo(right,bottom+20f);
			cb.lineTo(right,bottom);
			cb.stroke();
		}	   	
		

		} // printCropmarks

		// Looping over all Pixels of the page
		for(int x=0;x<(xEnd-xStart);x++) {
			for (int y=0;y<(yEnd-yStart);y++) {
				// Get color value of the pixel
				Color pdfColor = null;
				if (colorMode == SIMPLECOLOR) {
					
					pdfColor = new Color(ri.getPixelValue(x+xStart,y+yStart,RasterizerImage.RED),ri.getPixelValue(x+xStart,y+yStart,RasterizerImage.GREEN),ri.getPixelValue(x+xStart,y+yStart,RasterizerImage.BLUE));
				} else {
					pdfColor = new Color(0,0,0);
				}
				int value = ri.getPixelValue(x+xStart,y+yStart,RasterizerImage.GREY);
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
					cb.setColorFill(pdfColor);
					// create circle path
					cb.circle((rasterWidth*x)+document.left()+(dotSize/2),document.top()-(dotSize/2)-(rasterHeight*y),r);
					// fill path
					cb.fill();
				}
			}		
		}
	}

	
	
	public boolean rasterizeImage(RasterizerImage ri) {
		if (file == null) return false;
		if (ri == null) return false;
		if (pageSize == null) return false;
		if (landscape) pageSize = pageSize.rotate();
		
		int colsPerPage = (int)((pageSize.width()-(marginLeft + marginRight)) / dotSize);
		int rowsPerPage = (int)((pageSize.height()-(marginTop + marginBottom)) / dotSize);
		
		rasterHeight = (pageSize.height() - (marginTop + marginBottom)) / (float)rowsPerPage;
		rasterWidth = (pageSize.width() - (marginLeft + marginRight)) / (float)colsPerPage;
		logger.log(EventLogger.VERBOSE,"Dotsize:" + dotSize);
		logger.log(EventLogger.VERBOSE,"RasterWidth:" + rasterWidth);
		logger.log(EventLogger.VERBOSE,"RasterHeight:" + rasterHeight);
		
		int xSize = colsPerPage * pages;
		int ySize = (int)((float)ri.getOriginalImageSize(RasterizerImage.HEIGHT)/((float)ri.getOriginalImageSize(RasterizerImage.WIDTH)/(float)xSize));
		ri.setRasterImageSize(xSize,ySize);
		
		int yPages = (int)Math.ceil((double)ySize / (double)rowsPerPage);
		
		try {
			Document document = new Document(pageSize);
			document.setMargins(marginLeft,marginRight,marginTop,marginBottom);
			
			PdfWriter writer = PdfWriter.getInstance(document,file);
		
			// Signalling opening.
			document.open();
		
			// Get Stream for raw gfx.
			PdfContentByte cb = writer.getDirectContent();
			
			for(int yPage=0;yPage<yPages;yPage++) {
				for(int xPage=0;xPage<pages;xPage++) {
					// for heavens sake, document needs something on 
					// the page (not done by ContentByte)
					document.add(new Paragraph(""));
					// map one page.
					logger.log(EventLogger.VERBOSE,"Mapping Page " + (xPage +  (yPage*pages) + 1)  );
					if (progressBar != null) {
						progressBar.setValue(100*(xPage +  (yPage*pages) + 1) / (pages * yPages));
						progressBar.setString("Mapping Page " + (xPage +  (yPage*pages) + 1));
					}
					mapPage(cb,ri,document,xPage,yPage,colsPerPage, rowsPerPage);
					// new page					
					document.newPage();
				}
				// new Page
				document.newPage();
			}
			if (progressBar != null) {
				progressBar.setValue(100);
				progressBar.setString("finished.");
			}

			document.close();
		
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
