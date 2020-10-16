package com.supermap.android.track;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.data.GeoPoint;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.Navigation;
import com.supermap.navi.SuperMapPatent;
import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.supermap.track.Track;
import com.tencent.map.geolocation.TencentLocation;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:轨迹记录示范代码
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
 * 1、范例简介：示范如何运用轨迹模块实现轨迹记录
 * 2、示例数据：导航数据目录："/sdcard/SampleData/TrackData/"
 *          地图数据：track.smwu, track.udb, track.udd, road.udb, road.udd
 *          许可目录："/SuperMap/License/"
 * 3、关键类型/成员: 
 *        Track.createDataset()              方法
 *        Track.setDataset()                 方法
 *        Track.setMatchDatasets()           方法
 *        Track.setDistanceInternal()        方法
 *        Track.setTimeInternal()            方法
 *        Track.startTrack()                 方法
 *        Track.stopTrack()                  方法
 *             
 * 4、使用步骤：
 *  (1)点击【记录】按钮，设置轨迹名称，开始轨迹记录
 *  (2)点击【停止】按钮，可以停止轨迹记录
 *  (3)点击【查看】按钮，可以选择需要查看的路径
 *  (4)点击【采集】按钮，可以设置当前位置的名称和描述信息
 *  (5)点击【设置】按钮，可以进行行参数设置
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
public class MainActivity extends Activity implements OnClickListener{
	private Workspace          m_Workspace       = null;
	private MapControl         m_MapControl      = null;
	private static Map         m_Map             = null;
	private MapView            m_MapView         = null;
	private static Track       m_Track           = null;

    private RecordPopup        m_RecordPopup     = null;
    private SettingPopup       m_SettingPopup    = null;
    private TrackListPopup     m_TrackListPopup  = null;
    public static MyApplication m_MyApp          = null;
    
    
    private boolean m_ExitEnable                  = false;
    public static boolean m_EnableLocationService = false;
    public static TencentLocTool m_LocationTencent = null;
    private final String dataPath = MyApplication.SDCARD + "SampleData/TrackData/track.smwu";
    
//    private static Navigation mNavigation;
    private static Point2D    mPoint;

	private String fileString;
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
		File dirpath = this.getExternalFilesDir("");
		fileString = dirpath + File.separator;

		Environment.setLicensePath(MyApplication.SDCARD + "SuperMap/License");
		Environment.setWebCacheDirectory(MyApplication.SDCARD+"/SuperMap/WebCahe/");
		Environment.initialization(this);
		startMyLoctionService();                      // start service
		setContentView(R.layout.activity_main);
		TencentLocTool.getInstance().init(this);
		m_LocationTencent =TencentLocTool.getInstance();
		m_MyApp = MyApplication.getInstance();
		m_Track = new Track(this);
		mPoint = new Point2D();
		boolean isOpen = openWorkspace();
		
		if(isOpen){
			initView();
			initTrack();
			locating();
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
//		Manifest.permission.ACCESS_BACKGROUND_LOCATION,
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
					Manifest.permission.FOREGROUND_SERVICE,
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
		m_MapView    = (MapView) findViewById(R.id.mapview); 
		m_MapControl = m_MapView.getMapControl();
		m_Map        = m_MapControl.getMap();
		m_Map.setWorkspace(m_Workspace);

		m_Map.open("quanguo@SupermapCloud");
		m_Map.setScale(1/458984.375);
		m_Map.setCenter(new Point2D(12953693.6950684, 4858067.04711915));
		m_Map.refresh();
		
//		mNavigation = m_MapControl.getNavigation();
//		mNavigation.setEncryption(new SuperMapPatent());
		return true;
	}

  public void startMyLoctionService(){
	   new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intentService = new Intent();
			intentService.setAction("com.supermap.track.mylocationservice.START");
			intentService.setPackage(getPackageName());
			startService(intentService);
		}
	}).start();
   }
	/**
	 * Initialize m_Track
	 */
	private void initTrack(){
		if(m_Track != null){
			m_Track.setCustomLocation(true);            // 设置用户传入GPS数据
			m_Track.setDistanceInterval(3);             // 设置距离间隔为3米
			m_Track.setTimeInterval(25);                // 设置时间间隔为25s
			m_Track.setMatchDatasets(m_Map.getWorkspace().getDatasources().get("road").getDatasets());  // 设置匹配道路所在的数据集
		}
	}
	
	/**
	 * 设置用于轨迹记录的GPS数据
	 * @param gpsData
	 */
	private static int count = 0; 
	public static void setGpsData(GPSData gpsData){
		if(m_Track != null){
//			mPoint = mNavigation.encryptGPS(gpsData.dLongitude, gpsData.dLatitude);
			mPoint = new Point2D(gpsData.dLongitude, gpsData.dLatitude);
			gpsData.dLongitude = mPoint.getX();
			gpsData.dLatitude = mPoint.getY();
			m_Track.setGPSData(gpsData);            // 设置GPS数据 ，在setCustomLocation(true)时，设置的数据有效              
			count ++;
		}
		if(50 == count){
			m_Map.refresh();
		    count = 0;
		}
	}

    @Override
    public void onBackPressed() {
//super.onBackPressed();
        moveTaskToBack(true);
    }
	
	/**
	 * Initialize  view
	 */
	private boolean initView() {
        ((Button) findViewById(R.id.btn_recording)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_stop)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_review)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_setting)).setOnClickListener(this);
        
		m_RecordPopup  = new RecordPopup(m_MapControl, m_Track);
		m_SettingPopup = new SettingPopup(m_MapControl, m_Track);
		m_TrackListPopup = new TrackListPopup(m_MapControl, m_Track);
		return true;
	}
	
	
	
	/**
	 * MainView 
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_recording:
			m_RecordPopup.setFocusable(true);
			m_RecordPopup.show();
			break;
		case R.id.btn_stop:
			m_Track.stopTrack();
			m_EnableLocationService = false;
			break;
		case R.id.btn_review:
			m_TrackListPopup.setFocusable(true);
			m_TrackListPopup.show();
			break;
		case R.id.btn_setting:
			m_SettingPopup.setFocusable(true);
			m_SettingPopup.show();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取当前位置并显示在地图中心
	 */
	public static void locating(){
		TencentLocation location = m_LocationTencent.getLocInfo();
		if(location == null)
			return;
//		Point2D point2D = mNavigation.encryptGPS(location.getLongitude(), location.getLatitude());
		Point2D point2D = new Point2D(location.getLongitude(), location.getLatitude());
		PrjCoordSys Prj = m_Map.getPrjCoordSys();

		// 当投影不是经纬坐标系时，则对点进行投影转换
		if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds points = new Point2Ds();
			points.add(point2D);
			PrjCoordSys desPrjCoorSys = new PrjCoordSys();
			desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			CoordSysTranslator.convert(points, desPrjCoorSys, Prj, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			point2D = points.getItem(0);
		}
		m_Map.setCenter(point2D);
		m_Map.setScale(6.971914893617021E-5);
		m_Map.refresh();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!m_ExitEnable){
				m_MyApp.showInfo("再按一次退出程序！");
				m_ExitEnable = true;
			}else{
				Intent intentService = new Intent();
				intentService.setAction("com.supermap.mylocationservice.START");
				stopService(intentService);
				m_MyApp.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
