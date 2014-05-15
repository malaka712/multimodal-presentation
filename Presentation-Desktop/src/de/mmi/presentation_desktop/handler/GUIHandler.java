package de.mmi.presentation_desktop.handler;

public interface GUIHandler {
	
	/**
	 * This method is called when a position to highlight on the Desktop is received via the network
	 * @param x The (relative ?) x-coordinate
	 * @param y The (relative ?) y-coordinate
	 */
	public void onHighlight(float x, float y);
}
