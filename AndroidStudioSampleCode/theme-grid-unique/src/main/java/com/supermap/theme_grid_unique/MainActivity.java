package com.supermap.theme_grid_unique;

import android.hardware.HardwareBuffer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.supermap.data.Color;
import com.supermap.data.ColorGradientType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.ThemeGridRange;
import com.supermap.mapping.ThemeGridRangeItem;
import com.supermap.mapping.ThemeGridUnique;

import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {
    String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    private MapControl mMapControl;
    private MapView mMapView;
    private Workspace mWorkspace;
    private Dataset mDataset;
    private Map mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openMap();
    }
    /**
     * 打开数据
     */
    private void openMap() {

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapControl = mMapView.getMapControl();
        mWorkspace = new Workspace();
        mMapControl.getMap().setWorkspace(mWorkspace);
        mMap = mMapControl.getMap();

        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(SDCARD + "/sampledata/ThematicMaps/Thematicmaps.smwu");
        info.setType(WorkspaceType.SMWU);
        if (mWorkspace.open(info)) {
            mMapControl.getMap().setWorkspace(mWorkspace);
//            mMapControl.getMap().getLayers().add(mWorkspace.getDatasources().get(0).getDatasets().get("dxfimport_3"), true);
            String mapname = mWorkspace.getMaps().get(4);
            mMapControl.getMap().open(mapname);
            mMapControl.getMap().viewEntire();
            mDataset = mWorkspace.getDatasources().get(0).getDatasets().get("geomorGrid");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_themegrid) {
            DatasetGrid datasetGrid= (DatasetGrid)mDataset;
            ThemeGridRange themeGridRange=new ThemeGridRange();

            ThemeGridRangeItem themeGridRangeItem1=new ThemeGridRangeItem();
            themeGridRangeItem1.setVisible(true);
            themeGridRangeItem1.setStart(0);
            themeGridRangeItem1.setEnd(3047009.4);
            themeGridRangeItem1.setColor(new Color(155,228,248));

            ThemeGridRangeItem themeGridRangeItem2=new ThemeGridRangeItem();
            themeGridRangeItem2.setVisible(true);
            themeGridRangeItem2.setStart(3047009.4);
            themeGridRangeItem2.setEnd(6098166.2);
            themeGridRangeItem2.setColor(new Color(198,239,212));

            ThemeGridRangeItem themeGridRangeItem3=new ThemeGridRangeItem();
            themeGridRangeItem3.setVisible(true);
            themeGridRangeItem3.setStart(6098166.2);
            themeGridRangeItem3.setEnd(7623744.6);
            themeGridRangeItem3.setColor(new Color(252,233,145));

            ThemeGridRangeItem themeGridRangeItem4=new ThemeGridRangeItem();
            themeGridRangeItem4.setVisible(true);
            themeGridRangeItem4.setStart(7623744.6);
            themeGridRangeItem4.setEnd(9149323);
            themeGridRangeItem4.setColor(new Color(251,207,114));


            mMap.getLayers().add(datasetGrid,themeGridRange,true);
            themeGridRange.getRangeMode();
            mMap.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
