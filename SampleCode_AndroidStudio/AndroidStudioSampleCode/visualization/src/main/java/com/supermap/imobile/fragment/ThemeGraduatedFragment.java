package com.supermap.imobile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.supermap.data.Color;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoStyle;
import com.supermap.imobile.visualization.MainActivity;
import com.supermap.imobile.visualization.R;
import com.supermap.mapping.GraduatedMode;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.ThemeGraduatedSymbol;

/**
 * 等级符号图
 */
public class ThemeGraduatedFragment extends Fragment implements View.OnClickListener{

    private MapControl mMapControl;
    private Layer m_layerThemeGraduatedDensity;

    MainActivity activity_main = null;
    private DatasetVector m_dataset;

    boolean isShow = true;
    Button mBtnShow;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        activity_main = (MainActivity)activity;
        mMapControl = activity_main.mMapControl;
        m_dataset = (DatasetVector) mMapControl.getMap().getWorkspace().getDatasources().get(0)
                .getDatasets().get("BaseMap_R");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dotdensity, container, false);
        mBtnShow = (Button) rootView.findViewById(R.id.btn_show);
        mBtnShow.setOnClickListener(this);
        addThemeGraduatedDensity(true);
        return rootView;
    }

    //可见性改变
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            m_layerThemeGraduatedDensity.setVisible(false);
            mMapControl.getMap().refresh();
        }else{
            m_layerThemeGraduatedDensity.setVisible(true);
            isShow = true;
            mBtnShow.setText("隐藏专题图");
            mMapControl.getMap().refresh();
        }
    }
    public void addThemeGraduatedDensity(boolean value) {

        if (m_layerThemeGraduatedDensity == null) {
            try {
                ThemeGraduatedSymbol themeGraduatedDensity = new ThemeGraduatedSymbol();
                themeGraduatedDensity.setExpression("Urban");
                themeGraduatedDensity.setBaseValue(60.00000);
                themeGraduatedDensity
                        .setGraduatedMode(GraduatedMode.SQUAREROOT);
                themeGraduatedDensity.setFlowEnabled(false);
                GeoStyle geoStyle = themeGraduatedDensity.getPositiveStyle();
                geoStyle.setLineColor(new Color(155,187,89));
                themeGraduatedDensity.setPositiveStyle(geoStyle);

                // 将制作好的专题图添加到地图中显示
                // Add the graduated symbol map to the mapControl to display
                m_layerThemeGraduatedDensity = mMapControl.getMap()
                        .getLayers()
                        .add(m_dataset, themeGraduatedDensity, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }
        // 设置图层是否可显示，刷新地图，并设置图例面板
        // Set whether the layer visible, refresh the map and set the legend panel
        m_layerThemeGraduatedDensity.setVisible(value);
        mMapControl.getMap().refresh();

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_show:
                if(isShow){
                    isShow = false;
                    mBtnShow.setText("显示专题图");
                    if(m_layerThemeGraduatedDensity != null){
                        m_layerThemeGraduatedDensity.setVisible(false);
                        mMapControl.getMap().refresh();
                    }
                }else{
                    isShow = true;
                    mBtnShow.setText("隐藏专题图");
                    if(m_layerThemeGraduatedDensity != null){
                        m_layerThemeGraduatedDensity.setVisible(true);
                        mMapControl.getMap().refresh();
                    }
                }
                break;
        }


    }
}
