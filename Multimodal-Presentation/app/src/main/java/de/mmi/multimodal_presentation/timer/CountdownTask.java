package de.mmi.multimodal_presentation.timer;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

public class CountdownTask extends CountDownTimer{

	
	/*private final static long[] HALF_TIME_PATTERN = new long[]{
		0L,
		100L,
		500L,
		100L
	};*/
	
	private final static long[] FULL_TIME_PATTERN = new long[]{
		0L,
		300L,
		100L,
		300L
	};
	
	TextView textView;
	
	Vibrator vibrator;
	
	boolean vibrated = false;
	long halfTime;
	
	public CountdownTask(long time, TextView textView, Context context) {
		super(time, 1000L);
		
		this.textView = textView;
		halfTime = time / 2L;
		
		textView.setTextColor(Color.WHITE);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public void onFinish() {
					
		textView.setText("00:00");
		textView.setTextColor(Color.RED);
		
		if(vibrator.hasVibrator()){
			vibrator.vibrate(FULL_TIME_PATTERN, -1);
		}

	}

	@Override
	public void onTick(long millisUntilFinished) {
		
		textView.setText(convertToString(millisUntilFinished));
		
		if(!vibrated && halfTime >= millisUntilFinished){
			vibrated = true;
			if(vibrator.hasVibrator()){
				//vibrator.vibrate(HALF_TIME_PATTERN, -1);
				vibrator.vibrate(100L);
			}else{
				Log.e("Countdown", "error, no vibrator");
			}
		}
		
	}
	
	private String convertToString(long time){
		
		time /= 1000L;
		
		String timeString = "";
		
		long min = time / 60L;
		long sec = time % 60L;
		
		if(min < 10L){
			timeString += "0";
		}
		
		timeString += min + ":";
		
		if(sec < 10L){
			timeString += "0";
		}
		
		timeString += sec;
		
		return timeString;
	}

}
