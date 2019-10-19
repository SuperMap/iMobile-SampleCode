package com.supermap.licensedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Module;
import com.supermap.data.RecycleLicenseManager;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import org.json.JSONArray;

import java.util.ArrayList;


/**
 * 正式许可(自动激活归还)
 */
public class OfficialLicenseActivity extends AppCompatActivity implements View.OnClickListener {
    String RoothPath=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    RecycleLicenseManager licenseManager;
    String userSerialNumber = "***********";//许可序列号
    String phoneNumber="1522222";
    ArrayList<Module> modules = new ArrayList<>();
    MapView mapView;
    MapControl mapControl;
    Workspace workspace;
    int Flag=1;//激活标识符
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.initialization(this);
        setContentView(R.layout.activity_officiallicense2);
        initView();
        activeLicense();

    }

    //在线激活时，必须含有至少Core_Dev 、Core_Runtime 中一种
    private void activeLicense(){
        LicenseStatus licenseStatus=Environment.getLicenseStatus();
        if (!licenseStatus.isActivated()){
        modules.add(Module.Core_Runtime);
        if (licenseManager.activateDevice(userSerialNumber,modules)){
            Flag=1001;
        }}
        else {
            Toast.makeText(getBaseContext(),"启动Activity时已有许可激活！",Toast.LENGTH_SHORT).show();
//            textView.append("\n已经有许可激活");
            openMap();
        }
    }
    private void recycleLicense1(){
        licenseManager.recycleLicense(null);
        Flag=1002;
    }


    private void initView(){
        licenseManager=RecycleLicenseManager.getInstance(this);
        licenseManager.setActivateCallback(licenseCallback);

        findViewById(R.id.zoomin).setOnClickListener(this);
        findViewById(R.id.zoomout).setOnClickListener(this);

    }
    private void openMap(){
        mapView=findViewById(R.id.mapview);
        mapControl=mapView.getMapControl();
        workspace=new Workspace();
        mapControl.getMap().setWorkspace(workspace);
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SXWU);
        info.setServer(RoothPath+"/SampleData/LicenseDemo/Changchun.sxwu");
        if (workspace.open(info)){
            String mapname=workspace.getMaps().get(0);
            mapControl.getMap().open(mapname);
        }
        else {
            Toast.makeText(getBaseContext(),"打开工作空间失败!",Toast.LENGTH_SHORT).show();
        }
    }
    RecycleLicenseManager.RecycleLicenseCallback licenseCallback=new RecycleLicenseManager.RecycleLicenseCallback() {
        @Override
        public void success(LicenseStatus licenseStatus) {
            if (Flag==1001){
                Toast.makeText(OfficialLicenseActivity.this,"启动Activity时，许可激活成功！",Toast.LENGTH_SHORT).show();
                openMap();
                Flag=1;
            }
            else if (Flag==1002){
                Toast.makeText(OfficialLicenseActivity.this,"退出Activity时，许可归还成功！",Toast.LENGTH_SHORT).show();
                Flag=1;
            }
        }

        @Override
        public void activateFailed(String s) {

        }

        @Override
        public void recycleLicenseFailed(String s) {

        }

        @Override
        public void bindPhoneNumberFailed(String s) {

        }

        @Override
        public void upgradeFailed(String s) {

        }

        @Override
        public void queryResult(ArrayList<Module> arrayList) {

        }

        @Override
        public void queryLicenseCount(JSONArray jsonArray) {

        }

        @Override
        public void otherErrors(String s) {

        }
    };

    @Override
    protected void onDestroy(){
        if (Environment.getLicenseStatus().isActivated()) {
            recycleLicense1();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zoomin:
                mapControl.getMap().zoom(2);
                break;
            case R.id.zoomout:
                mapControl.getMap().zoom(0.5);
                break;
        }
    }
}
