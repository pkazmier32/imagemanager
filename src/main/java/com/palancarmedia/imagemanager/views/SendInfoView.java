package com.palancarmedia.imagemanager.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


public class SendInfoView {
	
	private final static Logger logger = LoggerFactory.getLogger(SendInfoView.class);
	File selectedFile=null;
	
	public JInternalFrame createView() {
		
		List<String> imgItems = new ArrayList<String>();
		
		JInternalFrame frame = new JInternalFrame("Send URL's",
 			   true, // resizable
 			   true, // closable
 			   true, // maximizable
 			   true); // iconifiable
	 	
	    frame.setBounds(50, 50, 650, 350);
	 	frame.setVisible(true); //necessary as of 1.3
	 	Container c = frame.getContentPane();
	 	c.setLayout(new BorderLayout());
	 	
	 	JPanel btnPanel = new JPanel();
	 	JPanel itmsPanel = new JPanel();
	 	DefaultListModel<String> model = new DefaultListModel<>();
	 	
	 	JList<String> wordList = new JList<String>(model);
	 	
	 	JScrollPane scrollPane = new JScrollPane(wordList);
	 	scrollPane.setPreferredSize(new Dimension(640,250));
	 	itmsPanel.add(scrollPane);
	 	
	 	JButton btnOpenFileChooser = new JButton("Open File");
	 	btnOpenFileChooser.addActionListener(new ActionListener() {
 	     
		@Override
		public void actionPerformed(ActionEvent ae) {
			JFileChooser fileChooser = new JFileChooser();
	 	        int returnValue = fileChooser.showOpenDialog(null);
	 	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	 	          selectedFile = fileChooser.getSelectedFile();
	 	          //System.out.println(selectedFile.getName());
	 	          try{
	 	             BufferedReader in = new BufferedReader(new FileReader(selectedFile));
	 	             String line = null;
	 	             while ((line = in.readLine()) != null) {
	 	                //logger.debug(line);
	 	                
	 	                imgItems.add(line);
	 	                model.addElement(line);
	 	             }
		 	         in.close();
		 	         
	 	         }
	 	         catch(IOException ex)
	 	         {
	 	             logger.error("Open plaintext error: "+ex);
	 	         }   
	 	       }
			}
 	    });
	 	btnPanel.add(btnOpenFileChooser);
	 	JLabel statusbar = new JLabel(" ");
	 	
	 	JButton btnSendAll = new JButton("Send All");
	 		 	
	 	btnSendAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				HttpClient client = HttpClientBuilder.create().build();
				HttpGet req = null;
				
				int i=1;
				for (String itm : imgItems) {
					
					String[] flds = itm.split(",");
					StringBuilder url = new StringBuilder("http://www.palancarmediasolutions.com/FitnessLog/AddImageToDb.php?");
					
					try {
						url.append("modelname=").append(URLEncoder.encode(flds[0], "UTF-8"));
						url.append("&series=").append(URLEncoder.encode(flds[1], "UTF-8"));
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					url.append("&imagepath=").append(flds[2]);
					url.append("&imagetype=X");
				
					HttpResponse response;
					try {
						
						req = new HttpGet(url.toString());
						req.addHeader("content-type", "text/xml");
						response = client.execute(req);
						
						BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
						//logger.debug("Item["+i+"] response -> "+ response.getStatusLine().getStatusCode());
						statusbar.setText("Sent : " + i + "/" + imgItems.size());
						req.releaseConnection();
						i++;
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					
				}
				
			}
	 		
	 	});
	 	
	 	btnPanel.add(btnSendAll);
	 	
	 	c.add(itmsPanel, BorderLayout.NORTH);
	 	c.add(btnPanel, BorderLayout.CENTER);
	 	
	 	
	    statusbar.setPreferredSize(new Dimension(-1, 22));
	    statusbar.setBorder(LineBorder.createGrayLineBorder());
	    frame.add(statusbar, BorderLayout.SOUTH);
	    
	     try {
	         frame.setSelected(true);
	     } catch (java.beans.PropertyVetoException e) {}
	     
	     return frame;
	}
	

}
