package com.supermap.scrollermenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.supermap.data.Rectangle2D;
import com.supermap.fingerslipdemo.R;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapView;


import java.util.Arrays;


/**
 * 手势滑动菜单的自定义控件
 */
public class ScrollerMenu extends RelativeLayout implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final boolean DEBUG = false;
    private static final String TAG = ScrollerMenu.class.getSimpleName();

    private ScrollerMenuListener menuListener;

    private static final int MODE_NONE = 0;
    private static final int MODE_MENU_SCROLLING = 1;
    private static final int MODE_MENU_VALUE_PROGRESSING = 2;
    private int menuItemSelectedIndex = 0;
    private int lastX;
    private int lastY;

    private MapView mapView;

    public static interface ScrollerMenuListener {

        /**
         * @param menuItems
         * @param index
         */
        public void onMenuItemSelected(String[] menuItems, int index);

        /**
         * @param menuItems
         * @param index
         * @param progressedBy
         */
        public void onMenuItemProgressed(String[] menuItems, int index, int progressedBy);


        void onDown();

        void onDoubleTap();

        void onUp();
    }

    private static final int MENU_ITEM_WIDTH_IN_DP = 200;
    private static final int MENU_ITEM_HEIGHT_IN_DP = 50;
    private static final int MENU_ITEM_DIVIDER_MARGIN_IN_DP = 0;
    private static final int MENU_ITEM_TEXT_COLOR = Color.BLACK;

    private Drawable menuItemDefaultBackground;
    private Drawable menuItemSelectedBackground;
    private Drawable menuPanelBackground;
    private int menuItemTextColor;
    private float menuItemWidth;
    private float menuItemHeight;
    private String[] menuItems;
    private String[] menuProgress;
    private ScrollerMenuScrollView scrollView;
    private GestureDetector gestureDetector = null;
    private TextView selectedMenu;
    private TextView selectedMenuProgress;
    private int mode;

    private Handler handler = new Handler();

    private LinearLayout[] menuLL = null;
    private TextView[] menuTvs = null;
    private TextView[] menuProgressTvs = null;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (DEBUG) {
            Log.e(TAG, "onLayout");
        }
        if (scrollView != null) {
            //Android7.0以上界面不显示，只能强制主动布局，有没有更好的方法？
            scrollView.requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (DEBUG) {
            Log.e(TAG, "onMeasure");
        }
    }

    public ScrollerMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScrollerMenu,
                0, 0);

        try {
            menuItemDefaultBackground = a.getDrawable(R.styleable.ScrollerMenu_scrollerMenuItemDefaultBackground);
            if (menuItemDefaultBackground == null) {
                menuItemDefaultBackground = context.getResources().getDrawable(R.drawable.scroller_menu_item_default_background);
            }

            menuItemSelectedBackground = a.getDrawable(R.styleable.ScrollerMenu_scrollerMenuItemSelectedBackground);
            if (menuItemSelectedBackground == null) {
                menuItemSelectedBackground = context.getResources().getDrawable(R.drawable.scroller_menu_item_selected_background);
            }

            menuPanelBackground = a.getDrawable(R.styleable.ScrollerMenu_scrollerMenuPanelBackground);
            if (menuPanelBackground == null) {
                menuPanelBackground = context.getResources().getDrawable(R.drawable.scroller_menu_panel_background);
            }

            menuItemWidth = a.getDimension(R.styleable.ScrollerMenu_scrollerMenuItemWidth, MENU_ITEM_WIDTH_IN_DP);
            menuItemHeight = a.getDimension(R.styleable.ScrollerMenu_scrollerMenuItemHeight, MENU_ITEM_HEIGHT_IN_DP);
            menuItemTextColor = a.getColor(R.styleable.ScrollerMenu_scrollerMenuItemTextColor, context.getResources().getColor(R.color.scroller_menu_item_text_color));
            CharSequence[] menuArray = (CharSequence[]) a.getTextArray(R.styleable.ScrollerMenu_scrollerMenuItems);
            if (menuArray != null && menuArray.length > 2) {
                menuItems = new String[menuArray.length];
                int i = 0;
                for (CharSequence ch : menuArray) {
                    menuItems[i++] = ch.toString();
                }
            }

        } finally {
            a.recycle();
        }

        gestureDetector = new GestureDetector(getContext(), this);
    }

    public ScrollerMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScrollerMenu(Context context) {
        super(context);
        menuItemWidth = MENU_ITEM_WIDTH_IN_DP;
        menuItemHeight = MENU_ITEM_HEIGHT_IN_DP;
        menuItemTextColor = MENU_ITEM_TEXT_COLOR;
    }

    public void setMenuItems(String[] items) {
        this.menuItems = Arrays.copyOf(items, items.length);
        requestLayout();
        invalidate();
    }

    public void setMenuProgress(String[] items) {
        this.menuProgress = Arrays.copyOf(items, items.length);
        requestLayout();
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
//        Log.e(TAG, "invalidate");

        if (menuItems == null || menuItems.length < 2) {
            Log.e(TAG, "Menu items are not provided");
            return;
        }

        if (menuProgress == null || menuTvs == null || menuProgressTvs == null) {
            return;
        }

        if (menuItems.length == menuProgress.length) {
            for (int i = 0; i < menuItems.length; i++) {
                menuTvs[i].setText(menuItems[i]);
                menuProgressTvs[i].setText(menuProgress[i]);
            }
        }

    }

    public void setMenuItems(int arrayResource) {
        this.menuItems = getResources().getStringArray(arrayResource);
        requestLayout();
        invalidate();
    }

    public void setMenuItemBackgroundDrawableResource(int drawableResource) {
        this.menuItemDefaultBackground = getContext().getResources().getDrawable(drawableResource);
        invalidate();
    }

    public void setMenuItemHeight(int dimesionResource) {
        this.menuItemHeight = getResources().getDimension(dimesionResource);
        requestLayout();
        invalidate();
    }

    public void setMenuItemWidth(int dimesionResource) {
        this.menuItemWidth = getResources().getDimension(dimesionResource);
        requestLayout();
        invalidate();
    }


    public void setMenuItemTextColor(int colorResource) {
        this.menuItemTextColor = getResources().getColor(colorResource);
    }

    private boolean isMenuValueProgressing() {
        return mode == MODE_MENU_VALUE_PROGRESSING;
    }

    private boolean isMenuScroling() {
        return mode == MODE_MENU_SCROLLING;
    }

    private boolean isVisible = false;

    private void hidePanel() {
        scrollView.setVisibility(View.INVISIBLE);
        selectedMenu.setVisibility(View.INVISIBLE);
        selectedMenuProgress.setVisibility(View.INVISIBLE);

        isVisible = false;
    }

    private void showPanel() {
        scrollView.setVisibility(View.VISIBLE);
        selectedMenu.setVisibility(View.VISIBLE);
        selectedMenuProgress.setVisibility(View.VISIBLE);

        isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void show() {
        showPanel();
    }

    public void hide() {
        hidePanel();
    }

    private void notifyMenuItemProgressed(final int progress) {
        if (DEBUG) {
            Log.d(TAG, "Progress changed by:" + progress + " for menu item:" + menuItems[menuItemSelectedIndex]);

        }
        if (this.menuListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ScrollerMenu.this.menuListener.onMenuItemProgressed(menuItems, menuItemSelectedIndex, progress);
                }
            });
        }
    }

    private void notifyMenuItemSelected() {
        menuItemSelectedIndex = scrollView.getSelectedChild();
        if (DEBUG) {
            Log.d(TAG, "Menu item selected is:" + this.menuItems[menuItemSelectedIndex]);
        }
        if (this.menuListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ScrollerMenu.this.menuListener.onMenuItemSelected(menuItems, menuItemSelectedIndex);
                }
            });
        }
    }

    LinearLayout menuPanel;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (DEBUG) {
            Log.e(TAG, "onSizeChanged");
        }

        if(menuItems == null || menuItems.length < 2) {
            Log.e(TAG, "Menu items are not provided");
            return;
        }

        int viewWidth = w;
        int viewHeight = h;

        Context context = getContext();
        scrollView = new ScrollerMenuScrollView(context);
        LinearLayout completeScrollPanel = new LinearLayout(context);
        menuPanel = new LinearLayout(context);

        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, menuItemWidth, getResources().getDisplayMetrics());
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, menuItemHeight, getResources().getDisplayMetrics());

        float menuItemDivider = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MENU_ITEM_DIVIDER_MARGIN_IN_DP, getResources().getDisplayMetrics());
        float totalMenuItemDividerHeight = (menuItems.length - 1) * menuItemDivider;

        float menuPanelHeight = totalMenuItemDividerHeight + menuItems.length * height;
        float menuPanelWidth = width;

        LinearLayout.LayoutParams menuPanelLayoutParams = new LinearLayout.LayoutParams(Math.round(menuPanelWidth), Math.round(menuPanelHeight));
//        menuPanel.setBackgroundDrawable(menuPanelBackground);
        menuPanel.setBackground(menuPanelBackground);
        menuPanel.setOrientation(LinearLayout.VERTICAL);
        menuPanel.setLayoutParams(menuPanelLayoutParams);

        menuTvs = new TextView[menuItems.length];
        menuProgressTvs = new TextView[menuProgress.length];
        menuLL = new LinearLayout[menuItems.length];
        if (menuItems.length == menuProgress.length) {
            for (int i = 0; i < menuItems.length; i++) {
                TextView menu_tv = new TextView(context);
                menuTvs[i] = menu_tv;
                LinearLayout.LayoutParams menu_tv_Params = new LinearLayout.LayoutParams(0, Math.round(height), 1.0f);
                menu_tv.setBackground(menuItemDefaultBackground);
                menu_tv.setGravity(Gravity.CENTER_VERTICAL);
                menu_tv.setPadding(dip2px(context,20),0,0,0);
                menu_tv.setSingleLine(true);
                menu_tv.setLayoutParams(menu_tv_Params);
                menu_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                menu_tv.setTextColor(menuItemTextColor);
                menu_tv.setText(menuItems[i]);

                TextView progress_tv = new TextView(context);
                menuProgressTvs[i] = progress_tv;
                LinearLayout.LayoutParams progress_tvParams = new LinearLayout.LayoutParams(0, Math.round(height), 1.0f);
                progress_tv.setBackground(menuItemDefaultBackground);
                progress_tv.setGravity(Gravity.CENTER);
                progress_tv.setSingleLine(true);
                progress_tv.setLayoutParams(progress_tvParams);
                progress_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                progress_tv.setTextColor(menuItemTextColor);
                progress_tv.setText(menuProgress[i]);

                LinearLayout menuLayoutItem = new LinearLayout(context);
                menuLL[i] = menuLayoutItem;
                LinearLayout.LayoutParams menuLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                menuLayoutItem.setOrientation(LinearLayout.HORIZONTAL);
                menuLayoutItem.setLayoutParams(menuLayoutItemParams);

                menuLayoutItem.addView(menu_tv);
                menuLayoutItem.addView(progress_tv);

                menuPanel.addView(menuLayoutItem);
                //now add margin
//            if (i != (menuItems.length - 1)) {
//                View view = new View(context);
//                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(Math.round(menuPanelWidth), Math.round(menuItemDivider));
//                view.setLayoutParams(viewParams);
//                menuPanel.addView(view);
//            }
            }
        } else {
            Log.e(TAG, "menuItems.length != menuProgress.length!");
        }

        float menuPanelAboveDividerHeight = (menuItems.length - 1) * menuItemDivider;
        float totalMenuPanelAboveHeight = (menuItems.length - 1) * height + menuPanelAboveDividerHeight;

        Space menuPanelAboveView = new Space(context);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(Math.round(menuPanelWidth), Math.round(totalMenuPanelAboveHeight));
        menuPanelAboveView.setLayoutParams(viewParams);

        Space menuPanelBelowView = new Space(context);
        viewParams = new LinearLayout.LayoutParams(Math.round(menuPanelWidth), Math.round(totalMenuPanelAboveHeight));
        menuPanelBelowView.setLayoutParams(viewParams);

        ScrollView.LayoutParams completeScrollPanelParams = new ScrollView.LayoutParams(Math.round(menuPanelWidth), 2 * Math.round(totalMenuPanelAboveHeight) + Math.round(menuPanelHeight));
        completeScrollPanel.setLayoutParams(completeScrollPanelParams);
        completeScrollPanel.setOrientation(LinearLayout.VERTICAL);
        completeScrollPanel.addView(menuPanelAboveView);
        completeScrollPanel.addView(menuPanel);
        completeScrollPanel.addView(menuPanelBelowView);

        LayoutParams scrollParams = new LayoutParams(Math.round(menuPanelWidth), Math.round(totalMenuPanelAboveHeight) + Math.round(menuPanelHeight));
        scrollParams.topMargin = Math.round(viewHeight / 2f) - Math.round(totalMenuPanelAboveHeight);
        scrollParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        scrollView.setLayoutParams(scrollParams);

        scrollView.addView(completeScrollPanel);
        scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        addView(scrollView);

        selectedMenu = new TextView(context);
        LinearLayout.LayoutParams menu_tv_Params = new LinearLayout.LayoutParams(0, Math.round(height), 1.0f);
        selectedMenu.setBackground(menuItemSelectedBackground);
        selectedMenu.setGravity(Gravity.CENTER_VERTICAL);
        selectedMenu.setPadding(dip2px(context,20),0,0,0);
        selectedMenu.setSingleLine(true);
        selectedMenu.setLayoutParams(menu_tv_Params);
        selectedMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        selectedMenu.setTextColor(Color.WHITE);
        selectedMenu.setText(menuItems[0]);

        selectedMenuProgress = new TextView(context);
        LinearLayout.LayoutParams progress_tvParams = new LinearLayout.LayoutParams(0, Math.round(height), 1.0f);
        selectedMenuProgress.setBackground(menuItemSelectedBackground);
        selectedMenuProgress.setGravity(Gravity.CENTER);
        selectedMenuProgress.setSingleLine(true);
        selectedMenuProgress.setLayoutParams(progress_tvParams);
        selectedMenuProgress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        selectedMenuProgress.setTextColor(Color.WHITE);
        selectedMenuProgress.setText(menuProgress[0]);

        LinearLayout menuLayoutItem = new LinearLayout(context);
        RelativeLayout.LayoutParams menuLayoutItemParams = new RelativeLayout.LayoutParams(Math.round(menuPanelWidth), Math.round(height));
        menuLayoutItemParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        menuLayoutItemParams.topMargin = Math.round(viewHeight / 2f);
        menuLayoutItem.setOrientation(LinearLayout.HORIZONTAL);
        menuLayoutItem.setLayoutParams(menuLayoutItemParams);

        menuLayoutItem.addView(selectedMenu);
        menuLayoutItem.addView(selectedMenuProgress);

        addView(menuLayoutItem);

        scrollView.setMenuPanel(menuTvs, menuProgressTvs, height, selectedMenu, selectedMenuProgress, Math.round(totalMenuPanelAboveHeight));

        scrollView.setVisibility(View.INVISIBLE);
        selectedMenu.setVisibility(View.INVISIBLE);
        selectedMenuProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
//        Log.e(TAG, "onDown: " + motionEvent.getPointerCount());
        scrollToItem(menuItemSelectedIndex);

        if (this.menuListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ScrollerMenu.this.menuListener.onDown();
                }
            });
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
//        Log.e(TAG, "onShowPress: " + motionEvent.getPointerCount());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
//        Log.e(TAG, "onSingleTapUp: " + motionEvent.getPointerCount());
        return false;
    }

    // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float scrollX, float scrollY) {
//        Log.e(TAG, "onScroll: " + motionEvent2.getPointerCount());

        if (motionEvent2.getPointerCount() == 2) {
            return true;
        }

        if (isDoubleTouch) {
            return true;
        }

        if (isModeNone()) {
            if (Math.abs(scrollX) > Math.abs(scrollY)) {
                mode = MODE_MENU_VALUE_PROGRESSING;
            } else {
                mode = MODE_MENU_SCROLLING;
            }
        }
        if (isMenuValueProgressing()) {
            hidePanel();
            float changeX = motionEvent2.getX() - motionEvent.getX();
            float effectiveDistance = changeX * 100f / getWidth();
            final int progress = Math.round(effectiveDistance);
            notifyMenuItemProgressed(progress);
        } else {
            //scroll menu
            showPanel();
            scrollView.smoothScrollBy((int) scrollX, (int) scrollY);
        }

        return true;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.e(TAG, "dispatchTouchEvent: " + event.getPointerCount());
        if (mapView != null) {
            mapView.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    public boolean isDoubleTouch() {
        return isDoubleTouch;
    }

    private boolean isDoubleTouch = false;//是否是双指操作

    //锁定地图不让滑动
    private void lockMap() {
        Map map = mapView.getMapControl().getMap();
        Rectangle2D viewBounds = map.getViewBounds();
        map.setLockedViewBounds(viewBounds);
        map.setViewBoundsLocked(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.e(TAG, "onTouchEvent-ACTION_DOWN: " + event.getPointerCount());
                isDoubleTouch = false;//每次触摸事件都初始化
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e(TAG, "onTouchEvent-ACTION_MOVE: " + event.getPointerCount());

                if (event.getPointerCount() != 2) {
                    //不让地图滑动
                   lockMap();
                }

                break;
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "onTouchEvent-ACTION_UP: " + event.getPointerCount());

                if (this.menuListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollerMenu.this.menuListener.onUp();
                        }
                    });
                }

                if (isMenuScroling()) {
                    notifyMenuItemSelected();
                }
                mode = MODE_NONE;
                hidePanel();

                break;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
//                Log.e(TAG, "ACTION_POINTER_DOWN: " + event.getPointerCount());
                if (mapView != null) {
                    if (event.getPointerCount() == 2) {
                        Map map = mapView.getMapControl().getMap();
                        map.setViewBoundsLocked(false);

//                        Log.e(TAG, "Action.PAN");
//                        mapView.getMapControl().setAction(Action.PAN);

                        //单指——>双指
                        mode = MODE_NONE;
                        hidePanel();
                    }
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
//                Log.e(TAG, "ACTION_POINTER_up: " + event.getPointerCount());

                if (mapView != null && event.getPointerCount() == 2) {
                    //不让地图滑动
                    lockMap();
                }
                isDoubleTouch = true;

                break;
        }

        return gestureDetector.onTouchEvent(event);
    }

    private boolean isModeNone() {
        return mode == MODE_NONE;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float scrollX, float scrollY) {
        return false;
    }

    public void setMenuListener(ScrollerMenuListener listener) {
        this.menuListener = listener;
    }

    public void setSelectedMenuItem(int selectedMenuItem) {
        this.menuItemSelectedIndex = selectedMenuItem;
    }

    /**
     * @return index of the menu item which is selected
     */
    public int getSelectedMenuItem() {
        return this.menuItemSelectedIndex;
    }

    @Override
    public boolean isInEditMode() {
        return false;
    }

    public void scrollToItem(int index) {
        if (menuLL != null && index < menuLL.length) {
            scrollView.scrollTo(0, menuLL[index].getTop());
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
//        Log.e(TAG, "onSingleTapConfirmed");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
//        Log.e(TAG, "onDoubleTap");
        if (this.menuListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ScrollerMenu.this.menuListener.onDoubleTap();
                }
            });
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
//        Log.e(TAG, "onDoubleTapEvent");
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 。
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
