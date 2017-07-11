package com.dayu.autophone.m;

public class PhoneBase
{
	String phone_number;
	String phone_name;
	int    phone_timeout;
	String[] extend_info;
	
	public PhoneBase(String phone_number, String phone_name)
	{
		super();
		this.phone_number = phone_number;
		this.phone_name = phone_name;
	}
	public String getPhone_number()
	{
		return phone_number;
	}
	public void setPhone_number(String phone_number)
	{
		this.phone_number = phone_number;
	}
	public String getPhone_name()
	{
		return phone_name;
	}
	public void setPhone_name(String phone_name)
	{
		this.phone_name = phone_name;
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
