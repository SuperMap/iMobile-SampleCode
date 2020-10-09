package com.supermap.trackingmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.supermap.data.Color;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private Button m_btnPt;
    private Button m_btnClear;
    private ArrayList<Integer> m_idList;
    private MapView m_mapview;
    private MapControl m_mapControl;
    private Workspace m_workspace;
    private Button m_btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化环境,设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);
        setContentView(R.layout.activity_main);

        openWorkspace();
        initView();
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    /**
     * 初始化
     */
    private void initView() {
//        m_dynamicLayer = new DynamicView(this, m_mapControl.getMap());
//        m_mapview.addDynamicView(m_dynamicLayer);

        m_btnPt = (Button)findViewById(R.id.btn_pt);
        m_btnPt.setOnClickListener((View.OnClickListener) new Btnlistener());

        findViewById(R.id.btn_line).setOnClickListener(new Btnlistener());
        findViewById(R.id.btn_region).setOnClickListener(new Btnlistener());
        m_btnAnim = (Button)findViewById(R.id.btn_syn);
        m_btnAnim.setOnClickListener(new Btnlistener());

        m_btnClear = (Button)findViewById(R.id.btn_clear);
        m_btnClear.setOnClickListener((View.OnClickListener) new Btnlistener());

        m_idList = new ArrayList<Integer>();
    }
    /**
     * 打开工作空间
     */
    private void openWorkspace() {
        m_mapview = (MapView) findViewById(R.id.mapview);
        m_mapControl = m_mapview.getMapControl();

        m_workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
//
//        info.setServer(sdcard+"/SampleData/GeometryInfo/World.smwu");
        info.setServer(sdcard+"/SampleData/city/changchun.smwu");

        info.setType(WorkspaceType.SMWU);
        boolean isOpen = m_workspace.open(info);
        if(!isOpen){
            showInfo("Workspace open failed!");
        }

        m_mapControl.getMap().setWorkspace(m_workspace);
        m_mapControl.getMap().open(m_workspace.getMaps().get(0));
//        m_mapControl.getMap().getTrackingLayer().setAsyncRefresh(false);

    }
    /**
     * 获取随机点
     * @return
     */
    private Point2D getPoint() {
        double x = Math.random() * 360 - 180;
        double y = Math.random() * 180 - 90;

        Point2D pt = new Point2D(x, y);
        return pt;
    }

    private double calculatesAngle(Point2D ptStart, Point2D ptEnd) {
        double Y = ptEnd.getY() - ptStart.getY();
        double X = ptEnd.getX() - ptStart.getX();
        double angle2 = Math.atan2(Y, X);

        return 90 - angle2*180/3.1415;
    }
    private void showInfo(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    class Btnlistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.airplane2);
            switch (v.getId()) {
                case R.id.btn_line:
                    for (int i = 0; i < 1000; i++) {
                        TrackingLayer trackingLayer = m_mapControl.getMap().getTrackingLayer();

                        Point2D point2D = getPoint();
                        Point2Ds point2Ds=new Point2Ds();
                        point2Ds.add(point2D);
                        point2Ds.add(new Point2D(point2D.getX()-2.5,point2D.getY()-4.7));
                        point2Ds.add(new Point2D(point2D.getX()+4.0,point2D.getY()+5.0));
                        GeoLine geoLine=new GeoLine();
                        geoLine.addPart(point2Ds);

                        GeoStyle style = new GeoStyle();
                        style.setLineColor(new Color(153, 102, 153));
                        style.setLineWidth(1.5);

                        geoLine.setStyle(style);
                        trackingLayer.add(geoLine,"line " +i );//i
                        Log.e("wangli ","wangli "+ i);
                    }

                    break;
                case R.id.btn_pt:
                    for (int i = 0; i < 1000; i++) {
                        TrackingLayer trackingLayer = m_mapControl.getMap().getTrackingLayer();

                        Point2D point2D = getPoint();
                        GeoPoint geoPoint = new GeoPoint(point2D);
//
                        GeoStyle style = new GeoStyle();
                        style.setMarkerSize(new Size2D(10, 10));
                        style.setMarkerSymbolID(3);
                        style.setLineColor(new Color(0, 255, 0));


                        geoPoint.setStyle(style);
                        trackingLayer.add(geoPoint,"point " +i );//i
                        Log.e("wangli ","wangli "+ i);
                    }
                    break;
                case R.id.btn_region:
                    for (int i = 0; i < 1000; i++) {
                        TrackingLayer trackingLayer = m_mapControl.getMap().getTrackingLayer();

                        Point2D point2D = getPoint();
                        Point2Ds point2Ds=new Point2Ds();
                        point2Ds.add(point2D);
                        point2Ds.add(new Point2D(point2D.getX()-2.5,point2D.getY()-4.7));
                        point2Ds.add(new Point2D(point2D.getX()+4.0,point2D.getY()+3.0));
                        GeoRegion geoRegion=new GeoRegion();
                        geoRegion.addPart(point2Ds);

                        GeoStyle style = new GeoStyle();

                        style.setLineColor(new Color(255, 204, 255));
                        style.setLineWidth(1);

                        geoRegion.setStyle(style);
                        trackingLayer.add(geoRegion,"region " +i );//i
                        Log.e("wangli ","wangli "+ i);
                    }

                    break;
                case R.id.btn_syn:
                    if(m_mapControl.getMap().getTrackingLayer().isAsyncRefresh()){
                        m_mapControl.getMap().getTrackingLayer().setAsyncRefresh(false);
                        m_btnAnim.setText("分层渲染");
                        m_mapControl.getMap().refresh();
                    }else {
                        m_mapControl.getMap().getTrackingLayer().setAsyncRefresh(true);
                        m_btnAnim.setText("实时渲染");
                        m_mapControl.getMap().refresh();
                    }

                    break;
                case R.id.btn_clear:
                    m_mapControl.getMap().getTrackingLayer().clear();
//                    m_idList.clear();
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
