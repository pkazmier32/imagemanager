package com.palancarmedia.imagemanager.views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;


public class S3ImageViewer extends JDialog {
	
	//String imagePath="";
	private final static Logger logger = LoggerFactory.getLogger(S3ImageViewer.class);
	JPanel imgPanel = new JPanel();
	
	public S3ImageViewer(String imagePath) {
		
	 	Properties prop = new Properties();
	 	InputStream input = null;
	 	String AWSKeyId="";
	 	String AWSSecretKey="";
	 	
	 	String filename = "app.properties";
		input = S3ImageViewer.class.getClassLoader().getResourceAsStream(filename);
		if(input==null) {
	       System.out.println("Sorry, unable to find " + filename);
		} else {
			try  {
			    prop.load(input);
				AWSKeyId = prop.getProperty("AWSAccessKeyID");
				AWSSecretKey=prop.getProperty("AWSSecretKey");
			} catch (Exception ex) {
				logger.error("Failed to read AWS properties");
			}
			
		}
	 	
	 	BasicAWSCredentials creds = new BasicAWSCredentials(AWSKeyId, AWSSecretKey);
	 	AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(Regions.US_EAST_1).build();
	 	BufferedImage srcImage=null;
	 	
	    S3Object s3Object = s3Client.getObject(new GetObjectRequest("PKazPhotoBackup", imagePath));
	    InputStream objectData = s3Object.getObjectContent();

	    // Read the source image
	    try {
			srcImage = ImageIO.read(objectData);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	 	
	 	if (imagePath != null || imagePath.length() > 1) {
	 		//ImageIcon ii = new ImageIcon(this.imagePath);
	 		ImageIcon ii = new ImageIcon(srcImage);
	 		JScrollPane jsp = new JScrollPane(new JLabel(ii));
	 		jsp.setPreferredSize(new Dimension(1000,650));
	 		imgPanel.add(jsp);
	 	}
	 	
	 	add(imgPanel);
	 	setBounds(10, 10, 1000, 650);
	 	pack();
	 	setVisible(true);
	    
	}
	
	/*
	public void setImagePath(String path) {
		this.imagePath = path;
	} */

}
