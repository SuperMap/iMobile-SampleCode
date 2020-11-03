package com.supermap.imobile.naviintegration;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.supermap.data.Color;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.imobile.fragment.NaviFragment;
import com.supermap.imobile.fragment.NetworkBuildFragment;
import com.supermap.indoor.FloorListView;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation2;
import com.supermap.navi.Navigation3;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:室内外增量一体化导航示范代码
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
 * 1、范例简介：示范增量路网以及室内外一体化导航。
 * 2、示例数据：导航数据目录："/sdcard/SampleData/SupermapNaviData/"
 *          地图数据：supermapindoor.smwu, bounds.udb, bounds.udd,supermap.udb, supermap.udd,
 IT产业园.udb, IT产业园.udd, NetworkModel.snm
 *          许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *		TopologyProcessing.clean()			  方法
 *		NetworkBuilder.buildNetwork()		  方法
 *      Navigation2.setNetWorkDataset();      方法
 *      Navigation2.loadModel();              方法
 *	    Navigation2.setStartPoint();          方法
 *      Navigation2.setDestinationPoint();    方法
 *      Navigation2.isGuiding();              方法
 *      Navigation2.cleanPath();              方法
 *      Navigation2.startGuide();             方法
 *      Navigation2.routeAnalyst();           方法
 *		Navigation3.setStartPoint();         方法
 *      Navigation3.setDestinationPoint();   方法
 *      Navigation3.isGuiding();             方法
 *      Navigation3.cleanPath();             方法
 *      Navigation3.startGuide();            方法
 *      Navigation3.routeAnalyst();          方法
 *		FloorListView.setCurrentFloorId();	  方法
 *
 * 4、使用步骤：
 *	增量路网：
 *	(1)点击【编辑路网】按钮，在室内7楼手绘一条路网
 *	(2)点击【提交】按钮，提交手绘线对象
 *	(3)点击【自动生成】按钮，可生成室内7楼的增量路网
 *  右滑界面，点击【室内外导航】选项，打开导航界面
 *  室内外导航：
 *  (1)点击【起点-室内】按钮，在地图上长按一点设置起点
 *  (2)点击【终点-室外】按钮，在地图上长按另一点设置终点
 *  (3)点击【分析】按钮，进行路径分析，显示导航路径
 *  (4)路径分析结束后，若点击【第一人称】按钮，将进行第一人称导航，并在地图上显示引导过程
 *  (5)路径分析结束后，若点击【第三人称】按钮，将进行第三人称导航，并在地图上显示引导过程
 *  (7)导航进行中，若点击【停止导航】，可以停止导航
 *  (8)点击【清除】按钮，可以清除现有路径结果，再重新分析路径
 *
 * 5、注意：
 *	如果缺少语音播报，原因是缺少语音资源。
 *  解决办法：请将产品包中Resource文件夹下的voice文件夹拷贝到工程目录中的assets文件夹下。
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

    private DrawerLayout layout_drawer;
    private NaviFragment mNaviFragment; //导航
    private NetworkBuildFragment mNetworkBuildFragment; //增量路网

    private Datasource mDatasource;

    String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private MapView mMapView;
    private MapControl mMapControl;
    private Workspace mWorkspace;
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
    private Navigation3 mNavigationEx;
    private FloorListView mFloorListView;
    private Navigation2 mNavigation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.initialization(this);
        setContentView(R.layout.main_activity);
        initView();
        initMap();
        initFloorListView();
        initNavigation2();

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
        layout_drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.buildnetwork);//增量路网
        showNetworkBuildFragment();
    }
    /**
     * 初始化地图
     */
    private void initMap() {

        mMapView = findViewById(R.id.view_MapView);
        mMapControl = mMapView.getMapControl();
        mWorkspace=new Workspace();
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setServer(RootPath+"/SampleData/SupermapNaviData/supermapindoor.smwu");
        info.setType(WorkspaceType.SMWU);
        mWorkspace.open(info);
        mMapControl.getMap().setWorkspace(mWorkspace);
        String mapname=mWorkspace.getMaps().get(0);
        mMapControl.getMap().open(mapname);
        mMapControl.getMap().refresh();
        mDatasource = mWorkspace.getDatasources().get("supermap");

        //删除增量的线
        DatasetVector dataset = (DatasetVector) mDatasource.getDatasets().get("T1_ROAD_INFO");
        Recordset set = dataset.query("SMID > 60", CursorType.DYNAMIC);
        set.deleteAll();
        set.update();
        set.dispose();
    }

    /**
     * 初始化楼梯
     */
    private void initFloorListView() {

        mNavigationEx = mMapControl.getNavigation3();

        mFloorListView = (FloorListView)findViewById(R.id.floor_list_view);
        mFloorListView.linkMapControl(mMapControl);
        mFloorListView.setVisibility(View.VISIBLE);
        //设置引导路径样式，非当前楼层的路径虚线表示
        GeoStyle style = new GeoStyle();
        if (Environment.isOpenGLMode()) {
            style.setLineSymbolID(964882);
        } else {
            style.setLineSymbolID(964883);
        }
        mNavigationEx.setRouteStyle(style);    //设置当前图层引导路径样式
        GeoStyle styleHint = new GeoStyle();
        styleHint.setLineWidth(2);
        styleHint.setLineColor(new com.supermap.data.Color(82, 198, 223));
        styleHint.setLineSymbolID(2);
        mNavigationEx.setHintRouteStyle(styleHint);//设置其他楼层引导路径的样式

        mNavigationEx.addNaviInfoListener(new NaviListener() {
            @Override
            public void onNaviInfoUpdate(NaviInfo naviInfo) {

            }

            @Override
            public void onStartNavi() {

            }

            @Override
            public void onAarrivedDestination() {

                if(mNaviFragment.bStartIndoor && mNaviFragment.bEndIndoor){
                    // /起终点室内
                    mNavigation2.cleanPath();
                    mNavigationEx.cleanPath();
                    mMapView.removeAllCallOut();
                    mMapControl.getMap().getTrackingLayer().clear();
                    mNaviFragment.layoutTools.setVisibility(View.VISIBLE);
                }else if(mNaviFragment.bStartIndoor){//起点室内，终点室外
                    mNaviFragment.startOutdoorNavi();
                }else if(mNaviFragment.bEndIndoor) {//终点室内，起点室外
                    mNavigation2.cleanPath();
                    mNavigationEx.cleanPath();
                    mMapView.removeAllCallOut();
                    mMapControl.getMap().getTrackingLayer().clear();
                    mNaviFragment.layoutTools.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStopNavi() {
                mNavigation2.cleanPath();
                mNavigationEx.cleanPath();
                mMapView.removeAllCallOut();
                mMapControl.getMap().getTrackingLayer().clear();
                mNaviFragment.layoutTools.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdjustFailure() {

            }

            @Override
            public void onPlayNaviMessage(String s) {

            }
        });
    }

    /**
     * 行业导航初始化
     */
    private void initNavigation2() {

        Datasource datasource = mWorkspace.getDatasources().get("IT产业园");
        if(datasource == null){
            return;
        }
        Datasets datasets = datasource.getDatasets();
        DatasetVector networkDataset = (DatasetVector)datasets.get("BuildNetwork"); //Road_Network

        mNavigation2 = mMapControl.getNavigation2();      // 获取行业导航控件，只能通过此方法初始化m_Navigation2
        mNavigation2.setPathVisible(true);
        mNavigation2.setRoadInfoVisibie(true);
        mNavigation2.setRoadInfoPosition(10, 100);
//	          设置道路信息显示栏的位置。
        mNavigation2.setRoadInfoSize(320, 50);// 设置分析所得路径可见
        mNavigation2.setNetworkDataset(networkDataset);    // 设置网络数据集
        boolean bResult = mNavigation2.loadModel("sdcard/sampleData/SupermapNaviData/NetworkModel.snm");

        mNavigation2.setIsEncryptGPS(false);
        mNavigation2.enablePanOnGuide(true);
        GeoStyle naviPathStyle=new GeoStyle() ;
        naviPathStyle.setLineSymbolID(964526);
        naviPathStyle.setLineColor(new Color(68, 180, 251));
        naviPathStyle.setLineWidth(2);
        mNavigation2.setRouteStyle(naviPathStyle);

        mNavigation2.addNaviInfoListener(new NaviListener() {

            @Override
            public void onStopNavi() {
                // TODO Auto-generated method stub
                mNavigation2.cleanPath();
                mNavigationEx.cleanPath();
                mMapView.removeAllCallOut();
                mMapControl.getMap().getTrackingLayer().clear();
                mNaviFragment.layoutTools.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartNavi() {

            }

            @Override
            public void onNaviInfoUpdate(NaviInfo arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAarrivedDestination() {
                // TODO Auto-generated method stub

                if(!mNaviFragment.bStartIndoor && !mNaviFragment.bEndIndoor){
                    // /起终点室外
                    mNavigation2.cleanPath();
                    mNavigationEx.cleanPath();
                    mMapView.removeAllCallOut();
                    mMapControl.getMap().getTrackingLayer().clear();
                    mNaviFragment.layoutTools.setVisibility(View.VISIBLE);
                }else if(mNaviFragment.bStartIndoor){//起点室内，终点室外
                    mNavigation2.cleanPath();
                    mNavigationEx.cleanPath();
                    mMapView.removeAllCallOut();
                    mMapControl.getMap().getTrackingLayer().clear();
                    mNaviFragment.layoutTools.setVisibility(View.VISIBLE);
                }else if(mNaviFragment.bEndIndoor) {//终点室内，起点室外
                    mNaviFragment.startIndoorNavi();
                }
            }

            @Override
            public void onAdjustFailure() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPlayNaviMessage(String arg0) {
                // TODO Auto-generated method stub

            }
        });


    }
    public Datasource getDatasource() {
        return mDatasource;
    }

    public MapControl getMapControl() {
        return mMapControl;
    }
    public MapView getMapView() {
        return mMapView;
    }
    public Workspace getWorkspace() {
        return mWorkspace;
    }

    public Navigation3 getNavigationEx(){
        return mNavigationEx;
    }
    public Navigation2 getNavigation2(){
        return mNavigation2;
    }

    public FloorListView getFloorListView() {
        return mFloorListView;
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

    @Override
    /**
     * 显示侧滑栏
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.buildnetwork) {
            showNetworkBuildFragment();
        } else if (id == R.id.navi) {
            showNaviFragment();
        }

        layout_drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 显示导航
     */
    private void showNaviFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mNaviFragment == null) {
            mNaviFragment = new NaviFragment();
            fragmentTransaction.add(R.id.ly_function_content, mNaviFragment, "NaviFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mNaviFragment)
                .commit();
    }

    /**
     * 显示我的图层管理
     */
    private void showNetworkBuildFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mNetworkBuildFragment == null) {
            mNetworkBuildFragment = new NetworkBuildFragment();
            fragmentTransaction.add(R.id.ly_function_content, mNetworkBuildFragment, "NetworkBuildFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mNetworkBuildFragment)
                .commit();
    }


    /**
     * 显示隐藏所有与侧滑栏
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mNaviFragment != null) {
            fragmentTransaction.hide(mNaviFragment);
        }
        if (mNetworkBuildFragment != null) {
            fragmentTransaction.hide(mNetworkBuildFragment);
        }


    }

    /**
     * 显示Callout
     *
     * @param point
     * @param pointName
     * @param idDrawable
     */
    public void showPointByCallout(Point2D point, final String pointName,
                                    final int idDrawable) {
        CallOut callOut = new CallOut(this);
        callOut.setStyle(CalloutAlignment.BOTTOM);
        callOut.setCustomize(true);
        callOut.setLocation(point.getX(), point.getY());
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(idDrawable);
        callOut.setContentView(imageView);
        mMapView.addCallout(callOut, pointName);
    }

    /**
     * 显示室外室内点的连接线
     * @param startPoint
     * @param endPoint
     */
    public void showNoNaviLine(Point2D startPoint,Point2D endPoint){

        Point2D[] point2d = new Point2D[2];
        point2d[0] = startPoint;//
        point2d[1] = endPoint;//new Point2D(116.497997488111,39.9832110293281)

        Point2Ds point2Ds = new Point2Ds(point2d);
        GeoLine geoLine = new GeoLine(point2Ds);

        GeoStyle geoStyle = new GeoStyle();
        geoStyle.setLineSymbolID(964866);
        geoLine.setStyle(geoStyle);

        mMapControl.getMap().getTrackingLayer().add(geoLine,"line");
        mMapControl.getMap().refresh();
    }
}
