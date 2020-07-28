package com.supermap.android.track;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.track.Track;

public class RecordPopup extends PopupWindow implements OnClickListener {

	
	private LayoutInflater  m_LayoutInflater = null;
	private View            m_ContentView    = null;
	private EditText        m_etTrackName    = null;

	private MapControl      m_MapControl     = null;	
	private Track           m_Track          = null;
	private Datasource      m_Datasource     = null;
	private Layers          m_Layers         = null;
	public RecordPopup (MapControl mapControl, Track track){
		if (mapControl != null && track != null) {
			m_LayoutInflater = LayoutInflater.from(mapControl.getContext());
			m_MapControl     = mapControl;
			m_Track          = track;
			m_Datasource     = mapControl.getMap().getWorkspace().getDatasources().get("track");
            m_Layers         = mapControl.getMap().getLayers();
			initView();
		}else{
			
		}
	}
	private void initView() {
		// TODO Auto-generated method stub
		m_ContentView = m_LayoutInflater.inflate(R.layout.record, null);
		setContentView(m_ContentView);
		
		((Button) m_ContentView.findViewById(R.id.btn_record_confirm)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_record_cancel)).setOnClickListener(this);
		m_etTrackName = (EditText) m_ContentView.findViewById(R.id.edit_TrackName);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btn_record_confirm:
			conform();
			break;
		case R.id.btn_record_cancel:
			cancel();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 取消，并关闭窗口
	 */
	private void cancel() {
		dismiss();
		m_etTrackName.setText(null);
	}
	
	/**
	 * 确认，并关闭窗口
	 */
	private void conform() {
		String datasetName = m_etTrackName.getText().toString();
		if(datasetName.length() == 0 || m_Datasource.getDatasets().contains(datasetName)){
			if(datasetName.length() == 0)
				MainActivity.m_MyApp.showInfo("请输入轨迹名称");
			if(m_Datasource.getDatasets().contains(datasetName))
				MainActivity.m_MyApp.showInfo("已存在同名轨迹，请重新输入");
			
		}else{
			DatasetVector dataset = m_Track.createDataset(m_Datasource, datasetName);    // 创建数据集
			m_Track.stopTrack();                                                         // 停止轨迹记录
			MainActivity.m_EnableLocationService = false;
			m_Track.setDataset(dataset);                                                 // 设置记录轨迹的点数据集
			m_Track.startTrack();                                                        // 开始记录轨迹
			
			clearLayers();
			m_Layers.add(dataset, true);
			MainActivity.m_EnableLocationService = true;
			m_MapControl.getMap().refresh();
			dismiss();
			
			MainActivity.locating();
		}
	}
	
	/**
	 * 清除除了底图以外的图层
	 */
	private void clearLayers() {
		int count = m_Layers.getCount();
		for(;count>1;){
		    m_Layers.remove(0);
		    count = m_Layers.getCount();
		}
	}
	
	/**
	 * 显示
	 */
   public void show(){
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
		m_etTrackName.setText(null);
	}
	
}