package de.mmi.presentation_desktop.network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataServer extends Thread{
	
	private final static int PORT = 62986;

	private ServerSocket sSocket;
	private Socket s;
	private DataOutputStream outStream;
	
	public void run(){
		try {
			sSocket = new ServerSocket(PORT);
			s = sSocket.accept();
			
			outStream = new DataOutputStream(s.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendImages(File basePath) throws IOException{
		
		if(outStream != null && s != null && !s.isClosed()){
			for(File curr : basePath.listFiles()){
				byte[] data = getImageBytes(curr);
				System.out.println("Sending length " + data.length);
				outStream.writeInt(data.length);
				outStream.write(data);
				outStream.flush();
			}
			outStream.writeInt(-1);
		}
	}
	
	public void close(){
		try {
			s.close();
			sSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private byte[] getImageBytes(File image) throws IOException{	
		
		FileInputStream fis = new FileInputStream(image);
		int l = fis.available();
		byte[] data = new byte[l];
		fis.read(data);
		fis.close();

		return data;
	}

}