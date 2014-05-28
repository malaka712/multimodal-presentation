package de.mmi.multimodal_presentation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapProvider {
	
	
	public static Bitmap getScaledBitmap(Context context, int resourceId, int viewWidth){
		
		Bitmap b = BitmapFactory.decodeResource(context.getResources(), resourceId);
		double ratio = (double)b.getHeight()/(double)b.getWidth();
		double width = (double) viewWidth;
		
		Bitmap scaledBmp = Bitmap.createScaledBitmap(b, (int)Math.round(width), (int)Math.round(ratio*width), false);
		
		// recycle old bitmap to free memory
		b.recycle();
		
		return scaledBmp;
	}
}
