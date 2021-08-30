package com.supermap.nonrecyclableopenmap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapView;

/**
 * <p>
 * Title:非回收式使用地图，提高效率，节省时间和内存开销
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
 * 1、范例简介：非回收式使用地图，提高效率，节省时间和内存开销
 *
 * 2、使用步骤：
 * （1）点击进入地图，进入地图Activity
 * （2）关闭地图，返回主Activity
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

import pub.devrel.easypermissions.EasyPermissions;

public class DynamicMainActivity extends Activity {

    private static MapView mMapView;
    private static Workspace mWorkspace;
    private static Dataset digitaDataset;
    private static Dataset satelliteDataset;
    private static Dataset roadDataset;
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


    public static Workspace getWorkspace() {
        if (mWorkspace == null) {
            mWorkspace = new Workspace();
        }
        return mWorkspace;
    }

    private Dataset getDigitaDataset() {
        if (digitaDataset == null) {
            DatasourceConnectionInfo mDigitaDatasourceConnectionInfo = new DatasourceConnectionInfo();
            mDigitaDatasourceConnectionInfo.setAlias("TianDiTu2");
            mDigitaDatasourceConnectionInfo.setEngineType(EngineType.OGC);
            mDigitaDatasourceConnectionInfo.setDriver("WMTS");
            String url = Contracts.DIGITAL_SOURCE_URL;
            mDigitaDatasourceConnectionInfo.setServer(url);
            Datasource datasource = mWorkspace.getDatasources().open(mDigitaDatasourceConnectionInfo);
            digitaDataset = datasource.getDatasets().get(0);
            mDigitaDatasourceConnectionInfo.dispose();
        }
        return digitaDataset;
    }

    private Dataset getSatelliteDataset() {
        if (satelliteDataset == null) {
            DatasourceConnectionInfo mStateDatasourceConnectionInfo = new DatasourceConnectionInfo();
            mStateDatasourceConnectionInfo.setAlias("TianDiTu1");
            mStateDatasourceConnectionInfo.setEngineType(EngineType.OGC);
            mStateDatasourceConnectionInfo.setDriver("WMTS");
            String url = Contracts.SATELLITE_SOURCE_URL;
            mStateDatasourceConnectionInfo.setServer(url);
            Datasource datasource = mWorkspace.getDatasources().open(mStateDatasourceConnectionInfo);
            satelliteDataset = datasource.getDatasets().get(0);
            mStateDatasourceConnectionInfo.dispose();
        }
        return satelliteDataset;
    }

    private Dataset getRoadDataset() {
        if (roadDataset == null) {
            DatasourceConnectionInfo mRoadDatasourceConnectionInfo = new DatasourceConnectionInfo();
            mRoadDatasourceConnectionInfo.setAlias("TianDiTu3");
            mRoadDatasourceConnectionInfo.setEngineType(EngineType.OGC);
            mRoadDatasourceConnectionInfo.setDriver("WMTS");
            String url = Contracts.ROAD_SOURCE_URL;
            mRoadDatasourceConnectionInfo.setServer(url);
            Datasource datasource = mWorkspace.getDatasources().open(mRoadDatasourceConnectionInfo);
            roadDataset = datasource.getDatasets().get(0);
            mRoadDatasourceConnectionInfo.dispose();
        }
        return roadDataset;
    }

    public static MapView getMapView(Activity mapActivity) {
        if (mMapView == null) {
            mMapView = new MapView(mapActivity);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            mMapView.setLayoutParams(lp);
        }
        return mMapView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

        String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        //组件功能必须在 Environment 初始化之后才能调用
        Environment.setLicensePath(RootPath + "/SuperMap/License/");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);

        initSM(DynamicMainActivity.this);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DynamicMainActivity.this, DynamicMapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSM(Activity mapActivity) {
        getMapView(mapActivity);
        getWorkspace();
        mMapView.getMapControl().getMap().setWorkspace(mWorkspace);
        getDigitaDataset();
        getSatelliteDataset();
        getRoadDataset();
        openMap();
    }

    protected void openMap() {

        //==客户的打开三个天地图图层
        addSatelliteLayer();
        addDigitaLayer();
        addRoadLayer();


    }

    private void addDigitaLayer() {
        mMapView.getMapControl().getMap().getLayers().add(getDigitaDataset(), true);
    }

    private void addSatelliteLayer() {
        mMapView.getMapControl().getMap().getLayers().add(getSatelliteDataset(), true);
    }

    private void addRoadLayer() {
        mMapView.getMapControl().getMap().getLayers().add(getRoadDataset(), true);
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//        Log.i("授权回调", "code:" + requestCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i("MMA.onPause==",System.currentTimeMillis()+"");
    }

}

