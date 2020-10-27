package com.supermap.navidemo.app;


import com.supermap.navidemo.configuration.DefaultDataConfiguration;
//import com.supermap.navidemo.lic.LicConfig;
import com.supermap.navidemo.navi.MainActivity;
import com.supermap.data.Environment;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

/**
 * 启动界面，在启动的时候把数据加载好，不然太慢了，启动的时候还可以加点好看的东西
 * @author Congle
 *
 */
public class StartupActivity extends Activity {

	private final String LicPath = DefaultDataConfiguration.LicensePath;

	private MyApplication mApp = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		LicConfig.configLic(this);
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

		// Initialize NaviData and License when they are contained in the package
		initData();

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	/**
	 * 初始化数据
	 */
	public void initData()
	{
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("正在初始化数据。。。");
		dialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// Initialize NaviData and License when they are contained in
				// the package

				new DefaultDataConfiguration().autoConfig();

				dialog.dismiss();

				// Start MainActivity after initialize data
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Intent intent = new Intent(StartupActivity.this,
								MainActivity.class);
						StartupActivity.this.startActivity(intent);

					}
				});
			};

		}).start();

	}
}
