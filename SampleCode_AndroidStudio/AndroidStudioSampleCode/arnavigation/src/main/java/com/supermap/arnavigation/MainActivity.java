package com.supermap.arnavigation;

/**
 * <p>
 * Title:AR导航箭头展示
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   	展示AR模式下导航功能的使用实现。
 *
 * 2、Demo数据：
 * 		数据目录："../SampleData/IndoorNavigationData/"
 *      地图数据："beijing.smwu", "beijing0525.udb", "bounds.udb", "kaide_mall.udb"
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    m_NavigationEx.setStartPoint();			方法
 *    m_NavigationEx.setDestinationPoint();		方法
 *    m_NavigationEx.addWayPoint();				方法
 *    m_NavigationEx.routeAnalyst();			方法
 *    m_NavigationEx.startGuide();				方法
 *    m_floorListView.setCurrentFloorId();		方法
 *
 * 4、功能展示
 *   (1)添加起点、终点、途径点；
 *   (2)路径分析；
 *   (3)导航。
 *   (4)楼层切换
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.supermap.ar.arlayer.ARLayerView;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point3D;
import com.supermap.data.Workspace;
import com.supermap.data.Datasource;
import com.supermap.data.Point2D;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;


import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ARLayerView mARLayerView;
    private Context mContext = null;

    //存储路线点坐标
    private ArrayList<Point3D> mNaviRoutePoints = new ArrayList<>();
    private ImageButton mIsARCity;
    private boolean showLine = false;

    private ModelRenderable mFinalDesModelRenderable = null;
    private ModelRenderable mArrowMarkerModelRenderable = null;
    private ImageButton mModule;
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
            Manifest.permission.CAMERA,
    };

    /**
     * 检测权限
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */

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
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CAMERA);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions() ;

        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);
        mContext = getBaseContext();
        setContentView(R.layout.activity_main);
        initView();
        initParam();
        initNaviLine();

    }

    private void initView() {

        //初始化AR导航视图
        mARLayerView = findViewById(R.id.lytARLayerView);
        mIsARCity = findViewById(R.id.btn_arcity);
        mIsARCity.setOnClickListener(this);

        mModule = findViewById(R.id.btn_module);
        mModule.setOnClickListener(this);


    }
    private void initParam() {
        //修正传感器方位角与地理方位角偏差
        mARLayerView.setBearingAdjustment(10);

        //设置场景刷新间隔，默认1000mm,单位mm
        mARLayerView.setRefreshInterval(1);
        /**
         * 通过异步加载导航箭头，终点模型;  修改sampleData文件下对应的.sfa文件调整模型颜色，透明度，大小等。
         */
        CompletableFuture<ModelRenderable> finalDesFuture = ModelRenderable.builder().setSource(mContext,
                Uri.parse("file:///android_asset/blue.sfb")).build();
        CompletableFuture<ModelRenderable> arrowFuture = ModelRenderable.builder().setSource(mContext,
                Uri.parse("file:///android_asset/arrowv6.sfb")).build();
        CompletableFuture.allOf(finalDesFuture, arrowFuture).handle(
                (notUsed, throwable) -> {
                    try
                    {
                        mFinalDesModelRenderable = finalDesFuture.get();
                        mArrowMarkerModelRenderable = arrowFuture.get();
                        // Everything finished loading successfully.
                        mARLayerView.setHasFinishedLoading(true);

                    } catch (InterruptedException | ExecutionException ex)
                    {
                    }
                    return null;
                });


    }

    /**
     * 添加导航箭头位置点
     */
    private void initNaviLine() {

        mNaviRoutePoints.add(new Point3D(0.127096980,0.57685166,0));
        mNaviRoutePoints.add(new Point3D(0.047429360,1.058599591,0));
        mNaviRoutePoints.add(new Point3D(-0.4301927,5.4954524040,0));
        mNaviRoutePoints.add(new Point3D(-1.0029597, 11.6404361724,0));
        mNaviRoutePoints.add(new Point3D(1.985295534, 12.197192192,0));
        mNaviRoutePoints.add(new Point3D(5.752028465, 9.786989212,0));
        mNaviRoutePoints.add(new Point3D(5.520273685, 12.614511911,0));
        mNaviRoutePoints.add(new Point3D(6.569637200, 8.17261314,0));
    }


    private static final int TIMER = 999;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    //这里刷新位置
                    if (mARLayerView != null) {
                        Point3D relativePosition = mARLayerView.getRelativePosition();
                        if (null != relativePosition) {
                            double x = relativePosition.getX();
                            double y = relativePosition.getY();
                            double z = relativePosition.getZ();
//                            txtLocation.setText("x : " + x + "\ny : " + y + "\nz : " + z);
                        }

                        if ( !mARLayerView.isARCity() ) {
                            mARLayerView.setCurrentPosition(new Point2D(0, 0));
                            mARLayerView.setBearingAdjustment(0);
                        }

                    }
                    if (mARLayerView.isARCity()) {
                        Message message = mHandler.obtainMessage(TIMER);
                        mHandler.sendMessageDelayed(message, 300);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void updatePosition() {

        Message message = mHandler.obtainMessage(TIMER);     // Message
        mHandler.sendMessageDelayed(message, 1000);
    }
    @Override
    public void onResume() {
        super.onResume();
        mARLayerView.onResume();
        showLine = false;
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_arcity:
                if (null == mARLayerView) {
                    break;
                }
                if (mARLayerView.isARCity()) {
                    mIsARCity.setBackgroundResource(R.drawable.arcity_disable);
                    mARLayerView.setIsARCity(false);
                } else {
                    mIsARCity.setBackgroundResource(R.drawable.arcity_enable);
                    mARLayerView.setIsARCity(true);
                }

                break;

            case R.id.btn_module:

                if (showLine) {
                    //隐藏路线
                    showLine = false;
                    mNaviRoutePoints.clear();
                    mModule.setBackgroundResource(R.drawable.disable_guide_arrow);

                } else {
                    showLine = true;
                    initNaviLine();
                    mModule.setBackgroundResource(R.drawable.enable_guide_arrow);
                }

                mARLayerView.drawNaviRoutes(mNaviRoutePoints, mArrowMarkerModelRenderable, mFinalDesModelRenderable);
                //刷新场景位置
                updatePosition();
        }
    }
}
