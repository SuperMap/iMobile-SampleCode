package com.supermap.hpcollector;

/**
 * <p>
 * Title:高精采集
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   	展示高精采集新建采集，保存采集结果的功能。
 *
 * 2、Demo数据：
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    mGatherView.addNewRoute();			    方法
 *    mGatherView.saveCurrentRoute();	    	方法
 *    mGatherView.clearCurrentRoute();			方法
 *    mGatherView.loadRoute();			        方法
 *    mGatherView.saveAllRoutes();              方法
 *    mGatherView.clearAllRoutes();             方法
 *    mGatherView.setPointCloudEnable();        方法
 *    mGatherView.setViewMode();                方法
 *
 * 4、功能展示
 *   (1)点击【新建采集】，新建路线的采集；
 *   (2)点击【保存当前】，保存当前采集的路线，然后点击【新建采集】继续采集；
 *   (3)导航。
 *   (4)楼层切换
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.supermap.ar.gather.ARGatherView;
import com.supermap.ar.gather.ViewMode;
import com.supermap.data.Environment;
import com.supermap.data.Point3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();


    private ARGatherView mARGatherView;

    private ArrayList<Point3D> mPointList = new ArrayList<>(); //用来存储路线点集合
    private Map<Integer,ArrayList<Point3D>> mAllRoutes = new HashMap<Integer,ArrayList<Point3D>>();
    private boolean mFlagIsFirstPerson    = false; //是否为第一人称视角


    private Context mContext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Environment.setLicensePath("/sdcard/SuperMap/license/");
        Environment.initialization(this);

        requestPermissions();

        setContentView(R.layout.activity_main);
        mARGatherView = findViewById(R.id.gatherView);


        //更改相机画面尺寸
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400, 400);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(0,0,100,100);
        mARGatherView.setCameraViewLayoutParams(params);

        Button btnAddNewRoute       = findViewById(R.id.btnAddNewRoute);
        Button btnSaveCurrentRoute  = findViewById(R.id.btnSaveCurrentRoute);
        Button btnClearCurrentRoute = findViewById(R.id.btnClearCurrentRoute);

        Button btnSaveAllRoutes     = findViewById(R.id.btnSaveAllRoutes);
        Button btnClearAllRoutes    = findViewById(R.id.btnClearAllRoutes);

        Button btnEnableFeaturePoint = findViewById(R.id.btnEnableFeaturePoint);

        Button btnChangeViewMode    = findViewById(R.id.btnChangeViewMode);


        btnAddNewRoute.setOnClickListener(this);
        btnSaveCurrentRoute.setOnClickListener(this);
        btnClearCurrentRoute.setOnClickListener(this);

        btnSaveAllRoutes.setOnClickListener(this);
        btnClearAllRoutes.setOnClickListener(this);

        btnEnableFeaturePoint.setOnClickListener(this);
        btnChangeViewMode.setOnClickListener(this);


        showPosition();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddNewRoute:
                mARGatherView.startNewRoute();
                break;

            case R.id.btnSaveCurrentRoute:
                mPointList = mARGatherView.saveCurrentRoute(true);   // notice: saveCurrentRoute(ArrayList<Point3D> out)


                break;

            case R.id.btnClearCurrentRoute:
                mARGatherView.saveCurrentRoute(false);

                break;

            case R.id.btnSaveAllRoutes:
                mAllRoutes = mARGatherView.getAllRoutes();  //Map<Integer/*route index*/,ArrayList<Point3D>/*route elements.*/> getAllRoutes()

                break;

            case R.id.btnClearAllRoutes:
                mARGatherView.clearAllRoutes();
                break;

            case R.id.btnEnableFeaturePoint:
                mARGatherView.setFeaturePointVisible(mARGatherView.isFeaturePointVisible() == false?true:false);
                break;

            case R.id.btnChangeViewMode:
                if(!mFlagIsFirstPerson){
                    mARGatherView.setViewMode(ViewMode.FIRST_PERSON);
                    mFlagIsFirstPerson = true;
                }
                else{
                    mARGatherView.setViewMode(ViewMode.THIRD_PERSON);
                    mFlagIsFirstPerson = false;
                }
                break;
        }

    }


    //显示当前位置
    private void showPosition() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Point3D currentPositioni = mARGatherView.getCurrentPostion();
                        ((TextView)findViewById(R.id.txvShowPosition)).setText(
                                "x:  "+ String.format("%.8f",currentPositioni.getX())+ "\n"+
                                        "y:  "+ String.format("%.8f",currentPositioni.getY())+"\n"+
                                        "z:  "+ String.format("%.8f",currentPositioni.getZ())
                        );
                    }
                });
            }
        }, 0, 200);
    }



    @Override
    protected void onResume() {
        super.onResume();

        mARGatherView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

        mARGatherView.onPause();

    }


    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };


    //申请动态权限
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (!checkPermissions(needPermissions)) {
            EasyPermissions.requestPermissions(
                    this,
                    "为了应用的正常使用，请允许以下权限。",
                    0,
                    needPermissions);
        } else {
        }
    }
    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(mContext, permissions);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        StringBuffer sb = new StringBuffer();
        for (String str : perms){
            sb.append(str);
            sb.append("\n");
        }
        sb.replace(sb.length() - 2,sb.length(),"");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
        }
    }



}
