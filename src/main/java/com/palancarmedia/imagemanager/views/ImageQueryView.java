package com.palancarmedia.imagemanager.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.palancarmedia.controllers.GetImagesByModelName;
import com.palancarmedia.models.ImageInfo;

public class ImageQueryView extends JInternalFrame {
	
	private final static Logger logger = LoggerFactory.getLogger(ImageQueryView.class);
	
	JPanel pnlMain = new JPanel();
	int selectedRowIndex=0;
	DefaultTableModel m_Model = new DefaultTableModel();
    JTable table = new JTable(m_Model);
    String modelName="";
    String modelRating="";
	
	public ImageQueryView() {
		
		super("REST Image Query", 
				true, // Resizable
				true, // closable
				true, // maximizable
				true); //icon
		
		getContentPane().setLayout(null);
		
		createView();
		getContentPane().add(pnlMain);
		
		setSize(750, 550);
		setVisible(true);
	}
	
	private void createView() {

		table.sizeColumnsToFit(5);
		m_Model.addColumn("Model Name");
		m_Model.addColumn("Series Name");
		m_Model.addColumn("Rating");
		m_Model.addColumn("Image Key");
		m_Model.addColumn("Bucket Name");
		table.removeColumn(table.getColumnModel().getColumn(4));
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(2).setPreferredWidth(25);
		
		Properties prop = new Properties();
	 	InputStream input = null;
	 	String AWSKeyId="";
	 	String AWSSecretKey="";
	 	
	 	JComboBox modelList = new JComboBox();
	 	modelList.addItem("Niemira");
	 	modelList.addItem("Candice B");
	 	modelList.setBounds(10, 15, 125, 25);
	 	
	 	JComboBox ratingList = new JComboBox();
	 	ratingList.addItem("4");
	 	ratingList.addItem("5");
	 	ratingList.addItem("6");
	 	ratingList.setBounds(10, 55, 125, 25);
	 	
	 	modelList.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox o = (JComboBox)e.getSource();
				modelName = (String)o.getSelectedItem();
			}
		});
	 	
	 	ratingList.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox o = (JComboBox)e.getSource();
				modelRating = (String)o.getSelectedItem();
			}
		});
	 	
	 	
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
	 	
	 	JPanel pnlCriteria = new JPanel();
	 	pnlCriteria.setLayout(null);
	 	pnlCriteria.setBounds(5, 5, 150, 480);
	 	pnlCriteria.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(pnlCriteria.getBackground().darker(), pnlCriteria.getBackground().brighter()), "Search Criteria"));
	 	
	 	JPanel pnlImage = new JPanel();
	 	pnlImage.setLayout(null);
	 	pnlImage.setBounds(160, 225, 550, 250);
	 	pnlImage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(pnlImage.getBackground().darker(), pnlImage.getBackground().brighter()), "Image"));
	 	
	 	
	 	JButton btnQuery = new JButton("Send Query");
	 	
	 	btnQuery.setBounds(10, 450, 125, 25);
	 	
	 	btnQuery.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				while (m_Model.getRowCount() > 0)
				{
					m_Model.removeRow(0);
				}
				
				GetImagesByModelName imgsController = new GetImagesByModelName();
				imgsController.setBaseURL("http://palancarmediasolutions.com/FitnessLog2/images");
				//List<ImageInfo> imgList = imgsController.getImageInfo("Niemira", 5);
				List<ImageInfo> imgList = imgsController.getImageInfo(modelName, Integer.parseInt(modelRating));
				
				for (ImageInfo ii : imgList) {
					Object[] rowData = new Object[5];
				 	rowData[0] = ii.getModelName();
				 	rowData[1] = ii.getSeriesName();
				 	rowData[2] = ii.getImageRating();
				 	rowData[4] = ii.getBucketName();
				 	rowData[3] = ii.getImageKey();
				 	
				 	m_Model.addRow(rowData);
				}
				//	imgList.forEach(ii -> logger.debug(ii.toString()));
			}
		});
	 	
	 	JPanel jp = new JPanel();
	 	
	 	jp.setBounds(new Rectangle(150, 20, 245, 225));
		//jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(jp.getBackground().darker(), jp.getBackground().brighter()), "Image"));
		pnlImage.add(jp);
		
	 	ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	
            	if (e.getValueIsAdjusting()) return;
            	
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                	jp.removeAll();
                	pnlImage.remove(jp);
                	
                	pnlImage.invalidate();
                	pnlImage.repaint();
                	//pnlMain.remove(pnlImage);
                	
                	pack();
                    setSize(750, 550);
                    
                    int selectedRow = lsm.getMinSelectionIndex();
                    selectedRowIndex = selectedRow;
                    
                    TableModel tm = (DefaultTableModel) table.getModel();
                    Object imgpth = tm.getValueAt(selectedRow, 3);
                    
                    Image img;
                    
            	 	BufferedImage srcImage=null;
            	 	
            	    S3Object s3Object = s3Client.getObject(new GetObjectRequest("PKazPhotoBackup", imgpth.toString()));
            	    InputStream objectData = s3Object.getObjectContent();
            	    
                    try {
                    	//File iFile = new File("c:\\temp\\imgs\\Test11_1.jpg");
                        
                    	//srcImage = ImageIO.read(iFile);
						img = ImageIO.read(objectData);
						
						Image resizedImage = img.getScaledInstance(jp.getWidth(), jp.getHeight(), Image.SCALE_SMOOTH);
						//Image resizedImage = srcImage.getScaledInstance(jp.getWidth(), jp.getHeight(), Image.SCALE_SMOOTH);
						if (resizedImage != null) {
							
							JLabel imgLbl = new JLabel(new ImageIcon(resizedImage));
	                        //imgLbl.repaint();
							
	                        jp.add(imgLbl);
	                        pnlImage.add(jp);
	                        pnlImage.revalidate();
	                        
	                        pack();
	                        setSize(750, 550);
						}
					} catch (IOException e1) {
						logger.debug("Not an image file");
						//e1.printStackTrace();
					}
                    
                }
            	
            }
        });
	 	
	 	table.addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
        		
        		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        		        		
        		JTable t = (JTable)e.getSource();
        		DefaultTableModel tm = (DefaultTableModel) t.getModel();
            	//Object bkt = tm.getValueAt(selectedRowIndex, 4);
                Object imgkey = tm.getValueAt(selectedRowIndex, 3);
                
                 if(e.getKeyCode()==KeyEvent.VK_ENTER) {
        			
                	JDesktopPane dsk = (JDesktopPane)getContentPane().getParent();
                	S3ImageViewer vw = new S3ImageViewer();
                	vw.setImagePath(imgkey.toString());
                	dsk.add(vw.createView());
                	
        		}
            }
        });
	 	
	 	pnlCriteria.add(btnQuery);
	 	pnlCriteria.add(modelList);
	 	pnlCriteria.add(ratingList);
	 	
	    JScrollPane spResults = new JScrollPane(table);
	    spResults.setBounds(5, 8, 530, 200);
	    //spResults.setPreferredSize(new Dimension(200,200));
	    
	 	JPanel pnlResults = new JPanel();
	 	pnlResults.setLayout(null);
	 	pnlResults.setBounds(160, 5, 550, 210);
	 	//pnlResults.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(pnlResults.getBackground().darker(), pnlResults.getBackground().brighter()), "Results"));
	 	pnlResults.add(spResults);
	 	
	 	
	 	JLabel statusbar = new JLabel(" ");
	    statusbar.setBounds(0, 495, 700, 22);
	    statusbar.setBorder(LineBorder.createGrayLineBorder());
	   	    
	    pnlMain.add(pnlCriteria);
		pnlMain.add(pnlResults);
		pnlMain.add(pnlImage);
		pnlMain.add(statusbar);
		pnlMain.setBounds(0, 0, 750, 550);
		pnlMain.setLayout(null);
		
		this.pack();
		
	}

}
