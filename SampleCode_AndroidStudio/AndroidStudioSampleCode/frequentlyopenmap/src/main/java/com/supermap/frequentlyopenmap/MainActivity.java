package com.supermap.frequentlyopenmap;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * <p>
 * Title:动态添加地图控件，打开地图，关闭地图及释放控件
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
 * 1、范例简介：动态添加地图控件，打开地图，关闭地图及释放控件
 * 2、示例数据：数据目录："/sdcard/SampleData/City/"
 *            地图数据：Changchun.smwu,Changchun.udb,Changchun.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *
 *   View.addView();//添加视图
 *
 * 4、使用步骤：
 * （1）点击打开地图，动态添加view并打开地图
 * （2）关闭地图，同时移除view
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
    MapView mapView;
    MapControl mapControl;
    Workspace workspace;
    private static final String TAG = "MainActivity";
    RelativeLayout layout;
    LinearLayout linearLayout;
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

        initView();



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
    /**
     * 初始化控件
     */
    private void initView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout = new RelativeLayout(this);
        layout.setLayoutParams(params);
        setContentView(layout);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(-1, -2);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params1);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(-1, -2);
        final Button btnOpenMap = new Button(this);
        btnOpenMap.setLayoutParams(params2);
        btnOpenMap.setText("打开地图");


        final Button btnCloseMap = new Button(this);
        btnCloseMap.setLayoutParams(params2);
        btnCloseMap.setText("关闭地图");
        btnCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWorkspace();
            }
        });
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });



        linearLayout.addView(btnCloseMap);
        linearLayout.addView(btnOpenMap);
        layout.addView(linearLayout);


    }
    public MapView initMapView(Context context) {
        // 初始化MapControl
        MapView mMapView = new MapView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(lp);


        return mMapView;
    }
    protected void openMap(){
        if (mapView==null) {
            mapView = initMapView(this);
            linearLayout.addView(mapView);

            mapControl = mapView.getMapControl();
            WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
            info.setType(WorkspaceType.SMWU);
            info.setServer(RootPath + "/SampleData/City/Changchun.smwu");
            workspace = new Workspace();
            workspace.open(info);
            mapControl.getMap().setWorkspace(workspace);
            String mapname = workspace.getMaps().get(0);
            mapControl.getMap().open(mapname);
        }
        else {
            Toast.makeText(this,"已经打开地图",Toast.LENGTH_SHORT).show();
        }

    }
    protected void closeWorkspace(){

        if (mapControl==null&workspace==null){
            Toast.makeText(this,"请先打开地图",Toast.LENGTH_SHORT).show();
        }
        else {
        mapControl.getMap().close();
        if (workspace.getDatasources().getCount() > 0) {
            workspace.close();

            workspace.dispose();
            workspace=null;
        }
        linearLayout.removeView(mapView);
        mapView=null;
        if (mapControl!=null){
            mapControl.dispose();
            mapControl=null;
        }
    }
    }
}

