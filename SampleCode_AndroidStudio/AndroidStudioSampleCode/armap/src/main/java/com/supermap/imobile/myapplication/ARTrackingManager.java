package com.supermap.imobile.myapplication;

import android.content.Context;

import com.supermap.ar.ArSensorManager;

public class ARTrackingManager {

    private Context         mContext;
    private SimpleRenderer  simpleRenderer = new SimpleRenderer();;
    private boolean         isInited = false;//是否初始化完成

    ARTrackingManager(Context context){
        mContext = context;
    }

    //AR 运动追踪初始化
    public void init(){

        //绑定资源文件
        org.artoolkit.ar.base.assets.AssetHelper assetHelper = new org.artoolkit.ar.base.assets.AssetHelper(mContext.getAssets());
        assetHelper.cacheAssetFolder(mContext, "Data");

        //SimpleRenderer 实现传感器监听
        ArSensorManager.registerSensorListener(simpleRenderer);

        //return matrix for render rather than set to it.
        isInited = true;
    }

    //提供渲染器
    public SimpleRenderer getSimpleRenderer() {
        return simpleRenderer;
    }

    //获取是否是注册模式
    public boolean getIsRegisterMode() {
        return simpleRenderer.getARSceneControlFlag();
    }

    //设置是否是注册模式
    public void setIsRegisterMode(boolean registerMode) {
        simpleRenderer.setARSceneControlFlag(registerMode);
    }

    //获取是否初始化完成
    public boolean isInited() {
        return isInited;
    }

    //获取投影矩阵，和模型矩阵配套使用
    public float [] getProjectionMatrix(){
       return simpleRenderer.getProjectionMatrix();
    }

    //获取模型矩阵，和投影矩阵配套使用
    public float [] getTransformMatrix(){
        return simpleRenderer.getTransformMatrix();
    }

}
