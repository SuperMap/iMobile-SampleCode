package com.supermap.imobile.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.FieldInfos;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.imobile.naviintegration.DoorInfo;
import com.supermap.imobile.naviintegration.MainActivity;
import com.supermap.imobile.naviintegration.R;
import com.supermap.indoor.FloorListView;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.Navigation2;
import com.supermap.navi.Navigation3;

/**
 * 导航界面
 */
public class NaviFragment extends Fragment implements View.OnClickListener {

    private MapControl mMapControl;
    private Navigation3 mNavigationEx;
    private Navigation2 mNavigation2;
    private FloorListView mFloorListView = null;
    private Point2D            startPoint        = null;
    private Point2D            destPoint         = null;
    private MapView mMapView;
    //室内室外状态
    private boolean longPressEnable = false;
    private boolean setStartIndoorPoint   = false;
    private boolean setStartOutdoorPoint    = false;
    private boolean setDestIndoorPoint   = false;
    private boolean setDestOutdoorPoint    = false;

    //起点是否是室内点
    public boolean bStartIndoor = true;
    //终点是否是室内点
    public boolean bEndIndoor = true;

    private Button mIndoorStrat,mOutdoorStart,mIndoorEnd,mOutdoorEnd;
    private RelativeLayout layoutStart,layoutEnd;
    public LinearLayout layoutTools;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_indoornavi, container, false);
        layout.findViewById(R.id.btn_start_point).setOnClickListener(this);
        layout.findViewById(R.id.btn_end_point).setOnClickListener(this);
        layout.findViewById(R.id.btn_analyse_path).setOnClickListener(this);
        layout.findViewById(R.id.btn_navi).setOnClickListener(this);
        layout.findViewById(R.id.btn_clear).setOnClickListener(this);
        layout.findViewById(R.id.btn_navi_other).setOnClickListener(this);

        mIndoorStrat = layout.findViewById(R.id.btn_indoor_start);
        mIndoorStrat.setOnClickListener(this);
        mOutdoorStart = layout.findViewById(R.id.btn_outdoor_start);
        mOutdoorStart.setOnClickListener(this);
        mIndoorEnd = layout.findViewById(R.id.btn_indoor_end);
        mIndoorEnd.setOnClickListener(this);
        mOutdoorEnd = layout.findViewById(R.id.btn_outdoor_end);
        mOutdoorEnd.setOnClickListener(this);

        layoutStart = (RelativeLayout)layout.findViewById(R.id.start_point);
        layoutEnd = (RelativeLayout)layout.findViewById(R.id.end_point);
        layoutTools = (LinearLayout)layout.findViewById(R.id.layout_btns_navi);

        // 设置手势事件监听
        mMapControl.setGestureDetector(new GestureDetector(activity_main, longTouchListener));
        return layout;

    }

    //可见性改变
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

            mNavigation2.cleanPath();
            mNavigationEx.cleanPath();
            mMapControl.getMap().getTrackingLayer().clear();
            mMapView.removeAllCallOut();
            startPoint = null;
            destPoint = null;

            longPressEnable = false;
            setStartIndoorPoint   = false;
            setStartOutdoorPoint    = false;
            setDestIndoorPoint   = false;
            setDestOutdoorPoint    = false;

    }
    MainActivity activity_main = null;
     @Override
     public void onAttach(Activity activity){
            super.onAttach(activity);
            activity_main = (MainActivity)activity;
            mMapControl = activity_main.getMapControl();
            mNavigationEx = activity_main.getNavigationEx();
            mNavigation2 = activity_main.getNavigation2();
            mFloorListView = activity_main.getFloorListView();
            mMapView = activity_main.getMapView();
     }

//     Context context;
    @Override
    public void onClick(View view) {
         int id = view.getId();
        switch (id){
            case R.id.btn_start_point:
                if(layoutStart.getVisibility() == View.VISIBLE){
                    layoutStart.setVisibility(View.INVISIBLE);
                }else {
                    layoutStart.setVisibility(View.VISIBLE);
                }
                layoutEnd.setVisibility(View.INVISIBLE);

               break;
            case R.id.btn_end_point:

                if(layoutEnd.getVisibility() == View.VISIBLE){
                    layoutEnd.setVisibility(View.INVISIBLE);
                }else {
                    layoutEnd.setVisibility(View.VISIBLE);
                }
                layoutStart.setVisibility(View.INVISIBLE);

                break;
            case R.id.btn_indoor_start:

                layoutStart.setVisibility(View.INVISIBLE);
                longPressEnable = true;
                setStartIndoorPoint = true;
                setStartOutdoorPoint = false;
                setDestIndoorPoint = false;
                setDestOutdoorPoint = false;
                break;
            case R.id.btn_outdoor_start:

                layoutStart.setVisibility(View.INVISIBLE);
                longPressEnable = true;
                setStartOutdoorPoint = true;
                setStartIndoorPoint = false;
                setDestIndoorPoint = false;
                setDestOutdoorPoint = false;
                break;
            case R.id.btn_indoor_end:

                layoutEnd.setVisibility(View.INVISIBLE);
                longPressEnable = true;
                setDestIndoorPoint = true;
                setDestOutdoorPoint = false;
                setStartIndoorPoint = false;
                setStartOutdoorPoint = false;
                break;
            case R.id.btn_outdoor_end:

                layoutEnd.setVisibility(View.INVISIBLE);
                longPressEnable = true;
                setDestOutdoorPoint = true;
                setDestIndoorPoint = false;
                setStartIndoorPoint = false;
                setStartOutdoorPoint = false;
                break;
            case R.id.btn_analyse_path:

                routeAnalyst();
                break;
            case R.id.btn_navi:

                firstPersonPathNavi();
                break;
            case R.id.btn_clear:
                mNavigationEx.cleanPath();
                mNavigation2.cleanPath();
                mMapView.removeAllCallOut();
                mMapControl.getMap().getTrackingLayer().clear();
                break;
            case R.id.btn_navi_other:
                thirdPersonPathNavi();
                break;
        }
    }

    /**
     * 路径分析
     */
    private void routeAnalyst() {
        if(bStartIndoor && bEndIndoor){//起终点室内
            indoorRouteAnalyst();
        }else if(bStartIndoor){//起点室内，终点室外
            //得到门口点
            Dataset dataset =mMapControl.getMap().getWorkspace().getDatasources().get("supermap").getDatasets().get("supermap_port");
            DoorInfo indoorpt = getDoorPoint(dataset,destPoint);
            mNavigationEx.setDestinationPoint(indoorpt.getDoorpoint().getX(),indoorpt.getDoorpoint().getY(),indoorpt.getFl_id());
            mNavigationEx.setEndPointBMP(Bitmap.createBitmap(2,2, Bitmap.Config.ARGB_8888));
            mNavigation2.setStartPoint(indoorpt.getDoorpoint().getX(),indoorpt.getDoorpoint().getY());
            boolean bIndoorRoute  = indoorRouteAnalyst();
            boolean bOutdoorRoute  = outdoorRouteAnalyst();
            if(bIndoorRoute && bOutdoorRoute){
                Point2D routeStartPoint = mNavigation2.getRouteStartPoint();//new Point2D(116.4999997717226,39.98439660036092);
                //显示虚线
                activity_main.showNoNaviLine(indoorpt.getDoorpoint(),routeStartPoint);
            }

        }else if(bEndIndoor){//终点室内，起点室外

            Dataset dataset =mMapControl.getMap().getWorkspace().getDatasources().get("supermap").getDatasets().get("supermap_port");
            DoorInfo indoorpt = getDoorPoint(dataset,destPoint);

            mNavigation2.setDestinationPoint(indoorpt.getDoorpoint().getX(),indoorpt.getDoorpoint().getY());
            mNavigationEx.setStartPoint(indoorpt.getDoorpoint().getX(),indoorpt.getDoorpoint().getY(),indoorpt.getFl_id());
            mNavigationEx.setStartPointBMP(Bitmap.createBitmap(2,2, Bitmap.Config.ARGB_8888));
            boolean bIndoorRoute  = indoorRouteAnalyst();
            boolean bOutdoorRoute  = outdoorRouteAnalyst();
            if(bIndoorRoute && bOutdoorRoute){
                Point2D routeEndPoint = mNavigation2.getRouteEndPoint();
                //显示虚线
                activity_main.showNoNaviLine(indoorpt.getDoorpoint(),routeEndPoint);
            }

        }else {//起终点室外
            outdoorRouteAnalyst();
        }

    }
    /**
     * 得到门点
     */
    public DoorInfo getDoorPoint(Dataset dataset, Point2D pt){

        DoorInfo doorInfo = new DoorInfo();
        DatasetVector datasetVector = (DatasetVector)dataset;
        Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
        //获取门口点
        double distance = 0.0;
        double tmpDistance = 0.0;
        boolean bFirstPoint = true;
        Point2D pt2D = null;
        String fl_id = "";
        while (!recordset.isEOF()){
            GeoPoint geoPoint = (GeoPoint) recordset.getGeometry();
            GeoPoint geoOutPoint = new GeoPoint(pt);
            //计算距离
            distance = Geometrist.distance(geoPoint,geoOutPoint);
            FieldInfos fieldInfos = recordset.getFieldInfos();
            Object ob;

            if( bFirstPoint){
                pt2D = geoPoint.getInnerPoint();
                tmpDistance = distance;
                bFirstPoint = false;
                if( fieldInfos.indexOf("FL_ID") != -1){
                    ob = recordset.getFieldValue("FL_ID");
                    if (ob != null) {
                        fl_id = ob.toString();
                    }
                }
            }else {
                if(distance < tmpDistance){
                    tmpDistance = distance;
                    pt2D = geoPoint.getInnerPoint();
                    if( fieldInfos.indexOf("FL_ID") != -1){
                        ob = recordset.getFieldValue("FL_ID");
                        if (ob != null) {
                            fl_id = ob.toString();
                        }
                    }
                }
            }
            recordset.moveNext();
        }

        doorInfo.setDoorpoint(pt2D);
        doorInfo.setFl_id(fl_id);
        //返回最短距离点
        return doorInfo;
    }

    /**
     * 室内路径分析
     * @return
     */
    private boolean indoorRouteAnalyst() {
        //判断室内地图数据源是否加载成功
        Datasource datasource = mFloorListView.getIndoorDatasource();
        if (datasource == null) {
            Toast.makeText(mMapControl.getContext(), "室内地图数据源加载失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        //设置室内地图所在的数据源
        mNavigationEx.setDatasource(datasource);

        //路径分析
        boolean bIndoorAnalystResult = mNavigationEx.routeAnalyst();
        if(bIndoorAnalystResult){
            Toast.makeText(mMapControl.getContext(), "分析成功", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(mMapControl.getContext(), "室内路径分析失败，确认是否添加导航点", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    /**
     * 室外路径分析
     * @return
     */
    private boolean outdoorRouteAnalyst() {
        //路径分析
        boolean bOutdoorAnalystResult = mNavigation2.routeAnalyst();
        mMapControl.getMap().refresh();
        if(bOutdoorAnalystResult){

            Toast.makeText(mMapControl.getContext(), "分析成功", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(mMapControl.getContext(), "室外路径分析失败，确认是否添加导航点", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    /**
     * 第三人称导航
     *
     */
    public void thirdPersonPathNavi() {

        mNavigationEx.setCarUpFront(false);
        mNavigation2.setCarUpFront(false);
        if(bStartIndoor){
            startIndoorNavi();
        }else{
            startOutdoorNavi();
        }
    }
    /**
     * 第一人称导航
     *
     */
    public void firstPersonPathNavi() {

        mNavigationEx.setCarUpFront(true);
        mNavigation2.setCarUpFront(true);

        if(bStartIndoor){
            startIndoorNavi();
        }else{
            startOutdoorNavi();
        }

    }

    /**
     * 室内导航
     */
    public void startIndoorNavi(){

        if(!mNavigationEx.startGuide(1)){
            Toast.makeText(mMapControl.getContext(), "导航启动失败", Toast.LENGTH_SHORT).show();
            return;
        }else {
            layoutTools.setVisibility(View.INVISIBLE);
            return;
        }
    }

    /**
     * 室外导航
     */
    public void startOutdoorNavi(){
        //开始导航  0：真实导航， 1：模拟导航 ,2：巡航 ,3：步行导航

        if(!mNavigation2.startGuide(1)){
            Toast.makeText(mMapControl.getContext(), "导航启动失败", Toast.LENGTH_SHORT).show();
            return;
        }else {
            layoutTools.setVisibility(View.INVISIBLE);
            return;
        }
    }
    /**
     * 从屏幕 获取起点
     * @param event
     * @return
     */
    private Point2D getStartPoint(MotionEvent event) {
        int x = 0;
        int y = 0;
        if (event == null) {
            x = 50;
            y = 50;
        } else {
            x = (int) event.getX();
            y = (int) event.getY();
        }
        Point point = new Point(x, y);
        return getPoint(point);
    }

    /**
     * 从屏幕 获取终点
     * @param event
     * @return
     */
    private Point2D getDestPoint(MotionEvent event) {
        int x = 0;
        int y = 0;
        if (event == null) {
            x = 200;
            y = 200;
        } else {
            x = (int) event.getX();
            y = (int) event.getY();
        }
        Point point = new Point(x, y);
        return getPoint(point);
    }

    /**
     * 将屏幕上的点转换为地图上的点和经纬坐标点
     * @return
     */
    private Point2D getPoint(Point point) {
        Point2D point2D = null;

        // 转换为地图上的二维点
        point2D = mMapControl.getMap().pixelToMap(point);

        if (mMapControl.getMap().getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
            PrjCoordSys srcPrjCoordSys = mMapControl.getMap().getPrjCoordSys();
            Point2Ds point2Ds = new Point2Ds();
            point2Ds.add(point2D);
            PrjCoordSys desPrjCoordSys = new PrjCoordSys();
            desPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
            // 转换投影坐标
            CoordSysTranslator.convert(point2Ds, srcPrjCoordSys,
                    desPrjCoordSys, new CoordSysTransParameter(),
                    CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

            point2D = point2Ds.getItem(0);
        }

        return point2D;
    }
    // 长按监听事件
    private GestureDetector.SimpleOnGestureListener longTouchListener = new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent event) {
            if (!longPressEnable)
                return;
            String currentFloorID ="";
            if(setStartIndoorPoint || setDestIndoorPoint){
                //获取当前楼层ID
                currentFloorID = mFloorListView.getCurrentFloorId();
                if (currentFloorID == null) {
                    Toast.makeText(mMapControl.getContext(), "请先打开室内地图", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (setStartIndoorPoint || setStartOutdoorPoint){

                startPoint = getStartPoint(event);
                mMapView.removeCallOut("startpoint");
//                mNavigationEx.setStartPointBMP(Bitmap.createBitmap(2,2, Bitmap.Config.ARGB_8888));
                if(setStartOutdoorPoint){

                    bStartIndoor = false;
                    activity_main.showPointByCallout(startPoint, "startpoint", R.drawable.icon_track_navi_start);
                    mNavigation2.setStartPoint(startPoint.getX(),startPoint.getY());
                }
                else{
                    GeoPoint geoPoint = new GeoPoint(startPoint);
                    DatasetVector datasetVector = (DatasetVector)activity_main.getWorkspace().getDatasources().get("bounds").getDatasets().get("Building");
                    Recordset recordset = datasetVector.getRecordset(false,CursorType.STATIC);
                    GeoRegion geoRegion = (GeoRegion) recordset.getGeometry();
                    if(!Geometrist.isWithin(geoPoint,geoRegion)){
                        Toast.makeText(mMapControl.getContext(), "请选择室内范围数据", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    bStartIndoor = true;
                    mNavigationEx.setStartPoint(startPoint.getX(),startPoint.getY(),currentFloorID);

                }
            }
            if (setDestIndoorPoint || setDestOutdoorPoint){
                destPoint = getDestPoint(event);
                mMapView.removeCallOut("endpoint");
//                mNavigationEx.setEndPointBMP(Bitmap.createBitmap(2,2, Bitmap.Config.ARGB_8888));
                if(setDestOutdoorPoint){
                    bEndIndoor = false;
                    activity_main.showPointByCallout(destPoint, "endpoint", R.drawable.icon_track_navi_end);
                    mNavigation2.setDestinationPoint(destPoint.getX(),destPoint.getY());
                }
                else{
                    GeoPoint geoPoint = new GeoPoint(destPoint);
                    DatasetVector datasetVector = (DatasetVector)activity_main.getWorkspace().getDatasources().get("bounds").getDatasets().get("Building");
                    Recordset recordset = datasetVector.getRecordset(false,CursorType.STATIC);
                    GeoRegion geoRegion = (GeoRegion) recordset.getGeometry();
                    if(!Geometrist.isWithin(geoPoint,geoRegion)){
                        Toast.makeText(mMapControl.getContext(), "请选择室内范围数据", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    bEndIndoor = true;
                    mNavigationEx.setDestinationPoint(destPoint.getX(),destPoint.getY(),currentFloorID);
                }
            }

            longPressEnable = false;
        }
//        // 地图漫游
//        public boolean onScroll(MotionEvent e1, MotionEvent e2,
//                                float distanceX, float distanceY) {
//            if (m_Navigation2.isGuiding())
//                m_Navigation2.enablePanOnGuide(true);
//            return false;
//        }

    };


}