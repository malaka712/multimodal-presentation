package de.mmi.multimodal_presentation.settings;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import de.mmi.multimodal_presentation.utils.FileManager;

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
