package com.supermap.android.track;


import java.util.ArrayList;
import java.util.HashMap;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.track.Track;

public class TrackListPopup extends PopupWindow implements OnClickListener {

	private MapControl        m_MapControl     = null;	
	private Datasource        m_Datasource     = null;
	private Datasets          m_Datasets       = null;
	private Layers            m_Layers         = null;

	private LayoutInflater    m_LayoutInflater = null;
	private View              m_ContentView    = null;
	private ListView          m_TrackList      = null;
	private ArrayList<String> m_TrackNameList  = null;
	public TrackListPopup (MapControl mapControl, Track track){
		m_LayoutInflater = LayoutInflater.from(mapControl.getContext());
		m_MapControl     = mapControl;
		m_Datasource     = mapControl.getMap().getWorkspace().getDatasources().get("track");
		m_Datasets       = m_Datasource.getDatasets();
		m_Layers         = mapControl.getMap().getLayers();
		m_TrackNameList  = new ArrayList<String> ();
		
		initView();
		
	}
	private void initView() {
		// TODO Auto-generated method stub
		m_ContentView = m_LayoutInflater.inflate(R.layout.tracklist, null);
		setContentView(m_ContentView);
		
		((Button) m_ContentView.findViewById(R.id.btn_tracklist_cancel)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_tracklist_confirm)).setOnClickListener(this);
		
		m_TrackList = (ListView) m_ContentView.findViewById(R.id.lv_track);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btn_tracklist_confirm:
			conform();
			break;
		case R.id.btn_tracklist_cancel:
			cancel();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 取消
	 */
	private void cancel() {
		dismiss();
	}
	
	/**
	 * 确认
	 */
	private void conform() {
		openDatasetsInMap();
		dismiss();
	}

	private static class ViewHolder{
		CheckBox Check;
	}
	
	/**
	 * 定义Listview 的Adapter类
	 *
	 */
	private class TrackListAdapter extends BaseAdapter{

		private HashMap<Integer, Boolean> mCheckStates = new HashMap<Integer, Boolean>();

		public TrackListAdapter() {
			for (int i = getCount() - 1; i >= 0; i--) {
				mCheckStates.put(i, false);
			}
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return (m_Datasets == null) ? 0 : m_Datasets.getCount();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return (m_Datasets == null) ? null : m_Datasets.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int index, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView == null){
				convertView = m_LayoutInflater.inflate(R.layout.tracklist_item, null);
				holder = new ViewHolder();
				holder.Check = (CheckBox) convertView.findViewById(R.id.tracklist_item);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final String name = m_Datasets.get(index).getName();
			holder.Check.setText(name);
			mCheckStates.get(index);
			holder.Check.setChecked(mCheckStates.get(index));
			holder.Check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View checkBox) {
					boolean checked = ((CheckBox)checkBox).isChecked();
					mCheckStates.put(index, checked);
					if(checked){
						m_TrackNameList.add(name);
					}else{
						if(m_TrackNameList.contains(name)){
							m_TrackNameList.remove(name);
						}
					}
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 显示选中的轨迹
	 */
	private void openDatasetsInMap() {
		clearLayers();
		
		for(String name:m_TrackNameList){
			Dataset dataset = m_Datasets.get(name);
			if(dataset.getType()!=DatasetType.TABULAR){
				//属性数据不能当地图元素打开
				m_Layers.add(dataset, true);
			}
		}
		m_TrackNameList.clear();
		m_MapControl.getMap().refresh();
	}
	
	private void clearLayers() {
		int count = m_Layers.getCount();
		for(;count>1;){
		    m_Layers.remove(0);
		    count --;
		}
	}
	
    /**
     * 显示
     */
   public void show(){
	    m_TrackList.setAdapter(new TrackListAdapter());
		showAt(100, 140, 350, 480);
	}
	
	private void showAt(int x,int y, int width, int height)
	{
		setWidth(MyApplication.dp2px(width));
		setHeight(MyApplication.dp2px(height));
		showAtLocation(m_MapControl.getRootView(), Gravity.LEFT|Gravity.TOP,MyApplication.dp2px(x), MyApplication.dp2px(y));
	
	}
	
	public void dismiss(){
		super.dismiss();
		if(m_TrackList != null){
		    int count = m_TrackList.getChildCount();
		    for(int i=0; i<count; i++)
		    	((CheckBox)m_TrackList.getChildAt(i).findViewById(R.id.tracklist_item)).setChecked(false);
		}
	}
	
}