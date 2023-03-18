package com.supermap.imobile.dynamicpoi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.imobile.streamingapp.R;


public class PoiOverlay extends FrameLayout {
    int mIndex;

    ViewGroup mViewNormal;
    ViewGroup mViewSelected;

    TextView mTextNormal;
    TextView mTextSelected;

    ImageView mImgNormal;

    public PoiOverlay( Context context) {
        super(context);
        init(context);
    }

    public PoiOverlay( Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PoiOverlay( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PoiOverlay( Context context,  AttributeSet attrs, int defStyleAttr,
                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public PoiOverlay( Context context, int resID, LayoutParams layoutParams){
        super(context);

        init(context,resID,layoutParams);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mapoverlay_poi, this, true);

        mViewNormal = findViewById(R.id.layoutNormal);
        mViewSelected = findViewById(R.id.layoutSelected);

        mTextNormal = findViewById(R.id.textNormal);
        mTextSelected = findViewById(R.id.textSelected);

        mImgNormal = findViewById(R.id.imgNormal);

        setSelected(false);
    }

    private void init(Context context, int resID, LayoutParams layoutParams) {
        LayoutInflater.from(context).inflate(R.layout.mapoverlay_poi, this, true);

        mViewNormal = findViewById(R.id.layoutNormal);
        mViewSelected = findViewById(R.id.layoutSelected);

        mTextNormal = findViewById(R.id.textNormal);
        mTextSelected = findViewById(R.id.textSelected);

        mImgNormal = findViewById(R.id.imgNormal);

        setSelected(false);

        mImgNormal.setBackgroundResource(resID);
        mImgNormal.setLayoutParams(layoutParams);
    }


    public void setNumberId(int number) {
        mIndex = number;
        mTextNormal.setText(String.valueOf(number));
        mTextSelected.setText(String.valueOf(number));
    }

    public void updateNormalImg(int resID){
        mImgNormal.setBackgroundResource(resID);
    }

    public void updateNormalImg(int resID, LayoutParams layoutParams){
        mImgNormal.setBackgroundResource(resID);
        mImgNormal.setLayoutParams(layoutParams);
    }
    public void updateNormalImg(int resID, LayoutParams layoutParams, LayoutParams textParams){
        mImgNormal.setBackgroundResource(resID);
        mImgNormal.setLayoutParams(layoutParams);
        mTextNormal.setLayoutParams(textParams);
    }


    public void setSelected(boolean selected) {
        if (isSelected() == selected)
            return;

        super.setSelected(selected);

        mViewNormal.setVisibility(selected ? View.INVISIBLE : View.VISIBLE);
        mViewSelected.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
    }

}
