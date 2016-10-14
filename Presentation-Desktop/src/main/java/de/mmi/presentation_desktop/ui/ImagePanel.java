package de.mmi.presentation_desktop.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3489304406404016623L;
	private BufferedImage image;

    public ImagePanel() {
    	image = null;
    }
    
    public void setImage(BufferedImage img){
    	synchronized (this) {
			image = img;
		}
    	this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (this) {
        	if(image != null)
            	g.drawImage(image, 0, 0, null);      
		}         
    }
}
