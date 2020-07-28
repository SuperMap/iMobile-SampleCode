package com.supermap.android.speech;

import android.app.Application;

import com.supermap.mapping.speech.SpeechManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //语音初始化
//        SpeechManager.init(MyApplication.this); //先初始化
    }

}

