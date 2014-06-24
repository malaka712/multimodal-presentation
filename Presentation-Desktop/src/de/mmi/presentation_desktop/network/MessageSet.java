package de.mmi.presentation_desktop.network;

public interface MessageSet {

	/*
	 * message fields
	 */
	public final static String MESSAGE_TYPE = "type";
	
	public final static String X_COORD = "x";
	public final static String Y_COORD = "y";

	/*
	 * Values for message type
	 */
	public final static String KEY = "key";
	public final static String HIGHLIGHT = "highlight";
	public final static String EXIT = "exit";
	public final static String IMAGE_REQUEST = "send-images";
	public final static String POINT = "point";
	public final static String SECONDS = "seconds";
	public final static String START = "start";
	
	/*
	 * Values for key-type
	 */
	public final static String NEXT = "next";
	public final static String PREVIOUS = "previous";

	/*
	 * Values for Pointer pos
	 */
	
	public final static float HIDE_POINTER = -2f;
	
	
}
