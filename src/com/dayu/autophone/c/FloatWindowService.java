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
     * �������߳��д������Ƴ��������� 
     */  
    private Handler handler = new Handler();  
  
    /** 
     * ��ʱ������ʱ���м�⵱ǰӦ�ô��������Ƴ��������� 
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
    	
        mBuilder.setContentTitle("�������:")//����֪ͨ������  
        .setContentText("0%") //����֪ͨ����ʾ����</span>  
        .setContentIntent(getDefalutIntent(Notification.FLAG_NO_CLEAR)) //����֪ͨ�������ͼ  
    //  .setNumber(number) //����֪ͨ���ϵ�����  
        .setTicker("���������") //֪ͨ�״γ�����֪ͨ��������������Ч����  
        .setWhen(System.currentTimeMillis())//֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��  
        .setPriority(Notification.PRIORITY_DEFAULT) //���ø�֪ͨ���ȼ�  
    //  .setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��    
        .setOngoing(false)//ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)  
       // .setDefaults(Notification.DEFAULT_VIBRATE)//��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������  
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND ������� // requires VIBRATE permission  
        .setSmallIcon(R.drawable.ic_launcher);//����֪ͨСICON 
        mBuilder.setOngoing(true); //����ɾ��֪ͨ��Ч
        
        
        Intent intent2 = new Intent(getApplicationContext(),StartPHONEtaskActivity.class);  
        intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK); //�ؼ���һ������������ģʽ
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);  
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setProgress(100, StartPHONEtaskActivity.progessperct, false);
        
        mNotificationManager.notify(123, mBuilder.build()); 
        
    	// ������ʱ����ÿ��0.5��ˢ��һ��  
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
        // Service����ֹ��ͬʱҲֹͣ��ʱ����������  
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
            // ��ǰ���������棬��û����������ʾ���򴴽���������  
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
            // ��ǰ���治�����棬������������ʾ�����Ƴ��������� 
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
            // ��ǰ���������棬������������ʾ��������ڴ����ݡ�  
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
     * �жϵ�ǰ�����Ƿ������� 
     */  
    private boolean isHome() {  
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);  
        return getHomes().contains(rti.get(0).topActivity.getPackageName());  
    }  
  
    /** 
     * ������������Ӧ�õ�Ӧ�ð����� 
     *  
     * @return ���ذ������а������ַ����б� 
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
