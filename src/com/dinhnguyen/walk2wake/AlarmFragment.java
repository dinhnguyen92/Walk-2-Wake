package com.dinhnguyen.walk2wake;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by Dinh Nguyen on 12/7/2014.
 * Fragment to display and configure a single Alarm
 */
public class AlarmFragment extends Fragment
{
	private static final String TAG = "AlarmFragment";
	public static final int HOUR_TAG = 0;
	public static final int MINUTE_TAG = 1;
	public static final int AM_PM_TAG = 2;
	
    /**Widgets and Components*/
	
	private boolean[] mIsRepeat;
    private Alarm mAlarm;
    private Button mSaveButton, mCancelButton, mDeleteButton;
    private ToggleButton[] mDayToggle;
    private TextView mTimeTextView;
    private TextView mAM_PMTextView, mCenterTextView;
    private TextView mRemainingTimeTextView;
    private AlarmController mController;
    private Animation fadeIn, fadeOut, fadeInMedium, fadeOutMedium;
    private View mDetailsView;
    private RelativeLayout mClockFace;
    private float mPivotX;
    private float mPivotY;
    private float mHourArrowLength;
    private float mMinuteArrowLength;
    private int mArrowSize;
    private Arrow mHourArrow, mMinuteArrow;
    private ImageView mClockRing;
    

    /**Life Cycle Methods*/
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Retrieve the alarm id
        long id = getArguments().getLong(AlarmDetailActivity.KEY_ALARM_ID);
        
        Log.i(TAG, "The retrieved ID is: " + id);
        
        mController = AlarmController.get(getActivity());
        
        //If the alarm has just been created
        if (id == -1)
        {
        	mAlarm = new Alarm();
        }
        else
        {
        	//Retrieve the alarm from database using the id
            mAlarm = mController.querySingleAlarm(id);           
        }
        
        //Retain the fragment for configuration change (eg:rotation)
        setRetainInstance(true);
    }

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_alarm, parent, false);
        
        //Wiring the widgets
        mTimeTextView = (TextView)v.findViewById(R.id.alarm_time_detail);
        mAM_PMTextView = (TextView)v.findViewById(R.id.alarm_am_pm_detail);
        mRemainingTimeTextView = (TextView)v.findViewById(R.id.remaining_time_textView);
        mSaveButton = (Button)v.findViewById(R.id.save_button);
        mCancelButton = (Button)v.findViewById(R.id.cancel_button);
        mDeleteButton = (Button)v.findViewById(R.id.delete_button);
        mClockFace = (RelativeLayout)v.findViewById(R.id.clock_face);
        mCenterTextView = (TextView)v.findViewById(R.id.center_textView);
        mDetailsView = (View)v.findViewById(R.id.details_view);
        mDayToggle = new ToggleButton[7];
        mDayToggle[0] = (ToggleButton)v.findViewById(R.id.sunday_button);
        mDayToggle[1] = (ToggleButton)v.findViewById(R.id.monday_button);
        mDayToggle[2] = (ToggleButton)v.findViewById(R.id.tuesday_button);
        mDayToggle[3] = (ToggleButton)v.findViewById(R.id.wednesday_button);
        mDayToggle[4] = (ToggleButton)v.findViewById(R.id.thursday_button);
        mDayToggle[5] = (ToggleButton)v.findViewById(R.id.friday_button);
        mDayToggle[6] = (ToggleButton)v.findViewById(R.id.saturday_button);
        mClockRing = (ImageView)v.findViewById(R.id.clock_ring);
        
        //Set the center TextView default tag
        mCenterTextView.setTag(Integer.valueOf(AM_PM_TAG));
        
    	//Calculating the position of the arrows pivot
    	WindowManager manager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
    	Display display = manager.getDefaultDisplay();
    	  
    	if (android.os.Build.VERSION.SDK_INT >= 13)
    	{
    		Point size = new Point();
    		display.getSize(size);
    		if (size.y < size.x)
    		{
    			mPivotX = size.y/2;
        		mPivotY = size.y/2;
        		mHourArrowLength = (float)(size.y/5.5);
        		mMinuteArrowLength = (float)(size.y/3.3);
        		mArrowSize = (int)size.y/9;
    		}
    		else
    		{
    			mPivotX = size.x/2;
        		mPivotY = size.x/2;
        		mHourArrowLength = (float)(size.x/5.5);
        		mMinuteArrowLength = (float)(size.x/3.3);
        		mArrowSize = (int)size.x/9;
    		}
    		Log.i(TAG, "Screen: " + size.y + "x" + size.x);
    	}   	
    	else
    	{
    		if (display.getHeight() < display.getWidth())
    		{
    			mPivotX = display.getHeight()/2;
        		mPivotY = display.getHeight()/2;
        		mHourArrowLength = (float)(display.getHeight()/5.5);
        		mMinuteArrowLength = (float)(display.getHeight()/3.3);
        		mArrowSize = (int)(display.getHeight()/9);
    		}
    		else
    		{
    			mPivotX = display.getWidth()/2;
        		mPivotY = display.getWidth()/2;
        		mHourArrowLength = (float)(display.getWidth()/5.5);
        		mMinuteArrowLength = (float)(display.getWidth()/3.3);
        		mArrowSize = (int)(display.getWidth()/9);
    		}		
    		Log.i(TAG, "Screen: " + display.getWidth() + "x" + display.getHeight());
    	}
    	
    	//Set up the hour arrow
        mHourArrow = new Arrow(getActivity(), mArrowSize, R.drawable.hour_arrow, 
        		mPivotX, mPivotY, mHourArrowLength);
        mClockFace.addView(mHourArrow);
        
        
        //Set up the minute arrow
        mMinuteArrow = new Arrow(getActivity(), mArrowSize, R.drawable.minute_arrow, 
        		mPivotX, mPivotY, mMinuteArrowLength);
        mClockFace.addView(mMinuteArrow);
    	
    	//Calculate the current rotation of the hour arrow
    	float hourRotation = (float)(360 * (mAlarm.getHour()%12)/12);
    	
    	//Calculate the current rotation of the minute arrow
    	float minuteRotation = (float)(360 * mAlarm.getMinute()/60);

    	Log.i(TAG, "Minute rotation = " + minuteRotation + " Minute = " + mAlarm.getMinute());
    	//Rotate the arrows
    	mHourArrow.rotateArrow(hourRotation);
    	mMinuteArrow.rotateArrow(minuteRotation);
    	
    	//Enable the views for UI interaction
    	mClockFace.setFocusable(true);
    	mDetailsView.setFocusable(true);
    	
    	//Set the arrows on touch listener
    	mHourArrow.setOnTouchListener(new OnHourArrowTouchListener());
    	mMinuteArrow.setOnTouchListener(new OnMinuteArrowTouchListener());       
        
        //Setting up the animation
        fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        fadeInMedium = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_medium);
        fadeOutMedium = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_medium);
        
		updateTimeDisplay();

		configureDayToggleButtons();

		configureButtons();

		updateRemainingTime();
		
		fadeIn();

		
        return v;
    }
    
	
    /*
     * Custom Methods
     */
	
    /**Method to update the numeric time display*/
    @SuppressLint("DefaultLocale")
	private void updateTimeDisplay()
    {
    	//Configuring the time TextView 
		int minute = mAlarm.getMinute();
		int hour = 0;
		    			
		if (mAlarm.getHour() == 0 || mAlarm.getHour() == 12)
		{
		   	hour = 12;
		}
		else hour = mAlarm.getHour() % 12;
		    			
		String time = String.format("%02d:%02d", hour, minute);
		    			
		mTimeTextView.setText(time);
		
		//Configure AM-PM indicator
		int tag = (Integer) mCenterTextView.getTag();
		String centerHour = String.format("%02d", hour);
		String centerMinute = String.format("%02d", minute);
		
		//Configure the center ring time display
		if (tag == HOUR_TAG)
		{		   
			mCenterTextView.setText(centerHour);
		}
		else if (tag == MINUTE_TAG)
		{
			mCenterTextView.setText(centerMinute);
		}
		
		
		if (mAlarm.getHour() > 11)
		{
		   mAM_PMTextView.setText("PM");
		   
		   if (tag == AM_PM_TAG)
		   {
			   mCenterTextView.setText("PM");
		   }
		}
		else 
		{
			mAM_PMTextView.setText("AM");
			   
			if (tag == AM_PM_TAG)
			{
				mCenterTextView.setText("AM");
			}
		}
		
		
		//Configure clock ring
		if (tag == HOUR_TAG)
		{
			switch(hour)
			{
				case 12:
					mClockRing.setImageResource(R.drawable.xii_i);
					break;
				case 1:
					mClockRing.setImageResource(R.drawable.i_ii);
					break;
				case 2:
					mClockRing.setImageResource(R.drawable.ii_iii);
					break;
				case 3:
					mClockRing.setImageResource(R.drawable.iii_iv);
					break;
				case 4:
					mClockRing.setImageResource(R.drawable.iv_v);
					break;
				case 5:
					mClockRing.setImageResource(R.drawable.v_vi);
					break;
				case 6:
					mClockRing.setImageResource(R.drawable.vi_vii);
					break;
				case 7:
					mClockRing.setImageResource(R.drawable.vii_viii);
					break;
				case 8:
					mClockRing.setImageResource(R.drawable.viii_ix);
					break;
				case 9:
					mClockRing.setImageResource(R.drawable.ix_x);
					break;
				case 10:
					mClockRing.setImageResource(R.drawable.x_xi);
					break;
				case 11:
					mClockRing.setImageResource(R.drawable.xi_xii);
					break;
			}
		}
		else if (tag == MINUTE_TAG)
		{
			if (0 <= minute && minute < 5)
			{
				mClockRing.setImageResource(R.drawable.xii_i);
			}
			else if (5 <= minute && minute < 10)
			{
				mClockRing.setImageResource(R.drawable.i_ii);
			}
			else if (10 <= minute && minute < 15)
			{
				mClockRing.setImageResource(R.drawable.ii_iii);
			}
			else if (15 <= minute && minute < 20)
			{
				mClockRing.setImageResource(R.drawable.iii_iv);
			}
			else if (20 <= minute && minute < 25)
			{
				mClockRing.setImageResource(R.drawable.iv_v);
			}
			else if (25 <= minute && minute < 30)
			{
				mClockRing.setImageResource(R.drawable.v_vi);
			}
			else if (30 <= minute && minute < 35)
			{
				mClockRing.setImageResource(R.drawable.vi_vii);
			}
			else if (35 <= minute && minute < 40)
			{
				mClockRing.setImageResource(R.drawable.vii_viii);
			}
			else if (40 <= minute && minute < 45)
			{
				mClockRing.setImageResource(R.drawable.viii_ix);
			}
			else if (45 <= minute && minute < 50)
			{
				mClockRing.setImageResource(R.drawable.ix_x);
			}
			else if (50 <= minute && minute < 55)
			{
				mClockRing.setImageResource(R.drawable.x_xi);
			}
			else if (55 <= minute && minute < 60)
			{
				mClockRing.setImageResource(R.drawable.xi_xii);
			}
		}
		else if (tag == AM_PM_TAG)
		{
			mClockRing.setImageResource(R.drawable.clock_face);
		}
		
		
			
    }
    
    /**Method to configure the day toggle buttons*/
    private void configureDayToggleButtons()
    {
    	//Configuring the day ToggleButton
    	mIsRepeat = mAlarm.isRepeat();
    	for (int i = 0; i < 7; i++)
    	{
    		mDayToggle[i].setChecked(mIsRepeat[i]);

    		//If the day is not chosen, set text color to red
    		if (!mDayToggle[i].isChecked())
    		{
    			mDayToggle[i].setTextColor(Color.RED);
    		}
    		else
    		{
    			mDayToggle[i].setTextColor(Color.GREEN);
    		}
    	}

    	mDayToggle[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[0])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[0] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});

    	mDayToggle[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[1])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[1] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});

    	mDayToggle[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[2])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[2] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});

    	mDayToggle[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[3])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[3] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});

    	mDayToggle[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[4])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[4] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});

    	mDayToggle[5].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[5])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[5] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});

    	mDayToggle[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    	{
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			if(isChecked)
    			{
    				buttonView.setTextColor(Color.GREEN);
    			}
    			else
    			{
    				buttonView.setTextColor(Color.RED);
    			}		
    			
    			//If the current day is the only one activated
    			if (oneDayEnabled() && mIsRepeat[6])
    			{	
    				//Warn the user that the alarm is inactive
    				showWarning();
    			}
    			
    			mIsRepeat[6] = isChecked;
    			mAlarm.setIsRepeat(mIsRepeat);
    			updateRemainingTime();
    		}
    	});
    }
    
    /**Method to configure buttons*/
    private void configureButtons()
    {
    	mCancelButton.setOnClickListener(new View.OnClickListener()
    	{
    		//Do nothing and finish the current activity
    		public void onClick(View v)
    		{
    			
    			//Fade out
    			fadeOut();
    			
    			
    			//delay exiting the fragment for 0.5s to allow the animation to finish
    			final Handler handler = new Handler();    			
    			Runnable exit = new Runnable()
    			{
    				@Override
    				public void run()
    				{
    					//Send the user back to the alarm list
            			Intent i = new Intent(getActivity(), AlarmListFragment.class);
            			getActivity().setResult(Activity.RESULT_OK, i);
            			getActivity().finish();
    				}			
    			};
    			long triggerTime = SystemClock.uptimeMillis() + 500;
    			handler.postAtTime(exit, triggerTime);    			
    		}
    	});
    	
    	mSaveButton.setOnClickListener(new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			//if the alarm has just been created then insert the new alarm
        		if (mAlarm.getId() == -1)
        		{
        			AlarmController.get(getActivity()).insertAlarm(mAlarm);
        		}
        		else //update the alarm
        		{
        			AlarmController.get(getActivity()).updateAlarm(mAlarm);
        		}
        		
        		//Fade out
    			fadeOut();
        		
    			//delay exiting the fragment for 0.5s to allow the animation to finish
    			final Handler handler = new Handler();    			
    			Runnable exit = new Runnable()
    			{
    				@Override
    				public void run()
    				{
    					//Send the user back to the alarm list
            			Intent i = new Intent(getActivity(), AlarmListFragment.class);
            			getActivity().setResult(Activity.RESULT_OK, i);
            			getActivity().finish();
    				}			
    			};
    			long triggerTime = SystemClock.uptimeMillis() + 500;
    			handler.postAtTime(exit, triggerTime);    			
    		}
    	});
    	
    	mDeleteButton.setOnClickListener(new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			//Delete the current alarm from database
    			long id = mAlarm.getId();
    			
    			if (id != -1)
    			{
    				mController.deleteAlarm(mAlarm.getId());
    			}
    			
    			//Fade out
    			fadeOut();
    		   			
    			//delay exiting the fragment for 0.5s to allow the animation to finish
    			final Handler handler = new Handler();    			
    			Runnable exit = new Runnable()
    			{
    				@Override
    				public void run()
    				{
    					//Send the user back to the alarm list
            			Intent i = new Intent(getActivity(), AlarmListFragment.class);
            			getActivity().setResult(Activity.RESULT_OK, i);
            			getActivity().finish();
    				}			
    			};
    			long triggerTime = SystemClock.uptimeMillis() + 500;
    			handler.postAtTime(exit, triggerTime);    			
    			
    		}
    	});
    	
    	mCenterTextView.setOnClickListener(new View.OnClickListener()
    	{
    		//Toggle and update AM or PM
    		public void onClick(View v)
    		{
    			if (mAM_PMTextView.getText().equals("AM"))
    			{
    				//((TextView)v).setText("PM");
    				int hour = mAlarm.getHour() + 12;
    				mAlarm.setHour(hour);
    				updateTimeDisplay();
    				updateRemainingTime();
    			}
    			
    			else
    			{
    				//((ImageView)v).setImageResource(R.drawable.am_button);
    				int hour = mAlarm.getHour() - 12;
    				mAlarm.setHour(hour);
    				updateTimeDisplay();
    				updateRemainingTime();
    			}
    		}
    	});
    }
    
    /**Method to calculate and display remaining time*/
    private void updateRemainingTime()
    {
    	//If none of the day is chosen set the remaining time to N/A
    	int num = 0;
    	for (int n = 0; n < 7; n++)
    	{
    		if (!mIsRepeat[n])
    		{
    			num++;
    		}
    	}
    	if (num == 7)
    	{
    		mRemainingTimeTextView.setText("Time left: N/A");
    		return;
    	}
		
    	
    	//Determine the current day of the week
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean isToday = false;
        
        //Determine if the alarm is today
        switch(dayOfWeek)
        {
            case Calendar.SUNDAY:
                isToday = mIsRepeat[0];
                break;
            case Calendar.MONDAY:
            	isToday = mIsRepeat[1];
                break;
            case Calendar.TUESDAY:
            	isToday = mIsRepeat[2];
                break;
            case Calendar.WEDNESDAY:
            	isToday = mIsRepeat[3];
                break;
            case Calendar.THURSDAY:
            	isToday = mIsRepeat[4];
                break;
            case Calendar.FRIDAY:
            	isToday = mIsRepeat[5];
                break;
            case Calendar.SATURDAY:
            	isToday = mIsRepeat[6];
                break;
        }
        
        //Determine of the alarm has already passed
    	Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                mAlarm.getHour(),
                mAlarm.getMinute(),
                0);
        long alarmTime = c.getTimeInMillis();
        
        if (alarmTime <= currentTime) isToday = false;
        
        
        //Calculate the remaining time
        long remainingTime = 0;
        if (isToday)
        {
        	remainingTime = alarmTime - currentTime;
        }
        else
        {	
        	//add 24 hours to alarmTime for every day of the week
        	//in which the alarm is not activated
        	do
        	{
        		alarmTime += 86400000;
        		
        		if (dayOfWeek < 7) dayOfWeek++;
        		else dayOfWeek = 1;
        		
        	}
        	while (!mIsRepeat[dayOfWeek - 1]);  
        	
        	remainingTime = alarmTime - currentTime;
        }
        
        //Create the remaining time message
        int day = (int)(remainingTime - (remainingTime % 86400000))/86400000;
        int minute = (int)((remainingTime - day*86400000) % 3600000)/60000;
        int hour = (int)(remainingTime - day*86400000 - minute*60000)/3600000;
        
        String d = "";
        String hr = "";
        String min = "";
        if (day > 1) d = new String(" " + day + " days");
        else if (day == 1) d = new String(" " + day + " day");
        
        if (hour > 1) hr = new String(" " + hour +" hours");
        else if (hour == 1) hr = new String(" " + hour + " hour");
        
        if (minute > 1) min = new String (" " + minute + " minutes");
        else if (minute == 1) min = new String(" " + minute + " minute");
        
        
        String time = "Time left:" + d + hr + min;
        
        mRemainingTimeTextView.setText(time);     
    }
    
    /**Method to check if only one day is enabled*/
    private boolean oneDayEnabled()
    {
    	int num = 0;
    	for (int i = 0; i < 7; i++)
    	{
    		if (mIsRepeat[i]) num++;
    	}
    	
    	if (num == 1)
    	{
    		
    		return true;
    	}
    		
    	else return false;
    }
    
    /**Method to display toast warning user to keep at least 1 day enabled*/
    private void showWarning()
    {
    	CharSequence warning = "Warning: this alarm is inactive!";
    	Toast toast = Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
    	toast.show();
    }
    
    /**Method to fade in the views*/
    private void fadeIn()
    {
    	fadeInMedium.setFillBefore(true);
    	fadeInMedium.setFillEnabled(true);
    	fadeIn.setFillBefore(true);
    	fadeIn.setFillEnabled(true);
    	
    	//Set the start time for each animation
    	long startTime = AnimationUtils.currentAnimationTimeMillis() + 400;
    	fadeInMedium.setStartTime(startTime);
    	fadeIn.setStartTime(startTime + 200);
    	
    	//Set the animation
    	mDetailsView.setAnimation(fadeInMedium);
    	mClockFace.setAnimation(fadeIn);
    }
    
    /**Method to fade out the views*/
    private void fadeOut()
    {
    	//Disable the views to prevent UI interaction during fade
    	mClockFace.setFocusable(false);
    	mDetailsView.setFocusable(false);
    	
    	fadeOut.setFillAfter(true);
    	fadeOutMedium.setFillAfter(true);
    	
    	long startTime = AnimationUtils.currentAnimationTimeMillis() + 300;
    	
    	//Start the fading out of the clock
    	mClockFace.startAnimation(fadeOut);
    	
    	//Delay and start the fading out of the details
    	//Set the start time for each animation  	
    	fadeOutMedium.setStartTime(startTime);
    	mDetailsView.setAnimation(fadeOutMedium);   	
    }
    
   
    /**
	 * Custom OnTouchListener for hour arrow
	 */
	private class OnHourArrowTouchListener implements OnTouchListener
	{
	
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			float rotation = 0;
						
			//Perform different operations depending on the type of touch
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					
					v.performClick();
					
					//Disable and hide the minute arrow
					mMinuteArrow.setClickable(false);
					mMinuteArrow.setVisibility(View.INVISIBLE);
					
					//Disable center TextView to prevent toggling AM PM
					mCenterTextView.setClickable(false);
					
					//Set tag on center text view
					mCenterTextView.setTag(Integer.valueOf(HOUR_TAG));
					
					updateTimeDisplay();
					
					break;
					
				case MotionEvent.ACTION_MOVE:
					
					rotation = ((Arrow)v).calculateRotation(event.getRawX(), event.getRawY());
					
					((Arrow)v).rotateArrow(rotation);
					
					//Update and display the current hour
					int hour = (int)(12 * rotation/360);
						
					if (mAM_PMTextView.getText().equals("PM"))
					{
						hour += 12;
					}
					mAlarm.setHour(hour);
					updateTimeDisplay();
					updateRemainingTime();
					
					
					break;
					
				case MotionEvent.ACTION_UP:
					
					//Redisplay and enable the minute arrow
					mMinuteArrow.setClickable(true);
					mMinuteArrow.setVisibility(View.VISIBLE);
					
					//Enable center TextView to allow toggling AM PM
					mCenterTextView.setClickable(true);
					
					//Reset center TextView tag to default
					mCenterTextView.setTag(Integer.valueOf(AM_PM_TAG));
					
					//Display AM or PM in the center ring
					updateTimeDisplay();
					updateRemainingTime();
					break;
			}
			
			
			return true;
		}
	}
	
	/**
	 * Custom OnTouchListener for hour arrow
	 */
	private class OnMinuteArrowTouchListener implements OnTouchListener
	{
	
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{			
			float rotation = 0;
			
			//Perform different operations depending on the type of touch			
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					
					v.performClick();
					
					//Disable and hide the minute arrow
					mHourArrow.setClickable(false);
					mHourArrow.setVisibility(View.INVISIBLE);
					
					//Disable center TextView to prevent toggling AM PM
					mCenterTextView.setClickable(false);
					
					//Set tag on center text view
					mCenterTextView.setTag(Integer.valueOf(MINUTE_TAG));					
					
					updateTimeDisplay();
					
					break;
					
				case MotionEvent.ACTION_MOVE:
					
					rotation = ((Arrow)v).calculateRotation(event.getRawX(), event.getRawY());
					
					((Arrow)v).rotateArrow(rotation);
					
					//Update and display the current minute
					int minute = (int)(60 * rotation/360);
				
					mAlarm.setMinute(minute);
					updateTimeDisplay();
					updateRemainingTime();
					
					
					break;
					
				case MotionEvent.ACTION_UP:
					
					//Redisplay and enable the minute arrow
					mHourArrow.setClickable(true);
					mHourArrow.setVisibility(View.VISIBLE);
					
					//Enable center TextView to allow toggling AM PM
					mCenterTextView.setClickable(true);
					
					//Reset center TextView tag to default
					mCenterTextView.setTag(Integer.valueOf(AM_PM_TAG));
					
					//Display AM or PM in the center ring
					updateTimeDisplay();
					updateRemainingTime();
					break;
			}
			
			
			return true;
		}
	}
	
	
}

