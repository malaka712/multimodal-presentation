package de.mmi.presentation_desktop.ui;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.pdfbox.pdfviewer.PDFPagePanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;

import de.mmi.presentation_desktop.core.Controller;
import de.mmi.presentation_desktop.handler.KeyHandler;


public class PdfViewer extends JFrame implements KeyHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 158446516198919582L;
	
	List<PDPage> allPages = null;
	ArrayList<ImageIcon> allImages = null;
	PDFPagePanel pdfPanel;
	int page = 0;
	PDPage testPage;
	DrawPanel drawPanel = null;
	JFileChooser filechooser = new JFileChooser();
	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("Menu");
	JMenuItem fullscreen = new JMenuItem("Fullscreen");
	JFrame var = this;
	JLabel imageLabel;

	File pdfFile;
	Controller controller;
	
	public PdfViewer(Controller controller) {
		this.controller = controller;
		//init();
	}


	public void nextPage() {
		page++;
		if (page > allPages.size()-1)
			page = 0;
		testPage = (PDPage) allPages.get(page);
		pdfPanel.setPage(testPage);
		this.add(pdfPanel);
		this.repaint();
	}

	public void previousPage() {
		page--;
		if (page < 0)
			page = allPages.size();
		testPage = (PDPage) allPages.get(page);
		pdfPanel.setPage(testPage);
		this.add(pdfPanel);
		this.repaint();
	}

	public void nextImage() {
		page++;
		if (page > allImages.size()-1)
			page = 0;
		imageLabel.setIcon(allImages.get(page));
		this.repaint();
	}

	public void previousImage() {
		page--;
		if (page < 0)
			page = allImages.size()-1;
		imageLabel.setIcon(allImages.get(page));
		this.repaint();
	}

	/* Maybe go fullscreen only on menubar */
	public void goFullScreen() {
		System.out.println("Hello Fullscreen World!");
	}

	@SuppressWarnings("unchecked")
	public void readPage() {
		/*
		 * Open File TODO: FileChoser
		 */
		File PDF_Path = new File("Democracy3-Manual.pdf");
		PDDocument inputPDF;
		try {
			inputPDF = PDDocument.load(PDF_Path);

			inputPDF.getDocumentCatalog().setPageMode(
					PDDocumentCatalog.PAGE_MODE_FULL_SCREEN);
			allPages = inputPDF.getDocumentCatalog().getAllPages();
			testPage = (PDPage) allPages.get(page);
			// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			pdfPanel = new PDFPagePanel();
			// pdfPanel.setBounds(0,0,getToolkit().getScreenSize().width,getToolkit().getScreenSize().height);
			pdfPanel.setPage(testPage);
			this.add(pdfPanel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resizeAllImages() {
		allImages.clear();
		for (int i = 0; i < allPages.size(); i++) {
			try {
				testPage = (PDPage) allPages.get(i);

				/* Convert PDF to Image */
				BufferedImage image;

				image = testPage.convertToImage();

				/* Rescale Image does this really work? */
				int imgheight = image.getHeight();
				int imgwidth = image.getWidth();
				int newheight;
				int newwidth;
				if (var.getHeight() - imgheight < var.getWidth() - imgwidth) {
					// adjust width, make height screenheight
					newheight = var.getHeight() - imgheight;
					newwidth = (int) (imgwidth + (newheight / 1.41));
					newheight = imgheight + newheight;
				} else {
					// adjust height, make width screenwidth
					newwidth = var.getWidth() - imgwidth;
					newheight = (int) (imgheight + (newwidth * 1.41));
					newwidth = imgwidth + newwidth;
				}
				Image dimage = image.getScaledInstance(newwidth, newheight,
						Image.SCALE_SMOOTH);
				ImageIcon icon = new ImageIcon(dimage);
				allImages.add(icon);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void setFile(File f){
		pdfFile = f;
		//readImage();
	}

	@SuppressWarnings("unchecked")
	public void readImage() {
		File PDF_Path;
		if(pdfFile == null)
			PDF_Path = new File("test.pdf");
		else
			PDF_Path = pdfFile;
		
		// File PDF_Path = new File("Democracy3-Manual.pdf");
		PDDocument inputPDF;
		try {

			/* Load PDDocument from given File */
			inputPDF = PDDocument.load(PDF_Path);
			inputPDF.getDocumentCatalog().setPageMode(
					PDDocumentCatalog.PAGE_MODE_FULL_SCREEN);
			allPages = inputPDF.getDocumentCatalog().getAllPages();

			
			resizeAllImages();

			controller.saveImages();
			

			inputPDF.close();
			
			/* JLabel Containing Image */
			imageLabel = new JLabel(allImages.get(page));
			imageLabel.setBounds(0, 0, var.getWidth(), var.getHeight());
			imageLabel.repaint();
			this.add(imageLabel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		allImages = new ArrayList<ImageIcon>();

		/* Add Menubar and make invisible */
		menuBar.add(menu);
		menu.add(fullscreen);
		this.setJMenuBar(menuBar);
		menuBar.setVisible(false);

		/* Set JFrame Fullscreen */
		this.setBounds(0, 0, getToolkit().getScreenSize().width, getToolkit()
				.getScreenSize().height);

		// readPage();
		readImage();

		this.setUndecorated(true);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		//this.setVisible(true);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getKeyCode() == (int) KeyEvent.VK_RIGHT) {
					// nextPage();
					nextImage();
				}
				if (arg0.getKeyCode() == (int) KeyEvent.VK_LEFT) {
					// previousPage();
					previousImage();
				}
				if (arg0.getKeyCode() == (int) KeyEvent.VK_F1) {
					var.dispose();
				}
				if (arg0.getKeyCode() == (int) KeyEvent.VK_ALT) {
					if (menuBar.isVisible())
						menuBar.setVisible(false);
					else
						menuBar.setVisible(true);
				}
			}
		});

		/*
		 * this.addWindowListener(new WindowAdapter() {
		 * 
		 * @Override public void windowClosing(WindowEvent e) { try {
		 * inputPDF.close(); } catch (IOException e1) { // TODO: implement error
		 * handling e1.printStackTrace(); } }
		 * 
		 * });
		 */

	}

	@Override
	public void onKeyPressed(int keyCode) {
		System.out.println("received keyCode " + keyCode);
		
		if(keyCode == KeyEvent.VK_DOWN){
			nextImage();
			System.out.println("going to next img");
		}
		else if(keyCode == KeyEvent.VK_UP){
			previousImage();
			System.out.println("going to prev img");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<ImageIcon> getImages(){
		ArrayList<ImageIcon> cloned = (ArrayList<ImageIcon>) allImages.clone();
		return cloned;
	}

}
