package de.mmi.multimodal_presentation.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.google.gson.JsonObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectionService extends Service{

	private final static int PORT = 62987;
	private final static int BITSTREAM_PORT = 62986;
	
	public final static String CONNECT = "connect";
	public final static String COMMAND = "command";
	public final static String REQUEST_IMAGES = "get-imgs";
	public final static String HIGHLIGHT = "highlight";
	public final static String EXIT = "exit";
	
	public final static String IP = "ip";
	
	private Socket socket;
	private BufferedWriter writer;
	
	private Handler mHandler;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

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
					sendHighlight(x,y);
				}else if(action.equals(EXIT)){
					exit();
				}else if(action.equals(REQUEST_IMAGES)){
					requestImages();
				}
			}
		}
		
		
		return Service.START_STICKY;
	}
	
	
	private void connectToIp(final String ip){
		new Thread(){
			public void run(){
				try{
					if(socket != null){
						socket.close();
					}
					
					socket = new Socket(InetAddress.getByName(ip.trim()), PORT);
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Connection established", Toast.LENGTH_SHORT).show();					
						}
					});
					
					Log.i("Service", "connected to ip " + ip);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}.start();
		
	}
	
	private void sendCommand(final String command){
		new Thread(){
			public void run(){
				if(socket != null && writer != null && socket.isConnected()){
					
					JsonObject jObj = new JsonObject();
					jObj.addProperty(MessageSet.KEY, command);
					try {
						writer.write(jObj.toString() + "\n");
						writer.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}.start();	
	}
	
	private void sendHighlight(final float x, final float y){
		new Thread(){
			public void run(){
				if(socket != null && writer != null && socket.isConnected()){
					
					JsonObject jObj = new JsonObject();
					jObj.addProperty(MessageSet.X_COORD, (double)x);
					jObj.addProperty(MessageSet.Y_COORD, (double)y);
					
					JsonObject posObj = new JsonObject();
					posObj.add(MessageSet.HIGHLIGHT, jObj);
					
					try {
						writer.write(posObj.toString() + "\n");
						writer.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}.start();	
	}
	
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
						// TODO Auto-generated catch block
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
	
	private void requestImages(){
		new Thread(){
			public void run(){
				if(socket != null && writer != null && socket.isConnected()){
					
					JsonObject jObj = new JsonObject();
					jObj.addProperty(MessageSet.IMAGE_REQUEST, MessageSet.IMAGE_REQUEST);
					try {
						writer.write(jObj.toString() + "\n");
						writer.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}.start();	
	}
}
