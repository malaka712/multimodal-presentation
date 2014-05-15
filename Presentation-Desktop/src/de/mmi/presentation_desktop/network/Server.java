package de.mmi.presentation_desktop.network;

import de.mmi.presentation_desktop.handler.GUIHandler;
import de.mmi.presentation_desktop.handler.KeyHandler;

/**
 * The class setting up a listener for input from android device and forwards incoming events to the given handlers.
 * @author patrick
 *
 */
public class Server extends Thread{

	private final static int PORT = 62987;
	
	KeyHandler mKeyHandler;
	GUIHandler mGUIHandler;
	
	public Server(KeyHandler keyHandler, GUIHandler guiHandler){
		this.mKeyHandler = keyHandler;
		this.mGUIHandler = guiHandler;
	}
	
	public void run(){
		// TODO: listen on port.. (communication via json with gson?)
		// Then use MessageTranslator to retrieve keyCode from String and forward to handler
	}
}
