package com.dayu.autophone.c;

import java.io.DataOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import com.dayu.autophone.StartPHONEtaskActivity;

import android.util.Log;

public class WritePhoneLog extends Thread
{

	private Process process;
	private String filename;
	int callmode = 1;
	String cmd = "";

	public WritePhoneLog(Process process, String filename,int callmode)
	{
		super();
		this.process = process;
		this.filename = filename;
		this.callmode = callmode;
	}


	@Override
	public void run()
	{
		DataOutputStream os = null;    
        
        //需要先创建目录 
		
		 switch (callmode)
		{
		case StartPHONEtaskActivity.CALLMODE_1:
			//////////////////////////////小米、华为
		     cmd="logcat -v time -s InCall > "+filename; 
			break;
		case StartPHONEtaskActivity.CALLMODE_2:
			//////////////////// 三星
	    	 cmd="logcat -v time -s CallCard -s InCallScreen > "+filename;  //三星测试
			break;
		case StartPHONEtaskActivity.CALLMODE_3:
			 /////////////////// 通用
			 cmd="logcat -v time *:D > "+filename;
	       break;
		default:
			break;
		}
	
      
        String clearlog="logcat -c";   
       
        
		try
		{
			//process = Runtime.getRuntime().exec("su");
			MyLog.log("su");
			os = new DataOutputStream(process.getOutputStream());  
			os.writeBytes(clearlog + "\n");  
			MyLog.log("clearlog");
			os.flush(); 
			
            os.writeBytes(cmd + "\n");  
            MyLog.log("cmd");
     //       os.writeBytes("exit\n");    
            os.flush();    
            process.waitFor();  
            
		} catch (IOException e)
		{
			MyLog.log("IOException"+e);
			e.printStackTrace();
		} //切换到root帐号    
		 catch (InterruptedException e)
		{
			 process.destroy();
			 MyLog.log("InterruptedException"+e);
			e.printStackTrace();
		}
	
	}

	
}
