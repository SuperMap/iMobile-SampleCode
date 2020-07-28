package com.supermap.areffect_3dterrain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.supermap.ar.Point3D;
import com.supermap.ar.areffect.ARAnimationGroup;
import com.supermap.ar.areffect.ARAnimationManager;
import com.supermap.ar.areffect.ARAnimationParameter;
import com.supermap.ar.areffect.ARAnimationRepeatMode;
import com.supermap.ar.areffect.ARAnimationRotation;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.AREffectView;
import com.supermap.ar.areffect.ARViewElement;
import com.supermap.ar.areffect.Vector;


import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:AR沙盘的示范代码
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范如何在AR场景中加载模型及视图构建AR沙盘
 * 2、关键类型/成员:
 *      AREffectView
 * 		AREffectElement
 * 	    ARViewEffectElement
 * 3、使用步骤：
 *   (1)点击不同按钮，加载不同的模型或视图
 *   (2)点击方向按钮，模型整体旋转一定角度
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity_demo extends AppCompatActivity {
    float rotationAngle = 0f;//旋转角度
    Vector axisZ = new Vector(0,0,1);//Z轴
    float heiScalse = 0.6f;//模型高度比
    ARAnimationGroup animationGroup;//动画组
    private AREffectView arFragment;//特效控件
    AREffectElement parentElement;//父节点

    AREffectElement terrainElement;//地形
    AREffectElement terrainModelElement;
    AREffectElement routeElement;//路线

    private AREffectElement ticDoorElement;//大门及售票处01
    private AREffectElement ticDoorElement02;//大门及售票处02

    ARViewElement markerElement_01;//标记-文本
    ARViewElement markerElement_00;//标记-文本

    private AREffectElement vistorCenterElement;//游客中心
    private ARViewElement vistorCenterElement_Marker;//游客中心标记01
    private ARViewElement vistorCenterElement_Marker02;//游客中心标记02

    private AREffectElement vistorCenterElement_03;
    private ARViewElement markerElement_02;//标记-文本
    private ARViewElement markerElement_03;
    private ARViewElement markerElement_04;
    private ARViewElement markerElement_05;

    private AREffectElement arbor01;
    private AREffectElement arbor02;
    private AREffectElement wharfElement;

    private ARViewElement arbor02_marker;

    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
    };
    private AREffectElement routeElement02;
    private AREffectElement wharfElement02;
    private AREffectElement shipElement;
    private ARViewElement wharfElement02_marker;
    private AREffectElement route_marker_01;
    private AREffectElement route_marker_02;
    private AREffectElement route_marker_03;
    private ARViewElement route_marker_01_text;
    private ARViewElement route_marker_02_text;
    private ARViewElement route_marker_03_text;
    private AREffectElement route_marker_line;
    private ARViewElement route_marker_04_text;

    @Override
    protected void onPause() {
        super.onPause();
        arFragment.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        arFragment.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

        setContentView(R.layout.activity_main_test);
        arFragment = findViewById(R.id.ar_effect);

        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        if (width > height){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        initMainModel();//加载模型

        //<editor-fold>设置按钮组
        findViewById(R.id.leftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模型逆时针旋转
                rotationAngle += 15;
                terrainElement.setRotationAngle(axisZ, rotationAngle);
            }
        });
        findViewById(R.id.rightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //模型顺时针旋转
                rotationAngle -= 15;
                terrainElement.setRotationAngle(axisZ, rotationAngle);
            }
        });

        findViewById(R.id.btn01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）景区模型，
                terrainElement.setVisiblity(true);//地形
                ticDoorElement.setVisiblity(true);//南门
                ticDoorElement02.setVisiblity(true);//北门
                markerElement_00.setVisiblity(true);//南门标记
                markerElement_03.setVisiblity(true);//北门标记
                vistorCenterElement.setVisiblity(true);
                vistorCenterElement_03.setVisiblity(true);
            }
        });

        findViewById(R.id.btn02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）内部景点
                arbor02.setVisiblity(true);//亭子01
                arbor01.setVisiblity(true);//亭子02

                wharfElement.setVisiblity(true);//码头

                wharfElement02.setVisiblity(true);
                shipElement.setVisiblity(true);
            }
        });

        findViewById(R.id.btn03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）图标
                vistorCenterElement_Marker.setVisiblity(true);
                vistorCenterElement_Marker02.setVisiblity(true);
                markerElement_05.setVisiblity(true);//水体标记
                markerElement_02.setVisiblity(true);//游客中心标记
                markerElement_01.setVisiblity(true);//亭子01标记
                markerElement_04.setVisiblity(true);//西坡标记
                arbor02_marker.setVisiblity(true);
                wharfElement02_marker.setVisiblity(true);

                route_marker_01_text.setVisiblity(true);
                route_marker_02_text.setVisiblity(true);
                route_marker_03_text.setVisiblity(true);
                route_marker_04_text.setVisiblity(true);
                route_marker_line.setVisiblity(true);

            }
        });

        findViewById(R.id.btn04).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）路线
                routeElement.setVisiblity(true);
                routeElement02.setVisiblity(true);
                route_marker_01.setVisiblity(true);
                route_marker_02.setVisiblity(true);
                route_marker_03.setVisiblity(true);
            }
        });
        //</editor-fold>

        /**新线程->播放动画*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message=handler.obtainMessage();
                message.what=LOAD_MODEL;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 检测权限
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */
    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    /**
     * 申请权限
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
                    Manifest.permission.CAMERA);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }


    //<editor-fold>//放置地形和控件
    private void initMainModel() {
        //绝对位置,设置父节点
        parentElement = new AREffectElement(getApplicationContext());
        parentElement.setParentNode(arFragment);
        parentElement.setPosition(new Point3D(0,0,0));

        //地形节点
        terrainElement = new AREffectElement(getApplicationContext());
        terrainElement.setParentNode(parentElement);
        terrainElement.setRelativePosition(new Point3D(0,2.5f,-1.5f));
        terrainElement.setScaleFactor(new float[]{1, 1, heiScalse});
        terrainElement.setVisiblity(false);

        //地形模型
        terrainModelElement = new AREffectElement(getApplicationContext());
        terrainModelElement.setParentNode(terrainElement);
        terrainModelElement.loadModel(R.raw.terrain2gltf03);
//        terrainModelElement.setScaleFactor(new float[]{0.955f,0.955f,0.955f*heiScalse});//模型比例
        terrainModelElement.setRotationAngle(axisZ,180);

        routeElement02 = new AREffectElement(getApplicationContext());
        routeElement02.setParentNode(terrainElement);
        routeElement02.setRelativePosition(new Point3D(-0.02f,2.0f,0.4f));
        routeElement02.setScaleFactor(new float[]{0.365f, 0.365f, heiScalse*0.365f});
        routeElement02.loadModel(R.raw.road05);
        routeElement02.setVisiblity(false);

        //加载亭子
        arbor02 = new AREffectElement(getApplicationContext());
        arbor02.setParentNode(terrainElement);
        arbor02.setRelativePosition(new Point3D(-1.1f,1.1f,0.9f));
        arbor02.setScaleFactor(new float[]{0.165f, 0.165f, 0.165f});
        arbor02.setRotationAngle(axisZ,-90);
        arbor02.loadModel(R.raw.lt02arbor);
        arbor02.setVisiblity(false);

        //码头
        wharfElement = new AREffectElement(getApplicationContext());
        wharfElement.setParentNode(terrainElement);
        wharfElement.setRelativePosition(new Point3D(-0.1f,-0.1f,0.3f));
        wharfElement.setScaleFactor(new float[]{0.065f, 0.065f, 0.065f});
        wharfElement.setRotationAngle(axisZ,-135);
        wharfElement.loadModel(R.raw.wharf);
        wharfElement.setVisiblity(false);

        //亭子标记-湖心亭
        arbor02_marker = new ARViewElement(getApplicationContext());
        arbor02_marker.setParentNode(terrainElement);
        arbor02_marker.loadModel(R.layout.marker06);
        arbor02_marker.setRelativePosition(new Point3D(-0.3f,-0.3f,0.9f));
        arbor02_marker.setScaleFactor(new float[]{0.8f,0.8f,0.8f});
        arbor02_marker.setVisiblity(false);

        //右侧码头02
        wharfElement02 = new AREffectElement(getApplicationContext());
        wharfElement02.setParentNode(terrainElement);
        wharfElement02.setRelativePosition(new Point3D(1.45F,-0.45F,0.3f));
        wharfElement02.setScaleFactor(new float[]{0.08F, 0.08F, 0.08F});
        wharfElement02.setRotationAngle(axisZ,-30);
        wharfElement02.loadModel(R.raw.wharf);
        wharfElement02.setVisiblity(false);

        shipElement = new AREffectElement(getApplicationContext());
        shipElement.setParentNode(wharfElement02);
        shipElement.setRelativePosition(new Point3D(0,0,0));
        shipElement.setRelativePosition(new Point3D(1.35F,-1.6F,0.4f));
        shipElement.setScaleFactor(new float[]{0.065F, 0.065F, 0.065F});
        shipElement.loadModel(R.raw.ship);
        shipElement.setVisiblity(false);

        //晓渔渡
        wharfElement02_marker = new ARViewElement(getApplicationContext());
        wharfElement02_marker.setParentNode(wharfElement02);
        wharfElement02_marker.setRelativePosition(new Point3D(1.3f,-0.75f,2.15f));
        wharfElement02_marker.setScaleFactor(new float[]{0.8f,0.8f,0.8f});
        wharfElement02_marker.loadModel(R.layout.marker07);
        wharfElement02_marker.setVisiblity(false);

        //看云亭
        arbor01 = new AREffectElement(getApplicationContext());
        arbor01.setParentNode(wharfElement);
        arbor01.setRelativePosition(new Point3D(2.4f,-1.8f,0.9f));
        arbor01.setScaleFactor(new float[]{0.2f, 0.2f, 0.2f});
        arbor01.setRotationAngle(axisZ,135);
        arbor01.loadModel(R.raw.lt01arbor);
        arbor01.setVisiblity(false);

        //<editor-fold>路线与指示箭头
        routeElement = new AREffectElement(getApplicationContext());
        routeElement.setParentNode(terrainElement);//路线叠加在地形上，使用地形作相对位置的父节点
        routeElement.setRelativePosition(new Point3D(-1.07f,0,0.18f));
        routeElement.loadModel(R.raw.road03);
        routeElement.setScaleFactor(new float[]{0.5f, 0.5f, 0.5f*heiScalse});
        routeElement.setVisiblity(false);

        route_marker_01 = new AREffectElement(getApplicationContext());
        route_marker_01.setParentNode(routeElement);//500m
        route_marker_01.setRelativePosition(new Point3D(1.3f,-1.55f,0.7f));
        route_marker_01.setScaleFactor(new float[]{0.01f, 0.01f, 0.01f});
        route_marker_01.loadModel(R.raw.blue);
        route_marker_01.setVisiblity(false);
        route_marker_01_text = new ARViewElement(getApplicationContext());
        route_marker_01_text.setParentNode(routeElement);
        route_marker_01_text.setScaleFactor(new float[]{0.55f,0.55f,0.55f});
        route_marker_01_text.setRelativePosition(new Point3D(1.4f,-1.50f,0.9f));
        route_marker_01_text.loadModel(R.layout.routemarker01);
        route_marker_01_text.setRotationAngle(axisZ,5);
        route_marker_01_text.setVisiblity(false);

        route_marker_02 = new AREffectElement(getApplicationContext());
        route_marker_02.setParentNode(routeElement);//1km
        route_marker_02.setRelativePosition(new Point3D(0.55f,-0.9f,0.65f));
        route_marker_02.setScaleFactor(new float[]{0.01f, 0.01f, 0.01f});
        route_marker_02.loadModel(R.raw.blue);
        route_marker_02.setVisiblity(false);

        route_marker_02_text = new ARViewElement(getApplicationContext());
        route_marker_02_text.setParentNode(routeElement);
        route_marker_02_text.setScaleFactor(new float[]{0.55f,0.55f,0.55f});
        route_marker_02_text.setRelativePosition(new Point3D(0.55f,-0.9f,0.85f));
        route_marker_02_text.loadModel(R.layout.routemarker02);
        route_marker_02_text.setRotationAngle(axisZ,10);
        route_marker_02_text.setVisiblity(false);

        route_marker_03 = new AREffectElement(getApplicationContext());
        route_marker_03.setParentNode(routeElement);//1km
        route_marker_03.setRelativePosition(new Point3D(-0.735f,1.0f,0.95f));
        route_marker_03.setScaleFactor(new float[]{0.01f, 0.01f, 0.01f});
        route_marker_03.loadModel(R.raw.blue);
        route_marker_03.setVisiblity(false);

        route_marker_03_text = new ARViewElement(getApplicationContext());
        route_marker_03_text.setParentNode(routeElement);
        route_marker_03_text.setScaleFactor(new float[]{0.55f,0.55f,0.55f});
        route_marker_03_text.setRelativePosition(new Point3D(-0.735f,1.0f,1.15f));
        route_marker_03_text.loadModel(R.layout.routemarker03);
        route_marker_03_text.setRotationAngle(axisZ,15);
        route_marker_03_text.setVisiblity(false);

        route_marker_line = new AREffectElement(getApplicationContext());
        route_marker_line.setParentNode(routeElement);//游园路线指示
        route_marker_line.setRelativePosition(new Point3D(0.8f,-1.35f,-1.2f));
        route_marker_line.setScaleFactor(new float[]{0.365f, 0.365f, 0.365f*heiScalse});
        route_marker_line.loadModel(R.raw.line);
        route_marker_line.setRotationAngle(axisZ,185);
        route_marker_line.setVisiblity(false);

        route_marker_04_text = new ARViewElement(getApplicationContext());
        route_marker_04_text = new ARViewElement(getApplicationContext());
        route_marker_04_text.setParentNode(route_marker_line);
        route_marker_04_text.setScaleFactor(new float[]{0.8f,0.8f,0.8f});
        route_marker_04_text.setRelativePosition(new Point3D(-0.8f,-0.55f,2.95f));
        route_marker_04_text.loadModel(R.layout.routemarker04);
        route_marker_04_text.setRotationAngle(axisZ,12);
        route_marker_04_text.setVisiblity(false);

        //</editor-fold>

        //<editor-fold>双方
        //
        ticDoorElement = new AREffectElement(getApplicationContext());
        ticDoorElement.loadModel(R.raw.ticdoor);
        ticDoorElement.setParentNode(terrainElement);
        ticDoorElement.setRelativePosition(new Point3D(-0.15f,-1.2f,0.45f));
        ticDoorElement.setScaleFactor(new float[]{0.08f, 0.08f, 0.08f});
        ticDoorElement.setRotationAngle(new Vector(0,0,1),195);
        ticDoorElement.setVisiblity(false);

        //游客中心
        vistorCenterElement = new AREffectElement(getApplicationContext());
        vistorCenterElement.loadModel(R.raw.bambohouse);//房屋
        vistorCenterElement.setParentNode(terrainElement);
        vistorCenterElement.setRelativePosition(new Point3D(1.2f,1.2f,0.55f));
        vistorCenterElement.setRotationAngle(new Vector(0,0,1),30);
        vistorCenterElement.setScaleFactor(new float[]{2.0f, 2.0f, 2.0f});
        vistorCenterElement.setRotationAngle(new Vector(0,0,1),150);
        vistorCenterElement.setVisiblity(false);

        vistorCenterElement_Marker = new ARViewElement(getApplicationContext());
        vistorCenterElement_Marker.setParentNode(vistorCenterElement);//房屋标记，相对于房屋的位置
        vistorCenterElement_Marker.setRelativePosition(new Point3D(0,0,0.1f));
        vistorCenterElement_Marker.setScaleFactor(new float[]{0.6f, 0.6f, 0.6f});
        vistorCenterElement_Marker.loadModel(R.layout.imgview);
        vistorCenterElement_Marker.setVisiblity(false);

        vistorCenterElement_03 = new AREffectElement(getApplicationContext());//房屋02
        vistorCenterElement_03.loadModel(R.raw.bambohouse);
        vistorCenterElement_03.setParentNode(vistorCenterElement);//相对于房屋
        vistorCenterElement_03.setRelativePosition(new Point3D(0.1f,0,0));
        vistorCenterElement_03.setScaleFactor(new float[]{3, 3, 2.4f});
        vistorCenterElement_03.setRotationAngle(new Vector(0,0,1),60);
        vistorCenterElement_03.setVisiblity(false);

        vistorCenterElement_Marker02 = new ARViewElement(getApplicationContext());
        vistorCenterElement_Marker02.loadModel(R.layout.imgview02);
        vistorCenterElement_Marker02.setParentNode(vistorCenterElement_Marker);
        vistorCenterElement_Marker02.setRelativePosition(new Point3D(0,0,0.1f));
        vistorCenterElement_Marker02.setRotationAngle(axisZ,90);
        vistorCenterElement_Marker02.setScaleFactor(new float[]{1.2f, 1.2f, 1.2f});
        vistorCenterElement_Marker02.setVisiblity(false);
        //</editor-fold>

        //<editor-fold>地名标记组
        //我方营地标记//南门标记
        markerElement_00 = new ARViewElement(getApplicationContext());
        markerElement_00.loadModel(R.layout.marker00);
        markerElement_00.setParentNode(ticDoorElement);
        markerElement_00.setRelativePosition(new Point3D(0.4f,0,3f));
        markerElement_00.setScaleFactor(new float[]{0.6f, 0.6f, 0.6f});
//        markerElement_00.setRotationAngle(axisZ,0);
        markerElement_00.setVisiblity(false);

        //1号阵地标记//看云亭
        markerElement_01 = new ARViewElement(getApplicationContext());
        markerElement_01.loadModel(R.layout.marker01);
        markerElement_01.setParentNode(terrainElement);
        markerElement_01.setRelativePosition(new Point3D(-1.0f,1.1f,1.15f));
        markerElement_01.setScaleFactor(new float[]{1.0f, 1.0f, 1.0f});
        markerElement_01.setRotationAngle(axisZ,-10);
        markerElement_01.setVisiblity(false);


        //敌方营地标记（更改为：游客中心）
        markerElement_02 = new ARViewElement(getApplicationContext());
        markerElement_02.loadModel(R.layout.marker02);
        markerElement_02.setParentNode(vistorCenterElement_03);
        markerElement_02.setRelativePosition(new Point3D(0.05f,0.02f,0.062f));
        markerElement_02.setScaleFactor(new float[]{1.0f, 1.0f, 1.0f});
        markerElement_02.setRotationAngle(axisZ,-30);
        markerElement_02.setVisiblity(false);

        markerElement_03 = new ARViewElement(getApplicationContext());
        markerElement_03.loadModel(R.layout.marker03);//2nd阵地//北门
        markerElement_03.setParentNode(terrainElement);
        markerElement_03.setScaleFactor(new float[]{1.365f, 1.365f, 1.365f});
        markerElement_03.setRelativePosition(new Point3D(0,2.4f,0.8f));
        markerElement_03.setVisiblity(false);

        ticDoorElement02 = new AREffectElement(getApplicationContext());
        ticDoorElement02.loadModel(R.raw.ticdoor);//大门以及售票厅
        ticDoorElement02.setParentNode(markerElement_03);
        ticDoorElement02.setScaleFactor(new float[]{0.08f, 0.08f, 0.08f});
        ticDoorElement02.setRelativePosition(new Point3D(-0.08f,-0.18f,-0.15f));
        ticDoorElement02.setVisiblity(false);

        markerElement_04 = new ARViewElement(getApplicationContext());
        markerElement_04.loadModel(R.layout.marker04);//西宁坡
        markerElement_04.setParentNode(terrainElement);
        markerElement_04.setScaleFactor(new float[]{1.365f, 1.365f, 1.365f});
        markerElement_04.setRelativePosition(new Point3D(-2.5f,2.5f,0.6f));
        markerElement_04.setRotationAngle(axisZ,30);
        markerElement_04.setVisiblity(false);

        markerElement_05 = new ARViewElement(getApplicationContext());
        markerElement_05.loadModel(R.layout.marker05);//水体标注
        markerElement_05.setParentNode(terrainElement);
        markerElement_05.setScaleFactor(new float[]{0.8f, 0.8f, 0.8f});
        markerElement_05.setRelativePosition(new Point3D(-1.65f,-0.53f,0.45f));
        markerElement_05.setRotationAngle(axisZ,30);
        markerElement_05.setVisiblity(false);

        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold>动画
    private static final int LOAD_MODEL=10001;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_MODEL) {
                //标记组-旋转
                ARAnimationParameter animationParameter = new ARAnimationParameter();//旋转参数
                animationParameter.setRepeatMode(ARAnimationRepeatMode.REVERSE);//旋转：往返
                animationParameter.setDuration(5000L);//周期

                ARAnimationRotation vistorCenterElement_Marker_Animation = new ARAnimationRotation(vistorCenterElement_Marker);
                vistorCenterElement_Marker_Animation.creatAnimation(animationParameter);//创建动画

                ARAnimationRotation route_marker_01_Animation = new ARAnimationRotation(route_marker_01);
                route_marker_01_Animation.creatAnimation(animationParameter);//创建动画

                ARAnimationRotation route_marker_02_Animation = new ARAnimationRotation(route_marker_02);
                route_marker_02_Animation.creatAnimation(animationParameter);//创建动画

                ARAnimationRotation route_marker_03_Animation = new ARAnimationRotation(route_marker_03);
                route_marker_03_Animation.creatAnimation(animationParameter);//创建动画

                animationGroup = ARAnimationManager.getInstance().addAnimationGroup("test_animation");//添加“test_animation”动画组

                //往动画组中添加动画
                animationGroup.addAnimation(vistorCenterElement_Marker_Animation);
                animationGroup.addAnimation(route_marker_01_Animation);
                animationGroup.addAnimation(route_marker_02_Animation);
                animationGroup.addAnimation(route_marker_03_Animation);

                /**--------播放所有动画--------**/
                //播放所有动画
                ARAnimationManager.getInstance().playAll();
            }
        }
    };

    //</editor-fold>
}
