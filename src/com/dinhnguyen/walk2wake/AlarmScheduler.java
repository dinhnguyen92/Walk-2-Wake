package com.dinhnguyen.walk2wake;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dinhnguyen.walk2wake.AlarmDatabaseHelper.AlarmCursor;

/**
 * Created by DinhNguyen on 12/8/2014.
 * IntentService to schedule and set up alarms in background threads
 */
public class AlarmScheduler extends IntentService
{
    private static final String TAG = "AlarmScheduler";
    public static final String EXTRA_DELETE_ID = "deleteID";

    public AlarmScheduler()
    {
        super(TAG);
    }

    @SuppressLint("NewApi")
	@Override
    protected void onHandleIntent(Intent intent)
    {
    	Log.i(TAG, "Received intent to schedule alarm");
    	      
        setTodayAlarms();
                
        //Set up the rescheduling of the alarm tomorrow at 30 seconds after midnight
        Intent i = new Intent(getApplicationContext(), AlarmScheduler.class);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 30);
        c.add(Calendar.DAY_OF_YEAR, 1);
        long triggerTime = c.getTimeInMillis();
        PendingIntent pi = PendingIntent.getService(getApplicationContext(),
        		-1, i, PendingIntent.FLAG_CANCEL_CURRENT);
        
        AlarmManager alarmManager = (AlarmManager)
                getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= 19)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pi);
        }
        else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pi);
        }      
    }

    @SuppressLint("NewApi")
    /**
     * Method to set up alarms for today (from current time until 12am)
     *Only alarms after the time of setup today will be set up.
     *Alarms that are within today but are already past the
     *setup time will not be set up
     **/
    private void setTodayAlarms()
    {
    	 AlarmManager alarmManager = (AlarmManager)
    			 getApplicationContext().getSystemService(Context.ALARM_SERVICE);
   	
        //Determine the current day of the week
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String dayColumnHeader = null;

        switch(dayOfWeek)
        {
            case Calendar.SUNDAY:
                dayColumnHeader = "repeat_Sunday";
                break;
            case Calendar.MONDAY:
                dayColumnHeader = "repeat_Monday";
                break;
            case Calendar.TUESDAY:
                dayColumnHeader = "repeat_Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayColumnHeader = "repeat_Wednesday";
                break;
            case Calendar.THURSDAY:
                dayColumnHeader = "repeat_Thursday";
                break;
            case Calendar.FRIDAY:
                dayColumnHeader = "repeat_Friday";
                break;
            case Calendar.SATURDAY:
                dayColumnHeader = "repeat_Saturday";
                break;
        }

        AlarmCursor cursor = AlarmController.get(getApplicationContext())
                .queryByDay(dayColumnHeader);
       
        //Iterate through each row and set up corresponding alarm
        if (cursor.moveToFirst())
        do
        {
            //Obtain the trigger time from the current row
            Alarm alarm = cursor.getAlarm();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            long currentTime = c.getTimeInMillis();
            c.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            c.set(Calendar.MINUTE, alarm.getMinute());
            c.set(Calendar.SECOND, 0);
            long triggerTime = c.getTimeInMillis();

            //If the alarm time has not passed yet
            if (triggerTime > currentTime) 
            {
            	Log.i(TAG, "Today's remaining alarm time: " + c.toString());

                //Generate the unique ID for the pending intent
                int id = (int)cursor.getLong(cursor.getColumnIndex("_id"));
                //Create the pending intent
                Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
                        id, i, PendingIntent.FLAG_CANCEL_CURRENT);

                //Use alarm manager to fire up the pending intent at exactly the trigger time
                if(android.os.Build.VERSION.SDK_INT >= 19)
                {
                	alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pi);
                }
                else
                {
                	alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pi);
                }
            }           
        }
        while(cursor.moveToNext());
        
        //close the cursor to prevent leaking
        cursor.close();       
    }
    
    public static void cancelAlarm(Context context, long id)
    {
    	AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	
    	AlarmCursor cursor = AlarmController.get(context)
                .queryById(id);
    	
    	if (cursor.moveToFirst())
    	{
    		//Create pending intent with matching id to be canceled
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context,
                    (int)id, i, PendingIntent.FLAG_CANCEL_CURRENT);
            
            //Cancel the pending intent to cancel the alarm
            alarmManager.cancel(pi);
    	}
    }

}

