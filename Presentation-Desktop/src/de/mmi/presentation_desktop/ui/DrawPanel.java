package de.mmi.presentation_desktop.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

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
	
	private AnimatorThread animThread;
	
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
		if(animThread != null && animThread.isAlive()){
			animThread.interrupt();
		}
		
		// TODO: use better animation (i bet there is some in java..)
		animThread = new AnimatorThread();
		animThread.start();
	}
	
	private class AnimatorThread extends Thread{
		
		private final static long SLEEP_TIME = 2L;
		private final static int STEPS = 200;
		private final static double END_VALUE = 1.0;
		private final static double START_VALUE = 3.0;
		
		public void run(){
			double stepSize = (START_VALUE - END_VALUE) / (float)STEPS;
			int val;
			synchronized (lock) {
				shade = true;
			}
			for(int i=0; i<STEPS && !isInterrupted(); i++){
				val = (int) Math.round(((START_VALUE - (double)i * stepSize) * DEFAULT_DIAMETER));
				synchronized (lock) {
					diameter = val;
				}
				repaint();
				try{
					Thread.sleep(SLEEP_TIME);
				}catch(InterruptedException e){
					return;
				}
			}
			synchronized (lock) {
				shade = false;
			}
			
			repaint();
		}
		
	}

	
}
