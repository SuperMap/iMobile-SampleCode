package com.supermap.navidemo.navi;

import android.content.Context;
import android.util.Log;

import com.supermap.data.Point2D;
import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.Calendar;
import java.util.Date;

public class TencentLocTool implements TencentLocationListener {

    private static final String TAG = "TencentLocTool";
    TencentLocationManager mLocationManager;

    private TencentLocation m_locationInfo = null;

    //当前位置信息,用于导航和巡航
    private GPSData m_GPSData = null;
    private Point2D m_Point = new Point2D(0, 0);
    private double mAccuracy = 0;

    private float mBearing = 0;

    public TencentLocation getLocInfo() {
        return m_locationInfo;
    }

    private static TencentLocTool tencentLocTool = null;

    private TencentLocTool() {
    }

    public static TencentLocTool getInstance() {
        if (tencentLocTool == null) {
            tencentLocTool = new TencentLocTool();
        }
        return tencentLocTool;
    }

    private Date startDate = null;//开始监听

    //毫秒
    public int calLastedTime(Date startDate) {
        long a = new Date().getTime();
        long b = startDate.getTime();
//        int c = (int)((a - b) / 1000);//秒
        int c = (int) (a - b);//毫秒
        return c;
    }

    public void init(Context context) {
        Log.e(TAG, "TencentLocTool init!");
        mLocationManager = TencentLocationManager.getInstance(context);

        int error = TencentLocationManager.getInstance(context)
                .requestLocationUpdates(
                        TencentLocationRequest
                                .create().setInterval(1000)
                                .setRequestLevel(
                                        TencentLocationRequest.REQUEST_LEVEL_NAME), this);
        if (error == 0) {
            startDate = new Date();
            Log.e(TAG, "监听状态:" + "监听成功!");
        } else if (error == 1) {
            Log.e(TAG, "监听状态:" + "设备缺少使用腾讯定位SDK需要的基本条件");
        } else if (error == 2) {
            Log.e(TAG, "监听状态:" + "配置的 key 不正确");
        } else {
            Log.e(TAG, "++++++++++++");
        }
    }

    /**
     * @param location 新的位置
     * @param error    错误码
     * @param reason   错误描述
     */
    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        if (TencentLocation.ERROR_OK == error) {
            m_locationInfo = location;
            //当前位置信息,用于导航和巡航
            m_GPSData = new GPSData();
            m_GPSData.dAltitude = location.getAltitude();
            m_GPSData.dLongitude = location.getLongitude() - 0.0060880056233;
            m_GPSData.dLatitude = location.getLatitude() - 0.00100216102211;
            m_GPSData.dSpeed = location.getSpeed();
//			m_GPSData.dBearing = locResult.Bearing;
            mBearing = location.getBearing();
            Calendar ca = Calendar.getInstance();
            m_GPSData.nYear = ca.get(Calendar.YEAR);
            m_GPSData.nMonth = ca.get(Calendar.MONTH);
            m_GPSData.nDay = ca.get(Calendar.DATE);
            m_GPSData.nHour = ca.get(Calendar.HOUR);
            m_GPSData.nMinute = ca.get(Calendar.MINUTE);
            m_GPSData.nSecond = ca.get(Calendar.SECOND);

            mAccuracy = location.getAccuracy();
            m_Point.setX(location.getLongitude());
            m_Point.setY(location.getLatitude());


            if (calLastedTime(startDate) >= 5000) {
                //每2秒更新一次位置
                locateMyPosition();
                startDate = new Date();//重置时间
            }

//            Log.d(TAG, "onLocationChanged--" + "Longitude:" + location.getLongitude() + ", Latitude:" + location.getLatitude());
        } else {
            Log.e(TAG, "reason: " + reason);
            Log.e(TAG, "error: " + error + "");
        }

    }

    //定位到当前位置
    private void locateMyPosition() {
        Point2D point2D = m_Point;

        boolean contains = false;
        //中国版图范围
        if (point2D.getX() >= 73.0 && point2D.getX() <= 135.0 && point2D.getY() >= 4.0 && point2D.getY() <= 53.0) {
            contains = true;
        } else {
            contains = false;
        }

        if (contains && point2D.getX() != 0 && point2D.getY() != 0) {
            if (this.listener != null) {
                listener.locationChangedListener(point2D, mAccuracy, mBearing);
            }
        }
    }

    /**
     * @param name   GPS，Wi-Fi等
     * @param status 新的状态, 启用或禁用
     * @param desc   状态描述
     */
    @Override
    public void onStatusUpdate(String name, int status, String desc) {
//        Log.d(TAG , "name: " + name + ", status: " + status + ", desc: " + desc);
    }

    public void destroyLocManager() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
        mLocationManager = null;
    }

    public TencentLocationManager getTencentLocationManager() {
        return mLocationManager;
    }

    /**
     * 获取位置信息,使用GPSData封装
     *
     * @return
     */
    public GPSData getGPSData() {
        return m_GPSData;
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public Point2D getGPSPoint() {
        return m_Point;
    }

    /**
     * 获取当前定位精度
     *
     * @return
     */
    public double getAccuracy() {
        return mAccuracy;
    }

    /**
     * 获取方位角
     *
     * @return
     */
    public float getBearing() {
        return mBearing;
    }

    public interface LocationChangedListener {
        void locationChangedListener(Point2D point2D, double accuracy, float bearing);
    }

    private LocationChangedListener listener = null;

    public void setLocationManager(LocationChangedListener listener) {
        this.listener = listener;
    }
}
