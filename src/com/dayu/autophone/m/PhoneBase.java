package com.dayu.autophone.m;

import com.dayu.autophone.c.MyLog;

public class PhoneBase
{
	String phone_number;
	int    phone_timeout;
	String[] extend_info;
	
	public PhoneBase(String[] phone_info)
	{
		super();
		this.extend_info = phone_info;

         MyLog.log("extend_info:"+extend_info[0]+extend_info[1]);
	}
	public String getPhone_number()
	{
		return extend_info[0];
	}
	

	public int getPhone_timeout()
	{
		return phone_timeout;
	}
	public void setPhone_timeout(int phone_timeout)
	{
		this.phone_timeout = phone_timeout;
	}
	public String[] getExtend_info()
	{
		return extend_info;
	}
	public void setExtend_info(String[] extend_info)
	{
		this.extend_info = extend_info;
	}
	
	
	

}
