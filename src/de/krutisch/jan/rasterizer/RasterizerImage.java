/*
 * Created on Sep 11, 2004
 *
 * $Id$
 * 
 */
package de.krutisch.jan.rasterizer;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * @author jan
 *
 */
public class RasterizerImage {
	static RasterizerImage me;
	static BufferedImage originalImage,rasterImage;
	static int cropX,cropY,cropW,cropH;
	static ColorModel cm;
	static EventLogger logger;
	
	
	public static final int WIDTH = 0,HEIGHT=1;
	public static final int RED = 0, GREEN = 1, BLUE = 2, GREY = 3;
	
	RasterizerImage() {
		// nothing until now		
	}
	
	
	static RasterizerImage getInstance(EventLogger l) {
		if (me==null)
			me = new RasterizerImage();
			logger = l;
		return me;
	}
	
	boolean loadImageFromFile(String filename) {
		try {
			File f = new File(filename);
			originalImage = ImageIO.read(f);
			// set crop
			cropX = 0;
			cropY = 0;
			cropW = originalImage.getWidth();
			cropH = originalImage.getHeight();
			
			return true;
		} catch(Exception e) {
			originalImage = null;
			return false;
		}
	}
	// genuine rescaler
	BufferedImage getResizedImage(int w,int h) {
		BufferedImage dimg = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		// get 2d reference.
		Graphics2D graphics2D = dimg.createGraphics();
		// Set rendering mode to bilinear interpolation
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// scale image by drawing into the new image
		graphics2D.drawImage(originalImage, 0, 0, w, h, null);
		// returning the new image in the correct size
		return dimg;
	}
	int getOriginalImageSize(int orientation) {
		if (originalImage == null) return 0;
		switch(orientation) {
			case WIDTH:
				return originalImage.getWidth();
			case HEIGHT:
				return originalImage.getHeight();
			default:
				return 0;
		}
	}
	int getRasterImageSize(int orientation) {
		if (rasterImage == null) return 0;
		switch(orientation) {
			case WIDTH:
				return rasterImage.getWidth();
			case HEIGHT:
				return rasterImage.getHeight();
			default:
				return 0;
		}
	}
	boolean setCrop(int x,int y,int w,int h) {
		if (x >= originalImage.getWidth()) return false;
		if (x + w > originalImage.getWidth()) return false;
		if (y >= originalImage.getHeight()) return false;
		if (y + h > originalImage.getHeight()) return false;
		
		cropX = x;
		cropY = y;
		cropW = w;
		cropH = h;
		
		return true;
	}
	boolean setRasterImageSize(int w, int h) {
		if (originalImage == null) return false;
		logger.log(EventLogger.VERBOSE,"Resizing Image");
		logger.log(EventLogger.VERBOSE,"Width: "+ w);
		logger.log(EventLogger.VERBOSE,"Height: "+ h);
		Color bgColor = new Color(0,0,0);
		rasterImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = rasterImage.createGraphics();
		// Set rendering mode to bilinear interpolation
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// scale image by drawing into the new image
		graphics2D.drawImage(originalImage, 0, 0, w, h, cropX,cropY,cropX+ cropW, cropY + cropH,bgColor, null);
		// returning the new image in the correct size
		return true;
		
	}
	private ColorModel getColorModel() {
		if (rasterImage == null) {
			return null;
		}
		if (cm == null) {
			cm = rasterImage.getColorModel();
		}
		return cm;
	}
	
	public BufferedImage getOriginalImage() {
		return originalImage;
	}
	
	public int getPixelValue (int x, int y,int color) {
		if (rasterImage == null) return 0;
		ColorModel colorModel = this.getColorModel();
		if (colorModel == null) return 0;
		
		int rgbColor = rasterImage.getRGB(x,y);
		int red = cm.getRed(rgbColor);
		int green = cm.getGreen(rgbColor);
		int blue = cm.getBlue(rgbColor);
		switch(color) {
			case RED:
				return red;
			case GREEN:
				return green;
			case BLUE:
				return blue;
			case GREY:
				int value = red + green + blue;
				return value / 3;
			default:
				return 0;
		}
	}
	
}
