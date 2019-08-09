package com.supermap.adapter;

import android.graphics.Bitmap;

/**
 * 点符号
 */

public class GridViewItemData {

    private Bitmap bitmap = null;
    private int symbolID = 0;
    private int index = 0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSymbolID() {
        return symbolID;
    }

    public void setSymbolID(int symbolID) {
        this.symbolID = symbolID;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
