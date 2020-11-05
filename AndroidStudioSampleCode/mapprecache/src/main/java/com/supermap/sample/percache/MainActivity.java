/**
 * <p>
 * Title:OSM在线图预缓存
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：OSM在线图预缓存
 * 2、示范数据： OSM在线图
 * 3、关键类型/成员:
 *      MapCacheListener() 事件监听
 *      MapCacheService.startDownload() 类
 *      MapCacheService.stopDownload() 类
 *      MapCacheService.removeDownload() 类
 *
 *
 * 4、使用步骤：
 *   (1)点击start按钮，开始地图预缓存。
 *   (2)点击stop按钮，停止缓存。
 *   (3)点击remove按钮，删除缓存文件。
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

package com.supermap.sample.percache;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.supermap.data.DatasetImage;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.MapCacheListener;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.sample.spinner.ItemData;
import com.supermap.sample.spinner.SpinnerAdapter;

import java.time.Duration;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private MapView mMapView;
  private Workspace mWorkspace;
  private MapControl mMapControl;
  private com.supermap.mapping.Map mMap;
  private Spinner mSpinner;

  public static String SDCARD   = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
  private Datasource mDatasource;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    com.supermap.data.Environment.setWebCacheDirectory(SDCARD + "/supermap/precache");
    Environment.initialization(this);
    setContentView(R.layout.activity_main);
    openMap();

    mSpinner = (Spinner) findViewById(R.id.spinner);
    ArrayList<ItemData> list = new ArrayList<>();
    list.add(new ItemData("Start", R.drawable.locationdisplayon));
    list.add(new ItemData("Stop", R.drawable.locationdisplaydisabled));
    list.add(new ItemData("Remove", R.drawable.locationdisplayrecenter));

    SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
    mSpinner.setAdapter(adapter);
    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
          case 0:
            startPreCache(mDatasource);
            Toast.makeText(MainActivity.this,
                    "cache is in "+ SDCARD + "/supermap/precache",Toast.LENGTH_LONG).show();
            break;
          case 1:
            stopPreCache(mDatasource);
            break;
          case 2:
            removeCache(mDatasource);
            break;
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) { }

    });
  }
  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
  }

  //open
  private boolean openMap(){
    mWorkspace = new Workspace();
    mMapView= (MapView)findViewById(R.id.mapView);
    mMapControl = mMapView.getMapControl();
    mMap = mMapControl.getMap();
    mMap.setWorkspace(mWorkspace);

    openOSM();

    mMap.refresh();
    return true;
  }

  /**
   * open OpenStreetMap
   */
  private void openOSM(){
    //OpenStreetMap
    String url = "https://openstreetmap.org";
    DatasourceConnectionInfo info = new DatasourceConnectionInfo();
    info.setAlias("OpenStreetMap2");
    info.setEngineType(EngineType.OpenStreetMaps);
    info.setServer(url);

    mDatasource = mWorkspace.getDatasources().open(info);
    if(mDatasource != null){
      mMap.getLayers().add(mDatasource.getDatasets().get(0), true);
    }

    mMap.setScale(4.3268196810674E-7);
    mMap.setCenter(new Point2D(12966754.806376,4858084.88475535));

  }

  public void startPreCache(Datasource dataSource) {
    DatasetImage datasetImage = (DatasetImage)dataSource.getDatasets().get(0);
    if(datasetImage != null) {
      DatasetImage.MapCacheService service = datasetImage.getMapCacheService();
      service.setListener(new MapCacheListener() {

        @Override
        public void onProgress(int nStep) {
          System.out.println("map precache progress is :" + nStep);
        }

        @Override
        public void onComplete(int nFailedCount) {
          System.out.println("map precache is completed, failedCount:" + nFailedCount);
        }

        @Override
        public void onChecked() {
          System.out.println("please check your net status!");
        }

        @Override
        public void onCacheStatus(int downLoadCount, long totalCount) {
          System.out.println("precache has download " + downLoadCount + " tiles, the total count is :" + totalCount + "!");
        }
      });

      Rectangle2D rcBounds = new Rectangle2D(11562170.646514472, 3576776.24557856, 11601306.404996481, 3606128.0644400679);
      double scale = 0.00011076655982855200;

      rcBounds.setRight(rcBounds.getLeft() + rcBounds.getWidth() / 2.0);
      boolean bResult = service.startDownload(1.0/5000, 1.0/10000, rcBounds);
      System.out.println("startdownload: " + bResult);
    }
  }

  public void stopPreCache(Datasource dataSource) {
    DatasetImage datasetImage = (DatasetImage)dataSource.getDatasets().get(0);
    if(datasetImage != null) {
      DatasetImage.MapCacheService service = datasetImage.getMapCacheService();
      service.stopDownload();
    }
  }

  public void removeCache(Datasource dataSource) {
    DatasetImage datasetImage = (DatasetImage)dataSource.getDatasets().get(0);
    if(datasetImage != null) {
      DatasetImage.MapCacheService service = datasetImage.getMapCacheService();

      service.removeCache();
    }
  }


}
