package com.supermap.dnavi;

import com.example.a3dnavi.R;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Point3D;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.indoor.FloorListView;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation3;
import com.supermap.navi.Navigation3D;
import com.supermap.realspace.PixelToGlobeMode;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import android.content.Context;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class NaviManager implements OnClickListener{

	private SceneControl mSceneControl = null;
	private Scene mScene;
	private Navigation3D mNavigation3D = null;

	private MapView mMapView = null;
	private MapControl mMapControl = null;

	private Navigation3 mNavigation3 = null;
	private FloorListView mFloorListView = null;

	private View mContentView = null;
	private Context mContext;

	//操作过程中的状态改变
	private boolean bGuideEnable = false;
	private boolean bEndPointEnable = false;
	private boolean bStartPointEnable = false;
	private boolean bViaPointEnable = false;
	//	private boolean bAnalystEnable = false;
	private boolean bLongPressEnable = false;
	private boolean bIs2Dmap = false;

	public void initNavi3D(SceneControl sceneControl, Navigation3D navigation3D) {
		mSceneControl = sceneControl;
		mScene = mSceneControl.getScene();
		mContext = mSceneControl.getContext();
		mSceneControl.setGestureDetector(new GestureDetector(mContext, mSceneGestrueListener));

		mNavigation3D = navigation3D;
		mNavigation3D.setDatasource(mScene.getWorkspace().getDatasources().get("kaide_mall"));
		mNavigation3D.setSceneControl(sceneControl);

		initNavi3D();
	}

	public void initNaviIndoor(MapView mapView) {
		mMapView = mapView;
		mMapControl = mMapView.getMapControl();
		mMapControl.setGestureDetector(new GestureDetector(mMapControl.getContext(), mMapGestrueListener));
		mNavigation3 = mMapControl.getNavigation3();
		mContext = mMapControl.getContext();

		setStyle();
	}

	public void setView(View view ){
		mContentView = view;
		mContentView.findViewById(R.id.btn_analyse_path).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_end_point).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_start_point).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_way_point).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_navi).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_clear).setOnClickListener(this);
	}

	public void changeTo2D(boolean value) {
		bIs2Dmap = value;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.btn_analyse_path:
				analyse();
				break;
			case R.id.btn_end_point:
				bEndPointEnable = true;
				bLongPressEnable = true;
				Toast.makeText(mContext, "长按设置终点", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_start_point:
				bStartPointEnable = true;
				bLongPressEnable = true;
				Toast.makeText(mContext, "长按设置起点", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_navi:
				startNavi();
				break;
			case R.id.btn_way_point:
				bViaPointEnable = true;
				bLongPressEnable = true;
				Toast.makeText(mContext, "长按设置途经点", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_clear:
				mNavigation3D.cleanPath();
				mNavigation3.cleanPath();
				break;
			default:
				break;
		}
	}

	private void analyse() {
		analyseIndoor();
		analyse3D();
	}

	// 开始导航
	private void startNavi(){
		if (bIs2Dmap) {
			startNaviIndoor(1);
		} else {
			startNavi3D();
		}
	}

	private void initNavi3D(){
		mNavigation3D.addNaviInfoListener(new NaviListener() {

			@Override
			public void onStopNavi() {
				// TODO Auto-generated method stub
				mContentView.setVisibility(View.VISIBLE);
				mNavigation3D.cleanPath();
				mNavigation3.cleanPath();
				System.out.println("导航停止");
			}

			@Override
			public void onStartNavi() {
				// TODO Auto-generated method stub
				mContentView.setVisibility(View.INVISIBLE);
				System.out.println("导航启动");
			}

			@Override
			public void onPlayNaviMessage(String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNaviInfoUpdate(NaviInfo naviInfo) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAdjustFailure() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAarrivedDestination() {
				// TODO Auto-generated method stub
				mContentView.setVisibility(View.VISIBLE);
				mNavigation3D.cleanPath();
				mNavigation3.cleanPath();
				System.out.println("导航到达目的地");
			}
		});
	}

	//接收长按事件
	private GestureDetector.SimpleOnGestureListener mSceneGestrueListener = new SimpleOnGestureListener(){
		public void onLongPress(MotionEvent e) {
			if(!bLongPressEnable){
				return;
			}
			int x = (int) e.getX();
			int y = (int) e.getY();
			Point pt = new Point(x, y);
			Point3D pt3d = mScene.pixelToGlobe(pt, PixelToGlobeMode.TERRAINANDMODEL);

			//添加第一个点
			if(bStartPointEnable){
				mNavigation3D.setStartPoint(pt3d.getX(), pt3d.getY(), pt3d.getZ());

				String fl_id = getFloorID(pt3d.getZ());
				mNavigation3.setStartPoint(pt3d.getX(), pt3d.getY(), fl_id);

				bStartPointEnable = false;
				bLongPressEnable = false;
				return;
			}

			if(bEndPointEnable){
				mNavigation3D.setDestinationPoint(pt3d.getX(), pt3d.getY(), pt3d.getZ());

				String fl_id = getFloorID(pt3d.getZ());
				mNavigation3.setDestinationPoint(pt3d.getX(), pt3d.getY(), fl_id);

//				bAnalystEnable = true;
				bEndPointEnable = false;
				bLongPressEnable = false;
			}

			if(bViaPointEnable){
				mNavigation3D.addWayPoint(pt3d.getX(), pt3d.getY(), pt3d.getZ());

				String fl_id = getFloorID(pt3d.getZ());
				mNavigation3.addWayPoint(pt3d.getX(), pt3d.getY(), fl_id);

//				bAnalystEnable = true;
				bViaPointEnable = false;
				bLongPressEnable = false;
			}
		};
	};

	private void analyse3D() {
//		if(!bAnalystEnable){
//			Toast.makeText(mContext, "请先设置起点和终点!", Toast.LENGTH_SHORT).show();
//			return;
//		}

		boolean result = mNavigation3D.routeAnalyst();

		if(result){
			Toast.makeText(mContext, "分析成功", Toast.LENGTH_SHORT).show();
			bGuideEnable = true;
		} else {
			Toast.makeText(mContext, "分析失败", Toast.LENGTH_SHORT).show();
		}

		return;
	}

	private void startNavi3D(){
		if (!bGuideEnable) {
			Toast.makeText(mContext, "先进行路径规划", Toast.LENGTH_SHORT).show();
		}

		mNavigation3D.startGuide(1);
	}

	public void setFloorListView(FloorListView floorListView){
		mFloorListView = floorListView;

		mNavigation3.addNaviInfoListener(new NaviListener() {

			@Override
			public void onStopNavi() {
				// TODO Auto-generated method stub
				mContentView.setVisibility(View.VISIBLE);
				mNavigation3.cleanPath();
				mNavigation3D.cleanPath();
				System.out.println("导航停止");
			}

			@Override
			public void onStartNavi() {
				// TODO Auto-generated method stub
				mContentView.setVisibility(View.INVISIBLE);
				System.out.println("导航启动");
			}

			@Override
			public void onPlayNaviMessage(String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNaviInfoUpdate(NaviInfo naviInfo) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAdjustFailure() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAarrivedDestination() {
				// TODO Auto-generated method stub
				mContentView.setVisibility(View.VISIBLE);
				mNavigation3.cleanPath();
				mNavigation3D.cleanPath();
				System.out.println("导航到达目的地");
			}
		});
	}

	//接收长按事件
	private GestureDetector.SimpleOnGestureListener mMapGestrueListener = new SimpleOnGestureListener(){
		public void onLongPress(MotionEvent e) {
			if(!bLongPressEnable){
				return;
			}
			int x = (int) e.getX();
			int y = (int) e.getY();
			Point2D pt = mMapControl.getMap().pixelToMap(new com.supermap.data.Point(x, y));

			//当投影不是经纬坐标系时，则对起始点进行投影转换
			if(mMapControl.getMap().getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
				Point2Ds points = new Point2Ds();
				points.add(pt);
				PrjCoordSys desPrjCoorSys = new PrjCoordSys();
				desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				CoordSysTranslator.convert(points, mMapControl.getMap().getPrjCoordSys(), desPrjCoorSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
				pt = points.getItem(0);
			}

			//添加第一个点
			if(bStartPointEnable){
				setFromPosition(pt);

				bStartPointEnable = false;
				bLongPressEnable = false;

				return;
			}
			if(bEndPointEnable){
				setToPosition(pt);
				bEndPointEnable = false;
				bLongPressEnable = false;
			}

			if(bViaPointEnable){
				setWayPosition(pt);
				bViaPointEnable = false;
				bLongPressEnable = false;
			}
		};
	};

	private void setFromPosition(Point2D pt) {
		String mCurrentFloorID = mFloorListView.getCurrentFloorId();

		if (mCurrentFloorID == null) {
			Toast.makeText(mMapControl.getContext(), "请先打开室内地图", Toast.LENGTH_SHORT).show();
			return;
		}
		mNavigation3.setStartPoint(pt.getX(), pt.getY(), mCurrentFloorID);

		//设置三维起点
		double height = getFloorHeight(mCurrentFloorID);
		mNavigation3D.setStartPoint(pt.getX(), pt.getY(), height);
	}

	private void setToPosition(Point2D pt) {
		String mCurrentFloorID = mFloorListView.getCurrentFloorId();

		if (mCurrentFloorID == null) {
			Toast.makeText(mMapControl.getContext(), "请先打开室内地图", Toast.LENGTH_SHORT).show();
			return;
		}

		mNavigation3.setDestinationPoint(pt.getX(), pt.getY(), mCurrentFloorID);

		//设置三维终点
		double height = getFloorHeight(mCurrentFloorID);
		mNavigation3D.setDestinationPoint(pt.getX(), pt.getY(), height);
	}

	private void setWayPosition(Point2D pt) {
		String mCurrentFloorID = mFloorListView.getCurrentFloorId();

		if (mCurrentFloorID == null) {
			Toast.makeText(mMapControl.getContext(), "请先打开室内地图", Toast.LENGTH_SHORT).show();
			return;
		}

		mNavigation3.addWayPoint(pt.getX(), pt.getY(), mCurrentFloorID);

		//设置三维途经点
		double height = getFloorHeight(mCurrentFloorID);
		mNavigation3D.addWayPoint(pt.getX(), pt.getY(), height);
	}

	private void analyseIndoor() {
		Datasource datasource = mMapControl.getMap().getWorkspace().getDatasources().get("kaide_mall");

		if (datasource == null) {
			Toast.makeText(mMapControl.getContext(), "室内地图数据源加载失败", Toast.LENGTH_SHORT).show();
			return;
		}

		mNavigation3.setDatasource(datasource);

		boolean result = mNavigation3.routeAnalyst();
		if (result) {
			Toast.makeText(mMapControl.getContext(), "分析成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mMapControl.getContext(), "分析失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void startNaviIndoor(int status) {
		if(!mNavigation3.startGuide(status)){
			System.out.println("导航启动失败");
			return;
		}
	}

	private void setStyle(){
		GeoStyle style = new GeoStyle();

		if (Environment.isOpenGLMode()) {
			style.setLineSymbolID(964882);
		} else {
			style.setLineSymbolID(964883);
		}

		mNavigation3.setRouteStyle(style);

		GeoStyle styleHint = new GeoStyle();
		styleHint.setLineWidth(2);
//		styleHint.setLineColor(new com.supermap.data.Color(198, 225, 253));
		styleHint.setLineColor(new com.supermap.data.Color(82, 198, 223));
		styleHint.setLineSymbolID(2);

		mNavigation3.setHintRouteStyle(styleHint);
	}


	private double getFloorHeight(String id) {
		double height = 0;

		Datasource datasource = mScene.getWorkspace().getDatasources().get("kaide_mall");
		DatasetVector dvt = (DatasetVector) datasource.getDatasets().get("FloorRelationTable");
		String sql = "FL_ID = '" + id + "'";;
		Recordset recordset = dvt.query(sql, CursorType.STATIC);

		if (recordset.getRecordCount() > 0) {
			height = recordset.getDouble("Height");
		}

		recordset.dispose();
		recordset = null;
		return height;
	}

	private String getFloorID(double z) {
		String id = "";
		double min = -1;

		Datasource datasource = mScene.getWorkspace().getDatasources().get("kaide_mall");
		DatasetVector dvt = (DatasetVector) datasource.getDatasets().get("FloorRelationTable");
		Recordset recordset = dvt.getRecordset(false, CursorType.STATIC);

		while (!recordset.isEOF()) {
			double height = recordset.getDouble("Height");
			double offset = Math.abs(height - z);
			if (min < 0 || offset < min) {
				min = offset;
				id = recordset.getString("FL_ID");
			}
			recordset.moveNext();
		}

		recordset.dispose();
		recordset = null;

		return id;
	}
}
