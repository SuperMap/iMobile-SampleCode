package com.supermap.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.supermap.Adapter.ImageAdapter;
import com.supermap.Dialog.AnimationSettingDialog;
import com.supermap.Util.FileScaner;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasets;
import com.supermap.data.Geometry;
import com.supermap.data.Recordset;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.plot.AnimationDefine;
import com.supermap.plot.AnimationGO;
import com.supermap.plot.AnimationGroup;
import com.supermap.plot.AnimationGrow;
import com.supermap.plot.AnimationManager;
import com.supermap.plot.GeoGraphicObject;
import com.supermap.plotanimation.MainActivity;
import com.supermap.plotanimation.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ValidFragment")
public class CreatAnimationFragment extends Fragment implements View.OnClickListener {
    //
    MapControl mapControl;
    Layer PlotLayer;
    //
    View rootView;
    //
    Button btn_createPlot;
    Button btn_edit;
    Button btn_cancle;
    Button btn_submit;
    Button btn_createanimation;
    Button btn_playanimation;
    Button btn_pause;
    Button btn_reset;
    Button btn_stop;
    //
    Button btn_creatPointPlot;
    Button btn_creatLinePlot;
    //
    Button btn_editplot;
    Button btn_deletplot;
    //
    GridView symbolgridview;
    //
    List<? extends Map<String, String>> list_point;
    List<?extends Map<String ,String>> list_line;
    //
    ImageAdapter adapter_point;
    ImageAdapter adapter_line;
    //
    private long libID = -1;
    public long libID_JB = -1;
    public long libID_TY = -1;
    //
    LinearLayout layou_createplot;
    LinearLayout layou_editplot;
    //
    private boolean isDeleteClicked = false;
    private boolean isCreateAnimation = false;
    //
    private AlertDialog.Builder deleteDialog = null;
    //
    public List<AnimationGO> animationlist =new ArrayList<AnimationGO>();
    //
    AnimationGroup animationGroup=null;
    //
    Timer timer;

    public CreatAnimationFragment(MapControl mapControl,long a,long b){
        this.mapControl=mapControl;
        this.libID_JB=a;
        this.libID_TY=b;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_creatanimation, container, false);
        initView();
        initPlotData();
        createPlotLayer();
        initAlertDialog();
//        animationGroup= AnimationManager.getInstance().addAnimationGroup("PlotLayer");
        return rootView;
    }

    private void initView() {
        btn_createPlot=(Button)rootView.findViewById(R.id.btn_createPlot);
        btn_edit=(Button)rootView.findViewById(R.id.btn_edit);
        btn_cancle=(Button)rootView.findViewById(R.id.btn_cancle);
        btn_submit=(Button)rootView.findViewById(R.id.btn_submit);
        btn_createanimation=(Button)rootView.findViewById(R.id.btn_createanimation);
        btn_playanimation=(Button)rootView.findViewById(R.id.btn_playanimation);
        btn_pause=(Button)rootView.findViewById(R.id.btn_pause);
        btn_reset=(Button)rootView.findViewById(R.id.btn_reset);
        btn_stop=(Button)rootView.findViewById(R.id.btn_stop);
        btn_creatPointPlot=(Button)rootView.findViewById(R.id.btn_creatPointPlot);
        btn_creatLinePlot=(Button)rootView.findViewById(R.id.btn_creatLinePlot);
        btn_editplot=(Button)rootView.findViewById(R.id.btn_editplot);
        btn_deletplot=(Button)rootView.findViewById(R.id.btn_deletplot);
        symbolgridview=(GridView) rootView.findViewById(R.id.symbolgridview);
        layou_createplot=(LinearLayout) rootView.findViewById(R.id.layou_createplot);
        layou_editplot=(LinearLayout) rootView.findViewById(R.id.layou_editplot);

        //
        btn_createPlot.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_createanimation.setOnClickListener(this);
        btn_playanimation.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_creatPointPlot.setOnClickListener(this);
        btn_creatLinePlot.setOnClickListener(this);
        btn_editplot.setOnClickListener(this);
        btn_deletplot.setOnClickListener(this);
        symbolgridview.setOnItemClickListener(itemClickListener);
        //
        mapControl.addGeometrySelectedListener(geoSelectedListener);
    }
    //
    private void initPlotData(){
        String pathSymbolIconJB = MainActivity.RootPath + "SampleData/Fujian/SymbolIcon/警用标号";
        String pathSymbolIconTY = MainActivity.RootPath + "SampleData/Fujian/SymbolIcon/常用标号";
        //
        FileScaner fileScaner=new FileScaner();
        //
        list_point=fileScaner.getPictureList(pathSymbolIconJB);
        list_line=fileScaner.getPictureList(pathSymbolIconTY);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_createPlot:
                symbolgridview.setVisibility(View.GONE);
                layou_editplot.setVisibility(View.GONE);
                if (layou_createplot.getVisibility()==View.GONE){
                    layou_createplot.setVisibility(View.VISIBLE);
                }
                else {
                    layou_createplot.setVisibility(View.GONE);
                }

                break;
            case R.id.btn_edit:
                symbolgridview.setVisibility(View.GONE);
                layou_createplot.setVisibility(View.GONE);
                if (layou_editplot.getVisibility()==View.GONE){
                    layou_editplot.setVisibility(View.VISIBLE);
                }
                else {
                    layou_editplot.setVisibility(View.GONE);
                }
                    symbolgridview.setVisibility(View.GONE);
                break;
            case R.id.btn_cancle:
                resetView();
                mapControl.cancel();
                mapControl.submit();
                break;
            case R.id.btn_submit:
                resetView();
                if (checkSubmit()){
                     mapControl.submit();
                    mapControl.setAction(Action.PAN);
                }
                break;
            case R.id.btn_createanimation:
                resetView();
                isCreateAnimation=true;
                mapControl.setAction(Action.SELECT);
                shownToast("请选择设置动画对象");
                break;
            case R.id.btn_playanimation:
                resetView();
                playAniamtion();
                break;
            case R.id.btn_pause:
                resetView();
                AnimationManager.getInstance().pause();
                break;
            case R.id.btn_reset:
                resetView();
                AnimationManager.getInstance().reset();
                break;
            case R.id.btn_stop:
                resetView();
                stopAniamtion();
                break;
            case R.id.btn_creatPointPlot:
                libID=libID_JB;
                if (list_point!=null){
                    if (adapter_point==null){
                    adapter_point=new ImageAdapter(getActivity(),list_point);}
                    symbolgridview.setAdapter(adapter_point);
                }
                symbolgridview.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_creatLinePlot:
                libID=libID_TY;
                if (list_line!=null){
                    if (adapter_line==null){
                        adapter_line=new ImageAdapter(getActivity(),list_line);}
                    symbolgridview.setAdapter(adapter_line);
                }
                symbolgridview.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_editplot:
                mapControl.setAction(Action.SELECT);
                shownToast("请选择要编辑的对象");
                break;
            case R.id.btn_deletplot:
                isDeleteClicked=true;
                mapControl.setAction(Action.SELECT);
                shownToast("请选择要删除的对象");
                break;
        }

    }
    AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map<String,String> symbol=null;
            if (libID==libID_JB){
                symbol=list_point.get(i);
            }
            if (libID==libID_TY){
                symbol=list_line.get(i);
            }
            String name = symbol.get("name");
            int symbolCode = Integer.parseInt(name);
            if (symbolCode >= 0) {
                mapControl.setPlotSymbol(libID, symbolCode);
                mapControl.setAction(Action.CREATEPLOT);
                symbolgridview.setVisibility(View.GONE);
            }
        }
    };
    // 几何对象选择监听，当有几何对象被选择时，设置为节点编辑状态
    private GeometrySelectedListener geoSelectedListener = new GeometrySelectedListener() {

        @Override
        public void geometrySelected(GeometrySelectedEvent Event) {
            // TODO Auto-generated method stub
            mapControl.setAction(Action.VERTEXEDIT);
            if(isDeleteClicked){
                deleteDialog.create().show();       // 点击删除按钮后，再选中对象，直接弹出删除及时
                isDeleteClicked = false;
            }
            if (isCreateAnimation){
                AnimationSettingDialog dialog=new AnimationSettingDialog(mapControl,mapControl.getCurrentGeometry(),animationlist);
                dialog.addAnimationListListener(new AnimationSettingDialog.AnimationListListener() {
                    @Override
                    public void getList(List<AnimationGO> list) {
                        animationlist=list;
                    }
                });
                dialog.show();
                isCreateAnimation=false;
            }
        }

        @Override
        public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void geometryMultiSelectedCount(int i) {

        }
    };
    private void initAlertDialog() {
        deleteDialog = new AlertDialog.Builder(getActivity());
        // 设置删除提示对话框
        deleteDialog.setTitle("是否删除该符号?");
        deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mapControl.deleteCurrentGeometry();
                mapControl.submit();
                mapControl.setAction(Action.PAN);
                mapControl.getMap().refresh();
            }
        });
        deleteDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
//				dismiss();
            }
        });
    }
    private boolean checkSubmit() {
        boolean submitEnable = false;
        Geometry geometry = mapControl.getCurrentGeometry();

        if(geometry != null){
            submitEnable = true;
            geometry.dispose();
        } else {
            shownToast("没有编辑中的几何对象");
        }

        Layer layer = mapControl.getEditLayer();
        if(layer != null){
            Dataset dataset = layer.getDataset();
            DatasetType type = dataset.getType();
            if(type != DatasetType.CAD){
                shownToast("当前编辑图层不是CAD图层，无法提交标绘符号,editlayer type is " + type.toString());
            } else {
                submitEnable = true;
            }
        }
        return submitEnable;
    }
    private void playAniamtion(){
        if (timer==null){
           timer= new Timer();
           timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    AnimationManager.getInstance().excute();
                }
            },0,500);
        }


        AnimationManager.getInstance().play();
    }
    private void stopAniamtion(){
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        AnimationManager.getInstance().stop();
    }
    private void createPlotLayer(){
        Datasets datasets=mapControl.getMap().getWorkspace().getDatasources().get(0).getDatasets();
        String LayerName="PlotLayer";
        if (datasets.contains(LayerName)){
            datasets.delete(LayerName);
        }
        DatasetVectorInfo info=new DatasetVectorInfo();
        info.setName(LayerName);
        info.setType(DatasetType.CAD);
        Dataset dataset=datasets.create(info);
        PlotLayer=(Layer) mapControl.getMap().getLayers().add(dataset,true);
        PlotLayer.setSelectable(true);
        PlotLayer.setEditable(true);

    }
    private void shownToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
    private void resetView(){
        layou_createplot.setVisibility(View.GONE);
        layou_editplot.setVisibility(View.GONE);
        symbolgridview.setVisibility(View.GONE);
    }
}
