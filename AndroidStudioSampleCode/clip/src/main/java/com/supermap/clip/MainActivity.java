package com.supermap.clip;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.supermap.analyst.spatialanalyst.OverlayAnalyst;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.Environment;
import com.supermap.data.GeoRegion;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:数据剪裁
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
 * 1、范例简介：示范数据源按范围剪裁
 * 2、示例数据：安装目录/SampleData/Map_clip/Map_clip.udb
 * 3、关键类型/成员:
 *      OverlayAnalyst.clipEx()
 * 4、使用步骤:
 *      （1）点击选择剪裁按钮，在需要剪裁的位置框选。
 *      （2）点击确定按钮，剪裁成功。
 *      （3）点击查看剪裁，查看剪裁结果。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

public class MainActivity extends Activity implements ReactLinListener{
    String rootPath =android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private RelativeLayout view;
    private MapView mapView;
    MapControl mapControl;
    Workspace workspace;
    private Datasource glDs;
    private GameView gameView;
    private GeoRegion region;
    private Spinner m_spnSelectLayer;
    private ArrayAdapter<String> adtSelectLayer;
    private String ClipDataName = "Lakes";
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
        Environment.setLicensePath(rootPath + "/SuperMap/license");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        Environment.setOpenGLMode(true);
        Environment.setWebCacheDirectory(rootPath + "/SuperMap/data/webCache");

        init();
    }



    public void init(){
        findViewById(R.id.button).setOnClickListener(listener);
        findViewById(R.id.button1).setOnClickListener(listener);
        findViewById(R.id.button2).setOnClickListener(listener);

        view = findViewById(R.id.clip);
        mapView = (MapView) findViewById(R.id.clipmapview);
        mapControl = mapView.getMapControl();
        workspace = new Workspace();
        mapControl.getMap().setWorkspace(workspace);
        DatasourceConnectionInfo ds = new DatasourceConnectionInfo();
        String path = rootPath + "/SampleData/Map_clip/Map_clip.udb";
        ds.setServer(path);
        glDs = workspace.getDatasources().open(ds);
        mapControl.getMap().getLayers().add(glDs.getDatasets().get(0), true);


        m_spnSelectLayer = (Spinner)findViewById(R.id.spn_select_layer);
        adtSelectLayer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        int nCount =glDs.getDatasets().getCount();
        for (int i = 0; i < nCount; i++) {
            String strLayerName = glDs.getDatasets().get(i).getName();
            adtSelectLayer.add(strLayerName);
        }

        //设置下拉列表风格,将adapter添加到spinner中
        adtSelectLayer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_spnSelectLayer.setAdapter(adtSelectLayer);
        m_spnSelectLayer.setOnItemSelectedListener(new spinnerSelectedListener());
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    gameView = new GameView(MainActivity.this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    gameView.setLayoutParams(params);
                    view.addView(gameView);
                    gameView.setReactCallBack(MainActivity.this);
                    break;
                case R.id.button1:
                    view.removeView(gameView);
                    break;
                case R.id.button2:
                    String testname = "clipData";
                    Datasource datasource = glDs;
                    // 裁剪数据集
                    String ClipData = ClipDataName;
                    DatasetVector dataClip = (DatasetVector) datasource.getDatasets().get(
                            ClipData);
                    // 创建矢量数据集，用来存储裁剪后的数据
                    if (datasource.getDatasets().contains(testname)) {
                        datasource.getDatasets().delete(testname);
                    }
                    DatasetVectorInfo datasetResultInfo = new DatasetVectorInfo(testname, dataClip);
                    DatasetVector SrcDataset = datasource.getDatasets().create(datasetResultInfo);
                    SrcDataset.setPrjCoordSys(dataClip.getPrjCoordSys());
                    boolean isTrue= OverlayAnalyst.clipEx(dataClip, region, SrcDataset, true, false, 0);
                    if(isTrue){
                        mapControl.getMap().getLayers().clear();
                        mapControl.getMap().getLayers().add(glDs.getDatasets().get("clipData"), true);
                    }
                    break;
            }
        }
    };

    public Point2D pixelToMap(Point point){
        return mapControl.getMap().pixelToMap(point);
    }

    public void reactCallBack(Rect rect){
        Point p1 = new Point(rect.left,rect.top);
        Point p2 = new Point(rect.left,rect.bottom);
        Point p3 = new Point(rect.right,rect.bottom);
        Point p4 = new Point(rect.right,rect.top);
        Point2Ds point2Ds = new Point2Ds();
        point2Ds.add(pixelToMap(p1));
        point2Ds.add(pixelToMap(p2));
        point2Ds.add(pixelToMap(p3));
        point2Ds.add(pixelToMap(p4));
        region = new GeoRegion(point2Ds);
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

    //下拉列表
    class spinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            ClipDataName = glDs.getDatasets().get(arg2).getName();
            mapControl.getMap().getLayers().clear();
            mapControl.getMap().getLayers().add(glDs.getDatasets().get(arg2), true);
            mapControl.getMap().refresh();

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }
}
