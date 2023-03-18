package com.supemap.timespan;


import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.supermap.data.Environment;
import com.supermap.data.TimeSpan;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity {
    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.WRITE_CALENDAR,
    };
    String rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private MapView mapView;
    MapControl mapControl;
    Workspace workspace;
    int count;
    private int seekWidth = 90;
    private int seekHeight = 90;
    ImageButton button;
    private SeekBar seekbar = null;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.setLicensePath(rootPath + "/SuperMap/license");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        Environment.setOpenGLMode(true);
        Environment.setWebCacheDirectory(rootPath + "/SuperMap/data/webCache");

        mapView = (MapView) findViewById(R.id.editmapview);

        workspace = new Workspace();
        mapControl = mapView.getMapControl();
        mapControl.getMap().setWorkspace(workspace);
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(rootPath + "/SampleData/台风Data/Typhoon.smwu");
        info.setType(WorkspaceType.SMWU);
        workspace.open(info);
        mapControl.getMap().open(workspace.getMaps().get(0));


        mapView.getMapControl().getMap().setTimeEnable(true);
        TimeSpan timeSpan = new TimeSpan(16, 0, 0);
        mapView.getMapControl().getMap().setTimeWindow(timeSpan);
        TimeSpan timeSpan1 = new TimeSpan(6, 0, 0);
        mapView.getMapControl().getMap().setTimeStep(timeSpan1);
        Layer layer = mapView.getMapControl().getMap().getLayers().get(10);
        layer.setTimeFilterEnable(true);
        layer.setTimeStartField("RTime");
        layer.setTimeEndField("RTime");
        layer.setTimeStepInterval(mapView.getMapControl().getMap().getTimeStep());


        //计算动画总帧数
        long start = mapView.getMapControl().getMap().getMapStartTime().getTime();
        long end = mapView.getMapControl().getMap().getMapEndTime().getTime();
        int hour = mapView.getMapControl().getMap().getTimeStep().getHours();
        int a = (int) (end - start);
        count = a / (1000 * 60 * 60 * hour);

        button = findViewById(R.id.bf);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                button.setBackground(getResources().getDrawable(R.drawable.zt));
                draw();
            }
        });

        ImageButton button2 = findViewById(R.id.ht);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageButton button3 = findViewById(R.id.kj);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        seekbar = findViewById(R.id.seekbar);
        seekbar.setMax(count);
        seekbar.setProgress(0);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("++++++++++++++++", "---------" + mapView.getMapControl().getMap().getMapEndTime() + "------*********" + a + "**********" + b);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


//        Drawable drawable = getNewDrawable(this, R.drawable.point, seekWidth, seekHeight);
        seekbar.setThumb(null);


        textView = findViewById(R.id.time);
    }

    /**
     * 申请动态权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (!checkPermissions(needPermissions)) {
            EasyPermissions.requestPermissions(
                    this,
                    "为了应用的正常使用，请允许以下权限。",
                    0,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }

    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //调用函数缩小图片
    public BitmapDrawable getNewDrawable(Activity context, int restId, int dstWidth, int dstHeight) {
        Bitmap Bmp = BitmapFactory.decodeResource(
                context.getResources(), restId);
        Bitmap bmp = Bmp.createScaledBitmap(Bmp, dstWidth, dstHeight, true);
        BitmapDrawable d = new BitmapDrawable(bmp);
        Bitmap bitmap = d.getBitmap();
        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
            d.setTargetDensity(context.getResources().getDisplayMetrics());
        }
        return d;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            draw();
        }
    };

    private int index = 0;

    private void draw() {
        if (index <= count) {
            mapView.getMapControl().getMap().setCurrentPlayerTick(index);
            seekbar.setProgress(index);
            textView.setText("时间 : " + mapView.getMapControl().getMap().getCurrentTickStartTime() + " 至 " + mapView.getMapControl().getMap().getCurrentTickEndTime());
            mapView.getMapControl().getMap().refresh();
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.bf));
            seekbar.setProgress(0);
            mapView.getMapControl().getMap().setCurrentPlayerTick(0);
            mapView.getMapControl().getMap().refresh();
            index = 0;
            return;
        }
        index = index + 4;
        handler.sendEmptyMessageDelayed(0, 1000);
    }
}
