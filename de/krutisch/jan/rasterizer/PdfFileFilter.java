/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.krutisch.jan.rasterizer;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * @author jan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class PdfFileFilter extends FileFilter {

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
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
            if (extension.equals("pdf")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Adobe Portable Document Format";
	}

}
