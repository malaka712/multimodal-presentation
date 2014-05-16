package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import de.mmi.presentation_desktop.handler.GUIHandler;

public class HighlightFrame extends JFrame implements GUIHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7048766033817355530L;
	
	private final static int FRAME_WIDTH = 300;

	DrawPanel panel;
	
	public HighlightFrame(){
		super("Presenter");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new DrawPanel();
		// don't show background in panel
		panel.setOpaque(false);
		this.setSize(FRAME_WIDTH, FRAME_WIDTH);
		
		this.add(panel);
		
		this.addComponentListener(new ComponentAdapter() {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Ellipse2D.Double(0,0,FRAME_WIDTH,FRAME_WIDTH));
            }
        });
		
	}
	
	@Override
	public void setVisible(boolean visible){
		if(visible){
			// full screen
			//this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			// no surroundings
			this.setUndecorated(true);
			// make background invisible
			this.setBackground(new Color(0, 0, 0, 0));
			// not focusable (so presentation-program receives key events)
			this.setFocusableWindowState(false);
		}
		
		// finally, set visible
		super.setVisible(visible);
		
	}

	@Override
	public void onHighlight(float x, float y) {
		Dimension d = new Dimension(800, 600);	
		this.setBounds((int)(d.width*x), (int)(d.height*y), FRAME_WIDTH, FRAME_WIDTH);
		panel.startAnimation();
	}
}
