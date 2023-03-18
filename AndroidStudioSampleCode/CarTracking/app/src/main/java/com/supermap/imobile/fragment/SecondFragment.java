package com.supermap.imobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.supermap.data.*;
import com.supermap.imobile.dynamicpoi.PoiOverlay;
import com.supermap.imobile.dynamicpoi.ScalesCallout;
import com.supermap.imobile.streamingapp.DeviceOrientation;
import com.supermap.imobile.streamingapp.R;
import com.supermap.imobile.streamingservice.DataFlowService;
import com.supermap.mapping.*;
import com.supermap.mapping.Map;
import com.supermap.mapping.dyn.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static android.app.Activity.RESULT_OK;

/**
 * 界面二
 */
public class SecondFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SecondFragment";
    private final String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private Context mContext = null;

    private FloatingActionButton floatingActionButton = null;
    private MapView mapView;
    private MapControl mapControl;
    private Map map;
    private Workspace workspace;

    private DynamicView m_dynamicLayer;
    private DataFlowService dataFlowService;

    private ArrayList<Integer> idList = new ArrayList<>();

    private HashMap<Integer, Point2D> point2DHashMap = new HashMap<>();

    private DynamicCircle dynCircle = null;
    private DynamicPoint dynPoint = null;
    private Gson gson = new Gson();
    private Handler handler = new Handler();

    private FloatingSearchView mSearchView;

//    private Point2D point2D01 = new Point2D(1.2982596646168513E7, 4857778.948683128);
//    private Point2D point2D02 = new Point2D(1.2982591093609799E7, 4857600.309674066);
//    private Point2D point2D03 = new Point2D(1.2982591235353377E7, 4857058.083083267);
//    private Point2D point2D04 = new Point2D(1.2982467972334664E7, 4857050.578723187);
//    private Point2D point2D05 = new Point2D(1.2982301425216269E7, 4857042.44560722);
//
//    private Point2D point2D06 = new Point2D(1.298231073613871E7, 4857229.52235898);
//    private Point2D point2D07 = new Point2D(1.2982312401959758E7, 4857446.5818781275);
//    private Point2D point2D08 = new Point2D(1.2982313119431943E7, 4857604.947628593);
//    private Point2D point2D09 = new Point2D(1.298231247289178E7, 4857827.852903218);
//    private Point2D point2D010 = new Point2D(1.2982315481384762E7, 4858142.077197182);
//
//    private Point2D point2D011 = new Point2D(1.298207930424059E7, 4858140.572829724);
//    private Point2D point2D012 = new Point2D(1.2981885311616587E7, 4858138.839682389);
//    private Point2D point2D013 = new Point2D(1.2981579181399895E7, 4858138.766233189);
//    private Point2D point2D014 = new Point2D(1.2980955634192526E7, 4858136.636067224);
//    private Point2D point2D015 = new Point2D(1.2980737224966774E7, 4858137.260885964);
//
//    private Point2D point2D016 = new Point2D(1.2980616891208313E7, 4858244.02016225);
//    private Point2D point2D017 = new Point2D(1.2980482659392744E7, 4858084.955106015);
//    private Point2D point2D018 = new Point2D(1.2980305500070715E7, 4857883.376361557);
//    private Point2D point2D019 = new Point2D(1.298003554715661E7, 4857561.912221261);
//    private Point2D point2D020 = new Point2D(1.2979854715205096E7, 4857348.058009785);
//
//    private Point2D point2D021 = new Point2D(1.2979643307326289E7, 4857097.854186402);
//    private Point2D point2D022 = new Point2D(1.2979519343658617E7, 4856955.454410158);
//    private Point2D point2D023 = new Point2D(1.2979290239173437E7, 4856682.681512203);
//    private Point2D point2D024 = new Point2D(1.2979103694753425E7, 4856462.833498014);
//    private Point2D point2D025 = new Point2D(1.3534979981919726E7, 3670189.658254801);
//
//    private Point2D point2D026 = new Point2D(1.3534924606983347E7, 3670400.5591120757);
//    private Point2D point2D027 = new Point2D(1.3534858929921005E7, 3670563.189937943);
//    private Point2D point2D028 = new Point2D(1.353464207996968E7, 3670476.2823664425);
//    private Point2D point2D029 = new Point2D(1.353447393349927E7, 3670412.1804937935);
//    private Point2D point2D030 = new Point2D(1.3534222676772239E7, 3670308.453845593);
//
//    private Point2D point2D031 = new Point2D(1.3533940307435323E7, 3670185.1416275785);
//    private Point2D point2D032 = new Point2D(1.353369291091516E7, 3670065.7708469094);
//    private Point2D point2D033 = new Point2D(1.3533493851955466E7, 3669968.2010541884);

    private Point2D point2D01 = new Point2D(1.2979436941646982E7, 4859344.765429385);
    private Point2D point2D02 = new Point2D(1.2979393284876639E7, 4859300.101761437);
    private Point2D point2D03 = new Point2D(1.2979279475536207E7, 4859397.241059697);
    private Point2D point2D04 = new Point2D(1.2979144637066878E7, 4859510.011340967);
    private Point2D point2D05 = new Point2D(1.2978979785731252E7, 4859314.28851003);

    private Point2D point2D06 = new Point2D(1.2978809601513218E7, 4859119.761447743);
    private Point2D point2D07 = new Point2D(1.2978687898599219E7, 4858973.332706664);
    private Point2D point2D08 = new Point2D(1.2978579016549509E7, 4858844.86936034);
    private Point2D point2D09 = new Point2D(1.2978449591580154E7, 4858963.213537586);
    private Point2D point2D010 = new Point2D(1.2978381060702767E7, 4859071.281459617);

    private Point2D point2D011 = new Point2D(1.297834959052304E7, 4859236.8018606845);
    private Point2D point2D012 = new Point2D(1.2978341378190361E7, 4859372.568905801);
    private Point2D point2D013 = new Point2D(1.2978335501012377E7, 4859495.384426226);
    private Point2D point2D014 = new Point2D(1.2978100578701971E7, 4859489.160111561);
    private Point2D point2D015 = new Point2D(1.2977741801132193E7, 4859481.434084042);

    private Point2D point2D016 = new Point2D(1.2977482602592697E7, 4859476.927154279);
    private Point2D point2D017 = new Point2D(1.2977352574606674E7, 4859473.542347701);
    private Point2D point2D018 = new Point2D(1.2977172572417025E7, 4859470.787417061);
    private Point2D point2D019 = new Point2D(1.297715163213303E7, 4859470.653825679);
    private Point2D point2D020 = new Point2D(1.2977147448260501E7, 4859343.022165862);

    private Point2D point2D021 = new Point2D(1.2977140674856411E7, 4859168.322134571);
    private Point2D point2D022 = new Point2D(1.2976991163552148E7, 4859142.60552496);
    private Point2D point2D023 = new Point2D(1.2976895069805264E7, 4859126.43026377);
    private Point2D point2D024 = new Point2D(1.2976882778677652E7, 4859191.056954803);
    private Point2D point2D025 = new Point2D(1.2976836906180628E7, 4859181.622542406);

//    private double Center_x = 1.2982517412188385E7;
//    private double Center_y = 4857798.020381248;

    private double Center_x = 1.297832709607983E7;
    private double Center_y = 4858870.304666624;

    private double Center_x1 = 121.38;
    private double Center_y1 = 31.30;

    private double Center_x2 = 121.46;
    private double Center_y2 = 31.31;

    private double Center_x3 = 121.43;
    private double Center_y3 = 31.25;

    private double Center_x4 = 121.50;
    private double Center_y4 = 31.26;

    private double Center_x5 = 121.54;
    private double Center_y5 = 31.275;

    private double Center_x6 = 121.58;
    private double Center_y6 = 31.28;

    private double Center_x7 = 121.63;
    private double Center_y7 = 31.26;

    private String time1 = "08时20分";
    private String time2 = "08时28分";
    private String time3 = "08时35分";
    private String time4 = "08时42分";
    private String time5 = "08时50分";
    private String time6 = "08时57分";
    private String time7 = "09时12分";


    private Point2D point2D_car01 = new Point2D(1.2979161276770493E7, 4859496.390770299);
    private Point2D point2D_car02 = new Point2D(1.2978582933299249E7, 4858841.029917445);

    private Point2D point2D_car03 = new Point2D(1.2978296473584542E7, 4859482.772216247);
    private Point2D point2D_car04 = new Point2D(1.2977166552882697E7, 4859459.054827996);

    private Point2D point2D_car05 = new Point2D(1.2977264788473636E7, 4858847.8246644);
    private Point2D point2D_car06 = new Point2D(1.2977746138544625E7, 4858933.738673548);

    private Point2D point2D_car07 = new Point2D(1.2977151290330887E7, 4859341.538692472);
    private Point2D point2D_car08 = new Point2D(1.2977128150607929E7, 4858837.859889686);

    private Point2D point2D_car09 = new Point2D(1.2977773432479106E7, 4858499.821282228);
    private Point2D point2D_car10 = new Point2D(1.297776094191609E7, 4858925.383056928);

    private Point2D point2D_car11 = new Point2D(1.2978828584053442E7, 4859097.848817527);
    private Point2D point2D_car12 = new Point2D(1.2979375591876986E7, 4858623.950536079);

    private Point2D point2D_car13 = new Point2D(1.2978083897022597E7, 4858178.278578231);
    private Point2D point2D_car14 = new Point2D(1.2978830631529432E7, 4857547.564339805);

    private Point2D point2D_car15 = new Point2D(1.2979721077479238E7, 4859008.956780012);
    private Point2D point2D_car16 = new Point2D(1.2980490220498513E7, 4858334.417261324);

    private Point2D point2D_car17 = new Point2D(1.2980948090495719E7, 4858775.920583299);
    private Point2D point2D_car18 = new Point2D(1.2979969807607409E7, 4857648.828209367);

    private Point2D point2D_car19 = new Point2D(1.297804120982671E7, 4856117.826503217);
    private Point2D point2D_car20 = new Point2D(1.2976275555243634E7, 4857624.39839758);

    DeviceOrientation deviceOrientation = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second, container, false);


        //方位角变化监听
//        deviceOrientation = new DeviceOrientation(mContext.getApplicationContext());
//        deviceOrientation.setOrientationChangeListener(value -> {
//            Point2D point2D = new Point2D(1.2971038159960426E7,4858307.794140533);
//            myLocation(point2D.getX(), point2D.getY(), (int) value);
//        });

        mSearchView = (FloatingSearchView) rootView.findViewById(R.id.floating_search_view);
        setupFloatingSearch();
        initView(rootView);
        initListening(rootView);
        openWorkspace();
        return rootView;
    }

    private void setupFloatingSearch() {

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String query) {

                searchAction();

               Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {
                mSearchView.clearQuery();
                Log.d(TAG, "onFocusCleared()");
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_voice_rec) {
                    mSearchView.clearSearchFocus();

                    choosePic();
                }
            }
        });

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                Log.d(TAG, "onClearSearchClicked()");
            }
        });
    }

    public void searchAction() {
        //操作 沪AZ0518
        handler.removeCallbacks(runnable);

        if (dataFlowService != null) {
            dataFlowService.close();
        }
        m_dynamicLayer.clear();

        List<Point2D> listsrc = new ArrayList<>();
        List<Point2D> listdes = new ArrayList<>();
        listsrc.add(point2D01);
        listsrc.add(point2D02);
        listsrc.add(point2D03);
        listsrc.add(point2D04);
        listsrc.add(point2D05);

        listsrc.add(point2D06);
        listsrc.add(point2D07);
        listsrc.add(point2D08);
        listsrc.add(point2D09);
        listsrc.add(point2D010);

        listsrc.add(point2D011);
        listsrc.add(point2D012);
        listsrc.add(point2D013);
        listsrc.add(point2D014);
        listsrc.add(point2D015);

        listsrc.add(point2D016);
        listsrc.add(point2D017);
        listsrc.add(point2D018);
        listsrc.add(point2D019);
        listsrc.add(point2D020);

        listsrc.add(point2D021);
        listsrc.add(point2D022);
        listsrc.add(point2D023);
        listsrc.add(point2D024);
        listsrc.add(point2D025);

        for (int i = 0; i < listsrc.size(); i++) {
            Point2D point2D = listsrc.get(i);
            PrjCoordSys desPrj = mapControl.getMap().getPrjCoordSys();

            //当投影不是经纬坐标系时，则对点进行投影转换
//            if (desPrj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
//                Point2Ds points = new Point2Ds();
//                points.add(point2D);
//                PrjCoordSys srcPrjCoorSys = new PrjCoordSys();
//                srcPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
//                CoordSysTranslator.convert(points, srcPrjCoorSys, desPrj,
//                        new CoordSysTransParameter(),
//                        CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
//
//                point2D.setX(points.getItem(0).getX());
//                point2D.setY(points.getItem(0).getY());
//            }

//            Point2Ds points = new Point2Ds();
//            points.add(point2D);
//            PrjCoordSys srcPrjCoorSys = new PrjCoordSys();
//            srcPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
//            CoordSysTranslator.convert(points, srcPrjCoorSys, desPrj,
//                    new CoordSysTransParameter(),
//                    CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
//
//            point2D.setX(points.getItem(0).getX());
//            point2D.setY(points.getItem(0).getY());

            listdes.add(point2D);

//            Collections.reverse(listdes);//倒序

            if (i == 0 || i == 4 || i == 7 || i == 12 || i == 14 || i ==18 || i ==24) {
                addCalloutCar(point2D, i, getDayTime(), getCurrentTime() + "_" + i);
            }
            Log.e(TAG, "x="+ point2D.getX() + ", y=" + point2D.getY());


            testDynamicPoi();
        }

        // 跟踪层
        for (int i = 1; i < listdes.size(); i++) {
            Point2Ds point2Ds = new Point2Ds();
            point2Ds.add(listdes.get(i - 1));
            point2Ds.add(listdes.get(i));
            GeoLine geoLine = new GeoLine(point2Ds);
            TrackingLayer trackingLayer = map.getTrackingLayer();

            GeoStyle lineStyle = geoLine.getStyle();
            if (lineStyle == null) lineStyle = new GeoStyle();
            lineStyle.setLineWidth(1);
            lineStyle.setLineColor(new com.supermap.data.Color(	65,105,225));
//                    lineStyle.setLineSymbolID(965150);
            geoLine.setStyle(lineStyle);
            trackingLayer.add(geoLine, "line_" + i);
        }

        mapControl.panTo(listdes.get(listdes.size() - 1), 100);
        mapControl.getMap().setScale(0.00005);
    }

    private void openWorkspace() {
        workspace = new Workspace();
        map = mapView.getMapControl().getMap();
        map.setWorkspace(workspace);
        mapControl = mapView.getMapControl();

        // 设置手势事件监听, 打点获取坐标
        mapControl.setGestureDetector(new GestureDetector(mContext, touchListener));

//        final String dataPath = SDCARD + "/SampleData/云南/云南.smwu";
//        final String dataPath = SDCARD + "/SampleData/GeometryInfo/World.smwu";
//        final String dataPath = SDCARD + "/SampleData/WorldNight/World.smwu";
//        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
//        info.setServer(dataPath);
//        info.setType(WorkspaceType.SMWU);
//        boolean isOpen = workspace.open(info);
//        if (!isOpen) {
//            return;
//        }
//        String name = workspace.getMaps().get(0);
//        map.open(name);
//        map.setViewBounds(map.getBounds());


//        {
//            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
//            info.setAlias("TianDiTu1");
//            info.setEngineType(EngineType.OGC);
//            info.setDriver("WMTS");
//            String url = "http://t0.tianditu.com/vec_c/wmts?tk=2ce94f67e58faa24beb7cb8a09780552";
//            info.setServer(url);
//            Datasource datasource = workspace.getDatasources().open(info);
//            if (datasource != null) {
//                mapControl.getMap().getLayers().add(datasource.getDatasets().get(0), true);
//            }
//        }

//        {
//            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
//            info.setAlias("testRest");
//            info.setEngineType(EngineType.SuperMapCloud);
//            String url = "http://support.supermap.com.cn:8090/iserver/services/map-china400/rest/maps/China_4326";
////            String url = "http://support.supermap.com.cn:8090/iserver/services/map-china400/rest/maps/China";
////            String url = "http://192.168.0.28:8090/iserver/services/map-china400/wmts-china";
//            info.setServer(url);
//            Datasource datasource = workspace.getDatasources().open(info);
//            if (datasource != null) {
//                mapControl.getMap().getLayers().add(datasource.getDatasets().get(0), true);
//            }
//        }

//        {
//            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
//            //设置数据源别名
//            info.setAlias("SuperMapCloud1");
//            //设置引擎类型
//            info.setEngineType(EngineType.SuperMapCloud);
//            //设置地图服务地址(必设)
//            String url = "http://supermapcloud.com";
//            info.setServer(url);
//            //打开数据源
//            Datasource datasource = workspace.getDatasources().open(info);
//            //添加数据集到地图窗口
//            mapControl = mapView.getMapControl();
//            mapControl.getMap().getLayers().add(datasource.getDatasets().get("quanguo"), true);
//        }

//        {
//            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
//            //设置数据源别名
//            info.setAlias("SuperMapCloud1");
//            //设置引擎类型
//            info.setEngineType(EngineType.SuperMapCloud);
//            //设置地图服务地址(必设)
//            String url = "http://supermapcloud.com";
//            info.setServer(url);
//            //打开数据源
//            Datasource datasource = workspace.getDatasources().open(info);
//            //添加数据集到地图窗口
//            mapControl = mapView.getMapControl();
//            mapControl.getMap().getLayers().add(datasource.getDatasets().get(0), true);
//        }

        {
            //临时使用googleMap替代
            DatasourceConnectionInfo dsGoogleMap = new DatasourceConnectionInfo();
            dsGoogleMap.setAlias("GoogleMaps");
            dsGoogleMap.setEngineType(EngineType.GoogleMaps);
            dsGoogleMap.setReadOnly(false);
            dsGoogleMap.setServer("http://www.goole.cn/maps");
            Datasource ds = workspace.getDatasources().open(dsGoogleMap);
            Datasets datasets = ds.getDatasets();
            System.out.println("openGoogle: " + datasets.getCount());
            mapControl.getMap().setAntialias(true);
            mapControl.getMap().setImageSize(500, 600);
            mapControl.getMap().getLayers().add(ds.getDatasets().get(0), true);
            mapControl.getMap().setVisibleScalesEnabled(true);
        }


//        DatasourceConnectionInfo infoText = new DatasourceConnectionInfo();
//        infoText.setAlias("CVA");
//        infoText.setEngineType(EngineType.OGC);
//        infoText.setDriver("WMTS");
//        String url_Text = "http://t0.tianditu.com/cva_c/wmts?tk=2ce94f67e58faa24beb7cb8a09780552";
//        infoText.setServer(url_Text);
//        Datasource mDtSource_Text = workspace.getDatasources().open(infoText);
//        if (mDtSource_Text != null) {
//            mapControl.getMap().getLayers().add(mDtSource_Text.getDatasets().get(0), true);
//        }

//        mapControl.getMap().setAntialias(true);

        m_dynamicLayer = new DynamicView(mContext, mapView.getMapControl().getMap());
        mapView.addDynamicView(m_dynamicLayer);
        m_dynamicLayer.setCacheEnabled(false);


//        myLocation(Center_x,Center_y, 20);
//        handler.postDelayed(runnable, 2000);
        Point2D point2D = new Point2D(1.2971339778971717E7,4846891.863591658);
        if( map.getBounds().contains(point2D) )
        {
            mapControl.getMap().setCenter(point2D);
            mapControl.getMap().setScale(3.3375205614122024E-6);
        }


        mapControl.setMapParamChangedListener(new MapParameterChangedListener() {
            @Override
            public void scaleChanged(double v) {
                shouldUpdateDynamicPOIContents(v);
            }

            @Override
            public void boundsChanged(Point2D point2D) {

            }

            @Override
            public void angleChanged(double v) {

            }

            @Override
            public void sizeChanged(int i, int i1) {

            }
        });


        mapControl.getMap().refresh();
    }


    // 手势事件监听
    public GestureDetector.SimpleOnGestureListener touchListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        public void onLongPress(MotionEvent event) {

        }

        public boolean onSingleTapUp(MotionEvent e) {
            //get the singletap point
            float eventX = e.getX();
            float eventY = e.getY();
            Point2D pnt2D = mapControl.getMap().pixelToMap(new com.supermap.data.Point((int)eventX, (int)eventY));
            Log.e(TAG, pnt2D.toString());
            Log.e(TAG,"SCALE: " +  map.getScale());

            return true;
        }
    };


    private void initView(View rootView) {
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mapView = rootView.findViewById(R.id.mapview2);
        rootView.findViewById(R.id.location).setOnClickListener(this);
        rootView.findViewById(R.id.ib_zoom_in).setOnClickListener(this);
        rootView.findViewById(R.id.ib_zoom_out).setOnClickListener(this);
    }

    private void initListening(View rootView) {
        floatingActionButton.setOnClickListener(view -> {
            connectDataflow();
        });
    }

    public void clearAll() {
        mapView.removeAllCallOut();
        map.getTrackingLayer().clear();

        handler.removeCallbacks(runnable);

        if (dataFlowService != null) {
            dataFlowService.close();
        }
        m_dynamicLayer.clear();
    }

    public void connectDataflow() {
        Toast.makeText(mContext, "订阅数据流", Toast.LENGTH_SHORT).show();
        mapView.removeAllCallOut();
        map.getTrackingLayer().clear();

        Point2D point2D = new Point2D(1.2978143849793682E7,4858975.286491773);
        if( map.getBounds().contains(point2D) )
        {
            mapControl.panTo(point2D, 300);
            mapControl.getMap().setScale(0.00008);
            mapControl.getMap().refresh();
        }

        handler.postDelayed(runnable, 500);

//        dataFlowService = DataFlowService.getInstance();
//        String token = "bvrNfl8HOG8mJW1TVZ6lYUu1JGNHfg2RhlS3fcoICihaHObcUsdUnvYktTVA3Fb7Gd2UeLbCNJeUiRfsxf5u2g..";
//        dataFlowService.setAddress("ws://192.168.0.28:8800/iserver/services/dataflow/dataflow/subscribe?token=" + token);
//        dataFlowService.setOnDataFlowListener(new DataFlowService.OnDataFlowListener() {
//            @Override
//            public void onDataFlowReceiveGeoJson(String geojson) {
//                Log.d("DataFlowReceiveGeoJson", geojson);
//
//                TaxiBean taxiBean = gson.fromJson(geojson, TaxiBean.class);
//                String toJson = gson.toJson(taxiBean.getGeometry());
//                GeoPoint geoPoint = new GeoPoint();
//                if (geoPoint.fromGeoJSON(toJson)) {
//                    String id = taxiBean.getProperties().getId();
//                    int angle = taxiBean.getProperties().getB();
//                    addElement(geoPoint, Integer.parseInt(id), angle);
//                }
//            }
//
//            @Override
//            public void onDataFlowDidFailed(String s) {
//                Log.e("onDataFlowDidFailed", s);
//            }
//
//            @Override
//            public void onDataFlowDidOpened(String info) {
//                Log.e("onDataFlowDidOpened", info);
//            }
//        });
//        dataFlowService.connect();
    }

    private Point2D getPoint(GeoPoint point) {
        double x = point.getX();
        double y = point.getY();

        Point2D pt = new Point2D(x, y);
        return pt;
    }

    private void addElement(GeoPoint point, int id, int angle) {
        Point2D currPoint2D = getPoint(point);

        //当投影不是经纬坐标系时，则对点进行投影转换
        PrjCoordSys desPrj = mapControl.getMap().getPrjCoordSys();
        if (desPrj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
            Point2Ds points = new Point2Ds();
            points.add(currPoint2D);
            PrjCoordSys srcPrjCoorSys = new PrjCoordSys();
            srcPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
            CoordSysTranslator.convert(points, srcPrjCoorSys, desPrj,
                    new CoordSysTransParameter(),
                    CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

            currPoint2D.setX(points.getItem(0).getX());
            currPoint2D.setY(points.getItem(0).getY());
            Log.e(TAG, "x=" + currPoint2D.getX() + ", Y=" + currPoint2D.getY());
        }

        if (idList.contains(id)) {
            startAnimation(currPoint2D, id);
        } else {
            Log.e(TAG, "addElement: " + id);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
            DynamicPoint element = new DynamicPoint();
            element.addPoint(currPoint2D);
            DynamicStyle style = new DynamicStyle();
            style.setLineColor(Color.rgb(0, 0, 200));
            style.setAngle(angle);
            style.setBackground(bitmap);
            element.setStyle(style);
            element.setTag("" + id);

            idList.add(id);
            m_dynamicLayer.addElement(element);
        }
        point2DHashMap.put(id, currPoint2D);//更新位置

        m_dynamicLayer.refresh();
    }

    private void startAnimation(Point2D curtPoint2D, int id) {
        try{
            List<DynamicElement> dynamicElements = m_dynamicLayer.queryByTag("" + id);
            DynamicElement element = dynamicElements.get(0);

            double angle = calculatesAngle(element.getBounds().getCenter(), curtPoint2D);
            float angle2 = element.getStyle().getAngle();

            element.addAnimator(new RotateAnimator((float) (angle - angle2), 1000));
            element.addAnimator(new TranslateAnimator(curtPoint2D, 4000));

            element.startAnimation();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculatesAngle(Point2D ptStart, Point2D ptEnd) {
        double Y = ptEnd.getY() - ptStart.getY();
        double X = ptEnd.getX() - ptStart.getX();
        double angle2 = Math.atan2(Y, X);

        return 90 - angle2*180/3.1415;
    }


    public void myLocation(double Longitude, double Latitude, int azimuth) {
        Point2D point2D = new Point2D(Longitude, Latitude);

        //当投影不是经纬坐标系时，则对点进行投影转换
//        PrjCoordSys desPrj = mapControl.getMap().getPrjCoordSys();
//        if (desPrj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
//            Point2Ds points = new Point2Ds();
//            points.add(point2D);
//            PrjCoordSys srcPrjCoorSys = new PrjCoordSys();
//            srcPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
//            CoordSysTranslator.convert(points, srcPrjCoorSys, desPrj,
//                    new CoordSysTransParameter(),
//                    CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
//
//            point2D.setX(points.getItem(0).getX());
//            point2D.setY(points.getItem(0).getY());
//        }

        if( map.getBounds().contains(point2D) )
        {
            drawCircleOnDyn(point2D, 0.005, azimuth);
//            mapControl.panTo(point2D, 300);
        }
    }

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            handler.postDelayed(this, 12000);

            m_dynamicLayer.clear();

//            Random rand = new Random();
//            int i = rand.nextInt(100); //生成0-200以内的随机数
//            myLocation(Center_x,Center_y, i);
            for (int i = 0; i < 10; i++) {
                if (i == 0) {
                    startCarAnimation(point2D_car01, point2D_car02, 0, 150);
                } else if (i == 1) {
                    startCarAnimation(point2D_car03, point2D_car04, 1, 50);
                } else if (i == 2) {
                    startCarAnimation(point2D_car05, point2D_car06, 2, 120);
                } else if (i == 3) {
                    startCarAnimation(point2D_car07, point2D_car08, 3,270);
                } else if (i == 4) {
                    startCarAnimation(point2D_car09, point2D_car10, 4, 300);
                } else if (i == 5) {
                    startCarAnimation(point2D_car11, point2D_car12, 5, 90);
                } else if (i == 6) {
                    startCarAnimation(point2D_car13, point2D_car14, 6, 180);
                } else if (i == 7) {
                    startCarAnimation(point2D_car15, point2D_car16, 7, 90);
                } else if (i == 8) {
                    startCarAnimation(point2D_car17, point2D_car18, 8, 45);
                } else if (i == 9) {
                    startCarAnimation(point2D_car19, point2D_car20, 9, 90);
                }
            }
        }

    };

    public void drawCircleOnDyn(Point2D point2D, double dAccuracy, double dAzimuth)
    {
        if (m_dynamicLayer == null) {
            return;
        }
        m_dynamicLayer.removeElement(dynCircle);
        m_dynamicLayer.removeElement(dynPoint);

        dynCircle = new DynamicCircle();
        dynCircle.setPoint(point2D);
        dynCircle.setRadius(dAccuracy);
        DynamicStyle style = new DynamicStyle();
        style.setBackColor(android.graphics.Color.rgb(64,224,208));
        style.setLineColor(android.graphics.Color.rgb(170,182,195));
        style.setAlpha(50);
        style.setSize(6.0f);
        dynCircle.setStyle(style);

        m_dynamicLayer.addElement(dynCircle);

        //绘制中心点
        dynPoint = new DynamicPoint();
        dynPoint.addPoint(point2D);

        DynamicStyle dynStyle = new DynamicStyle();
        dynStyle.setBackground(BitmapFactory.decodeResource(mapControl.getResources(), R.drawable.mylocation001));
        dynStyle.setAngle((float)dAzimuth);
        dynPoint.setStyle(dynStyle);

        m_dynamicLayer.addElement(dynPoint);

        m_dynamicLayer.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location:
//                handler.removeCallbacks(runnable);
//                m_dynamicLayer.clear();
//
//                mapView.removeAllCallOut();
//                map.getTrackingLayer().clear();

                Point2D point2D = new Point2D(1.2971038159960426E7,4858307.794140533);
                if( map.getBounds().contains(point2D) )
                {
                    mapControl.panTo(point2D, 300);
                    mapControl.getMap().setScale(0.0005);

                    myLocation(point2D.getX(), point2D.getY(), 0);

                    mapControl.getMap().refresh();
                }

                break;
            case R.id.ib_zoom_in:
//                searchAction();
                mapControl.zoomTo(mapControl.getMap().getScale() * 1.4, 200);

                break;
            case R.id.ib_zoom_out:
//                connectDataflow();

                mapControl.zoomTo(mapControl.getMap().getScale() * 0.6, 200);

                break;
        }
    }


    private List<CallOut> mCallOuts = new ArrayList<>();

    private void addCalloutCar(Point2D point2D, int index,  String day, String time) {


        LayoutInflater lfCallOut = getLayoutInflater();
        View calloutLayout = lfCallOut.inflate(R.layout.callout_car, null);
        TextView timeView = calloutLayout.findViewById(R.id.time);
        TextView dayView = calloutLayout.findViewById(R.id.day);
        ImageView imageView = calloutLayout.findViewById(R.id.img);

        if (index == 0) {
            imageView.setBackgroundResource(R.drawable.car_1);
            timeView.setText(time1);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_1, "出租车", day, time1);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        } else if (index == 4) {
            timeView.setText(time2);
            imageView.setBackgroundResource(R.drawable.car_2);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_2, "出租车", day, time2);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        } else if (index == 7) {
            timeView.setText(time3);
            imageView.setBackgroundResource(R.drawable.car_3);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_3, "出租车", day, time3);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        }else if (index == 12) {
            timeView.setText(time4);
            imageView.setBackgroundResource(R.drawable.car_4);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_4, "出租车", day, time4);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        }else if (index == 14) {
            timeView.setText(time5);
            imageView.setBackgroundResource(R.drawable.car_5);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_5, "出租车", day, time5);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        }else if (index == 18) {
            timeView.setText(time6);
            imageView.setBackgroundResource(R.drawable.car_7);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_7, "出租车", day, time6);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        }else if (index == 24) {
            timeView.setText(time7);
            imageView.setBackgroundResource(R.drawable.car8);

            imageView.setOnClickListener(v -> {
                ObjDialog dialog = new ObjDialog(mContext, R.style.SpeechDialog,
                        R.drawable.car_8, "出租车", day, time7);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(dialog1 -> {

                });
            });
        }

        dayView.setText(day);

        CallOut callout = new CallOut(mContext);
        // 设置显示内容
        callout.setContentView(calloutLayout);
        // 设置自定义背景图片
        callout.setCustomize(true);
        // 设置显示位置
        callout.setLocation(point2D.getX(), point2D.getY());
        mapView.addCallout(callout, "Callout" + time);
    }




    public void shouldUpdateDynamicPOIContents(double newMapScale){
        //第二种方法：
        for(int i = 0 ;i < mScalessCalloutNames.size();i++){
            ScalesCallout scalesCallout = ((ScalesCallout)mapView.getCallOut(mScalessCalloutNames.get(i)));
            if(scalesCallout != null){
                scalesCallout.shouldUpdateContents(newMapScale);
            }
        }
        mapView.getMapControl().getMap().refresh();
    }

    //测试动态图标接口
    public void testDynamicPoi(){
        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                mContext.getResources().getDimension(R.dimen.navilib_padding_10),
                mContext.getResources().getDisplayMetrics()
        );

        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                mContext.getResources().getDimension(R.dimen.navilib_padding_13),
                mContext.getResources().getDisplayMetrics()
        );

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(width, height);

        //每一级对应的布局
        ArrayList<View> customViews = new ArrayList<>(
                Arrays.asList(
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_1,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_2,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_3,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_4,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_5,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_6,lp),
                        new PoiOverlay(mContext,R.mipmap.mapctrl_poi_overlay_normal_7,lp)
                ));

        ArrayList<Double> sclaeTable = new ArrayList<>(Arrays.asList( 1/20D, 1/50D, 1/100D, 1/200D,  1/500D,  1/1000D,   1/2000D,  1/5000D ));
        ScalesCallout scalesCallout = new ScalesCallout(mContext,sclaeTable,customViews);

        addDynamicPointCallout(new Point2D(1.2971038159960426E7,4858307.794140533),scalesCallout);
    }


    private ArrayList<String> mScalessCalloutNames = new ArrayList<>();

    //分级比例尺动态图标接口
    private void addDynamicPointCallout(Point2D point, ScalesCallout scalesCallout) {
        String scalesCalloutName = "ScalesCallout"+mScalessCalloutNames.size();
        mScalessCalloutNames.add(scalesCalloutName);

        scalesCallout.setStyle(CalloutAlignment.CENTER);
        scalesCallout.setCustomize(true);
        scalesCallout.setLocation(point.getX(), point.getY());
        mapView.addCallout(scalesCallout,scalesCalloutName);
    }

    private static String getDayTime() {
//        //得到long类型当前时间
//        long l = System.currentTimeMillis();
//        //new日期对
//        Date date = new Date(l);
//        //转换提日期输出格式
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//
//        return dateFormat.format(date);

        return "2019-10-11";
    }

    private static String getCurrentTime() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对
        Date date = new Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分", Locale.CHINA);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_ms", Locale.CHINA);

        return dateFormat.format(date);
    }

    private void startCarAnimation(Point2D point2D01, Point2D point2D02, int angle, int id) {
        try{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
//            bitmap.setWidth(100);
//            bitmap.setHeight(100);
            DynamicPoint element = new DynamicPoint();
            element.addPoint(point2D01);
            DynamicStyle style = new DynamicStyle();
            style.setLineColor(Color.rgb(0, 0, 200));
            style.setAngle(angle);
            style.setBackground(bitmap);
            element.setStyle(style);
            element.setTag("" + id);

            idList.add(id);
            m_dynamicLayer.addElement(element);


            double angle02 = calculatesAngle(element.getBounds().getCenter(), point2D02);
            float angle2 = element.getStyle().getAngle();

            element.addAnimator(new RotateAnimator((float) (angle02 - angle2), 1000));
            element.addAnimator(new TranslateAnimator(point2D02, 12000));

            element.startAnimation();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void choosePic() {
        PictureSelector.create(SecondFragment.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                        .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(300)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(15)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
//                        .previewVideo()// 是否可预览视频 true or false
//                        .enablePreviewAudio() // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
//                    .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
//                    .compress(false)// 是否压缩 true or false
//                    .glideOverride(400, 500)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                    .withAspectRatio(3,4)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
//                        .isGif()// 是否显示gif图片 true or false
//                        .compressSavePath(getPath())//压缩图片保存地址
//                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
//                    .circleDimmedLayer(false)// 是否圆形裁剪 true or false
//                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
//                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
//                        .openClickSound()// 是否开启点击声音 true or false
//                        .selectionMedia()// 是否传入已选图片 List<LocalMedia> list
//                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                        .cropCompressQuality()// 裁剪压缩质量 默认90 int
//                    .minimumCompressSize(100)// 小于100kb的图片不压缩
//                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
//                        .cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
//                        .rotateEnabled() // 裁剪是否可旋转图片 true or false
//                        .scaleEnabled()// 裁剪是否可放大缩小图片 true or false
//                        .videoQuality()// 视频录制质量 0 or 1 int
//                        .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
//                        .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
//                        .recordVideoSecond()//视频秒数录制 默认60s int
//                    .isDragFrame(false)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    for (int i = 0; i < selectList.size(); i++) {
                        Log.e(TAG, selectList.get(i).getPath());
                    }
                }
                connectDataflow();

                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        deviceOrientation.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

//        deviceOrientation.pause();
    }

}
