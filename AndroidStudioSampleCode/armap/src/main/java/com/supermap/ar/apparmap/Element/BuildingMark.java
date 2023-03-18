package com.supermap.ar.apparmap.Element;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.are.sceneform.rendering.RenderingResources;
import com.supermap.ar.apparmap.bean.BuildingInfo;
import com.supermap.ar.apparmap.R;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.ARViewElement;

/**
 * 建筑物的标记类
 */
public class BuildingMark {
    private Context mContext;
    private boolean selectedStatus = false;
    private String type;

    public BuildingMark(Context context, String type){
        mContext = context;
        this.type = type;
    }

    public ARViewElement drawMark(AREffectElement parent) {
        String beizhu = this.type;    // 备注
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_test, null);
        if ("1".equals(beizhu)){     // 商场
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.market);
        }else if ("2".equals(beizhu)){    // 医院
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.hospital);
        }else if ("3".equals(beizhu)){    // 学校
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.book);
        }else if ("4".equals(beizhu)){
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.build);
        }else {

        }

        ARViewElement infoElement = new ARViewElement(mContext);
        infoElement.setParentNode(parent);
        infoElement.loadModel(view);
        infoElement.setVisualizerType(AREffectElement.VisualizerType.NULL);
        return infoElement;
    }

    public void select() {
        this.selectedStatus = true;
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_test, null);
        if ("1".equals(this.type)){     // 商场
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.market_choose);
        }else if ("2".equals(this.type)){    // 医院
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.hospital_choose);
        }else if ("3".equals(this.type)){    // 学校
            Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.book_choose);
            (view.findViewById(R.id.building_mark)).setBackground(drawable);
        }else if ("4".equals(this.type)){   // 普通建筑
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.build);
        }else{
          //  (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.build_choose);
        }
    }

    public void unSelect() {
        this.selectedStatus = false;
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_test, null);
        if ("1".equals(this.type)){     // 商场
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.market);
        }else if ("2".equals(this.type)){    // 医院
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.hospital);
        }else if ("3".equals(this.type)){    // 学校
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.book);
        }else if ("4".equals(this.type)){
            (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.build);
        }else{
           // (view.findViewById(R.id.building_mark)).setBackgroundResource(R.mipmap.build);
        }
    }

    public boolean isSelectedStatus() {
        return selectedStatus;
    }
}
