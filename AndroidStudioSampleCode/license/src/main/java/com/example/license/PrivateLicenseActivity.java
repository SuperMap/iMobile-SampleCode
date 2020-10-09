package com.example.license;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.PrivateCloudLicenseManager;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.util.Vector;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:私有云许可示范代码
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范如何查询私有云许可模块，以及激活
 * 2、示例数据：数据目录："/sdcard/SampleData/Changhun"
 * 3、关键类型/成员:
 *   PrivateCloudLicenseManager.getInstance().setServerUrl(); //设置私有云许可地址
 *   PrivateCloudLicenseManager.getInstance().queryFormalLicense(); //查询模块
 *   PrivateCloudLicenseManager.getInstance().ApplyFormalLicense();//激活许可
 *
 * 4、使用步骤：
 *  (1)点击"查询模块"按钮，查询许可模块，
 *  (2)如果查询成功，点击"选择模块"按钮，勾选需要激活的模块
 *  (3)点击"激活许可"按钮，进行许可激活
 *  (4)激活成功后，点击"打开地图"按钮，打开地图
 * 5、注意事项
 *   必须开启WIFI
 *   必须与许可服务处于同一局域网下
 *   如果许可网络断开连接，许可将进行回收，无法使用移动端功能
 *   如果为正式许可，需要勾选"核心开发模块"、"核心运行模块"进行开发
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class PrivateLicenseActivity extends AppCompatActivity {
    private Vector<PrivateCloudLicenseManager.privateCloudLicInfo> licinfos=new Vector<>();
    private ListView listView=null;
    private int[] activecount=null;
    private boolean isActive=false;
    private Workspace workspace=null;
    private WorkspaceConnectionInfo info=null;
    private MapView mapView=null;
    private MapControl mapControl=null;
    private String RootPath=android.os.Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.initialization(this);
        setContentView(R.layout.activity_private);
//        PrivateCloudLicenseManager.getInstance().setServerUrl("ws://10.10.1.149:9183/");
        PrivateCloudLicenseManager.getInstance().setServerUrl("http://192.168.11.216:9183/");
    }
    public void query(View view){
        PrivateCloudLicenseManager.getInstance().queryFormalLicense(licQueryCallback);
    }
    public void select(View view){
        if (licinfos==null){
            showMsg("没有可用模块");
        }
        else {
            showMoudleDialog();
        }
    }
    public void active(View view){
        activeDevice();
    }
    public void openmap(View view){
        openmap();
    }
    private void openmap(){
        mapView=findViewById(R.id.mapview);
        mapControl=mapView.getMapControl();
        workspace=new Workspace();
        mapControl.getMap().setWorkspace(workspace);
        info=new WorkspaceConnectionInfo();
        info.setType(WorkspaceType.SXWU);
        info.setServer(RootPath+"/SampleData/LicenseDemo/Changchun.sxwu");
        workspace.open(info);
        String mapname=workspace.getMaps().get(0);
        mapControl.getMap().open(mapname);
    }
    private void activeDevice(){
        isActive=PrivateCloudLicenseManager.getInstance().ApplyFormalLicense(activecount);
        if (isActive){
            showMsg("激活成功");
        }
        else {
            showMsg("激活失败");
        }
    }
    private void showMoudleDialog(){
        final int length=licinfos.size();
        final String[] items = new String[length];
        final boolean[] itemsChecked = new boolean[length];
        if (licinfos.size()>0){
            for (int i=0;i<length;i++){
                items[i]=licinfos.get(i).name;
                itemsChecked[i]=false;
            }}

        Vector<PrivateCloudLicenseManager.privateCloudLicInfo> tempinfos=new Vector<>();

        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setTitle("选择模块");
        alertBuilder.setMultiChoiceItems(items,itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                listView.setItemChecked(which,isChecked);
                itemsChecked[which]=isChecked;
                if (isChecked){
                    tempinfos.add(licinfos.get(which));
                }
            }
        });
        listView=alertBuilder.create().getListView();
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activecount=new int[tempinfos.size()];
                for (int i=0;i<tempinfos.size();i++){
                    activecount[i]=Integer.parseInt(tempinfos.get(i).id);
                }
                dialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempinfos.clear();
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }
    PrivateCloudLicenseManager.privateLicQueryCallback licQueryCallback=new PrivateCloudLicenseManager.privateLicQueryCallback() {
        @Override
        public void onQueryComplete(Vector<PrivateCloudLicenseManager.privateCloudLicInfo> vector) {
            if (vector==null){
                showMsg("没有可用模块");
            }
            else {
                showMsg("查询成功");
                licinfos=vector;
            }
        }
    };
    private void showMsg(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
