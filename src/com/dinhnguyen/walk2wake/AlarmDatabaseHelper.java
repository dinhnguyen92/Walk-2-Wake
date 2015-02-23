package com.dinhnguyen.walk2wake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by DinhNguyen on 12/7/2014.
 * Database helper to provide API for SQLite operations
 * All transactions between the database helper and
 * the rest of the application have to go through
 * AlarmController
 */

public class AlarmDatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = "AlarmDatabaseHelper";

    //Database name and version number
    private static final String DB_NAME = "alarms.sqlite";
    private static final int VERSION = 1;

    //Table's name and column headers
    private static final String TABLE_ALARM = "alarm";
    private static final String COLUMN_ALARM_ID = "_id";
    private static final String COLUMN_ALARM_HOUR = "hour";
    private static final String COLUMN_ALARM_MINUTE = "minute";
    private static final String COLUMN_REPEAT_MONDAY = "repeat_Monday";
    private static final String COLUMN_REPEAT_TUESDAY = "repeat_Tuesday";
    private static final String COLUMN_REPEAT_WEDNESDAY = "repeat_Wednesday";
    private static final String COLUMN_REPEAT_THURSDAY = "repeat_Thursday";
    private static final String COLUMN_REPEAT_FRIDAY ="repeat_Friday";
    private static final String COLUMN_REPEAT_SATURDAY = "repeat_Saturday";
    private static final String COLUMN_REPEAT_SUNDAY = "repeat_Sunday";

    public AlarmDatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create the Alarm table
        db.execSQL("create table alarm ("
                +"_id integer primary key autoincrement, " //Set id as the primary key
                + "hour integer, "
                + "minute integer, "
                + "repeat_Monday boolean, "
                + "repeat_Tuesday boolean, "
                + "repeat_Wednesday boolean, "
                + "repeat_Thursday boolean, "
                + "repeat_Friday boolean, "
                + "repeat_Saturday boolean, "
                + "repeat_Sunday boolean, "
                + "enable boolean)");
        Log.i(TAG, "SQLite Alarm table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Implement schema changes and data alteration when upgrading
    }

    /**
     * API methods for database operations
     */
    public long insert(Alarm alarm)
    {
        //Create ContentValue object with all the information from passed in Alarm
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_HOUR, alarm.getHour());
        cv.put(COLUMN_ALARM_MINUTE, alarm.getMinute());
        cv.put(COLUMN_REPEAT_MONDAY, alarm.isRepeat()[1]);
        cv.put(COLUMN_REPEAT_TUESDAY, alarm.isRepeat()[2]);
        cv.put(COLUMN_REPEAT_WEDNESDAY, alarm.isRepeat()[3]);
        cv.put(COLUMN_REPEAT_THURSDAY, alarm.isRepeat()[4]);
        cv.put(COLUMN_REPEAT_FRIDAY, alarm.isRepeat()[5]);
        cv.put(COLUMN_REPEAT_SATURDAY, alarm.isRepeat()[6]);
        cv.put(COLUMN_REPEAT_SUNDAY, alarm.isRepeat()[0]);

        //Insert the ContentValue into database
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_ALARM, null, cv);
        db.close();

        if (id != -1)
        {
            Log.i(TAG, "New alarm with ID " + id + " inserted into table.");
        }
        else
        {
            Log.e(TAG, "Fail to insert alarm.");
        }

        return id; //return the id number to be assigned to the new alarm

    }

    public void delete(long id)
    {
        SQLiteDatabase db = getWritableDatabase();
        boolean isSuccessful = db.delete(TABLE_ALARM,
               COLUMN_ALARM_ID  + "=" + id, null) > 0;

        if (isSuccessful)
        {
            Log.i(TAG, "Alarm with ID " + id + " deleted from table.");
        }
        else
        {
            Log.e(TAG, "Fail to delete alarm with ID " + id + ".");
        }
    }

    public void update(Alarm alarm)
    {
        //Create ContentValue object with all the information from passed in Alarm
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_HOUR, alarm.getHour());
        cv.put(COLUMN_ALARM_MINUTE, alarm.getMinute());
        cv.put(COLUMN_REPEAT_MONDAY, alarm.isRepeat()[1]);
        cv.put(COLUMN_REPEAT_TUESDAY, alarm.isRepeat()[2]);
        cv.put(COLUMN_REPEAT_WEDNESDAY, alarm.isRepeat()[3]);
        cv.put(COLUMN_REPEAT_THURSDAY, alarm.isRepeat()[4]);
        cv.put(COLUMN_REPEAT_FRIDAY, alarm.isRepeat()[5]);
        cv.put(COLUMN_REPEAT_SATURDAY, alarm.isRepeat()[6]);
        cv.put(COLUMN_REPEAT_SUNDAY, alarm.isRepeat()[0]);

        SQLiteDatabase db = getWritableDatabase();
        boolean isSuccessful = db.update(TABLE_ALARM, cv,
                COLUMN_ALARM_ID + "=" + alarm.getId(), null) > 0;

        if (isSuccessful)
        {
            Log.i(TAG, "Alarm with ID " + alarm.getId() + " updated.");
        }
        else
        {
            Log.e(TAG, "Fail to update alarm.");
        }
    }

    public AlarmCursor queryAllAlarms()
    {
        //Select all existing alarms and order them by ID in ascending order
        Cursor cursor = getReadableDatabase().query(TABLE_ALARM,
                null, null, null, null, null, COLUMN_ALARM_ID + " asc");

        return new AlarmCursor(cursor);
    }
    
    public AlarmCursor queryById(long id)
    {
    	Cursor cursor = getReadableDatabase().query(TABLE_ALARM,
    			null, //all columns
    			COLUMN_ALARM_ID + " = ?", //Look for alarm
    			new String[] {String.valueOf(id)},
    			null, null, null, "1");
    	return new AlarmCursor(cursor);
    }
    
    public Alarm querySingleAlarm(long id)
    {
    	Cursor cursor = getReadableDatabase().query(TABLE_ALARM,
    			null, //all columns
    			COLUMN_ALARM_ID + " = ?", //Look for alarm
    			new String[] {String.valueOf(id)},
    			null, null, null, "1");
    	
    	AlarmCursor alarmCursor = new AlarmCursor(cursor);
    	
    	if (alarmCursor.moveToFirst())
    	{
    		Alarm alarm = alarmCursor.getAlarm();
    		alarmCursor.close();
    		return alarm;
    	}
    	
    	alarmCursor.close();
    	return null;
    }

    public AlarmCursor queryByDay (String dayColumnHeader)
    {
        //Select existing enabled alarms based on the given day of week
        Cursor cursor = getReadableDatabase().query(TABLE_ALARM,
                null, //return all columns
                dayColumnHeader + " = 1",
                null, null, null,
                COLUMN_ALARM_ID + " asc"); //order the rows in ascending order based on ID

        return new AlarmCursor(cursor);
    }

    /**
     * A convenience class to wrap a cursor from the database
     * Using the getAlarm() method, each row in the database
     * will be converted into a corresponding alarm object
     */
    public static class AlarmCursor extends CursorWrapper
    {
        public AlarmCursor(Cursor c)
        {
            super(c);
        }

        /**
         * Returns an Alarm object for the current row
         */
        public Alarm getAlarm()
        {
            //If current row doesn't exist
            if (isBeforeFirst() || isAfterLast())
            {
                return null;
            }

            Alarm alarm = new Alarm();
            long id = getLong(getColumnIndex(COLUMN_ALARM_ID));
            alarm.setId(id);
            int hour = getInt(getColumnIndex(COLUMN_ALARM_HOUR));
            alarm.setHour(hour);
            int minute = getInt(getColumnIndex(COLUMN_ALARM_MINUTE));
            alarm.setMinute(minute);

            boolean[] isRepeat = new boolean[]
                    {
                            getInt(getColumnIndex(COLUMN_REPEAT_SUNDAY)) > 0,
                            getInt(getColumnIndex(COLUMN_REPEAT_MONDAY)) > 0,
                            getInt(getColumnIndex(COLUMN_REPEAT_TUESDAY)) > 0,
                            getInt(getColumnIndex(COLUMN_REPEAT_WEDNESDAY)) > 0,
                            getInt(getColumnIndex(COLUMN_REPEAT_THURSDAY)) > 0,
                            getInt(getColumnIndex(COLUMN_REPEAT_FRIDAY)) > 0,
                            getInt(getColumnIndex(COLUMN_REPEAT_SATURDAY)) > 0
                    };
            alarm.setIsRepeat(isRepeat);

            return alarm;
        }
    }
}
