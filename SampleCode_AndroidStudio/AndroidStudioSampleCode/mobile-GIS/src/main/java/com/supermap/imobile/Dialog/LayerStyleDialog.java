package com.supermap.imobile.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.supermap.data.Color;
import com.supermap.data.DatasetType;
import com.supermap.data.GeoStyle;
import com.supermap.data.Size2D;
import com.supermap.imobile.adapter.LayerStyleDialogAdapter;
import com.supermap.imobile.bean.MyLayerData;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.MapControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 图层风格设置对话框，只有点线面图层可设置其风格
 */
public class LayerStyleDialog extends Dialog implements View.OnClickListener {

    Context mContext = null;
    MyLayerData layerData = null;
    MapControl mMapControl = null;

    Layer layer = null;
    DatasetType mDatasetType = null;//数据类型

    Spinner mForceColorSpinner = null;//前景色
    Spinner mBackColorSpinner = null;//背景色
    Spinner mLineColorSpinner = null;//线颜色
    Spinner mLineWidthSpinner = null;//线宽度
    Spinner mSymbolWidthSpinner = null;//符号宽度
    Spinner mSymbolHeightSpinner = null;//符号高度
    Spinner mSymbolColorSpinner = null;//符号颜色

    LinearLayout layout_ForceColor = null;
    LinearLayout layout_BackColor = null;
    LinearLayout layout_LineColor = null;
    LinearLayout layout_LineWidth = null;
    LinearLayout layout_SymbolWidth = null;
    LinearLayout layout_SymbolHeight = null;
    LinearLayout layout_SymbolColor = null;

    Button btn_cancle = null;
    Button btn_confirm = null;

    LayerStyleDialogAdapter adapterForceColor = null;//前景色
    private List<String> listForceColor = new ArrayList<String>();

    LayerStyleDialogAdapter adapterBackColor = null;//背景色
    private List<String> listBackColor = new ArrayList<String>();

    LayerStyleDialogAdapter adapterLineWidth = null;//线宽度
    private List<String> listLineWidth = new ArrayList<String>();

    LayerStyleDialogAdapter adapterLineColor = null;//线颜色
    private List<String> listLineColor = new ArrayList<String>();

    LayerStyleDialogAdapter adapterSymbolWidth = null;//符号宽度
    private List<String> listSymbolWidth = new ArrayList<String>();

    LayerStyleDialogAdapter adapterSymbolHeight = null;//符号高度
    private List<String> listSymbolHeight = new ArrayList<String>();

    LayerStyleDialogAdapter adapterSymbolColor = null;//符号颜色
    private List<String> listSymbolColor = new ArrayList<String>();

    private String mForceColor = null;//前景色
    private String mBackColor = null;//背景色
    private String mLineColor = null;//线颜色
    private String mLineWidth = null;//线宽度
    private String mSymbolWidth = null;//符号宽度
    private String mSymbolHeight = null;//符号高度
    private String mSymbolColor = null;//符号颜色

    /**
     * @param mMapControl
     * @param themeResId
     * @param layerData
     */
    public LayerStyleDialog(MapControl mMapControl, int themeResId, MyLayerData layerData) {
        super(mMapControl.getContext(), themeResId);
        this.setContentView(R.layout.dialog_layerstyle_region);
        this.mContext = mMapControl.getContext();
        this.layerData = layerData;
        this.mMapControl = mMapControl;

        initData();//注册数据

        initView();//初始界面

        readLayerProperty();//读取当前图层对象

        initListener();//注册监听
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_layerstyle_cancle:
                this.dismiss();
                break;
            case R.id.btn_layerstyle_confirm:
                saveLayerSetting();
                this.dismiss();
                break;
        }

    }

    /**
     * 初始化界面
     */
    private void initView() {
        mForceColorSpinner = (Spinner) findViewById(R.id.sp_forcecolor);
        mBackColorSpinner = (Spinner) findViewById(R.id.sp_backcolor);
        mLineColorSpinner = (Spinner) findViewById(R.id.sp_line_color);
        mLineWidthSpinner = (Spinner) findViewById(R.id.sp_line_width);
        mSymbolWidthSpinner = (Spinner) findViewById(R.id.sp_symbol_width);
        mSymbolHeightSpinner = (Spinner) findViewById(R.id.sp_symbol_height);
        mSymbolColorSpinner = (Spinner) findViewById(R.id.sp_symbol_color);

        layout_ForceColor = (LinearLayout) findViewById(R.id.layou_forcecolor);
        layout_BackColor = (LinearLayout) findViewById(R.id.layou_backcolor);
        layout_LineColor = (LinearLayout) findViewById(R.id.layou_line_color);
        layout_LineWidth = (LinearLayout) findViewById(R.id.layou_line_width);
        layout_SymbolWidth = (LinearLayout) findViewById(R.id.layou_symbol_width);
        layout_SymbolHeight = (LinearLayout) findViewById(R.id.layou_symbol_height);
        layout_SymbolColor = (LinearLayout) findViewById(R.id.layou_symbol_color);

        btn_cancle = (Button) findViewById(R.id.btn_layerstyle_cancle);
        btn_confirm = (Button) findViewById(R.id.btn_layerstyle_confirm);

        btn_cancle.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        adapterForceColor = new LayerStyleDialogAdapter(listForceColor, mContext);
        mForceColorSpinner.setAdapter(adapterForceColor);

        adapterBackColor = new LayerStyleDialogAdapter(listBackColor, mContext);
        mBackColorSpinner.setAdapter(adapterBackColor);

        adapterLineColor = new LayerStyleDialogAdapter(listLineColor, mContext);
        mLineColorSpinner.setAdapter(adapterLineColor);

        adapterLineWidth = new LayerStyleDialogAdapter(listLineWidth, mContext);
        mLineWidthSpinner.setAdapter(adapterLineWidth);

        adapterSymbolWidth = new LayerStyleDialogAdapter(listSymbolWidth, mContext);
        mSymbolWidthSpinner.setAdapter(adapterSymbolWidth);

        adapterSymbolHeight = new LayerStyleDialogAdapter(listSymbolHeight, mContext);
        mSymbolHeightSpinner.setAdapter(adapterSymbolHeight);

        adapterSymbolColor = new LayerStyleDialogAdapter(listSymbolColor, mContext);
        mSymbolColorSpinner.setAdapter(adapterSymbolColor);
    }

    private void initData() {
        //前景色
        listForceColor.add("#FF000000");
        listForceColor.add("#FF4F81BD");
        listForceColor.add("#FFC00000");
        listForceColor.add("#FFD99694");
        listForceColor.add("#FFFFF45B");
        listForceColor.add("#FF0070C0");
        listForceColor.add("#FFEEF1A0");
        listForceColor.add("#FF494429");
        listForceColor.add("#FF70A800");
        listForceColor.add("#FFE36C09");

        //背景色
        listBackColor.add("#FF000000");
        listBackColor.add("#FF4F81BD");
        listBackColor.add("#FFC00000");
        listBackColor.add("#FFD99694");
        listBackColor.add("#FFFFF45B");
        listBackColor.add("#FF0070C0");
        listBackColor.add("#FFEEF1A0");
        listBackColor.add("#FF494429");
        listBackColor.add("#FF70A800");
        listBackColor.add("#FFE36C09");

        //线颜色
        listLineColor.add("#FF000000");
        listLineColor.add("#FF4F81BD");
        listLineColor.add("#FFC00000");
        listLineColor.add("#FFD99694");
        listLineColor.add("#FFFFF45B");
        listLineColor.add("#FF0070C0");
        listLineColor.add("#FFEEF1A0");
        listLineColor.add("#FF494429");
        listLineColor.add("#FF70A800");
        listLineColor.add("#FFE36C09");

        //线宽度
        listLineWidth.add("0.1");
        listLineWidth.add("0.3");
        listLineWidth.add("0.5");
        listLineWidth.add("0.8");
        listLineWidth.add("1.0");
        listLineWidth.add("1.5");
        listLineWidth.add("2.0");
        listLineWidth.add("2.5");
        listLineWidth.add("3.0");
        listLineWidth.add("3.5");

        //符号宽度
        listSymbolWidth.add("1.0");
        listSymbolWidth.add("1.5");
        listSymbolWidth.add("2.0");
        listSymbolWidth.add("2.5");
        listSymbolWidth.add("3.0");
        listSymbolWidth.add("3.5");
        listSymbolWidth.add("4.0");
        listSymbolWidth.add("4.5");
        listSymbolWidth.add("5.0");
        listSymbolWidth.add("5.5");
        listSymbolWidth.add("6.0");

        //符号高度
        listSymbolHeight.add("1.0");
        listSymbolHeight.add("1.5");
        listSymbolHeight.add("2.0");
        listSymbolHeight.add("2.5");
        listSymbolHeight.add("3.0");
        listSymbolHeight.add("3.5");
        listSymbolHeight.add("4.0");
        listSymbolHeight.add("4.5");
        listSymbolHeight.add("5.0");
        listSymbolHeight.add("5.5");
        listSymbolHeight.add("6.0");


        //符号颜色
        listSymbolColor.add("#FF000000");
        listSymbolColor.add("#FF4F81BD");
        listSymbolColor.add("#FFC00000");
        listSymbolColor.add("#FFD99694");
        listSymbolColor.add("#FFFFF45B");
        listSymbolColor.add("#FF0070C0");
        listSymbolColor.add("#FFEEF1A0");
        listSymbolColor.add("#FF494429");
        listSymbolColor.add("#FF70A800");
        listSymbolColor.add("#FFE36C09");
    }

    /**
     * 读取图层属性数据, 根据选择的图层类型设置可用的属性
     */
    private void readLayerProperty() {
        layer = layerData.getLayer();
        mDatasetType = layer.getDataset().getType();
        if (mDatasetType.equals(DatasetType.POINT)) {
            layout_ForceColor.setVisibility(View.GONE);
            layout_BackColor.setVisibility(View.GONE);
            layout_LineWidth.setVisibility(View.GONE);
            layout_LineColor.setVisibility(View.GONE);
        } else if (mDatasetType.equals(DatasetType.LINE)) {
            layout_ForceColor.setVisibility(View.GONE);
            layout_BackColor.setVisibility(View.GONE);
            layout_SymbolWidth.setVisibility(View.GONE);
            layout_SymbolHeight.setVisibility(View.GONE);
            layout_SymbolColor.setVisibility(View.GONE);

        } else if (mDatasetType.equals(DatasetType.REGION)) {
            layout_SymbolWidth.setVisibility(View.GONE);
            layout_SymbolHeight.setVisibility(View.GONE);
            layout_SymbolColor.setVisibility(View.GONE);
        }
    }

    //符号高度与符号宽度应该是联动一致
    private void initListener() {

        mSymbolWidthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSymbolHeightSpinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSymbolHeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSymbolWidthSpinner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 保存设置
     */
    private void saveLayerSetting() {
        getSpinerData();//读取spiner控件数据

        LayerSettingVector layerSettingVector = (LayerSettingVector) layer.getAdditionalSetting();

        GeoStyle geoStyle = new GeoStyle();

        String strlineColorString = null;
        //根据不同数据类型，设置图层风格
        //点
        if (mDatasetType.equals(DatasetType.POINT)) {
            geoStyle.setMarkerSize(new Size2D(Double.valueOf(mSymbolWidth), Double.valueOf(mSymbolHeight)));//设置符号大小
            strlineColorString = mSymbolColor.substring(3);
            geoStyle.setPointColor(new Color(Integer.parseInt(strlineColorString.substring(0, 2), 16),//设置符号颜色
                    Integer.parseInt(strlineColorString.substring(2, 4), 16),
                    Integer.parseInt(strlineColorString.substring(4), 16)));
            layerSettingVector.setStyle(geoStyle);
            layer.setAdditionalSetting(layerSettingVector);
            geoStyle.dispose();
        }
        //线
        else if (mDatasetType.equals(DatasetType.LINE)) {
            geoStyle.setLineWidth(Double.valueOf(mLineWidth));//设置线宽
            strlineColorString = mLineColor.substring(3);
            geoStyle.setLineColor(new Color(Integer.parseInt(strlineColorString.substring(0, 2), 16),
                    Integer.parseInt(strlineColorString.substring(2, 4), 16),
                    Integer.parseInt(strlineColorString.substring(4), 16)));//设置线符号颜色
            layerSettingVector.setStyle(geoStyle);
            layer.setAdditionalSetting(layerSettingVector);
            geoStyle.dispose();
        }
        //面
        else if (mDatasetType.equals(DatasetType.REGION)) {
            geoStyle.setLineWidth(Double.valueOf(mLineWidth));//设置线宽
            strlineColorString = mLineColor.substring(3);
            //设置线符号颜色
            geoStyle.setLineColor(new Color(Integer.parseInt(strlineColorString.substring(0, 2), 16),
                    Integer.parseInt(strlineColorString.substring(2, 4), 16),
                    Integer.parseInt(strlineColorString.substring(4), 16)));
            strlineColorString = mForceColor.substring(3);
            //设置前景色
            geoStyle.setFillForeColor(new Color(Integer.parseInt(strlineColorString.substring(0, 2), 16),
                    Integer.parseInt(strlineColorString.substring(2, 4), 16),
                    Integer.parseInt(strlineColorString.substring(4), 16)));
            strlineColorString = mBackColor.substring(3);
            //设置背景色
            geoStyle.setFillBackColor(new Color(Integer.parseInt(strlineColorString.substring(0, 2), 16),
                    Integer.parseInt(strlineColorString.substring(2, 4), 16),
                    Integer.parseInt(strlineColorString.substring(4), 16)));
            layerSettingVector.setStyle(geoStyle);
            layer.setAdditionalSetting(layerSettingVector);
            geoStyle.dispose();
        }
        mMapControl.getMap().refresh();

    }

    /**
     * //读取spiner控件数据
     */
    private void getSpinerData() {
        //获取当前点击位置
        int indexForceColor = mForceColorSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mForceColor = listForceColor.get(indexForceColor);

        //获取当前点击位置
        int indexBackColor = mBackColorSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mBackColor = listBackColor.get(indexBackColor);

        //获取当前点击位置
        int indexLineColor = mLineColorSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mLineColor = listLineColor.get(indexLineColor);

        //获取当前点击位置
        int indexLineWidth = mLineWidthSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mLineWidth = listLineWidth.get(indexLineWidth);

        //获取当前点击位置
        int indexSymbolWidth = mSymbolWidthSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mSymbolWidth = listSymbolWidth.get(indexSymbolWidth);

        //获取当前点击位置
        int indexSymbolHeight = mSymbolHeightSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mSymbolHeight = listSymbolHeight.get(indexSymbolHeight);

        //获取当前点击位置
        int indexSymbolColor = mSymbolColorSpinner.getSelectedItemPosition();
        //获取当前点击位置对应颜色值
        mSymbolColor = listSymbolColor.get(indexSymbolColor);
    }
}
