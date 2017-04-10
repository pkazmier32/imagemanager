package com.palancarmedia.imagemanager.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.palancarmedia.imagemanager.models.ImageInfo;
import com.palancarmedia.imagemanager.utils.PropertiesFileHelper;

public class GetImageInfo {
	
	private String baseUrl;
	
	public class ModelListItem {
		String modelName;
		
		public String getModelName() {
			return this.modelName;
		}
		
		public void setModelName(String name) {
			this.modelName = name;
		}
	}
	
	private final static Logger logger = LoggerFactory.getLogger(GetImagesByModelName.class);
	
	
	public static List<ImageInfo> getImageInfo(String restMethod, Set<String> params) {
		
		PropertiesFileHelper prp = new PropertiesFileHelper();
		String baseUrl = prp.getPropertyValue("baseUrl");
		if (baseUrl == null) {
			logger.error("Base url not found in properties");
			return null;
		}
		
		List<ImageInfo> imgList = new ArrayList<ImageInfo>();
		StringBuilder url = new StringBuilder(baseUrl);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet req = null;
		
		try {
			url.append("/").append(restMethod);
			
			if (params != null && params.size() > 0) {
				for (String p : params) {
					url.append("/").append(URLEncoder.encode(p, "UTF-8"));
				}
			}
		
			logger.debug(url.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		HttpResponse response;
		try {
			
			req = new HttpGet(url.toString());
			req.addHeader("content-type", "application/json");
			response = client.execute(req);
			
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			//logger.debug("Item["+i+"] response -> "+ response.getStatusLine().getStatusCode());
			 StringBuilder sb = new StringBuilder();

		    String line;
		   
		    while ((line = rd.readLine()) != null) {
		        sb.append(line);
		    }
		    
		    Type listType = new TypeToken<ArrayList<ImageInfo>>(){}.getType();
		    imgList = new Gson().fromJson(sb.toString(), listType);
			
			req.releaseConnection();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return imgList;
	}
	
	public static List<String> getEntityList(String restMethod, Set<String> params) {
		
		PropertiesFileHelper prp = new PropertiesFileHelper();
		String baseUrl = prp.getPropertyValue("baseUrl");
		if (baseUrl == null) {
			logger.error("Base url not found in properties");
			return null;
		}
		
		List<String> entityList = new ArrayList<String>();
		StringBuilder url = new StringBuilder(baseUrl);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet req = null;
		
		try {
			url.append("/").append(restMethod);
			
			if (params != null && params.size() > 0) {
				for (String p : params) {
					url.append("/").append(URLEncoder.encode(p, "UTF-8"));
				}
			}
		
			logger.debug(url.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		HttpResponse response;
		try {
			
			req = new HttpGet(url.toString());
			req.addHeader("content-type", "application/json");
			response = client.execute(req);
			
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			//logger.debug("Item["+i+"] response -> "+ response.getStatusLine().getStatusCode());
			 StringBuilder sb = new StringBuilder();

		    String line;
		   
		    while ((line = rd.readLine()) != null) {
		        sb.append(line);
		    }
		    
		    Type listType = new TypeToken<ArrayList<ModelListItem>>(){}.getType();
		    List<ModelListItem> li = new ArrayList<ModelListItem>();
		    li = new Gson().fromJson(sb.toString(), listType);
			li.forEach(i -> entityList.add(i.modelName));
		    
			req.releaseConnection();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return entityList;
		
	}

}
