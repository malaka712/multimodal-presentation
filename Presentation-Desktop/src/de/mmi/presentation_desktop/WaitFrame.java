package de.mmi.presentation_desktop;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class WaitFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7360179535367144578L;

	public WaitFrame(){
		super("Decoding");
	
		Container pane = this.getContentPane();
		JLabel label = new JLabel("Please wait, decoding..");
		
		pane.add(label, BorderLayout.CENTER);
		
	}

	@Override
	public void setVisible(boolean b) {
		
		if(b){
			this.pack();
			this.setLocationRelativeTo(null);
		}
		
		super.setVisible(b);
	}
	
	
	
	
}
