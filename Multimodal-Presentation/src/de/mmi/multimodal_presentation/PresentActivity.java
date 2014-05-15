package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class PresentActivity extends Activity {

	private final static String TAG = "presenter";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_present);
		
		// Don't fall asleep as we need to access screen the whole time while presenting
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
