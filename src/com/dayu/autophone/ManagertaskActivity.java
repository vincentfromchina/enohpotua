package com.dayu.autophone;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.dayu.autophone.ManagercontentplateActivity.platelistadapter;
import com.dayu.autophone.ManagercontentplateActivity.platelistadapter.ViewHolder;
import com.dayu.autophone.c.DBHelper;
import com.dayu.autophone.c.GernatorPHONEtext;
import com.dayu.autophone.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ManagertaskActivity extends Activity
{
	final static String TAG = AutoPHONEActivity.TAG;
	DBHelper sqldb ;
	Cursor m_Cursor;
	private LayoutInflater mInflater;//�õ�һ��LayoutInfalter�����������벼��
	private List<String> taskname ;
	private List<Integer>  taskid;
	private List<String>  taskinfo;
	private  platelistadapter m_datasetadpter;
	 private static ListView lv_smstask ;
	 private static TextView tv_showtaskinfo ;
	 static int pressplateid ;
	 static boolean isfirstlongclick = true;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_managersmstask);
		
		sqldb = new DBHelper(ManagertaskActivity.this, "dbname", null);
		
		mInflater = LayoutInflater.from(ManagertaskActivity.this);
		
	    lv_smstask = (ListView)findViewById(R.id.lv_smstask);
	    tv_showtaskinfo = (TextView)findViewById(R.id.tv_showtaskinfo);

	    update_dataset();
	
	m_datasetadpter = new platelistadapter();
	
	lv_smstask.setAdapter(m_datasetadpter);
	
	lv_smstask.setOnItemClickListener(new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			//������ڱ�������ʾ����˵ڼ���
			if (AutoPHONEActivity.isdebug) Log.e(TAG,"�����˵�"+position +"id:"+id);
            
           if (isfirstlongclick)
		   {
        	   isfirstlongclick = !isfirstlongclick;
        	   Toast.makeText(ManagertaskActivity.this, "�����޸���������", Toast.LENGTH_LONG).show();    
		   }   
            
           tv_showtaskinfo.setText(taskinfo.get(position));
           
		}
		
	});
	
	lv_smstask.setOnItemLongClickListener(new OnItemLongClickListener()
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			pressplateid = taskid.get(position);
			if (AutoPHONEActivity.isdebug) Log.e(TAG,"�㳤���˵�"+position+"ģ����"+taskname.get(position));
			show_modtasknameDialog(taskname.get(position));
		
			return false;
		}
		
	});
	
}
	
	class platelistadapter extends BaseAdapter
	{
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			 ViewHolder holder;
	            //�۲�convertView��ListView�������
	     
	            if (convertView == null) {
	                     convertView = mInflater.inflate(R.layout.showsmstask, parent, false);

	                     holder = new ViewHolder();
	                    /*�õ������ؼ��Ķ���*/
	                    holder.taskname = (TextView) convertView.findViewById(R.id.tv_listsmstask);
	                    holder.delete_task = (Button) convertView.findViewById(R.id.btn_deltask);
	                    holder.start_task = (Button) convertView.findViewById(R.id.btn_starttask);
	                    
	                    convertView.setTag(holder);//��ViewHolder����
	                    if (AutoPHONEActivity.isdebug) Log.e(TAG, "getView " + position + " " + convertView);
	          }
	          else{
	                    holder = (ViewHolder)convertView.getTag();//ȡ��ViewHolder����
	                    if (AutoPHONEActivity.isdebug) Log.e(TAG, "��ִ��");
	                  }
	            /*����TextView��ʾ�����ݣ������Ǵ���ڶ�̬�����е�����*/
	            holder.start_task.setTag(taskid.get(position));
                holder.delete_task.setTag(taskid.get(position));
	            holder.taskname.setText((String)getItem(position));
	            if (AutoPHONEActivity.isdebug) Log.e(TAG, ""+position+"--"+taskid.get(position));
                
                holder.delete_task.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						if (AutoPHONEActivity.isdebug) Log.e(TAG, "������ɾ����ť" + v.getTag()); 
						sqldb.delrec_phonetask((Integer)v.getTag());
						 update_dataset();
					     m_datasetadpter.notifyDataSetChanged();
					   //  lv_contentplate.setAdapter(m_datasetadpter);
					}
				});
                
                holder.start_task.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						Intent open_starttaskactivity = new Intent();
						open_starttaskactivity.setClass(ManagertaskActivity.this, StartPHONEtaskActivity.class);
         				
						Bundle bundle = new Bundle();
         				bundle.putInt("taskid", (int)v.getTag());
         				open_starttaskactivity.putExtras(bundle);
         				
						startActivity(open_starttaskactivity);
						
						finish();
					}
				});
	            /*ΪButton���ӵ���¼�*/
	            return convertView;
		}
		
		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int position)
		{
			if (position >= getCount() || position < 0) {
				return null;
			}
			return taskname.get(position);
		}
		
		@Override
		public int getCount()
		{			
			//Log.e("autophone", "getCount "+m_Cursor.getCount());
			return taskname.size();
		}
		
	   class ViewHolder{
		    public TextView taskname;
		    public Button   delete_task;
		    public Button	start_task;
		    }
	};
	
	public void update_dataset()
	{
		taskname = new ArrayList<>();		
		taskid = new ArrayList<>();
		taskinfo = new ArrayList<>();
		
	    m_Cursor =  sqldb.query_phonetask(false);
	    if (AutoPHONEActivity.isdebug) Log.e(TAG,"m_Cursor.getCount"+ m_Cursor.getCount());
	    
		while (m_Cursor.moveToNext())
		{
			taskname.add(m_Cursor.getString(2));	
			taskid.add(Integer.valueOf(m_Cursor.getInt(9)));
			taskinfo.add("ʱ��:"+m_Cursor.getString(1)+"   �����:"+Integer.valueOf(m_Cursor.getInt(7)));
		}
	}
    

	public void show_modtasknameDialog(String platename){         
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        LayoutInflater inflater = getLayoutInflater();  
        final View layout = inflater.inflate(R.layout.modtasknamedialog, null);//��ȡ�Զ��岼��  
        builder.setView(layout);  
    //    builder.setIcon(R.drawable.ic_launcher);//���ñ���ͼ��  
        builder.setTitle("�༭��������");//���ñ�������  
        //builder.setMessage("");//��ʾ�Զ��岼������  
        final EditText edt_modtaskname = (EditText)layout.findViewById(R.id.edt_modtaskname);
        edt_modtaskname.setText(platename);  
            
        //ȷ�ϰ�ť  
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
                
            	String modtaskname = edt_modtaskname.getText().toString();

				sqldb.update_phonetaskname(pressplateid, modtaskname);

				update_dataset();
				m_datasetadpter.notifyDataSetChanged();
            }  
        });  
        //ȡ��  
        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {             
            	arg0.dismiss();
            }  
        });  
        final AlertDialog dlg = builder.create();  
        dlg.show();  
     } 

}