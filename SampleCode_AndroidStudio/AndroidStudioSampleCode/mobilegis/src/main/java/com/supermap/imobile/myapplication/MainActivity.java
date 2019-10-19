package com.supermap.imobile.myapplication;

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

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.imobile.fragment.MyDataAnalystFragment;
import com.supermap.imobile.fragment.MyDatasCollectorFragment;
import com.supermap.imobile.fragment.MyInsightFragment;
import com.supermap.imobile.fragment.MyLayerManageFragment;
import com.supermap.imobile.fragment.MyMapsFragment;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 登录后进入的主界面
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout layout_drawer;
    private MyMapsFragment mMyMapsFragment; //我的地图
    private MyLayerManageFragment mMyLayerManageFragment; //我的数据
    private MyDatasCollectorFragment mMyDatasCollectorFragment;//我的采集
    private MyDataAnalystFragment mMyDataAnalystFragment;//我的分析
    private MyInsightFragment mMyInsightFragment;//我的洞察

    //
//    private LinearLayout layout_tool;

    String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    MapView mMapView;
    MapControl mMapControl;
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
        Environment.setLicensePath(RootPath + "/Mobile GIS/License/");
        Environment.initialization(this);
        Environment.setOpenGLMode(true);
        setContentView(R.layout.activity_main);
        initView();
        openMap();
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
        mToolbar.setTitle("我的地图");
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

        navigationView.setCheckedItem(R.id.my_map);//默认选中我的地图
        showMyMapsFragment();
    }


    /**
     * 打开数据
     */
    private void openMap() {
        mMapControl = mMapView.getMapControl();
        mWorkspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(RootPath + "/Mobile GIS/Data/Changchun.sxwu");
        info.setType(WorkspaceType.SXWU);
        if (mWorkspace.open(info)) {
            mMapControl.getMap().setWorkspace(mWorkspace);
            String mapname = mWorkspace.getMaps().get(1);
            mMapControl.getMap().open(mapname);
//            mMapControl.getMap().viewEntire();
        }

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

        if (id == R.id.my_map) {
            showMyMapsFragment();

            mToolbar.setTitle("我的地图");
            flag = 1;
        } else if (id == R.id.my_layermanage) {
            showMyLayerManageFragment();
            mToolbar.setTitle("我的图层");
            flag = 2;
        } else if (id == R.id.my_datacollection) {
            showMyDataCollectorFragment();
            mToolbar.setTitle("我的采集");
            flag = 3;
        } else if (id == R.id.my_Insight) {
            showMyInsightFragment();
            mToolbar.setTitle("我的洞察");
            flag = 5;
        } else if (id == R.id.my_dataanalyst) {
            showMyDataAnalystFragment();
            mToolbar.setTitle("我的分析");
            flag = 4;
        }

        layout_drawer.closeDrawer(GravityCompat.START);
        fragmentChangeListener.getFragmentFlag(flag);
        return true;
    }


    /**
     * 侧滑栏点击事件
     */
    FragmentChangeListener fragmentChangeListener = new FragmentChangeListener() {
        @Override
        public void getFragmentFlag(int i) {
            switch (i) {
                case 1:
                    mMapControl.setAction(Action.PAN);

                    break;
                case 2:
                    mMapControl.getMap().getTrackingLayer().clear();
                    mMapControl.getMap().getMapView().removeAllCallOut();
                    if (mMyInsightFragment != null) {
                        mMyInsightFragment.clear();
                    }
                    if (mMyDataAnalystFragment != null) {
                        mMyDataAnalystFragment.clear();
                    }
                    mMapControl.setAction(Action.PAN);
                    break;
                case 3:
                    mMapControl.getMap().getTrackingLayer().clear();
                    mMapControl.getMap().getMapView().removeAllCallOut();
                    if (mMyInsightFragment != null) {
                        mMyInsightFragment.clear();
                    }
                    if (mMyDataAnalystFragment != null) {
                        mMyDataAnalystFragment.clear();
                    }
                    mMapControl.setAction(Action.PAN);
                    break;
                case 4:
                    mMapControl.getMap().getTrackingLayer().clear();
                    mMapControl.getMap().getMapView().removeAllCallOut();
                    if (mMyInsightFragment != null) {
                        mMyInsightFragment.clear();
                    }
                    mMapControl.setAction(Action.PAN);
                    break;
                case 5:
                    mMapControl.getMap().getTrackingLayer().clear();
                    mMapControl.getMap().getMapView().removeAllCallOut();

                    if (mMyDataAnalystFragment != null) {
                        mMyDataAnalystFragment.clear();
                    }
                    mMapControl.setAction(Action.PAN);
                    break;
            }
        }
    };

    /**
     * 显示我的数据分析
     */
    private void showMyDataAnalystFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyDataAnalystFragment == null) {
            mMyDataAnalystFragment = new MyDataAnalystFragment(mMapControl);
            fragmentTransaction.add(R.id.content_framelayout, mMyDataAnalystFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyDataAnalystFragment)
                .commit();
    }

    /**
     * 显示我的数据采集
     */
    private void showMyDataCollectorFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyDatasCollectorFragment == null) {
            mMyDatasCollectorFragment = new MyDatasCollectorFragment(mMapControl);
            fragmentTransaction.add(R.id.content_framelayout, mMyDatasCollectorFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyDatasCollectorFragment)
                .commit();
    }

    /**
     * 显示我的地图
     */
    private void showMyMapsFragment() {
//        layout_tool.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyMapsFragment == null) {
            mMyMapsFragment = new MyMapsFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMyMapsFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyMapsFragment)
                .commit();
    }

    /**
     * 显示我的图层管理
     */
    private void showMyLayerManageFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyLayerManageFragment == null) {
            mMyLayerManageFragment = new MyLayerManageFragment(mMapControl);
            fragmentTransaction.add(R.id.content_framelayout, mMyLayerManageFragment, "MyDatasFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyLayerManageFragment)
                .commit();
    }

    /**
     * 显示我的洞察
     */
    private void showMyInsightFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyInsightFragment == null) {
            mMyInsightFragment = new MyInsightFragment(mMapControl);
            fragmentTransaction.add(R.id.content_framelayout, mMyInsightFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyInsightFragment)
                .commit();
    }

    /**
     * 显示隐藏所有与侧滑栏
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mMyMapsFragment != null) {
            fragmentTransaction.hide(mMyMapsFragment);
        }
        if (mMyLayerManageFragment != null) {
            fragmentTransaction.hide(mMyLayerManageFragment);
        }
        if (mMyDatasCollectorFragment != null) {
            fragmentTransaction.hide(mMyDatasCollectorFragment);
        }
        if (mMyDataAnalystFragment != null) {
            fragmentTransaction.hide(mMyDataAnalystFragment);
        }
        if (mMyInsightFragment != null) {
            fragmentTransaction.hide(mMyInsightFragment);
        }

    }

    //fragment切换时监听
    public interface FragmentChangeListener {
        public void getFragmentFlag(int i);
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
