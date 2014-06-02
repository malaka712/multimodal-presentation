package de.mmi.multimodal_presentation;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import de.mmi.multimodal_presentation.network.ConnectionService;
import de.mmi.multimodal_presentation.network.MessageSet;
import de.mmi.multimodal_presentation.timer.CountdownTask;
import de.mmi.multimodal_presentation.utils.BitmapProvider;

public class PresentActivity extends Activity implements GestureDetector.OnGestureListener, OnTouchListener{

	private final static String TAG = "presenter";
    private GestureDetector mGestureDetector;
	private ImageView[] currentSlideViews;
    private ImageView nextSlideView;
    private TextView lastSlideTextView;
    private TextView timerTextView;
    
    // this value defines how many bitmaps are stored before the current bmp of main and after current bmp of preview
    // NOTE: If this value is 0, this will cause a crash when trying to move to next/previous slide, as we assume there is at least one image buffered.
    private int cacheWidth = 1;
    private Bitmap[] bmpBuffer;
    private int[] rawImageResources;
    
    private int currentSlideViewIndex = 0;
    private int currentSlide = 0;
    
    private AnimationSet animOutForward;
    private AnimationSet animInForward;
    private AnimationSet animOutBack;
    private AnimationSet animInBack;
    private Animation shake;
    private final static int ANIMATION_DURATION = 400;
    
    CountdownTask cTask;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_present);
		
		mGestureDetector = new GestureDetector(this, this);
		
		currentSlideViews = new ImageView[2];
		currentSlideViews[0] = (ImageView) findViewById(R.id.current_slide_1);
		currentSlideViews[1] = (ImageView) findViewById(R.id.current_slide_2);
		currentSlideViews[1].setVisibility(View.INVISIBLE);
		
		currentSlideViews[0].setOnTouchListener(this);
		currentSlideViews[1].setOnTouchListener(this);
		
        nextSlideView = (ImageView) findViewById(R.id.preview_reader);
        lastSlideTextView = (TextView) findViewById(R.id.no_more_slide_text);
        timerTextView = (TextView) findViewById(R.id.timer_text);
        
        rawImageResources = new int[]{
        		R.raw.test1,
        		R.raw.test2,
        		R.raw.test3
        };
        
        bmpBuffer = new Bitmap[rawImageResources.length];
        for(int i=0; i<bmpBuffer.length; i++)
        	bmpBuffer[i] = null;
        
        initAnimations();
        
		// Don't fall asleep as we need to access screen the whole time while presenting
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//updateBuffer();
		initBmps();
		
		
	}
	
	private void initBmps(){
		new Thread(){
			public void run(){
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);

				// TODO: move to image decoding from file
				for(int i=0; i<=cacheWidth+1; i++){
					if(i >= rawImageResources.length)
						break;
					
					bmpBuffer[i] = BitmapProvider.getScaledBitmap(PresentActivity.this, rawImageResources[i], size.x);
				}
				
				nextSlideView.post(new Runnable() {
					
					@Override
					public void run() {
						// load first bitmap in main view and preview view
						currentSlideViews[currentSlideViewIndex].setImageBitmap(bmpBuffer[0]);
						nextSlideView.setImageBitmap(bmpBuffer[1]);		
						cTask = new CountdownTask(10000L, timerTextView, PresentActivity.this);
						cTask.start();
					}
				});
				
			}
		}.start();
		
	}
	
	private void initAnimations(){
		
		// goes out left and becomes transparent
		animOutForward = new AnimationSet(true);
		animOutForward.setDuration(ANIMATION_DURATION);
		animOutForward.addAnimation(new AlphaAnimation(1.0f, 0.0f));
		animOutForward.addAnimation(new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 		// fromXType
				0f, 								// fromXValue
				Animation.RELATIVE_TO_SELF, 		// toXType 
				-1f, 								// toXValue
				Animation.RELATIVE_TO_SELF, 		// fromYType
				0f, 								// fromYValue
				Animation.RELATIVE_TO_SELF, 		// toYType
				0f));								// toYValue
		
		// comes in from back (from small to real size) and becomes opaque
		animInForward = new AnimationSet(true);
		animInForward.setDuration(ANIMATION_DURATION);
		animInForward.addAnimation(new AlphaAnimation(0.0f, 1.0f));
		animInForward.addAnimation(new ScaleAnimation(
				0f,							// fromX 
				1f,  						// toX
				0f, 						// fromY
				1f,  						// toY
				Animation.RELATIVE_TO_SELF, // pivotXType
				0.5f, 						// pivotXValue
				Animation.RELATIVE_TO_SELF, // pivotYType
				0.5f)); 					// pivotYValue
		
		// comes in from left and becomes opaque
		animInBack = new AnimationSet(true);
		animInBack.setDuration(ANIMATION_DURATION);
		animInBack.addAnimation(new AlphaAnimation(0.0f, 1.0f));
		animInBack.addAnimation(new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 		// fromXType
				-1f, 								// fromXValue
				Animation.RELATIVE_TO_SELF, 		// toXType 
				0f, 								// toXValue
				Animation.RELATIVE_TO_SELF, 		// fromYType
				0f, 								// fromYValue
				Animation.RELATIVE_TO_SELF, 		// toYType
				0f));								// toYValue
		
		// goes out back (from real size to 0) and becomes transparent
		animOutBack = new AnimationSet(true);
		animOutBack.setDuration(ANIMATION_DURATION);
		animOutBack.addAnimation(new AlphaAnimation(1.0f, 0.0f));
		animOutBack.addAnimation(new ScaleAnimation(
				1f,							// fromX 
				0f,  						// toX
				1f, 						// fromY
				0f,  						// toY
				Animation.RELATIVE_TO_SELF, // pivotXType
				0.5f, 						// pivotXValue
				Animation.RELATIVE_TO_SELF, // pivotYType
				0.5f)); 					// pivotYValue
		
		
		shake = AnimationUtils.loadAnimation(this, R.anim.shake);
	}

	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		// if app is going to background, we forget about timer.. 
		if(cTask != null){
			cTask.cancel();
			cTask = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.mGestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO: Hier, Monika. 
		float x = e.getX();
		float y = e.getY();
		
		// Intent erstellen
		Intent i = new Intent(getApplicationContext(), ConnectionService.class);
		// Sagen, dass position gesendet werden soll
		i.setAction(ConnectionService.HIGHLIGHT);
		
		// position als relativen wert (zwischen 0 und 1) als float eingeben
		i.putExtra("X", x / (float)currentSlideViews[currentSlideViewIndex].getWidth());
		i.putExtra("Y", y / (float)currentSlideViews[currentSlideViewIndex].getHeight());
		
		// service starten -> relative koordinaten werden dann automatisch gesendet, wenn die verbindung steht. 
		// wenn die verbindung nicht steht, passiert nichts (auch kein crash)
		// details kannst du dir (wenn du möchtest) in der ConnectionService Klasse anschauen
		startService(i);
		
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO: move image under finger
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		String command;
		
		float compVal;
		if(Math.abs(velocityX) > Math.abs(velocityY))
			compVal = velocityX;
		else 
			compVal = velocityY;
		
		if(compVal < 0f){
			
			if(currentSlide == bmpBuffer.length-1){
				shakeView();
				return true;
			}
			
			command = MessageSet.NEXT;
			nextSlide();

		}else{
			if(currentSlide == 0){
				shakeView();
				return true;
			}
			
			command = MessageSet.PREVIOUS;
			previousSlide();

		}
		
		Intent i = new Intent(getApplicationContext(), ConnectionService.class);
		i.setAction(ConnectionService.COMMAND);
		i.putExtra(ConnectionService.COMMAND, command);
		
		startService(i);
		
		return true;
	}

	
	private void nextSlide(){
		
		ImageView showView = currentSlideViews[(currentSlideViewIndex+1)%2];	
		showView.setImageBitmap(bmpBuffer[currentSlide+1]);
		
		animateSlideChange(showView, currentSlideViews[currentSlideViewIndex], animInForward, animOutForward);
		
		currentSlide++;
		currentSlideViewIndex = (currentSlideViewIndex+1)%2;
		
		int previewSlide = currentSlide+1;
		if(previewSlide != bmpBuffer.length){
			animateSlidePreview(bmpBuffer[previewSlide], true);
		}else{
			lastSlideTextView.setVisibility(View.VISIBLE);
			nextSlideView.setVisibility(View.INVISIBLE);
		}
		
		updateBuffer();
	}
	
	
	private void previousSlide(){
		
		ImageView showView = currentSlideViews[(currentSlideViewIndex+1)%2];
		showView.setImageBitmap(bmpBuffer[currentSlide-1]);
		
		animateSlideChange(showView, currentSlideViews[currentSlideViewIndex], animInBack, animOutBack);
		
		int previewSlide = currentSlide;
				
		currentSlide--;
		currentSlideViewIndex = (currentSlideViewIndex+1)%2;
		
		if(previewSlide != bmpBuffer.length){
			animateSlidePreview(bmpBuffer[previewSlide], (previewSlide != bmpBuffer.length-1));
		}else{
			lastSlideTextView.setVisibility(View.VISIBLE);
			nextSlideView.setVisibility(View.INVISIBLE);
		}
		
		updateBuffer();
	}
	
	private void animateSlideChange(final ImageView showView, final ImageView hideView, Animation in, Animation out){
		
		in.setAnimationListener(new AnimationListener() {	
			@Override
			public void onAnimationStart(Animation animation) {
				showView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				if(cTask != null)
					cTask.cancel();
				
				cTask = new CountdownTask(10000L, timerTextView, PresentActivity.this);
				cTask.start();
			}
		});
		
		out.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				hideView.setVisibility(View.INVISIBLE);
			}
		});
		
		showView.startAnimation(in);
		hideView.startAnimation(out);
	}
	
	private void updateBuffer(){
		new Thread(){
			public void run(){
				
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				
				for(int i=(currentSlide-cacheWidth); i<(currentSlide+cacheWidth+1); i++){
					
					// loop might be out of range
					if(i<0)	continue;
					if(i>=bmpBuffer.length)	continue;
					
					if(bmpBuffer[i] == null){
						//bmpBuffer[i] = BitmapProvider.getScaledBitmap(new File("" /* TODO: add file path here*/), size.x);
						bmpBuffer[i] = BitmapProvider.getScaledBitmap(PresentActivity.this, rawImageResources[i], size.x);
						Log.i(TAG, "got bitmap " + i);
					}
				}
				
				
				/* TODO: This code recycles the bitmap that is not in range of buffer anymore 
				 * it only works if we don't make any jumps, as it is only removing the one bmp before 
				 * and after current cache-window. This is faster than iterating through whole image-array
				 * but needs to be changed if there is a possibility to jump in presentation.*/
				int previousBmp = (currentSlide-cacheWidth-1);
				if(previousBmp >= 0 && bmpBuffer[previousBmp].isRecycled()){
					Log.i(TAG, "deleting img " + previousBmp);
					bmpBuffer[previousBmp].recycle();
					bmpBuffer[previousBmp] = null;
				}
				
				int nextBmp = (currentSlide+cacheWidth+1);
				if(nextBmp < bmpBuffer.length && bmpBuffer[nextBmp].isRecycled()){
					Log.i(TAG, "deleting img " + nextBmp);
					bmpBuffer[nextBmp].recycle();
					bmpBuffer[nextBmp] = null;
				}
			}
		}.start();
	}

	private void shakeView(){
		currentSlideViews[currentSlideViewIndex].startAnimation(shake);
	}
	
	/**
	 * This method starts an animation for the slide-preview imageview 
	 * @param newBmp The new bitmap that will be shown in preview window
	 * @param full true when there currently is an image displayed in preview-window, false otherwise
	 */
	private void animateSlidePreview(final Bitmap newBmp, final boolean full){
		
		final Animation inAlpha = new AlphaAnimation(0.0f, 1.0f);
		inAlpha.setDuration(ANIMATION_DURATION/2);
		inAlpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				nextSlideView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
		
		Animation outAlpha = new AlphaAnimation(1.0f, 0.0f);
		outAlpha.setDuration(ANIMATION_DURATION/2);
		outAlpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				lastSlideTextView.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				nextSlideView.setVisibility(View.INVISIBLE);
				nextSlideView.setImageBitmap(newBmp);
				nextSlideView.startAnimation(inAlpha);
			}
		});
		
		if(full)
			nextSlideView.startAnimation(outAlpha);
		
		else{
			nextSlideView.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					nextSlideView.setVisibility(View.INVISIBLE);
					nextSlideView.setImageBitmap(newBmp);
					nextSlideView.startAnimation(inAlpha);
					
				}
			}, ANIMATION_DURATION/2);
		}
	}
}
