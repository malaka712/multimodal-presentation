package de.mmi.presentation_desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;

import de.mmi.presentation_desktop.core.Controller;

public class Main {

	public static void main(String[] args) {
		// start the Controller (core element that spawns windows)
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
