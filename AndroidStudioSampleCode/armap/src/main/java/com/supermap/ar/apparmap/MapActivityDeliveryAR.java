package com.supermap.ar.apparmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.are.sceneform.ARPlatForm;
import com.google.are.sceneform.ArHelpers;
import com.google.are.sceneform.math.Quaternion;
import com.google.are.sceneform.math.Vector3;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.ar.Point3D;
import com.supermap.ar.apparmap.Element.CarEntity;
import com.supermap.ar.apparmap.bean.PoiBean;
import com.supermap.ar.areffect.ARAnimationGroup;
import com.supermap.ar.areffect.ARAnimationManager;
import com.supermap.ar.areffect.ARAnimationParameter;
import com.supermap.ar.areffect.ARAnimationRepeatMode;
import com.supermap.ar.areffect.ARAnimationTranslation;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.AREffectView;
import com.supermap.ar.areffect.ARGltfElement;
import com.supermap.ar.areffect.ARMapElement;
import com.supermap.ar.areffect.ARViewElement;
import com.supermap.ar.areffect.ConvertTool;
import com.supermap.ar.areffect.EffectView;
import com.supermap.ar.areffect.Location;
import com.supermap.ar.areffect.loader.ArSceneLoader;
import com.supermap.ar.areffect.preset.ARGeoDottedLine;
import com.supermap.ar.areffect.preset.BaseShape;
import com.supermap.ar.areffect.preset.Lighting;
import com.supermap.ar.areffect.preset.PresetUtils;
import com.supermap.ar.areffect.preset.StripeLine;
import com.supermap.data.Color;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Datasources;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.hiar.ARCamera;
import com.supermap.hiar.AREngine;
import com.supermap.hiar.ARFrame;
import com.supermap.hiar.ARPlane;
import com.supermap.hiar.ARPose;
import com.supermap.hiar.TrackingState;
import com.supermap.mapping.Action;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mapping.dyn.DynamicView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * <p>
 * Title:AR地图-物流配送
 * 数据：长春数据集
 * </p>
 * <pre>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   	展示如何在AR场景中加载地图
 *
 * 2、Demo数据：
 *      数据路径：/SampleData/chuangchun2/Changchun2.smwu
 *      许可目录："../SuperMap/license/"
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </pre>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

public class MapActivityDeliveryAR extends AppCompatActivity {
    public static final String TAG = "SuperMap";
    private static final String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    private MapView mapView;
    private Map map;
    private MapControl mapControl;
    private TransportationAnalyst m_analyst;
    private Layer m_layerLine;
    private TrackingLayer m_trackingLayer;
    private DynamicView dynamicView;
    private TransportationAnalystResult m_result;
    private DatasetVector m_datasetLine;
    private static String m_datasetName = "RoadNet";

    private Point2Ds m_Points = new Point2Ds();
    private Point2Ds m_targetPoints = new Point2Ds();

    private ARMapElement mapElement;
    private ArSceneLoader sceneLoader;
    private EffectView.OnUpdateListener onUpdateListener;
    private EffectView.OnUpdateListener onUpdateListener1;
    private TextView mSearchingTextView;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private boolean isBegin = false;
    private Bitmap outputMap;
    //数据源
    private Datasource datasource;

    // AR相关
    private AREffectView arEffectView;

    private Point3D pointByScreenRayTest;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // android生命周期相关
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.app.ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CAMERA
        }, PackageManager.PERMISSION_GRANTED);

        //强制使用ARCore
//        AREngine.enforceARCore();

        //设置许可路径
        Environment.setOpenGLMode(false);
        Environment.setLicensePath(SDCARD + "/SuperMap/License");
        Environment.initialization(this);
        setContentView(R.layout.activity_delivery_ar);
        //控件绑定
        mapView = findViewById(R.id.map_view1);
        mapControl = mapView.getMapControl();
        map = mapControl.getMap();
        mSearchingTextView = findViewById(R.id.plane_info);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);

        initAR();
        setOrientationListener();

        ARCamera.setInitCallback(new ARCamera.InitCallback() {
            @Override
            public void complete(float offsetAngle) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //注意：针对ARCore与AREngine，由于初始坐标系不同，需要作适配
                        Toast.makeText(MapActivityDeliveryAR.this, "当前平台："+ ARPlatForm.getEngineType(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arEffectView != null){
            arEffectView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (arEffectView != null){
            arEffectView.onPause();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 核心二维路径分析区
    private boolean analyst(){
        TransportationAnalystParameter parameter = new TransportationAnalystParameter();

        //设置最佳路径分析的返回对象
        parameter.setPoints(m_targetPoints);
        parameter.setNodesReturn(true);
        parameter.setEdgesReturn(true);
        parameter.setPathGuidesReturn(true);
        parameter.setRoutesReturn(true);
        parameter.setStopIndexesReturn(true);

        try{
            //进行分析并显示结果
            m_result = m_analyst.findMTSPPath(parameter, m_Points, false);
//            m_result = m_analyst.findPath(parameter, true);
        }
        catch(Exception e){
            m_result = null;
        }
        if (m_result == null) {
            MapActivityDeliveryAR.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MapActivityDeliveryAR.this, "分析失败", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        showResult();
//        showRoadAnalyst = true;
        return true;
    }

    private List<CarEntity> carEntities = new ArrayList<>();

    public void showResult() {
        GeoLineM[] routes = m_result.getRoutes();
        if (routes == null) {
            return;
        }
        int num = 0;
        for (GeoLineM geoLineM : routes) {
            num++;
            List<Point3D> point3DListDotted = new ArrayList<>();
            List<Point3D> point3DList = new ArrayList<>();
            GeoLine geoLine = geoLineM.convertToLine();
            Point2Ds part = geoLine.getPart(0);
            for (int j = 0; j < part.getCount(); j++) {
                Point2D item = part.getItem(j);
                // 地图坐标转手机屏幕坐标
                Point point = map.mapToPixel(item);

                // 屏幕坐标转三维AR场景坐标
                Point3D relativePoint = mapElement.getARFromMap(point);
                Point3D newPoint = new Point3D(relativePoint.x, relativePoint.y, relativePoint.z + 0.02f);
                Point3D newPoint2 = new Point3D(relativePoint.x, relativePoint.y, relativePoint.z + 0.02f);
                point3DList.add(newPoint);
                point3DListDotted.add(newPoint2);
            }

            renderDotted(point3DListDotted, num);

            // 渲染小车行进

            createCarEntity(part, (float) 0.12, num);

        }

        // 播放小车移动动画
        ARAnimationManager.getInstance().playAll();
        Log.d(TAG, "showResult: " + routes.length+","+carEntities.size());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 手势区
    private OPERATION_STATE state = OPERATION_STATE.STATE_NULL;
    private enum OPERATION_STATE{
        ADD_CENTER_POINT,
        ADD_TARGET_POINT,
        STATE_NULL
    }

    /**
     * 添加配送中心点
     * @param v 固定参数
     */
    public void centerPoint(View v){
        state = OPERATION_STATE.ADD_CENTER_POINT;
        if (mapControl != null) mapControl.setAction(Action.NULL);
        text1.setTextColor(getResources().getColor(R.color.blue));
        text2.setTextColor(getResources().getColor(R.color.white));
        text3.setTextColor(getResources().getColor(R.color.white));
        Log.d(TAG, "centerPoint: here1");

    }

    /**
     * 添加配送目标点
     * @param v 固定参数
     */
    public void targetPoint(View v){
        state = OPERATION_STATE.ADD_TARGET_POINT;
        if (mapControl != null) mapControl.setAction(Action.NULL);
        text1.setTextColor(getResources().getColor(R.color.white));
        text2.setTextColor(getResources().getColor(R.color.red));
        text3.setTextColor(getResources().getColor(R.color.white));
        Log.d(TAG, "targetPoint: here2");
    }

    /**
     * 路径分析
     * @param v
     */
    public void roadAnalyst(View v){
        state = OPERATION_STATE.STATE_NULL;
        if (mapControl != null) mapControl.setAction(Action.SELECT);
        text1.setTextColor(getResources().getColor(R.color.white));
        text2.setTextColor(getResources().getColor(R.color.white));
        text3.setTextColor(getResources().getColor(R.color.red));
        Log.d(TAG, "roadAnalyst: here3");
        if (m_Points.getCount() == 0 || m_targetPoints.getCount() == 0){
            Toast.makeText(this, "请至少选取一个配送中心点与目标点～", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "roadAnalyst: " + m_Points.getCount() + "..." + m_targetPoints.getCount());
        analyst();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 初始化区

    /**
     * 加载地图
     */
    private void loadMap() {
        //打开工作空间
        initMap();
        load();
        loadArMap();

    }

    public static final int LOAD_MAP = 0x11;
    /**
     * 把地图异步加载的AR场景中
     */
    public void loadArMap(){
        handler.sendEmptyMessage(LOAD_MAP);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_MAP:
                    mapElement.loadModel(mapView);
                    break;
            }
            return false;
        }
    });

    private boolean showRoadAnalyst = false;
    private void initAR(){
        arEffectView = findViewById(R.id.ar_view);
        arEffectView.setDeviceLocation(new Location(103,30,0));
        onUpdateListener = new EffectView.OnUpdateListener() {
            @Override
            public void onUpdate() {
                ARFrame mFrame = arEffectView.getArSceneView().getArFrame();
                if (mFrame == null) {
                    return;
                }

                // 识别平面后，隐藏
                for (ARPlane plane : mFrame.getUpdatedPlanes()) {
//                for (ARPlane plane : mFrame.getUpdatedTrackables(ARPlane.class)) {
                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                        if (mSearchingTextView != null) {
                            mSearchingTextView.setVisibility(View.GONE);
                            mSearchingTextView = null;
                        }

                        ARPose centerPose = plane.getCenterPose();
                        sceneLoader = new ArSceneLoader(arEffectView);
                        AREffectElement sceneElement = sceneLoader.getSceneElement();
                        checkARPlatForm(sceneElement);

                        //提取Pose的位置
                        Vector3 vector3 = ArHelpers.extractPositionFromPose(centerPose);
                        sceneElement.setPosition(vector3);

                        mapElement = new ARMapElement(MapActivityDeliveryAR.this);
                        mapElement.setParentNode(sceneLoader.getSceneElement());


//                        checkARPlatForm(mapElement.getArObjParent());

                        mapElement.setActionType(ARMapElement.ActionType.GESTURE_MAP);  // 默认使用地图手势
                        mapElement.setTransformable(true);   // 可旋转
                        mapElement.setArMapShapeType(ARMapElement.ARMapShapeType.MODE_ROUND);
//                        mapElement.setScaleFactor(new float[]{0.35f, 0.5f, 0.5f});

                        mapElement.setOnUpdateListener(new ARMapElement.OnMapUpdateListener() {
                            @Override
                            public void onArObjClear(MotionEvent event) {
                            }


                            @Override
                            public void onArObjUpdate(Point3D[] arViewVertice, Point leftTop, Point rightBottom, Point centerPoint) {
                            }
                        });

                        mapElement.setOnMapClickListener(new ARMapElement.OnMapClickListener() {
                            @Override
                            public void onMapClick(Point point, Point pp) {
                                // 沙盘点击事件
                                // 核心代码，把手机点击屏幕的坐标转换为地图地理坐标
                                Point2D pt = mapControl.getMap().pixelToMap(point);
                                Point2D pt_ = pt;

                                // 如果不是经纬度坐标系，转换为经纬度
                                if(map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
                                    Point2Ds points = new Point2Ds();
                                    points.add(pt);
                                    PrjCoordSys desPrjCoorSys = new PrjCoordSys();
                                    desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
                                    CoordSysTranslator.convert(points, map.getPrjCoordSys(), desPrjCoorSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
                                    pt_ = points.getItem(0);
                                }
                                // 获取地图所在平面  找到点击与平面的碰撞检测点
                                List<Point3D> point3DList = mapElement.getArViewVertice();
                                pointByScreenRayTest = PresetUtils.getPointByScreenRayTest(arEffectView, point3DList, pp.getX(), pp.getY());

                                switch (state){
                                    case ADD_CENTER_POINT:
                                        m_Points.add(pt_);   // 加入配送中心点集合
                                        renderPoint(pointByScreenRayTest, mapElement.getArObjParent(),true);
                                        /*
                                         * 2022/09/27 新增渲染点击位置的tag
                                         * */
//                                        addCallout(pt, centerPointCount++, true);
                                        break;
                                    case ADD_TARGET_POINT:
                                        m_targetPoints.add(pt_);   // 加入配送目标点集合
                                        renderPoint(pointByScreenRayTest, mapElement.getArObjParent(),false);
                                        /*
                                         * 2022/09/27 新增渲染点击位置的tag
                                         * */
//                                        addCallout(pt, targetPointCount++, false);
                                        break;
                                    default:
                                        // Toast.makeText(MapActivityDeliveryAR.this, "请选择是配送中心点或者配送目标点～", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                        });

                        // 异步任务加载地图
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loadMap();
                            }
                        }).start();

                        //场景灯光设置
                        setSceneLighting();
                        arEffectView.removeOnUpdateListener(onUpdateListener);
                        break;
                    }
                }
            }
        };
        arEffectView.addOnUpdateListener(onUpdateListener);
    }

    private void checkARPlatForm(AREffectElement element) {
        if (AREngine.isUsingAREngine()){
            element.setRotationAngle(new Quaternion(new Vector3(0,1,0),180));
        }
    }

    private void initMap(){
        //打开工作空间
        // 地图视图

        WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();
//        connectionInfo.setServer(SDCARD + "/SuperMap/data/City/Changchun.smwu");
//        connectionInfo.setServer(SDCARD + "/SampleData/City/Changchun.smwu");
        connectionInfo.setServer(SDCARD + "/SampleData/chuangchun2/Changchun2.smwu");
        connectionInfo.setType(WorkspaceType.SMWU);
        Workspace workspace = new Workspace();
        boolean open = workspace.open(connectionInfo);
        if (!open){
            Log.e(TAG,"工作空间打开失败");
            return;
        }
        //地图绑定工作空间
        map.setWorkspace(workspace);
        Datasources datasources = workspace.getDatasources();
//        datasource = datasources.get("changchun");
        datasource = datasources.get("Changchun2");

        //打开地图
//        boolean mapOpenStatus = map.open("长春市区图_copy"); // 暗黑版
//        boolean mapOpenStatus = map.open("长春市区图");   // 正常版
        boolean mapOpenStatus = map.open("长春市区图_Local1");   // 清新版
        if (!mapOpenStatus){
            Log.e(TAG, "Not find -> MAP_NAME: " + PropertiesSuper.MAP_NAME);
            //加载默认地图数据
            return;
        }

        m_datasetLine = (DatasetVector)datasource.getDatasets().get(m_datasetName);
        m_trackingLayer = map.getTrackingLayer();
        m_layerLine = map.getLayers().add(m_datasetLine,
                true);
        m_layerLine.setSelectable(false);
        m_layerLine.setVisible(false);
        LayerSettingVector lineSetting = (LayerSettingVector)m_layerLine
                .getAdditionalSetting();
        GeoStyle lineStyle = new GeoStyle();
        lineStyle.setLineColor(new Color(0, 0, 255));
        lineStyle.setLineWidth(0.1);
        lineSetting.setStyle(lineStyle);
        dynamicView = new DynamicView(this, map);

        map.viewEntire();
//        map.setScale(0.000018);
        map.setScale(1/7972.77);
        map.setCenter(new Point2D(4727.08, -4702.169));
        map.refresh();
    }

    // 预先加载路网数据
    private static String m_nodeID = "SmNodeID";
    private static String m_edgeID = "SmEdgeID";
    private void load(){
        // 设置网络分析基本环境，这一步骤需要设置　分析权重、节点、弧段标识字段、容限
        TransportationAnalystSetting setting = new TransportationAnalystSetting();
        setting.setNetworkDataset(m_datasetLine);
        setting.setEdgeIDField(m_edgeID);
        setting.setNodeIDField(m_nodeID);
        setting.setEdgeNameField("roadName");

        setting.setTolerance(89);

        WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
        WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
        weightFieldInfo.setFTWeightField("smLength");
        weightFieldInfo.setTFWeightField("smLength");
        weightFieldInfo.setName("length");
        weightFieldInfos.add(weightFieldInfo);
        setting.setWeightFieldInfos(weightFieldInfos);
        setting.setFNodeIDField("SmFNode");
        setting.setTNodeIDField("SmTNode");

        //构造交通网络分析对象，加载环境设置对象
        m_analyst = new TransportationAnalyst();
        m_analyst.setAnalystSetting(setting);
        m_analyst.load();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 其他AR渲染区

    /**
     * 根据触点加载POI
     * @param pointByScreenRayTest 碰撞检测点
     * @param element              POI父节点
     * @param isCenterPoint        是否是配送中心点
     */
    private ARGltfElement gltfElement;
    private int centerPointCount = 1;
    private int targetPointCount = 1;
    private void renderPoint(Point3D pointByScreenRayTest, AREffectElement element, boolean isCenterPoint){
        AREffectElement elementTmp = new AREffectElement(this);
        elementTmp.setParentNode(element);
        elementTmp.setPosition(pointByScreenRayTest);
        elementTmp.setRotationAngle(new Quaternion(new Vector3(1, 0, 0), -90));

        gltfElement = new ARGltfElement(this);
        gltfElement.setParentNode(elementTmp);
        gltfElement.setScaleFactor(new float[]{0.038f, 0.038f, 0.038f});

        Point3D pp = new Point3D(0, 0, 0);
        gltfElement.setRelativePosition(pp);

        ARGltfElement gltfElementTxt = new ARGltfElement(this);
        if (isCenterPoint){
            // 中心点
            gltfElement.loadModel(R.raw.poi_blue1);
            gltfElement.setElementName("配送中心"+centerPointCount);
            renderTag(centerPointCount++, element, pointByScreenRayTest, true, gltfElementTxt);
        }else{
            // 目标点
            gltfElement.loadModel(R.raw.poi_red1);
            String eleName = "配送点"+targetPointCount;
            gltfElement.setElementName(eleName);
            renderTag(targetPointCount++, element, pointByScreenRayTest, false, gltfElementTxt);

            // 把当前位置加入bean类，方便后续动画管理


            // 给每个模型添加动画组（按名称）

        }
        PoiBean bean = new PoiBean(pointByScreenRayTest, element, gltfElement, gltfElementTxt);
        targetPoiLists.add(bean);
        addAnimation(gltfElement, pp);

    }


    Quaternion q1 = new Quaternion(new Vector3(0, 1, 0), 180);
    private void renderTag(int count, AREffectElement effectView, Point3D point, boolean isCenterPoint, ARGltfElement gltfElement111){
        AREffectElement element = new AREffectElement(this);
        element.setParentNode(effectView);
        element.setPosition(point);

        Point3D point3D = new Point3D(0, 0, 0.2f);
        gltfElement111.setParentNode(element);
        gltfElement111.setRelativePosition(point3D);
        gltfElement111.setScaleFactor(new float[]{0.06f, 0.06f, 0.06f});
//        openAnimation(point3D, gltfElement111);
        if (isCenterPoint){
            if (count > 2) return;
            gltfElement111.loadModel(resHashMapCenter.get(count));
            gltfElement111.setElementName("配送中心"+count);
        }else{
            if (count > 9) return;
            gltfElement111.loadModel(resHashMap.get(count));
            gltfElement111.setElementName("配送点"+count);
        }
        effectElements.add(gltfElement111);
        addTxtAnimation(gltfElement111, point3D);
    }


    private void renderTagText(int count, AREffectElement effectView, Point3D point, boolean isCenterPoint){
        View viewText = LayoutInflater.from(this).inflate(R.layout.layout_tag_name, null);
        TextView two_point_dis = viewText.findViewById(R.id.tv_distance);
        if (isCenterPoint){
            two_point_dis.setText("配送中心" + count);
        }else{
            two_point_dis.setText("配送点" + count);
        }

        ARViewElement viewElement = new ARViewElement(this);
        viewElement.setParentNode(effectView);

        viewElement.setRelativePosition(new Point3D(point.x+ 0.3f, point.y, point.z));
        viewElement.setRotationQuaternion(new Quaternion(new Vector3(1, 0, 0), 90));
        viewElement.loadModel(viewText);
    }

    /**
     * 场景灯光设置
     */
    public void setSceneLighting(){
        Lighting lighting = new Lighting(Lighting.Type.DIRECTIONAL);
        lighting.setParentNode(arEffectView);
        lighting.setIntensity(2000);            // 设置光强
        lighting.getParentElement().setRelativePosition(new Point3D(0,0,0));   // 设置位置
        lighting.getParentElement().setRotationAngle(new Quaternion(new Vector3(1,0,0),-45));   // 设置延X轴旋转45度
        lighting.getParentElement().setRotationAngle(new Quaternion(new Vector3(0,1,0),45));
    }

    /**
     * 创建CAR
     * @param point2Ds
     * @param speed
     */
    private Point3D offset = new Point3D(0,0,0.005f);
    //车辆动画组
    private ARAnimationGroup carAnimationGroup = ARAnimationManager.getInstance().addAnimationGroup("car");
    private void createCarEntity(Point2Ds point2Ds, float speed, int i){

        CarEntity carEntity = new CarEntity(mapElement);
        //绑定地图 用于将数据集中的数据转换为地图上的3D点
        carEntity.bindMap(map);
        //绑定动画组
        carEntity.bindAnimationGroup(carAnimationGroup);
        carEntity.setRoadLine(point2Ds);
        carEntity.setSpeed(speed);
        carEntity.setOffset(offset);
        carEntity.setName("car" + i);
        //Init
        carEntity.init(i);
        carEntities.add(carEntity);
    }

    private void renderDotted(List<Point3D> pts, int num){

        ARGeoDottedLine dottedLine = new ARGeoDottedLine(BaseShape.MatType.OPAQUE);
        dottedLine.setParentNode(mapElement.getArObjParent());
        dottedLine.setSolidLineLength(0.015f);
        dottedLine.setDottedLineLength(0.025f);
        dottedLine.setRadius(0.0025f);
        dottedLine.setColor("#FF5C5C5C");
        dottedLine.addPart(pts);
    }

    private List<ARGltfElement> effectElements = new ArrayList<>();
    private long currentTime = 0L;  //实时时刻
    public void setOrientationListener(){
        onUpdateListener1 = new EffectView.OnUpdateListener() {
            @Override
            public void onUpdate() {
//                try {
                for (ARGltfElement v: effectElements){
                    Quaternion quaternion = getQuaternion(v);
                    v.setRotationAngleNoRepeat(Quaternion.multiply(quaternion, q1) );
                }


                if ((System.currentTimeMillis() - currentTime) > 300){
                    // 小车靠近的动画
                    B: for (PoiBean bean: targetPoiLists){
                        // 取出每个目标配送点
                        Point3D pPoi = bean.point;     // 碰撞点
                        pPoi.z = 0;

                        for (CarEntity e: carEntities){
                            // 得到每辆车实体
//                            Point3D position = e.getCarModel().getRelativePosition();   // 汽车位置点
                            Point3D position = e.getCarModel().getPosition();   // 汽车位置点
                            position.z = 0.0f;
                            float distance = ConvertTool.getDistance(position, pPoi);
//                            Log.d(TAG, "onUpdate111: "+bean.gltfElementPoi.getElementName() + "..." + distance);
                            if (distance <= 0.21) {  // 当距离小于这个阈值说明，当前POI与当前小车很近，POI要产生动画
                                playAnimation(true, bean);
                                continue B;    // 只要有一个车辆让当前建筑物变色，那么就结束内循环直接判断下一个建筑物
                            }
                        }

                        // 所有小车距离当前POI很远 所有POI暂停动画
                        playAnimation(false, bean);

                    }

                    currentTime = System.currentTimeMillis();
                }


            }
        };
        arEffectView.addOnUpdateListener(onUpdateListener1);

        resHashMap.put(1, R.raw.point1);
        resHashMap.put(2, R.raw.point2);
        resHashMap.put(3, R.raw.point3);
        resHashMap.put(4, R.raw.point4);
        resHashMap.put(5, R.raw.point5);
        resHashMap.put(6, R.raw.point6);
        resHashMap.put(7, R.raw.point7);
        resHashMap.put(8, R.raw.point8);
        resHashMap.put(9, R.raw.point9);


        resHashMapCenter.put(1, R.raw.center1);
        resHashMapCenter.put(2, R.raw.center2);
    }

    public Quaternion getQuaternion(ARGltfElement viewElement){
        Quaternion lookRotation = arEffectView.getLookRotation(viewElement);
        lookRotation.x = 0;
        lookRotation.z = 0;

        return lookRotation.normalized();
    }


    private HashMap<Integer, Integer> resHashMap = new HashMap();
    private HashMap<Integer, Integer> resHashMapCenter = new HashMap();

    private List<PoiBean> targetPoiLists = new ArrayList<>();


    private void playAnimation(boolean play, PoiBean bean){
        if (play){
            if (bean.isBusy) return;
            ARAnimationManager.getInstance().playAnimationGroup(bean.gltfElementPoi.getElementName());
            ARAnimationManager.getInstance().playAnimationGroup(bean.gltfElementTxt.getElementName());
            bean.isBusy = true;
        }else{
            ARAnimationManager.getInstance().pauseAnimationGroup(bean.gltfElementPoi.getElementName());
            ARAnimationManager.getInstance().pauseAnimationGroup(bean.gltfElementTxt.getElementName());
            bean.isBusy = false;
        }
    }



    public void addAnimation(ARGltfElement gltfElement, Point3D point3D){
        ARAnimationGroup animationGroup = ARAnimationManager.getInstance().addAnimationGroup(gltfElement.getElementName());//根据id创建唯一动画组
        ARAnimationParameter arAnimationParameter1 = new ARAnimationParameter();
        arAnimationParameter1.setDuration(2200L);
        arAnimationParameter1.setStartPosition(point3D);
        arAnimationParameter1.setEndPosition(new Point3D(point3D.x, point3D.y-0.13f, point3D.z));
        arAnimationParameter1.setRepeatMode(ARAnimationRepeatMode.REVERSE);
        // 上下
        ARAnimationTranslation translation2 = new ARAnimationTranslation(gltfElement);
        translation2.creatAnimation(arAnimationParameter1);
        animationGroup.addAnimation(translation2);
    }

    public void addTxtAnimation(ARGltfElement gltfElement, Point3D point3D){
        ARAnimationGroup animationGroup = ARAnimationManager.getInstance().addAnimationGroup(gltfElement.getElementName());//根据id创建唯一动画组
        // 设置旋转动画参数
        ARAnimationParameter arAnimationParameter1 = new ARAnimationParameter();
        arAnimationParameter1.setDuration(2200L);
        arAnimationParameter1.setStartPosition(point3D);
        arAnimationParameter1.setEndPosition(new Point3D(point3D.x, point3D.y, point3D.z+0.13f));
        arAnimationParameter1.setRepeatMode(ARAnimationRepeatMode.REVERSE);
        // 上下
        ARAnimationTranslation translation2 = new ARAnimationTranslation(gltfElement);
        translation2.creatAnimation(arAnimationParameter1);
        animationGroup.addAnimation(translation2);
    }
}
