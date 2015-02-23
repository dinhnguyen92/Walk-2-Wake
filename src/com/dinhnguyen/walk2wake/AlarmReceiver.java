package com.dinhnguyen.walk2wake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DinhNguyen on 1/4/2014.
 * Broadcast receiver to receiver alarm
 */
public class AlarmReceiver extends BroadcastReceiver
{
	private static String TAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.i(TAG, "Alarm received. Starting Ring Service.");
		
		//Start the ring activity
		Intent i = new Intent(context.getApplicationContext(), WalkActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);	
	}
}
