package com.supermap.mapoverlay;

import android.Manifest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.MapViewGroup;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:MVT叠加矢量图层
 *
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
 * 1、范例简介：示范MVT叠加矢量图层
 * 2、示例数据：数据目录："/sdcard/SampleData/Beijing/"
 *            地图数据：Beijing.smwu,clip1.udb,clip1.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 EngineType.OpenGLCache 			//枚举
 *   MapViewGroup.addMapView()			//方法
 *
 * 4、使用步骤：
 *
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
    private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    MapView mapView;
    Workspace workspace;
    MapControl mapControl;

    MapView mapglview;
    MapControl mapglControl;
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
        //设置一些系统需要用到的路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);

        setContentView(R.layout.activity_main);


        //验证mapcontrol叠加显示地图


        mapglview = findViewById(R.id.mapglview);
        mapglControl = mapglview.getMapControl();
        Workspace glworkspace = new Workspace();
        mapglControl.getMap().setWorkspace(glworkspace);
        DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
        datasourceConnectionInfo.setServer("http://10.10.3.139:8090/iserver/services/map-changchun/rest/OpenGLTile");
        datasourceConnectionInfo.setEngineType(EngineType.OpenGLCache);
        Datasource dataSource2 = glworkspace.getDatasources().open(datasourceConnectionInfo);
        mapglControl.getMap().getLayers().add(dataSource2.getDatasets().get(0), true);
        mapglControl.getMap().setScale(1 / 288895.854936);
        mapglControl.getMap().refresh();

        mapView = findViewById(R.id.mapview);
        mapControl = mapView.getMapControl();
        workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard + "/SampleData/Beijing/beijing.smwu");
        info.setType(WorkspaceType.SMWU);
        workspace.open(info);
        mapControl.getMap().setWorkspace(workspace);
        String mapname = workspace.getMaps().get(1);
        mapControl.getMap().open(mapname);
        mapControl.getMap().setScale(1 / 288895.854936);
        Layer layer = mapControl.getMap().getLayers().get(0);
        LayerSettingVector layerSettingVector = (LayerSettingVector) layer.getAdditionalSetting();
        GeoStyle geoStyle = new GeoStyle();
        geoStyle.setFillOpaqueRate(0);
        layerSettingVector.setStyle(geoStyle);
        layer.setAdditionalSetting(layerSettingVector);


        MapViewGroup mapViewGroup = new MapViewGroup();
        mapViewGroup.addMapView(mapglview);
        mapViewGroup.addMapView(mapView);


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
}
