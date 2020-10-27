package com.supermap.navidemo.navi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.mapping.MapView;
import com.supermap.mapping.MapControl;


import com.supermap.navidemo.app.MyApplication;
import com.supermap.navidemo.configuration.DefaultDataConfiguration;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.TrackingLayer;
import com.supermap.navi.Navigation;
import com.supermap.navidemo.R;
import com.supermap.plugin.SpeakPlugin;
import com.supermap.plugin.Speaker;

import com.tencent.map.geolocation.TencentLocation;

/**
 * <p>
 * Title:导航
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
 *   展示如何运用 iMobile 的导航模块实现路径导航。
 * 2、Demo数据：导航数据目录："/SuperMap/Demos/Data/NaviData/"
 *           地图数据：http://supermapcloud.com
 *           许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	    Navigation.setStartPoint();          方法
 *      Navigation.setDestinationPoint();    方法
 *      Navigation.enablePanOnGuide();       方法
 *      Navigation.connectNaviData();        方法
 *      Navigation.isGuiding();              方法
 *      Navigation.cleanPath();              方法
 *      Navigation.stopGuide();              方法
 *      Navigation.startGuide();             方法
 *      Navigation.routeAnalyst();           方法
 *
 * 4、功能展示
 *   (1)查找两点之间最佳路径；
 *   (2)对查找到的最佳路径进行真实导航；
 *   (3)对查找到的最佳路径进行模拟导航。
 * 5、注意：
 *	如果运行本范例失败，常见原因是缺少语音资源。
 *  解决办法：请将产品包中Resource文件夹下的voice文件夹拷贝到工程目录中的assets文件夹下。
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
	private ImageButton      btn_clean              = null;
	private ImageButton      btn_end_point          = null;
	private ImageButton      btn_location           = null;
	private ImageButton      btn_start_point        = null;
	private ImageButton      btn_zoomIn             = null;
	private ImageButton      btn_zoomOut            = null;
	private ImageButton      btn_back               = null;
	private ImageButton      btn_navi_simulation    = null;
	private ImageButton      btn_navi_real          = null;
	private ImageButton      btn_pause_navigation   = null;
	private ImageButton      btn_path_analyst       = null;
	private ImageButton      btn_conti_navigation   = null;
	private ImageButton      btn_stop_navigation    = null;

	// 定义地图控件
	private MapView        m_mapView        = null;
	private Workspace      m_workspace      = null;
	private MapControl     m_mapControl     = null;
	private Map            m_Map            = null;
	private Navigation     m_Navigation     = null;
	private TrackingLayer  m_TrackingLayer  = null;

	// 定义几何对象
	private Point2D        startPoint       = null;          // 定义起点
	private Point2D        destPoint        = null;          // 定义终点

	// 定义字符串常量
	private final String LicPath = DefaultDataConfiguration.LicensePath;


	// 定义布尔变量
	private boolean isStartPoint       = false;
	private boolean isEndPoint         = false;
	private boolean isLongPressEnable  = false;
	private boolean bGuideEnable       = false;
	private boolean mExitEnable        = false;

	// 定义整型变量
	private int    steps            = 0;
	private int    routeAnalystMode = 0;                    // 0:推荐模式; 1:时间最快模式; 2:距离最短模式; 3:最少收费模式
	private int    naviMode         = 1;                    // 0:真实导航; 1:模拟导航; 2:定点巡航
	private int    startStep        = 0;
	private int    destStep         = 0;

	private TencentLocTool m_MyLocation = null;
	private MyApplication mApp   = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置许可路径，并初始化
		Environment.setLicensePath(LicPath);
		Environment.setWebCacheDirectory(MyApplication.SDCARD+"/SuperMap/WebCahe/");
		Environment.setTemporaryPath(MyApplication.SDCARD + "/SuperMap/temp/");
		Environment.initialization(this);

		mApp   = (MyApplication) getApplication();
		mApp.registerActivity(this);
//		m_MyLocation = new LocationTencent(this);
		TencentLocTool.getInstance().init(this);
		m_MyLocation =TencentLocTool.getInstance();

		initView();                                           // 初始化界面控件，布局文件中包含地图显示控件，需在许可初始化后再显示布局

		boolean  isOpenMap = false;
		isOpenMap = initMap();                                // 打开工作空间，打开地图
		if(isOpenMap){
			initNaviData();                                   // 初始化导航数据
		}else{
			showInfo("Initialize Map failed.");
		}
	}

	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	public void initView() {
		// 显示布局
		setContentView(R.layout.activity_main);
		// 获取按钮控件，并设置监听
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new ImageButtonOnClickListener());

		btn_start_point = (ImageButton) findViewById(R.id.btn_start_point);
		btn_start_point.setOnClickListener(new ImageButtonOnClickListener());

		btn_end_point = (ImageButton) findViewById(R.id.btn_end_point);
		btn_end_point.setOnClickListener(new ImageButtonOnClickListener());

		btn_clean = (ImageButton) findViewById(R.id.btn_clear);
		btn_clean.setOnClickListener(new ImageButtonOnClickListener());

		btn_location = (ImageButton) findViewById(R.id.btn_location);
		btn_location.setOnClickListener(new ImageButtonOnClickListener());

		btn_zoomOut = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn_zoomOut.setOnClickListener(new ImageButtonOnClickListener());

		btn_zoomIn = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn_zoomIn.setOnClickListener(new ImageButtonOnClickListener());

		btn_path_analyst = (ImageButton) findViewById(R.id.btn_path_analyst);
		btn_path_analyst.setOnClickListener(new ImageButtonOnClickListener());

		btn_navi_simulation = (ImageButton) findViewById(R.id.btn_navi_simulation);
		btn_navi_simulation.setOnClickListener(new ImageButtonOnClickListener());

		btn_navi_real = (ImageButton) findViewById(R.id.btn_navi_real);
		btn_navi_real.setOnClickListener(new ImageButtonOnClickListener());

		btn_pause_navigation = (ImageButton) findViewById(R.id.btn_pause_navigation);
		btn_pause_navigation.setOnClickListener(new ImageButtonOnClickListener());

		btn_conti_navigation = (ImageButton) findViewById(R.id.btn_continue_navigation);
		btn_conti_navigation.setOnClickListener(new ImageButtonOnClickListener());

		btn_stop_navigation = (ImageButton) findViewById(R.id.btn_stop_navigation);
		btn_stop_navigation.setOnClickListener(new ImageButtonOnClickListener());

		listenNaviStatus();
	}

	// 按钮点击事件监听
	private class ImageButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

				case R.id.btn_back:
					// 开始设置起点或终点，但未长按，返回上一状态,只需修改按钮状态即可
					if(isStartPoint) {
						btn_start_point.setEnabled(true);
						isStartPoint = false;
						break;
					}
					if(isEndPoint) {
						btn_end_point.setEnabled(true);
						isEndPoint = false;
						break;
					}
					// 返回到启动模拟导航或真实导航界面
					boolean naviStatus = m_Navigation.isGuiding() || (btn_conti_navigation.getVisibility() == View.VISIBLE) || (m_Navigation.isGuiding() == false && btn_pause_navigation.getVisibility() == View.VISIBLE);
					if(naviStatus)
					{
						if(m_Navigation.isGuiding()){
							m_Navigation.stopGuide();                    // 停止导航
							m_Navigation.enablePanOnGuide(true);
						}
						btn_stop_navigation.setVisibility(View.GONE);
						btn_pause_navigation.setVisibility(View.GONE);
						btn_conti_navigation.setVisibility(View.GONE);
						btn_back.setVisibility(View.INVISIBLE);
						btn_navi_real.setVisibility(View.VISIBLE);
						btn_navi_simulation.setVisibility(View.VISIBLE);
					}else{
						// 撤销上一步设置的起点或终点
						deleteStartOrDestPoint();
					}
					break;

				case R.id.btn_start_point:
					// 起点和终点设置的互斥判断，即在完成设置起点或终点前，不可以开始设置另一个点
					if (isLongPressEnable) {
						showInfo("请先完成终点的设置");
					} else {
						m_mapView.removeCallOut("startPoint");
						btn_start_point.setEnabled(false);
						isStartPoint      = true;
						isLongPressEnable = true;
						showInfo("请长按设置起点");                 // 长按后获取起点
					}
					break;

				case R.id.btn_end_point:
					// 起点和终点设置的互斥判断，即在完成设置起点或终点前，不可以开始设置另一个点
					if (isLongPressEnable) {
						showInfo("请先完成起点的设置");
					} else {
						btn_end_point.setEnabled(false);
						isEndPoint = true;
						isLongPressEnable = true;
						showInfo("请长按设置终点");                  // 长按后获取终点
					}
					break;

				case R.id.btn_clear:
					// 停止导航，清除导航路径和设置的起点
					clear();
					break;

				case R.id.btn_path_analyst:
					// 完成起点和终点设置后才能进行路径分析
					if((startPoint == null) || (destPoint == null))
					{
						showInfo("请设置起点和终点");
					}else{
						startNaviRouteAnalyst(startPoint, destPoint);            // 开始路径分析
					}
					m_Map.refresh();
					break;

				case R.id.btn_navi_simulation:
					naviMode = 1;
					startNavigation();
					btn_back.setVisibility(View.VISIBLE);
					btn_navi_simulation.setVisibility(View.GONE);
					btn_navi_real.setVisibility(View.GONE);
					btn_pause_navigation.setVisibility(View.VISIBLE);
					btn_stop_navigation.setVisibility(View.VISIBLE);
					break;

				case R.id.btn_navi_real:
					naviMode = 0;

					//TencentMapLBSApiResult location = m_MyLocation.getLocationInfo();
					TencentLocation location = m_MyLocation.getLocInfo();
					if(location == null ){
						showInfo("现在无法定位，请稍后再试");
						break;
					}
					m_mapView.removeCallOut("startPoint");
					startPoint = getPoint(m_MyLocation.getLocInfo(), "startPoint", R.drawable.start_point);
					if((startPoint != null) && (destPoint != null))
						startNaviRouteAnalyst(startPoint, destPoint);
					break;

				case R.id.btn_pause_navigation:
					m_Navigation.enablePanOnGuide(true);
					btn_pause_navigation.setVisibility(View.GONE);
					btn_conti_navigation.setVisibility(View.VISIBLE);
					btn_stop_navigation.setVisibility(View.GONE);
					break;

				case R.id.btn_stop_navigation:
					m_Navigation.enablePanOnGuide(true);
					btn_pause_navigation.setVisibility(View.GONE);
					btn_stop_navigation.setVisibility(View.GONE);
					if(m_Navigation.isGuiding()){
						m_Navigation.stopGuide();
					}
					btn_navi_real.setVisibility(View.VISIBLE);
					btn_navi_simulation.setVisibility(View.VISIBLE);
					btn_back.setVisibility(View.INVISIBLE);
					break;

				case R.id.btn_continue_navigation:
					m_Navigation.enablePanOnGuide(false);
					btn_pause_navigation.setVisibility(View.VISIBLE);
					btn_conti_navigation.setVisibility(View.GONE);
					btn_stop_navigation.setVisibility(View.VISIBLE);
					break;


				case R.id.btn_location:
					if(m_Navigation.isGuiding()){
						showInfo("请先按返回键，退出导航");
						break;
					}
					TencentLocation location1 = m_MyLocation.getLocInfo();
					if(location1 == null ){
						showInfo("现在无法定位，请稍后再试");
						break;
					}
					m_mapView.removeCallOut("location");

					getPoint(location1, "location", R.drawable.location);

					// 开始设置起点，但还未长按时
					if (isStartPoint ){
						isStartPoint      = false;
						isLongPressEnable = false;
						btn_start_point.setEnabled(true);
					}
					m_Map.refresh();
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

		/**
		 *  撤销上一步设置的起点或终点
		 */
		private void deleteStartOrDestPoint() {
			if ((startPoint != null) && startStep == steps && !bGuideEnable) {

				steps--;
				startStep = 0;
				m_mapView.removeCallOut("startPoint"); // 清除起点
				startPoint = null;

				btn_start_point.setEnabled(true);
				isLongPressEnable = false;
				if (steps == 0)
					btn_back.setVisibility(View.INVISIBLE);

			} else {

				if ((destPoint != null) && (destStep == steps) && !bGuideEnable) {

					steps--;
					destStep = 0;
					m_mapView.removeCallOut("destPoint"); // 清除终点
					destPoint = null;

					btn_end_point.setEnabled(true);
					isLongPressEnable = false;
					if (steps == 0)
						btn_back.setVisibility(View.INVISIBLE);

				}
			}
		}

		/**
		 *  停止导航，清除导航路径和设置的起点
		 */
		private void clear() {
			if (m_Navigation.isGuiding())
				m_Navigation.stopGuide();
			m_Navigation.cleanPath();        // 清除路径
			m_mapView.removeAllCallOut();    // 清除起点和终点显示

			steps = 0;
			startStep = 0;
			destStep = 0;
			naviMode = 1;
			startPoint = null;
			destPoint = null;
			m_Map.refresh();

			// 设置按键可用
			btn_start_point.setEnabled(true);
			btn_end_point.setEnabled(true);

			btn_back.setVisibility(View.VISIBLE);
			btn_start_point.setVisibility(View.VISIBLE);
			btn_end_point.setVisibility(View.VISIBLE);
			btn_path_analyst.setVisibility(View.VISIBLE);
			btn_navi_simulation.setVisibility(View.GONE);
			btn_navi_real.setVisibility(View.GONE);
			btn_stop_navigation.setVisibility(View.GONE);
			btn_pause_navigation.setVisibility(View.GONE);
			btn_conti_navigation.setVisibility(View.GONE);
			btn_back.setVisibility(View.INVISIBLE);

			isLongPressEnable = false;
			bGuideEnable = false;
		}
	}

	/**
	 * 打开工作空间，显示地图
	 * @return
	 */
	public boolean initMap() {
		// 获取地图控件
		m_mapView    = (MapView) findViewById(R.id.mapView);
		m_mapControl = m_mapView.getMapControl();
		// 打开云服务地图
		m_workspace = new Workspace();
		DatasourceConnectionInfo info = new DatasourceConnectionInfo();
		String dataServer = "http://supermapcloud.com";
		info.setEngineType(EngineType.SuperMapCloud);
		info.setAlias("SuperMapCloud");
		info.setServer(dataServer);

		// 在新建的工作空间中打开数据源
		Datasource datasource = m_workspace.getDatasources().open(info);
		if(datasource == null){
			return false;
		}
		m_Map = m_mapControl.getMap();                                   // 获取地图控件
		m_Map.setWorkspace(m_workspace);                                 // 关联地图和工作空间
		m_Map.getLayers().add(datasource.getDatasets().get(0),false);    // 将工作空间中的数据集添加到地图最底层
		m_TrackingLayer = m_Map.getTrackingLayer();                      // 获取地图的跟踪层

		//设置地图初始的显示范围，放地图出图时就显示的是北京
		m_mapControl.getMap().setScale(1/458984.375);
		m_mapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
		m_mapControl.getMap().refresh();

		m_mapControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {

				if(event.getAction() == MotionEvent.ACTION_DOWN){
					m_Navigation.enablePanOnGuide(true);
				}
				if(m_Navigation.isGuiding()){
					btn_pause_navigation.setVisibility(View.GONE);
					btn_conti_navigation.setVisibility(View.VISIBLE);
					btn_stop_navigation.setVisibility(View.GONE);
				}
				return m_mapControl.onMultiTouch(event);
			}
		});

		// 设置长按事件监听
		m_mapControl.setGestureDetector(new GestureDetector(longTouchListener));;
		return true;
	}

	// 长按事件监听
	SimpleOnGestureListener longTouchListener = new SimpleOnGestureListener() {

		public void onLongPress(MotionEvent event) {
			if (isLongPressEnable) {              // 允许获取长按事件结果时才响应
				isLongPressEnable = false;        // 设置一个点后，使长按响应无效

				steps ++;
				// 判断是设置起点还是终点
				if (isStartPoint ){
					startStep = destStep +1;
					isStartPoint = false;
					setStartPoint(event);
				}
				if (isEndPoint) {
					destStep = startStep +1;
					isEndPoint = false;
					setDestinationPoint(event);
				}
				if(steps==2)
					btn_back.setVisibility(View.VISIBLE);
			}
		}
	};

	/**
	 * 获取并显示起点
	 * @param event
	 */
	public void setStartPoint(MotionEvent event) {
		startPoint = getPoint(event, "startPoint", R.drawable.start_point);
	}

	/**
	 * 获取并显示终点
	 * @param event
	 */
	public void setDestinationPoint(MotionEvent event) {
		destPoint = getPoint(event, "destPoint", R.drawable.end_point);
	}

	/**
	 * 显示标注点
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showPointByCallOut(final Point2D point2D,
								   final String pointName, int idDrawable) {
		// 设置标注
		CallOut callOut = new CallOut(MainActivity.this);
		callOut.setStyle(CalloutAlignment.BOTTOM); // 设置标注点的对齐方式：下方对齐
		callOut.setCustomize(true); // 设置自定义背景
		callOut.setLocation(point2D.getX(), point2D.getY()); // 设置标注点坐标
		ImageView imageView = new ImageView(MainActivity.this);

		// 显示起点
		imageView.setBackgroundResource(idDrawable);
		callOut.setContentView(imageView);
		m_mapView.addCallout(callOut, pointName); // 在地图上显示CallOut标注点，并设置名称

	}

	/**
	 * 获取屏幕上的点，并转换成地图经纬坐标点，并返回经纬坐标点
	 * @param event
	 * @param pointName
	 * @param idDrawable
	 * @return
	 */
	public Point2D getPoint(MotionEvent event, final String pointName, final int idDrawable) {

		// 获取屏幕上的点击处的点坐标(x, y)
		int x = (int) event.getX();
		int y = (int) event.getY();

		// 转换为地图二维点
		Point2D point2D = m_Map.pixelToMap(new Point(x, y));
		showPointByCallOut(point2D, pointName, idDrawable);

		// 投影转换，转换为经纬坐标系
		if (m_Map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(point2D);
			PrjCoordSys destPrjCoordSys = new PrjCoordSys();

			// 设置目标坐标系类型
			destPrjCoordSys
					.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);

			// 获取当前地图坐标系
			PrjCoordSys sourPrjCoordSys = m_Map.getPrjCoordSys();
			// 转换投影坐标
			CoordSysTranslator.convert(point2Ds, sourPrjCoordSys,
					destPrjCoordSys, new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

			point2D = point2Ds.getItem(0);
		}

		return point2D;                  // 返回经纬坐标点
	}

	/**
	 * 将获得的位置信息（经纬坐标）转换成地图坐标系上的点，并显示，但返回经纬坐标点
	 * @param location
	 * @param pointName
	 * @param idDrawable
	 * @return
	 */
	public Point2D getPoint(TencentLocation location,
							final String pointName, final int idDrawable) {

		if (location == null) {
			showInfo("现在无法定位，请稍后再试");
			return null;
		}
		Point2D point2D = new Point2D(location.getLongitude(), location.getLatitude());
		PrjCoordSys Prj = m_Map.getPrjCoordSys();

		// 当投影不是经纬坐标系时，则对点进行投影转换
		if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds points = new Point2Ds();
			points.add(point2D);
			PrjCoordSys desPrjCoorSys = new PrjCoordSys();
			desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			boolean b1 = CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
					new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			// 在地图上显示该点
			showPointByCallOut(points.getItem(0), pointName, idDrawable);
			m_Map.setCenter(points.getItem(0));
			m_Map.setScale(1 / 57373.046875);
			m_Map.refresh();
		}else {
			// 在地图上显示该点
			showPointByCallOut(point2D, pointName, idDrawable);
			m_Map.setCenter(point2D);
			m_Map.setScale(1 / 57373.046875);
		}
		return point2D;
	}

	/**
	 *  初始化导航数据
	 */
	public void initNaviData() {
		m_Navigation = m_mapControl.getNavigation();

		// 设置导航数据,
		m_Navigation.connectNaviData(DefaultDataConfiguration.MapDataPath);

		SpeakPlugin.getInstance().setSpeaker(Speaker.CONGLE);

	}

	/**
	 * 开始路径分析
	 * @param startpoint2D
	 * @param destpoint2D
	 */
	public void startNaviRouteAnalyst(Point2D startpoint2D, Point2D destpoint2D) {

		if((startpoint2D == null ) || (destpoint2D == null) ){
			showInfo("请先设置起点和终点");
			return;
		}

		// 设置导航的起点和终点
		m_Navigation.setStartPoint(startpoint2D.getX(), startpoint2D.getY());
		m_Navigation.setDestinationPoint(destpoint2D.getX(), destpoint2D.getY());

		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("路径分析中....");
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 导航路径分析
				int analystResult = m_Navigation.routeAnalyst(routeAnalystMode);
				dialog.dismiss();
				if(analystResult == 0){
					System.out.println("路径分析失败");
					Runnable action = new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showInfo("路径分析失败");
						}
					};
					MainActivity.this.runOnUiThread(action);
					return;
				}
				// 分析结束后修改主界面
				Runnable action = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btn_start_point.setVisibility(View.GONE);
						btn_end_point.setVisibility(View.GONE);
						btn_path_analyst.setVisibility(View.GONE);
						btn_back.setVisibility(View.INVISIBLE);
						btn_navi_simulation.setVisibility(View.VISIBLE);
						btn_navi_real.setVisibility(View.VISIBLE);
						bGuideEnable = true;
						if(naviMode == 0){
							btn_back.setVisibility(View.VISIBLE);
							btn_navi_simulation.setVisibility(View.GONE);
							btn_navi_real.setVisibility(View.GONE);
							btn_stop_navigation.setVisibility(View.VISIBLE);
							m_Navigation.startGuide(naviMode);
							m_Map.refresh();
						}
						m_Map.refresh();                               // 路径分析完之后，刷新才能显示路径
					}
				};
				MainActivity.this.runOnUiThread(action);
			}
		}).start();

	}

	/**
	 * 开始模拟导航
	 */
	public void startNavigation() {

		if(naviMode == 0  && destPoint != null)
			bGuideEnable = true;
		if(!bGuideEnable ){
			SpeakPlugin.getInstance().playSound("请先进行路径分析!");
			Toast.makeText(this, "请先进行路径分析!", Toast.LENGTH_SHORT).show();
			return;
		}
		m_Navigation.enablePanOnGuide(false);
		// 开始导航
		m_Navigation.startGuide(naviMode);
	}
	/**
	 * 停止导航
	 */
	public void stoptNavigationSimulation() {

		//如果是已经导航，再次点击则停止导航
		if(m_Navigation.isGuiding()){
			m_Navigation.stopGuide();
			m_Navigation.enablePanOnGuide(true);
			btn_pause_navigation.setVisibility(View.GONE);
			btn_navi_simulation.setVisibility(View.VISIBLE);
			btn_navi_real.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Toast message
	 * @param mesg
	 */
	public void showInfo(String mesg) {
		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!mExitEnable){
//				Toast.makeText(this, "再按一次退出程序！", 1500).show();
				mExitEnable = true;
			}else if (m_Navigation != null){
				if(m_Navigation.isGuiding()){
					m_Navigation.stopGuide();
				}
				m_Navigation.enablePanOnGuide(true);
				mApp.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 监听导航状态，当导航结束后，返回选择导航模式状态
	 */
	public void listenNaviStatus() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if(m_Navigation != null){
						if(m_Navigation.isGuiding() == false  && btn_pause_navigation.getVisibility() == View.VISIBLE){
							Runnable action = new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(btn_back != null){
										btn_back.performClick();
									}
								}
							};
							MainActivity.this.runOnUiThread(action);
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}