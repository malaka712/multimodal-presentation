package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
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
	
	private final static Rectangle exitBounds = new Rectangle(665, 5, 30, 30);
	
	private final static int CHOOSE_PRESENTATION = 0;
	private final static int SET_TIME = 1;
	private final static int SCAN_QR_CODE = 2;
	
	private final static String[] toolTips = new String[]{
			"Open Window to choose PDF-Presentation",
			"Set time for Presentation",
			"Use Smartphone to scan given QR-Code",
			"Start presentation using your Smartphone"
		};
	
	private JPanel layer;
	//private JPanel buttonPanel;
	
	/**
	 * The button that will be displayed to choose presentation and indicate current steps to be done for user
	 */
	private JButton actionButton;
	/**
	 * Will be used to close window (and therefore exit App)
	 */
	private JButton exitButton;
	/**
	 * Need to repaint Buttons on being set visible first time
	 */
	private boolean firstTime = true;
	/**
	 * Counter used to determine where button is / what functionality used
	 */
	private int boundIndex = 0;
	/**
	 * Parent for callback
	 */
	private Controller controller;
	
	public MainFrame(Controller controller){
		super("Natural Presentation");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.controller = controller;
		
		initComponents();

		this.setResizable(false);
	}	
	
	private void initComponents(){
		
		this.setUndecorated(true);
		
		// main pane where the components are drawn above each other
		layer = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -4040946267324782193L;
			public boolean isOptimizedDrawingEnabled() {
				return false;
			}
		};
		layer.setLayout(new OverlayLayout(layer));
		
		// own panel that haas the image as background
		final ImagePanel imgP = new ImagePanel();

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
		
		// exitbutton at top right corner, icon is an "x"		
		URL path = loader.getResource("exit.png");
		ImageIcon exitIcon = new ImageIcon(path);
		exitButton = new JButton();
		exitButton.setToolTipText("Close Application");
		exitButton.setBackground(new Color(1f,1f,1f));
		exitButton.setContentAreaFilled(false);
		exitButton.setIcon(exitIcon);
		exitButton.setBorder(null);
		exitButton.setFocusable(false);
		exitButton.setBounds(exitBounds);
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// exit application if exit-button is pressed..
				MainFrame.this.dispose();			
			}
		});
		
		// mouse listener to paint background if mouse is in region and stop painting background if mouse leaves button region
		exitButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setContentAreaFilled(false);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setContentAreaFilled(true);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		// a button only consisting of a blue background. Will be located within the screens of the background image
		actionButton = new JButton();
		actionButton.setBackground(new Color(0, 0, 0.5f, 1f));
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
					
				case SCAN_QR_CODE:
					r = new Runnable(){
						@Override
						public void run(){
							controller.showQRAndStartServer();
						}
					};
				}
				
				if (r != null)
					new Thread(r).start();

			}
		});
		
		layer.add(exitButton);
		layer.add(actionButton);
		layer.add(imgP);
		updateButtonBounds();
		this.add(layer);
	}
	
	// set new position of actionButton and reset exit-button position
	private void updateButtonBounds(){
		actionButton.setVisible(true);
		actionButton.setToolTipText(toolTips[boundIndex]);
		actionButton.setBounds(buttonBounds[boundIndex]);
		exitButton.setBounds(exitBounds);
	}

	/**
	 * Go to next step (e.g. if presentation was chosen, button will be set to "set time""
	 */
	public void next(){
		boundIndex = (boundIndex+1)%buttonBounds.length;
		updateButtonBounds();
	}
	
	/**
	 * Current step was aborted. Show actionButton at old position
	 */
	public void redo(){
		this.setButtonVisible(true);
	}
	
	public void setButtonVisible(boolean visible){
		actionButton.setVisible(visible);
	}
	
	public void setButtonEnabled(boolean enabled){
		actionButton.setEnabled(enabled);
	}
	
	@Override
	public void setVisible(boolean b){
		if(b && firstTime){
			firstTime = false;
			new Thread(){
				public void run(){
					// not sure why, but button will not accept position on first showing if this is not done
					try {
						Thread.sleep(10L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateButtonBounds();
				}
			}.start();
		}
		
		super.setVisible(b);
	}

}
