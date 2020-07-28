package com.supermap.imobile.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.ar.ARRendererInfoUtil;
import com.supermap.ar.ArView;
import com.supermap.ar.ArObject;
import com.supermap.ar.ArObjectList;
import com.supermap.ar.ArViewAdapter;
import com.supermap.ar.CameraView;
import com.supermap.ar.GeoObject;
import com.supermap.ar.LowPassFilter;
import com.supermap.ar.OnClickArObjectListener;
import com.supermap.ar.Point3D;
import com.supermap.ar.World;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;

import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Title:视频地图的POI投射和地图投射功能
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
 * 1、范例简介：示范视频地图的POI投射和地图投射功能
 * 2、示例数据：数据目录："/sdcard/SampleData/AR/"
 *            地图数据：supermapindoor.smwu,supermap.udb,supermap.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 ArView.setOnClickArObjectListener()    //监听事件
 *	 ArView.setDistanceFactor()  			//设置默认POI显示大小方法
 *   ArView.setArViewAdapter()    			//设置AR视图适配器
 *	 ArView.setWorld()						//AR场景关联
 *   ArView.getMapChangedMatrix()   		//地图投射时获取矩阵
 *   ArView.getIntersectionPoint()			//获取内点
 *   ArView.setHead()   					//设置视点高度
 *   ArView.setDistanceFactor()   			//设置投射物（POI/地图）显示大小
 *   ArView.setMaxDistanceToRender();  		//设置最大显示范围
 *   ArView.storeArObjectViewAndUri()		//设置缓存路径
 *	 ARRendererInfoUtil.saveARRendererMode()//设置绘制模式
 *   LowPassFilter.ALPHA  					//设置滤波值常量
 *   GeoObject.setDistanceFromUser() 		//添加距离信息
 *   World.addArObject()					//添加到AR场景中
 *	 World.setGeoPosition()					//设置位置信息
 *
 * 4、使用步骤：
 * （1）点击POI投射，展示POI信息
 * （2）点击POI，展示详细信息
 * （3）点击地图投射，展示地图信息
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
public class ARProjectionActivity extends FragmentActivity{
	final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

	//AR相关
	private ArView m_ArView = null;
	private World m_World = null;

	//进度条控件
	private SeekBar m_SeekMaxRender = null;
	private SeekBar m_SeekDistanceFactor = null;
	private SeekBar m_SeekHeadFactor = null;

	//地图控件
	private MapControl m_MapControl = null;
	private MapView m_MapView = null;

	//传感器相关
	private SensorManager m_SensorManager = null;
	private SensorEventListener m_SensorEventListener = null;
	private Sensor m_magneticSensor = null;         //磁力传感器
	private Sensor m_accelerometerSensor = null;    //加速度传感器

	//矩阵相关
	private float [] m_transformMatirx  = new float[16];
	private float [] m_projectionMatrix = new float[16];

	//用于保存POI投射时的弹出框
	private List<ArObject> m_listARObjects;

	//用于地图投射刷新
	private static final int TIMER = 999;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//①设置绘制模式，必须放在setContentView前面
		ARRendererInfoUtil.saveARRendererMode(this,ARRendererInfoUtil.MODE_PROJECTION);

		setContentView(R.layout.ar_projection);

		//②添加相机布局
		AddCarmeraView();

		//③初始化并注册传感器
		InitAndRegisterSensor();

		//④初始化ARView
		InitARView();

		//⑤创建增强现实世界并关联ARView
		InitARWord();

		//⑥初始化地图并关联ARView
		InitMap();

		//⑦初始化进度条（可选）
		initSeekbar();

		//⑧设置滤波值，根据设备实际设置，越小投射的内容移动的越慢
		initLowPassFilter();

		//⑨地图投射时，延缓地图刷新，节约资源
		new Handler().postDelayed(new Runnable(){
			public void run(){
				setTimer();
			}
		}, 3000);
	}

		//②添加相机布局
		public void AddCarmeraView(){

			RelativeLayout mRelativeLayout = findViewById(R.id.camera_ar);
			FrameLayout.LayoutParams cameraViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
					.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			CameraView mArCameraView = new CameraView(this);
			mRelativeLayout.addView(mArCameraView, 0,cameraViewParams);
		}

		//③初始化并注册传感器
		public void InitAndRegisterSensor(){
			m_SensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			m_SensorEventListener = new SensorEventListener() {
				@Override
				public void onSensorChanged(SensorEvent event) {

				}
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {

				}
			};
			m_SensorManager.registerListener(m_SensorEventListener, m_magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
			m_SensorManager.registerListener(m_SensorEventListener, m_accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			m_magneticSensor = m_SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);  //磁场传感器
			m_accelerometerSensor = m_SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
		}

		//④初始化ARView
		public void InitARView(){

			m_listARObjects = Collections.synchronizedList(new ArrayList<ArObject>());
			m_ArView = findViewById(R.id.arview);
			m_ArView.setOnClickArObjectListener(new OnClickArObjectListener() {
				@Override
				public void onClickArObject(ArrayList<ArObject> arrayList) {//点击控制是否显示弹出框
					if (arrayList.size() == 0) {
						return;
					}
					ArObject arObject = arrayList.get(0);
					if (m_listARObjects.contains(arObject)) {
						m_listARObjects.remove(arObject);
					} else {
						m_listARObjects.add(arObject);
					}
				}
			});
			m_ArView.setDistanceFactor(0.8f);   //设置默认POI显示大小
			m_ArView.setArViewAdapter(new ArViewAdapter(this) {//设置适配器以在AR视图顶部绘制视图
				@Override
				public View getView(ArObject arObject, View view, ViewGroup viewGroup) {

					if (!m_listARObjects.contains(arObject)) {
						return null;
					}
					List<ArObjectList> arObjectList = m_World.getArObjectLists();
					ArObjectList localARObjectList = arObjectList.get(0);
					for(int i = 0;i<localARObjectList.size();i++){
						if(localARObjectList.get(i).getId() == ArView.PROJECTION_MAP_ID){
							return null;
						}
					}
					LayoutInflater inflater = (LayoutInflater) ARProjectionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					if (view == null) {
						view = inflater.inflate(R.layout.ar_object_view, null);
					}
					arObject.setIsShow(true);
					TextView textView = (TextView) view.findViewById(R.id.titleTextView);
					textView.setText(arObject.getName());
					Button button = (Button) view.findViewById(R.id.button);
					button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
					button.setText("方位角: " + arObject.getAngle().z);
					button.setTag(arObject.getName());

					setPosition(arObject.getScreenPositionTopRight());

					return view;
				}
			});
		}

		//⑤创建增强现实世界并关联ARView
		public void InitARWord(){
			m_World = new World(this);
			m_World.setGeoPosition(116.512097222,39.9918833333);   //设置当前位置
			m_ArView.setWorld(m_World);         //AR场景关联
		}

		//⑥初始化地图并关联ARView
		public void InitMap(){
			Workspace workspace = new Workspace();
			WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
			info.setServer(sdcard+"/SampleData/AR/supermapindoor.smwu");
			info.setType(WorkspaceType.SMWU);
			if (workspace.open(info)) {
				m_MapView = findViewById(R.id.projectionMapView);
				m_MapControl = m_MapView.getMapControl();
				m_MapControl.getMap().setWorkspace(workspace);
				String mapName = workspace.getMaps().get(0);
				if (m_MapControl.getMap().open(mapName)) {
					m_MapControl.getMap().setAlphaOverlay(true);
					m_MapControl.setMapOverlay(true);

					m_ArView.setMapView(m_MapView);
					m_ArView.setMapcontrol(m_MapControl);

					m_MapControl.getMap().setIsArmap(true);//设置为AR地图
					m_MapControl.getMap().setARMapType(5); //设置ARMap类型为POI视图模式

					m_ArView.getMapChangedMatrix(m_transformMatirx,m_projectionMatrix);
					m_MapControl.getMap().setTransformMatrix(m_transformMatirx);
					m_MapControl.getMap().setProjectMatrix(m_projectionMatrix);

					m_MapControl.getMap().refresh();
				}
				else{
					Toast.makeText(this,"打开地图失败！",Toast.LENGTH_SHORT).show();
					return;
				}
			}
			else{
				Toast.makeText(this,"打开工作空间失败！",Toast.LENGTH_SHORT).show();
				return;
			}
		}

		//⑦初始化进度条
		public void initSeekbar() {

			m_ArView.setMaxDistanceToRender(8);

			//设置视点高度
			m_SeekHeadFactor = (SeekBar) findViewById(R.id.seekHead);
			m_SeekHeadFactor.setMax(100);
			m_SeekHeadFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if(seekBar == m_SeekHeadFactor) {
						m_ArView.setHead((float)progress);  //设置视点高度
					}
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});

			//设置投射物大小
			m_SeekDistanceFactor = (SeekBar) findViewById(R.id.seekFactorRender);
			m_SeekDistanceFactor.setMax(100);
			m_SeekDistanceFactor.setProgress(20);
			m_SeekDistanceFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if(seekBar == m_SeekDistanceFactor) {
						m_ArView.setDistanceFactor((float)progress/10); //设置投射物（POI/地图）显示大小
					}
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}
			});

			//设置最大显示范围
			m_SeekMaxRender = (SeekBar) findViewById(R.id.seekMaxRender);
			m_SeekMaxRender.setMax(100);
			m_SeekMaxRender.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if(seekBar == m_SeekMaxRender) {
						m_ArView.setMaxDistanceToRender(progress);  //设置最大显示范围
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

		//⑧设置滤波值
		public void initLowPassFilter(){
			LowPassFilter.ALPHA = 0.018f;//设置滤波值，取决设备 越小移动的越慢
		}

		//POI投射
		public void btnPOIProjection_onClick(View view){

			List<ArObjectList> arObjectList = m_World.getArObjectLists();
			ArObjectList localARObjectList = arObjectList.get(0);
			for(int i = 0;i<localARObjectList.size();i++){
				if(localARObjectList.get(i).getId() == ArView.PROJECTION_MAP_ID){
					m_World.remove(localARObjectList.get(i));
				}
			}
			
			m_World.clearWorld();

			Point3D intersectionPoint = m_ArView.getIntersectionPoint(getResources().getDisplayMetrics().widthPixels/2,getResources().getDisplayMetrics().heightPixels/2);
			if(intersectionPoint != null){

				//①创建一个GeoObject(POI)，并设置id
				GeoObject tempArObject = new GeoObject(System.currentTimeMillis());

				//②设置GeoObject(POI)的地理经纬度坐标
				tempArObject.setGeoPosition(
						m_World.getLongitude()+intersectionPoint.x,
						m_World.getLatitude()+intersectionPoint.y,
						m_World.getAltitude()+intersectionPoint.z);

				//③设置GeoObject(POI)的名称
				tempArObject.setName("POI");
				DecimalFormat df = new DecimalFormat("0.00");
				tempArObject.setDistanceFromUser(Double.parseDouble(df.format(20)));//添加距离信息

				//④设置GeoObject(POI)的展示样式
				View poiView = getLayoutInflater().inflate(R.layout.static_ar_object_view, null);
				Button btnPOIName = (Button)poiView.findViewById(R.id.btn_poi_name);
				btnPOIName.setText(tempArObject.getName());
				Button btnPOIDistance = (Button)poiView.findViewById(R.id.btn_poi_distance);
				btnPOIDistance.setText(""+tempArObject.getDistanceFromUser()+"m");
				m_ArView.storeArObjectViewAndUri(poiView,tempArObject); //根据布局存储UI

				//⑤添加到AR场景中
				m_World.addArObject(tempArObject);
			}
		}

		//地图投射
		public void btnMapProjection_onClick(View view){

			m_World.clearWorld();

			//①添加地图
			int layersNum = m_MapControl.getMap().getLayers().getCount();
			for(int i = 0;i<layersNum;i++){
				m_MapControl.getMap().getLayers().get(i).setVisible(true);
			}

			Point3D point = m_ArView.getIntersectionPoint(getResources().getDisplayMetrics().widthPixels/2,getResources().getDisplayMetrics().heightPixels/2);
			if(point != null){

				//②设置GeoObject(POI)的地理经纬度坐标
				GeoObject tempArObject = new GeoObject(ArView.PROJECTION_MAP_ID);
				tempArObject.setGeoPosition(
						m_World.getLongitude()+point.x,
						m_World.getLatitude()+point.y,
						m_World.getAltitude()+point.z);

				//③设置展示样式
				tempArObject.setName("");
				DecimalFormat df = new DecimalFormat("0.00");
				tempArObject.setDistanceFromUser(Double.parseDouble(df.format(20)));//添加距离信息
				m_ArView.storeArObjectViewAndUri(view,tempArObject);

				//④添加到AR场景中去
				m_World.addArObject(tempArObject);
			}
		}

		private void setTimer(){
			Message message = m_handler.obtainMessage(TIMER);     // Message
			m_handler.sendMessageDelayed(message, 1000);
		}

		private Handler m_handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what){
					case TIMER:

						m_ArView.getMapChangedMatrix(m_transformMatirx,m_projectionMatrix);
						m_MapControl.getMap().setTransformMatrix(m_transformMatirx);
						m_MapControl.getMap().setProjectMatrix(m_projectionMatrix);
						m_MapControl.getMap().refresh();

						Message message = m_handler.obtainMessage(TIMER);
						m_handler.sendMessageDelayed(message, 200);

						break;
					default:
						break;
				}
			}
		};

		@Override
		protected void onResume() {
			super.onResume();
			m_SensorManager.registerListener(m_SensorEventListener, m_magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
			m_SensorManager.registerListener(m_SensorEventListener, m_accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}

		@Override
		protected void onPause() {
			super.onPause();
			m_SensorManager.unregisterListener(m_SensorEventListener);
		}

}