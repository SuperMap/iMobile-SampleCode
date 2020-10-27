package com.supermap.carsmonitordemo.carmonitor;


import com.supermap.carsmonitordemo.R;
import com.supermap.carsmonitordemo.app.MyApplication;
import com.supermap.carsmonitordemo.communication.MessageReciver;
import com.supermap.carsmonitordemo.monitors.DisplayManager;
import com.supermap.carsmonitordemo.monitors.SoundMonitor;
import com.supermap.data.GeometryType;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;

import com.supermap.mapping.Map;
import com.supermap.mapping.MapView;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * Title:车辆监控
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
 *   展示如何实现车辆状态监控和区域监控
 * 2、Demo数据：数据目录："/SuperMap/Demos/Data/CarsMonitorData/"
 *           地图数据："carsmonitor.sxwu", "carsmonitor.bru", "carsmonitor.lsl", "carsmonitor.sym"
 *           许可目录："/SuperMap/License/"
 *           轨迹文件目录："/SuperMap/Demos/CarsMonitorDemo/Track/"
 * 3、关键类型/成员:
 *	  LayerSettingVector.setStyle();             方法
 *    Layer.setAdditionalSetting();              方法
 *    MapControl.setAction();                    方法
 *	  MapControl.addGeometryAddedListener();     方法
 *    DynamicPoint.addAnimator();                方法
 *    DynamicPoint.setStyle();                   方法
 *	  DynamicPoint.setUserData();                方法
 *	  DynamicPoint.setOnClickListenner();        方法
 *    DynamicView.addElement();                  方法
 *    DynamicView.startAnimation();              方法
 *    Geometrist.canContain();                   方法
 *
 * 4、功能展示：
 *  (1)开启监控，显示被监控车辆；
 *  (2)绘制监控区，监控车辆的进出。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MonitorActivity extends Activity implements OnTouchListener{
	private MapView        mMapView           = null;
	private Map            m_Map              = null;
	private TextView       mWarningInfo       = null;
	private ImageButton    btn_start_monitor  = null;
	private ImageButton    btn_draw_fence     = null;
	private ImageButton    btn_clear_fence    = null;
	private ImageButton    btn_entire         = null;
	private ImageButton    btn_zoom_in        = null;
	private ImageButton    btn_zoom_out       = null;

	private MyApplication mApp               = null;
	private MessageReciver mMessageReciver    = null;

	public static boolean  startMonitor       = false;
	private boolean        mExitEnable        = false;

	DisplayManager mDisplayManager            = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 显示车辆监控界面
		setContentView(R.layout.monitorlayout);

		mApp = (MyApplication) getApplication();
		mApp.registerActivity(this);

		if(mMapView == null) {
			mMapView = (MapView) findViewById(R.id.mapview);
			Workspace workspace = ((MyApplication)getApplication()).getWorkspace();

			mMapView.getMapControl().setOnTouchListener(this);
			mMapView.getMapControl().getMap().setWorkspace(workspace);
			mMapView.getMapControl().getMap().open(workspace.getMaps().get(0));
			mMapView.getMapControl().getMap().setCenter(new Point2D(12963755,4865688));
			mMapView.getMapControl().getMap().setScale(1/80000.0);
//			mMapView.getMapControl().getMap().setFullScreenDrawModel(true);
			mMapView.getMapControl().getMap().refresh();

			m_Map = mMapView.getMapControl().getMap();


		}

		if(mWarningInfo == null){
			mWarningInfo = (TextView) findViewById(R.id.warninginfo);
			mWarningInfo.setVisibility(View.GONE);
		}

		mDisplayManager = new DisplayManager(mMapView);
		mDisplayManager.attachUI(mWarningInfo);
		// 清空监控区,非正常退出时，可能会有监控区未清除
		mDisplayManager.getDomainMonitor().clearMonitorDomain();

		btn_start_monitor = (ImageButton) findViewById(R.id.btn_start_monitor);
		btn_start_monitor.setOnClickListener(imageButtonListener);

		btn_draw_fence = (ImageButton) findViewById(R.id.btn_draw_fence);
		btn_draw_fence.setOnClickListener(imageButtonListener);

		btn_clear_fence = (ImageButton) findViewById(R.id.btn_clear_fence);
		btn_clear_fence.setOnClickListener(imageButtonListener);

		btn_entire = (ImageButton) findViewById(R.id.btn_entire);
		btn_entire.setOnClickListener(imageButtonListener);

		btn_zoom_in = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn_zoom_in.setOnClickListener(imageButtonListener);

		btn_zoom_out = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn_zoom_out.setOnClickListener(imageButtonListener);

		mMessageReciver = new MessageReciver(mDisplayManager);

		IntentFilter intentfilter = new IntentFilter(MyApplication.BroadcastAction);
		registerReceiver(mMessageReciver, intentfilter);

		//初始化音效管理器
		SoundMonitor.init(this);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!mExitEnable){
				Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT ).show();
				mExitEnable = true;
			}else{
				exit();

				//彻底退出程序
				mApp.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		exit();
		super.onDestroy();
	}


	private void exit() {
		// 清空监控区
		mDisplayManager.getDomainMonitor().clearMonitorDomain();
		//释放当前资源
		mMapView.getMapControl().getMap().close();

		//停止后台服务
		Intent intent = new Intent();
		intent.setAction("com.supermap.backstageservice.START");
		stopService(intent);
		unregisterReceiver(mMessageReciver);
	}

	/**
	 * 按钮监听对象
	 */
	OnClickListener imageButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.btn_start_monitor:                    // 启动车辆监控
					startMonitor = true;

					break;
				case R.id.btn_draw_fence:                       // 绘制监控区
					mDisplayManager.drawMonitorDomain();

					break;
				case R.id.btn_clear_fence:                      // 清除监控区

					mDisplayManager.getDomainMonitor().clearMonitorDomain();

					m_Map.getTrackingLayer().clear();
					m_Map.refresh();

					break;
				case R.id.btn_entire:
					m_Map.viewEntire();
					m_Map.refresh();

					break;
				case R.id.btn_zoomIn:
					m_Map.zoom(2);
					m_Map.refresh();

					break;
				case R.id.btn_zoomOut:
					m_Map.zoom(0.5);
					m_Map.refresh();

					break;
				default:
					break;
			}
		}
	};

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub

		mMapView.getMapControl().onMultiTouch(event);
		mExitEnable = false;                       // 单击一次返回键后触摸屏幕，取消退出应用

		int action = event.getAction();

		// 当抬起手，并且有新的Region类的几何对象时，提交绘制的几何图形
		if(action == MotionEvent.ACTION_UP){
			if (mMapView.getMapControl().getCurrentGeometry() != null && mMapView.getMapControl().getCurrentGeometry().getType()==GeometryType.GEOREGION) {
				mMapView.getMapControl().submit();

				return true;
			}
		}
		return true;
	}
}
