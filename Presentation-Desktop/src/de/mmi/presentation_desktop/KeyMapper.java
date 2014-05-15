package de.mmi.presentation_desktop;

import java.awt.AWTException;
import java.awt.Robot;

import de.mmi.presentation_desktop.handler.KeyHandler;

/**
 * This class is an implementation of {@link de.mmi.presentation_desktop.handler.KeyHandler} and forwards received Key Events to OS or 
 * corresponding program (possibilities need to be examined).
 * @author patrick
 *
 */
public class KeyMapper implements KeyHandler{
	
	/**
	 * The robot will simulate button press
	 */
	Robot robot;
	
	public KeyMapper() throws AWTException{
		robot = new Robot();
	}

	@Override
	public void onKeyPressed(int keyCode) {
		robot.keyPress(keyCode);
		robot.keyRelease(keyCode);
	}
}
