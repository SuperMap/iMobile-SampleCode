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
 * 		数据目录："../SampleData/IndoorNavigationData/"
 *      地图数据："beijing.smwu", "beijing0525.udb", "bounds.udb", "kaide_mall.udb"
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    m_NavigationEx.setStartPoint();			方法
 *    m_NavigationEx.setDestinationPoint();		方法
 *    m_NavigationEx.addWayPoint();				方法
 *    m_NavigationEx.routeAnalyst();			方法
 *    m_NavigationEx.startGuide();				方法
 *    m_floorListView.setCurrentFloorId();		方法
 *
 * 4、功能展示
 *   (1)添加起点、终点、途径点；
 *   (2)路径分析；
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

import com.supermap.ar.gather.GatherView;
import com.supermap.ar.gather.ViewMode;
import com.supermap.data.Environment;
import com.supermap.data.Point3D;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CAMERA,
    };
    /**
     * 检测权限
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */

    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    /**
     * 申请动态权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (!checkPermissions(needPermissions)) {
            EasyPermissions.requestPermissions(
                    this,
                    "为了应用的正常使用，请允许以下权限。",
                    0,

                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CAMERA);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private GatherView mGatherView;

    private ArrayList<Point3D> mPointList = new ArrayList<>(); //用来存储路线点集合
    private boolean mFirstPerson = false; //是否为第一人称视角

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions() ;
        //设置许可文件路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");

        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);

        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {

        mGatherView = findViewById(R.id.gatherView);

        //更改相机画面尺寸
//  mGatherView.setCameraViewLayoutParams(new ViewGroup.LayoutParams(500,500));

        Button btnAddNewRoute       = findViewById(R.id.btnAddNewRoute);
        Button btnSaveCurrentRoute  = findViewById(R.id.btnSaveCurrentRoute);
        Button btnClearCurrentRoute = findViewById(R.id.btnClearCurrentRoute);

        Button btnSaveAllRoutes     = findViewById(R.id.btnSaveAllRoutes);
        Button btnClearAllRoutes    = findViewById(R.id.btnClearAllRoutes);
        Button btnLoadRoute         = findViewById(R.id.btnLoadRoute);

        Button btnCEnablePointCloud = findViewById(R.id.btnCEnablePointCloud);

        Button btnChangeViewMode    = findViewById(R.id.btnChangeViewMode);

        btnAddNewRoute.setOnClickListener(this);
        btnSaveCurrentRoute.setOnClickListener(this);
        btnClearCurrentRoute.setOnClickListener(this);

        btnSaveAllRoutes.setOnClickListener(this);
        btnClearAllRoutes.setOnClickListener(this);
        btnLoadRoute.setOnClickListener(this);

        btnCEnablePointCloud.setOnClickListener(this);
        btnChangeViewMode.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddNewRoute:
                mGatherView.addNewRoute();
                break;

            case R.id.btnSaveCurrentRoute:
                mGatherView.saveCurrentRoute(null);   // notice: saveCurrentRoute(ArrayList<Point3D> out)
                break;

            case R.id.btnClearCurrentRoute:
                mGatherView.clearCurrentRoute();
                break;

            case R.id.btnLoadRoute:
                mGatherView.loadRoute(mPointList);    // notice: loadRoutes(ArrayList<Point3D> in)
                break;

            case R.id.btnSaveAllRoutes:
                mPointList.clear();                    //先清除一下
                mGatherView.saveAllRoutes(mPointList); // notice: saveAllRoutes(ArrayList<Point3D> out)
                break;

            case R.id.btnClearAllRoutes:
                mGatherView.clearAllRoutes();
                break;

            case R.id.btnCEnablePointCloud:
                mGatherView.setPointCloudEnable(mGatherView.isPointCloudEnable() == false?true:false);
                break;

            case R.id.btnChangeViewMode:
                if(!mFirstPerson){
                    mGatherView.setViewMode(ViewMode.FIRST_PERSON);
                    mFirstPerson = true;
                }
                else{
                    mGatherView.setViewMode(ViewMode.THIRD_PERSON);
                    mFirstPerson = false;
                }
                break;
        }
    }


}
