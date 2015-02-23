package com.dinhnguyen.walk2wake;

import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class AlarmtoneService extends Service 
{
	private static final String TAG = "AlarmtoneService";
	private static final String PREFS_FILE = "alarmtone";
	private static final String PREF_LAST_ALARMTONE = "AlarmtoneService.lastAlarmtone";
	
	/*
	 * Components and variables
	 */
	
	private MediaPlayer mPlayer;
	private SharedPreferences mLastTone;
	
	
	@Override
	public IBinder onBind(Intent i)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId)
	{
		
		Log.i(TAG, "Received intent to play alarmtone");
		
		//Retrieve that last alarm tone that was played
		mLastTone = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		int lastIndex = mLastTone.getInt(PREF_LAST_ALARMTONE, 0);
		
		if (mPlayer == null) //Check if mPlayer is already playing to prevent "echo"
		{
			//Generate random alarm tone index from 0 to 7 inclusive
			Random numberGenerator = new Random(System.currentTimeMillis());		
			int index = numberGenerator.nextInt(8);
			
			//If the new index matches the last index, generate a new index
			while (index == lastIndex)
			{
				index = numberGenerator.nextInt(8);
			}
			
			//Update the last tone played
			mLastTone.edit().putInt(PREF_LAST_ALARMTONE, index);
			
			switch(index)
			{
				case 0: 
					mPlayer = MediaPlayer.create(this, R.raw.colonel_bogey);
					break;
				case 1: 
					mPlayer = MediaPlayer.create(this, R.raw.inception_alarm);
					break;
				case 2: 
					mPlayer = MediaPlayer.create(this, R.raw.land_of_hope_and_glory);
					break;
				case 3: 
					mPlayer = MediaPlayer.create(this, R.raw.the_people_sing);
					break;
				case 4: 
					mPlayer = MediaPlayer.create(this, R.raw.morning_mood);
					break;
				case 5: 
					mPlayer = MediaPlayer.create(this, R.raw.o_sole_mio);
					break;
				case 6: 
					mPlayer = MediaPlayer.create(this, R.raw.ode_to_joy);
					break;
				case 7: 
					mPlayer = MediaPlayer.create(this, R.raw.once_upon_a_dream);
					break;
				default:
					mPlayer = MediaPlayer.create(this, R.raw.inception_alarm);
					break;					
			}

			final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

			mPlayer.setLooping(true);
			mPlayer.start();
		}
		
		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		if (mPlayer.isPlaying())
		{
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}			
		
		super.onDestroy();
	}
}
