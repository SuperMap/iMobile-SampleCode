package com.supermap.basedemo.base;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.basedemo.R;
import com.supermap.basedemo.appconfig.DataManager;
import com.supermap.basedemo.appconfig.DefaultDataManager;
import com.supermap.basedemo.appconfig.MyApplication;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.Datasets;
import com.supermap.data.Workspace;

import com.supermap.mapping.MapControl;

public class UserDatasetPopup extends PopupWindow implements OnClickListener{
private TextView mTvTitle = null;
	
	private ListView mListDatasets = null;
	
	private Datasets       mOpenedDatasets = null;
	private MapControl     mMapControl     = null;
	private Workspace      mWorkspace      = null;
	private LayoutInflater mInflater       = null; 
	private View           mContentView    = null;
	private DefaultDataManager mDefaultDataManager  = null;
	private DataManager mUserDataManager     = null;
	private UserDatasourcePopup mUserDatasourcePopup = null;
	private ArrayList<String>   mCheckedDatasets     = new ArrayList<String>();
	
	/**
	 * 构造函数
	 * @param mapControl             地图控件
	 * @param userDatasourcePopup    数据源列表显示对象
	 */
	public UserDatasetPopup(MapControl mapControl, UserDatasourcePopup userDatasourcePopup) {
		mMapControl = mapControl;
		mInflater = LayoutInflater.from(mapControl.getContext());
		mUserDatasourcePopup = userDatasourcePopup;
		mOpenedDatasets = MyApplication.getInstance().getUserDataManager().getOpenedDatasets();
		
		mUserDataManager = MyApplication.getInstance().getUserDataManager();
		initView();
	}
	
	/**
	 * 初始化显示主界面
	 */
	private void initView(){
		mContentView = mInflater.inflate(R.layout.activity_dataset, null);
		setContentView(mContentView);
		 
		mTvTitle  = (TextView) mContentView.findViewById(R.id.common_title).findViewById(R.id.tv_title);
		mListDatasets = (ListView) mContentView.findViewById(R.id.list_dataset);
		
		mContentView.findViewById(R.id.common_title).findViewById(R.id.btn_back).setOnClickListener(this);
		mContentView.findViewById(R.id.common_title).findViewById(R.id.btn_go).setVisibility(View.VISIBLE);
		mContentView.findViewById(R.id.common_title).findViewById(R.id.btn_go).setOnClickListener(this);

		mTvTitle.setText("打开数据集");
		mListDatasets.setAdapter(new DatasetsAdapter());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			mUserDatasourcePopup.show();
			dismiss();
			break;
		case R.id.btn_go:
			openDatasetsInMap();
			break;
		default:
			break;
		}
	}
	
	private static class ViewHolder{
		CheckBox Check;
	}
	
	private class DatasetsAdapter extends BaseAdapter{
		
		private HashMap<Integer, Boolean> mCheckStates = new HashMap<Integer, Boolean>();
		
		public DatasetsAdapter() {
			for(int i=getCount()-1;i>=0;i--){
				mCheckStates.put(i, false);
			}
		}

		@Override
		public int getCount() {
			return mOpenedDatasets!=null?mOpenedDatasets.getCount():0;
		}

		@Override
		public Object getItem(int arg0) {
			return mOpenedDatasets!=null?mOpenedDatasets.get(arg0):null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int index, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.listview_dataset_item, null);
				holder = new ViewHolder();
				holder.Check = (CheckBox) convertView.findViewById(R.id.check_dataset);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final String name = mOpenedDatasets.get(index).getName();
			holder.Check.setText(name);
			
			holder.Check.setChecked(mCheckStates.get(index));
			holder.Check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View checkBox) {
					boolean checked = ((CheckBox)checkBox).isChecked();
					mCheckStates.put(index, checked);
					if(checked){
						mCheckedDatasets.add(name);
					}else{
						if(mCheckedDatasets.contains(name)){
							mCheckedDatasets.remove(name);
						}
					}
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 显示数据集
	 */
	private void openDatasetsInMap() {
		mMapControl.getMap().close();
		mMapControl.getMap().setWorkspace(mUserDataManager.getWorkspace());
		for(String name:mCheckedDatasets){
			Dataset dataset = mUserDataManager.getOpenedDatasets().get(name);
			if(dataset.getType()!=DatasetType.TABULAR){
				//属性数据不能当地图元素打开
				mMapControl.getMap().getLayers().add(dataset, false);
			}
		}
		mMapControl.getMap().viewEntire();
		mMapControl.getMap().refresh();
	}
	
	/**
	 * 显示数据源列表
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
	 * 关闭数据源列表
	 */
	public void dismiss(){
		
		super.dismiss();
		if(mListDatasets != null){
		    int count = mListDatasets.getChildCount();
		    for(int i=0; i<count; i++)
		    	((CheckBox)mListDatasets.getChildAt(i).findViewById(R.id.check_dataset)).setChecked(false);
		}
	}
}
