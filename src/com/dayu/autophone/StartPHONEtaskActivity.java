package com.dayu.autophone;

import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.dayu.autophone.c.DBHelper;
import com.dayu.autophone.c.DeletePhoneLog;
import com.dayu.autophone.c.FloatWindowService;
import com.dayu.autophone.c.Getnowtime;
import com.dayu.autophone.c.MyLog;
import com.dayu.autophone.c.MyWindowManager;
import com.dayu.autophone.c.ReadPhoneLog;
import com.dayu.autophone.c.WritePhoneLog;
import com.dayu.autophone.m.PhoneBase;
import com.dayu.autophone.m.PhoneTask;
import com.dayu.autophone.m.PhoneTaskQuery;
import com.android.internal.telephony.ITelephony;
import com.dayu.autophone.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StartPHONEtaskActivity extends Activity
{
	final static String TAG = AutoPHONEActivity.TAG;

    //String send_target[];
	HashMap<Integer, String[]> send_target;
	public static final int CALLMODE_1 = 1,CALLMODE_2 = 2,CALLMODE_3 = 3;
	static int callmode = 1;
    int    send_num = 0;
    static int    send_totalnum = 0;
    static int    send_success = 0;
    static int    send_fail = 0;
    static int    send_interval = 1;
    static int    cutdown = 10;
    public static int progessperct = 0;
	private DBHelper sqldb;
	static private String mfilePath="";
	static int taskid;
	private Cursor m_Cursor;
	static PhoneTaskQuery m_TaskQuery = null;
    PhoneTask m_PhoneTask = null;
	static send_call m_sendcall = null;
    load_task m_loadtask = null;
	static ProgressBar mProgressBar;
	static EditText edt_sendinteval;
	static EditText edt_cutdown;
	static Button btn_sendpause;
	static Button btn_start;
	static Button btn_sendstop;
	static ToggleButton tgbtn_checkcutdown;
	static boolean send_isstart = false;
	public static boolean load_finish = false;
	static boolean send_finish = false;
	public static boolean showinfo = true;
	public static boolean checkcutdown = false;
	public static boolean isidle=  true;
	static long filesize = 0;
	final static Object loadtask_lock = "导入数据共享锁";
	final static Object sendtask_lock = "发送进程共享锁";
    static TextView tv_sendstatus;
    static TextView tv_successorfail;
    static TextView tv_callmode_1,tv_callmode_2,tv_callmode_3;
    static WritePhoneLog m_WritePhoneLog = null;
    public static ReadPhoneLog m_ReadPhoneLog = null;
    public static int zitisize,ziticolor;

	static boolean teac = false;
	static byte[] signfromdb ;
	private static final String ROOT = Environment
			.getExternalStorageDirectory().toString()+"/";
	TelephonyManager tm ;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_smstask);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	  if (savedInstanceState!=null )
		{	
		if (savedInstanceState.containsKey("taskid"))
		 {
			taskid = savedInstanceState.getInt("taskid");
		 }
		}else {
			 Bundle bundle = this.getIntent().getExtras();
			 taskid = bundle.getInt("taskid");
		}
		
		
		new Thread(r_sign).start(); //访问服务器获得注册信息
		
		tv_callmode_1 = (TextView)findViewById(R.id.tv_callmode_1);
		tv_callmode_2 = (TextView)findViewById(R.id.tv_callmode_2);
		tv_callmode_3 = (TextView)findViewById(R.id.tv_callmode_3);
		
		tv_callmode_1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				callmode = CALLMODE_1;
				tv_callmode_1.setBackgroundColor(Color.rgb(51, 255, 51));
				tv_callmode_2.setBackgroundColor(Color.rgb(192, 192, 192));
				tv_callmode_3.setBackgroundColor(Color.rgb(192, 192, 192));
			}
		});
		
		tv_callmode_2.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				callmode = CALLMODE_2;
				tv_callmode_2.setBackgroundColor(Color.rgb(51, 255, 51));
				tv_callmode_1.setBackgroundColor(Color.rgb(192, 192, 192));
				tv_callmode_3.setBackgroundColor(Color.rgb(192, 192, 192));
			}
		});
		
		tv_callmode_3.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				callmode = CALLMODE_3;
				tv_callmode_3.setBackgroundColor(Color.rgb(51, 255, 51));
				tv_callmode_2.setBackgroundColor(Color.rgb(192, 192, 192));
				tv_callmode_1.setBackgroundColor(Color.rgb(192, 192, 192));
			}
		});
		
		mProgressBar = (ProgressBar)findViewById(R.id.pbr);
		tv_sendstatus = (TextView)findViewById(R.id.tv_sendstatus);
		edt_sendinteval = (EditText)findViewById(R.id.edt_sendinteval);
		edt_cutdown = (EditText)findViewById(R.id.edt_cutdown);
		tv_successorfail = (TextView)findViewById(R.id.tv_successorfail);
		
		tgbtn_checkcutdown = (ToggleButton)findViewById(R.id.tgbtn_checkcutdown);
		tgbtn_checkcutdown.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				checkcutdown = tgbtn_checkcutdown.isChecked();
				
			}
		});
		
		edt_sendinteval.setSelection(edt_sendinteval.getText().length()); //将光标设置在文本最后面
		edt_sendinteval.setEnabled(true);
		
		edt_sendinteval.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void afterTextChanged(Editable s)
			{
				edt_sendinteval.setSelection(edt_sendinteval.getText().length());
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		edt_cutdown.setSelection(edt_cutdown.getText().length()); //将光标设置在文本最后面
		edt_cutdown.setEnabled(true);
		
		edt_cutdown.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void afterTextChanged(Editable s)
			{
				edt_cutdown.setSelection(edt_cutdown.getText().length());
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		sqldb = new DBHelper(StartPHONEtaskActivity.this, "dbname", null);
		m_PhoneTask = new PhoneTask();
		m_PhoneTask.setTaskid(taskid);
		
		if (AutoPHONEActivity.isdebug) Log.e(TAG,"m_Task.getTaskfail()"+ m_PhoneTask.getTaskfail());
	   
		init_task();
		
	    btn_start = (Button)findViewById(R.id.btn_start);
		btn_start.setVisibility(send_isstart ? View.INVISIBLE : View.VISIBLE);
		btn_start.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				check_sendinteval();
				
				edt_sendinteval.setSelection(edt_sendinteval.getText().length());
				
				
				tv_successorfail.setText("外呼成功：   外呼失败：");
				
				send_isstart = true;
				load_finish = false;
				PhoneTaskQuery.init_sendlist();
			//     send_target = new String[10];
				send_target = new HashMap<>();
				
			     send_num = 0;
			     send_success = 0;
			     send_fail = 0;
			     send_totalnum = 0;
			     progessperct = 0;

			     if (AutoPHONEActivity.isdebug) Log.e(TAG, "send_totalnum" + send_totalnum);
			     m_PhoneTask.setTasktotal(0);
			     m_PhoneTask.setTaskfail(0);
			     m_PhoneTask.setTasksuccess(0);
                 if (AutoPHONEActivity.isdebug)  Log.e(TAG,"m_Task.getTaskfail()2:"+ m_PhoneTask.getTaskfail());
			     
			      if (m_sendcall==null)
					{
			    	   m_sendcall = new send_call();
			    	   m_sendcall.start();
					}else {
						if (AutoPHONEActivity.isdebug) Log.e(TAG, "m_sendcall is not null");
					}
			      
			    if (m_TaskQuery==null)
				{
			    	  m_TaskQuery = new PhoneTaskQuery();
				}else {
					if (AutoPHONEActivity.isdebug) Log.e(TAG, "m_TaskQuery is not null");
				}
			    if (send_isstart )
				   {
					   synchronized (sendtask_lock)
						  {
						   if (AutoPHONEActivity.isdebug) Log.e(TAG, "通知你解锁了："+Thread.currentThread().getId());
							sendtask_lock.notifyAll();
						  }
				   }
				
				//btn_sendstop.setVisibility(View.VISIBLE);
				btn_sendpause.setVisibility(View.VISIBLE);
				btn_start.setVisibility(View.INVISIBLE);
				edt_sendinteval.setEnabled(false);
				edt_cutdown.setEnabled(false);
				
                m_loadtask = new load_task();
				m_loadtask.start();
										
			}
		});
				
		
	    btn_sendstop = (Button)findViewById(R.id.btn_sendstop);
	//	btn_sendstop.setVisibility(send_isstart ? View.VISIBLE : View.INVISIBLE);
		btn_sendstop.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
			  if (m_loadtask!=null)
			   {
				  if (m_loadtask.isAlive())
					{
					  AlertDialog isExit = new AlertDialog.Builder(StartPHONEtaskActivity.this).create();  
			            // 设置对话框标题  
			            isExit.setTitle("自动外呼助手");  
			            // 设置对话框消息  
			            isExit.setMessage("外呼任务正在执行，要退出吗？");  
			            // 添加选择按钮并注册监听  
			            isExit.setButton("朕确定", listener);  
			            isExit.setButton2("取消了", listener);  
			            // 显示对话框  
			            isExit.show();
				   		
			            if (AutoPHONEActivity.isdebug) Log.e(TAG, "m_loadtask.isAlive");
					}else
					{
						 // 创建退出对话框  
			            AlertDialog isExit = new AlertDialog.Builder(StartPHONEtaskActivity.this).create();  
			            // 设置对话框标题  
			            isExit.setTitle("自动外呼助手");  
			            // 设置对话框消息  
			            isExit.setMessage("确定要退出吗？");  
			            // 添加选择按钮并注册监听  
			            isExit.setButton("朕确定", listener);  
			            isExit.setButton2("取消了", listener);  
			            // 显示对话框  
			            isExit.show();  
			            if (AutoPHONEActivity.isdebug) Log.e(TAG, "m_loadtask not Alive");
					}
			   }else {
				   // 创建退出对话框  
		            AlertDialog isExit = new AlertDialog.Builder(StartPHONEtaskActivity.this).create();  
		            // 设置对话框标题  
		            isExit.setTitle("自动外呼助手");  
		            // 设置对话框消息  
		            isExit.setMessage("确定要退出吗？");  
		            // 添加选择按钮并注册监听  
		            isExit.setButton("朕确定", listener);  
		            isExit.setButton2("取消了", listener);  
		            // 显示对话框  
		            isExit.show();  
			   }	
			  
			}
		});
		
	    btn_sendpause = (Button)findViewById(R.id.btn_sendpause);
		btn_sendpause.setVisibility(send_isstart ? View.VISIBLE : View.INVISIBLE);
		btn_sendpause.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				send_isstart = !send_isstart;
				   if (send_isstart )
				   {
					   synchronized (sendtask_lock)
						  {
						   if (AutoPHONEActivity.isdebug) Log.e(TAG, "通知你解锁了："+Thread.currentThread().getId());
							sendtask_lock.notifyAll();
						  }
					   btn_sendpause.setText("暂停");
				   }else
				   { 
					   rejectCall();
					   btn_sendpause.setText("继续");
					   
					   FloatWindowService.changefolattext(" ");
				   }	
				
			}
		});
		
		//获得相应的系统服务  
	     tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
	        /** 
	         * 返回电话状态 
	         *  
	         * CALL_STATE_IDLE 无任何状态时  
	         * CALL_STATE_OFFHOOK 接起电话时 
	         * CALL_STATE_RINGING 电话进来时  
	         */  
	     tm.listen(new MyPhoneCallListener(), PhoneStateListener.LISTEN_CALL_STATE);
	     
		   
	    Intent  it = new Intent(StartPHONEtaskActivity.this,FloatWindowService.class);
	    startService(it);
	    
	    try
		{
			final Process process = Runtime.getRuntime().exec("su");
			 new Thread(new Runnable()
				{
					public void run()
					{
						DataOutputStream os = null; 
						os = new DataOutputStream(process.getOutputStream());  
						try
						{
							os.writeBytes("cd "+ROOT+"Bddlfs/dtls/" + "\n");
							os.flush(); 
							
							os.writeBytes("rm *" + "\n");  
				            os.flush();    
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
						MyLog.log("clearlog");
					  
					}
				}).start();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	Runnable r_sign = new Runnable(){
	    @Override
	    public void run() {
	    	Sign();
	    }
	};
	
	private void init_task()
	{
		m_Cursor =  sqldb.get_sendtask(taskid);
		if (AutoPHONEActivity.isdebug)  Log.e(TAG,"m_Cursor.get_sendtask");
	    
		while (m_Cursor.moveToNext())
		{
			m_PhoneTask.setTaskfilepath(m_Cursor.getString(m_Cursor.getColumnIndex("taskfilepath")));
			m_PhoneTask.setTaskfilename(m_Cursor.getString(m_Cursor.getColumnIndex("taskfilename")));
			m_PhoneTask.setTaskname(m_Cursor.getString(m_Cursor.getColumnIndex("taskname")));
		//	m_PhoneTask.setPlatename(m_Cursor.getString(m_Cursor.getColumnIndex("platename")));
		//	m_PhoneTask.setPlatecontent(m_Cursor.getString(m_Cursor.getColumnIndex("platecontent")));
				
			mfilePath = m_PhoneTask.getTaskfilepath() +"/"+ m_PhoneTask.getTaskfilename();
		}
		
	    TextView  tv_sendinfo = (TextView)findViewById(R.id.tv_sendinfo);
	    tv_sendinfo.setText(tv_sendinfo.getText().toString()+"\t\t"+ m_PhoneTask.getTaskname()+"\r\n文件路径："+mfilePath);
		
	    EditText  edt_showresult = (EditText)findViewById(R.id.edt_showresult);
	 //   edt_showresult.setText(GernatorPHONEtext.getSMSresult(m_PhoneTask.getPlatecontent()));
	    
		m_Cursor =  sqldb.get_config();
		if (AutoPHONEActivity.isdebug) Log.e(TAG,"m_Cursor.get_sendinteval");
	    
		while (m_Cursor.moveToNext())
		{
			send_interval = m_Cursor.getInt(m_Cursor.getColumnIndex("sendinteval"));
			callmode = m_Cursor.getInt(m_Cursor.getColumnIndex("callmode"));
			
			switch (callmode)
			{
			case CALLMODE_1:
				tv_callmode_1.setBackgroundColor(Color.rgb(51, 255, 51));
				tv_callmode_2.setBackgroundColor(Color.rgb(192, 192, 192));
				tv_callmode_3.setBackgroundColor(Color.rgb(192, 192, 192));
				break;
			case CALLMODE_2:
				tv_callmode_2.setBackgroundColor(Color.rgb(51, 255, 51));
				tv_callmode_3.setBackgroundColor(Color.rgb(192, 192, 192));
				tv_callmode_1.setBackgroundColor(Color.rgb(192, 192, 192));
				break;
			case CALLMODE_3:
				tv_callmode_3.setBackgroundColor(Color.rgb(51, 255, 51));
				tv_callmode_2.setBackgroundColor(Color.rgb(192, 192, 192));
				tv_callmode_1.setBackgroundColor(Color.rgb(192, 192, 192));
				break;
			default:
				break;
			}
			
			edt_sendinteval.setText(String.valueOf(send_interval));
			cutdown = m_Cursor.getInt(m_Cursor.getColumnIndex("cutdown"));
			edt_cutdown.setText(String.valueOf(cutdown));
			
			int tmp_checkcutdown = m_Cursor.getInt(m_Cursor.getColumnIndex("iscutdown"));
			
			if (tmp_checkcutdown==1)
			{
				checkcutdown = true;
				tgbtn_checkcutdown.setChecked(checkcutdown);
			}
			
			zitisize = m_Cursor.getInt(m_Cursor.getColumnIndex("zitisize"));
			ziticolor = m_Cursor.getInt(m_Cursor.getColumnIndex("ziticolor"));
			int tmp_showinfo = m_Cursor.getInt(m_Cursor.getColumnIndex("showinfo"));
			
			if (tmp_showinfo==0)
			{
				showinfo = false;
			}
			
			signfromdb = m_Cursor.getBlob(m_Cursor.getColumnIndex("sign"));
			if (signfromdb != null)
			{
				if (AutoPHONEActivity.isdebug) Log.e(TAG, "signfromdb:" + new String(signfromdb));
			}
			
		}
		
		
	}

	class load_task extends Thread
	{
		boolean mylife = true;

		@Override
		public void interrupt()
		{
			// TODO Auto-generated method stub
			super.interrupt();
		}


		@Override
		public void run()
		{
			
			File feFile = new File(mfilePath);
			
			if (AutoPHONEActivity.isdebug) Log.e(TAG, "open file  AbsolutePath: "+feFile.getAbsolutePath());
			
			if (AutoPHONEActivity.isdebug) Log.e(TAG, "open file  name: " +feFile.getName());
			if(feFile.canRead())
			{
				FileReader frd = null;
				filesize = feFile.length();
				if (AutoPHONEActivity.isdebug) Log.e(TAG,"filesize is: "+ String.valueOf(filesize));
				BufferedReader buffd = null;
				try
				{
					Getnowtime m_Getnowtime = new Getnowtime();
					m_PhoneTask.setTaskStarttime(m_Getnowtime.getnowtime("yyyy-M-d HH:mm"));
					
					frd = new FileReader(feFile);
					
					 buffd = new BufferedReader(frd);
					
					String tmp_str = "";
					send_num = 0;
					send_totalnum = 0;
					long readbytes = 0;
					
					while(((tmp_str=buffd.readLine())!=null)&& mylife)
					{   
						String[] extend_info = null;
						
						if (AutoPHONEActivity.isdebug) Log.e(TAG,"load_file 线程号："+Thread.currentThread().getId());
						readbytes += tmp_str.getBytes().length+2;
						tmp_str = tmp_str.trim();
						if((tmp_str.length()>0)&&(tmp_str.length()>=5))
						 {	
							extend_info = new String[tmp_str.split(",").length];
							
							extend_info = tmp_str.split(",");
							
					//		send_target[send_num] = extend_info[0];
						//	MyLog.log("queryadd"+send_target[send_num]);
							send_target.put((Integer)send_num, extend_info);
							
							send_num++;
							
						 }
						
						if (AutoPHONEActivity.isdebug) Log.e(TAG,"readbytes is: "+ String.valueOf(readbytes));
						
						progessperct =  Math.round((float)readbytes/filesize*100);
						
						if (AutoPHONEActivity.isdebug) Log.e(TAG, String.valueOf(progessperct)+"%");
						
						if (progessperct<0)
						{
							progessperct = 0;
						}else if (progessperct>100)
						{
							progessperct = 100;
						}
						
						
						if (send_num==10||progessperct>=100)
						{
							
							for (int i = 0; i < send_num; i++)
							{
								String sms_sendtext = m_PhoneTask.getPlatecontent();
							//	PhoneBase t_phonebase = new PhoneBase(send_target[i],"111");
								PhoneBase t_phonebase = new PhoneBase(send_target.get(i));
								
								
								t_phonebase.setPhone_timeout(cutdown);
								PhoneTaskQuery.insert_sendlist(t_phonebase);
							}
							
							synchronized (sendtask_lock)
							{
								sendtask_lock.notifyAll();
							}
							
							if (send_num >= 10 && !teac)
							{
								mylife = false;
								
						
							  if (signfromdb != null)
							   {
								  if (AutoPHONEActivity.isdebug)  Log.e(TAG,"signfromdbd: "+ signfromdb.toString());
								  runOnUiThread(new Runnable()
									{
										public void run()
										{
											Toast.makeText(StartPHONEtaskActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
										}
									});
							   }else{
									runOnUiThread(new Runnable()
									{
										public void run()
										{
											Toast.makeText(StartPHONEtaskActivity.this, "未激活设备，只能外呼10条", Toast.LENGTH_LONG).show();
										}
									});
							   }
							}
							
							synchronized (loadtask_lock) //暂停导入线程
							{
								try
								{
									if (AutoPHONEActivity.isdebug) Log.e(TAG, "锁住了："+Thread.currentThread().getId());
									loadtask_lock.wait();
								} catch (InterruptedException e)
								{
									e.printStackTrace();
								}
							}
							
							send_num=0;
						}
						
					}
					
					while (PhoneTaskQuery.query_sendlist_count()!=0)
					{
						; //等待队列清空然后更新数据库任务表		
					}
					
				    m_Getnowtime = new Getnowtime();
				    m_PhoneTask.setTaskEndtime(m_Getnowtime.getnowtime("yyyy-M-d HH:mm"));
					
				    m_PhoneTask.setTaskfail(send_fail);
				    m_PhoneTask.setTasksuccess(send_success);
				    m_PhoneTask.setTasktotal(send_totalnum);
					sqldb.update_phonetask(m_PhoneTask);
					
					synchronized (sendtask_lock)
					{
						load_finish  = true;
						sendtask_lock.notifyAll();
					}
					
		
				} catch (FileNotFoundException e)
				{
					MyLog.log(""+e);
					e.printStackTrace();
				} catch (IOException e)
				{
					MyLog.log(""+e);
					e.printStackTrace();
				}finally {
					if (buffd!=null)
					{
						try
						{
							buffd.close();
						} catch (IOException e)
						{
							MyLog.log(""+e);
							e.printStackTrace();
						}
					}
					
					if (frd!=null)
					{
						try
						{
							frd.close();
						} catch (IOException e)
						{
							MyLog.log(""+e);
							e.printStackTrace();
						}
					}
				}
				
			}else
			{
				if (AutoPHONEActivity.isdebug) Log.e(TAG, "读取文件出错");
			}
		}
		
	}
	
	class send_call extends Thread
	{

		@Override
		public void run()
		{
			if (AutoPHONEActivity.isdebug) Log.e(TAG,"send_call 启动 线程号："+Thread.currentThread().getId());
			
			while (true)
			{
				if (load_finish&&PhoneTaskQuery.query_sendlist_count()==0)
				{
					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							mProgressBar.setProgress(100);
							StringBuilder sb = new StringBuilder();
							sb.append("任务进度：").append("100%")
							  .append("\t\t外呼数量：").append(send_totalnum);
							tv_sendstatus.setText(sb.toString());
							send_finish = true;
							
							//btn_sendstop.setVisibility(View.INVISIBLE);
					        btn_sendpause.setVisibility(View.INVISIBLE);
					        btn_start.setVisibility(View.VISIBLE);
					        edt_sendinteval.setEnabled(true);
					        edt_cutdown.setEnabled(true);
					        
					        send_isstart = false;
						}
					});
					
					synchronized (sendtask_lock)
					{
						try
						{
							if (AutoPHONEActivity.isdebug) Log.e(TAG, "外呼任务完毕锁住："+Thread.currentThread().getId());
							sendtask_lock.wait();
						} catch (InterruptedException e)
						{
							send_fail++;
							e.printStackTrace();
						}
					}
				}
				
				if (!send_isstart)
				{
					synchronized (sendtask_lock)
					{
						try
						{
							if (AutoPHONEActivity.isdebug) Log.e(TAG, "暂停锁住了："+Thread.currentThread().getId());
							sendtask_lock.wait();
						} catch (InterruptedException e)
						{
							
							e.printStackTrace();
						}
					}
				}
				
				PhoneBase t_PhoneBase = null;
				
				t_PhoneBase = PhoneTaskQuery.poll_sendlist();
				
				
			   if(t_PhoneBase!=null)	
				{
				   
				   Process process;
				  
					try
					{
						process = Runtime.getRuntime().exec("su");
						
						File f = new File(ROOT+"Bddlfs/dtls");

						if (!f.exists())
						{
							f.mkdirs();
						}
						
						 m_WritePhoneLog = new WritePhoneLog(process, ROOT+"Bddlfs/dtls/"+t_PhoneBase.getPhone_number(),callmode);
						m_WritePhoneLog.start();
						
						sleep(1000);
						
						 m_ReadPhoneLog = new ReadPhoneLog(process, ROOT+"Bddlfs/dtls/"+t_PhoneBase.getPhone_number(),t_PhoneBase.getPhone_timeout(),callmode);
						m_ReadPhoneLog.start();
						
						
						
						if (showinfo)
						{
							StringBuilder sb1 = new StringBuilder();
						//	sb1.append(t_PhoneBase.getPhone_number());
							for (String str : t_PhoneBase.getExtend_info())
							{
								sb1.append(str).append("\r\n\r\n");
							}
							
							MyWindowManager.showcallinfo = sb1.toString();
						}else {
							
							MyWindowManager.showcallinfo = " ";
						}
							
						
						Intent it = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+t_PhoneBase.getPhone_number()));
				        startActivity(it);
						
				        send_totalnum++;
						
					} catch (IOException e1)
					{
						send_fail++;
						MyLog.log("IOException"+e1);
					//	e1.printStackTrace();
					} catch (InterruptedException e)
					{
						send_fail++;
						e.printStackTrace();
					}
				   
				   
					m_PhoneTask.setTasktotal(send_totalnum);
				   
				   if (AutoPHONEActivity.isdebug) Log.e(TAG, t_PhoneBase.getPhone_number()+",");
				   if (AutoPHONEActivity.isdebug) Log.e(TAG,"外呼 线程号："+Thread.currentThread().getId());
				  
				   runOnUiThread(new Runnable()
					{
						public void run()
						{
							if (progessperct>=98)
							{
								mProgressBar.setProgress(98);
								StringBuilder sb = new StringBuilder();
								sb.append("任务进度：").append("98%")
								  .append("\t\t外呼数量：").append(send_totalnum);
								tv_sendstatus.setText(sb.toString());
								
								StringBuilder sb1 = new StringBuilder();
								sb1 = new StringBuilder()
				    	        	    .append("外呼成功：").append(send_success)
									    .append("\t\t外呼失败：").append(send_fail);
				    	        	 
				    	         tv_successorfail.setText(sb1.toString());
							}else {
								mProgressBar.setProgress(progessperct);
								StringBuilder sb = new StringBuilder();
								sb.append("任务进度：").append(progessperct+"%")
								  .append("\t\t外呼数量：").append(send_totalnum);
								tv_sendstatus.setText(sb.toString());
								
								StringBuilder sb1 = new StringBuilder();
								sb1 = new StringBuilder()
				    	        	    .append("外呼成功：").append(send_success)
									    .append("\t\t外呼失败：").append(send_fail);
				    	        	 
				    	         tv_successorfail.setText(sb1.toString());
							}
						}
					});

				   
				   try
					{
					//	m_WritePhoneLog.join();
					    m_ReadPhoneLog.join();
					//	m_ReadPhoneLog.go = false;
					    m_WritePhoneLog.interrupt();
						
						Process process2 = Runtime.getRuntime().exec("su");
						DeletePhoneLog m_DeletePhoneLog = new DeletePhoneLog(ROOT+"Bddlfs/dtls/"+t_PhoneBase.getPhone_number()+".txt", process2);
						m_DeletePhoneLog.start();
						
						send_success++;
						
						MyWindowManager.showcallinfo = " ";
						
					} catch (InterruptedException e)
					{
						send_fail++;
						e.printStackTrace();
					} catch (IOException e)
					{
						send_fail++;
						e.printStackTrace();
					}				   
				   
				   
					try
					{
						sleep(send_interval*1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					
				   
				   
				}else {
					if (PhoneTaskQuery.query_sendlist_count()==0)
					{
					  if (send_isstart)
					  {
						  synchronized (loadtask_lock)
							{
							  if (AutoPHONEActivity.isdebug) Log.e(TAG, "解锁导入线程了："+Thread.currentThread().getId());
								loadtask_lock.notifyAll();
							}
						  
						  synchronized (sendtask_lock)
						   {
							  try
							{
								  if (AutoPHONEActivity.isdebug)  Log.e(TAG, "等待队列导入锁住："+Thread.currentThread().getId());
								sendtask_lock.wait();
							} catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						  }
					   }
					}else
					{
						if (AutoPHONEActivity.isdebug) Log.e(TAG, "the query not empty");
					}
				}
			}
		}
	}

 public  void stop_sendtask()
 {
	 send_isstart = false;
	 load_finish = false;
     send_finish = false;
	 
	 if (m_loadtask!=null)
	{
		 m_loadtask.mylife = false;
	}
	 
	 if ((m_WritePhoneLog != null) && m_WritePhoneLog.isAlive())
	{
		 m_WritePhoneLog.interrupt();
	}
     
	 synchronized (loadtask_lock)
		{
		 if (AutoPHONEActivity.isdebug) Log.e(TAG, "帮你解锁了："+Thread.currentThread().getId());
		 PhoneTaskQuery.init_sendlist();
		 loadtask_lock.notifyAll();
		}
	 
	 check_sendinteval();
		
	 sqldb.update_inteval(send_interval);
	 sqldb.update_cutdown(cutdown);
	 sqldb.update_iscutdown(tgbtn_checkcutdown.isChecked());
	 sqldb.update_callmode(callmode);
	 
	 rejectCall();
	 
	 Intent  it = new Intent(StartPHONEtaskActivity.this,FloatWindowService.class);
	 stopService(it);

	Intent open_managertask = new Intent(StartPHONEtaskActivity.this, ManagertaskActivity.class);
	startActivity(open_managertask);
	
 }
  private void check_sendinteval()
    {
		  if (edt_sendinteval.getText().toString().equals(""))
			{
				edt_sendinteval.setText("1");
			}
			
			send_interval = Integer.valueOf(edt_sendinteval.getText().toString());
		    
			if (send_interval>120) {
				edt_sendinteval.setText("120");
				send_interval = 120;
			}else if (send_interval<1)
			{
				edt_sendinteval.setText("1");
				send_interval = 1;
			}
			
			 if (edt_cutdown.getText().toString().equals(""))
				{
				    edt_cutdown.setText("30");
				}
				
			     cutdown = Integer.valueOf(edt_cutdown.getText().toString());
			    
				if (cutdown>40) {
					edt_cutdown.setText("40");
					cutdown = 40;
				}else if (cutdown<10)
				{
					edt_cutdown.setText("10");
					cutdown = 10;
				}
	
    }

	@Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
		  if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if (m_loadtask!=null)
				   {
					  if (m_loadtask.isAlive())
						{
						  AlertDialog isExit = new AlertDialog.Builder(StartPHONEtaskActivity.this).create();  
				            // 设置对话框标题  
				            isExit.setTitle("自动外呼助手");  
				            // 设置对话框消息  
				            isExit.setMessage("外呼任务正在执行，要退出吗？");  
				            // 添加选择按钮并注册监听  
				            isExit.setButton("朕确定", listener);  
				            isExit.setButton2("取消了", listener);  
				            // 显示对话框  
				            isExit.show();
					   		
				            if (AutoPHONEActivity.isdebug) Log.e(TAG, "m_loadtask.isAlive");
						}else
						{
							 // 创建退出对话框  
				            AlertDialog isExit = new AlertDialog.Builder(StartPHONEtaskActivity.this).create();  
				            // 设置对话框标题  
				            isExit.setTitle("自动外呼助手");  
				            // 设置对话框消息  
				            isExit.setMessage("确定要退出吗？");  
				            // 添加选择按钮并注册监听  
				            isExit.setButton("朕确定", listener);  
				            isExit.setButton2("取消了", listener);  
				            // 显示对话框  
				            isExit.show();  
				            if (AutoPHONEActivity.isdebug) Log.e(TAG, "m_loadtask not Alive");
						}
				   }else {
					   // 创建退出对话框  
			            AlertDialog isExit = new AlertDialog.Builder(StartPHONEtaskActivity.this).create();  
			            // 设置对话框标题  
			            isExit.setTitle("自动外呼助手");  
			            // 设置对话框消息  
			            isExit.setMessage("确定要退出吗？");  
			            // 添加选择按钮并注册监听  
			            isExit.setButton("朕确定", listener);  
			            isExit.setButton2("取消了", listener);  
			            // 显示对话框  
			            isExit.show();  
				}
		    }
			          
			        return false;  
	    } 
	  
	  /**监听对话框里面的button点击事件*/  
	    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
	    {  
	        public void onClick(DialogInterface dialog, int which)  
	        {  
	            switch (which)  
	            {  
	            case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序  
	            	
	            	FloatWindowService.changefolattext(" ");
	            	
	            	stop_sendtask();
	                finish();  
	                break;  
	            case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
	                break;  
	            default:  
	                break;  
	            }  
	        }  
	    };    
	    
	    //加密
	    public static String getBase64(String str) {  
	        byte[] b = null;  
	        String s = null;  
	        try {  
	            b = str.getBytes("utf-8");  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        }  
	        if (b != null) {  
	        	
	            s = Base64.encodeToString(b, Base64.NO_WRAP);
	        }  
	        return s;  
	    }  
	  
	    // 解密  
	    public static String getFromBase64(String s) {  
	        byte[] b = null;  
	        String result = null;  
	        if (s != null) {  
	              
	            try {  
	                b = Base64.decode(s.getBytes(), Base64.DEFAULT);
	                result = new String(b, "UTF-8");  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	        }  
	        return result;  
	    }
	    
	    public void rejectCall() {
			
			 try {
		            Method method = Class.forName("android.os.ServiceManager")
		                    .getMethod("getService", String.class);
		            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
		            ITelephony telephony = ITelephony.Stub.asInterface(binder);
		            telephony.endCall();
		        } catch (NoSuchMethodException e) {
		            Log.e(TAG, "NoSuchMethodException", e);
		        } catch (ClassNotFoundException e) {
		            Log.e(TAG, "ClassNotFoundException", e);
		        } catch (Exception e) {
		        }
		    }
	    
	   private void Sign()
		{
		 	HttpClient mHttpClient = new DefaultHttpClient();
		 	String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			 String uri = getResources().getString(R.string.url)+"/AutoPhone_Sign";
			 if (AutoPHONEActivity.isdebug) Log.e(TAG, Imei);
			    HttpPost httppost = new HttpPost(uri);   
			    List<NameValuePair> params = new ArrayList<NameValuePair>();
			     // 添加要传递的参数
			    NameValuePair pair1 = new BasicNameValuePair("serialno", Base64.encodeToString(Imei.getBytes(), Base64.DEFAULT));
			    params.add(pair1);
			   
			    HttpEntity mHttpEntity;
			 			try
			 			{
			 				mHttpEntity = new UrlEncodedFormEntity(params, "gbk");
			 			
			 				httppost.setEntity(mHttpEntity); 
			 				if (AutoPHONEActivity.isdebug) Log.e(TAG, "发送数据");
			 			} catch (UnsupportedEncodingException e1)
			 			{
			 				// TODO Auto-generated catch block
			 				if (AutoPHONEActivity.isdebug) Log.e(TAG, "数据传递出错了");
			 				e1.printStackTrace();
			 			}
			 		    		
			 			mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);	
			 		     
			 		    HttpResponse httpresponse = null;  
			 		    try
			 			{
			 				httpresponse = mHttpClient.execute(httppost);
			 				
			 			   if (httpresponse.getStatusLine().getStatusCode()==200)	
			 			   {
			 				  String response = EntityUtils.toString(httpresponse.getEntity(), "utf-8");
			 				 
			 				 JSONObject mJsonObject = new JSONObject(response);
			 				if (AutoPHONEActivity.isdebug) Log.e(TAG,"rescode:"+httpresponse.getStatusLine().getStatusCode());
			 				if (AutoPHONEActivity.isdebug) Log.e(TAG, response);
							try
							{
								String resp = mJsonObject.getString("resp");
								switch (resp)
								{
								case "0": //SIGN_OK
									String baseencode_serialid = mJsonObject.getString("baseencode_serialid");
									 if (AutoPHONEActivity.isdebug) Log.e(TAG,"baseencode_serialid:"+ baseencode_serialid);
									String tp = getBase64(getMD5(Imei));
									 if (AutoPHONEActivity.isdebug) Log.e(TAG, getMD5(Imei));
									 if (AutoPHONEActivity.isdebug) Log.e(TAG, tp);
									if (baseencode_serialid.equals(tp))
									{
										if (AutoPHONEActivity.isdebug) Log.e(TAG,"认证成功");
										teac = true;
										sqldb.update_serial(baseencode_serialid.getBytes());
									}
									
									break;
								case "2": //SIGN_NOREG
									if (AutoPHONEActivity.isdebug) Log.e(TAG,"认证失败");
									teac = false;
									break;
								
								default:
									
									break;
								} 
							} catch (JSONException e)
							{
								e.printStackTrace();
							}
						 		
			 			   }
			 				
			 			} catch (ClientProtocolException e1)
						{
							e1.printStackTrace();
						} catch (IOException e1)
						{
							e1.printStackTrace();
							if (AutoPHONEActivity.isdebug) Log.e(TAG, "sockettimeout");
							
						} catch (JSONException e1)
						{
							e1.printStackTrace();
						} 
		      } 
	   
	   public static String getMD5(String val)
	    {  
		        MessageDigest md5;
				try
				{
					md5 = MessageDigest.getInstance("MD5");
					 md5.update(val.getBytes());  
				        byte[] m = md5.digest();//加密  
				        StringBuffer sb = new StringBuffer();  
				         for(int i = 0; i < m.length; i ++){  
				          sb.append(m[i]);  
				         }  
				         return sb.toString();  
				} catch (NoSuchAlgorithmException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		       return "1";
		 }
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt("taskid", taskid);
	}  
	   
	   

	/*
	@Override
	protected void onRestart()
	{
		if (AutoSMSActivity.isdebug) Log.e(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume()
	{
		if (AutoSMSActivity.isdebug) Log.e(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		if (AutoSMSActivity.isdebug) Log.e(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		if (AutoSMSActivity.isdebug) Log.e(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		if (AutoSMSActivity.isdebug) Log.e(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void recreate()
	{
		if (AutoSMSActivity.isdebug) Log.e(TAG, "recreate");
		super.recreate();
	}
	
	*/
	
	 @Override
	public void finish()
	{
		 FloatWindowService.changefolattext(" ");
		 
		super.finish();
	}

	public class MyPhoneCallListener extends PhoneStateListener  
	 {  
	   
		 @Override  
		 public void onCallStateChanged(int state, String incomingNumber)  
		 {  
		   
		   switch (state) {  
		     case TelephonyManager.CALL_STATE_IDLE:                   //电话通话的状态  
		    	 isidle = true;
		    	 MyLog.log("空闲状态");
		         break;  
		      default:
		    	  isidle = false;
		    	  MyLog.log("callstate: unknow"); 
		       break;
		   }  
		   super.onCallStateChanged(state, incomingNumber);  
		 }  
	 }

}
