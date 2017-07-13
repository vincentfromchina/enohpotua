package com.dayu.autophone.c;

import java.io.DataOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import android.util.Log;

public class WritePhoneLog extends Thread
{

	private Process process;
	private String filename;

	public WritePhoneLog(Process process, String filename)
	{
		super();
		this.process = process;
		this.filename = filename;
	}


	@Override
	public void run()
	{
		DataOutputStream os = null;    
        
        //��Ҫ�ȴ���Ŀ¼ 
		
     //   String cmd="logcat -v time -s InCall > "+filename; 
		
		String cmd="logcat -v time -s CallCard -s InCallScreen > "+filename;  //���ǲ���
       
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
		} //�л���root�ʺ�    
		 catch (InterruptedException e)
		{
			 process.destroy();
			 MyLog.log("InterruptedException"+e);
			e.printStackTrace();
		}
	
	}

	
}
