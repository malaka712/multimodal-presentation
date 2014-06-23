package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.mmi.multimodal_presentation.network.ConnectionService;
import de.mmi.multimodal_presentation.settings.SettingsActivity;

public class InitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        
        /*
         * Get buttons via ID (as defined in layout-file)
         */
        Button presentButton = (Button) findViewById(R.id.start_present_activity);
        Button scanButton = (Button) findViewById(R.id.scan_ip);
        Button exitButton = (Button) findViewById(R.id.exit);
        Button downloadButton = (Button) findViewById(R.id.load_presentation);
        Button settingsButton = (Button) findViewById(R.id.settings_button);
        
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
            
        scanButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				new Thread(){
					public void run(){
						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
						startActivityForResult(intent, 0);	
					}
				}.start();	
			}
		});
        
        exitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ConnectionService.class);
				intent.setAction(ConnectionService.EXIT);
				startService(intent);
			}
		});
        
        downloadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ConnectionService.class);
				intent.setAction(ConnectionService.REQUEST_IMAGES);
				startService(intent);
			}
		});
        
        settingsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openSettings();
			}
		});
        //startService()
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
    	if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.i("InitAct", "Scanned ip: " + contents);
                
                Intent i = new Intent(getApplicationContext(), ConnectionService.class);
                i.setAction(ConnectionService.CONNECT);
                i.putExtra(ConnectionService.IP, contents);
                startService(i);
                
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(getApplicationContext(), "Scan not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// add menu to enable settings
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.init_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
	private void openSettings(){
		Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
		startActivity(settingsIntent);
	}
    
}
