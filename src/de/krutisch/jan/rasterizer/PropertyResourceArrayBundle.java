/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.krutisch.jan.rasterizer;

import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * @author jan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertyResourceArrayBundle {
	private ResourceBundle rb;
	
	PropertyResourceArrayBundle(ResourceBundle bundle){
			rb = bundle;
	}
	String[] getStringArray(String key) {
		String[] sr = new String[1];
		String s = rb.getString(key);
		StringTokenizer st = new StringTokenizer(s,",");
		if (!st.hasMoreTokens()) return null;
		ArrayList al = new ArrayList(st.countTokens());
		while (st.hasMoreTokens()) {
			al.add(st.nextToken());
		}
		return (String[])al.toArray(sr);
		
	}
	
}
