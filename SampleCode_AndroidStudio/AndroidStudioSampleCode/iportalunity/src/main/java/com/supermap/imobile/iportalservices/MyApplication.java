package com.supermap.imobile.iportalservices;

import android.app.Application;
import com.supermap.imobile.utils.ListDataSave;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ListDataSave.init(getApplicationContext());
    }
}
