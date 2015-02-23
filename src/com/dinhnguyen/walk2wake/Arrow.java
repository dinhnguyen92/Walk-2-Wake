package com.dinhnguyen.walk2wake;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * Created by DinhNguyen on 12/5/2014.
 * Rotating arrow to choose time
 */
public class Arrow extends ImageView
{
	private int mResourceId;
	private int mArrowSize;
	private float mPivotX;
	private float mPivotY;
	private float mArrowLength;
	
	public Arrow(Context context, int arrowSize, int resourceId,
			float pivotX, float pivotY, float arrowLength)
	{
		super(context);
		
		mArrowSize = arrowSize;
		mResourceId = resourceId;
		mPivotX = pivotX;
		mPivotY = pivotY;
		mArrowLength = arrowLength;
		
		LayoutParams params = new LayoutParams(mArrowSize,mArrowSize);
		this.setLayoutParams(params);
		this.setAdjustViewBounds(true);
		this.setImageResource(mResourceId);
	}
	
	/**
	 * Method to calculate arrow's new rotation angle (in degrees)
	 */
	public float calculateRotation(float newX, float newY)
	{
		float x = Math.abs(newX - mPivotX);
		float y = Math.abs(newY - mPivotY);
		float rotation = 0;
		
		//1st quadrant
		if (x == 0 && y == 0) //no change in rotation
		{
			rotation = this.getRotation();
		}
		else if (x == 0 && newY < mPivotY) //0 degree
		{
			rotation = 0;
		}
		else if (x == 0 && newY > mPivotY) //180 degrees
		{
			rotation = 180;
		}
		else if (newX > mPivotX && y == 0) //90 degrees
		{
			rotation = 90;
		}
		else if (newX < mPivotX && y == 0) //270 degrees
		{
			rotation = 270;
		}
		else if (newX > mPivotX && newY < mPivotY) //1st quadrant
		{
			rotation = (float)(Math.atan(x/y)*360/(2*Math.PI));
		}
		else if (newX > mPivotX && newY > mPivotY) //4th quadrant
		{
			rotation = (float)(180 - Math.atan(x/y)*360/(2*Math.PI));
		}
		else if (newX < mPivotX && newY > mPivotY) //3rd quadrant
		{
			rotation = (float)(180 + Math.atan(x/y)*360/(2*Math.PI));
		}
		else if (newX < mPivotX && newY < mPivotY) //2nd quadrant
		{
			rotation = (float)(360 - Math.atan(x/y)*360/(2*Math.PI));
		}
		
		return rotation;
	}
	
	/**
	 * Method to rotate the arrow
	 */
	public void rotateArrow(float rotation)
	{	
		//Calculate the this's new position
		double rad = rotation * 2*Math.PI / 360;
		float X = 0;
    	float Y = 0;
    	
    	if (rotation == 0 || rotation == 360)
    	{
    		X = mPivotX;
    		Y = mPivotY - mArrowLength;
    	}
    	else if (0 < rotation && rotation < 90) //1st quadrant
    	{
    		X = (float)(Math.abs(mArrowLength*Math.sin(rad)) + mPivotX);
    		Y = (float)(mPivotY - Math.abs(mArrowLength*Math.cos(rad)));
    	}
    	else if (90 <= rotation && rotation < 180) //4th quadrant
    	{
    		X = (float)(Math.abs(mArrowLength*Math.sin(rad)) + mPivotX);
    		Y = (float)(Math.abs(mArrowLength*Math.cos(rad)) + mPivotY);
    	}
    	else if (180 <= rotation && rotation < 270) //3rd quadrant
    	{
    		X = (float)(mPivotX - Math.abs(mArrowLength*Math.sin(rad)));
    		Y = (float)(Math.abs(mArrowLength*Math.cos(rad)) + mPivotY);
    	}
    	else if (270 <= rotation && rotation <= 360) //2nd quadrant
    	{
    		X = (float)(mPivotX - Math.abs(mArrowLength*Math.sin(rad)));
    		Y = (float)(mPivotY - Math.abs(mArrowLength*Math.cos(rad)));
    	}
    	
    	//Offset the coordinates with the size of the arrow
    	X -= mArrowSize/2;
    	Y -= mArrowSize/2;
    	
    	//Carry out the translation and rotation
    	this.setRotation(rotation);
    	this.setX(X);
    	this.setY(Y);
    	
	}
}
