package com.supermap.imobile.visualization;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.imobile.fragment.GridAggregationFragment;
import com.supermap.imobile.fragment.HeatMapFragment;
import com.supermap.imobile.fragment.ThemeGraduatedFragment;
import com.supermap.imobile.fragment.ThemeDotDensityFragment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:示范专题图可视化
 * 点密度图，等级符号图，热力图，网格图
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
 * 1、范例简介：示范点密度图，等级符号图，热力图，网格图
 * 2、示例数据：数据目录："/sdcard/SampleData/ThematicMaps/"
 *            地图数据：ThematicMaps.smwu,ThematicMaps.udb,ThematicMaps.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 ThemeDotDensity.setDotExpression()  方法
 *   ThemeDotDensity.setDotExpression()  方法
 *   ThemeDotDensity.setValue()          方法
 *   ThemeDotDensity.setStyle()          方法
 *   ThemeGraduatedSymbol.setExpression()方法
 *   ThemeGraduatedSymbol.setBaseValue() 方法
 *   ThemeGraduatedSymbol.setGraduatedMode()方法
 *   ThemeGraduatedSymbol.setPositiveStyle()方法
 *   Layers.addHeatmap()                方法
 *   LayerHeatmap.setFuzzyDegree()      方法
 *   Layers.addGridAggregation()        方法
 *   LayerGridAggregation.setColorset() 方法
 *   LayerGridAggregation.setGridWidth()    方法
 *   LayerGridAggregation.setGridAggregationType()方法
 *   LayerGridAggregationType.HEXAGON   枚举
 *
 *
 * 4、使用步骤：
 * （1）右滑，选择等级符号图
 * （2）右滑，选择热力图
 * （3）右滑，选择网格图
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
public class  MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout layout_drawer;
    private ThemeDotDensityFragment mThemeDotDensityFragment; //点密度图
    private ThemeGraduatedFragment mThemeGraduatedFragment; //等级符号图
    private GridAggregationFragment mGridAggregationFragment;//网格图
    private HeatMapFragment mHeatMapFragment;//热力图

    //
//    private LinearLayout layout_tool;

    String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    MapView mMapView;
    public MapControl mMapControl;
    Workspace mWorkspace;
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
        Environment.setLicensePath(RootPath + "/Supermap/License/");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initView();
        openMap();

        try {
            mMapControl.getMap().save();
            mWorkspace.save();

        } catch (Exception e) {
            e.printStackTrace();
        }


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
    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("点密度图");
        setSupportActionBar(mToolbar);

        layout_drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, layout_drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        layout_drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMapView = findViewById(R.id.mapview);


//        layout_tool=findViewById(R.id.layout_tool);

        navigationView.setCheckedItem(R.id.dotmaps);//默认选中我的地图
//        showThemeDotDensityFragment();
    }


    /**
     * 打开数据
     */
    private void openMap() {
        mMapControl = mMapView.getMapControl();
        mWorkspace = new Workspace();
        mMapControl.getMap().setWorkspace(mWorkspace);

        String url = RootPath + "/sampledata/DataConversion/World.udb";
        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setAlias("OSM");
        info.setEngineType(EngineType.UDB);
        info.setServer(url);
//        info.setDriver("WMTS");

        Datasource datasource = mWorkspace.getDatasources().open(info);
        if(datasource != null){
            mMapControl.getMap().getLayers().add(datasource.getDatasets().get("dxfimport_3"),true);
        }
        mMapControl.getMap().viewEntire();
//        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
//        info.setServer(RootPath + "/sampledata/ThematicMaps/Thematicmaps.smwu");
//        info.setServer(RootPath + "/sampledata/DataConversion/World.smwu");
//        info.setType(WorkspaceType.SMWU);
//        if (mWorkspace.open(info)) {
//            mMapControl.getMap().setWorkspace(mWorkspace);
////            mMapControl.getMap().getLayers().add(mWorkspace.getDatasources().get(0).getDatasets().get("dxfimport_3"), true);
//            String mapname = mWorkspace.getMaps().get(1);
//            mMapControl.getMap().open(mapname);
//            mMapControl.getMap().viewEntire();
//        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout layout_drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout_drawer.isDrawerOpen(GravityCompat.START)) {
            layout_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    int flag = 0;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    /**
     * 显示侧滑栏
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dotmaps) {
            showThemeDotDensityFragment();
            mToolbar.setTitle("点密度图");
            flag = 1;
        } else if (id == R.id.graduatedmaps) {
            showThemeGraduatedDensityFragment();
            mToolbar.setTitle("等级符号图");
            flag = 2;
        } else if (id == R.id.heatmaps) {
            showHeatMapsFragment();
            mToolbar.setTitle("热力图");
            flag = 3;
        } else if (id == R.id.gridaggregation) {
            showGridAggregationFragment();
            mToolbar.setTitle("网格图");
            flag = 5;
        }

        layout_drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 显示我的数据采集
     */
    private void showGridAggregationFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mGridAggregationFragment == null) {
            mGridAggregationFragment = new GridAggregationFragment();
            fragmentTransaction.add(R.id.content_framelayout, mGridAggregationFragment, "GridAggregationFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mGridAggregationFragment)
                .commit();
    }

    /**
     * 显示我的地图
     */
    private void showThemeDotDensityFragment() {
//        layout_tool.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mThemeDotDensityFragment == null) {
            mThemeDotDensityFragment = new ThemeDotDensityFragment();
            fragmentTransaction.add(R.id.content_framelayout, mThemeDotDensityFragment, "ThemeDotDensityFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mThemeDotDensityFragment)
                .commit();
    }

    /**
     * 显示我的图层管理
     */
    private void showThemeGraduatedDensityFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mThemeGraduatedFragment == null) {
            mThemeGraduatedFragment = new ThemeGraduatedFragment();
            fragmentTransaction.add(R.id.content_framelayout, mThemeGraduatedFragment, "ThemeGraduatedFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mThemeGraduatedFragment)
                .commit();
    }

    /**
     * 显示我的洞察
     */
    private void showHeatMapsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mHeatMapFragment == null) {
            mHeatMapFragment = new HeatMapFragment();
            fragmentTransaction.add(R.id.content_framelayout, mHeatMapFragment, "HeatMapFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mHeatMapFragment)
                .commit();
    }

    /**
     * 显示隐藏所有与侧滑栏
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mThemeDotDensityFragment != null) {
            fragmentTransaction.hide(mThemeDotDensityFragment);
        }
        if (mThemeGraduatedFragment != null) {
            fragmentTransaction.hide(mThemeGraduatedFragment);
        }
        if (mGridAggregationFragment != null) {
            fragmentTransaction.hide(mGridAggregationFragment);
        }
        if (mHeatMapFragment != null) {
            fragmentTransaction.hide(mHeatMapFragment);
        }

    }


    /**
     * 退出应用
     */
    public void exit() {
        finish();
        Process.killProcess(Process.myPid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (layout_drawer.isDrawerOpen(GravityCompat.START)) {
            return super.onKeyDown(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否退出应用?");
            builder.setIcon(R.mipmap.user);
            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    //quit the application
                    try {
                        mMapControl.getMap().save();
                        mMapControl.getMap().getWorkspace().save();
                        exit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            builder.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
