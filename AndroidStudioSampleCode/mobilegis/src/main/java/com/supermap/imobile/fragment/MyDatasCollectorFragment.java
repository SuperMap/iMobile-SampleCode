package com.supermap.imobile.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoText;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.TextPart;
import com.supermap.data.TextStyle;
import com.supermap.imobile.Dialog.ColorPickDialog;
import com.supermap.imobile.Pop.AttributePop;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometryAddedListener;
import com.supermap.mapping.GeometryEvent;
import com.supermap.mapping.GeometryIsSelectedListener;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.SnapSetting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * 我的数据采集界面
 */
public class MyDatasCollectorFragment extends Fragment {

    ScrollView scrollview_draw;//绘制菜单栏
    ScrollView scrollview_edit;//编辑菜单栏
    MapControl mMapControl;
    Layers layers;//图层集合
    View btnview; //按钮点击视图
    Point Pixelpoint = null;//像素点
    String mLabeltext;//标注内容
    Button btn_colorpick;//颜色选择按钮
    int red, green, blue;
    boolean OpenSnapSetting = false;//默认捕捉是未开启状态
    LinearLayout layout_attribute = null;//属性编辑视图
    SnapSetting mSnapSetting = null;//捕捉
    boolean geometryIsSelect = false;
    boolean LabelDialogShowFlag;//设置标示符，以便于在点击标注后弹出dialog

    public MyDatasCollectorFragment() {

    }

    @SuppressLint("ValidFragment")
    public MyDatasCollectorFragment(MapControl mMapControl) {
        this.mMapControl = mMapControl;
        layers = mMapControl.getMap().getLayers();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mydatacollector, container, false);
        StartPreparDate(rootView);
        return rootView;
    }

    //数据准备
    private void StartPreparDate(final View view) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("数据加载中....");
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                initView(view);
                dialog.dismiss();
            }
        }).start();
    }

    /**
     * 初始化视图
     * @param view
     */
    private void initView(View view) {
        scrollview_draw = view.findViewById(R.id.scrollview_draw);
        scrollview_edit = view.findViewById(R.id.scrollview_edit);
        layout_attribute = view.findViewById(R.id.linear_attribute);


        view.findViewById(R.id.linear_draw).setOnClickListener(listener);
        view.findViewById(R.id.linear_edit).setOnClickListener(listener);
        view.findViewById(R.id.linear_creatPoint).setOnClickListener(listener);
        view.findViewById(R.id.linear_createline).setOnClickListener(listener);
        view.findViewById(R.id.linear_createregion).setOnClickListener(listener);
        view.findViewById(R.id.linear_commit).setOnClickListener(listener);
        view.findViewById(R.id.linear_drawline).setOnClickListener(listener);
        view.findViewById(R.id.linear_drawregion).setOnClickListener(listener);
        view.findViewById(R.id.linear_freedraw).setOnClickListener(listener);
        view.findViewById(R.id.linear_undo).setOnClickListener(listener);
        view.findViewById(R.id.linear_redo).setOnClickListener(listener);
        view.findViewById(R.id.linear_addnode).setOnClickListener(listener);
        view.findViewById(R.id.linear_editnode).setOnClickListener(listener);
        view.findViewById(R.id.linear_deletnode).setOnClickListener(listener);
        view.findViewById(R.id.linear_merge).setOnClickListener(listener);
        view.findViewById(R.id.linear_erase).setOnClickListener(listener);
        view.findViewById(R.id.linear_clip).setOnClickListener(listener);
        view.findViewById(R.id.linear_drawhollow).setOnClickListener(listener);
        view.findViewById(R.id.linear_fillhollw).setOnClickListener(listener);
        view.findViewById(R.id.linear_patchhollow).setOnClickListener(listener);
        view.findViewById(R.id.linear_select).setOnClickListener(listener);
        view.findViewById(R.id.linear_delete).setOnClickListener(listener);
        view.findViewById(R.id.linear_snap).setOnClickListener(listener);
        view.findViewById(R.id.linear_addlable).setOnClickListener(listener);
        view.findViewById(R.id.linear_pan).setOnClickListener(listener);
        view.findViewById(R.id.linear_attribute).setOnClickListener(listener);

        view.findViewById(R.id.imagebtn_draw).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_edit).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_creatPoint).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_createline).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_createregion).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_commit).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_commit).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_drawline).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_drawregion).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_freedraw).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_undo).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_redo).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_addnode).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_editnode).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_deletnode).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_merge).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_erase).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_clip).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_drawhollow).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_fillhollw).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_patchhollow).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_select).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_delete).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_snap).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_addlable).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_pan).setOnClickListener(listener);
        view.findViewById(R.id.imagebtn_attribute).setOnClickListener(listener);

        view.findViewById(R.id.tv_draw).setOnClickListener(listener);
        view.findViewById(R.id.tv_edit).setOnClickListener(listener);
        view.findViewById(R.id.tv_select).setOnClickListener(listener);
        view.findViewById(R.id.tv_delete).setOnClickListener(listener);
        view.findViewById(R.id.tv_snap).setOnClickListener(listener);
        view.findViewById(R.id.tv_pan).setOnClickListener(listener);
        view.findViewById(R.id.tv_commit).setOnClickListener(listener);


        mMapControl.addGeometrySelectedListener(geometrySelectedListener);
        mMapControl.addGeometryAddedListener(geometryAddedListener);
        mMapControl.setOnTouchListener(touchListener);
        mMapControl.addGeometryIsSelectedListener(geometryIsSelectedListener);


    }




    //当点击屏幕时，弹出标注对话框
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Pixelpoint = new Point((int) motionEvent.getX(), (int) motionEvent.getY());//获取点击的像素点坐标
            if (LabelDialogShowFlag) {
                addlableDialog();//添加标注对话框

                LabelDialogShowFlag = false;
            }
            return false;
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            btnview = view;
            switch (view.getId()) {

                case R.id.linear_draw:
                    scrollview_edit.setVisibility(View.GONE);
                    showDrawScrollView(scrollview_draw.getVisibility());
                    PAN();
                    break;
                case R.id.imagebtn_draw:
                    scrollview_edit.setVisibility(View.GONE);
                    showDrawScrollView(scrollview_draw.getVisibility());
                    PAN();
                    break;
                case R.id.tv_draw:
                    scrollview_edit.setVisibility(View.GONE);
                    showDrawScrollView(scrollview_draw.getVisibility());
                    PAN();
                    break;

                case R.id.linear_edit:
                    if (mMapControl.getCurrentGeometry() != null) {
                        scrollview_edit.setVisibility(View.VISIBLE);
                        mMapControl.setAction(Action.VERTEXEDIT);
                    } else {
                        scrollview_draw.setVisibility(View.GONE);
                        if (scrollview_edit.getVisibility() == View.VISIBLE) {
                            scrollview_edit.setVisibility(View.GONE);
                        }
                        Toast.makeText(getActivity(), "请先选择对象", Toast.LENGTH_SHORT).show();
                        mMapControl.setAction(Action.SELECT);
                    }
                    break;
                case R.id.imagebtn_edit:
                    if (mMapControl.getCurrentGeometry() != null) {
                        scrollview_edit.setVisibility(View.VISIBLE);
                        mMapControl.setAction(Action.VERTEXEDIT);
                    } else {
                        scrollview_draw.setVisibility(View.GONE);
                        if (scrollview_edit.getVisibility() == View.VISIBLE) {
                            scrollview_edit.setVisibility(View.GONE);
                        }
                        Toast.makeText(getActivity(), "请先选择对象", Toast.LENGTH_SHORT).show();
                        mMapControl.setAction(Action.SELECT);
                    }
                    break;
                case R.id.tv_edit:
                    if (mMapControl.getCurrentGeometry() != null) {
                        scrollview_edit.setVisibility(View.VISIBLE);
                        mMapControl.setAction(Action.VERTEXEDIT);
                    } else {
                        scrollview_draw.setVisibility(View.GONE);
                        if (scrollview_edit.getVisibility() == View.VISIBLE) {
                            scrollview_edit.setVisibility(View.GONE);
                        }
                        Toast.makeText(getActivity(), "请先选择对象", Toast.LENGTH_SHORT).show();
                        mMapControl.setAction(Action.SELECT);
                    }
                    break;
                case R.id.linear_creatPoint:
                    mMapControl.setAction(Action.CREATEPOINT);
                    layers.get("UserPoint@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.imagebtn_creatPoint:
                    mMapControl.setAction(Action.CREATEPOINT);
                    layers.get("UserPoint@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.linear_createline:
                    mMapControl.setAction(Action.CREATEPOLYLINE);
                    layers.get("User_Line@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.imagebtn_createline:
                    mMapControl.setAction(Action.CREATEPOLYLINE);
                    layers.get("User_Line@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.linear_createregion:
                    mMapControl.setAction(Action.CREATEPOLYGON);
                    layers.get("UserRegion@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.imagebtn_createregion:
                    mMapControl.setAction(Action.CREATEPOLYGON);
                    layers.get("UserRegion@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.linear_commit:
                    if (isEditting()) {
                        mMapControl.submit();
                        isFlag = true;
                        arrtibutePopShow();
                        PAN();

                        scrollview_edit.setVisibility(View.GONE);

                    } else {

                    }
                    break;
                case R.id.imagebtn_commit:
                    if (isEditting()) {

                        isFlag = true;
                        mMapControl.submit();
                        arrtibutePopShow();
                        PAN();
                        scrollview_edit.setVisibility(View.GONE);

                    } else {

                    }
                    break;
                case R.id.tv_commit:
                    if (isEditting()) {

                        isFlag = true;
                        mMapControl.submit();
                        arrtibutePopShow();
                        PAN();
                        scrollview_edit.setVisibility(View.GONE);

                    } else {

                    }
                    break;

                case R.id.linear_drawline:
                    mMapControl.setAction(Action.DRAWLINE);
                    layers.get("User_Line@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.imagebtn_drawline:
                    mMapControl.setAction(Action.DRAWLINE);
                    layers.get("User_Line@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.linear_drawregion:
                    mMapControl.setAction(Action.DRAWPLOYGON);
                    layers.get("UserRegion@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.imagebtn_drawregion:
                    mMapControl.setAction(Action.DRAWPLOYGON);
                    layers.get("UserRegion@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.linear_freedraw:
                    mMapControl.setAction(Action.FREEDRAW);
                    layers.get("UserCAD@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.imagebtn_freedraw:
                    mMapControl.setAction(Action.FREEDRAW);
                    layers.get("UserCAD@Changchun").setEditable(true);
                    refreshLayer();
                    break;
                case R.id.linear_undo:
                    mMapControl.undo();
                    break;
                case R.id.imagebtn_undo:
                    mMapControl.undo();
                    break;
                case R.id.linear_redo:
                    mMapControl.redo();
                    break;
                case R.id.imagebtn_redo:
                    mMapControl.redo();
                    break;
                case R.id.linear_addnode:
                    mMapControl.setAction(Action.VERTEXADD);
                    break;
                case R.id.imagebtn_addnode:
                    mMapControl.setAction(Action.VERTEXADD);
                    break;
                case R.id.linear_editnode:
                    mMapControl.setAction(Action.VERTEXEDIT);
                    break;
                case R.id.imagebtn_editnode:
                    mMapControl.setAction(Action.VERTEXEDIT);
                    break;
                case R.id.linear_deletnode:
                    mMapControl.setAction(Action.VERTEXDELETE);
                    break;
                case R.id.imagebtn_deletnode:
                    mMapControl.setAction(Action.VERTEXDELETE);
                    break;
                case R.id.linear_merge:
                    mMapControl.setAction(Action.UNION_REGION);
                    break;
                case R.id.imagebtn_merge:
                    mMapControl.setAction(Action.UNION_REGION);
                    break;
                case R.id.linear_erase:
                    mMapControl.setAction(Action.DRAWREGION_ERASE_REGION);
                    break;
                case R.id.imagebtn_erase:
                    mMapControl.setAction(Action.DRAWREGION_ERASE_REGION);
                    break;
                case R.id.linear_clip:
                    mMapControl.setAction(Action.SPLIT_BY_LINE);
                    break;
                case R.id.imagebtn_clip:
                    mMapControl.setAction(Action.SPLIT_BY_LINE);
                    break;
                case R.id.linear_drawhollow:
                    mMapControl.setAction(Action.DRAW_HOLLOW_REGION);
                    break;
                case R.id.imagebtn_drawhollow:
                    mMapControl.setAction(Action.DRAW_HOLLOW_REGION);
                    break;
                case R.id.linear_fillhollw:
                    mMapControl.setAction(Action.FILL_HOLLOW_REGION);
                    break;
                case R.id.imagebtn_fillhollw:
                    mMapControl.setAction(Action.FILL_HOLLOW_REGION);
                    break;
                case R.id.linear_patchhollow:
                    mMapControl.setAction(Action.PATCH_HOLLOW_REGION);
                    break;
                case R.id.imagebtn_patchhollow:
                    mMapControl.setAction(Action.PATCH_HOLLOW_REGION);
                    break;
                case R.id.linear_select:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);

                    mMapControl.setAction(Action.SELECT);
                    break;
                case R.id.imagebtn_select:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);

                    mMapControl.setAction(Action.SELECT);
                    break;
                case R.id.tv_select:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);

                    mMapControl.setAction(Action.SELECT);
                    break;
                case R.id.linear_delete:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);
                    DeletDialogShow();
                    break;
                case R.id.imagebtn_delete:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);
                    DeletDialogShow();
                    break;
                case R.id.tv_delete:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);
                    DeletDialogShow();
                    break;
                case R.id.linear_snap:
                    openSnap();
                    break;
                case R.id.imagebtn_snap:
                    openSnap();
                    break;
                case R.id.tv_snap:
                    openSnap();
                    break;
                case R.id.linear_addlable:
//                    mMapControl.setOnTouchListener(touchListener);
                    LabelDialogShowFlag = true;
                    Toast.makeText(getActivity(), "请先点击屏幕需要标注处", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.imagebtn_addlable:
                    LabelDialogShowFlag = true;
                    Toast.makeText(getActivity(), "请先点击屏幕需要标注处", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.colorpick:
                    ColorPickDialog colorPickDialog = new ColorPickDialog(getActivity());
                    colorPickDialog.setPickColorListener(new ColorPickDialog.OnPickColorListener() {
                        @Override
                        public void getColor(int r, int g, int b) {
                            btn_colorpick.setBackgroundColor(Color.argb(255, r, g, b));
                        }
                    });
                    colorPickDialog.show();
                    break;
                case R.id.linear_pan:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);

                    PAN();
                    break;
                case R.id.imagebtn_pan:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);

                    PAN();
                    break;
                case R.id.tv_pan:
                    scrollview_draw.setVisibility(View.GONE);
                    scrollview_edit.setVisibility(View.GONE);

                    PAN();
                    break;
                case R.id.imagebtn_attribute:
                    attibutepopshow();
                    break;
                case R.id.linear_attribute:
                    attibutepopshow();
                    break;
            }
        }

    };


    public void attibutepopshow() {
        if (geometryIsSelect) {
            if (isEditting()){
            if (isUserLayer) {
                if (isUserLayer2) {
                    if (temprecordset != null) {
                        AttributePop pop = new AttributePop(mMapControl, getActivity(), temprecordset, mMapControl.getHeight(), mMapControl.getWidth());
                        pop.myDatasCollectorFragment = MyDatasCollectorFragment.this;
                        pop.show();
                    } else {
                        Toast.makeText(getActivity(), "当前对象未处于编辑状态", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "当前对象不能进行属性编辑", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "当前对象不能进行属性编辑", Toast.LENGTH_SHORT).show();
            }
        }else {
                Toast.makeText(getActivity(), "当前对象未处于编辑状态", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getActivity(), "当前无编辑对象", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isFlag = false;
    DatasetVector tmpeditdataset;//当前正在采集的数据集
    AttributePop attributePop;
    Recordset recordset;//当前增加的几何对象记录集

    /**
     * 属性表pop
     */
    private void arrtibutePopShow() {
        if (tmpeditdataset != null) {
            attributePop = new AttributePop(mMapControl, getActivity(), recordset, mMapControl.getHeight(), mMapControl.getWidth());
            attributePop.myDatasCollectorFragment = this;
            attributePop.show();
            isFlag = false;
            tmpeditdataset = null;
        }
    }

    /**
     * 对象添加监听
     */
    GeometryAddedListener geometryAddedListener = new GeometryAddedListener() {
        @Override
        public void geometryAdded(GeometryEvent Event) {
            //当点击提交时，才进行数据存储
            if ((mMapControl.getAction().equals(Action.CREATEPOINT)
                    || mMapControl.getAction().equals(Action.CREATEPOLYLINE)
                    || mMapControl.getAction().equals(Action.CREATEPOLYGON)
                    || mMapControl.getAction().equals(Action.DRAWLINE)
                    || mMapControl.getAction().equals(Action.DRAWPLOYGON)) && isFlag) {
                tmpeditdataset = (DatasetVector) Event.getLayer().getDataset();
                int[] a = {Event.getID()};
                recordset = tmpeditdataset.query(a, CursorType.DYNAMIC);


            }

        }
    };

   

    //捕捉
    private void openSnap() {

        if (!OpenSnapSetting) {
            mSnapSetting = new SnapSetting();
            mSnapSetting.openAll();
            mMapControl.setSnapSetting(mSnapSetting);
            Toast.makeText(getActivity(), "捕捉已开启", Toast.LENGTH_SHORT).show();
            OpenSnapSetting = true;
        } else {
            mSnapSetting.closeAll();
            mMapControl.setSnapSetting(mSnapSetting);
            mSnapSetting.dispose();//关闭捕捉则销毁，若不销毁，会出现内存泄漏导致崩溃
            mSnapSetting = null;
            Toast.makeText(getActivity(), "捕捉已关闭", Toast.LENGTH_SHORT).show();
            OpenSnapSetting = false;
        }

    }

    private void DeletDialogShow() {

        if (geometryIsSelect) {
            if (mMapControl.getCurrentGeometry() != null) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("删除当前编辑对象")
                        .setMessage("确定要删除这个对象？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mMapControl.deleteCurrentGeometry();
                                PAN();
                                geometryIsSelect = false;

                                mMapControl.getMap().refresh();
                                temprecordset=null;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            } else {
                if (isUserLayer) {
                    Toast.makeText(getActivity(), "该对象未处于编辑状态", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "当前对象不能删除", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(getActivity(), "未选择删除对象", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加标注
     */
    private void drawlable() {

        mMapControl.getMap().getLayers().get("UserText@Changchun").setEditable(true);
        //将屏幕坐标转成地图坐标
        Point2D Pixelpoint2D = mMapControl.getMap().pixelToMap(Pixelpoint);
        DatasetVector datasetVector = (DatasetVector) mMapControl.getMap().getLayers().get("UserText@Changchun").getDataset();
        Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
        recordset.moveFirst();
        //设置风格
        GeoText geoText = new GeoText();
        TextPart textPart = new TextPart();
        textPart.setText(mLabeltext);//设置内容
        textPart.setAnchorPoint(Pixelpoint2D);//设置瞄点
        TextStyle textStyle = new TextStyle();
        textStyle.setForeColor(new com.supermap.data.Color(red, green, blue));//设置前景色
        geoText.addPart(textPart);
        geoText.setTextStyle(textStyle);
        recordset.addNew(geoText);
        recordset.update();//更新记录
        textPart.dispose();
        geoText.dispose();
        recordset.close();
        recordset.dispose();
        mMapControl.getMap().refresh();


    }

    /**
     * 添加标注对话框
     */
    private void addlableDialog() {
        View SetTextView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_settext, null);
        final EditText editText = SetTextView.findViewById(R.id.edittext);
        btn_colorpick = SetTextView.findViewById(R.id.colorpick);
        btn_colorpick.setOnClickListener(listener);
        final AlertDialog alerdialog = new AlertDialog.Builder(getActivity())
                .setView(SetTextView)
                .setTitle("Please Add Your Lable")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .show();
        alerdialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLabeltext = editText.getText().toString();
                if (mLabeltext.isEmpty()) {
                    Toast.makeText(getActivity(), "标注不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    drawlable();
                    alerdialog.dismiss();
                    PAN();
                }
            }
        });
    }

    /**
     *
     * @param i
     */
    private void showDrawScrollView(int i) {
        if (i == View.GONE) {
            scrollview_draw.setVisibility(View.VISIBLE);
        } else if (i == View.VISIBLE) {
            scrollview_draw.setVisibility(View.GONE);
        }
    }

    //判断是都在编辑
    private boolean isEditting() {
        Action action = mMapControl.getAction();
        if (action.equals(Action.PAN) || action.equals(Action.NULL) || action.equals(action.SELECT)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 对象是否选中监听
     */

    GeometryIsSelectedListener geometryIsSelectedListener = new GeometryIsSelectedListener() {
        @Override
        public void geometryIsSelected(boolean b) {
            geometryIsSelect = b;
        }
    };

    /**
     * 对象选中监听
     */
    Recordset temprecordset;//当前选择对象的记录集
    boolean isUserLayer = false;//设置变量，用于区分是否是User图层
    boolean isUserLayer2 = false;//设置变量，用于区分是否是User图层
    GeometrySelectedListener geometrySelectedListener = new GeometrySelectedListener() {
        @Override
        public void geometrySelected(GeometrySelectedEvent Event) {
//            mMapControl.apPixelpointEditGeometry(Event.getGeometryID(),Event.getLayer());
            switch (btnview.getId()) { //根据点击按钮，来判断是否显示编辑二级菜单
                case R.id.linear_edit:
                    if (mMapControl.getCurrentGeometry() != null) {
                        scrollview_edit.setVisibility(View.VISIBLE);
                        mMapControl.setAction(Action.VERTEXEDIT);
                    }
                    else {
                        Toast.makeText(getActivity(),"当前对象未处于编辑状态",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imagebtn_edit:
                    if (mMapControl.getCurrentGeometry() != null) {
                        scrollview_edit.setVisibility(View.VISIBLE);
                        mMapControl.setAction(Action.VERTEXEDIT);
                    }
                    else {
                        Toast.makeText(getActivity(),"当前对象未处于编辑状态",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.tv_edit:
                    if (mMapControl.getCurrentGeometry() != null) {
                        scrollview_edit.setVisibility(View.VISIBLE);
                        mMapControl.setAction(Action.VERTEXEDIT);
                    }
                    else {
                        Toast.makeText(getActivity(),"当前对象未处于编辑状态",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.linear_select:
                    if (mMapControl.getCurrentGeometry() != null) {
                        mMapControl.setAction(Action.VERTEXEDIT);
                    }

                    break;
                case R.id.imagebtn_select:
                    if (mMapControl.getCurrentGeometry() != null) {
                        mMapControl.setAction(Action.VERTEXEDIT);
                    }

                    break;
                case R.id.tv_select:
                    if (mMapControl.getCurrentGeometry() != null) {
                    mMapControl.setAction(Action.VERTEXEDIT);
                }

                    break;
            }
            if (Event.getLayer().getCaption().equals("UserPoint@Changchun")
                    || Event.getLayer().getCaption().equals("User_Line@Changchun")
                    || Event.getLayer().getCaption().equals("UserText@Changchun")
                    || Event.getLayer().getCaption().equals("UserCAD@Changchun")
                    || Event.getLayer().getCaption().equals("UserRegion@Changchun")) {
                isUserLayer = true;

                //当打开图层为其中三个时，可查看属性表
                if (Event.getLayer().getCaption().equals("UserPoint@Changchun")
                        || Event.getLayer().getCaption().equals("User_Line@Changchun")
                        || Event.getLayer().getCaption().equals("UserRegion@Changchun")) {
                    isUserLayer2 = true;
                    if (isEditting()) {

                        int[] id = {Event.getGeometryID()};
                        temprecordset = ((DatasetVector) Event.getLayer().getDataset()).query(id, CursorType.DYNAMIC);
                    }
                } else {
                    isUserLayer2 = false;
                }
            } else {
                isUserLayer = false;
            }

        }

        @Override
        public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arrayList) {

        }
    };

    /**
     * 数据采集后回调
     */
    public static final int TAKE_PHOTO = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //拍照
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                }
                break;
            case 2://音频
                if (resultCode == RESULT_OK) {

                    InputStream inputStream = null;//输入流
                    OutputStream outputStream = null;//输出流
                    try {
                        Uri uri = data.getData();//数据地址
                        String path = getAudioFilePathFromUri(uri);//获取音频地址
                        inputStream = new FileInputStream(path);
                        outputStream = new FileOutputStream(attributePop.audiopath);
                        byte bt[] = new byte[1024];
                        int len;
                        while ((len = inputStream.read(bt)) != -1) {
                            outputStream.write(bt, 0, len);//将默认位置的音频数据，存储到指定位置
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();//释放
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (outputStream != null) {
                            try {
                                outputStream.close();//
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                break;
            case 3://视频
                if (resultCode == RESULT_OK) {

                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        Uri uri = data.getData();
                        String path = getVedioFilePathFromUri(uri);
                        inputStream = new FileInputStream(path);
                        outputStream = new FileOutputStream(attributePop.vediopath);
                        byte bt[] = new byte[1024];
                        int len;
                        while ((len = inputStream.read(bt)) != -1) {
                            outputStream.write(bt, 0, len);//将默认位置的音频数据，存储到指定位置
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 通过Uri，获取录音文件的路径（绝对路径）
     *
     * @param uri 录音文件的uri
     * @return 录音文件的路径（String）
     */
    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
//        int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        String temp = cursor.getString(index);
        cursor.close();
        return temp;
    }

    /**
     * 通过Uri，获取视频文件的路径（绝对路径）
     *
     * @param uri 视频文件的uri
     * @return 视频文件的路径（String）
     */
    private String getVedioFilePathFromUri(Uri uri) {
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        String temp = cursor.getString(index);
        cursor.close();
        return temp;
    }


    /**
     * 刷新图层，每当点击绘制时，当前编辑图层对象已经发生改变，需要同步实时刷新
     */
    public void refreshLayer() {
        if (MyLayerManageFragment.Layeradapter != null) {
            MyLayerManageFragment.Layeradapter.refresh();
        }
    }
    public void PAN(){
        mMapControl.setAction(Action.PAN);
        geometryIsSelect=false;
    }
}
