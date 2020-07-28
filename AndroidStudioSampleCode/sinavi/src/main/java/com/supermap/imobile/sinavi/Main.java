package com.supermap.imobile.sinavi;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation;
import com.supermap.plugin.SpeakPlugin;
import com.supermap.plugin.Speaker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:路径导航
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
 * 1、范例简介：示范如何运用导航模块实现路径导航
 * 2、示例数据：安装目录/SampleData/NaviData;
 * 3、关键类型/成员: 
 *	    Navigation.setStartPoint 方法
 *      Navigation.setDestinationPoint 方法
 *      Navigation.enablePanOnGuide 方法
 *      Navigation.connectNaviData 方法
 *      Navigation.isGuiding 方法
 *      Navigation.cleanPath 方法
 *      Navigation.stopGuide 方法
 *      Navigation.startGuide 方法
 *      Navigation.routeAnalyst 方法
 *      
 * 4、使用步骤：
 *  (1)点击【设置起点】按钮，在地图上长按一点设置起点
 *  (2)点击【设置终点】按钮，在地图上长按另一点设置终点
 *  (3)点击【路径分析】按钮，进行路径分析
 *  (4)路径分析结束后，点击【开始引导】按钮，进行引导，引导过程将在地图上显示出来
 *  
 * 5、注意： 
 *	如果运行本范例失败，常见原因是缺少语音资源。解决办法：请将产品包中Resource文件夹下的voice文件夹拷贝到工程目录中的assets文件夹下。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
public class Main extends Activity implements OnClickListener{
	
	private Workspace mWorkspace = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;
	
	private Navigation mNavi = null;
	
	private Button btnRoute = null;
	private Button btnGuide = null;
	private Button btnSetting = null;
	private Button btnPan = null;
	
	//操作过程中的状态改变
	private boolean bGuideEnable = false;
	private boolean bEndPointEnable = false;
	private boolean bAnalystEnable = false;
	private boolean bLongPressEnable = false;
	//当进行路径分析后则不能修改起点终点
	private boolean bSettingEnable = true;
	
	//是否重置起始点
	private boolean bResetPoint = false;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	
	private View layout;
	
	//接收长按事件
	private GestureDetector.SimpleOnGestureListener mGestrueListener = new SimpleOnGestureListener(){
		public void onLongPress(MotionEvent e) {
			if(!bLongPressEnable){
				return;
			}
			int x = (int) e.getX();
			int y = (int) e.getY();
			Point2D pt = mMapControl.getMap().pixelToMap(new Point(x, y));
			CallOut callout = new CallOut(Main.this);
			callout.setStyle(CalloutAlignment.LEFT_BOTTOM);
			callout.setCustomize(true);
			callout.setLocation(pt.getX(),pt.getY());
			//当投影不是经纬坐标系时，则对起始点进行投影转换 
			if(mMapControl.getMap().getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
				Point2Ds points = new Point2Ds();
				points.add(pt);
				PrjCoordSys desPrjCoorSys = new PrjCoordSys();
				desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				CoordSysTranslator.convert(points, mMapControl.getMap().getPrjCoordSys(), desPrjCoorSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
				pt = points.getItem(0);
			}
			ImageView image = new ImageView(Main.this);
			//添加第一个点
			if(!bEndPointEnable){
				image.setBackgroundResource(R.drawable.startpoint);
				callout.setContentView(image);
				mMapView.addCallout(callout);
				mNavi.setStartPoint(pt.getX(), pt.getY());
				bEndPointEnable = true;
				bLongPressEnable = false;
				btnSetting.setText("设置终点");
				btnSetting.invalidate();
				return;
			}
			image.setBackgroundResource(R.drawable.despoint);
			callout.setContentView(image);
			mMapView.addCallout(callout);
			mNavi.setDestinationPoint(pt.getX(), pt.getY());
			bAnalystEnable = true;
			btnSetting.setText("重置起点");
			bResetPoint = true;
			bEndPointEnable = false;
			bLongPressEnable = false;
		};
	};
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestPermissions();
        //设置一些系统需要用到的路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        Environment.setTemporaryPath(sdcard+"/SuperMap/temp/");
        
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);
        
        setContentView(R.layout.main);
        initUI();
        initData();
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
    private void initUI(){
    	mMapView = (MapView) findViewById(R.id.mapview);
    	mMapControl = mMapView.getMapControl();
    	btnRoute = (Button) findViewById(R.id.analyst);
    	btnRoute.setOnClickListener(this);
    	btnGuide = (Button) findViewById(R.id.guide);
    	btnGuide.setOnClickListener(this);
    	btnPan = (Button) findViewById(R.id.pan);
    	btnPan.setOnClickListener(this);
    	btnSetting = (Button) findViewById(R.id.setting);
    	btnSetting.setOnClickListener(this);
    	
    	layout = findViewById(R.id.layout_btns);
    }
    
    private void initData(){
    	mWorkspace = new Workspace();
    	mNavi = mMapControl.getNavigation();
    	
    	DatasourceConnectionInfo info = new DatasourceConnectionInfo();
    	String dataServer = "http://supermapcloud.com";
		info.setEngineType(EngineType.SuperMapCloud);
		info.setAlias("SuperMapCloud");
		info.setServer(dataServer);
		Datasource datasource = mWorkspace.getDatasources().open(info);
    	
    	mMapControl.getMap().setWorkspace(mWorkspace);
    	mMapControl.getMap().setFullScreenDrawModel(true);               // 使用导航中地图旋转功能前，需提前设置整屏刷新，导航中点击界面左上方的"指南针"按钮即可实现地图旋转，但不支持网络地图，仅矢量地图可用
    	mMapControl.getMap().getLayers().add(datasource.getDatasets().get(0),false);
    	//设置地图初始的显示范围，放地图出图时就显示的是北京
    	mMapControl.getMap().setScale(1/458984.375);
    	mMapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
    	mMapControl.getMap().refresh();
    	
    	mMapControl.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mNavi.enablePanOnGuide(true);
				}
				return mMapControl.onMultiTouch(event);
			}
		});
    	//设置手势委托
    	mMapControl.setGestureDetector(new GestureDetector(mGestrueListener));
    	
    	//设置导航数据
		mNavi.connectNaviData(sdcard+"/SampleData/NaviData");
		
		SpeakPlugin.getInstance().setSpeaker(Speaker.CONGLE);
		
		mNavi.addNaviInfoListener(new NaviListener() {
			
			@Override
			public void onStopNavi() {
				// TODO Auto-generated method stub
				layout.setVisibility(View.VISIBLE);     // 导航停止后，显示按钮界面
				btnGuide.setText("开始引导");
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
				btnGuide.setText("开始引导");
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

	@Override
	public void onClick(View btn) {
		switch (btn.getId()) {
		case R.id.pan:
			mNavi.enablePanOnGuide(false);
			break;
		case R.id.guide:
			if(!bGuideEnable){
				SpeakPlugin.getInstance().playSound("请先进行路径分析!");
				Toast.makeText(this, "请先进行路径分析!", Toast.LENGTH_SHORT).show();
				return;
			}
			//如果是已经导航，再次点击则停止导航
			if(mNavi.isGuiding()){
				mNavi.cleanPath();
				mNavi.stopGuide();
				bSettingEnable = true;
				bGuideEnable = false;
				bEndPointEnable = false;
				mMapView.removeAllCallOut();
				btnGuide.setText("开始引导");
				return;
			}
			layout.setVisibility(View.GONE);        // 导航开始前，先隐藏按钮界面
			//1代表模拟导航
			mNavi.startGuide(1);
			mNavi.enablePanOnGuide(false);
			btnGuide.setText("停止引导");
			//bGuideEnable = false;
			break;
		case R.id.analyst:
			if(!bAnalystEnable){
				Toast.makeText(this, "请先设置起点和终点!", Toast.LENGTH_SHORT).show();
				return;
			}
			//如果分析过了则再次点击是清除分析结果
			if(bGuideEnable){
				bGuideEnable = false;
				mNavi.cleanPath();
				bSettingEnable = true;
				return;
			}
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("路径分析中...");
			dialog.show();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//0代表最快的模式
					mNavi.routeAnalyst(0);
					bGuideEnable = true;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.dismiss();
							btnRoute.setText("清除路径");
							bSettingEnable = false;
							mMapControl.getMap().refresh();
						}
					});
				}
			}).start();
			
			break;
		case R.id.setting:
			if(!bSettingEnable){
				Toast.makeText(this, "不能修改起点和终点!", Toast.LENGTH_SHORT).show();
				bLongPressEnable = true;
				return;
			}
			if(bEndPointEnable){
				Toast.makeText(this, "长按设置终点!", Toast.LENGTH_SHORT).show();
				bLongPressEnable = true;
				return;
			}
			if (bResetPoint) {
				mMapView.removeAllCallOut();//清空起始点
				bAnalystEnable = false;
				bResetPoint = false;
			}
			Toast.makeText(this, "长按设置起点!", Toast.LENGTH_SHORT).show();
			bLongPressEnable = true;
			break;
		default:
			break;
		}
	}
    
	
    
}