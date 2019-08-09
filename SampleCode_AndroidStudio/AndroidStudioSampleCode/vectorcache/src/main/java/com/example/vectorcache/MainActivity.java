package com.example.vectorcache;

import java.io.File;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.WindowManager;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:矢量地图包
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
 * 1、范例简介：展示矢量地图包数据的使用
 * 2、示例数据：
 * 		安装目录/SampleData/VectorCache/VectorCache.smwu;
 * 		解压    安装目录/SampleData/VectorCache/北京_0926.rar  文件为  安装目录/SampleData/VectorCache/北京_0926/北京.xml;
 * 3、关键类型/成员: 
 *	    Datasources.open 方法
 *      Layers.add 方法
 *      
 * 4、使用步骤：
 *  (1)单指平移地图
 *  (2)双指缩放地图
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
	
	public Workspace mWorkspace = null;
	MapView mMapView = null;
	MapControl mMapControl = null;
	public Map mMap = null;
	
	String m_strDataPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SampleData/";
	String m_strPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperMap/";
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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Environment.setLicensePath(m_strPath+"License/");
		Environment.initialization(this);
		Environment.setOpenGLMode(true);
		
		setContentView(R.layout.activity_main);
		
		mWorkspace = new Workspace();
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapControl = ( (MapView)findViewById(R.id.mapview) ).getMapControl();
		mMap = mMapControl.getMap();
		mMap.setWorkspace(mWorkspace);
		mMap.setFullScreenDrawModel(true);
		
		InitData();
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// 打开workspacePath的工作空间，并更新地图名数组及数据源别名数组
	public void openWorkspace(String filePath)
	{
		WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();       
        File f =new File(filePath);
        String fileName=f.getName();
        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
        if (prefix.compareToIgnoreCase("SMWU") == 0) {
        	connectionInfo.setType(WorkspaceType.SMWU);
		} else if (prefix.compareToIgnoreCase("SXWU") == 0){
			connectionInfo.setType(WorkspaceType.SXWU);
		}       
        connectionInfo.setServer(filePath); 
		if (mWorkspace != null)
		{	
			mMap.close();
			mWorkspace.close();	
		}
        mWorkspace.open(connectionInfo);
	}
	
	public void InitData()
	{
		// 打开工作空间，使用工作空间的符号库资源
		openWorkspace(m_strDataPath+"VectorCache/VectorCache.smwu");
		
		// 打开 OpenGLCache 数据源引擎
		DatasourceConnectionInfo dsInfo = new DatasourceConnectionInfo();
		dsInfo.setServer(m_strDataPath+"VectorCache/北京_0926/北京.xml");
		dsInfo.setEngineType(EngineType.OpenGLCache);
		dsInfo.setAlias("北京");
		Datasource ds = mWorkspace.getDatasources().open(dsInfo);
		
		// 把OpenGLCache数据集添加到地图中
		mMap.getLayers().add(ds.getDatasets().get(0), true);
		mMap.setScale(1/144447.92746805);
		mMap.setCenter(new Point2D(12957168, 4854184));
	}

}
