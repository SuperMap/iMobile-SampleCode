package com.supermap.carsmonitordemo.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import com.supermap.carsmonitordemo.carmonitor.MonitorActivity;
import com.supermap.carsmonitordemo.configuration.DefaultDataConfiguration;
import com.supermap.carsmonitordemo.filemanager.MyAssetManager;
import com.supermap.carsmonitordemo.filemanager.MySharedPreferences;
//import com.supermap.carsmonitordemo.lic.LicConfig;
import com.supermap.data.Environment;


import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 启动界面，在启动的时候把数据加载好，不然太慢了，启动的时候还可以加点好看的东西
 * @author Congle
 *
 */
public class StartupActivity extends Activity {
	private final String WorksapceName = "carsmonitor.sxwu";           // 车辆监控的地图工作空间文件名
	private final String LicName       = "Trial License.slm";          // 许可文件名
	// 车辆轨迹文件存放目录
	private final String TrackPath     = DefaultDataConfiguration.MapDataPath + "/Track/";
	// 地图数据目录
	private final String WorkspacePath = DefaultDataConfiguration.MapDataPath;
	// 许可文件目录
	private final String LicPath       = DefaultDataConfiguration.LicensePath;

	private MyApplication mApp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.startup);                               //  显示启动界面

		// Initialization
		MySharedPreferences.init(this);
		MyAssetManager.init(this);

		// 初始化数据


//		Environment.setLicensePath(LicConfig.getConfigPath());
		Environment.setLicensePath(DefaultDataConfiguration.LicensePath);
		Environment.setWebCacheDirectory(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/SuperMap/WebCahe/");
		Environment.initialization(this);

		mApp = (MyApplication) getApplication();
		mApp.registerActivity(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("正在初始化数据。。。");
		dialog.show();

		new Thread(){
			public void run() {
				new DefaultDataConfiguration().autoConfig();
//				if(!uploadMapData()){
//					Log.e(this.getClass().getName(), "环境初始化失败");
//				}
				copyTrackData(); //拷贝轨迹文件
				if(!mApp.openWorkspace(WorkspacePath+WorksapceName)){
					Log.e(this.getClass().getName(), "数据打开失败");
				}

				//启动后台服务
				Intent intentService = new Intent();
				intentService.setAction("com.supermap.backstageservice.START");
				intentService.setPackage(getPackageName());
				startService(intentService);
				dialog.dismiss();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Intent intent = new Intent(StartupActivity.this, MonitorActivity.class);
						StartupActivity.this.startActivity(intent);

					}
				});
			};
		}.start();

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	/**
	 * 加载数据
	 * @return  成功或失败
	 */
	private boolean uploadMapData(){
		File wksFile = new File(WorkspacePath+WorksapceName);
		File licFile = new File(LicPath+LicName);
		if(!wksFile.exists() || !licFile.exists()){
			try {
				AssetManager mgr = this.getAssets();
				InputStream is = mgr.open(WorksapceName);
				boolean reslut = copyFile(is, wksFile, true);            // 复制工作空间
				is.close();

				is = mgr.open(LicName);
				reslut &= copyFile(is, licFile, true);                   // 复制License
				is.close();

				return reslut;
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 复制车辆的轨迹文件到TrackPath
	 */
	private void copyTrackData(){
		AssetManager mgr = this.getAssets();
		try {
			String[] list = mgr.list("Track");
			int listLength = list.length;
			for (int i = 0; i < listLength; i++) {

				File trackFile = new File(TrackPath+i+".txt");
				InputStream is = mgr.open("Track/"+list[i]);
				copyFile(is, trackFile, true);
				is.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean copyFile(InputStream src,File des,boolean rewrite){
		//目标路径不存在的话就创建一个
		if(!des.getParentFile().exists()){
			des.getParentFile().mkdirs();
		}
		if(des.exists()){
			if(rewrite){
				des.delete();
			}else{
				return false;
			}
		}

		try{
			InputStream fis = src;
			FileOutputStream fos = new FileOutputStream(des);
			//1kb
			byte[] bytes = new byte[1024];
			int readlength = -1;
			while((readlength = fis.read(bytes))>0){
				fos.write(bytes, 0, readlength);
			}
			fos.flush();
			fos.close();
			fis.close();
		}catch(Exception e){
			return false;
		}
		return true;
	}
}
