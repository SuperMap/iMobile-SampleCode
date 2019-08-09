package com.supermap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.supermap.data.GeoStyle;
import com.supermap.data.Rectangle2D;
import com.supermap.fingerslipdemo.MainActivity;
import com.supermap.fingerslipdemo.R;
import com.supermap.mapping.*;

import com.supermap.scrollermenu.ScrollerMenu;
import com.supermap.seekbar.BubbleSeekBar;


/**
 * 栅格风格
 */
public class GridStyleFragment extends Fragment implements ScrollerMenu.ScrollerMenuListener, View.OnClickListener {
    private static final String TAG = "GridStyleFragment";

    private static final String KEY_BUNDLE_PROGRESS = "KEY_BUNDLE_PROGRESS";
    private static final String KEY_BUNDLE_SELECTED_MENU_INDEX = "KEY_BUNDLE_SELECTED_MENU_INDEX";
    private static int PROGRESS_MAX = 100;
    private static int PROGRESS_MIN = 0;

    private TextView selectedMenu;
    private TextView progressText;
    private int progress;
    private ScrollerMenu scrollerMenu;


    private BubbleSeekBar seekbar = null;
    private LinearLayout ll_show_progress = null;

    private String[] menuItems = new String[]{
            "透明度", "对比度", "亮度"
    };

    private int gridOpaqueRate = 50;
    private int gridContrast = 0;
    private int gridBrightness = 0;

    private String[] menuProgress = new String[]{
            "" + gridOpaqueRate + "%",
            "" + gridContrast + "%",
            "" + gridBrightness + "%"
    };

    /**
     * 当前选择的菜单
     */
    private int selectedItem = 0;

    public GridStyleFragment() {
    }

    private MapView mapView = null;
    private int currentLayer = 3;

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    private Context mContext = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grid_style, container, false);

        initView(rootView);

        initListening(rootView);

        initScrollerMenu(savedInstanceState);

        initSeekBar(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        selectedMenu = (TextView) rootView.findViewById(R.id.fragment_image_holder_selected_menu);
        progressText = (TextView) rootView.findViewById(R.id.fragment_image_holder_progress);
        scrollerMenu = (ScrollerMenu) rootView.findViewById(R.id.scroller_menu);
    }

    private void initListening(View rootView) {
        rootView.findViewById(R.id.menu_show_hide).setOnClickListener(this);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    private void initScrollerMenu(Bundle savedInstanceState) {
        int menuItemIndex = 0;
        if (savedInstanceState != null) {
            progress = savedInstanceState.getInt(KEY_BUNDLE_PROGRESS);
            menuItemIndex = savedInstanceState.getInt(KEY_BUNDLE_SELECTED_MENU_INDEX);
        }
        scrollerMenu.setSelectedMenuItem(menuItemIndex);
        progressText.setText(String.valueOf(progress));
        selectedMenu.setText(menuItems[menuItemIndex]);

        scrollerMenu.setMenuItems(menuItems);
        scrollerMenu.setMenuProgress(menuProgress);

        scrollerMenu.setMenuListener(this);

        scrollerMenu.setMapView(mapView);
    }

    private void showSeekBar() {
        seekbar.setVisibility(View.VISIBLE);
    }

    private void hideSeekBar() {
        seekbar.setVisibility(View.GONE);
    }

    private boolean isSeekBarTouched = false;

    private void initSeekBar(View rootView) {
        seekbar = rootView.findViewById(R.id.seekbar);
        ll_show_progress = rootView.findViewById(R.id.ll_show_progress);

        setSeekBar(0, 100);
        seekbar.setVisibility(View.VISIBLE);
        seekbar.setProgress(50);
        progress = 50;

        progressText.setText(menuProgress[0]);

        seekbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e(TAG, "seekBar:OnClick");
                isSeekBarTouched = true;
            }
        });

        seekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progressedBy, float progressFloat, boolean fromUser) {
//                Log.e(TAG, "onProgressChanged");

                scrollerMenu.hide();

                if (!isSeekBarTouched) {
                    //由滑动屏幕引起的变更不需要再重复设置
//                    Log.e(TAG, "onProgressChanged：!isSeekBarTouched");
                    return;
                }

                //设置参数
                switch (selectedItem) {
                    case 0://透明度
                        setGridOpaqueRate(progressedBy);
                        break;
                    case 1://对比度
                        setGridContrast(progressedBy);
                        break;
                    case 2://亮度
                        setGridBrightness(progressedBy);
                        break;
                }

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progressedBy, float progressFloat) {
//                Log.e(TAG, "getProgressOnActionUp");
                isSeekBarTouched = false;
                progress = progressedBy;
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progressedBy, float progressFloat, boolean fromUser) {
//                Log.e(TAG, "getProgressOnFinally");

            }
        });
    }


    //设置SeekBar的范围
    private void setSeekBar(int minProgress, int maxProgress) {
        PROGRESS_MIN = minProgress;
        PROGRESS_MAX = maxProgress;

        seekbar.getConfigBuilder()
                .min(minProgress)
                .max(maxProgress)
                .progress(minProgress)
                .build();
    }

    //设置SeekBar及默认的范围
    private void setSeekBar(int minProgress, int maxProgress, int defultProgress) {
        PROGRESS_MIN = minProgress;
        PROGRESS_MAX = maxProgress;

        seekbar.getConfigBuilder()
                .min(minProgress)
                .max(maxProgress)
                .progress(defultProgress)
                .build();
    }

    @Override
    public void onMenuItemSelected(String[] menuItems, int index) {
//        Log.e(TAG, "onMenuItemSelected");
        //只有地图响应双指手势
        if (scrollerMenu.isDoubleTouch()) {
            return;
        }

        selectedItem = index;

        if (selectedItem == 0) {
            //透明度
            chooseMenuGridOpaqueRate();
        }
        else if (selectedItem == 1) {
            //对比度
            chooseMenuGridBrightness();
        }
        else if (selectedItem == 2) {
            //亮度
            chooseMenuGridContrast();
        }

    }

    //选择透明度菜单
    private void chooseMenuGridOpaqueRate() {
        selectedMenu.setText(menuItems[0]);
        progressText.setText(menuProgress[0]);

        setSeekBar(0, 100);
        showSeekBar();

        String str = menuProgress[0];
        String sub;
        try {
            sub = str.substring(0, str.length() - 1);
            progress = Integer.parseInt(sub);
            seekbar.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            seekbar.setProgress(0);
        }

    }

    //选择对比度菜单
    private void chooseMenuGridBrightness() {
        selectedMenu.setText(menuItems[1]);
        progressText.setText(menuProgress[1]);

        setSeekBar(-100, 100, 0);
        showSeekBar();

        String str = menuProgress[1];
        String sub;
        try {
            sub = str.substring(0, str.length() - 1);
            progress = Integer.parseInt(sub);
            seekbar.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            seekbar.setProgress(0);
        }

    }

    //选择亮度菜单
    private void chooseMenuGridContrast() {
        selectedMenu.setText(menuItems[2]);
        progressText.setText(menuProgress[2]);

        setSeekBar(-100, 100, 0);
        showSeekBar();

        String str = menuProgress[2];
        String sub;
        try {
            sub = str.substring(0, str.length() - 1);
            progress = Integer.parseInt(sub);
            seekbar.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            seekbar.setProgress(0);
        }

    }

    /**
     * 刷新ScrollerView
     */
    private void refreshScrollerMenu() {
        scrollerMenu.scrollToItem(0);
        scrollerMenu.scrollToItem(menuItems.length - 1);
        scrollerMenu.scrollToItem(selectedItem);
    }

    @Override
    public void onMenuItemProgressed(String[] menuItems, int index, int progressedBy) {
//      Log.e(TAG, "onMenuItemProgressed");
        showSeekBar();

        int totalprogress = progress + progressedBy;
        if (totalprogress < PROGRESS_MIN) {
            totalprogress = PROGRESS_MIN;
        }

        if (totalprogress > PROGRESS_MAX) {
            totalprogress = PROGRESS_MAX;
        }
        seekbar.setProgress(totalprogress);

        //设置参数
        switch (selectedItem) {
            case 0:
                //透明度
                setGridOpaqueRate(totalprogress);
                break;
            case 1:
                //对比度
                setGridContrast(totalprogress);
                break;
            case 2:
                //亮度
                setGridBrightness(totalprogress);
                break;
        }

    }

    //设置透明度
    private void setGridOpaqueRate(int gridOpaqueRate) {
        Layers layers = mapView.getMapControl().getMap().getLayers();
        Layer layer = layers.get(currentLayer);
        if (layer != null && layer.getTheme() == null) {
            layer.setEditable(true);

            if (layer.getAdditionalSetting() != null && layer.getAdditionalSetting() instanceof LayerSettingGrid) {
                LayerSettingGrid layerSettingGrid = (LayerSettingGrid) layer.getAdditionalSetting();
                if (layerSettingGrid != null) {
                    layerSettingGrid.setOpaqueRate(100 - gridOpaqueRate);
                    mapView.getMapControl().getMap().refresh();

                    this.gridOpaqueRate = gridOpaqueRate;
                    menuProgress[0] = "" + this.gridOpaqueRate + "%";

                    progressText.setText(menuProgress[0]);
                    scrollerMenu.setMenuProgress(menuProgress);

                }
            }

        } else {
            // 专题图
            Log.e(TAG, "专题图");
        }

    }

    //设置对比度
    private void setGridBrightness(int gridBrightness) {
        Layers layers = mapView.getMapControl().getMap().getLayers();
        Layer layer = layers.get(currentLayer);
        if (layer != null && layer.getTheme() == null) {
            layer.setEditable(true);

            if (layer.getAdditionalSetting() != null && layer.getAdditionalSetting() instanceof LayerSettingGrid) {
                LayerSettingGrid layerSettingGrid = (LayerSettingGrid) layer.getAdditionalSetting();
                if (layerSettingGrid != null) {
                    layerSettingGrid.setBrightness(gridBrightness);
                    mapView.getMapControl().getMap().refresh();

                    this.gridBrightness = gridBrightness;
                    menuProgress[1] = "" + this.gridBrightness + "%";

                    progressText.setText(menuProgress[1]);
                    scrollerMenu.setMenuProgress(menuProgress);

                }
            }

        } else {
            // 专题图
            Log.e(TAG, "专题图");
        }

    }

    //设置亮度
    private void setGridContrast(int gridContrast) {
        Layers layers = mapView.getMapControl().getMap().getLayers();
        Layer layer = layers.get(currentLayer);
        if (layer != null && layer.getTheme() == null) {
            layer.setEditable(true);

            if (layer.getAdditionalSetting() != null && layer.getAdditionalSetting() instanceof LayerSettingGrid) {
                LayerSettingGrid layerSettingGrid = (LayerSettingGrid) layer.getAdditionalSetting();
                if (layerSettingGrid != null) {
                    layerSettingGrid.setContrast(progress);
                    mapView.getMapControl().getMap().refresh();

                    this.gridContrast = gridContrast;
                    menuProgress[2] = "" + this.gridContrast + "%";

                    progressText.setText(menuProgress[2]);
                    scrollerMenu.setMenuProgress(menuProgress);

                }
            }

        } else {
            // 专题图
            Log.e(TAG, "专题图");
        }

    }

    @Override
    public void onDown() {
//        Log.e(TAG, "OnDown");
        //触摸屏幕时隐藏
    }

    private void lockMap() {
        //不让地图滑动
        Map map = mapView.getMapControl().getMap();
        Rectangle2D viewBounds = map.getViewBounds();
        map.setLockedViewBounds(viewBounds);
        map.setViewBoundsLocked(true);
    }


    public void onDoubleTap() {
//        Log.e(TAG, "OnDoubleTap");
        //双击后不让地图滑动
        lockMap();
    }

    @Override
    public void onUp() {
//        Log.e(TAG,"onUP");
        //手指抬起
        if (seekbar != null) {
            progress = seekbar.getProgress();
        }

        //进度不刷新的问题
        refreshScrollerMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_PROGRESS, progress);
        outState.putInt(KEY_BUNDLE_SELECTED_MENU_INDEX, scrollerMenu.getSelectedMenuItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_show_hide:
                showOrHideMenu();

                break;

            case R.id.btn_cancel:
                hideEditFragment();
                break;
        }
    }

    //隐藏或显示菜单
    private void showOrHideMenu() {
        //进度不刷新的问题
        refreshScrollerMenu();

        if (!scrollerMenu.isVisible()) {
            scrollerMenu.show();
        } else {
            scrollerMenu.hide();
        }
    }

    private void hideEditFragment() {
        mapView.getMapControl().setAction(Action.PAN);
        Map map = mapView.getMapControl().getMap();
        map.setViewBoundsLocked(false);

        MainActivity mainActivity = (MainActivity) mContext;
        mainActivity.showBottomMenu();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(this);
        fragmentTransaction.commit();
    }

}
