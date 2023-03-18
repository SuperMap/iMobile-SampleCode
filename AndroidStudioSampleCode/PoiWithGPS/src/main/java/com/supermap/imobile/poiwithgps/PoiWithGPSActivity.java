package com.supermap.imobile.poiwithgps;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.supermap.ar.arlayer.ARLayerView;
import com.supermap.ar.arlayer.ARObject;
import com.supermap.data.Point2D;
import com.supermap.data.Point3D;
import com.supermap.imobile.poc.R;


import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PoiWithGPSActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks,
        ARLayerView.OnARObjectSelectListener
         {
    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.CHANGE_WIFI_STATE,
    };

    private final String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

    private ImageButton mImageButton ;
    private ImageButton mInitARCityImageButton;

    public static boolean isARCityFlag = false;
    public boolean isUserPosition = true; //是否接受第三方坐标输入

    private ARLayerView mARLayerView;

    private ArrayList<ARObject> mARObjectList = new ArrayList<>();

    private Map<Integer,ARObject> mARObjectHashMap = new HashMap<>();

    private ViewRenderable mVRenderableGroup0Book0Details;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Environment.setLicensePath("/sdcard/SuperMap/license/");
//        Environment.initialization(this);

        requestPermissions();

        setContentView(R.layout.activity_main);

        mARLayerView = findViewById(R.id.lytARLayerView);

        //1. 使用图书图片创建渲染器
        CompletableFuture<ViewRenderable> vRenderGroup0Book0 = ViewRenderable.builder()
                .setView(this,R.layout.vrender_group0_book0).build();
        CompletableFuture<ViewRenderable> vRenderGroup0Book1 = ViewRenderable.builder()
                .setView(this,R.layout.vrender_group0_book1).build();
        CompletableFuture<ViewRenderable> vRenderGroup0Book2 = ViewRenderable.builder()
                .setView(this,R.layout.vrender_group0_book2).build();

        CompletableFuture<ViewRenderable> vRenderGroup1Book0 = ViewRenderable.builder()
                .setView(this,R.layout.vrender_group1_book0).build();

        CompletableFuture<ViewRenderable> vRenderGroup2Book0 = ViewRenderable.builder()
                .setView(this,R.layout.vrender_group2_book0).build();


        CompletableFuture<ViewRenderable> vRenderGroup0Book0Details = ViewRenderable.builder()
                .setView(this,R.layout.vrender_group0_book0_details).build();


        //2. 或者使用3ds Max, maya, blender等建模工具创建更精细的模型数据再创建渲染器，
        CompletableFuture<ModelRenderable> finalDesFuture = ModelRenderable.builder().setSource(this,
                Uri.parse("file:///android_asset/samplebook.sfb")).build();


        //3. 异步加载
        CompletableFuture.allOf(vRenderGroup0Book0,vRenderGroup0Book1,
                vRenderGroup0Book2,vRenderGroup1Book0,vRenderGroup2Book0,vRenderGroup0Book0Details,
                finalDesFuture).handle(
                (notUsed, throwable) -> {
                    try
                    {
                        mARObjectHashMap.put(500,new ARObject(vRenderGroup0Book0.get(),new Point3D(   116.505995,39.985902,0),500));
                        mARObjectHashMap.put(501,new ARObject(vRenderGroup0Book1.get(),new Point3D(116.506046,39.985913,0),501));
                        mARObjectHashMap.put(502,new ARObject(vRenderGroup0Book2.get(),new Point3D(   116.5061187,39.985909,0),502));
                        mARObjectHashMap.put(503,new ARObject(vRenderGroup1Book0.get(),new Point3D( 1116.506132,39.985876,0),503));
                        mARObjectHashMap.put(504,new ARObject(finalDesFuture.get(),new Point3D(116.506070,39.985818,0),504));

                        mVRenderableGroup0Book0Details = vRenderGroup0Book0Details.get();
                        mARObjectHashMap.get(500).addChild(new ARObject(mVRenderableGroup0Book0Details,new Point3D(0,0,-2),800));

                        Set< Map.Entry< Integer,ARObject> > st = mARObjectHashMap.entrySet();
                        ArrayList<ARObject> tempARObjectList =new ArrayList<>();
                        for (Map.Entry< Integer,ARObject> me:st)
                        {
                            tempARObjectList.add((ARObject)me.getValue());
                        }


//                        mARLayerView.addARObject();
//                        mARLayerView.removeARObject();

                        mARLayerView.drawAllARObjects(tempARObjectList);

                        //6、设置场景中对象显示监听。
                        mARLayerView.setOnARObjectSelectListener(this);

//                        mARLayerView.setNorthStart(false);       //设置是否正北启动
//                        mARLayerView.setMagneticAdjustMent(20);  //设置磁偏角调整
//                        mARLayerView.setFarClipPlane(100);       //设置远裁剪平面

                    } catch (InterruptedException | ExecutionException ex)
                    {
                    }
                    return null;
                });


        initView();


        //刷新场景位置
        initSceneUpdate();

    }

     private void initView() {
         mInitARCityImageButton = findViewById(R.id.initARCity);
         mInitARCityImageButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (isUserPosition == true) {
                     isUserPosition = false;
                     //依赖AR场景位置更新，不依赖GPS等第三方数据
                     mARLayerView.setUserPosition(isUserPosition);

                     mInitARCityImageButton.setBackgroundResource(R.drawable.arcity_enable);
                 } else {
                     isUserPosition = true;
                     //接受第三方坐标输入，依赖GPS位置进行AR场景更新，受GPS精度和地磁影响
                     mARLayerView.setUserPosition(isUserPosition);
                     mInitARCityImageButton.setBackgroundResource(R.drawable.arcity_disable);
                 }

             }
         });
     }


     private double getDistance(Point3D p0,Point2D p1){
        return Math.sqrt(
                Math.pow(p1.getX()-p0.getX(),2)+Math.pow(p1.getY()-p0.getY(),2)
        );
    }



    /*
    * 检测靠近当前位置范围1m内的目标并绘制
    * 复杂一点更改一下mARObjectList数据结构为Map<ARObject,Boolean>,增加是否已经存在于场景的标记位
    *  private Map<ARObject,Boolean> mARObjectsMap = new HashMap<>();
    * */
     private void processTask(){
//         boolean needClearFlag = true;
//         //mARObjectList 遍历需要添加的书籍列表
//         for(int i = 0;i<mARObjectList.size();i++){
//             //距离小于 1m 向场景添加当前对象
//             if(getDistance(mARObjectList.get(i).getPosition(),mARLayerView.getRelativePosition()) < 1){
//                 mARLayerView.addARObject(mARObjectList.get(i));
//                 mARLayerView.refresh();
//
//                 needClearFlag = false;
//             }
//         }
//         if(true == needClearFlag){
//             mARLayerView.clearAllARObjects();
//         }

         Set< Map.Entry< Integer,ARObject> > st = mARObjectHashMap.entrySet();
         ArrayList<Integer> tempNeedRemoveList =new ArrayList<>();
         for (Map.Entry< Integer,ARObject> me:st)
         {
             if(getDistance(me.getValue().getPosition(),mARLayerView.getCurrentPosition()) < 1){
                 mARLayerView.addARObject(me.getValue());
                 tempNeedRemoveList.add(me.getKey());
                 mARLayerView.refresh();
             }
         }

         for(int i = 0; i< tempNeedRemoveList.size();i++){
             if(mARObjectHashMap.containsKey(tempNeedRemoveList.get(i)))
             {
                 mARObjectHashMap.remove(tempNeedRemoveList.get(i));
             }
         }

     }



     //7.  对象选中监听,返回选中的对象进行操作
     @Override
     public void OnARObjectSelect(ARObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.txtLocation)).setText("find:"+object.getID());
            }
        });


//         View pannel = ((ViewRenderable)object.getRenderable()).getView();
//        // update button text when the renderable's node is tapped
//        ((TextView) (pannel.findViewById(R.id.txtUserInfo))).setText("SuperMap" + "\niMobile \nExample");
//        ((TextView) (pannel.findViewById(R.id.txtTime))).setText(simpleDateFormat.format(date));


        //8.一些对象操作接口
//         if(object.getID() == 500){
//
//             if(flagOperationCompleted == false){
//
////                 object.setRenderable(mVRenderableGroup0Book0Details);
//
////                 object.addChild(new ARObject(mVRenderableGroup0Book0Details,new Point3D(0,0,-2),800));;
//
////                 object.setRotation(new Vector3(0,180,0));
//
////                 object.setScaleFacor(new Vector3(2.0f,2.0f,2.0f));
//
////                 object.setPosition(new Point3D(5,5,-2));
//
//
////                 mARLayerView.addARObject(new ARObject(mVRenderableGroup0Book0Details,new Point3D(9,5,0),504));
////                 mARLayerView.removeARObject(object);
//

         //         //每次refresh一下
//                 mARLayerView.refresh();
//                 flagOperationCompleted = true;
//             }
//         }

     }



    private static final int TIMER = 999;
    private static boolean flag = true;

    private void initSceneUpdate(){
     Message message = mHandler.obtainMessage(TIMER);     // Message
     mHandler.sendMessageDelayed(message, 1000);
    }


    private Handler mHandler = new Handler(){
     @Override
     public void handleMessage(Message msg) {
         super.handleMessage(msg);
         switch (msg.what){
             case TIMER:
                 //这里刷新位置，预留与GPS结合，室内外一体化。
                 if(mARLayerView != null){
                     if(isUserPosition == true){
                         //这里如果使用第三方坐标输入的话，把坐标传到AR场景
                         mARLayerView.setCurrentPosition(new Point2D(116.506076,39.985880));
                         mARLayerView.setBearingAdjustment(0);
                     }

                 }
                 if (flag) {
                     Message message = mHandler.obtainMessage(TIMER);
                     mHandler.sendMessageDelayed(message, 500);
                 }
                 break;
             default:
                 break;
         }
     }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mARLayerView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mARLayerView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mARLayerView.onDestory();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    //申请动态权限
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
                    Manifest.permission.CAMERA,
                    Manifest.permission.CHANGE_WIFI_STATE);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        StringBuffer sb = new StringBuffer();
        for (String str : perms){
            sb.append(str);
            sb.append("\n");
        }
        sb.replace(sb.length() - 2,sb.length(),"");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setRationale("此功能需要：" + sb + "权限，否则无法正常使用，是否打开设置？")
                    .setPositiveButton("是")
                    .setNegativeButton("否")
                    .build()
                    .show();
        }
    }



         }
