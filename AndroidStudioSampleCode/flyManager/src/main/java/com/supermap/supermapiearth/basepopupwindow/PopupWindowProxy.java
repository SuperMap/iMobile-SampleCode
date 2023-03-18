package com.supermap.supermapiearth.basepopupwindow;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 
 * <p>
 * 与basePopupWindow强引用(或者说与PopupController强引用)
 */

@SuppressLint("NewApi")
public class PopupWindowProxy extends PopupWindow {
    private final boolean isOverAndroidN = Build.VERSION.SDK_INT >= 24;


    private PopupController mController;

    public PopupWindowProxy(Context context, PopupController mController) {
        super(context);
        this.mController = mController;
    }

    public PopupWindowProxy(Context context, AttributeSet attrs, PopupController mController) {
        super(context, attrs);
        this.mController = mController;
    }

    public PopupWindowProxy(Context context, AttributeSet attrs, int defStyleAttr, PopupController mController) {
        super(context, attrs, defStyleAttr);
        this.mController = mController;
    }

    public PopupWindowProxy(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, PopupController mController) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mController = mController;
    }

    public PopupWindowProxy(PopupController mController) {
        this.mController = mController;
    }

    public PopupWindowProxy(View contentView, PopupController mController) {
        super(contentView);
        this.mController = mController;
    }

    public PopupWindowProxy(int width, int height, PopupController mController) {
        super(width, height);
        this.mController = mController;
    }

    public PopupWindowProxy(View contentView, int width, int height, PopupController mController) {
        super(contentView, width, height);
        this.mController = mController;
    }

    public PopupWindowProxy(View contentView, int width, int height, boolean focusable, PopupController mController) {
        super(contentView, width, height, focusable);
        this.mController = mController;
    }


    /**
     * fix showAsDropDown when android api ver is over N
     * <p>
     * https://code.google.com/p/android/issues/detail?id=221001
     *
     * @param anchor
     * @param xoff
     * @param yoff
     * @param gravity
     */
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (isOverAndroidN && anchor != null) {
            // FIXME: 在api>=24时，如果popup的高度是match_parent，那么无法准确定位到某个anchor下，这里的bug似乎高度的测量模式有关,说实话，下面这个解决方案其实我个人觉得并不是非常的好。。。。，
           // setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            Rect rect=new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h=anchor.getResources().getDisplayMetrics().heightPixels-rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
        //android7.0 bug 先z
        //super.showAsDropDown(anchor, xoff, yoff, gravity);
    }
    
    public void showAsDropDownBottom(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        //android7.0 bug 先z
        //super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void dismiss() {
        if (mController == null) return;

        boolean performDismiss = mController.onBeforeDismiss();
        if (!performDismiss) return;
        boolean dismissAtOnce = mController.callDismissAtOnce();
        if (dismissAtOnce) {
            callSuperDismiss();
        }
    }

    void callSuperDismiss() {
        super.dismiss();
    }

}
