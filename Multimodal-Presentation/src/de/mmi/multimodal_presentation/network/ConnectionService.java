package de.mmi.multimodal_presentation.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.mmi.multimodal_presentation.utils.FileManager;

public class ConnectionService extends Service{

	private final static int PORT = 62987;
	private final static int BITSTREAM_PORT = 62986;
	
	public final static String PREFS = "prefs";
	public final static String CONNECT = "connect";
	public final static String COMMAND = "command";
	public final static String REQUEST_IMAGES = "get-imgs";
	public final static String HIGHLIGHT = "highlight";
	public final static String POINT = "point";
	public final static float HIDE_POINTER = -2.0f;
	public final static String EXIT = "exit";
	public final static String START = "start";
	
	public final static String IP = "ip";
	
	private String serverIp = null;
	
	private Socket socket;
	private BufferedWriter writer;
	
	private MessageReaderThread messageReader;
	private DataReadThread dataReader;
	
	private Handler mHandler;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// is called each time startService(...) is called
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(mHandler == null)
			mHandler = new Handler();
		
		if(intent != null){
			String action = intent.getAction();
			if(action != null){
				if(action.equals(CONNECT)){
					String ip = intent.getStringExtra(IP);
					connectToIp(ip);
				}else if(action.equals(COMMAND)){
					String comm = intent.getStringExtra(COMMAND);
					sendCommand(comm);
				}else if(action.equals(HIGHLIGHT)){
					float x = intent.getFloatExtra("X", 0f);
					float y = intent.getFloatExtra("Y", 0f);
					sendHighlight(HIGHLIGHT, x,y);
				}else if(action.equals(EXIT)){
					exit();
				}else if(action.equals(REQUEST_IMAGES)){
					requestImages();
				}else if(action.equals(POINT)){
					float x = intent.getFloatExtra("X", 0f);
					float y = intent.getFloatExtra("Y", 0f);
					sendHighlight(POINT, x, y);
				}else if(action.equals(START)){
					sendStartPresentation();
				}
			}
		}
		
		
		return Service.START_STICKY;
	}
	
	/**
	 * Established connection to the Desktop-Server with given ip. The port used is {@link ConnectionService#PORT}
	 * @param ip The IP to connect to
	 */
	private void connectToIp(final String ip){
		new Thread(){
			public void run(){
				try{
					if(socket != null){
						socket.close();
					}
					
					socket = new Socket(InetAddress.getByName(ip.trim()), PORT);
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					messageReader = new MessageReaderThread( new BufferedReader(new InputStreamReader(socket.getInputStream())) );
					messageReader.start();
					
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Connection established", Toast.LENGTH_SHORT).show();					
						}
					});
					
					serverIp = ip;
					Log.i("Service", "connected to ip " + ip);
					
					// TODO: warning, currently automatically starting download of images when connected
					requestImages();
				}catch(IOException e){
					e.printStackTrace();
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Connect failed", Toast.LENGTH_SHORT).show();					
						}
					});
				}
			}
		}.start();
		
	}
	
	/**
	 * Sends a command to the server
	 * @param command The command to send. Can be {@link MessageSet#NEXT NEXT} or {@link MessageSet#PREVIOUS PREVIOUS}
	 */
	private void sendCommand(final String command){

		JsonObject jObj = new JsonObject();
		jObj.addProperty(MessageSet.KEY, command);
		
		sendMessage(jObj.toString());
	}
	
	/**
	 * Sends a message to the Server with given type and (relative position)
	 * @param type The message-type ({@link ConnectionService#HIGHLIGHT} or {@link ConnectionService#POINT})
	 * @param x
	 * @param y
	 */
	private void sendHighlight(final String type, final float x, final float y){
		JsonObject jObj = new JsonObject();
		jObj.addProperty(MessageSet.X_COORD, (double)x);
		jObj.addProperty(MessageSet.Y_COORD, (double)y);
		
		JsonObject posObj = new JsonObject();
		posObj.add(type, jObj);
		sendMessage(posObj.toString());
	}
	
	/**
	 * Starts a Server to retrieve Image-Data from Desktop-App and sends a request message that 
	 * Desktop App starts sending the data. 
	 * This is an extra connection and not uses the Socket used for JSON-Messages as 
	 * we directy transfer the raw image data. 
	 */
	private void requestImages(){
		Log.i("Service", "requesting images");
		// first create an image-reader thread
		if(dataReader != null && dataReader.isAlive()){
			//throw new IllegalStateException("Can't start read-thread before last one finished");
			dataReader.interrupt();
		}else{
			
		}
		
		dataReader = new DataReadThread(getApplicationContext(), serverIp, mHandler);
		dataReader.start();
		
		// now actually request images
		JsonObject jObj = new JsonObject();
		jObj.addProperty(MessageSet.IMAGE_REQUEST, MessageSet.IMAGE_REQUEST);
		sendMessage(jObj.toString());
	}
	
	/**
	 * Sends command to Desktop App to start presentation
	 */
	private void sendStartPresentation(){
		JsonObject jObj = new JsonObject();
		jObj.addProperty(MessageSet.START, MessageSet.START);
		sendMessage(jObj.toString());
	}
	
	/**
	 * Actually sends any message given as a String.
	 * @param message The String-message to be send (JSONObject as a String). Do not attach a newline to the end of the String, this will be done automatically here.
	 */
	private void sendMessage(final String message){
		new Thread(){
			public void run(){
				if(socket != null && writer != null && socket.isConnected()){
					try {
						writer.write(message + "\n");
						writer.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					mHandler.post(new Runnable() {					
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "No connection established, yet.",  Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}.start();
	}
	

	/**
	 * Sends a message to the Desktop-App to stop the Application and shuts down connection. 
	 * After execution of this method, the Socket will be closed.
	 */
	private void exit(){
			
		new Thread(){
			public void run(){

				if(socket != null){
					JsonObject jObj = new JsonObject();
					jObj.addProperty(MessageSet.EXIT, MessageSet.EXIT);
					
					try {
						writer.write(jObj.toString() + "\n");
						writer.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					try {
						socket.close();
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "Connection closed", Toast.LENGTH_SHORT).show();		
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "No connection established, yet.",  Toast.LENGTH_SHORT).show();
						}
					});
				}
				stopSelf();
			}
		}.start();
	}
	
	/**
	 * This class retrieves the Timer-Value set on Desktop App and saves it in SharedPreferences for further usage.
	 * @author patrick
	 *
	 */
	private class MessageReaderThread extends Thread{
		
		BufferedReader reader;
		
		public MessageReaderThread(BufferedReader reader){
			this.reader = reader;
		}
		
		public void run(){
			try{
				while(!isInterrupted() && reader != null){
					String str = reader.readLine();
					if(reader == null)
						this.interrupt();
					
					JsonParser parser = new JsonParser();
					JsonElement elem = parser.parse(str);
					JsonObject obj = (JsonObject) elem;
					
					JsonElement jsonTime = obj.get(MessageSet.SECONDS);
					
					if(jsonTime != null){
						int time = jsonTime.getAsInt();
						Log.i("ConnService", "received time: " + time);
						// store time in shared prefs.. 
						SharedPreferences shPrefs = getApplicationContext().getSharedPreferences(PREFS, 0);
						SharedPreferences.Editor editor = shPrefs.edit();
						editor.putInt(MessageSet.SECONDS, time);
						editor.commit();
					}
				}
			}catch(Exception e){
				// TODO: maybe handle.. 
			}
		}
	}
	
	/**
	 * This class creates a socket and connects to the server to receive images. It automatically interrupts itself if last image was received.
	 * This images are stored in internal storage (see Context.getFilesDir()) and are subfoldered by creation date.
	 * @author patrick
	 *
	 */
	private class DataReadThread extends Thread{
				
		private Socket dataSocket;
		private DataInputStream dataInStream;
		private Context ctx;
		
		private FileOutputStream fos;
		private Handler handler;
		
		public DataReadThread(Context ctx, String ip, Handler handler){			
			try {
				dataSocket = new Socket(InetAddress.getByName(ip.trim()), BITSTREAM_PORT);
				dataInStream = new DataInputStream(dataSocket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.ctx = ctx;
			
			this.handler = handler;
		}
		
		public void run(){
			
			try{
							
				File dirFile = FileManager.getNewPresentationFolder(ctx);
				
				int count = 0;
				
				while(!isInterrupted()){
					
					int len = dataInStream.readInt();
					
					if(len == -1){
						dataSocket.close();
						this.interrupt();
					}else{
						byte[] buffer = new byte[len];
					
						dataInStream.readFully(buffer, 0, len);
						
						fos = new FileOutputStream(dirFile.getAbsolutePath() + "/" + count + ".jpg");
						fos.write(buffer);
						fos.flush();
						fos.close();
						
						count++;
					}
					
				}
				
				Log.i("tag", "Done loading images.");

				handler.post(new Runnable() {				
					@Override
					public void run() {
						Toast.makeText(ctx, "Done loading images.", Toast.LENGTH_LONG).show();
					}
				});
				
			}catch(IOException e){
				e.printStackTrace();
				Log.i("tag", "Error in loading images.");
				handler.post(new Runnable() {				
					@Override
					public void run() {
						Toast.makeText(ctx, "Error in loading images.", Toast.LENGTH_LONG).show();
					}
				});
			}finally{
				if(dataSocket != null){
					try {
						dataSocket.close();
					} catch (IOException e) {}
				}
			}
		}
	}
}
