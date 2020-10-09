package com.example.license;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
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
 * 正式许可(手动激活归还)
 */
public class OfficialLicense2Activity extends AppCompatActivity implements View.OnClickListener{
    String RoothPath=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    RecycleLicenseManager licenseManager;
    String userSerialNumber = "**************";//许可序列号
    String phoneNumber="1522222";
    ArrayList<Module> modules = new ArrayList<>();
    MapView mapView;
    MapControl mapControl;
    Workspace workspace;
    View view;
    TextView textView;
    int Flag=1;//激活标识符
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.initialization(this);
        setContentView(R.layout.activity_officiallicense);
        initView();
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
            textView.append("\n已经有许可激活");
        }
    }
    private void recycleLicense1(){
        licenseManager.recycleLicense(null);
        Flag=1002;
    }
    private void recycleLicense2(){

        licenseManager.recycleLicense(phoneNumber);
        Flag=1002;
    }
    private void bindphoneNumber(){
        licenseManager.bindPhoneNumber(phoneNumber);
        Flag=1003;
    }
    private void queryLicenseCount(){
        licenseManager.queryLicenseCount(userSerialNumber);
    }
    private void initView(){
        licenseManager=RecycleLicenseManager.getInstance(this);
        licenseManager.setActivateCallback(licenseCallback);

        findViewById(R.id.recycle).setOnClickListener(this);
        findViewById(R.id.queryCount).setOnClickListener(this);
        findViewById(R.id.active).setOnClickListener(this);
        findViewById(R.id.recycle2).setOnClickListener(this);
        findViewById(R.id.bind).setOnClickListener(this);
        findViewById(R.id.clearinfo).setOnClickListener(this);


        textView=findViewById(R.id.tv);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
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
                textView.append("\n激活成功");
                openMap();
            }
            else if (Flag==1002){
                textView.append("\n归还成功");

            }
            else if (Flag==1003){
                textView.append("\n绑定手机号成功");

            }
        }

        @Override
        public void activateFailed(String s) {
            textView.append("\n"+s);
        }

        @Override
        public void recycleLicenseFailed(String s) {
            textView.append("\n"+s);
        }

        @Override
        public void bindPhoneNumberFailed(String s) {
            textView.append("\n"+s);
        }

        @Override
        public void upgradeFailed(String s) {

        }

        @Override
        public void queryResult(ArrayList<Module> arrayList) {

        }

        @Override
        public void queryLicenseCount(JSONArray jsonArray) {
            textView.append("\n"+jsonArray.toString());
        }

        @Override
        public void otherErrors(String s) {
            textView.append("\n"+s);
        }
    };

    @Override
    public void onClick(View v) {
        view=v;
        switch (v.getId()){
            case R.id.active:
                activeLicense();
                break;
            case R.id.recycle:
                recycleLicense1();
                break;
            case R.id.queryCount:
                queryLicenseCount();
                break;
            case R.id.bind:
                bindphoneNumber();
                break;
            case R.id.recycle2:
                recycleLicense2();
                break;
            case R.id.clearinfo:
                textView.setText("");
                break;
        }
    }
/*    @Override
    protected void onDestroy(){

        if (Environment.getLicenseStatus().isActivated()) {
            recycleLicense1();
        }
        super.onDestroy();
    }*/
}
