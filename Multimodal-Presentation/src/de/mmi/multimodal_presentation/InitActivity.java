package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        
        /*
         * Get buttons via ID (as defined in layout-file)
         */
        Button presentButton = (Button) findViewById(R.id.start_present_activity);
        Button gyroButton = (Button) findViewById(R.id.start_gyro_activity);
        
        /*
         * Add onclicklisteners to start corresponding activity 
         */
        presentButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent actInt = new Intent(getApplicationContext(), PresentActivity.class);
				startActivity(actInt);
			}
		});
        
        gyroButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent actInt = new Intent(getApplicationContext(), GyroActivity.class);
				startActivity(actInt);
			}
		});
    }
}
