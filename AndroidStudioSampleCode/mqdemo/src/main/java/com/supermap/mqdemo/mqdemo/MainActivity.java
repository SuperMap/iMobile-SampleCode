package com.supermap.mqdemo.mqdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoCircle;
import com.supermap.demo.mqdemo.R;
import com.supermap.plot.GeoGraphicObject;
import com.supermap.data.Enum;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.SpatialQueryMode;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometryAddedListener;
import com.supermap.mapping.GeometryEvent;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.RefreshListener;
import com.supermap.mapping.dyn.DynamicElement;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicPolygon;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.plugin.LocationManagePlugin.GPSData;

public class MainActivity extends Activity implements OnClickListener, MessageReceivedListener {

	/**
	 * 地图显示控件
	 */
	private MapControl 				mMapControl = null;
	private MapView					m_MapView = null;

	/**
	 * 地图显示操作
	 */
	private MapShow 				mapShow 			= null;
	public MessageQueue 			m_MessageQueue 		= null;

	private DynamicView				m_locateDynamicView = null;

	private PlotTypePopup           m_PlotTypePopup     = null;

	private int						m_LocationID = 0;
	private int						m_LocationPolygonID = 0;

	private Button btnZoomin;
	private Button btnZoomout;
	private Button btnFullScreen;

	private Button btnMultiMedia;
	private Button btnPlot;
	private Button btnSendMessage;
	private Button btnConsultMessage;
	private Button btnMessageSwitch;
	private Button btnTextMessage;
	private Button btnPositionUpload;
	private Button btnLocate;
	private Button btnDelete;
	private TextView btnNewMessage;
	private Button btnClear;


	private String mLibJBPath                           = null;
	private String mLibTYPath                           = null;
	private long mLIBIDJB                                  = 0;
	private long mLIBIDTY                                  = 0;

	public String sTextMessage 							= null;

	// 是否打开消息开关
	private boolean mMessageQueueOn = false;


	private MessageListPopup 		m_popupMessageList 		= null;
	private MultiMediaPopup 		m_popupMultiMedia 		= null;
	private TextMessagePopup		m_popupTextMessage		= null;


	private TencentLocTool			mTencentLocation 	= null;

	private GPSData					m_GPSData 			= null;
	public  Point2D					m_StartPoint		= null;

	private ArrayList<String>       m_ArraySendedIDs	= null;

	private int 					m_IDSelected 		= -1;
	private Layer					m_layerSelected 	= null;

	private int 					m_nMessageCount = 0;
	public Handler					m_handler = null;
	private static final int 		RECEIVEMSG = 0xff04;


	DatasetVector datasetVectorCAD = null;
	DatasetVector datasetVectorMultiMedia = null;
	float eventX = 0;
	float eventY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLibJBPath = DefaultDataConfiguration.MapDataPath + "Symbol/JB.plot";
		mLibTYPath = DefaultDataConfiguration.MapDataPath + "Symbol/TY.plot";

		setContentView(R.layout.activity_main);

		// 初始化布局
		initView();

		prepareData();

		try {
			m_MessageQueue = new MessageQueue(getApplicationContext());
			boolean bInit = m_MessageQueue.init();
			if (!bInit) {
				MyApplication.getInstance().showInfo("服务器连接失败!");
			}

		}catch (Exception ex) {
			ex.printStackTrace();
		}

		// 定位点初始化
		m_StartPoint = new Point2D();


		TencentLocTool.getInstance().init(this);
		mTencentLocation =TencentLocTool.getInstance();

		m_handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {

					case RECEIVEMSG:
//					String message = msg.obj.toString();
						messageChange();
						break;
					default:
						break;
				}
			};

		};

	}

	/*
	 * 准备地图数据
	 */
	private void prepareData(){
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setCancelable(false);
		progress.setMessage("数据加载中...");
		progress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
		progress.show();
		new Thread(){
			@Override
			public void run() {
				super.run();
				//配置数据
				new DefaultDataConfiguration().autoConfig();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();

						loadSymbolLibrary();

						// 打开地图
						mapShow = new MapShow(getApplicationContext(), mMapControl);
						mapShow.setMapData(DefaultDataConfiguration.DefaultWorkspace);
						mapShow.showMap();

						if (m_locateDynamicView == null) {
							m_locateDynamicView = new DynamicView(mMapControl.getContext(), mMapControl.getMap());
							m_MapView.addDynamicView(m_locateDynamicView);
						}


						// 初始化MapControl事件
						initMapControl();
						// 初始化手势操作
						initGestureDetecor();

						m_ArraySendedIDs = new ArrayList<String>();


					}
				});
			}
		}.start();
	}

	//防止初始化OnCreate()调用两次,这里做个重载
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mapShow != null) {
			mapShow.dispose();
		}

		if (m_MessageQueue != null) {
			m_MessageQueue.stopReceive();
		}
		if (m_popupMessageList != null) {
			m_popupMessageList.dismiss();
		}
		if (m_popupMultiMedia != null) {
			m_popupMultiMedia.dismiss();
		}
		if (m_popupTextMessage != null) {
			m_popupTextMessage.dismiss();
		}
//		if (mTencentLocation != null) {
//			mTencentLocation.dispose();
//		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		m_MessageQueue.setSynLocationReceivedListener(this);


	}

	private void loadSymbolLibrary() {
		File fLibJB = new File(mLibJBPath);
		File fLibTY = new File(mLibTYPath);

		if(fLibJB.exists() && fLibTY.exists()){
			mLIBIDJB = mMapControl.addPlotLibrary(mLibJBPath);
			mLIBIDTY = mMapControl.addPlotLibrary(mLibTYPath);
		}else{
			MyApplication.getInstance().showError("符号库文件不存在。。。");
		}
	}


	private void initView() {

		// 避免软键盘遮挡
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


		// 初始化MapControl
		m_MapView = (MapView)findViewById(R.id.mapview);
		mMapControl = m_MapView.getMapControl();

		// 初始化放大缩小按钮
		btnZoomin = (Button)findViewById(R.id.zoomin);
		btnZoomout = (Button)findViewById(R.id.zoomout);
		btnZoomin.setOnClickListener(new zoominBtnlistener());
		btnZoomout.setOnClickListener(new zoomoutBtnlistener());

		btnFullScreen = (Button)findViewById(R.id.fullscreen);
		btnFullScreen.setOnClickListener(new fullScreenBtnListener());

		btnMultiMedia = (Button)findViewById(R.id.multi_media);
		btnPlot = (Button)findViewById(R.id.plot);
		btnSendMessage = (Button)findViewById(R.id.send_message);
		btnConsultMessage = (Button)findViewById(R.id.consult_message);
		btnMessageSwitch = (Button)findViewById(R.id.message_switch);

		btnLocate = (Button)findViewById(R.id.locate);
		btnPositionUpload = (Button)findViewById(R.id.position_upload);
		btnTextMessage = (Button)findViewById(R.id.text_message);

		btnDelete = (Button)findViewById(R.id.delete);
		btnDelete.setOnClickListener(this);

		btnNewMessage = (TextView)findViewById(R.id.new_message);

		btnClear = (Button)findViewById(R.id.clear);
		btnClear.setOnClickListener(this);

		btnMultiMedia.setOnClickListener(this);
		btnPlot.setOnClickListener(this);
		btnSendMessage.setOnClickListener(this);
		btnConsultMessage.setOnClickListener(this);
		btnMessageSwitch.setOnClickListener(this);

		btnLocate.setOnClickListener(this);
		btnPositionUpload.setOnClickListener(this);
		btnTextMessage.setOnClickListener(this);


		// 文本消息初始化
		m_popupTextMessage = new TextMessagePopup(btnTextMessage.getRootView(), getApplicationContext(), this);
	}
	// 放大按钮
	class zoominBtnlistener implements OnClickListener {

		public void onClick(View v) {
			mMapControl.getMap().zoom(2);
			mMapControl.getMap().refresh();
		}
	}

	// 缩小按钮
	class zoomoutBtnlistener implements OnClickListener {

		public void onClick(View v) {
			mMapControl.getMap().zoom(0.5);
			mMapControl.getMap().refresh();
		}
	}

	class fullScreenBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mMapControl.getMap().viewEntire();
			mMapControl.getMap().refresh();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.consult_message:
			{
				mMapControl.submit();
				mMapControl.setAction(Action.SELECT);
				mMapControl.getMap().refresh();

				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}
				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				m_nMessageCount = 0;
				btnNewMessage.setVisibility(View.INVISIBLE);
				showMessageList(v);
			}
			break;
			case R.id.message_switch:
			{
				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}
				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				mMessageQueueOn = !mMessageQueueOn;
				if (!mMessageQueueOn) {
					btnMessageSwitch.setBackgroundResource(R.drawable.btn_message_switch_noselect);
					m_MessageQueue.suspend();
				} else {
					btnMessageSwitch.setBackgroundResource(R.drawable.btn_message_switch_focused);
					m_MessageQueue.resume();

					// 先去下载一次多媒体数据
					if (m_popupMultiMedia == null) {
						// 多媒体列表初始化
						m_popupMultiMedia = new MultiMediaPopup(btnMultiMedia.getRootView(), getApplicationContext(), this);
						// 设置多媒体工作空间
						m_popupMultiMedia.setWorkspace(mapShow.getWorkspace(), mapShow.getBounds());
					}
				}

			}
			break;
			case R.id.locate:
			{
				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}
				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				m_StartPoint = mTencentLocation.getGPSPoint();
				if ((m_StartPoint.getX() == 0) || (m_StartPoint.getY() == 0)) {
					MyApplication.getInstance().showInfo("现在无法定位，请稍后再试！");
					break;
				}
				// 导航纠偏
//				m_StartPoint = m_Navigation.encryptGPS(m_StartPoint.getX(), m_StartPoint.getY());

				Point2D pt = mapShow.getPoint(m_StartPoint);
				if (pt == null) {
					MyApplication.getInstance().showInfo("现在无法定位，请稍后再试！");
					break;
				}
				drawCircleOnDyn(pt, 0, mTencentLocation.getAccuracy());
				mapShow.panTo(pt);
				m_MapView.refresh();
			}
			break;
			case R.id.multi_media:
			{
				mMapControl.submit();
				mMapControl.setAction(Action.SELECT);
				mMapControl.getMap().refresh();

				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				showMultiMediaPopup(v);
				break;
			}
			case R.id.plot:
			{
				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}

				mMapControl.submit();
				mMapControl.setAction(Action.SELECT);
				mMapControl.getMap().refresh();

				if(m_PlotTypePopup == null){
					m_PlotTypePopup = new PlotTypePopup(mMapControl, btnPlot.getRootView(), this);
				}
				if(m_PlotTypePopup.isShowing()){
					m_PlotTypePopup.dismiss();
					mMapControl.setAction(Action.SELECT);
				}else{
					m_PlotTypePopup.show(v);
				}

				break;
			}
			case R.id.text_message:
			{
				mMapControl.submit();
				mMapControl.setAction(Action.SELECT);
				mMapControl.getMap().refresh();

				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}
				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				if (!mMessageQueueOn) {
					MyApplication.getInstance().showInfo("请先打开消息开关！");
					break;
				}
				if(m_popupTextMessage == null){
					m_popupTextMessage = new TextMessagePopup(btnTextMessage.getRootView(), getApplicationContext(), this);
				}

				if(m_popupTextMessage.isShowing()){
					m_popupTextMessage.dismiss();
				}else{
					m_popupTextMessage.setFocusable(true);
					m_popupTextMessage.show();
				}
			}
			break;
			case R.id.send_message:
			{
				mMapControl.submit();
				mMapControl.setAction(Action.SELECT);
				mMapControl.getMap().refresh();

				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}
				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				if (!mMessageQueueOn) {
					MyApplication.getInstance().showInfo("请先打开消息开关！");
					break;
				}
				sendPlot();
			}
			break;
			case R.id.position_upload:
			{
				if (m_popupMultiMedia != null && m_popupMultiMedia.isShowing()) {
					m_popupMultiMedia.dismiss();
				}
				if (m_PlotTypePopup != null && m_PlotTypePopup.isShowing()) {
					m_PlotTypePopup.dismiss();
				}
				if (!mMessageQueueOn) {
					MyApplication.getInstance().showInfo("请先打开消息开关！");
					break;
				}
				m_StartPoint = mTencentLocation.getGPSPoint();
				if ((m_StartPoint.getX() == 0) || (m_StartPoint.getY() == 0)) {
					MyApplication.getInstance().showInfo("现在无法定位，请稍后再试！");
					break;
				}
				// 导航纠偏
//				m_StartPoint = m_Navigation.encryptGPS(m_StartPoint.getX(), m_StartPoint.getY());

				String json = m_StartPoint.toJson();
				String msg = "{content_type=0}"+json;
				boolean bOk = m_MessageQueue.sendMessageByType(msg, 0);
				if (bOk) {
					MyApplication.getInstance().showInfo("发送成功");
				} else {
					MyApplication.getInstance().showInfo("发送失败，请重新发送！");
				}
			}
			break;
			case R.id.delete:
			{
				if (m_layerSelected == null || m_IDSelected == -1) {
					break;
				}
				DatasetVector dataset = (DatasetVector)m_layerSelected.getDataset();
				if (dataset != null && dataset.getName().equalsIgnoreCase("CAD")) {
					QueryParameter parameter = new QueryParameter();
					parameter.setAttributeFilter("SmID=" + m_IDSelected);
					parameter.setCursorType(CursorType.DYNAMIC);
					Recordset recordset = dataset.query(parameter);
					recordset.moveFirst();
					if(!recordset.isEmpty()) {
						String strPlotID = (String)recordset.getFieldValue("PlotID");
						recordset.delete();
						recordset.update();
						String msg = "{content_type=5}";
						msg += "{delete:feildName=PlotID,feildValue=";
						msg += strPlotID;
						msg += "}";
						m_MessageQueue.sendMessageByRoutingKey(msg, "plot");
					}
					recordset.close();
					recordset.dispose();
				}
				mMapControl.getMap().refresh();
				mMapControl.setAction(Action.SELECT);
				m_IDSelected = -1;
				btnDelete.setVisibility(View.INVISIBLE);
				break;
			}
			case R.id.clear:
			{
				// 清空屏幕
				clearScreen();
				break;
			}
			default:
			{
//			mMapControl.getMap().refresh();

			}
		}

	}

	public long getLibIDJB(){
		return mLIBIDJB;
	}

	public long getLibIDTY(){
		return mLIBIDTY;
	}

	private void showMessageList(View parent) {
		if (m_popupMessageList == null) {
			// 消息列表初始化
			m_popupMessageList = new MessageListPopup(btnConsultMessage.getRootView(), getApplicationContext(), this);
		}

		m_popupMessageList.refreshList(m_MessageQueue.getMessages(), m_MessageQueue.getClientIds());
		m_popupMessageList.setFocusable(true);
		m_popupMessageList.show();
	}

	private void showMultiMediaPopup(View parent) {
		if (m_popupMultiMedia == null) {
			// 多媒体列表初始化
			m_popupMultiMedia = new MultiMediaPopup(btnMultiMedia.getRootView(), getApplicationContext(), this);
			// 设置多媒体工作空间
			m_popupMultiMedia.setWorkspace(mapShow.getWorkspace(), mapShow.getBounds());
		}

		m_GPSData = mTencentLocation.getGPSData();
//		Point2D pnt = m_Navigation.encryptGPS(m_GPSData.dLongitude,m_GPSData.dLatitude);
//		m_GPSData.dLongitude = pnt.getX();
//		m_GPSData.dLatitude = pnt.getY();

		m_popupMultiMedia.setGPSData(m_GPSData);

//		m_popupMultiMedia.refreshList();
		if(m_popupMultiMedia.isShowing()){
			m_popupMultiMedia.dismiss();
		}else{
			m_popupMultiMedia.setFocusable(false);
			m_popupMultiMedia.show(parent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		m_popupMultiMedia.onActivityResult(requestCode, resultCode, data);
	}

	public void drawCircleOnDyn(Point2D point2D, float azimuth, double q){

		if (point2D.getX() == 0 || point2D.getY() == 0) {
			MyApplication.getInstance().showInfo("定位点为空");
			return ;
		}
		m_locateDynamicView.removeElement(m_LocationID);
		m_locateDynamicView.removeElement(m_LocationPolygonID);

		//构造精度范围
		if (q == 0) {
			q = 60;
		}
		GeoCircle geoCircle = new GeoCircle(point2D, q);
		GeoRegion geoRegion = geoCircle.convertToRegion(50*4);
		//绘制精度范围
		DynamicPolygon dynPolygon = new DynamicPolygon();
		dynPolygon.fromGeometry(geoRegion);
		DynamicStyle style = new DynamicStyle();
		style.setBackColor(android.graphics.Color.rgb(128, 128, 255));
		style.setLineColor(android.graphics.Color.rgb(128,255,255));//224, 224, 224

		style.setAlpha(65);//95
//		style.setSize(3.0f);//6.0f
		dynPolygon.setStyle(style);

		m_locateDynamicView.addElement(dynPolygon);
		m_LocationPolygonID = dynPolygon.getID();
		drawPoint(point2D, azimuth);
	}

	public void drawPoint(Point2D point2D, float azimuth){
		DynamicPoint dynPoint = new DynamicPoint();
		dynPoint.addPoint(point2D);

		DynamicStyle dynStyle = new DynamicStyle();
		dynStyle.setBackground(BitmapFactory.decodeResource(mapShow.getResources(), R.drawable.location));
		dynStyle.setAngle(azimuth);
		dynPoint.setStyle(dynStyle);

		m_locateDynamicView.addElement(dynPoint);
		m_LocationID = dynPoint.getID();
		m_locateDynamicView.refresh();
	}

	public void sendPlot() {
		Layer layerCAD = mMapControl.getMap().getLayers().get(0);
		Recordset record = ((DatasetVector)layerCAD.getDataset()).getRecordset(false, CursorType.DYNAMIC);
		record.moveFirst();

		int failedCount = 0;// 记录发送失败的数据个数
		while (!record.isEOF()) {
			String msg = "{content_type=2}";

			String plotID = record.getFieldValue("PlotID").toString();
			if (m_ArraySendedIDs.contains(plotID)) {
				record.moveNext();
				continue;
			}
			msg += "{PlotID=";
			msg += plotID;
			msg += "}";
			// 对象类型
			GeometryType type = record.getGeometry().getType();
			msg += "{type=";
			msg += type.value();//type.toString();
			msg += "}";

			String geoJson = record.getGeometry().toXML();
			msg += geoJson;

			boolean bOk = m_MessageQueue.sendMessageByType(msg, 2);
			if (bOk) {
				m_ArraySendedIDs.add(plotID);
				MyApplication.getInstance().showInfo("发送成功");
			} else {
				failedCount++;
			}

			record.moveNext();
		}

		record.dispose();
		if (failedCount != 0) {
			MyApplication.getInstance().showInfo("发送失败" + failedCount + "个对象，请继续发送！");
		}
	}

	@Override
	public void SynchronousLocationReceived(Point2D location, String clientID) {
		// 查询现有的动态层对象
		List<DynamicElement> m_locateDynamicPoints = m_locateDynamicView.query(mapShow.getBounds());

		boolean bExist = false;
		DynamicPoint pnt;
		for (int i =0; i < m_locateDynamicPoints.size(); i++) {
			if (m_locateDynamicPoints.get(i).getID() == m_LocationID || m_locateDynamicPoints.get(i).getID() == m_LocationPolygonID) {
				// 不与本设备定位点进行对比
				continue ;
			}
			pnt = (DynamicPoint)m_locateDynamicPoints.get(i);
			if (pnt.getUserData().equals(clientID)) {
				m_locateDynamicView.removeElement(pnt);
				pnt.updatePoint(0, mapShow.getPoint(location));
				m_locateDynamicView.addElement(pnt);
				bExist = true;
				break;
			}
		}


		if (bExist == false) {
			DynamicPoint dynPoint = new DynamicPoint();
			dynPoint.addPoint(mapShow.getPoint(location));

			DynamicStyle dynStyle = new DynamicStyle();
			dynStyle.setBackground(BitmapFactory.decodeResource(mapShow.getResources(), R.drawable.navi_end));
			dynStyle.setAngle(0);
			dynPoint.setStyle(dynStyle);
			dynPoint.setUserData(clientID);

			m_locateDynamicView.addElement(dynPoint);

		}

		m_locateDynamicView.refresh();
	}

	@Override
	public void MultiMediaInfoReceived(String msg) {
		if (m_popupMultiMedia == null) {
			// 多媒体列表初始化
			m_popupMultiMedia = new MultiMediaPopup(btnMultiMedia.getRootView(), getApplicationContext(), this);
			// 设置多媒体工作空间
			m_popupMultiMedia.setWorkspace(mapShow.getWorkspace(), mapShow.getBounds());
		}

		m_popupMultiMedia.processReceivedInfo(msg);

	}



	@Override
	public void PlotObjectsReceived(String msg) {
		// {PlotID=xxxxxxxxxx}xml
		int pos = msg.indexOf("}");
		if (pos > msg.length() || pos < 0)
			return;
		String plotID = msg.substring(1, pos);
		msg = msg.substring(pos+1);
		pos = plotID.indexOf("=");
		if (pos > msg.length() || pos < 0)
			return;
		plotID = plotID.substring(pos+1);
		if (plotID.equalsIgnoreCase("0")) {
			return ;
		}

		// 获取数据类型 {type=**}
		pos = msg.indexOf("}");
		if (pos > msg.length() || pos < 0)
			return;
		String type = msg.substring(1, pos);
		msg = msg.substring(pos+1);

		pos = type.indexOf("=");
		if (pos > type.length() || pos < 0)
			return;
		type = type.substring(pos+1);
		GeometryType geometryType = (GeometryType)Enum.parse(GeometryType.class, Integer.parseInt(type));


		Geometry geometry;
		if(geometryType == GeometryType.GEOGRAPHICOBJECT) {
			geometry = new GeoGraphicObject();
			geometry.fromXML(msg);
		} else if (geometryType == GeometryType.GEOREGION) {
			geometry = new GeoRegion();
			geometry.fromXML(msg);
		} else {
			geometry = new GeoLine();
			geometry.fromXML(msg);
		}

		Layer layerCAD = mMapControl.getMap().getLayers().get(0);
		Recordset record = ((DatasetVector)layerCAD.getDataset()).getRecordset(false, CursorType.DYNAMIC);
		record.moveFirst();

		String nodeName = "PlotID";
		try {
			boolean bHasGeometry = false;
			while(!record.isEOF()) {
				String old = record.getFieldValue(nodeName).toString();
				if (old.equalsIgnoreCase(plotID)) {
					record.edit();
					record.setGeometry(geometry);
					record.update();
					bHasGeometry = true;
					break;
				}
				record.moveNext();
			}
			if (!bHasGeometry) {
				record.edit();
				if (geometry.getType() == GeometryType.GEOLINE) {
					GeoStyle style = new GeoStyle();
					style.setLineColor(new com.supermap.data.Color(127,127,127));
					style.setFillForeColor(new com.supermap.data.Color(189,235,255));
					style.setLineWidth(1.0);
					geometry.setStyle(style);
				} else if (geometry.getType() == GeometryType.GEOREGION) {
					GeoStyle style = new GeoStyle();
					style.setLineColor(new com.supermap.data.Color(91,89,91));
					style.setFillForeColor(new com.supermap.data.Color(189,235,255));
					style.setLineWidth(1.0);
					geometry.setStyle(style);
				}

				record.addNew(geometry);
				record.setFieldValue(nodeName, plotID);
				record.update();

				m_ArraySendedIDs.add(plotID);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		record.close();
		record.dispose();
		mMapControl.getMap().refresh();
	}

	public void NewMessageReceived() {
		m_handler.sendEmptyMessage(RECEIVEMSG);
	}


	public void clearLists() {
		if (m_ArraySendedIDs != null) {
			for (int index = 0; index < m_ArraySendedIDs.size(); index++) {
				m_ArraySendedIDs.remove(index);
			}
		}
	}

	public Recordset HitTest(DatasetVector datasetVector, float x, float y){
		final float tolerance = dip2px(10.0f);

		//地理范围和手机屏幕的坐标是相反的
		Point lb = new Point((int)(x-tolerance),(int)(y+tolerance));
		Point rt = new Point((int)(x+tolerance),(int)(y-tolerance));
		Point2D leftBottom = mMapControl.getMap().pixelToMap(lb);
		Point2D rightTop = mMapControl.getMap().pixelToMap(rt);

		//投影转换
		leftBottom = mapShow.convertMapToDatasetPoint(leftBottom, datasetVector.getPrjCoordSys().getType());
		rightTop = mapShow.convertMapToDatasetPoint(rightTop, datasetVector.getPrjCoordSys().getType());

		//构造屏幕当前点10像素范围的查询Bounds
		Rectangle2D queryBounds = new Rectangle2D(leftBottom,rightTop);

		QueryParameter param = new QueryParameter();
		param.setAttributeFilter("");
		param.setSpatialQueryObject(queryBounds);
		param.setSpatialQueryMode(SpatialQueryMode.INTERSECT);
		param.setCursorType(CursorType.DYNAMIC);
		//空间和属性联合查询
		Recordset recordset = datasetVector.query(param);

		return recordset;
	}

	private int dip2px(float dipValue){
		final float scale = mMapControl.getContext().getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	private void GeometrySelected(Recordset recordset, boolean bCAD) {
		if (recordset == null || recordset.getRecordCount() == 0) {
			return;
		}
		if (bCAD) {
			boolean bFrom = mapShow.getMap().getLayers().get("CAD@multimedia").getSelection().fromRecordset(recordset);
			System.out.println(bFrom);
		} else {
			mapShow.getMap().getLayers().get("MQDemo_MediaDataset@multimedia").getSelection().fromRecordset(recordset);
//			String mediaFilePath = MyApplication.SDCARD + mapShow.getWorkspace().getDatasources().get("multimedia").getDatasets().get("MQDemo_MediaDataset").getDescription();
//			String mediaFileName = (String)recordset.getFieldValue("MediaFileName");
//			mediaFilePath += mediaFileName;
//			String strExtension;
//			int pos = mediaFileName.indexOf(".");
//			strExtension = mediaFileName.substring(pos + 1);
//
////			mediaFilePath = "/storage/emulated/0/SuperMap/MediaFiles/1440488164716.jpeg";
////			strExtension = "jpeg";
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			Uri uri = Uri.fromFile(new File(mediaFilePath));
//
//			if(strExtension.equals("jpeg")){
//				intent.setDataAndType(uri, "image/*");
//			}else if(strExtension.equals("mp4")){
//				intent.setDataAndType(uri, "video/*");
//			}else {
//				intent.setDataAndType(uri, "audio/*");
//			}
//
//			getApplicationContext().startActivity(intent);
		}
	}

	private void initGestureDetecor() {
		GestureDetector gsDetector = new GestureDetector(mMapControl.getContext(), new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				//当前屏幕位置
//				eventX = e.getX();
//				eventY = e.getY();
//				if (mapShow.getMap().getLayers().get("CAD@multimedia").getSelection() != null) {
//					mapShow.getMap().getLayers().get("CAD@multimedia").getSelection().clear();
//				}
//				if (mapShow.getMap().getLayers().get("MQDemo_MediaDataset@multimedia").getSelection() != null) {
//					mapShow.getMap().getLayers().get("MQDemo_MediaDataset@multimedia").getSelection().clear();
//				}
				m_IDSelected = -1;
				btnDelete.setVisibility(View.INVISIBLE);
//				mapShow.getMap().refresh();
				return true;

//				if(datasetVectorCAD == null){
//					datasetVectorCAD = (DatasetVector)mapShow.getWorkspace().getDatasources().get("multimedia").getDatasets().get("CAD");
//				}
//				Recordset recordset = HitTest(datasetVectorCAD, e.getX(), e.getY());
//				if(recordset == null || recordset.getRecordCount() < 1){//单击没有选中
//					if(datasetVectorMultiMedia == null){
//						datasetVectorMultiMedia = (DatasetVector)mapShow.getWorkspace().getDatasources().get("multimedia").getDatasets().get("MQDemo_MediaDataset");
//					}
//					recordset.close();
//					recordset.dispose();
//					recordset = HitTest(datasetVectorMultiMedia, e.getX(), e.getY());
//
//					GeometrySelected(recordset, false);
//				}
//				else{//单击选中
//					GeometrySelected(recordset, true);
//				}
//
//				mapShow.getMap().refresh();
//				return true;
			}

			@Override
			public void onShowPress(MotionEvent arg0) {

			}

			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
									float arg3) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
//				super.onLongPress(e);
			}

			@Override
			public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
								   float arg3) {
				return false;
			}

			@Override
			public boolean onDown(MotionEvent arg0) {
				return false;
			}
		});

		mMapControl.setGestureDetector(gsDetector);

	}

	private void initMapControl() {
		mMapControl.addGeometryAddedListener(new GeometryAddedListener() {

			@Override
			public void geometryAdded(GeometryEvent arg0) {
				int id = arg0.getID();
				Calendar cal = Calendar.getInstance();
				String strDataName = new String(String.valueOf(cal.getTimeInMillis()));
				Recordset record = ((DatasetVector)mMapControl.getMap().getLayers().get(0).getDataset()).getRecordset(false, CursorType.DYNAMIC);

				boolean bOk = record.seekID(id);
				record.edit();
				bOk = record.setFieldValue("PlotID", strDataName);
				record.update();
				record.close();
				record.dispose();
			}
		});

		mMapControl.setRefreshListener(new RefreshListener() {

			@Override
			public void mapRefresh() {
				m_locateDynamicView.refresh();
			}
		});

		mMapControl.addGeometrySelectedListener(new GeometrySelectedListener() {

			@Override
			public void geometrySelected(GeometrySelectedEvent event) {
				m_IDSelected = event.getGeometryID();
				m_layerSelected = event.getLayer();

				if (m_layerSelected.isEditable()) {
					btnDelete.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void geometryMultiSelectedCount(int i) {

			}
		});

	}

	private void messageChange() {
		//主线程更新UI
		m_nMessageCount++;
		btnNewMessage.setText(m_nMessageCount+"");
//		btnNewMessage.setColor(Color.WHITE);
		btnNewMessage.setVisibility(View.VISIBLE);
	}

	@Override
	public void DataDistributionReceived(String msg) {

	}

	@Override
	public void DeleteOrderReceived(String msg) {
		// {delete:fieldName=PlotID,fieldValue=xxxxxxx}
		int pos = msg.indexOf(":");
		if (pos > msg.length() || pos < 0)
			return;
		String order = msg.substring(1, pos);
		msg = msg.substring(pos+1);

		if (!order.equalsIgnoreCase("delete")) {
			return ;
		}
		pos = msg.indexOf(",");
		if (pos > msg.length() || pos < 0)
			return;

		// fileName为fieldName=PlotID
		String fieldName = msg.substring(0, pos);
		msg = msg.substring(pos+1);

		pos = fieldName.indexOf("=");
		if (pos > fieldName.length() || pos < 0)
			return;
		fieldName = fieldName.substring(pos+1);
		if (!fieldName.equalsIgnoreCase("PlotID")) {
			return ;
		}

		// fileValue为fieldValue=xxxxxxx}
		String fieldValue = msg.substring(0, msg.length()-1);

		pos = fieldValue.indexOf("=");
		if (pos > fieldValue.length() || pos < 0)
			return;
		fieldValue = fieldValue.substring(pos+1, msg.length() - 1);

		// 查处所有数据集对象，依次遍历找到PlotID值为fieldValue的对象，并删除？？？？
		DatasetVector dv = (DatasetVector)(mapShow.getMap().getWorkspace().getDatasources().get("multimedia").getDatasets().get("CAD"));
		Recordset recordset = dv.getRecordset(false, CursorType.DYNAMIC);
		recordset.moveFirst();
		while (!recordset.isEOF()) {
			String value = (String)recordset.getFieldValue(fieldName);
			if (value.equalsIgnoreCase(fieldValue)) {
				recordset.delete();
				recordset.update();
				break;
			}
			recordset.moveNext();
		}
		recordset.close();
		recordset.dispose();
	}

	private void clearScreen() {
		if (m_popupMultiMedia != null) {
			m_popupMultiMedia.clearMultiMedia();
		}


		//获取当前编辑图层
		Layer layer = mMapControl.getMap().getLayers().get(0);
		//由图层获取关联数据集的记录集
		Recordset rc = ((DatasetVector) layer.getDataset()).getRecordset(false, CursorType.DYNAMIC);
		//编辑删除操作
		rc.deleteAll();
		//更新记录集
		rc.update();


		// 清空动态层
		m_locateDynamicView.clear();

		mMapControl.getMap().refresh();
		clearLists();
	}
}
