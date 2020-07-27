package com.supermap.a3dar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Point2D;
import com.supermap.data.Point3D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.ARControl;
import com.supermap.realspace.Layer3DOSGBFile;
import com.supermap.realspace.Layer3Ds;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneType;

/**
 * <p>
 * Title:三维AR在线-离线范例，BIM数据放在平面上，剖切功能。
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 *
 * 1、范例简介：示范如何将BIM数据放置在平面上，进行剖切操作 2、示例数据：无
 * 3、关键类型/成员:
 * SceneControl.getScene 方法
 *  arControl.setARstate(boolean value);
 *  arControl.setLayerScale(int layerid,double scale);
 *  layer3DOSGBFile.setCustomClipCross(Point3D point3d,point2d point2d, double rotx, double rotY, double rotZ,, double z);
 * Scene.open 方法
 *
 * 4、使用步骤： (1)将SampleData/BIM/中的数据拷贝到Android设备 sd卡中的/sdcard/SuperMap/data/下
 *               (2)运行程序。
 *               (3)将手机对准平面，（沿着手机和平面这条射线来回伸缩可提升识别效率）成功识别出平面后，点击识别区域，添加场景，剖切操作。
 * ----------------------------------------
 * --------------------------------------
 * ============================================================================>
 * </p>
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class ARActivity extends Activity  {

    //工作空间
    private Workspace workspace;
    //工作空间信息连接类
    private WorkspaceConnectionInfo workspaceConnectionInfon;
    //数据路径
    private String path= "/sdcard/SampleData/BIM/BIM.sxwu";
    //三维空间
    private SceneControl sceneControl;
    //
    private SeekBar seekbarw;

    private double crossw = 300;
    private double crossh = 300;
    private double crossz = 1;
    private Point3D point3d=null;
    private Point2D point2d=null;
    private Layer3Ds layer3Ds;
    private int layerCount;
    private Layer3DOSGBFile layer3DOSGBFile;
    //三维AR控件
    private ARControl arControl;
    //打开场景标志量
    private boolean isFirst=false;
    //打开场景后再去剖切
    private boolean isOpenScene=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //动态权限
        CameraPermissionHelper.requestCameraPermission(this);
        //全屏显示
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this,true);
        Environment.initialization(this);
        SceneControl.sceneType=SceneType.ST_NONEARTH;
        setContentView(R.layout.activity_main);
        sceneControl=findViewById(R.id.sceneControl);
        arControl=new ARControl(this,sceneControl);
        seekbarw=findViewById(R.id.seekbar1);

        sceneControl.sceneControlInitedComplete(new SceneControl.SceneControlInitedCallBackListenner() {
            @Override
            public void onSuccess(String success) {
                //开启AR
                arControl.setARState(true);

            }
        });
        arControl.setAnchorOnClickListener(new ARControl.AnchorOnClickListener() {

            @Override
            public void onSuccess() {
                if(!isFirst && isLicenseAvailable() ){
                    openLocalScene();
//                    openOnlineScene();

                    //设置一个数据放缩，范例数据在室内，默认放缩到0.005，就是室外。
                    arControl.setSceneScale(0.005,0.005,0.005);
                    point3d=new Point3D(51.7309421285678 ,16.400098261876 ,  11.4698015927305);
                    isFirst=true;
                }
            }
        });


        seekbarw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress==0){
                    return;
                }
                if(!isOpenScene){
                    Toast.makeText(ARActivity.this, "请先识别平面后，击中屏幕中锚点，打开场景后，再去进行剖切", Toast.LENGTH_LONG).show();
                    return;
                }
                crossw=progress/1.0;
                point2d=new Point2D(crossw, crossh);
                for(int i=0;i<layerCount;i++){
                    layer3DOSGBFile=(Layer3DOSGBFile)layer3Ds.get(i);
                    layer3DOSGBFile.setCustomClipCross(point3d, point2d, 0, 0, 0, crossz);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    //在线
    private void openOnlineScene(){
        if(sceneControl.getScene().open("http://10.10.1.149:8090/iserver/services/3D-BIM/rest/realspace","BIM")){
            layer3Ds=sceneControl.getScene().getLayers();
            sceneControl.getScene().refresh();
            isOpenScene=true;
        }

    }
    //打开本地场景
    private  void openLocalScene() {
        if (workspace == null) {
            workspace = new Workspace();
            workspaceConnectionInfon = new WorkspaceConnectionInfo();
        }
        workspaceConnectionInfon.setServer(path);
        workspaceConnectionInfon.setType(WorkspaceType.SXWU);
        sceneControl.getScene().setWorkspace(workspace);
        if (workspace.open(workspaceConnectionInfon)){
            sceneControl.getScene().open(workspace.getScenes().get(0));
            layerCount=sceneControl.getScene().getLayers().getCount();
            layer3Ds=sceneControl.getScene().getLayers();
            isOpenScene=true;
        }
    }

    // 判断许可是否可用
    private boolean isLicenseAvailable() {
        LicenseStatus licenseStatus = Environment.getLicenseStatus();
        if (!licenseStatus.isLicenseExsit()) {
            Toast.makeText(ARActivity.this, "许可不存在，场景打开失败，请加入许可", Toast.LENGTH_LONG).show();
            return false;
        } else if (!licenseStatus.isLicenseValid()) {
            Toast.makeText(ARActivity.this, "许可过期，场景打开失败，请更换有效许可", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}

