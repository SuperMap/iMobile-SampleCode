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
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    mARLayerView.setRefreshInterval();		方法
 *    mARLayerView.setHasFinishedLoading();		方法
 *    mARLayerView.drawNaviRoutes();            方法
 *    mARLayerView.isARCity();			        方法
 *    mARLayerView.setIsARCity();				方法
 *    mARLayerView.setCurrentPosition();		方法
 *    mARLayerView.getRelativePosition();       方法
 *
 *
 * 4、功能展示
 *   (1)初始化AR箭头环境；
 *   (2)点击【导航箭头】按钮，显示导航箭头；
 *   (3)点击【固定姿态】按钮，将AR箭头在场景位置中固定，不根据实际位置刷新。
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

import com.google.are.sceneform.ARPlatForm;
import com.google.are.sceneform.rendering.ModelRenderable;
import com.supermap.ar.arlayer.ARLayerView;
import com.supermap.data.Environment;
import com.supermap.data.Point3D;
import com.supermap.data.Point2D;
import com.supermap.hiar.ARCamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ARLayerView mARLayerView;
    private Context mContext = null;

    //存储路线点坐标
    private ArrayList<Point3D> mNaviRoutePoints = new ArrayList<>();
    private ImageView mIsUserPositionBtn;
    private boolean showLine = true;
    public boolean isUserPosition = false; //是否接受第三方坐标输入

    private ModelRenderable mFinalDesModelRenderable = null;
    private ModelRenderable mArrowMarkerModelRenderable = null;
    private ImageView mModule;

    /**
     * 偏移角度
     */
    private float offsetAngle = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //针对即支持ARCore，又支持AREngine的设备，通过AREngine.enforceARCore()可强制使用ARCore
        //在2020年之前的华为设备会支持ARCore，详情参考ARCore支持列表。
//        AREngine.enforceARCore();


        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);
        mContext = getBaseContext();
        setContentView(R.layout.activity_main);
        initView();


        /**
         * 注意事项：
         * AREngine与ARCore在场景初始化时，场景的世界坐标系的X轴的朝向有所不同
         * ARCore场景启动时，X轴为右方。
         * AREngine场景启动时，X轴方向与正右方之间有一个偏移角度。
         * 在AREngine场景启动后，通过ARCamera.getOffsetAngle()可获取到这个偏移角度。或是通过ARCamera.setInitCallback()设置回调事件
         * */
        ARCamera.setInitCallback(new ARCamera.InitCallback() {
            @Override
            public void complete(float angle) {
                offsetAngle = angle;
                Toast.makeText(mContext, "当前平台："+ ARPlatForm.getEngineType() +"\n偏移角:" + offsetAngle, Toast.LENGTH_SHORT).show();

                initNaviLine(offsetAngle);//onCreate
                initParam();
                //刷新场景位置
                updatePosition();
            }
        });

    }

    private void initView() {
        //初始化AR导航视图
        mARLayerView = findViewById(R.id.lytARLayerView);
        mIsUserPositionBtn = findViewById(R.id.btnIsUserPosition);
        mIsUserPositionBtn.setOnClickListener(this);

        mModule = findViewById(R.id.btn_module);
        mModule.setOnClickListener(this);


    }
    private void initParam() {
        //修正传感器方位角与地理方位角偏差
        mARLayerView.setBearingAdjustment(10);

        //设置场景刷新间隔，默认1000ms,单位ms
        mARLayerView.setRefreshInterval(1000);
        /**
         * 通过异步加载导航箭头，终点模型;  可通过三方软件（如：Blender）编辑glb模型，不再使用sfa/sfb
         */
        CompletableFuture<ModelRenderable> finalDesFuture = ModelRenderable.builder()
                .setSource(mContext, R.raw.targetv2)
                .setIsFilamentGltf(true)/*使用GLTF模型，此处设置为true*/
                .build();
        CompletableFuture<ModelRenderable> arrowFuture = ModelRenderable.builder().setSource(mContext,R.raw.arrowv9)
                .setIsFilamentGltf(true)
                .build();
        CompletableFuture.allOf(finalDesFuture, arrowFuture).handle(
                (notUsed, throwable) -> {
                    try
                    {
                        mFinalDesModelRenderable = finalDesFuture.get();
                        mArrowMarkerModelRenderable = arrowFuture.get();
                        // Everything finished loading successfully.

                        mARLayerView.drawNaviRoutes(mNaviRoutePoints, mArrowMarkerModelRenderable, mFinalDesModelRenderable);

                    } catch (InterruptedException | ExecutionException ex)
                    {
                    }
                    return null;
                });

    }

    /**
     * 添加导航箭头位置点
     */
    private void initNaviLine(float offsetAngle) {

        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(0.127096980f,0.57685166f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(0.047429360f,1.058599591f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(-0.4301927f,5.4954524040f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(-1.0029597f, 11.6404361724f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(1.985295534f, 12.197192192f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(5.752028465f, 9.786989212f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(5.520273685f, 12.614511911f,0)));
        mNaviRoutePoints.add(Utils.correctPosition(offsetAngle,new Point3D(6.569637200f, 8.17261314f,0)));
    }

    private static final int TIMER = 999;
    private boolean flag = true;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    //这里刷新位置
                    if (mARLayerView != null) {
//                        Point3D relativePosition = mARLayerView.getRelativePosition();
//                        if (null != relativePosition) {
//                            double x = relativePosition.getX();
//                            double y = relativePosition.getY();
//                            double z = relativePosition.getZ();
////                            txtLocation.setText("x : " + x + "\ny : " + y + "\nz : " + z);
//                        }

                        //使用第三方坐标输入
                        if (isUserPosition == true) {
                            //输入第三方坐标数据，默认为（0,0）
                            mARLayerView.setCurrentPosition(new Point2D(0, 0));
                            mARLayerView.setBearingAdjustment(0);
                        }

                    }
                    if (flag == true) {
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
        showLine = true;

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

            case R.id.btnIsUserPosition:
                if (null == mARLayerView) {
                    break;
                }
                /**
                 * 注：
                 * 当需要设置外部的三方GPS数据时，
                 * 通过{@link ARLayerView#setUserPosition(boolean)}方法，
                 * 参数设置为true，通过{@link ARLayerView#setCurrentPosition(Point2D)}
                 * 传入当前的位置点。
                 */
                Toast.makeText(mContext, "请参考源码注释，接入三方数据！", Toast.LENGTH_SHORT).show();
//                if (isUserPosition) {
//                    isUserPosition = false;
//                    //依赖AR场景位置更新，不依赖GPS等第三方数据
//                    mARLayerView.setUserPosition(isUserPosition);
//                    mIsUserPositionBtn.setBackgroundResource(R.drawable.arcity_enable);
//                } else {
//                    isUserPosition = true;
//                    //接受第三方坐标输入，依赖GPS位置进行AR场景更新，受GPS精度和地磁影响
//                    mARLayerView.setUserPosition(isUserPosition);
//                    mIsUserPositionBtn.setBackgroundResource(R.drawable.arcity_disable);
//                }
                break;

            case R.id.btn_module:
                if (showLine) {
                    //隐藏路线
                    showLine = false;
                    mNaviRoutePoints.removeAll(mNaviRoutePoints);
                    mModule.setBackgroundResource(R.drawable.disable_guide_arrow);

                } else {
                    showLine = true;
                    initNaviLine(offsetAngle);
                    mModule.setBackgroundResource(R.drawable.enable_guide_arrow);
                }

                mARLayerView.drawNaviRoutes(mNaviRoutePoints, mArrowMarkerModelRenderable, mFinalDesModelRenderable);


        }
    }
}
