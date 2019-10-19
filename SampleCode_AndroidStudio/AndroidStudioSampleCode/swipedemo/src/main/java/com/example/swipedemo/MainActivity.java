package com.example.swipedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.supermap.data.DatasetType;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String RootPath= android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/";
    MapView mapView;
    MapControl mapControl;
    Workspace workspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.setLicensePath(RootPath+"SuperMap/License/");
//        Environment.setOpenGLMode(true);
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initData();
    }
    private void initData(){

        mapView=findViewById(R.id.mapview);
        mapControl=mapView.getMapControl();
        workspace=new Workspace();
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SMWU);
        info.setServer(RootPath+"SampleData/Hunan/Hunan.smwu");
        workspace.open(info);
        mapControl.getMap().setWorkspace(workspace);
        String mapname=workspace.getMaps().get(0);
        mapControl.getMap().open(mapname);
        mapControl.setAction(Action.SWIPE);
        int count=mapControl.getMap().getLayers().getCount();
        for (int i=0;i<count;i++){
            if (mapControl.getMap().getLayers().get(i).getTheme()!=null){
                mapControl.getMap().getLayers().get(i).setVisible(false);
            }
            else {
                if (mapControl.getMap().getLayers().get(i).getDataset().getType()== DatasetType.POINT){
                    mapControl.getMap().getLayers().get(i).setVisible(false);
                }
                else {
                    if (!mapControl.getMap().getLayers().get(i).getIsSwipe()){
                        mapControl.getMap().getLayers().get(i).setIsSwipe(true);
                    }
                }
            }

        }

        findViewById(R.id.openSwipe).setOnClickListener(this);
        findViewById(R.id.CloseSwipe).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.openSwipe:
                mapControl.setAction(Action.SWIPE);
                break;
            case R.id.CloseSwipe:
                mapControl.setAction(Action.PAN);
                break;

        }
    }
}
