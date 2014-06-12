package de.mmi.multimodal_presentation.settings;

import de.mmi.multimodal_presentation.utils.FileManager;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class CacheClearActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Handler handler = new Handler();
		FileManager.clearCache(getApplicationContext());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), "Cache cleared", Toast.LENGTH_SHORT).show();
			}
		});
		
		finish();
	}

	

}
