package com.supermap.glmapcachedemo.glmapcachedemo;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.glmapcachedemo.R;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
public class MainActivity extends Activity {

	Workspace mWorkspace;
	MapControl mMapControl;
	Map mMap;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		//将地图显示控件和工作空间关联
		mWorkspace = new Workspace();
		mMapControl = ( (MapView)findViewById(R.id.mapView) ).getMapControl();
		mMap = mMapControl.getMap();      //获取在地图控件中显示的地图对象
		mMap.setWorkspace(mWorkspace);    //设置当前地图所关联的工作空间
// 	    mMap.setFullScreenDrawModel(true); //设置全屏绘制模式

		//以数据源的方式打开GL地图瓦片数据
		DatasourceConnectionInfo dsInfo = new DatasourceConnectionInfo();
		dsInfo.setServer(sdcard + "/SuperMap/Demos/GLMapCacheDemo/VectorCache.xml");
		dsInfo.setEngineType(EngineType.OpenGLCache);
		dsInfo.setAlias("VectorCache");
		Datasource ds = mWorkspace.getDatasources().open(dsInfo);
		if (ds == null) {   // GL地图瓦片打开失败
			Toast.makeText(this, "GL数据打开失败", Toast.LENGTH_SHORT).show();
			return;
		}

		mMap.getLayers().add(ds.getDatasets().get(0), true);    // 把GL地图瓦片数据集添加到地图中
		mMap.setScale(2.2222975368585084E-6);						// 设置显示比例尺
		mMap.setCenter(new Point2D(-73.97883664797858, 40.60765967412399));

	}



}
