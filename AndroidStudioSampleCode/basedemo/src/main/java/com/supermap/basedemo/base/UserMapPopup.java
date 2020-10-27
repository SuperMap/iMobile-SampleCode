package com.supermap.basedemo.base;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.basedemo.R;
import com.supermap.basedemo.appconfig.DataManager;
import com.supermap.basedemo.appconfig.DefaultDataManager;
import com.supermap.basedemo.appconfig.MyApplication;
import com.supermap.data.Workspace;

import com.supermap.mapping.MapControl;

public class UserMapPopup extends PopupWindow implements OnClickListener{
	
	private TextView mTvTitle = null;
	
	private ListView mListMaps = null;
	
	private LayoutInflater mInflater = null;
	
	private DataManager mUserDataManager = null;
	private Workspace mWorkspace = null;
	private MapControl   mMapControl     = null;
	private View         mContentView    = null; 
	private UserWorkspacePopup  mUserWorkspacePopup = null;
	private DefaultDataManager mDefaultDataManager = null;
	
	/**
	 * 构造函数
	 * @param mapControl         地图控件
	 * @param workspacePopup     工作空间列表对象
	 */
	public UserMapPopup(MapControl mapControl, UserWorkspacePopup workspacePopup) {
		mMapControl = mapControl;
		mInflater = LayoutInflater.from(mapControl.getContext());
		mUserWorkspacePopup = workspacePopup;

		mDefaultDataManager = MyApplication.getInstance().getDefaultDataManager();
		mUserDataManager    = MyApplication.getInstance().getUserDataManager();
		mUserDataManager.open();
		mWorkspace = mUserDataManager.getWorkspace();
		mMapControl.getMap().setWorkspace(mWorkspace);
		initView();
	}
	
	/**
	 * 初始化显示主界面
	 */
	private void initView(){
		mContentView = mInflater.inflate(R.layout.activity_maps, null);
		setContentView(mContentView);
		mTvTitle = (TextView) mContentView.findViewById(R.id.common_title).findViewById(R.id.tv_title);
		mListMaps = (ListView) mContentView.findViewById(R.id.list_maps);
		mContentView.findViewById(R.id.common_title).findViewById(R.id.btn_back).setOnClickListener(this);
		
		mTvTitle.setText("地图");
		mListMaps.setAdapter(new MapsAdapter());
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			mUserWorkspacePopup.show();
			dismiss();
			break;
		default:
			break;
		}
	}
	
	
	private static class ViewHolder{
		TextView MapName;
		ImageView MapType;
	}
	
	private class MapsAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mWorkspace.getMaps().getCount();
		}

		@Override
		public Object getItem(int arg0) {
			return mWorkspace.getMaps().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.listview_map_item,	 null);
				holder = new ViewHolder();
				holder.MapName = (TextView) convertView.findViewById(R.id.tv_map_name);
				holder.MapType = (ImageView) convertView.findViewById(R.id.img_map_type);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final String name = mWorkspace.getMaps().get(index);
			holder.MapName.setText(name);
			int resId = mDefaultDataManager.getMapTypeResource(name);
			holder.MapType.setImageResource(resId);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					mMapControl.getMap().close();
					mMapControl.getMap().open(name);
					// 对长春市地图增加整屏刷新
					if(name.equals("长春市区图")){
						mMapControl.getMap().setFullScreenDrawModel(true);
					}else{
						mMapControl.getMap().setFullScreenDrawModel(false);
					}
					mMapControl.getMap().refresh();

					int count = mListMaps.getChildCount();
					for(int index=0; index<count; index++)
						mListMaps.getChildAt(index).setEnabled(true);
					arg0.setEnabled(false);
					
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 显示地图列表
	 */
	  public void show(){
			
			showAt(100, 140, 350, 480);
		}
		private void showAt(int x,int y, int width, int height)
		{
			setWidth(MyApplication.dp2px(width));
			setHeight(MyApplication.dp2px(height));
			showAtLocation(mMapControl.getRootView(), Gravity.LEFT|Gravity.TOP,MyApplication.dp2px(x), MyApplication.dp2px(y));
		}
		
    /**
     * 关闭地图列表
     */
	public void dismiss() {

		super.dismiss();
	}

}
