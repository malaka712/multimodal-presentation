package de.mmi.multimodal_presentation.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class may be utilized to create scaled bitmaps in order to save memory.
 * @author patrick
 *
 */
public class BitmapProvider {
	
	/**
	 * This method decodes an image from a resource and scales it to the given width. The height of the scaled bitmap is calculated with the 
	 * height of the original bitmap (ratio is kept the same)
	 * @param context The Context of the Activity or Application (needed to access resources)
	 * @param resourceId The resource id of the image (R.raw.some_id)
	 * @param viewWidth The width to scale the image to
	 * @return The scaled Bitmap
	 */
	public static Bitmap getScaledBitmap(Context context, int resourceId, int viewWidth){
		
		Bitmap b = BitmapFactory.decodeResource(context.getResources(), resourceId);
		Bitmap scaledBmp = scaleBitmap(b, viewWidth);
		
		// recycle old bitmap to free memory
		b.recycle();
		
		return scaledBmp;
	}
	
	private static Bitmap scaleBitmap(Bitmap original, int viewWidth){
		double ratio = (double)original.getHeight()/(double)original.getWidth();
		double width = (double) viewWidth;
		
		Bitmap scaledBmp = Bitmap.createScaledBitmap(original, (int)Math.round(width), (int)Math.round(ratio*width), false);
		return scaledBmp;
	}
	
	/**
	 * This method decodes a Bitmap from a File and returns a scaled bitmap with the given width. The height of the scaled bitmap is calculated with the 
	 * height of the original bitmap (ratio is kept the same)
	 * @param file The file to decode the bitmap from
	 * @param viewWidth The width of the view to scale to
	 * @return The scaled Bitmap or null if the image could not be decoded.
	 */
	public static Bitmap getScaledBitmap(File file, int viewWidth){
		
		Bitmap scaledBmp;
		Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
		
		try{
			scaledBmp = scaleBitmap(b , viewWidth);	
			b.recycle();
			
			if(scaledBmp == null)
				throw new NullPointerException("Could not decode Image, please check path.");
			
			return scaledBmp;
		}catch(NullPointerException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method decodes a list of Bitmap from the given files. The ratio of the image is kept. The height of the image 
	 * is calculated with respect to the ratio.
	 * @param files The image-files to decode from 
	 * @param viewWidth The width of the view the image is scaled to
	 * @return A list of scaled Bitmaps or null if one image failed to be decoded or an OutOfMemoryError occured during decoding.
	 */
	public static Bitmap[] getScaledBitmapArray(File[] files, int viewWidth){
		Bitmap[] bmpArray = null;
		try{
			bmpArray = new Bitmap[files.length];
			
			int len = bmpArray.length;
			for(int i=0; i<len; i++){
				bmpArray[i] = getScaledBitmap(files[i], viewWidth);
				if(bmpArray[i] == null)
					throw new NullPointerException("Decoded Image is null. Please check paths.");
			}
			
			return bmpArray;
		}catch(NullPointerException ex){
			
			ex.printStackTrace();
			
			if(bmpArray != null){
				int len = bmpArray.length;
				for(int i=0; i<len; i++){
					if(bmpArray[i] != null)
						bmpArray[i].recycle();
				}
			}
			
			return null;
		
		}catch(OutOfMemoryError er){
			er.printStackTrace();
			
			if(bmpArray != null){
				int len = bmpArray.length;
				for(int i=0; i<len; i++){
					if(bmpArray[i] != null)
						bmpArray[i].recycle();
				}
			}		
			
			return null;
		}
	}
}
