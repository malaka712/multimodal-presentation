package de.mmi.presentation_desktop.network;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class MyServer extends Thread {

	List<Image> images = new ArrayList<Image>();
	private int port = 62988;

	public MyServer(List<ImageIcon> allImages) {
		Iterator<ImageIcon> iter = allImages.iterator();
		for (int i = 0; i < images.size(); i++) {
			images.add(allImages.get(i).getImage());
		}
	}

	public void run() {
		try {
			ServerSocket sock = new ServerSocket(port);
			Socket socket = sock.accept();
			JsonReader reader;
			sock.accept();
			System.out.println("connection accepted");
			ImageIO.write((RenderedImage) images.get(0), "BMP",
					socket.getOutputStream());
			BufferedOutputStream buffer;
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (true) {
				String str = br.readLine();

				JsonParser parser = new JsonParser();
				JsonElement elem = parser.parse(str);
				JsonObject obj = (JsonObject) elem;

				JsonElement jsonPos = obj.get(MessageSet.HIGHLIGHT);
				JsonElement jsonKey = obj.get(MessageSet.KEY);
				JsonElement jsonExit = obj.get(MessageSet.EXIT);
				
				break;
			}

		
			socket.close();
			sock.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
