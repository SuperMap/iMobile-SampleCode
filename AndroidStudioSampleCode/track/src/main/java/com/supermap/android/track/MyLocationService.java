package com.supermap.android.track;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.map.geolocation.TencentLocation;

public class MyLocationService extends Service{

	
	private GPSData  gpsData = new GPSData();
	private TencentLocation locationResult = null;
	private NotificationManager notificationManager;
	private String notificationId = "serviceid";
	private String notificationName = "servicename";
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

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//创建NotificationChannel
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			@SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
			notificationManager.createNotificationChannel(channel);
		}
		startForeground(1,getNotification());
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
//				startAlarm();
				initGpsData();
				MainActivity.setGpsData(gpsData);
				String  a = getApplicationContext().getFilesDir().getAbsolutePath();
				Toast.makeText(MyLocationService.this, a, Toast.LENGTH_SHORT).show();
				Log.e("+++++++++++++","+++++++++++++");
//				Toast.makeText(MyLocationService.this, "speed:"+gpsData.dSpeed+"---x:"+gpsData.dLatitude+"----y:"+gpsData.dLongitude, Toast.LENGTH_SHORT).show();
			}
			mHandler.postDelayed(mRunnable, 400);
		}
	};
	
	/**
	 * 初始化GPSData
	 */
	private void initGpsData(){
		locationResult = MainActivity.m_LocationTencent.getLocInfo();
		if(locationResult != null && gpsData != null){
			gpsData.dLatitude  = locationResult.getLatitude();
			gpsData.dLongitude = locationResult.getLongitude();
			gpsData.dAltitude  = locationResult.getAltitude();
			gpsData.dSpeed     = locationResult.getSpeed();
		}
	}

	private Notification getNotification() {
		Notification.Builder builder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.bg_white_roundcorner)
				.setContentTitle("title")
				.setContentText("text");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(notificationId);
		}
		Notification notification = builder.build();
		return notification;
	}
}