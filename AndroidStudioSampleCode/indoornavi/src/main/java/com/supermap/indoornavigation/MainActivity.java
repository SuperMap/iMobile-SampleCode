package com.supermap.indoornavigation;

/**
 * <p>
 * Title:室内导航
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
 *   	展示设置起点、终点、途径点、路径分析、导航等 室内导航功能实现。
 *   
 * 2、Demo数据：
 * 		数据目录："../SampleData/IndoorNavigationData/"
 *      地图数据："beijing.smwu", "beijing0525.udb", "bounds.udb", "kaide_mall.udb"
 *      许可目录："../SuperMap/License/"
 *      
 * 3、关键类型/成员: 
 *    m_NavigationEx.setStartPoint();			方法
 *    m_NavigationEx.setDestinationPoint();		方法
 *    m_NavigationEx.addWayPoint();				方法
 *    m_NavigationEx.routeAnalyst();			方法
 *    m_NavigationEx.startGuide();				方法
 *    m_floorListView.setCurrentFloorId();		方法
 *
 * 4、功能展示
 *   (1)添加起点、终点、途径点；
 *   (2)路径分析；
 *   (3)导航。
 *   (4)楼层切换
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Datasource;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.FloorChangeListener;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation3;
import com.supermap.indoor.FloorListView;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.Button;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity {
	
	private MapControl m_mapControl = null;
	private Workspace m_wokspace = null;
	private MapView m_mapView = null;
	private Navigation3 m_NavigationEx = null;
	private FloorListView m_floorListView = null;
	
	private boolean bLongPressEnable = false;	//地图长按状态（true：长按可添加控制点，falae:不可添加控制点）
	
	//当前操作状态，包括添加起点、添加途径点、添加终点、分析、导航、无状态
	private enum NAVI_STATE {STATE_ADDSTARTPOINT, STATE_ADDVIAPOINT, 
							STATE_ADDENDPOINT, STATE_ANALYSE, 
							STATE_NAVIGATION,STATE_NULL};	
	NAVI_STATE m_naviState = NAVI_STATE.STATE_NULL;	//初始为无状态
	
	final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	
	//按钮变量
	Button m_btnStart;
	Button m_btnVia;
	Button m_btnEnd;
	Button m_btnAnalyse;
	Button m_btnNavi;
	Button m_btnClear;
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
        //不使用OpenGL模式显示
        Environment.setOpenGLMode(false);
      
        //设置许可文件路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
             
        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);

        setContentView(R.layout.activity_main);
          
        //打开工作空间
        m_wokspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard+"/SampleData/IndoorNavigationData/beijing.smwu");
        info.setType(WorkspaceType.SMWU);
        m_wokspace.open(info);
              
        //将地图显示控件和工作空间关联
        m_mapView = (MapView)findViewById(R.id.Map_view);
        m_mapControl =  m_mapView.getMapControl();
        m_mapControl.getMap().setWorkspace(m_wokspace);
              
        //打开工作空间中的地图。参数0表示第一张地图
        String mapName = m_wokspace.getMaps().get(0);
        m_mapControl.getMap().open(mapName);
        //设置使用全屏绘制模式,点、文字和普通图层同时显示
        m_mapControl.getMap().setFullScreenDrawModel(true);
        //刷新地图        
        m_mapControl.getMap().refresh();
      
        m_NavigationEx = m_mapControl.getNavigation3();
        m_floorListView = (FloorListView)findViewById(R.id.floor_list_view);
        m_floorListView.linkMapControl(m_mapControl);
        m_mapControl.setGestureDetector(new GestureDetector(m_mapControl.getContext(), mGestrueListener));
      
        //设置引导路径样式，非当前楼层的路径虚线表示
        GeoStyle style = new GeoStyle();
        if (Environment.isOpenGLMode()) {
        	style.setLineSymbolID(964882);
        }else {
        	style.setLineSymbolID(964883);
        }
        m_NavigationEx.setRouteStyle(style);	//设置当前图层引导路径样式
        GeoStyle styleHint = new GeoStyle();
        styleHint.setLineWidth(2);
        styleHint.setLineColor(new com.supermap.data.Color(82, 198, 223));
        styleHint.setLineSymbolID(2);
        m_NavigationEx.setHintRouteStyle(styleHint);//设置其他楼层引导路径的样式
      
        //添加导航引导信息更新监听器
        m_NavigationEx.addNaviInfoListener(new NaviListener() {
    	  
        	@Override
        	//停止导航后
        	public void onStopNavi() {
    		
        		//结束导航后，关闭导航状态
        		m_naviState = NAVI_STATE.STATE_NULL;
        		
        		//清除路径信息
        		m_NavigationEx.cleanPath();
        		m_mapView.removeAllCallOut();
        	}
    	  
        	@Override
        	//启动导航
        	public void onStartNavi() {
				
        	}
    	  
        	@Override
        	//播放导航语音信息
        	public void onPlayNaviMessage(String message) {
				
        	}
			
        	@Override
        	//导航引导信息更新
        	public void onNaviInfoUpdate(NaviInfo naviInfo) {
				
        	}
			
        	@Override
        	//道路匹配失败
        	public void onAdjustFailure() {
				
        	}
			
        	@Override
        	public void onAarrivedDestination() {
				
        		//到达目的地后，关闭导航状态
        		m_naviState = NAVI_STATE.STATE_NULL;
        		//清除路径信息
        		m_NavigationEx.cleanPath();
        		m_mapView.removeAllCallOut();
        	}
        });
      
        m_btnStart = (Button)findViewById(R.id.buttonStart);
        m_btnEnd = (Button)findViewById(R.id.buttonEnd);
        m_btnVia = (Button)findViewById(R.id.buttonVia);
        m_btnAnalyse = (Button)findViewById(R.id.buttonAnalyse);
        m_btnNavi = (Button)findViewById(R.id.buttonNavigation);
        m_btnClear = (Button)findViewById(R.id.buttonClear);
   }
    
    //起点按钮事件
    public void buttonStart_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION || m_naviState ==NAVI_STATE.STATE_ANALYSE ){
    		Toast.makeText(m_mapControl.getContext(), "导航或分析状态，添加起点无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bLongPressEnable = true; //地图可以长按
    	m_naviState = NAVI_STATE.STATE_ADDSTARTPOINT;	 //切换添加起点状态
    }
    //终点按钮事件
    public void buttonEnd_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION  || m_naviState ==NAVI_STATE.STATE_ANALYSE ){
    		Toast.makeText(m_mapControl.getContext(), "导航或分析状态，添加终点无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bLongPressEnable = true; //地图可以长按
    	m_naviState = NAVI_STATE.STATE_ADDENDPOINT;	 //切换可以添加终点状态
    }
    //途径点按钮事件
    public void buttonVia_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION  || m_naviState ==NAVI_STATE.STATE_ANALYSE ){
    		Toast.makeText(m_mapControl.getContext(), "导航或分析状态，添加途径点无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bLongPressEnable = true; //地图可以长按
    	m_naviState = NAVI_STATE.STATE_ADDVIAPOINT;	 //切换可以添加途径点状态
    }
    //分析按钮事件
    public void buttonAnalyse_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION){
    		Toast.makeText(m_mapControl.getContext(), "导航状态，分析功能无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	//判断室内地图是否打开
    	String currentFloorID = m_floorListView.getCurrentFloorId();
		if (currentFloorID == null) {
			Toast.makeText(m_mapControl.getContext(), "请先打开室内地图", Toast.LENGTH_SHORT).show();
			return;
		}
		//判断室内地图数据源是否加载成功
		Datasource datasource = m_floorListView.getIndoorDatasource();
		if (datasource == null) {
			Toast.makeText(m_mapControl.getContext(), "室内地图数据源加载失败", Toast.LENGTH_SHORT).show();
			return;
		}
		//设置室内地图所在的数据源
		m_NavigationEx.setDatasource(datasource);
		
    	//路径分析
    	boolean bResult = m_NavigationEx.routeAnalyst();
    	if(bResult){
    		Toast.makeText(m_mapControl.getContext(), "分析成功", Toast.LENGTH_SHORT).show();
    	}
    	else{
    		Toast.makeText(m_mapControl.getContext(), "分析失败，确认是否添加导航点", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	//切换到分析状态，长按地图无效
    	m_naviState = NAVI_STATE.STATE_ANALYSE;
    	bLongPressEnable = false;
    }
    //导航按钮事件
    public void buttonNavi_Click(View view)
    {    	
		//开始导航  0：真实导航， 1：模拟导航 ,2：巡航 ,3：步行导航
    	if(!m_NavigationEx.startGuide(1)){
			Toast.makeText(m_mapControl.getContext(), "导航启动失败", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//导航开始后，切换到导航状态，长按地图无效
    	m_naviState = NAVI_STATE.STATE_NAVIGATION;
    	bLongPressEnable = false;
    }
    //清除按钮事件
    public void buttonClear_Click(View view)
    {
    	if( m_naviState == NAVI_STATE.STATE_NAVIGATION){
    		Toast.makeText(m_mapControl.getContext(), "导航状态，不能清除", Toast.LENGTH_SHORT).show();
			return;
    	}
    	
    	//状态更新
    	bLongPressEnable = false; 				//地图不可用长按
    	m_naviState = NAVI_STATE.STATE_NULL;	//不能添加导航点
    	
    	//清除路径分析结果，包括导航点等
    	m_NavigationEx.cleanPath();
    }
    
    private GestureDetector.SimpleOnGestureListener mGestrueListener = new SimpleOnGestureListener(){
		public void onLongPress(MotionEvent e) {
			
			//非长按状态（添加起点、终点或途径点状态），返回，不操作
			if(!bLongPressEnable){
				return;
			}
			//获取当前楼层ID
			String mCurrentFloorID = m_floorListView.getCurrentFloorId();
			if (mCurrentFloorID == null) {
				Toast.makeText(m_mapControl.getContext(), "请先打开室内地图", Toast.LENGTH_SHORT).show();
				return;
			}
			//获取长按时的屏幕坐标
			int x = (int)e.getX();
			int y = (int)e.getY();
			//屏幕坐标转换为地图
			Point2D pt = m_mapControl.getMap().pixelToMap(new Point(x, y));
			//当投影不是经纬坐标系时，则对起始点进行投影转换 
			if( m_mapControl.getMap().getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
				Point2Ds points = new Point2Ds();
				points.add(pt);
				PrjCoordSys desPrjCoorSys = new PrjCoordSys();
				desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				CoordSysTranslator.convert(points, m_mapControl.getMap().getPrjCoordSys(), desPrjCoorSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
				pt = points.getItem(0);
			}
			
			switch (m_naviState){
			case STATE_ADDSTARTPOINT:	
				//设置起点：可以连续设置，保留最后一次设置的值
				m_NavigationEx.setStartPoint(pt.getX(), pt.getY(), mCurrentFloorID);
				break;
			case STATE_ADDENDPOINT:		
				//设置终点：可以连续设置，保留最后一次设置的值
				m_NavigationEx.setDestinationPoint(pt.getX(), pt.getY(), mCurrentFloorID);
				break;
			case STATE_ADDVIAPOINT:
				//设置途径点：可以连续设置，途径点个数不限制 注意：没有起点和终点时不能添加途径点
				m_NavigationEx.addWayPoint(pt.getX(), pt.getY(), mCurrentFloorID);
				break;
			default:
				break;
			}
		};
	};
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
