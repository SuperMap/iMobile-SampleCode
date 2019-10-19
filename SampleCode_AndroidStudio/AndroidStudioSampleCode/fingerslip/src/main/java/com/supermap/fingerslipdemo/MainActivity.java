package com.supermap.fingerslipdemo;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.supermap.adapter.LayerAdapter;
import com.supermap.data.Environment;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.fragment.SymbolFillFragment;
import com.supermap.fragment.SymbolLineFragment;
import com.supermap.fragment.SymbolMarkerFragment;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapView;



import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:示范通过指划操作配制地图风格
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
 * 1、范例简介：示范通过指划操作配制地图风格
 * 2、示例数据：数据目录："/sdcard/SampleData/Hunan/"
 *            地图数据：Hunan.smwu,Hunan.udb,Hunan.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 GeoStyle.setFillForeColor() 			//方法
 *   GeoStyle.setFillBackColor()			//方法
 *   GeoStyle.setFillOpaqueRate()			//方法
 *   GeoStyle.setFillGradientMode()			//方法
 *   GeoStyle.setLineWidth()				//方法
 *	 GeoStyle.setLineColor()				//方法
 *   GeoStyle.setMarkerSymbolID()			//方法
 *	 GeoStyle.setMarkerSize()				//方法
 *	 GeoStyle.setMarkerAngle()				//方法
 *
 * 4、使用步骤：
 * (1)选择图层-湖南面，单指上下滑动，修改面风格
 * (2)选择图层-省会，单指上下滑动，修改点风格
 * (3)选择图层-国道，单指上下滑动，修改线风格
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

    private MapView mapView;

    private LinearLayout ll_layer_menu = null;

    private SymbolMarkerFragment symbolMarkerFragment = null;
    private SymbolLineFragment symbolLineFragment = null;
    private SymbolFillFragment symbolFillFragment = null;

    private LinearLayout ll_bottom_menu;

    private ListView layerlistview=null;
    private Map map=null;
    private List<Layer> layerList=new ArrayList<Layer>();
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
        Environment.setLicensePath(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperMap/License");
        Environment.setOpenGLMode(true);
        Environment.initialization(this);

        setContentView(R.layout.activity_main);

        openWorkspace();

        init();


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
    private void init() {
        ll_layer_menu = findViewById(R.id.ll_layer_menu);
        ll_bottom_menu = findViewById(R.id.bottom_menu);


        findViewById(R.id.btn_symbol_mark).setOnClickListener(this);
        findViewById(R.id.btn_symbol_line).setOnClickListener(this);
        findViewById(R.id.btn_symbol_fill).setOnClickListener(this);
        findViewById(R.id.btn_grid_style).setOnClickListener(this);
        findViewById(R.id.btn_text_style).setOnClickListener(this);

        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_maplayer).setOnClickListener(this);
        findViewById(R.id.btn_attr).setOnClickListener(this);
        findViewById(R.id.btn_setting).setOnClickListener(this);

        layerlistview=findViewById(R.id.layer_listview);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.height=mapView.getHeight()/2;
        params.width=mapView.getWidth();
//        layerlistview.setLayoutParams(params);
        for (int i=0;i<map.getLayers().getCount();i++){
            if (map.getLayers().get(i).getCaption().equals("省会")
                    ||map.getLayers().get(i).getCaption().equals("国道")
                    ||map.getLayers().get(i).getCaption().equals("湖南面")) {
                layerList.add(map.getLayers().get(i));
            }
        }
        LayerAdapter adapter=new LayerAdapter(layerList,this);
        layerlistview.setAdapter(adapter);

    }

    /**
     * 打开工作空间，显示地图
     */
    private void openWorkspace() {
        final String dataPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SampleData/hunan/Hunan.smwu";
        Workspace workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(dataPath);
        info.setType(WorkspaceType.SMWU);
        boolean isOpen = workspace.open(info);
        if (!isOpen) {
            return;
        }
        mapView = (MapView) findViewById(R.id.mapView);
        map = mapView.getMapControl().getMap();
        map.setWorkspace(workspace);
        map.open(workspace.getMaps().get(0));
        map.setViewBounds(map.getBounds());

        map.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_symbol_mark:
                //点
                hideLayerMenu();
                ll_bottom_menu.setVisibility(View.GONE);
                showSymbolMarkerFragment();

                break;
            case R.id.btn_symbol_line:
                //线
                hideLayerMenu();
                ll_bottom_menu.setVisibility(View.GONE);
                showSymbolLineFragment();

                break;
            case R.id.btn_symbol_fill:
                //面
                hideLayerMenu();
                ll_bottom_menu.setVisibility(View.GONE);
                showSymbolFillFragment();
                break;
            case R.id.btn_maplayer:
//                showOrHideLayerMenu();
                if (layerlistview.getVisibility()==View.GONE){
                    layerlistview.setVisibility(View.VISIBLE);
                }
                else {
                    layerlistview.setVisibility(View.GONE);
                }
                break;

        }
    }

    public void showOrHideLayerMenu() {
        layerlistview.setVisibility(View.GONE);
        if (ll_layer_menu.getVisibility() == View.VISIBLE) {
            hideLayerMenu();
        } else {
            showLayerMenu();
        }
    }

    private void showLayerMenu() {
        ll_layer_menu.setVisibility(View.VISIBLE);
    }

    private void hideLayerMenu() {
        ll_layer_menu.setVisibility(View.GONE);
    }

    public void showBottomMenu() {
        ll_bottom_menu.setVisibility(View.VISIBLE);
    }

    private void lockMap() {
        //不让地图滑动
        Map map = mapView.getMapControl().getMap();
        Rectangle2D viewBounds = map.getViewBounds();
        map.setLockedViewBounds(viewBounds);
        map.setViewBoundsLocked(true);
    }

    public void showSymbolMarkerFragment() {
        layerlistview.setVisibility(View.GONE);
        lockMap();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (symbolMarkerFragment == null) {
            symbolMarkerFragment = new SymbolMarkerFragment();
            symbolMarkerFragment.setMapView(mapView);

            fragmentTransaction.add(R.id.container, symbolMarkerFragment, "symbolMarkerFragment");
        }

        hideAllFragment(fragmentTransaction);

        fragmentTransaction
                .show(symbolMarkerFragment)
                .commit();
    }

    public void showSymbolLineFragment() {
        layerlistview.setVisibility(View.GONE);
        lockMap();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (symbolLineFragment == null) {
            symbolLineFragment = new SymbolLineFragment();
            symbolLineFragment.setMapView(mapView);

            fragmentTransaction.add(R.id.container, symbolLineFragment, "symbolLineFragment");
        }

        hideAllFragment(fragmentTransaction);

        fragmentTransaction
                .show(symbolLineFragment)
                .commit();
    }

    public void showSymbolFillFragment() {
        layerlistview.setVisibility(View.GONE);
        lockMap();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (symbolFillFragment == null) {
            symbolFillFragment = new SymbolFillFragment();
            symbolFillFragment.setMapView(mapView);

            fragmentTransaction.add(R.id.container, symbolFillFragment, "symbolFillFragment");
        }

        hideAllFragment(fragmentTransaction);

        fragmentTransaction
                .show(symbolFillFragment)
                .commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (symbolLineFragment != null) {
            fragmentTransaction.hide(symbolLineFragment);
        }

        if (symbolMarkerFragment != null) {
            fragmentTransaction.hide(symbolMarkerFragment);
        }

        if (symbolFillFragment != null) {
            fragmentTransaction.hide(symbolFillFragment);
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    /**
     * 监听keyUp
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }

        return super.onKeyUp(keyCode, event);
    }

}
