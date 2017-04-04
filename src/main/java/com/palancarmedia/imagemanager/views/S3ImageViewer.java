package com.palancarmedia.imagemanager.views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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


public class S3ImageViewer {
	
	String imagePath="";
	private final static Logger logger = LoggerFactory.getLogger(S3ImageViewer.class);
		
	public JInternalFrame createView() {
		JInternalFrame frame = new JInternalFrame("S3 Image Viewer",
	 			   true, // resizable
	 			   true, // closable
	 			   true, // maximizable
	 			   true); // iconifiable
		 	
	    frame.setBounds(10, 10, 1000, 650);
	 	frame.setVisible(true); //necessary as of 1.3
	 	//frame.setResizable(false);
	 	
	 	BasicAWSCredentials creds = new BasicAWSCredentials("AKIAIG43BCYOOESPLKTQ", "Biy4zfEz18CyROnHo0tzNcy2T4srj2yq+Xcj0JDB"); 
	 	AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(Regions.US_EAST_1).build();
	 	BufferedImage srcImage=null;
	 	
	    S3Object s3Object = s3Client.getObject(new GetObjectRequest("PKazPhotoBackup", this.imagePath));
	    InputStream objectData = s3Object.getObjectContent();

	    // Read the source image
	    try {
			srcImage = ImageIO.read(objectData);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	 	
	 	try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	Container c = frame.getContentPane();
	 	
	 	JPanel imgPanel = new JPanel();
	 	
	 	if (this.imagePath != null || this.imagePath.length() > 1) {
	 		//ImageIcon ii = new ImageIcon(this.imagePath);
	 		ImageIcon ii = new ImageIcon(srcImage);
	 		JScrollPane jsp = new JScrollPane(new JLabel(ii));
	 		jsp.setPreferredSize(new Dimension(1000,650));
	 		imgPanel.add(jsp);
	 	}
	 	
	    frame.add(imgPanel);
	    frame.setLayer(0);
	 	frame.pack();
	 	return frame;
	}
	
	public void setImagePath(String path) {
		this.imagePath = path;
	}

}
