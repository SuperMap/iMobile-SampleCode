package com.supermap.imb.collectordemo;

import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.supermap.data.Point2D;
import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;

public class LocationIMB {
	
	// 
	private int m_reqGeoType = TencentMapLBSApi.GEO_TYPE_WGS84;
	private int m_reqLevel   = 0;
	private int m_reqDelay   = 1;
	// 
	private TencentMapLBSApiResult m_locationIfo = null;
	
	//
	private LocListener       m_Listener = null;
	
	//当前位置信息,用于导航和巡航
	private GPSData m_GPSData = null;
	private Point2D m_Point = new Point2D();
	private double mAccuracy = 0;
	private MainActivity m_MainActivity = null;
	
	//系统方位角，用于定位和巡航
	private SensorManager mSensorManager;
	private Sensor mSensorOrientation;
	
	/**
	 * 方位角
	 */
	private float mAzimuth = 0;
	/**
	 * 记录上一个方位角
	 */
	private float mProAzimuth = 0;
	
	
	/**
	 * 构造函数
	 * @param context
	 */
	public LocationIMB(MainActivity activity) {
		m_Listener = new LocListener(m_reqGeoType, m_reqLevel, m_reqDelay);
		
		int req = TencentMapLBSApi.getInstance().requestLocationUpdate(activity.getApplicationContext(), m_Listener);
		TencentMapLBSApi.getInstance().setGPSUpdateInterval(1000);
		if(req == -2)
			System.out.println("Key不正确，请在manifext文件中设置正确的Key");
		
		m_MainActivity = activity;
		
		mSensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
		mSensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		startSensor();
	}

	/**
	 * 获取位置信息
	 * @return
	 */
	public TencentMapLBSApiResult getLocationInfo() {
		return m_locationIfo;
	}
	
	/**
	 * 获取位置信息,使用GPSData封装
	 * @return
	 */
	public GPSData getGPSData(){
		return m_GPSData;
	}
	
	/**
	 * 获取当前位置
	 * @return
	 */
	public Point2D getGPSPoint(){
		return m_Point;
	}
	
	/**
	 * 获取当前定位精度
	 * @return
	 */
	public double getAccuracy(){
		return mAccuracy;
	}
	
	/**
	 * 关闭定位监听和方位角监听
	 * @return
	 */
	public void closeLocation(){
		TencentMapLBSApi.getInstance().removeLocationUpdate();
		stopSensor();
	}
	/**
	 * 位置信息监听类
	 *
	 */
	private class LocListener extends TencentMapLBSApiListener {

		// 初始化监听器
		public LocListener(int reqGeoType, int reqLevel, int reqDelay) {
			super(reqGeoType, reqLevel, reqDelay);
		}
		// 更新位置信息
		@Override
		public void onLocationUpdate(TencentMapLBSApiResult locResult) {
			
			m_locationIfo = locResult;
			
			//当前位置信息,用于导航和巡航
			m_GPSData = new GPSData();
			m_GPSData.dAltitude = locResult.Altitude;
			m_GPSData.dLongitude = locResult.Longitude;
			m_GPSData.dLatitude = locResult.Latitude;
			m_GPSData.dSpeed  = locResult.Speed;
			//m_GPSData.dBearing = locResult.Bearing;
			Calendar  ca = Calendar.getInstance();
			m_GPSData.nYear = ca.get(Calendar.YEAR);
			m_GPSData.nMonth = ca.get(Calendar.MONTH);
			m_GPSData.nDay = ca.get(Calendar.DATE);
			m_GPSData.nHour = ca.get(Calendar.HOUR);
			m_GPSData.nMinute = ca.get(Calendar.MINUTE);
			m_GPSData.nSecond = ca.get(Calendar.SECOND);
			
			mAccuracy = locResult.Accuracy;
			
			m_Point.setX(locResult.Longitude);
			m_Point.setY(locResult.Latitude);
			if(locResult.Longitude == 0 && locResult.Latitude == 0 //没有定位
			/*|| !m_MainActivity.getMap().getBounds().contains(m_Point)*/){//不在景区内定位了也不显示
				System.out.println("Unable to locate the position.");
			}
			else{
				m_MainActivity.mPoint2D.setX(locResult.Longitude);
				m_MainActivity.mPoint2D.setY(locResult.Latitude);
				m_MainActivity.mAccuracy = locResult.Accuracy;
			}
			m_MainActivity.drawCircleOnDyn(m_MainActivity.mPoint2D, mAzimuth, m_MainActivity.mAccuracy);
//			m_MainActivity.showInfo("get a new location.");
		}
	}
	
	/**
	 * 方向变化监听,手机方位角变化时会触发
	 */
	private SensorEventListener mSensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				mAzimuth = event.values[0];
				
				if (Math.abs(mAzimuth-mProAzimuth)<3) {
					return;
				}
				mProAzimuth = mAzimuth;
				//绘制变化后的当前方位角
				//m_MainActivity.drawCircleOnDyn(m_MainActivity.mCurrentPnt2D,mAzimuth);
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 获取当亲方位角
	 */
	public float getAzimuth(){
		return mAzimuth;
	}
	
	/**
	 * 开始监测方位变化
	 */
	public void startSensor(){
		if (mSensorManager != null && mSensorOrientation != null) {
			mSensorManager.registerListener(mSensorEventListener, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);
		}
	}

	/**
	 * 停止监测方位变化
	 */
	public void stopSensor(){
		if (mSensorManager != null && mSensorOrientation != null) {
			mSensorManager.unregisterListener(mSensorEventListener, mSensorOrientation);
		}
	}
}
