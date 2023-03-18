package com.supermap.aimap;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.supermap.ai.airender.AIMapRender;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:AI智能配图
 * 通过传入图片，自动获取图片颜色赋予到地图上
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
 * 1、范例简介：示范如何使用AI智能配图
 * 2、示例数据：数据目录："/sdcard/SampleData/AIMap/"
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *
 *
 *   AIMapRender.getInstance().initialization(this);//初始化AI配图
 *   AIMapRender.getInstance().setAIMapRenderListener();//设置智能配图渲染监听
 *   AIMapRender.getInstance().matchPictureStyle();//传入图片，开始配图
 *
 * 4、使用步骤：
 * （1）点击左下角智能配图菜单建，弹出图片列表
 * （2）选择图片即可自动开始配图
 *
 * 5、Note
 * 静态库除了依赖imb、imb2d、autocad其中一个以外，
 * 还需要依赖配图环境库，libpython2.7、libstar_java、libstarcore、libstarpy
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
public class MainActivity extends AppCompatActivity {
    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
    };
    private String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private MapControl mMapControl;
    private MapView mMapView;
    private Workspace mWorkspace;
    private View mDialogView;
    private ImageView mPreView;
    private String pre_path=sdcard+"/SampleData/aimap/picture/ai_";
    private String[] paths={
            pre_path+"01.png",
            pre_path+"02.png",
            pre_path+"03.png",
            pre_path+"04.png",
            pre_path+"05.png",
            pre_path+"06.png",
            pre_path+"07.png",
            pre_path+"08.png",
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.setLicensePath(sdcard + "/SuperMap/license");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initMap();
        initDialog();
    }
    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }
    /**
     * 申请动态权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (!checkPermissions(needPermissions)) {
            EasyPermissions.requestPermissions(
                    this,
                    "为了应用的正常使用，请允许以下权限。",
                    0,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }
    private void initMap(){
        mMapView=findViewById(R.id.mapview);
        mMapControl=mMapView.getMapControl();
        mWorkspace=new Workspace();
        mMapControl.getMap().setWorkspace(mWorkspace);
        WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();
        info.setServer(sdcard+"/SampleData/aimap/Hunan/Hunan.smwu");
        info.setType(WorkspaceType.SMWU);
        if (mWorkspace.open(info)){
            String mapName = mWorkspace.getMaps().get(0);
            mMapControl.getMap().open(mapName);
            initAIMap();
        }else {
            Toast.makeText(this,"打开地图失败，请检查数据",Toast.LENGTH_SHORT).show();
        }
    }
    void initAIMap(){
        AIMapRender.getInstance().initialization(this);
        AIMapRender.getInstance().setAIMapRenderListener(new AIMapRender.AIMapRenderListener() {
            @Override
            public void onMatchPictureStyleFinished(boolean b, String s, String s1) {
                Toast.makeText(MainActivity.this,"配图完成",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initDialog(){
        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogView.getVisibility()==View.GONE){
                    mDialogView.setVisibility(View.VISIBLE);
                }else {
                    mDialogView.setVisibility(View.GONE);
                }
            }
        });
        mDialogView=findViewById(R.id.select_dialog);
        mPreView=findViewById(R.id.image_ai_select);
        RadioGroup radioGroup=findViewById(R.id.radio_status);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_1:
                        AIMapRender.getInstance().matchPictureStyle(paths[0],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_01);
                        break;
                    case R.id.radio_2:
                        AIMapRender.getInstance().matchPictureStyle(paths[1],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_02);
                        break;
                    case R.id.radio_3:
                        AIMapRender.getInstance().matchPictureStyle(paths[2],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_03);
                        break;
                    case R.id.radio_4:
                        AIMapRender.getInstance().matchPictureStyle(paths[3],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_04);
                        break;
                    case R.id.radio_5:
                        AIMapRender.getInstance().matchPictureStyle(paths[4],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_05);
                        break;
                    case R.id.radio_6:
                        AIMapRender.getInstance().matchPictureStyle(paths[5],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_06);
                        break;
                    case R.id.radio_7:
                        AIMapRender.getInstance().matchPictureStyle(paths[6],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_07);
                        break;
                    case R.id.radio_8:
                        AIMapRender.getInstance().matchPictureStyle(paths[7],mMapControl);
                        mPreView.setBackgroundResource(R.drawable.ai_08);
                        break;
                }
                mPreView.setVisibility(View.VISIBLE);
                mDialogView.setVisibility(View.GONE);
            }
        });
    }
}