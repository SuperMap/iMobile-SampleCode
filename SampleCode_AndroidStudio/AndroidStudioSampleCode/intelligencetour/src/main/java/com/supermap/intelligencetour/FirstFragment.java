package com.supermap.intelligencetour;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.supermap.ar.ARRendererInfoUtil;
import com.supermap.ar.ArObject;
import com.supermap.ar.ArView;
import com.supermap.ar.ArViewAdapter;
import com.supermap.ar.CameraView;
import com.supermap.ar.GeoObject;
import com.supermap.ar.LowPassFilter;
import com.supermap.ar.OnClickArObjectListener;
import com.supermap.ar.World;
import com.supermap.ar.WorldLocationChanged;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.onlineservices.CoordinateType;
import com.supermap.onlineservices.Geocoding;
import com.supermap.onlineservices.GeocodingData;
import com.supermap.onlineservices.POIInfo;
import com.supermap.onlineservices.POIQuery;
import com.supermap.onlineservices.POIQueryParameter;
import com.supermap.onlineservices.POIQueryResult;
import com.supermap.plugin.LocationManagePlugin;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;

/**
 * 界面一
 */
public class FirstFragment extends Fragment implements View.OnClickListener, OnClickArObjectListener, WorldLocationChanged ,TencentLocationListener {

    private static final String TAG = "FirstFragment";
    private Context mContext = null;

    private ArView mArView;
    private World mWorld;
    Point2Ds point2Ds;
    ArrayList<String> strArray;
    private CameraView mArCameraView;
    private FloatingSearchView mSearchView;
    SlidingUpPanelLayout floating_search_view;
    private POIInfo[] poiInfos;
    boolean result = false;
    TextView nowaddress;
    String str="";
    String queryStr="";

    TencentLocationManager mLocationManager;
    private TencentLocation m_locationInfo = null;
    //当前位置信息,用于导航和巡航
    private LocationManagePlugin.GPSData m_GPSData = null;
    private Point2D m_Point = new Point2D(0,0);

    private Date startDate = null;//开始监听
    //毫秒
    public int calLastedTime(Date startDate) {
        long a = new Date().getTime();
        long b = startDate.getTime();
        int c = (int)(a - b);//毫秒
        return c;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ARRendererInfoUtil.saveARRendererMode(getActivity(), ARRendererInfoUtil.MODE_PROJECTION);
        ConcurrentMap map = new ConcurrentHashMap();
        map.keySet();
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        //初始化布局
        initView(rootView);
        //初始化相机
        initCamera(rootView);
        //初始化位置
        initLocation(rootView);
        //初始化ArView
        initArView(rootView);

        initARWord();

        initLowPassFilter();
        return rootView;
    }

    private void initLowPassFilter() {
        LowPassFilter.ALPHA = 0.01f;
    }

    private void initARWord() {

        // 创建增强现实世界
        mWorld = new World(getContext());
        mWorld.setGeoPosition(116.500316,39.983976);//设置当前位置
        mArView.setWorld(mWorld);//AR场景关联
    }

    private void initLocation(View rootView) {

        mLocationManager = TencentLocationManager.getInstance(getContext());

        int error = TencentLocationManager.getInstance(getContext())
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

    private void initCamera(View rootView){

        RelativeLayout relativeLayout = rootView.findViewById(R.id.camera);
        FrameLayout.LayoutParams cameraViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mArCameraView = new CameraView(getContext());
        relativeLayout.addView(mArCameraView,0,cameraViewParams);
    }

    private void initArView(View rootView) {

        mArView = rootView.findViewById(R.id.arView);
        mArView.setDistanceFactor(0.98f);
        mArView.setMaxDistanceToRender(2500);
//        mArView.setHead(10);

        mArView.setPOIOverlapEnable(true);
        mArView.setOnClickArObjectListener(this);
        CustomArViewAdapter customArViewAdapter = new CustomArViewAdapter(getActivity());
        mArView.setArViewAdapter(customArViewAdapter);

    }

    private void initView(View rootView) {
//        m_locationTencent = new LocationTencent(getActivity());
        mSearchView = (FloatingSearchView) rootView.findViewById(R.id.floating_search_view);
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                query(currentQuery);
            }
        });

        rootView.findViewById(R.id.ditie).setOnClickListener(this);
        rootView.findViewById(R.id.chuzuche).setOnClickListener(this);
        rootView.findViewById(R.id.gongjiaozhan).setOnClickListener(this);
        rootView.findViewById(R.id.qichezhan).setOnClickListener(this);
        rootView.findViewById(R.id.yaodian).setOnClickListener(this);
        rootView.findViewById(R.id.bianlidian).setOnClickListener(this);
        rootView.findViewById(R.id.shangchang).setOnClickListener(this);
        rootView.findViewById(R.id.chaoshi).setOnClickListener(this);
        rootView.findViewById(R.id.wc).setOnClickListener(this);
        rootView.findViewById(R.id.park).setOnClickListener(this);
        rootView.findViewById(R.id.atm).setOnClickListener(this);
        rootView.findViewById(R.id.youju).setOnClickListener(this);
        rootView.findViewById(R.id.movie).setOnClickListener(this);
        rootView.findViewById(R.id.jiudian).setOnClickListener(this);
        rootView.findViewById(R.id.ktv).setOnClickListener(this);
        rootView.findViewById(R.id.wangba).setOnClickListener(this);
        rootView.findViewById(R.id.mianguan).setOnClickListener(this);
        rootView.findViewById(R.id.xican).setOnClickListener(this);
        rootView.findViewById(R.id.huoguo).setOnClickListener(this);
        rootView.findViewById(R.id.shaokao).setOnClickListener(this);
        rootView.findViewById(R.id.jiuba).setOnClickListener(this);
        rootView.findViewById(R.id.cafe).setOnClickListener(this);
        rootView.findViewById(R.id.zizhu).setOnClickListener(this);
        rootView.findViewById(R.id.kuaican).setOnClickListener(this);

        floating_search_view = rootView.findViewById(R.id.sliding_layout);
        floating_search_view.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelStateChanged(View view, SlidingUpPanelLayout.PanelState panelState, SlidingUpPanelLayout.PanelState panelState1) {
//                panelState1 == SlidingUpPanelLayout.PanelState.DRAGGING &&
                if(panelState1 == SlidingUpPanelLayout.PanelState.EXPANDED){
                    //隐藏图层POI
                    mWorld.clearWorld();
//                    mArView.setVisibility(View.INVISIBLE);
                }
            }
        });
        nowaddress = rootView.findViewById(R.id.address);

    }


    @Override
    public void onWorldLocationChanged(LocationManagePlugin.GPSData gpsData) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fab:
//                Toast.makeText(mContext, "自定义操作", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.ditie:
//                Intent intent = new Intent(getContext(), test.class);
//                startActivity(intent);
                query("地铁");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.chuzuche:
                query("出租车");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.gongjiaozhan:
                query("公交站");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.qichezhan:
                query("汽车站");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.yaodian:
                query("药店");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.bianlidian:
                query("便利店");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.shangchang:
                query("商场");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.chaoshi:
                query("超市");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.wc:
                query("厕所");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.park:
                query("公园");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.atm:
                query("ATM");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.youju:
                query("邮局");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.movie:
                query("电影院");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.jiudian:
                query("酒店");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.ktv:
                query("KTV");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.wangba:
                query("网吧");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.mianguan:
                query("面馆");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.xican:
                query("西餐");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.huoguo:
                query("火锅");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.shaokao:
                query("烧烤");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.jiuba:
                query("酒吧");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.cafe:
                query("咖啡");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.zizhu:
                query("自助餐");
                floating_search_view.setPanelState(COLLAPSED);
                break;
            case R.id.kuaican:
                query("快餐");
                floating_search_view.setPanelState(COLLAPSED);
                break;
        }
    }


    public void query(String str) {
        mWorld.clearWorld();
        if (m_Point.getY() == 0) {
            Toast.makeText(getContext(), "定位失败请检查网络", Toast.LENGTH_SHORT).show();
        } else {
            mWorld.setGeoPosition(m_Point.getX(), m_Point.getY());
            Log.e("--------------",""+m_Point.getX()+"++++++++++++"+ m_Point.getY());
            queryPOI(str);
            queryStr = str;
        }
    }


    public void queryPOI(final String str) {
        POIQuery poiQuery = new POIQuery(getContext());
        POIQueryParameter queryParameter = new POIQueryParameter();
        //			用户申请的钥匙
        queryParameter.setKey("fvV2osxwuZWlY0wJb8FEb2i5");
//			查询的关键字
        queryParameter.setKeywords(str);
        //			在某个范围内查询
        queryParameter.setCity("北京市");

        queryParameter.setPageSize(10);

        queryParameter.setLocation(m_Point.getX(), m_Point.getY());

//        queryParameter.setLocation(104.065789,30.539893);

        queryParameter.setRadius("1000");

        queryParameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_LONGITUDE_LATITUDE);
//			进行POI查询
        poiQuery.query(queryParameter);
        //			查看POI查询是否成功
        poiQuery.setPOIQueryCallback(new POIQuery.POIQueryCallback() {
            @Override
            public void querySuccess(POIQueryResult queryResult) {
                result = true;
                Toast.makeText(getContext(), "查询成功:附近1KM内共" + queryResult.getPOIInfos().length + "个查询对象", Toast.LENGTH_SHORT).show();
                poiInfos = queryResult.getPOIInfos();
                point2Ds = new Point2Ds();
                strArray = new ArrayList<String>();
                for (int i = 0; i < poiInfos.length; i++) {
                    Point2D point2D = poiInfos[i].getLocation();
                    point2Ds.add(point2D);
                    strArray.add(poiInfos[i].getName());
                }

                createScreenCoordPoi(getResources().getDisplayMetrics().widthPixels / 2,
                        getResources().getDisplayMetrics().heightPixels / 2, str);
            }

            @Override
            public void queryFailed(String errInfo) {
                Toast.makeText(getContext(), errInfo, Toast.LENGTH_SHORT).show();
            }
        });




        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("搜索中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (result) {
                            dialog.dismiss();
                            result=false;
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
            }
        }).start();
        dialog.show();

    }

    private void createScreenCoordPoi(int x, int y, String str) {

        for (int i = 0; i < point2Ds.getCount(); i++) {
            GeoObject tempArObject = new GeoObject(System.currentTimeMillis() + i);
            tempArObject.setGeoPosition(point2Ds.getItem(i).getX(),point2Ds.getItem(i).getY());
            tempArObject.setName(i+strArray.get(i).replace("/",""));
            updateImagesByStaticView(tempArObject, strArray.get(i), poiInfos[i].getAddress(), str,point2Ds.getItem(i));

            mWorld.addArObject(tempArObject);

        }
    }

    //计算经纬度距离
    public static int getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return (int) s;
    }

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    private void updateImagesByStaticView(ArObject arObject, String name, String address, String str, Point2D point2D) {
        View view = getLayoutInflater().inflate(R.layout.ar_object_view_wrapcontent, null);

        TextView textView = view.findViewById(R.id.tv_name);
        textView.setText(name);

        TextView m_address = view.findViewById(R.id.tv_address);
        m_address.setText(address);

        TextView m_info = view.findViewById(R.id.info);
        m_info.setText(String.valueOf(getDistance(point2D.getY(),point2D.getX(),m_Point.getY(),m_Point.getX())+"米"));


        ImageView iv = (ImageView) view.findViewById(R.id.ai_ar_content);

        switch (str) {
            case "地铁":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.ditie));
                break;
            case "出租车":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.dishi));
                break;
            case "公交站":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.gongjiaozhan));
                break;
            case "汽车站":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.qichezhan));
                break;
            case "药店":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.yaodian));
                break;
            case "便利店":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.bianlidian));
                break;
            case "商场":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.shangchang));
                break;
            case "超市":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.chaoshi));
                break;
            case "厕所":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.wc));
                break;
            case "公园":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.park));
                break;
            case "ATM":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.yinhang));
                break;
            case "邮局":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.youju));
                break;
            case "电影院":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.dianyingyuan));
                break;
            case "酒店":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.jiudian));
                break;
            case "KTV":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.ktv));
                break;
            case "网吧":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.wangba));
                break;
            case "面馆":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.mianguan));
                break;
            case "西餐":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.xican));
                break;
            case "火锅":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.huoguo));
                break;
            case "烧烤":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.shaokao));
                break;
            case "酒吧":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.jiuba));
                break;
            case "咖啡":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.kafei));
                break;
            case "自助餐":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.zizhucan));
                break;
            case "快餐":
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.kuaican));
                break;
            default:
                iv.setImageDrawable(getResources().getDrawable(R.mipmap.address));
                break;
        }
        mArView.storeArObjectViewAndUri(view, arObject);
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
        ReverseGeocoding();
        Point2D point2D = m_Point;
        Log.e("-------------",""+point2D);
    }


    private class CustomArViewAdapter extends ArViewAdapter {
        LayoutInflater inflater;

        public CustomArViewAdapter(Context context) {
            super(context);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(ArObject arObject, View recycledView, ViewGroup parent) {
            return recycledView;
        }
    }

    @Override
    public void onClickArObject(ArrayList<ArObject> arObjects) {
        ArObject arObject = arObjects.get(0);
        for (int i = 0; i < point2Ds.getCount(); i++) {
            if (arObject.getName().indexOf(i+strArray.get(i).replace("/","")) != -1) {
                Intent intent = new Intent(getContext(), SearchDemo.class);
                intent.putExtra("name", poiInfos[i].getName());
                intent.putExtra("address", poiInfos[i].getAddress());
                intent.putExtra("telephone", poiInfos[i].getTelephone());
                intent.putExtra("pointx", poiInfos[i].getLocation().getX());
                intent.putExtra("pointy", poiInfos[i].getLocation().getY());
                intent.putExtra("locationx", m_Point.getX());
                intent.putExtra("locationy", m_Point.getY());
                intent.putExtra("queryStr",queryStr);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mArCameraView.releaseCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mArCameraView.releaseCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        mArCameraView.startPreviewCamera();
    }

    public void ReverseGeocoding(){
        Geocoding reverseGeocoding = new Geocoding();
//			必须调用
//			设置钥匙
        reverseGeocoding.setKey("fvV2osxwuZWlY0wJb8FEb2i5");
        reverseGeocoding.setGeocodingCallback(new Geocoding.GeocodingCallback() {
            @Override
            public void reverseGeocodeSuccess(final GeocodingData data) {
                if (m_Point.getY() != 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            str = data.getFormatedAddress();
                            handler.sendEmptyMessage(0x123);
                        }
                    }).start();
                }
            }
            @Override
            public void geocodeSuccess(List<GeocodingData> dataList) {
            }
            @Override
            public void geocodeFailed(String errorMsg) {
            }
        });
//			进行逆地理编码
        reverseGeocoding.reverseGeocoding(new Point2D(m_Point.getX(),m_Point.getY()));
    }

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0x123)
            {
                if(!str.equals("[]")){
                    nowaddress.setText(str);
                }
            }
        };
    };

}
