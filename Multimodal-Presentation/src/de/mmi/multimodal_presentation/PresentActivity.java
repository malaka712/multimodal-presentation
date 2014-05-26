package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.qoppa.android.pdfViewer.fonts.StandardFontTF;
import com.qoppa.viewer.QPDFViewerView;
import com.qoppa.viewer.listeners.DocumentListener;

import de.mmi.multimodal_presentation.network.ConnectionService;
import de.mmi.multimodal_presentation.network.MessageSet;

public class PresentActivity extends Activity implements GestureDetector.OnGestureListener, OnTouchListener{

	//private final static String TAG = "presenter";
	private QPDFViewerView pdfView;
    private View gestureCatcher;
    private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_present);
		
		StandardFontTF.mAssetMgr = getAssets();
		
		mGestureDetector = new GestureDetector(this, this);
		
		gestureCatcher = findViewById(R.id.gesture_catcher);
		gestureCatcher.setOnTouchListener(this);
		
		pdfView = (QPDFViewerView) findViewById(R.id.main_reader);
		/*				
		ViewGroup parent = (ViewGroup) pdfView.getParent();
		int index = parent.indexOfChild(pdfView);
		parent.removeView(pdfView);
		pdfView = new QPDFViewerView(this);
		parent.addView(pdfView, index);		
		*/
		
		pdfView.setActivity(this);
		
		/*pdfView.addDocumentListener(new DocumentListener() {
			@Override
			public void zoomChanged(float arg0) {}
			
			@Override
			public void documentSaved(String arg0) {}
			
			@Override
			public void documentOpened() {
				previewReader.setDocument(pdfView.getDocument());
				previewReader.goToPage(2);
			}
		});
		*/
		pdfView.open();
		pdfView.getToolbar().setVisibility(View.GONE);
		
		pdfView.setMaximumScale(100f);
		pdfView.setTouchListenerActive(false);
		
        
		// Don't fall asleep as we need to access screen the whole time while presenting
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  pdfView.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.mGestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		
		//float relativeX = 0f;
		//float relativeY = 0f;
		
		View pageView = pdfView.getPageView(pdfView.getCurrentPageNumber()-1);
		
		//int top = pageView.getTop();
		//int bottom = pageView.getBottom();
		
		/*if(top != pdfView.getTop()){
			
		}else if(bottom != pdfView.getBottom()){
			
		}*/
		
		Intent i = new Intent(getApplicationContext(), ConnectionService.class);
		i.setAction(ConnectionService.HIGHLIGHT);
		i.putExtra("X", x / (float)pageView.getWidth());
		i.putExtra("Y", y / (float)pageView.getHeight());
		
		startService(i);
		
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		String command;
		
		if(velocityX < 0f){
			command = MessageSet.NEXT;
			pdfView.nextPage();
		}else{
			command = MessageSet.PREVIOUS;
			pdfView.prevPage();
		}
		
		pdfView.getPageView(pdfView.getCurrentPageNumber()-1).invalidate();
		
		Intent i = new Intent(getApplicationContext(), ConnectionService.class);
		i.setAction(ConnectionService.COMMAND);
		i.putExtra(ConnectionService.COMMAND, command);
		
		startService(i);
		
		return true;
	}

}
