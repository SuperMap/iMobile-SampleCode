package com.supermap.intelligencetour;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

/**
 * Created by Carson_Ho on 17/8/11.
 */

public class SearchDemo extends AppCompatActivity implements View.OnClickListener {

    TextView name, address, telephone;
    MapView m_mapView;
    MapControl m_mapControl;
    Workspace m_workspace;
    Point2Ds point2Ds;
    double x, y, locationy, locationx;
    String queryStr;
    private SlidrConfig mSlidrConfig;
    private SlidrConfig.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        //指划返回
        mBuilder = new SlidrConfig.Builder().scrimColor(Color.BLACK)//滑动时Activity之间的颜色
                .position(SlidrPosition.LEFT)//从左边滑动
                .sensitivity(1f)//敏感度
                .scrimStartAlpha(0.8f)//滑动开始时两个Activity之间的透明度
                .scrimEndAlpha(0f)//滑动结束时两个Activity之间的透明度
                .velocityThreshold(10f)//超过这个滑动速度，忽略位移限定值就切换Activity
                .distanceThreshold(.9f);//滑动位移占屏幕的百分比，超过这个间距就切换Activity
        mSlidrConfig = mBuilder.build();

        SlidrInterface slidrInterface = Slidr.attach(this,mSlidrConfig);
        slidrInterface.lock();

        setContentView(R.layout.activity_search);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        telephone = findViewById(R.id.telephone);

        String m_name = getIntent().getStringExtra("name");
        String m_address = getIntent().getStringExtra("address");
        String m_telephone = getIntent().getStringExtra("telephone");
        x = getIntent().getDoubleExtra("pointx", 0.00);
        y = getIntent().getDoubleExtra("pointy", 0.00);
        locationx = getIntent().getDoubleExtra("locationx", 0.00);
        locationy = getIntent().getDoubleExtra("locationy", 0.00);
        queryStr = getIntent().getStringExtra("queryStr");

        if (m_telephone.indexOf("null") != -1) {
            m_telephone = "暂无";
        }
        name.setText(m_name);
        address.setText(m_address);
        telephone.setText("联系方式:" + m_telephone);


        m_mapView = findViewById(R.id.mapview);

        m_workspace = new Workspace();

        m_mapControl = m_mapView.getMapControl();

        m_mapControl.getMap().setWorkspace(m_workspace);

        DatasourceConnectionInfo info = new DatasourceConnectionInfo();

        info.setAlias("GoogleMaps");
        info.setEngineType(EngineType.GoogleMaps);
        info.setReadOnly(false);
        info.setServer("http://www.google.cn/maps");


        //打开数据源
        Datasource datasource = m_workspace.getDatasources().open(info);

        if(datasource!=null){
            Dataset dataset = datasource.getDatasets().get(0);

            if(dataset!=null){
                //添加数据集到地图窗口
                m_mapControl.getMap().getLayers().add(dataset, true);

                point2Ds = new Point2Ds();
                point2Ds.add(new Point2D(x, y));
                point2Ds.add(new Point2D(locationx, locationy));

                PrjCoordSys sourcePrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);

                CoordSysTransParameter coordSysTransParameter = new CoordSysTransParameter();

                CoordSysTranslator.convert(point2Ds, sourcePrjCoordSys, m_mapControl.getMap().getPrjCoordSys(), coordSysTransParameter, CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

                m_mapControl.getMap().setScale(0.00005);
                m_mapControl.getMap().setCenter(point2Ds.getItem(1));


                LayoutInflater lfCallOut = getLayoutInflater();
                View calloutLayout = lfCallOut.inflate(R.layout.callout2, null);
                CallOut callout = new CallOut(this);
                // 设置显示内容
                callout.setContentView(calloutLayout);
                // 设置自定义背景图片
                callout.setCustomize(true);
                // 设置显示位置
                callout.setLocation(point2Ds.getItem(0).getX(), point2Ds.getItem(0).getY());
                m_mapView.addCallout(callout);


                LayoutInflater lfCallOut1 = getLayoutInflater();
                View calloutLayout1 = lfCallOut1.inflate(R.layout.callout1, null);
                CallOut callout1 = new CallOut(this);
                callout1.setContentView(calloutLayout1);
                callout1.setCustomize(true);
                callout1.setLocation(point2Ds.getItem(1).getX(), point2Ds.getItem(1).getY());
                m_mapView.addCallout(callout1);
            }
        }

        findViewById(R.id.location_more).setOnClickListener(this);
        findViewById(R.id.goback).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location_more:
                Intent intent = new Intent(this, LocationMore.class);
                intent.putExtra("pointx", x);
                intent.putExtra("pointy", y);
                intent.putExtra("searchpointx", point2Ds.getItem(0).getX());
                intent.putExtra("searchpointy", point2Ds.getItem(0).getY());
                intent.putExtra("locationx", point2Ds.getItem(1).getX());
                intent.putExtra("locationy", point2Ds.getItem(1).getY());
                intent.putExtra("locationTencentx", locationx);
                intent.putExtra("locationTencenty", locationy);
                intent.putExtra("queryStr",queryStr);
                startActivity(intent);
                break;
            case R.id.goback:
                finish();
                break;
        }
    }
}