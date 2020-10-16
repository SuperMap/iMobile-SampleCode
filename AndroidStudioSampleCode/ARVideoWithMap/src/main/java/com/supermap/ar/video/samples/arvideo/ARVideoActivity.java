package com.supermap.ar.video.samples.arvideo;

import android.Manifest;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.SeekBar;


import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class ARVideoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = ARVideoActivity.class.getSimpleName();

    //------------iMobile map ------------
    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath() + "/";

    private MapControl m_mapcontrol = null;
    private Workspace m_workspace;
    private MapView m_mapView = null;
    private Map m_map = null;
    //-------------------------------------

    @Nullable
    private MediaPlayer mediaPlayer;

    private GLSurfaceView mGLSurfaceView;
    GLVideoRenderer glVideoRenderer;


    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET
    };

    //申请动态权限
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (!checkPermissions(needPermissions)) {
            EasyPermissions.requestPermissions(
                    this,
                    "为了应用的正常使用，请允许以下权限。",
                    0,
                    needPermissions);
        } else {
        }
    }

    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        StringBuffer sb = new StringBuffer();
        for (String str : perms) {
            sb.append(str);
            sb.append("\n");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setRationale("此功能需要：" + sb + "权限，否则无法正常使用，是否打开设置？")
                    .setPositiveButton("是")
                    .setNegativeButton("否")
                    .build()
                    .show();
        }
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();

        Environment.setLicensePath(SDCARD + "SuperMap/license/");
        Environment.initialization(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_video);

        initARVideo();

        initMap();

        initUI();

        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, true);
    }



    private void initARVideo() {

//        glVideoRenderer = new GLVideoRenderer(this, "rain.mp4");
//        glVideoRenderer = new GLVideoRenderer(this, "winter_snow.mp4");
        glVideoRenderer = new GLVideoRenderer(this, "thunder.mp4");
        //Create renderer v_spring  v_summer   v_autumn  v_winter
        mGLSurfaceView = findViewById(R.id.glSurfaceView);
        mGLSurfaceView.setPreserveEGLContextOnPause(true);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGLSurfaceView.setRenderer(glVideoRenderer);//Set up renderer
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mGLSurfaceView.setWillNotDraw(false);

        glVideoRenderer.setupAlpha(0.5f);
        mGLSurfaceView.setAlpha(0.5f);
    }


    private void initUI() {
        ((SeekBar) findViewById(R.id.seekbar_alpha)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float alphaValue = (float) progress / 100;
                glVideoRenderer.setupAlpha(alphaValue);
                glVideoRenderer.setPlaySpeed(0.6f);
                mGLSurfaceView.setAlpha(alphaValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void initMap() {
        m_workspace = new Workspace();
       //打开在线地图
        m_mapView = (MapView) findViewById(R.id.testMapView);
        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setAlias("GoogleMapRoad");
        info.setEngineType(EngineType.GoogleMaps);
//        String url = "http://www.google.cn/maps?scale=2";
        String url = "http://www.google.cn/maps";


        info.setServer(url);
        Datasource datasource = m_workspace.getDatasources().open(info);
        m_mapcontrol = m_mapView.getMapControl();

        m_map = m_mapcontrol.getMap();
        m_map.setWorkspace(m_workspace);
//        m_map.getLayers().add(datasource.getDatasets().get("roadmap"), true);
        m_map.getLayers().add(datasource.getDatasets().get("satellite"), true);
        m_map.setScale(2.113418494351797E-4);
        m_map.setCenter(new Point2D(12969353.320288,4863848.899989));

//        m_map.viewEntire();
        m_map.refresh();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
