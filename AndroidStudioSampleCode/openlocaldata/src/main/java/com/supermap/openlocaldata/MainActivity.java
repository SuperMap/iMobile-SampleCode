package com.supermap.openlocaldata;

import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.*;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:初始化加载本地场景，避免出白球的情况。
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
 * 1、范例简介：示范如何在初始化情况下打开一个本地场景数据 2、示例数据：无 3、关键类型/成员: SceneControl.getScene 方法
 * Scene.open 方法
 * 
 * 4、使用步骤： (1)将SampleData/珠峰/中的数据拷贝到Android设备 sd卡中的/sdcard/SuperMap/data/下
 * (2)sceneName修改为用户通过发布的三维场景名， (3)运行程序直接打开场景。
 * ----------------------------------------
 * --------------------------------------
 * ============================================================================>
 * </p>
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

@SuppressLint({ "HandlerLeak", "ShowToast", "SdCardPath" })
public class MainActivity extends Activity {
	private Workspace m_workspace;
	private Scene m_scene;
	private SceneControl mSceneControl;
	// 离线三维场景数据名称
	String workspacePath = "/sdcard/SuperMap/data/珠峰/珠峰.sxwu";
	// 三维场景名称
	String sceneName = "珠峰";
	WorkspaceConnectionInfo info;
	WorkspaceType workspaceTypetemp = null;
	// 第一次启动的标志量
	boolean firstStart = false;
	// scenecontrol初始化完成消息标志量
	final int SCENECONTROL_INITED = 0;

	boolean isLicenseAvailable = false;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
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
		requestPermissions() ;
		Environment.setLicensePath(sdcard+"/SuperMap/license/");
		Environment.initialization(this);
		info = new WorkspaceConnectionInfo();
		// 组件功能必须在 Environment 初始化之后才能调用
		setContentView(R.layout.activity_main);
		mSceneControl = (SceneControl) findViewById(R.id.sceneControl);
		// 获取当前许可的状态，返回true 许可可用，返回false 许可不可用，不可用情况下无法打开本地场景。
		isLicenseAvailable = isLicenseAvailable();
		// 获取场景控件，在许可可用情况下打开本地场景。
		// 在非按钮事件、非触摸事件中,需要在此接口中写有关scene的方法，防止场景控件绘制失败。
		mSceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner(){

			@Override
			public void onSuccess(String success) {
				if (!firstStart && isLicenseAvailable) {
					openLocalScene();
					firstStart = true;
				}
				
			}
			
		});

	}

	// 打开一个本地场景
	private void openLocalScene() {

		// 新建一个工作空间对象
		if (m_workspace == null) {
			m_workspace = new Workspace();
		}
		// 根据工作空间类型，设置服务路径和类型信息。
		workspaceTypetemp = WorkspaceType.SXWU;
		info.setServer(workspacePath);
		info.setType(workspaceTypetemp);
		// 场景关联工作空间
		if (m_workspace.open(info)) {
			m_scene = mSceneControl.getScene();
			m_scene.setWorkspace(m_workspace);
		}
		// 打开场景
		boolean successed = mSceneControl.getScene().open(sceneName);
		if (successed) {
			Toast.makeText(MainActivity.this, "打开场景成功", Toast.LENGTH_LONG);
		}
	}

	// 判断许可是否可用
	@SuppressLint("ShowToast")
	private boolean isLicenseAvailable() {
		LicenseStatus licenseStatus = Environment.getLicenseStatus();
		if (!licenseStatus.isLicenseExsit()) {
			Toast.makeText(MainActivity.this, "许可不存在，场景打开失败，请加入许可", Toast.LENGTH_LONG).show();
			return false;
		} else if (!licenseStatus.isLicenseValid()) {
			Toast.makeText(MainActivity.this, "许可过期，场景打开失败，请更换有效许可", Toast.LENGTH_LONG).show();
			return false;
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