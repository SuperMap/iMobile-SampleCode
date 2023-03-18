package com.supermap.ar.apparmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.are.sceneform.ARPlatForm;
import com.google.are.sceneform.ArHelpers;
import com.google.are.sceneform.math.Quaternion;
import com.google.are.sceneform.math.Vector3;
import com.supermap.ar.Point3D;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.AREffectView;
import com.supermap.ar.areffect.ARMapElement;
import com.supermap.ar.areffect.EffectView;
import com.supermap.ar.areffect.Location;
import com.supermap.ar.areffect.loader.ArSceneLoader;
import com.supermap.data.Environment;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.hiar.ARCamera;
import com.supermap.hiar.AREngine;
import com.supermap.hiar.ARFrame;
import com.supermap.hiar.ARPlane;
import com.supermap.hiar.ARPose;
import com.supermap.hiar.TrackingState;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

/**
 * <p>
 * Title:AR地图-物流配送
 * 数据：长春数据集
 * </p>
 * <pre>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   	展示如何在AR场景中加载地图
 *
 * 2、Demo数据：
 *      数据路径：/SampleData/chuangchun2/Changchun2.smwu
 *      许可目录："../SuperMap/license/"
 *
 * 3、使用步骤：
 *      启动程序->识别平面
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </pre>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

public class MapActivity extends AppCompatActivity {
    public static final String TAG = "SuperMap";
    private static final String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    private MapView mapView;
    private Map map;
    private MapControl mapControl;

    private ARMapElement mapElement;
    private ArSceneLoader sceneLoader;
    private EffectView.OnUpdateListener onUpdateListener;
    private TextView mSearchingTextView;

    // AR相关
    private AREffectView arEffectView;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // android生命周期相关
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.app.ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CAMERA
        }, PackageManager.PERMISSION_GRANTED);

        //强制使用ARCore
//        AREngine.enforceARCore();

        //设置许可路径
        Environment.setOpenGLMode(false);
        Environment.setLicensePath(SDCARD + "/SuperMap/License");
        Environment.initialization(this);
        setContentView(R.layout.activity_map);
        //控件绑定
        mapView = findViewById(R.id.map_view);
        mapControl = mapView.getMapControl();
        map = mapControl.getMap();

        initAR();

        ARCamera.setInitCallback(new ARCamera.InitCallback() {
            @Override
            public void complete(float offsetAngle) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //注意：针对ARCore与AREngine，由于初始坐标系不同，需要作适配
                        Toast.makeText(MapActivity.this, "当前平台："+ ARPlatForm.getEngineType(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arEffectView != null){
            arEffectView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (arEffectView != null){
            arEffectView.onPause();
        }
    }

    /**
     * 加载地图
     */
    private void loadMap() {
        //打开工作空间
        initMap();
        loadArMap();

    }

    public static final int LOAD_MAP = 0x11;
    /**
     * 把地图异步加载的AR场景中
     */
    public void loadArMap(){
        handler.sendEmptyMessage(LOAD_MAP);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_MAP:
                    mapElement.loadModel(mapView);
                    break;
            }
            return false;
        }
    });

    private void initAR(){
        arEffectView = findViewById(R.id.ar_view);
        arEffectView.setDeviceLocation(new Location(103,30,0));
        onUpdateListener = new EffectView.OnUpdateListener() {
            @Override
            public void onUpdate() {
                ARFrame mFrame = arEffectView.getArSceneView().getArFrame();
                if (mFrame == null) {
                    return;
                }

                // 识别平面后，隐藏
                for (ARPlane plane : mFrame.getUpdatedPlanes()) {
                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                        if (mSearchingTextView != null) {
                            mSearchingTextView.setVisibility(View.GONE);
                            mSearchingTextView = null;
                        }

                        ARPose centerPose = plane.getCenterPose();
                        sceneLoader = new ArSceneLoader(arEffectView);
                        AREffectElement sceneElement = sceneLoader.getSceneElement();
                        //检测AR平台类型
                        checkARPlatForm(sceneElement);

                        //提取Pose的位置
                        Vector3 vector3 = ArHelpers.extractPositionFromPose(centerPose);
                        sceneElement.setPosition(vector3);

                        mapElement = new ARMapElement(MapActivity.this);
                        mapElement.setParentNode(sceneLoader.getSceneElement());

                        mapElement.setActionType(ARMapElement.ActionType.GESTURE_MAP);  // 默认使用地图手势
                        mapElement.setTransformable(true);   // 可旋转
                        mapElement.setArMapShapeType(ARMapElement.ARMapShapeType.MODE_ROUND);

                        mapElement.setOnUpdateListener(new ARMapElement.OnMapUpdateListener() {
                            @Override
                            public void onArObjClear(MotionEvent event) {
                            }


                            @Override
                            public void onArObjUpdate(Point3D[] arViewVertice, Point leftTop, Point rightBottom, Point centerPoint) {
                            }
                        });

//                        mapElement.setOnMapClickListener(new ARMapElement.OnMapClickListener() {
//                            @Override
//                            public void onMapClick(Point point, Point pp) {
//                                // 沙盘点击事件
//                                // 核心代码，把手机点击屏幕的坐标转换为地图地理坐标
//                                Point2D pt = mapControl.getMap().pixelToMap(point);
//                                Point2D pt_ = pt;
//                            }
//                        });

                        // 异步任务加载地图
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loadMap();
                            }
                        }).start();

                        arEffectView.removeOnUpdateListener(onUpdateListener);
                        break;
                    }
                }
            }
        };
        arEffectView.addOnUpdateListener(onUpdateListener);
    }

    private void checkARPlatForm(AREffectElement element) {
        if (AREngine.isUsingAREngine()){
            element.setRotationAngle(new Quaternion(new Vector3(0,1,0),180));
        }
    }

    private void initMap(){
        //打开工作空间
        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();
        connectionInfo.setServer(SDCARD + "/SampleData/chuangchun2/Changchun2.smwu");
        connectionInfo.setType(WorkspaceType.SMWU);
        Workspace workspace = new Workspace();
        boolean open = workspace.open(connectionInfo);
        if (!open){
            Log.e(TAG,"工作空间打开失败");
            return;
        }
        //地图绑定工作空间
        map.setWorkspace(workspace);

        //打开地图
//        boolean mapOpenStatus = map.open("长春市区图_copy"); // 暗黑版
//        boolean mapOpenStatus = map.open("长春市区图");   // 正常版
        boolean mapOpenStatus = map.open("长春市区图_Local1");   // 清新版
        if (!mapOpenStatus){
            Log.e(TAG, "Not find -> MAP_NAME: " + PropertiesSuper.MAP_NAME);
            //加载默认地图数据
            return;
        }

        map.viewEntire();
        map.setScale(1/7972.77);
        map.setCenter(new Point2D(4727.08, -4702.169));
        map.refresh();
    }

}
