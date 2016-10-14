package de.mmi.presentation_desktop.ui;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * JPanel without a default layout-manager, managing the components itself.
 * @author patrick
 *
 */

public class ManagedPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3851405274497952903L;

	private HashMap<JButton, Rectangle> bounds;
	
	public ManagedPanel(){
		super();
		// we manage layout ourselves (to have absolute coordinates)
		this.setLayout(null);
		// be transparent so other panel underlying is visible
		this.setOpaque(false);
		
		// store bounds with components
		bounds = new HashMap<JButton, Rectangle>(2); // size is 2
		
	}
	
	public void addButton(JButton button, Rectangle bound){
		//System.out.println("Adding Button..");
		super.add(button);
		bounds.put(button, bound);
	}
	
	public void updateBounds(JButton button, Rectangle bound){
		bounds.put(button, bound);
	}
	
	@Override
	public void repaint() {

		

		//System.out.println("Repainting ManagedPanel");
		Component[] components = this.getComponents();
		
		for(int i=0; i<components.length; i++){
			if(components[i] instanceof JButton){
				//System.out.println("Instance " + i + " is JButton");
				JButton button = (JButton) components[i];
				components[i].setBounds(bounds.get(button));
				
			}
			//components[i].repaint();
		}
		
		super.repaint();
	}

}
