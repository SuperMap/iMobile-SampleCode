package com.supermap.onlinedemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
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
import com.supermap.onlineservices.POIInfo;
import com.supermap.onlineservices.POIQuery;
import com.supermap.onlineservices.POIQuery.POIQueryCallback;
import com.supermap.onlineservices.POIQueryParameter;
import com.supermap.onlineservices.POIQueryResult;
import com.supermap.popups.POIQueryPopupWindow;

/**
  * <p>
 * Title:  POI查询
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
 * 1、范例简介：示范如何运用关键字，城市进行POI查询，点击查找后，会显示查询到的图标，点击可查看详细信息
 * 2、示例数据：地图引擎：SuperMapCloud
 *          地图数据：超图云地图数据 http://supermapcloud.com
 *          许可目录："/SuperMap/license/"
 * 3、关键类型/成员: 
 *   			QueryParameter.setKey()										方法
 *     			QueryParameter.setKeywords()								方法							
 *     			QueryParameter.setCity()									方法
 *     			QueryParameter.setCoordinateType()							方法
 *     
 *     			POIiQuery.setPOIQueryCallback()								方法
 *     			POIQuery.query()											方法
 *     
 *     			POIInfo.getLocation()										方法
 *     			POIInfo.getConfidence()										方法
 *     			POIInfo.getName()											方法
 *     			POIInfo.getTelephone()										方法
 *     			POIInfo.getAddress()										方法
 *     			
 *     			CoordinateConvertParameter.setKey()							方法
 *     			CoordinateConvertParameter.setPoint2Ds()					方法
 *     			CoordinateConvertParameter.setSrcCoordinateType()			方法
 *     			CoordinateConvertParameter.setDestCoordinateType()			方法
 *     			
 *     			CoordinateConvert.convert()									方法
 *     			CoordinateConvert.setConvertCallback()						方法
 * 4、使用步骤：
 *			(1) 输入关键字或者城市
 *			(2) 点击查找
 *			(3) 显示出查找的位置，点击查找的位置，显示详细信息，触摸屏幕后，信息消失
 *			(4) 点击下一组数据，可查询到其他数据
 *			(5) 点击清除信息，地图恢复原貌
 * 5、注意： 
 *	(1) 如果查找成功后未能在地图中显示
 *  	解决办法：缩小地图，即可查看在地图显示的位置
 *  (2) 关键字不能为空
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 *
 */
public class POIQueryActivity extends Activity implements OnClickListener {

	private Workspace workspace;
	private MapView mapView;
	private Datasource datasource;
	private Dataset dataset;
	private MapControl mapControl;
	private List<CallOut> callOutList;
	private List<String> searchModeList;
	private ArrayAdapter<String> spinnerAdapter;
	private Spinner spinner;
	private Button btnQuery, btnNextQuery, btnIn, btnOut, btnClear,btnEntire;
	private EditText etCity, etKeywords;
	private List<Point2D> point2DList = new ArrayList<Point2D>();
	private POIQueryPopupWindow popup;
	private int next = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		设置ActionBar消失
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_poi_query);
//		打开地图
		if(openMap()){
			initView();
		}
//		添加一个手势，在图层上添加一个CallOut对象
		new Gesture(POIQueryActivity.this, mapView);
	}

	private void initView() {
//		防止软件盘弹出
		TextView tvFocus=(TextView) findViewById(R.id.tvFocus_apq);
		tvFocus.requestFocus();

		btnQuery = (Button) findViewById(R.id.btnQuery_apq);
		btnNextQuery = (Button) findViewById(R.id.btnNextQuery_apq);
		btnIn = (Button) findViewById(R.id.btnIn_apq);
		btnOut = (Button) findViewById(R.id.btnOut_apq);
		btnClear = (Button) findViewById(R.id.btnClear_apq);
		btnEntire = (Button) findViewById(R.id.btnEntire_apq);
		btnQuery.setOnClickListener(this);
		btnNextQuery.setOnClickListener(this);
		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnEntire.setOnClickListener(this);
//		提供选择输出的坐标类型，并未显示，暂时不用。这里用的超图云服务，都转换为910111类型的坐标
		spinner = (Spinner) findViewById(R.id.spinnerQueryMode_apq);
		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getStringList());
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
	}

	private boolean openMap() {
//		获取显示的屏幕
		Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		mapView = (MapView) findViewById(R.id.mapView_apq);
		mapControl = mapView.getMapControl();
		
		mapControl.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					if(popup != null){
						popup.dismiss();
					}
				}
				
				return false;
			}
		});
		
		workspace = new Workspace();
		DatasourceConnectionInfo dcInfo = new DatasourceConnectionInfo();
		/**
		 * 超图云服务地图的坐标类型为四维、高德墨卡托(910111代表四维、高德墨卡托)
		 * 若服务器返回的的坐标类型不为四维、高德墨卡托，需进行坐标转换
		 */
		dcInfo.setEngineType(EngineType.SuperMapCloud);
		dcInfo.setServer("http://t2.supermapcloud.com/");
//		给地图设置工作空间
		mapControl.getMap().setWorkspace(workspace);
//		mapControl.getMap().setMapDPI(dm.densityDpi);
//		打开数据源
		datasource = workspace.getDatasources().open(dcInfo);
		if (datasource != null) {
			dataset=datasource.getDatasets().get(0);
//			在地图上增添一个图层
			mapControl.getMap().getLayers().add(dataset, true);
			
//			设置地图初始的显示范围，放地图出图时就显示的是北京
			mapControl.getMap().setScale(1/458984.375);
			mapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
			mapControl.getMap().refresh();
			return true;
		}
		return false;
	}

	private List<String> getStringList() {
		if (searchModeList == null) {
			searchModeList = new ArrayList<String>();
			searchModeList.add("");
			searchModeList.add("");
			searchModeList.add("");
		}
		return searchModeList;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapControl.getMap().close();
		workspace.dispose();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnQuery_apq:
			if (popup != null) {
				popup.dismiss();
			}

			POIQuery poiQuery = new POIQuery(this);
			POIQueryParameter queryParameter = new POIQueryParameter();
			etCity = (EditText) findViewById(R.id.etCity_apq);
			etKeywords = (EditText) findViewById(R.id.etKeywords_apq);
//			以下两个方法必须调用
//			用户申请的钥匙
			queryParameter.setKey(MyApplication.KEY);
//			查询的关键字
			queryParameter.setKeywords(etKeywords.getText().toString());
//			在某个范围内查询
			queryParameter.setCity(etCity.getText().toString() + "市");
			/**
			 * 设置的目标坐标类型不为四维、高德墨卡托
			 * 910102代表百度经纬度坐标类型 	
			 */
			queryParameter.setCoordinateType(CoordinateType.BAIDU_LONGITUDE_LATITUDE);
//			进行POI查询
			poiQuery.query(queryParameter);
//			查看POI查询是否成功
			poiQuery.setPOIQueryCallback(new POIQueryCallback() {

				@Override
				public void querySuccess(final POIQueryResult queryResult) {
					Toast.makeText(POIQueryActivity.this, "查询成功",Toast.LENGTH_SHORT).show();
					mapView.removeAllCallOut();
					mapControl.getMap().refresh();
					callOutList = new ArrayList<CallOut>();
					final POIInfo[] poiInfos = queryResult.getPOIInfos();
					next = 2;
					Point2Ds point2Ds=new Point2Ds();
					for(int i=0;i< poiInfos.length; i++){
						Point2D point2D = poiInfos[i].getLocation();
//						Log.i("Src-Point2D-"+i,point2D.getX()+" "+point2D.getY());
						point2Ds.add(point2D);
					}

					CoordinateConvert coordConvert=new CoordinateConvert(POIQueryActivity.this);
					CoordinateConvertParameter parameter=new CoordinateConvertParameter();
					//	以下四个函数必须被调用
					parameter.setKey(MyApplication.KEY);
//					预转换的坐标集合
					parameter.setPoint2Ds(point2Ds);
//					原坐标集合的类型
					parameter.setSrcCoordinateType(CoordinateType.BAIDU_LONGITUDE_LATITUDE);
//					目标坐标的类型，超图云为四维、高德墨卡托
					parameter.setDestCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//					进行转换
					coordConvert.convert(parameter);
//					查看是否转换成功
					coordConvert.setConvertCallback(new ConvertCallback() {

						@Override
						public synchronized void convertSuccesss(Point2Ds point2Ds) {
							//把坐标类型转换为四维、高德墨卡托
							Point2Ds destPoints=prjCoordinateConvert(point2Ds);
							for(int i=0;i<destPoints.getCount();i++){
								point2DList.add(destPoints.getItem(i));
								System.out.println(destPoints.getItem(i).getX()+" "+destPoints.getItem(i).getY());
							}
							addCallOutOnMapView(destPoints, poiInfos);
						}

						@Override
						public void convertFailed(String errInfo) {
							Toast.makeText(POIQueryActivity.this, "位置显示失败 "+errInfo,Toast.LENGTH_SHORT).show();
							Log.e("POIQueryActivity", errInfo);
						}
					});
				}

				@Override
				public void queryFailed(String errInfo) {
					Toast.makeText(POIQueryActivity.this, errInfo,Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case R.id.btnNextQuery_apq:
			if (popup != null) {
				popup.dismiss();
			}
			if (callOutList == null) {
				return;
			}
			if (callOutList.size() > 0 && next < callOutList.size()) {
				mapView.removeAllCallOut();
				mapControl.getMap().refresh();
				int i = 0;
				while (i < 2 && next < callOutList.size()) {
					mapView.addCallout(callOutList.get(next));
					mapControl.panTo(point2DList.get(next), 100);
					mapControl.getMap().refresh();
					next++;
					i++;
				}
			} 
			if(next == callOutList.size() || callOutList.size()<= 2){
				Toast.makeText(this, "无数据啦!!!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btnIn_apq:
			mapControl.getMap().zoom(2);
			mapControl.getMap().refresh();

			break;
		case R.id.btnOut_apq:
			mapControl.getMap().zoom(0.5);
			mapControl.getMap().refresh();
			break;
		case R.id.btnClear_apq:
			mapView.removeAllCallOut();
			if (popup != null) {
				popup.dismiss();
			}
			break;
		case R.id.btnEntire_apq:
			if (mapControl != null) {
				mapControl.getMap().viewEntire();
				mapControl.getMap().refresh();
			}
			break;
		default:
			break;
		}
	}

	private Point2Ds prjCoordinateConvert(Point2Ds point2Ds){
		PrjCoordSys srcPrj = mapControl.getMap().getPrjCoordSys();
		//	设置坐标投影类型为四维、高德墨卡托(在这里可以不设置，因为加载的地图的投影坐标类型就为四维、高德墨卡托)
		if (srcPrj.getType() != PrjCoordSysType.PCS_SPHERE_MERCATOR) {
			PrjCoordSys destPrj = new PrjCoordSys(PrjCoordSysType.PCS_SPHERE_MERCATOR);
			CoordSysTranslator.convert(point2Ds,srcPrj,destPrj,
					new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			return point2Ds;
		}
		return point2Ds;
	}

	/**
	 * 在地图上添加CallOut对象
	 * @param point2Ds
	 * @param poiInfos
	 */
	private void addCallOutOnMapView(Point2Ds point2Ds,POIInfo[] poiInfos){

		for (int i = 0; i < point2Ds.getCount(); i++) {
			Point2D point2D = point2Ds.getItem(i);
			Log.i("Convert-Point2D-"+i,point2D.getX()+" "+point2D.getY());

			CallOut callout = new CallOut(POIQueryActivity.this);
			callout.setCustomize(true);
			final View view = LayoutInflater.from(POIQueryActivity.this).inflate(R.layout.callout, null);
			Button btnInfos = (Button) view.findViewById(R.id.btnInfos_c);
			final POIInfo poiInfo = poiInfos[i];
			btnInfos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (popup != null) {
						popup.dismiss();
					}
					popup = new POIQueryPopupWindow(
							POIQueryActivity.this,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, poiInfo);
					popup.showAsDropDown(view, 0, 0);
				}
			});

			callout.setLocation(point2D.getX(), point2D.getY());
			callout.setContentView(view);
			callout.setStyle(CalloutAlignment.CENTER);
			callOutList.add(callout);
			if (i < 2) {
				mapView.addCallout(callout);
				mapControl.panTo(point2DList.get(i), 100);
				mapControl.getMap().refresh();
			}
		}
	}
}
