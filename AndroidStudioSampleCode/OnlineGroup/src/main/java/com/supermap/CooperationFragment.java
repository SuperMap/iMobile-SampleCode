package com.supermap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.supermap.data.*;
import com.supermap.mapping.*;
import com.supermap.supermap.R;


/**
 * 协同管理
 */
public class CooperationFragment extends Fragment implements View.OnClickListener {
    private MapView mMapView = null;
    private LinearLayout cd_group_child = null;
    Workspace mWorkSpace;
    MapControl mMapControl;
    public CooperationFragment() {
        // Required empty public constructor
    }

    public static CooperationFragment newInstance(String title) {
        CooperationFragment f = new CooperationFragment();
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cooperation, container, false);
        initView(view);
        openMap();
        return view;
    }

    private void initView(View view) {
        mMapView = view.findViewById(R.id.mapView);
        cd_group_child = view.findViewById(R.id.cd_group_child);

        view.findViewById(R.id.ib_create_group).setOnClickListener(this);
        view.findViewById(R.id.ib_join_group).setOnClickListener(this);

        view.findViewById(R.id.ib_delete_group).setOnClickListener(this);



    }

    private void openMap() {
        mWorkSpace = new Workspace();
        mMapControl = mMapView.getMapControl();
        mMapControl.getMap().setWorkspace(mWorkSpace);
        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        //设置数据源别名
        info.setAlias("GoogleMap");
        //设置引擎类型
        info.setEngineType(EngineType.GoogleMaps);
        //设置地图服务地址(对接谷歌地图服务时，不设置该项也可打开，若服务地址变更，可在此处设置变更后的谷歌地图服务地址)
        String url = "http://www.google.cn/maps";
        info.setServer(url);
        //打开数据源
        Datasource datasource = mWorkSpace.getDatasources().open(info);
        //添加数据集到地图窗口
        mMapControl = mMapView.getMapControl();

        mMapControl.getMap().getLayers().add(datasource.getDatasets().get(1), true);

//        Point2D point2D = new Point2D(1.296938682121914E7, 4863849.610990819);
        Point2D point2D = new Point2D(11582869.316047,3532375.460537);

//        mMapControl.getMap().setScale(2.58542883436988E-4);
        mMapControl.getMap().setScale(1/14331.9428034705);
        mMapControl.getMap().setCenter(point2D);
        mMapControl.getMap().refresh();
    }

    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;
    boolean showpop=false;
    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.ib_create_group:
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                break;
            case R.id.ib_join_group:
                startActivity(new Intent(getActivity(), ApplyJoinGroupActivity.class));
                break;
            case R.id.ib_delete_group:
                startActivity(new Intent(getActivity(), DeleteGroupActivity.class));
                break;


        }
    }
}
