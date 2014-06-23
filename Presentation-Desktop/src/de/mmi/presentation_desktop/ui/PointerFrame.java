package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import de.mmi.presentation_desktop.handler.PointerHandler;

public class PointerFrame extends JFrame implements PointerHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7048766033817355530L;
	
	private final static int FRAME_WIDTH = 15;

	Panel panel;
	
	Dimension screenDimensions;
	int screenWidth;
	int screenHeight;
	
	public PointerFrame(){
		super("Presenter");
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(FRAME_WIDTH, FRAME_WIDTH);
		
		this.addComponentListener(new ComponentAdapter() {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Ellipse2D.Double(0,0,FRAME_WIDTH,FRAME_WIDTH));
            }
        });
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenWidth = gd.getDisplayMode().getWidth();
		screenHeight = gd.getDisplayMode().getHeight();
		
		panel = new Panel();
		panel.setBackground(Color.green);
		this.add(panel);
		
		// no surroundings
		this.setUndecorated(true);
		// make background invisible
		this.setBackground(new Color(0, 255, 0));
		// not focusable (so presentation-program receives key events)
		this.setFocusableWindowState(false);
	}
	
	@Override
	public void setVisible(boolean visible){
		// finally, set visible
		super.setVisible(visible);
		
		if(visible){
			toFront();
			repaint();
		}
	}
	
	@Override
	public void onPoint(float x, float y){
		this.setBounds((int)(screenWidth*x) - FRAME_WIDTH/2, (int)(screenHeight*y) - FRAME_WIDTH/2, FRAME_WIDTH, FRAME_WIDTH);
		setVisible(true);
	}

	@Override
	public void onHidePointer() {
		setVisible(false);
	}

	
}
