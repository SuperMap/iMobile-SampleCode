package com.supermap.adapter;

/**
 * This is just a simple class for holding data that is used to render our custom view
 */
public class CustomData {
    private boolean mIsSelected;
    private String mText;
    private int mDrawableId;

    public CustomData(boolean isSelected, String text, int drawableId) {
        mIsSelected = isSelected;
        mText = text;
        mDrawableId = drawableId;
    }

    public int getDrawableId() {
        return mDrawableId;
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
    public String getText() {
        return mText;
    }
}
