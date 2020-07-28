package com.example.mchart;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.LicenseType;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapView;
import com.supermap.mapping.imChart.BarChart;
import com.supermap.mapping.imChart.BarChartData;
import com.supermap.mapping.imChart.ChartPoint;
import com.supermap.mapping.imChart.GridHotChart;
import com.supermap.mapping.imChart.HeatMap;
import com.supermap.mapping.imChart.InstrumentChart;
import com.supermap.mapping.imChart.LegendView;
import com.supermap.mapping.imChart.LineChart;
import com.supermap.mapping.imChart.LineChartData;
import com.supermap.mapping.imChart.PieChart;
import com.supermap.mapping.imChart.PieChartData;
import com.supermap.mapping.imChart.PointDensityChart;
import com.supermap.mapping.imChart.PolymerChart;
import com.supermap.mapping.imChart.RelationPointChart;
import com.supermap.mapping.imChart.RelationalChartPoint;
import com.supermap.mapping.imChart.TimeLine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:数据可化
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
 * 1、范例简介：数据可化，展示关系图、热度图、点密度图等
 * 2、示例数据：
 * 		目录../SampleData/MChartData/HotMap/hotMap.smwu;
 * 3、关键类型/成员:
 *	    GridHotChart,HeatMap,PointDensityChart，RelationPointChart
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

public class MainActivity extends Activity{

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

    private Workspace workspace;
    private MapView mMapView;
    private HeatMap mHotChart;
    private PolymerChart mPolymerChart;
    private GridHotChart mGridHotChart;
    private PointDensityChart mDensithart;
    private RelationPointChart mRelationChart;
    Map map;
    private Button mBtnRealTime,mTimeData;
    private PieChart m_pieChart = null;
    private LineChart m_lineChart = null;
    private BarChart m_barChart = null;
    private InstrumentChart m_instrumentChart = null;
    private Layers m_layers = null;
    private Layer m_layer = null;
    Double v ;
    int z;
    LinearLayout layout2 = null;
    LinearLayout layout3 =null;
    String queryName;
    String houseValue;
    ArrayList<String> strName = new ArrayList<String>();
    ArrayList<String> strValue = new ArrayList<String>();
    int id;
    private enum CHART_STATE {STATE_LINECHART, STATE_BARCHART,
        STATE_PIECHART, STATE_NULL,STATE_DIALCHART};
    private CHART_STATE m_lastChart = CHART_STATE.STATE_NULL;
    private CHART_STATE m_curChart = CHART_STATE.STATE_NULL;
    private CHART_STATE m_dChart = CHART_STATE.STATE_NULL;
    private boolean bDataInit = false;
    private Handler mHandle = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1000001) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layout_left);
                layout.setVisibility(View.VISIBLE);
                layout2 = (LinearLayout) findViewById(R.id.charts_right);
                layout3 = (LinearLayout) findViewById(R.id.charts_right1);
                Button btn = (Button) findViewById(R.id.btn_hot);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        initHotMap();
                        mMapView.getMapControl().setAction(Action.PAN);
                        mMapView.getMapControl().getMap().getTrackingLayer().clear();
                        layout2.setVisibility(View.INVISIBLE);
                        layout3.setVisibility(View.INVISIBLE);
                        m_curChart = CHART_STATE.STATE_NULL;
                    }
                });

                btn = (Button) findViewById(R.id.btn_polymer);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        initPolymeMap();
                        mMapView.getMapControl().setAction(Action.PAN);
                        mMapView.getMapControl().getMap().getTrackingLayer().clear();
                        layout2.setVisibility(View.INVISIBLE);
                        layout3.setVisibility(View.INVISIBLE);
                        m_curChart = CHART_STATE.STATE_NULL;
                    }
                });
                btn = (Button) findViewById(R.id.btn_density);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        initDensityPoint();
                        mMapView.getMapControl().setAction(Action.PAN);
                        mMapView.getMapControl().getMap().getTrackingLayer().clear();
                        layout2.setVisibility(View.INVISIBLE);
                        layout3.setVisibility(View.INVISIBLE);
                        m_curChart = CHART_STATE.STATE_NULL;
                    }
                });
                btn = (Button) findViewById(R.id.btn_relative);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        initRelationMap();
                        mMapView.getMapControl().setAction(Action.PAN);
                        mMapView.getMapControl().getMap().getTrackingLayer().clear();
                        layout2.setVisibility(View.INVISIBLE);
                        layout3.setVisibility(View.INVISIBLE);
                        m_curChart = CHART_STATE.STATE_NULL;
                    }
                });

                mBtnRealTime = (Button) findViewById(R.id.btn_real_time);
                mBtnRealTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean bSelected = false;
                        if(!mBtnRealTime.isSelected()){
                            bSelected = true;
                        }
                        mBtnRealTime.setSelected(bSelected);
                        realTimeData(bSelected);
                    }
                });

                mTimeData = (Button) findViewById(R.id.btn_time_data);
                mTimeData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // clear();
                        //// TODO: 17/5/11 timedata
                        timeData();
                    }
                });

                btn = (Button) findViewById(R.id.btn_grid);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        initGridHotMap();
                        mMapView.getMapControl().setAction(Action.PAN);
                        mMapView.getMapControl().getMap().getTrackingLayer().clear();
                        layout2.setVisibility(View.INVISIBLE);
                        layout3.setVisibility(View.INVISIBLE);
                        m_curChart = CHART_STATE.STATE_NULL;
                    }
                });

                Toast toast = Toast.makeText(getApplicationContext(), "数据加载完成 ...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

            }
        };
    };
    //创建仪表盘
    private void CreateDiaChart() {
        // TODO Auto-generated method stub
        m_instrumentChart.setPoint("", 0);
        m_instrumentChart.setPointColor(Color.RED);
        m_instrumentChart.setLabelsColor(Color.BLACK);
        m_instrumentChart.setMaxValue(100);
        m_instrumentChart.setMinValue(0);
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        recordset.moveFirst();
        while (!recordset.isEOF()) {
            strValue.add((String) recordset.getFieldValue("DZM"));
            strName.add((String) recordset.getFieldValue("NAME"));
            recordset.moveNext();
        }
    }
    //创建饼状图
    public void CreatePieChart(){
        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //北京、天津、上海、重庆在数据中的ID
        int [] IDs = {34, 7, 6, 18, 25};
        //设置显示的颜色
        int [] colors =  {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
        };
        //图表标签值
        String [] labels = {"四川", "河南", "山东", "广东", "陕西"};

        int i = 0;
        String field_HousePrice = "Pop_1990";	//数据中的属性名，表示房屋2012年均价
        boolean isTrue = false;
        for(i=0; i<IDs.length; i++){
            isTrue = recordset.seekID(IDs[i]);	//通过ID获取对应的记录
            if(isTrue){
                Object value = recordset.getFieldValue(field_HousePrice);	//获取对应的属性值
                v = (Double) value;
                PieChartData data = new PieChartData();
                data.setColor(colors[i]);	//设置图标子项的颜色
                data.setGeoID(IDs[i]);		//设置图表子项关系的几何对象SmID, 和图表所关联的图层数据集对应
                data.setLabel(labels[i]);	//设置图表子项的标签
                data.setValue(v);	//设置图表子项的数值，每一个饼状图的子项都只有一个数值
                m_pieChart.addData(data);	//添加图表数据
            }
        }
        m_pieChart.setChartTitle("各地人口");		//设置图表标题
        LegendView legendView = m_pieChart.getLegendView();//获取图例LegendView
        legendView.setColumnWidth(legendView.getColumnWidth() + 30);	//设置图例子项宽度
        legendView.setNumColumns(5);
        m_pieChart.reLayout();	//修改布局后需调用该方法重新布局
        recordset.close();	//关闭记录集
        recordset.dispose();//释放记录集所占资源
    }
    //柱状图
    public void CreateBarChart(){

        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //北京、天津、上海、重庆在数据中的ID
        int [] IDs = {34, 7, 6, 18, 25};
        String [] labels = {"四川", "河南", "山东", "广东", "陕西"};
        //柱状图显示的颜色
        int [] colors =  {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
        };

        String field_University = "HighSchool_1990";
        String field_Museum = "HighSchool_2000";
        String field_ProvinceName = "NAME";
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
                double v1 = (Double) value;
                barChartData.addData(labels[i], colors[i], v1);
                Object value1 = recordset.getFieldValue(field_Museum);
                if(value1 == null){
                    recordset.moveNext();
                    continue;
                }
                double v2 = (Double) value1;
                barChartData.setGeoID(IDs[i]);
                barChartData.addData(labels[i], colors[i],v2);
                ((BarChart) m_barChart).addData(barChartData);

            }
        }
        m_barChart.setChartTitle("1990年和2000年GDP对比");			//设置图表标题
        m_barChart.setValueAlongXAxis(true);
        ((BarChart)m_barChart).addXLabel("1990");
        ((BarChart)m_barChart).addXLabel("2000");

        LegendView legendView = m_barChart.getLegendView();					//获取图例LegendView
        legendView.setColumnWidth(legendView.getColumnWidth() + 30);		//设置图例子项宽度
        legendView.setNumColumns(5);
        m_barChart.reLayout();
        recordset.close();	//关闭记录集
        recordset.dispose();//释放记录集所占资源

    }

    public void CreateLineChart(){

        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector)m_layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //北京、天津、上海、重庆在数据中的ID
        int [] IDs = {34, 7, 6, 18, 25};
        //设置显示的颜色
        int [] colors =  {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
        };
        //图表标签值
        String [] labels = {"四川", "河南", "山东", "广东", "陕西"};

        String field_PoP = "GDP_";
        int [] years = {1994, 1997, 1998, 1999};
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
                    Double va = (Double)value;
                    lineChartData.addValue(va);		//添加数值
                }
                ((LineChart)m_lineChart).addData(lineChartData);	//添加图表数据
            }
        }

        for(int i=0; i<years.length; i++){
            ((LineChart)m_lineChart).addXLabel(years[i]+"");	//添加X轴标签
        }

        m_lineChart.setChartTitle("GDP增长趋势");	//设置图表标题

        LegendView legendView = m_lineChart.getLegendView();	//获取图例LegendView
        legendView.setColumnWidth(legendView.getColumnWidth() + 30);//设置图例子项宽度
        legendView.setNumColumns(5);
        m_lineChart.reLayout();

        recordset.close();	//关闭记录集
        recordset.dispose();//释放记录集所占资源
    }
    //仪表盘按钮
    public void btnInstrumentChart_Click(View view){
        clear();
        for(int i=0;i<map.getLayers().getCount();i++){
            Layer layer = map.getLayers().get(i);
            layer.setVisible(true);
        }
        showInstrumentChart();
        mMapView.getMapControl().setAction(Action.SELECT);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_right);
        layout.setVisibility(View.INVISIBLE);
    }
    public void showInstrumentChart(){
        if(m_curChart == CHART_STATE.STATE_BARCHART){  //如果是打开状态则关闭
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            m_barChart.setVisibility(View.INVISIBLE);
            m_instrumentChart.setVisibility(View.INVISIBLE);
            m_pieChart.setVisibility(View.INVISIBLE);
            m_lineChart.setVisibility(View.INVISIBLE);
            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_NULL;
        }
        else{
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.VISIBLE);
            m_pieChart.setVisibility(View.VISIBLE);
            m_barChart.setVisibility(View.VISIBLE);
            m_lineChart.setVisibility(View.VISIBLE);
            m_instrumentChart.setVisibility(View.VISIBLE);
            m_lastChart = m_curChart;
            m_curChart = CHART_STATE.STATE_BARCHART;
        }
    }


    private void openMap(){
        String rootPath =android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        Environment.initialization(this);
        Environment.setOpenGLMode(true);
        Environment.setLicensePath(rootPath + "/SuperMap/license");
//        String jStirng = Environment.getLicensePath();
//        Environment.setLicenseType(LicenseType.UUID);

        setContentView(R.layout.activity_main_chart);
        RelativeLayout view  = (RelativeLayout) findViewById(R.id.activity_main);

        mMapView = new MapView(this);
        //mMapView.setLayoutParams(new ViewGroup.LayoutParams(1000,1000));

        map = mMapView.getMapControl().getMap();
        workspace = map.getWorkspace();

        String path =  rootPath + "/SampleData/MChartData/HotMap/hotMap.smwu";
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(path);
        info.setType(WorkspaceType.SMWU);
        boolean isOpen = workspace.open(info);
        if(isOpen){
            map.open(workspace.getMaps().get(0));
            //   map.viewEntire();
            map.refresh();
        }else {
            Toast.makeText(this, "Open workspace failed!", Toast.LENGTH_LONG).show();
        }
        info.dispose();

        map.refresh();
        view.addView(mMapView,0);



        m_pieChart = (PieChart) findViewById(R.id.pieChart);
        m_lineChart = (LineChart) findViewById(R.id.lineChart);
        m_barChart = (BarChart) findViewById(R.id.barChart);
        m_instrumentChart = (InstrumentChart) findViewById(R.id.diaChart);
        m_layers = map.getLayers();
        m_layer = m_layers.get("Provinces_R@hotMap#2");	//获取用于创建动态图表的图层

        m_pieChart.setLayer(m_layer);
        m_lineChart.setLayer(m_layer);
        m_barChart.setLayer(m_layer);
        m_barChart.setControl(mMapView.getMapControl());
        m_pieChart.setControl(mMapView.getMapControl());
        m_lineChart.setControl(mMapView.getMapControl());
        m_instrumentChart.setLayer(m_layer);
        m_layer.setEditable(false);
        m_layer.setSelectable(true);
        CreateDiaChart();       //仪表盘
        CreatePieChart();		//饼图
        CreateBarChart();		//柱状图
        CreateLineChart();      //折线图


        mMapView.getMapControl().addGeometrySelectedListener(new GeometrySelectedListener() {
            @Override
            public void geometrySelected(GeometrySelectedEvent event) {
                id = event.getGeometryID();
                m_pieChart.setHighLigtItem(id);
                m_lineChart.setHighLigtItem(id);
                m_barChart.setHighLigtItem(id);
                queryName = strName.get(id - 1);
                if(strValue.get(id - 1)!=null){
                    houseValue = strValue.get(id - 1);
                    z = Integer.valueOf(houseValue).intValue();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_instrumentChart.setPoint("", z/10000);
                        m_instrumentChart.setChartTitle(queryName);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions() ;

//        checkPermission();
        if(!bDataInit) {
            openMap();
            ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(1);
            mFixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    prepateData();
                    mHandle.obtainMessage(1000001).sendToTarget();
                }
            });
            Toast toast = Toast.makeText(getApplicationContext(), "数据加载中 ...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,0);
            toast.show();
            bDataInit = true;
        }
        mMapView.setBackgroundColor(0x0);
    }

    private ArrayList<ChartPoint> mHotDatas = new ArrayList<ChartPoint>();
    private ArrayList<RelationalChartPoint> mRelativeDatas = new ArrayList<RelationalChartPoint>();
    private ArrayList<ChartPoint> m_30WData = new ArrayList<ChartPoint>();
    private ArrayList<ChartPoint> mPolymerDatas = new ArrayList<ChartPoint>();
    private void prepateData(){
        Recordset rd = ((DatasetVector)workspace.getDatasources().get(0).getDatasets().get(3)).getRecordset(false, CursorType.DYNAMIC);

        Random rand = new Random();
        int   i = 0;
        //hot
        int n = rd.getRecordCount();
        for(int k=0;k<5000;k++){
            int index = rand.nextInt(n);
            rd.moveTo(index);
            Point2D point = rd.getGeometry().getInnerPoint();
            float dw = rand.nextInt(100)+1;
            mHotDatas.add(new ChartPoint(point,dw));
        }
        rd.dispose();

        rd = ((DatasetVector)workspace.getDatasources().get(0).getDatasets().get(6)).getRecordset(false, CursorType.DYNAMIC);

        while (!rd.isEOF()) {

            Point2D point = rd.getGeometry().getInnerPoint();
            mPolymerDatas.add(new ChartPoint(point,0));
            rd.moveNext();
        }
        rd.dispose();

        //relative
        rd = ((DatasetVector)workspace.getDatasources().get(0).getDatasets().get(0)).getRecordset(false, CursorType.DYNAMIC);

        while (!rd.isEOF()) {

            Point2D point = rd.getGeometry().getInnerPoint();
            RelationalChartPoint relPoint = new RelationalChartPoint(point,0);
            relPoint.setRelationName((String)rd.getFieldValue("name"));

            mRelativeDatas.add(relPoint);
            rd.moveNext();
        }
        rd.dispose();


        //grid,density
        rd = ((DatasetVector)workspace.getDatasources().get(0).getDatasets().get(5)).getRecordset(false, CursorType.DYNAMIC);
        i = 0;
        while (!rd.isEOF()) {

            Point2D point = rd.getGeometry().getInnerPoint();
            m_30WData.add(new ChartPoint(point,0));
            rd.moveNext();
            rd.moveNext();// 使用1/2的数据
        }
        rd.dispose();
    }
    private Timer realTimer = null;
    private void realTimeData(boolean bSelected){

        if(timeLine != null){
            timeLine.dispose();
            timeLine = null;
            mHotChart.removeAllData();
        }
        Log.v("xzy","~~~~~~~stop");
        if(bSelected) {
            if (realTimer == null) {
                realTimer = new Timer();
            } else
                realTimer.cancel();
        }else {
            if (realTimer != null) {
                realTimer.cancel();
                realTimer = null;
                mHotChart.setUpdataInterval(0);
                return;
            }
        }
        realTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Random rand = new Random();
                int nCount = 5000 + rand.nextInt(mHotDatas.size()+5000);
                ArrayList<ChartPoint> arrTmp = new ArrayList<ChartPoint>();
                for(int i=0;i<nCount;i++){

                    int index = rand.nextInt(mHotDatas.size());

                    {
                        ChartPoint weightPoint = mHotDatas.get(index);
                        float dWeight = rand.nextInt(50)+1;
                        weightPoint.setWeighted(dWeight);

                        arrTmp.add(weightPoint);

                    }
                }
                mHotChart.addChartDatas(arrTmp);
            }
        },50,2000);
        mHotChart.setUpdataInterval(2.0f);
    }

    private  TimeLine timeLine;
    private void timeData(){
        mBtnRealTime.setSelected(false);
        mHotChart.removeAllData();

        mHotChart.setUpdataInterval(0);
        if(realTimer != null){
            realTimer.cancel();
            realTimer = null;
        }

        Random rand = new Random();

        ArrayList<Point2D> targetCitys = new ArrayList<Point2D>();
        int[] speeds = new int[mHotDatas.size()+1];
        int nSteps = 16;
        for(int i=0;i<mHotDatas.size();i++){

            int speed = rand.nextInt(nSteps)+1;
            int city = rand.nextInt(10);
            if(city<4){//bj
                targetCitys.add(new Point2D(116.26,39.56));
            }else if(city>=4 && city<7){//sh
                targetCitys.add(new Point2D(121.27,31.13));
            }else if(city>=7 && city<9){//gz
                targetCitys.add(new Point2D(114.6,22.59));
            }else{//cd
                targetCitys.add(new Point2D(103.59,30.45));
            }
            speeds[i] = speed;
        }

        for(int j=0;j<nSteps;j++){
            ArrayList<ChartPoint>  arrTmp = new ArrayList<ChartPoint>();
            for(int i=0;i<mHotDatas.size();i++){

                ChartPoint weightPoint = mHotDatas.get(i);
                int speed = speeds[i];
                Point2D targetCity = targetCitys.get(i);

                float dWeight = 25 ;//+ rand.nextInt(20);

                double offsetX = (targetCity.getX()-weightPoint.getPoint().getX());
                double offsetY = (targetCity.getY()-weightPoint.getPoint().getY());

                float n = j;
                if(j > speed){
                    n = speed;
                }
                if(j==0)
                    n = 0.10f;
                double dX = weightPoint.getPoint().getX()+(offsetX/speed)*n;
                double dY = weightPoint.getPoint().getY()+(offsetY/speed)*n;
                arrTmp.add(new ChartPoint(new Point2D(dX,dY),dWeight));
            }
            mHotChart.insertChartDataset(arrTmp,String.valueOf(nSteps-j)+"时",0);
        }

        mHotChart.setPlayInterval(1.0f);
        mHotChart.setIsLoopPlay(true);


        FrameLayout frameLayoutLayout = (FrameLayout) findViewById(R.id.layout_timeline);
        frameLayoutLayout.setVisibility(View.VISIBLE);
        timeLine = new TimeLine(frameLayoutLayout,this);
        timeLine.addChart(mHotChart);
        timeLine.load();

        mHotChart.startPlay();
    }
    private void initRelationMap()
    {
        for(int i=0;i<map.getLayers().getCount();i++){
            Layer layer = map.getLayers().get(i);
            layer.setVisible(false);
        }
        Layer layer1 = map.getLayers().get("China_line_vac@hotMap");
        layer1.setVisible(true);
        Layer layer = map.getLayers().get("Provinces_R@hotMap#2");
        layer.setVisible(true);

        map.setScale(0.000000081887287763467158);
        map.setCenter(new Point2D(109.38087581861375,30.922633709727265));
        map.refresh();

        ArrayList<RelationalChartPoint> datas = new ArrayList<RelationalChartPoint>();
        ArrayList<RelationalChartPoint> targetArr = new ArrayList<RelationalChartPoint>();

        //  Recordset rd = ((DatasetVector)workspace.getDatasources().get(0).getDatasets().get(0)).getRecordset(false, CursorType.DYNAMIC);

        for(int i = 0;i<mRelativeDatas.size();i++)
        {
            RelationalChartPoint relPoint = mRelativeDatas.get(i);
            relPoint.getRelationalPoints().clear();
            String name = relPoint.getRelationName();
            if(
                    name.equalsIgnoreCase("成都市") ||
                            name.equalsIgnoreCase("北京市") ||
                            name.equalsIgnoreCase("上海市") ||
                            name.equalsIgnoreCase("广州市") ||
                            name.equalsIgnoreCase("乌鲁木齐市") ||
                            name.equalsIgnoreCase("长春市")
                    ){
                targetArr.add(relPoint);
            }
            datas.add(relPoint);
        }

        int n = datas.size();
        for(int j=0;j<targetArr.size();j++){
            RelationalChartPoint relPoint = targetArr.get(j);
            int nCount = 19;
            if(relPoint.getRelationName().equalsIgnoreCase("北京市")){
                nCount = 50;
            }else if(relPoint.getRelationName().equalsIgnoreCase("上海市")){
                nCount = 45;
            }else if(relPoint.getRelationName().equalsIgnoreCase("广州市")){
                nCount = 36;
            }else if(relPoint.getRelationName().equalsIgnoreCase("成都市")){
                nCount = 40;
            }else if(relPoint.getRelationName().equalsIgnoreCase("长春市")){
                nCount = 25;
            }
            Random rand = new Random();
            for(int k=0;k<nCount;k++)
            {
                int ntaget = rand.nextInt(n-1)+1;
                RelationalChartPoint weightTargetPointTmp = datas.get(ntaget);

                if(relPoint.getRelationalPoints().contains(weightTargetPointTmp) || relPoint.equals(weightTargetPointTmp)){
                    k--;
                    continue;
                }
                relPoint.getRelationalPoints().add(weightTargetPointTmp);
            }
        }
        
        Resources res = MainActivity.this.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.icon_plan);
        
        mRelationChart = new RelationPointChart(this,mMapView);
        mRelationChart.setIsAnimation(true);
        mRelationChart.setLineWidth(0.75f);
        mRelationChart.setMaxRadius(30);
        mRelationChart.setAnimationImage(bmp);
        mRelationChart.addChartRelationDatas(targetArr);
        mRelationChart.setTitle("关系图");
     

    }
   
    
    private void initPolymeMap(){
        for(int i=0;i<map.getLayers().getCount();i++){
            Layer layer = map.getLayers().get(i);
            layer.setVisible(false);
        }
        Layer layer1 = map.getLayers().get("China_line_vac@hotMap");
        layer1.setVisible(true);
        Layer layer = map.getLayers().get("Provinces_R@hotMap#2");
        layer.setVisible(true);

        map.setScale(0.00000004710369833422136);
        map.setCenter(new Point2D(104.45644039125189,34.960112480819163));
        map.refresh();



        mPolymerChart = new PolymerChart(this,mMapView);
        mPolymerChart.setMaxRadius(45);
        mPolymerChart.addChartDatas(mPolymerDatas);
        mPolymerChart.setTitle("聚合图");
    }
    private void initGridHotMap(){
        for(int i=0;i<map.getLayers().getCount();i++){
            Layer layer = map.getLayers().get(i);
            layer.setVisible(false);
        }
        Layer layer1 = map.getLayers().get("China_line_vac@hotMap");
        layer1.setVisible(true);
        Layer layer = map.getLayers().get("Provinces_R@hotMap#2");
        layer.setVisible(true);

        map.setScale(3.4070912983014954E-8);
        map.setCenter(new Point2D(112.00259362778627,36.26642049416556));
        map.refresh();

        mGridHotChart = new GridHotChart(this,mMapView);
        mGridHotChart.addChartDatas(m_30WData);
        mGridHotChart.setTitle("格网热力图");
    }
    private void initDensityPoint(){

        for(int i=0;i<map.getLayers().getCount();i++){
            Layer layer = map.getLayers().get(i);
            layer.setVisible(false);
        }
        Layer layer1 = map.getLayers().get("China_line_vac@hotMap");
        layer1.setVisible(true);
        Layer layer = map.getLayers().get("Provinces_R@hotMap");
        layer.setVisible(true);

        map.setScale(0.000000065099303341972671);
        map.setCenter(new Point2D(108.79366417027659,32.180036928883418));
        map.refresh();



        mDensithart = new PointDensityChart(this,mMapView);
        mDensithart.setRadious(3);
        mDensithart.addChartDatas(m_30WData);
        mDensithart.setTitle("密度图");
    }
    private void initHotMap(){

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_right);
        layout.setVisibility(View.VISIBLE);

        for(int i=0;i<map.getLayers().getCount();i++){
            Layer layer = map.getLayers().get(i);
            layer.setVisible(false);
        }
        Layer layer1 = map.getLayers().get("China_line_vac@hotMap");
        layer1.setVisible(true);
        Layer layer = map.getLayers().get("Provinces_R@hotMap");
        layer.setVisible(true);

        map.setScale(0.000000065099303341972671);
        map.setCenter(new Point2D(108.79366417027659,32.180036928883418));
        map.refresh();

        mHotChart = new HeatMap(this,mMapView);
        mHotChart.setRadious(13);
        mHotChart.setSmoothTransColor(true);
        mHotChart.addChartDatas(mHotDatas);
        mHotChart.setTitle("热力图");
    }

    private void clear(){
        if(mPolymerChart != null)
            mPolymerChart.dispose();
        if(mHotChart != null)
            mHotChart.dispose();
        if(mDensithart != null)
            mDensithart.dispose();
        if(mRelationChart != null)
            mRelationChart.dispose();
        if(mGridHotChart != null)
            mGridHotChart.dispose();

        mPolymerChart = null;
        mHotChart =null;
        mDensithart = null;
        mRelationChart = null;
        mGridHotChart = null;

        if(realTimer != null){
            realTimer.cancel();
            realTimer = null;
        }
        if(timeLine != null){
            timeLine.dispose();
            timeLine = null;
        }
        mBtnRealTime.setSelected(false);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_right);
        layout.setVisibility(View.INVISIBLE);
        findViewById(R.id.layout_timeline).setVisibility(View.GONE);
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
}
