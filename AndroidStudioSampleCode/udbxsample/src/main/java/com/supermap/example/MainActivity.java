package com.supermap.example;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:udbx示例代码
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
 * 1、范例简介：展示如何接入桌面配置数据中含有udbx数据源
 * 2、示例数据：数据目录："/sdcard/SampleData/HunanUDBX/"
 *            许可目录："/SuperMap/License/"
 *
 * 3、关键类型/成员:   EngineType.UDBX
 *
 * 4、依赖
 *      动态库需依赖imb、imb2d、autocad其中一个
 *      java需要依赖data、mapping.jar包
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
    private String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private MapControl mMapControl;
    private MapView mMapView;
    private Workspace mWorkspace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.setLicensePath(sdcard + "/SuperMap/license");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initMap();
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
            EasyPermissions.requestPermissions(this, "为了应用的正常使用，请允许以下权限。",
                    0, needPermissions);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }
    private void initMap(){
        mMapView=findViewById(R.id.mapview);
        mMapControl=mMapView.getMapControl();
        mWorkspace=new Workspace();
        mMapControl.getMap().setWorkspace(mWorkspace);
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();

        /*************************打开UDBX数据源示例***********************************/
/*        DatasourceConnectionInfo info=new DatasourceConnectionInfo();
        info.setServer(sdcard+"/SampleData/HunanUDBX/Hunan.udbx");
        info.setEngineType(EngineType.UDBX);
        mWorkspace.getDatasources().get(info);*/
        /*************************打开UDBX数据源示例***********************************/

        info.setServer(sdcard+"/SampleData/HunanUDBX/Hunan.smwu");
        info.setType(WorkspaceType.SMWU);
        if (mWorkspace.open(info)){
            String mapName = mWorkspace.getMaps().get(0);
            mMapControl.getMap().open(mapName);
        }else {
            Toast.makeText(this,"打开地图失败，请检查数据",Toast.LENGTH_SHORT).show();
        }
    }
}