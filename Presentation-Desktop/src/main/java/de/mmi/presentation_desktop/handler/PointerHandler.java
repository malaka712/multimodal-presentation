package de.mmi.presentation_desktop.handler;

public interface PointerHandler {
	
	/**
	 * Will show pointer on Screen for given relative location
	 * @param x relative x-coordinate
	 * @param y relative y-coordinate
	 */
	public void onPoint(float x, float y);
	
	/**
	 * Will hide the pointer (or do nothing if pointer is hidden)
	 */
	public void onHidePointer();

}
