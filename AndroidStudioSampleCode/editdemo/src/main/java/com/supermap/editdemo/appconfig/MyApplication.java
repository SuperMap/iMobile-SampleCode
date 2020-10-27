package com.supermap.editdemo.appconfig;

import android.app.Application;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.supermap.editdemo.file.MyAssetManager;
import com.supermap.editdemo.file.MySharedPreferences;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
//import com.supermap.editdemo.lic.LicConfig;

public class MyApplication extends Application {
	public static String DATAPATH = "";
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	private static MyApplication sInstance = null;
	private Workspace mWorkspace = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		LicConfig.configLic(this);

		DATAPATH = this.getFilesDir().getAbsolutePath()+"/";
		sInstance = this;

		//第一步就是设置环境参数，初始化好iMobile
//		Environment.setLicensePath(LicConfig.getConfigPath());
		Environment.setLicensePath(DefaultDataConfig.LicensePath);
		Environment.initialization(this);

		//初始化系统相关的类
		MySharedPreferences.init(this);
		MyAssetManager.init(this);

		//配置数据
//		new DefaultDataConfig().autoConfig();
		mWorkspace = new Workspace();
//		openWorkspace();
//		mWorkspace = new Workspace();

	}

	public void openWorkspace() {


		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(DefaultDataConfig.WorkspacePath);
		info.setType(WorkspaceType.SMWU);
		if(!mWorkspace.open(info)){
			ShowError("工作空间损坏!");
		}
	}

	/**
	 * 获取打开的工作空间
	 * @return
	 */
	public Workspace getOpenedWorkspace(){
		return mWorkspace;
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
		Toast toast = Toast.makeText(sInstance, info, 500);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	/**
	 * Toast显示错误信息
	 * @param err
	 */
	public void ShowError(String err){
		Toast toast = Toast.makeText(sInstance, "Error: "+err, 500);
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
}
