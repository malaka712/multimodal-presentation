package de.mmi.presentation_desktop.utils;

import java.awt.event.KeyEvent;
import java.util.Random;

import de.mmi.presentation_desktop.handler.GUIHandler;
import de.mmi.presentation_desktop.handler.KeyHandler;

public class Tester extends Thread{
	
	private final static long SLEEP_TIME = 1000L;
	
	private KeyHandler keyHandler;
	private GUIHandler guiHandler;
	
	
	public Tester(KeyHandler listener, GUIHandler guiHandler){
		this.keyHandler = listener;
		this.guiHandler = guiHandler;
	}
	
	public void run(){
		Random r = new Random();
		int x = 0;
		try{
			while(!isInterrupted()){
				Thread.sleep(SLEEP_TIME);
				keyHandler.onKeyPressed(KeyEvent.VK_DOWN);
				if(x%2 == 0)
					guiHandler.onHighlight(r.nextFloat(), r.nextFloat());
				
				x++;
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

}
