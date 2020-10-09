package com.example.license;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.supermap.data.CloudLicenseManager;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.LicenseType;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.license.LicenseInfo;
import com.supermap.data.license.QueryFormalLicenseResponse;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import javax.security.auth.login.LoginException;

public class CloudLicenseActivity extends AppCompatActivity {
    public static final String TAG = "+++---CloudLicenseActivity";
    String licenseid;
    String returnId;
    CloudLicenseManager manager = null;
    private MapView mMapView;
    private MapControl mMapControl;
    private Workspace mWorkspace;
    private WorkspaceConnectionInfo info;
    Datasource datasource;
    String DataPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/SampleData/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.setLicenseType(LicenseType.UUID);
        Environment.initialization(this);
        setContentView(R.layout.activity_cloud_license);
        manager = CloudLicenseManager.getInstance(this);
        manager.setLoginCallback(licenseLoginCallback);
        mMapView = (MapView) findViewById(R.id.mapview);
    }

    public void login(View view) {
        manager.login("992275331@qq.com","1995824lwh");
    }

    public void query(View view) {
        manager.queryLicense();
    }

    public void active(View view) {
        manager.applyFormal(licenseid);
//        manager.active();
    }
    

    public void recycle(View view) {
        manager.recycleLicense(licenseid,returnId);
    }
    public void lastlicense(View view) {
     CloudLicenseManager.LastLicenseInfo info= manager.getLastLicense();
     if (info!=null){
         Toast.makeText(this,info.licenseID+","+info.returnID,Toast.LENGTH_SHORT).show();
     }else {
         Toast.makeText(this,"没有信息",Toast.LENGTH_SHORT).show();
     }
    }
    public void openmap(View view) {
        prepareData();
    }
    public void logout(View view) {
        manager.logout();
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
        } else {
            return;
        }
    }
    CloudLicenseManager.LicenseLoginCallback licenseLoginCallback = new CloudLicenseManager.LicenseLoginCallback() {
        @Override
        public void loginAccount(boolean issuccess) {
            if (issuccess) {
                Log.i(TAG, "loginAccount: ");
                showMsg("登录成功");
            }
        }

        @Override
        public void logoutAccount(boolean logout) {
            if (logout){
                Log.i(TAG, "退出成功");
                showMsg("退出成功");
            }
        }

        @Override
        public void queryLicense(QueryFormalLicenseResponse queryFormalLicenseResponse) {
            if (queryFormalLicenseResponse!=null){
              int licenseCount =  queryFormalLicenseResponse.licenseCount;
              boolean formal =queryFormalLicenseResponse.formal;
                LicenseInfo[] licenseInfos=queryFormalLicenseResponse.licenses;
                for (int i=0;i<licenseInfos.length;i++){
                   String[] productType = licenseInfos[i].moduleNames;
                }
                licenseid=licenseInfos[0].id;
            }
            Log.i(TAG, "licenseid:"+licenseid);
            showMsg("查询成功");
        }

        @Override
        public void activeLicense(String returnid) {
            returnId=returnid;
            Log.i(TAG, "returnid:"+returnid);
            showMsg("激活成功");
        }

        @Override
        public void recycleLicense(int days) {
            Log.i(TAG, String.valueOf(days));
            showMsg(String.valueOf(days));
        }

        @Override
        public void otherError(String error) {
            showMsg(error);
            Log.i(TAG, error);
        }
    };
    public void showMsg(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
