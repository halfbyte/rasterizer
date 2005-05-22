/*
 * Created on 22.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.krutisch.jan.rasterizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * @author Jan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImagePreview extends JComponent {
	
	
	private float pageAspect = 2f;
	private float hPages = 3;
	private BufferedImage image;
	
	public ImagePreview() {
		setPreferredSize(new Dimension(200, 200));
		this.setMinimumSize(new Dimension(100,200));
	}
	
	/*public ImagePreview(String filename) {
		loadImage(filename);
	}
	
	public void setImage(String filename) {
		loadImage(filename);
		
	}
	*/
	public void setImage(BufferedImage image) {
		this.image = image; 
		repaint();
	}
	
	public void setPageData(PageFormat pf,int hPages,boolean landscape) {
		float pageWidth = (float) (pf.getWidth() - pf.getMarginLeft() - pf.getMarginRight());
		float pageHeight = (float) (pf.getHeight() - pf.getMarginTop() - pf.getMarginBottom());
		pageAspect = pageWidth / pageHeight;
		if (landscape) pageAspect = 1/pageAspect;
		
		this.hPages = hPages;
		repaint();
	}
	/*
	private void loadImage(String filename) {
		System.out.println("loading " + filename);
		if (filename==null || filename.length()==0) return;
		try {
			File f = new File(filename);
			image = ImageIO.read(f);
		} catch(Exception e) {
			image = null;
			System.out.println("loading gfx failed");
		}
		System.out.println("loading " + filename + "succeeded");
		repaint();
	}
	*/
	protected void paintComponent(Graphics g) {
		

		
        
       
        
       
        
        
        
        //Graphics2D gfx = (Graphics2D)this.getGraphics();
        Graphics2D gfx = (Graphics2D)g;
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        		RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        // Background
        gfx.setBackground(new Color(255,255,255));
        gfx.clearRect(0,0,getWidth(),getHeight());
        
        // Images
        if (image == null) return;
        
        float aspect = 1;
        float aspectX = (float)getWidth() / (float)image.getWidth();
        float aspectY = (float)getHeight() / (float)image.getHeight();
        
        if (aspectX < aspectY) {
        	aspect = aspectX;
        } else {
        	aspect = aspectY;
        }
        
        int width = (int)((float)image.getWidth() * aspect);
        int height = (int)((float)image.getHeight() * aspect);
        
        
        //int height = getHeight();
        
        
        
        // Pagegitter
        int pageWidth = (int)((float)width / hPages);
        int pageHeight = (int)((float)pageWidth / pageAspect);
        int vPages = (int)Math.ceil((double)height / (double)pageHeight);
        /*
        System.out.println("vPages:" + vPages);
        System.out.println("height:" + height);
        System.out.println("width:" + width);
        System.out.println("pHeight:" + pageHeight);
        System.out.println("pWidth:" + pageWidth);
        */
        
        //if (height%pageHeight >0) vPages++;
        
        if (vPages * pageHeight > getHeight()) {
        	float factor = (float)getHeight() / (float)(vPages * pageHeight);
        	width = (int) ((float)width * factor);
        	height = (int) ((float)height * factor);
        	pageHeight = (int) ((float)pageHeight * factor);
        	pageWidth = (int) ((float)pageWidth * factor);
        	
        }
        gfx.drawImage(image, 0, 0, width, height,0,0,image.getWidth(),image.getHeight(),new Color(0,0,0), null);
        for (int y=0;y<vPages;y++) {
        	for (int x=0;x<hPages;x++) {
        		gfx.drawRect(x*pageWidth,y*pageHeight,pageWidth,pageHeight);
        	}
        }
        
        
    }

}
