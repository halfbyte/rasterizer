package de.krutisch.jan.rasterizer;
/*
 * Created on Sep 13, 2004
 *
 * $Id$
 * 
 */
import java.io.File;
import java.util.HashSet;

import javax.swing.filechooser.*;
import javax.imageio.ImageIO;

/* ImageFilter.java is a 1.4 example used by FileChooserDemo2.java. */
public class ImageFileFilter extends FileFilter {
	
	HashSet set;
	
	ImageFileFilter() {
		String[] formatNames = ImageIO.getReaderFormatNames();
	    this.set = new HashSet();
	    for (int i=0; i<formatNames.length; i++) {
	    	String name = formatNames[i].toLowerCase();
	    	this.set.add(name);
	    }
	}
	
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String s = f.getName();
        int i=s.lastIndexOf(".");
        String extension=null;
        if (i > 0 &&  i < s.length() - 1) {
            extension = s.substring(i+1).toLowerCase();
        }
         
        if (extension != null) {
        	if (this.set.contains(extension)) {
        		return true;
        	}
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Images";
    }
}

