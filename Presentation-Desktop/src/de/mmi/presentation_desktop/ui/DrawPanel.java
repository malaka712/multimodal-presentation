package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class DrawPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5999317167168506945L;

	Color blue = new Color(0, 0, 255);
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(g instanceof Graphics2D){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setPaint(blue);
			
			g2d.drawOval(100, 100, 100, 100);
		}
	}

	
}
