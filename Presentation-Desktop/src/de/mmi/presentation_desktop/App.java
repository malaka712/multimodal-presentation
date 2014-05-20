package de.mmi.presentation_desktop;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.SwingUtilities;

import de.mmi.presentation_desktop.network.Server;
import de.mmi.presentation_desktop.ui.HighlightFrame;
import de.mmi.presentation_desktop.utils.Tester;

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
				
				Server s = new Server(mapper, frame);
				s.start();
			}
		});
        
        
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

	        for (NetworkInterface netint : Collections.list(nets))
	            displayInterfaceInformation(netint);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.printf("Display name: %s\n", netint.getDisplayName());
        System.out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	System.out.printf("InetAddress: %s\n", inetAddress);
        }
        System.out.printf("\n");
     }
}
