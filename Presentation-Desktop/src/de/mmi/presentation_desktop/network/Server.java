package de.mmi.presentation_desktop.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import de.mmi.presentation_desktop.handler.GUIHandler;
import de.mmi.presentation_desktop.handler.KeyHandler;
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
	
	ServerSocket sSocket;
	JsonReader reader;
	BufferedReader br;
	JsonWriter writer;
	
	public Server(KeyHandler keyHandler, GUIHandler guiHandler){
		this.mKeyHandler = keyHandler;
		this.mGUIHandler = guiHandler;
	}
	
	public void run(){
		try {
			sSocket = new ServerSocket(PORT);
			Socket socket = sSocket.accept();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
			//writer = new JsonWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			while(!isInterrupted()){
				//reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
				String str = br.readLine();
				if(br == null)
					this.interrupt();
				
				JsonParser parser = new JsonParser();
				JsonElement elem = parser.parse(str);
				JsonObject obj = (JsonObject) elem;
				
				JsonElement jsonPos = obj.get(MessageSet.HIGHLIGHT);
				JsonElement jsonKey = obj.get(MessageSet.KEY);
				
				String name;
				float[] position = null;
				String key = null;
				
				if(jsonKey != null){
					key = jsonKey.getAsString();
				}else if(jsonPos != null){
					position = new float[2];
					position[0] = (float)((JsonObject)jsonPos).get(MessageSet.X_COORD).getAsDouble();
					position[1] = (float)((JsonObject)jsonPos).get(MessageSet.Y_COORD).getAsDouble();
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
							System.out.println("received key event " + finalKey);
							mKeyHandler.onKeyPressed(MessageTranslator.translateToKeyCode(finalKey));
						}
					}.start();
				}else if(position != null){
					final float[] pos = position;
					new Thread(){
						public void run(){
							System.out.println("received position [" + pos[0] + ", " + pos[1] + "]");
							mGUIHandler.onHighlight(pos[0], pos[1]);
						}
					}.start();
				}else{
					throw new IllegalStateException("Reader did not read anything!");
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
	
	private float[] readPosition(JsonReader reader) throws IOException{
		float[] retVal = new float[2];
		
		reader.beginObject();
		String name;
		while(reader.hasNext()){
			name = reader.nextName();
			if(name.equals(MessageSet.X_COORD)){
				retVal[0] = (float) reader.nextDouble();
			}else if(name.equals(MessageSet.Y_COORD)){
				retVal[1] = (float) reader.nextDouble();
			}else{
				throw new IllegalArgumentException("Received unexpected JSON-Name: " + name);
			}
		}
		reader.endObject();
		System.out.println("Read [" + retVal[0] + ", " + retVal[1] + "]");
		return retVal;
	}
}
