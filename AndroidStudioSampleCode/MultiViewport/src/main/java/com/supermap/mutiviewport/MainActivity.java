package com.supermap.mutiviewport;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Layer3D;
import com.supermap.realspace.Layer3Ds;
import com.supermap.realspace.MultiViewportMode;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;


/**
 * <p>
 * Title:分屏
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户如何使用安卓分屏的功能2、示例数据：CBD_android 3、关键类型/成员: SceneControl.getScene
 * 方法 Scene.open 方法，Scene.setMultiViewportMode(MultiViewportMode multiViewportMode );
 * 
 * 4、使用步骤： (1)将SampleData/CBD_android/中的数据拷贝到Android设备 sd卡中的/sdcard/SuperMap/data/下
 * (2)场景名：CBD_android
 * (3)运行程序，初始化直接加载场景。
 * (4)根据界面按钮，用户可以选择设置对应的分屏效果，例如：水平分屏，垂直分屏，三分屏，四分屏。
 * -----------------------------------------------------------------------------
 * -
 * ============================================================================>
 * </p>
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
@SuppressLint("ShowToast")
public class MainActivity extends Activity {
  
	private SceneControl mSceneControl;
	
	private Button btn_none,btn_quad,btn_triple,btn_vertical,btn_horizontal;
	private Workspace m_workspace;
	WorkspaceConnectionInfo info;
	WorkspaceType workspaceTypetemp = null;
	@SuppressLint("SdCardPath")
	String workspacePath = "/sdcard/SuperMap/data/CBD_android/CBD_android.sxwu";
	// 三维场景名称
	String sceneName = "CBD_android";
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.setLicensePath(sdcard+"/SuperMap/license/");
		Environment.initialization(this);
		setFullScreen();
		setContentView(R.layout.activity_main);
		mSceneControl=(SceneControl)findViewById(R.id.sceneControl);
		btn_none=(Button)findViewById(R.id.btn_none);
		btn_quad=(Button)findViewById(R.id.btn_quad);
		btn_triple=(Button)findViewById(R.id.btn_triple);
		btn_vertical=(Button)findViewById(R.id.btn_vertical);
		btn_horizontal=(Button)findViewById(R.id.btn_horizontal);
		
		//初始化回调接口中打开本地场景，前提是需要许可可用的情况下。

        mSceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {
					
					@Override
					public void onSuccess(String success) {
						if(isLicenseAvailable()){
							openLocalSence();
						}
						
					}
				});
		/**
		 * 退出分屏
		 */
		btn_none.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mSceneControl.getScene().setMultiViewportMode(MultiViewportMode.RealspaceMultiViewportModeNone);
			}
		});
		
		/**
		 * 水平分屏
		 */
		btn_horizontal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Scene scene=mSceneControl.getScene();
				scene.setMultiViewportMode(MultiViewportMode.RealspaceMultiViewportModeHorizontal);
				Layer3Ds layer3ds=scene.getLayers();
				Layer3D  layer3dA=layer3ds.get("buildingPlanA@CBD");
				layer3dA.setVisibleInViewport(0, true);
				layer3dA.setVisibleInViewport(1, false);
				Layer3D  layer3dB=layer3ds.get("buildingPlanB");
				layer3dB.setVisible(true);
				layer3dB.setVisibleInViewport(0, false);
				layer3dB.setVisibleInViewport(1, true);
			}
		});
		
		/**
		 * 垂直分屏
		 */
		btn_vertical.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Scene scene=mSceneControl.getScene();
				scene.setMultiViewportMode(MultiViewportMode.RealspaceMultiViewportModeVertical);
				Layer3Ds layer3ds=scene.getLayers();
				Layer3D  layer3dA=layer3ds.get("buildingPlanA@CBD");
				layer3dA.setVisibleInViewport(0, true);
				layer3dA.setVisibleInViewport(1, false);
				Layer3D  layer3dB=layer3ds.get("buildingPlanB");
				layer3dB.setVisible(true);
				layer3dB.setVisibleInViewport(0, false);
				layer3dB.setVisibleInViewport(1, true);
			}
		});
		/**
		 * 三分屏
		 */
		btn_triple.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Scene scene=mSceneControl.getScene();
				scene.setMultiViewportMode(MultiViewportMode.RealspaceMultiViewportModeTriple);
				Layer3Ds layer3ds=scene.getLayers();
				Layer3D  layer3dA=layer3ds.get("buildingPlanA@CBD");
				layer3dA.setVisibleInViewport(0, true);
				layer3dA.setVisibleInViewport(1, false);
				layer3dA.setVisibleInViewport(2, true);
				Layer3D  layer3dB=layer3ds.get("buildingPlanB");
				layer3dB.setVisible(true);
				layer3dB.setVisibleInViewport(0, false);
				layer3dB.setVisibleInViewport(1, true);
				layer3dB.setVisibleInViewport(2, true);
			}
		});
		/**
		 * 四分屏
		 */
		btn_quad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Scene scene=mSceneControl.getScene();
				scene.setMultiViewportMode(MultiViewportMode.RealspaceMultiViewportModeQuad);
				Layer3Ds layer3ds=scene.getLayers();
				Layer3D  layer3dA=layer3ds.get("buildingPlanA@CBD");
				layer3dA.setVisibleInViewport(0, true);
				layer3dA.setVisibleInViewport(1, false);
				layer3dA.setVisibleInViewport(2, true);
				layer3dA.setVisibleInViewport(3, false);
				Layer3D  layer3dB=layer3ds.get("buildingPlanB");
				layer3dB.setVisible(true);
				layer3dB.setVisibleInViewport(0, false);
				layer3dB.setVisibleInViewport(1, true);
				layer3dB.setVisibleInViewport(2, false);
				layer3dB.setVisibleInViewport(3, true);
			}
		});
	}
	/**
	 * 打开本地场景
	 */
	private void openLocalSence(){
		// 新建一个工作空间对象
		if (m_workspace == null) {
			m_workspace = new Workspace();
		}
		if (info == null) {
			info = new WorkspaceConnectionInfo();
		}
		// 根据工作空间类型，设置服务路径和类型信息。
		
		workspaceTypetemp = WorkspaceType.SXWU;
		info.setServer(workspacePath);
		info.setType(workspaceTypetemp);
		// 场景关联工作空间
		if (m_workspace.open(info)) {
			Scene m_scene = mSceneControl.getScene();
			m_scene.setWorkspace(m_workspace);
		}
		// 打开场景
		boolean successed = mSceneControl.getScene().open(sceneName);
		if (successed) {
			Toast.makeText(MainActivity.this, "打开场景成功", Toast.LENGTH_LONG);
		}
	}
	// 判断许可是否可用
		private boolean isLicenseAvailable() {

			LicenseStatus licenseStatus = Environment.getLicenseStatus();
			boolean a = licenseStatus.isLicenseValid();
			if (!licenseStatus.isLicenseExsit()) {
				Toast.makeText(MainActivity.this, "许可不存在，场景打开失败，请加入许可", Toast.LENGTH_LONG).show();
				return false;
			} else if (!licenseStatus.isLicenseValid()) {
				Toast.makeText(MainActivity.this, "许可过期，场景打开失败，请更换有效许可", Toast.LENGTH_LONG).show();
				return false;
			}
			return true;
		}
		//充满 屏幕
		private void setFullScreen() {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
}
