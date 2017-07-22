package com.dayu.autophone.c;

import java.lang.reflect.Field;

import com.dayu.autophone.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatWindowSmallView extends LinearLayout
{
	public static  int txcolor = Color.GREEN;
	
	public static  int txsize = 18;

	static TextView percentView;
	/** 
     * ��¼С�������Ŀ�� 
     */  
    public static int viewWidth;  
  
    /** 
     * ��¼С�������ĸ߶� 
     */  
    public static int viewHeight;  
  
    /** 
     * ��¼ϵͳ״̬���ĸ߶� 
     */  
     private static int statusBarHeight;  
  
    /** 
     * ���ڸ���С��������λ�� 
     */  
    private WindowManager windowManager;  
  
    /** 
     * С�������Ĳ��� 
     */  
    private WindowManager.LayoutParams mParams;  
  
    /** 
     * ��¼��ǰ��ָλ������Ļ�ϵĺ�����ֵ 
     */  
    private float xInScreen;  
  
    /** 
     * ��¼��ǰ��ָλ������Ļ�ϵ�������ֵ 
     */  
    private float yInScreen;  
  
    /** 
     * ��¼��ָ����ʱ����Ļ�ϵĺ������ֵ 
     */  
    private float xDownInScreen;  
  
    /** 
     * ��¼��ָ����ʱ����Ļ�ϵ��������ֵ 
     */  
    private float yDownInScreen;  
  
    /** 
     * ��¼��ָ����ʱ��С��������View�ϵĺ������ֵ 
     */  
    private float xInView;  
  
    /** 
     * ��¼��ָ����ʱ��С��������View�ϵ��������ֵ 
     */  
    private float yInView;  
  
    public FloatWindowSmallView(Context context) {  
        super(context);  
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);  
        View view = findViewById(R.id.small_window_layout);  
     view.getLayoutParams();
		   viewWidth = view.getLayoutParams().width;  
        viewHeight = view.getLayoutParams().height;  

        percentView = (TextView) findViewById(R.id.percent);  
       // percentView.setText(MyWindowManager.getUsedPercentValue(context));  
        percentView.setText(MyWindowManager.showcallinfo);
        percentView.setTextColor(txcolor);
        percentView.setTextSize(txsize);
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            // ��ָ����ʱ��¼��Ҫ����,�������ֵ����Ҫ��ȥ״̬���߶�  
            xInView = event.getX();  
            yInView = event.getY();  
            xDownInScreen = event.getRawX();  
            yDownInScreen = event.getRawY() - getStatusBarHeight();  
            xInScreen = event.getRawX();  
            yInScreen = event.getRawY() - getStatusBarHeight();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            xInScreen = event.getRawX();  
            yInScreen = event.getRawY() - getStatusBarHeight();  
            // ��ָ�ƶ���ʱ�����С��������λ��  
            updateViewPosition();  
            break;  
        case MotionEvent.ACTION_UP:  
            // �����ָ�뿪��Ļʱ��xDownInScreen��xInScreen��ȣ���yDownInScreen��yInScreen��ȣ�����Ϊ�����˵����¼���  
            if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {  
              //  openBigWindow();  
            	MyLog.log("�����С��");
            }  
            break;  
        default:  
            break;  
        }  
        return true;  
    }  
  
    /** 
     * ��С�������Ĳ������룬���ڸ���С��������λ�á� 
     *  
     * @param params 
     *            С�������Ĳ��� 
     */  
    public void setParams(WindowManager.LayoutParams params) {  
        mParams = params;  
    }  
    
    /** 
     * ����С���������������ݡ� 
     */  
    public static void updateFolatText(String str) {  
    	 percentView.setText(str);
    }  
  
    /** 
     * ����С����������Ļ�е�λ�á� 
     */  
    private void updateViewPosition() {  
        mParams.x = (int) (xInScreen - xInView);  
        mParams.y = (int) (yInScreen - yInView);  
        windowManager.updateViewLayout(this, mParams);  
    }  
  
    /** 
     * �򿪴���������ͬʱ�ر�С�������� 
     */  
    private void openBigWindow() {  
        MyWindowManager.createBigWindow(getContext());  
        MyWindowManager.removeSmallWindow(getContext());  
    }  
  
    /** 
     * ���ڻ�ȡ״̬���ĸ߶ȡ� 
     *  
     * @return ����״̬���߶ȵ�����ֵ�� 
     */  
    private int getStatusBarHeight() {  
        if (statusBarHeight == 0) {  
            try {  
                Class<?> c = Class.forName("com.android.internal.R$dimen");  
                Object o = c.newInstance();  
                Field field = c.getField("status_bar_height");  
                int x = (Integer) field.get(o);  
                statusBarHeight = getResources().getDimensionPixelSize(x);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return statusBarHeight;  
  }
}
