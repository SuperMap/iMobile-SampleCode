package com.supermap.dynamicchart;

/**
 * <p>
 * Title:动态图表
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
 *   	展示动态图表。
 *   
 * 2、Demo数据：
 * 		数据目录："../SampleData/DynamicChartData/"
 *      地图数据："China400.smwu", "China.udb", "China.udd"
 *      许可目录："../SuperMap/License/"
 *      
 * 3、关键类型/成员: 
 *    recordset.seekID();				方法
 *    recordset.getFieldValue();		方法
 *    data.setColor();					方法
 *    data.setGeoID();					方法
 *    data.setLabel();					方法
 *    data.setValue();					方法
 *    m_pieChart.addData();				方法		
 *    m_pieChart.setChartTitle();		方法
 *    m_pieChart.getLegendView();		方法
 *    m_pieChart.reLayout();			方法
 *    m_lineChart.addXLabel();			方法
 *
 * 4、功能展示
 *   (1)创建、显示折线图
 *   (2)创建、显示饼状图
 *   (3)创建、显示柱状图
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.imChart.BarChartData;
import com.supermap.mapping.imChart.BarChart;
import com.supermap.mapping.imChart.ChartsView;
import com.supermap.mapping.imChart.InstrumentChart;
import com.supermap.mapping.imChart.LegendView;
import com.supermap.mapping.imChart.LineChartData;
import com.supermap.mapping.imChart.LineChart;
import com.supermap.mapping.imChart.PieChartData;
import com.supermap.mapping.imChart.PieChart;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2017/9/12.
 */

public class MainActivity extends Activity {
    final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private MapControl m_mapControl = null;
    private Workspace m_workSpace = null;
    private MapView m_mapView = null;
    private ChartsView m_pieChart = null;
    private ChartsView m_lineChart = null;
    private ChartsView m_barChart = null;
    private InstrumentChart m_instrumentChart = null;
    private RelativeLayout m_layout = null;
    private Layers m_layers = null;
    private Layer m_layer = null;
    boolean m_bShowPieChart = false;
    boolean m_bShowLineChart = false;
    boolean m_bShowBarChart = false;
    String queryName;
    int houseValue;
    ArrayList<String> strName = new ArrayList<String>();
    ArrayList<Integer> strValue = new ArrayList<Integer>();
    int id;

    private enum CHART_STATE {STATE_LINECHART, STATE_BARCHART,
        STATE_PIECHART, STATE_NULL,STATE_DIALCHART};
    private CHART_STATE m_lastChart = CHART_STATE.STATE_NULL;
    private CHART_STATE m_curChart = CHART_STATE.STATE_NULL;
    private CHART_STATE m_dChart = CHART_STATE.STATE_NULL;
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
        //使用OpenGL模式显示
        Environment.setOpenGLMode(true);
        //设置许可文件路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);
        setContentView(R.layout.activity_main);

        //打开工作空间
        m_workSpace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard+"/SampleData/DynamicChartData/China400.smwu");
        info.setType(WorkspaceType.SMWU);
        boolean result = m_workSpace.open(info);

        if (!result) {
            System.out.println("工作空间打开失败");
            return;
        }

        //将地图显示控件和工作空间关联
        m_mapView = (MapView)findViewById(R.id.Map_view);
        m_mapControl =  m_mapView.getMapControl();
        m_mapControl.getMap().setWorkspace(m_workSpace);
        //打开工作空间中的地图。参数0表示第一张地图
        String mapName = m_workSpace.getMaps().get(0);
        result = m_mapControl.getMap().open(mapName);
        if (!result) {
            System.out.println("地图打开失败");
            return;
        }

        //初始化图表
        m_pieChart = (ChartsView) findViewById(R.id.pieChart);
        m_lineChart = (ChartsView) findViewById(R.id.lineChart);
        m_barChart = ((ChartsView) findViewById(R.id.barChart));
        m_instrumentChart = (InstrumentChart) findViewById(R.id.diaChart);

        m_layers = m_mapControl.getMap().getLayers();
        m_mapControl.setAction(Action.SELECT);
        m_layer = m_layers.get("China_Provinces@China");	//获取用于创建动态图表的图层

        m_pieChart.setLayer(m_layer);
        m_lineChart.setLayer(m_layer);
        m_barChart.setLayer(m_layer);
        m_instrumentChart.setLayer(m_layer);
		m_barChart.setControl(m_mapView.getMapControl());
        m_pieChart.setControl(m_mapView.getMapControl());
        m_lineChart.setControl(m_mapView.getMapControl());
        m_layer.setEditable(false);
        m_layer.setSelectable(true);

        //刷新地图
        m_mapControl.getMap().refresh();

        //图表窗口不可见
        m_layout = (RelativeLayout) findViewById(R.id.ChartView);
        m_layout.setVisibility(View.INVISIBLE);

        //创建动态图表
        CreatePieChart();		//饼图
        CreateLineChart();		//现状图
        CreateBarChart();		//面状图
        CreateDiaChart();       //仪表盘

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                DatasetVector data = (DatasetVector) m_layer.getDataset();
                Recordset recordset = data.getRecordset(false, CursorType.STATIC);
                recordset.moveFirst();
                while (!recordset.isEOF()) {
                    strName.add((String) recordset.getFieldValue("省级行政单位"));
                    strValue.add((Integer) recordset.getFieldValue("House_Ave_Price_2012"));
                    recordset.moveNext();
                }
            }
        }).start();
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
    private void CreateDiaChart() {
        // TODO Auto-generated method stub
        ((InstrumentChart) m_instrumentChart).setMinValue(0);
        ((InstrumentChart) m_instrumentChart).setMaxValue(200);
        ((InstrumentChart) m_instrumentChart).setPointColor(Color.BLACK);
        ((InstrumentChart) m_instrumentChart).setPoint("", 0);

        m_mapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
            @Override
            public void geometrySelected(GeometrySelectedEvent event) {
                id = event.getGeometryID();
                queryName = strName.get(id - 1);
                if(strValue.get(id - 1)!=null){
                    houseValue = strValue.get(id - 1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((InstrumentChart) m_instrumentChart).setPoint("", houseValue/100);
                        ((InstrumentChart) m_instrumentChart).setChartTitle(queryName+"房价");
                    }
                });
            }
            @Override
            public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> events) {
            }

            @Override
            public void geometryMultiSelectedCount(int i) {

            }
        });

    }

    //"仪表盘"按钮
    public void btnDiaChart_Click(View view){

        showDiaChart();
    }


    //"柱状图"按钮
    public void btnBarChart_Click(View view){

        showBarChart();
    }

    //"折线图"按钮
    public void btnLineChart_Click(View view){

        showLineChart();
    }

    //"饼状图"按钮
    public void btnPieChart_Click(View view){

        showPieChart();
    }

    //创建饼状图
    public void CreatePieChart(){

        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //北京、天津、上海、重庆在数据中的ID
        int [] IDs = {30, 22, 29, 27};
        //设置显示的颜色
        int [] colors =  {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
        };
        //图表标签值
        String [] labels = {"北京市", "天津市", "上海市", "重庆市"};

        int i = 0;
        String field_HousePrice = "House_Ave_Price_2012";	//数据中的属性名，表示房屋2012年均价
        boolean isTrue = false;
        for(i=0; i<IDs.length; i++){
            isTrue = recordset.seekID(IDs[i]);	//通过ID获取对应的记录
            if(isTrue){
                Object value = recordset.getFieldValue(field_HousePrice);	//获取对应的属性值

                PieChartData data = new PieChartData();
                data.setColor(colors[i]);	//设置图标子项的颜色
                data.setGeoID(IDs[i]);		//设置图表子项关系的几何对象SmID, 和图表所关联的图层数据集对应
                data.setLabel(labels[i]);	//设置图表子项的标签
                data.setValue((Integer)value);	//设置图表子项的数值，每一个饼状图的子项都只有一个数值
                ((PieChart)m_pieChart).addData(data);	//添加图表数据
            }
        }
        m_pieChart.setChartTitle("2012年直辖市平均房价对比");		//设置图表标题

        LegendView legendView = m_pieChart.getLegendView();//获取图例LegendView
        legendView.setColumnWidth(legendView.getColumnWidth() + 50);	//设置图例子项宽度
        legendView.setNumColumns(5);
        m_pieChart.reLayout();	//修改布局后需调用该方法重新布局

        recordset.close();	//关闭记录集
        recordset.dispose();//释放记录集所占资源
    }

    public void CreateLineChart(){

        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //北京、天津、上海、重庆在数据中的ID
        int [] IDs = {30, 22, 29, 27};
        //设置显示的颜色
        int [] colors =  {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
        };
        //图表标签值
        String [] labels = {"北京市", "天津市", "上海市", "重庆市"};

        String field_PoP = "Popu_";
        int [] years = {2002, 2010, 2011, 2012};
        boolean isTrue = false;
        for(int i=0; i<IDs.length; i++){
            isTrue = recordset.seekID(IDs[i]);	//通过ID获取对应的记录
            if(isTrue){
                LineChartData lineChartData = new LineChartData();
                lineChartData.setColor(colors[i]);	//设置图标子项的颜色
                lineChartData.setGeoID(IDs[i]);		//设置图表子项关系的几何对象SmID
                lineChartData.setLabel(labels[i]);	//设置图表子项的标签

                for(int j=0; j<years.length; j++){
                    Object value = recordset.getFieldValue(field_PoP + years[j]); 	//获取对应的属性值
                    lineChartData.addValue((Integer) value);		//添加数值
                }
                ((LineChart)m_lineChart).addData(lineChartData);	//添加图表数据
            }
        }

        for(int i=0; i<years.length; i++){
            ((LineChart)m_lineChart).addXLabel(years[i]+"");	//添加X轴标签
        }

        m_lineChart.setChartTitle("直辖市人口趋势对比");	//设置图表标题

        LegendView legendView = m_lineChart.getLegendView();	//获取图例LegendView
        legendView.setColumnWidth(legendView.getColumnWidth() + 30);//设置图例子项宽度
        legendView.setNumColumns(5);
        m_lineChart.reLayout();

        recordset.close();	//关闭记录集
        recordset.dispose();//释放记录集所占资源
    }

    public void CreateBarChart(){

        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //北京、天津、上海、重庆在数据中的ID
        int [] IDs = {30, 22, 29, 27};
        //柱状图显示的颜色
        int [] colors =  {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140)};

        String field_University = "university_num_2012";
        String field_Museum = "museum_num_2012";
        String field_ProvinceName = "省级行政单位";
        boolean isTrue = false;
        for(int i=0; i<IDs.length; i++){

            isTrue = recordset.seekID(IDs[i]);	//通过ID获取对应的记录
            if(isTrue){

                BarChartData barChartData = new BarChartData();
                Object name = recordset.getFieldValue(field_ProvinceName);
                if(name == null){
                    recordset.moveNext();
                    continue;
                }

                Object value = recordset.getFieldValue(field_University);
                if(value == null){
                    recordset.moveNext();
                    continue;
                }

                barChartData.addData("大学", colors[0], (Short) value);
                value = recordset.getFieldValue(field_Museum);
                if(value == null){
                    recordset.moveNext();
                    continue;
                }

                barChartData.setGeoID(IDs[i]);
                barChartData.addData("图书馆", colors[1], (Short) value);
                ((BarChart) m_barChart).addData(barChartData);
                ((BarChart)m_barChart).addXLabel((String)name);
            }
        }
        m_barChart.setChartTitle("2012年直辖市大学和图书馆数量对比");			//设置图表标题

        LegendView legendView = m_barChart.getLegendView();					//获取图例LegendView
        legendView.setColumnWidth(legendView.getColumnWidth() + 30);		//设置图例子项宽度
        legendView.setNumColumns(5);
        m_barChart.reLayout();
        recordset.close();	//关闭记录集
        recordset.dispose();//释放记录集所占资源
    }

    public void showBarChart(){

        if(m_curChart == CHART_STATE.STATE_BARCHART){	//当前已经是打开柱状图状态，关闭柱状图

            m_layout.setVisibility(View.INVISIBLE);
            m_barChart.setVisibility(View.INVISIBLE);

            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_NULL;
        }
        else{ //当前状态为非柱状图状态，打开柱状图
            m_layout.setVisibility(View.VISIBLE);
            m_pieChart.setVisibility(View.INVISIBLE);
            m_barChart.setVisibility(View.VISIBLE);
            m_lineChart.setVisibility(View.INVISIBLE);
            m_instrumentChart.setVisibility(View.INVISIBLE);
            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_BARCHART;
        }
    }

    public void showLineChart(){

        if( m_curChart == CHART_STATE.STATE_LINECHART){	//当前已经是打开线状图状态，关闭线状图

            m_layout.setVisibility(View.INVISIBLE);
            m_lineChart.setVisibility(View.INVISIBLE);

            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_NULL;
        }
        else{//当前状态为非线状图状态，打开线状图
            m_layout.setVisibility(View.VISIBLE);
            m_pieChart.setVisibility(View.INVISIBLE);
            m_barChart.setVisibility(View.INVISIBLE);
            m_lineChart.setVisibility(View.VISIBLE);
            m_instrumentChart.setVisibility(View.INVISIBLE);

            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_LINECHART;
        }
    }

    public void showPieChart(){

        if(m_curChart == CHART_STATE.STATE_PIECHART){//当前已经是打开饼状图状态，关闭饼状图

            m_layout.setVisibility(View.INVISIBLE);
            m_pieChart.setVisibility(View.INVISIBLE);

            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_NULL;
        }
        else{
            m_layout.setVisibility(View.VISIBLE);
            m_pieChart.setVisibility(View.VISIBLE);
            m_barChart.setVisibility(View.INVISIBLE);
            m_lineChart.setVisibility(View.INVISIBLE);
            m_instrumentChart.setVisibility(View.INVISIBLE);

            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_PIECHART;
        }
    }

    private void showDiaChart() {
        // TODO Auto-generated method stub
        if(m_curChart == CHART_STATE.STATE_DIALCHART){	//当前已经是打开仪表盘状态，关闭仪表盘

            m_layout.setVisibility(View.INVISIBLE);
            m_instrumentChart.setVisibility(View.INVISIBLE);
            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_NULL;
        }
        else{ //打开仪表盘
            m_layout.setVisibility(View.VISIBLE);
            m_pieChart.setVisibility(View.INVISIBLE);
            m_barChart.setVisibility(View.INVISIBLE);
            m_lineChart.setVisibility(View.INVISIBLE);
            m_instrumentChart.setVisibility(View.VISIBLE);
            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_DIALCHART;
        }
    }


}