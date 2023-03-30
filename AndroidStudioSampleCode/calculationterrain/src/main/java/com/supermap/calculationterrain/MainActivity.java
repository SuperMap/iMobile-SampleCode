package com.supermap.calculationterrain;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.analyst.spatialanalyst.ProfileResult;
import com.supermap.analyst.spatialanalyst.SlopeType;
import com.supermap.analyst.spatialanalyst.TerrainAnalystSetting;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.FillGradientMode;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * <p>
 * Title:地形分析
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   展示对栅格数据进行地形分析（坡度分析、坡向分析、表面距离、表面面积）的操作。
 * 2、Demo数据：数据目录："/SampleData/calculationTerrain/BeijingTerrain.udbx"
 *           许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *        CalculationTerrain.calculateAspect()
 *        CalculationTerrain.calculateSlope()
 *        CalculationTerrain.computeSurfaceDistance()
 *        CalculationTerrain.computeSurfaceArea()
 *
 * 4、功能展示
 *   (1)对栅格数据进行坡度分析；
 *   (2)对栅格数据进行坡向分析；
 *   (3)对栅格数据进行表面距离、面积计算。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private MapControl m_mapControl;
    private MapView m_mapView;
    private Workspace m_workspace;
    private Datasource datasource;
    private DatasetGrid datasetGrid;
    private DatasetVector datasetLine;
    private DatasetVector datasetRegion;
    private Layer layerGrid;
    private Layer layerLine;
    private Layer layerRegion;
    private Recordset recordsetl;
    private Recordset recordsetr;

    String rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        //设置一些系统需要用到的路径
        Environment.setLicensePath(rootPath + "/SuperMap/license/");
        Environment.setWebCacheDirectory(rootPath + "/SuperMap/WebCahe/");
        //组件功能必须在Environment初始化之后才能调用
        Environment.initialization(this);

        initUI();
        initData();
    }
    private void initUI(){
        setContentView(R.layout.activity_main);
        m_mapView = findViewById(R.id.Map_view);
        findViewById(R.id.button_l).setOnClickListener(this);
        findViewById(R.id.button_r).setOnClickListener(this);
        findViewById(R.id.button_sub).setOnClickListener(this);
        findViewById(R.id.button_del).setOnClickListener(this);
        findViewById(R.id.button_dis).setOnClickListener(this);
        findViewById(R.id.button_area).setOnClickListener(this);
        findViewById(R.id.button_asp).setOnClickListener(this);
        findViewById(R.id.button_slo).setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.button_asp:
                //坡向分析
                CalculationTerrain.calculateAspect(datasetGrid,datasource,"aspect");
                m_mapControl.getMap().getLayers().add(datasource.getDatasets().get("aspect"),true);
                layerGrid.setVisible(false);
                break;
            case R.id.button_slo:
                //坡度分析
                CalculationTerrain.calculateSlope(datasetGrid, SlopeType.DEGREE,0.001,datasource,"slope");
                m_mapControl.getMap().getLayers().add(datasource.getDatasets().get("slope"),true);
                layerGrid.setVisible(false);
                break;
            case R.id.button_l:
                //开启编辑线图层
                m_mapControl.setAction(Action.CREATEPOLYLINE);
                layerLine.setEditable(true);
                break;
            case R.id.button_dis:
                //获取线数据集记录集
                recordsetl= datasetLine.getRecordset(false,CursorType.DYNAMIC);
                if(recordsetl.getRecordCount() == 0){
                    Toast.makeText(this, "请先绘制线段", Toast.LENGTH_SHORT).show();
                }else {
                    GeoLine geoLine=(GeoLine) recordsetl.getGeometry();
                    //计算地表距离
                    double distance= CalculationTerrain.computeSurfaceDistance(datasetGrid,geoLine);
                    Toast.makeText(this, "地表距离为："+distance+"m", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_r:
                //开启编辑面图层
                m_mapControl.setAction(Action.CREATEPOLYGON);
                layerRegion.setEditable(true);
                break;
            case R.id.button_area:
                //获取面数据集记录集
                recordsetr=datasetRegion.getRecordset(false, CursorType.DYNAMIC);
                if(recordsetr.getRecordCount() == 0){
                    Toast.makeText(this, "请先绘制面", Toast.LENGTH_SHORT).show();
                }else {
                    GeoRegion geoRegion=(GeoRegion) recordsetr.getGeometry();
                    //计算地表面积
                    double area= CalculationTerrain.computeSurfaceArea(datasetGrid,geoRegion);
                    Toast.makeText(this, "地表面积为："+area+"㎡", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_del:
                //清除坡向分析结果
                if(datasource.getDatasets().get("aspect")!=null){
                    layerGrid.setVisible(true);
                    m_mapControl.getMap().getLayers().remove("aspect@Terrain");
                    datasource.getDatasets().delete("aspect");
                }
                //清除坡度分析结果
                if(datasource.getDatasets().get("slope")!=null){
                    layerGrid.setVisible(true);
                    m_mapControl.getMap().getLayers().remove("slope@Terrain");
                    datasource.getDatasets().delete("slope");
                }
                recordsetl=datasetLine.getRecordset(false, CursorType.DYNAMIC);
                //清除线数据集中的对象
                if(recordsetl.getRecordCount()!=0){
                    recordsetl.edit();
                    recordsetl.delete();
                    recordsetl.update();
                    m_mapControl.getMap().refresh();
                }
                recordsetr=datasetRegion.getRecordset(false, CursorType.DYNAMIC);
                //清除面数据集中的对象
               if(recordsetr.getRecordCount()!=0){
                    recordsetr.delete();
                    recordsetr.update();
                    m_mapControl.getMap().refresh();
                }
                break;
            case R.id.button_sub:
                //提交编辑内容
                m_mapControl.submit();
                m_mapControl.setAction(Action.PAN);
                break;
        }
        }

    private void initData(){
        m_workspace = new Workspace();
        //将地图显示控件和工作空间关联
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setServer(rootPath+"/SampleData/calculationTerrain/BeijingTerrain.smwu");
        info.setType(WorkspaceType.SMWU);
        m_workspace.open(info);

        m_mapControl = m_mapView.getMapControl();
        m_mapControl.getMap().setWorkspace(m_workspace);
        m_mapControl.getMap().open(m_workspace.getMaps().get(0));
        m_mapControl.getMap().setScale(5.5366763408422735E-6);

        //获取数据集
        datasource=m_workspace.getDatasources().get(0);
        datasetGrid= (DatasetGrid) datasource.getDatasets().get("BeijingTerrain");
        datasetLine= (DatasetVector) datasource.getDatasets().get("New_Line");
        datasetRegion= (DatasetVector)datasource.getDatasets().get("New_Region");

        //获取图层
        layerGrid=m_mapControl.getMap().getLayers().get("BeijingTerrain@Terrain");
        layerLine=m_mapControl.getMap().getLayers().get("New_Line@Terrain");
        layerRegion=m_mapControl.getMap().getLayers().get("New_Region@Terrain");

    }
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
    /**
     * 检测权限
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */

    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }
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
}