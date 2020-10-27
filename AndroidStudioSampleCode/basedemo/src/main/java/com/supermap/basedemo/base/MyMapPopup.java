package com.supermap.basedemo.base;

import java.util.ArrayList;

import com.supermap.basedemo.R;
import com.supermap.basedemo.appconfig.DefaultDataManager;
import com.supermap.basedemo.appconfig.MyApplication;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;

import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

@SuppressLint("NewApi")
public class MyMapPopup extends PopupWindow implements OnClickListener{
	private View     mContentView  = null;
	private TextView mTvTitle      = null;
	
	private ListView mListMaps = null;
	private ArrayList<String> mapList = null;
	private int indexOfMapList = 0;
	private LayoutInflater mInflater = null;
	private MapControl     mMapControl = null;
	private Workspace mWorkspace = null;

	private DefaultDataManager mDefaultDataManager = null;
	
	private Datasource mDatasourceWMS;
	
	/**
	 * 构造函数
	 * @param mapControl 地图控件
	 */
	public MyMapPopup(MapControl mapControl) {
		mMapControl = mapControl;
		mInflater = LayoutInflater.from(mapControl.getContext());
		mDefaultDataManager = MyApplication.getInstance().getDefaultDataManager();
		mWorkspace = mDefaultDataManager.getWorkspace();
		initView();
	}
	
	/**
	 * 初始化显示主界面
	 */
	private void initView(){
		mContentView = mInflater.inflate(R.layout.activity_maps, null);
		setContentView(mContentView);
		 
		mTvTitle  = (TextView) mContentView.findViewById(R.id.common_title).findViewById(R.id.tv_title);
		mListMaps = (ListView) mContentView.findViewById(R.id.list_maps);
		mContentView.findViewById(R.id.common_title).findViewById(R.id.btn_back).setOnClickListener(this);
		
		mTvTitle.setText("地图展示");
		initMapList();
		mListMaps.setAdapter(new MapsAdapter());
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
		TextView MapName;
		ImageView MapType;
	}
	
	private class MapsAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mapList.size();
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
			final String name = mapList.get(index);
			holder.MapName.setText(name);
			int type = mDefaultDataManager.getMapTypeResource(name);
			holder.MapType.setImageResource(type);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					if(mMapControl.getMap().getWorkspace() != mWorkspace){
						mMapControl.getMap().close();
						mMapControl.getMap().refresh();
						mMapControl.getMap().setWorkspace(mWorkspace);
					}
					
					if(mWorkspace.getMaps().indexOf(name) == -1){
						
						// 不存在指定的地图，就提示是否打开超图云服务地图
						popOpenMapInfo(arg0);
					}else {
						if(name.equals("WMS地图")){
							openWMS();
							return;
						}
						// 恢复地图列表的默认背景色
						mMapControl.getMap().close();
						mMapControl.getMap().open(name);
						// 对长春市地图增加整屏刷新
						if(name.equals("长春市区图")){
							mMapControl.getMap().setFullScreenDrawModel(true);
						}else{
							mMapControl.getMap().setFullScreenDrawModel(false);
						}
						mMapControl.getMap().refresh();
						
//						mListMaps.getChildAt(indexOfMapList).setEnabled(true);
//						arg0.setEnabled(false);                           // 固定当前条目的背景色
//						indexOfMapList = mListMaps.indexOfChild(arg0);
						
					}
				}
			});
			return convertView;
		}
	}
	
		/**
	 * 初始化地图列表
	 */
	private void initMapList(){
		mapList = new ArrayList<String>();
		mapList.add("长春市区图");
		mapList.add("超图云服务");
		mapList.add("iServerRest地图");
		mapList.add("天地图");
		mapList.add("谷歌地图");
		mapList.add("百度地图");
		mapList.add("SIT地图");
		mapList.add("DEM地图");
		mapList.add("SCI地图");
		mapList.add("CAD地图");
		mapList.add("WMS地图");
		mapList.add("Bing地图");
		mapList.add("OpenStreetMap");
		
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
	public void dismiss(){
		
		super.dismiss();
		if (mListMaps.getChildAt(indexOfMapList) != null)
			mListMaps.getChildAt(indexOfMapList).setEnabled(true);
	}
	
	/**
	 * 提示框
	 * @param arg0  Android当前显示控件
	 */
	public void popOpenMapInfo(View arg0){
		AlertDialog.Builder builer = new AlertDialog.Builder(mMapControl.getContext());
		builer.setTitle("指定的地图不存在");
		builer.setMessage("是否打开超图云服务地图?");
		final View view = arg0;
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 恢复地图列表的默认背景色
				int count = mListMaps.getChildCount();
				for(int index=0; index<count; index++ ){
					
					boolean isFind = ((TextView)(mListMaps.getChildAt(index).findViewById(R.id.tv_map_name))).getText().toString().contains("超图云服务");
					if(isFind){
						mListMaps.getChildAt(index).setEnabled(false);  // 显示选中超图云服务地图
						indexOfMapList = index;
					}else{
						mListMaps.getChildAt(index).setEnabled(true);
					}
				}
				
				mMapControl.getMap().open("超图云服务");
				mMapControl.getMap().refresh();
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builer.create().show();
	}
	
	private void openWMS(){
		// OpenGL暂时不能显示WMS，非OpenGL可以
//		if(Environment.isOpenGLMode()){
//			Toast.makeText(mMapControl.getContext(), "打开失败,OpenGL显示引擎暂时不支持显示WMS地图", Toast.LENGTH_SHORT).show();
//			return;
//		}
		if (mDatasourceWMS == null) {
			mDatasourceWMS = OpenDatasource.open_WMS130_World(mWorkspace, "WorldWMS130");
		}
		if (mDatasourceWMS != null) {
			mMapControl.getMap().close();
			mMapControl.getMap().getLayers().add(mDatasourceWMS.getDatasets().get(0), true);
			mMapControl.getMap().refresh();
		}else {
			Toast.makeText(mMapControl.getContext(), "打开WMS地图", Toast.LENGTH_SHORT).show();
		}
	}
}
