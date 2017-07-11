package com.dayu.autophone;


import com.dayu.autophone.c.DBHelper;
import com.dayu.autophone.c.MyLog;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SysSetActivity extends Activity implements OnClickListener
{
	static ToggleButton tgbtn_showinfo;
	static TextView     tv_zitisize;
	static TextView     tv_ziticolor;
	DBHelper     sqldb;
	static int showinfo = 1,zitisize = 17,ziticolor = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sys_set);
		
	     tgbtn_showinfo = (ToggleButton)findViewById(R.id.tgbtn_showinfo);
	     tv_zitisize = (TextView)findViewById(R.id.tv_zitisize);
	     tv_ziticolor = (TextView)findViewById(R.id.tv_ziticolor);
	     
	     TextView tv_small = (TextView)findViewById(R.id.tv_small);
	     TextView tv_zhong = (TextView)findViewById(R.id.tv_zhong);
	     TextView tv_da = (TextView)findViewById(R.id.tv_da);
	     TextView tv_teda  = (TextView)findViewById(R.id.tv_teda);
	     
	     TextView tv_color_1 = (TextView)findViewById(R.id.tv_color_1);
	     TextView tv_color_2 = (TextView)findViewById(R.id.tv_color_2);
	     TextView tv_color_3 = (TextView)findViewById(R.id.tv_color_3);
	     TextView tv_color_4 = (TextView)findViewById(R.id.tv_color_4);
	     TextView tv_color_5 = (TextView)findViewById(R.id.tv_color_5);
	     TextView tv_color_6 = (TextView)findViewById(R.id.tv_color_6);
	     
	     
	     tv_small.setOnClickListener(this);
	     tv_zhong.setOnClickListener(this);
	     tv_da.setOnClickListener(this);
	     tv_teda.setOnClickListener(this);
	     tv_color_1.setOnClickListener(this);
	     tv_color_2.setOnClickListener(this);
	     tv_color_3.setOnClickListener(this);
	     tv_color_4.setOnClickListener(this);
	     tv_color_5.setOnClickListener(this);
	     tv_color_6.setOnClickListener(this);
	     
	     tgbtn_showinfo.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					showinfo = 1;
				}else
				{
					showinfo = 0;
				}
				
			}
		});
	     
	     sqldb = new DBHelper(SysSetActivity.this, "dbname", null);
	     
	    Cursor  m_Cursor =  sqldb.get_config();
	     
			while (m_Cursor.moveToNext())
			{
				MyLog.log("zitisize:"+m_Cursor.getInt(m_Cursor.getColumnIndex("zitisize")));
				tv_zitisize.setTextSize(m_Cursor.getInt(m_Cursor.getColumnIndex("zitisize")));
				
				int tmp_ziticolor = m_Cursor.getInt(m_Cursor.getColumnIndex("ziticolor"));
				
				 switch (tmp_ziticolor)
					{
					case 1:
						 tv_ziticolor.setBackgroundColor(Color.rgb(0, 0, 0));
						break;
					case 2:
						tv_ziticolor.setBackgroundColor(Color.rgb(255, 255, 255));
						break;
					case 3:
						tv_ziticolor.setBackgroundColor(Color.rgb(255, 0, 0));
						break;
					case 4:
						tv_ziticolor.setBackgroundColor(Color.rgb(0, 255, 0));
						break;
					case 5:
						tv_ziticolor.setBackgroundColor(Color.rgb(0, 0, 255));
						break;
					case 6:
						tv_ziticolor.setBackgroundColor(Color.rgb(66, 77, 55));
						break;
					default:
						break;
					}
				 
				int tmp_showinfo = m_Cursor.getInt(m_Cursor.getColumnIndex("showinfo"));
				
				if (tmp_showinfo==0)
				{
					tgbtn_showinfo.setChecked(false);
				}
			}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tv_small:
			tv_zitisize.setTextSize(15);
			zitisize = 15;
			break;
		case R.id.tv_zhong:
			tv_zitisize.setTextSize(18);
			zitisize = 18;
			break;
		case R.id.tv_da:
			tv_zitisize.setTextSize(21);
			zitisize = 21;
			break;
		case R.id.tv_teda:
			tv_zitisize.setTextSize(24);
			zitisize = 24;
			break;
		case R.id.tv_color_1:
			tv_ziticolor.setBackgroundColor(Color.rgb(0, 0, 0));
			ziticolor = 1;
			break;
		case R.id.tv_color_2:
			tv_ziticolor.setBackgroundColor(Color.rgb(255, 255, 255));
			ziticolor = 2;
			break;
		case R.id.tv_color_3:
			tv_ziticolor.setBackgroundColor(Color.rgb(255, 0, 0));
			ziticolor = 3;
			break;
		case R.id.tv_color_4:
			tv_ziticolor.setBackgroundColor(Color.rgb(0, 255, 0));
			ziticolor = 4;
			break;
		case R.id.tv_color_5:
			tv_ziticolor.setBackgroundColor(Color.rgb(0, 0, 255));
			ziticolor = 5;
			break;
		case R.id.tv_color_6:
			tv_ziticolor.setBackgroundColor(Color.rgb(66, 77, 55));
			ziticolor = 6;
			break;
		default:
			break;
		}
		
	}

	@Override
	protected void onPause()
	{
		sqldb.update_ziticolor(ziticolor);
		sqldb.update_zitisize(zitisize);
		sqldb.update_showinfo(showinfo);
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		
		super.onStop();
	}

	
	
}
