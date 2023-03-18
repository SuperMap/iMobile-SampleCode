package com.supermap.imobile.streamingapp;

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
import android.view.*;
import android.widget.ImageButton;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.imobile.fragment.FirstFragment;
import com.supermap.imobile.fragment.SecondFragment;
import com.supermap.imobile.ui.CtrlCompass;

/**
 * 登录后进入的主界面
 * <p>
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

    private SecondFragment mSecondFragment = null;
    private FirstFragment mFirstFragment = null;

    private CtrlCompass mCtrlCompass; //指南针
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置一些系统需要用到的路径
        Environment.setLicensePath(SDCARD + "/SuperMap/license/");
        Environment.setOpenGLMode(true);
        Environment.setWebCacheDirectory(SDCARD+"/GoogleMapCache");
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);

        //Android全屏
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 仅当缺口区域完全包含在状态栏之中时，才允许窗口延伸到刘海区域显示
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            // 永远不允许窗口延伸到刘海区域
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            // 始终允许窗口延伸到屏幕短边上的刘海区域
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        naviUser = (ImageButton) headerView.findViewById(R.id.naviUser);
        naviUser.setOnClickListener(this);

        navigationView.setCheckedItem(R.id.my_service);
        toolbar.setTitle("流处理模型");
//        showFirstFragment();
        showSecondFragment();

        mCtrlCompass = findViewById(R.id.ctrlCompass);
        mCtrlCompass.start();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //设置
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.naviUser:
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.my_map:
//                showFirstFragment();
//                toolbar.setTitle("流处理模型");
                mSecondFragment.searchAction();
                break;
            case R.id.my_service:
                showSecondFragment();
                mSecondFragment.clearAll();
//                toolbar.setTitle("流数据服务");
                break;
            case R.id.my_car:
                mSecondFragment.connectDataflow();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void showFirstFragment() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        if (mFirstFragment == null) {
//            mFirstFragment = new FirstFragment();
//        }
//        fragmentTransaction
//                .replace(R.id.content_framelayout, mFirstFragment, "FirstFragment")
//                .commit();
//    }
//
//    private void showSecondFragment() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        if (mSecondFragment == null) {
//            mSecondFragment = new SecondFragment();
//        }
//        fragmentTransaction
//                .replace(R.id.content_framelayout, mSecondFragment, "SecondFragment")
//                .commit();
//    }

    private void showFirstFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mFirstFragment == null) {
            mFirstFragment = new FirstFragment();
            fragmentTransaction.add(R.id.content_framelayout, mFirstFragment, "FirstFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mFirstFragment)
                .commit();
    }

    private void showSecondFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mSecondFragment == null) {
            mSecondFragment = new SecondFragment();
            fragmentTransaction.add(R.id.content_framelayout, mSecondFragment, "MyDatasFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(mSecondFragment)
                .commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (mFirstFragment != null) {
            fragmentTransaction.hide(mFirstFragment);
        }
        if (mSecondFragment != null) {
            fragmentTransaction.hide(mSecondFragment);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCtrlCompass != null)
            mCtrlCompass.unregisterCompassListener();
    }



}
