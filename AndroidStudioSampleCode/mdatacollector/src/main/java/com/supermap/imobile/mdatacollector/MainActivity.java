package com.supermap.imobile.mdatacollector;

/**
 * <p>
 * Title:多媒体采集示范代码
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码 
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 * 
 * 1、范例简介：示范如何进行多媒体采集以及数据上传下载
 * 2、示例数据：/sdcard/SampleData/MDataCollectorData/media.smwu
 * 3、关键类型/成员: 
 *		MDataCollector.captureImage();
 *      MDataCollector.captureVideo();
 *      MDataCollector.startCaptureAudio();
 *      MDataCollector.stopCaptureAudio();
 *      MDataCollector.setLocalFilePath();
 *      MDataCollector.setMediaDataset();
 *      MDataCollector.downloadMediaFile();
 *      MDataCollector.uploadMediaFile();
 *      MDataCollector.addNetworkAccessMediaFileListener();
 * 4、使用步骤：
 *   (1)点击"采集"，可进行拍照，录像，录音等操作；
 *   (2)点击"查看"，查看已采集的点，点击相应的点自动打开采集的多媒体文件；
 *   (3)点击"网络"，可进行上传或下载等操作，但在上传或下载前，需要根据提示登录iPortal服务。
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
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.Color;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.GeometryType;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity  {

	private Button btn_Network      = null;
	private Button btn_captureImage = null;
	private Button btn_captureVideo = null;
	private Button btn_captureAudio = null;
	private Button btn_download     = null;
	private Button btn_upload       = null;
	private Button btn_stopCaptureAudio = null;
	private TextView mTextView      = null;
	private boolean mExitEnable = false;
	
	private MyApplication  m_App = null;
	private LoginPopUp     m_LoginPopup = null;
	private NetworkAccess  m_NetworkAccess = null;
	private MapView        m_MapView    = null;
	private MapControl     m_MapControl = null;
	private Map            m_Map        = null;
	private Workspace      m_Workspace  = null;
	private Datasource     m_Datasource = null;
	private Layer          m_MediaLayer = null;
	private DrawRegion     m_DrawRegion = null;
	
	private char transmission = 0;          // 1:download, 2:upload
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
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        m_App = MyApplication.getInstance();
        m_App.registerActivity(this);
        
       	 if(initMap()) {
       		m_Map.setFullScreenDrawModel(true);                    // 在map.open()之后设置
        	initView();
        	m_NetworkAccess = new NetworkAccess(this,m_Datasource, m_Map);
			m_LoginPopup = new LoginPopUp(this, m_NetworkAccess);
			m_NetworkAccess.setLoginPopUp(m_LoginPopup);
			m_DrawRegion = new DrawRegion(m_MapControl, m_NetworkAccess);
        } else {
        	m_App.showInfo("打开地图失败");
        }
       	 initPhotoError();
    }
	private void initPhotoError(){
		// android 7.0系统解决拍照的问题
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		builder.detectFileUriExposure();
	}

	
    /**
     * 初始化地图
     * @return  true or false, 打开地图返回true, 否则false;
     */
    private boolean initMap() {
    	m_MapView = (MapView) findViewById(R.id.m_MapView);
    	m_MapControl = m_MapView.getMapControl();
    	m_Map = m_MapControl.getMap();
    	
    	m_Workspace = new Workspace();
    	String pathWK = MyApplication.RootPath + "/SampleData/MDataCollectorData/media.smwu";
    	WorkspaceConnectionInfo wkInfo = new WorkspaceConnectionInfo();
    	wkInfo.setServer(pathWK);
    	wkInfo.setType(WorkspaceType.SMWU);
    	m_Workspace.open(wkInfo);
    	m_Map.setWorkspace(m_Workspace);
    	m_Datasource = m_Workspace.getDatasources().get("media");
    	if(m_Datasource == null)
    		return false;
    	return m_Map.open(m_Workspace.getMaps().get(0));
    }
    
    /**
     * 初始化界面按钮
     */
    private void initView() {
    
    	((Button) findViewById(R.id.btn_collect)).setOnClickListener(listener1);
    	((Button) findViewById(R.id.btn_review)).setOnClickListener(listener1);
    	btn_Network = (Button) findViewById(R.id.btn_network);
    	btn_Network.setOnClickListener(listener1);
    	
    	btn_captureImage = (Button) findViewById(R.id.btn_captureImage);
    	btn_captureVideo = (Button) findViewById(R.id.btn_captureVideo);
    	btn_captureAudio = (Button) findViewById(R.id.btn_captureAudio);
    	btn_download     = (Button) findViewById(R.id.btn_download);
    	btn_upload       = (Button) findViewById(R.id.btn_upload);
    	btn_stopCaptureAudio = (Button) findViewById(R.id.btn_stopcaptureaudio);
    	
     	btn_captureImage.setOnClickListener(listener2);
    	btn_captureVideo.setOnClickListener(listener2);
    	btn_captureAudio.setOnClickListener(listener2);
    	btn_download.setOnClickListener(listener2);
    	btn_upload.setOnClickListener(listener2);
    	btn_stopCaptureAudio.setOnClickListener(listener2);
    }
    
	private OnClickListener listener1 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_collect:
				if (!m_NetworkAccess.isMediaDatasetExisted()) {
					
					m_NetworkAccess.initMDataCollector();
					
				} 
				if(btn_captureVideo.getVisibility() == View.VISIBLE){
					setButtonVisibility1(View.GONE);
				}else{
				    setButtonVisibility1(View.VISIBLE);
				}
				if(m_MapControl.getAction() == Action.DRAWPLOYGON)
				   m_DrawRegion.enabledDrawing(false);
				break;
			case R.id.btn_review:
				if (!m_NetworkAccess.isMediaDatasetExisted()) {
					
					m_NetworkAccess.initMDataCollector();
					
				} 
				showMediaData();
				if(m_MapControl.getAction() == Action.DRAWPLOYGON)
					   m_DrawRegion.enabledDrawing(false);
				break;
			case R.id.btn_network:
				if (m_NetworkAccess.isLogined()) {
					if(btn_upload.getVisibility() == View.VISIBLE){
						setButtonVisibility2(View.GONE);
					}else{
					    setButtonVisibility2(View.VISIBLE);
					}
				} else {
					m_LoginPopup.show();
					m_App.showInfo("请先登录");
				}
				if(m_MapControl.getAction() == Action.DRAWPLOYGON)
					   m_DrawRegion.enabledDrawing(false);
				break;
		    default:
			    break;
			}
		}

		private void showMediaData() {
			if (m_MediaLayer == null && m_NetworkAccess.getMDataCollector() != null) {
				m_MediaLayer = m_Map.getLayers().add(m_NetworkAccess.getMDataCollector().getMediaDataset(), true);
				LayerSettingVector layerSetting = new LayerSettingVector();
				GeoStyle geoStyle_P = new GeoStyle();
				geoStyle_P.setLineColor(new Color(255, 50, 20));
				geoStyle_P.setMarkerAngle(14.0);
				geoStyle_P.setMarkerSize(new Size2D(10, 10));
				layerSetting.setStyle(geoStyle_P);
				m_MediaLayer.setAdditionalSetting(layerSetting);
			}
			m_MapControl.setAction(Action.SELECT);
			m_Map.refresh();
		}
	};
	
	private OnClickListener listener2 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_captureImage:
				m_NetworkAccess.getMDataCollector().captureImage();
				setButtonVisibility1(View.GONE);
				break;
			case R.id.btn_captureVideo:
				m_NetworkAccess.getMDataCollector().captureVideo();
				setButtonVisibility1(View.GONE);
				break;
			case R.id.btn_captureAudio:
				m_NetworkAccess.getMDataCollector().startCaptureAudio();
				setButtonVisibility1(View.GONE);
				btn_stopCaptureAudio.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_download:
				transmission = 1;
				m_DrawRegion.enabledDrawing(true);
				m_App.showInfo("请绘制一个区域");
				setButtonVisibility2(View.GONE);
				break;
			case R.id.btn_upload:
				transmission = 2;
				m_DrawRegion.enabledDrawing(true);
				setButtonVisibility2(View.GONE);
				m_App.showInfo("请绘制一个区域");
				break;
			case R.id.btn_stopcaptureaudio:
				m_NetworkAccess.getMDataCollector().stopCaptureAudio();
				btn_stopCaptureAudio.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};
	
	private void setButtonVisibility1(int viewStatus){
		btn_captureImage.setVisibility(viewStatus);
		btn_captureVideo.setVisibility(viewStatus);
		btn_captureAudio.setVisibility(viewStatus);
	}
	
	private void setButtonVisibility2(int viewStatus){
		btn_download.setVisibility(viewStatus);
		btn_upload.setVisibility(viewStatus);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(m_NetworkAccess != null && m_NetworkAccess.getMDataCollector() != null)
		   m_NetworkAccess.getMDataCollector().onActivityResult(requestCode, resultCode, data);
    }

	public void setNetworkStatus(boolean enabled, String info){
		btn_Network.setEnabled(enabled);
		if(mTextView == null)
			mTextView = (TextView) findViewById(R.id.tv_info);
		mTextView.setText(info);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!mExitEnable){
				Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
				mExitEnable = true;
			}else{
				m_App.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			super.dispatchTouchEvent(event);
			mExitEnable = false;                       // 单击一次返回键后触摸屏幕，取消退出应用
			
			int action = event.getAction();
			
			// 当抬起手，并且有新的Region类的几何对象时，提交绘制的几何图形
			if(action == MotionEvent.ACTION_UP){
				GeoRegion geometry = (GeoRegion)m_MapControl.getCurrentGeometry();
				if (geometry != null && !geometry.isEmpty()&& geometry.getType()==GeometryType.GEOREGION) {
					m_DrawRegion.showPopupWn(this, transmission);
					geometry = null;
					return true;
				}
			}
			return true;
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
}
