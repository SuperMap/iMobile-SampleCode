package com.supermap.pathsmoothtool;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.PathSmoothTool;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Point3D;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingMoveData;
import com.supermap.mapping.TrackingMoveHelper;
import com.tencent.map.geolocation.TencentLocation;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements TencentLocTool.LocationChangedListener, View.OnClickListener {
    String rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
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
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.WRITE_CALENDAR,
    };

    private MapView mMapView = null;
    private Workspace mWorkSpace = null;
    private MapControl mMapControl = null;
    private TrackingMoveHelper moveHelper = null;
    private List<TrackingMoveData> mList = new ArrayList<>();
    private List<Point2D> sourcePositionList = new ArrayList<>();
    private List<Point2D> outList = new ArrayList<>();
    private List<TrackingMoveData> list = new ArrayList<>();
    private Datasource datasource;
    private Dataset dataset;
    private static boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        Environment.setLicensePath(rootPath + "/SuperMap/license");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        Environment.setOpenGLMode(true);
        Environment.setWebCacheDirectory(rootPath + "/SuperMap/data/webCache");


        mMapView = (MapView) findViewById(R.id.mapview);
        mMapControl = mMapView.getMapControl();

        mWorkSpace = new Workspace();

//        WorkspaceConnectionInfo workspaceConnectionInfo = new WorkspaceConnectionInfo();
//        workspaceConnectionInfo.setType(WorkspaceType.SMWU);
//        workspaceConnectionInfo.setServer(rootPath+"/SampleData/BeijingMap/BeijingMap_DarkBlue.smwu");
//        mWorkSpace.open(workspaceConnectionInfo);

        DatasourceConnectionInfo dcInfo = new DatasourceConnectionInfo();
        dcInfo.setAlias("GoogleMaps");
        dcInfo.setEngineType(EngineType.GoogleMaps);
        dcInfo.setReadOnly(false);
        dcInfo.setServer("http://www.goole.cn/maps");
        mMapControl.getMap().setWorkspace(mWorkSpace);
        datasource = mWorkSpace.getDatasources().open(dcInfo);
        if (datasource != null) {
            dataset = datasource.getDatasets().get(1);
            if (dataset != null) {
                mMapControl.getMap().getLayers().add(dataset, true);
                mMapControl.getMap().refresh();
            }
        }

        Button startCollect = findViewById(R.id.startCollect);
        Button stopCollect = findViewById(R.id.stopCollect);
        Button showTrack = findViewById(R.id.showTrack);
        Button smmothTrack = findViewById(R.id.smmothTrack);
        Button clearCollect = findViewById(R.id.clearCollect);
        ImageButton location = findViewById(R.id.location);
        location.setOnClickListener(this);
        clearCollect.setOnClickListener(this);
        smmothTrack.setOnClickListener(this);
        showTrack.setOnClickListener(this);
        stopCollect.setOnClickListener(this);
        startCollect.setOnClickListener(this);


        TencentLocTool.getInstance().init(this);
        TencentLocTool.getInstance().setLocationManager(this);

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

    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void locationChangedListener(Point2D point2D, double accuracy, float bearing) {
        if (start) {
//            sourcePositionList.add(point2D);



        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startCollect:
                start = true;
                Toast.makeText(MainActivity.this, "开始记录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stopCollect:
                start = false;
                Toast.makeText(MainActivity.this, "停止记录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.showTrack:
                if (sourcePositionList.size() > 1) {
                    if (moveHelper != null) {
                        moveHelper.stop();
                    }
                    list.clear();

                    List<Point2D> m_list1 = new ArrayList<>();
                    for (int i = 0; i < sourcePositionList.size(); i++) {
                        PrjCoordSys Prj = mMapControl.getMap().getPrjCoordSys();
                        if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
                            Point2Ds points = new Point2Ds();
                            points.add(sourcePositionList.get(i));
                            PrjCoordSys desPrjCoorSys = new PrjCoordSys();
                            desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
                            CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
                                    new CoordSysTransParameter(),
                                    CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
                            m_list1.add(new Point2D(points.getItem(0).getX(), points.getItem(0).getY()));
                        }else{
							m_list1.add(sourcePositionList.get(i));
						}
                    }


                    for (int i = 0; i < m_list1.size(); i++) {
                        list.add(new TrackingMoveData("2017-08-17 20:09:00" + i, m_list1.get(i)));
                    }
                    Bitmap mbitmap = BitmapFactory.decodeResource(mMapView.getContext().getResources(), R.drawable.track_point);
                    //初始化MoveHelper时的参数 Context,MapView,List<Bean>
                    moveHelper = new TrackingMoveHelper(MainActivity.this, mMapView, list)
                            .LineStyle(new com.supermap.data.Color(255, 0, 255, 0), 0.8) //Color.GRAY
                            .Precision(0.07)
                            .Fellow(false) //false
                            .Time(5)
                            .Icon(mbitmap, 8, 8)
                            .OnDraw(new TrackingMoveHelper.onDraw() {
                                @Override
                                public void onFinish() {

                                }

                                @Override
                                public void Length(double v) {

                                }
                            });
                    moveHelper.start();
                } else {
                    Toast.makeText(MainActivity.this, "请先记录轨迹", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.smmothTrack:
                if (sourcePositionList.size() > 1) {
                    if (moveHelper != null) {
                        moveHelper.stop();
                    }
                    mList.clear();
                    PathSmoothTool pathSmoothTool = new PathSmoothTool();
                    pathSmoothTool.setIntensity(3);//设置滤波强度，默认3
                    outList = pathSmoothTool.pathOptimize(sourcePositionList);


                    List<Point2D> m_list2 = new ArrayList<>();
                    for (int i = 0; i < outList.size(); i++) {
                        PrjCoordSys Prj = mMapControl.getMap().getPrjCoordSys();
                        if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
                            Point2Ds points = new Point2Ds();
                            points.add(outList.get(i));
                            PrjCoordSys desPrjCoorSys = new PrjCoordSys();
                            desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
                            CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
                                    new CoordSysTransParameter(),
                                    CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
                            m_list2.add(new Point2D(points.getItem(0).getX(), points.getItem(0).getY()));
                        }
                    }

                    for (int i = 0; i < m_list2.size(); i++) {
                        mList.add(new TrackingMoveData("2017-08-17 20:09:00" + i, m_list2.get(i)));
                    }

                    if (mList.size() > 0) {
                        Bitmap bitmap = BitmapFactory.decodeResource(mMapView.getContext().getResources(), R.drawable.track_point);
                        //初始化MoveHelper时的参数 Context,MapView,List<Bean>
                        moveHelper = new TrackingMoveHelper(MainActivity.this, mMapView, mList)
                                .LineStyle(new com.supermap.data.Color(255, 0, 255, 0), 0.8) //Color.GRAY
                                .Precision(0.07)
                                .Fellow(false) //false
                                .Time(5)
                                .Icon(bitmap, 8, 8)
                                .OnDraw(new TrackingMoveHelper.onDraw() {
                                    @Override
                                    public void onFinish() {

                                    }

                                    @Override
                                    public void Length(double v) {

                                    }
                                });
                        moveHelper.start();
                    } else {
                        Toast.makeText(MainActivity.this, "轨迹错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "请先记录轨迹", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clearCollect:
                sourcePositionList.clear();
                outList.clear();
                mList.clear();
                list.clear();
                if (moveHelper != null) {
                    moveHelper.stop();
                }
                break;
            case R.id.location:
                locateMyPosition();
                mMapControl.getMap().setScale(2.58542883436988E-4);
                break;
        }
    }

    //定位到当前位置
    private void locateMyPosition() {
        Point2D point2D = getCurrentPoint2D();

        boolean contains = false;
        //中国版图范围
        contains = point2D.getX() >= 73.0 && point2D.getX() <= 135.0 && point2D.getY() >= 4.0 && point2D.getY() <= 53.0;

        if (contains && point2D.getX() != 0 && point2D.getY() != 0) {
            //当投影不是经纬坐标系时，则对点进行投影转换
            PrjCoordSys Prj = mMapControl.getMap().getPrjCoordSys();
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
            mMapControl.panTo(point2D, 200);

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

    //测试数据，网络影响定位时可使用此数据演示
//    {
//        sourcePositionList.add(new Point2D(104.067950,30.540799));
//        sourcePositionList.add(new Point2D(104.068214,30.540826));
//        sourcePositionList.add(new Point2D(104.068512,30.540830));
//        sourcePositionList.add(new Point2D(104.068759,30.540823));
//        sourcePositionList.add(new Point2D(104.068923,30.540709));
//        sourcePositionList.add(new Point2D(104.068912,30.540584));
//        sourcePositionList.add(new Point2D(104.068908,30.540494));
//        sourcePositionList.add(new Point2D(104.068874,30.540382));
//        sourcePositionList.add(new Point2D(104.068880,30.540294));
//        sourcePositionList.add(new Point2D(104.068881,30.540211));
//        sourcePositionList.add(new Point2D(104.068882,30.540168));
//        sourcePositionList.add(new Point2D(104.068888,30.540107));
//        sourcePositionList.add(new Point2D(104.068877,30.540045));
//        sourcePositionList.add(new Point2D(104.068898,30.539978));
//        sourcePositionList.add(new Point2D(104.068892,30.539913));
//        sourcePositionList.add(new Point2D(104.068891,30.539848));
//        sourcePositionList.add(new Point2D(104.068892,30.539775));
//        sourcePositionList.add(new Point2D(104.068888,30.539690));
//        sourcePositionList.add(new Point2D(104.068890,30.539613));
//        sourcePositionList.add(new Point2D(104.068879,30.539544));
//        sourcePositionList.add(new Point2D(104.068882,30.539485));
//        sourcePositionList.add(new Point2D(104.068875,30.539448));
//        sourcePositionList.add(new Point2D(104.068884,30.539416));
//        sourcePositionList.add(new Point2D(104.068882,30.539382));
//        sourcePositionList.add(new Point2D(104.068899,30.539348));
//        sourcePositionList.add(new Point2D(104.068890,30.539313));
//        sourcePositionList.add(new Point2D(104.068888,30.539282));
//        sourcePositionList.add(new Point2D(104.068890,30.539251));
//        sourcePositionList.add(new Point2D(104.068896,30.539219));
//        sourcePositionList.add(new Point2D(104.068891,30.539192));
//        sourcePositionList.add(new Point2D(104.068894,30.539156));
//        sourcePositionList.add(new Point2D(104.068896,30.539110));
//        sourcePositionList.add(new Point2D(104.068897,30.539052));
//        sourcePositionList.add(new Point2D(104.068874,30.539007));
//        sourcePositionList.add(new Point2D(104.068817,30.538963));
//        sourcePositionList.add(new Point2D(104.068785,30.538914));
//        sourcePositionList.add(new Point2D(104.068776,30.538855));
//        sourcePositionList.add(new Point2D(104.068762,30.538803));
//        sourcePositionList.add(new Point2D(104.068725,30.538767));
//        sourcePositionList.add(new Point2D(104.068677,30.538740));
//        sourcePositionList.add(new Point2D(104.068644,30.538692));
//        sourcePositionList.add(new Point2D(104.068599,30.538677));
//        sourcePositionList.add(new Point2D(104.068549,30.538673));
//        sourcePositionList.add(new Point2D(104.068503,30.538668));
//        sourcePositionList.add(new Point2D(104.068462,30.538657));
//        sourcePositionList.add(new Point2D(104.068430,30.538651));
//        sourcePositionList.add(new Point2D(104.068399,30.538646));
//        sourcePositionList.add(new Point2D(104.068356,30.538635));
//        sourcePositionList.add(new Point2D(104.068310,30.538631));
//        sourcePositionList.add(new Point2D(104.068269,30.538625));
//        sourcePositionList.add(new Point2D(104.068225,30.538624));
//        sourcePositionList.add(new Point2D(104.068174,30.538618));
//        sourcePositionList.add(new Point2D(104.068119,30.538616));
//        sourcePositionList.add(new Point2D(104.068057,30.538615));
//        sourcePositionList.add(new Point2D(104.067989,30.538616));
//        sourcePositionList.add(new Point2D(104.067940,30.538617));
//        sourcePositionList.add(new Point2D(104.067889,30.538615));
//        sourcePositionList.add(new Point2D(104.067846,30.538636));
//        sourcePositionList.add(new Point2D(104.067832,30.538680));
//        sourcePositionList.add(new Point2D(104.067832,30.538723));
//        sourcePositionList.add(new Point2D(104.067824,30.538759));
//        sourcePositionList.add(new Point2D(104.067805,30.538790));
//        sourcePositionList.add(new Point2D(104.067808,30.538833));
//        sourcePositionList.add(new Point2D(104.067790,30.538872));
//        sourcePositionList.add(new Point2D(104.067796,30.538920));
//        sourcePositionList.add(new Point2D(104.067784,30.538969));
//        sourcePositionList.add(new Point2D(104.067777,30.539020));
//        sourcePositionList.add(new Point2D(104.067762,30.539055));
//
//
//        sourcePositionList.add(new Point2D(104.067730,30.539093));
//        sourcePositionList.add(new Point2D(104.067699,30.539132));
//        sourcePositionList.add(new Point2D(104.067744,30.539152));
//        sourcePositionList.add(new Point2D(104.067747,30.539188));
//        sourcePositionList.add(new Point2D(104.067749,30.539226));
//        sourcePositionList.add(new Point2D(104.067754,30.539268));
//        sourcePositionList.add(new Point2D(104.067744,30.539312));
//        sourcePositionList.add(new Point2D(104.067760,30.539347));
//        sourcePositionList.add(new Point2D(104.067744,30.539388));
//        sourcePositionList.add(new Point2D(104.067753,30.539426));
//        sourcePositionList.add(new Point2D(104.067760,30.539463));
//        sourcePositionList.add(new Point2D(104.067751,30.539526));
//        sourcePositionList.add(new Point2D(104.067754,30.539557));
//        sourcePositionList.add(new Point2D(104.067750,30.539594));
//        sourcePositionList.add(new Point2D(104.067767,30.539625));
//        sourcePositionList.add(new Point2D(104.067769,30.539664));
//        sourcePositionList.add(new Point2D(104.067758,30.539694));
//        sourcePositionList.add(new Point2D(104.067770,30.539727));
//        sourcePositionList.add(new Point2D(104.067755,30.539753));
//        sourcePositionList.add(new Point2D(104.067762,30.539799));
//        sourcePositionList.add(new Point2D(104.067761,30.539848));
//        sourcePositionList.add(new Point2D(104.067746,30.539896));
//        sourcePositionList.add(new Point2D(104.067761,30.539952));
//        sourcePositionList.add(new Point2D(104.067750,30.540002));
//        sourcePositionList.add(new Point2D(104.067738,30.540046));
//        sourcePositionList.add(new Point2D(104.067749,30.540094));
//        sourcePositionList.add(new Point2D(104.067730,30.540135));
//        sourcePositionList.add(new Point2D(104.067745,30.540176));
//        sourcePositionList.add(new Point2D(104.067729,30.540220));
//        sourcePositionList.add(new Point2D(104.067716,30.540272));
//        sourcePositionList.add(new Point2D(104.067709,30.540329));
//        sourcePositionList.add(new Point2D(104.067691,30.540378));
//        sourcePositionList.add(new Point2D(104.067691,30.540426));
//        sourcePositionList.add(new Point2D(104.067691,30.540470));
//
//        sourcePositionList.add(new Point2D(104.067640,30.540499));
//        sourcePositionList.add(new Point2D(104.067674,30.540519));
//        sourcePositionList.add(new Point2D(104.067728,30.540546));
//        sourcePositionList.add(new Point2D(104.067780,30.540570));
//        sourcePositionList.add(new Point2D(104.067813,30.540605));
//        sourcePositionList.add(new Point2D(104.067836,30.540649));
//    }
}
