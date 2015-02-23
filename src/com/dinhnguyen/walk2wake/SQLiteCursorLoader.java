package com.dinhnguyen.walk2wake;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by DinhNguyen on 12/17/2014.
 * AsyncTaskLoader to load cursor from SQLite database in background thread
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor>
{
	private Cursor mCursor;
	
	public SQLiteCursorLoader(Context context)
	{
		super(context);
	}
	
	protected abstract Cursor loadCursor();
	
	@Override
	public Cursor loadInBackground()
	{
		Cursor cursor = loadCursor();
		if (cursor != null)
		{
			//Ensure that the content window is filled
			cursor.getCount();
		}
		return cursor;
	}
	
	@Override
	public void deliverResult(Cursor data)
	{
		Cursor oldCursor = mCursor;
		mCursor = data;
		
		if (isStarted())
		{
			super.deliverResult(data);
		}
		
		if (oldCursor != null && oldCursor != data && !oldCursor.isClosed())
		{
			oldCursor.close();
		}
	}
	
	@Override
	protected void onStartLoading()
	{
		if (mCursor != null)
		{
			deliverResult(mCursor);
		}
		if (takeContentChanged() || mCursor == null)
		{
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading()
	{
		//Attempt to cancel current load task
		cancelLoad();
	}
	
	@Override
	public void onCanceled(Cursor cursor)
	{
		if (cursor != null && !cursor.isClosed())
		{
			cursor.close();
		}
	}
	
	@Override
	protected void onReset()
	{
		super.onReset();
		
		//Ensure loader is stopped
		onStopLoading();
		
		if (mCursor != null && !mCursor.isClosed())
		{
			mCursor.close();
		}
		
		mCursor = null;
	}
}
