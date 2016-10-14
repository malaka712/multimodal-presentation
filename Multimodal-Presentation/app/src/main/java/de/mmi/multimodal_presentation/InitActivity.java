package de.mmi.multimodal_presentation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileNotFoundException;

import de.mmi.multimodal_presentation.network.ConnectionService;
import de.mmi.multimodal_presentation.settings.CacheClearActivity;

public class InitActivity extends Activity {

    private static final String LOG_TAG = "InitActivity";
    private static final int PHOTO_REQUEST = 10;
	private static final int REQUEST_WRITE_PERMISSION = 20;

    private Uri imageUri;
	private Button presentButton;
	private BarcodeDetector detector;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        
        /*
         * Get buttons via ID (as defined in layout-file)
         */
        presentButton = (Button) findViewById(R.id.start_present_activity);
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
            
        /*scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*new Thread(){
					public void run(){
						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
						startActivityForResult(intent, 0);	
					}
				}.start();

			}
		});*/

		scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                Log.d(LOG_TAG, "Scan click");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.i(LOG_TAG, "Requesting permission");
					ActivityCompat.requestPermissions(InitActivity.this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
				}else {
                    Log.i(LOG_TAG, "Scanning Code");
                    takePicture();
                }
			}
		});

		detector = new BarcodeDetector.Builder(getApplicationContext())
				.setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
				.build();
		if (!detector.isOperational()) {
			Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_LONG).show();
			return;
		}
        
        /*
         * Init broadcastreceiver..
         */
        LocalBroadcastManager.getInstance(this).registerReceiver(
        		mBroadcastReceiver, new IntentFilter(ConnectionService.IMAGE_RECEIVED_SUCCESSFULLY));
    }

    private void scanCode() {
        launchMediaScanIntent();
        try {
            Bitmap bitmap = decodeBitmapUri(this, imageUri);
            if (detector.isOperational() && bitmap != null) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);
                for (int index = 0; index < barcodes.size(); index++) {
                    Barcode code = barcodes.valueAt(index);
                    Toast.makeText(this, code.displayValue + "\n", Toast.LENGTH_SHORT).show();

                    //Required only if you need to extract the type of barcode
                    int type = barcodes.valueAt(index).valueFormat;
                    switch (type) {
                        case Barcode.CONTACT_INFO:
                            Log.i(LOG_TAG, code.contactInfo.title);
                            break;
                        case Barcode.EMAIL:
                            Log.i(LOG_TAG, code.email.address);
                            break;
                        case Barcode.ISBN:
                            Log.i(LOG_TAG, code.rawValue);
                            break;
                        case Barcode.PHONE:
                            Log.i(LOG_TAG, code.phone.number);
                            break;
                        case Barcode.PRODUCT:
                            Log.i(LOG_TAG, code.rawValue);
                            break;
                        case Barcode.SMS:
                            Log.i(LOG_TAG, code.sms.message);
                            break;
                        case Barcode.TEXT:
                            Log.i(LOG_TAG, code.rawValue);
                            break;
                        case Barcode.URL:
                            Log.i(LOG_TAG, "url: " + code.url.url);
                            break;
                        case Barcode.WIFI:
                            Log.i(LOG_TAG, code.wifi.ssid);
                            break;
                        case Barcode.GEO:
                            Log.i(LOG_TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                            break;
                        case Barcode.CALENDAR_EVENT:
                            Log.i(LOG_TAG, code.calendarEvent.description);
                            break;
                        case Barcode.DRIVER_LICENSE:
                            Log.i(LOG_TAG, code.driverLicense.licenseNumber);
                            break;
                        default:
                            Log.i(LOG_TAG, code.rawValue);
                            break;
                    }
                }
                if (barcodes.size() == 0) {
                    Log.i(LOG_TAG, "Scan Failed: Found nothing to scan");
                }
            } else {
                Log.i(LOG_TAG, "Could not set up the detector!");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                    .show();
            Log.e(LOG_TAG, e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(InitActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
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
        }else if (requestCode == PHOTO_REQUEST) {
            Log.d(LOG_TAG, "Request answer");
            if (resultCode == RESULT_OK) {
                Log.i(LOG_TAG, "May take photo..");
                scanCode();
            }else {
                Log.w(LOG_TAG, "Result code not ok");
            }


        }
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
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

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }
    
    /**
     * Broadcastreceiver to receive notification from service as soon as images were retrieved successfully
     */   
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ConnectionService.IMAGE_RECEIVED_SUCCESSFULLY)){
				presentButton.setEnabled(true);
			}
		}
    }; 
}
