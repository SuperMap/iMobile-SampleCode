package com.supermap.itinerary;


import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingMoveData;
import com.supermap.mapping.TrackingMoveHelper;
import com.supermap.spinner.ItemData;
import com.supermap.spinner.SpinnerAdapter;
import com.tencent.map.geolocation.TencentLocation;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:行程记录
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范GPS采集行程，并对行程回放展示功能
 * 2、示范数据： google在线图
 * 3、关键类型/成员:
 *      TrackingMoveData 类
 *      TrackingMoveHelper.onDraw() 方法
 *      TrackingMoveHelper.start() 方法
 *      TrackingMoveHelper.stop() 方法
 *
 *
 * 4、使用步骤：
 *   (1)点击Onlication按钮，开始记录轨迹
 *   (2)点击re-play按钮，回放轨迹
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
public class MainActivity extends Activity implements View.OnClickListener,TencentLocTool.LocationChangedListener{
    String rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private MapView mapView;
    private Workspace workspace;
    private List<Point2D> sourcePositionList = new ArrayList<>();
    private TrackingMoveHelper moveHelper = null;
    private List<TrackingMoveData> list = new ArrayList<>();
    private TextView textView ,textView1,textView2;
    private double mlength = 0;
    private String m_final;
    private View lay;
    private boolean startloca;
    private Spinner mSpinner;
    private Date curDate,endDate;
    private float m_speed =0;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.setLicensePath(rootPath + "/SuperMap/license");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        Environment.setOpenGLMode(true);
//        Environment.setWebCacheDirectory(rootPath + "/SuperMap/data/webCache");
        initView();
        TencentLocTool.getInstance().init(this);
        TencentLocTool.getInstance().setLocationManager(this);
        openMap();
    }

    private void initView() {
        mSpinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("Stop", R.drawable.locationdisplaydisabled));
        list.add(new ItemData("OnLocation", R.drawable.locationdisplayon));
        list.add(new ItemData("Re-Play", R.drawable.locationdisplayrecenter));
//    list.add(new ItemData("Navigation", R.drawable.locationdisplaynavigation));
//    list.add(new ItemData("Compass", R.drawable.locationdisplayheading));

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        mapView.getMapControl().getMap().setScale(2.7642873528291995E-5);
                        mapView.getMapControl().getMap().setCenter(new Point2D(1.1588636065792363E7,3572616.3886855906));
                        mapView.getMapControl().getMap().refresh();
                        startloca = false;
                        break;
                    case 1:
                        locateMyPosition();
                        sourcePositionList.clear();
                        curDate = new Date(System.currentTimeMillis());
                        startloca = true;
                        break;
                    case 2:
                        startloca = false;
                        endDate = new Date(System.currentTimeMillis());
                        mapView.getMapControl().getMap().setScale(4.788473226528291E-5);
                        mapView.getMapControl().getMap().getTrackingLayer().clear();
                        mapView.removeAllCallOut();
                        drawSlantAngle();
                        mapView.getMapControl().getMap().refresh();
                        lay.setVisibility(View.INVISIBLE);
                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }

        });
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        lay =findViewById(R.id.lay);
    }



    private void openMap() {
        mapView = findViewById(R.id.mapview1);
        workspace = new Workspace();
        Map map = mapView.getMapControl().getMap();
        map.setWorkspace(workspace);
        DatasourceConnectionInfo dcInfo = new DatasourceConnectionInfo();
        dcInfo.setAlias("GoogleMaps");
        dcInfo.setEngineType(EngineType.GoogleMaps);
        dcInfo.setReadOnly(false);
        dcInfo.setServer("http://www.goole.cn/maps");
        Datasource datasource = workspace.getDatasources().open(dcInfo);
        if (datasource != null) {
            Dataset dataset = datasource.getDatasets().get(1);
            if (dataset != null) {
                mapView.getMapControl().getMap().getLayers().add(dataset, true);
                mapView.getMapControl().getMap().setScale(2.7642873528291995E-5);
                mapView.getMapControl().getMap().setCenter(new Point2D(1.1588636065792363E7,3572616.3886855906));
                mapView.getMapControl().getMap().refresh();
            }
        }

    }


    private static final double EARTH_RADIUS = 6378137.0;
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    //定位到当前位置
    private void locateMyPosition() {
        Point2D point2D = getCurrentPoint2D();

        boolean contains = false;
        //中国版图范围
        contains = point2D.getX() >= 73.0 && point2D.getX() <= 135.0 && point2D.getY() >= 4.0 && point2D.getY() <= 53.0;

        if (contains && point2D.getX() != 0 && point2D.getY() != 0) {
            //当投影不是经纬坐标系时，则对点进行投影转换
            PrjCoordSys Prj = mapView.getMapControl().getMap().getPrjCoordSys();
            if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
                Point2Ds points = new Point2Ds();
                points.add(point2D);
                PrjCoordSys desPrjCoorSys = new PrjCoordSys();
                desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
                CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
                        new CoordSysTransParameter(),
                        CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

                point2D.setX(points.getItem(0).getX());
                point2D.setY(points.getItem(0).getY());
            }
            mapView.getMapControl().panTo(point2D, 200);

        } else {
            Toast.makeText(this, "未获取到当前位置", Toast.LENGTH_SHORT).show();
        }
    }

    //当前位置
    private Point2D getCurrentPoint2D() {
        TencentLocTool tencentLocationTool = TencentLocTool.getInstance();
        if (tencentLocationTool.getLocInfo() != null) {
            TencentLocation tencentLocation = tencentLocationTool.getLocInfo();
            double longitude = tencentLocation.getLongitude();//经度
            double latitude = tencentLocation.getLatitude();//纬度

            boolean contains = false;//China
            if (longitude >= 73.0 && longitude <= 135.0 && latitude >= 4.0 && latitude <= 53.0) {
                contains = true;
            }
            if (contains) {
                return new Point2D(longitude, latitude);
            }
        }
        return new Point2D(0, 0);
    }

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
                    Manifest.permission.CHANGE_WIFI_STATE);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
        }
    }

    public static void addEndCallout(String userid, Point2D point2D, Activity activity, MapView mapView) {
        mapView.removeCallOut(userid);

        LayoutInflater lfCallOut = activity.getLayoutInflater();
        View calloutLayout = lfCallOut.inflate(R.layout.callout_endpoint, null);


        CallOut callout_poi = new CallOut(activity);
        // 设置显示内容
        callout_poi.setContentView(calloutLayout);
        // 设置自定义背景图片
        callout_poi.setCustomize(true);

        // 设置显示位置
        callout_poi.setLocation(point2D.getX(), point2D.getY());

        mapView.addCallout(callout_poi, userid);
    }

    //显示POI
    public static void addStartCallout(String userid, Point2D point2D, Activity activity, MapView mapView) {
        mapView.removeCallOut(userid);

        LayoutInflater lfCallOut = activity.getLayoutInflater();
        View calloutLayout = lfCallOut.inflate(R.layout.callout_startpoint, null);

        CallOut callout_poi = new CallOut(activity);
        // 设置显示内容
        callout_poi.setContentView(calloutLayout);
        // 设置自定义背景图片
        callout_poi.setCustomize(true);

        // 设置显示位置
        callout_poi.setLocation(point2D.getX(), point2D.getY());

        mapView.addCallout(callout_poi, userid);
    }


    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            cancelangle();
        }
    };

    private void cancelangle(){
        if(angle>=0){
            mapView.getMapControl().getMap().setAngle(angle);
            mapView.getMapControl().getMap().refresh();
            angle = angle-5;
            handler1.sendEmptyMessageDelayed(0, 50);
        }else {
            cancelSlanangle();
        }
    }

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            cancelSlanangle();
        }
    };

    private void cancelSlanangle(){
        if(slanangle>=0){
            mapView.getMapControl().getMap().setSlantAngle(slanangle);
            mapView.getMapControl().getMap().refresh();
            slanangle--;
            handler2.sendEmptyMessageDelayed(0, 50);
        }else {
            mapView.getMapControl().getMap().setScale(4.788473226528291E-5);
//            mapView.getMapControl().getMap().setCenter(new Point2D(1.1588636065792363E7,3572616.3886855906));
            mapView.getMapControl().getMap().refresh();
        }
    }


    Handler slanhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawSlantAngle();
        }
    };

    private double slanangle = 0;
    public void drawSlantAngle(){
        if(slanangle<=15){
            mapView.getMapControl().getMap().setSlantAngle(slanangle);
            mapView.getMapControl().getMap().refresh();
            slanangle=slanangle+1;
            slanhandler.sendEmptyMessageDelayed(0, 50);
        }else {
            drawAngle();
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawAngle();
        }
    };

    private double angle = 0;
    public void drawAngle(){
        if(angle<=180){
            mapView.getMapControl().getMap().setAngle(angle);
            mapView.getMapControl().getMap().refresh();
            angle=angle+5;
            handler.sendEmptyMessageDelayed(0, 50);
        }else {
            if (sourcePositionList.size() > 1) {
                if (moveHelper != null) {
                    moveHelper.stop();
                }
                list.clear();

                List<Point2D> m_list1 = new ArrayList<>();
                for (int i = 0; i < sourcePositionList.size(); i++) {
                    PrjCoordSys Prj = mapView.getMapControl().getMap().getPrjCoordSys();
                    if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
                        Point2Ds points = new Point2Ds();
                        points.add(sourcePositionList.get(i));
                        PrjCoordSys desPrjCoorSys = new PrjCoordSys();
                        desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
                        CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
                                new CoordSysTransParameter(),
                                CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
                        m_list1.add(new Point2D(points.getItem(0).getX(), points.getItem(0).getY()));
                    }
                }

                for (int i = 0; i < m_list1.size(); i++) {
                    list.add(new TrackingMoveData("2017-08-17 20:09:00" + i, m_list1.get(i)));
                }

                Bitmap mbitmap = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.drawable.current);
                //初始化MoveHelper时的参数 Context,MapView,List<Bean>
                moveHelper = new TrackingMoveHelper(this, mapView, list)
                        .LineStyle(new com.supermap.data.Color(255, 215, 0, 0), 1.2) //Color.GRAY
                        .Precision(0.5)
                        .Fellow(true) //false
                        .Time(1)
                        .Icon(mbitmap, 50, 50)
                        .OnDraw(new TrackingMoveHelper.onDraw() {
                            @Override
                            public void onFinish() {
                                addEndCallout("end", m_list1.get(m_list1.size() - 1), MainActivity.this, mapView);
                                addStartCallout("start",m_list1.get(0),MainActivity.this,mapView);
                                m_final = String .format("%.2f",mlength);
                                textView.setText(m_final);
                                long time = endDate.getTime() - curDate.getTime();
                                textView1.setText(generateTime(time));
                                textView2.setText(String.valueOf(m_speed));
                                lay.setVisibility(View.VISIBLE);
                                cancelangle();
                            }

                            @Override
                            public void Length(double length) {
                                double d = length/1000;
                                mlength = mlength+d;
                                String result = String .format("%.2f",d);
                                textView.setText(result);
                            }
                        });
                moveHelper.start();
            } else {
                Toast.makeText(this, "请先记录轨迹", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将毫秒转时分秒
     *
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("00:%02d:%02d", minutes, seconds);
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void locationChangedListener(Point2D point2D, double accuracy, float bearing, float speed) {
        if(startloca){
            sourcePositionList.add(new Point2D(point2D));
            if(speed>0){
                m_speed=speed;
            }
        }
    }

}
