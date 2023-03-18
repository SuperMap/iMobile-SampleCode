package com.example.swipedemo;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.supermap.data.DatasetType;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String RootPath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/";
    MapView mapView;
    MapControl mapControl;
    Workspace workspace;
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
        Environment.setLicensePath(RootPath+"SuperMap/License/");
//        Environment.setOpenGLMode(true);
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initData();
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
    private void initData(){

        mapView=findViewById(R.id.mapview);
        mapControl=mapView.getMapControl();
        workspace=new Workspace();
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SMWU);
        info.setServer(RootPath+"SampleData/Hunan/Hunan.smwu");
        workspace.open(info);
        mapControl.getMap().setWorkspace(workspace);
        String mapname=workspace.getMaps().get(0);
        mapControl.getMap().open(mapname);
        mapControl.setAction(Action.SWIPE);
        int count=mapControl.getMap().getLayers().getCount();
        for (int i=0;i<count;i++){
            if (mapControl.getMap().getLayers().get(i).getTheme()!=null){
                mapControl.getMap().getLayers().get(i).setVisible(false);
            }
            else {
                if (mapControl.getMap().getLayers().get(i).getDataset().getType()== DatasetType.POINT){
                    mapControl.getMap().getLayers().get(i).setVisible(false);
                }
                else {
                    if (!mapControl.getMap().getLayers().get(i).getIsSwipe()){
                        mapControl.getMap().getLayers().get(i).setIsSwipe(true);
                    }
                }
            }

        }

        findViewById(R.id.openSwipe).setOnClickListener(this);
        findViewById(R.id.CloseSwipe).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.openSwipe:
                mapControl.setAction(Action.SWIPE);
                break;
            case R.id.CloseSwipe:
                mapControl.setAction(Action.PAN);
                break;

        }
    }
}
