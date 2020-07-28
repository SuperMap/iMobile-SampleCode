package com.supermap.imobile.facilityanalyst;

import java.util.ArrayList;
import com.supermap.analyst.networkanalyst.FacilityAnalyst;
import com.supermap.analyst.networkanalyst.FacilityAnalystResult;
import com.supermap.analyst.networkanalyst.FacilityAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.data.Color;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.GeoText;
import com.supermap.data.Geometry;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Size2D;
import com.supermap.data.TextPart;
import com.supermap.data.TextStyle;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.Selection;
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
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:设施网络分析示范代码
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
 * 1、范例简介：示范如何进行设施网络分析，并展示出来
 * 2、示例数据：安装目录/SampleData/FacilityAnalyst/FacilityAnalyst.smwu
 * 3、关键类型/成员: 
 *      FacilityAnalystSetting.setContentView() 	方法
 *      FacilityAnalystSetting.setNetworkDataset()	方法
 *		FacilityAnalystSetting.setNodeIDField()		方法
 *		FacilityAnalystSetting.setEdgeIDField()		方法
 *		FacilityAnalystSetting.setFNodeIDField()	方法
 *		FacilityAnalystSetting.setTNodeIDField()	方法
 *		FacilityAnalystSetting.setDirectionField()	方法
 *		FacilityAnalyst.setAnalystSetting()			方法
 *		FacilityAnalyst.load()						方法
 *		FacilityAnalyst.traceDownFromNode()			方法
 *		FacilityAnalyst.traceUpFromNode()			方法

 * 4、使用步骤：
 *   (1)在地图上长按，进行节点选择
 *   (2)选中节点之后（进行连通性分析时，选中点至少有两个），点击相关分析按钮，进行分析，结果在地图中展现出来
 *   (3)单击清除按钮。重新选取节点，进行分析
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
public class MainFrame extends Activity {
	
	private MapView m_MapView = null;
	
	private Workspace m_workspace =  null;
	
	private MapControl m_mapControl = null;
	
	private Datasource m_datasource = null;

	private DatasetVector m_datasetVector = null;
	
	private Selection m_selection = null;

	private FacilityAnalyst m_facilityAnalyst = null;
	
	private TrackingLayer m_trackingLayer = null;
	
	private ArrayList<Integer> m_elementIDs = null;
	
	private Button m_btnLoadModel =  null;
	private Button m_btnFeatureSelect =  null;
	private Button m_btnTraceUp =  null;
	private Button m_btnTraceDown =  null;
	private Button m_btnConnectedAnalyst = null;
	private Button m_btnClear =  null;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestPermissions();
		 // 初始化环境,设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);	
		setContentView(R.layout.main);
		
		initVeiw();
		openMap();	
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
		
		final ProgressDialog dialog = new ProgressDialog(MainFrame.this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("分析模型载入中...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//加载网络模型
				loadModel();
				dialog.dismiss();
			}
		}).start();
		
		super.onStart();
	}
	
	private void initVeiw() {
		m_btnLoadModel = (Button)findViewById(R.id.btn_loadModel);	
		m_btnLoadModel.setOnClickListener(new Btnlistener());
		
		m_btnFeatureSelect = (Button)findViewById(R.id.btn_featureSelect);
		m_btnFeatureSelect.setOnClickListener(new Btnlistener());
		
		m_btnTraceUp = (Button)findViewById(R.id.btn_traceUp);
		m_btnTraceUp.setOnClickListener(new Btnlistener());
		m_btnTraceUp.setEnabled(false);
		
		m_btnTraceDown = (Button)findViewById(R.id.btn_traceDown);
		m_btnTraceDown.setOnClickListener(new Btnlistener());
		m_btnTraceDown.setEnabled(false);
		
		m_btnConnectedAnalyst = (Button)findViewById(R.id.btn_connectedAnalyst);
		m_btnConnectedAnalyst.setOnClickListener(new Btnlistener());
		m_btnConnectedAnalyst.setEnabled(false);
		
		m_btnClear = (Button)findViewById(R.id.btn_clear);
		m_btnClear.setOnClickListener(new Btnlistener());
		
		m_elementIDs = new ArrayList<Integer>();
	}

	/**
	 * 打开地图
	 */
	private void openMap() {
		m_MapView = (MapView) findViewById(R.id.mapview);
		m_mapControl=m_MapView.getMapControl();
		m_workspace = new Workspace();
		
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(sdcard+"/SampleData/FacilityAnalyst/FacilityAnalyst.smwu");
		info.setType(WorkspaceType.SMWU);
		boolean isOpen = m_workspace.open(info);
		if(!isOpen){
			showInfo("Workspace open failed!");
		}
		
		m_datasource = m_workspace.getDatasources().get("FacilityNet");
		
		m_mapControl.getMap().setWorkspace(m_workspace);	
		m_mapControl.getMap().open(m_workspace.getMaps().get(0));
		
		//除网络数据集结点图层外，设置其他图层不可选
		for (int i = 0; i < m_mapControl.getMap().getLayers().getCount(); i++) {
			if (i != 0)	{
				m_mapControl.getMap().getLayers().get(i).setSelectable(false);
			} else {
				m_mapControl.getMap().getLayers().get(i).setSelectable(true);
			}
		}
		
		m_selection = m_mapControl.getMap().getLayers().get(1).getSelection();
		m_trackingLayer = m_mapControl.getMap().getTrackingLayer();
		
		// 手势监听器
    	m_mapControl.setGestureDetector(new GestureDetector(new MapGestureListener()));
		m_mapControl.setAction(Action.PAN);
		m_mapControl.getMap().refresh();
	}

	/**
	 * 加载设施网络分析模型
	 */
	public boolean loadModel() {
		m_datasetVector = (DatasetVector) m_datasource.getDatasets().get(
				"WaterNet");
		FacilityAnalystSetting analystSetting = new FacilityAnalystSetting();
		analystSetting.setNetworkDataset(m_datasetVector);
		analystSetting.setNodeIDField("SmNodeID");
		analystSetting.setEdgeIDField("SmID");
		analystSetting.setFNodeIDField("SmFNode");
		analystSetting.setTNodeIDField("SmTNode");
		analystSetting.setDirectionField("Direction");
		WeightFieldInfo fieldInfo = new WeightFieldInfo();
		fieldInfo.setName("length");
		fieldInfo.setFTWeightField("SmLength");
		fieldInfo.setTFWeightField("SmLength");
		WeightFieldInfos fieldInfos = new WeightFieldInfos();
		fieldInfos.add(fieldInfo);
		analystSetting.setWeightFieldInfos(fieldInfos);
		m_facilityAnalyst = new FacilityAnalyst();
		m_facilityAnalyst.setAnalystSetting(analystSetting);
	
		return m_facilityAnalyst.load();
	}
	
	/**
	 * 根据结果数据ID集合，高亮显示
	 * @param resultIDs
	 */
	private void displayResult() {
		Recordset recordset = m_selection.toRecordset();
		recordset.moveFirst();
		while (!recordset.isEOF()) {
			Geometry geometry = recordset.getGeometry();
			GeoStyle style = getGeoStyle(new Size2D(10,10), new Color(255, 105, 0));
			geometry.setStyle(style);
			m_trackingLayer.add(geometry, "");
			recordset.moveNext();
		}
		m_mapControl.getMap().refresh();
	}

	
	/**
	 * 上游分析
	 */
	public void traceUp() {
		m_selection.clear();
		for (int i = 0; i < m_elementIDs.size(); i++) {
			FacilityAnalystResult facilityPathResult = m_facilityAnalyst.traceUpFromNode(m_elementIDs.get(i),
					"length", true);
			int[] resultIDs = facilityPathResult.getEdges();
			for (int j = 0; j < resultIDs.length; j++) {
				m_selection.add(resultIDs[j]);
			}
		}

		displayResult();
		m_mapControl.setAction(Action.PAN);
	}

	/**
	 * 下游分析
	 */
	public void traceDown() {

		m_selection.clear();
		for (int i = 0; i < m_elementIDs.size(); i++) {
			FacilityAnalystResult facilityPathResult = m_facilityAnalyst.traceDownFromNode(m_elementIDs.get(i),
					"length", true);
			int[] resultIDs = facilityPathResult.getEdges();
			for (int j = 0; j < resultIDs.length; j++) {
				m_selection.add(resultIDs[j]);
			}
		}

		System.out.println("selection is "+m_selection.getCount());
		displayResult();
		m_mapControl.setAction(Action.PAN);
	
	}
	
	/**
	 * 连通性分析
	 */
	public void connectedAnalyst() {
		m_selection.clear();
		int[] IDs = new int[m_elementIDs.size()];
		for (int i = 0; i < IDs.length; i++) {
			IDs[i] = m_elementIDs.get(i);
		}
		for (int i = 0; i < IDs.length - 1; i++) {
			FacilityAnalystResult facilityPathResult = m_facilityAnalyst
					.findPathFromNodes(IDs[i], IDs[i + 1], "length", false);	
			if (facilityPathResult == null) {
				System.out.println("facilityPathResult is null");
				continue;
			}
			int[] edgess = facilityPathResult.getEdges();
			for (int j = 0; j < edgess.length; j++) {
				m_selection.add(edgess[j]);
			}
		}

		displayResult();
		m_mapControl.setAction(Action.PAN);

	}
	
	private void showInfo(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 清空选择与分析结果 
	 */
	public void clearUp() {
		m_selection.clear();
		m_trackingLayer.clear();
		m_elementIDs.clear();
		m_mapControl.setAction(Action.PAN);
		m_mapControl.getMap().refresh();
	}
	
	/**
	 * 得到一个GeoStyle对象
	 */
	private GeoStyle getGeoStyle(Size2D size2D, Color color) {
		GeoStyle geoStyle = new GeoStyle();
		geoStyle.setMarkerSize(size2D);
		geoStyle.setLineColor(color);
		return geoStyle;
	}
	
	class Btnlistener implements OnClickListener{
			
		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_loadModel:
				
						
				break;
			case R.id.btn_featureSelect:
				m_mapControl.setAction(Action.SELECT);
				GeoStyle geoStyle = new GeoStyle();
				geoStyle.setLineColor(new Color(255, 0, 0));
				geoStyle.setLineWidth(0.4);
				m_selection.setStyle(geoStyle);
				break;
			case R.id.btn_traceUp:
				traceUp();	
				break;
			case R.id.btn_traceDown:
				traceDown();	
				break;
			case R.id.btn_clear:
				clearUp();
				break;
			case R.id.btn_connectedAnalyst:
				connectedAnalyst();
				break;
			default:
				break;
			}
		}
	}
	
	// 手势监听器
	class MapGestureListener extends SimpleOnGestureListener{
		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("长按");
			
			Point pt =new Point((int)e.getX(), (int)e.getY());
			Selection selection = m_mapControl.getMap().getLayers().get(0).hitTestEx(pt, 20);			
			
			if (selection != null && selection.getCount() > 0) {
				Recordset recordset = selection.toRecordset();
				GeoPoint point = (GeoPoint) recordset.getGeometry();
				m_elementIDs.add(recordset.getInt32("SMNODEID"));
				System.out.println(recordset.getInt32("SMNODEID"));
				
				m_btnTraceDown.setEnabled(true);
				m_btnTraceUp.setEnabled(true);
				
				if (m_elementIDs.size() > 1) {
					m_btnConnectedAnalyst.setEnabled(true);
				}
				GeoStyle geoStyle = getGeoStyle(new Size2D(10,
						10), new Color(255, 105, 0));
				geoStyle.setMarkerSymbolID(3614);
				point.setStyle(geoStyle);
		
				int count = m_elementIDs.size();
				TextPart textPart = new TextPart("要素" + count,
						new Point2D(point.getX(), point.getY()));
				GeoText geoText = new GeoText(textPart);
				TextStyle textStyle = new TextStyle();
				textStyle.setForeColor(new Color(0, 255, 0));
				geoText.setTextStyle(textStyle);
		
				m_trackingLayer.add(point, "");
				m_trackingLayer.add(geoText, "");
				m_mapControl.getMap().refresh();
		
				point.dispose();
				geoText.dispose();
				recordset.close();
				recordset.dispose();
			}
			
			super.onLongPress(e);
		}
	}
}