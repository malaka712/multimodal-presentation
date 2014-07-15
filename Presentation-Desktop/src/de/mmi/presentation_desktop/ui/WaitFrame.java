package de.mmi.presentation_desktop.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7360179535367144578L;
	
	private final static int BORDER_WIDTH = 20;
	
	public WaitFrame(){
		super("Decoding");
	
		setUndecorated(true);
		
		Container pane = this.getContentPane();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
		
		JLabel label = new JLabel("Please wait, decoding..");
		label.setHorizontalAlignment(JLabel.CENTER);	
		
		panel.add(label, BorderLayout.CENTER);
		pane.add(panel);
		
	}

	@Override
	public void setVisible(boolean b) {
		
		if(b){
			this.pack();
			this.setLocationRelativeTo(null);
			this.setAlwaysOnTop(true);
		}
		
		super.setVisible(b);
	}
	
	
	
	
}
