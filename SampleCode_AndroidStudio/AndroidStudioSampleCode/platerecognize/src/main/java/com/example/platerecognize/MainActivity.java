package com.example.platerecognize;

import android.annotation.SuppressLint;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import pr.hyperlpr.util.DeepAssetUtil;
import pr.hyperlpr.util.DeepCarUtil;

import com.supermap.data.Environment;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {


    private CameraBridgeViewBase m_OpenCvCameraView = null;
    private static Mat m_Rgba;
    public static long m_handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        Environment.initialization(this);

        setContentView(R.layout.activity_main);
    }

    //车牌智能识别
    public void buttonPlate_Click(View view){

        new MyAsyncTask(this).execute();
        if(OpenCVLoader.initDebug()){
            System.loadLibrary("opencv_java3");
            System.loadLibrary("hyperlpr");
            m_handle = DeepAssetUtil.initRecognizer(MainActivity.this);
        }else{

        }
        m_OpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.javaCameraView);
        m_OpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        m_OpenCvCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener() {
            @Override
            public void onCameraViewStarted(int i, int i1) {

            }
            @Override
            public void onCameraViewStopped() {

            }
            @Override
            public Mat onCameraFrame(Mat mat) {
                m_Rgba = mat;
                new MyAsyncTask(MainActivity.this).execute();
                return m_Rgba;
            }
        });
        m_OpenCvCameraView.enableView();
        m_Rgba = new Mat(m_OpenCvCameraView.getHeight(), m_OpenCvCameraView.getWidth(), CvType.CV_8UC4);
    }
   // 防止内存泄漏，静态加弱引用
    @SuppressLint("StaticFieldLeak")
    private static class MyAsyncTask extends AsyncTask<String, Integer, String> {

        private final WeakReference<MainActivity> weakActivity;
        MyAsyncTask(MainActivity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                return  DeepCarUtil.SimpleRecognization(m_Rgba.getNativeObjAddr(), m_handle);
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String license) {//license为车牌识别结果
            super.onPostExecute(license);
            MainActivity activity = weakActivity.get();
            if (activity == null
                    || activity.isFinishing()
                    || activity.isDestroyed()) {
                return;
            }
            Toast.makeText(activity,license ,Toast.LENGTH_SHORT).show();
        }
    }
}
