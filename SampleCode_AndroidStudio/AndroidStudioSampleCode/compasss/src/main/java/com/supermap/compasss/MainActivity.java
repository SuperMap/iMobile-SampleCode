package com.supermap.compasss;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:指南针示范代码（含有大、小指南针两种控件）
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
 * 1、范例简介：指南针示范代码（含有大、小指南针两种控件）
 * 2、示例数据：数据目录："/sdcard/SampleData/DOM/"
 *            地图数据：DOM.smwu,DOM.udb,DOM.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *
 *
 *   getSystemService()//获取手机服务
 *   SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)//加速度传感器
 *   SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)//磁力传感器
 *   SensorManager.registerListener()//注册手机服务监听，当不需要手机服务时，务必注销监听，否则该服务一直运行，耗电量大
 *
 * 4、使用步骤：
 * （1）打开应用是一个跟地图相关联的小指南针
 * （2）可点击小指南针，跳转到全屏指南针
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
public class MainActivity extends AppCompatActivity {
    String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    CompassView2 compassView2;
    LinearLayout layout;
    public static MapView mapView;
    public static Workspace workspace;
    public static MapControl mapControl;
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
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.setLicensePath(RootPath + "/SuperMap/License/");
        Environment.initialization(this);
        setContentView(R.layout.activity);
        initview();
    }
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
                    Manifest.permission.CHANGE_WIFI_STATE);
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
    private void initview() {
        compassView2=findViewById(R.id.compass);
        compassView2.setAzimuthChangeListener(new CompassView2.AzimuthChangeListener() {
            @Override
            public void getAzimuth(int Azimuth) {
                mapControl.getMap().setAngle((float)Azimuth);
                mapControl.getMap().refresh();
            }
        });
        layout=findViewById(R.id.aaa);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });

        mapView=findViewById(R.id.mapview);
        mapControl=mapView.getMapControl();
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setServer(RootPath+"/SampleData/DOM/DOM.smwu");
        info.setType(WorkspaceType.SMWU);
        workspace=new Workspace();
        workspace.open(info);
        mapControl.getMap().setWorkspace(workspace);
        String mapname=workspace.getMaps().get(0);
        mapControl.getMap().open(mapname);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        compassView2.unregisterCompassListener();
    }
}
