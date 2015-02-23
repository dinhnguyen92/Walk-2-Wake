package com.dinhnguyen.walk2wake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by Dinh Nguyen on 12/7/2014.
 * Activity to display and configure a single Alarm
 */

public class AlarmDetailActivity extends FragmentActivity
{
	public static final String EXTRA_ALARM_ID = "alarmId";
	public static final String KEY_ALARM_ID = "id";
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Setting the activity view
        setContentView(R.layout.activity_alarm_detail);
        
        //Retrieve the alarm id from the received intent
        long id = -1;
        Intent i = getIntent();
        if (i != null)
        {
        	id = i.getLongExtra(AlarmDetailActivity.EXTRA_ALARM_ID, -1);
        }

        /*
         * Hosting the alarm fragment and send the id to the fragment
         */
        FragmentManager fm = getSupportFragmentManager();
        //Find fragment inside the layout(if already exists in back stack)
        Fragment fragment = fm.findFragmentById(R.id.fragmentHolder);

        //If fragment not already in the back stack of fragment manager
        if (fragment == null)
        {
            //create a new fragment 
            fragment = new AlarmFragment();
            
            //send the id to the new fragment
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_ALARM_ID, id);
            fragment.setArguments(bundle);
            
            //Host the fragment
            fm.beginTransaction()
                .add(R.id.fragmentHolder, fragment)
                .commit();
        }
    }
}
