package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class AnimatedButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static float RADIUS_MAX = 1.3f;
	private final static float RADIUS_MIN = 1.0f;
	
	private final static float[] fracs = new float[]{0.0f, 1.0f};
	
	private AnimationThread animThread;
	private Color[] gradientColors;
	private final static Color[] endColorsBase = new Color[]{
			// 33b5e5 - value used in app if button is pressed
			//new Color(51, 181, 229),
			new Color(41, 160, 200),
			// dark blue, used as default
			new Color(0, 0, 120)
	};
	// at start the same as base, but will change over time
	private final static Color[] endColors = new Color[]{
			// 33b5e5 - value used in app if button is pressed
			new Color(51, 181, 229),
			// dark blue, used as default
			new Color(0, 0, 120)
	};
	
	private float radiusState = RADIUS_MAX;
	
	boolean mouseOver = false;
	boolean mousePressed = false;
	
	public AnimatedButton(){
		super();
		initColor();
	}
	
	public AnimatedButton(String text){
		super(text);
		initColor();
	}
	
	private void initColor(){
		gradientColors = new Color[2];
		gradientColors[0] = Color.white;
		gradientColors[1] = new Color(0f,0f,0.6f);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if(mousePressed && isEnabled()){
        	paintButton(g2d, endColors[0]);
        }else if(mouseOver && isEnabled()){
        	paintButton(g2d, endColors[0]);
        }else{
        	paintButton(g2d, endColors[1]);
        }
               
	}
	
	private void paintButton(Graphics2D g2d, Color endColor){
			
		float cx = getWidth();
        float cy = getHeight();
        cx /= 2.0f;
        cy /= 2.0f;
        
        float radius;
        
        synchronized (AnimatedButton.this) {
        	
        	if(cx > cy){
            	radius = radiusState * cy;
            }else{
            	radius = radiusState * cx;
            }
        	
        	gradientColors[1] = endColor;
        	RadialGradientPaint rgp = new RadialGradientPaint(cx, cy, radius, fracs, gradientColors);
            g2d.setPaint(rgp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
		}
        
	}
	
	@Override
	public void setVisible(boolean visible){
	
		if(visible){
			animate();
		}else{
			stopAnimation();
		}
		
		super.setVisible(visible);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if(id == MouseEvent.MOUSE_ENTERED){
			mouseOver = true;
		}else if (id == MouseEvent.MOUSE_EXITED || id == MouseEvent.MOUSE_CLICKED){
			mouseOver = false;
		}	
	}
	
	@Override
	public void setBounds(Rectangle r){
		super.setBounds(r);
		//animate();
	}
	
	public void animate(){
		if(animThread == null || !animThread.isAlive()){
			animThread = new AnimationThread();
			animThread.start();
		}
	}
	
	public void stopAnimation(){
		if(animThread != null && animThread.isAlive()){
			animThread.interrupt();
		}
	}
	
	private class AnimationThread extends Thread{
		
		private final long SLEEP_TIME = 15L;
		private final float STEPS = 80.0f;
		private final float COLOR_MIN = 0.8f;	
		private final float COLOR_MAX = 1.0f;
		
		private float colorDelta;
		private float colorModificator = COLOR_MIN;
		
		private float radiusDelta;
		private float radiusModificator = RADIUS_MIN;
		
		public void run(){
			colorDelta = (COLOR_MAX - COLOR_MIN) / STEPS;
			radiusDelta = (RADIUS_MAX - RADIUS_MIN) / STEPS;
			
			while(!isInterrupted()){
				
				colorModificator += colorDelta;
				if(colorModificator >= COLOR_MAX){
					colorModificator = COLOR_MAX;
					colorDelta *= -1;
				
				}else if(colorModificator <= COLOR_MIN){
					colorModificator = COLOR_MIN;
					colorDelta *= -1;
				}
				
				radiusModificator += radiusDelta;
				if(radiusModificator >= RADIUS_MAX){
					radiusModificator = RADIUS_MAX;
					radiusDelta *= -1;
				
				}else if(radiusModificator <= RADIUS_MIN){
					radiusModificator = RADIUS_MIN;
					radiusDelta *= -1;
				}
				
				synchronized(AnimatedButton.this){
					
					radiusState = radiusModificator;
					
					for (int i=0; i<endColorsBase.length; i++){
						int r = (int) (endColorsBase[i].getRed()*colorModificator + 0.5f);
						int g = (int) (endColorsBase[i].getGreen()*colorModificator + 0.5f);
						int b = (int) (endColorsBase[i].getBlue()*colorModificator + 0.5f);
						endColors[i] = new Color(r,g,b);
					}
				}
				
				AnimatedButton.this.repaint();
				
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {}
				
			}
			
			System.out.println("AnimThread killed");
		}
	}
}
