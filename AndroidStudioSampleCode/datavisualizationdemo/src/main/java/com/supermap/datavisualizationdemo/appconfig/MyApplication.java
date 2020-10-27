package com.supermap.datavisualizationdemo.appconfig;


import com.supermap.data.Environment;
//import com.supermap.datavisualizationdemo.lic.LicConfig;
import com.supermap.datavisualizationdemo.file.MyAssetManager;
import com.supermap.datavisualizationdemo.file.MySharedPreferences;

import android.app.Application;

public class MyApplication extends Application {
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	private static MyApplication sInstance = null;


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sInstance = this;
//		LicConfig.configLic(this);

		//第一步就是设置环境参数，初始化好iMobile
//		Environment.setLicensePath(LicConfig.getConfigPath());
		Environment.setLicensePath(DefaultDataConfig.LicPath);
		Environment.initialization(this);

		//初始化系统相关的类
		MySharedPreferences.init(this);
		MyAssetManager.init(this);

	}

	/**
	 * 获取当前application
	 * @return
	 */
	public static MyApplication getInstance(){
		return sInstance;
	}


	public static int dp2px(int dp){
		return (int) (dp*sInstance.getResources().getDisplayMetrics().density);
	}

}
