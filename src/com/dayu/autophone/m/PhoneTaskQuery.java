package com.dayu.autophone.m;

import java.util.LinkedList;

import com.dayu.autophone.AutoPHONEActivity;

import android.R.bool;
import android.util.Log;

public class PhoneTaskQuery
{
	final static String TAG = "AutoPhone";
	static LinkedList<PhoneBase> m_sendlist ;
	static int m_sendlist_count = 0;
	static int tasktotal_count = 0;
	static int addlist_num = 5;
	static boolean exit = false;
	
		
	public PhoneTaskQuery()
	{
		if (AutoPHONEActivity.isdebug) Log.e(TAG,"create sms send query "+ String.valueOf(m_sendlist.size()));
	}
	
	synchronized public static void init_sendlist()
	{
		m_sendlist = new LinkedList<>();
		m_sendlist.clear();
		m_sendlist_count = 0;
		tasktotal_count = 0;
		if (AutoPHONEActivity.isdebug) Log.e(TAG,"init query success");
	}

	synchronized public static int insert_sendlist(PhoneBase smsBase)
	{
		m_sendlist.addLast(smsBase);
		m_sendlist_count = m_sendlist.size();
		if (AutoPHONEActivity.isdebug) Log.e(TAG,"insert "+ String.valueOf(m_sendlist.size()));
		return m_sendlist_count;
	}
	
	synchronized public static int query_sendlist_count()
	{
		m_sendlist_count = m_sendlist.size();
		return m_sendlist_count;
	}
	
	synchronized public static PhoneBase poll_sendlist()
	{
		PhoneBase t_SmsBase = null;
		if ((t_SmsBase = m_sendlist.pollFirst())!=null)
		{
			if (AutoPHONEActivity.isdebug) Log.e(TAG,"poll "+ String.valueOf(m_sendlist.size()));
			return t_SmsBase;		
		}
		
		return null;
	}
	
	
}
