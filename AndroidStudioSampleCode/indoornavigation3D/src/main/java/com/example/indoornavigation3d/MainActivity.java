package com.example.indoornavigation3d;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Point3D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.indoor3d.FloorListView3D;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation3D;
import com.supermap.realspace.PixelToGlobeMode;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;
import com.supermap.realspace.SceneView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:三维室内导航
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
 * 1、范例简介：二三维一体，展示室内导航，二三维地图自由切换
 * 2、示例数据：
 * 		目录../SampleData/IndoorNavigation3DData/凯德Mall.sxwu;
 * 3、关键类型/成员: 
 *	    Navigation3D,FloorListView3D,SceneControl,SceneView
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
public class MainActivity extends Activity {

	private Workspace m_workspace = null;
	private SceneView m_sceneView = null;
	private SceneControl  m_sceneControl = null;
	private Scene m_scene = null;
	
	private Navigation3D m_navigation3D = null;
	private FloorListView3D m_floorListView3D = null;
	
	//当前操作状态，包括添加起点、添加途径点、添加终点、分析、导航、无状态
	private enum NAVI_STATE {STATE_ADDSTARTPOINT, STATE_ADDVIAPOINT, 
								STATE_ADDENDPOINT, STATE_ANALYSE, 
								STATE_NAVIGATION,STATE_NULL};	
	NAVI_STATE m_naviState = NAVI_STATE.STATE_NULL;	//初始为无状态
		
	private boolean bLongPressEnable = false;	//地图长按状态（true：长按可添加控制点，falae:不可添加控制点）

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
       String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
       Environment.setLicensePath(sdcard + "/SuperMap/license/");	//设置许可文件路径
            
       Environment.initialization(this);	//组件功能必须在 Environment 初始化之后才能调用
       setContentView(R.layout.activity_main);
         
       //打开工作空间
       m_workspace = new Workspace();
       WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
       info.setServer(sdcard+"/SampleData/IndoorNavigation3DData/3D室内场景.sxwu");
       info.setType(WorkspaceType.SXWU);
       boolean result = m_workspace.open(info);  
       if (!result) {
			System.out.println("工作空间打开失败");
			return;
       }  
       
       m_sceneView= (SceneView)findViewById(R.id.scene_control);
       m_sceneControl= m_sceneView.getSceneControl();
       m_sceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {//场景控件初始化回调监听
			
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				
				//打开场景
				m_scene = m_sceneControl.getScene();
				m_scene.setWorkspace(m_workspace); 
	    		String mapName = m_workspace.getScenes().get(0);
	    		m_scene.open(mapName);
	    		
	    		m_navigation3D = m_sceneView.getNavigation();		//获取导航模块
	    		m_sceneControl.setNavigationControlVisible(true);	//设置导航条可见
				
				//初始化FloorList
	    		m_floorListView3D = (FloorListView3D)findViewById(R.id.floor_list_view_3d);
	    		m_floorListView3D.setVisibility(View.VISIBLE);

	    		//先设置导航类，再linkSceneControl
	    		m_floorListView3D.setNavigation3D(m_navigation3D);
	    		m_floorListView3D.linkScenepControl(m_sceneControl, m_workspace);
				//设置室内地图所在的数据源	
				m_navigation3D.setDatasource(m_workspace.getDatasources().get("beijingMall"));
				//设置三场景控件
				m_navigation3D.setSceneControl(m_sceneControl);
				//设置用户手势识别器(用于通过手势添加导航点)
				m_sceneControl.setGestureDetector(new GestureDetector(m_sceneControl.getContext(), mSceneGestrueListener));
				//添加导航引导信息更新监听器
				m_navigation3D.addNaviInfoListener(new NaviListener() {
					
					@Override
					public void onStopNavi() {
						
						//结束导航后，关闭导航状态
		        		m_naviState = NAVI_STATE.STATE_NULL;
		        		
						m_navigation3D.cleanPath();
						System.out.println("导航停止");
					}
					
					@Override
					public void onStartNavi() {
						
					}
					
					@Override
					public void onPlayNaviMessage(String message) {
						
					}
					
					@Override
					public void onNaviInfoUpdate(NaviInfo naviInfo) {
						
					}
					
					@Override
					public void onAdjustFailure() {
						
					}
					
					@Override
					public void onAarrivedDestination() {
		
						//到达目的地，关闭导航状态
		        		m_naviState = NAVI_STATE.STATE_NULL;
		        		
						m_navigation3D.cleanPath();
						System.out.println("导航到达目的地");
					}
				});
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //起点按钮事件
    public void buttonStart_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION || m_naviState ==NAVI_STATE.STATE_ANALYSE ){
    		Toast.makeText(m_sceneControl.getContext(), "导航或分析状态，添加起点无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bLongPressEnable = true; //地图可以长按
    	m_naviState = NAVI_STATE.STATE_ADDSTARTPOINT;	 //切换添加起点状态
    }
    //终点按钮事件
    public void buttonEnd_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION  || m_naviState ==NAVI_STATE.STATE_ANALYSE ){
    		Toast.makeText(m_sceneControl.getContext(), "导航或分析状态，添加终点无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bLongPressEnable = true; //地图可以长按
    	m_naviState = NAVI_STATE.STATE_ADDENDPOINT;	 //切换可以添加终点状态
    }
    //途径点按钮事件
    public void buttonVia_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION  || m_naviState ==NAVI_STATE.STATE_ANALYSE ){
    		Toast.makeText(m_sceneControl.getContext(), "导航或分析状态，添加途径点无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	bLongPressEnable = true; //地图可以长按
    	m_naviState = NAVI_STATE.STATE_ADDVIAPOINT;	 //切换可以添加途径点状态
    }
    //分析按钮事件
    public void buttonAnalyse_Click(View view)
    {
    	if(m_naviState == NAVI_STATE.STATE_NAVIGATION){
    		Toast.makeText(m_sceneControl.getContext(), "导航状态，分析功能无效！", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(m_naviState == NAVI_STATE.STATE_ANALYSE){
    		return;
    	}
		
    	//路径分析
    	boolean bResult = m_navigation3D.routeAnalyst();
    	if(bResult){
    		Toast.makeText(m_sceneControl.getContext(), "路径分析成功", Toast.LENGTH_SHORT).show();
    	}
    	else{
    		Toast.makeText(m_sceneControl.getContext(), "路径分析失败", Toast.LENGTH_SHORT).show();
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
    	if(!m_navigation3D.startGuide(1)){
			Toast.makeText(m_sceneControl.getContext(), "导航启动失败", Toast.LENGTH_SHORT).show();
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
    		Toast.makeText(m_sceneControl.getContext(), "导航状态，不能清除", Toast.LENGTH_SHORT).show();
			return;
    	}
    	
    	//状态更新
    	bLongPressEnable = false; 				//地图不可用长按
    	m_naviState = NAVI_STATE.STATE_NULL;	//不能添加导航点
    	
    	//清除路径分析结果，包括导航点等
    	m_navigation3D.cleanPath();
    }
    
  //接收长按事件
  	private GestureDetector.SimpleOnGestureListener mSceneGestrueListener = new SimpleOnGestureListener(){
  		public void onLongPress(MotionEvent e) {
  			if(!bLongPressEnable){
  				return;
  			}
  			int x = (int) e.getX();
  			int y = (int) e.getY();
  			Point pt = new Point(x, y);
  			Point3D pt3d = m_scene.pixelToGlobe(pt, PixelToGlobeMode.TERRAINANDMODEL);
  			
  			switch (m_naviState){
			case STATE_ADDSTARTPOINT:	
				//设置起点：可以连续设置，保留最后一次设置的值
				m_navigation3D.setStartPoint(pt3d.getX(), pt3d.getY(), pt3d.getZ());
				break;
			case STATE_ADDENDPOINT:		
				//设置终点：可以连续设置，保留最后一次设置的值
				m_navigation3D.setDestinationPoint(pt3d.getX(), pt3d.getY(), pt3d.getZ());
				break;
			case STATE_ADDVIAPOINT:
				//设置途径点：可以连续设置，途径点个数不限制 注意：没有起点和终点时不能添加途径点
				m_navigation3D.addWayPoint(pt3d.getX(), pt3d.getY(), pt3d.getZ());
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
