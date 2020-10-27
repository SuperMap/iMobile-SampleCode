package com.supermap.plotdemo.plotdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.supermap.data.Environment;
//import com.supermap.plotdemo.lic.LicConfig;
import com.supermap.plotdemo.util.MyAssetManager;
import com.supermap.plotdemo.util.MySharedPreferences;


public class MyApplication extends Application {
	public static String DATAPATH = "";
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	private static MyApplication sInstance = null;
	private ArrayList<Activity> mActivities = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		LicConfig.configLic(this);
		
		mActivities = new ArrayList<Activity>();
		DATAPATH = this.getFilesDir().getAbsolutePath()+"/";
		sInstance = this;
		
		//设置环境参数，初始化好iMobile
//		Environment.setLicensePath(LicConfig.getConfigPath());
		Environment.setLicensePath(DefaultDataConfig.LicensePath);
		Environment.initialization(this);

		//初始化系统相关的类
		MySharedPreferences.init(this);
		MyAssetManager.init(this);
		
	}
	
	/**
	 * 获取当前应用Application
	 * @return
	 */
	public static MyApplication getInstance(){
		return sInstance;
	}
	
	/**
	 * Toast显示信息
	 */
	public void ShowInfo(String info){
		Toast toast = Toast.makeText(sInstance, info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	/**
	 * Toast显示错误信息
	 * @param err
	 */
	public void ShowError(String err){
		Toast toast = Toast.makeText(sInstance, "Error: "+err, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
		Log.e(this.getClass().getName(), err);
	}
	
	/**
	 * 获取显示尺寸值
	 * @param dp
	 * @return
	 */
	public static int dp2px(int dp){
		return (int) (dp*sInstance.getResources().getDisplayMetrics().density);
	}
	
	/**
	 * 注册Activity
	 * @param act
	 */
	public void registerActivity(Activity act){
	
		mActivities.add(act);
	}
	
	/**
	 * 退出应用
	 */
	public void exit(){
	
		for(Activity act:mActivities){
			act.finish();
		}
		Process.killProcess(Process.myPid());
	}
}
