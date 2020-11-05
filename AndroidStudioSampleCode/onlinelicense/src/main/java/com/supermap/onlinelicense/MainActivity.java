package com.supermap.onlinelicense;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.supermap.data.CloudLicenseManager;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.license.LicenseInfo;
import com.supermap.data.license.QueryFormalLicenseResponse;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:公有云许可示范代码
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
 * 1、范例简介：示范如何查询私有云许可模块，以及激活
 * 2、示例数据：数据目录："/sdcard/SampleData/Changhun"
 * 3、关键类型/成员:
 *   CloudLicenseManager.loginAccount 方法
 *   CloudLicenseManager.queryLicense 方法
 *   CloudLicenseManager.activeLicense 方法
 *   CloudLicenseManager.logoutAccount 方法
 *
 * 4、使用步骤：
 *  (1)点击"查询模块"按钮，查询许可模块，
 *  (2)如果查询成功，点击"选择模块"按钮，勾选需要激活的模块
 *  (3)点击"激活许可"按钮，进行许可激活
 *  (4)激活成功后，点击"打开地图"按钮，打开地图
 * 5、注意事项
 *   必须开启WIFI
 *   如果许可网络断开连接，许可将进行回收，无法使用移动端功能
 *   如果为正式许可，需要勾选"核心开发模块"、"核心运行模块"进行开发
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
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    private Workspace mWorkspace=null;
    private MapView mMapView=null;
    private MapControl mMapControl =null;

    CloudLicenseManager manager = null;
    String licenseid;
    String returnId;
    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initView();
        manager = CloudLicenseManager.getInstance(this);
        manager.setLoginCallback(licenseLoginCallback);
        manager.login("992275331@qq.com", "1995824lwh");
    }

    private void initView() {
        findViewById(R.id.btn_query).setOnClickListener(this);
        findViewById(R.id.btn_active).setOnClickListener(this);
        findViewById(R.id.btn_recycle).setOnClickListener(this);
        findViewById(R.id.btn_openMap).setOnClickListener(this);
    }

    CloudLicenseManager.LicenseLoginCallback licenseLoginCallback = new CloudLicenseManager.LicenseLoginCallback() {

        @Override
        public void loginAccount(boolean issuccess) {
            if (issuccess) {
                Log.i(TAG, "loginAccount: ");
            }
        }

        @Override
        public void logoutAccount(boolean b) {

        }

        @Override
        public void queryLicense(QueryFormalLicenseResponse queryFormalLicenseResponse) {
            if (queryFormalLicenseResponse!=null){
                int licenseCount =  queryFormalLicenseResponse.licenseCount;
                boolean formal =queryFormalLicenseResponse.formal;
                LicenseInfo[] licenseInfos=queryFormalLicenseResponse.licenses;
                for (int i=0;i<licenseInfos.length;i++){
                    String[] productType = licenseInfos[i].moduleNames;
                }
                licenseid=licenseInfos[0].id;
            }
            Log.i(TAG, "licenseid:"+licenseid);

        }

        @Override
        public void activeLicense(String returnid) {
            returnId=returnid;
            Log.i(TAG, "returnid:"+returnid);
        }

        @Override
        public void recycleLicense(int days) {
            Log.i(TAG, String.valueOf(days));
        }

        @Override
        public void otherError(String error) {
            Log.i(TAG, error);
        }
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

    private void openmap(){
        mMapView=findViewById(R.id.mapview);
        mMapControl =mMapView.getMapControl();
        mWorkspace=new Workspace();
        mMapControl.getMap().setWorkspace(mWorkspace);
        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setAlias("GOOGLE");
        info.setEngineType(EngineType.GoogleMaps);
        String url3 = "http://www.google.cn/maps";
        info.setServer(url3);
        Datasource datasourcegoogle = mWorkspace.getDatasources().open(info);
        mMapControl.getMap().getLayers().add(datasourcegoogle.getDatasets().get(0),false);
//        mMapControl.getMap().setCenter(new Point2D(12969338.6207241,4863846.35831212));
//        mMapControl.getMap().setScale(1/1791.49285043908);
        mMapControl.getMap().refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_query:

                manager.queryLicense();
                break;
            case  R.id.btn_active:
                manager.applyFormal(licenseid);
                break;
            case R.id.btn_recycle:
                manager.recycleLicense(licenseid,returnId);
                break;
            case R.id.btn_openMap:
                openmap();
                break;
        }
    }
}
