package com.example.license;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.supermap.data.Datasource;
import com.supermap.data.EduLicense;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

//import com.supermap.data.EduLicense;

public class EduLicenseActivity extends AppCompatActivity {
    public static final String TAG = "+++---";
    String licenseid;
    String returnId;
    EduLicense eduLicense = null;
    private MapView mMapView;
    private MapControl mMapControl;
    private Workspace mWorkspace;
    private WorkspaceConnectionInfo info;
    Datasource datasource;
    String DataPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/SampleData/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.setEduLicense(true);
        Environment.initialization(this);
        setContentView(R.layout.activity_edu_license);
        eduLicense = EduLicense.getInstance(this);
        Log.i("+++---", Environment.getDeviceID());
        mMapView = (MapView) findViewById(R.id.mapview);
        eduLicense.setEduLicenseApplyListener(new EduLicense.EduLicenseApplyListener() {
            @Override
            public void getApplySuccess(boolean isApplySuccess) {
                if (isApplySuccess){
                    showMsg("激活成功");
                    Log.i(TAG, "激活成功 ");
                }
                else {
                    showMsg("激活失败");
                    Log.i(TAG, "激活失败 ");
                }
            }
        });
    }

    public void login(View view) {
        eduLicense.setUrl("https://192.168.11.216:9583");
        eduLicense.applyLicense();

    }

    public void activestatue(View view){
        Log.i(TAG, eduLicense.getLicenseInfos().message);
    }
    public void openmap(View view) {
        prepareData();
    }
    private void prepareData() {
        mMapControl = mMapView.getMapControl();
        mWorkspace = new Workspace();
        info = new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SXWU);
        info.setServer(DataPath + "LicenseDemo/Changchun.sxwu");
        boolean isopen = mWorkspace.open(info);
        if (isopen) {
            mMapControl.getMap().setWorkspace(mWorkspace);
            String mapname = mWorkspace.getMaps().get(0);
            mMapControl.getMap().open(mapname);
            mMapControl.getMap().refresh();
        }
    }
    private void showMsg(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
