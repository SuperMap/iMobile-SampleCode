package com.supermap.imobile.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.supermap.data.Color;
import com.supermap.data.Colors;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.imobile.visualization.MainActivity;
import com.supermap.imobile.visualization.R;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerGridAggregation;
import com.supermap.mapping.LayerGridAggregationType;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;

/**
 * 网格图
 */
public class GridAggregationFragment extends Fragment implements View.OnClickListener {

    MapControl mMapControl;
    Layers layers;//图层集合
    private LayerGridAggregation m_layerGridAggregation;
    private MainActivity activity_main;
    boolean isShow = true;
    Button mBtnShow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dotdensity, container, false);
        mBtnShow = (Button) rootView.findViewById(R.id.btn_show);
        mBtnShow.setOnClickListener(this);
        addGridAggregationLayer(true);
        return rootView;
    }
    private Dataset m_pointdataset;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        activity_main = (MainActivity)activity;
        mMapControl = activity_main.mMapControl;
        m_pointdataset = (DatasetVector) mMapControl.getMap().getWorkspace().getDatasources().get(0)
                .getDatasets().get("BaseMap_p");

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_show:
                if(isShow){
                    isShow = false;
                    mBtnShow.setText("显示专题图");
                    if(m_layerGridAggregation != null){
                        m_layerGridAggregation.setVisible(false);
                        mMapControl.getMap().refresh();
                    }
                }else{
                    isShow = true;
                    mBtnShow.setText("隐藏专题图");
                    if(m_layerGridAggregation != null){
                        m_layerGridAggregation.setVisible(true);
                        mMapControl.getMap().refresh();
                    }
                }
                break;
        }
    }
    //可见性改变
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            m_layerGridAggregation.setVisible(false);
            mMapControl.getMap().refresh();
        }else{
            m_layerGridAggregation.setVisible(true);
            isShow = true;
            mBtnShow.setText("隐藏专题图");
            mMapControl.getMap().refresh();
        }
    }
    public void addGridAggregationLayer(boolean value) {

        if (m_layerGridAggregation == null) {
            try {
                m_layerGridAggregation = mMapControl.getMap().getLayers().addGridAggregation(m_pointdataset);
                Color[] colors = new Color[]{
                        new Color(0,0,255,50),
                        new Color(200,54,0,100)
                };
                m_layerGridAggregation.setColorset(Colors.makeGradient(2,colors));
    //            layerGridAggregation.setIsShowGridLabel(false);
                m_layerGridAggregation.setGridWidth(100);
                m_layerGridAggregation.setGridHeight(100);
                m_layerGridAggregation.setGridAggregationType(LayerGridAggregationType.QUADRANGLE);
                mMapControl.getMap().refresh();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }
        // 设置图层是否可显示，刷新地图，并设置图例面板
        // Set whether the layer visible, refresh the map and set the legend panel
//        m_layerThemeGraduatedDensity.setVisible(value);
        mMapControl.getMap().refresh();

    }
}
