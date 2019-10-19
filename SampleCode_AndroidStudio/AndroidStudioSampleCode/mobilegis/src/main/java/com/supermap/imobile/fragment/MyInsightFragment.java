package com.supermap.imobile.fragment;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.imChart.BarChart;
import com.supermap.mapping.imChart.BarChartData;
import com.supermap.mapping.imChart.ChartLegend;
import com.supermap.mapping.imChart.ChartPoint;
import com.supermap.mapping.imChart.ColorScheme;
import com.supermap.mapping.imChart.GridHotChart;
import com.supermap.mapping.imChart.HeatMap;
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

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 我的洞察界面
 */
public class MyInsightFragment extends Fragment {

    private MapControl mMapControl;
    private Workspace mWorkspace;
    private MapView mMapView;
    private Layer layer;
    //
    private HeatMap mHeatMap;//热力图
    private PolymerChart mPolymerChart;//聚合图
    private RelationPointChart mRelationChart;//关系图
    private GridHotChart mGridHotChart;//格网热力图
    private PointDensityChart mDensithart;//密度图
    //
    private PieChart mpieChart;//饼图
    private LineChart mlineChart;//折线图
    private BarChart mbarChart;//柱状图
    //
    private View layou_chartView;//图表界面
    private View layout_menuView;//主菜单界面
    private ViewGroup containView;//整个主界面
    private Button btn_RealTime;//实时数据
    private Button btn_TimeData;//时空数据
    private FrameLayout layout_timeline;//时空数据时间线
    private LinearLayout layout_rightMenu;//右侧实时、时空菜单
    private TimeLine timeLine;//时空线
    private Double mFiledValue;

    private ArrayList<ChartPoint> mHotDatas = new ArrayList<ChartPoint>();//热力图数据
    private ArrayList<ChartPoint> mPolymerDatas = new ArrayList<ChartPoint>();//点密度图数据
    private ArrayList<ChartPoint> m30Wdatas = new ArrayList<ChartPoint>();//点密度图数据
    private ArrayList<RelationalChartPoint> mRelativeDatas = new ArrayList<RelationalChartPoint>();//关系图数据
    private ArrayList<ChartPoint> mGridhotDatas = new ArrayList<ChartPoint>();//格网热力图数据

    public MyInsightFragment() {

    }

    @SuppressLint("ValidFragment")
    public MyInsightFragment(MapControl mMapControl) {
        this.mMapControl = mMapControl;
        mWorkspace = mMapControl.getMap().getWorkspace();
        mMapView = mMapControl.getMap().getMapView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myinsight, container, false);//定位加载界面
        //主界面的视图在切换时，可进行一定程度动画
        LayoutTransition transition = new LayoutTransition();
        container.setLayoutTransition(transition);
        initView(rootView, container);
        StartPreparDate();
        return rootView;
    }

    /**
     * 初始化视图
     * @param view
     * @param container
     */
    private void initView(View view, ViewGroup container) {

        containView = view.findViewById(R.id.contain);
        //添加动态图表界面
        layou_chartView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_chart, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //下方显示图表
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //设置为地图界面一半
        params.height = mMapControl.getHeight() / 2;
        params.width = mMapControl.getMapWidth();
        layou_chartView.setLayoutParams(params);
        //预先准备数据，将图表界面加载进来，使用时显示即可
        containView.addView(layou_chartView);
        layou_chartView.setVisibility(View.GONE);

        layout_timeline = view.findViewById(R.id.layout_timeline);
        layout_rightMenu = view.findViewById(R.id.layout_right);
        layout_menuView = view.findViewById(R.id.layou_menu);
        btn_RealTime = view.findViewById(R.id.btn_real_time);
        btn_TimeData = view.findViewById(R.id.btn_time_data);
        mpieChart = layou_chartView.findViewById(R.id.pieChart);
        mlineChart = layou_chartView.findViewById(R.id.lineChart);
        mbarChart = layou_chartView.findViewById(R.id.barChart);

        layou_chartView.findViewById(R.id.btn_back).setOnClickListener(listener);
        layou_chartView.findViewById(R.id.btn_pieChart).setOnClickListener(listener);
        layou_chartView.findViewById(R.id.btn_lineChart).setOnClickListener(listener);
        layou_chartView.findViewById(R.id.btn_barChart).setOnClickListener(listener);
        view.findViewById(R.id.btn_hotmap).setOnClickListener(listener);
        view.findViewById(R.id.btn_polymer).setOnClickListener(listener);
        view.findViewById(R.id.btn_relative).setOnClickListener(listener);
        view.findViewById(R.id.btn_grid).setOnClickListener(listener);
        view.findViewById(R.id.btn_density).setOnClickListener(listener);
        view.findViewById(R.id.btnBarChart).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_delete).setOnClickListener(listener);
        btn_RealTime.setOnClickListener(listener);
        btn_TimeData.setOnClickListener(listener);

        layer = mMapControl.getMap().getLayers().get("Vegetable@Changchun");
        layer.setSelectable(true);
        CreateBarChart();//生成饼图
        CreateLineChart();//生成折线图
        CreatePieChart();//生成柱状图

//        mMapControl.addGeometrySelectedListener(selectedListener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_hotmap:
                    clear();
                    initHotMap();
                    layout_rightMenu.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_real_time:
                    boolean bSelected = false;
                    if (!btn_RealTime.isSelected()) {
                        bSelected = true;
                    }
                    btn_RealTime.setSelected(bSelected);
                    realTimeData(bSelected);
                    break;
                case R.id.btn_time_data:
                    timeData();
                    break;
                case R.id.btn_polymer:
                    clear();
                    initPolymeMap();
                    break;
                case R.id.btn_relative:
                    clear();
                    initRelationMap();
                    break;
                case R.id.btn_grid:
                    clear();
                    initGridHotMap();
                    break;
                case R.id.btn_density:
                    clear();
                    initDensityPoint();
                    break;
                case R.id.btnBarChart:
                    clear();
                    mMapControl.setAction(Action.SELECT);
                    layout_menuView.setVisibility(View.GONE);
                    layou_chartView.setVisibility(View.VISIBLE);

                    break;
                case R.id.imagebtn_delete:
                    clear();
                    break;
                case R.id.btn_back:
                    layou_chartView.setVisibility(View.GONE);
                    layout_menuView.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_pieChart:
                    mpieChart.setVisibility(View.VISIBLE);
                    mlineChart.setVisibility(View.GONE);
                    mbarChart.setVisibility(View.GONE);
                    break;
                case R.id.btn_lineChart:
                    mpieChart.setVisibility(View.GONE);
                    mlineChart.setVisibility(View.VISIBLE);
                    mbarChart.setVisibility(View.GONE);
                    break;
                case R.id.btn_barChart:
                    mpieChart.setVisibility(View.GONE);
                    mlineChart.setVisibility(View.GONE);
                    mbarChart.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    private void StartPreparDate() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("数据加载中....");
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                prepateData();
                dialog.dismiss();
            }
        }).start();
    }

    /**
     * 数据准备
     */

    private void prepateData() {
        //热力图数据，以School数据集为例，
        Recordset rd = ((DatasetVector) mWorkspace.getDatasources().get(0).getDatasets().get("School")).getRecordset(false, CursorType.DYNAMIC);
        Random rand = new Random();
        int i = 0;
        //hot
        int n;
        //获取数据数量
        n = rd.getRecordCount();
        //填写数据
        for (int size = 0; size < 1000; size++) {
            int index = rand.nextInt(n);
            rd.moveTo(index);
            Point2D point = rd.getGeometry().getInnerPoint();
            float dw = rand.nextInt(100) + 1;
            mHotDatas.add(new ChartPoint(point, dw));
        }
        rd.dispose();

        //polymer
        rd = ((DatasetVector) mWorkspace.getDatasources().get(0).getDatasets().get("BuildingPoint")).getRecordset(false, CursorType.DYNAMIC);
        n = rd.getRecordCount();
        for (int k = 0; k < 3000; k++) {
            int index = rand.nextInt(n);
            rd.moveTo(index);
            Point2D point = rd.getGeometry().getInnerPoint();
            mPolymerDatas.add(new ChartPoint(point, 0));
        }
        rd.dispose();

        //
        //relative
        rd = ((DatasetVector) mWorkspace.getDatasources().get(0).getDatasets().get("Hospital")).getRecordset(false, CursorType.DYNAMIC);

        while (!rd.isEOF()) {

            Point2D point = rd.getGeometry().getInnerPoint();
            RelationalChartPoint relPoint = new RelationalChartPoint(point, 0);
            relPoint.setRelationName((String) rd.getFieldValue("name"));

            mRelativeDatas.add(relPoint);
            rd.moveNext();
//            rd.moveNext();
        }
        rd.dispose();

        rd = ((DatasetVector) mWorkspace.getDatasources().get(0).getDatasets().get("DensityPoint")).getRecordset(false, CursorType.DYNAMIC);
        while (!rd.isEOF()) {
            int a = rand.nextInt(100);
            for (int j = 0; j < a; j++) {
                Point2D point = rd.getGeometry().getInnerPoint();
                int weight = rand.nextInt(100) + 1;
                m30Wdatas.add(new ChartPoint(point, weight));
            }
            rd.moveNext();
        }
        rd.dispose();
    }

    /**
     * 热力图
     */
    private void initHotMap() {
        mHeatMap = new HeatMap(getActivity(), mMapView);
        mHeatMap.setRadious(30); //设置热力图圆点半径，单位dp, 默认20
        mHeatMap.setSmoothTransColor(true);//设置使用渐变显示，即不同分段颜色显示时平滑过渡
        mHeatMap.addChartDatas(mHotDatas);//添加统计数据
        mHeatMap.setTitle("学校热力图");//设置图表的标题
        mHeatMap.setTitleSize(18);

        //以下设置为可选设置，不设置则显示默认效果

        ChartLegend chartLegend = mHeatMap.getLegend();//获取图表关联的图例

        chartLegend.setAlignment(ChartLegend.BOTTOMRIGHT);//设置图例位置（默认右下）

        chartLegend.setOrient(true);//设置横向排列图例（默认纵向）


        ColorScheme colorScheme = new ColorScheme();

        float[] ranges = new float[]{0, 100, 200, 300};
        //设置图例范围
        colorScheme.setSegmentValue(ranges);
        //设置显示颜色（注意：颜色必须与范围的个数一致，否则设置失败，显示默认的风格）
        colorScheme.setColors(new com.supermap.data.Color[]{
                (new com.supermap.data.Color(207, 69, 92)),
                (new com.supermap.data.Color(255, 221, 103)),
                (new com.supermap.data.Color(255, 138, 92)),
                (new com.supermap.data.Color(68, 68, 68))});
        try {
            //设置图例颜色
            mHeatMap.setColorScheme(colorScheme);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 实时数据
     */
    private Timer realTimer = null;

    private void realTimeData(boolean bSelected) {
//        heatMap.removeAllData();
        //清除数据
        if (timeLine != null) {
            timeLine.dispose();
            timeLine = null;
            mHeatMap.removeAllData();
        }
        //如果已经选中，则停止
        if (bSelected) {
            if (realTimer == null) {
                realTimer = new Timer();
            } else
                realTimer.cancel();
        } else {
            if (realTimer != null) {
                realTimer.cancel();
                realTimer = null;
                mHeatMap.setUpdataInterval(0);
                return;
            }
        }
        //清除数据
        if (layout_timeline.getVisibility() == View.VISIBLE) {
            layout_timeline.setVisibility(View.GONE);
        }
        realTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Random rand = new Random();
                int nCount = mHotDatas.size();//数据数量
                ArrayList<ChartPoint> arrTmp = new ArrayList<ChartPoint>();//临时数据
                //填写数据
                for (int i = 0; i < nCount; i++) {
                    int index = rand.nextInt(mHotDatas.size());
                    {
                        ChartPoint weightPoint = mHotDatas.get(index);
                        float dWeight = rand.nextInt(100) + 1;
                        weightPoint.setWeighted(dWeight);//设置权重
                        arrTmp.add(weightPoint);//添加权重点
                    }
                }
                mHeatMap.addChartDatas(arrTmp);//添加数据
            }
        }, 50, 2000);
        //图例
        ColorScheme colorScheme = new ColorScheme();
        float[] ranges = new float[]{0, 100, 200, 300};
        //设置图例范围
        colorScheme.setSegmentValue(ranges);
        //设置显示颜色（注意：颜色必须与范围的个数一致，否则设置失败，显示默认的风格）
        colorScheme.setColors(new com.supermap.data.Color[]{
                (new com.supermap.data.Color(207, 69, 92)),
                (new com.supermap.data.Color(255, 221, 103)),
                (new com.supermap.data.Color(255, 138, 92)),
                (new com.supermap.data.Color(68, 68, 68))});
        try {
            //设置图例
            mHeatMap.setColorScheme(colorScheme);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHeatMap.setUpdataInterval(2.0f);
    }

    /**
     * 时空数据
     */
    private void timeData() {
        //清除数据
        btn_RealTime.setSelected(false);
        //移除所有数据
        mHeatMap.removeAllData();
        //实时数据更新间隔时间
        mHeatMap.setUpdataInterval(0);
        if (realTimer != null) {
            realTimer.cancel();
            realTimer = null;
        }

        Random rand = new Random();

        ArrayList<Point2D> targetCitys = new ArrayList<Point2D>();
        int[] Steps = new int[mHotDatas.size() + 1];
        int nSteps = 10;//时间轴上时间点的个数
        for (int i = 0; i < mHotDatas.size(); i++) {

            int Step = rand.nextInt(nSteps) + 1;
            int city = rand.nextInt(10);
            //存数据
            if (city < 3) {targetCitys.add(new Point2D(1166.26, -1069)); }
            //存数据
            else if (city >= 3 && city < 6) { targetCitys.add(new Point2D(5070, -6458)); }
            //存数据
            else if (city >= 6 && city < 8) { targetCitys.add(new Point2D(7179, -4220)); }
            //存数据
            else { targetCitys.add(new Point2D(6264, -1256)); }
            Steps[i] = Step;
        }

        //设置每个时间节点数据
        for (int Step = 0; Step < nSteps; Step++) {
            //临时数据
            ArrayList<ChartPoint> arrTmp = new ArrayList<ChartPoint>();
            //存入每个阶段数据
            for (int i = 0; i < mHotDatas.size(); i++) {
                //图标点
                ChartPoint weightPoint = mHotDatas.get(i);
                //获取当前阶段
                int step = Steps[i];
                //当前数据
                Point2D targetCity = targetCitys.get(i);
                //权重
                float dWeight = rand.nextInt(30);//+ rand.nextInt(20);
                //x偏移量
                double offsetX = (targetCity.getX() - weightPoint.getPoint().getX());
                //y偏移量
                double offsetY = (targetCity.getY() - weightPoint.getPoint().getY());

                float CurrentStep = Step;
                if (Step > step) {
                    CurrentStep = step;
                }
                if (Step == 0)
                    CurrentStep = 2f;
                double dX = weightPoint.getPoint().getX() + (offsetX / step) * CurrentStep;
                double dY = weightPoint.getPoint().getY() + (offsetY / step) * CurrentStep;
                arrTmp.add(new ChartPoint(new Point2D(dX, dY), dWeight));
            }
            //插入统计数据表
            mHeatMap.insertChartDataset(arrTmp, String.valueOf(nSteps - Step) + "时", 0);
        }
        //渲染时空数据,时间间隔 单位秒，默认2s
        mHeatMap.setPlayInterval(1.0f);
        //是否循环播放,默认off
        mHeatMap.setIsLoopPlay(true);
        layout_timeline.setVisibility(View.VISIBLE);
        //时间线
        timeLine = new TimeLine(layout_timeline, getActivity());
        //时间线添加数据
        timeLine.addChart(mHeatMap);
        //加载时间线
        timeLine.load();
        //开始播放
        mHeatMap.startPlay();
    }

    /**
     * 聚合图
     */
    private void initPolymeMap() {
        mPolymerChart = new PolymerChart(getActivity(), mMapView);
        //设置聚合点显示最大半径
        mPolymerChart.setMaxRadius(20);
        //设置数据
        mPolymerChart.addChartDatas(mPolymerDatas);
        //设置标题
        mPolymerChart.setTitle("学校聚合图");
        //设置标题大小
        mPolymerChart.setTitleSize(18);
    }

    /**
     * 关系图
     */
    private void initRelationMap() {
        ArrayList<RelationalChartPoint> datas = new ArrayList<RelationalChartPoint>();
        //设置数据
        ArrayList<RelationalChartPoint> targetArr = new ArrayList<RelationalChartPoint>();
        //准备数据
        for (int num = 0; num < mRelativeDatas.size(); num++) {
            //关系点
            RelationalChartPoint relPoint = mRelativeDatas.get(num);
            //清空关系点
            relPoint.getRelationalPoints().clear();
            //获取关系点名称
            String name = relPoint.getRelationName();
            //如果为一下点，则存入
            if (
                    name.equalsIgnoreCase("吉林炭素集团长春粮尿病专科医院") ||
                            name.equalsIgnoreCase("市职业病医院") ||
                            name.equalsIgnoreCase("二二八医院") ||
                            name.equalsIgnoreCase("市医院") ||
                            name.equalsIgnoreCase("省监狱管理局中心医院")) {
                targetArr.add(relPoint);
            }
            datas.add(relPoint);
        }
        //设置关系
        for (int targetArrNum = 0; targetArrNum < targetArr.size(); targetArrNum++) {
            //创建关系点
            RelationalChartPoint relPoint1 = targetArr.get(targetArrNum);

            for (int rand = 0; rand < targetArr.size(); rand++) {
                RelationalChartPoint relPoint = targetArr.get(rand);
                //设置权重
                if (relPoint.getRelationName().equalsIgnoreCase("吉林炭素集团长春粮尿病专科医院")) {
                    relPoint.setWeighted(50); }
                    else if (relPoint.getRelationName().equalsIgnoreCase("市职业病医院")) {
                    relPoint.setWeighted(40); }
                    else if (relPoint.getRelationName().equalsIgnoreCase("二二八医院")) {
                    relPoint.setWeighted(36); }
                    else if (relPoint.getRelationName().equalsIgnoreCase("市医院")) {
                    relPoint.setWeighted(41); }
                    else if (relPoint.getRelationName().equalsIgnoreCase("省监狱管理局中心医院")) {
                    relPoint.setWeighted(65); }
                    //将relPoint1进行点位关联，不包含本身
                if (relPoint1.getRelationName().equals(relPoint.getRelationName()) == false) {
                    relPoint1.getRelationalPoints().add(relPoint);
                }
            }
        }
        mRelationChart = new RelationPointChart(getActivity(), mMapView);
        //设置动画
        mRelationChart.setIsAnimation(true);
        //设置线宽
        mRelationChart.setLineWidth(1.75f);
        //设置辐射半径
        mRelationChart.setMaxRadius(30);
        //设置关联数据
        mRelationChart.addChartRelationDatas(targetArr);
        //设置标题
        mRelationChart.setTitle("主医院关系图");
        //设置标题字符
        mRelationChart.setTitleSize(18);
        //设置图例
        ColorScheme colorScheme = mRelationChart.getColorScheme();
        //设置图例颜色
        colorScheme.setColors(new com.supermap.data.Color[]{
                new com.supermap.data.Color(23, 44, 60),
                new com.supermap.data.Color(39, 72, 98),
                new com.supermap.data.Color(82, 82, 82),
                new com.supermap.data.Color(65, 65, 65),
                new com.supermap.data.Color(49, 49, 49),
                new com.supermap.data.Color(202, 62, 71)});

        try {
            //设置图例
            mRelationChart.setColorScheme(colorScheme);

        } catch (Exception e) {

        }
    }

    /**
     * 格网热力图
     */
    private void initGridHotMap() {
        mGridHotChart = new GridHotChart(getActivity(), mMapView);
        //设置数据
        mGridHotChart.addChartDatas(m30Wdatas);
        //设置格网大小
        mGridHotChart.setGridSize(40);
        //设置标题
        mGridHotChart.setTitle("建筑物格网热力图");
        //设置标题大小
        mGridHotChart.setTitleSize(18);
        //设置图例范围
        float[] ranges = new float[]{0, 50, 80};
        //调色板类
        ColorScheme colorScheme = new ColorScheme();
        //设置图例范围
        colorScheme.setSegmentValue(ranges);
        //设置显示颜色
        colorScheme.setColors(new com.supermap.data.Color[]{
                (new com.supermap.data.Color(249, 210, 118)),
                (new com.supermap.data.Color(173, 29, 69)),
                (new com.supermap.data.Color(131, 20, 44))});
        try {
            //设置图例的颜色表
            mGridHotChart.setColorScheme(colorScheme);
        } catch (Exception e) {
        }

    }

    //密度图
    private void initDensityPoint() {
        mDensithart = new PointDensityChart(getActivity(), mMapView);
        //设置原始点半径，单位pixel
        mDensithart.setRadious(5);
        //设置数据
        mDensithart.addChartDatas(m30Wdatas);
        //设置标题
        mDensithart.setTitle("建筑物密度图");
        //设置标题大小
        mDensithart.setTitleSize(18);
        //设置图例范围
        float[] ranges = new float[]{0, 100, 200};
        //调色板类
        ColorScheme colorScheme = new ColorScheme();
        //设置图例范围
        colorScheme.setSegmentValue(ranges);
        //设置显示颜色
        colorScheme.setColors(new com.supermap.data.Color[]{
                (new com.supermap.data.Color(131, 20, 44)),
                (new com.supermap.data.Color(173, 29, 69)),
                (new com.supermap.data.Color(249, 210, 118))});
        try {
            //设置图例的颜色表
            mDensithart.setColorScheme(colorScheme);
        } catch (Exception e) {
        }

    }

    //设置饼图，折线图，柱状图需要用到的图层


    /**
     * 创建饼图
     */
    private void CreatePieChart() {
        mpieChart.setLayer(layer);
        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector) layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        //获取图标显示内容的ID
        int[] IDs = {420, 29, 419, 33, 358};
        //设置显示的颜色
        int[] colors = {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)};
        //图表标签值
        String[] labels = {"武阳", "东和", "中和", "化安", "安和"};

        int i = 0;
        String field_Area = "SmArea";    //数据中的属性名
        boolean isTrue = false;
        for (i = 0; i < IDs.length; i++) {
            //通过ID获取对应的记录
            isTrue = recordset.seekID(IDs[i]);
            if (isTrue) {
                //获取对应的属性值
                Object value = recordset.getFieldValue(field_Area);
                mFiledValue = (Double) value;
                PieChartData data = new PieChartData();
                //设置图标子项的颜色
                data.setColor(colors[i]);
                //设置图表子项关系的几何对象SmID, 和图表所关联的图层数据集对应
                data.setGeoID(IDs[i]);
                //设置图表子项的标签
                data.setLabel(labels[i]);
                //设置图表子项的数值，每一个饼状图的子项都只有一个数值
                data.setValue(mFiledValue);
                //添加图表数据
                mpieChart.addData(data);
            }
        }
        //设置图表标题
        mpieChart.setChartTitle("各地水稻种植面积(单位:m²)");
        mpieChart.setChartTitleSize(18);
        //获取图例LegendView
        LegendView legendView = mpieChart.getLegendView();
        //设置图例子项宽度
        legendView.setColumnWidth(legendView.getColumnWidth() + 30);
        legendView.setNumColumns(5);
        //修改布局后需调用该方法重新布局
        mpieChart.reLayout();
        //关闭记录集
        recordset.close();
        //释放记录集所占资源
        recordset.dispose();
    }

    //柱状图
    public void CreateBarChart() {

        mbarChart.setLayer(layer);
        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector) layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);

        //获取图标显示内容的ID
        int[] IDs = {420, 29, 419, 33, 358};
        //设置显示的颜色
        int[] colors = {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)};
        //图表标签值
        String[] labels = {"武阳", "东和", "中和", "化安", "安和"};
        //数据中的属性名
        String field_Area = "SmArea";
        boolean isTrue = false;
        BarChartData barChartData = new BarChartData();
        for (int i = 0; i < IDs.length; i++) {
            //通过ID获取对应的记录
            isTrue = recordset.seekID(IDs[i]);
            if (isTrue) {
                Object value = recordset.getFieldValue(field_Area);
                if (value == null) {
                    recordset.moveNext();
                    continue;
                }
                double v1 = (Double) value;
                //添加数据
                barChartData.addData(labels[0], colors[i], v1);
                //设置id
                barChartData.setGeoID(IDs[i]);

            }
        }
        //添加数据
        mbarChart.addData(barChartData);
        //设置图表标题
        mbarChart.setChartTitle("各地水稻种植面积(单位:m²)");
        mbarChart.setChartTitleSize(18);
        mbarChart.setValueAlongXAxis(true);
        for (int i = 0; i < labels.length; i++) {
            //添加X轴标签
            mbarChart.addXLabel(labels[i]);
        }
        //获取图例LegendView
        LegendView legendView = mbarChart.getLegendView();
        //设置图例子项宽度
        legendView.setColumnWidth(mMapControl.getWidth() / 2);
        legendView.setNumColumns(0);
        mbarChart.reLayout();
        //关闭记录集
        recordset.close();
        //释放记录集所占资源
        recordset.dispose();

    }

    public void CreateLineChart() {
        mlineChart.setLayer(layer);
        //获取用来创建图表的图层所对应的数据集
        DatasetVector datasetVector = (DatasetVector) layer.getDataset();
        //获取记录集
        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);

        //获取图标显示内容的ID
        int[] IDs = {420, 29, 419, 33, 358};
        //设置显示的颜色
        int[] colors = {
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
        };
        //图表标签值
        String[] labels = {"武阳", "东和", "中和", "化安", "安和"};

        String field_Area = "SmArea";    //数据中的属性名

        int[] years = {1994, 1997, 1998, 1999};

        LineChartData lineChartData = new LineChartData();
        lineChartData.setColor(colors[0]);    //设置图标子项的颜色
//        lineChartData.setGeoID(IDs[i]);		//设置图表子项关系的几何对象SmID
        lineChartData.setLabel("各地种植面积");    //设置图表子项的标签

        boolean isTrue = false;
        for (int i = 0; i < IDs.length; i++) {
            isTrue = recordset.seekID(IDs[i]);    //通过ID获取对应的记录
            if (isTrue) {
                Object value = recordset.getFieldValue(field_Area);    //获取对应的属性值
                Double va = (Double) value;
                lineChartData.addValue(va);        //添加数值
            }
        }
        mlineChart.addData(lineChartData);    //添加图表数据

        for (int i = 0; i < labels.length; i++) {
            mlineChart.addXLabel(labels[i] + "");    //添加X轴标签
        }

        mlineChart.setChartTitle("各地水稻种植面积(单位:m²)");    //设置图表标题
        mlineChart.setChartTitleSize(18);

        LegendView legendView = mlineChart.getLegendView();    //获取图例LegendView
        legendView.setColumnWidth(mMapControl.getWidth() / 2);//设置图例子项宽度
        legendView.setNumColumns(1);
        mlineChart.reLayout();

        recordset.close();    //关闭记录集
        recordset.dispose();//释放记录集所占资源
    }


    //清除
    public void clear() {
        if (mPolymerChart != null)
            mPolymerChart.dispose();
        if (mHeatMap != null)
            mHeatMap.dispose();
        if (mDensithart != null)
            mDensithart.dispose();
        if (mRelationChart != null)
            mRelationChart.dispose();
        if (mGridHotChart != null)
            mGridHotChart.dispose();
//
        mPolymerChart = null;
        mHeatMap = null;
        mDensithart = null;
        mRelationChart = null;
        mGridHotChart = null;

        if (realTimer != null) {
            realTimer.cancel();
            realTimer = null;
        }
        if (timeLine != null) {
            timeLine.dispose();
            timeLine = null;
        }
        btn_RealTime.setSelected(false);

        mMapControl.getMap().getTrackingLayer().clear();

        layout_timeline.setVisibility(View.GONE);
        layout_rightMenu.setVisibility(View.GONE);
        layou_chartView.setVisibility(View.GONE);
    }
}
