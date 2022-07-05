package com.supermap.gridmap;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.mapGrid.MapGrid;
import com.supermap.data.mapGrid.MapGridParam;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:地图格网
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------------
 * -------------------SuperMap iMobile for Android-----------------------
 * 示范程序说明-----------------------------------------------------------
 *
 * 1、范例简介：示范如何给地图加上格网
 * 2、示例数据：SampleData/GeometryInfo/World.smwu
 * 3、关键类型/成员:
 *  MapGrid.createMapGrid();
 *  MapGridParam.setShowGrid();
 *  MapGridParam.setColumnLineCount();
 *  MapGridParam.setRowLineCount();
 *  MapGridParam.setCellWidth();
 *  MapGridParam.setCellHeight();
 *  MapGridParam.setShowMajorDivision();
 *  MapGridParam.setBounds();
 *
 * 4、使用步骤： (1)将SampleData/GeometryInfo/中的数据拷贝到Android设备内置存储下
 *              (2)运行程序。
 *              (3)点击格网按钮。
 * --------------------------------------
 * --------------------------------------
 * ============================================================================>
 * </p>
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */


public class MainActivity extends Activity {
    private MapControl m_mapControl;
    String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private MapView m_mapView = null;
    private Workspace m_workSpace;
    private Dataset clipDataset;
    private Datasource targetDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //申请权限
        requestPermissions();
        //设置一些系统需要用到的路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
        Environment.setTemporaryPath(sdcard + "/SuperMap/temp/");
        Environment.setWebCacheDirectory("/sdcard/SuperMap/WebCatch");
        Environment.initialization(this);
        Environment.setOpenGLMode(true);
        Environment.setFontsPath("/sdcard/SuperMap/fonts/");
        setContentView(R.layout.activity_main);
        openMap();
    }

    public void openMap()
    {
        m_workSpace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard+"/SampleData/GeometryInfo/World.smwu");
        info.setType(WorkspaceType.SMWU);
        boolean result = m_workSpace.open(info);
        if (!result) {
            Toast.makeText(this, "工作空间打开失败！", Toast.LENGTH_LONG).show();
            m_workSpace.close();
            m_workSpace = null;
            return;
        }
        //将地图显示控件和工作空间关联
        m_mapView = (MapView)findViewById(R.id.Map_view);
        m_mapControl =  m_mapView.getMapControl();
        m_mapControl.getMap().setWorkspace(m_workSpace);
        //打开工作空间中的地图。参数0表示第一张地图
        String mapname = m_workSpace.getMaps().get(0);
        if (!m_mapControl.getMap().open(mapname)) {
            Toast.makeText(this, "地图打开失败！", Toast.LENGTH_LONG).show();
            m_mapControl.dispose();
            m_workSpace.close();
            m_mapControl = null;
            m_mapView = null;
            m_workSpace = null;
            return;
        }
        m_mapControl.getMap().setScale(0.000000005);
        m_mapControl.getMap().setCenter(new Point2D(0,0));
        clipDataset = m_workSpace.getDatasources().get(0).getDatasets().get(0);
        targetDatasource = m_workSpace.getDatasources().get(0);
    }
    public DatasetVector buildGridMap()
    {
        DatasetVector datasetVector=null;
        MapGridParam mapGridParam = new MapGridParam();
        mapGridParam.setShowGrid(true);
        mapGridParam.setColumnLineCount(6);
        mapGridParam.setRowLineCount(4);
        mapGridParam.setCellWidth(60.0);
        mapGridParam.setCellHeight(45.0);
        mapGridParam.setShowMajorDivision(false);
        if (clipDataset != null) {
            mapGridParam.setBounds(clipDataset.getBounds());
        } else {
            mapGridParam.setBounds(new Rectangle2D(-180,-90,180,90));
        }
        MapGrid mapGrid = new MapGrid(mapGridParam);
        datasetVector = mapGrid.createMapGrid(DatasetType.CAD, targetDatasource, "MapGrid_2");
        return datasetVector;
    }

    public void clickBtn(View view)
    {
        DatasetVector datasetVector =  buildGridMap();
        if (datasetVector!=null)
        {
            m_mapControl.getMap().getLayers().add(datasetVector,true);
        }
    }
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
    /**
     * 检测权限
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */
    public boolean checkPermissions(String[] permissions) {
        return EasyPermissions.hasPermissions(this, permissions);
    }
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
}
