package com.dinhnguyen.walk2wake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DinhNguyen on 12/12/2014.
 * Broadcast receiver to reschedule the alarms after booting up
 */

public class BootUpReceiver extends BroadcastReceiver
{
    private static String TAG = "BootUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "Device booted up. Rescheduling alarms.");

        //Set up the Alarm scheduler to schedule the new alarm
        Intent i = new Intent(context, AlarmScheduler.class);
        context.startService(i);
    }
}

