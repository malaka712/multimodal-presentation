package de.mmi.presentation_desktop.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.mmi.presentation_desktop.handler.Controller;

public class MainWindow extends JFrame{

	Controller controller;
	
	public MainWindow(Controller controller){
		super("Multimodal Presentation");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.controller = controller;
		
		JButton openQR = new JButton();
		openQR.setText("Show QR-Code");
		openQR.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainWindow.this.controller.showQRAndStartServer();
			}
		});
		
		JButton startPres = new JButton();
		startPres.setText("Start Presentation");
		startPres.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainWindow.this.controller.startPresentation();				
			}
		});
		
		Container pane = this.getContentPane();
		
		JButton selectFile = new JButton();
		selectFile.setText("Choose Presentation");
		selectFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseFile();
			}
		});
		
		pane.add(openQR, BorderLayout.CENTER);
		pane.add(startPres, BorderLayout.SOUTH);
		pane.add(selectFile, BorderLayout.NORTH);
		
	}
	
	private void chooseFile(){
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDFs", "pdf", "PDF");
		chooser.setFileFilter(filter);
		int retVal = chooser.showOpenDialog(this);
		if(retVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	    }
		
		File selection = chooser.getSelectedFile();
		
		controller.newPDF(selection);
	}
}
