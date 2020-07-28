package com.supermap.android.speech;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.*;
import com.supermap.mapping.speech.*;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation2;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:语音控制示范代码
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范如何运用语音控制，需要用户自行放入讯飞的语音识别SDK。除产品包外，额外需要用到的依赖：pinyin4j.jar。
 * 2、示例数据：数据目录："/sdcard/SampleData/Beijing/"
 *            地图数据：beijing.smwu,clip1.udb,clip1.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *   SpeechControl.getInstance();//得到语音控制单例对象
 *   SpeechControl.setSpeechManager();//必须绑定SpeechManager,才能实现语音控制
 *   SpeechControl.setContext();//设置所需的上下文环境
 *   SpeechControl.setMapView();//绑定MapView
 *   SpeechControl.setDatasourceName();//设置需要查询的数据源别名
 *   SpeechControl.setDatasetName();//设置需要查询的数据集别名
 *   SpeechControl.setGPSData(); //设置当前位置（可多次实时更新）
 *   SpeechControl.setXName(); //设置查询的POI的X坐标属性名称
 *   SpeechControl.setYName();//设置查询的POI的Y坐标属性名称
 *   SpeechControl.setPOIName();//设置查询的POI名称的属性名称
 *   SpeechControl.setPOIType();//设置查询的POI类型的属性名称
 *   SpeechControl.setPOIRange();//设置查询的POI的范围
 *   SpeechControl.setLocationDrawable();//设置当前位置图标
 *   SpeechControl.setPOIDrawable();//设置POI展示图标
 *   SpeechControl.setPOISelectedDrawable();//设置POI点击后的图标
 *   SpeechControl.setStartPointDrawable();//设置导航起点图标
 *   SpeechControl.setDestPointDrawable();//设置导航终点图标
 *
 *   SpeechControl.starNoSpeechtListening();//设置文字命令回调监听
 *   SpeechControl.voiceCommand();//直接输入文字命令
 *
 *   SpeechControl.startListening();//设置语音控制回调监听
 *
 * 4、使用步骤：
 *  (1)点击搜索框，可直接输入文字命令。
 *  (2)点击语音图标，开始语音控制输入。
 *     示范的语音命令如下：
 *     定位到"故宫"、"故宫"在什么位置、"故宫"在哪里
 *     (定位到){我的位置/当前的位置/当前位置}
 *     我在{哪儿/哪里/什么位置}
 *     (我想/怎么/我要)去"地坛公园"
 *     去"地坛公园"(应该)怎么走
 *     从"地坛公园"到"工人体育场"(应该)怎么走
 *     从"地坛公园"到"工人体育场"的路线
 *     （搜索/查找/查询）（附近/周边）的"酒店"、（附近/周边）的"酒店"
 *     关闭第{}个地图、打开第{}个地图
 *     隐藏第几个图层、显示第几个图层
 *     {编辑/选择/选中}第"十二个"图层: "设置图层风格为颜色"
 *     上移(一点)地图、左移(一点)地图、右移(一点)地图、下移(一点)地图
 *     放大、缩小(一点)地图
 *    （向左、上、右、下）移动（一点）地图、（往左、上、右、下）移动（一点）地图
 *
 * 5、注意：
 *	如果运行本范例失败，常见原因是缺少语音资源。
 *  解决办法：请将产品包中Resource文件夹下的voice文件夹拷贝到工程目录中的assets文件夹下。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Workspace m_Workspace = null;
    private MapControl m_MapControl = null;
    private static Map m_Map = null;
    private MapView m_MapView = null;
    private Navigation2 m_Navigation2 = null;
    private boolean isFindPath = false;
    private boolean m_ExitEnable = false;
    public static MyApplication m_MyApp = null;

    private final String dataPath = MyApplication.SDCARD + "SampleData/Beijing/beijing.smwu";

    private ArrayList<POIInfo> mPoiList = null;
    public ArrayList<POIInfo> getPoiList() {
        return mPoiList;
    }
    public void setPoiList(ArrayList<POIInfo> poiList) {
        mPoiList = poiList;
    }

    //显示语音搜索对话框
    private void showSpeechSearchDialog() {
        if (mSpeechControl == null) {
            Log.e(TAG, "SpeechControl is null!");
            return;
        }

        SpeechSearchDialog dialog = new SpeechSearchDialog(this, R.style.SpeechDialog, mSpeechControl);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    private FloatingSearchView mSearchView;

    private ListView mSearchResultsListView = null;
    public SearchResultsListViewAdapter mSearchResultsListViewAdapter = null;

    private SlidingUpPanelLayout slidingUpPanelLayout = null;

    public LinearLayout mLL_draw = null;
    private Button mBtn_drawpoint = null;
    private Button mBtn_drawline = null;
    private Button mBtn_drawsurface = null;
    private Button mBtn_drawundo = null;
    private Button mBtn_drawsave = null;
    private Button mBtn_drawquit = null;

    //语音控制
    private SpeechControl mSpeechControl = null;
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
        Environment.setLicensePath(MyApplication.SDCARD + "SuperMap/License");
        Environment.setWebCacheDirectory(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperMap/WebCahe/");
        Environment.initialization(this);
        Environment.setOpenGLMode(true);
//        Environment.setFontsPath("/sdcard/SuperMap/fonts/");
        setContentView(R.layout.activity_main_speech);

        //语音组件初始化，需要自行传入对应的APPID
        SpeechManager.init(MainActivity.this, getString(R.string.app_id)); //先初始化

        initView();
        m_MyApp = MyApplication.getInstance();
        boolean isOpen = openWorkspace();

        if (isOpen) {
            initNavigation2();

            initSpeechControl();
        }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    // 初始化语音控制
    private void initSpeechControl() {
        mSpeechControl = SpeechControl.getInstance();//得到语音控制单例对象
        SpeechManager speechManager = SpeechManager.getInstance(MainActivity.this);//得到SpeechManager对象
        if (speechManager == null) {
            return;
        }
        mSpeechControl.setSpeechManager(speechManager);//必须绑定SpeechManager,才能实现语音控制

        SpeechMode.setSpeechModeType(SpeechMode.SpeechModeType.NORMAL);//设置语音控制模式（默认为NORMAL）

        mSpeechControl.setContext(MainActivity.this);
        mSpeechControl.setMapView(m_MapView);//绑定MapView

        mSpeechControl.setDatasourceName("clip1");//设置需要查询的数据源别名
        mSpeechControl.setDatasetName("POI_All_new");//设置需要查询的数据集别名

        mSpeechControl.setGPSData(new Point2D(116.422429, 39.935264)); //设置当前位置（可多次实时更新）

        mSpeechControl.setXName("smX"); //设置查询的POI的X坐标属性名称
        mSpeechControl.setYName("smY");//设置查询的POI的Y坐标属性名称
        mSpeechControl.setPOIName("Name");//设置查询的POI名称的属性名称
        mSpeechControl.setPOIType("Kind");//设置查询的POI类型的属性名称
        mSpeechControl.setPOIRange(500, 1000, 2000);//设置查询的POI的范围

        mSpeechControl.setLocationDrawable(R.drawable.icon_walk_start);//设置当前位置图标
        mSpeechControl.setPOIDrawable(R.drawable.b_poi_hl);//设置POI展示图标
        mSpeechControl.setPOISelectedDrawable(R.drawable.tmc_poi_hl);//设置POI点击后的图标
        mSpeechControl.setStartPointDrawable(R.drawable.startpoint);//设置导航起点图标
        mSpeechControl.setDestPointDrawable(R.drawable.destpoint);//设置导航终点图标

    }

    // 语音控制监听
    SpeechControlListener mSpeechControlListener = new SpeechControlListener() {
        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onVolumeChanged(int volume) {
        }

        @Override
        public void onError(String error) {
        }

        @Override
        public void onResult(String info, boolean isLast) {
        }

        @Override
        public void onPOIShow(ArrayList<POIInfo> poiList) {
            Log.e(TAG , "onPOIShow");
            showSlideLayout();

            mPoiList = poiList;
            mSearchResultsListViewAdapter.setList(poiList);
            mSearchResultsListViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPOIClick(ArrayList<POIInfo> poiList, POIInfo info, int position) {
            Log.e(TAG , "onPOIClick");
            showSlideLayout();

            mPoiList = poiList;
            mSearchResultsListViewAdapter.setList(mPoiList);
            mSearchResultsListViewAdapter.notifyDataSetChanged();
            moveToItem(position);
        }

        @Override
        public void onSpeechModeState(SpeechMode.SpeechModeType type) {
            Log.e(TAG , "onSpeechModeState：" + type );

            if (type == SpeechMode.SpeechModeType.EDIT) {
                mLL_draw.setVisibility(View.VISIBLE);
            } else if (type == SpeechMode.SpeechModeType.NORMAL) {
                mLL_draw.setVisibility(View.GONE);
            }

        }
    };

    int height = 0;
    /**
     * 初始化主界面控件
     */
    private void initView() {
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchResultsListView = (ListView) findViewById(R.id.listview);

        mLL_draw = (LinearLayout) findViewById(R.id.ll_draw);
        mBtn_drawpoint = (Button) findViewById(R.id.draw_point);
        mBtn_drawline = (Button) findViewById(R.id.draw_line);
        mBtn_drawsurface = (Button) findViewById(R.id.draw_surface);
        mBtn_drawundo = (Button) findViewById(R.id.draw_undo);
        mBtn_drawsave = (Button) findViewById(R.id.draw_save);
        mBtn_drawquit = (Button) findViewById(R.id.draw_quit);

        mBtn_drawpoint.setOnClickListener(onClickListener);
        mBtn_drawline.setOnClickListener(onClickListener);
        mBtn_drawsurface.setOnClickListener(onClickListener);
        mBtn_drawundo.setOnClickListener(onClickListener);
        mBtn_drawsave.setOnClickListener(onClickListener);
        mBtn_drawquit.setOnClickListener(onClickListener);

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        height = slidingUpPanelLayout.getPanelHeight();
        hideSlideLayout();

        setupFloatingSearch();
        setupResultsList();
    }

    public void hideSlideLayout() {
        slidingUpPanelLayout.setPanelHeight(0);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);//隐藏状态
    }

    // queryAndShowPoI()
    public void showSlideLayout() {
        slidingUpPanelLayout.setPanelHeight(height);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);//默认状态
    }

    private void setupFloatingSearch() {

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String query) {
                hideSlideLayout();

                clean();

                if (mSpeechControl != null) {
                    mSpeechControl.starNoSpeechtListening(mSpeechControlListener);//设置文字命令回调监听
                    mSpeechControl.voiceCommand(query);
                }

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
                    hideSlideLayout();
                    if (SpeechMode.getSpeechModeType().equals(SpeechMode.SpeechModeType.EDIT)) {
                        Toast.makeText(MainActivity.this, "提示：当前为图层编辑模式", Toast.LENGTH_SHORT).show();
                    } else if (SpeechMode.getSpeechModeType().equals(SpeechMode.SpeechModeType.NORMAL)) {
                        mLL_draw.setVisibility(View.GONE);
                    }

                    //语音识别
                    clean();
                    showSpeechSearchDialog();
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

    private void setupResultsList() {
        mSearchResultsListViewAdapter = new SearchResultsListViewAdapter(MainActivity.this);
        mSearchResultsListView.setAdapter(mSearchResultsListViewAdapter);

        mSearchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPoiList != null) {
                    for (int i = 0; i < mPoiList.size(); i++) {
                        if (i == position){
                            mPoiList.get(i).setSelected(true);
                        } else {
                            mPoiList.get(i).setSelected(false);
                        }

                    }

                    moveToItem(position);
                    mSearchResultsListViewAdapter.setList(mPoiList);
                    mSearchResultsListViewAdapter.notifyDataSetChanged();

                    locationPOI(mPoiList.get(position).getPoint2D());
                    showSelectedPointByCallout(mPoiList.get(position).getPoint2D(), "SelectedCallout", R.drawable.tmc_poi_hl);
                }
            }
        });

    }

    /**
     * 打开工作空间，显示地图
     */
    private boolean openWorkspace() {
        m_Workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(dataPath);
        info.setType(WorkspaceType.SMWU);
//		info.setType(WorkspaceType.SXWU);
        boolean isOpen = m_Workspace.open(info);
        if (!isOpen) {
            m_MyApp.showInfo("Workspace open failed!");
            return false;
        }
        m_MapView = (MapView) findViewById(R.id.mapView);
        m_MapControl = m_MapView.getMapControl();
        m_Map = m_MapControl.getMap();
        m_Map.setWorkspace(m_Workspace);
        m_Map.open(m_Workspace.getMaps().get(0));    // open map
        m_MapControl.setGestureDetector(new GestureDetector(longTouchListener));

//        m_Map.viewEntire();
        m_Map.setViewBounds(m_Map.getBounds());
        m_Map.refresh();

        return true;
    }

    // 手势监听事件
    private GestureDetector.SimpleOnGestureListener longTouchListener = new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent event) {
        }

        // 地图漫游
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (m_Navigation2 != null && m_Navigation2.isGuiding()) {
                m_Navigation2.enablePanOnGuide(true);
            }
            return false;
        }

    };

    /**
     * 初始化行业导航控件
     */
    private void initNavigation2() {
        String networkDatasetName = "BuildNetwork";         // 已有的网络数据集的名称

        // 初始化行业导航对象
        try {
            DatasetVector networkDataset = (DatasetVector) m_Workspace.getDatasources().get("clip1").getDatasets().get(networkDatasetName);
            m_Navigation2 = m_MapControl.getNavigation2();      // 获取行业导航控件，只能通过此方法初始化m_Navigation2
            m_Navigation2.setPathVisible(true);                 // 设置分析所得路径可见
            m_Navigation2.setNetworkDataset(networkDataset);    // 设置网络数据集
            m_Navigation2.loadModel(MyApplication.SDCARD + "SampleData/Beijing/NetworkModel.snm");  // 加载网络模型
        } catch (Exception e) {
            e.printStackTrace();
        }

        m_Navigation2.addNaviInfoListener(new NaviListener() {

            @Override
            public void onStopNavi() {
                // 导航停止后，显示按钮界面
                showLayout();
                clean();
            }

            @Override
            public void onStartNavi() {
                // 导航开始前，先隐藏按钮界面
                hideLayout();
                hideSlideLayout();
            }

            @Override
            public void onNaviInfoUpdate(NaviInfo arg0) {
            }

            @Override
            public void onAarrivedDestination() {
                // 到达目的地后，显示按钮界面
                showLayout();
                clean();
            }

            @Override
            public void onAdjustFailure() {
            }

            @Override
            public void onPlayNaviMessage(String arg0) {
            }
        });
    }

    // 按钮单击监听事件
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.draw_point:
                    m_MapControl.setAction(Action.CREATEPOINT);

                    break;
                case R.id.draw_line:
                    m_MapControl.setAction(Action.CREATEPOLYLINE);

                    break;
                case R.id.draw_surface:
                    m_MapControl.setAction(Action.CREATEPOLYGON);

                    break;
                case R.id.draw_undo:
                    m_MapControl.undo();

                    break;
                case R.id.draw_save:
                    if (mSpeechControl != null) {
                        mSpeechControl.saveEdit();
                    }

                    break;
                case R.id.draw_quit:
                   if (mSpeechControl != null) {
                       mSpeechControl.quitEditMode();
                   }
                    break;
                default:
                    break;
            }
            m_Map.refresh();
        }
    };

    //poi导航
    public void startNavi(Point2D point2D_center, Point2D destPoint2D) {
        if (point2D_center == null || destPoint2D == null) {
            return;
        }

        hideSlideLayout();

        m_MapView.removeAllCallOut();
        //跟踪层
        clearTrackingLayer();

        Point2D start = getPoint(point2D_center, "startPoint", R.drawable.startpoint);
        Point2D dest = getPoint(destPoint2D, "destPoint", R.drawable.destpoint);

        m_Navigation2.setStartPoint(start.getX(), start.getY());        // 设置起点
        m_Navigation2.setDestinationPoint(dest.getX(), dest.getY());    // 设置终点
        m_Navigation2.setPathVisible(true);                             // 设置路径可见
        isFindPath = m_Navigation2.routeAnalyst();                      // 路径分析
        if (isFindPath) {
            m_Map.refresh();                                            // 刷新后可显示所得路径
        } else {
            m_MyApp.showInfo("路径分析失败！");
        }
        if (isFindPath) {
            // 导航开始前，先隐藏按钮界面
            m_Navigation2.startGuide(1);

            hideLayout();
            hideSlideLayout();
        }
    }

    /**
     *
     */
    private Point2D getPoint(Point2D point2D, final String pointName, final int idDrawable) {
        showNaviPointByCallout(point2D, pointName, idDrawable);

        if (m_Map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
            PrjCoordSys srcPrjCoordSys = m_Map.getPrjCoordSys();
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

    /**
     * 显示导航Callout
     */
    private void showNaviPointByCallout(final Point2D point, final String pointName,
                                        final int id) {
        CallOut callOut = new CallOut(m_MapView.getContext());
        callOut.setStyle(CalloutAlignment.BOTTOM);
        callOut.setCustomize(true);
        callOut.setLocation(point.getX(), point.getY());
        ImageView imageView = new ImageView(m_MapView.getContext());
        imageView.setBackgroundResource(id);

        callOut.setContentView(imageView);
        m_MapView.addCallout(callOut, pointName);
    }

    //导航时隐藏界面
    public void hideLayout() {
        mSearchView.setVisibility(View.GONE);
        mLL_draw.setVisibility(View.GONE);
    }

    public void showLayout() {
        mSearchView.setVisibility(View.VISIBLE);
    }

    /**
     * 停止导航、清除起点、终点、路径
     * 清空Callout
     * 清空跟踪层
     */
    private void clean() {
        if (m_Navigation2 != null){
            if (m_Navigation2.isGuiding())
                m_Navigation2.stopGuide();           // 停止正在进行的导航
            m_Navigation2.cleanPath();               // 清除路径，需在停止导航后进行，否则无效
            m_Navigation2.enablePanOnGuide(false);
        }

        m_MapView.removeAllCallOut();//清空Callout
        clearTrackingLayer(); //清空跟踪层

        m_Map.refresh();

        isFindPath = false;
    }

    /**
     * 显示选择的POI
     * pointName = SelectedCallout
     */
    public void showSelectedPointByCallout(Point2D point, String pointName, int id) {
        CallOut callOut = new CallOut(m_MapView.getContext());
        callOut.setStyle(CalloutAlignment.BOTTOM);
        callOut.setCustomize(true);
        callOut.setLocation(point.getX(), point.getY());
        ImageView imageView = new ImageView(m_MapView.getContext());
        Bitmap bitmap = BitmapFactory.decodeResource(m_MapView.getContext().getResources(), id);
        Bitmap zoomImg = zoomImg(bitmap, 110, 150);
        imageView.setImageBitmap(zoomImg);

//        imageView.setBackgroundResource(id);
        callOut.setContentView(imageView);

        m_MapView.removeCallOut(pointName);
        m_MapView.addCallout(callOut, pointName);
    }

    private Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 清空跟踪层
     */
    private void clearTrackingLayer() {
        if (m_Map.getTrackingLayer().getCount() < 1) {
            return;
        }

        m_Map.getTrackingLayer().clear();
        m_Map.refresh();
    }

    //(POI结果列表)ListView移动到指定位置
    public void moveToItem(int potision) {
        mSearchResultsListView.smoothScrollToPosition(potision);
//        mSearchResultsListView.setSelection(potision);
    }

    //定位POI点
    public void locationPOI(Point2D point2D) {
        if (m_Map.getBounds().contains(point2D)) {
//            m_Map.setScale(0.0002);
            m_MapControl.panTo(point2D, 300);
            m_Map.refresh();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!m_ExitEnable) {
                m_MyApp.showInfo("再按一次退出程序！");
                m_ExitEnable = true;
            } else {
                m_MyApp.exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}



