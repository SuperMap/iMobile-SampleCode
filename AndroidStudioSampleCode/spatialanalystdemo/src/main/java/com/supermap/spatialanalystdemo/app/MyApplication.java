package com.supermap.spatialanalystdemo.app;


import android.app.Application;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
//import com.supermap.spatialanalystdemo.lic.LicConfig;
import com.supermap.spatialanalystdemo.configuration.DefaultDataConfiguration;
import com.supermap.spatialanalystdemo.filemanager.MyAssetManager;
import com.supermap.spatialanalystdemo.filemanager.MySharedPreferences;


public class MyApplication extends Application {
	public static String DATAPATH = "";
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

	private static MyApplication sInstance = null;
	private Workspace mWorkspace = null;

	@Override
	public void onCreate()
	{
		super.onCreate();
//		LicConfig.configLic(this);

		DATAPATH = this.getFilesDir().getAbsolutePath() + "/";
		sInstance = this;

		// Initialization
		MySharedPreferences.init(this);
		MyAssetManager.init(this);

		// Cofigure datas and license file before using them
		new DefaultDataConfiguration().autoConfig();

		// Initialize SuperMap Enviroment
//		Environment.setLicensePath(LicConfig.getConfigPath());
		Environment.setLicensePath(DefaultDataConfiguration.LicensePath);
		Environment.initialization(this);

		mWorkspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(DefaultDataConfiguration.WorkspacePath);
		info.setType(WorkspaceType.SMWU);
		if(!mWorkspace.open(info))
		{
			showError("工作空间损坏");
		}


	}

	/**
	 * 获取当前打开的工作空间
	 * @return
	 */
	public Workspace  getOpenedWorkspace()
	{
		return mWorkspace;
	}

	/**
	 * 获取应用对象
	 * @return
	 */
	public static MyApplication getInstance()
	{
		return sInstance;
	}

	/**
	 * Toast显示信息
	 * @param info
	 */
	public void showInfo(String info)
	{
		Toast toast = Toast.makeText(sInstance, info, 500);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();

	}

	/**
	 * Toast显示错误信息
	 * @param err
	 */
	public void showError (String err)
	{
		Toast toast = Toast.makeText(sInstance, "Error: " + err, 500);
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
		Log.e(this.getClass().getName(), err);
	}

	/**
	 * 获取显示尺寸值
	 * @param dp
	 * @return
	 */
	public static int dp2px (int dp)
	{
		int density = (int) (dp*sInstance.getResources().getDisplayMetrics().density);

		return density;
	}
}

