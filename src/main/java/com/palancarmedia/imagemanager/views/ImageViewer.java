package com.palancarmedia.imagemanager.views;

import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImageViewer {
	
	String imagePath="";
	
	public JInternalFrame createView() {
		JInternalFrame frame = new JInternalFrame("Send URL's",
	 			   true, // resizable
	 			   true, // closable
	 			   true, // maximizable
	 			   true); // iconifiable
		 	
	    frame.setBounds(10, 10, 1000, 650);
	 	frame.setVisible(true); //necessary as of 1.3
	 	frame.setResizable(false);
	 	
	 	try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	Container c = frame.getContentPane();
	 	
	 	JPanel imgPanel = new JPanel();
	 	
	 	if (this.imagePath != null || this.imagePath.length() > 1) {
	 		ImageIcon ii = new ImageIcon(this.imagePath);
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
