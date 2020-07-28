package com.supermap.myapplication;

import com.supermap.data.Environment;

import android.app.Application;


public class MyApplication extends Application {

	public static final String ROOT_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath().toString();
	public static final String KEY="fvV2osxwuZWlY0wJb8FEb2i5";
	
	@Override
	public void onCreate(){
		super.onCreate();
		Environment.setLicensePath(ROOT_PATH + "/SuperMap/license/");
		Environment.setWebCacheDirectory(MyApplication.ROOT_PATH+"/SuperMap/WebCache/");
		Environment.setTemporaryPath(MyApplication.ROOT_PATH + "/SuperMap/temp/");
		Environment.setOpenGLMode(false);
		Environment.initialization(this);
		
	}
}
