package com.supermap.ar;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.AR.ARMode;
import com.supermap.mapping.AR.ArControl2;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapLoadedListener;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:AR模式示范代码
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范如何运用多种AR模式进行地图浏览。
 * 2、示例数据：数据目录："/sdcard/SampleData/AR/supermapindoor.smwu""
 *            地图数据：supermapindoor.smwu, supermap.udb, supermap.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *       ArControl2.beginAR();//开始AR地图模式
 *       ArControl2.setARState();//设置是否开启AR的外部扩展
 *       ArControl2.setDatasetName(); //设置AR地图的POI数据集名称
 *       ArControl2.setTileName(); //设置AR地图的POI文字Title名称
 *       ArControl2.setRecordset();//设置AR地图的POI记录集
 *       ArControl2.setARMode();//设置AR地图的模式
 *       ArControl2.showMapView();//设置是否显示地图
 *       ArControl2.setARCenter();//设置AR地图的中心点
 *       mArControl2.showCamera();//显示摄像头
 *       mArControl2.hideCamera();//隐藏摄像头
 *
 * 4、使用步骤：
 *      (1)点击临近模式，可查看附近的POI信息。
 *      (2)点击跟随模式，以上帝视角查看地图。
 *      (3)点击普通地图，进行普通地图模式下的浏览。
 *      (4)点击无限屏模式，移动手机时，地图自动平移。
 *      (5)点击开启摄像，开启摄像头。
 *      (6)点击关闭摄像，关闭摄像头。
 *      (7)点击开启地图，打开当前地图。
 *      (8)点击关闭地图，关闭当前地图。
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends FragmentActivity  implements OnClickListener {
    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath() + "/";

    private ArControl2 mArControl2;
    private MapControl mMapcontrol = null;


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
            Manifest.permission.CAMERA
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        requestPermissions() ;
        //设置一些系统需要用到的路径
        Environment.setLicensePath(SDCARD + "SuperMap/license/");
        Environment.initialization(this);

        setContentView(R.layout.activity_main);

        initView();
        initMap();
    }

    private void initView() {
        findViewById(R.id.btn_AR_Near).setOnClickListener(this);
        findViewById(R.id.btn_AR_Following).setOnClickListener(this);
        findViewById(R.id.btn_map_mode).setOnClickListener(this);
        findViewById(R.id.btn_unlimitedmap_mode).setOnClickListener(this);
        findViewById(R.id.btn_show_carema).setOnClickListener(this);
        findViewById(R.id.btn_hide_carema).setOnClickListener(this);
        findViewById(R.id.btn_open_mapview).setOnClickListener(this);
        findViewById(R.id.btn_hide_mapview).setOnClickListener(this);
        //滑动条
        SeekBar seekBar = (SeekBar) findViewById(R.id.progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                if (mArControl2 != null && mArControl2.mapControl != null) {
                    mArControl2.mapControl.getMap().setSlantAngle((double) progress);
                    mArControl2.mapControl.getMap().refresh();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //当前进度
        seekBar.setProgress(40);

    }

    private class TestMapLoadedListener implements MapLoadedListener {
        @Override
        public void onMapLoaded() {
            mMapcontrol.getMap().zoom(6.0);
        }
    }

    private void initMap() {
        //打开工作空间
        Workspace m_workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        String strAR_POI_DatasetName = null;
        String strAR_POI_TitleName = null;
        {
            strAR_POI_DatasetName = "T7_REGION_INFO"; //AR地图的POI数据集名称
            strAR_POI_TitleName = "FT_NAME_CN"; //AR地图的POI文字Title名称
            info.setServer(SDCARD + "SampleData/AR/supermapindoor.smwu");

        }
        info.setType(WorkspaceType.SMWU);

        if (m_workspace.open(info)) {
            mArControl2 = (ArControl2) findViewById(R.id.test_arcontrol);
            mMapcontrol = mArControl2.mapControl;
            mMapcontrol.getMap().setWorkspace(m_workspace);
            String mapName = m_workspace.getMaps().get(0);
            mMapcontrol.getMap().open(mapName);
            if (!mMapcontrol.getMap().IsArmap()) {
                mMapcontrol.getMap().setIsArmap(true);
            }
            mMapcontrol.getMap().viewEntire();
            mMapcontrol.getMap().setMapLoadedListener(new TestMapLoadedListener());
            mArControl2.beginAR();
            mArControl2.setARState(true);
            mArControl2.setDatasetName(strAR_POI_DatasetName); //AR地图的POI数据集名称
            mArControl2.setTileName(strAR_POI_TitleName); //AR地图的POI文字Title名称
            {
                Datasource dtSource = m_workspace.getDatasources().get(0);
                if (dtSource != null) {
                    Dataset datasetDLTB = dtSource.getDatasets().get(strAR_POI_DatasetName); //"T7_REGION_INFO"
                    if (datasetDLTB != null) {
                        DatasetVector plDatasetVector = (DatasetVector) datasetDLTB;

                        Recordset mRecordsetAR = plDatasetVector.getRecordset(false, CursorType.STATIC);

                        mArControl2.setRecordset(mRecordsetAR);
                    }
                }
            }

            mArControl2.setARMode(ARMode.AR_NORMAL);
            mArControl2.hideCamera();

        } else {
            System.out.println("Open Workspace failed");
        }

        mMapcontrol.getMap().refresh();
    }

    private ArrayList<Button> mArrBtnDestTmp = new ArrayList<Button>();
    private ArrayList<Button> mArrBtnDest_DownTmp = new ArrayList<Button>();

    public void drawCircleOnTrackingLayer(com.supermap.data.Point2D point2D, float azimuth, float pitch, float roll) {
        if (point2D == null) {
            return;
        }
        for (int n = 0; n < mArrBtnDestTmp.size(); n++) {
            mArControl2.removeView(mArrBtnDestTmp.get(n));
            mArControl2.removeView(mArrBtnDest_DownTmp.get(n));
        }

        mArControl2.setARCenter(point2D);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_AR_Near:
                Toast.makeText(MainActivity.this, "AR临近模式", Toast.LENGTH_SHORT).show();
                mArControl2.setARMode(ARMode.AR_NEARING);

            break;
            case R.id.btn_AR_Following:
                Toast.makeText(MainActivity.this, "AR跟随模式", Toast.LENGTH_SHORT).show();
                mArControl2.setARMode(ARMode.AR_FOLLOWING);

            break;
            case R.id.btn_map_mode://find a  POI
                Toast.makeText(MainActivity.this, "普通地图模式", Toast.LENGTH_SHORT).show();
                mArControl2.setARMode(ARMode.AR_NORMAL);

                break;
            case R.id.btn_unlimitedmap_mode://unlimited Pan the map
                Toast.makeText(MainActivity.this, "无限屏AR模式", Toast.LENGTH_SHORT).show();
                mArControl2.setARMode(ARMode.AR_INFINITE);
                break;

            case R.id.btn_show_carema:
                Toast.makeText(MainActivity.this, "打开AR摄像头", Toast.LENGTH_SHORT).show();
                mArControl2.showCamera();
                mMapcontrol.enableRotateTouch(true);

                break;
            case R.id.btn_hide_carema:
                Toast.makeText(MainActivity.this, "隐藏AR摄像头", Toast.LENGTH_SHORT).show();
                mArControl2.hideCamera();
                mMapcontrol.enableRotateTouch(true);

                break;
            //开启地图
            case R.id.btn_open_mapview:
                mArControl2.showMapView(true);
                break;
            //隐藏地图
            case R.id.btn_hide_mapview:
                mArControl2.showMapView(false);
                break;
        }

        mMapcontrol.getMap().refresh();
    }
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
}
