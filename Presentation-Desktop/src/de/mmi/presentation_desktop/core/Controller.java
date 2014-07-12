package de.mmi.presentation_desktop.core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.mmi.presentation_desktop.handler.GUIHandler;
import de.mmi.presentation_desktop.handler.PointerHandler;
import de.mmi.presentation_desktop.network.DataServer;
import de.mmi.presentation_desktop.network.Server;
import de.mmi.presentation_desktop.ui.HighlightFrame;
import de.mmi.presentation_desktop.ui.MainFrame;
import de.mmi.presentation_desktop.ui.PdfViewer;
import de.mmi.presentation_desktop.ui.PointerFrame;
import de.mmi.presentation_desktop.ui.QRFrame;
import de.mmi.presentation_desktop.ui.TimeFrame;
import de.mmi.presentation_desktop.ui.TimeFrame.TimerListener;
import de.mmi.presentation_desktop.ui.WaitFrame;

public class Controller implements GUIHandler, PointerHandler, TimerListener {
	
	
	Server server;
	PdfViewer pdfViewer;
	HighlightFrame highlightFrame;
	PointerFrame pointerFrame;
	DataServer ds;
	final boolean transparency;
	MainFrame mainWindow;
	
	private QRFrame qFrame;
	private int time = 0;
	
	public Controller(boolean transparency){
		this.transparency = transparency;
		
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				pdfViewer = new PdfViewer(Controller.this);
				
				mainWindow = new MainFrame(Controller.this);
				mainWindow.pack();
				mainWindow.setLocationRelativeTo(null);
				mainWindow.setVisible(true);
				/*
				MainWindow win = new MainWindow(Controller.this);
				win.pack();
				win.setVisible(true);*/
			}
		});
	}

	/**
	 * Method to show QRFrame (that displays IPs in QR-Code)
	 * and start the servers. A connection from Android-Side is established as soon
	 * as the QR-Code was scanned successfully.
	 */
	public void showQRAndStartServer(){		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				qFrame = new QRFrame(false);
				qFrame.init();
				qFrame.setVisible(true);
			}
		});

		// if server is not null, we have done all this..
		if(server == null){
			// Message server handling JSON-Messages
			server = new Server(pdfViewer, this, this, this);
			server.start();
			
			// DataServer sending images
			ds = new DataServer();
			ds.start();
			new Thread(){
				public void run(){
					try {
						ds.join();
						mainWindow.next();
						qFrame.dispose();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
		}
	}
	
	// Set PDF-Viewer visible, create other frames needed during presentation
	public void startPresentation(){
		pdfViewer.setVisible(true);
		highlightFrame = new HighlightFrame(transparency);
		pointerFrame = new PointerFrame();
	}

	// send all images in "PDF-Images"-Folder to Android-Device
	public void sendImages(){
		System.out.println("Sending images");
		
		try {
			ds.sendImages(new File("PDF-Images"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// send timer-data (needs to be implemented)
	public void sendTimerData() {
		// TODO: get real data (from not yet existing timer window)
		
		server.sendTimerMessage(time);
		
	}
	
	/**
	 * Is used to save images in PDF-Images folder. Is called after
	 * images are decoded.
	 */
	public void saveImages(){
		// this thread reads all imageicons, and saves them in a folder as jpegs
		new Thread(){
			
			DecimalFormat format;
			
			public void run(){
				if(pdfViewer == null)
					return;
				
				format = new DecimalFormat("0000");
				
				List<ImageIcon> images = pdfViewer.getImages();
				int count = 0;
				File f = new File("PDF-Images");
				if(!f.exists())
					f.mkdir();
				else{
					File[] list = f.listFiles();
					for(File file : list)
						file.delete();
				}
				for(ImageIcon img : images){
					writeImage(f, img.getImage(), count);
					count++;
				}
			}
			
			private void writeImage(File folder, Image img, int count){
				BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = bi.createGraphics();
				g2d.drawImage(img, 0, 0, null);
				g2d.dispose();
				try {
					System.out.println("Saving " + format.format(count));
					ImageIO.write(bi, "jpg", new File(folder.getAbsolutePath() + "/" + format.format(count) + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
	 * Informs controller that a new PDF-File has been selected. 
	 * Show wait-frame (or change to sth similar) to show user that 
	 * decoding is done. After decoding, hide frame and invoke next step.
	 * @param pdfFile The newly chosen File
	 */
	public void newPDF(final File pdfFile){
		final WaitFrame wf = new WaitFrame();
		wf.setVisible(true);
		new Thread(){
			public void run(){
				pdfViewer.setFile(pdfFile);
				pdfViewer.init();
				wf.setVisible(false);
				mainWindow.next();
				// TODO: don't show QR-Frame, but Time-Frame instead to set time for presentation
				//showQRAndStartServer();
			}
		}.start();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onHighlight(float x, float y) {
		highlightFrame.onHighlight(x, y);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hideFrame() {
		highlightFrame.hideFrame();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPoint(float x, float y) {
		pointerFrame.onPoint(x, y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onHidePointer() {
		pointerFrame.onHidePointer();
	}

	public void choosePresentation() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDFs", "pdf", "PDF");
		chooser.setFileFilter(filter);
		int retVal = chooser.showOpenDialog(mainWindow);
		if(retVal == JFileChooser.APPROVE_OPTION) {
			// selection was confirmed with "OK", decode file
			newPDF(chooser.getSelectedFile());

		}else if(retVal == JFileChooser.CANCEL_OPTION){
	    	// selection was canceled, set button visible again
			mainWindow.redo();
	    }

	}

	public void setTime() {
		TimeFrame tFrame = new TimeFrame(this, mainWindow);
		tFrame.pack();
		tFrame.setVisible(true);
	}

	@Override
	public void onTimeSet(int seconds) {
		this.time = seconds;		
		System.out.println("time set to " + seconds + " seconds");
		mainWindow.next();
		showQRAndStartServer();
	}

	@Override
	public void onCancel(){
		mainWindow.redo();
	}
}
