package de.mmi.presentation_desktop.utils;

import java.awt.event.KeyEvent;

import de.mmi.presentation_desktop.network.MessageSet;

public class MessageTranslator {

	/**
	 * This method translates a String into a keyCode.
	 * @param event The Event as a String (e.g. "next", "previous")
	 * @return The keyCode as adapter from {@link java.awt.event.KeyEvent KeyEvent}
	 */
	public static int translateToKeyCode(String event){

		int code = -1;
		
		if(event.equals(MessageSet.NEXT)){
			code = KeyEvent.VK_DOWN;
		}else if (event.equals(MessageSet.PREVIOUS)){
			code = KeyEvent.VK_UP;
		}else{
			throw new IllegalArgumentException("Unkown Argument: " + event);
		}
		
		return code;
	}
}
