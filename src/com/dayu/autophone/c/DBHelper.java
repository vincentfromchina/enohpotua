package com.dayu.autophone.c;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.dayu.autophone.AutoPHONEActivity;
import com.dayu.autophone.m.PhoneTask;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
	
	private static final String DB_NAME = "autophonetask.db";  
    private static final int DB_VERSION = 4;    
    private static final String CREATE_phonetask = "create table if not exists phonetask("  
            + "taskStarttime DATETEXT(19,19),"
            + "taskEndtime DATETEXT(19,19),"
    		+ "taskname VARCHAR2(50),"
    		+ "taskfilepath VARCHAR2(500), taskfilename VARCHAR2(50), "
            + "tasksuccess INT , Taskfail INT, "
    		+ "tasktotal INT, taskcontentplate INT,"
            + "taskid INTEGER PRIMARY KEY AUTOINCREMENT"
    		+ ")";  
    private static final String CREATE_contentplate = "create table if not exists contentplate("  
            + "plateid INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "platename VARCHAR2(50),"
    		+ "platecontent VARCHAR2(1000),"
    		+ "plateaddtime DATETEXT(19, 19)"
    		+ ")";
    
    private static final String CREATE_config = "CREATE TABLE if not exists config ("
    		+"sign BLOB(200)," 
    		+"cutdown INT DEFAULT 30,"
    		+"showinfo INT DEFAULT 1, "
    		+ "zitisize INT DEFAULT 17, "
    		+ "ziticolor INT DEFAULT 4 ,"
    		+"iscutdown BOOLEAN DEFAULT 0,"
    		+"sendinteval INT DEFAULT 1)";
    
    private static final String insert_config = "insert into config(sendinteval,"
    		+ "cutdown,showinfo,zitisize,ziticolor)"
    		+"values(1,8,1,17,4)";
   

	public DBHelper(Context context, String dbname, CursorFactory factory)
	{
		super(context, DB_NAME, null, DB_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		
		String sql = CREATE_phonetask;
        db.execSQL(sql);
        
        sql = CREATE_contentplate;
        db.execSQL(sql);
        
        sql = CREATE_config;
        db.execSQL(sql);
        
        sql = insert_config;
        db.execSQL(sql);
        
        if (AutoPHONEActivity.isdebug) Log.e("database","数据库创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String sql = "";
	  /* sql = "CREATE TABLE [condition] ([periodid] INT,  [wendu_l] INT,  [wendu_h] INT,  [sun_l] INT,  [sun_h] int,  [water] VARCHAR2(1000),  [pzid] INT,  [waterpersecond] INT)";
        db.execSQL(sql);
      		
		/* sql = "alter table condition add [suntime] VARCHAR2(500)";
		db.execSQL(sql);
		
	  
		 sql = "update condition set suntime='06:00:00|20:00:00'";
		 db.execSQL(sql); */
		 
				 
		String pre ="drop table if exists phonetask";
		db.execSQL(pre);
		
		pre ="drop table if exists contentplate";
		db.execSQL(pre);
		
		pre ="drop table if exists config";
		db.execSQL(pre);
		
		sql = CREATE_phonetask;
        db.execSQL(sql);
        
        sql = CREATE_contentplate;
        db.execSQL(sql);
        
        sql = CREATE_config;
        db.execSQL(sql);
        
        sql = insert_config;
        db.execSQL(sql);
        
        if (AutoPHONEActivity.isdebug) Log.e("database","数据库更新成功");
	}
	
	public void delrec_phonetask(Integer taskid)
	{
		String sql ="delete from phonetask where taskid=" +taskid;
		SQLiteDatabase db = getReadableDatabase();
    	db.execSQL(sql);
	}
	
	public void delrec_phonecontentplate(Integer plateid)
	{
		String sql ="delete from phonetask where taskcontentplate =" +plateid;
		SQLiteDatabase db = getReadableDatabase();
    	db.execSQL(sql);
    	
		sql ="delete from contentplate where plateid =" +plateid;
	    db = getReadableDatabase();
    	db.execSQL(sql);
    	
    	
    	if (AutoPHONEActivity.isdebug) Log.e("database","delete成功"); 
	}

    public Cursor query_count()
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","query访问成功");  
    	Cursor c = db.rawQuery("select count(*) as tol from phonetask", null);
    	return c;
    }
    
    public void insert_phonetask(PhoneTask s)
    {
    	String sql ="insert into phonetask(taskStarttime,taskname,taskfilepath,"
    			+ "taskfilename,tasksuccess,taskfail,tasktotal,taskcontentplate,"
    			+"taskEndtime)"
    			+ "values ('"+ s.getTaskStarttime()+ "','"+ s.getTaskname()
    			+ "','"+ s.getTaskfilepath() + "','" + s.getTaskfilename()
    			+ "',"+ s.getTasksuccess() +"," + s.getTaskfail()
    			+ ","+ s.getTasktotal() +"," + s.getTaskcontentplate()
    		    +",'" + s.getTaskEndtime()+"')";
    	SQLiteDatabase db = getReadableDatabase();
    	db.execSQL(sql);
    	
    	if (AutoPHONEActivity.isdebug) Log.e("database","数据库insert成功");
    }
    
    public void insert_contentplate(String platename,String contentplate,String opttime)
    {
    	String sql ="insert into contentplate(platename,platecontent,plateaddtime)"
    			+ "values ('"+ platename+ "','"+ contentplate+ "','"+ opttime
    			+"')";
    	SQLiteDatabase db = getReadableDatabase();
    	db.execSQL(sql);
    	
    	if (AutoPHONEActivity.isdebug) Log.e("database","数据库insert成功");
    }
   
    public void update_phonetask(PhoneTask s)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update_phonetask成功");  
        db.execSQL("update phonetask set taskEndtime='"+s.getTaskEndtime()+ "',tasksuccess=" +s.getTasksuccess()+ ",Taskfail="+ s.getTaskfail()+",tasktotal="+s.getTasktotal()+ " where taskid="+s.getTaskid());
    }
    
    public void update_contentplate(int plateid,String contentplate)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update contentplate成功");  
        db.execSQL("update contentplate set platecontent='"+contentplate+"' where plateid="+plateid);
    }
    
    public void update_contentplatename(int plateid,String contentplatename)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update contentplate set platename='"+contentplatename+"' where plateid="+plateid);  
        db.execSQL("update contentplate set platename='"+contentplatename+"' where plateid="+plateid);
    }
    
    public void update_phonetaskname(int taskid,String taskname)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update phonetask set taskname='"+taskname+"' where taskid="+taskid);  
        db.execSQL("update phonetask set taskname='"+taskname+"' where taskid="+taskid);
    }
    
    public void update_inteval(int inteval)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set sendinteval="+inteval);  
        db.execSQL("update config set sendinteval="+inteval);
    }
    
    public void update_cutdown(int cutdown)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set cutdown="+cutdown);  
        db.execSQL("update config set cutdown="+cutdown);
    }
    
    public void update_showinfo(int showinfo)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set showinfo="+showinfo);  
        db.execSQL("update config set showinfo="+showinfo);
    }
    
    public void update_zitisize(int zitisize)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set zitisize="+zitisize);  
        db.execSQL("update config set zitisize="+zitisize);
    }
    
    public void update_ziticolor(int ziticolor)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set ziticolor="+ziticolor);  
        db.execSQL("update config set ziticolor="+ziticolor);
    }
    
    public void update_iscutdown(boolean iscutdown)
    {
    	int a = 0;
    	if (iscutdown)
		{
			a = 1;
		}
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set iscutdown="+a);  
        db.execSQL("update config set iscutdown="+a);
    }
    
    public void update_serial(byte[] encoded)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","update config set sign="+encoded);  
    	String sqlstr = "update config set sign= ?";

        Object[] args = new Object[]{encoded};
         db.execSQL(sqlstr,args);  
    }
    
    
    public Cursor query_contentplate()
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","query访问成功");  
    	return db.rawQuery("select * from contentplate order by plateaddtime desc", null);
    }
     
    public int query_contentplate_withtask(int plateid) //获取已关联任务
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","query访问成功");  
    	Cursor cr = db.rawQuery("select * from phonetask where plateid ="+plateid, null);
    	return cr.getCount();
    }
    
    public Cursor query_phonetask(boolean asc)
    {
    	String order;
    	order = asc ? "asc" : "desc";
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","query访问成功");  
    	return db.rawQuery("select * from phonetask order by taskEndtime "+ order, null);
    }
    
    public Cursor get_sendtask(int taskid)
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","select * from phonetask where taskid=" + taskid);  
    	return db.rawQuery("select * from phonetask where taskid=" + taskid ,null);
    			         
    }
    
    public Cursor get_config()
    {
    	SQLiteDatabase db = getReadableDatabase();
    	if (AutoPHONEActivity.isdebug) Log.e("database","select * from config");  
    	return db.rawQuery("select * from config",null);
    			         
    }
    
 
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
		if (AutoPHONEActivity.isdebug) Log.e("database","数据库open成功");
	}

	public static void copyDataBaseToSD(String dbfilepath){  
	     if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {  
	            return ;  
	         }  
	     File dbFile = new File( dbfilepath);  
	     File file  = new File(Environment.getExternalStorageDirectory(), "copyof_smstask.db");  
	       
	     FileChannel inChannel = null,outChannel = null;  
	       
	     try {  
	        file.createNewFile();  
	        inChannel = new FileInputStream(dbFile).getChannel();  
	        outChannel = new FileOutputStream(file).getChannel();  
	        inChannel.transferTo(0, inChannel.size(), outChannel);  
	    } catch (Exception e) {  
	    	if (AutoPHONEActivity.isdebug) Log.e("database", "copy dataBase to SD error.");  
	        e.printStackTrace();  
	    }finally{  
	        try {  
	            if (inChannel != null) {  
	                inChannel.close();  
	                inChannel = null;  
	            }  
	            if(outChannel != null){  
	                outChannel.close();  
	                outChannel = null;  
	            }  
	        } catch (IOException e) {  
	        	if (AutoPHONEActivity.isdebug) Log.e("database", "file close error.");  
	            e.printStackTrace();  
	        }  
	    }  
	}  
    
}
