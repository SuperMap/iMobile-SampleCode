package com.supermap.adapter;

/**
 *
 * Created by Administrator on 2018/10/19.
 */

public class ColorInfo {
    private boolean mIsSelected;
    private String mColor;

    public ColorInfo(boolean isSelected, String color) {
        mIsSelected = isSelected;
        mColor = color;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    /**
     * 是否选中
     */
    public boolean isSelected() {
        return mIsSelected;
    }

    /**
     * @return the text
     */
    public String getColor() {
        return mColor;
    }

    @Override
    public String toString() {
        return "ColorInfo{" +
                "mIsSelected=" + mIsSelected +
                ", mColor='" + mColor + '\'' +
                '}';
    }
}
