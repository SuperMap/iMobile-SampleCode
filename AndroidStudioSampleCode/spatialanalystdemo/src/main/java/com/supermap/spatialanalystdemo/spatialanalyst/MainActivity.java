package com.supermap.spatialanalystdemo.spatialanalyst;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;


import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.supermap.data.Workspace;
import com.supermap.mapping.MapView;
import com.supermap.mapping.MapControl;
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
import com.supermap.spatialanalystdemo.R;
import com.supermap.spatialanalystdemo.app.MyApplication;
import com.supermap.data.Color;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.Datasources;
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
import com.supermap.data.Recordset;
import com.supermap.data.StatisticMode;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Map;
import com.supermap.mapping.TrackingLayer;

/**
 * <p>
 * Title:空间分析
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
 *   展示如何对指定的路径进行缓冲区分析和叠加分析。
 * 2、Demo数据：数据目录："/SuperMap/Demos/Data/SpatialAnalystData/"
 *           地图数据："Changchun.smwu", "Changchun.udb", "Changchun.udd"
 *           许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *		TransportationAnalystSetting.setNetworkDataset();	       方法
 *		TransportationAnalystSetting.setEdgeIDField();		       方法
 *		TransportationAnalystSetting.setNodeIDField();		       方法
 *		TransportationAnalystSetting.setEdgeNameField();	       方法
 *		TransportationAnalystSetting.setWeightFieldInfos();	        方法
 *		TransportationAnalystSetting.setFNodeIDField();		        方法
 *		TransportationAnalystSetting.setTNodeIDField();		        方法
 *		TransportationAnalyst.setAnalystSetting();			         方法
 *		TransportationAnalyst.load();						         方法
 *
 *		TransportationAnalystParameter.setPoints();			         方法
 *		TransportationAnalystParameter.setNodesReturn();	         方法
 *		TransportationAnalystParameter.setEdgesReturn();	         方法
 *		TransportationAnalystParameter.setPathGuidesReturn();  方法
 *		TransportationAnalystParameter.setRoutesReturn();	          方法
 *		TransportationAnalyst.findPath();					          方法
 *      TransportationAnalystResult.getRoutes();               方法
 *      BufferAnalystParameter.setLeftDistance();              方法
 *		BufferAnalystParameter.setRightDistance();             方法
 *		BufferAnalystParameter.setEndType();                   方法
 *      BufferAnalystGeometry.createBuffer();                  方法
 *      TrackingLayer.add();                                   方法
 *
 * 4、功能展示
 *   (1)查找两点之间最佳路径；
 *   (2)缓冲区分析；
 *   (3)叠加分析。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
@SuppressLint("SdCardPath")
public class MainActivity extends Activity {

	// 定义按钮控件
	private ImageButton      btn_analyse_path     = null;
	private ImageButton      btn_clean            = null;
	private ImageButton      btn_create_buffer    = null;
	private ImageButton      btn_end_point        = null;
	private ImageButton      btn_entire           = null;
	private ImageButton      btn_overlay_analyst  = null;
	private ImageButton      btn_path_analyst     = null;
	private ImageButton      btn_start_point      = null;
	private ImageButton      btn_zoomIn           = null;
	private ImageButton      btn_zoomOut          = null;
	private ImageButton      btn_setting          = null;      // 缓冲区半径设置按钮，用于显示或隐藏缓冲区半径输入文本框

	// 定义文本控件
	private TextView         areaOverlayView      = null;      // 显示叠加分析影响面积
	private EditText         bufferRadiusText     = null;      // 缓冲区半径输入文本框

	// 定义布局控件
	private RelativeLayout    m_FrameLayout00     = null;      // 缓冲区半径设置布局
	private FrameLayout    m_FrameLayout01        = null;      // 叠加分析影响面积布局
	private LinearLayout   m_PathAnalystDropDown  = null;      // 路径分析的起点、终点设置按钮布局

	// 定义地图控件
	private MapView     m_mapView     = null;
	private Workspace   m_workspace   = null;
	private MapControl  m_mapControl  = null;
	private Map         m_Map         = null;

	// 定义主界面与地图控件
	private Point2Ds       m_Point2Ds       = null;
	private TrackingLayer  m_TrackingLayer  = null;

	// 定义数据空件
	private Datasources   m_Datasources = null;
	private Datasource    m_Datasource  = null;
	private Datasets      m_Datasets    = null;
	private DatasetVector m_DatasetLine = null;


	// 定义空间分析空件
	private TransportationAnalyst        m_TransAnalyst        = null;
	private TransportationAnalystResult  m_TransAnalystResult  = null;


	// 定义字符串常量
	private String  m_nodeID           = "SmNodeID";
	private String  m_edgeID           = "SmEdgeID";
	private String  roadDatasetName    = "RoadNet";
	private String  resultDatasetName  = "resultDatasetClip";

	// 定义布尔变量
	private boolean isStartPoint       = false;
	private boolean isEndPoint         = false;
	private boolean isLongPressEnable  = false;
	private boolean isExitEnable       = false;
	// 定义整型变量
	private int    dataSourceIndex = 0;
	private double clipArea        = 0;
	private int    bufferRadius    = 30;                          // 设置缓冲区半径默认值

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		boolean  isOpenMap = false;
		isOpenMap = initMap();                                    // 打开工作空间，打开地图
		if(isOpenMap){
			initAnalystEnvironment();                             // 初始化化空间分析所需的资源环境
			initView();                                           // 初始化界面控件
		}else{
			showInfo("Initialize Map failed.");
			System.out.println("Initialize Map failed.");
		}

	}

	/**
	 * 打开工作空间，显示地图
	 * @return
	 */
	public boolean initMap() {
		boolean isOpen = false;

		// 获取地图控件
		m_mapView   = (MapView) findViewById(R.id.mapView);
		m_mapControl = m_mapView.getMapControl();

		// 打开一个工作空间
		m_workspace = new Workspace();

		m_workspace = MyApplication.getInstance().getOpenedWorkspace();
		// 关联地图控件和工作空间
		m_mapControl.getMap().setWorkspace(m_workspace);

		// 打开地图
		isOpen = m_mapControl.getMap().open(m_workspace.getMaps().get(0));   // 默认打开第一幅地图
		if(!isOpen)
		{
			System.out.println("Open map failed.");
			showInfo("Open map failed. ");

			return false;
		}
//		m_mapControl.getMap().setFullScreenDrawModel(true);
		m_mapControl.getMap().refresh();

		return true;
	}

	/**
	 * 初始化化界面控件
	 */
	public void initView() {

		// 设置长按事件监听
		m_mapControl.setGestureDetector(new GestureDetector(longTouchListener));;

		btn_path_analyst = (ImageButton) findViewById(R.id.btn_path_analyst);
		btn_path_analyst.setOnClickListener(new ImageButtonOnClickListener());

		btn_start_point = (ImageButton) findViewById(R.id.btn_start_point);
		btn_start_point.setOnClickListener(new ImageButtonOnClickListener());

		btn_end_point = (ImageButton) findViewById(R.id.btn_end_point);
		btn_end_point.setOnClickListener(new ImageButtonOnClickListener());

		btn_analyse_path = (ImageButton) findViewById(R.id.btn_analyse_path);
		btn_analyse_path.setOnClickListener(new ImageButtonOnClickListener());

		btn_clean = (ImageButton) findViewById(R.id.btn_clear);
		btn_clean.setOnClickListener(new ImageButtonOnClickListener());

		btn_create_buffer = (ImageButton) findViewById(R.id.btn_create_buffer);
		btn_create_buffer.setOnClickListener(new ImageButtonOnClickListener());

		btn_overlay_analyst = (ImageButton) findViewById(R.id.btn_overlay_analyst);
		btn_overlay_analyst.setOnClickListener(new ImageButtonOnClickListener());

		btn_setting = (ImageButton) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(new ImageButtonOnClickListener());



		btn_entire = (ImageButton) findViewById(R.id.btn_entire);
		btn_entire.setOnClickListener(new ImageButtonOnClickListener());

		btn_zoomOut = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn_zoomOut.setOnClickListener(new ImageButtonOnClickListener());

		btn_zoomIn = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn_zoomIn.setOnClickListener(new ImageButtonOnClickListener());

		m_PathAnalystDropDown = (LinearLayout) findViewById(R.id.PathAnalyst_DropDown);

		m_FrameLayout01   = (FrameLayout) findViewById(R.id.frameLayout01);
		areaOverlayView = (TextView) findViewById(R.id.areaOverlayView);

		m_FrameLayout00   = (RelativeLayout) findViewById(R.id.frameLayout00);
		bufferRadiusText   = (EditText) findViewById(R.id.bufferRadiusText);

		bufferRadiusText.addTextChangedListener(textWatcher);
	}

	// 监听EditText中输入的变化，并获取缓冲区半径值
	@SuppressLint("NewApi") private TextWatcher textWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s)
		{

			String digitStr = bufferRadiusText.getText().toString();
			if (digitStr.length() >= 1) {
				int buf = Integer.valueOf(bufferRadiusText.getText().toString());
				if (buf <= 1000) {
					bufferRadius = buf;
				} else {
					showInfo("设置半径达到最大值 1000");
				}
			}
		}


		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}


		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

	};

	// 按钮点击事件监听
	private class ImageButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_path_analyst:               // 用于呼出路径分析设置界面

					// 如果起点、终点设置按钮未显示，则显示；反之隐藏
					if(m_PathAnalystDropDown.getVisibility() == View.VISIBLE){
						btn_path_analyst.setBackgroundResource(R.drawable.bg_white_round);
						m_PathAnalystDropDown.setVisibility(View.GONE);

					}else{
						btn_path_analyst.setBackgroundResource(R.drawable.bg_white_top_round);
						m_PathAnalystDropDown.setVisibility(View.VISIBLE);

					}
					break;

				case R.id.btn_start_point:

					// 起点和终点设置的互斥判断，即在完成设置起点或终点前，不可以开始设置另一个点
					if(isLongPressEnable){
						showInfo("请先完成终点的设置");
					}else{
						btn_start_point.setEnabled(false);
						isStartPoint = true;
						isLongPressEnable = true;
						showInfo("请长按设置起点");
					}

					break;

				case R.id.btn_end_point:

					if(isLongPressEnable){
						showInfo("请先完成起点的设置");
					}else{
						btn_end_point.setEnabled(false);
						isEndPoint = true;
						isLongPressEnable = true;

						showInfo("请长按设置终点");
					}

					break;

				case R.id.btn_analyse_path:
					// 完成起点和终点设置后才能进行路径分析
					if(m_Point2Ds.getCount()<2)
					{
						showInfo("请设置起点或终点");

					}else{
						btn_analyse_path.setEnabled(false);

						startPathAnalyse();                             // 开始路径分析

					}
					break;

				case R.id.btn_clear:

					// 清除分析结果，注意缓冲区半径与前一次输入的值一致
					m_mapView.removeAllCallOut();

					m_Point2Ds.clear();

					boolean isContained = m_Datasets.contains(resultDatasetName);
					if(isContained){                                     // 如果有分析结果数据集，就删除
						m_Map.getLayers().remove(0);
						m_Datasets.delete(resultDatasetName);

					}
					if(m_TrackingLayer.getCount()>0){
						m_TrackingLayer.clear();
					}

					// 隐藏部分按钮
					btn_path_analyst.setBackgroundResource(R.drawable.bg_white_round);
					m_PathAnalystDropDown.setVisibility(View.GONE);
					btn_create_buffer.setVisibility(View.GONE);
					btn_overlay_analyst.setVisibility(View.GONE);

					// 隐藏文本框
					m_FrameLayout01.setVisibility(View.GONE);

					m_FrameLayout00.setVisibility(View.GONE);

					// 设置按键可用
					btn_path_analyst.setEnabled(true);
					btn_analyse_path.setEnabled(true);
					btn_start_point.setEnabled(true);
					btn_end_point.setEnabled(true);
					btn_create_buffer.setEnabled(true);
					btn_overlay_analyst.setEnabled(true);
					btn_setting.setEnabled(true);

					isLongPressEnable = false;
					clipArea = 0;
					m_Map.refresh();
					break;
				case R.id.btn_create_buffer:
					btn_create_buffer.setEnabled(false);

					// 开始缓冲区分析时，隐藏路径分析设置按钮
					m_PathAnalystDropDown.setVisibility(View.GONE);
					m_FrameLayout00.setVisibility(View.GONE);

					startBufferAnalyse();
					break;

				case R.id.btn_overlay_analyst:
					btn_overlay_analyst.setEnabled(false);

					startOverlayAnalyse();

					break;

				case R.id.btn_setting:
					// 设置是否显示缓冲半径输入框
					if(m_FrameLayout00.getVisibility() == View.VISIBLE)
					{
						m_FrameLayout00.setVisibility(View.GONE);
					}else{
						m_FrameLayout00.setVisibility(View.VISIBLE);
					}
					break;

				case R.id.btn_entire:
					m_mapControl.getMap().viewEntire();
					m_mapControl.getMap().refresh();

					break;

				case R.id.btn_zoomOut:
					m_mapControl.getMap().zoom(0.5);
					m_mapControl.getMap().refresh();

					break;

				case R.id.btn_zoomIn:
					m_mapControl.getMap().zoom(2);
					m_mapControl.getMap().refresh();

					break;

				default:
					break;
			}
		}
	}

	// 长按事件监听
	SimpleOnGestureListener longTouchListener = new SimpleOnGestureListener() {

		public void onLongPress(MotionEvent event) {
			if(isLongPressEnable){                                            //  允许获取长按事件结果时才响应
				getPoints(event, isStartPoint, isEndPoint);               //  获取长按设置的点的坐标，并转换为经纬坐标
				isLongPressEnable = false;                                   //  设置一个点后，使长按响应无效

				// 判断设置的是起点还是终点
				if(isStartPoint)
					isStartPoint  = false;
				if(isEndPoint)
					isEndPoint    = false;
			}
		}
	};

	/**
	 * 获取屏幕上的点，并转换成地图坐标
	 * @param event
	 * @param bStartPoint
	 * @param bEndPoint
	 */
	public void getPoints(MotionEvent event , boolean bStartPoint, boolean bEndPoint) {

		//获取屏幕上的点击处的点坐标(x, y)
		int x = (int) event.getX();
		int y = (int) event.getY();
		isStartPoint = bStartPoint;
		isEndPoint   = bEndPoint;

		// 转换为地图二维点
		Point2D point2D = m_Map.pixelToMap(new Point(x, y));

		// 设置标注
		CallOut callOut = new CallOut(MainActivity.this);
		callOut.setStyle(CalloutAlignment.BOTTOM);             // 设置标注点的对齐方式：下方对齐
		callOut.setCustomize(true);                            // 设置自定义背景
		callOut.setLocation(point2D.getX(), point2D.getY());   // 设置标注点坐标

		// 投影转换，转换为经纬坐标系
		if(m_Map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(point2D);
			PrjCoordSys destPrjCoordSys = new PrjCoordSys();
			// 设置目标坐标系类型
			destPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			// 获取当前地图坐标系
			PrjCoordSys sourPrjCoordSys = m_Map.getPrjCoordSys();
			// 转换投影坐标
			CoordSysTranslator.convert(point2Ds, sourPrjCoordSys, destPrjCoordSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

			point2D = point2Ds.getItem(0);
		}

		ImageView imageView = new ImageView(MainActivity.this);

		// 添加点到线
		if(isStartPoint && !isEndPoint) {
			// 显示起点
			imageView.setBackgroundResource(R.drawable.start_point);
			callOut.setContentView(imageView);
			m_mapView.addCallout(callOut);

			// 添加起点
			m_Point2Ds.add(point2D);

			isStartPoint = false;

		}else if(!isStartPoint && isEndPoint){
			// 显示终点
			imageView.setBackgroundResource(R.drawable.end_point);
			callOut.setContentView(imageView);
			m_mapView.addCallout(callOut);

			// 添加终点
			m_Point2Ds.add(point2D);

			isEndPoint = false;

		}else{

		}
	}

	/**
	 * 初始化地图控件，数据对象，分析控件, 及其环境
	 */
	private void initAnalystEnvironment() {

		m_Datasources = m_workspace.getDatasources(); // 获取数据源集合
		m_Datasource = m_Datasources.get(dataSourceIndex); // 默认获取第一个数据源
		m_Datasets = m_Datasource.getDatasets(); // 获取数据集集合
		m_DatasetLine = (DatasetVector) m_Datasets.get(roadDatasetName); // 获取道路数据集
		m_Map = m_mapControl.getMap();
		m_TrackingLayer = m_Map.getTrackingLayer(); // 获取地图的跟踪层
		m_Point2Ds = new Point2Ds();

		initNetworkAnaystEnvironment();
	}

	/**
	 * 初始化网络分析环境
	 */
	public void initNetworkAnaystEnvironment() {

		// 定义并初始化网络分析环境设置
		TransportationAnalystSetting transAnalystSetting = new TransportationAnalystSetting();
		transAnalystSetting.setNetworkDataset(m_DatasetLine);
		transAnalystSetting.setEdgeIDField(m_edgeID);
		transAnalystSetting.setNodeIDField(m_nodeID);
		transAnalystSetting.setEdgeNameField("roadName");
		transAnalystSetting.setTolerance(89);

		WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
		WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
		weightFieldInfo.setFTWeightField("smLength");
		weightFieldInfo.setTFWeightField("smLength");
		weightFieldInfo.setName("length");
		weightFieldInfos.add(weightFieldInfo);

		transAnalystSetting.setWeightFieldInfos(weightFieldInfos);
		transAnalystSetting.setFNodeIDField("SmFNode");
		transAnalystSetting.setTNodeIDField("SmTNode");

		// 初始化网络分析对象
		m_TransAnalyst = new TransportationAnalyst();
		m_TransAnalyst.setAnalystSetting(transAnalystSetting);
		m_TransAnalyst.load();

	}

	/**
	 * 开始路径分析
	 */
	public void startPathAnalyse() {
		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
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
					// 分析结束后修改主界面
					Runnable action = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							btn_create_buffer.setVisibility(View.VISIBLE);
							m_PathAnalystDropDown.setVisibility(View.GONE);
							btn_path_analyst
									.setBackgroundResource(R.drawable.bg_white_round);
						}

					};
					MainActivity.this.runOnUiThread(action);
				} else {
					// showInfo("分析失败");
					// 显示分析失败提示
					Runnable action = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showInfo("分析路径失败");
						}
					};
					MainActivity.this.runOnUiThread(action);
					System.out.println("分析路径失败。。。。。。");
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
		transParameter.setWeightName("length");
		transParameter.setPoints(m_Point2Ds);
		transParameter.setNodesReturn(true);
		transParameter.setEdgesReturn(true);
		transParameter.setPathGuidesReturn(true);
		transParameter.setRoutesReturn(true);
		try {
			m_TransAnalystResult = m_TransAnalyst.findPath(transParameter,
					false);

		} catch (Exception e) {
			m_TransAnalystResult = null;
		}

		if (m_TransAnalystResult == null) {
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
		int count = m_TrackingLayer.getCount();

		for (i = 0; i < count; i++) {
			int index = m_TrackingLayer.indexOf("result");
			if (index != -1)
				m_TrackingLayer.remove(index);
		}

		// 获取路径分析结果中的路由集合
		GeoLineM[] routes = m_TransAnalystResult.getRoutes();
		if (routes == null) {
			System.out.println("获取路由集合失败");
			return;
		}

		for (i = 0; i < routes.length; i++) {
			GeoLineM geoLineM = routes[i];
			GeoStyle geoStyle = new GeoStyle();
			geoStyle.setLineColor(new Color(255, 90, 0));
			geoStyle.setLineWidth(1);
			geoLineM.setStyle(geoStyle);
			m_TrackingLayer.add(geoLineM, "result"); // 添加分析结果到跟踪层，并设置标签
		}
		m_Map.refresh();
	}

	public void showInfo(String mesg) {
		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 开始缓冲区分析
	 */
	public void startBufferAnalyse()
	{
		if(bufferRadius <=0){
			showInfo("缓冲区半径无效，分析失败");
			btn_create_buffer.setEnabled(true);
			return;
		}
		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
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
				// 分析结束后修改主界面
				Runnable action = new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btn_overlay_analyst.setVisibility(View.VISIBLE);
					}
				};
				MainActivity.this.runOnUiThread(action);
			}
		}).start();

	}

	/**
	 *  创建缓冲区
	 */
	public void createBuffer(){

		GeoRegion geometryBuffer = null;                                   // 定义缓冲区分析结果对象

		// 设置缓冲分析几何对象,即跟踪层第一个几何对象,并将其转换成GeoLine类型
		int index = m_TrackingLayer.indexOf("result");

		Geometry geometry = m_TrackingLayer.get(index);                     // 获取路径分析结果
		GeoLine geoLineForBuffer = ((GeoLineM)geometry).convertToLine();    // 将路由对象转换成线对象
		Geometry geoForBuffer = (Geometry)geoLineForBuffer;

		// 设置缓冲区分析参数
		BufferAnalystParameter bufferAnalystParameter = new BufferAnalystParameter();
		bufferAnalystParameter.setLeftDistance(bufferRadius);
		bufferAnalystParameter.setRightDistance(bufferRadius);
		bufferAnalystParameter.setEndType(BufferEndType.ROUND);

		// 设置投影坐标系
		PrjCoordSys prjCoordSys = m_Map.getPrjCoordSys();

		// 生成缓冲区几何对象
		geometryBuffer = BufferAnalystGeometry.createBuffer(geoForBuffer, bufferAnalystParameter, prjCoordSys);

		// 设置几何对象风格
		GeoStyle style = new GeoStyle();
		style.setLineColor(new Color(50, 244, 50));
		style.setLineSymbolID(0);
		style.setLineWidth(0.5);
		style.setMarkerSymbolID(351);
		style.setMarkerSize(new com.supermap.data.Size2D(5,5));
		style.setFillForeColor(new Color(147, 16, 133));
		style.setFillOpaqueRate(70);

		geometryBuffer.setStyle(style);            // 设置缓冲区分析结果的风格
		m_TrackingLayer.clear();                   // 清除跟踪层上原有的结果，即路径分析结果
		m_TrackingLayer.add(geometryBuffer, "");   // 添加缓冲区结果到跟踪层

		m_Map.refresh();
	}

	/**
	 * 开始叠加分析
	 */
	public void startOverlayAnalyse()
	{
		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("叠加分析中。。。。");
		dialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 叠加分析之 裁剪
				overlayAnalystClip();

				dialog.dismiss();

				// 分析结束后修改主界面
				Runnable action = new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// 显示影响面积
						m_FrameLayout01.setVisibility(View.VISIBLE);
						DecimalFormat df = new DecimalFormat("0.000000");
						areaOverlayView.setText("" + df.format(clipArea));
					}
				};
				MainActivity.this.runOnUiThread(action);
			}
		}).start();
	}

	/**
	 *  叠加分析之裁剪
	 */
	public void overlayAnalystClip() {
		DatasetVector srcDataset     = null;
		Geometry[]    clipGeometries = new Geometry[1];                // 用于存放缓冲区分析结果对象，结果只有一个面对象
		DatasetVector resultDataset  = null;
		DatasetVectorInfo resultDatasetInfo = new DatasetVectorInfo();

		// 获取将被裁剪的数据集
		srcDataset =(DatasetVector) m_Datasource.getDatasets().get("ResidentialArea");
		// 获取缓冲区分析的结果，并放入几何对象集合中，获取的几何对象必须是面对象
		int geoCount = m_TrackingLayer.getCount();
		for(int i=0, j=0; i<geoCount && j<1; i++)
		{
			if(m_TrackingLayer.get(i).getType() == GeometryType.GEOREGION)
			{
				clipGeometries[j] = m_TrackingLayer.get(i);
				j++;
			}
		}
		// 删除已有的分析结果数据集
		boolean isContained = m_Datasets.contains(resultDatasetName);
		if(isContained)
			m_Datasets.delete(resultDatasetName);


		// 设置结果数据集信息
		resultDatasetInfo.setType(srcDataset.getType());
		resultDatasetInfo.setName(resultDatasetName);
		resultDatasetInfo.setEncodeType(EncodeType.NONE);
		// 创建结果数据集
		resultDataset = m_Datasource.getDatasets().create(resultDatasetInfo);

		OverlayAnalystParameter overlayAnalystParameter = new OverlayAnalystParameter();
		overlayAnalystParameter.setTolerance(0.0112242);

		OverlayAnalyst.clip(srcDataset, clipGeometries, resultDataset, overlayAnalystParameter);

		// 显示结果前先清空跟踪层
		m_TrackingLayer.clear();

		// 显示结果
		/***************************************************/
		// 设置几何对象的风格
		GeoStyle style = new GeoStyle();
		style.setLineColor(new Color(50, 244, 50));
		style.setLineSymbolID(0);
		style.setLineWidth(0.5);
		style.setMarkerSymbolID(351);
		style.setMarkerSize(new com.supermap.data.Size2D(5,5));
		style.setFillForeColor(new Color(244, 50, 50));
		style.setFillOpaqueRate(70);

		// 定义图层风格
		LayerSettingVector  m_LayerSettingVector = new LayerSettingVector();
		m_LayerSettingVector.setStyle(style);

		// 添加结果数据集到地图最上面的图层
		m_Map.getLayers().add(resultDataset, true);
		// 设置图层风格
		m_Map.getLayers().get(0).setAdditionalSetting(m_LayerSettingVector);

		m_Map.refresh();
		// 获取影响面积
		Recordset recordset = resultDataset.getRecordset(false, CursorType.DYNAMIC);
		clipArea = recordset.statistic("SMAREA", StatisticMode.SUM);
		recordset.dispose();
		/***************************************************/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!isExitEnable){
				Toast.makeText(this, "再按一次退出程序！", 1500).show();
				isExitEnable = true;
			}else{
				m_Map.close();
				this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}