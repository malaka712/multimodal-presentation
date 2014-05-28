package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import de.mmi.multimodal_presentation.network.ConnectionService;
import de.mmi.multimodal_presentation.network.MessageSet;
import de.mmi.multimodal_presentation.utils.BitmapProvider;

public class PresentActivity extends Activity implements GestureDetector.OnGestureListener, OnTouchListener{

	//private final static String TAG = "presenter";
    private GestureDetector mGestureDetector;
	private ImageView[] currentSlides;
    private ImageView nextSlide;
    
    // this value defines how many bitmaps are stored before the current bmp of main and after current bmp of preview
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
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_present);
		
		mGestureDetector = new GestureDetector(this, this);
		
		currentSlides = new ImageView[2];
		currentSlides[0] = (ImageView) findViewById(R.id.current_slide_1);
		currentSlides[1] = (ImageView) findViewById(R.id.current_slide_2);
		currentSlides[1].setVisibility(View.INVISIBLE);
		
		currentSlides[0].setOnTouchListener(this);
		currentSlides[1].setOnTouchListener(this);
		
        nextSlide = (ImageView) findViewById(R.id.preview_reader);
        
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
		
		initBmps();
	}
	
	private void initBmps(){
		new Thread(){
			public void run(){
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);

				for(int i=0; i<=cacheWidth+1; i++){
					if(i >= rawImageResources.length)
						break;
					
					bmpBuffer[i] = BitmapProvider.getScaledBitmap(PresentActivity.this, rawImageResources[i], size.x);
				}
				
				nextSlide.post(new Runnable() {
					
					@Override
					public void run() {
						// load first bitmap in main view and preview view
						currentSlides[currentSlideViewIndex].setImageBitmap(bmpBuffer[0]);
						nextSlide.setImageBitmap(bmpBuffer[1]);						
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
		float x = e.getX();
		float y = e.getY();
		
		Intent i = new Intent(getApplicationContext(), ConnectionService.class);
		i.setAction(ConnectionService.HIGHLIGHT);
		i.putExtra("X", x / (float)currentSlides[currentSlideViewIndex].getWidth());
		i.putExtra("Y", y / (float)currentSlides[currentSlideViewIndex].getHeight());
		
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
		
		if(velocityX < 0f){
			
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
		
		ImageView showView = currentSlides[(currentSlideViewIndex+1)%2];	
		showView.setImageBitmap(bmpBuffer[currentSlide+1]);
		
		animate(showView, currentSlides[currentSlideViewIndex], animInForward, animOutForward);
		
		currentSlide++;
		currentSlideViewIndex = (currentSlideViewIndex+1)%2;
		
		updateBuffer();
	}
	
	
	private void previousSlide(){
		
		ImageView showView = currentSlides[(currentSlideViewIndex+1)%2];
		showView.setImageBitmap(bmpBuffer[currentSlide-1]);
		
		animate(showView, currentSlides[currentSlideViewIndex], animInBack, animOutBack);
		
		currentSlide--;
		currentSlideViewIndex = (currentSlideViewIndex+1)%2;
		
		updateBuffer();
	}
	
	private void animate(final ImageView showView, final ImageView hideView, Animation in, Animation out){
		
		in.setAnimationListener(new AnimationListener() {	
			@Override
			public void onAnimationStart(Animation animation) {
				showView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
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
				// TODO: load new bitmap, remove the ones out of range
			}
		}.start();
	}

	private void shakeView(){
		currentSlides[currentSlideViewIndex].startAnimation(shake);
	}
}
