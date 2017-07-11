package com.dayu.autophone.c;

import java.io.DataOutputStream;
import java.io.IOException;

public class DeletePhoneLog extends Thread
{
	private String filename;
	private Process process;
	public DeletePhoneLog(String filename, Process process)
	{
		super();
		this.filename = filename;
		this.process = process;
	}
	@Override
	public void run()
	{
		DataOutputStream os = null;    
		String removefile="rm "+filename; 
		
		os = new DataOutputStream(process.getOutputStream());  
		try
		{
			os.writeBytes(removefile + "\n");
			MyLog.log("removefile"+this.getId());
			os.flush(); 
			os.writeBytes("exit\n");
			MyLog.log("removefile");
			os.flush(); 
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		
	}
	
	
}
