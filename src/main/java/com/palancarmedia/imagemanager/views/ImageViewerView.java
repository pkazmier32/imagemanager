package com.palancarmedia.imagemanager.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ImageViewerView {
	
	private final static Logger logger = LoggerFactory.getLogger(SendInfoView.class);
	int selectedRowIndex=0;
	int numberOfRows=0;
	String currentPath="";
	JTable table=null;
	
	public JInternalFrame createView() {
		
		JInternalFrame frame = new JInternalFrame("Send URL's",
	 			   true, // resizable
	 			   true, // closable
	 			   true, // maximizable
	 			   true); // iconifiable
		
		boolean ALLOW_ROW_SELECTION = true;
		 	
	    frame.setBounds(10, 10, 900, 1050);
	 	frame.setVisible(true); //necessary as of 1.3
	 	Container c = frame.getContentPane();
	 	c.setLayout(new BorderLayout());
	 	
	 	
	 	Vector<String> columns = new Vector<String>();
	 	columns.add("File Name");
	 	columns.add("Size");
	 	
	 	JPanel treePanel = new JPanel();
	 	
	 	JMenuBar menuBar = new JMenuBar();
		
		JMenu renameFiles = new JMenu("File");
		menuBar.add(renameFiles);
		
		JMenuItem menuItem = new JMenuItem("Rename Files");
        menuItem.setActionCommand("renamefiles");
        
        renameFiles.add(menuItem);
		
		frame.add(menuBar, BorderLayout.NORTH);
	 	
	 	JLabel statusbar = new JLabel(" ");
	    statusbar.setPreferredSize(new Dimension(-1, 22));
	    statusbar.setBorder(LineBorder.createGrayLineBorder());
	    frame.add(statusbar, BorderLayout.SOUTH);
	    
        menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RenameFilesDlg dlg = new RenameFilesDlg();
				dlg.setVisible(true);
				
				dlg.addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent e) {
						//System.out.println("New file name: " + dlg.getNewFilePrefix());
						
						if (dlg.getNewFilePrefix() != null && dlg.getNewFilePrefix().length() > 1) {
							File f = new File(currentPath);
						 	URI u = f.toURI();
						 	try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(u))) {
						 		int fileNumber=1;
						 		for (Path file : ds) {
						 			File fi = new File(currentPath + file.getFileName().toString());
						 			
						 			String[] fileParts = fi.getName().toString().split("\\.(?=[^\\.]+$)");
						 			String fileExt = fileParts[1];
						 			StringBuilder newFileName = new StringBuilder(dlg.getNewFilePrefix());
						 			newFileName.append("_").append(fileNumber).append(".").append(fileExt);
						 			
						 			File newFile = new File(currentPath + "\\" + newFileName.toString());
						 			fi.renameTo(newFile);
						 			fileNumber++;
						 		}
						 		
						 	} catch (Exception ex) {
						 		System.out.println("File rename exception: " + ex.getLocalizedMessage());
						 	}
						 	
						 	Vector<Vector> rowData = new Vector<Vector>();
						 	try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(u))) {
						 		rowData.clear();
						 		numberOfRows=0;
						 		DefaultTableModel tm = (DefaultTableModel) table.getModel();
					 			for (Path file : ds) {
						 			File fi = new File(currentPath + file.getFileName().toString());
						 			
						 			if (fi.getName().contains(".")) {
						 				Vector<String> row = new Vector<String>();
						 				//System.out.println(fi.getName() + " : " + fi.length());
						 				row.add(fi.getName());
						 				row.add(String.valueOf(fi.length()).format("%,d%n",fi.length()));
						 				row.add(currentPath);
						 				rowData.add(row);
						 				numberOfRows++;
						 			}
						 			
						 		}
					 			tm.setDataVector(rowData, columns);
					 			tm.fireTableDataChanged();
					 			selectedRowIndex=0;
						 	} catch (Exception e1) {
								logger.error(e1.getLocalizedMessage());
							}
						 	
					 		frame.pack();
						}
					}
				});
			}
        	
        });
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Workstation");
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        paths = File.listRoots();
        for (File pth:paths) {
        	
            DefaultMutableTreeNode drive = new DefaultMutableTreeNode(pth);
            
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(pth.toURI()), path -> path.toFile().isDirectory())) {
    	 		for (Path file : ds) {
    	 			DefaultMutableTreeNode dirName = new DefaultMutableTreeNode(file.getFileName());
    	 			//root.add(dirName );
    	 			drive.add(dirName);
    	 			root.add(drive);
    	 		}
    	 	} catch (Exception e1) {
    			logger.error(e1.getLocalizedMessage());
    		}
            
        }
        
        
	 	DefaultTreeModel treeModel = new DefaultTreeModel(root);
	 	JTree tree = new JTree(treeModel);
	 	int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
	 	tree.getSelectionModel().setSelectionMode(mode);
	 	
	 	Vector<Vector> rowData = new Vector<Vector>();
	 		 		 	
	 	tree.addTreeSelectionListener(event -> {
	 		
	 		TreePath path = tree.getSelectionPath();
	 		if (path == null) return;
	 		
	 		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
	 		
	 		StringBuilder sPath = new StringBuilder();
	 		for (Object o : path.getPath()) {
	 			if (!o.toString().equalsIgnoreCase("Workstation"))
	 				sPath.append(o.toString()).append("\\");
	 		}
	 		currentPath = sPath.toString();
	 		File f = new File(sPath.toString());
		 	URI u = f.toURI();
		 			
	 		//logger.debug("sPath: " + sPath.toString());
	 		try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(u))) {
		 		rowData.clear();
		 		numberOfRows=0;
		 		DefaultTableModel tm = (DefaultTableModel) table.getModel();
	 			for (Path file : ds) {
		 			DefaultMutableTreeNode dirName = new DefaultMutableTreeNode(file.getFileName());
		 			
		 			if (Files.isDirectory(file))
		 				selectedNode.add(dirName);
		 			
		 			File fi = new File(sPath.toString() + file.getFileName().toString());
		 			
		 			if (fi.getName().contains(".")) {
		 				Vector<String> row = new Vector<String>();
		 				//System.out.println(fi.getName() + " : " + fi.length());
		 				row.add(fi.getName());
		 				row.add(String.valueOf(fi.length()).format("%,d%n",fi.length()));
		 				row.add(sPath.toString());
		 				rowData.add(row);
		 				numberOfRows++;
		 			}
		 			
		 		}
	 			tm.setDataVector(rowData, columns);
	 			tm.fireTableDataChanged();
		 	} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage());
			}
	 		
	 		frame.pack();
	 	});
	 	
	 		 	
	 	JScrollPane scrollPane = new JScrollPane(tree);
	 	scrollPane.setPreferredSize(new Dimension(300,600));
	 	treePanel.add(scrollPane);
	 	
	 	try {
	         frame.setSelected(true);
	     } catch (java.beans.PropertyVetoException e) {}
	 	
	 	c.add(treePanel, BorderLayout.LINE_START);
	 	
	 	JPanel fileListPanel = new JPanel();
	 	fileListPanel.setLayout(new GridLayout(2,1));
	 	
	 	//JTable table = new JTable(rowData, columns);
	 	table = new JTable(rowData, columns);
		//table.setPreferredScrollableViewportSize(new Dimension(400, 90));
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
	    table.setFillsViewportHeight(true);
	 	
	 	JScrollPane fileListPane = new JScrollPane(table);
	    fileListPane.setPreferredSize(new Dimension(550, 200));
	   
	    fileListPanel.setPreferredSize(new Dimension(500, 500));
		
	    fileListPanel.add(fileListPane);
		c.add(fileListPanel, BorderLayout.CENTER);
	 	
	    JPanel jsp = new JPanel();
	    jsp.setLayout(null);
	 //	jsp.setPreferredSize(new Dimension(300,300));
        fileListPanel.add(jsp);
        
        int rowSelected=0;
        
        table.addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
        		
        		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        		        		
        		JTable t = (JTable)e.getSource();
        		DefaultTableModel tm = (DefaultTableModel) t.getModel();
            	Object fn = tm.getValueAt(selectedRowIndex, 0);
                //Object p = tm.getValueAt(selectedRowIndex, 2);
                
                if (e.getKeyCode()==KeyEvent.VK_DELETE) {
                	         	
                    //System.out.println("Row " + selectedRowIndex
                    //                   + " is now selected, file name " +   p.toString() + "\\" + fn.toString() + " will be deleted");
                	
                    tm.removeRow(selectedRowIndex);
                    numberOfRows--;
                    File iFile = new File(currentPath + "\\" + fn.toString());
                    iFile.delete();
                    int currImg = selectedRowIndex+1;
                    statusbar.setText("Image: " + currImg + "/" + numberOfRows);
                    
                }
                else if(e.getKeyCode()==KeyEvent.VK_ENTER) {
        			
                	JDesktopPane dsk = (JDesktopPane)frame.getParent();
                	ImageViewer vw = new ImageViewer();
                	vw.setImagePath(currentPath + "\\" + fn.toString());
                	dsk.add(vw.createView());
                	
        		}
            }
        });
						
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (ALLOW_ROW_SELECTION) { // true by default
            ListSelectionModel rowSM = table.getSelectionModel();
            rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) return;
 
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    
                    if (!lsm.isSelectionEmpty()) {
                        
                        int selectedRow = lsm.getMinSelectionIndex();
                        //System.out.println("selectedRow: " + selectedRow);
                        selectedRowIndex = selectedRow;
                        
                        TableModel tm = (DefaultTableModel) table.getModel();
                        Object fn = tm.getValueAt(selectedRow, 0);
                        
                        //System.out.println("Row " + selectedRow
                        //                   + " is now selected, file name is " +   currentPath + "\\" + fn.toString());
                        
                        int currImg = selectedRowIndex+1;
                        statusbar.setText("Image: " + currImg + "/" + numberOfRows);
                        
                        File iFile = new File(currentPath + "\\" + fn.toString());
                        Image img;
                        JLabel imgLbl = null;
						try {
							img = ImageIO.read(iFile);
							JPanel jp = new JPanel();
							jp.setPreferredSize(new Dimension(300, 300));
							jp.setBounds(new Rectangle(100, 5, 295, 295));
							
							//Image resizedImage = img.getScaledInstance(jsp.getWidth(), jsp.getHeight(), Image.SCALE_SMOOTH);
							Image resizedImage = img.getScaledInstance(jp.getWidth(), jp.getHeight(), Image.SCALE_SMOOTH);
							if (resizedImage != null) {
		                        imgLbl = new JLabel(new ImageIcon(resizedImage));
		                       
		                        if (jsp.getComponentCount() > 0)
		                        	jsp.removeAll();
		                        
		                        //jsp.add(imgLbl);
		                        jp.add(imgLbl);
		                        jsp.add(jp);
							}
						} catch (IOException e1) {
							logger.debug("Not an image file");
							//e1.printStackTrace();
						}
                                  	
                        frame.pack();
                        
                    }
                }
            });
        } else {
            table.setRowSelectionAllowed(false);
        }
	    
	    
	 	frame.pack();
	 	return frame;
		
	}
	
	

}
