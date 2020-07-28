package com.supermap.onlinedemo;

import java.util.List;

import com.supermap.data.Color;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.myapplication.MyApplication;
import com.supermap.onlineservices.CoordinateConvert;
import com.supermap.onlineservices.CoordinateConvertParameter;
import com.supermap.onlineservices.CoordinateType;
import com.supermap.onlineservices.NavigationOnline;
import com.supermap.onlineservices.NavigationOnlineData;
import com.supermap.onlineservices.NavigationOnlineParameter;
import com.supermap.onlineservices.PathInfo;
import com.supermap.onlineservices.RouteType;
import com.supermap.onlineservices.CoordinateConvert.ConvertCallback;
import com.supermap.onlineservices.NavigationOnline.NavigationOnlineCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * <p>
 * Title: 路径导航Demo
 * </p>
 *
 *	<p>
 *============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码 
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *	Description: 
 *		1、范例简介：设置相关地理坐标，可进行路径导航
 *		2、范例数据：地图引擎：SuperMapCloud
 *          地图数据：超图云地图数据 http://supermapcloud.com
 *          许可目录："/SuperMap/license/"
 *      3、关键类型/成员:
 * 				    navigationOnline.setKey()										方法
 *					navigationOnline.setNavigationOnlineCallback()					方法
 *					navigationOnline.routeAnalyst()									方法

 *					parameter.setStartPoint()										方法
 *					parameter.setEndPoin()											方法
 *					parameter.setCoordinateType()									方法
 *					parameter.setRouteType()										方法
 *
 *      			CoordinateConvertParameter.setKey()								方法
 *     				CoordinateConvertParameter.setPoint2Ds()						方法
 *     				CoordinateConvertParameter.setSrcCoordinateType()				方法
 *     				CoordinateConvertParameter.setDestCoordinateType()				方法
 *     			
 *     				CoordinateConvert.convert()										方法
 *     				CoordinateConvert.setConvertCallback()							方法
 *     4、步骤：
 *     		(1) 设置起点、终点
 *     		(2) 点击搜索
 *     		(3) 跳转到新的界面，进行路线选择
 *     		(4) 选择后，可查看具体信息，点击具体信息，地图显示起点、终点的位置
 *     5、注意事项：
 *     		(1) 查找成功，在地图上未看到图标，请缩小地图
 *	</p>
 *	
 *	<p>
 *	Company: 北京超图软件股份有限公司
 *	</p>
 */
public class NavigationOnlineActivity extends Activity implements OnClickListener{
	private Workspace workspace;
	private Dataset dataset;
	private Datasource datasource;
	private MapView mapView;
	private MapControl mapControl;
	private TrackingLayer trackingLayer;
	private Button btnStart, btnEnd, btnNavigation, btnIn, btnOut, btnClear,btnEntire;
	private RouteType routeType=null;
	private	double startX=-1;
	private	double startY=-1;
	private	double endX=-1;
	private	double endY=-1;
	private	String startName="";
	private	String endName="";
	private EditText etStartX,etStartY,etStartName,etEndX,etEndY,etEndName;
	private boolean navigationFlag=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_navigation_online);
		if(openMap()){
			initView();
		}
		new Gesture(this, mapView);
	}

	private boolean openMap(){
		mapView=(MapView) findViewById(R.id.mapView_ano);
		mapControl=mapView.getMapControl();
		trackingLayer=mapControl.getMap().getTrackingLayer();

		workspace=new Workspace();

		DatasourceConnectionInfo dcInfo=new DatasourceConnectionInfo();
		dcInfo.setEngineType(EngineType.SuperMapCloud);
		dcInfo.setServer("http://t2.supermapcloud.com/");
		mapControl.getMap().setWorkspace(workspace);
		datasource=workspace.getDatasources().open(dcInfo);
		if(datasource != null){
			dataset=datasource.getDatasets().get(0);
			mapControl.getMap().getLayers().add(dataset,true);
			//设置地图初始的显示范围，地图出图时是成都
			mapControl.getMap().setScale(1/114746.09);
			mapControl.getMap().setCenter(new Point2D(11586694.1130399,3589094.2752003));
			mapControl.getMap().refresh();
			return true;
		}
		return false;
	}

	private void initView(){
		btnStart = (Button) findViewById(R.id.btnStart_ano);
		btnEnd = (Button) findViewById(R.id.btnEnd_ano);
		btnIn = (Button) findViewById(R.id.btnIn_ano);
		btnOut = (Button) findViewById(R.id.btnOut_ano);
		btnClear = (Button) findViewById(R.id.btnClear_ano);
		btnEntire = (Button) findViewById(R.id.btnEntire_ano);
		btnNavigation = (Button) findViewById(R.id.btnNavigation_ano);
		btnStart.setOnClickListener(this);
		btnEnd.setOnClickListener(this);
		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnEntire.setOnClickListener(this);
		btnNavigation.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnStart_ano:
			final LinearLayout startView=(LinearLayout) findViewById(R.id.include_ano);
			startView.setVisibility(View.VISIBLE);

			etStartX=(EditText) startView.findViewById(R.id.etX_i);
			etStartX.setText("104.068480");

			etStartY=(EditText) startView.findViewById(R.id.etY_i);
			etStartY.setText("30.537340");

			TextView tvStartAlias=(TextView) startView.findViewById(R.id.tvAlias_i);
			tvStartAlias.setVisibility(View.GONE);

			etStartName=(EditText) startView.findViewById(R.id.etName_i);
			etStartName.setVisibility(View.GONE);

			Button btnFinish=(Button) startView.findViewById(R.id.btnFinish_i);
			btnFinish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startX=Double.parseDouble(etStartX.getText().toString());
					startY=Double.parseDouble(etStartY.getText().toString());
					startName=etStartName.getText().toString();
					if(startX <= 0|| startY <= 0 || startName == null || startName == ""){
						Toast.makeText(NavigationOnlineActivity.this, "请设置正确的起点值", Toast.LENGTH_SHORT).show();
						return;
					}
					Point2Ds points=new Point2Ds();
					Point2D point2D=new Point2D(startX,startY);
					points.add(point2D);
					coordConvert(points,R.drawable.start_point);
					Toast.makeText(NavigationOnlineActivity.this, "finish", Toast.LENGTH_SHORT).show();
					startView.setVisibility(View.INVISIBLE);
				}
			});

			break;
		case R.id.btnEnd_ano:
			final LinearLayout endView=(LinearLayout) findViewById(R.id.include_ano);
			endView.setVisibility(View.VISIBLE);

			etEndX=(EditText) endView.findViewById(R.id.etX_i);
			etEndX.setText("104.073324");

			etEndY=(EditText) endView.findViewById(R.id.etY_i);
			etEndY.setText("30.696837");

			TextView tvEndAlias=(TextView) endView.findViewById(R.id.tvAlias_i);
			tvEndAlias.setVisibility(View.GONE);

			etEndName=(EditText) endView.findViewById(R.id.etName_i);
			etEndName.setVisibility(View.GONE);

			Button btnEndFinish=(Button) endView.findViewById(R.id.btnFinish_i);
			btnEndFinish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					endX=Double.parseDouble(etEndX.getText().toString());
					endY=Double.parseDouble(etEndY.getText().toString());
					endName=etEndName.getText().toString();
					if(endX <= 0 || endY <= 0 || endName == null || endName == ""){
						Toast.makeText(NavigationOnlineActivity.this, "请设置正确的终点值", Toast.LENGTH_SHORT).show();
						return;
					}
					Point2Ds points=new Point2Ds();
					Point2D point2D=new Point2D(endX,endY);
					points.add(point2D);
					
					coordConvert(points,R.drawable.end_point);
					Toast.makeText(NavigationOnlineActivity.this, "finish", Toast.LENGTH_SHORT).show();
					endView.setVisibility(View.INVISIBLE);
				}
			});
			break;
		case R.id.btnNavigation_ano:
			trackingLayer.clear();
			mapControl.getMap().refresh();
			if(startX <= 0 || endX <= 0){
				Toast.makeText(this, "坐标值未设置", Toast.LENGTH_SHORT).show();
				return;
			}
			final LinearLayout llNavigation2=(LinearLayout) findViewById(R.id.llNavigation_ano);
			llNavigation2.setVisibility(View.VISIBLE);
			Button btnNavigationFinish=(Button) findViewById(R.id.btnNavigationFinish_ano);
			btnNavigationFinish.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(routeType != null){
						llNavigation2.setVisibility(View.GONE);
						NavigationOnline navigationOnline=new NavigationOnline();
//						必须调用
//						设置钥匙
						navigationOnline.setKey(MyApplication.KEY);
						
//						设置回调，查看NavigationOnline是否调用成功
						navigationOnline.setNavigationOnlineCallback(new NavigationOnlineCallback() {

							@Override
							public void calculateSuccess(NavigationOnlineData data) {
								navigationFlag=true;
								setNavigationOnline(data);
							}

							@Override
							public void calculateFailed(String errorInfo) {
								Log.e("NavigationOnlineActivity", errorInfo);
							}
						});
						NavigationOnlineParameter parameter=new NavigationOnlineParameter();
						Point2D startPoint=new Point2D(startX,startY);
						Point2D endPoint=new Point2D(endX,endY);
//						以下两个方法必须调用
//						设置起点
						parameter.setStartPoint(startPoint);
//						设置终点
						parameter.setEndPoint(endPoint);
						
//						设置目标类型
						parameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//						设置道路类型
						parameter.setRouteType(routeType);
//						进行道路分析
						navigationOnline.routeAnalyst(parameter);
						final ProgressDialog dialog=new ProgressDialog(NavigationOnlineActivity.this);
						dialog.setMessage("加载中...");
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						new Thread(new Runnable(){
							public void run(){
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								runOnUiThread(new Runnable(){
									public void run(){
										if(navigationFlag){
											dialog.dismiss();
											mapControl.getMap().refresh();
										}else{
											Toast.makeText(NavigationOnlineActivity.this, "加载路径失败", Toast.LENGTH_SHORT).show();
											dialog.dismiss();
										}
										
									}
								});
							}
						}).start();
						dialog.show();
					}
				}
			});

			RadioGroup rgNavigationSelected=(RadioGroup) findViewById(R.id.rgNavigationSelected_ano);
			rgNavigationSelected.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch(checkedId){
					case R.id.rbMinLength_ano:
						routeType=RouteType.MIN_LENGTH;
						break;
					case R.id.rbNoHighWay_ano:
						routeType=RouteType.NO_HIGHWAY;
						break;
					case R.id.rbRecommend_ano:
						routeType=RouteType.RE_COMMEND;
						break;
					default:
						break;
					}
				}
			});

			break;
		case R.id.btnIn_ano:
			mapControl.getMap().zoom(2);
			mapControl.getMap().refresh();

			break;
		case R.id.btnOut_ano:
			mapControl.getMap().zoom(0.5);
			mapControl.getMap().refresh();
			break;
		case R.id.btnClear_ano:
			if(trackingLayer != null){
				trackingLayer.clear();
			}
			if(mapView != null){
				mapView.removeAllCallOut();
			}

			mapControl.getMap().refresh();
			break;
		case R.id.btnEntire_ano:
			if (mapControl != null) {
				mapControl.getMap().viewEntire();
				mapControl.getMap().refresh();
			}
			break;
		default:
			break;
		}
	}
	
	private void setNavigationOnline(NavigationOnlineData data){
		if(data == null){
			return ;
		}
//		从data中获取geoline
		GeoLine geoLine=data.getRoute();
		GeoStyle geoLineStyle=new GeoStyle();
		geoLineStyle.setLineColor(new Color(0, 0, 255));
		geoLineStyle.setLineWidth(1);
		geoLineStyle.setLineSymbolID(15);
//		为geoLine设置风格
		geoLine.setStyle(geoLineStyle);
//		在跟踪图层上显示geoLine
		trackingLayer.add(geoLine, "线路");
		mapControl.getMap().refresh();
//		得到线路信息的集合
		List<PathInfo> pathInfoList=data.getPathInfos();
		int pathInfoCount=pathInfoList.size();
		System.out.println(pathInfoCount);
//		for(int i=0;i<pathInfoCount;i++){
//			得到具体线路信息
//			PathInfo pathInfo=pathInfoList.get(i);
//			获取下一条道路的路口点坐标
//			Point2D nextLocation=pathInfo.getCorressLocation();
//			获取当前道路的长度
//			double currentLoadLength=pathInfo.getLength();
//			获取	当前道路的名称
//			String currentName=pathInfo.getRoadName();
//			获取下一条道路的转弯方向
//			int nextDirection=pathInfo.getNextDirection();
			
//			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ic_next);
//			Display display=getWindowManager().getDefaultDisplay();
//			DisplayMetrics dm=new DisplayMetrics();
//			display.getMetrics(dm);
//			bitmap.setDensity(dm.densityDpi);
//			Matrix matirx=new Matrix();
//			matirx.setRotate(45);
//			Bitmap newBitmap=Bitmap.createBitmap(bitmap, 0, 0,
//					bitmap.getWidth(), bitmap.getHeight(),matirx, true);
//			@SuppressWarnings("deprecation")
//			BitmapDrawable bitmapDrawable=new BitmapDrawable(newBitmap);
//			ImageView imageView=new ImageView(this);
//			imageView.setImageDrawable(bitmapDrawable);
//			addCallOutOnMapView(new Point2Ds(new Point2D[]{nextLocation}), imageView);
//		}
		
	}
	
	private void coordConvert(Point2Ds point2Ds,final int viewId){
		CoordinateConvert coordConvert=new CoordinateConvert(this);
		CoordinateConvertParameter parameter=new CoordinateConvertParameter();
		//	以下四个函数必须被调用
		parameter.setKey(MyApplication.KEY);
//		预转换的坐标集合
		parameter.setPoint2Ds(point2Ds);
//		原坐标集合的类型
		parameter.setSrcCoordinateType(CoordinateType.NAVINFO_AMAP_LONGITUDE_LATITUDE);
//		目标坐标的类型，超图云为四维、高德墨卡托
		parameter.setDestCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//		进行转换
		coordConvert.convert(parameter);
//		查看是否转换成功
		coordConvert.setConvertCallback(new ConvertCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public synchronized void convertSuccesss(Point2Ds point2Ds) {
				ImageView imageView=new ImageView(NavigationOnlineActivity.this);
				imageView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeResource(
						getResources(), viewId)));
				addCallOutOnMapView(point2Ds,imageView);
			}

			@Override
			public void convertFailed(String errInfo) {
				Log.e("POIQueryActivity", errInfo);
			}
		});
	}
	
	private void addCallOutOnMapView(Point2Ds point2Ds,View view){
		for (int i = 0; i < point2Ds.getCount(); i++) {
			Point2D point2D = point2Ds.getItem(i);

			CallOut callout = new CallOut(this);
			callout.setCustomize(true);
			callout.setLocation(point2D.getX(), point2D.getY());
			callout.setContentView(view);
			callout.setStyle(CalloutAlignment.BOTTOM);
			mapView.addCallout(callout);
			mapControl.panTo(point2D, 100);
			mapControl.getMap().refresh();
		}
	}
}
