package com.supermap.nonrecyclableopenmap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;


public class DynamicMapActivity extends Activity {


    MapView mapView;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DynamicMapActivity===", "onCreate");
        // 动态代码加控件
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(params);
        setContentView(layout);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(-1, -2);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params1);

        layout.addView(linearLayout);

        mapView = DynamicMainActivity.getMapView(DynamicMapActivity.this);

        linearLayout.addView(mapView);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                setMap();
//            }
//        }).start();

        setMap();

    }

    private void setMap() {

        mapView.getMapControl().getMap().setVisibleScalesEnabled(true);
//        mapView.getMapControl().getMap().setCenter(new Point2D(104.0 + Math.random(), 30.0 + Math.random()));
        mapView.getMapControl().getMap().setCenter(new Point2D(104.5 , 30.5));
        mapView.getMapControl().getMap().setScale(1.0 / 20000.0);
    }



    @Override
    protected void onPause() {
        super.onPause();
//        linearLayout.removeView(mapView);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        linearLayout.removeView(mapView);

        // todo 就是释放慢导致的,用异步异常交互体验好，无卡顿。但是多次跳转依然内存溢出。
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mapView.getMapControl().getMap().close();
//                mapView.getMapControl().getMap().dispose();
//                linearLayout.removeView(mapView);
////                mapView = null;
////                mapView.getMapControl().dispose();
//                Log.e("DMA===onDestroy", "DS count=" + mWorkspace.getDatasources().getCount());
////                mWorkspace.close();
//            }
//        }).start();
    }

}



