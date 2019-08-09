package com.supermap.ar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.AR.ArControl2;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StaticViewGeoObjectActivity extends FragmentActivity implements
		OnClickArObjectListener {

	private static final String TMP_IMAGE_PREFIX = "viewImage_";
	private ArView mArView;
	private World mWorld;
	private SeekBar mSeekBarPushAwayDistance,mSeekMaxRender,mSeekDistanceFactor;

	//--------------------放入我们的AR导航数据集-----------------------
	String strAR_POI_DatasetName = "T7_REGION_INFO";   //超图室内导航
	String strAR_POI_TitleName =   "FT_NAME_CN";
	final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

//	String strAR_POI_DatasetName = "points_it_zone";  //园区数据
//	String strAR_POI_TitleName =   "Name";

	Recordset mRecordsetAR = null;

	private MapControl m_mapcontrol = null;
	private Workspace m_workspace;
	private MapView m_mapView = null;
	public ArControl2 arControl2;

	public ArrayList<String> arrName = new ArrayList<String>();
	public ArrayList<Double> arrX = new ArrayList<Double>();
	public ArrayList<Double> arrY = new ArrayList<Double>();


	RelativeLayout mRL_all = null; // for arrender.
	//--------------------------------------------------------------

	//-------------定位模块---------------------
	public int mDegrees = 0;

	private double PI = 3.1415926535897932384626433833;
	private double RTOD = 57.295779513082320876798154814;//角度和弧度的转换
	//-----------------------------------------
	TextView mTextViewOFGPSLocation;	//GPS更新

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//设置绘制模式
		ARRendererInfoUtil.saveARRendererMode(this,ARRendererInfoUtil.MODE_POIMAP); //设置绘制模式

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.simple_camera);

        //添加相机, 这里使用AR包内的CameraView，可自定义
        FrameLayout.LayoutParams cameraViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((RelativeLayout)findViewById(R.id.rl_all)).addView(new CameraView(this), 1,cameraViewParams);

		mArView = findViewById(R.id.arView);
		mArView.setOnClickArObjectListener(this);

		initMap(); //初始化地图并且获取POI数据

		// 创建增强现实世界
		mWorld = CustomWorldHelper.generateMyObjects(this);

		initTextViewOfGPSLoation();

		initLowPassFilter();	//初始化滤波

		initLayoutSeekbar();	//初始化进度条

		initArControlPoi();


		mArView.setWorld(mWorld);

		//replace all GeoObjects the images with a simple static view
		replaceImagesByStaticViews(mWorld);

	}



	private void initTextViewOfGPSLoation() {
		mTextViewOFGPSLocation = (TextView) findViewById(R.id.textGPSLocation);
	}


	private void initLowPassFilter()
	{
		//设置滤波值
//		LowPassFilter.ALPHA = 0.098f;
		LowPassFilter.ALPHA = 0.030f;
//		mArFragment.setPullCloserDistance(10);  //设置POI固定大小

	}


	private void initLayoutSeekbar()
	{
		mArView.setMaxDistanceToRender(8);

		//设置distance factor
		mSeekDistanceFactor = (SeekBar) findViewById(R.id.seekFactorRender);
		mSeekDistanceFactor.setMax(100); //100
//		mSeekBarPushAwayDistance.setProgress(150);
		mSeekDistanceFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(seekBar == mSeekDistanceFactor) {
					mArView.setDistanceFactor(progress/10);//把0-100映射到10内
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		//设置Max render.
		mSeekMaxRender = (SeekBar) findViewById(R.id.seekMaxRender);
		mSeekMaxRender.setMax(100);
		mSeekMaxRender.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(seekBar == mSeekMaxRender) {
					mArView.setMaxDistanceToRender(progress);
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

	}


	private void initArControlPoi() {
		mRL_all = (RelativeLayout) findViewById(R.id.rl_all);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}


	private void initMap() {

		m_workspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(sdcard+"/SampleData/AR/supermapindoor.smwu");
		info.setType(WorkspaceType.SMWU);

		if (m_workspace.open(info)) {


			m_mapView = (MapView)findViewById(R.id.testMapView);
			m_mapcontrol = m_mapView.getMapControl();

			m_mapcontrol.getMap().setWorkspace(m_workspace);
			String mapName = m_workspace.getMaps().get(0);    //supermap
//			String mapName = m_workspace.getMaps().get(6);	  //supermpa_it_zone


			m_mapcontrol.getMap().open(mapName);
			m_mapcontrol.getMap().refresh();

			System.out.println("localMapName is:"+mapName);

		}


		//查找出所有的POI
		mRecordsetAR = queryByName_All(strAR_POI_DatasetName);
		if(mRecordsetAR != null)
		{
			//查询AR地图所有的数据, 包括: 名称, X坐标, Y坐标
			queryARRecordsetData(strAR_POI_TitleName, arrName, arrX, arrY);
		}
	}

	public void postToastMessage(final String message) {
		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(StaticViewGeoObjectActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	public Recordset queryByName_All(String strDatasetName) {
		if (mRecordsetAR != null) {
			return mRecordsetAR;
		}
		Layers layers = m_mapcontrol.getMap().getLayers();

		Datasource dtSource = m_workspace.getDatasources().get(0);   //supermap
//		Datasource dtSource = m_workspace.getDatasources().get(6);   //points_it_zone


		mRecordsetAR = ((DatasetVector)(dtSource.getDatasets().get(9))).getRecordset(false, CursorType.STATIC);

		return mRecordsetAR;
	}


	/**
	 * 查询AR图斑所有的数据, 包括: 名称, X坐标, Y坐标
	 */
	public void  queryARRecordsetData(String strQueryName ,ArrayList<String> arrName, ArrayList<Double> arrX, ArrayList<Double> arrY){
		if (strQueryName == null || arrName == null || arrX == null || arrY == null){
			return;
		}

		//遍历POI数据集, 获取POI的名称,XY坐标
		if ( mRecordsetAR != null && mRecordsetAR.getRecordCount() > 0) {
			mRecordsetAR.moveFirst();
			for (int i = 0; i < mRecordsetAR.getRecordCount(); i++) {
				//根据查询结果,获取坐标
				Geometry plGeometry = mRecordsetAR.getGeometry();
				Point2D plCenter = plGeometry.getBounds().getCenter();

				Object plName = mRecordsetAR.getFieldValue(strQueryName);
				String plStrName = null;
				if (plName != null) {
					plStrName = plName.toString();

					arrName.add(plStrName);
					arrX.add(plCenter.getX());
					arrY.add(plCenter.getY());
				}

				mRecordsetAR.moveNext();
			}//for
		}//if

	}


	private void replaceImagesByStaticViews(World world) {
		for (ArObjectList arList : world.getArObjectLists()) {
			for (ArObject arObject : arList) {
				View view = getLayoutInflater().inflate(R.layout.static_ar_object_view, null);

				Button button_poi_name = (Button) view.findViewById(R.id.btn_poi_name);
				Button button_poi_dist = (Button) view.findViewById(R.id.btn_poi_distance);
				button_poi_name.setText(arObject.getName());
				button_poi_dist.setText(""+arObject.getDistanceFromUser()+"m");

				mArView.storeArObjectViewAndUri(view,arObject);
			}
		}
	}

	
	@Override
	public void onClickArObject(ArrayList<ArObject> arObjects) {
		if (arObjects.size() > 0) {
			Toast.makeText(this, "Clicked on: " + arObjects.get(0).getName(), Toast.LENGTH_LONG).show();
		}
	}

}
