package com.palancarmedia.imagemanager.utils;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.palancarmedia.imagemanager.views.S3ImageViewer;

public class PropertiesFileHelper {
	
	private final static Logger logger = LoggerFactory.getLogger(PropertiesFileHelper.class);
	
	private Properties props=null;
	
	public String getPropertyValue(String key) {
		
		String val="";
				
		if (this.props != null) {
			if (props.containsKey(key))
				val = props.getProperty(key);
			else 
				logger.error(key + " not found in properties file");
		} else {
			this.props = loadProperties();
			if (props.containsKey(key))
				val = props.getProperty(key);
			else 
				logger.error(key + " not found in properties file");
		}
				
		return val;
		
	}
	
	private Properties loadProperties() {
		Properties props = new Properties();
	 	InputStream input = null;
	 	
	 	String filename = "app.properties";
		input = PropertiesFileHelper.class.getClassLoader().getResourceAsStream(filename);
		if(input==null) {
	       System.out.println("Sorry, unable to find " + filename);
	       
		} else {
			try  {
			    props.load(input);
				
			} catch (Exception ex) {
				logger.error("Failed to read AWS properties");
				
			}
			
		}
	 	
	 	return props;
	}

}
