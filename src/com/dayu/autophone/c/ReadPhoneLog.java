package com.dayu.autophone.c;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.android.internal.telephony.ITelephony;
import com.dayu.autophone.AutoPHONEActivity;
import com.dayu.autophone.StartPHONEtaskActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class ReadPhoneLog extends Thread
{
	final static String TAG = ContactPicker.TAG;
	
	private String filename;
	private Process process;
	public boolean go = true;
	private boolean killcmd = false;
	private long sessiontime = 0; 
	boolean call_ok = false;  //是否已经接通电话
	int timeout = 30;
	int callmode = 1;
	
	 public ReadPhoneLog(Process process,String filename,int timeout,int callmode)
	{
		super();
		this.filename = filename;
		this.process = process;
		this.timeout = timeout;
		this.callmode = callmode;
	}


	@Override  
     public void run() {  
		int highwatermark = 0;
		long last_UTC = 0;
		
        
         try {  
       	 
           while(go)
          {
           	
           	Thread.sleep(800);
           	
           	 File feFile = new File(filename);
           	int total_filecount = 0;
           	
           	if(feFile.canRead())
   			{
           		
   				FileReader frd = null;
   				BufferedReader buffd = null;
   				frd = new FileReader(feFile);
   				
   				MyLog.log("file size:" + feFile.length());
					  buffd = new BufferedReader(frd);
					 
					String red_buf="",tmp_str = "";
					
					while(((tmp_str=buffd.readLine())!=null) && go)
					 {
						if (total_filecount<=highwatermark)
						{
							if ( StartPHONEtaskActivity.checkcutdown &&  call_ok && ((System.currentTimeMillis() - sessiontime) > (timeout * 1000)))
							{
							   // MyLog.log("已拨打时间"+(System.currentTimeMillis() - sessiontime));
								rejectCall();
							}
							
							total_filecount++;
							continue;
						} 
						
						if (tmp_str.length()>18)
						{
							red_buf = tmp_str.substring(0, 18);
						}else {
							total_filecount++;
							continue;
						}
						
						
						
  					   SimpleDateFormat year = new SimpleDateFormat("yyyy-");
						String t_time = "2017-06-26 00:00:00.083";
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss.S");
						String now_time = year.format(System.currentTimeMillis())+red_buf;
						
						switch (callmode)
						{
						case StartPHONEtaskActivity.CALLMODE_1:
						////////////////////模式一 小米、华为
							
						      try
						      {
						    	int startpos = tmp_str.indexOf("mState");
	  							if (startpos>=0)
							   {
		  							int somemark = tmp_str.indexOf("CallHandlerService");
		  							if (somemark>=0)
								   {
	  							   
		  							long gettime = sdf.parse(now_time.trim()).getTime();
									if (gettime > last_UTC)
	  							    {
										int DIALING = tmp_str.indexOf("mState=DIALING");
										
										int ACTIVE = tmp_str.indexOf("mState=ACTIVE");
										
	  	  								int disconn = tmp_str.indexOf("mState=DISCONNECTED");
	  	  								
	  	  								if (DIALING>0)
										{
	  	  								   call_ok = true;
	  	  								   sessiontime = gettime;
										}
	  	  								
		  	  							if (ACTIVE>0)
										{
			  							    sessiontime = Long.MAX_VALUE;
										}
		  	  							
	  	  								
	  	  								if (disconn>0)
										{
	  	  									if (!killcmd)
											{
	  	  									  killcmd = true;
	  	  									  process.destroy();
		  									  MyLog.log("kill writelog process");
											}
	  	  									  
										}
	  	  								
	  	  								
	  	  							  MyLog.log("this line:"+red_buf+tmp_str.substring(startpos,startpos+50));
	  	  							  MyLog.log("old time is:"+ last_UTC);
	  	  							   last_UTC = gettime;
	  	  							  MyLog.log("update time is:"+ last_UTC);
	  							    }
								 }
							 }	   
						      } catch (ParseException e)
						       {
						    	  MyLog.log("some error"+now_time);
							    e.printStackTrace();
						       }
						       
							break;
						case StartPHONEtaskActivity.CALLMODE_2:
						 /////////////////////////模式二  三星
							
							int startpos = tmp_str.indexOf("CallStatus");
							if (startpos>=0)
							   {
								 MyLog.log("read buf:"+tmp_str);
								long gettime = sdf.parse(now_time.trim()).getTime();
								if (gettime > last_UTC)
							    {
									int DIALING = tmp_str.indexOf("DIALING");
									
									int ACTIVE = tmp_str.indexOf("ACTIVE");
									
		  								int disconn = tmp_str.indexOf("DISCONNECTED");
		  								
		  								if (DIALING>0)
									{
		  								   call_ok = true;
		  								 MyLog.log("call ok");
		  								   sessiontime = gettime;
									}
		  								
	  	  							if (ACTIVE>0)
									{
		  							    sessiontime = Long.MAX_VALUE;
		  							  MyLog.log("call active");
									}
	  	  							
		  								
		  								if (disconn>0)
									{
		  							    MyLog.log("call disconn");
		  								if (!killcmd)
										{
		  									  killcmd = true;
		  								 //	  process.destroy();
		  									  this.go = false;
	  									  MyLog.log("kill writelog process");
										}
		  									  
									}
		  								
		  								
		  						//	  MyLog.log("this line:"+red_buf+tmp_str.substring(startpos,startpos+50));
		  							  MyLog.log("old time is:"+ last_UTC);
		  							   last_UTC = gettime;
		  							  MyLog.log("update time is:"+ last_UTC);
								    }
							   }
							break;	
						case StartPHONEtaskActivity.CALLMODE_3:
							///////////////// 通用
							boolean cango = true;
							long gettime = 0;
							try
							{
								 gettime = sdf.parse(now_time.trim()).getTime();
							} catch (ParseException e)
							{
								MyLog.log("time parse error.." );
							   cango = false;
							}
								
							if (!cango)
							{
								MyLog.log("can not go..");
								total_filecount++;
								continue;
							}
							
								if (gettime > last_UTC)
							    {
									int DIALING = tmp_str.indexOf("DIALING");
									
									int ACTIVE = tmp_str.indexOf("ACTIVE");
									
		  						//	int disconn = tmp_str.indexOf("DISCONNECTD");
		  								
		  					    	if (DIALING>0)
									{
		  								   call_ok = true;
		  								 MyLog.log("call ok");
		  								   sessiontime = gettime;
									}
		  								
	  	  							if (ACTIVE>0)
									{
		  							    sessiontime = Long.MAX_VALUE;
		  							  MyLog.log("call active");
									}
	  	  							
		  							/*	
		  							if (disconn>0)
									{
		  							    MyLog.log("call disconn");
		  								if (!killcmd)
										{
		  									  killcmd = true;
		  								 //	  process.destroy();
		  									  this.go = false;
	  									  MyLog.log("kill writelog process");
										}
									}
		  							*/
	  	  							
	  	  							if (StartPHONEtaskActivity.isidle && call_ok)
									{
	  	  							 MyLog.log("call disconn");
		  								if (!killcmd)
										{
		  									  killcmd = true;
		  								 //	  process.destroy();
		  									  this.go = false;
										  MyLog.log("kill writelog process");
										}
									}
		  								
		  						//	  MyLog.log("this line:"+red_buf+tmp_str.substring(startpos,startpos+50));
		  						//	  MyLog.log("old time is:"+ last_UTC);
		  							   last_UTC = gettime;
		  						//	  MyLog.log("update time is:"+ last_UTC);
								    }else
								    {
								    	//MyLog.log("time old....");
								    }
							break;
						default:
							break;
						}
						
					      
						total_filecount++;
					 }
					
					highwatermark =  total_filecount;
					 MyLog.log("total_filecount:"+total_filecount+",hwm:"+highwatermark);
						 
   			}else
   			{
   			      MyLog.log(feFile.getPath()+" file not exits");
   			}
           	
          }
         }
         catch(Exception e)
         {
        	 MyLog.log("error:"+e);
             e.printStackTrace();
             
         }
        
     } 
	
	 public void rejectCall() {
			
		 try {
	            Method method = Class.forName("android.os.ServiceManager")
	                    .getMethod("getService", String.class);
	            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
	            ITelephony telephony = ITelephony.Stub.asInterface(binder);
	            telephony.endCall();
	        } catch (NoSuchMethodException e) {
	        	if (AutoPHONEActivity.isdebug) Log.e(TAG, "NoSuchMethodException", e);
	        } catch (ClassNotFoundException e) {
	        	if (AutoPHONEActivity.isdebug) Log.e(TAG, "ClassNotFoundException", e);
	        } catch (Exception e) {
	        }
	    }


	@Override
	public void interrupt()
	{
		MyLog.log("readlog 线程退出");
		super.interrupt();
	}
	 
	 
}
