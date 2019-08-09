package com.supermap.android.track;

import android.content.Context;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;

public class LocationTencent {
	
	// 
	private int m_reqGeoType = TencentMapLBSApi.GEO_TYPE_WGS84;
	private int m_reqLevel   = 0;
	private int m_reqDelay   = 1;
	// 
	private TencentMapLBSApiResult m_locationIfo = null;
	
	//
	private LocListener       m_Listener = null;
	
	public LocationTencent(Context context) {
		
		m_Listener = new LocListener(m_reqGeoType, m_reqLevel, m_reqDelay);
		
		int req = TencentMapLBSApi.getInstance().requestLocationUpdate(context.getApplicationContext(), m_Listener);
		TencentMapLBSApi.getInstance().setGPSUpdateInterval(1000);
		//System.out.println("Tencent requestLocationUpdate return value is " + req);
		if(req == -2)
			System.out.println("Key不正确，请在manifext文件中设置正确的Key");
	}

	// 获取位置信息
	public TencentMapLBSApiResult getLocationInfo() {
		return m_locationIfo;
	}
	
	// 位置信息监听类
	private class LocListener extends TencentMapLBSApiListener {

		// 初始化监听器
		public LocListener(int reqGeoType, int reqLevel, int reqDelay) {
			super(reqGeoType, reqLevel, reqDelay);
			// TODO Auto-generated constructor stub
		}
		// 更新位置信息
		@Override
		public void onLocationUpdate(TencentMapLBSApiResult locResult) {
			
			m_locationIfo = locResult;
		}
		
	}
	
}
