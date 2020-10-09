package com.supermap.imobile.fragment;

import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.supermap.analyst.BufferAnalystGeometry;
import com.supermap.analyst.BufferAnalystParameter;
import com.supermap.analyst.BufferEndType;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.analyst.spatialanalyst.OverlayAnalyst;
import com.supermap.analyst.spatialanalyst.OverlayAnalystParameter;
import com.supermap.data.Color;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.EncodeType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.SpatialQueryMode;
import com.supermap.data.StatisticMode;
import com.supermap.imobile.Dialog.ColorPickDialog;
import com.supermap.imobile.Pop.QueryResultPopup;
import com.supermap.imobile.myapplication.R;
import com.supermap.imobile.utils.Gesture;
import com.supermap.mapping.Action;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.TrackingLayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 我的分析界面，可以进行路径分析，缓冲区分析，叠加分析，区域查询，关键字查询
 */
public class MyDataAnalystFragment extends Fragment {

    MapControl mMapControl;

    Layers layers;//图层集

    boolean isLongPressEnable = false;//是否长按
    boolean isStartPoint = false;//起点
    boolean isEndPoint = false;//终点

    Map mMap;
    //路径分析所需要用到的二维点集合
    Point2Ds mFindpathPoint2Ds = null;
    //跟踪图层
    TrackingLayer mTrackingLayer;
    //工作空间中的数据源
    Datasource mDatasource;
    //添加起点
    ImageButton btn_add_startpoint;
    //添加终点
    ImageButton btn_add_endpoint;
    //缓冲区分析
    ImageButton btn_buffer;
    //叠加分析
    ImageButton btn_overlay;
    //设置缓冲区半径
    EditText bufferRadiusText;
    //关键字查询
    EditText mKeyQuery;
    //缓冲区半径值
    int bufferRadius;
    //被选中的记录集
    Recordset selectRecord;
    //查询结果窗体
    QueryResultPopup queryResultPopup;
    //设置线颜色
    ImageButton btn_line_color;
    //设置填充颜色
    ImageButton btn_fill_color;
    //路径分析
    ImageButton btn_findpath;

    //二级菜单
    //路径分析
    LinearLayout layout_findpath;
    //关键字查询
    LinearLayout layout_query_key;
    //叠加分析
    LinearLayout layout_overlay;
    //缓冲区查询
    LinearLayout layout_buffer;
    //设置
    LinearLayout layout_setting;
    //设置参数
    LinearLayout layout_bufferradious;
    //
    LinearLayout layout_popshow;

    //空间分析
    private TransportationAnalyst mTransAnalyst = null;
    //空间分析结果
    private TransportationAnalystResult mTransAnalystResult = null;
    //手势处理
    Gesture gesture = null;
    //设置默认线颜色值
    int[] mLineColor={15,36,62};
    //设置默认填充颜色值
    int[] mFillColor={15,36,62};


    /**
     *
     */
    public MyDataAnalystFragment() {

    }

    public MyDataAnalystFragment(MapControl mMapControl) {
        this.mMapControl = mMapControl;
        layers = mMapControl.getMap().getLayers();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mydataanalyst, container, false);
        //设置动画效果
        LayoutTransition transition = new LayoutTransition();
        container.setLayoutTransition(transition);
        //界面初始化
        initView(rootView);
        //数据准备
        StartPreparDate();
        return rootView;
    }

    //数据准备
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
                initAnalystEnvironment();
                dialog.dismiss();
            }
        }).start();
    }

    /**
     * 视图加载
     *
     * @param view
     */
    private void initView(View view) {

        mMap = mMapControl.getMap();
        gesture = new Gesture(mMap.getMapView());
        mMapControl.setGestureDetector(new GestureDetector(LongTouchListener));
        mMapControl.addGeometrySelectedListener(geometrySelectedListener);


        layout_findpath = view.findViewById(R.id.linear_findpath);
        layout_query_key = view.findViewById(R.id.linear_query_key);
        layout_overlay = view.findViewById(R.id.layout_overlay);
        layout_buffer = view.findViewById(R.id.layout_buffer);
        layout_setting = view.findViewById(R.id.layout_setting);
        bufferRadiusText = view.findViewById(R.id.bufferRadiusText);
        layout_bufferradious = view.findViewById(R.id.layout_radious);
        layout_popshow = view.findViewById(R.id.layout_popshow);
        mKeyQuery = view.findViewById(R.id.edittext_query);


        view.findViewById(R.id.clear).setOnClickListener(listener);
        view.findViewById(R.id.findpath_analyst).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_buffer).setOnClickListener(listener);
        view.findViewById(R.id.btn_quety_key).setOnClickListener(listener);
        view.findViewById(R.id.query_Geogion).setOnClickListener(listener);
        view.findViewById(R.id.query_key).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_overlay).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_setting).setOnClickListener(listener);
        view.findViewById(R.id.btn_popshow).setOnClickListener(listener);

        btn_findpath=(ImageButton) view.findViewById(R.id.btn_findpath);
        btn_add_startpoint = (ImageButton)view.findViewById(R.id.add_start);
        btn_add_endpoint = (ImageButton)view.findViewById(R.id.add_end);
        btn_buffer =(ImageButton) view.findViewById(R.id.imagebtn_buffer);
        btn_overlay = (ImageButton)view.findViewById(R.id.imagebtn_overlay);
        btn_line_color=(ImageButton)view.findViewById(R.id.line_color);
        btn_fill_color=(ImageButton)view.findViewById(R.id.fill_color);

        btn_findpath.setOnClickListener(listener);
        btn_add_startpoint.setOnClickListener(listener);
        btn_add_endpoint.setOnClickListener(listener);
        btn_buffer.setOnClickListener(listener);
        btn_overlay.setOnClickListener(listener);
        btn_line_color.setOnClickListener(listener);
        btn_fill_color.setOnClickListener(listener);



        //获取屏幕宽度
        int width = getResources().getDisplayMetrics().widthPixels;
        //获取宽度
        int height = mMapControl.getHeight();
        //查询结果窗体
        queryResultPopup = new QueryResultPopup(mMapControl.getMap().getMapView(), width, height);
        //设置查询结果窗体监听
        queryResultPopup.setPopshowListener(popShowListener);

    }

    //初始化地图控件，数据对象，分析控件, 及其环境
    private void initAnalystEnvironment() {
        mDatasource = mMap.getWorkspace().getDatasources().get(0);//获取数据源

        mTrackingLayer = mMap.getTrackingLayer();
        mFindpathPoint2Ds = new Point2Ds();

        initNetworkAnaystEnvironment();
    }

    // 定义并初始化网络分析环境设置
    private void initNetworkAnaystEnvironment() {

        //网络分析设置
        TransportationAnalystSetting transAnalystSetting = new TransportationAnalystSetting();
        //网络分析数据集
        transAnalystSetting.setNetworkDataset((DatasetVector) mDatasource.getDatasets().get("RoadNet"));
        //设置网络数据集中标志弧段 ID 的字段
        transAnalystSetting.setEdgeIDField("SmEdgeID");
        //设置网络数据集中标识结点 ID 的字段
        transAnalystSetting.setNodeIDField("SmNodeID");
        //设置存储弧段名称的字段
        transAnalystSetting.setEdgeNameField("roadName");
        //设置容限
        transAnalystSetting.setTolerance(89);

        //权值字段信息集合类
        WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
        //权值字段信息类
        WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
        //设置正向权值字段
        weightFieldInfo.setFTWeightField("smLength");
        //设置反向权值字段
        weightFieldInfo.setTFWeightField("smLength");
        //设置权值字段信息的名称
        weightFieldInfo.setName("length");
        weightFieldInfos.add(weightFieldInfo);

        //设置权值字段信息集合对象
        transAnalystSetting.setWeightFieldInfos(weightFieldInfos);
        //设置网络数据集中标志弧段起始结点 ID 的字段
        transAnalystSetting.setFNodeIDField("SmFNode");
        //设置网络数据集中标志弧段终止结点 ID 的字段
        transAnalystSetting.setTNodeIDField("SmTNode");

        // 初始化网络分析对象
        mTransAnalyst = new TransportationAnalyst();
        //设置交通网络分析环境设置对象
        mTransAnalyst.setAnalystSetting(transAnalystSetting);
        //加载网络模型
        mTransAnalyst.load();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_findpath:
                    clearQuery();
                    isShowFindPathLayout();
                    break;
                case R.id.add_start:
                    isLongPressEnable = true;
                    isStartPoint = true;
                    btn_add_startpoint.setEnabled(false);
                    shown("请长按设置起点");
                    break;
                case R.id.add_end:
                    isLongPressEnable = true;
                    isEndPoint = true;
                    btn_add_endpoint.setEnabled(false);
                    shown("请长按设置终点");
                    break;
                case R.id.findpath_analyst:
                    //完成起点或者终点的设置后才能进行路径分析
                    if (mFindpathPoint2Ds.getCount() < 2) {
                        shown("请设置起点或者终点");
                    } else {
                        startPathAnalyse();
                    }
                    break;
                case R.id.clear:
                    clear();
                    break;
                case R.id.imagebtn_buffer:
                    startBufferAnalyse();
                    btn_buffer.setEnabled(false);
                    break;
                case R.id.query_Geogion:
                    clearAnalyst();
                    layout_query_key.setVisibility(View.GONE);
                    shown("绘制查询区域");
                    gesture.draw();
                    gesture.setDrawnListener(new Gesture.DrawnListener() {
                        @Override
                        public void drawnGeometry(Geometry geoRegion) {
                            query(geoRegion, DatasetType.POINT);
                            geoRegion.dispose();
                        }
                    });
                    break;
                case R.id.query_key:
                    clearAnalyst();
                    isShowQueryKeyLayout();
                    break;
                case R.id.btn_quety_key:
                    closeKeyBoard();
                    query(mKeyQuery.getText().toString());
                    break;
                case R.id.imagebtn_overlay:
                    startOverlayAnalyse();
                    btn_overlay.setEnabled(false);
                    break;
                case R.id.imagebtn_setting:
                    if (layout_bufferradious.getVisibility() == View.GONE) {
                        layout_bufferradious.setVisibility(View.VISIBLE);
                    } else {
                        layout_bufferradious.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_popshow:
                    queryResultPopup.show();
//                    layout_popshow.setVisibility(View.GONE);
                    break;
                case R.id.line_color:
                    ColorPickDialog colorPickDialog=new ColorPickDialog(getActivity());
                    colorPickDialog.setPickColorListener(new ColorPickDialog.OnPickColorListener() {
                        @Override
                        public void getColor(int r, int g, int b) {
                            btn_line_color.setBackgroundColor(android.graphics.Color.argb(255,r,g,b));
                            mLineColor[0]=r;mLineColor[1]=g;mLineColor[2]=b;

                        }
                    });
                    colorPickDialog.show();
                    break;
                case R.id.fill_color:
                    ColorPickDialog colorPickDialog2=new ColorPickDialog(getActivity());
                    colorPickDialog2.setPickColorListener(new ColorPickDialog.OnPickColorListener() {
                        @Override
                        public void getColor(int r, int g, int b) {
                            btn_fill_color.setBackgroundColor(android.graphics.Color.argb(255,r,g,b));
                            mFillColor[0]=r;mFillColor[1]=g;mFillColor[2]=b;
                        }
                    });
                    colorPickDialog2.show();
                    break;
            }
        }
    };

    //属性查询结果pop显示监听，根据其状态来控制开关
    QueryResultPopup.PopShowListener popShowListener = new QueryResultPopup.PopShowListener() {
        @Override
        public void Popshow(boolean isshow) {
            if (isshow) {
                layout_popshow.setVisibility(View.GONE);
            } else {
                layout_popshow.setVisibility(View.VISIBLE);
            }
        }
    };
    //长按监听
    GestureDetector.SimpleOnGestureListener LongTouchListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            //允许获取长按事件结果时才响应
            if (isLongPressEnable) {
                //获取长按设置的点的坐标，并转换为经纬坐标
                getPoints(e, isStartPoint, isEndPoint);
                //设置一个点后，使长按响应无效
                isLongPressEnable = false;
                //判断设置的是起点还是终点
                if (isStartPoint) {
                    isStartPoint = false;
                }
                if (isEndPoint) {
                    isEndPoint = false;
                }

            }
        }
    };

    GeometrySelectedListener geometrySelectedListener = new GeometrySelectedListener() {
        @Override
        public void geometrySelected(GeometrySelectedEvent Event) {
            selectRecord = Event.getLayer().getSelection().toRecordset();
        }

        @Override
        public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arrayList) {

        }

        @Override
        public void geometryMultiSelectedCount(int i) {

        }
    };

    /**
     * 获取屏幕上的点，并转换成地图坐标
     *
     * @param event
     * @param bStartPoint
     * @param bEndPoint
     */
    private void getPoints(MotionEvent event, boolean bStartPoint, boolean bEndPoint) {
        //获取屏幕上的点击处的点坐标(x, y)
        int x = (int) event.getX();
        int y = (int) event.getY();
        isStartPoint = bStartPoint;
        isEndPoint = bEndPoint;

        // 转换为地图二维点
        Point2D point2D = mMap.pixelToMap(new Point(x, y));

        // 设置标注
        CallOut callOut = new CallOut(getActivity());
        callOut.setStyle(CalloutAlignment.BOTTOM);             // 设置标注点的对齐方式：下方对齐
        callOut.setCustomize(true);                            // 设置自定义背景
        callOut.setLocation(point2D.getX(), point2D.getY());   // 设置标注点坐标

        // 投影转换，转换为经纬坐标系
        if (mMap.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
            Point2Ds point2Ds = new Point2Ds();
            point2Ds.add(point2D);
            PrjCoordSys destPrjCoordSys = new PrjCoordSys();
            // 设置目标坐标系类型
            destPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
            // 获取当前地图坐标系
            PrjCoordSys sourPrjCoordSys = mMap.getPrjCoordSys();
            // 转换投影坐标
            CoordSysTranslator.convert(point2Ds, sourPrjCoordSys, destPrjCoordSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

            point2D = point2Ds.getItem(0);
        }

        ImageView imageView = new ImageView(getActivity());

        // 添加点到线
        if (isStartPoint && !isEndPoint) {
            // 显示起点
            imageView.setBackgroundResource(R.drawable.start_point);
            //设置callout背景
            callOut.setContentView(imageView);
            //添加callout
            mMap.getMapView().addCallout(callOut);
            // 添加起点
            mFindpathPoint2Ds.add(point2D);

            isStartPoint = false;

        } else if (!isStartPoint && isEndPoint) {
            // 显示终点
            imageView.setBackgroundResource(R.drawable.end_point);
            callOut.setContentView(imageView);
            mMap.getMapView().addCallout(callOut);

            // 添加终点
            mFindpathPoint2Ds.add(point2D);

            isEndPoint = false;

        } else {

        }
    }

    /**
     * 开始路径分析
     */
    public void startPathAnalyse() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("路径分析中....");
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                boolean isGetResult = analysePath();

                dialog.dismiss();
                if (isGetResult) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //设置不可见
                            layout_buffer.setVisibility(View.VISIBLE);
                            layout_setting.setVisibility(View.VISIBLE);
                            layout_findpath.setVisibility(View.GONE);
                            btn_findpath.setEnabled(false);

                        }
                    });

                } else {
                    // 显示分析失败提示
                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getActivity(), "分析路径失败", Toast.LENGTH_SHORT).show();
                        }
                    };
                    getActivity().runOnUiThread(action);
                }
            }
        }).start();

    }

    /**
     * 最短路径分析，完成起点和终点设置后，可以调用该方法
     *
     * @return
     */
    public boolean analysePath() {

        // 定义并设置网络分析参数
        TransportationAnalystParameter transParameter = new TransportationAnalystParameter();
        //设置权值字段信息的名称
        transParameter.setWeightName("length");
        //设置分析时途经点的集合
        transParameter.setPoints(mFindpathPoint2Ds);
        //设置分析结果中是否包含结点的集合
        transParameter.setNodesReturn(true);
        //设置分析结果中是否包含途经弧段的集合
        transParameter.setEdgesReturn(true);
        //设置分析结果中是否包含行驶导引集合
        transParameter.setPathGuidesReturn(true);
        //设置分析结果中是否包含路由（GeoLineM）对象的集合
        transParameter.setRoutesReturn(true);
        try {
            mTransAnalystResult = mTransAnalyst.findPath(transParameter, false);

        } catch (Exception e) {
            mTransAnalystResult = null;
        }

        if (mTransAnalystResult == null) {
            return false;
        }
        // 显示分析结果
        showPathAnalystResult();
        return true;
    }

    /**
     * 显示路径分析结果
     */
    public void showPathAnalystResult() {
        int i = 0; // 循环计数

        // 清除跟踪层上标签为“result”的几何对象
        int count = mTrackingLayer.getCount();

        for (i = 0; i < count; i++) {
            int index = mTrackingLayer.indexOf("findpath");
            if (index != -1)
                mTrackingLayer.remove(index);
        }

        // 获取路径分析结果中的路由集合
        GeoLineM[] routes = mTransAnalystResult.getRoutes();
        if (routes == null) {
            System.out.println("获取路由集合失败");
            return;
        }
        for (i = 0; i < routes.length; i++) {
            GeoLineM geoLineM = routes[i];
            GeoStyle geoStyle = new GeoStyle();
            //设置线颜色
            geoStyle.setLineColor(new Color(255, 90, 0));
            //设置线宽
            geoStyle.setLineWidth(1);
            geoLineM.setStyle(geoStyle);
            mTrackingLayer.add(geoLineM, "findpath"); // 添加分析结果到跟踪层，并设置标签
        }
        mTrackingLayer.setShowGuideLine(true, routes, mFindpathPoint2Ds.getItem(0), mFindpathPoint2Ds.getItem(1));
        mMap.refresh();
    }

    /**
     * 开始缓冲区分析
     */
    public void startBufferAnalyse() {
        bufferRadius = Integer.parseInt(bufferRadiusText.getText().toString());
        if (bufferRadius <= 0) {
            shown("缓冲区半径无效，分析失败");
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("缓冲区分析中....");
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 创建缓冲区
                createBuffer();
                dialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout_overlay.setVisibility(View.VISIBLE);

                    }
                });
            }
        }).start();
    }

    /**
     * 创建缓冲区
     */
    public void createBuffer() {

        GeoRegion geometryBuffer = null;// 定义缓冲区分析结果对象
        // 设置缓冲分析几何对象,即跟踪层第一个几何对象,并将其转换成GeoLine类型
        int index = mTrackingLayer.indexOf("findpath");

        // 获取路径分析结果
        Geometry geometry = mTrackingLayer.get(index);
        // 将路由对象转换成线对象
        GeoLine geoLineForBuffer = ((GeoLineM) geometry).convertToLine();
        Geometry geoForBuffer = (Geometry) geoLineForBuffer;

        // 设置缓冲区分析参数
        BufferAnalystParameter bufferAnalystParameter = new BufferAnalystParameter();
        //设置（左）缓冲区的距离
        bufferAnalystParameter.setLeftDistance(bufferRadius);
        //设置右缓冲区的距离
        bufferAnalystParameter.setRightDistance(bufferRadius);
        //设置缓冲区端点类型
        bufferAnalystParameter.setEndType(BufferEndType.ROUND);

        // 设置投影坐标系
        PrjCoordSys prjCoordSys = mMap.getPrjCoordSys();

        // 生成缓冲区几何对象
        geometryBuffer = BufferAnalystGeometry.createBuffer(geoForBuffer, bufferAnalystParameter, prjCoordSys);

        // 设置几何对象风格
        GeoStyle style = new GeoStyle();
        //设置线颜色
        style.setLineColor(new Color(mLineColor[0], mLineColor[1], mLineColor[2]));
        //设置线符号
        style.setLineSymbolID(0);
        //设置线宽度
        style.setLineWidth(1);
        //设置填充符号
        style.setMarkerSymbolID(351);
        //设置填充符号宽度
        style.setMarkerSize(new com.supermap.data.Size2D(5, 5));
        //设置填充符号颜色
        style.setFillForeColor(new Color(mFillColor[0], mFillColor[1], mFillColor[2]));
        style.setFillOpaqueRate(70);

        geometryBuffer.setStyle(style);            // 设置缓冲区分析结果的风格
        mTrackingLayer.add(geometryBuffer, "");   // 添加缓冲区结果到跟踪层

        mMap.refresh();
    }


    /**
     * 开始叠加分析
     */
    public void startOverlayAnalyse() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("叠加分析中....");
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 叠加分析之 裁剪
                overlayAnalystClip();

                dialog.dismiss();

                // 分析结束后修改主界面
                Runnable action = new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 显示影响面积
//                        m_FrameLayout01.setVisibility(View.VISIBLE);
//                        DecimalFormat df = new DecimalFormat("0.000000");
//                        areaOverlayView.setText("" + df.format(clipArea));
                    }
                };
                getActivity().runOnUiThread(action);
            }
        }).start();
    }

    private String resultDatasetName = "resultDatasetClip";

    /**
     * 叠加分析之裁剪
     */
    public void overlayAnalystClip() {
        DatasetVector srcDataset = null;
        Geometry[] clipGeometries = new Geometry[1];                // 用于存放缓冲区分析结果对象，结果只有一个面对象
        DatasetVector resultDataset = null;
        DatasetVectorInfo resultDatasetInfo = new DatasetVectorInfo();

        // 获取将被裁剪的数据集
        srcDataset = (DatasetVector) mMapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets().get("ResidentialArea");
        // 获取缓冲区分析的结果，并放入几何对象集合中，获取的几何对象必须是面对象
        int geoCount = mTrackingLayer.getCount();
        for (int i = 0, j = 0; i < geoCount && j < 1; i++) {
            if (mTrackingLayer.get(i).getType() == GeometryType.GEOREGION) {
                clipGeometries[j] = mTrackingLayer.get(i);
                j++;
            }
        }
        // 删除已有的分析结果数据集
        boolean isContained = mMapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets().contains(resultDatasetName);
        if (isContained)
            mMapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets().delete(resultDatasetName);


        // 设置结果数据集信息
        resultDatasetInfo.setType(srcDataset.getType());
        resultDatasetInfo.setName(resultDatasetName);
        resultDatasetInfo.setEncodeType(EncodeType.NONE);
        // 创建结果数据集
        resultDataset = mMapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets().create(resultDatasetInfo);

        OverlayAnalystParameter overlayAnalystParameter = new OverlayAnalystParameter();
        overlayAnalystParameter.setTolerance(0.0112242);

        OverlayAnalyst.clip(srcDataset, clipGeometries, resultDataset, overlayAnalystParameter);

        // 显示结果前先清空跟踪层
        mTrackingLayer.clear();

        // 显示结果
        /***************************************************/
        // 设置几何对象的风格
        GeoStyle style = new GeoStyle();
        style.setLineColor(new Color(mLineColor[0],mLineColor[1],mLineColor[2]));
        style.setLineSymbolID(0);
        style.setLineWidth(1);
        style.setMarkerSymbolID(351);
        style.setMarkerSize(new com.supermap.data.Size2D(5, 5));
        style.setFillForeColor(new Color(mFillColor[0],mFillColor[1],mFillColor[2]));
        style.setFillOpaqueRate(70);

        // 定义图层风格
        LayerSettingVector m_LayerSettingVector = new LayerSettingVector();
        m_LayerSettingVector.setStyle(style);

        // 添加结果数据集到地图最上面的图层
        mMap.getLayers().add(resultDataset, true);
        // 设置图层风格
        mMap.getLayers().get(0).setAdditionalSetting(m_LayerSettingVector);

        mMap.refresh();
        // 获取影响面积
        Recordset recordset = resultDataset.getRecordset(false, CursorType.DYNAMIC);
        double clipArea = recordset.statistic("SMAREA", StatisticMode.SUM);
        DecimalFormat df = new DecimalFormat("0.00");
        shown("影响面积:" + String.valueOf(df.format(clipArea)) + " m²");
        recordset.dispose();
        /***************************************************/
    }

    /**
     * 根据绘制区域查询结果
     *
     * @param georegion
     * @param type
     */
    private void query(Geometry georegion, DatasetType type) {
        Layers layers = mMapControl.getMap().getLayers();
        queryResultPopup.clearData();
        for (int i = 0; i < layers.getCount(); i++) {
            Log.i("++++++++", String.valueOf(i));
            Dataset dataset = layers.get(i).getDataset();
            if (dataset != null) {
                if (dataset.getType().equals(type)) {
                    DatasetVector dv = (DatasetVector) dataset;
                    QueryParameter param = new QueryParameter();
                    //设置空间查询中的搜索对象
                    param.setSpatialQueryObject(georegion);
                    // 设置空间查询操作模式
                    param.setSpatialQueryMode(SpatialQueryMode.CONTAIN);
                    //设置查询所采用的游标类型
                    param.setCursorType(CursorType.STATIC);
                    //将查询结果存入记录集中
                    Recordset recordset = dv.query(param);
                    //将结果添加到pop显示
                    queryResultPopup.addResult(recordset);

                }
            }
        }
        queryResultPopup.show();
        GeoStyle style = new GeoStyle();
        style.setFillForeColor(new Color(190, 190, 190));
        style.setFillOpaqueRate(70);
        style.setLineColor(new Color(180, 180, 200));
        style.setLineWidth(1);
        georegion.setStyle(style);
        mTrackingLayer.add(georegion, "queryregion");
        mMap.refresh();

    }

    /**
     * 按关键字查询
     *
     * @param key 关键字
     */
    private void query(String key) {
        if (key.length() == 0) {
            shown("没有输入关键字");
            return;
        }
        Layers layers = mMapControl.getMap().getLayers();
        queryResultPopup.clearData();
        for (int i = 0; i < layers.getCount(); i++) {
            Dataset dataset = layers.get(i).getDataset();
            if (dataset != null) {
                if (dataset.getType().equals(DatasetType.POINT)) {
                    DatasetVector dv = (DatasetVector) dataset;
                    QueryParameter param = new QueryParameter();
                    param.setAttributeFilter("name like \"%" + key + "%\"");
                    Recordset recordset = dv.query(param);
                    queryResultPopup.addResult(recordset);

//                    if (recordset.getRecordCount() > 0) {
//                        CallOut callOut = new CallOut(mMap.getMapView().getContext());
//                        callOut.setLocation(recordset.getGeometry().getInnerPoint().getX(),recordset.getGeometry().getInnerPoint().getY());
//                        ImageView imageView = new ImageView(getActivity());
//                        imageView.setBackgroundResource(R.drawable.ic_poi);
//                        callOut.setCustomize(true);
//                        callOut.setContentView(imageView);
//                        callOut.setStyle(CalloutAlignment.BOTTOM);
//                        mMap.getMapView().addCallout(callOut);
//                    }
                }
            }
        }
        queryResultPopup.show();
    }

    public void clear() {
        clearAnalyst();
        clearQuery();
    }

    //提示信息
    private void shown(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }

    /**
     * 关闭键盘
     */
    private void closeKeyBoard() {
        //隐藏键盘
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mKeyQuery.getWindowToken(), 0);
    }

    private void isShowFindPathLayout() {
        if (layout_findpath.getVisibility() == View.GONE) {
            layout_findpath.setVisibility(View.VISIBLE);
        } else {
            layout_findpath.setVisibility(View.GONE);
        }
    }

    private void isShowQueryKeyLayout() {
        if (layout_query_key.getVisibility() == View.GONE) {
            layout_query_key.setVisibility(View.VISIBLE);
        } else {
            layout_query_key.setVisibility(View.GONE);
        }
    }

    private void clearAnalyst() {
        layout_findpath.setVisibility(View.GONE);
        layout_buffer.setVisibility(View.GONE);
        layout_bufferradious.setVisibility(View.GONE);
        layout_setting.setVisibility(View.GONE);
        layout_overlay.setVisibility(View.GONE);

        mFindpathPoint2Ds.clear();
        boolean isContained = mMapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets().contains(resultDatasetName);
        // 如果有分析结果数据集，就删除
        if (isContained) {
            mMap.getLayers().remove(0);
            mMapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets().delete(resultDatasetName);

        }

        isLongPressEnable = false;

        btn_add_endpoint.setEnabled(true);
        btn_add_startpoint.setEnabled(true);
        btn_buffer.setEnabled(true);
        btn_overlay.setEnabled(true);
        btn_findpath.setEnabled(true);


        mTrackingLayer.clear();
        mMap.getMapView().removeAllCallOut();

        mMapControl.setAction(Action.PAN);
        mMap.refresh();
    }

    private void clearQuery() {
        layout_popshow.setVisibility(View.GONE);
        layout_query_key.setVisibility(View.GONE);
        queryResultPopup.dismiss();
        if (mTrackingLayer != null) {
            mTrackingLayer.clear();
        }
        mMap.getMapView().removeAllCallOut();
        mMapControl.setAction(Action.PAN);
        mMap.refresh();
    }

    /**
     * 隐藏其他二级菜单栏
     */
    private void hideSecMenulayout(int i) {
        switch (i) {
            case 1:
                layout_query_key.setVisibility(View.GONE);
                queryResultPopup.dismiss();
                break;
            case 2:
                break;
            case 3:
                layout_findpath.setVisibility(View.GONE);
                layout_buffer.setVisibility(View.GONE);
                layout_query_key.setVisibility(View.GONE);
                layout_overlay.setVisibility(View.GONE);
                break;
            case 4:
                layout_findpath.setVisibility(View.GONE);
                layout_buffer.setVisibility(View.GONE);

                layout_overlay.setVisibility(View.GONE);
                break;
            case 5:
                layout_findpath.setVisibility(View.GONE);
                layout_buffer.setVisibility(View.GONE);

                layout_query_key.setVisibility(View.GONE);
                break;

        }

    }
}
