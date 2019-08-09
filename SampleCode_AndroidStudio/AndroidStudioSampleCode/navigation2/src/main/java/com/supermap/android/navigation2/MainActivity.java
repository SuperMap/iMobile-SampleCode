package com.supermap.android.navigation2;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.supermap.android.navigation2sample.R;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation2;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:行业导航示范代码
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
 * 1、范例简介：示范如何运用行业导航模块实现路径导航
 * 2、示例数据：导航数据目录："/sdcard/SampleData/Navigation2Data/"
 *          地图数据：navi_beijing.smwu, beijing.udb, beijing.udd, netModel.snm
 *          许可目录："/SuperMap/License/"
 * 3、关键类型/成员: 
 *      Navigation2.setNetWorkDataset();      方法
 *      Navigation2.loadModel();              方法
 *      Navigation2.load();                   方法
 *	    Navigation2.setStartPoint();          方法
 *      Navigation2.setDestinationPoint();    方法
 *      Navigation2.isGuiding();              方法
 *      Navigation2.cleanPath();              方法
 *      Navigation2.stopGuide();              方法
 *      Navigation2.startGuide();             方法
 *      Navigation2.routeAnalyst();           方法
 *      
 * 4、使用步骤：
 *  (1)点击【设置起点】按钮，在地图上长按一点设置起点
 *  (2)点击【设置终点】按钮，在地图上长按另一点设置终点
 *  (3)点击【路径分析】按钮，进行路径分析，显示导航路径
 *  (4)路径分析结束后，若点击【模拟导航】按钮，将进行模拟引导，并在地图上显示引导过程
 *  (5)路径分析结束后，若点击【真实导航】按钮，将开始真实导航，并在地图上显示引导过程
 *  (7)导航进行中，若点击【停止导航】，可以停止导航
 *  (8)停止导航后，点击【清楚路径】按钮，可以清除现有路径结果，再重新分析路径
 *  (9)点击【清空记录】按钮，可以清空导航，以重新设置
 *
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
public class MainActivity extends Activity {
	private Workspace          m_Workspace       = null;
	private MapControl         m_MapControl      = null;
	private static Map         m_Map             = null;
	private MapView            m_MapView         = null;
    private Navigation2        m_Navigation2     = null;
	private Point2D            startPoint        = null;
	private Point2D            destPoint         = null;
	private boolean longPressEnable = false;
	private boolean setStartPoint   = false;
	private boolean setDestPoint    = false;
	private boolean isFindPath      = false;
	private boolean m_ExitEnable    = false;
	private boolean isPathShowed    = true;
	private Button btn_startPoint   = null;
	private Button btn_destPoint    = null;
	private Button btn_pathAnalyst  = null;
	private Button btn_startGuide   = null;
	private Button btn_stopGuide    = null;
	private Button btn_showPath     = null;
    public static MyApplication m_MyApp         = null;
    private final String dataPath = MyApplication.SDCARD + "SampleData/Navigation2Data/navi_beijing.smwu";
    
    private int     step         = 0;
    
    private View layout;
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
		requestPermissions() ;
		Environment.setLicensePath(MyApplication.SDCARD + "SuperMap/License");
		Environment.setWebCacheDirectory(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/SuperMap/WebCahe/");
		Environment.initialization(this);
		//Environment.setOpenGLMode(true);
		
		setContentView(R.layout.activity_main);
		m_MyApp = MyApplication.getInstance();
		boolean isOpen = openWorkspace();
		
		if(isOpen){
			initView();
			initNavigation2();
			startDefaultNavi();
		}
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
    /**
     * 打开工作空间，显示地图
     * @return
     */
	private boolean openWorkspace() {
				
		m_Workspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(dataPath);
		info.setType(WorkspaceType.SMWU);
		boolean isOpen = m_Workspace.open(info);
		if(!isOpen){
			m_MyApp.showInfo("Workspace open failed!");
			return false;
		}
		m_MapView = (MapView) findViewById(R.id.mapView); 
		m_MapControl = m_MapView.getMapControl();
		m_Map     = m_MapControl.getMap();
		m_Map.setWorkspace(m_Workspace);
    	m_Map.open(m_Workspace.getMaps().get(0));    // open map
    	m_Map.setScale(1/229492.1875);
    	m_Map.setCenter(new Point2D(12953693.6950684, 4858067.04711915));
    	m_Map.refresh();
    	m_MapControl.setGestureDetector(new GestureDetector(longTouchListener));
		return true;
	}
	
	/**
	 * 初始化行业导航控件
	 */
	private void initNavigation2() {
		String networkDatasetName = "RoadNetwork";         // 已有的网络数据集的名称

		// 初始化行业导航对象
		DatasetVector networkDataset = (DatasetVector) m_Workspace.getDatasources().get("beijing").getDatasets().get(networkDatasetName);
		m_Navigation2 = m_MapControl.getNavigation2();      // 获取行业导航控件，只能通过此方法初始化m_Navigation2
		m_Navigation2.setPathVisible(true);                 // 设置分析所得路径可见
		m_Navigation2.setNetworkDataset(networkDataset);    // 设置网络数据集
		m_Navigation2.loadModel( MyApplication.SDCARD + "SampleData/Navigation2Data/netModel.snm");  // 加载网络模型
		
		m_Navigation2.addNaviInfoListener(new NaviListener() {
			
			@Override
			public void onStopNavi() {
				// TODO Auto-generated method stub
				layout.setVisibility(View.VISIBLE);     // 导航停止后，显示按钮界面
				clean();
			}
			
			@Override
			public void onStartNavi() {
				// TODO Auto-generated method stub
				layout.setVisibility(View.GONE);        // 导航开始前，先隐藏按钮界面
			}
			
			@Override
			public void onNaviInfoUpdate(NaviInfo arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAarrivedDestination() {
				// TODO Auto-generated method stub
				layout.setVisibility(View.VISIBLE);     // 到达目的地后，显示按钮界面
				clean();
			}

			@Override
			public void onAdjustFailure() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPlayNaviMessage(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 启动默认设置的模拟导航
	 */
	private void startDefaultNavi() {
		startPoint = getStartPoint(null);
		destPoint  = getDestPoint(null);
		routeAnalyze();
		//m_Navigation2.enablePanOnGuide(true);
		//m_Navigation2.startGuide(1);
	}

	/**
	 * 初始化主界面控件
	 */
	private void initView() {
		btn_startPoint = (Button) findViewById(R.id.btn_startPoint);
		btn_destPoint = (Button) findViewById(R.id.btn_destPoint);
		btn_startGuide = (Button) findViewById(R.id.btn_startSimuGuide);
		btn_stopGuide = (Button) findViewById(R.id.btn_stopGuide);
		btn_pathAnalyst = (Button) findViewById(R.id.btn_findPath);
		btn_showPath = (Button) findViewById(R.id.btn_showPath);
		((Button) findViewById(R.id.btn_changeView)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_cleanPath)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_startRealGuide)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_clean)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_showCar)).setOnClickListener(onClickListener);

		btn_startPoint.setOnClickListener(onClickListener);
		btn_destPoint.setOnClickListener(onClickListener);
		btn_pathAnalyst.setOnClickListener(onClickListener);
		btn_startGuide.setOnClickListener(onClickListener);
		btn_stopGuide.setOnClickListener(onClickListener);
		btn_showPath.setOnClickListener(onClickListener);

		layout = findViewById(R.id.layout_btns);
	}

    // 按钮单击监听事件
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_startPoint:
				longPressEnable = true;
				setStartPoint = true;
				m_MyApp.showInfo("请长按设置起点");
				break;
				
			case R.id.btn_destPoint:
				longPressEnable = true;
				setDestPoint = true;
				m_MyApp.showInfo("请长按设置终点");
				break;

			case R.id.btn_findPath:
				routeAnalyze();
				break;
				
			case R.id.btn_startSimuGuide:
				if (isFindPath) {
					layout.setVisibility(View.GONE);        // 导航开始前，先隐藏按钮界面
					m_Navigation2.startGuide(1);
				}
				break;
				
			case R.id.btn_startRealGuide:
				if (isFindPath)
					m_Navigation2.startGuide(0);
				break;
				
			case R.id.btn_stopGuide:
				if (m_Navigation2.isGuiding()) {
					m_Navigation2.stopGuide();

					m_Navigation2.enablePanOnGuide(false);
				}
				break;

			case R.id.btn_showPath:
				showPath();
				break;

			case R.id.btn_changeView:
				changeView();
				break;
				
			case R.id.btn_cleanPath:
				m_Navigation2.cleanPath();
				break;
			case R.id.btn_clean:
				clean();
				break;
			case R.id.btn_showCar:
				if(m_Navigation2.isGuiding()){
					setMapCenter(m_Navigation2.getCarPosition());
					m_Navigation2.enablePanOnGuide(false);
				}else{
					if(startPoint != null)
						setMapCenter(startPoint);
					m_MyApp.showInfo("没有启动导航, 不能确定行进位置");
				}
				break;
			default:
				break;
			}
			m_Map.refresh();
		}

		
	};
	private void setMapCenter(Point2D point) {
		Point2D point2D = new Point2D(point.getX(), point.getY());
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

			// 设置地图中心点，地图显示的中心将移到该点处
			m_Map.setCenter(points.getItem(0));
			m_Map.refresh();
		}
	}
	
	/**
	 * 路径分析
	 */
	private void routeAnalyze() {
		if (startPoint == null || destPoint == null) {
           m_MyApp.showInfo("请先设置起点、终点");
		} else {
			m_Navigation2.setStartPoint(startPoint.getX(), startPoint.getY());        // 设置起点
			m_Navigation2.setDestinationPoint(destPoint.getX(),destPoint.getY());     // 设置终点
			m_Navigation2.setPathVisible(true);                                       // 设置路径可见
			isPathShowed = true;
			isFindPath = m_Navigation2.routeAnalyst();                                // 路径分析
			if(isFindPath){
			    m_Map.refresh();                                                      // 刷新后可显示所得路径
			}else{
				m_MyApp.showInfo("路径分析失败！");
			}
		}
	}
	
	/**
	 * 显示或隐藏路径
	 */
	private void showPath() {
		if(!isFindPath){
			return;                    // 没有路径，返回
		}
		if (isPathShowed) {
			m_Navigation2.setPathVisible(false);
			isPathShowed = false;
            m_Map.refresh();
			btn_showPath.setText("显示路径");
		} else {
			m_Navigation2.setPathVisible(true);
			isPathShowed = true;
            m_Map.refresh();
			btn_showPath.setText("隐藏路径");
		}
	}

	/**
	 * 停止导航、清除起点、终点、路径
	 */
	private void clean() {
	
		if(m_Navigation2.isGuiding())
		    m_Navigation2.stopGuide();           // 停止正在进行的导航
		m_Navigation2.cleanPath();               // 清除路径，需在停止导航后进行，否则无效
		m_Navigation2.enablePanOnGuide(false);
		m_MapView.removeAllCallOut();
		m_Map.refresh();
		
		startPoint = null;
		destPoint  = null;
		isFindPath = false;
		isPathShowed = false;
		btn_showPath.setText("显示路径");
	}

	/**
	 * 修改道路信息栏和转向信息栏
	 */
	private void changeView() {
		if (m_Navigation2 == null)
			return;

		Bitmap carBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);

		if (step == 0) {
			m_Navigation2.setCarPicture(carBitmap);                  // 设置小车图片
		}

		if (step == 1) {
			m_Navigation2.setCarPicture(null);
			step = -1;
		}

		step++;
	}
	
	// 长按监听事件
	private SimpleOnGestureListener longTouchListener = new SimpleOnGestureListener() {
		public void onLongPress(MotionEvent event) {
			if (!longPressEnable)
				return;
			if (setStartPoint)
				startPoint = getStartPoint(event);
			if (setDestPoint)
				destPoint = getDestPoint(event);

			longPressEnable = false;
			setStartPoint = false;
			setDestPoint = false;
		}
		// 地图漫游
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (m_Navigation2.isGuiding())
				m_Navigation2.enablePanOnGuide(true);
			return false;
        }

	};

	/**
	 * 从屏幕 获取起点
	 * @param event
	 * @return
	 */
	private Point2D getStartPoint(MotionEvent event) {
		int x = 0;
		int y = 0;
		if (event == null) {
			x = 50;
			y = 50;
		} else {
			x = (int) event.getX();
			y = (int) event.getY();
		}
		Point point = new Point(x, y);
		return getPoint(point, "startPoint", R.drawable.startpoint);
	}

	/**
	 * 从屏幕 获取终点
	 * @param event
	 * @return
	 */
	private Point2D getDestPoint(MotionEvent event) {
		int x = 0;
		int y = 0;
		if (event == null) {
			x = 200;
			y = 200;
		} else {
			x = (int) event.getX();
			y = (int) event.getY();
		}
		Point point = new Point(x, y);
		return getPoint(point, "destPoint", R.drawable.destpoint);
	}

	/**
	 * 将屏幕上的点转换为地图上的点和经纬坐标点
	 * 
	 * @param event
	 * @param pointName
	 * @param idDrawable
	 * @return
	 */
	private Point2D getPoint(Point point, final String pointName,
			final int idDrawable) {
		Point2D point2D = null;

		// 转换为地图上的二维点
		point2D = m_MapControl.getMap().pixelToMap(point);
		showPointByCallout(point2D, pointName, idDrawable);

		if (m_Map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			PrjCoordSys srcPrjCoordSys = m_Map.getPrjCoordSys();
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(point2D);
			PrjCoordSys desPrjCoordSys = new PrjCoordSys();
			desPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			// 转换投影坐标
			CoordSysTranslator.convert(point2Ds, srcPrjCoordSys,
					desPrjCoordSys, new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

			point2D = point2Ds.getItem(0);
		}
		
		return point2D;
	}

	/**
	 * 显示Callout
	 * 
	 * @param point
	 * @param pointName
	 * @param idDrawable
	 */
	private void showPointByCallout(Point2D point, final String pointName,
			final int idDrawable) {
		CallOut callOut = new CallOut(this);
		callOut.setStyle(CalloutAlignment.BOTTOM);
		callOut.setCustomize(true);
		callOut.setLocation(point.getX(), point.getY());
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(idDrawable);
		callOut.setContentView(imageView);
		m_MapView.addCallout(callOut, pointName);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!m_ExitEnable){
				m_MyApp.showInfo("再按一次退出程序！");
				m_ExitEnable = true;
			}else{
				m_MyApp.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}



