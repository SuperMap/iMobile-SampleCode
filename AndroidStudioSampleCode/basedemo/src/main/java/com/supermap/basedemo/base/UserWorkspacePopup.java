package com.supermap.basedemo.base;

import java.io.File;
import java.io.FilenameFilter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.basedemo.R;
import com.supermap.basedemo.appconfig.DataManager;
import com.supermap.basedemo.appconfig.DefaultDataConfig;

import com.supermap.basedemo.appconfig.MyApplication;
import com.supermap.mapping.MapControl;

public class UserWorkspacePopup extends PopupWindow implements OnClickListener{
	private TextView mTvTitle = null;
	
	private ListView mListWks = null;
	
	private static String[] mWorkspacesFiles = null;
	static{
		File dir = new File(DefaultDataConfig.MapDataPath);
		FilenameFilter wks = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith("SMWU")||name.endsWith("smwu")
				 ||name.endsWith("SXWU") || name.endsWith("sxwu")){
					return true;
				}
				return false;
			}
		};
		mWorkspacesFiles = dir.list(wks);
	}
	
	private LayoutInflater mInflater = null; 
	
	private MapControl   mMapControl     = null;
	private View         mContentView    = null; 
	private UserMapPopup mUserMapPopup   = null;
	private UserWorkspacePopup mUserWorkspacePopup = null;
	
		/**
	 * 构造函数
	 * @param mapControl    地图控件
	 */
	public UserWorkspacePopup(MapControl mapControl) {
		mMapControl = mapControl;
		mInflater = LayoutInflater.from(mapControl.getContext());

		initView();
		initWorsapce();
		mUserWorkspacePopup = this;
	}
	
	/**
	 * 初始化显示主界面
	 */
	private void initView(){
		mContentView = mInflater.inflate(R.layout.activity_wks, null);
		setContentView(mContentView);
		
		mTvTitle = (TextView) mContentView.findViewById(R.id.common_title).findViewById(R.id.tv_title);
		mListWks = (ListView) mContentView.findViewById(R.id.list_wks);		
		mContentView.findViewById(R.id.common_title).findViewById(R.id.btn_back).setOnClickListener(this);
		
		mTvTitle.setText("打开工作空间");
		mListWks.setAdapter(new WorkspaceAdapter());
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
		    dismiss();
		    MainActivity.reset();
			break;
		default:
			break;
		}
	}
	
	
	private static class ViewHolder{
		TextView Sever;
	}
	
	private class WorkspaceAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mWorkspacesFiles.length;
		}

		@Override
		public Object getItem(int arg0) {
			return mWorkspacesFiles[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int index, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.listview_wks_item,	 null);
				holder = new ViewHolder();
				holder.Sever = (TextView) convertView.findViewById(R.id.tv_wks_name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final String name = mWorkspacesFiles[index];
			holder.Sever.setText(name);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					MyApplication.getInstance().getUserDataManager().setWorkspaceServer(DefaultDataConfig.MapDataPath+mWorkspacesFiles[index]);
					mUserMapPopup  = new UserMapPopup(mMapControl, mUserWorkspacePopup);
					
					mUserMapPopup.show();
				}
			});
			return convertView;
		}
		
	}
	
	/**
	 * 显示工作空间列表
	 */
	public void show() {

		showAt(100, 140, 350, 480);
	}

	private void showAt(int x, int y, int width, int height) {
		setWidth(MyApplication.dp2px(width));
		setHeight(MyApplication.dp2px(height));
		showAtLocation(mMapControl.getRootView(), Gravity.LEFT | Gravity.TOP,
				MyApplication.dp2px(x), MyApplication.dp2px(y));
	}

	/**
	 * 关闭工作空间列表
	 */
	public void dismiss() {

		super.dismiss();
		if (mUserMapPopup != null)
			mUserMapPopup.dismiss();
	}

	/**
	 * 初始化工作空间
	 */
	public void initWorsapce(){
		DataManager dataManager = MyApplication.getInstance().getUserDataManager();
		for(int index = 0; index<mWorkspacesFiles.length; index++){
			dataManager.initWorkspace(DefaultDataConfig.MapDataPath+mWorkspacesFiles[index]);
		}
	}
}
