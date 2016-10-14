package de.mmi.presentation_desktop.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.mmi.presentation_desktop.core.Controller;
import de.mmi.presentation_desktop.handler.GUIHandler;
import de.mmi.presentation_desktop.handler.KeyHandler;
import de.mmi.presentation_desktop.handler.PointerHandler;
import de.mmi.presentation_desktop.utils.MessageTranslator;

/**
 * The class setting up a listener for input from android device and forwards incoming events to the given handlers.
 * @author patrick
 *
 */
public class Server extends Thread{

	private final static int PORT = 62987;
	
	KeyHandler mKeyHandler;
	GUIHandler mGUIHandler;
	PointerHandler mPointerHandler;
	Controller controller;
	
	ServerSocket sSocket;
	JsonReader reader;
	BufferedReader br;
	BufferedWriter writer;
	
	public Server(KeyHandler keyHandler, GUIHandler guiHandler, Controller controller, PointerHandler pointerHandler){
		this.mKeyHandler = keyHandler;
		this.mGUIHandler = guiHandler;
		this.controller = controller;
		this.mPointerHandler = pointerHandler;
	}
	
	public void run(){
		try {
			sSocket = new ServerSocket(PORT);
			System.out.println("server started");
			Socket socket = sSocket.accept();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			while(!isInterrupted()){
				//reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
				String str = br.readLine();
				if(br == null)
					this.interrupt();
				
				JsonParser parser = new JsonParser();
				JsonElement elem = parser.parse(str);
				JsonObject obj = (JsonObject) elem;
				
				JsonElement jsonPointer = obj.get(MessageSet.POINT);
				JsonElement jsonPos = obj.get(MessageSet.HIGHLIGHT);
				JsonElement jsonKey = obj.get(MessageSet.KEY);
				JsonElement jsonExit = obj.get(MessageSet.EXIT);
				JsonElement jsonImageReq = obj.get(MessageSet.IMAGE_REQUEST);
				JsonElement jsonStart = obj.get(MessageSet.START);
				

				float[] position = null;
				float[] pointerPos = null;
				String key = null;
				
				if(jsonKey != null){
					key = jsonKey.getAsString();
				}else if(jsonPos != null){
					position = new float[2];
					position[0] = (float)((JsonObject)jsonPos).get(MessageSet.X_COORD).getAsDouble();
					position[1] = (float)((JsonObject)jsonPos).get(MessageSet.Y_COORD).getAsDouble();
				}else if(jsonExit != null){
					if(socket != null){
						socket.close();
					}
					
					System.exit(0);
				}else if (jsonImageReq != null){
					// App has requested images
					controller.sendImages();
					// as to change program-flow, also send time
					controller.sendTimerData();
				}else if (jsonPointer != null){
					pointerPos = new float[2];
					pointerPos[0] = (float)((JsonObject)jsonPointer).get(MessageSet.X_COORD).getAsDouble();
					pointerPos[1] = (float)((JsonObject)jsonPointer).get(MessageSet.Y_COORD).getAsDouble();
				}else if (jsonStart != null){
					controller.startPresentation();
				}else{
					System.out.println("null");
				}
				
				//reader.beginObject();
				
				/*while(reader.hasNext()){
					name = reader.nextName();
					if(name.equals(MessageSet.KEY)){
						key = reader.nextString();
					}else if(name.equals(MessageSet.HIGHLIGHT)){
						position = readPosition(reader);
					}else{
						throw new IllegalArgumentException("Received unkown JSON-Name: " + name);
					}
				}
				reader.endObject();
				*/
				if(key != null){
					final String finalKey = key;
					new Thread(){
						public void run(){
							//System.out.println("received key event " + finalKey);
							mKeyHandler.onKeyPressed(MessageTranslator.translateToKeyCode(finalKey));
							mGUIHandler.hideFrame();
						}
					}.start();
				}else if(position != null){
					final float[] pos = position;
					new Thread(){
						public void run(){
							//System.out.println("received position [" + pos[0] + ", " + pos[1] + "]");
							mGUIHandler.onHighlight(pos[0], pos[1]);
						}
					}.start();
				
				}else if(pointerPos != null){
					final float[] pPos = pointerPos;
					new Thread(){
						public void run(){
							
							if(pPos[0] == pPos[1] && pPos[0] == MessageSet.HIDE_POINTER)
								mPointerHandler.onHidePointer();
							else
								mPointerHandler.onPoint(pPos[0], pPos[1]);
						}
					}.start();
				}else{
					//throw new IllegalStateException("Reader did not read anything!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(sSocket != null){
				try{
					sSocket.close();
				}catch(IOException e){}
			}
		}
	}
	
	public void sendTimerMessage(final int time){
		new Thread(){
			public void run(){
				if(writer != null){
					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty(MessageSet.SECONDS, time);
					try{
						System.out.println("Sending message: " + jsonObj.toString());
						writer.write(jsonObj.toString() + "\n");
						writer.flush();
					}catch(IOException e){
						e.printStackTrace();
					}
				}else{
					System.err.println("Not connected.. can't send timer");
				}
			}
		}.start();
	}

}
