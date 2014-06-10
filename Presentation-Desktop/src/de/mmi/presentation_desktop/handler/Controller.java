package de.mmi.presentation_desktop.handler;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import de.mmi.presentation_desktop.WaitFrame;
import de.mmi.presentation_desktop.network.DataServer;
import de.mmi.presentation_desktop.network.Server;
import de.mmi.presentation_desktop.ui.HighlightFrame;
import de.mmi.presentation_desktop.ui.MainWindow;
import de.mmi.presentation_desktop.ui.PdfViewer;
import de.mmi.presentation_desktop.ui.QRFrame;

public class Controller implements GUIHandler {
	
	
	Server server;
	PdfViewer pdfViewer;
	HighlightFrame highlightFrame;
	DataServer ds;
	
	public Controller(){
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				pdfViewer = new PdfViewer(Controller.this);
				MainWindow win = new MainWindow(Controller.this);
				win.pack();
				win.setVisible(true);
			}
		});
	}

	public void showQRAndStartServer(){
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				QRFrame qFrame = new QRFrame();
				qFrame.init();
				qFrame.setVisible(true);
			}
		});

		// if server is not null, we have done all this..
		if(server == null){
			server = new Server(pdfViewer, this, this);
			server.start();
			
			ds = new DataServer();
			ds.start();
			
		}
	}
	
	public void startPresentation(){
		pdfViewer.setVisible(true);
		highlightFrame = new HighlightFrame();
	}

	
	public void sendImages(){
		System.out.println("Sending images");
		
		try {
			ds.sendImages(new File("PDF-Images"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveImages(){
		// this thread reads all imageicons, and saves them in a folder as jpegs
		new Thread(){
			public void run(){
				if(pdfViewer == null)
					return;
				
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
					ImageIO.write(bi, "jpg", new File(folder.getAbsolutePath() + "/" + count + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void newPDF(final File pdfFile){
		final WaitFrame wf = new WaitFrame();
		wf.setVisible(true);
		new Thread(){
			public void run(){
				pdfViewer.setFile(pdfFile);
				pdfViewer.init();
				wf.setVisible(false);
			}
		}.start();
	}
	
	
	@Override
	public void onHighlight(float x, float y) {
		highlightFrame.onHighlight(x, y);
		
	}

	@Override
	public void hideFrame() {
		highlightFrame.hideFrame();
	}
}
