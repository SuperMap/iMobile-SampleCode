package com.supermap.android.track;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;

public class MyLocationService extends Service{

	
	private GPSData  gpsData = new GPSData();
	private TencentMapLBSApiResult locationResult = null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("onCreate");
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("onDestroy");
		mHandler.removeCallbacks(mRunnable);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		mHandler.postDelayed(mRunnable, 1000);
		System.out.println("onStartCommand"+ flags + "__"+ startId);
		return super.onStartCommand(intent, flags, startId);
	}

	Handler mHandler = new Handler();
	
	Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(MainActivity.m_EnableLocationService){
				initGpsData();
				MainActivity.setGpsData(gpsData);
			}
			mHandler.postDelayed(mRunnable, 400);
		}
	};
	
	/**
	 * 初始化GPSData
	 */
	private void initGpsData(){
		locationResult = MainActivity.m_LocationTencent.getLocationInfo();
		if(locationResult != null && gpsData != null){
			gpsData.dLatitude  = locationResult.Latitude;
			gpsData.dLongitude = locationResult.Longitude;
			gpsData.dAltitude  = locationResult.Altitude;
			gpsData.dSpeed     = locationResult.Speed;
		}
	}
}