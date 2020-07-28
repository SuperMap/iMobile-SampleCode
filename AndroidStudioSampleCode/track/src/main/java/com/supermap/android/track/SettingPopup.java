package com.supermap.android.track;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.supermap.mapping.MapControl;
import com.supermap.track.Track;

public class SettingPopup extends PopupWindow implements OnClickListener {

	private MapControl      m_MapControl       = null;
	private Track           m_Track            = null;
	
	private LayoutInflater  m_LayoutInflater   = null;
	private View            m_ContentView      = null;
	private RadioGroup      m_rgTrackDistance  = null;
	private RadioGroup      m_rgLocationModule = null;
	
	private int   m_distanceId    = 0;
	private int   m_locationModuleId = 0;
	
	public SettingPopup (MapControl mapControl, Track track){
		m_LayoutInflater = LayoutInflater.from(mapControl.getContext());
		m_MapControl     = mapControl;
		m_Track          = track;
		
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		m_ContentView = m_LayoutInflater.inflate(R.layout.setting, null);
		setContentView(m_ContentView);
		
		((Button) m_ContentView.findViewById(R.id.btn_set_cancel)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_set_confirm)).setOnClickListener(this);
		
		m_rgTrackDistance = (RadioGroup) m_ContentView.findViewById(R.id.rg_TrackDistance);
		m_rgLocationModule = (RadioGroup) m_ContentView.findViewById(R.id.rg_LocationModule);
		
		m_rgTrackDistance.setOnCheckedChangeListener(onCheckedChangeListener);
		m_rgLocationModule.setOnCheckedChangeListener(onCheckedChangeListener);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btn_set_confirm:
			confirm();
			break;
		case R.id.btn_set_cancel:
			cancel();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 取消，并关闭窗口
	 */
	private void cancel (){
		dismiss();
	}
	
	/**
	 * 确认，并关闭窗口
	 */
	private void confirm() {
		switch(m_distanceId){
		case R.id.rb_3m:
			m_Track.setDistanceInterval(3);
			break;
		case R.id.rb_5m:
			m_Track.setDistanceInterval(5);
			break;
		case R.id.rb_10m:
			m_Track.setDistanceInterval(10);
			break;
		case R.id.rb_15m:
			m_Track.setDistanceInterval(15);
			break;
		default:
			break;
		}
		
		switch(m_locationModuleId){
		case R.id.rb_TencentLocation:
			MainActivity.m_EnableLocationService = true;
			m_Track.setCustomLocation(true);
			break;
		case R.id.rb_GPS:
			MainActivity.m_EnableLocationService = false;
			m_Track.setCustomLocation(false);
			break;
		default:
			break;
		}
		dismiss();
	}
	
	// CheckBox监听事件
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			int rgId = group.getId();
			switch(rgId){
			case R.id.rg_TrackDistance:
				m_distanceId = group.getCheckedRadioButtonId();
				break;
			case R.id.rg_LocationModule:
				m_locationModuleId = group.getCheckedRadioButtonId();
				break;
		    default:
		    	break;
			}
		}
	};
	
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
		
	}
}