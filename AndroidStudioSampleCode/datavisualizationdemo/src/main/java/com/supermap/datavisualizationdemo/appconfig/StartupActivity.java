package com.supermap.datavisualizationdemo.appconfig;


import com.supermap.datavisualizationdemo.DataVisualization.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

/**
 * 启动界面，初始化数据
 *
 */
public class StartupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(android.R.layout.list_content);
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
				new DefaultDataConfig().autoConfig();

				dialog.dismiss();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Intent intent = new Intent(StartupActivity.this, MainActivity.class);
						StartupActivity.this.startActivity(intent);
						finish();
					}
				});
			};
		}.start();

	}

	@Override
	protected void onStop() {

		super.onStop();
	}

}
