package com.supermap.imb.collectordemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.View;

import com.supermap.data.Point2D;
import com.supermap.data.Point3D;
import com.supermap.plugin.LocationManagePlugin;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.Calendar;

import static com.tencent.map.geolocation.TencentLocationRequest.REQUEST_LEVEL_POI;


/**
 * 腾讯定位工具
 * Created by Neshoir on 2018-07-20.
 */
public class TencentLocation implements TencentLocationListener
{
    private TencentLocationManager mLocationManager;
    private LocationManagePlugin.GPSData mGPSData = null;
    private Point2D mPoint = null;
    private LocationManagePlugin.GPSData collectGPSData = null;
    private Point3D mPoint3d = null;
    private MainActivity m_MainActivity = null;
    private boolean isReal = false;
    /**
     * 方位角
     */
    private float mAzimuth = 0;
    /**
     * 记录上一个方位角
     */
    private float mProAzimuth = 0;

    /**
     * 实例化
     *
     * @param mainActivity
     */
    public TencentLocation(MainActivity mainActivity)
    {
        m_MainActivity = mainActivity;
        mLocationManager = TencentLocationManager.getInstance(mainActivity);
        mLocationManager.removeUpdates(null);
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);

        mGPSData = new LocationManagePlugin.GPSData();
        mPoint = new Point2D();
        collectGPSData = new LocationManagePlugin.GPSData();
        mPoint3d = new Point3D();
        startLocation();
    }

    /**
     * 定位资源释放
     */
    public void dispose()
    {
        stopLocation(null);
    }

    /**
     * 停止更新定位
     *
     * @param view
     */
    private void stopLocation(View view)
    {
        mLocationManager.removeUpdates(this);

    }

    /**
     * 开始定位
     */
    private void startLocation()
    {
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(1000);//每隔十秒请求
        request.setRequestLevel(REQUEST_LEVEL_POI);
        request.setAllowCache(true);
        request.setAllowDirection(true);
        request.setAllowGPS(true);
        request.setIndoorLocationMode(true);
        //请求定位更新，及实时定位
        int errorNo = mLocationManager.requestLocationUpdates(request, this);
        Log.e("Tencent", String.valueOf(errorNo));
        if (errorNo == 2)
        {
            System.out.println("Key不正确，请重新配置！");
        }
    }

    /**
     * 位置变化
     *
     * @param location
     * @param error
     * @param reason
     */
    @Override
    public void onLocationChanged(com.tencent.map.geolocation.TencentLocation location, int error, String reason)
    {
        if (error == com.tencent.map.geolocation.TencentLocation.ERROR_OK)
        {
            //            Log.i("传感器方向:",String.valueOf(location.getDirection()));
            setLocation(location);// 定位成功
            //m_MainActivity.setLocationCallOut(new Point2D(location.getLongitude(), location.getLatitude()));
            //Log.e("Tencent", location.getAddress());
        }
    }

    /**
     * 获取位置点对象
     *
     * @return
     */
    public LocationManagePlugin.GPSData getLocation()
    {
        Calendar cal = Calendar.getInstance();
        mGPSData.lTime = cal.getTimeInMillis();
        mGPSData.nYear = cal.get(Calendar.YEAR);
        mGPSData.nMonth = cal.get(Calendar.MONTH);
        mGPSData.nDay = cal.get(Calendar.DAY_OF_MONTH);
        mGPSData.nHour = cal.get(Calendar.HOUR_OF_DAY);
        mGPSData.nMinute = cal.get(Calendar.MINUTE);
        mGPSData.nSecond = cal.get(Calendar.SECOND);
        return mGPSData;
    }

    /**
     * 获取定位点
     *
     * @return
     */
    public Point2D getGPSPoint()
    {
        return mPoint;
    }

    /**
     * 获取采集照片定位点
     *
     * @return
     */
    public LocationManagePlugin.GPSData getCollectorGPSPoint()
    {
        return collectGPSData;
    }

    public Point3D getGPSPoint3d()
    {
        return mPoint3d;
    }

    /**
     * 获取gps精度
     *
     * @return
     */
    public double getAccuracy()
    {
        return mGPSData.dAccuracy;
    }

    public double getBearing()
    {
        return mGPSData.dBearing;
    }

    /**
     * @param location
     */
    private void setLocation(com.tencent.map.geolocation.TencentLocation location)
    {
        mGPSData = new LocationManagePlugin.GPSData();
        mGPSData.dAltitude = location.getAltitude();
        mGPSData.dAccuracy = location.getAccuracy();
        mGPSData.dBearing = location.getDirection();
        mGPSData.dSpeed = location.getSpeed();
        mGPSData.lTime = location.getTime();
        Calendar cal = Calendar.getInstance();
        mGPSData.nYear = cal.get(Calendar.YEAR);
        mGPSData.nMonth = cal.get(Calendar.MONTH);
        mGPSData.nDay = cal.get(Calendar.DAY_OF_MONTH);
        mGPSData.nHour = cal.get(Calendar.HOUR_OF_DAY);
        mGPSData.nMinute = cal.get(Calendar.MINUTE);
        mGPSData.nSecond = cal.get(Calendar.SECOND);
        mPoint.setX(location.getLongitude());
        mPoint.setY(location.getLatitude());

        if(location.getLongitude() == 0 && location.getLatitude() == 0 //没有定位
			/*|| !m_MainActivity.getMap().getBounds().contains(m_Point)*/){//不在景区内定位了也不显示
            System.out.println("Unable to locate the position.");
        }
        else{
            m_MainActivity.mPoint2D.setX(location.getLongitude());
            m_MainActivity.mPoint2D.setY(location.getLatitude());
            m_MainActivity.mAccuracy = location.getAccuracy();
        }
        m_MainActivity.drawCircleOnDyn(m_MainActivity.mPoint2D, mAzimuth, m_MainActivity.mAccuracy);


    }



    /**
     * 定位服务状态更新
     *
     * @param arg0
     * @param arg1
     * @param arg2
     */
    @Override
    public void onStatusUpdate(String arg0, int arg1, String arg2)
    {
        //        Log.i("状态更新：", arg0);
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

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };
}
