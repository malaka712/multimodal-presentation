package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import de.mmi.presentation_desktop.core.Controller;

public class MainFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7496607393633466258L;

	// The Button will move around the screen depending on the current state
	private final static Rectangle[] buttonBounds = new Rectangle[]{
			new Rectangle(441, 64, 67, 48),
			new Rectangle(485, 162, 67, 48),
			new Rectangle(625, 263, 35, 57),
			new Rectangle(630, 358, 46, 76)
		};
	
	private final static int CHOOSE_PRESENTATION = 0;
	private final static int SET_TIME = 1;
	
	private final static String[] toolTips = new String[]{
			"Open Window to choose PDF-Presentation",
			"Set time for Presentation in the opened Window",
			"Use Smartphone to scan given QR-Code",
			"Start presentation using your Smartphone"
		};
	
	private JLayeredPane layer;
	private JPanel buttonPanel;
	
	/**
	 * The button that will be displayed to choose presentation and indicate current steps to be done for user
	 */
	private JButton actionButton;
	private JButton exitButton;
	
	private int boundIndex = 0;
	
	private Controller controller;
	
	public MainFrame(Controller controller){
		super("Natural Presentation");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.controller = controller;
		
		initComponents();

		this.setResizable(false);
	}	
	
	private void initComponents(){
		
		// main pane where the components are drawn above each other
		layer = new JLayeredPane();
		layer.setLayout(new OverlayLayout(layer));
		
		// own panel that haas the image as background
		final ImagePanel imgP = new ImagePanel();

		// transparent panel without layoutmanager (drawn above imagepanel)
		buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setBackground(new Color(0, 0, 0, 0));
		
		ClassLoader loader = MainFrame.class.getClassLoader();
		BufferedImage in = null;
		try {
			// load image and set as background of imagepanel
			InputStream inStr = loader.getResourceAsStream("back.jpg");
			in = ImageIO.read(inStr);
			BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = newImage.createGraphics();
			g.drawImage(in, 0, 0, null);
			g.dispose();
			imgP.setImage(newImage);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// add panels to layout		
		layer.add(imgP);
		layer.add(buttonPanel);
				
		URL path = loader.getResource("exit.png");
		//System.out.println("Read path: " + path);
		ImageIcon exitIcon = new ImageIcon(path);
		exitButton = new JButton();
		exitButton.setToolTipText("Close Application");
		exitButton.setBackground(new Color(0,0,0,0));
		exitButton.setIcon(exitIcon);
		exitButton.setBorder(null);
		//exitButton.setFocusable(false);
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// exit application if exit-button is pressed..
				MainFrame.this.dispose();			
			}
		});
		
		// a button only consisting of a blue background. Will be located within the screens of the background image
		actionButton = new JButton();
		actionButton.setBackground(new Color(0, 0, 0.5f, 0.5f));
		actionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Runnable r = null;
				
				switch(boundIndex){
				case CHOOSE_PRESENTATION:
					r = new Runnable() {
						@Override
						public void run() {
							controller.choosePresentation();
						}
					};
					break;
				case SET_TIME:
					r = new Runnable() {			
						@Override
						public void run() {
							controller.setTime();
						}
					};
					break;
				}
				
				if (r != null)
					new Thread(r).start();

				actionButton.setVisible(false);
			}
		});
		
		buttonPanel.add(actionButton);
		actionButton.setBounds(buttonBounds[boundIndex]);
		
		this.add(layer);
	}
	
	private void updateButtonBounds(){
		actionButton.setToolTipText(toolTips[boundIndex]);
		actionButton.setBounds(buttonBounds[boundIndex]);
		actionButton.repaint();
		buttonPanel.repaint();
	}

	public void next(){
		boundIndex = (boundIndex+1)%buttonBounds.length;
		if(boundIndex >= (buttonBounds.length/2))
			actionButton.setEnabled(false);
		updateButtonBounds();
		actionButton.setVisible(true);
	}
	
	public void redo(){
		this.setButtonVisible(true);
	}
	
	public void setButtonVisible(boolean visible){
		actionButton.setVisible(visible);
	}
	
	@Override
	public void setVisible(boolean b) {
		if(b)
			buttonPanel.repaint();
		
		super.setVisible(b);
	}
	
	
		
}
