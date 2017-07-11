package com.dayu.autophone.c;

import android.util.Log;

public class MyLog
{
	final static String TAG = ContactPicker.TAG;
	private static boolean isdebug = true;
	
	static public void log(String logmsg)
	{
		if (isdebug)
		{
			Log.e(TAG, logmsg);
		}
	}
}
