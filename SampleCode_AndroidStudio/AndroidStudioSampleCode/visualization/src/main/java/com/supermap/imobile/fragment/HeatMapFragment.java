package com.supermap.imobile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Workspace;
import com.supermap.imobile.visualization.MainActivity;
import com.supermap.imobile.visualization.R;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerHeatmap;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

/**
 * 热力图
 */
public class HeatMapFragment extends Fragment implements View.OnClickListener {

    private MapControl mMapControl;
    private Button mBtnShow;

    boolean isShow = true;

    private LayerHeatmap m_layerHeatMaps;
    private MainActivity activity_main;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dotdensity, container, false);//定位加载界面

        mBtnShow = (Button) rootView.findViewById(R.id.btn_show);
        mBtnShow.setOnClickListener(this);
        addHeatMapLayer(true);
        return rootView;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_show:
                if(isShow){
                    isShow = false;
                    mBtnShow.setText("显示专题图");
                    if(m_layerHeatMaps != null){
                        m_layerHeatMaps.setVisible(false);
                        mMapControl.getMap().refresh();
                    }
                }else{
                    isShow = true;
                    mBtnShow.setText("隐藏专题图");
                    if(m_layerHeatMaps != null){
                        m_layerHeatMaps.setVisible(true);
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
            m_layerHeatMaps.setVisible(false);
            mMapControl.getMap().refresh();
        }else{
            m_layerHeatMaps.setVisible(true);
            isShow = true;
            mBtnShow.setText("隐藏专题图");
            mMapControl.getMap().refresh();
        }
    }
    public void addHeatMapLayer(boolean value) {

        if (m_layerHeatMaps == null) {
            try {
                m_layerHeatMaps = mMapControl.getMap().getLayers().addHeatmap(m_pointdataset,70,
                        new com.supermap.data.Color(255,80,80,255), new com.supermap.data.Color(0,0,255,20));
                m_layerHeatMaps.setFuzzyDegree(0.8);
                m_layerHeatMaps.setIntensity(0.3);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }
        // 设置图层是否可显示，刷新地图，并设置图例面板
        mMapControl.getMap().refresh();

    }
}
