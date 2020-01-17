package com.supermap.onlinedemo;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.myapplication.MyApplication;
import com.supermap.onlineservices.CoordinateConvert;
import com.supermap.onlineservices.CoordinateConvert.ConvertCallback;
import com.supermap.onlineservices.CoordinateConvertParameter;
import com.supermap.onlineservices.CoordinateType;
import com.supermap.onlineservices.Geocoding;
import com.supermap.onlineservices.GeocodingData;
import com.supermap.onlineservices.GeocodingParameter;
import com.supermap.onlineservices.Geocoding.GeocodingCallback;
import com.supermap.popups.GeocodingPopupWindow;
import com.supermap.popups.ReverseGeocodingPopupWindow;


/**
 * <p>
 * Title: 地理编码与逆地理编码
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
 *		1、范例简介：用关键字和城市进行正向地理编码，用地理坐标进行逆地理编码，点击相关按钮，会有图标显示在地图上，点击图标有相关数据详细信息
 *		2、范例数据：地图引擎：SuperMapCloud
 *          地图数据：超图云地图数据 http://supermapcloud.com
 *          许可目录："/SuperMap/license/"
 *      3、关键类型/成员:
 *      			Geocoding.setKey()												方法
 *      			Geocoding.setGeocodingCallback()								方法
 *      			Geocoding.geocoding()											方法
 *      			
 *      			GeocodingParameter.setCity()									方法
 *      			GeocodingParameter.setLandmark()								方法
 *      			GeocodingParameter.setCoordinateType()							方法
 *      
 *      			ReverseGeocoding.setKey()										方法
 *      			ReverseGeocoding.setGeocodingCallback()							方法
 *      			ReverseGeocoding.reverseGeocoding()								方法
 *      			
 *      			GeocodingData.getLocation()										方法
 *      			GeocodingData.getName()											方法
 *      			GeocodingData.getFormatedAddress()								方法
 *					GeocodingData.getAddress().getProvince()						方法
 *					GeocodingData.getAddress().getCity()							方法
 *					GeocodingData.getAddress().getCityCode()						方法
 *					GeocodingData.getAddress().getCounty()							方法
 *					GeocodingData.getAddress().getStreetNumber().getDistance()		方法
 *					GeocodingData.getAddress().getStreetNumber().getNumber()		方法
 *					GeocodingData.getAddress().getStreetNumber().getStreet()		方法
 *					GeocodingData.getConfidence()									方法
 *
 *      			CoordinateConvertParameter.setKey()								方法
 *     				CoordinateConvertParameter.setPoint2Ds()						方法
 *     				CoordinateConvertParameter.setSrcCoordinateType()				方法
 *     				CoordinateConvertParameter.setDestCoordinateType()				方法
 *     			
 *     				CoordinateConvert.convert()										方法
 *     				CoordinateConvert.setConvertCallback()							方法
 *     4、步骤：
 *     		地理编码
 *     		(1) 输入城市、关键字
 *     		(2) 点击正向编码
 *     		(3) 点击后，在地图上显示图标，点击图标，显示详细信息
 *     		逆地理编码
 *     		(1) 输入地理坐标
 *     		(2) 点击逆向编码
 *     		(3) 点击后，在地图上显示图标，点击图标，显示详细信息
 *     5、注意事项：
 *     		(1) 进行地理编码时，需输入关键字
 *     		(2) 进行逆地理编码时，需输入地理坐标
 *     		(3) 查找成功，在地图上未看到图标，请缩小地图
 *	</p>
 *	
 *	<p>
 *	Company: 北京超图软件股份有限公司
 *	</p>
 */
public class GeocodingActivity extends Activity implements OnClickListener {
	private Context context = this;

	private GeocodingPopupWindow geocodingPopup = null;
	private ReverseGeocodingPopupWindow reverseGeocodingPopup = null;

	private MapControl mapControl;
	private MapView mapView;
	private Workspace workspace;
	private Datasource datasource;

	private Button btnGeocoding, btnReverseGeocoding, btnIn, btnOut, btnClear,btnEntire;
	private EditText etCity, etLandmark, etX, etY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		设置ActionBar消失
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_geocoding);
//		判断打开地图是否成功
		if (openMap()) {
			initView();
		}
//		添加一个手势，在图层上添加一个CallOut对象
		new Gesture(this, mapView);
	}

	private boolean openMap() {
		mapView = (MapView) findViewById(R.id.mapView_ag);
		mapControl = mapView.getMapControl();
		
		mapControl.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					if(geocodingPopup != null){
						geocodingPopup.dismiss();
					}
					if(reverseGeocodingPopup != null){
						reverseGeocodingPopup.dismiss();
					}
				}
				
				return false;
			}
		});
//		添加动态层
//		if(m_locateDynamicView == null){
//			m_locateDynamicView = new DynamicView(context,mapControl.getMap());
//			mapView.addDynamicView(m_locateDynamicView);
//		}
//		打开工作空间
		workspace = new Workspace();
		DatasourceConnectionInfo dcInfo = new DatasourceConnectionInfo();
//		打开超图云地图
		dcInfo.setEngineType(EngineType.SuperMapCloud);
		dcInfo.setServer("http://t2.supermapcloud.com/");
//		给地图设置工作空间
		mapControl.getMap().setWorkspace(workspace);
		datasource = workspace.getDatasources().open(dcInfo);
		if (datasource != null) {
//			给地图添加一个图层
			mapControl.getMap().getLayers().add(datasource.getDatasets().get(0), true);
			//设置地图初始的显示范围，放地图出图时就显示的是北京
			mapControl.getMap().setScale(1/458984.375);
			mapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
			mapControl.getMap().refresh();
			return true;
		}
		return false;
	}

	private void initView() {
//		防止软件盘弹出
		TextView tvFocus=(TextView) findViewById(R.id.tvFocus_ag);
		tvFocus.requestFocus();

		etCity = (EditText) findViewById(R.id.etCity_ag);
		etLandmark = (EditText) findViewById(R.id.etLandmark_ag);
		etX = (EditText) findViewById(R.id.etX_ag);
		etY = (EditText) findViewById(R.id.etY_ag);

		btnGeocoding = (Button) findViewById(R.id.btnGeocoding_ag);
		btnReverseGeocoding = (Button) findViewById(R.id.btnReverseGeocoding_ag);
		btnIn = (Button) findViewById(R.id.btnIn_ag);
		btnOut = (Button) findViewById(R.id.btnOut_ag);
		btnClear = (Button) findViewById(R.id.btnClear_ag);
		btnEntire = (Button) findViewById(R.id.btnEntire_ag);

		btnGeocoding.setOnClickListener(this);
		btnReverseGeocoding.setOnClickListener(this);
		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnEntire.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGeocoding_ag:
//			移除所有的CallOut对象
			mapView.removeAllCallOut();
			if (geocodingPopup != null) {
				geocodingPopup.dismiss();
			}
			if (reverseGeocodingPopup != null) {
				reverseGeocodingPopup.dismiss();
			}
			Geocoding geocoding = new Geocoding();
			GeocodingParameter parameter = new GeocodingParameter();
//			以下三个方法必须调用
//			设置钥匙
			geocoding.setKey(MyApplication.KEY);
//			在哪个范围进行地理编码
			parameter.setCity(etCity.getText().toString() + "市");
//			关键字进行地理编码
			parameter.setLandmark(etLandmark.getText().toString());
			
//			这里加载的超图云地图，把目标坐标转换为超图云地图的坐标类型
			parameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//			编码成功，进行回调，查询是否成功
			geocoding.setGeocodingCallback(new GeocodingCallback() {
				@Override
				public void reverseGeocodeSuccess(GeocodingData data) {

				}
				@Override
				public void geocodeSuccess(List<GeocodingData> dataList) {
					final int count = dataList.size();
					Log.i("GeocodingData counts", "" + count);
					for (int i = 0; i < count; i++) {
//						获取地理编码得到的数据
						GeocodingData geocodingData = dataList.get(i);
						Point2D pos=geocodingData.getLocation();
//						把CallOut对象绘画到图层上
						drawCallOutOnMapView(pos, geocodingData,GEOCODING_POPUPU_WINDOW);
//						给地图设置中心位置
						mapControl.getMap().setCenter(pos);
						mapControl.getMap().refresh();
					}
				}
				@Override
				public void geocodeFailed(String errorMsg) {
					Log.e("Geocoding", errorMsg);
				}
			});
//			进行地理编码
			geocoding.geocoding(parameter);
			break;
		case R.id.btnReverseGeocoding_ag:
			mapView.removeAllCallOut();
			mapControl.getMap().refresh();
			if (geocodingPopup != null) {
				geocodingPopup.dismiss();
			}
			if (reverseGeocodingPopup != null) {
				reverseGeocodingPopup.dismiss();
			}
			Point2D srcPoint2D = new Point2D();
			srcPoint2D.setX(Double.parseDouble(etX.getText().toString()));
			srcPoint2D.setY(Double.parseDouble(etY.getText().toString()));
			
			Point2Ds srcPoint2Ds=new Point2Ds();
			srcPoint2Ds.add(srcPoint2D);
			final Point2Ds srcPoints=new Point2Ds(srcPoint2Ds);
			Geocoding reverseGeocoding = new Geocoding();
//			必须调用
//			设置钥匙
			reverseGeocoding.setKey(MyApplication.KEY);
			reverseGeocoding.setGeocodingCallback(new GeocodingCallback() {
				@Override
				public void reverseGeocodeSuccess(GeocodingData data) {
//					进行坐标转换
					coordConvert(srcPoints,data);
				}
				@Override
				public void geocodeSuccess(List<GeocodingData> dataList) {
				}
				@Override
				public void geocodeFailed(String errorMsg) {
				}
			});
//			进行逆地理编码
			reverseGeocoding.reverseGeocoding(srcPoint2D);
			break;
		case R.id.btnIn_ag:
			mapControl.getMap().zoom(2);
			mapControl.getMap().refresh();
			break;
		case R.id.btnOut_ag:
			mapControl.getMap().zoom(0.5);
			mapControl.getMap().refresh();
			break;
		case R.id.btnEntire_ag:
			mapControl.getMap().viewEntire();
			mapControl.getMap().refresh();
			break;
		case R.id.btnClear_ag:
			mapView.removeAllCallOut();
			if (geocodingPopup != null) {
				geocodingPopup.dismiss();
			}
			if (reverseGeocodingPopup != null) {
				reverseGeocodingPopup.dismiss();
			}
			break;
		default:

			break;
		}
	}

	private void coordConvert(Point2Ds srcPoint2Ds,final GeocodingData geocodingData){
		CoordinateConvert coordConvert=new CoordinateConvert(GeocodingActivity.this);
		CoordinateConvertParameter coordParameter=new CoordinateConvertParameter();
		//以下四个函数必须被调用
//		设置钥匙
		coordParameter.setKey(MyApplication.KEY);
//		设置预转换的坐标
		coordParameter.setPoint2Ds(srcPoint2Ds);
//		设置原坐标类型
		coordParameter.setSrcCoordinateType(CoordinateType.BAIDU_LONGITUDE_LATITUDE);
//		设置目标坐标类型
		coordParameter.setDestCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
		
		//进行转换
		coordConvert.convert(coordParameter);
		//调用转换回调，查询是否转换成功
		coordConvert.setConvertCallback(new ConvertCallback() {

			@Override
			public void convertSuccesss(Point2Ds points) {
				Point2D destPoint2D=points.getItem(0);
//				将CallOut对象绘画到地图上
				drawCallOutOnMapView(destPoint2D, geocodingData, REVERSE_GEOCODING_POPUPU_WINDOW);
//				给地图设置中心位置
				mapControl.getMap().setCenter(destPoint2D);
				mapControl.getMap().refresh();
			}

			@Override
			public void convertFailed(String errInfo) {
				Log.e("GeodingActivity", errInfo);
			}
		});
	}
	
	private final int GEOCODING_POPUPU_WINDOW=0;
	private final int REVERSE_GEOCODING_POPUPU_WINDOW=1;
	/**
	 * 
	 * @param point2D  显示的位置
	 * @param geocodingData    弹出窗口所加载的内容信息
	 * @param popupWindow  指定弹出的窗口
	 */
	private void drawCallOutOnMapView(Point2D point2D,final GeocodingData geocodingData,final int popupWindow){
		Point2D changePoint2D = point2D;
		CallOut callout = new CallOut(
				this);
		final View view = LayoutInflater.from(this).inflate(R.layout.callout,null);
		Button btnClick = (Button) view.findViewById(R.id.btnInfos_c);
		btnClick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (geocodingPopup != null) {
					geocodingPopup.dismiss();
				}
				if (reverseGeocodingPopup != null) {
					reverseGeocodingPopup.dismiss();
				}
				if(popupWindow == GEOCODING_POPUPU_WINDOW){
					geocodingPopup = new GeocodingPopupWindow(
							context,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT,
							geocodingData);
					geocodingPopup.showAsDropDown(view);
				}
				if(popupWindow == REVERSE_GEOCODING_POPUPU_WINDOW){
					reverseGeocodingPopup = new ReverseGeocodingPopupWindow(
							context, LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, geocodingData);
					reverseGeocodingPopup.showAsDropDown(view);
				}
			}
		});
		callout.setContentView(view);
		callout.setLocation(
				changePoint2D.getX(),
				changePoint2D.getY());
		callout.setStyle(CalloutAlignment.CENTER);
		callout.setCustomize(true);
		mapView.addCallout(callout);
		mapControl.getMap().refresh();
	}
//   添加动态层
	//	private void drawCircleOnDyn(Point2D point2D, float azimuth, double q){
	//
	//		if (point2D.getX() == 0 || point2D.getY() == 0) {
	//			System.out.println("��λ��Ϊ��");
	//			return ;
	//		}
	//		m_locateDynamicView.removeElement(m_LocationID);
	//		m_locateDynamicView.removeElement(m_LocationPolygonID);
	//		//���쾫�ȷ�Χ
	//		if (q == 0) {
	//			q = 30;
	//		}
	//		GeoCircle geoCircle = new GeoCircle(point2D, q);
	//		GeoRegion geoRegion = geoCircle.convertToRegion(50*4);
	//		//���ƾ��ȷ�Χ
	//		DynamicPolygon dynPolygon = new DynamicPolygon();
	//		dynPolygon.fromGeometry(geoRegion);
	//		DynamicStyle style = new DynamicStyle();
	//		style.setBackColor(android.graphics.Color.rgb(128, 128, 255));
	//		style.setLineColor(android.graphics.Color.rgb(128,255,255));//224, 224, 224
	//		style.setAlpha(100);//95
	////		style.setSize(3.0f);//6.0f
	//		dynPolygon.setStyle(style);
	//
	//		m_locateDynamicView.addElement(dynPolygon);
	//		m_LocationPolygonID = dynPolygon.getID();
	//		drawPoint(point2D, azimuth);
	//	}
	//	
	//	public void drawPoint(Point2D point2D, float azimuth){
	//		DynamicPoint dynPoint = new DynamicPoint();
	//		dynPoint.addPoint(point2D);
	//		
	//		DynamicStyle dynStyle = new DynamicStyle();
	//		dynStyle.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.ic_btn_poi));
	//		dynStyle.setAngle(azimuth);
	//		dynPoint.setStyle(dynStyle);
	//		
	//		m_locateDynamicView.addElement(dynPoint);
	//		m_LocationID = dynPoint.getID();
	////		m_locateDynamicView.moveTo(m_LocationID);
	//		m_locateDynamicView.refresh();
	//	}
}
