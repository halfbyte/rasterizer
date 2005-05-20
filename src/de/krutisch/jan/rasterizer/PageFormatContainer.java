/*
 * Created on 20.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.krutisch.jan.rasterizer;

import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Jan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PageFormatContainer {
	private Vector pageFormatList;
	private EventLogger logger;
	
	public PageFormatContainer(EventLogger logger) {
		pageFormatList = new Vector();
		this.logger = logger;
		//parsePaperSizeXML(url);
	}
	
	public PageFormatContainer() {
		pageFormatList = new Vector();
	}
	public void add(PageFormat format) {
		pageFormatList.add(format);
	}
	public Vector getVector() {
		return pageFormatList;
	}
	
	void parsePaperSizeXML(URL url) {
		try {
			SAXReader reader = new SAXReader();
	        Document document = reader.read(url);
	        parsePaperSizeXML(document);
		} catch (Exception e) {
			logger.log(EventLogger.ERROR,e.getMessage());
		}
	}
	void parsePaperSizeXML(File file) {
		try {
			SAXReader reader = new SAXReader();
	        Document document = reader.read(file);
	        parsePaperSizeXML(document);
		} catch (Exception e) {
			logger.log(EventLogger.ERROR,e.getMessage());
		}
	}
	
	int getSize() {
		if (pageFormatList != null) return pageFormatList.size();
		return 0;
	}
	
	public void parsePaperSizeXML(Document doc) {
		//pageFormatList = new Vector();
		
		logger.log(EventLogger.DEBUG,"Start");
		/* Document doc = null;
		try {
			doc = this.parse(url);
		} catch (Exception e) {
			logger.log(EventLogger.ERROR,"Error:" + e);
		}
		*/
		if (doc!=null) {
			logger.log(EventLogger.DEBUG,"Doc");
			Element root = doc.getRootElement();
//			 iterate through child elements of root
	        for ( Iterator i = root.elementIterator("PageFormat"); i.hasNext(); ) {
	        	PageFormat pf = new PageFormat();
	        	
	        	int width=0;
	    		int height=0;
	    		int marginTop=0;
	    		int marginRight=0;
	    		int marginBottom=0;
	    		int marginLeft=0;
	            
	        	Element pageFormat = (Element) i.next();
	            String name,description;
		        if ((name = pageFormat.attribute("name").getText())!=null) {
		        	logger.log(EventLogger.DEBUG,"---" + name+"----");
		        	pf.setName(name);
		        }
		        if ((description = pageFormat.attribute("description").getText())!=null) {
		        	logger.log(EventLogger.DEBUG,"d--" + description+"----");
		        	pf.setDescription(description);
		        }
		        
	            for ( Iterator j = pageFormat.elementIterator(); j.hasNext(); ) {
		        	Element element = (Element) j.next();
		        		
		        	if (element.getName().equals("Width")) {
		        		logger.log(EventLogger.DEBUG,element.getText());
		        		try {
		        			width = Integer.parseInt(element.getTextTrim());
		        			
		        		} catch (NumberFormatException e) {
		        			logger.log(EventLogger.DEBUG,"Error: " + e);
		        			width = 0;
		        		}
		        		pf.setWidth(width);
		        	}
		        	if (element.getName().equals("Height")) {
		        		logger.log(EventLogger.DEBUG,element.getText());
		        		try {
		        			height = Integer.parseInt(element.getTextTrim());
		        			
		        		} catch (NumberFormatException e) {
		        			logger.log(EventLogger.DEBUG,"Error: " + e);
		        			height = 0;
		        		}
		        		pf.setHeight(height);
		        	}
		        	if (element.getName().equals("Margins")) {
		        		
			            for ( Iterator k = element.elementIterator(); k.hasNext(); ) {
			            	Element margin = (Element) k.next();
				        	if (margin.getName().equals("Top")) {
				        		logger.log(EventLogger.DEBUG,margin.getText());
				        		try {
				        			pf.setMarginTop(Integer.parseInt(margin.getTextTrim()));
				        		} catch (NumberFormatException e) {
				        			logger.log(EventLogger.DEBUG,"Error: " + e);
				        			pf.setMarginTop(0);
				        		}
				        	}
				        	if (margin.getName().equals("Left")) {
				        		logger.log(EventLogger.DEBUG,margin.getText());
				        		try {
				        			pf.setMarginLeft(Integer.parseInt(margin.getTextTrim()));
				        		} catch (NumberFormatException e) {
				        			logger.log(EventLogger.DEBUG,"Error: " + e);
				        			pf.setMarginLeft(0);
				        		}
				        	}
				        	if (margin.getName().equals("Bottom")) {
				        		logger.log(EventLogger.DEBUG,margin.getText());
				        		try {
				        			pf.setMarginBottom(Integer.parseInt(margin.getTextTrim()));
				        		} catch (NumberFormatException e) {
				        			logger.log(EventLogger.DEBUG,"Error: " + e);
				        			pf.setMarginBottom(0);
				        		}
				        	}
				        	if (margin.getName().equals("Right")) {
				        		logger.log(EventLogger.DEBUG,margin.getText());
				        		try {
				        			pf.setMarginRight(Integer.parseInt(margin.getTextTrim()));
				        		} catch (NumberFormatException e) {
				        			logger.log(EventLogger.DEBUG,"Error: " + e);
				        			pf.setMarginRight(0);
				        		}
				        	}

			            	
			            	
			            }

		        	}
		        	
		        }
	            
	            if (pf.isValid()) {
	            	this.add(pf);
	            } else {
	            	System.out.print("not Valid");
	            }
	        }
			
		}
		
		
	}
	
	
	public Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

	
	
}
