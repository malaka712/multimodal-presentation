package de.mmi.presentation_desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice.WindowTranslucency;

import de.mmi.presentation_desktop.handler.Controller;

public class Main {

	public static void main(String[] args) {
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
