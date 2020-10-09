package com.example.license;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

/**
 * 试用许可
 */

public class TrailLicenseActivity extends AppCompatActivity {

    
    String RoothPath=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    MapView mapView;
    MapControl mapControl;
    Workspace workspace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.setLicensePath(RoothPath+"/SuperMap/License/");
        Environment.initialization(this);
        setContentView(R.layout.activity_traillicense);
        initView();
        openMap();
    }

    private void openMap() {
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SXWU);
        info.setServer(RoothPath+"/SampleData/LicenseDemo/Changchun.sxwu");
        if (workspace.open(info)){
            String mapname=workspace.getMaps().get(0);
            mapControl.getMap().open(mapname);
            Toast.makeText(getBaseContext(),"当前为试用许可",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getBaseContext(),"打开工作空间失败!",Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        mapView=findViewById(R.id.mapview);
        mapControl=mapView.getMapControl();
        workspace=new Workspace();
        mapControl.getMap().setWorkspace(workspace);
    }
}
