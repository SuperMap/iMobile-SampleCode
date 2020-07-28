package com.supermap.imobile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supermap.analyst.TopologyProcessing;
import com.supermap.analyst.TopologyProcessingOptions;
import com.supermap.analyst.networkanalyst.NetworkBuilder;
import com.supermap.analyst.networkanalyst.NetworkSplitMode;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.EncodeType;
import com.supermap.imobile.naviintegration.MainActivity;
import com.supermap.imobile.naviintegration.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;

/**
 * 增量路网界面
 */
public class NetworkBuildFragment extends Fragment implements View.OnClickListener{

    private MapControl mMapControl;

    MainActivity activity_main = null;
    private String mEditlayerName = null;
    boolean bUpdate = false;
    private Datasource mDatasource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_updatenet, container, false);
        layout.findViewById(R.id.btn_addline).setOnClickListener(this);
        layout.findViewById(R.id.btn_submitline).setOnClickListener(this);
        layout.findViewById(R.id.btn_autobuildNetWork).setOnClickListener(this);
        layout.findViewById(R.id.btn_cancel).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mMapControl.setAction(Action.PAN);
        if(!hidden){
            mMapControl.getMap().getLayers().add(mMapControl.getMap().getWorkspace().getDatasources().get("supermap").
                    getDatasets().get("T1_ROAD_INFO"),true);
            mEditlayerName = mMapControl.getMap().getLayers().get(0).getName();
            mMapControl.getMap().getLayers().get(0).setEditable(true);
            bUpdate = false;
        }else{
            if(bUpdate){
                mMapControl.getMap().getLayers().remove(0);
            }
            mMapControl.getMap().getLayers().remove(mEditlayerName);
            mEditlayerName = null;
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity_main = (MainActivity) activity;
        mMapControl = activity_main.getMapControl();
        mDatasource = activity_main.getDatasource();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_addline:
                mMapControl.setAction(Action.DRAWLINE);
                break;
            case R.id.btn_cancel:
                mMapControl.setAction(Action.PAN);
                break;
            case R.id.btn_submitline:
                mMapControl.submit();
//                mMapControl.setAction(Action.DRAWLINE);
                break;
            case R.id.btn_autobuildNetWork:
                mMapControl.submit();
                buildNetwork();
                mMapControl.setAction(Action.PAN);
                break;

        }
    }
    private void buildNetwork() {

        for(int i = 0; i < mMapControl.getMap().getLayers().getCount(); i++){
            String strName = mMapControl.getMap().getLayers().get(i).getName();
        }
        if(bUpdate){
            mMapControl.getMap().getLayers().remove(0);
        }

        DatasetVector lineDataset;
        //路网生成
        lineDataset= (DatasetVector)mDatasource.getDatasets().get("T1_ROAD_INFO");

        //线线打断功能
        mDatasource.getDatasets().delete("F7_tmpDataset");
        DatasetVector datasetVector2 = (DatasetVector) mDatasource.copyDataset(
                lineDataset, "F7_tmpDataset", EncodeType.NONE);
        // 构造拓扑处理选项topologyProcessingOptions，各属性设置成false
        TopologyProcessingOptions topologyProcessingOptions = new TopologyProcessingOptions();
        topologyProcessingOptions.setLinesIntersected(true);
        TopologyProcessing.clean(datasetVector2,topologyProcessingOptions);

        mDatasource.getDatasets().delete("T1_Network_adjust");

        String[] lineFieldNames = new String[datasetVector2.getFieldInfos().getCount()];
        for(int i = 0;i < datasetVector2.getFieldInfos().getCount();i++){
            lineFieldNames[i] = datasetVector2.getFieldInfos().get(i).getCaption();
        }

        DatasetVector datasets[] = {datasetVector2};
        DatasetVector resultDataset = NetworkBuilder.buildNetwork(datasets,null,lineFieldNames,null,
                mDatasource,"T1_Network_adjust", NetworkSplitMode.LINE_SPLIT_BY_POINT,0.0000001);

        DatasetVector datasetVector = (DatasetVector) mDatasource.getDatasets().get("T1_Network_adjust");
        mMapControl.getMap().getLayers().add(datasetVector.getChildDataset(),true);

        bUpdate = true;

    }
}
