package com.supermap.compasss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 小指南针
 */
public class CompassView2 extends RelativeLayout {

    private Bitmap mCompassBitmap;
    private SensorManager mSensorManager;
    private Sensor aSensor;
    private Sensor mSensor;
    private ImageView mCompassImageView;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] r=new float[9];
    private float[] I = new float[9];
    private float[] values=new float[3];
    private float currentDegree = 0f;
    private float oldDegree = 0;
    private int mAzimuthSpace =1;
    private AzimuthChangeListener azimuthChangeListener;

    public CompassView2(Context context) {
        super(context);
        init(context);
    }


    public CompassView2(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }
    public CompassView2(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        init(context);
    }


    private void init(Context context) {
        mCompassBitmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_c22);
        mCompassImageView=new ImageView(context);
        mCompassImageView.setImageBitmap(mCompassBitmap);
        mSensorManager= (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        aSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(listener,aSensor,SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(listener,mSensor,SensorManager.SENSOR_DELAY_UI);
        this.addView(mCompassImageView);
    }

    private final static double PI = Math.PI;

    private final static double TWO_PI = PI*2;

    private double mod(double a, double b){

        return a % b;

    }

    final SensorEventListener listener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            final float alpha = 0.95f;

            synchronized (this) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                            * event.values[0];
                    mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                            * event.values[1];
                    mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                            * event.values[2];
                }


                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                            * event.values[0];
                    mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                            * event.values[1];
                    mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                            * event.values[2];
                }

                boolean success = SensorManager.getRotationMatrix(r, I, mGravity,
                        mGeomagnetic);
                if (success) {
                    SensorManager.getOrientation(r, values);
                }
                calulateOrientation();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void calulateOrientation(){
        values[0]=(float)Math.toDegrees(values[0]);
        RotateAnimation rotateAnimation = new RotateAnimation(currentDegree,values[0], Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(700);
        currentDegree = values[0];
        mCompassImageView.setAnimation(rotateAnimation);
        if (Math.abs(values[0]-oldDegree) < mAzimuthSpace )
            return;
        oldDegree = values[0];
        azimuthChangeListener.getAzimuth((int)currentDegree);

    }

    public void setAzimuthChangeListener(AzimuthChangeListener listener){
        this.azimuthChangeListener=listener;
    }
    public interface AzimuthChangeListener{
        void getAzimuth(int Azimuth);
    }

    public void unregisterCompassListener(){
        mSensorManager.unregisterListener(listener);
    }
}
