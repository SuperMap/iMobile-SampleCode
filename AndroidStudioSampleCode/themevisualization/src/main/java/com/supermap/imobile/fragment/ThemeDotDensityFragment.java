package com.supermap.imobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoStyle;
import com.supermap.data.Size2D;
import com.supermap.data.Color;
import com.supermap.imobile.visualization.MainActivity;
import com.supermap.imobile.visualization.R;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.ThemeDotDensity;
import com.supermap.mapping.view.LayerListView;

/**
 * 点密度图
 */
public class ThemeDotDensityFragment extends Fragment implements View.OnClickListener {

    private Context mContext = null;
    LayerListView layerListView = null;
    private Layer m_layerThemeDotDensity;


    private MapControl mMapControl;
    private Dataset m_dataset;

    boolean isShow = true;
    Button mBtnShow;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dotdensity, container, false);
        mBtnShow = (Button) rootView.findViewById(R.id.btn_show);
        mBtnShow.setOnClickListener(this);
        addThemeDotDensityLayer(true);
        return rootView;

    }

    //可见性改变
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            m_layerThemeDotDensity.setVisible(false);
            mMapControl.getMap().refresh();
        }else{
            m_layerThemeDotDensity.setVisible(true);
            isShow = true;
            mBtnShow.setText("隐藏专题图");
            mMapControl.getMap().refresh();
        }
    }
    MainActivity activity_main = null;
     @Override
     public void onAttach(Activity activity){
            super.onAttach(activity);
            activity_main = (MainActivity)activity;
            mMapControl = activity_main.mMapControl;
            m_dataset = (DatasetVector) mMapControl.getMap().getWorkspace().getDatasources().get(0)
                 .getDatasets().get("BaseMap_R");

     }

    @Override
    public void onClick(View view) {
         int id = view.getId();
        switch (id){
            case R.id.btn_show:
                if(isShow){
                    isShow = false;
                    mBtnShow.setText("显示专题图");
                    if(m_layerThemeDotDensity != null){
                        m_layerThemeDotDensity.setVisible(false);
                        mMapControl.getMap().refresh();
                    }
                }else{
                    isShow = true;
                    mBtnShow.setText("隐藏专题图");
                    if(m_layerThemeDotDensity != null){
                        m_layerThemeDotDensity.setVisible(true);
                        mMapControl.getMap().refresh();
                    }
                }
                break;
        }


    }
    /**
     * 设置ThemeDotDensity的属性，添加点密度专题图图层到地图
     */
    public void addThemeDotDensityLayer(boolean value) {
        if (m_layerThemeDotDensity == null) {
            try {
                ThemeDotDensity dotDensity = new ThemeDotDensity();
                dotDensity.setDotExpression("Pop_Density99");
                dotDensity.setValue(0.00030);

                GeoStyle geostyle = new GeoStyle();
                geostyle.setLineColor(new Color(255,0,0));
                geostyle.setMarkerSize(new Size2D(1, 1));

                dotDensity.setStyle(geostyle);

                // 将制作好的专题图添加到地图中显示
                m_layerThemeDotDensity = mMapControl.getMap().getLayers().add(
                        m_dataset, dotDensity, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        // 设置图层是否可显示，刷新地图，并设置图例面板
        m_layerThemeDotDensity.setVisible(value);
        mMapControl.getMap().refresh();
    }



}
