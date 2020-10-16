package com.supermap.imb.collectordemo;

import java.util.ArrayList;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoStyle;
import com.supermap.data.GeometryType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.imb.appconfig.DefaultDataConfig;
import com.supermap.mapping.Action;
import com.supermap.mapping.EditStatusListener;
import com.supermap.mapping.FinishEditedEvent;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.ScaleType;
import com.supermap.mapping.ScaleView;
import com.supermap.mapping.SnapMode;
import com.supermap.mapping.SnapSetting;
import com.supermap.mapping.collector.CollectionChangedListener;
import com.supermap.mapping.collector.Collector;
import com.supermap.mapping.collector.CollectorElement.GPSElementType;
import com.supermap.mapping.dyn.DynamicCircle;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
/**
 * <p>
 * Title:GPS采集
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
 * 1、范例简介：示范GPS采集
 * 2.示范数据：安装目录/SampleData/CollectorData/gpscollector.udb
 * 3.关键类成员/方法
 * MapControl.setAction(MapControl.setAction(
 * 4、使用步骤
 *   (1)打开采集/关闭采集
 *   (2)采集点对象/线对象/面对象
 *   (3)设置采集对象的风格/属性
 *   (4)节点编辑采集对象
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
public class MainActivity extends Activity implements OnClickListener, OnTouchListener{
	private MapView mMapView = null;
	private MapControl mMapControl = null;
	private DynamicView mDynamicView = null;
	private Map mMap = null;
	private ScaleView mScaleView = null;
	private Collector collector = null;
	private SettingPopup m_SettingPopup = null;

	//采集类型
	String strType;
	//采集类型列表
	ListView listview;

	//系统方位角,用于获取定位和
	private SensorManager mSensorManager;
	private Sensor mSensorOrientation;
	/*private*/ float mAzimuth = 0;
	/*private*/ Point2D mPoint2D = new Point2D(0, 0);//(116.499861111,39.9833333333)
	/*private*/ double mAccuracy = 0;
	
	TencentLocation m_MyLocation = null;
	Point2D mCurrentPnt2D = new Point2D(0, 0);//0,0
	//几何计算30米范围对应的度数 *2
	final public double dUnit = 2.694945898329385 * 2 * Math.pow(10, -4);
    //平面坐标换算  单位:米/度
    final public double dReverseUnit = 111319.489;//6378137*2*3.1415926 / 360
    int mDynPolygonID = -1;
    int mDynPointID = -1;
    DynamicCircle mDynGeoCircle = null;
    DynamicPoint mDynGeoPoint = null;
	
	private SubmitInfo   mSubmitInfo   = null;
	// 只负责接受焦点
	private RadioButton mBtnReceiveFocus = null;
	private View  anchorView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//	    //去掉Activity上面的状态栏  
//	    getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN);
        
        initUI();
        prepareData();
       
        //mMapControl.setMagnifierEnabled(true);
        mMapControl.setOnTouchListener(this);
        mSubmitInfo   = new SubmitInfo(mMapControl);
    }
    
    /*
     * 准备地图数据
     */
	private void prepareData(){
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setCancelable(false);
		progress.setMessage("数据加载中...");
		progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
		progress.show();
		new Thread(){
			@Override
			public void run() {
				super.run();
				////配置数据
				//new DefaultDataConfig().autoConfig();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						
						//配置数据
						new DefaultDataConfig().autoConfig();
						
						//OpenStreetMap
						openOSM();
						
						//open collector udb
						openCollectorUDB();
						
					}
				});
			}
		}.start();
	}
    /**
     * 初始化界面
     */
    private void initUI(){
    	mMapView = (MapView) findViewById(R.id.mapView);
    	mMapControl = mMapView.getMapControl();
    	mMap = mMapControl.getMap();
    	
    	mDynamicView = new DynamicView(this, mMapControl.getMap());
    	mMapView.addDynamicView(mDynamicView);

    	findViewById(R.id.btn_add).setOnClickListener(this);
    	findViewById(R.id.btn_edit).setOnClickListener(this);
    	findViewById(R.id.btn_undo).setOnClickListener(this);
    	findViewById(R.id.btn_redo).setOnClickListener(this);
    	findViewById(R.id.btn_cancel).setOnClickListener(this);
    	findViewById(R.id.btn_submit).setOnClickListener(this);
    	findViewById(R.id.btn_setting).setOnClickListener(this);
    	findViewById(R.id.btn_measure).setOnClickListener(this);
    	
    	
    	//拓扑编辑
    	findViewById(R.id.btnObject0).setOnClickListener(this);
    	findViewById(R.id.btnObject).setOnClickListener(this);
    	findViewById(R.id.btnObject2).setOnClickListener(this);
    	findViewById(R.id.btnObject2_1).setOnClickListener(this);
    	findViewById(R.id.btnObject3).setOnClickListener(this);
    	findViewById(R.id.btnObject4).setOnClickListener(this);
    	findViewById(R.id.btnObject5).setOnClickListener(this);
    	
    	
    	//GPS采集
    	findViewById(R.id.btnControlDisplay2).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay3).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay3_1).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay3_2).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay4).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay5).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay6).setOnClickListener(this);
    	findViewById(R.id.btnControlDisplay7).setOnClickListener(this);


    	mBtnReceiveFocus = (RadioButton)findViewById(R.id.btn_receivefocus1);  	
    	anchorView = findViewById(R.id.btn_add);
    	
    	
    	//Add A GestureDetector To MapControl 
    	initGesture();
    	
    	//initialize the location
    	initLocate();
    	
    	//open Snap Mode
    	initSnap();
    	
    	//initialize the ScaleView
    	initScaleView();
    	
    	//Add a GeometryselectedListenter to MapControl
    	mMapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
			
			@Override
			public void geometrySelected(GeometrySelectedEvent event) {
				// TODO Auto-generated method stub
				
				System.out.println("Selected ID: " + event.getGeometryID());
			}
			
			@Override
			public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> events) {
				// TODO Auto-generated method stub

			}

			@Override
			public void geometryMultiSelectedCount(int i) {

			}
		});
    	
    	//Add a EditStatusListener to MapControl
    	mMapControl.setEditStatusListener( new EditStatusListener() {
			
			@Override
			public void deleteNodeEnable(boolean isEnable) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addNodeEnable(boolean isEnable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void finishGeometryEdited(FinishEditedEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeFinishGeometryEdited(FinishEditedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    }
    
    /**
     * open OpenStreetMap
     */
    private void openOSM(){
		Workspace workspace = new Workspace();
    	mMapView = (MapView) findViewById(R.id.mapView);
    	mMapControl = mMapView.getMapControl();
        mMap = mMapControl.getMap();
    	
        mMap.setWorkspace(workspace);
    	
		//OpenStreetMap
		String url = "http://t2.supermapcloud.com";
		DatasourceConnectionInfo info = new DatasourceConnectionInfo();
		info.setAlias("OpenStreetMap2");
		info.setEngineType(EngineType.SuperMapCloud);
		info.setServer(url);
		
		Datasource datasource = workspace.getDatasources().open(info);
		if(datasource != null){
			mMap.getLayers().add(datasource.getDatasets().get(0), true);
		}

//		Rectangle2D bound = new Rectangle2D(12968258.504923185, 4862779.700006611, 12969117.62508615, 4864134.068308002);
//		mMap.setViewBounds(bound);
		mMap.refresh();
    }
    
    /**
     * 打开数据源
     */
    private void openCollectorUDB(){
    	String path = DefaultDataConfig.WorkspacePath;
    	
        Workspace workspace = mMap.getWorkspace();//new Workspace();
		DatasourceConnectionInfo info = new DatasourceConnectionInfo();
		info.setEngineType(EngineType.UDB);
		info.setServer(path);
		
		//open the datasource
		Datasource datasource = workspace.getDatasources().open(info);
		
		if(datasource != null){
			mMap.getLayers().add(datasource.getDatasets().get(0), true);
		}

    	//initialize the collector
		initCollector();
		mMap.setDynamicProjection(true);
		mMap.refresh();
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		//提交对象
		case R.id.btnObject0:
			mMapControl.submit();
			mMapControl.setAction(Action.PAN);
			break;
		//添加对象
		case R.id.btnObject:
			mMapControl.setAction(Action.CREATEPOLYGON);
			mMapControl.getMap().getLayers().get(0).setEditable(true);
			break;
		//删除对象
		case R.id.btnObject2:
			mMapControl.deleteCurrentGeometry();
			mMapControl.setAction(Action.SELECT);
			break;
		//选择对象
		case R.id.btnObject2_1:
			mMapControl.setAction(Action.SELECT);
			break;
		//定位
		case R.id.btnObject3:
			//mMapControl.setAction(Action.SELECT_BY_RECTANGLE);
			if(collector != null){				
				//当前定位坐标
				Point2D point2D = new Point2D(mPoint2D.getX(), mPoint2D.getY());//collector.getGPSPoint();
				
				//有效坐标
				if( !(point2D.getX() == 0 && point2D.getY() == 0) ){
					//当投影不是经纬坐标系时，则对点进行投影转换
					PrjCoordSys Prj = mMap.getPrjCoordSys();
					if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
						Point2Ds points = new Point2Ds();
						points.add(point2D);
						PrjCoordSys desPrjCoorSys = new PrjCoordSys();
						desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
						CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
								new CoordSysTransParameter(),
								CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			
						point2D.setX(points.getItem(0).getX());
						point2D.setY(points.getItem(0).getY());
					}
	
					//添加定位点
//					mMapControl.panTo(point2D, 300);
//					mMap.pan(point2D.getX(), point2D.getY());
					mMap.setCenter(point2D);
					mMap.refresh();
				}//if
			}
			break;
		case R.id.btnObject4:
			mMapControl.redo();
			break;
		case R.id.btnObject5:
			mMapControl.undo();
			break;
		case R.id.btn_submit:
			boolean isEdittable = isEditting();
			if(isEdittable){
				mMapControl.submit();
				mMapControl.cancel();
			}else{
				reset();
				mSubmitInfo.show();
			}
			break;
		
		//GPS采集
		case R.id.btnControlDisplay2:
			{
				//获取数据采集模块
				collector = mMapControl.getCollector();
				
				//存储采集对象的数据集,可以是点线面CAD类型
				Dataset dataset = mMapControl.getMap().getLayers().get(0).getDataset();
				collector.setDataset(dataset);
				
				collector.openGPS();
				
				collector.createElement(GPSElementType.LINE);

				//风格
				GeoStyle geoStyle = new GeoStyle();
				com.supermap.data.Color color = new com.supermap.data.Color(110, 208, 254);
				//线颜色
				geoStyle.setLineColor(color);
				//设置绘制风格
				collector.setStyle(geoStyle);
				
				//打开单指打点
				collector.setSingleTapEnable(true);
			}
			break;
		case R.id.btnControlDisplay3:
			if(collector != null){
				listview = new ListView(getApplicationContext());
				String[] StringArrays = new String[] { "点对象", "线对象", "面对象" };
				final boolean[] areaStates = new boolean[] { false, false, false };
				AlertDialog builder = new AlertDialog.Builder(this)
						.setTitle("选择类型")
						.setMultiChoiceItems(StringArrays, areaStates,
								new OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
									}
								})
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								for (int i = 0; i < areaStates.length; i++) {
									if (listview.getCheckedItemPositions().get(i)) {
										if(i == 0){
											collector.createElement(GPSElementType.POINT);
											strType = "点";
										}
										else if(i == 1){
											collector.createElement(GPSElementType.LINE);
											strType = "线";
										}
										else if(i == 2){
											collector.createElement(GPSElementType.POLYGON);
											strType = "面";
										}
									}
								}
								
								mMapControl.getMap().refresh();
								Toast.makeText(getApplicationContext(), "创建" + strType + "对象",
										Toast.LENGTH_SHORT).show();
							}
						}).setNegativeButton("取消", null).create();
				listview = builder.getListView();
				builder.show();
			}
			break;
		//定位采集
		case R.id.btnControlDisplay3_1:
			if(collector != null){
				////添加定位点
				
//				//当前定位坐标
//				Point2D point2D = new Point2D(mPoint2D.getX(), mPoint2D.getY());//collector.getGPSPoint();
//				
//				//有效坐标
//				if( !(point2D.getX() == 0 && point2D.getY() == 0) ){
//					//当投影不是经纬坐标系时，则对点进行投影转换
//					PrjCoordSys Prj = mMap.getPrjCoordSys();
//					if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
//						Point2Ds points = new Point2Ds();
//						points.add(point2D);
//						PrjCoordSys desPrjCoorSys = new PrjCoordSys();
//						desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
//						CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
//								new CoordSysTransParameter(),
//								CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
//			
//						point2D.setX(points.getItem(0).getX());
//						point2D.setY(points.getItem(0).getY());
//					}
//	
//					//添加定位点
					collector.addGPSPoint(mPoint2D);
//					collector.addGPSPoint();
//				}//if
			}
			break;
		//编辑设置
		case R.id.btnControlDisplay3_2:
			if(m_SettingPopup == null){
		    	m_SettingPopup = new SettingPopup(mMapControl, collector, this);
			}
	    	m_SettingPopup.setFocusable(true);
	    	m_SettingPopup.show();
			break;
		//回退
		case R.id.btnControlDisplay4:
			if(collector != null){
				collector.undo();
			}
			break;
		//重做
		case R.id.btnControlDisplay5:
			if(collector != null){
				collector.redo();
			}
			break;
		//提交
		case R.id.btnControlDisplay6:
			if(collector != null){
				collector.submit();
				mMapControl.getMap().refresh();
			}
			break;
		//屏幕打点
		case R.id.btnControlDisplay7:
			if(collector != null){
				
				if(collector.IsSingleTapEnable() == false){
					collector.setSingleTapEnable(true);
				}
				else{
					collector.setSingleTapEnable(false);
				}
			}
		default:
			break;
		}		
	}  
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		mMapControl.onMultiTouch(event);
		 
		int action = event.getAction();
		
		// 绘制完点对象，自动提交
		if(action == MotionEvent.ACTION_UP){
			if (mMapControl.getCurrentGeometry() != null && mMapControl.getCurrentGeometry().getType()==GeometryType.GEOPOINT) {
				
				mMapControl.submit();

				return true;
			}
		}
			
		return true;	
	}
	
	
	/**
	 * 注册地图控件的手势监听器
	 */
	void initGesture()
	{		
        //当前视图添加单击手势监听
		GestureDetector gestureDetector = new GestureDetector(mMapControl.getContext(), new GestureDetector.SimpleOnGestureListener() {
			//单击地图，平移不会触发
			@Override
			public boolean onSingleTapUp(MotionEvent e){
				if(collector != null){
//					//当前屏幕位置
//					float eventX = e.getX();
//					float eventY = e.getY();
//					Point2D pnt2D = mMapControl.getMap().pixelToMap(new Point((int)eventX, (int)eventY));
//					Point pntMotion = new Point((int)e.getX(), (int)e.getY());
//					collector.addPoint(pntMotion);
//					collector.addGPSPoint(pnt2D);
				}

				return true;
			}
		});
		
		//设置mapControl的手势监听器
		mMapControl.setGestureDetector(gestureDetector);
	}

	/**
	 * 初始化定位模块
	 */
	private void initLocate(){
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		startSensor();

		//腾讯定位
		m_MyLocation = new TencentLocation(this);
	}
	
	/**
	 * 初始化采集
	 */
	private void initCollector(){
		//获取数据采集模块
		collector = mMapControl.getCollector();
		
		//存储采集对象的数据集,可以是点线面CAD类型
		Dataset dataset = mMapControl.getMap().getLayers().get(0).getDataset();
		collector.setDataset(dataset);
		
		collector.openGPS();
		
		//创建线对象
		collector.createElement(GPSElementType.LINE);
		
		//风格
		GeoStyle geoStyle = new GeoStyle();
		//线颜色
		com.supermap.data.Color color = new com.supermap.data.Color(0,110,220);//(220,110,245);
		geoStyle.setLineColor(color);
		//面颜色
		com.supermap.data.Color colorFore = new com.supermap.data.Color(160,255,90);//(160,255,160);
		geoStyle.setFillForeColor(colorFore);
		//设置绘制风格
		collector.setStyle(geoStyle);
		
//		//打开单指打点,默认不打开
//		collector.setSingleTapEnable(true);
		//添加定位变化监听
		collector.setCollectionChangedListener(mCollectionChangedListener);
	}
	
	/**
	 * 打开捕捉功能
	 */
	private void initSnap(){
		SnapSetting snapSetting = new SnapSetting();
		//保存所有捕捉模式
		com.supermap.data.Enum[] snapModes = com.supermap.data.Enum.getEnums(SnapMode.class);
		//所有捕捉模式无效
		for (int j = 0; j < snapModes.length; j++) {
			snapSetting.set((SnapMode) snapModes[j], false);
		}

//			//打开端点捕捉
//			snapSetting.set(SnapMode.POINT_ON_ENDPOINT, true);
//			//打开节点捕捉
//			snapSetting.set(SnapMode.POINT_ON_POINT, true);
//			//打开线上点捕捉
//			snapSetting.set(SnapMode.POINT_ON_LINE, true);
		//打开默认捕捉模式
		snapSetting.openDefault();
		
		//捕捉设置
		mMapControl.setSnapSetting(snapSetting);
		//刷新地图
		mMapControl.getMap().refresh();
	}

	/**
	 * 提示信息,居中显示
	 * @param info
	 */
	public void showInfo(String info)
	{
		Toast toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	

	/**
	 * 开始监测方位变化
	 */
	public void startSensor(){
		if (mSensorManager != null && mSensorOrientation != null) {
			mSensorManager.registerListener(mSensorEventListener, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);
		}
	}
	/**
	 * 停止监测方位变化
	 */
	public void stopSensor(){
		if (mSensorManager != null && mSensorOrientation != null) {
			mSensorManager.unregisterListener(mSensorEventListener, mSensorOrientation);
		}
	}
	//定位当前位置
	public void locate(Point2D pt){
		//定时器偏移到当前位置
		mMapControl.panTo(pt, 20);
		
//		//清空所有动态对象
//		mDynamicView.clear();
//		
//		DynamicPoint dynElement = new DynamicPoint();
//		dynElement.addPoint(pt);
//		DynamicStyle style = new DynamicStyle();
//		style.setBackground(BitmapFactory.decodeResource(mMapControl.getResources(), R.drawable.navi_popup_small));
//		dynElement.setStyle(style);
//		mDynamicView.addElement(dynElement);
//		
//		//指定动态对象添加动画
//		DynamicElement element = mDynamicView.query(dynElement.getID());
//		if(element != null){
//			element.addAnimator(new ZoomAnimator(2.0f, 400));
//			element.addAnimator(new ZoomAnimator(0.5f, 400));
//		}
//		mDynamicView.startAnimation();
	}
	
	/**
	 * 初始化比例尺控件 
	 */
	private void initScaleView(){
		mScaleView = (ScaleView) findViewById(R.id.scaleView);
		mScaleView.setMapView(mMapView);
		mScaleView.setLevelEnable(true);
		mScaleView.setScaleType(ScaleType.Chinese);
	}
	
	public Map getMap(){
		return mMapControl.getMap();
	}

	
	public void drawCircleOnDyn(Point2D pnt2D, float dAzimuth, double dAccuracy){
		//copy another point2D
//		Point2D point2D = new Point2D(pnt2D.getX(), pnt2D.getY());
//		if(point2D.getX() == 0 && point2D.getY() == 0){
//			System.out.println("Unable to draw position.");
//			return;
//		}
//		if(mDynamicView == null || mMapControl == null){
//			return;
//		}
//
//		//当投影不是经纬坐标系时，则对点进行投影转换
//		PrjCoordSys Prj = mMapControl.getMap().getPrjCoordSys();
//		if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
//			Point2Ds points = new Point2Ds();
//			points.add(point2D);
//			PrjCoordSys desPrjCoorSys = new PrjCoordSys();
//			desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
//			CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
//					new CoordSysTransParameter(),
//					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
//
//			point2D.setX(points.getItem(0).getX());
//			point2D.setY(points.getItem(0).getY());
//		}
//		//定位的精度
//		if(dAccuracy <= 0 || dAccuracy > 500){  //无效精度范围
//			dAccuracy = dUnit;//默认半径为60米
//		}
//		else{
//			dAccuracy = 60;//dUnit * (m_MyLocation.getAccuracy() / 60.0);
//		}
		
		//添加动态对象
		if(mDynPointID == -1 && mDynPolygonID == -1){
//			DynamicCircle dynGeoCircle = new DynamicCircle();
//			dynGeoCircle.addPoint(new Point2D(point2D.getX(), point2D.getY()));
//			dynGeoCircle.setRadius(dAccuracy/*100*//*mMapControl.getMap().getBounds().getWidth() / 100000*/);//单位:米
//			
//			DynamicStyle styleGeCircle  = new DynamicStyle();
//			styleGeCircle.setAlpha(60);
//			styleGeCircle.setBackColor(Color.rgb(255, 255, 255));//52, 168, 83
//			styleGeCircle.setSize(5);
//			styleGeCircle.setLineColor(Color.rgb(51, 129, 204));//172, 52, 48
//			dynGeoCircle.setStyle(styleGeCircle);
//			mDynamicView.addElement(dynGeoCircle);
//			mDynGeoCircle = dynGeoCircle;
//			mDynPolygonID = dynGeoCircle.getID();

//			//绘制中心点
//			DynamicPoint dynPoint = new DynamicPoint();
//			dynPoint.addPoint(point2D);
//			DynamicStyle dynStyle = new DynamicStyle();
//			dynStyle.setBackground(BitmapFactory.decodeResource(mMapControl.getResources(), R.drawable.navi_popup_small_2_1));
//			dynStyle.setAngle(dAzimuth);
//			dynPoint.setStyle(dynStyle);
//			
//			mDynamicView.addElement(dynPoint);
//			mDynGeoPoint = dynPoint;
//			mDynPointID = dynPoint.getID();
//			
//			mDynamicView.refresh();
		}
		//修改动态对象
		else{
			//绘制中心圆
//			mDynGeoCircle.setPoint(new Point2D(point2D.getX(), point2D.getY()));
//			mDynGeoCircle.setRadius(dAccuracy);//单位:米
//			
//			//绘制中心点
//			DynamicStyle dynStyle = new DynamicStyle();
//			dynStyle.setBackground(BitmapFactory.decodeResource(mMapControl.getResources(), R.drawable.navi_popup_small_2_1));
//			dynStyle.setAngle(dAzimuth);
//			mDynGeoPoint.setPoint(point2D);
//			mDynGeoPoint.setStyle(dynStyle);
//			
//			mDynamicView.refresh();
		}
		
	}
	
	private CollectionChangedListener mCollectionChangedListener = new CollectionChangedListener() {
		
		@Override
		public void collectionChanged(Point2D pnt2d, double dAccuracy) {
			mPoint2D.setX(pnt2d.getX());
			mPoint2D.setY(pnt2d.getY());
			mAccuracy = dAccuracy;
			
			//绘制当前定位的位置
			drawCircleOnDyn(mPoint2D, mAzimuth, mAccuracy);
			showInfo("位置发生变化");
		}
	};


	
	/**
	 * 方向变化监听,手机方位角变化时会触发
	 */
	private SensorEventListener mSensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				if (Math.abs(mAzimuth - event.values[0])<3) {
					return;
				}
				//更新当前方位角
				mAzimuth = event.values[0];
				
				//绘制当前定位的位置
				drawCircleOnDyn(mPoint2D, mAzimuth, mAccuracy);
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 判断是否在编辑
	 * @return
	 */
	private boolean isEditting() {
		
		Action action= mMapControl.getAction();
		if(action.equals(Action.PAN) || action.equals( Action.NULL)){
			return false;
		}else{
		    return true;
		}
	}
	
	private void closeLocateion(){
		stopSensor();
	}
	
	/**
	 * 重置按钮
	 */
	protected void reset(){
		((RadioButton)findViewById(R.id.btn_add)).setChecked(false);
		((RadioButton)findViewById(R.id.btn_edit)).setChecked(false);
		((RadioButton)findViewById(R.id.btn_setting)).setChecked(false);
		((RadioButton)findViewById(R.id.btn_measure)).setChecked(false);
	}
}