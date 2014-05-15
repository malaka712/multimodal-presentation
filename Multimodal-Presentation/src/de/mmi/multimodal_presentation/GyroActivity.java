package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class GyroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gyro);
		
		// Don't fall asleep as some devices shut down sensors when screen is off
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
