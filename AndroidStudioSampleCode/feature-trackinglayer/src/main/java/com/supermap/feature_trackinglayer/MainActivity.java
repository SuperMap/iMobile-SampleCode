package com.supermap.feature_trackinglayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.supermap.data.Color;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
/**
 * <p>
 * Title:要素展示
 * </p>
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
 *   	展示要素。
 *
 * 2、Demo数据：
 *      许可目录："../SuperMap/License/"
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

    private MapView mMapView;
    private MapControl mMapControl;
    private Workspace mWorkspace;
    String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.supermap.data.Environment.setWebCacheDirectory(sdcard + "/supermap/webcache/");
        setContentView(R.layout.activity_main);
        openGoogleMap();
    }

    private void openGoogleMap() {

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapControl = mMapView.getMapControl();
        mWorkspace = new Workspace();
        mMapControl.getMap().setWorkspace(mWorkspace);

        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setAlias("GOOGLE");
        info.setEngineType(EngineType.GoogleMaps);
        String url3 = "http://www.google.cn/maps";
       info.setServer(url3);
        Datasource datasourcegoogle = mWorkspace.getDatasources().open(info);


        mMapControl.getMap().getLayers().add(datasourcegoogle.getDatasets().get(0),false);
        mMapControl.getMap().setCenter(new Point2D(12969335.4856042,4863834.11645054));
        mMapControl.getMap().setScale(1/153518.188615112);
        mMapControl.enableRotateTouch(true);
        mMapControl.getMap().refresh();
    }

    public void showPointByCallout(Point2D point, final String pointName,
                                   final int idDrawable) {
        CallOut callOut = new CallOut(this);
        callOut.setStyle(CalloutAlignment.BOTTOM);
        callOut.setCustomize(true);
        callOut.setLocation(point.getX(), point.getY());
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(idDrawable);
        imageView.setMaxWidth(40);
        imageView.setMaxHeight(40);
        callOut.setContentView(imageView);
        mMapView.addCallout(callOut, pointName);
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
        if (id == R.id.show_tracking) {
            //添加图片
            for (int i = 0; i < 10; i++) {
                Point2D point2D = getPoint();
                showPointByCallout(point2D,"point " + i,R.drawable.icon_tree);

            }
            for (int i = 0; i < 10; i++) {
                Point2D point2D = getPoint();
                showPointByCallout(point2D,"point 1" + i,R.drawable.icon_warning);
            }
            return true;
        }else if (id == R.id.remove_tracking) {
            //添加图片
            mMapView.removeAllCallOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 获取随机点
     * @return
     */
    private Point2D getPoint() {
        double x = Math.random() * 10000 + 12961243.4374382;
        double y = Math.random() * 10000 + 4858528.61037415;

        Point2D pt = new Point2D(x, y);
        return pt;
    }
}
