package com.supermap.trackmoving;


import com.supermap.data.Color;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.Datasource;
import com.supermap.data.Point2D;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingMoveData;
import com.supermap.mapping.TrackingMoveHelper;
import com.supermap.mapping.dyn.DynamicView;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    MapView mMapView;
    private MapControl mMapControl;
    private Map mMap;
    private DynamicView mDynamicView;
    private Workspace mWorkspace;
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
        requestPermissions();

        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        openMap();
    }

    //open
    private boolean openMap() {
        mWorkspace = new Workspace();
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapControl = mMapView.getMapControl();
        mMap = mMapControl.getMap();
        mMap.setWorkspace(mWorkspace);
        mDynamicView = new DynamicView(this, mMapControl.getMap());
        mMapView.addDynamicView(mDynamicView);

        //Rest
        openIServer_Rest();

        return true;
    }

    private Button btnStart, btnPause, btnReStart;
    private TrackingMoveHelper moveHelper;
    private List<TrackingMoveData> list;

    private void openIServer_Rest() {

        getList();

        btnStart = (Button) findViewById(R.id.btnStart);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnReStart = (Button) findViewById(R.id.btnReStart);
        Bitmap bitmap = BitmapFactory.decodeResource(mMapView.getContext().getResources(), R.drawable.track_point);
        //初始化MoveHelper时的参数 Context,MapView,List<Bean>
        moveHelper = new TrackingMoveHelper(this, mMapView, list)
                .LineStyle(new Color(255,0,255,0),0.8) //Color.GRAY
                .Precision(0.07)
                .Fellow(true) //false
                .Time(10)
                .Icon(bitmap,20,20)
                .OnDraw(new TrackingMoveHelper.onDraw() {
                    @Override
                    public void onFinish() {
                    }
                });
        //开始动画，在开始动画前，必须进行必要参数的设置
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveHelper.start();
            }
        });
        //暂停动画
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveHelper.pause();
            }
        });
        //重新开始动画
        btnReStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveHelper.reStart();
            }
        });

        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setAlias("testRest");
        info.setEngineType(EngineType.Rest);
        info.setServer("http://support.supermap.com.cn:8090/iserver/services/map-china400/rest/maps/China_4326");

        mMapControl.getMap().setWorkspace(mWorkspace);
        Datasource datasourceOSM = mWorkspace.getDatasources().open(info);
        if (datasourceOSM != null) {
            mMap.getLayers().add(datasourceOSM.getDatasets().get(0), true);
        }
//        mMap.viewEntire();
        mMap.setCenter(new Point2D(116.374908293842,40.0177291925482));
        mMap.setScale(0.00001);
        mMap.refresh();
    }


    public void getList() {
        list = new ArrayList<>();

        list.add(new TrackingMoveData("2017-08-17 20:09:00", new Point2D(116.374908293842, 40.0177291925482)));
        list.add(new TrackingMoveData("2017-08-17 20:10:00", new Point2D(116.374427318262, 40.0177243457963)));
        list.add(new TrackingMoveData("2017-08-17 20:13:00", new Point2D(116.373946342681, 40.0177194990444)));
        list.add(new TrackingMoveData("2017-08-17 20:14:00", new Point2D(116.373465367101, 40.0177146522925)));
        list.add(new TrackingMoveData("2017-08-17 20:15:00", new Point2D(116.37298439152, 40.0177098055407)));
        list.add(new TrackingMoveData("2017-08-17 20:16:00", new Point2D(116.37250341594, 40.0177049587888)));
        list.add(new TrackingMoveData("2017-08-17 20:18:00", new Point2D(116.372022440359, 40.0177001120369)));
        list.add(new TrackingMoveData("2017-08-17 20:19:00", new Point2D(116.371541464779, 40.017695265285)));
        list.add(new TrackingMoveData("2017-08-17 20:20:00", new Point2D(116.371541464779, 40.017695265285)));
        list.add(new TrackingMoveData("2017-08-17 20:21:00", new Point2D(116.371060489198, 40.0176904185331)));
        list.add(new TrackingMoveData("2017-08-17 20:23:00", new Point2D(116.370579513618, 40.0176855717812)));
        list.add(new TrackingMoveData("2017-08-17 20:26:00", new Point2D(116.370098538037, 40.0176807250294)));
        list.add(new TrackingMoveData("2017-08-17 20:27:00", new Point2D(116.369617562457, 40.0176758782775)));
        list.add(new TrackingMoveData("2017-08-17 20:28:00", new Point2D(116.369173719687, 40.0176714057099)));
        list.add(new TrackingMoveData("2017-08-17 20:29:00", new Point2D(116.36913658698, 40.0176717900063)));
        list.add(new TrackingMoveData("2017-08-17 20:30:00", new Point2D(116.368655612737, 40.0176767677376)));
        list.add(new TrackingMoveData("2017-08-17 20:31:00", new Point2D(116.368174638494, 40.0176817454689)));
        list.add(new TrackingMoveData("2017-08-17 20:32:00", new Point2D(116.367693664251, 40.0176867232003)));
        list.add(new TrackingMoveData("2017-08-17 20:34:00", new Point2D(116.367212690009, 40.0176917009316)));
        list.add(new TrackingMoveData("2017-08-17 20:35:00", new Point2D(116.366731715766, 40.0176966786629)));
        list.add(new TrackingMoveData("2017-08-17 20:36:00", new Point2D(116.366250741523, 40.0177016563943)));
        list.add(new TrackingMoveData("2017-08-17 20:37:00", new Point2D(116.36576976728, 40.0177066341256)));
        list.add(new TrackingMoveData("2017-08-17 20:38:00", new Point2D(116.365288793038, 40.0177116118569)));
        list.add(new TrackingMoveData("2017-08-17 20:39:00", new Point2D(116.364807818795, 40.0177165895883)));
        list.add(new TrackingMoveData("2017-08-17 20:40:00", new Point2D(116.364326844552, 40.0177215673196)));
        list.add(new TrackingMoveData("2017-08-17 20:42:00", new Point2D(116.363845870309, 40.0177265450509)));
        list.add(new TrackingMoveData("2017-08-17 20:43:00", new Point2D(116.363590055379, 40.0177291925482)));
        list.add(new TrackingMoveData("2017-08-17 20:44:00", new Point2D(116.363609160861, 40.0175048331795)));
        list.add(new TrackingMoveData("2017-08-17 20:45:00", new Point2D(116.363649973052, 40.0170255677363)));
        list.add(new TrackingMoveData("2017-08-17 20:46:00", new Point2D(116.363690785243, 40.016546302293)));
        list.add(new TrackingMoveData("2017-08-17 20:47:00", new Point2D(116.363731597434, 40.0160670368498)));
        list.add(new TrackingMoveData("2017-08-17 20:48:00", new Point2D(116.363772409625, 40.0155877714066)));
        list.add(new TrackingMoveData("2017-08-17 20:49:00", new Point2D(116.363813221816, 40.0151085059634)));
        list.add(new TrackingMoveData("2017-08-17 20:50:00", new Point2D(116.363816420148, 40.0150709473394)));
        list.add(new TrackingMoveData("2017-08-17 20:51:00", new Point2D(116.363373145473, 40.0150657243657)));
        list.add(new TrackingMoveData("2017-08-17 20:54:00", new Point2D(116.362892178859, 40.0150600572792)));
        list.add(new TrackingMoveData("2017-08-17 20:55:00", new Point2D(116.362411212244, 40.0150543901927)));
        list.add(new TrackingMoveData("2017-08-17 20:56:00", new Point2D(116.36193024563, 40.0150487231062)));
        list.add(new TrackingMoveData("2017-08-17 20:58:00", new Point2D(116.361449279016, 40.0150430560196)));
        list.add(new TrackingMoveData("2017-08-17 20:59:00", new Point2D(116.360968312401, 40.0150373889331)));
        list.add(new TrackingMoveData("2017-08-17 21:03:00", new Point2D(116.360487345787, 40.0150317218465)));
        list.add(new TrackingMoveData("2017-08-17 21:04:00", new Point2D(116.36000637917, 40.01502605476)));
        list.add(new TrackingMoveData("2017-08-17 21:05:00", new Point2D(116.359525412558, 40.0150203876735)));
        list.add(new TrackingMoveData("2017-08-17 21:06:00", new Point2D(116.359044445944, 40.015014720587)));
        list.add(new TrackingMoveData("2017-08-17 21:07:00", new Point2D(116.358911850146, 40.0150131582501)));
        list.add(new TrackingMoveData("2017-08-17 21:08:00", new Point2D(116.358570979859, 40.0150851757629)));
        list.add(new TrackingMoveData("2017-08-17 21:10:00", new Point2D(116.358100368602, 40.0151846043535)));
        list.add(new TrackingMoveData("2017-08-17 21:11:00", new Point2D(116.357629757344, 40.015284032944)));
        list.add(new TrackingMoveData("2017-08-17 21:12:00", new Point2D(116.357159146087, 40.0153834615346)));
        list.add(new TrackingMoveData("2017-08-17 21:13:00", new Point2D(116.356723657377, 40.0154754695942)));
        list.add(new TrackingMoveData("2017-08-17 21:14:00", new Point2D(116.356687795874, 40.0154738539862)));
        list.add(new TrackingMoveData("2017-08-17 21:15:00", new Point2D(116.356207283256, 40.015452206259)));
        list.add(new TrackingMoveData("2017-08-17 21:16:00", new Point2D(116.355726770638, 40.0154305585318)));
        list.add(new TrackingMoveData("2017-08-17 21:17:00", new Point2D(116.355246258021, 40.0154089108047)));
        list.add(new TrackingMoveData("2017-08-17 21:18:00", new Point2D(116.354765745403, 40.0153872630775)));
        list.add(new TrackingMoveData("2017-08-17 21:19:00", new Point2D(116.354285232785, 40.0153656153503)));
        list.add(new TrackingMoveData("2017-08-17 21:20:00", new Point2D(116.353804720167, 40.0153439676232)));
        list.add(new TrackingMoveData("2017-08-17 21:21:00", new Point2D(116.353324207549, 40.015322319896)));
        list.add(new TrackingMoveData("2017-08-17 21:23:00", new Point2D(116.352843694931, 40.0153006721688)));
        list.add(new TrackingMoveData("2017-08-17 21:25:00", new Point2D(116.352363182314, 40.0152790244417)));
        list.add(new TrackingMoveData("2017-08-17 21:26:00", new Point2D(116.351882669696, 40.0152573767145)));
        list.add(new TrackingMoveData("2017-08-17 21:27:00", new Point2D(116.351402157078, 40.0152357289873)));
        list.add(new TrackingMoveData("2017-08-17 21:28:00", new Point2D(116.35092164446, 40.0152140812602)));
        list.add(new TrackingMoveData("2017-08-17 21:29:00", new Point2D(116.350441131842, 40.015192433533)));
        list.add(new TrackingMoveData("2017-08-17 21:30:00", new Point2D(116.350309988915, 40.0151865253711)));
        list.add(new TrackingMoveData("2017-08-17 21:31:00", new Point2D(116.349960351001, 40.015178763666)));
        list.add(new TrackingMoveData("2017-08-17 21:32:00", new Point2D(116.349479469478, 40.0151680884489)));
        list.add(new TrackingMoveData("2017-08-17 21:33:00", new Point2D(116.348998587954, 40.0151574132319)));
        list.add(new TrackingMoveData("2017-08-17 21:34:00", new Point2D(116.348517706431, 40.0151467380148)));
        list.add(new TrackingMoveData("2017-08-17 21:35:00", new Point2D(116.348036824907, 40.0151360627978)));
        list.add(new TrackingMoveData("2017-08-17 21:36:00", new Point2D(116.347555943383, 40.0151253875807)));
        list.add(new TrackingMoveData("2017-08-17 21:37:00", new Point2D(116.34707506186, 40.0151147123637)));
        list.add(new TrackingMoveData("2017-08-17 21:38:00", new Point2D(116.346594180336, 40.0151040371466)));
        list.add(new TrackingMoveData("2017-08-17 21:39:00", new Point2D(116.346113298813, 40.0150933619296)));
        list.add(new TrackingMoveData("2017-08-17 21:40:00", new Point2D(116.345632417289, 40.0150826867125)));
        list.add(new TrackingMoveData("2017-08-17 21:41:00", new Point2D(116.345151535765, 40.0150720114955)));
        list.add(new TrackingMoveData("2017-08-17 21:42:00", new Point2D(116.345103599222, 40.0150709473394)));
        list.add(new TrackingMoveData("2017-08-17 21:43:00", new Point2D(116.344947351931, 40.0146670655348)));
        list.add(new TrackingMoveData("2017-08-17 21:44:00", new Point2D(116.344773804624, 40.01421846512)));
        list.add(new TrackingMoveData("2017-08-17 21:45:00", new Point2D(116.344600257316, 40.0137698647051)));
        list.add(new TrackingMoveData("2017-08-17 21:46:00", new Point2D(116.344426710009, 40.0133212642902)));
        list.add(new TrackingMoveData("2017-08-17 21:47:00", new Point2D(116.344253162702, 40.0128726638754)));
        list.add(new TrackingMoveData("2017-08-17 21:48:00", new Point2D(116.344079615395, 40.0124240634605)));
        list.add(new TrackingMoveData("2017-08-17 21:50:00", new Point2D(116.343906068088, 40.0119754630457)));
        list.add(new TrackingMoveData("2017-08-17 21:51:00", new Point2D(116.343906068088, 40.0119754630457)));
        list.add(new TrackingMoveData("2017-08-17 21:52:00", new Point2D(116.343896320452, 40.0119502664933)));
        list.add(new TrackingMoveData("2017-08-17 21:53:00", new Point2D(116.343706809304, 40.0115377296008)));
        list.add(new TrackingMoveData("2017-08-17 21:54:00", new Point2D(116.343506020442, 40.0111006428439)));
        list.add(new TrackingMoveData("2017-08-17 21:55:00", new Point2D(116.343305231581, 40.0106635560869)));
        list.add(new TrackingMoveData("2017-08-17 21:56:00", new Point2D(116.343104442719, 40.01022646933)));
        list.add(new TrackingMoveData("2017-08-17 21:57:00", new Point2D(116.342903653858, 40.0097893825731)));
        list.add(new TrackingMoveData("2017-08-17 21:58:00", new Point2D(116.342702864996, 40.0093522958161)));
        list.add(new TrackingMoveData("2017-08-17 21:59:00", new Point2D(116.342502076135, 40.0089152090592)));
        list.add(new TrackingMoveData("2017-08-17 22:02:00", new Point2D(116.342462676913, 40.0088294429572)));
        list.add(new TrackingMoveData("2017-08-17 22:03:00", new Point2D(116.342257378544, 40.0085018374045)));
        list.add(new TrackingMoveData("2017-08-17 22:04:00", new Point2D(116.342001961742, 40.0080942551955)));
        list.add(new TrackingMoveData("2017-08-17 22:05:00", new Point2D(116.341746544939, 40.0076866729866)));
        list.add(new TrackingMoveData("2017-08-17 22:06:00", new Point2D(116.341491128137, 40.0072790907776)));
        list.add(new TrackingMoveData("2017-08-17 22:07:00", new Point2D(116.341235711335, 40.00687150856866)));
        list.add(new TrackingMoveData("2017-08-17 22:08:00", new Point2D(116.340980294532, 40.0064639263597)));
        list.add(new TrackingMoveData("2017-08-17 22:09:00", new Point2D(116.34072487773, 40.0060563441507)));
        list.add(new TrackingMoveData("2017-08-17 22:11:00", new Point2D(116.340651758759, 40.0059396643103)));
        list.add(new TrackingMoveData("2017-08-17 22:12:00", new Point2D(116.340531781224, 40.0056180090937)));
        list.add(new TrackingMoveData("2017-08-17 22:13:00", new Point2D(116.340363681153, 40.0051673391855)));
        list.add(new TrackingMoveData("2017-08-17 22:16:00", new Point2D(116.340195581082, 40.0047166692773)));
        list.add(new TrackingMoveData("2017-08-17 22:17:00", new Point2D(116.340027481011, 40.0042659993692)));
        list.add(new TrackingMoveData("2017-08-17 22:20:00", new Point2D(116.339897209528, 40.0039167464714)));
        list.add(new TrackingMoveData("2017-08-17 22:23:00", new Point2D(116.339897209529, 40.0038085040946)));
        list.add(new TrackingMoveData("2017-08-17 22:25:00", new Point2D(116.339897209529, 40.0028465040946)));
        list.add(new TrackingMoveData("2017-08-17 22:26:00", new Point2D(116.339897209529, 40.0023655040946)));
        list.add(new TrackingMoveData("2017-08-17 22:29:00", new Point2D(116.339897209529, 40.0018845040946)));
        list.add(new TrackingMoveData("2017-08-17 22:31:00", new Point2D(116.339897209529, 40.0014035040946)));
        list.add(new TrackingMoveData("2017-08-17 22:35:00", new Point2D(116.339897209528, 40.0009689588623)));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        moveHelper.pause();
    }
}
