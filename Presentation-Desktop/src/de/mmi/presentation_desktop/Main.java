package de.mmi.presentation_desktop;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialArray;
import javax.swing.JFrame;

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
		new Main();
	}

}
