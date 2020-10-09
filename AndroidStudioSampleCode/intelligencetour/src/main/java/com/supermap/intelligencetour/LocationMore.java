package com.supermap.intelligencetour;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.beyondar.android.fragment.BeyondarFragmentSupport;
//import com.beyondar.android.plugin.radar.RadarView;
//import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.supermap.ar.World;
import com.supermap.data.Color;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.onlineservices.CoordinateType;
import com.supermap.onlineservices.NavigationOnline;
import com.supermap.onlineservices.NavigationOnlineData;
import com.supermap.onlineservices.NavigationOnlineParameter;
import com.supermap.onlineservices.POIInfo;
import com.supermap.onlineservices.POIQuery;
import com.supermap.onlineservices.POIQueryParameter;
import com.supermap.onlineservices.POIQueryResult;
import com.supermap.onlineservices.PathInfo;
import com.supermap.onlineservices.RouteType;
import com.supermap.plugin.LocationManagePlugin;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationMore extends AppCompatActivity implements View.OnClickListener,TencentLocationListener {

    private Workspace workspace;
    private Dataset dataset;
    private Datasource datasource;
    private MapView mapView;
    private MapControl mapControl;
    private TrackingLayer trackingLayer;
    double x, y, locationx, locationy, locationTencentx, locationTencenty, searchpointx, searchpointy;
    private boolean navigationFlag = false;
    boolean istouch = true;
    private POIInfo[] poiInfos;
    Point2Ds point2Ds;
    LayoutInflater lfCallOut;
    View calloutLayout;
    private View bottom;
    private TextView nametxt, lengthtxt;
    private Point2D selectPoint;
    private CallOut callout1;
    private String queryStr ;

//    private BeyondarFragmentSupport mBeyondarFragment;
//    private RadarView mRadarView;
//    private RadarWorldPlugin mRadarPlugin;
    private World mWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_locationmore);

        x = getIntent().getDoubleExtra("pointx", 0.00);
        y = getIntent().getDoubleExtra("pointy", 0.00);
        searchpointx = getIntent().getDoubleExtra("searchpointx", 0.00);
        searchpointy = getIntent().getDoubleExtra("searchpointy", 0.00);
        locationx = getIntent().getDoubleExtra("locationx", 0.00);
        locationy = getIntent().getDoubleExtra("locationy", 0.00);
        locationTencentx = getIntent().getDoubleExtra("locationTencentx", 0.00);
        locationTencenty = getIntent().getDoubleExtra("locationTencenty", 0.00);
        queryStr = getIntent().getStringExtra("queryStr");

//        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
//                R.id.beyondarFragment);
        init();
        openMap();
        query(new Point2D(locationTencentx, locationTencenty), new Point2D(x, y));
        queryPOI(queryStr,"");


        mapControl.setGestureDetector(new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                bottom.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        }));

    }

    private void init() {
        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.ditie_black).setOnClickListener(this);
        findViewById(R.id.gongjiao_black).setOnClickListener(this);
        findViewById(R.id.school_black).setOnClickListener(this);
        findViewById(R.id.chaoshi_black).setOnClickListener(this);
        findViewById(R.id.canting_black).setOnClickListener(this);
        findViewById(R.id.bank_black).setOnClickListener(this);
        findViewById(R.id.hospital_black).setOnClickListener(this);
        findViewById(R.id.nvi_routeanalyst).setOnClickListener(this);

        bottom = (View) findViewById(R.id.bottom_view);
        bottom.setVisibility(View.INVISIBLE);
        nametxt = (TextView) findViewById(R.id.name);
    lengthtxt = (TextView) findViewById(R.id.length);

        mLocationManager = TencentLocationManager.getInstance(this);

        int error = TencentLocationManager.getInstance(this)
                .requestLocationUpdates(
                        TencentLocationRequest
                                .create().setInterval(1000)
                                .setRequestLevel(
                                        TencentLocationRequest.REQUEST_LEVEL_NAME), this);
        if (error == 0) {
            startDate = new Date();
            Log.e("监听状态:", "监听成功!");
        } else if (error == 1) {
            Log.e("监听状态:", "设备缺少使用腾讯定位SDK需要的基本条件");
        } else if (error == 2) {
            Log.e("监听状态:", "配置的 key 不正确");
        }


    }


    private void openMap() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapControl = mapView.getMapControl();
        trackingLayer = mapControl.getMap().getTrackingLayer();

        workspace = new Workspace();

        DatasourceConnectionInfo dcInfo = new DatasourceConnectionInfo();
        dcInfo.setAlias("GoogleMaps");
        dcInfo.setEngineType(EngineType.GoogleMaps);
        dcInfo.setReadOnly(false);
        dcInfo.setServer("http://www.google.cn/maps");
        mapControl.getMap().setWorkspace(workspace);
        datasource = workspace.getDatasources().open(dcInfo);
        if (datasource != null) {
            dataset = datasource.getDatasets().get(0);
            if (dataset != null) {
                mapControl.getMap().getLayers().add(dataset, true);
                //设置地图初始的显示范围，地图出图时是成都
                mapControl.getMap().setScale(0.0002);
                mapControl.getMap().setCenter(new Point2D(locationx, locationy));
                mapControl.getMap().refresh();
            }
        }

        addlocationcallout();

    }


    private void query(Point2D startPoint, Point2D endPoint) {
        NavigationOnline navigationOnline = new NavigationOnline();
//						必须调用
//						设置钥匙
        navigationOnline.setKey("fvV2osxwuZWlY0wJb8FEb2i5");

//						设置回调，查看NavigationOnline是否调用成功
        navigationOnline.setNavigationOnlineCallback(new NavigationOnline.NavigationOnlineCallback() {

            @Override
            public void calculateSuccess(NavigationOnlineData data) {
                navigationFlag = true;
                setNavigationOnline(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapControl.getMap().refresh();
                    }
                });
            }

            @Override
            public void calculateFailed(String errorInfo) {
                Log.e("LocationMore", errorInfo);
            }
        });
        NavigationOnlineParameter parameter = new NavigationOnlineParameter();
//						以下两个方法必须调用
//						设置起点
        parameter.setStartPoint(startPoint);
//						设置终点
        parameter.setEndPoint(endPoint);
//						设置目标类型
        parameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//						设置道路类型
        parameter.setRouteType(RouteType.RE_COMMEND);
//						进行道路分析
        navigationOnline.routeAnalyst(parameter);
        final ProgressDialog dialog = new ProgressDialog(LocationMore.this);
        dialog.setMessage("加载中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (navigationFlag) {
                            dialog.dismiss();
                            navigationFlag = false;
                            mapControl.getMap().refresh();
                        } else {
                            Toast.makeText(LocationMore.this, "加载路径失败", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        }).start();
        dialog.show();
    }

    private void setNavigationOnline(NavigationOnlineData data) {
        if (data == null) {
            return;
        }
        trackingLayer.clear();
//		从data中获取geoline
        Point2Ds point2Ds = data.getRoute().getPart(0);

        PrjCoordSys sourcePrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);

        CoordSysTransParameter coordSysTransParameter = new CoordSysTransParameter();

        CoordSysTranslator.convert(point2Ds, sourcePrjCoordSys, mapControl.getMap().getPrjCoordSys(), coordSysTransParameter, CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

        GeoLine geoLine = new GeoLine(point2Ds);
        GeoStyle geoLineStyle = new GeoStyle();
        Color color = new Color(82, 198, 223);
        geoLineStyle.setLineColor(color);
        geoLineStyle.setLineWidth(2);
//		为geoLine设置风格
        geoLine.setStyle(geoLineStyle);
//		在跟踪图层上显示geoLine
        trackingLayer.add(geoLine, "线路");
        mapControl.getMap().refresh();
//		得到线路信息的集合
        List<PathInfo> pathInfoList = data.getPathInfos();
        int pathInfoCount = pathInfoList.size();
        System.out.println(pathInfoCount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                finish();
                break;
            case R.id.ditie_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("地铁", "ditie_black");
                break;
            case R.id.gongjiao_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("公交", "gongjiao_black");
                break;
            case R.id.school_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("学校", "school_black");
                break;
            case R.id.chaoshi_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("超市", "chaoshi_black");
                break;
            case R.id.canting_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("餐厅", "canting_black");
                break;
            case R.id.bank_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("银行", "bank_black");
                break;
            case R.id.hospital_black:
                if (point2Ds != null) {
                    point2Ds.clear();
                }
                mapView.removeAllCallOut();
                addlocationcallout();
                queryPOI("医院", "hospital_black");
                break;
            case R.id.nvi_routeanalyst:
                query(new Point2D(locationTencentx, locationTencenty), selectPoint);
//                double armin = getAngle(selectPoint.getX(), selectPoint.getY(),locationTencentx, locationTencenty);
//                arDrawView.setAzimuth(armin);
                break;
        }
    }


    private void addlocationcallout() {
        LayoutInflater lfCallOut = getLayoutInflater();
        View calloutLayout = lfCallOut.inflate(R.layout.callout2, null);
        CallOut callout = new CallOut(this);
        // 设置显示内容
        callout.setContentView(calloutLayout);
        // 设置自定义背景图片
        callout.setCustomize(true);
        // 设置显示位置
        callout.setLocation(searchpointx, searchpointy);
        mapView.addCallout(callout);


        LayoutInflater lfCallOut1 = getLayoutInflater();
        View calloutLayout1 = lfCallOut1.inflate(R.layout.callout1, null);
        callout1 = new CallOut(this);
        callout1.setContentView(calloutLayout1);
        callout1.setCustomize(true);
        callout1.setLocation(locationx, locationy);
        mapView.addCallout(callout1);
    }

    private void addCallout(final Point2D point2D, String id, final String name, final Point2D selectPoint2d) {
        switch (id) {
            case "ditie_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_ditie, null);
                final TextView textViewdt = calloutLayout.findViewById(R.id.text);
                Button b = calloutLayout.findViewById(R.id.btnSelected);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewdt.setText(name);
                        if(textViewdt.getVisibility()== View.VISIBLE){
                            textViewdt.setVisibility(View.INVISIBLE);
                        }else {
                            textViewdt.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                        }
                        selectPoint = selectPoint2d;
                    }
                });
                break;
            case "gongjiao_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_gongjiao, null);
                final TextView textViewgj = calloutLayout.findViewById(R.id.text);
                Button b1 = calloutLayout.findViewById(R.id.btnSelected);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewgj.setText(name);
                        if(textViewgj.getVisibility()== View.VISIBLE){
                            textViewgj.setVisibility(View.INVISIBLE);
                        }else {
                            textViewgj.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                            selectPoint = selectPoint2d;
                        }
                    }
                });
                break;
            case "school_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_school, null);
                final TextView textViewsc = calloutLayout.findViewById(R.id.text);
                Button b2 = calloutLayout.findViewById(R.id.btnSelected);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewsc.setText(name);
                        if(textViewsc.getVisibility()== View.VISIBLE){
                            textViewsc.setVisibility(View.INVISIBLE);
                        }else {
                            textViewsc.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                            selectPoint = selectPoint2d;
                        }
                    }
                });
                break;
            case "chaoshi_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_chaoshi, null);
                final TextView textViewcs = calloutLayout.findViewById(R.id.text);
                Button b3 = calloutLayout.findViewById(R.id.btnSelected);
                b3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewcs.setText(name);
                        if(textViewcs.getVisibility()== View.VISIBLE){
                            textViewcs.setVisibility(View.INVISIBLE);
                        }else {
                            textViewcs.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                            selectPoint = selectPoint2d;
                        }
                    }
                });
                break;
            case "canting_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_canting, null);
                final TextView textViewct = calloutLayout.findViewById(R.id.text);
                Button b4 = calloutLayout.findViewById(R.id.btnSelected);
                b4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewct.setText(name);
                        if(textViewct.getVisibility()== View.VISIBLE){
                            textViewct.setVisibility(View.INVISIBLE);
                        }else {
                            textViewct.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                            selectPoint = selectPoint2d;
                        }
                    }
                });
                break;
            case "bank_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_bank, null);
                final TextView textViewyh = calloutLayout.findViewById(R.id.text);
                Button b5 = calloutLayout.findViewById(R.id.btnSelected);
                b5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewyh.setText(name);
                        if(textViewyh.getVisibility()== View.VISIBLE){
                            textViewyh.setVisibility(View.INVISIBLE);
                        }else {
                            textViewyh.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                            selectPoint = selectPoint2d;
                        }
                    }
                });
                break;
            case "hospital_black":
                lfCallOut = getLayoutInflater();
                calloutLayout = lfCallOut.inflate(R.layout.callout_hospital, null);
                final TextView textViewyy = calloutLayout.findViewById(R.id.text);
                Button b6 = calloutLayout.findViewById(R.id.btnSelected);
                b6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewyy.setText(name);
                        if(textViewyy.getVisibility()== View.VISIBLE){
                            textViewyy.setVisibility(View.INVISIBLE);
                        }else {
                            textViewyy.setVisibility(View.VISIBLE);
                            nametxt.setText(name);
                            lengthtxt.setText(String.valueOf(getPoitLength(point2D)) + "米");
                            bottom.setVisibility(View.VISIBLE);
                            selectPoint = selectPoint2d;
                        }
                    }
                });
                break;
        }
        CallOut callout = new CallOut(this);
        callout.setStyle(CalloutAlignment.CENTER);
        callout.setContentView(calloutLayout);
        callout.setCustomize(true);
        callout.setLocation(point2D.getX(), point2D.getY());
        mapView.addCallout(callout);
    }

    private Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth / width);
        float scaleHeight = ((float) newHeight / height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private void queryPOI(String str, final String id) {
        POIQuery poiQuery = new POIQuery(this);
        POIQueryParameter queryParameter = new POIQueryParameter();
        //			用户申请的钥匙
        queryParameter.setKey("fvV2osxwuZWlY0wJb8FEb2i5");
//			查询的关键字
        queryParameter.setKeywords(str);
        //			在某个范围内查询
        queryParameter.setCity("成都市");

        queryParameter.setPageSize(10);

        queryParameter.setLocation(locationTencentx, locationTencenty);

        queryParameter.setRadius("1000");

        queryParameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_LONGITUDE_LATITUDE);
//			进行POI查询
        poiQuery.query(queryParameter);
        //			查看POI查询是否成功
        poiQuery.setPOIQueryCallback(new POIQuery.POIQueryCallback() {
            @Override
            public void querySuccess(POIQueryResult queryResult) {
                poiInfos = queryResult.getPOIInfos();
                if(!id.equals("")){
                    point2Ds = new Point2Ds();
                    for (int i = 0; i < poiInfos.length; i++) {
                        Point2D point2D = poiInfos[i].getLocation();
                        point2Ds.add(point2D);
                    }
//
                    PrjCoordSys sourcePrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);

                    CoordSysTransParameter coordSysTransParameter = new CoordSysTransParameter();

                    CoordSysTranslator.convert(point2Ds, sourcePrjCoordSys, mapControl.getMap().getPrjCoordSys(), coordSysTransParameter, CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

                    if (point2Ds.getCount() > 3) {
                        GeoRegion geoRegion = new GeoRegion(point2Ds);
                        Rectangle2D rect2DLine = geoRegion.getBounds();
                        rect2DLine.inflate(rect2DLine.getWidth() / 2, rect2DLine.getHeight() / 2);
                        mapControl.getMap().setViewBounds(rect2DLine);
                        mapControl.getMap().refresh();
                    }

                    for (int i = 0; i < point2Ds.getCount(); i++) {
                        addCallout(point2Ds.getItem(i), id, poiInfos[i].getName(), poiInfos[i].getLocation());
                    }

                }else {
                    // Create the Radar plugin
//                    mRadarPlugin = new RadarWorldPlugin(LocationMore.this);
//                    // set the radar view in to our radar plugin
//                    mRadarPlugin.setRadarView(mRadarView);
//                    // Set how far (in meters) we want to display in the view
////                    mRadarPlugin.setMaxDistance(3000);
//
//                    // We can customize the color of the items
//                    mRadarPlugin.setListColor(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, android.graphics.Color.RED);
//                    // and also the size
//                    mRadarPlugin.setListDotRadius(CustomWorldHelper.LIST_TYPE_EXAMPLE_1, 3);
//
//                    // We create the world and fill it ...
//                    mWorld = CustomWorldHelper.generateObjects(LocationMore.this,poiInfos,locationTencentx,locationTencenty);
//                    // .. and send it to the fragment
//                    mBeyondarFragment.setWorld(mWorld);
//
//                    // add the plugin
//                    mWorld.addPlugin(mRadarPlugin);
//
//                    // We also can see the Frames per seconds
//                    mBeyondarFragment.showFPS(true);
//
//                    mSeekBarMaxDistance.setOnSeekBarChangeListener(LocationMore.this);
//                    mSeekBarMaxDistance.setMax(2000);
//                    mSeekBarMaxDistance.setProgress(500);
                }

            }

            @Override
            public void queryFailed(String errInfo) {
                Toast.makeText(LocationMore.this, errInfo, Toast.LENGTH_SHORT).show();
            }
        });

    }
    //根据经纬度计算方位角
    public static double getAngle(double lon1, double lat1, double lon2,
                                  double lat2) {
        double fLat = Math.PI * (lat1) / 180.0;
        double fLng = Math.PI * (lon1) / 180.0;
        double tLat = Math.PI * (lat2) / 180.0;
        double tLng = Math.PI * (lon2) / 180.0;

        double degree = (Math.atan2(Math.sin(tLng - fLng) * Math.cos(tLat), Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat) * Math.cos(tLat) * Math.cos(tLng - fLng))) * 180.0 / Math.PI;
        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }

    }



    //根据地图点计算与定位点之间距离
    int getPoitLength(Point2D point2D) {
        double a = Math.pow((locationx - point2D.getX()), 2);
        double b = Math.pow((locationy - point2D.getY()), 2);
        double c = Math.abs(Math.sqrt(a + b));
        return (int) c;
    }



    TencentLocationManager mLocationManager;
    private TencentLocation m_locationInfo = null;
    //当前位置信息,用于导航和巡航
    private LocationManagePlugin.GPSData m_GPSData = null;
    private Point2D m_Point = new Point2D(0,0);
    private double mAccuracy = 0;

    public TencentLocation getLocInfo() {
        return m_locationInfo;
    }

    private Date startDate = null;//开始监听
    //毫秒
    public int calLastedTime(Date startDate) {
        long a = new Date().getTime();
        long b = startDate.getTime();
        int c = (int)(a - b);//毫秒
        return c;
    }


    /**
     * @param location 新的位置
     * @param error    错误码
     * @param reason   错误描述
     */
    @Override
    public void onLocationChanged(TencentLocation location, int error,
                                  String reason) {
        if (TencentLocation.ERROR_OK == error) {
            m_locationInfo = location;
            //当前位置信息,用于导航和巡航
            m_GPSData = new LocationManagePlugin.GPSData();
            m_GPSData.dAltitude = location.getAltitude();
            m_GPSData.dLongitude = location.getLongitude() - 0.0060880056233;
            m_GPSData.dLatitude = location.getLatitude() - 0.00100216102211;
            m_GPSData.dSpeed = location.getSpeed();
            Calendar ca = Calendar.getInstance();
            m_GPSData.nYear = ca.get(Calendar.YEAR);
            m_GPSData.nMonth = ca.get(Calendar.MONTH);
            m_GPSData.nDay = ca.get(Calendar.DATE);
            m_GPSData.nHour = ca.get(Calendar.HOUR);
            m_GPSData.nMinute = ca.get(Calendar.MINUTE);
            m_GPSData.nSecond = ca.get(Calendar.SECOND);

            mAccuracy = location.getAccuracy();
            m_Point.setX(location.getLongitude());
            m_Point.setY(location.getLatitude());

            if (calLastedTime(startDate) >= 2000) {
                //每2秒更新一次位置
                locateMyPosition();
                startDate = new Date();//重置时间
            }

            Log.d("onLocationChanged", "Longitude:" + location.getLongitude() + ", Latitude:" + location.getLatitude());
        } else {
            Log.e("reason:", reason);
            Log.e("error:", error + "");
        }
    }


    /**
     * @param name   GPS，Wi-Fi等
     * @param status 新的状态, 启用或禁用
     * @param desc   状态描述
     */
    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        Log.e("name:", name);
        Log.e("status:", "" + status);
        Log.e("desc:", desc);
    }

    //定位到当前位置
    private void locateMyPosition() {
        Point2D point2D = m_Point;
        locationTencentx = m_Point.getX();
        locationTencenty = m_Point.getY();
        Point2Ds resultPoint = new Point2Ds();
        resultPoint.add(point2D);

        PrjCoordSys sourcePrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);

        CoordSysTransParameter coordSysTransParameter = new CoordSysTransParameter();

        CoordSysTranslator.convert(resultPoint, sourcePrjCoordSys, mapControl.getMap().getPrjCoordSys(), coordSysTransParameter, CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

        locationx = resultPoint.getItem(0).getX();
        locationy = resultPoint.getItem(0).getY();
        if(callout1!=null){
            callout1.setLocation(locationx, locationy);
        }

    }

}
