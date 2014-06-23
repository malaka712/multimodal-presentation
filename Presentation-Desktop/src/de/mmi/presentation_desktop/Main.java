package de.mmi.presentation_desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice.WindowTranslucency;

import javax.sql.rowset.serial.SerialArray;
import de.mmi.presentation_desktop.handler.Controller;

import org.apache.pdfbox.pdfviewer.PDFPagePanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.operator.NextLine;

import de.mmi.presentation_desktop.handler.KeyHandler;
import de.mmi.presentation_desktop.network.MyServer;
import de.mmi.presentation_desktop.network.Server;
import de.mmi.presentation_desktop.ui.DrawPanel;
import de.mmi.presentation_desktop.ui.QRFrame;

public class Main {

	PdfViewer viewer;
	QRFrame qr;
	MyServer server;

	public Main() {
		PdfViewer viewer = new PdfViewer();
		QRFrame qr = new QRFrame();
		qr.init();
		qr.setVisible(true);
		// Server server = new Server(null, viewer);
		// server.start();
		// MyServer server = new MyServer(viewer.allImages);
		// server.start();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//new Main();
		//PdfViewer viewer = new PdfViewer();
		new Controller(transparencyEnabled());
	}
	
	private static boolean transparencyEnabled(){
		
		boolean enabled = true;
		
		/*
		 * Initial testing needed to ensure that we can run windows in transparent mode
		 */
		GraphicsEnvironment ge = 
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gd = ge.getDefaultScreenDevice();

        //If translucent windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT)) {
            System.err.println(
                "Per-Pixel-Transparency is not supported");
                enabled = false;
        }
        
        if (!gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
            System.err.println(
                "Translucency is not supported");
                enabled = false;
        }
        return enabled;
	}
}
