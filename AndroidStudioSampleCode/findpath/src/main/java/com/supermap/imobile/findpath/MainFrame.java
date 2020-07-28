package com.supermap.imobile.findpath;

import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.data.Color;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:最佳路径分析示范代码
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
 * 1、范例简介：示范如何进行最佳路径分析
 * 2、示例数据：安装目录/sdcard/SampleData/City/Changchun.udb
 * 3、关键类型/成员: 
 *		TransportationAnalystSetting.setNetworkDataset()	方法
 *		TransportationAnalystSetting.setEdgeIDField()		方法
 *		TransportationAnalystSetting.setNodeIDField()		方法
 *		TransportationAnalystSetting.setEdgeNameField()		方法
 *		TransportationAnalystSetting.setWeightFieldInfos()	方法
 *		TransportationAnalystSetting.setFNodeIDField()		方法
 *		TransportationAnalystSetting.setTNodeIDField()		方法
 *		TransportationAnalyst.setAnalystSetting()			方法
 *		TransportationAnalyst.load()						方法
 *		TransportationAnalyst.findPath()					方法
 *		TransportationAnalystParameter.setPoints()			方法
 *		TransportationAnalystParameter.setNodesReturn()		方法
 *		TransportationAnalystParameter.setEdgesReturn()		方法
 *		TransportationAnalystParameter.setPathGuidesReturn()方法
 *		TransportationAnalystParameter.setRoutesReturn()	方法

 * 4、使用步骤：
 *   (1)点击设置起点按钮，长按地图选取起点
 *   (2)点击添加目标点按钮，长按地图选取目标点（可以设置多个目标点）
 *   (3)点击路径分析按钮，进行分析，结果在地图中展现出来
 *   (4)点击清除结果按钮，清除分析结果
 *   (5)点击重新设置起点，进行下一次分析
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
public class MainFrame extends Activity  implements OnClickListener {
    
	private Workspace mWorkspace = null;	
	private MapView mMapView = null;
	private MapControl mMapControl = null;	
	
	private Button btnRoute = null;
	private Button btnSetting = null;
	private Button btnClean = null;
	private Button btnDsetting = null;
	
	//操作过程中的状态改变
	private boolean bEndPointEnable = false;
	private boolean bAnalystEnable = false;
	private boolean bLongPressEnable = false;
	//当进行路径分析后则不能修改起点终点
	private boolean bSettingEnable = true;
	
	private DatasetVector m_datasetLine;
	private Layer m_layerLine;
	private TrackingLayer m_trackingLayer;	
	private Point2Ds m_Points = null;
	private static String m_datasetName = "RoadNet";

	private static String m_nodeID = "SmNodeID";
	private static String m_edgeID = "SmEdgeID";	
	private TransportationAnalyst m_analyst;
	private TransportationAnalystResult m_result;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

	//接收长按事件
	private GestureDetector.SimpleOnGestureListener mGestrueListener = new SimpleOnGestureListener(){
		public void onLongPress(MotionEvent e) {
			if(!bLongPressEnable){
				return;
			}
			int x = (int) e.getX();
			int y = (int) e.getY();
			Point2D pt = mMapControl.getMap().pixelToMap(new Point(x, y));
			CallOut callout = new CallOut(MainFrame.this);
			callout.setStyle(CalloutAlignment.LEFT_BOTTOM);
			callout.setCustomize(true);
			callout.setLocation(pt.getX(),pt.getY());
			//当投影不是经纬坐标系时，则对起始点进行投影转换 
			if(mMapControl.getMap().getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
				Point2Ds points = new Point2Ds();
				points.add(pt);
				PrjCoordSys desPrjCoorSys = new PrjCoordSys();
				desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				CoordSysTranslator.convert(points, mMapControl.getMap().getPrjCoordSys(), desPrjCoorSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
				pt = points.getItem(0);
			}
			ImageView image = new ImageView(MainFrame.this);
			//添加第一个点
			if(!bEndPointEnable){
				image.setBackgroundResource(R.drawable.startpoint);
				callout.setContentView(image);
				mMapView.addCallout(callout);
				m_Points.add(pt);//添加目标点
				bEndPointEnable = true;
				bLongPressEnable = false;
				btnSetting.setText("添加目标点");
				btnSetting.invalidate();
				return;
			}
			image.setBackgroundResource(R.drawable.despoint);
			callout.setContentView(image);
			mMapView.addCallout(callout);
			m_Points.add(pt);//添加目标点
			bAnalystEnable = true;
			btnSetting.setText("添加目标点");
			bLongPressEnable = false;
		};
	};
	/**
	 * 需要申请的权限数组
	 */
	protected String[] needPermissions = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_WIFI_STATE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.CHANGE_WIFI_STATE,
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestPermissions();
        // 初始化环境,设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        //设置一些系统需要用到的路径
        Environment.initialization(this);
        
        setContentView(R.layout.main);
        initUI();
        initialize();
    }
	/**
	 * 检测权限
	 * return true:已经获取权限
	 * return false: 未获取权限，主动请求权限
	 */

	public boolean checkPermissions(String[] permissions) {
		return EasyPermissions.hasPermissions(this, permissions);
	}

	/**
	 * 申请动态权限
	 */
	private void requestPermissions() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return;
		}
		if (!checkPermissions(needPermissions)) {
			EasyPermissions.requestPermissions(
					this,
					"为了应用的正常使用，请允许以下权限。",
					0,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.ACCESS_WIFI_STATE,
					Manifest.permission.ACCESS_NETWORK_STATE,
					Manifest.permission.CHANGE_WIFI_STATE);
			//没有授权，编写申请权限代码
		} else {
			//已经授权，执行操作代码
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("交通网络分析对象载入中...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//加载网络模型
				load();
				dialog.dismiss();
			}
		}).start();
		
    	super.onStart();
    }
    
    private void initUI(){
    	mMapView = (MapView)findViewById(R.id.mapview);
    	mMapControl = mMapView.getMapControl();
    	btnRoute = (Button) findViewById(R.id.analyst);
    	btnRoute.setOnClickListener(this);
    	btnSetting = (Button) findViewById(R.id.setting);
    	btnSetting.setOnClickListener(this);
    	btnClean = (Button)findViewById(R.id.btn_bufferClean);
    	btnClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMapControl.getMap().getTrackingLayer().clear();
				mMapControl.getMap().refresh();
			}
		});
    	btnDsetting = (Button)findViewById(R.id.Dsetting);
    	btnDsetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bSettingEnable = true;
				bEndPointEnable = false;
				bAnalystEnable = false;
				bLongPressEnable = true;
				mMapView.removeAllCallOut();
				m_Points.clear();
				btnSetting.setText("设置起点");
				btnRoute.setText("路径分析");
			}
		});
    	
    	m_Points = new Point2Ds();
    }
    
    
    /**
	 * 打开网络数据集并初始化相应变量
	 */
	private void initialize()
	{
    	mWorkspace = new Workspace();
    	
		//打开数据源,得到点线数据集
		DatasourceConnectionInfo info = new DatasourceConnectionInfo();
		String dataServer = sdcard+"/SampleData/City/Changchun.udb";
	
		info.setEngineType(EngineType.UDB);
		info.setServer(dataServer);
		Datasource datasource = mWorkspace.getDatasources().open(info);
    	
    	mMapControl.getMap().setWorkspace(mWorkspace);
   
		m_datasetLine = (DatasetVector)datasource.getDatasets().get(m_datasetName);
		m_trackingLayer = mMapControl.getMap().getTrackingLayer();

		

		//加载线数据集并设置风格
		m_layerLine = mMapControl.getMap().getLayers().add(m_datasetLine,
				true);
		m_layerLine.setSelectable(false);
		LayerSettingVector lineSetting = (LayerSettingVector)m_layerLine
				.getAdditionalSetting();
		GeoStyle lineStyle = new GeoStyle();
		lineStyle.setLineColor(new Color(0, 0, 255));
		lineStyle.setLineWidth(0.1);
		lineSetting.setStyle(lineStyle);
		
    	mMapControl.getMap().viewEntire();
		mMapControl.getMap().refresh();

		//设置手势委托
    	mMapControl.setGestureDetector(new GestureDetector(mGestrueListener));
	}
	
	/**
	 * 加载环境设置对象
	 */
	public void load()
	{
		// 设置网络分析基本环境，这一步骤需要设置　分析权重、节点、弧段标识字段、容限
		TransportationAnalystSetting setting = new TransportationAnalystSetting();
		setting.setNetworkDataset(m_datasetLine);
		setting.setEdgeIDField(m_edgeID);
		setting.setNodeIDField(m_nodeID);
		setting.setEdgeNameField("roadName");
	
		setting.setTolerance(89);

		WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
		WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
		weightFieldInfo.setFTWeightField("smLength");
		weightFieldInfo.setTFWeightField("smLength");
		weightFieldInfo.setName("length");
		weightFieldInfos.add(weightFieldInfo);
		setting.setWeightFieldInfos(weightFieldInfos);
		setting.setFNodeIDField("SmFNode");
		setting.setTNodeIDField("SmTNode");

		//构造交通网络分析对象，加载环境设置对象
		m_analyst = new TransportationAnalyst();
		m_analyst.setAnalystSetting(setting);
		m_analyst.load();

	}

	/**
	 * 进行最短路径分析
	 */
	public boolean analyst()
	{
		TransportationAnalystParameter parameter = new TransportationAnalystParameter();
		
		parameter.setWeightName("length");

		//设置最佳路径分析的返回对象
		parameter.setPoints(m_Points);
		parameter.setNodesReturn(true);
		parameter.setEdgesReturn(true);
		parameter.setPathGuidesReturn(true);
		parameter.setRoutesReturn(true);

		try{
			//进行分析并显示结果
			m_result = m_analyst.findPath(parameter, false);
		}
		catch(Exception e){
			m_result = null;
		}
		if (m_result == null)
		{
			toastInfo("分析失败");
			return false;
		}
		showResult();
		return true;
	}

	private void toastInfo(final String msg) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(MainFrame.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	/**
	 * 显示结果
	 */
	public void showResult()
	{

		//删除原有结果
		int count = m_trackingLayer.getCount();
		for (int i = 0; i < count; i++)
		{
			int index = m_trackingLayer.indexOf("result");
			if (index != -1)
				m_trackingLayer.remove(index);
		}

		GeoLineM[] routes = m_result.getRoutes();
		
		if (routes == null) {
			return;
		}
		
		for (int i = 0; i < routes.length; i++)
		{
			GeoLineM geoLineM = routes[i];
			GeoStyle style = new GeoStyle();
			style.setLineColor(new Color(255, 80, 0));
			style.setLineWidth(1);
			geoLineM.setStyle(style);
			m_trackingLayer.add(geoLineM, "result");
		}
		
		mMapControl.getMap().refresh();
	}
	
	@Override
	public void onClick(View btn) {
		switch (btn.getId()) {
		case R.id.analyst:
			if(!bAnalystEnable){
				Toast.makeText(this, "请先设置起点和终点!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("路径分析中...");
			dialog.show();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					//进行最短路径分析
					analyst();
					dialog.dismiss();
				}
			}).start();
			
			break;
		case R.id.setting:
			if(!bSettingEnable){
				Toast.makeText(this, "不能修改起点和终点!", Toast.LENGTH_SHORT).show();
				bLongPressEnable = true;
				return;
			}
			if(bEndPointEnable){
				Toast.makeText(this, "长按添加终点!", Toast.LENGTH_SHORT).show();
				bLongPressEnable = true;
				return;
			}
			Toast.makeText(this, "长按设置起点!", Toast.LENGTH_SHORT).show();
			bLongPressEnable = true;
			break;
		default: 
			break;
		}
	}
	
}