package com.dinhnguyen.walk2wake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dinhnguyen.walk2wake.AlarmDatabaseHelper.AlarmCursor;

/**
 * Created by DinhNguyen on 12/13/2014.
 * ListFragment to display the list of stored alarms
 */
public class AlarmListFragment extends ListFragment implements LoaderCallbacks<Cursor>
{
	private static final int REQUEST_NEW_ALARM = 0;
	private static final int REQUEST_UPDATE_ALARM = 1;
	
	//Components
	private Button mNewAlarmButton;
	private static AlarmController mController;
	
	/*
	 * Life Cycle methods
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mController = AlarmController.get(getActivity());
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		
		View v = inflater.inflate(R.layout.list_fragment, container, false);
		
		//Wire and configure the new alarm button
		mNewAlarmButton = (Button)v.findViewById(R.id.add_alarm_button);
		mNewAlarmButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Start AlarmDetailActivity to create a new alarm with id -1
				Intent i = new Intent(getActivity(), AlarmDetailActivity.class);
				i.putExtra(AlarmDetailActivity.EXTRA_ALARM_ID, (int)-1);
				startActivityForResult(i, REQUEST_NEW_ALARM);	
			}
		});
		mNewAlarmButton.setTextColor(Color.GREEN);
						
		
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		//Initialize the loader to load the alarms
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		//Restart the loader to reflect any changes in database
		getLoaderManager().restartLoader(0, null, this);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		//Pass the id of the corresponding alarm to AlarmDetailActivity
		Intent i = new Intent(getActivity(), AlarmDetailActivity.class);
		i.putExtra(AlarmDetailActivity.EXTRA_ALARM_ID, id);
		startActivityForResult(i, REQUEST_UPDATE_ALARM);	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			//Restart the loader to reflect any changes in database
			getLoaderManager().restartLoader(0, null, this);
		}
	}
	
	
	/*
	 * Implementing LoaderCallbacks<Cursor>
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		return new AlarmListCursorLoader(getActivity());
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		AlarmCursorAdapter adapter = 
				new AlarmCursorAdapter(getActivity(), (AlarmCursor)cursor);
		setListAdapter(adapter);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		//Remove the current cursor from the adapter
		setListAdapter(null);
	}
	
	
	/**
	 * Convenience class to customize CursorAdapter for AlarmCursor
	 */
	private static class AlarmCursorAdapter extends CursorAdapter
	{
		private AlarmCursor mCursor;
		
		public AlarmCursorAdapter(Context context, AlarmCursor cursor)
		{
			super(context, cursor, 0);
			mCursor = cursor;
		}
		
		/**Define how the ListView item for each alarm is created*/
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			LayoutInflater inflater = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			return inflater.inflate(R.layout.list_item_alarm, parent, false);
		}
		
		/**Configure list item according to current alarm*/
		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			Alarm alarm = mCursor.getAlarm();
			
			//Wiring up the widgets
			TextView timeTextView = (TextView)view.findViewById(R.id.alarm_time_textView);
			TextView AM_PM = (TextView)view.findViewById(R.id.alarm_am_pm_textView);
			TextView monday = (TextView)view.findViewById(R.id.monday_textView);
			TextView tuesday = (TextView)view.findViewById(R.id.tuesday_textView);
			TextView wednesday = (TextView)view.findViewById(R.id.wednesday_textView);
			TextView thursday = (TextView)view.findViewById(R.id.thursday_textView);
			TextView friday = (TextView)view.findViewById(R.id.friday_textView);
			TextView saturday = (TextView)view.findViewById(R.id.saturday_textView);
			TextView sunday = (TextView)view.findViewById(R.id.sunday_textView);
			
			//Configuring the time TextView 
			int minute = alarm.getMinute();
			int hour = 0;
			
			if (alarm.getHour() == 0 || alarm.getHour() == 12)
			{
				hour = 12;
			}
			else hour = alarm.getHour() % 12;
			
			String time = String.format("%02d:%02d", hour, minute);
			
			timeTextView.setText(time);
			
			if (alarm.getHour() > 11)
			{
				AM_PM.setText("PM");
			}
			else AM_PM.setText("AM");
			
			boolean[] isRepeat = alarm.isRepeat();
			
			if (!isRepeat[0]) sunday.setTextColor(Color.RED);
			else sunday.setTextColor(Color.GREEN);
			if (!isRepeat[1]) monday.setTextColor(Color.RED);
			else monday.setTextColor(Color.GREEN);
			if (!isRepeat[2]) tuesday.setTextColor(Color.RED);
			else tuesday.setTextColor(Color.GREEN);
			if (!isRepeat[3]) wednesday.setTextColor(Color.RED);
			else wednesday.setTextColor(Color.GREEN);
			if (!isRepeat[4]) thursday.setTextColor(Color.RED);
			else thursday.setTextColor(Color.GREEN);
			if (!isRepeat[5]) friday.setTextColor(Color.RED);
			else friday.setTextColor(Color.GREEN);
			if (!isRepeat[6]) saturday.setTextColor(Color.RED);
			else saturday.setTextColor(Color.GREEN);
		}	
	}
	
	/**
	 * Convenient class to load AlarmCursor in background thread
	 */
	private static class AlarmListCursorLoader extends SQLiteCursorLoader
	{
		public AlarmListCursorLoader(Context context)
		{
			super(context);
		}
		
		@Override
		protected Cursor loadCursor()
		{
			//Query the list of stored alarms
			return AlarmController.get(getContext()).queryAllAlarms();
		}
	}
}
