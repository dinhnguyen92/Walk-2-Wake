package com.dinhnguyen.walk2wake;

import java.util.Calendar;

/**
 * Created by Dinh Nguyen on 12/7/2014.
 * Class to model information stored in a single Alarm
 */
public class Alarm 
{
	//Information stored
    private long mId; //serves as unique ID for each Alarm
    private int mHour; //in 24 hours form
    private int mMinute;
    private boolean[] mIsRepeat;

    //Constructor
    public Alarm()
    {
        //Set the default time to be the current time
        Calendar calendar = Calendar.getInstance();
        mId = -1;
        mHour = calendar.get(Calendar.HOUR_OF_DAY); //in 24 hours term
        mMinute = calendar.get(Calendar.MINUTE);

        //Repeat status array, first element is Sunday
        mIsRepeat = new boolean[]
                {true, true, true, true, true, true, true};
    }

    /**Getters and Setters*/
    public long getId() 
    {
        return mId;
    }

    public int getHour() 
    {
        return mHour;
    }

    public int getMinute() 
    {
        return mMinute;
    }

    public boolean[] isRepeat() 
    {
        return mIsRepeat;
    }

    public void setId(long id) 
    {
        this.mId = id;
    }

    public void setHour(int hour) 
    {
        this.mHour = hour;
    }

    public void setMinute(int minute) 
    {
        this.mMinute = minute;
    }

    public void setIsRepeat(boolean[] isRepeat) 
    {
        this.mIsRepeat = isRepeat;
    }
}
