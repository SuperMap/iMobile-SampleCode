package com.supermap.navidemo.app;



import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.supermap.navidemo.filemanager.MyAssetManager;
import com.supermap.navidemo.filemanager.MySharedPreferences;
//import com.supermap.navidemo.lic.LicConfig;


public class MyApplication extends Application {
	public static String DATAPATH = "";
	public static String SDCARD   = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

	private static MyApplication sInstance = null;

	private ArrayList<Activity> mActivities = new ArrayList<Activity>();
	@Override
	public void onCreate()
	{
		super.onCreate();

		DATAPATH = this.getFilesDir().getAbsolutePath() + "/";
		sInstance = this;

		// Initialization
		MySharedPreferences.init(this);
		MyAssetManager.init(this);

	}

	/**
	 * 获取当前应用对象
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
	public   void showInfo(String info)
	{
		Toast toast = Toast.makeText(sInstance, info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();

	}

	/**
	 * Toast显示错误信息
	 * @param err
	 */
	public void showError (String err)
	{
		Toast toast = Toast.makeText(sInstance, "Error: " + err, Toast.LENGTH_SHORT);
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

