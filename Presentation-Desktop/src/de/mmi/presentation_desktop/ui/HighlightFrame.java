package de.mmi.presentation_desktop.ui;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.mmi.presentation_desktop.handler.GUIHandler;

public class HighlightFrame extends JFrame implements GUIHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7048766033817355530L;
	
	private final static int FRAME_WIDTH = 300;

	JPanel panel;
	
	public HighlightFrame(){
		super("Presenter");
		// TODO: change icon with following code
		/*
		 * ImageIcon img = new ImageIcon(pathToFileOnDisk);
		 * this.setIconImage(img.getImage());
		 * size needs to be: 20x20 (at least sometimes, more details here: http://www.coderanch.com/t/343726/GUI/java/Frame-setIconImage-optimum-image-size)
		 */
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new DrawPanel();
		// don't show background in panel
		panel.setOpaque(false);
		this.setSize(300, 300);
		
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
			//this.setOpacity(0.0f);
			this.setBackground(new Color(0, 0, 0, 0));
			// not focusable (so presentation-program receives key events)
			this.setFocusableWindowState(false);
		}
		
		// finally, set visible
		super.setVisible(visible);
	}

	@Override
	public void onHighlight(float x, float y) {
		// TODO Auto-generated method stub
		
	}
}
