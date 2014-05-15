package de.mmi.presentation_desktop;

import de.mmi.presentation_desktop.handler.KeyHandler;

/**
 * This class is an implemenation of {@link de.mmi.presentation_desktop.handler.KeyHandler} and forwards received Key Events to OS or 
 * corresponding program (possibilities need to be examined).
 * @author patrick
 *
 */
public class KeyMapper implements KeyHandler{

	@Override
	public void onKeyPressed(int keyCode) {
		// TODO: forward key to OS/corresponding program..
	}
}
