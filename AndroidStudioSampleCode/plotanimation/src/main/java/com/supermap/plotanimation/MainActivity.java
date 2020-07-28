package com.supermap.plotanimation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.supermap.Fragment.CreatAnimationFragment;
import com.supermap.Fragment.MyMapFragment;
import com.supermap.Fragment.ReadAnimationFragment;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.plot.AnimationManager;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:示范动态标绘
 * 添加标号，添加动画，动画播放，态势推演
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
 * 1、范例简介：示范动态标绘
 * 2、示例数据：数据目录："/sdcard/SampleData/Fujian/"
 *            地图数据：TourLine.smwu,Fujian.udb,Fujian.udd，TourLineFile.xml
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 AnimationManager.pause(); 				//方法
 *   MapControl.setAnimations()			    //方法
 *   AnimationManager.getAnimationFromXML()	//方法
 *   AnimationManager.excute()              //方法
 *   AnimationManager.play()                //方法
 *   AnimationManager.stop()                //方法
 *   AnimationManager.pause()               //方法
 *   AnimationManager.reset()               //方法
 *   AnimationManager.addAnimationGroup()   //方法
 *
 *
 * 4、使用步骤：
 * （1）右滑，读取动画
 * （2）右滑，创建动画
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    public static String RootPath=android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    private Toolbar mToolbar;
    private DrawerLayout layout_drawer;
    //
    private ReadAnimationFragment readAnimationFragment;
    private MyMapFragment myMapFragment;
    private CreatAnimationFragment creatAnimationFragment;

    private MapView mapView;
    private MapControl mapControl;
    private Workspace workspace;
    //
    private long libID_JB = -1;
    private long libID_TY = -1;

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
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initView();
        openMap();
        preparePlotSymbol();
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
    private void initView(){
        mapView=(MapView)findViewById(R.id.mapview);
        //
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("我的地图");
        setSupportActionBar(mToolbar);
        //
        layout_drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,layout_drawer,mToolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        layout_drawer.addDrawerListener(toggle);
        toggle.syncState();
        //
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(itemSelectedListener);
        //
        navigationView.setCheckedItem(R.id.my_map);//默认选中我的地图
        showMyMapFragment();
    }
    NavigationView.OnNavigationItemSelectedListener itemSelectedListener=new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id=menuItem.getItemId();
            if (id==R.id.my_map){
                mToolbar.setTitle("我的地图");
                mapControl.getMap().getLayers().get("Plot_CAD@Fujian").setVisible(true);
                if (mapControl.getMap().getLayers().contains("Plot_CAD@Fujian")){
                    mapControl.getMap().getLayers().get("Plot_CAD@Fujian").setVisible(false);
                }
                //初始化时，先删除
                AnimationManager.getInstance().deleteAll();
                mapControl.getMap().refresh();
                showMyMapFragment();
            }
            else if (id==R.id.my_readanimation){
                mapControl.getMap().getLayers().get("Plot_CAD@Fujian").setVisible(false);
                if (mapControl.getMap().getLayers().contains("Plot_CAD@Fujian")){
                    mapControl.getMap().getLayers().get("Plot_CAD@Fujian").setVisible(false);
                }
                //初始化时，先删除
                AnimationManager.getInstance().deleteAll();
                mapControl.getMap().refresh();
                showReadxmlFragment();
            }
            else if (id==R.id.my_creatanimation){
                mapControl.getMap().getLayers().get("Plot_CAD@Fujian").setVisible(false);
                if (mapControl.getMap().getLayers().contains("Plot_CAD@Fujian")){
                    mapControl.getMap().getLayers().get("Plot_CAD@Fujian").setVisible(true);
                }
                //初始化时，先删除
                AnimationManager.getInstance().deleteAll();
                mapControl.getMap().refresh();
                showCreatAnimationFragment();
            }
            layout_drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };
    private void openMap(){
        mapControl=mapView.getMapControl();
        workspace=new Workspace();
        mapControl.getMap().setWorkspace(workspace);
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SMWU);
        info.setServer(RootPath+"SampleData/Fujian/TourLine.smwu");
        boolean isopen=workspace.open(info);
        if (isopen){
            String mapname=workspace.getMaps().get(0);
            mapControl.getMap().open(mapname);
            for (int i=0;i<mapControl.getMap().getLayers().getCount();i++){
                mapControl.getMap().getLayers().get(i).setSelectable(false);
            }
        }
    }
    private void preparePlotSymbol(){
        String pathJB=RootPath+"SampleData/Fujian/Symbol/JY.plot";
//        String pathZY=RootPath+"SampleData/PlotAnimation/Symbol/Symbol.plot";
        String pathTY=RootPath+"SampleData/Fujian/Symbol/TY.plot";
        libID_JB=mapControl.addPlotLibrary(pathJB);
//        libID_ZY=mapControl.addPlotLibrary(pathZY);
        libID_TY=mapControl.addPlotLibrary(pathTY);
        if (libID_JB<0){shownToast("加载JB符号库失败");}
//        if (libID_ZY<0){shownToast("加载Symbol符号库失败");}
        if (libID_TY<0){shownToast("加载TY符号库失败");}
    }

    /**
     * 显示我的洞察
     */
    private void showMyMapFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (myMapFragment == null) {
            myMapFragment = new MyMapFragment();
            fragmentTransaction.add(R.id.content_framelayout, myMapFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(myMapFragment)
                .commit();
    }
    /**
     * 显示我的洞察
     */
    private void showCreatAnimationFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (creatAnimationFragment == null) {
            creatAnimationFragment = new CreatAnimationFragment(mapControl,libID_JB,libID_TY);
            fragmentTransaction.add(R.id.content_framelayout, creatAnimationFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(creatAnimationFragment)
                .commit();
    }
    /**
     * 显示我的洞察
     */
    private void showReadxmlFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (readAnimationFragment == null) {
            readAnimationFragment = new ReadAnimationFragment(mapControl);
            fragmentTransaction.add(R.id.content_framelayout, readAnimationFragment, "MyMapsFragment");
        }
        hideAllFragment(fragmentTransaction);
        fragmentTransaction
                .show(readAnimationFragment)
                .commit();
    }
    /**
     * 显示隐藏所有与侧滑栏
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (readAnimationFragment != null) {
            fragmentTransaction.hide(readAnimationFragment);
        }
        if (creatAnimationFragment !=null) {
            fragmentTransaction.hide(creatAnimationFragment);
        }
        if(myMapFragment != null) {
            fragmentTransaction.hide(myMapFragment);
        }
    }



    /**
     * 退出应用
     */
    public void exit() {
        mapView.getMapControl().getMap().close();
        finish();
//        Process.killProcess(Process.myPid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (layout_drawer.isDrawerOpen(GravityCompat.START)) {
            return super.onKeyDown(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否退出应用?");
//            builder.setIcon(R.mipmap.user);
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
    private void shownToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
