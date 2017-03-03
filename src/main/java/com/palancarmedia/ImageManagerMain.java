package com.palancarmedia;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.palancarmedia.imagemanager.views.SendInfoView;

public class ImageManagerMain extends JFrame implements ActionListener 
{
	JDesktopPane desktop;
	private final static Logger logger = LoggerFactory.getLogger(ImageManagerMain.class);
	
	public ImageManagerMain() {
		super("ImageManagerMain");
		
		//Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        //setBounds(inset, inset,
        //          screenSize.width  - inset*2,
        //          screenSize.height - inset*2);
        setBounds(inset, inset, 850, 750);
 
        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        
        // createFrame(); //create first "window"
        
        setContentPane(desktop);
        setJMenuBar(createMenuBar());
		
	}
	
	protected JMenuBar createMenuBar() {
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("Send All...");
	    menuItem.setActionCommand("sendall");
	    menuItem.addActionListener(this);
	    menu.add(menuItem);
				
		menuItem = new JMenuItem("Exit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("exit");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
		return menuBar;
	}
	
	//Create a new internal frame.
    protected void createSendAllView() {
    	
    	SendInfoView siv = new SendInfoView();
    	desktop.add(siv.createView());
    	
    }
	
	//Quit the application.
    protected void quit() {
        System.exit(0);
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
 
        //Create and set up the window.
        ImageManagerMain frame = new ImageManagerMain();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Display the window.
        frame.setVisible(true);
    }
	
	public static void main( String[] args )
    {
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ("exit".equals(e.getActionCommand())) {
			quit();
		} 
		else if ("sendall".equals(e.getActionCommand())) {
			createSendAllView();
		}
		
	}
}
