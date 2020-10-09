package com.supermap.feature_show_info;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
/**
 * <p>
 * Title:要素展示
 * 展示要素属性特征
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
 *   	展示要素属性特征。
 *
 * 2、Demo数据：
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    mMapControl.onMultiTouch()		方法
 *    mMapView.addCallout();	        方法
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

    private String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath();
    private MapView mMapView;
    private Workspace mWorkspace;
    private MapControl mMapControl;
    private Map mMap;
    private View popView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.supermap.data.Environment.setWebCacheDirectory(SDCARD + "/supermap/webcache/");
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

    private long mLastTime;
    private long mDownTime;

    public void showPointByCallout(Point2D point, final String pointName,
                                   final int idDrawable) {
        CallOut callOut = new CallOut(this);
        callOut.setStyle(CalloutAlignment.BOTTOM);
        callOut.setCustomize(true);
        callOut.setLocation(point.getX(), point.getY());
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(idDrawable);

        callOut.setContentView(imageView);
        mMapView.addCallout(callOut, pointName);

        imageView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent ev) {
                switch (ev.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        mMapControl.onMultiTouch(ev);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mDownTime = System.currentTimeMillis();
                        mMapControl.onMultiTouch(ev);
                    case MotionEvent.ACTION_UP:
                        mLastTime = System.currentTimeMillis();
                        Log.e("wangli ","wnagli " + (mLastTime - mDownTime));
                        if(mLastTime - mDownTime >100)
                        {
                            mMapControl.onMultiTouch(ev);
                        }else{
                            showCallout("警示",point);
                        }

                        break;

                    default:
                        mMapControl.onMultiTouch(ev);
                        break;

                }



                return true;
            }
        });

    }

    private void showCallout(String title, Point2D point2D) {

        if (popView != null) {
            popView.setVisibility(View.GONE);
        }
        popView = LayoutInflater.from(this).inflate(R.layout.layout, null);
        TextView textView = (TextView) popView.findViewById(R.id.map_bubbleTitle);
        textView.setText(title);

        CallOut callOut = new CallOut(getApplicationContext());
        callOut.setStyle(CalloutAlignment.BOTTOM);
        callOut.setCustomize(true);
        callOut.setLocation(point2D.getX(), point2D.getY()+5.0);

        callOut.setContentView(popView);
        mMapView.addCallout(callOut);
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
//            for (int i = 0; i < 10; i++) {
//                Point2D point2D = getPoint();
//                showPointByCallout(point2D,"point " + i,R.drawable.icon_tree);
//
//            }
            for (int i = 0; i < 40; i++) {
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

    public boolean onTouch(View v, MotionEvent event) {
        mMapControl.onMultiTouch(event);
        return true;
    }
}
