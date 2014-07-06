package de.mmi.multimodal_presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.mmi.multimodal_presentation.network.ConnectionService;
import de.mmi.multimodal_presentation.settings.CacheClearActivity;

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
        
        /*
         * Add onclicklisteners to start corresponding activity 
         */
        presentButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				// start present-activity
				Intent actInt = new Intent(getApplicationContext(), PresentActivity.class);
				startActivity(actInt);
				
				// tell desktop to start presentation
				Intent service = new Intent(getApplicationContext(), ConnectionService.class);
				service.setAction(ConnectionService.START);
				startService(service);
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

    /*
	private void openSettings(){
		Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
		startActivity(settingsIntent);
	}*/
    
    @Override
    public void onBackPressed(){
        // catch this event to clear cache.. 
    	
    	AlertDialog dialog = new AlertDialog.Builder(this)
    		.setMessage("Should the current presentation be removed and connection closed?")
    		.setTitle("Done?")
    		.setPositiveButton("Yes, done!",  new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// clear cache
					Intent i = new Intent(InitActivity.this, CacheClearActivity.class);
					startActivity(i);
					
					// exit connection(, close desktop side )
					Intent intent = new Intent(getApplicationContext(), ConnectionService.class);
					intent.setAction(ConnectionService.EXIT);
					startService(intent);
					back();
				}
			})
			.setNegativeButton("No, keep", new OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					back();
				}
			}).setNeutralButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
										
				}
			}).create();
    	
    	dialog.show();
    }
    
    private void back(){
    	super.onBackPressed();
    }
}
