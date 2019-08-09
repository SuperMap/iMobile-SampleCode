package com.supermap.compasss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
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
    private float[] accelerometerValues =new float[3];
    private float[] magneticFieldValues =new float[3];
    private float[] r=new float[9];
    private float[] values=new float[3];
    private float currentDegree = 0f;
    private float oldDegree = 0;
    private int mAzimuthSpace =3;
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

//    @Override
//    protected void onDraw(Canvas canvas)
//    {
//        super.onDraw(canvas);
//        Paint paint = new Paint();
//        canvas.drawBitmap(mCompassBitmap,10,10,paint);
//    }

    final SensorEventListener listener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                accelerometerValues=event.values;
            }
            if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                magneticFieldValues=event.values;
            }
           boolean isget= SensorManager.getRotationMatrix(r,null,accelerometerValues,magneticFieldValues);
            if (isget){
                SensorManager.getOrientation(r,values);
            }
            calulateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

//    @Override
//    protected void onDraw(Canvas canvas) {
//
//
//        canvas.save();
//        canvas.rotate(currentDegree, getWidth() / 2, getHeight() / 2);// 绕图片中心点旋转，
//        canvas.drawBitmap(mCompassBitmap);
//        canvas.restore();// 保存一下
//    }


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
/*    public double getAzimuthSpace(){
        return this.currentDegree;
    }*/
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
