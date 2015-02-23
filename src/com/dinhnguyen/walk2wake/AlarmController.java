package com.dinhnguyen.walk2wake;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dinhnguyen.walk2wake.AlarmDatabaseHelper.AlarmCursor;

/**
 * Created by Dinh Nguyen on 12/7/2014.
 * Singleton to manage all transactions between
 * alarm database and the rest of the application
 */
public class AlarmController 
{
    private static AlarmController sAlarmController;
    private Context mAppContext;
    private AlarmDatabaseHelper dbHelper;

    //Private constructor to implement the singleton structure
    private AlarmController(Context appContext)
    {
        mAppContext = appContext;
        dbHelper = new AlarmDatabaseHelper(mAppContext);
    }

    //Method to pull the singleton out
    public static AlarmController get(Context c)
    {
        //Only create a new AlarmController instance if there's no such instance
        //Ensures that only one instance of AlarmController exists at any time
        if (sAlarmController == null)
        {
            sAlarmController = new AlarmController(c.getApplicationContext());
        }

        return sAlarmController;
    }

    /**
     * Methods for managing and querying stored alarms
     */
    public void insertAlarm(Alarm alarm)
    {
        alarm.setId(dbHelper.insert(alarm));

        //Set up the Alarm scheduler to schedule the new alarm
        Intent i = new Intent(mAppContext, AlarmScheduler.class);
        mAppContext.startService(i);
    }

    public void deleteAlarm(long id)
    {
    	//Use AlarmScheduler to cancel the alarm
    	AlarmScheduler.cancelAlarm(mAppContext, id);
    	
    	//Once the alarm has been canceled, erase it from database
        dbHelper.delete(id);
        
        //Check if there's any alarm left in the database
        AlarmCursor cursor = dbHelper.queryAllAlarms();
        
        //If there's no alarm left
        if (cursor.getCount() == 0)
        {
        	 AlarmManager alarmManager = (AlarmManager)
                     mAppContext.getSystemService(Context.ALARM_SERVICE);
        	 
        	//Reconstruct the pending intent used to periodically set up alarms every day
            Intent i = new Intent(mAppContext, AlarmScheduler.class);
            PendingIntent pi = PendingIntent.getService(mAppContext,
                    -1, i, PendingIntent.FLAG_CANCEL_CURRENT);
            
            //Cancel the pending intent stop rescheduling of the alarms
            alarmManager.cancel(pi);
        }
    }

    public void updateAlarm(Alarm alarm)
    {
        dbHelper.update(alarm);

        //Set up the Alarm scheduler to schedule the new alarm
        Intent i = new Intent(mAppContext, AlarmScheduler.class);
        mAppContext.startService(i);
    }

    public AlarmCursor queryAllAlarms()
    {
        return dbHelper.queryAllAlarms();
    }
    
    public AlarmCursor queryById(long id)
    {
    	
    	return dbHelper.queryById(id);
    }
    
    public Alarm querySingleAlarm(long id)
    {
    	return dbHelper.querySingleAlarm(id);
    }

    public AlarmCursor queryByDay(String dayColumnHeader)
    {
        return dbHelper.queryByDay(dayColumnHeader);
    }

}
