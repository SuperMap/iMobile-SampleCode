package com.supermap.imobile.bean;

import com.supermap.data.Dataset;
import com.supermap.mapping.Layer;

/**
 * 图层数据,用于存储图层
 */
public class MyLayerData {


    Layer mLayer;
    public MyLayerData(Layer mLayer){
        this.mLayer=mLayer;
    }

    /**
     * 获取图层数据
     * @return
     */
    public Layer getLayer(){
        return mLayer;
    }

    /**
     * 获取图层对应的数据集
     * @return
     */
    public Dataset getDataset(){
        return mLayer.getDataset();
    }

    /**
     *设置别名
     * @param s  别名
     */
    public void setCaption(String s){
        mLayer.setCaption(s);
    }
}
