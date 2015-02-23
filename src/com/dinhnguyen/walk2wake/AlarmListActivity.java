package com.dinhnguyen.walk2wake;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by DinhNguyen on 12/13/2014.
 * Activity to display the list of stored alarms
 */
public class AlarmListActivity extends FragmentActivity
{	
	/*
	 * Life Cycle Methods
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_alarm_list);
		
		/*
		 * Hosting the list fragment to display the list of alarms
		 */
		FragmentManager fm = getSupportFragmentManager();
        //Find list fragment inside the fragment holder (if exists in fm backstack)
		Fragment fragment = fm.findFragmentById(R.id.listFragment_holder);

        //If fragment not already in the backstack of fm
        if (fragment == null)
        {
            //create a new list fragment and add it to fragment holder using fm
            fragment = new AlarmListFragment();
            fm.beginTransaction()
                .add(R.id.listFragment_holder, fragment)
                .commit();
        }
	}
}
