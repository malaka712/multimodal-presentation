package de.mmi.presentation_desktop;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

import de.mmi.presentation_desktop.network.Server;
import de.mmi.presentation_desktop.ui.HighlightFrame;
import de.mmi.presentation_desktop.ui.QRFrame;

public class App extends Thread{
	
	public static void main(String args[]){
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
                System.exit(0);
        }
        
        if (!gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
            System.err.println(
                "Translucency is not supported");
                //System.exit(0);
        }
        
        /*
         * Make UI visible
         */  
        SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				QRFrame qFrame = new QRFrame();
				qFrame.init();
				qFrame.setVisible(true);
				
				
				HighlightFrame frame = new HighlightFrame();
				//frame.setVisible(true);
				
				KeyMapper mapper = null;
				try {
					mapper = new KeyMapper();
				} catch (AWTException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				//new Tester(mapper, frame).start();
				
				Server s = new Server(mapper, frame, null);
				s.start();
			}
		});
        
	}
	
}
