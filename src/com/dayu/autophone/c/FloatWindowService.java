package com.dayu.autophone.c;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.dayu.autophone.R;
import com.dayu.autophone.StartPHONEtaskActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class FloatWindowService extends Service
{
	 /** 
     * 用于在线程中创建或移除悬浮窗。 
     */  
    private Handler handler = new Handler();  
  
    /** 
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。 
     */  
    private Timer timer;  
    
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    
    private static final String ROOT = Environment
			.getExternalStorageDirectory().toString()+"/";
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
    	
    	 mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
         mBuilder = new NotificationCompat.Builder(this);  
    	
        mBuilder.setContentTitle("外呼进度:")//设置通知栏标题  
        .setContentText("0%") //设置通知栏显示内容</span>  
        .setContentIntent(getDefalutIntent(Notification.FLAG_NO_CLEAR)) //设置通知栏点击意图  
    //  .setNumber(number) //设置通知集合的数量  
        .setTicker("外呼任务监控") //通知首次出现在通知栏，带上升动画效果的  
        .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间  
        .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级  
    //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消    
        .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)  
       // .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合  
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission  
        .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON 
        mBuilder.setOngoing(true); //滑行删除通知无效
        
        
        Intent intent2 = new Intent(getApplicationContext(),StartPHONEtaskActivity.class);  
        intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK); //关键的一步，设置启动模式
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);  
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setProgress(100, StartPHONEtaskActivity.progessperct, false);
        
        mNotificationManager.notify(123, mBuilder.build()); 
        
    	// 开启定时器，每隔0.5秒刷新一次  
        if (timer == null) {  
            timer = new Timer();  
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);  
        }  
        
        return super.onStartCommand(intent, flags, startId);  
    }  
    
    public PendingIntent getDefalutIntent(int flags){  
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);  
        return pendingIntent;  
    }  
  
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        // Service被终止的同时也停止定时器继续运行  
        timer.cancel();  
        timer = null;  
        
        mNotificationManager.cancel(123);
        
        MyWindowManager.showcallinfo = " ";
        
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
        
        if (StartPHONEtaskActivity.m_ReadPhoneLog.isAlive())
		{
        	StartPHONEtaskActivity.m_ReadPhoneLog.interrupt();
		}
        
    }  
  
    class RefreshTask extends TimerTask {  
  
        @Override  
        public void run() {  
            // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。  
         //   if (isHome() && !MyWindowManager.isWindowShowing()) { 
        	int remoteProgress = StartPHONEtaskActivity.progessperct;
        	
        	if (remoteProgress>=98 && !StartPHONEtaskActivity.load_finish )
			{
        		remoteProgress = 98;
			}
        	 mBuilder.setProgress(100, remoteProgress, false);
        	 mBuilder.setContentText(String.valueOf(remoteProgress)+"%");
        	 mNotificationManager.notify(123, mBuilder.build());
        	 
        	        	 
        	 if ( !MyWindowManager.isWindowShowing()) { 
                handler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        MyWindowManager.createSmallWindow(getApplicationContext());  
                    }  
                });  
            }  
            // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。 
           	/*
            else if (!isHome() && MyWindowManager.isWindowShowing()) { 
                handler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        MyWindowManager.removeSmallWindow(getApplicationContext());  
                        MyWindowManager.removeBigWindow(getApplicationContext());  
                    }  
                });  
            }  
            // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。  
             */
      //      else if (isHome() && MyWindowManager.isWindowShowing()) {
            else if ( MyWindowManager.isWindowShowing()) {  
                handler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        MyWindowManager.updateUsedPercent(getApplicationContext());  
                    }  
                });  
            }  
        }  
  
    }  
    
    public static void changefolattext(String msg)
    {
    	FloatWindowSmallView.updateFolatText(msg);
    }
  
    /** 
     * 判断当前界面是否是桌面 
     */  
    private boolean isHome() {  
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);  
        return getHomes().contains(rti.get(0).topActivity.getPackageName());  
    }  
  
    /** 
     * 获得属于桌面的应用的应用包名称 
     *  
     * @return 返回包含所有包名的字符串列表 
     */  
    private List<String> getHomes() {  
        List<String> names = new ArrayList<String>();  
        PackageManager packageManager = this.getPackageManager();  
        Intent intent = new Intent(Intent.ACTION_MAIN);  
        intent.addCategory(Intent.CATEGORY_HOME);  
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,  
                PackageManager.MATCH_DEFAULT_ONLY);  
        for (ResolveInfo ri : resolveInfo) {  
            names.add(ri.activityInfo.packageName);  
        }  
        return names;  
    }  

}
