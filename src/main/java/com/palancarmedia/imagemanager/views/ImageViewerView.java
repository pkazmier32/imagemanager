package com.palancarmedia.imagemanager.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
	
	public JInternalFrame createView() {
		
		JInternalFrame frame = new JInternalFrame("Send URL's",
	 			   true, // resizable
	 			   true, // closable
	 			   true, // maximizable
	 			   true); // iconifiable
		 	
	    frame.setBounds(10, 10, 900, 1050);
	 	frame.setVisible(true); //necessary as of 1.3
	 	Container c = frame.getContentPane();
	 	c.setLayout(new BorderLayout());
	 	
	 	JPanel treePanel = new JPanel();
	 	
	 	boolean ALLOW_ROW_SELECTION = true;
	 	
	 	DefaultMutableTreeNode root = new DefaultMutableTreeNode("C:\\");
	 	
	 	try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get("c:\\"), path -> path.toFile().isDirectory())) {
	 		for (Path file : ds) {
	 			DefaultMutableTreeNode dirName = new DefaultMutableTreeNode(file.getFileName());
	 			root.add(dirName );
	 		}
	 	} catch (Exception e1) {
			logger.error(e1.getLocalizedMessage());
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
	 		Path pth = (Path)selectedNode.getUserObject();
	 		
	 		StringBuilder sPath = new StringBuilder();
	 		for (Object o : path.getPath()) {
	 			sPath.append(o.toString()).append("\\");
	 		}
	 		 		
	 		File f = new File(sPath.toString());
		 	URI u = f.toURI();
		 			
	 		logger.debug(sPath.toString());
	 		
	 		try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(u))) {
		 		rowData.clear();
	 			for (Path file : ds) {
		 			DefaultMutableTreeNode dirName = new DefaultMutableTreeNode(file.getFileName());
		 			selectedNode.add(dirName);
		 			File fi = new File(sPath.toString() + file.getFileName().toString());
		 			
		 			if (fi.getName().contains(".")) {
		 				Vector<String> row = new Vector<String>();
		 				System.out.println(fi.getName() + " : " + fi.length());
		 				row.add(fi.getName());
		 				row.add(String.valueOf(fi.length()).format("%,d%n",fi.length()));
		 				row.add(sPath.toString());
		 				rowData.add(row);
		 			}
		 			
		 		}
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
	 	
	 	Vector<String> columns = new Vector<String>();
	 	columns.add("File Name");
	 	columns.add("Size");
	 	
	 	JTable table = new JTable(rowData, columns);
		//table.setPreferredScrollableViewportSize(new Dimension(400, 90));
		table.getColumnModel().getColumn(1).setPreferredWidth(75);
	    table.setFillsViewportHeight(true);
	 	
	 	JScrollPane fileListPane = new JScrollPane(table);
	    fileListPane.setPreferredSize(new Dimension(550, 200));
	   
	    fileListPanel.setPreferredSize(new Dimension(700, 300));
		
	    fileListPanel.add(fileListPane);
		c.add(fileListPanel, BorderLayout.CENTER);
	 	
	    JPanel jsp = new JPanel();
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
                Object p = tm.getValueAt(selectedRowIndex, 2);
                
                if (e.getKeyCode()==KeyEvent.VK_DELETE) {
                	         	
                    System.out.println("Row " + selectedRowIndex
                                       + " is now selected, file name " +   p.toString() + "\\" + fn.toString() + " will be deleted");
                    tm.removeRow(selectedRowIndex);
                    File iFile = new File(p.toString() + "\\" + fn.toString());
                    iFile.delete();
                    
                }
                else if(e.getKeyCode()==KeyEvent.VK_ENTER) {
        			
                	JDesktopPane dsk = (JDesktopPane)frame.getParent();
                	ImageViewer vw = new ImageViewer();
                	vw.setImagePath(p.toString() + "\\" + fn.toString());
                	dsk.add(vw.createView());
                	
        			System.out.println("Row " + selectedRowIndex
                            + " is now selected, file name " +   p.toString() + "\\" + fn.toString() + " will be displayed");
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
                    
                    if (lsm.isSelectionEmpty()) {
                        System.out.println("No rows are selected.");
                    } else {
                        int selectedRow = lsm.getMinSelectionIndex();
                        System.out.println("selectedRow: " + selectedRow);
                        selectedRowIndex = selectedRow;
                        
                        TableModel tm = (DefaultTableModel) table.getModel();
                        Object fn = tm.getValueAt(selectedRow, 0);
                        Object p = tm.getValueAt(selectedRow, 2);
                        
                        System.out.println("Row " + selectedRow
                                           + " is now selected, file name is " +   p.toString() + "\\" + fn.toString());
                        
                        File iFile = new File(p.toString() + "\\" + fn.toString());
                        Image img;
                        JLabel imgLbl = null;
						try {
							img = ImageIO.read(iFile);
							Image resizedImage = img.getScaledInstance(jsp.getWidth(), jsp.getHeight(), Image.SCALE_SMOOTH);
							if (resizedImage != null) {
		                        imgLbl = new JLabel(new ImageIcon(resizedImage));
		                       
		                        if (jsp.getComponentCount() > 0)
		                        	jsp.removeAll();
		                        
		                        jsp.add(imgLbl);
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
