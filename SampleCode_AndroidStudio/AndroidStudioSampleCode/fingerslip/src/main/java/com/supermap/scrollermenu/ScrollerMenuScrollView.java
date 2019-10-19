package com.supermap.scrollermenu;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import com.supermap.fingerslipdemo.R;

/**
 * 包含上下滑动菜单的ScrollView
 */
class ScrollerMenuScrollView extends ScrollView {
    private static final String TAG = ScrollerMenuScrollView.class.getSimpleName();
    private static final boolean DEBUG = false;

    private int itemHeight;
    private TextView[] menuTvs;
    private TextView[] menuProgressTvs;
    private TextView selectedText;
    private TextView selectedProgressText;
    private int scrollHeight;
    private int selectedChild = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (DEBUG) {
            Log.e(TAG, "onLayout");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (DEBUG) {
            Log.e(TAG, "onMeasure");
        }
    }

    public ScrollerMenuScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollerMenuScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollerMenuScrollView(Context context) {
        super(context);
    }

    public void setMenuPanel(TextView[] menuTvs, TextView[] menuProgressTvs, float height, TextView tv, TextView selectedMenuProgress, int scrollHeight) {
        this.itemHeight = (int) height;
        this.menuTvs = menuTvs;
        this.menuProgressTvs = menuProgressTvs;
        this.selectedText = tv;
        this.selectedProgressText = selectedMenuProgress;
        this.scrollHeight = scrollHeight;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, Math.min(scrollY, this.scrollHeight), clampedX, clampedY);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, Math.min(y, this.scrollHeight));
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        selectedChild = 0;
        if (t > 0) {
//          selectedChild = t / itemHeight;

            float index = (float)t / (float) itemHeight;
            selectedChild = Math.round(index);
        }
        this.selectedText.setText(menuTvs[selectedChild].getText());
        this.selectedProgressText.setText(menuProgressTvs[selectedChild].getText());

        for (int i = 0; i < menuTvs.length; i++) {
//          menuItems[i].setVisibility(i == selectedChild ? View.INVISIBLE : View.VISIBLE);

            menuTvs[i].setTextColor(i == selectedChild ? Color.TRANSPARENT : getResources().getColor(R.color.scroller_menu_item_text_color));
            menuProgressTvs[i].setTextColor(i == selectedChild ? Color.TRANSPARENT : getResources().getColor(R.color.scroller_menu_item_text_color));
        }
    }

    protected int getSelectedChild() {
        return selectedChild;
    }

    @Override
    public boolean isInEditMode() {
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //不响应手势，由上层控制
        return false;
    }
}
