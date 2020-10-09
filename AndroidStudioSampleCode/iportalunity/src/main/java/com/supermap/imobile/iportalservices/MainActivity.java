package com.supermap.imobile.iportalservices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.supermap.data.Environment;
import com.supermap.imobile.bean.LogoutEvent;
import com.supermap.imobile.fragment.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/**
 * <p>
 * Title:云端一体化
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范对接protal
 * 2、登陆protal，查看protal资源
 * 3、关键类型/成员:
 *      IPortalService.addOnResponseListener 事件监听
 *      IPortalService.getIPortalServiceHost 方法
 *      IPortalService.login 方法
 *      IPortalService.logout 方法
 *      IPortalService.getMyMaps 方法
 *      IPortalService.getMyDatas 方法
 *      IPortalService.uploadData 方法
 *      IPortalService.downloadData 方法
 *      IPortalService.getMyServices 方法
 *      IPortalService.getMyScenes 方法
 *      IPortalService.getMyInsights 方法
 *
 * 4、使用步骤：
 *   (1)左拉，选择我的地图
 *   (2)展示protal我的地图资源
 *   (3)点击地图栏左上角按钮，修改地图资源
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
/**
 * 登录后进入的主界面
 * Actitivy中注册EventBus的时候要放在onCreate里面，注销要放在onDestory里面，其他都不行，收不到消息。
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private final String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

    private Toolbar toolbar = null;
    private DrawerLayout drawer = null;
    private NavigationView navigationView = null;
    private ImageButton naviUser = null;

    private MyMapsFragment mMyMapsFragment = null; //我的地图
    private MyDatasFragment mMyDatasFragment = null; //我的数据
    private MyServicesFragment mMyServicesFragment = null; //我的服务
    private MyScenesFragment mMyScenesFragment = null; //我的场景
    private MyInsightsFragment mMyInsightsFragment = null; //我的洞察
    private MyMapDashboardsFragment mMyMapDashboardsFragment = null; //我的大屏

    private MapsResourceFragment mMapsResourceFragment = null; //地图资源
    private ServicesResourceFragment mServicesResourceFragment = null; //服务资源
    private ScenesResourceFragment mScenesResourceFragment = null; //场景资源
    private DatasResourceFragment mDatasResourceFragment = null; //数据资源
    private InsightsResourceFragment mInsightsResourceFragment = null; //洞察资源
    private MapDashboardsResourceFragment mMapDashboardsResourceFragment = null;  //大屏资源

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        //设置一些系统需要用到的路径
        Environment.setLicensePath(SDCARD + "/SuperMap/license/");
        Environment.setOpenGLMode(true);
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的地图");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        naviUser = (ImageButton) headerView.findViewById(R.id.naviUser);
        naviUser.setOnClickListener(this);

        Toast.makeText(getApplication(), "登录成功", Toast.LENGTH_LONG).show();
        navigationView.setCheckedItem(R.id.my_map);//默认选中我的地图
        showMyMapsFragment();
    }

    private void initListener() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            //设置
//            startActivity(new Intent(this, SettingsActivity.class));
//            return true;
//        }

        switch (id) {
//            case R.id.browse:
//                break;
//            case R.id.action_settings:
//                //设置
//                startActivity(new Intent(this, SettingsActivity.class));
//                break;
//            case R.id.edit:
//                break;
//            case R.id.export:
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.naviUser:
                startActivity(new Intent(this, UserScrollingActivity.class));
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
//            case R.id.theme_cartography:
//                showThemeFragment();
//                toolbar.setTitle("专题制图");
//                break;
            case R.id.my_map:
                showMyMapsFragment();
                toolbar.setTitle("我的地图");
                break;
            case R.id.my_service:
                showMyServicesFragment();
                toolbar.setTitle("我的服务");
                break;
            case R.id.my_scene:
                showMyScenesFragment();
                toolbar.setTitle("我的场景");
                break;
            case R.id.my_data:
                showMyDatasFragment();
                toolbar.setTitle("我的数据");
                break;
            case R.id.my_Insight:
                showMyInsightsFragment();
                toolbar.setTitle("我的洞察");
                break;
            case R.id.my_large_screen:
                showMyMapDashboardsFragment();
                toolbar.setTitle("我的大屏");
                break;
            case R.id.maps:
                showMapsFragment();
                toolbar.setTitle("地图资源");
                break;
            case R.id.services:
                toolbar.setTitle("服务资源");
                showServicesFragment();
                break;
            case R.id.scenes:
                toolbar.setTitle("场景资源");
                showScenesFragment();
                break;
            case R.id.datas:
                toolbar.setTitle("数据资源");
                showDatasFragment();
                break;
            case R.id.insights:
                toolbar.setTitle("洞察资源");
                showInsightsFragment();
                break;
            case R.id.large_screens:
                toolbar.setTitle("大屏资源");
                showMapDashboardsFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMyMapsFragment() {
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

    private void showMyDatasFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyDatasFragment == null) {
            mMyDatasFragment = new MyDatasFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMyDatasFragment, "MyDatasFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyDatasFragment)
                .commit();
    }


    private void showMyServicesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyServicesFragment == null) {
            mMyServicesFragment = new MyServicesFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMyServicesFragment, "MyServicesFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyServicesFragment)
                .commit();
    }

    private void showMyScenesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyScenesFragment == null) {
            mMyScenesFragment = new MyScenesFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMyScenesFragment, "MyScenesFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyScenesFragment)
                .commit();
    }

    private void showMyInsightsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyInsightsFragment == null) {
            mMyInsightsFragment = new MyInsightsFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMyInsightsFragment, "MyInsightsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyInsightsFragment)
                .commit();
    }

    private void showMyMapDashboardsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMyMapDashboardsFragment == null) {
            mMyMapDashboardsFragment = new MyMapDashboardsFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMyMapDashboardsFragment, "MyMapDashboardsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMyMapDashboardsFragment)
                .commit();
    }

    private void showMapsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMapsResourceFragment == null) {
            mMapsResourceFragment = new MapsResourceFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMapsResourceFragment, "MapsResourceFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMapsResourceFragment)
                .commit();
    }

    private void showDatasFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mDatasResourceFragment == null) {
            mDatasResourceFragment = new DatasResourceFragment();
            fragmentTransaction.add(R.id.content_framelayout, mDatasResourceFragment, "DatasResourceFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mDatasResourceFragment)
                .commit();
    }

    private void showServicesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mServicesResourceFragment == null) {
            mServicesResourceFragment = new ServicesResourceFragment();
            fragmentTransaction.add(R.id.content_framelayout, mServicesResourceFragment, "ServicesResourceFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mServicesResourceFragment)
                .commit();
    }

    private void showScenesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mScenesResourceFragment == null) {
            mScenesResourceFragment = new ScenesResourceFragment();
            fragmentTransaction.add(R.id.content_framelayout, mScenesResourceFragment, "ScenesResourceFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mScenesResourceFragment)
                .commit();
    }

    private void showInsightsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mInsightsResourceFragment == null) {
            mInsightsResourceFragment = new InsightsResourceFragment();
            fragmentTransaction.add(R.id.content_framelayout, mInsightsResourceFragment, "InsightsResourceFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mInsightsResourceFragment)
                .commit();
    }

    private void showMapDashboardsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mMapDashboardsResourceFragment == null) {
            mMapDashboardsResourceFragment = new MapDashboardsResourceFragment();
            fragmentTransaction.add(R.id.content_framelayout, mMapDashboardsResourceFragment, "MyInsightsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mMapDashboardsResourceFragment)
                .commit();
    }


    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mMyMapsFragment != null) {
            fragmentTransaction.hide(mMyMapsFragment);
        }
        if (mMyDatasFragment != null) {
            fragmentTransaction.hide(mMyDatasFragment);
        }
        if (mMyServicesFragment != null) {
            fragmentTransaction.hide(mMyServicesFragment);
        }
        if (mMyScenesFragment != null) {
            fragmentTransaction.hide(mMyScenesFragment);
        }
        if (mMyInsightsFragment != null) {
            fragmentTransaction.hide(mMyInsightsFragment);
        }
        if (mMapDashboardsResourceFragment != null) {
            fragmentTransaction.hide(mMapDashboardsResourceFragment);
        }
        if (mMapsResourceFragment != null) {
            fragmentTransaction.hide(mMapsResourceFragment);
        }
        if (mDatasResourceFragment != null) {
            fragmentTransaction.hide(mDatasResourceFragment);
        }
        if (mServicesResourceFragment != null) {
            fragmentTransaction.hide(mServicesResourceFragment);
        }
        if (mScenesResourceFragment != null) {
            fragmentTransaction.hide(mScenesResourceFragment);
        }
        if (mInsightsResourceFragment != null) {
            fragmentTransaction.hide(mInsightsResourceFragment);
        }
        if (mMyMapDashboardsFragment != null) {
            fragmentTransaction.hide(mMyMapDashboardsFragment);
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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
                    exit();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent event) {
        if (!event.getMode().equals(TAG)) {
            return;
        }
        finish();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
