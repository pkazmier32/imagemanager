package com.palancarmedia.imagemanager.views;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RenameFilesDlg extends JDialog implements ActionListener,PropertyChangeListener {
	
	private JTextField textField = new JTextField();
	private JPanel jp = new JPanel();
	private String filePrefix="";
	JButton btnAccept = new JButton("Accept");
	JButton btnCancel = new JButton("Cancel");
	
	
	public RenameFilesDlg() {
		//super(true);
		
		this.setBounds(new Rectangle(10, 10, 300, 140));
		jp.setLayout(null);
		
		textField.setBounds(new Rectangle(15, 10, 250, 25));
		
		btnAccept.setBounds(new Rectangle(45, 50, 90, 30));
		btnAccept.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				filePrefix = textField.getText();
				RenameFilesDlg.this.setVisible(false);
				dispose();
			}
			
		});
		
		
		btnCancel.setBounds(new Rectangle(140, 50, 90, 30));
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				RenameFilesDlg.this.setVisible(false);
				dispose();
			}
			
		});
		
		jp.add(textField);
		jp.add(btnAccept);
		jp.add(btnCancel);
		setContentPane(jp);
	}
	
	public String getNewFilePrefix() {
		return this.filePrefix;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
