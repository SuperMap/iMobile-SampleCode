package com.supermap.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.adapter.ColorInfo;
import com.supermap.adapter.ColorInfoAdapter;
import com.supermap.adapter.CustomArrayAdapter;
import com.supermap.adapter.CustomData;
import com.supermap.data.*;
import com.supermap.fingerslipdemo.MainActivity;
import com.supermap.fingerslipdemo.R;
import com.supermap.mapping.*;
import com.supermap.scrollermenu.HorizontalListView;
import com.supermap.scrollermenu.ScrollerMenu;
import com.supermap.seekbar.BubbleSeekBar;


import java.util.ArrayList;

/**
 * 文本风格
 */
public class TextStyleFragment extends Fragment implements ScrollerMenu.ScrollerMenuListener, View.OnClickListener {
    private static final String TAG = "TextStyleFragment";

    private static final String KEY_BUNDLE_PROGRESS = "KEY_BUNDLE_PROGRESS";
    private static final String KEY_BUNDLE_SELECTED_MENU_INDEX = "KEY_BUNDLE_SELECTED_MENU_INDEX";
    private static int PROGRESS_MAX = 100;
    private static int PROGRESS_MIN = 0;

    private TextView selectedMenu;
    private TextView progressText;
    private int progress;
    private ScrollerMenu scrollerMenu;

    private LinearLayout linearLayoutMenu;

    private BubbleSeekBar seekbar = null;
    private LinearLayout ll_show_progress = null;

    ColorInfoAdapter colorInfoAdapter = null;
    CustomArrayAdapter adapterFont = null;
    CustomArrayAdapter adapterPosition = null;
    CustomArrayAdapter adapterStyle = null;

    private HorizontalListView horizontalListView;
    private ArrayList<ColorInfo> mColorInfos = new ArrayList<>();

    private String[] menuItems = new String[]{
            "字体", "字号", "颜色", "旋转角度", "位置", "风格"
    };

    private String textFont = "";
    private int textSize = 6;
    private String textColor = "";
    private int textAngle = 0;
    private String textPosition = " ";
    private String textStyle = " ";

    private Geometry m_EditGeometry;

    private String[]  menuProgress = new String[]{
            "" + textFont,
            "" + textSize,
            "" + textColor,
            "" + textAngle + "°",
            "" + textPosition,
            "" + textStyle,
    };

    private int mLastPosition = -1;

    private int selectedItem = 0;

    public TextStyleFragment() {
    }

    private MapView mapView = null;
    private TrackingLayer m_TrackingLayer = null;
//    private int currentLayer = 1;
    private String currentLayer = "ChartName@Changchun";


    private Recordset m_RecordSet = null;


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
        View rootView = inflater.inflate(R.layout.fragment_text_style, container, false);

        initView(rootView);

        initListening(rootView);

        m_EditGeometry = getEditGeometry();
        initTextStyleParam();

        initScrollerMenu(savedInstanceState);

        initSeekBar(rootView);


        initHorizontalListView(rootView);

        m_TrackingLayer = mapView.getMapControl().getMap().getTrackingLayer();

        m_TrackingLayer.clear();
        m_TrackingLayer.add(m_EditGeometry, "TEXT");

        return rootView;
    }

    private void initHorizontalListView(View rootView) {
        horizontalListView = rootView.findViewById(R.id.horizontal_listview);
        setupCustomLists();
    }

    private void initView(View rootView) {
        selectedMenu = (TextView) rootView.findViewById(R.id.fragment_image_holder_selected_menu);
        progressText = (TextView) rootView.findViewById(R.id.fragment_image_holder_progress);
        scrollerMenu = (ScrollerMenu) rootView.findViewById(R.id.scroller_menu);

        linearLayoutMenu = rootView.findViewById(R.id.ll_menu);
    }

    private void initListening(View rootView) {
        rootView.findViewById(R.id.menu_show_hide).setOnClickListener(this);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        rootView.findViewById(R.id.samples).setOnClickListener(this);
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

    private void initTextStyleParam() {

        if (m_EditGeometry != null) {
            GeoText geoText = (GeoText) m_EditGeometry;
            TextStyle textStyle = geoText.getTextStyle();
            String textFontFile = textStyle.getFontName();
            textFont = TextStyleProcess.getFontName(textFontFile, 1);
            textSize = (int)textStyle.getFontHeight();

            com.supermap.data.Color color = textStyle.getForeColor();
            textColor = "#" + Integer.toHexString(color.getR()) + Integer.toHexString(color.getG()) + Integer.toHexString(color.getB());

            textAngle = (int)textStyle.getRotation();

            TextAlignment textAlignment = textStyle.getAlignment();
            textPosition = TextStyleProcess.getTextAlignment(textAlignment);

        }

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

        setSeekBar(0, 100, 0);

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
//              Log.e(TAG, "onProgressChanged");
                scrollerMenu.hide();

                if (!isSeekBarTouched) {
                    //由滑动屏幕引起的变更不需要再重复设置
//                    Log.e(TAG, "onProgressChanged：!isSeekBarTouched");
                    return;
                }

                //设置参数
                if (selectedItem == 1) {
                    setTextFontSize(progressedBy);
                } else if (selectedItem == 3) {
                    setTextRotation(progressedBy);
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
    private void setSeekBar(int minProgress, int maxProgress, float progress) {
        PROGRESS_MIN = minProgress;
        PROGRESS_MAX = maxProgress;

        seekbar.getConfigBuilder()
                .min(minProgress)
                .max(maxProgress)
                .progress(progress)
                .build();
    }

    private void setupCustomLists() {
        setupCustomColorLists();

        // Make an array colorInfoAdapter using the built in android layout to render a list of strings
        colorInfoAdapter = new ColorInfoAdapter(mContext, mColorInfos);
        adapterFont = new CustomArrayAdapter(mContext, mFontCustomData);
        adapterPosition = new CustomArrayAdapter(mContext, mPositionCustomData);
        adapterStyle = new CustomArrayAdapter(mContext, mStyleCustomData);

        // Assign colorInfoAdapter to HorizontalListView
        horizontalListView.setAdapter(adapterFont);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //字体
                if (selectedItem == 0) {
                    String font = mFontCustomData[position].getText();
                    setTextFont(font);
                    if (mLastPosition != position) {
                        for (int i = 0; i < mFontCustomData.length; i++) {
                            if (i == position) {
                                mFontCustomData[i].setSelected(true);
                            } else {
                                mFontCustomData[i].setSelected(false);
                            }
                        }
                    }
                    adapterFont.notifyDataSetChanged();

                } else if (selectedItem == 2) {//颜色
                    if (mLastPosition != position) {
                        for (int i = 0; i < mColorInfos.size(); i++) {
                            if (i == position) {
                                mColorInfos.get(i).setSelected(true);
                            } else {
                                mColorInfos.get(i).setSelected(false);
                            }
                        }
                    }
                    setTextFontColor(position);
                    colorInfoAdapter.notifyDataSetChanged();

                } else if (selectedItem == 4) {//字体位置
                    String font = mPositionCustomData[position].getText();
                    setTextFontPosition(font);
                    if (mLastPosition != position) {
                        for (int i = 0; i < mPositionCustomData.length; i++) {
                            if (i == position) {
                                mPositionCustomData[i].setSelected(true);
                            } else {
                                mPositionCustomData[i].setSelected(false);
                            }
                        }
                    }
                    adapterPosition.notifyDataSetChanged();

                } else if (selectedItem == 5) {//字体风格
                    if (mStyleCustomData[position].isSelected()) {
                        mStyleCustomData[position].setSelected(false);
                        setTextStyle(position, false);
                    } else {
                        mStyleCustomData[position].setSelected(true);
                        setTextStyle(position, true);
                    }
                    adapterStyle.notifyDataSetChanged();
                }

                mLastPosition = position;

            }
        });
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
            //字体
            chooseMenuTextFont();
        }
        else if (selectedItem == 1) {
            //字号
            chooseMenuTextFontSize();
        }
        else if (selectedItem == 2) {
            //颜色
            chooseMenuTextFontColor();
        }
        else if (selectedItem == 3) {
            //旋转角度
            chooseMenuTextFontRotation();
        }
        else if (selectedItem == 4) {
            //位置
            chooseMenuTextFontPosition();
        }
        else if (selectedItem == 5) {
            //风格
            chooseMenuTextFontStyle();
        }

    }

    //选择字体风格菜单
    private void chooseMenuTextFontStyle() {
        hideSeekBar();

        horizontalListView.setVisibility(View.VISIBLE);
        horizontalListView.setAdapter(adapterStyle);

        selectedMenu.setText(menuItems[5]);
        progressText.setText(menuProgress[5]);
    }

    //选择字体位置菜单
    private void chooseMenuTextFontPosition() {
        hideSeekBar();

        horizontalListView.setVisibility(View.VISIBLE);
        horizontalListView.setAdapter(adapterPosition);

        selectedMenu.setText(menuItems[4]);
        progressText.setText(menuProgress[4]);
    }

    //选择旋转角度菜单
    private void chooseMenuTextFontRotation() {
        horizontalListView.setVisibility(View.GONE);

        selectedMenu.setText(menuItems[3]);
        progressText.setText(menuProgress[3]);

        setSeekBar(0, 360, 0);
        showSeekBar();

        String str = menuProgress[3];
        try {
            String sub;
            sub = str.substring(0, str.length() - 2);
            progress = Integer.parseInt(sub);
            seekbar.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            seekbar.setProgress(0);
        }
    }

    //选择颜色菜单
    private void chooseMenuTextFontColor() {
        hideSeekBar();

        horizontalListView.setVisibility(View.VISIBLE);
        horizontalListView.setAdapter(colorInfoAdapter);

        selectedMenu.setText(menuItems[2]);
        progressText.setText(menuProgress[2]);

    }

    //选择字号菜单
    private void chooseMenuTextFontSize() {
        horizontalListView.setVisibility(View.GONE);

        selectedMenu.setText(menuItems[1]);
        progressText.setText(menuProgress[1]);

        setSeekBar(1, 72, textSize);
        showSeekBar();

        String str = menuProgress[1];
        try {
            String sub;
            sub = str.substring(0, str.length());
            progress = Integer.parseInt(sub);
            seekbar.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            seekbar.setProgress(0);
        }
    }

    //选择字体菜单
    private void chooseMenuTextFont() {
        hideSeekBar();

        horizontalListView.setVisibility(View.VISIBLE);
        horizontalListView.setAdapter(adapterFont);

        selectedMenu.setText(menuItems[0]);
        progressText.setText(menuProgress[0]);

    }

    private void refreshScrollerMenu() {
        scrollerMenu.scrollToItem(0);
        scrollerMenu.scrollToItem(menuItems.length - 1);
        scrollerMenu.scrollToItem(selectedItem);
    }

    @Override
    public void onMenuItemProgressed(String[] menuItems, int index, int progressedBy) {
//      Log.e(TAG, "onMenuItemProgressed");

        if (index == 0 || index == 2 || index == 4 || index == 5 ) {
            hideSeekBar();
        } else {
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
                case 1:
                    //字体
                    setTextFontSize(totalprogress);
                    break;
                case 3:
                    //旋转角度
                    setTextRotation(totalprogress);
                    break;
            }
        }

    }

    @Override
    public void onDown() {
        //触摸屏幕时隐藏
        horizontalListView.setVisibility(View.GONE);
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
                horizontalListView.setVisibility(View.GONE);
                break;
            case R.id.samples:
                scrollerMenu.hide();
                showOrHideSamples();
                break;
            case R.id.btn_cancel:
                hideEditFragment();
                m_RecordSet.dispose();
//                m_TrackingLayer.clear();
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

        scrollerMenu.invalidate();
    }

    private void showOrHideSamples() {
        if (horizontalListView.getVisibility() == View.VISIBLE) {
            horizontalListView.setVisibility(View.GONE);
        } else {
            horizontalListView.setVisibility(View.VISIBLE);
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

    private Geometry getEditGeometry() {
        Workspace workspace;
        try {
            workspace = mapView.getMapControl().getMap().getWorkspace();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (workspace == null) {
            return null;
        }
        Layers layers = mapView.getMapControl().getMap().getLayers();
        Layer layer = layers.get(currentLayer);
        if (layer == null) {
            return null;
        }
        layer.setEditable(true);
        layer.setSelectable(true);

//        Datasource datasourcePD = workspace.getDatasources().get(0);
//        Dataset dataset = datasourcePD.getDatasets().get("ChartName@Changchun");
//        DatasetVector plDatasetVector = (DatasetVector) dataset;
//        m_RecordSet = plDatasetVector.query("SmID=2", CursorType.DYNAMIC);
        m_RecordSet=((DatasetVector)layer.getDataset()).getRecordset(false,CursorType.DYNAMIC);
        if (m_RecordSet != null && m_RecordSet.getRecordCount() > 0) {
            m_RecordSet.moveFirst();
            Geometry plGeometry = m_RecordSet.getGeometry();
            if (plGeometry.getType() == GeometryType.GEOTEXT) {
                return plGeometry;
            }
        }//if
        return null;
    }


    private void setTextRotation(int progress) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setRotation(progress);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        textAngle = progress;
        menuProgress[3] = "" + textAngle;

        progressText.setText(menuProgress[3]);
        scrollerMenu.setMenuProgress(menuProgress);
    }

    //设置字号
    private void setTextFontSize(int progress) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setFontHeight((double) progress);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        textSize = progress;
        menuProgress[1] = "" + textSize;

        progressText.setText(menuProgress[1]);
        scrollerMenu.setMenuProgress(menuProgress);

    }

    //设置字体风格
    private void setTextStyle(int position, boolean bSelected) {
        String textStyle = mStyleCustomData[position].getText();
        if (textStyle.equals("加粗")) {
            setTextBold(bSelected);
        } else if (textStyle.equals("斜体")) {
            setTextItalic(bSelected);
        } else if (textStyle.equals("下划线")) {
            setTextUnderline(bSelected);
        } else if (textStyle.equals("删除线")) {
            setTextStrikeout(bSelected);
        } else if (textStyle.equals("轮廓")) {
            setTextOutline(bSelected);
        } else if (textStyle.equals("阴影")) {
            setTextShadow(bSelected);
        }
    }

    private void setTextOutline(boolean bSelected) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setOutline(bSelected);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextShadow(boolean bSelected) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setShadow(bSelected);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setTextStrikeout(boolean bSelected) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setStrikeout(bSelected);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextUnderline(boolean bSelected) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setUnderline(bSelected);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextItalic(boolean bSelected) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setItalic(bSelected);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextBold(boolean bSelected) {
        try {
            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setBold(bSelected);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置字体
    private void setTextFont(String font) {
        try {
            textFont = TextStyleProcess.getFontName(font, 2);
            menuProgress[0] = "" + textFont;
            progressText.setText(menuProgress[0]);
            scrollerMenu.setMenuProgress(menuProgress);

            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setFontName(TextStyleProcess.getFontName(font, 2));
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextFontPosition(String textAlignment) {

        try {
            textPosition = TextStyleProcess.getTextAlignment(TextStyleProcess.getTextAlignment(textAlignment));
            menuProgress[4] = "" + textPosition;
            progressText.setText(menuProgress[4]);
            scrollerMenu.setMenuProgress(menuProgress);


            Geometry geometry = m_TrackingLayer.get(0);
            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setAlignment(TextStyleProcess.getTextAlignment(textAlignment));
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextFontColor(int position) {
        try {
            String color = mColorInfos.get(position).getColor();

            textColor = color;
            menuProgress[2] = "" + textColor;
            progressText.setText(menuProgress[2]);
            scrollerMenu.setMenuProgress(menuProgress);

            Geometry geometry = m_TrackingLayer.get(0);

            int parseColor = Color.parseColor(color);
            int[] rgb = getRGB(parseColor);
            com.supermap.data.Color makerColor = new com.supermap.data.Color(rgb[0], rgb[1], rgb[2]);

            GeoText plGeoText = (GeoText) geometry;
            TextStyle textStyle = plGeoText.getTextStyle();
            textStyle.setForeColor(makerColor);
            textStyle.setSizeFixed(true);
            plGeoText.setTextStyle(textStyle);

            addGeometryToTrackLayer(geometry);
            refreshDataset(plGeoText);

            mapView.getMapControl().getMap().refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshDataset(Geometry geometry) {
        m_RecordSet.edit();
        m_RecordSet.setGeometry(geometry);
        m_RecordSet.update();
    }

    private void addGeometryToTrackLayer(Geometry geometry) {
        m_TrackingLayer.clear();
        m_TrackingLayer.add(geometry, "newText");

    }


    private int[] getRGB(int color) {
        int[] rgb = new int[3];

        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = color & 0xff;

        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;

        return rgb;
    }

    private CustomData[] mPositionCustomData = new CustomData[]{
            new CustomData(false, "左上角", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "中上点", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "右上角", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "左基线", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "中心基线", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "右基线", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "左下角", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "中下点", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "右下角", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "左中点", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "中心点", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "右中点", R.drawable.ic_fo_vintage2_style_1_default)
    };

    private void setupCustomColorLists() {
        mColorInfos.add(new ColorInfo(false, "#000000"));
        mColorInfos.add(new ColorInfo(false, "#424242"));
        mColorInfos.add(new ColorInfo(false, "#757575"));
        mColorInfos.add(new ColorInfo(false, "#BDBDBD"));
        mColorInfos.add(new ColorInfo(false, "#EEEEEE"));
        mColorInfos.add(new ColorInfo(false, "#FFFFFF"));
        mColorInfos.add(new ColorInfo(false, "#3E2723"));
        mColorInfos.add(new ColorInfo(false, "#5D4037"));
        mColorInfos.add(new ColorInfo(false, "#A1887F"));
        mColorInfos.add(new ColorInfo(false, "#D7CCC8"));
        mColorInfos.add(new ColorInfo(false, "#263238"));
        mColorInfos.add(new ColorInfo(false, "#546E7A"));
        mColorInfos.add(new ColorInfo(false, "#90A4AE"));
        mColorInfos.add(new ColorInfo(false, "#CFD8DC"));
        mColorInfos.add(new ColorInfo(false, "#FFECB3"));
        mColorInfos.add(new ColorInfo(false, "#FFF9C4"));
        mColorInfos.add(new ColorInfo(false, "#F1F8E9"));
        mColorInfos.add(new ColorInfo(false, "#E3F2FD"));
        mColorInfos.add(new ColorInfo(false, "#EDE7F6"));
        mColorInfos.add(new ColorInfo(false, "#FCE4EC"));
        mColorInfos.add(new ColorInfo(false, "#FBE9E7"));
        mColorInfos.add(new ColorInfo(false, "#004D40"));
        mColorInfos.add(new ColorInfo(false, "#006064"));
        mColorInfos.add(new ColorInfo(false, "#009688"));
        mColorInfos.add(new ColorInfo(false, "#8BC34A"));
        mColorInfos.add(new ColorInfo(false, "#A5D6A7"));
        mColorInfos.add(new ColorInfo(false, "#80CBC4"));
        mColorInfos.add(new ColorInfo(false, "#80DEEA"));
        mColorInfos.add(new ColorInfo(false, "#A1C2FA"));
        mColorInfos.add(new ColorInfo(false, "#9FA8DA"));
        mColorInfos.add(new ColorInfo(false, "#01579B"));
        mColorInfos.add(new ColorInfo(false, "#1A237E"));
        mColorInfos.add(new ColorInfo(false, "#3F51B5"));
        mColorInfos.add(new ColorInfo(false, "#03A9F4"));
        mColorInfos.add(new ColorInfo(false, "#4A148C"));
        mColorInfos.add(new ColorInfo(false, "#673AB7"));
        mColorInfos.add(new ColorInfo(false, "#9C27B0"));
        mColorInfos.add(new ColorInfo(false, "#880E4F"));
        mColorInfos.add(new ColorInfo(false, "#E91E63"));
        mColorInfos.add(new ColorInfo(false, "#F44336"));
        mColorInfos.add(new ColorInfo(false, "#F48FB1"));
        mColorInfos.add(new ColorInfo(false, "#EF9A9A"));
        mColorInfos.add(new ColorInfo(false, "#F57F17"));
        mColorInfos.add(new ColorInfo(false, "#F4B400"));
        mColorInfos.add(new ColorInfo(false, "#FADA80"));
        mColorInfos.add(new ColorInfo(false, "#FFF59D"));
        mColorInfos.add(new ColorInfo(false, "#FFEB3B"));
    }

    private CustomData[] mFontCustomData = new CustomData[]{
            new CustomData(false, "黑体", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "宋体", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "微软雅黑", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "仿宋", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "幼圆", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "华文新魏", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "华文中宋", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "新罗马", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "楷体", R.drawable.ic_fo_vintage2_style_1_default)

    };


    private CustomData[] mStyleCustomData = new CustomData[]{
            new CustomData(false, "加粗", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "斜体", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "下划线", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "删除线", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "轮廓", R.drawable.ic_fo_vintage2_style_1_default),
            new CustomData(false, "阴影", R.drawable.ic_fo_vintage2_style_1_default)
    };
    //编辑时在跟踪层上进行设置，等保存的时候实时更新


}
