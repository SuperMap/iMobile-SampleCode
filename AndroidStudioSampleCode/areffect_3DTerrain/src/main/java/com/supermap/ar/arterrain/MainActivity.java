package com.supermap.ar.arterrain;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.support.v7.app.AppCompatActivity;

import com.supermap.ar.Point3D;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.AREffectView;
import com.supermap.ar.areffect.ARGltfElement;
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
 * 3、关键方法:
 *      AREffectElement.setParentNode();					方法
 *      AREffectElement.setPosition();						方法
 *      AREffectElement.setRelativePosition();				方法
 *	    AREffectElement.loadModel();						方法
 *      ARViewEffectElement.setVisiblity();					方法
 *      ARViewEffectElement.setParentNode();				方法
 *      ARViewEffectElement.setPosition();					方法
 *      ARViewEffectElement.setRelativePosition();			方法
 *	    ARViewEffectElement.loadModel();					方法
 * 4、使用步骤：
 *   (1)点击第一个按钮，显示（隐藏）地形
 *   (2)点击其它按钮，显示（隐藏）不同的模型或标注
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends AppCompatActivity {
    Vector axisZ = new Vector(0,0,1);//Z轴
    float heiScalse = 0.6f;//模型高度比
    private AREffectView arFragment;//特效控件
    AREffectElement parentElement;//父节点

    ARGltfElement terrainElement;//地形
    ARGltfElement terrainModelElement;

    private ARGltfElement ticDoorElement;//大门及售票处01
    private ARGltfElement ticDoorElement02;//大门及售票处02

    ARViewElement markerElement_01;//标记-文本
    ARViewElement markerElement_00;//标记-文本

    private ARGltfElement vistorCenterElement;//游客中心

    private ARGltfElement vistorCenterElement_03;
    private ARViewElement markerElement_02;//标记-文本
    private ARViewElement markerElement_03;
    private ARViewElement markerElement_04;
    private ARViewElement markerElement_05;

    private ARGltfElement arbor01;
    private ARGltfElement arbor02;
    private ARGltfElement wharfElement;

    private ARViewElement arbor02_marker;

    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
    };
    private ARGltfElement routeElement02;
    private ARGltfElement wharfElement02;
    private ARGltfElement shipElement;
    private ARViewElement wharfElement02_marker;

    private static boolean isLoadModel01 = false;
    private static boolean isLoadModel02 = false;
    private static boolean isLoadModel03 = false;
    private static boolean isLoadModel04 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

        setContentView(R.layout.activity_main);
        arFragment = findViewById(R.id.ar_effect);

        //开启手势控制
        arFragment.openGesture();
        //关闭手势控制
//        arFragment.closeGesture();


        //<editor-fold>设置按钮组
        findViewById(R.id.btn01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）景区模型，
                terrainElement.setVisiblity(!(isLoadModel01));//地形
                ticDoorElement.setVisiblity(!(isLoadModel01));//南门
                ticDoorElement02.setVisiblity(!(isLoadModel01));//北门
                markerElement_00.setVisiblity(!(isLoadModel01));//南门标记
                markerElement_03.setVisiblity(!(isLoadModel01));//北门标记
                vistorCenterElement.setVisiblity(!(isLoadModel01));
                vistorCenterElement_03.setVisiblity(!(isLoadModel01));
                isLoadModel01 = !isLoadModel01;
            }
        });

        findViewById(R.id.btn02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）内部景点
                arbor02.setVisiblity(!(isLoadModel02));//亭子01
                arbor01.setVisiblity(!(isLoadModel02));//亭子02

                wharfElement.setVisiblity(!(isLoadModel02));//码头

                wharfElement02.setVisiblity(!(isLoadModel02));
                shipElement.setVisiblity(!(isLoadModel02));
                isLoadModel02 = !isLoadModel02;
            }
        });

        findViewById(R.id.btn03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）图标
                markerElement_05.setVisiblity(!(isLoadModel03));//水体标记
                markerElement_02.setVisiblity(!(isLoadModel03));//游客中心标记
                markerElement_01.setVisiblity(!(isLoadModel03));//亭子01标记
                markerElement_04.setVisiblity(!(isLoadModel03));//西坡标记
                arbor02_marker.setVisiblity(!(isLoadModel03));
                wharfElement02_marker.setVisiblity(!(isLoadModel03));

                isLoadModel03 = !isLoadModel03;
            }
        });

        findViewById(R.id.btn04).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示（隐藏）路线
                routeElement02.setVisiblity(!(isLoadModel04));
                isLoadModel04 = !isLoadModel04;
            }
        });
        //</editor-fold>


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
        terrainElement = new ARGltfElement(getApplicationContext());
        terrainElement.setParentNode(parentElement);
        terrainElement.setRelativePosition(new Point3D(0,2.5f,-1.5f));
        terrainElement.setScaleFactor(new float[]{1, 1, heiScalse});
        terrainElement.setVisiblity(isLoadModel01);

        //地形模型
        terrainModelElement = new ARGltfElement(getApplicationContext());
        terrainModelElement.setParentNode(terrainElement);
        terrainModelElement.loadModel(R.raw.terrain10301);
        terrainModelElement.setScaleFactor(new float[]{0.955f*0.01f,0.955f*0.01f,0.955f*heiScalse*0.01f});//模型比例
        terrainModelElement.setRelativePosition(new Point3D(-1.45f,0.13f,0.55f));
        terrainModelElement.setRotationAngle(axisZ,180);

        routeElement02 = new ARGltfElement(this);
        routeElement02.setParentNode(terrainElement);
        routeElement02.setRelativePosition(new Point3D(-1f,-0.16f,0.14f));
        routeElement02.setScaleFactor(new float[]{0.365f*0.31f, 0.365f*0.31f, heiScalse*0.365f*0.31f});
        routeElement02.loadModel(R.raw.road03);
        routeElement02.setVisiblity(isLoadModel04);
        routeElement02.setRotationAngle(axisZ,180);
//        routeElement02.setTransformable(arFragment,true);

        //加载亭子
        arbor02 = new ARGltfElement(this);
        arbor02.setParentNode(terrainElement);
        arbor02.setRelativePosition(new Point3D(-1.1f,1.1f,0.65f));
        arbor02.setScaleFactor(new float[]{0.05f, 0.05f, 0.05f});
        arbor02.setRotationAngle(axisZ,-240);
        arbor02.loadModel(R.raw.lt02arbor);
        arbor02.setVisiblity(isLoadModel02);

        //码头
        wharfElement = new ARGltfElement(this);
        wharfElement.setParentNode(terrainElement);
        wharfElement.setRelativePosition(new Point3D(1.38f,-0.5f,0));
        wharfElement.setScaleFactor(new float[]{0.065f*0.5f, 0.065f*0.5f, 0.065f*0.5f});
        wharfElement.setRotationAngle(axisZ,-225);
        wharfElement.loadModel(R.raw.wharf);
        wharfElement.setVisiblity(isLoadModel02);
//        wharfElement.setTransformable(arFragment,true);

        //码头02
        wharfElement02 = new ARGltfElement(this);
        wharfElement02.setParentNode(terrainElement);
        wharfElement02.setRelativePosition(new Point3D(-0.2f,-0.1f,-0.01f));
        wharfElement02.setScaleFactor(new float[]{0.08F*0.35f, 0.08F*0.35f, 0.08F*0.35f});
        wharfElement02.setRotationAngle(axisZ,60);
        wharfElement02.loadModel((R.raw.wharf));
        wharfElement02.setVisiblity(isLoadModel02);
//        wharfElement02.setTransformable(arFragment,true);

        //亭子标记-湖心亭
        arbor02_marker = new ARViewElement(this);
        arbor02_marker.setParentNode(terrainElement);
        arbor02_marker.loadModel(R.layout.marker06);
        arbor02_marker.setRelativePosition(new Point3D(-0.3f,-0.3f,0.9f));
        arbor02_marker.setScaleFactor(new float[]{0.8f,0.8f,0.8f});
        arbor02_marker.setVisiblity(isLoadModel03);

        shipElement = new ARGltfElement(this);
        shipElement.setParentNode(wharfElement);
        shipElement.setRelativePosition(new Point3D(0,0,0));
        shipElement.setRelativePosition(new Point3D(-3F,-5F,3f));
        shipElement.setScaleFactor(new float[]{0.00025F, 0.00025F, 0.00025F});
        shipElement.loadModel(R.raw.ship);
        shipElement.setVisiblity(isLoadModel02);

        //晓渔渡
        wharfElement02_marker = new ARViewElement(this);
        wharfElement02_marker.setParentNode(wharfElement);
        wharfElement02_marker.setRelativePosition(new Point3D(1.3f,-0.75f,5f));
        wharfElement02_marker.setScaleFactor(new float[]{0.8f,0.8f,0.8f});
        wharfElement02_marker.loadModel(R.layout.marker07);
        wharfElement02_marker.setVisiblity(isLoadModel03);

        //看云亭
        arbor01 = new ARGltfElement(this);
        arbor01.setParentNode(terrainElement);
        arbor01.setRelativePosition(new Point3D(-0.4f,-0.1f,0.07f));
        arbor01.setScaleFactor(new float[]{0.285f,0.285f,0.285f});
        arbor01.setRotationAngle(axisZ,-45);
        arbor01.loadModel(R.raw.lt01arbor);
        arbor01.setVisiblity(isLoadModel02);
//        arbor01.setTransformable(arFragment,true);

        markerElement_03 = new ARViewElement(this);
        markerElement_03.loadModel(R.layout.marker03);//2nd阵地//北门
        markerElement_03.setParentNode(terrainElement);
        markerElement_03.setScaleFactor(new float[]{1.365f, 1.365f, 1.365f});
        markerElement_03.setRelativePosition(new Point3D(0,2.4f,0.8f));
        markerElement_03.setVisiblity(isLoadModel01);

        ticDoorElement = new ARGltfElement(this);
        ticDoorElement.loadModel(R.raw.ticdoor);
        ticDoorElement.setParentNode(terrainElement);
        ticDoorElement.setRelativePosition(new Point3D(-0.272f,-1.3f,0.45f));
        ticDoorElement.setScaleFactor(new float[]{0.012f, 0.012f, 0.012f});
        ticDoorElement.setRotationAngle(axisZ,30);
        ticDoorElement.setVisiblity(isLoadModel01);

        ticDoorElement02 = new ARGltfElement(this);
        ticDoorElement02.loadModel(R.raw.ticdoor);//大门以及售票厅
        ticDoorElement02.setParentNode(markerElement_03);
        ticDoorElement02.setScaleFactor(new float[]{0.015f, 0.015f, 0.015f});
        ticDoorElement02.setRelativePosition(new Point3D(-0.08f,-0.18f,-0.25f));
        ticDoorElement02.setVisiblity(isLoadModel01);

        //游客中心
        vistorCenterElement = new ARGltfElement(this);
        vistorCenterElement.setParentNode(terrainElement);
        vistorCenterElement.setRelativePosition(new Point3D(1.2f,1.2f,0.55f));
        vistorCenterElement.setRotationAngle(new Vector(0,0,1),30);
        vistorCenterElement.setScaleFactor(new float[]{2.0f, 2.0f, 2.0f});
        vistorCenterElement.setRotationAngle(new Vector(0,0,1),150);
        vistorCenterElement.setVisiblity(isLoadModel01);

        vistorCenterElement_03 = new ARGltfElement(this);//房屋02
        vistorCenterElement_03.loadModel(R.raw.bambohouse);
        vistorCenterElement_03.setParentNode(vistorCenterElement);//相对于房屋
        vistorCenterElement_03.setScaleFactor(new float[]{0.8f,0.8f,0.6f});
        vistorCenterElement_03.setRelativePosition(new Point3D(0.1f,0,-0.05f));
        vistorCenterElement_03.setVisiblity(isLoadModel01);
        //</editor-fold>

        //<editor-fold>地名标记组
        //我方营地标记//南门标记
        markerElement_00 = new ARViewElement(this);
        markerElement_00.loadModel(R.layout.marker00);
        markerElement_00.setParentNode(ticDoorElement);
        markerElement_00.setRelativePosition(new Point3D(0.4f,0,20f));
        markerElement_00.setScaleFactor(new float[]{0.6f, 0.6f, 0.6f});
//        markerElement_00.setRotationAngle(axisZ,0);
        markerElement_00.setVisiblity(isLoadModel01);

        //1号阵地标记//看云亭
        markerElement_01 = new ARViewElement(this);
        markerElement_01.loadModel(R.layout.marker01);
        markerElement_01.setParentNode(terrainElement);
        markerElement_01.setRelativePosition(new Point3D(-1.0f,1.1f,1.15f));
        markerElement_01.setScaleFactor(new float[]{1.0f, 1.0f, 1.0f});
        markerElement_01.setRotationAngle(axisZ,-10);
        markerElement_01.setVisiblity(isLoadModel03);


        //游客中心标记
        markerElement_02 = new ARViewElement(this);
        markerElement_02.loadModel(R.layout.marker02);
        markerElement_02.setParentNode(vistorCenterElement_03);
        markerElement_02.setRelativePosition(new Point3D(0.05f,0.02f,0.2f));
        markerElement_02.setScaleFactor(new float[]{1.0f, 1.0f, 1.0f});
        markerElement_02.setRotationAngle(axisZ,-30);
        markerElement_02.setVisiblity(isLoadModel03);

        markerElement_04 = new ARViewElement(this);
        markerElement_04.loadModel(R.layout.marker04);//西宁坡
        markerElement_04.setParentNode(terrainElement);
        markerElement_04.setScaleFactor(new float[]{1.365f, 1.365f, 1.365f});
        markerElement_04.setRelativePosition(new Point3D(-2.5f,2.5f,0.6f));
        markerElement_04.setRotationAngle(axisZ,30);
        markerElement_04.setVisiblity(isLoadModel03);

        markerElement_05 = new ARViewElement(this);
        markerElement_05.loadModel(R.layout.marker05);//水体标注
        markerElement_05.setParentNode(terrainElement);
        markerElement_05.setScaleFactor(new float[]{0.8f, 0.8f, 0.8f});
        markerElement_05.setRelativePosition(new Point3D(-1.65f,-0.53f,0.45f));
        markerElement_05.setRotationAngle(axisZ,30);
        markerElement_05.setVisiblity(isLoadModel03);
        //</editor-fold>
    }
    //</editor-fold>

    private void setNull() {
        parentElement.setVisiblity(false);
        parentElement = null;
        terrainElement = null;//地形
        terrainModelElement = null;
        ticDoorElement = null;//大门及售票处01
        ticDoorElement02 = null;//大门及售票处02
        markerElement_01 = null;//标记-文本
        markerElement_00 = null;//标记-文本
        vistorCenterElement = null;//游客中心
        vistorCenterElement_03 = null;
        markerElement_02 = null;//标记-文本
        markerElement_03 = null;
        markerElement_04 = null;
        markerElement_05 = null;
        arbor01 = null;
        arbor02 = null;
        wharfElement = null;
        arbor02_marker = null;
        routeElement02 = null;
        wharfElement02 = null;
        shipElement = null;
        wharfElement02_marker = null;
    }
    //</editor-fold>

    @Override
    protected void onPause() {
        super.onPause();
        arFragment.onPause();
        setNull();
    }
    @Override
    protected void onResume() {
        super.onResume();
        arFragment.onResume();
        initMainModel();
    }

}
