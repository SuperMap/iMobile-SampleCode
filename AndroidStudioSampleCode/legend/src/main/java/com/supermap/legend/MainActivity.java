package com.supermap.legend;

import android.Manifest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Legend;
import com.supermap.mapping.LegendItem;
import com.supermap.mapping.LegendView;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.ThemeRange;
import com.supermap.mapping.ThemeType;

import java.util.ArrayList;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:imobile图例功能展示
 *
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
 * 1、范例简介：示范imobile图例功能展示
 * 2、示例数据：数据目录："/sdcard/SampleData/Hunan/"
 *            地图数据：Hunan.smwu,Hunan.udb,Hunan.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 LegendView.setRowWidth() 			//方法
 *   LegendView.setRowHeight()			//方法
 *   LegendView.setTextSize()			//方法
 *   LegendView.setNumColumns()			//方法
 *   Map.createLegend()				    //方法
 *	 LegendItem.setColor()				//方法
 *   LegendItem.setCaption()			//方法
 *	 Legend.addUserDefinedLegendItem()	//方法
 *
 *
 * 4、使用步骤：
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
public class MainActivity extends AppCompatActivity {
    private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    MapView mapView;
    Workspace m_workSpace;
    MapControl m_mapControl;
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
        //设置一些系统需要用到的路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);

        setContentView(R.layout.activity_main);

        m_workSpace = new Workspace();

        mapView = (MapView) findViewById(R.id.mapview);

        m_mapControl = mapView.getMapControl();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard + "/SampleData/hunan/hunan.smwu");
        info.setType(WorkspaceType.SMWU);
        m_workSpace.open(info);
        m_mapControl.getMap().setWorkspace(m_workSpace);
        String mapName = m_workSpace.getMaps().get(0);
        m_mapControl.getMap().open(mapName);
        m_mapControl.getMap().refresh();

        final LegendView legView = findViewById(R.id.LegendView);

        legView.setRowWidth(60);
        legView.setRowHeight(60);
        legView.setTextSize(10);
        legView.setNumColumns(2);
        Legend legend = m_mapControl.getMap().createLegend();
        Layers layers = m_mapControl.getMap().getLayers();
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        for (int i = 0; i < layers.getCount(); i++) {
            Layer layer = layers.get(i);
            if (layer.getTheme() != null) {
                if (layer.getTheme().getType() == ThemeType.RANGE) {
                    ThemeRange themeRange = (ThemeRange) layer.getTheme();
                    for (int j = 0; j < themeRange.getCount(); j++) {
                        GeoStyle GeoStyle = themeRange.getItem(j).getStyle();
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Caption", themeRange.getItem(j).getCaption());
                        map.put("Color", GeoStyle.getFillForeColor().toColorString());
                        arrayList.add(map);
                    }
                }
            }
        }

        if (legend != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                HashMap<String, String> hashMap = arrayList.get(i);
                String caption = hashMap.get("Caption");
                String colorString = hashMap.get("Color");
                int color = android.graphics.Color.parseColor(colorString);
                LegendItem legendItem = new LegendItem();
                legendItem.setColor(color);
                legendItem.setCaption(caption);
                legend.addUserDefinedLegendItem(legendItem);
            }
            legend.connectLegendView(legView);


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
}
