package com.supermap.ar.areffect;


import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;

import com.supermap.ar.Point3D;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:AR特效动画的示范代码
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
 * 1、范例简介：示范如何在AR场景中使用动画
 * 2、关键类型/成员:
 *      AREffectView
 * 		AREffectElement
 * 	    ARParticleElement
 * 	    ARAnimationParameter
 * 	    ARAnimationRotation
 * 	    ARAnimationTranslation
 * 	    ARAnimationGroup
 * 3、关键方法:
 *      AREffectElement.setParentNode();					方法
 *      AREffectElement.setPosition();						方法
 *      AREffectElement.setRelativePosition();				方法
 *	    AREffectElement.loadModel();						方法
 *      ARParticleElement.setDiffusivityX();				方法
 *      ARParticleElement.setDiffusivityY();				方法
 *      ARParticleElement.setDiffusivityZ();				方法
 *      ARParticleElement.setRandomSize();					方法
 *      ARParticleElement.setInitRandomDirection();			方法
 *      ARParticleElement.init();							方法
 *      ARParticleElement.setScaleFactor();					方法
 *      ARAnimationParameter.setDuration();					方法
 *      ARAnimationParameter.setClockwise();				方法
 *      ARAnimationParameter.setStartPosition();			方法
 *      ARAnimationParameter.setEndPosition();				方法
 *      ARAnimationParameter.setRepeatMode();				方法
 *      ARAnimationRotation.creatAnimation();				方法
 *      ARAnimationTranslation.creatAnimation();			方法
 *      ARAnimationGroup.addAnimation();					方法
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
    private static final String TAG = "AREffectSample";
    private AREffectView arFragment;//特效控件

    ARAnimationGroup animationGroup;//动画组
    ARGltfElement arEffectElement1;

    AREffectElement parentElement;

    ARParticleElement particleElement1;

//    @Override
//    protected void onResume() {
//        super.onResume();
//        arFragment.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        arFragment.onPause();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();//请求权限

        setContentView(R.layout.activity_main);
        arFragment = findViewById(R.id.ar_effect);
        initModel();//加载模型
    }

    private void initModel() {
        //父节点
        parentElement = new AREffectElement(getApplicationContext());
        parentElement.setParentNode(arFragment);
        parentElement.setPosition(new Point3D(0,1,0));

        //模型--旋转、位移使用
        arEffectElement1 = new ARGltfElement(this);
        arEffectElement1.setParentNode(arFragment);
        arEffectElement1.setPosition(new Point3D(0,2,-1));
        arEffectElement1.loadModel(R.raw.leaf);
        arEffectElement1.setScaleFactor(new float[]{0.03f, 0.03f, 0.03f});

        //粒子对象
        particleElement1 = new ARParticleElement(this);
        particleElement1.setParentNode(parentElement);
        particleElement1.loadModel(R.raw.leaf);//单个粒子的模型
        particleElement1.setDiffusivityX(50);//设置粒子在X轴的扩散系数，值越大范围越大
        particleElement1.setDiffusivityY(50);
        particleElement1.setDiffusivityZ(50);
        particleElement1.setRandomSize(true);//默认false，初始化时单个粒子随机大小
        particleElement1.setInitRandomDirection(true);
        particleElement1.init(20);//创建20个子对象
        particleElement1.setRelativePosition(new Point3D(0,5,0));//相对位置
        particleElement1.setScaleFactor(new float[]{0.05f, 0.05f, 0.05f});


        //新线程->播放动画
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message=handler.obtainMessage();
                message.what=LOAD_MODEL;
                handler.sendMessage(message);
            }
        }).start();
    }


    private static final int LOAD_MODEL=10001;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what==LOAD_MODEL){
                ARAnimationParameter parameter=new ARAnimationParameter();//采用默认参数设置


                //旋转
                parameter.setDuration(10000L);
                parameter.setClockwise(true);
                parameter.setRepeatMode(ARAnimationRepeatMode.INFINITE);//不会停止播放
                ARAnimationRotation arAnimationRotation = new ARAnimationRotation(arEffectElement1);
                arAnimationRotation.creatAnimation(parameter);


                //位移动画，对象arEffectObject2
                parameter.setStartPosition(new Point3D(0,2,-1));
                parameter.setEndPosition(new Point3D(0.5f,1,0));
                parameter.setStartDelay(3600L);
                parameter.setRepeatCount(-1);
                parameter.setRepeatMode(ARAnimationRepeatMode.REVERSE);//每次反向
                parameter.setDuration(5000L);
                ARAnimationTranslation translation = new ARAnimationTranslation(arEffectElement1);
                translation.creatAnimation(parameter);

                animationGroup= ARAnimationManager.getInstance().addAnimationGroup("testAnimation");
                animationGroup.addAnimation(translation);
//                animationGroup.addAnimation(animationModel);
                animationGroup.addAnimation(arAnimationRotation);

                /**-----粒子系统的动画设置-----*/
                //创建粒子对象动画01（整体旋转）
                ARAnimationParameter pParameter = new ARAnimationParameter();
                pParameter.setClockwise(false);
                pParameter.setRotationAxis(new Vector(1,-1,2));
                pParameter.setDuration(10000L);
                pParameter.setRepeatMode(ARAnimationRepeatMode.INFINITE);
                particleElement1.createRotatingAnimation(pParameter,"particleSys_rotating");

                //粒子位移
                pParameter.setStartDelay(6000L);//推迟6s播放动画
                pParameter.setRepeatCount(10);//动画播放次数
                pParameter.setRepeatMode(ARAnimationRepeatMode.RESTART);
                pParameter.setEndPosition(new Point3D(0,6,-2));
                pParameter.setStartPosition(new Point3D(2,8,6));
                pParameter.setDuration(3000L);
                particleElement1.createTranslatingAnimation(pParameter,"particleSys_translating");


                /**--------播放所有动画--------**/
                //播放所有动画
                ARAnimationManager.getInstance().playAll();
            }
        }
    };

    /**
     * 检测权限
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */
    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    /**
     * 申请动态权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        String[] needPermissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        };
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
}
