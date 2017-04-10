package com.palancarmedia.imagemanager.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.palancarmedia.imagemanager.models.ImageInfo;
import com.palancarmedia.imagemanager.views.SendInfoView;

public class GetImagesByModelName {
	
	private String baseUrl;
	
	private final static Logger logger = LoggerFactory.getLogger(GetImagesByModelName.class);
	
	public void setBaseURL(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public List<ImageInfo> getImageInfo(String modelName, int rating) {
		
		List<ImageInfo> imgList = new ArrayList<ImageInfo>();
		StringBuilder url = new StringBuilder(baseUrl);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet req = null;
		
		try {
			url.append("/getImageInfoByModelName/").append(URLEncoder.encode(modelName, "UTF-8"));
			url.append("/").append(rating);
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

}
