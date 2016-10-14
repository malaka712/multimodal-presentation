package de.mmi.multimodal_presentation.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

public class FileManager {

	private final static Comparator<File> fileComparator = new Comparator<File>(){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-FF--HH-mm-ss", Locale.GERMANY);
		
		@Override
		public int compare(File lhs, File rhs) {
			int diff = 0;
			try {
				Date leftDate = dateFormat.parse(lhs.getName());
				Date rightDate = dateFormat.parse(rhs.getName());
				diff = leftDate.compareTo(rightDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return diff;
		}
	};
	
	/**
	 * Creates new folder (by date) and return corresponding File
	 * @param ctx
	 * @return
	 */
	public static File getNewPresentationFolder(Context ctx){
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-FF--HH-mm-ss", Locale.GERMANY);
		
		File mainDir = ctx.getFilesDir();
		File dirFile = new File(mainDir.getAbsolutePath() + "/" + dateFormat.format(new Date(System.currentTimeMillis())));
		dirFile.mkdir();
		
		return dirFile;	
	}
	
	/**
	 * Returns all files within folder with latest timestamp
	 * @param ctx
	 * @return
	 */
	public static File[] getLatestPresentationFiles(Context ctx){
		
		File basePath = ctx.getFilesDir();
		File[] list = basePath.listFiles();
		
		if(list != null && list.length > 0){
			Arrays.sort(list, fileComparator);
			
			basePath = list[list.length-1];
			
			return basePath.listFiles();
		}
			
		return null;
	}

	/**
	 * Deletes all files in the Apps cache
	 * @param ctx
	 */
	public static void clearCache(Context ctx) {	
		File basePath = ctx.getFilesDir();
		
		for(File child : basePath.listFiles())
			deleteRecursively(child);
	}
	
	private static void deleteRecursively(File f){
		
		if(f.isDirectory()){
			for(File child : f.listFiles()){
				deleteRecursively(child);
			}
		}
		
		if(!f.delete()){
			Log.e("FileManager", "ERROR: could not delete: " + f.getAbsolutePath());
		}else{
			Log.i("FileManager", "deleted file " + f.getAbsolutePath());
		}
	}
}
