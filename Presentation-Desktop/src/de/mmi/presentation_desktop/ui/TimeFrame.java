package de.mmi.presentation_desktop.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class TimeFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1679344057192787994L;
	
	private TimerListener listener;
	
	private Color transparent = new Color(0f,0f,0f,0f);
	
	private JPanel timePanel;
	private JPanel buttonPanel;
	
	private JTextField minutesField;
	private JTextField secondsField;
	
	public TimeFrame(TimerListener listener, Component parent){
		super("Set Time for Presentation");
		this.listener = listener;
		initComponents();
		
		setLocationRelativeTo(parent);
	}
	
	private void initComponents(){
		
		Container pane = this.getContentPane();
		
		// fixed size
		this.setResizable(false);
		
		
		/*
		 * Load image icons
		 */
		ClassLoader loader = TimeFrame.class.getClassLoader();
		
		URL path = loader.getResource("arrow_up.png");
		ImageIcon upIcon = null;
		if(path != null) upIcon = new ImageIcon(path);

		URL path2 = loader.getResource("arrow_down.png");
		ImageIcon downIcon = null;
		if(path2 != null) downIcon = new ImageIcon(path2);
		
		
		/*
		 * Add one main panel with empty Border
		 * and one panel for the Cancel/Ok-Buttons
		 */
		
		JButton fakeButton = new JButton("Cancel");
		Dimension d = fakeButton.getPreferredSize();		
		
		timePanel = new JPanel();	
		GridLayout timeLayout = new GridLayout(3, 4);
		timeLayout.setHgap(10);
		timeLayout.setVgap(5);
		//timePanel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.green));
		timePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		timePanel.setLayout(timeLayout);
		
		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(2*d.width+10, d.height+20));		
		BorderLayout buttonLayout = new BorderLayout(5, 0);
		buttonPanel.setLayout(buttonLayout);
		//buttonPanel.setBorder(BorderFactory.createMatteBorder(10, 60, 10, 10, Color.red));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 10));

		BorderLayout mainLayout = new BorderLayout();
		pane.setLayout(mainLayout);
		
		pane.add(buttonPanel, BorderLayout.SOUTH);
		pane.add(timePanel, BorderLayout.NORTH);
		pane.add(new JSeparator(), BorderLayout.CENTER);
		
		
		/*
		 * First row
		 */	
		// Button with up-arrow
		JButton increaseMinutes = new JButton();
		increaseMinutes.setIcon(upIcon);
		increaseMinutes.setToolTipText("Increase minutes");
		increaseMinutes.setBackground(transparent);
		timePanel.add(increaseMinutes);
		
		// Buffer label
		timePanel.add(new JLabel());
		
		JButton increaseSeconds = new JButton();
		increaseSeconds.setIcon(upIcon);
		increaseSeconds.setToolTipText("Increase seconds");
		increaseSeconds.setBackground(transparent);
		timePanel.add(increaseSeconds);
		
		
		// More buffer labels
		timePanel.add(new JLabel());
		
		
		/*
		 * Second row
		 */		
		minutesField = new JTextField("10");
		minutesField.setHorizontalAlignment(JTextField.CENTER);
		timePanel.add(minutesField);
		
		timePanel.add(new JLabel("min"));
		
		secondsField = new JTextField("00");
		secondsField.setHorizontalAlignment(JTextField.CENTER);
		timePanel.add(secondsField);
		
		timePanel.add(new JLabel("sec"));
	
		
		/*
		 * third row
		 */		
		JButton decreaseMinutes = new JButton();
		decreaseMinutes.setIcon(downIcon);
		decreaseMinutes.setToolTipText("Decrease minutes");
		decreaseMinutes.setBackground(transparent);
		timePanel.add(decreaseMinutes);
		
		timePanel.add(new JLabel());
		
		JButton decreaseSeconds = new JButton();
		decreaseSeconds.setIcon(downIcon);
		decreaseSeconds.setToolTipText("Decrease seconds");
		decreaseSeconds.setBackground(transparent);
		timePanel.add(decreaseSeconds);
		
		timePanel.add(new JLabel());
		
		
		/*
		 * Buttons
		 */		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Close window without saving");
		buttonPanel.add(cancelButton, BorderLayout.WEST);
		
		JButton applyButton = new JButton("Ok");
		applyButton.setToolTipText("Save time and close window");
		buttonPanel.add(applyButton, BorderLayout.EAST);
		
		
		/*
		 * Now add all the listeners.. 
		 */
		
		increaseMinutes.addActionListener(increaseMinutesListener);
		increaseSeconds.addActionListener(increaseSecondsListener);
		
		decreaseMinutes.addActionListener(decreaseMinutesListener);
		decreaseSeconds.addActionListener(decreaseSecondsListener);
		
		cancelButton.addActionListener(cancelListener);
		applyButton.addActionListener(okayListener);
	}

	
	/*
	 * -----------------------------------------------------------------------
	 * 		The action listeners for the components on the gui
	 * 			And helper functions..
	 * -----------------------------------------------------------------------
	 */
	
	private void increase(JButton button, JTextField field){
		button.setBackground(transparent);
		button.setSelected(false);
		
		int number;
		try{
			number = Integer.parseInt(field.getText().trim());
			number++;
		}catch(NumberFormatException e){
			number = 0;
		}
		
		field.setText(String.format("%02d", number));
	}
	
	private void decrease(JButton button, JTextField field){
		button.setBackground(transparent);
		button.setSelected(false);
		
		int number = -1;
		try{
			number = Integer.parseInt(field.getText().trim());
			number--;
		}catch(NumberFormatException e){}
		
		if(number < 0)
			number = 0;
		
		field.setText(String.format("%02d", number));
	}
	
	private ActionListener increaseMinutesListener = new ActionListener() {	
		@Override
		public void actionPerformed(ActionEvent e) {
			increase((JButton)e.getSource(), minutesField);
		}
	};
	
	private ActionListener increaseSecondsListener = new ActionListener() {	
		@Override
		public void actionPerformed(ActionEvent e) {
			increase((JButton)e.getSource(), secondsField);
		}
	};
	
	private ActionListener decreaseMinutesListener = new ActionListener() {	
		@Override
		public void actionPerformed(ActionEvent e) {
			decrease((JButton)e.getSource(), minutesField);
		}
	};
	
	private ActionListener decreaseSecondsListener = new ActionListener() {	
		@Override
		public void actionPerformed(ActionEvent e) {
			decrease((JButton)e.getSource(), secondsField);
		}
	};
	
	private ActionListener cancelListener = new ActionListener() {	
		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(){
				public void run(){
					listener.onCancel();
				}
			}.start();
			
			TimeFrame.this.dispose();
		}
	};
	
	private ActionListener okayListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				final int seconds = Integer.parseInt(secondsField.getText().trim());
				final int minutes = Integer.parseInt(minutesField.getText().trim());
				
				new Thread(){
					public void run(){
						listener.onTimeSet(60*minutes + seconds);
					}
				}.start();
				
				TimeFrame.this.dispose();
			}catch(NumberFormatException ex){
				
			}
		}
	};
	
	/*
	 * The listener for the callback
	 */
	
	public interface TimerListener{
		public void onTimeSet(int seconds);
		public void onCancel();
	}
}
