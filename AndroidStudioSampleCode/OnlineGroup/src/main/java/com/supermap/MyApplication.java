package com.supermap;

import android.app.Application;

import com.supermap.data.Environment;
import com.supermap.onlineservices.OnlineService;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        initEnvironment();//数据准备

    }
    private static final String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath();
    private void initEnvironment() {
        //初始化

        Environment.setLicensePath(SDCARD+"/SuperMap/License/");
        Environment.initialization(this);
        Environment.setOpenGLMode(true);
        OnlineService.init(this);
    }

}
