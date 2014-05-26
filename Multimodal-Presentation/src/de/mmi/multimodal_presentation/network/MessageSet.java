package de.mmi.multimodal_presentation.network;

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
	
	/*
	 * Values for key-type
	 */
	public final static String NEXT = "next";
	public final static String PREVIOUS = "previous";

	
	
}
