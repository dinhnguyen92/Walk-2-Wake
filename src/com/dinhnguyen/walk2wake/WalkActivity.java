package com.dinhnguyen.walk2wake;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WalkActivity extends Activity implements SensorEventListener
{
	/*
	 * Components and variables
	 */
	
	private static final String TAG = "WalkActivity";
	
	private WakeLock mWakeLock;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	private double[] mAcceleration;
	private double mTotalAcceleration;	
	private final float alpha = (float)0.8; //Constant used to filter out gravitational acceleration
	private double[] gravity;
	
	private ImageView mWalkStatusImageView;
	private ProgressBar mWalkProgressBar;
	private TextView mWalkPercentage;
	
	private int mStopTime;
	
	
	/*
	 * Life cycle methods
	 */
	
	@SuppressLint("Wakelock")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Setting the activity view
        setContentView(R.layout.activity_walk);
        
        //Setting up the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		// Acquire WakeLock
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		if (mWakeLock == null) 
		{
			mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | 
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK | 
					PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
		}

		if (!mWakeLock.isHeld()) 
		{
			mWakeLock.acquire();
		}
		
		//Setting up the variables
		gravity = new double[] {0, 0 ,0};
		mAcceleration = new double[] {0, 0, 0};
		mTotalAcceleration = 0;
		mStopTime = 0;
		
		//Setting up the sensor
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);		

		//Configure views
		mWalkStatusImageView = (ImageView)findViewById(R.id.walk_status_imageView);
		mWalkPercentage = (TextView)findViewById(R.id.walk_percent_textView);
		mWalkProgressBar = (ProgressBar)findViewById(R.id.walk_progressBar);
		mWalkProgressBar.setProgress(0);
		
		
		//Start AlarmtoneService
		Intent i = new Intent(this, AlarmtoneService.class);
		startService(i);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if (mSensorManager != null)
		{
			//Re-register the sensor listener
			mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		//Reinitialize the variables
		mTotalAcceleration = 0;
		mStopTime = 0;
				
		for (int i = 0; i <3; i++)
		{
			gravity[i] = 0;
			mAcceleration[i] = 0;
		}
		
		//Reset the progressBar
		mWalkProgressBar.setProgress(0);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		//Unregister the sensor listener
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	protected void onStop()
	{
		//Unregister the sensor listener
		mSensorManager.unregisterListener(this);
		
		super.onStop();
	}
	
		
		
	
	@Override
	public void onBackPressed()
	{
		//Do nothing to disable back/return key
	}
	
	
	/*
	 * Sensor methods
	 */
	
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		//Do nothing for now
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public final void onSensorChanged(SensorEvent event)
	{
		//Isolate gravitational acceleration with low-pass filter
		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
		
		//Remove gravity from acceleration with high-pass filter
		mAcceleration[0] = event.values[0] - gravity[0];
		mAcceleration[1] = event.values[1] - gravity[1];
		mAcceleration[2] = event.values[2] - gravity[2];

		//Calculate aggregate acceleration:
		mTotalAcceleration = Math.sqrt(mAcceleration[0] * mAcceleration[0]
				+ mAcceleration[1] * mAcceleration[1]
				+ mAcceleration[2] * mAcceleration[2]);
		
		//If the user walks around
		if (1 <= mTotalAcceleration && mTotalAcceleration <= 3)
		{
			//Increment the walkingScore and the progress bar
			int newScore = mWalkProgressBar.getProgress() + (int)mTotalAcceleration;
			if (newScore < 200)
			{
				mWalkProgressBar.setProgress(newScore);
			}
			else
			{
				mWalkProgressBar.setProgress(200);
			}
			
			
			//Reset stop time
			mStopTime = 0;
			
			if (mWalkProgressBar.getProgress()<= 40)
			{
				mWalkStatusImageView.setImageResource(R.drawable.sleeping);
			}
			else if (mWalkProgressBar.getProgress() <= 80)
			{
				mWalkStatusImageView.setImageResource(R.drawable.fall_off_bed);
			}
			else if (mWalkProgressBar.getProgress() <= 120)
			{
				mWalkStatusImageView.setImageResource(R.drawable.crawl_off_bed);
			}
			else if (mWalkProgressBar.getProgress() <= 160)
			{
				mWalkStatusImageView.setImageResource(R.drawable.walk_off_bed);
			}
			else if (mWalkProgressBar.getProgress() <= 200)
			{
				mWalkStatusImageView.setImageResource(R.drawable.awake);
			}
		}
		//If the user stops walking
		else if (mWalkProgressBar.getProgress() > 1)
		{
			//Decrement walking score and progress bar
			mWalkProgressBar.incrementProgressBy(-1);
			
			//Increment stop time
			mStopTime++;
			
			if (mStopTime >= 10)
			{
				if (mWalkProgressBar.getProgress() <= 40)
				{
					mWalkStatusImageView.setImageResource(R.drawable.sleep_give_up);
				}
				else if (mWalkProgressBar.getProgress() <= 80)
				{
					mWalkStatusImageView.setImageResource(R.drawable.jump_to_bed);
				}
				else if (mWalkProgressBar.getProgress() <= 120)
				{
					mWalkStatusImageView.setImageResource(R.drawable.crawl_to_bed);
				}
				else if (mWalkProgressBar.getProgress() <= 160)
				{
					mWalkStatusImageView.setImageResource(R.drawable.walk_to_bed);
				}
				else if (mWalkProgressBar.getProgress() <= 200)
				{
					mWalkStatusImageView.setImageResource(R.drawable.awake);
				}
			}		
		}
		
		//Update the percentage textView
		int percent = (mWalkProgressBar.getProgress() * 100 / 200);
		String percentage = String.format("%02d%%", percent);
		mWalkPercentage.setText(percentage);
		
		//If the progress bar is full
		if (mWalkProgressBar.getProgress() == 200)
		{			
			 //Releasing the window
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			
			//Release the wake lock
			if (mWakeLock != null && mWakeLock.isHeld())
			{
				try
				{
					mWakeLock.release();
				}
				catch (Throwable T)
				{
					//Do nothing
					//Necessary to wrap the wake lock release
					//in a wrap in case the lock has already been released
				}
			}			
			
			//Stop AlarmtoneService
			Intent i = new Intent(this, AlarmtoneService.class);
			stopService(i);
			
			this.finish();
		}		
	}
}
