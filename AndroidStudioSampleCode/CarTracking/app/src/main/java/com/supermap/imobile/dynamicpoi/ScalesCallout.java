package com.supermap.imobile.dynamicpoi;

import android.content.Context;
import android.view.View;

import com.supermap.mapping.CallOut;

import java.util.ArrayList;

/*
*  分级比例尺Callout
*  实现思路： 在地图比例尺变化监听中判断对应ScalesCallout的Scale Table那一层级， 自动设置对应的 自定义view
* */
public class ScalesCallout extends CallOut {

    private ArrayList<Double> mScaleTable      = null;
    private ArrayList<View> mCustomViewTable = null;

    private PoiOverlay mPoiOverLay ;

    public ScalesCallout(Context context) {
        super(context);
    }

    //构造函数：  分级比例尺列表，  对应的不同显示内容
    public ScalesCallout(Context context, ArrayList<Double> scaleTabl, ArrayList<View> customViewTable){
        super(context);
        this.mScaleTable = scaleTabl;
        this.mCustomViewTable = customViewTable;

//        this.mPoiOverLay = poiOverlay;

    }



    //根据实际情况重载此方法
    public void shouldUpdateContents(double newMapScale){
        int index = -1 ;
        if(mScaleTable == null || mCustomViewTable == null){
            return;
        }
        int size = mScaleTable.size();
        for(int i = 0 ;i < size - 1;i++){
            if(newMapScale > mScaleTable.get(0)){ //大于最大的比例尺
                index = -2;
                break;
            }
            if(newMapScale < mScaleTable.get(size - 1)){//小于最小的比例尺
                index = -1;
                break;
            }
            if(newMapScale < mScaleTable.get(i) && newMapScale > mScaleTable.get(i+1)){
                index = i;
            }
        }

        updateContentViewByIndex(index);
    }


    private void updateContentViewByIndex(int index) {
        int viewSize  = mCustomViewTable.size();
        if(viewSize == 0){ //没有传自定义多级比例尺对应view数据
            return;
        }
        if(viewSize == 1){//自定义多级比例尺view数据只有一项
//            mPoiOverLay.updateNormalImg(mCustomViewBitMapResID.get(0));

            this.setContentView(mCustomViewTable.get(0));
            return;
        }
        //viewSize >= 2
        //以上两种只是容错，实际应该1 对 1
        if(index == -2){
//            mPoiOverLay.updateNormalImg(mCustomViewBitMapResID.get(0));
            this.setContentView(mCustomViewTable.get(0));

            return;
        }

        if(index == -1){
//            mPoiOverLay.updateNormalImg(mCustomViewBitMapResID.get(viewSize-1));
            this.setContentView(mCustomViewTable.get(viewSize-1));

            return;
        }


//        mPoiOverLay.updateNormalImg(
//                        mCustomViewBitMapResID.get(index) == null?
//                        mCustomViewBitMapResID.get(0):
//                        mCustomViewBitMapResID.get(index));

        this.setContentView(
                mCustomViewTable.get(index) == null?
                        mCustomViewTable.get(0):
                        mCustomViewTable.get(index));


    }


}
