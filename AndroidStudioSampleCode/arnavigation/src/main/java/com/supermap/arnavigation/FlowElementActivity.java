package com.supermap.arnavigation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.are.sceneform.ARPlatForm;
import com.supermap.ar.Point3D;
import com.supermap.ar.areffect.ARAnimationGroup;
import com.supermap.ar.areffect.ARAnimationManager;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.AREffectView;
import com.supermap.ar.areffect.ConvertTool;
import com.supermap.ar.areffect.Location;
import com.supermap.ar.areffect.preset.FlowElement;
import com.supermap.ar.areffect.preset.PresetUtils;
import com.supermap.data.Environment;
import com.supermap.hiar.ARCamera;

import java.util.Arrays;
import java.util.List;

/**
 * 展示如何在AR场景中创建一个动态的地导航箭头指引
 * <pre>
 *     AR导航指引实现流程：
 *     1、通过路径分析功能获取路径的节点（地理坐标点集）
 *     2、将地理坐标点集转换为AR场景坐标点集
 *     3、根据AR场景坐标点集放置模型
 *     注意事项：
 *          此处不再演示如何通过路径分析得到节点（详情参考路径分析相关示例）
 *          此处所用的AR视图控件与{@link MainActivity}中所用的AR视图控件不同。
 *
 *     补充说明：
 *         //此处，简单描述下如何控制流动元素的显示、隐藏。以及如何释放对象
 *         //通过控制父节点的显隐，来达到控制所有子节点显隐的目的
 *         flowElement.getParentElement().setVisiblity(false);
 *         //销毁节点-自身及子节点下所有内容将被销毁，图形内存即刻被释放。
 *         flowElement.getParentElement().destroy();
 *
 *         //当有外部高精度GPS点传入，可通过开启AR视图控件的姿态融合器来纠偏
 *         IPoseMixer iPoseMixer = arEffectView.openPoseMixer();
 *         //可通过定时器，或其它能实时更新的回调。此处以AR视图的刷新监听事件为例
 *         arEffectView.addOnUpdateListener(new EffectView.OnUpdateListener() {
 *             @Override
 *             public void onUpdate() {
 *                  //实时更新高精度位置
 *                      iPoseMixer.getLocationUpdateListener()
 *                          .update(经度,纬度,相对地面的高度,水平精度);
 *                 }
 *         });
 * </pre>
 *
 */
public class FlowElementActivity extends Activity {
    //视图控件
    private AREffectView arEffectView;

    //场景节点
    private AREffectElement sceneNode;

    //流动元素-箭头指引
    private FlowElement flowElement;

    //动画组
    private ARAnimationGroup arAnimationGroup;
    //偏移角度
    private float offsetAngle;

    private boolean useGeoCoordinate = true;

    //<editor-fold> 生命周期
    @Override
    protected void onPause() {
        super.onPause();
        arEffectView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arEffectView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁场景节点下所有内容
        sceneNode.destroy();
        arEffectView.onDestroy();
    }
    //</editor-fold>

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //组件功能必须在 Environment 初始化之后才能调用

        Environment.setLicensePath(android.os.Environment
                .getExternalStorageDirectory().getAbsolutePath()+"/SuperMap/license");
        Environment.initialization(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        arEffectView = findViewById(R.id.ar_view);

//        //当有外部高精度GPS点传入，可通过开启AR视图控件的姿态融合器来纠偏
//        IPoseMixer iPoseMixer = arEffectView.openPoseMixer();
//        //实时传入高精度位置，可通过定时器，或其它能实时更新的回调。此处以AR视图的刷新监听事件为例
//        arEffectView.addOnUpdateListener(new EffectView.OnUpdateListener() {
//            @Override
//            public void onUpdate() {
////                iPoseMixer.getLocationUpdateListener().update(/*经度*/,/*纬度*/,/*相对地面的高度*/,/*水平精度*/);
//            }
//        });

        sceneNode = new AREffectElement(this).setParentNode(arEffectView);

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
                Toast.makeText(getApplicationContext(), "当前平台："+ ARPlatForm.getEngineType()
                        +"\n偏移角:" + offsetAngle, Toast.LENGTH_SHORT).show();

                //关闭平面渲染
                arEffectView.setPlaneVisible(false);
                if (!useGeoCoordinate){
                    //简单示例,通过AR场景坐标创建
                    loadFlowElement(Arrays.asList(
                            Utils.correctPosition(offsetAngle,new Point3D(0,0,-1)),
                            Utils.correctPosition(offsetAngle,new Point3D(0,6,-1)),
                            Utils.correctPosition(offsetAngle,new Point3D(6,6,-1)),
                            Utils.correctPosition(offsetAngle,new Point3D(6,0,-1))
                    ));
                }else {
                    //以AR场景定点启动为例（也可通过图片识别、两点定位等其它方式）
                    //在ARCamera初始化成功时，需要对AR控件进行初始地理位置、方位角设置，示例如下：
                    //设置设备初始位置
                    arEffectView.setDeviceLocation(new Location(/*经度*/104.0312,
                            /*纬度*/30.31234,
                            /*手机初始化时的高度，例如手机在离地面1.3m的位置上*/1.3));
                    //设置设备初始方位角，此处以朝向正北启动为例。
                    // (图片识别、两点定位可直接通过校正得到包含设备初始位置、初始方位角的结果，之后传入此处即可)
                    arEffectView.setAzimuth(0.0f);

                    //地理坐标=>AR场景坐标
                    loadFlowElement(Arrays.asList(
                            Utils.correctPosition(offsetAngle,
                                    ConvertTool.convertToArPosition(arEffectView,
                                            new Location(104.0312,30.31234,0))),
                            Utils.correctPosition(offsetAngle,
                                    ConvertTool.convertToArPosition(arEffectView,
                                            new Location(104.0312,30.31239,0))),
                            Utils.correctPosition(offsetAngle,
                                    ConvertTool.convertToArPosition(arEffectView,
                                            new Location(104.03128,30.31239,0))),
                            Utils.correctPosition(offsetAngle,
                                    ConvertTool.convertToArPosition(arEffectView,
                                            new Location(104.03128,30.31234,0)))
                    ));
                }
            }
        });

    }

    /**
     * 加载流动元素
     * @param sourceList AR场景顶点集
     */
    public void loadFlowElement(List sourceList){
        //desc-将连续的点，按照一定间距进行拆分，得到新的点集
        List  point3DS = PresetUtils.genNewPointsBySpacingDistance(sourceList, 2.0f);

        flowElement = new FlowElement();
        flowElement.setParentNode(sceneNode);
        //desc-设置点集，此处点集是经过拆分后得到的
        flowElement.setPoints(point3DS);
        //desc-设置两点间的位移时长
        flowElement.setUnifiedDuration(2400);//或使用统一速度设置flowElement.setUnifiedSpeed(1.0f);
        //desc-设置模型资源的缩放比例
        flowElement.setScaleFactor(new float[]{2,2,2});
        //设置模型资源的旋转角度
//        flowElement.setRotationAngle(new Vector(0,0,1),-90);
        //desc-生成动画组，参数为gltf模型的资源id
        arAnimationGroup = flowElement.generateAnimation(R.raw.arrowv9);

        //desc-将动画组添加至动画管理器中
        ARAnimationManager.getInstance().addAnimationGroup(arAnimationGroup.getGroupName());
        //desc-播放动画
        ARAnimationManager.getInstance().playAnimationGroup(arAnimationGroup.getGroupName());
    }
}
