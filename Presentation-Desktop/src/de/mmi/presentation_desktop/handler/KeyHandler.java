package de.mmi.presentation_desktop.handler;

public interface KeyHandler {

	/**
	 * This method will be called when a key event is received via the network
	 * @param keyCode The key code (as adapted from KeyEvent)
	 * @see java.awt.event.KeyEvent
	 */
	public void onKeyPressed(int keyCode);

}
