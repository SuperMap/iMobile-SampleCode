package com.supermap.ar.apparmap;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //申请启用AR功能必备的权限
        androidx.core.app.ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CAMERA
        }, PackageManager.PERMISSION_GRANTED);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }


    public void toMapActivity(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }

    public void toDeliveryARActivity(View view) {
        startActivity(new Intent(this, MapActivityDeliveryAR.class));
    }
}