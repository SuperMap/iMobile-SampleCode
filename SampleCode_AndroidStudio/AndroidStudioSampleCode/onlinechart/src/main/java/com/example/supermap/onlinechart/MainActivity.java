package com.example.supermap.onlinechart;

/**
 * <p>
 * Title:分布式分析服务
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   	展示聚合分析、密度分析、缓冲区分析等分布式分析服务。
 *
 * 2、Demo数据：
 * 		数据目录："../SampleData/OnlineChartData/"
 *      地图数据："chart.smwu", "chart.udb", "chart.udb"
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    AggregatePointsOnline			类
 *    BufferAnalystOnline		    类
 *    DensityAnalystOnline			类
 *    OverlayAnalystOnline			类
 *    QueryOnline				    类
 *    SummaryRegionOnline		    类
 *    TopologyValidatorOnline       类
 *    VectorClipAnalystOnline       类
 *    addListener()                 方法
 *    setDataPath()                 方法
 *    execute()                     方法
 *    setDatasetSource()            方法
 *    setMeshType()                 方法
 *
 * 4、功能展示
 *   (1)点聚合分析；
 *   (2)创建缓冲区分析；
 *   (3)密度分析；
 *   (4)叠加分析
 *   (5)单对象空间查询；
 *   (6)区域汇总；
 *   (7)创建拓扑检查；
 *   (8)矢量裁剪。
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

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.distributeanalystservices.AggregatePointsOnline;
import com.supermap.distributeanalystservices.BufferAnalystOnline;
import com.supermap.distributeanalystservices.DensityAnalystOnline;
import com.supermap.distributeanalystservices.DistributeAnalystListener;
import com.supermap.distributeanalystservices.QueryOnline;
import com.supermap.distributeanalystservices.SummaryRegionOnline;
import com.supermap.distributeanalystservices.VectorClipAnalystOnline;
import com.supermap.distributeanalystservices.OverlayAnalystOnline;
import com.supermap.distributeanalystservices.TopologyValidatorOnline;
import com.supermap.distributeanalystservices.FeatureJoinOnline;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements DistributeAnalystListener{

    private MapControl m_mapControl = null;
    private Workspace m_wokspace = null;
    private Datasource m_datasource = null;

    private Button m_btnLoad = null;
    private TextView m_txtTitle = null;

    String m_ip = "192.168.12.4";
    String m_port = "8090";
    String m_name = "iMobile";
    String m_password = "iMobile_910";

    private AggregatePointsOnline m_AggregatePointsOnline = new AggregatePointsOnline();        //聚合分析
    private BufferAnalystOnline m_BufferAnalystOnline = new BufferAnalystOnline();              //缓冲区分析
    private DensityAnalystOnline m_DensityAnalystOnline = new DensityAnalystOnline();           //密度分析
    private OverlayAnalystOnline m_OverlayAnalystOnline = new OverlayAnalystOnline();           //叠加分析
    private QueryOnline m_QueryOnline = new QueryOnline();                                        //单对象空间查询
    private SummaryRegionOnline m_SummaryRegionOnline = new SummaryRegionOnline();               //区域汇总
    private TopologyValidatorOnline m_TopologyValidatorOnline = new TopologyValidatorOnline();  //创建拓扑检查
    private VectorClipAnalystOnline m_VectorClipAnalystOnline = new VectorClipAnalystOnline();  //矢量裁剪
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
        requestPermissions() ;
        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);
        //设置许可文件路径
        String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        Environment.setLicensePath(sdcard + "/SuperMap/license/");

        setContentView(R.layout.activity_main);

        //打开工作空间
        m_wokspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard+"/SampleData/OnlineChartData/chart.smwu");
        info.setType(WorkspaceType.SMWU);
        m_wokspace.open(info);

        //将地图显示控件和工作空间关联
        MapView mMapView = (MapView)findViewById(R.id.Map_view);
        m_mapControl =  mMapView.getMapControl();
        m_mapControl.getMap().setWorkspace(m_wokspace);

        //打开工作空间中的地图
        String mapName = m_wokspace.getMaps().get(1);
        m_mapControl.getMap().open(mapName);
        //设置使用全屏绘制模式,点、文字和普通图层同时显示
        m_mapControl.getMap().setFullScreenDrawModel(true);
        //刷新地图
        m_mapControl.getMap().refresh();

        m_btnLoad = (Button) findViewById(R.id.etLoad_ada);
        m_txtTitle = (TextView) findViewById(R.id.txtTitle);

        //点聚合分析
        Button btnAggregatePointsOnline = (Button) findViewById(R.id.buttonAggregatePoints);
        btnAggregatePointsOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildAggregatePoints();
            }
        });
        //创建缓冲区分析
        Button btnBuffersOnline = (Button) findViewById(R.id.buttonBuffers);
        btnBuffersOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildBuffers();
            }
        });
        //密度分析
        Button btnDensityAnalystOnline = (Button) findViewById(R.id.buttonDensity);
        btnDensityAnalystOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildDensityAnalystOnline();
            }
        });
        //叠加分析
        Button btnOverlayAnalystOnline = (Button)findViewById(R.id.buttonOverlay);
        btnOverlayAnalystOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildOverlayAnalystOnline();
            }
        });
        //单对象空间查询分析
        Button btnQueryOnline = (Button) findViewById(R.id.buttonQuery);
        btnQueryOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildQuery();
            }
        });
        //区域汇总分析
        Button btnSummaryRegionOnline = (Button) findViewById(R.id.buttonSummaryRegion);
        btnSummaryRegionOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BulidSummaryRegion();
            }
        });
        //创建拓扑检查
        Button btnTopologyValidatorOnline = (Button)findViewById(R.id.buttonTopologyValidator);
        btnTopologyValidatorOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BulidTopologyValidator();
            }
        });
        //矢量裁剪
        Button btnVectorClipAnalystOnline = (Button) findViewById(R.id.buttonVectorClip);
        btnVectorClipAnalystOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildVectorClip();
            }
        });
    }

    //点聚合分析
    public void BuildAggregatePoints(){

        ClearAnalyst();
        m_txtTitle.setText("点聚合分析");

        //添加分布式分析监听器
        m_AggregatePointsOnline.addListener(MainActivity.this);
       //登陆iServer
        m_AggregatePointsOnline.login(m_ip, m_port, m_name, m_password);

        //设置数据路径（必填项）
        m_AggregatePointsOnline.setDatasetSource("iMobile_chart_科教文化服务");
       // m_AggregatePointsOnline.setDataPath("iMobile_chart_科教文化服务");
        //设置网格面类型 0:四边形网格 1:六边形网格 （必填项）
        m_AggregatePointsOnline.setMeshType(0);
        //设置聚合类型 SUMMARYMESH：网格面聚合、SUMMARYREGION：多边形聚合 （必填项）
        m_AggregatePointsOnline.setAggregateType("SUMMARYMESH");
        //设置网格大小（必填项）
        m_AggregatePointsOnline.setResolution(100);
        //设置网格单位 可用值包括：Meter(默认),Kilometer,Yard,Foot,Mile
        m_AggregatePointsOnline.setMeshSizeUnit("Meter");
        //执行分析
        m_AggregatePointsOnline.execute();

        m_btnLoad.setText("加载中...");
    }

    //创建缓冲区分析
    public void buildBuffers(){

        ClearAnalyst();
        m_txtTitle.setText("创建缓冲区分析");

        //添加分布式分析监听器
        m_BufferAnalystOnline.addListener(MainActivity.this);
        //登陆iServer
        m_BufferAnalystOnline.login(m_ip, m_port, m_name, m_password);

        //设置输入源数据集（必填项）
        m_BufferAnalystOnline.setDatasetSource("iMobile_chart_road");
        //设置缓冲距离
        m_BufferAnalystOnline.setDistance(100);
        //执行分析
        m_BufferAnalystOnline.execute();

        m_txtTitle.setText("创建缓冲区分析");
        m_btnLoad.setText("加载中...");

    }

    //密度分析
    public void BuildDensityAnalystOnline(){

        ClearAnalyst();
        m_txtTitle.setText("密度分析");

        //添加分布式分析监听器
        m_DensityAnalystOnline.addListener(MainActivity.this);
        //登陆iServer
        m_DensityAnalystOnline.login(m_ip, m_port,m_name, m_password);
        //设置数据路径（必填项）
        m_DensityAnalystOnline.setDatasetSource("iMobile_chart_高等院校");
        //m_DensityAnalystOnline.setDataPath("iMobile_chart_高等院校");
        //设置网格面类型 0:四边形网格 1:六边形网格 （必填项）
        m_DensityAnalystOnline.setMeshType(0);
        //设置密度分析的分析方法  0：简单点密度分析 1：核密度分析 （必填项）
        m_DensityAnalystOnline.setAnalystMethod(0);
        //设置网格大小（必填项）
        m_DensityAnalystOnline.setResolution(100);
        //设置搜索半径（必填项）
        m_DensityAnalystOnline.setRadius(500);
        //执行分析
        m_DensityAnalystOnline.execute();

        m_btnLoad.setText("加载中...");
    }

    //叠加分析
    public void BuildOverlayAnalystOnline(){

        ClearAnalyst();
        m_txtTitle.setText("叠加分析");

        //添加分布式分析监听器
        m_OverlayAnalystOnline.addListener(MainActivity.this);
        //登陆iServer
        m_OverlayAnalystOnline.login(m_ip,m_port,m_name,m_password);
        //设置输入源数据集（必填项）
        m_OverlayAnalystOnline.setDatasetSource("iMobile_chart_名胜景点");
        //设置叠加对象数据集（必填项）
        m_OverlayAnalystOnline.setDatasetOverlay("iMobile_chart_beijing");
        //设置叠加分析模式（必填项）
        m_OverlayAnalystOnline.setAnalystMode("clip");
        //执行分析
        m_OverlayAnalystOnline.execute();

        m_btnLoad.setText("加载中...");
    }

    //单对象空间查询
    public void BuildQuery(){

        ClearAnalyst();
        m_txtTitle.setText("单对象空间查询");

        //添加分布式分析监听器
        m_QueryOnline.addListener(MainActivity.this);
        //登陆iServer
        m_QueryOnline.login(m_ip, m_port, m_name,m_password);
        //设置查询对象数据集（必填项）
        m_QueryOnline.setDataset("iMobile_chart_beijing");
        //设置源数据集（必填项）
        m_QueryOnline.setDatasetSource("iMobile_chart_科教文化服务");
        //设置查询模式（必填项）
        m_QueryOnline.setQueryMode("CONTAIN");
        //设置查询对象类型（必填项）
        m_QueryOnline.setQueryType(QueryOnline.QueryType.DATASET_QUERY);
        //执行分析
        m_QueryOnline.execute();

        m_btnLoad.setText("加载中...");
    }

    //区域汇总分析
    public void BulidSummaryRegion(){

        ClearAnalyst();
        m_txtTitle.setText("区域汇总分析");

        //添加分布式分析监听器
        m_SummaryRegionOnline.addListener(MainActivity.this);
        //登陆iServer
        m_SummaryRegionOnline.login(m_ip, m_port, m_name, m_password);
        //设置数据路径（必填项）
        m_SummaryRegionOnline.setDatasetSource("iMobile_chart_road");
        //m_SummaryRegionOnline.setDataPath("iMobile_chart_road");
        //设置网络面汇总类型 0：四边形网格   1：六边形网格
        m_SummaryRegionOnline.setMeshType(0);
        // 设置汇总类型 SUMMARYMESH:网格面汇总 SUMMARYREGION:多边形汇总
        m_SummaryRegionOnline.setSummaryType("SUMMARYMESH");
        // 设置标准属性字段统计
        m_SummaryRegionOnline.setStandardFields("KIND", "max");
        //设置网格大小
        m_SummaryRegionOnline.setResolution(200);
        //设置权重字段统计
        m_SummaryRegionOnline.setWeightedFields("", "");
        //执行分析
        m_SummaryRegionOnline.execute();

        m_btnLoad.setText("加载中...");
    }

    //创建拓扑检查
    public void BulidTopologyValidator(){

        ClearAnalyst();
        m_txtTitle.setText("创建拓扑检查");

        //添加分布式分析监听器
        m_TopologyValidatorOnline.addListener(MainActivity.this);
        //登陆iServer
        m_TopologyValidatorOnline.login(m_ip, m_port, m_name, m_password);
        //设置输入源数据集（必填参数）。
        m_TopologyValidatorOnline.setDatasetSource("iMobile_chart_road");
        //设置拓扑检查数据集
        m_TopologyValidatorOnline.setDatasetTopology("iMobile_chart_road");
        //设置拓扑检查规则
        m_TopologyValidatorOnline.setRule(TopologyValidatorOnline.RuleType.LINENOOVERLAPWITH);
        //执行分析
        m_TopologyValidatorOnline.execute();

        m_btnLoad.setText("加载中...");

    }

    //矢量裁剪分析
    public void BuildVectorClip(){

        ClearAnalyst();
        m_txtTitle.setText("矢量裁剪分析");

        //添加分布式分析监听器
        m_VectorClipAnalystOnline.addListener(MainActivity.this);
        //登陆iServer
        m_VectorClipAnalystOnline.login(m_ip, m_port, m_name, m_password);
        //设置裁剪对象数据集（必填项）
        m_VectorClipAnalystOnline.setDataset("iMobile_chart_beijing");
        //设置源数据集（必填项）
        m_VectorClipAnalystOnline.setDatasetSource("iMobile_chart_名胜景点");
        //设置裁剪分析模式（必填项） clip:内部裁剪 intersect:外部裁剪
        m_VectorClipAnalystOnline.setAnalystMode("clip");
        //设置裁剪对象的类型（必填项）
        m_VectorClipAnalystOnline.setClipType(VectorClipAnalystOnline.ClipType.DATASET_VECTOR_CLIP);
        //执行分析
        m_VectorClipAnalystOnline.execute();

        m_btnLoad.setText("加载中...");
    }

    //清除分析结果
    public void ClearAnalyst(){

        if ( m_datasource != null)//关闭已加载的数据源
        {
            m_mapControl.getMap().getLayers().remove(0);
            m_wokspace.getDatasources().close(m_datasource.getAlias());
            m_mapControl.getMap().refresh();
            m_datasource = null;
        }

        m_btnLoad.setText("");
        m_txtTitle.setText("");

    }

    @Override
    public void onPostExecute(final boolean bResult, final ArrayList<String> datasources) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                m_btnLoad.setText("");

                if (bResult && datasources != null && datasources.size() > 0) {
                    String url = datasources.get(0);
                    DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
                    datasourceConnectionInfo.setEngineType(EngineType.Rest);
                    datasourceConnectionInfo.setServer(url);
                    m_datasource = m_wokspace.getDatasources().open(datasourceConnectionInfo);
                    if (m_datasource != null) {
                        m_mapControl.getMap().getLayers().add(m_datasource.getDatasets().get(0), true);
                        m_mapControl.getMap().refresh();
                    }
                }
            }
        });
    }

    @Override
    public void onExecuteFailed(String errorInfo){
        m_btnLoad.setText("加载失败..." );
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
}
