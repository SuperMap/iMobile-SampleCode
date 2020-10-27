package com.supermap.carsmonitordemo.monitors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.supermap.carsmonitordemo.R;
import com.supermap.carsmonitordemo.communication.CarData;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.Datasources;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometryAddedListener;
import com.supermap.mapping.GeometryEvent;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mapping.dyn.DynamicElement;
import com.supermap.mapping.dyn.DynamicLine;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicPolygon;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.mapping.dyn.TranslateAnimator;
import com.supermap.mapping.dyn.ZoomAnimator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

/**
 * 显示管理，所有的监控最终通过显示管理将结果显示出来
 * @author Congle
 *
 */
public class DisplayManager {

	private DomainMonitor mDomainMonitor = null;
	private PathMonitor mPathMonitor = null;
	private StatusMonitor mStatusMonitor = null;

	private HashMap<String, Integer> mCarsData = new HashMap<String, Integer>();
	private HashMap<Integer, Bitmap> mBitmaps = new HashMap<Integer, Bitmap>();
	private DynamicView mDynView = null;
	private Bitmap  res = null;
	private MapView mMapView = null;
	private Context mContext = null;
	private ArrayList<String> carStateList = null;
	private GeometryAddedListener mGeometryAddedListener = null;

	private PopupWindow pwDetailInfo;
	View m_DetailLayout;
	private TextView mTxtCarName;
	private TextView mTxtCarNo;
	private TextView mTxtPhoneNo;
	private TextView mTxtX;
	private TextView mTxtY;
	private Switch mSwitchTrack;
	private Layer editLayer = null;

	/**
	 * 构造函数
	 * @param mapview   地图显示空件
	 */
	public DisplayManager(MapView mapview){
		mMapView = mapview;
		mDomainMonitor = new DomainMonitor(this);
		mPathMonitor = new PathMonitor(this);
		mStatusMonitor = new StatusMonitor(this);

		mDynView = new DynamicView(mapview.getContext(), mapview.getMapControl().getMap());
		mapview.addDynamicView(mDynView);

		mContext = mMapView.getContext();
		carStateList = new ArrayList<String> ();
		carStateList.add("");
		carStateList.add("正常");
		carStateList.add("异常");
		carStateList.add("超速");

		res = BitmapFactory.decodeResource(mDynView.getResources(), R.drawable.car0);

		m_DetailLayout = View.inflate(mContext, R.layout.detailinfo, null);
		pwDetailInfo = new PopupWindow(m_DetailLayout,dip2px(255),dip2px(225));

		mTxtCarName = (TextView)m_DetailLayout.findViewById(R.id.txt_CarName);
		mTxtCarNo = (TextView)m_DetailLayout.findViewById(R.id.txt_CarNo);
		mTxtPhoneNo = (TextView)m_DetailLayout.findViewById(R.id.txt_PhoneNo);
		mTxtX = (TextView)m_DetailLayout.findViewById(R.id.txt_x);
		mTxtY = (TextView)m_DetailLayout.findViewById(R.id.txt_y);

		mSwitchTrack = (Switch)m_DetailLayout.findViewById(R.id.switch_cartrack);
		mSwitchTrack.setOnCheckedChangeListener(m_checkedChangeListener);

		mGeometryAddedListener = new GeometryAddedListener() {

			@Override
			public void geometryAdded(GeometryEvent event) {
				// TODO Auto-generated method stub
				//mDomainMonitor.drawMonitorDomain(event);

				mDomainMonitor.monitorDomainList(event);

//				mMapView.getMapControl().removeGeometryAddedListener(mGeometryAddedListener);
				//弄完后要恢复平移
				mMapView.getMapControl().setAction(Action.PAN);
				editLayer.setEditable(false);

				mMapView.getMapControl().getMap().refresh();
			}
		};

		mBitmaps.put(Color.WHITE, createCarBackgroud(Color.WHITE, carStateList.get(0)));
		mBitmaps.put(Color.GREEN, createCarBackgroud(Color.GREEN, carStateList.get(1)));
		mBitmaps.put(Color.RED, createCarBackgroud(Color.RED, carStateList.get(2)));
		mBitmaps.put(Color.YELLOW, createCarBackgroud(Color.YELLOW, carStateList.get(3)));
	}

	@Override
	protected void finalize( )
	{
		//清除创建的位图
		res.recycle();
		mBitmaps.get(Color.WHITE).recycle();
		mBitmaps.get(Color.GREEN).recycle();
		mBitmaps.get(Color.RED).recycle();
		mBitmaps.get(Color.YELLOW).recycle();
		mBitmaps.clear();
	}

	/**
	 * 清空可编辑图层
	 */
	public void clearEditLayer() {
		if(editLayer == null)
		{
			drawRegion();                        // 异常关闭程序后，再启动时执行，以便清除可能存在 监控区
			mMapView.getMapControl().submit();
			mMapView.getMapControl().setAction(Action.PAN);
		}
		DatasetVector datasetVector = (DatasetVector) editLayer.getDataset();
		Recordset     recordset     = (Recordset) datasetVector.getRecordset(false, CursorType.DYNAMIC);
		if(recordset != null){
			recordset.deleteAll();
			recordset.update();
		}
	}

	/**
	 * 删除指定id的动态层对象
	 * @param id
	 */
	public void deleteElment(int id){

		mDynView.removeElement(id);
	}

	/**
	 * 增加动态层对象
	 * @param element
	 */
	public void addElment(DynamicElement element){
		mDynView.addElement(element);
		mDynView.refresh();
	}

	/**
	 * 更新轨迹
	 * @param id
	 * @param pt
	 */
	public void updateLine(int id,Point2D pt){
		DynamicLine line = (DynamicLine) mDynView.query(id);
		if(line!= null){
			Point2Ds linePts = new Point2Ds(new Point2D[]{pt});
			CoordSysTranslator.forward(linePts, mMapView.getMapControl().getMap().getPrjCoordSys());
			Point2D newPoint = new Point2D(linePts.getItem(0).getX(), linePts.getItem(0).getY());
			line.addPoint(newPoint);
			mDynView.refresh();
		}
	}

	/**
	 * 动态层添加轨迹线
	 * @param pts
	 * @return
	 */
	public int addLine(Point2Ds pts){
		Point2Ds linePts = pts.clone();
		CoordSysTranslator.forward(linePts, mMapView.getMapControl().getMap().getPrjCoordSys());
		DynamicLine line = new DynamicLine();
		int ptCount = linePts.getCount();
		int i=0;
		for(;i<ptCount;i++){
			Point2D pt = linePts.getItem(i);
			line.addPoint(pt);
		}
		DynamicStyle style = new DynamicStyle();
		style.setLineColor(Color.argb(255, 0,255,0));
		style.setSize(dip2px(3));
		line.setStyle(style);
		mDynView.addElement(line);

		mDynView.refresh();
		return line.getID();
	}

	/**
	 * 移除指定id的轨迹
	 * @param id
	 */
	public void removeLine(int id){
		mDynView.removeElement(id);
	}

	/**
	 * 跟新动态层对象风格
	 * @param carData
	 * @param backgroundColor
	 */
	public void updateElementStyle(CarData carData,int backgroundColor){
		DynamicPoint pt = queryCar(carData);
		if(pt == null )
			return;
//		int state = carData.getState();
//		String carState = carStateList.get(carData.getState());
//
//		Bitmap oldBack = pt.getStyle().getBackground();
//		Bitmap newBack = createCarBackgroud(backgroundColor, carState);
//
////		oldBack.recycle();
////		oldBack = null;
//
//		pt.getStyle().setBackground(newBack);

		pt.getStyle().setBackground(mBitmaps.get(backgroundColor));
	}

	/**
	 * 创建车辆背景
	 * @param backgroud    背景资源id
	 * @param carState     车辆状态
	 * @return
	 */
	public Bitmap createCarBackgroud(int backgroud,String carState){
		int width = dip2px(245);
		int height = dip2px(85);
		Bitmap car = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		Canvas drawCanvas  = new Canvas(car);
		drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		//画车
		int centerX = width/2;
		int centerY = height/2;
		int resWidth = res.getWidth();
		int resHeight = res.getHeight();
		int resCenterX = resWidth/2;
		int resCenterY = resHeight/2;
		drawCanvas.drawBitmap(res, centerX-resCenterX, centerY-resCenterY, null);


		//绘制圆角的气泡框
		Paint paint = new Paint();
		paint.setColor(Color.argb(255, 115, 196, 30));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(3.5f);
		paint.setAntiAlias(true);
		int rectWidth = dip2px(50);
		int rectHeight = dip2px(38);
		int rectLeft = centerX+resWidth/2-dip2px(8);
		int rectRigth = rectLeft+rectWidth;
		int rectTop = centerY-rectHeight;
		int rectBottom = centerY-dip2px(13);
		RectF rect = new RectF(rectLeft, rectTop, rectRigth, rectBottom);
		drawCanvas.drawRoundRect(rect, dip2px(5), dip2px(5), paint);

		// 绘制三角形
		Path triangle = new Path();
		triangle.moveTo(rectLeft+dip2px(5), rectBottom);
		triangle.lineTo(rectLeft+dip2px(2), rectBottom+dip2px(8));
		triangle.lineTo(rectLeft+dip2px(15), rectBottom);
		triangle.close();
		paint.setStyle(Style.FILL);
		drawCanvas.drawPath(triangle, paint);

		//填充圆角矩形，作为背景色
		paint.reset();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.5f);
		paint.setColor(backgroud);
		paint.setStyle(Style.FILL);
		rect = new RectF(rectLeft+dip2px(2), rectTop+dip2px(2), rectRigth-dip2px(2), rectBottom-dip2px(2));
		drawCanvas.drawRoundRect(rect, dip2px(5), dip2px(5), paint);
		//写车牌号
		paint.reset();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setTextSize(sp2px(22));
		drawCanvas.drawText(carState,  rectLeft+dip2px(4), rectBottom-dip2px(5), paint);
		return car;
	}

	/**
	 * 获取显示尺寸参数
	 * @param dipValue
	 * @return
	 */
	public int dip2px(float dipValue){
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	/**
	 * 获取显示尺寸参数
	 * @param spVale
	 * @return
	 */
	public int sp2px(float spVale){
		final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
		return (int)(spVale * scale + 0.5f);
	}

	/**
	 * 查询车辆对应的动态对象
	 * @param cardata
	 * @return
	 */
	public DynamicPoint queryCar(CarData cardata){
		int id = -1;
		String carNo = cardata.getCarNo();

		if(mCarsData.containsKey(carNo)){
			id = (Integer) mCarsData.get(carNo);

			if(id==0)
				System.out.println();
		}else{
			DynamicPoint car = new DynamicPoint();
			Point2Ds pts = new Point2Ds(new Point2D[]{new Point2D(cardata.getX(), cardata.getY())});
			CoordSysTranslator.forward(pts, mMapView.getMapControl().getMap().getPrjCoordSys());
			Point2D newPoint2D = new Point2D(pts.getItem(0).getX(), pts.getItem(0).getY());

			car.addPoint(newPoint2D);

			DynamicStyle style = new DynamicStyle();
			style.setBackground(mBitmaps.get(Color.GREEN));

			car.setStyle(style);
			car.setUserData(cardata);
			//查看事件暂时不写
			car.setOnClickListenner(mOnClick);

			mDynView.addElement(car);
			mDynView.invalidate();

			id = car.getID();

			if(id==0)
				System.out.println();
			mCarsData.put(carNo, id);                      // 记录车辆信息，及其ID
		}
		return (DynamicPoint) mDynView.query(id);
	}

	/**
	 * 查询车辆对应的动态对象
	 * @param cardata
	 * @return
	 */
	public DynamicPolygon queryCar0(CarData cardata){
		int id = -1;
		String carNo = cardata.getCarNo();

		if(mCarsData.containsKey(carNo)){
			id = (Integer) mCarsData.get(carNo);

			if(id==0)
				System.out.println();
		}else{
			DynamicPolygon car = new DynamicPolygon();
			Point2Ds pts = new Point2Ds(new Point2D[]{new Point2D(cardata.getX(), cardata.getY())});
			CoordSysTranslator.forward(pts, mMapView.getMapControl().getMap().getPrjCoordSys());
			Point2D newPoint2D = new Point2D(pts.getItem(0).getX(), pts.getItem(0).getY());

			car.addPoint(newPoint2D);

			DynamicStyle style = new DynamicStyle();
			style.setBackground(mBitmaps.get(Color.GREEN));

			car.setStyle(style);
			car.setUserData(cardata);

			car.setOnClickListenner(mOnClick);

			mDynView.addElement(car);
			mDynView.invalidate();

			id = car.getID();

			if(id==0)
				System.out.println();
			mCarsData.put(carNo, id);                      // 记录车辆信息，及其ID
		}

		// 查询失败
		if(mDynView.query(id) == null){
		}else{

		}

		return (DynamicPolygon) mDynView.query(id);
	}

	// 动态对象点击监听
	private DynamicElement.OnClickListener mOnClick = new DynamicElement.OnClickListener() {

		@Override
		public void onClick(DynamicElement element) {
			// TODO Auto-generated method stub
			final CarData cardata = (CarData)element.getUserData();

			locateCarPosition(cardata);
			m_DetailLayout.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					pwDetailInfo.dismiss();
				}
			});

			mTxtCarName.setText(cardata.getCarName());
			mTxtCarNo.setText(cardata.getCarNo());
			mTxtPhoneNo.setText(cardata.getPhoneNo());
			mTxtX.setText(cardata.getX()+"");
			mTxtY.setText(cardata.getY()+"");
			mSwitchTrack.setChecked(mPathMonitor.isShowPath(cardata.getCarNo()));

			pwDetailInfo.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
		}
	};

	/**
	 * 获取车辆位置
	 * @param carData
	 */
	public void locateCarPosition( CarData carData) {

		// 获取经纬坐标的坐标值(x, y)
		double x = carData.getX();
		double y = carData.getY();

		Point2D point2D = new Point2D(x, y);

		// 获取地图的坐标系统
		PrjCoordSys Prj =mMapView.getMapControl().getMap().getPrjCoordSys();

		// 当投影不是经纬坐标系时，则对点进行投影转换
		if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds points = new Point2Ds();
			points.add(point2D);
			PrjCoordSys desPrjCoorSys = new PrjCoordSys();
			desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);


			boolean b1 = CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
					new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			double curScale = mMapView.getMapControl().getMap().getScale();
			if(curScale < (1 / 57373.046875)){
				mMapView.getMapControl().getMap().setCenter(points.getItem(0));
				mMapView.getMapControl().getMap().setScale(1 / 57373.046875);
			}
			mMapView.getMapControl().getMap().refresh();
		}


	}

	private OnCheckedChangeListener m_checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch(buttonView.getId()){
				case R.id.switch_cartrack:
					mPathMonitor.showPath(isChecked,mTxtCarNo.getText().toString());
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 闪烁车辆
	 * @param data
	 * @param times   闪烁次数
	 */
	public void flashing(CarData data,int times){
		DynamicElement element = queryCar(data);
		if(element == null )
			return;

		ZoomAnimator zoomin = new ZoomAnimator(1.2f, 400);
		ZoomAnimator zoomout = new ZoomAnimator(1/1.2f, 400);
		for(int i=0;i<times;i++){
			//闪烁4s
			element.addAnimator(zoomin);
			element.addAnimator(zoomout);
		}
		mDynView.startAnimation();
	}

	/**
	 * 绘制监控区域
	 *
	 */
	public void drawMonitorDomain(){
		drawRegion();
	}


	/**
	 *  Draw a region
	 * @return
	 */
	public boolean drawRegion(){
		if(editLayer != null){
			editLayer.setEditable(true);
			mMapView.getMapControl().setAction(Action.DRAWPLOYGON);
			return true;
		}

		Workspace workspace = mMapView.getMapControl().getMap().getWorkspace();
		String datasourceName = "TempUDB";

		String tempPath = Environment.getTemporaryPath() + "/TempUDB.udb";

		File dataFile = new File(tempPath);

		// 设计图层风格
		GeoStyle geoStyle_R = new GeoStyle();
		geoStyle_R.setFillForeColor(new com.supermap.data.Color(149, 23,17));
		geoStyle_R.setFillBackOpaque(true);
		geoStyle_R.setFillOpaqueRate(40);

		geoStyle_R.setLineWidth(0.1);
		geoStyle_R.setLineColor(new com.supermap.data.Color(40,40,40));
		LayerSettingVector layerSettingVector = new LayerSettingVector();
		layerSettingVector.setStyle(geoStyle_R);

		//不存在则加
		if(workspace.getDatasources().indexOf(datasourceName)==-1 || dataFile.exists() == false){

			if(workspace.getDatasources().indexOf(datasourceName)!=-1 && dataFile.exists() == false){
				workspace.getDatasources().close(datasourceName);

			}
			Datasources datasources= workspace.getDatasources();
			DatasourceConnectionInfo info = new DatasourceConnectionInfo();

			if(dataFile.exists()){
				dataFile.delete();

			}
			String tempPathUDD = Environment.getTemporaryPath() + "/TempUDB.udd";

			File dataFileUDD = new File(tempPathUDD);
			if(dataFileUDD.exists()){
				dataFileUDD.delete();

			}

			info.setServer(Environment.getTemporaryPath()+"/TempUDB.udb");
			info.setEngineType(EngineType.UDB);
			info.setAlias(datasourceName);
			Datasource udb = datasources.create(info);
			if(udb == null){
				Log.e(this.getClass().getName(),"创建UDB失败了");
				return false;
			}
			DatasetVectorInfo vecInfo = new DatasetVectorInfo();
			vecInfo.setName("TempRegion");
			vecInfo.setType(DatasetType.REGION);
			DatasetVector dataset = udb.getDatasets().create(vecInfo);
			if(dataset == null){
				Log.e(this.getClass().getName(),"创建REGION失败了");
				return false;
			}

			// 确保新增数据集只关联一个图层
			Layers layers = mMapView.getMapControl().getMap().getLayers();
			int count = layers.getCount();
			Dataset datasetLayer = null;
			ArrayList<Integer> removeIndexs = new ArrayList<Integer>();
			if(count > 1){
				for(int i=0; i<count-1; i++){
					layers.remove(0);   // 只保留最后一个图层
				}
			}

			Layer drawLayer = mMapView.getMapControl().getMap().getLayers().add(dataset, true);
			drawLayer.setEditable(true);

			String mapXML = mMapView.getMapControl().getMap().toXML();
			workspace.getMaps().setMapXML(0, mapXML);
			try {
				workspace.save();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			editLayer = drawLayer;

			editLayer.setAdditionalSetting(layerSettingVector);
		}else{
			if (mMapView.getMapControl().getMap().getLayers().getCount() == 1) {
				Datasource datasource = workspace.getDatasources().get(datasourceName);
				Dataset dataset = datasource.getDatasets().get(0);
				editLayer = mMapView.getMapControl().getMap().getLayers().add(dataset, true);
			} else {
				editLayer = mMapView.getMapControl().getMap().getLayers().get(0);
			}
			editLayer.setEditable(true);
			editLayer.setAdditionalSetting(layerSettingVector);
		}



		mMapView.getMapControl().setAction(Action.DRAWPLOYGON);
		mMapView.getMapControl().addGeometryAddedListener(mGeometryAddedListener);

		return true;
	}

	/**
	 * 根据车辆信息转换动态点
	 * @param cardata
	 */
	public void Translate(CarData cardata) {
		DynamicPoint car = queryCar(cardata);

		if(car == null){
			return;
		}

		Point2Ds pts = new Point2Ds(new Point2D[]{new Point2D(cardata.getX(), cardata.getY())});
		CoordSysTranslator.forward(pts, mMapView.getMapControl().getMap().getPrjCoordSys());
		Point2D newPoint2D = new Point2D(pts.getItem(0).getX(), pts.getItem(0).getY());
		car.addAnimator(new TranslateAnimator(newPoint2D, 200));
		mDynView.startAnimation();
	}

	/**
	 * 刷新动态层
	 */
	public void refresh(){
		mDynView.refresh();
	}

	/**
	 * 设置UI
	 * @param v
	 */
	public void attachUI(TextView v){
		mDomainMonitor.attacthWarningDisplay(v);
	}

	/**
	 * 开启监控
	 * @param cardata
	 */
	public void monitor(CarData cardata){
		mDomainMonitor.monitor(cardata);
		mPathMonitor.monitor(cardata);
		mStatusMonitor.monitor(cardata);
	}

	/**
	 * 获取监控区域对象
	 * @return
	 */
	public DomainMonitor getDomainMonitor() {
		return mDomainMonitor;
	}

	/**
	 * 获取路径监控对象
	 * @return
	 */
	public PathMonitor getPathMonitor() {
		return mPathMonitor;
	}

	/**
	 * 获取状态监控对象
	 * @return
	 */
	public StatusMonitor getStatusMonitor() {
		return mStatusMonitor;
	}

	/**
	 * 获取车辆数据
	 * @return
	 */
	public HashMap<String, Integer> getCarsData() {
		return mCarsData;
	}

	/**
	 * 获取跟踪层
	 * @return
	 */
	public TrackingLayer getTrackingLayer() {
		if(mMapView == null)
			return null;
		return mMapView.getMapControl().getMap().getTrackingLayer();
	}
}
