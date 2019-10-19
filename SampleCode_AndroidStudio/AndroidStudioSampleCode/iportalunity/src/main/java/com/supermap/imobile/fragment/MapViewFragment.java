package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.supermap.data.*;
import com.supermap.imobile.bean.OpenRestMapEvent;
import com.supermap.imobile.iportalservices.R;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 打开地图
 */
public class MapViewFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MapViewFragment";
    private Context mContext = null;

    private ImageButton zoomIn = null;
    private ImageButton zoomOut = null;

    private String rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataAddress = "/SampleData/GeometryInfo/World.smwu";   //  工作空间地址

    private Workspace workspace = null;
    private MapControl mapControl = null;
    private MapView mapView = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mapview, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        zoomIn = rootView.findViewById(R.id.ib_zoom_in);
        zoomOut = rootView.findViewById(R.id.ib_zoom_out);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);

        mapView = rootView.findViewById(R.id.mapview);
        mapControl = mapView.getMapControl();

        workspace = new Workspace();
        WorkspaceConnectionInfo wci = new WorkspaceConnectionInfo();
        wci.setServer(rootPath + dataAddress);
        wci.setType(WorkspaceType.SMWU);
        // 打开工作空间
        workspace.open(wci);
        // 地图关联工作空间
        mapControl.getMap().setWorkspace(workspace);
    }

    private void openRestMap(String url) {
//        String url = "http://192.168.169.121/iserver/services/map_population-and-economy2/rest/maps/2014年人口密度专题图";
//        String url = "http://192.168.169.121/iserver/services/map_population-and-economy2/rest/maps/2014年港口货物中转量专题图";
        mapControl.getMap().close();
        workspace.getDatasources().closeAll(); // 数据源别名冲突

        DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
        datasourceConnectionInfo.setEngineType(EngineType.Rest);
        datasourceConnectionInfo.setServer(url);
        Datasource datasource = workspace.getDatasources().open(datasourceConnectionInfo);

        if (datasource != null) {
            mapControl.getMap().getLayers().add(datasource.getDatasets().get(0), true);
            mapControl.getMap().viewEntire();
            mapControl.enableRotateTouch(true);
//            mapControl.enableSlantTouch(true);
            mapControl.getMap().refresh();
        }
    }

    @Override
    public void onClick(View v) {
        double scale = mapControl.getMap().getScale();
        switch (v.getId()) {
            case R.id.ib_zoom_in:
                mapControl.zoomTo(scale * 1.4 , 100);
                break;
            case R.id.ib_zoom_out:
                mapControl.zoomTo(scale * 0.6 , 100);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenRestMapEvent(OpenRestMapEvent event) {
        Log.e(TAG, "onOpenRestMapEvent: " + event.getUrl());
        if (!event.getMode().equals(TAG)) {
            return;
        }
        openRestMap(event.getUrl());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
