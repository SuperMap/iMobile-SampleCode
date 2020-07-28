package com.supermap.compasss;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    CompassView compassView;
    private SensorManager mSensorManager;
    LocationManager locationManager;
    private SensorEventListener mSensorEventListener;
    private float val;
    private float currentDegree = 0f;
    private float oldDegree = 0;
    private int mAzimuthSpace = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        compassView = (CompassView) findViewById(R.id.compassView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                currentDegree = event.values[0];
                if (Math.abs(currentDegree - oldDegree) < mAzimuthSpace)
                    return;
                oldDegree = currentDegree;
                compassView.setVal(currentDegree);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        initLocation();

    }

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(true);//包含高度信息
        criteria.setBearingRequired(true);//包含方位信息
        criteria.setSpeedRequired(true);//包含速度信息
        criteria.setCostAllowed(false);//允许付费
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);//高耗电
        locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }
    LocationListener locationListener =new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double Latitude=location.getLatitude();
            double Longitude=location.getLongitude();
            compassView.setLatLong(Latitude,Longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
