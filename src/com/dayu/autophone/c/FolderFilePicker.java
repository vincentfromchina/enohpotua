package com.dayu.autophone.c;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dayu.autophone.AutoPHONEActivity;
import com.dayu.autophone.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FolderFilePicker extends Dialog {

	final static String TAG = ContactPicker.TAG;
	private static final String ROOT = Environment
			.getExternalStorageDirectory().toString()+"/";
	private final int SELECTED_COLOR = 0xffadbcb6;

	private final String ALL_FILE = "*";
	private String[] mExtensions;
	private ListView mFilesLv;

	private TextView mTitle;
	private FileLvAdapter mFileLvAdapter;
	private String mCurrentPath;
	private PickPathEvent mEvent;
	SimpleAdapter simpleAdapter;

	public FolderFilePicker(Context context, PickPathEvent event,
			String... extensions) {
		super(context);
		mEvent = event;
		
		mExtensions = extensions == null || extensions.length == 0 ? null
				: extensions;
		for (String s : mExtensions)
		{
			if (AutoPHONEActivity.isdebug) Log.e(TAG, s.toString());
		}
		init();
		
	}

	public interface PickPathEvent {
		public void onPickEvent(String resultPath);
	}

	private void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_folder);
		mTitle = (TextView) findViewById(R.id.title_tv);
		mFilesLv = (ListView) findViewById(R.id.folder_lv);
		findViewById(R.id.ok_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						setResult();
						FolderFilePicker.this.dismiss();
					}
				});

		findViewById(R.id.cancel_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						FolderFilePicker.this.dismiss();
					}
				});

		setCurrentPath(ROOT);
		
		mFileLvAdapter = new FileLvAdapter();
		setListAdapterData();
		mFilesLv.setAdapter(mFileLvAdapter);
		
		mFilesLv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isRootPath(mFileLvAdapter.getItem(position)
						.getAbsolutePath())
						|| mFileLvAdapter.getItem(position).isDirectory()) {
					return false;
				}
				mFileLvAdapter.setSelectedIndex(position);
				return true;
			}
		});
		mFilesLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File f = mFileLvAdapter.getItem(position);
				if (f.isDirectory()) {
					setCurrentPath(mFileLvAdapter.getItem(position)
							.getAbsolutePath());
					setListAdapterData();
					mFileLvAdapter.notifyDataSetChanged();
				}else {
					mFileLvAdapter.setSelectedIndex(position);
				}
			}
		});
	}

	private void setResult() {
		if (mEvent != null) {
			 if (mExtensions == null) 
			 {
				mEvent.onPickEvent(mCurrentPath);
			} else {
				File f = mFileLvAdapter.getItem(mFileLvAdapter
						.getSelectedIndex());
				mEvent.onPickEvent(f == null ? null : f.getAbsolutePath());
			}
			FolderFilePicker.this.dismiss();
		}
	}

	private void setCurrentPath(String path) {
		this.mCurrentPath = path;
		mTitle.setText(path);
	}

	private void setListAdapterData() {
		List<File> list = new ArrayList<File>();

		File topFile = new File(mCurrentPath);
		File[] files = topFile.listFiles();
		if (files == null) {
			return;
		}
	//	Log.e("gushiriji", topFile.toString()  );
		
		
		for (File file : files) 
		{
		//	Log.e("gushiriji", file.toString());
			if (file.isDirectory()) {
				list.add(file);
			} else {
				if (mExtensions != null && mExtensions.length > 0) {
					if (ALL_FILE.equals(mExtensions[0])) {
						list.add(file);
					} else {
						for (int i = 0; i < mExtensions.length; i++) {
							if (file.getAbsolutePath().endsWith(mExtensions[i])) {
							//	Log.e("gushiriji", file.getAbsolutePath()  );
								list.add(file);
							}
						}
					}
				}
			}
		}

		Collections.sort(list, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile()) {
					// folder is before of file
					return -1;
				}
				if (o1.isFile() && o2.isDirectory()) {
					// file is behind of folder
					return 1;
				}
				// sort by the ASCII code:a-z 0-9
				return -(o2.getName().toLowerCase().compareTo(o1.getName()
						.toLowerCase()));
			}
		});

		if (!isRootPath(mCurrentPath)) {
			// if the current path is not ROOT_PATH, add ../ at the first
			list.add(0, new File(mCurrentPath).getParentFile());
		}
		mFileLvAdapter.setList(list);
		mFileLvAdapter.setSelectedIndex(-1);
	}

	private boolean isRootPath(String path) {
		return ROOT.equals(path);
	}

	private class FileLvAdapter extends BaseAdapter {

		private int mSelectedIndex = -1;
		public List<File> list;

		public void setList(List<File> l) {
			list = l;
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public File getItem(int position) {
			if (position >= getCount() || position < 0) {
				return null;
			}
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.folder_list_item, null);
				holder.mIcon = (ImageView) convertView.findViewById(R.id.icon);
				holder.mText = (TextView) convertView.findViewById(R.id.text);
				holder.mText.setTextColor(0xff000000);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
		//	holder.mIcon.setBackgroundResource(getItem(position).isFile() ? R.drawable.excel: R.drawable.ic_folder);
		    if (getItem(position).isFile())
			{
				if (getItem(position).getAbsolutePath().endsWith("txt")||getItem(position).getAbsolutePath().endsWith("TXT"))
				{
					holder.mIcon.setBackgroundResource(R.drawable.ic_file);
				}else
				{
					holder.mIcon.setBackgroundResource(R.drawable.excel);
				}
			}else
			{
				holder.mIcon.setBackgroundResource(R.drawable.ic_folder);
			}
				
			
			holder.mText
					.setText(!isRootPath(mCurrentPath) && position == 0 ? "返回上一级目录"
							: getItem(position).getName());

			if (position == mSelectedIndex) {
				convertView.setBackgroundColor(SELECTED_COLOR);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			return convertView;
		}

		public void setSelectedIndex(int index) {
			mSelectedIndex = index;
			notifyDataSetChanged();
		}

		public int getSelectedIndex() {
			return mSelectedIndex;
		}

		class ViewHolder {
			public ImageView mIcon;
			public TextView mText;
		}
	}
}