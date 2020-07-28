package com.example.openofflinescene;

import com.example.openofflinescene.R;

import com.supermap.data.Environment; 
import com.supermap.realspace.*;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * <p>
 * Title:打开离线三维场景
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
 * 1、范例简介：示范如何打开用户通过iServer下载的离线三维场景数据
 * 2、示例数据：无
 * 3、关键类型/成员: 
 *      SceneControl.getScene 方法
 *      Scene.open 方法

 * 4、使用步骤：
 *   (1)将SampleData/CBD/中的数据拷贝到Android设备 sd卡中的/sdcard/SuperMap/LocalData/下
 *   (2)将程序中url是/SampleData/CBD/下的文件夹名称，sceneName修改为用户通过iServer发布的三维场景名，密码默认是supermap
 *   (3)运行程序，点击打开离线场景按钮，打开场景。
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
	
    private SceneControl m_sceneControl;
    private Button m_btnOpen;
    
    // 离线三维场景数据名称
    String url = "192_168_1_111_8090_iserver_services_realspace-CBD";
    // 三维场景名称
	String sceneName = "CBD";
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
	    Environment.initialization(this);
	    
	    //组件功能必须在 Environment 初始化之后才能调用
	    setContentView(R.layout.activity_main);
	    
	    //获取场景控件 
	    m_sceneControl = (SceneControl)findViewById(R.id.SceneControl);
	    
	    m_btnOpen = (Button)findViewById(R.id.btn_open);
	    m_btnOpen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 获取Scene
				Scene scene = m_sceneControl.getScene();
				// 根据url,通过已知的场景名称打开场景, 密码默认是supermap
				scene.open(url, sceneName, "supermap");
			}
		});
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