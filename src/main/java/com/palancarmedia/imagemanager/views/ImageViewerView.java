package com.palancarmedia.imagemanager.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageViewerView {
	
	private final static Logger logger = LoggerFactory.getLogger(SendInfoView.class);
	
	public JInternalFrame createView() {
		
		JInternalFrame frame = new JInternalFrame("Send URL's",
	 			   true, // resizable
	 			   true, // closable
	 			   true, // maximizable
	 			   true); // iconifiable
		 	
	    frame.setBounds(10, 10, 900, 750);
	 	frame.setVisible(true); //necessary as of 1.3
	 	Container c = frame.getContentPane();
	 	c.setLayout(new BorderLayout());
	 	
	 	JPanel treePanel = new JPanel();
	 	
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
	 	StringBuilder sPath = new StringBuilder("C:");
	 	
	 	
	 	tree.addTreeSelectionListener(event -> {
	 		TreePath path = tree.getSelectionPath();
	 		if (path == null) return;
	 		
	 		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
	 		Path pth = (Path)selectedNode.getUserObject();
	 		sPath.append("\\").append(pth.getFileName().toString());
	 		File f = new File(sPath.toString());
		 	URI u = f.toURI();
	 		
	 		logger.debug(sPath.toString());
	 		
	 		try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(u))) {
		 		for (Path file : ds) {
		 			DefaultMutableTreeNode dirName = new DefaultMutableTreeNode(file.getFileName());
		 			selectedNode.add(dirName );
		 		}
		 	} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage());
			}
	 	});
	 	
	 	JScrollPane scrollPane = new JScrollPane(tree);
	 	scrollPane.setPreferredSize(new Dimension(300,600));
	 	treePanel.add(scrollPane);
	 	
	 	try {
	         frame.setSelected(true);
	     } catch (java.beans.PropertyVetoException e) {}
	 	
	 	c.add(treePanel, BorderLayout.LINE_START);
	 	
	 	return frame;
		
	}

}
