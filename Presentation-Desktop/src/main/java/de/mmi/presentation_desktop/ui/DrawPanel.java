package de.mmi.presentation_desktop.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class DrawPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5999317167168506945L;

	private final static int DEFAULT_DIAMETER = 100;
	private final static int DEFAULT_STROKE_WIDTH = 3;

	private final static Color red = new Color(255, 0, 0);
	private final static Color red2 = new Color(255, 0, 0, 160);
	private final static Color red3 = new Color(255, 0, 0, 80);
	private final static Color red4 = new Color(255, 0, 0, 40);
	
	private static BasicStroke strk = new BasicStroke(DEFAULT_STROKE_WIDTH);
	
	private int diameter = DEFAULT_DIAMETER;
	private int x = 150;
	private int y = 150;
	
	private boolean shade = false;
	
	private Timer timer;
	
	private final static Object lock = new Object();
	
	public DrawPanel(){
		super();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(g instanceof Graphics2D){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setPaint(red);
			g2d.setStroke(strk);
			synchronized (lock) {
				g2d.drawOval(x-diameter/2, y-diameter/2, diameter, diameter);
				
				if(shade){

					int dia2 = (int)(1.05 * diameter);
					int dia3 = (int)(1.1 * diameter);
					int dia4 = (int)(1.2 * diameter);
					
					g2d.setPaint(red2);
					g2d.drawOval(x-dia2/2, y-dia2/2, dia2, dia2);
					
					g2d.setPaint(red3);
					g2d.drawOval(x-dia3/2, y-dia3/2, dia3, dia3);
					
					g2d.setPaint(red4);
					g2d.drawOval(x-dia4/2, y-dia4/2, dia4, dia4);
				}
			}
		}
	}
	
	public void startAnimation(){
		
		if(timer != null && timer.isRunning()){
			timer.stop();
		}
		
		AnimationActionListener listener = new AnimationActionListener(200);
		timer = new Timer(3, listener);
		timer.start();
	}
	
	
	private class AnimationActionListener implements ActionListener{
		
		private final static double END_VALUE = 1.0;
		private final static double START_VALUE = 3.0;
		private double stepSize;
		private int step;
		
		public AnimationActionListener(int steps){
			super();
			stepSize = (START_VALUE - END_VALUE) / (double)steps;
			step = 0;
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int val = (int) Math.round(((START_VALUE - (double)step * stepSize) * DEFAULT_DIAMETER));
			synchronized (lock) {
				diameter = val;
			}
			
			repaint();
			
			step++;
			
			if(step == 200)
				timer.stop();
		}
	};
	
}
