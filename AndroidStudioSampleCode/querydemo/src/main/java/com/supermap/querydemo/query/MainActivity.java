package com.supermap.querydemo.query;

import com.supermap.data.Color;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.SpatialQueryMode;
import com.supermap.querydemo.R;
import com.supermap.querydemo.appconfig.DefaultDataConfig;
import com.supermap.querydemo.appconfig.MyApplication;
import com.supermap.querydemo.query.Gesture.DrawnListener;
import com.supermap.querydemo.query.Gesture.SearchAroundListener;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
/**
 * <p>
 * Title:地图查询
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   展示点、线、面范围查询和属性查询。
 * 2、Demo数据：数据目录："SuperMap/Demos/Data/QueryData/"
 *           地图数据："changchun.smwu", "changchun.udb", "changchun.udd"
 *           许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *    QueryParameter.setSpatialQueryObject();       方法
 *    QueryParameter.setSpatialQueryMode();         方法
 *    QueryParameter.setCursorType();               方法
 *    QueryParameter.setAttributeFilter();          方法
 *    DatasetVector.query();                        方法
 *    Recordset.getGeometry();                      方法
 *
 * 4、功能展示
 *   (1)点、线、面范围查询；
 *   (2)属性查询。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends Activity implements OnClickListener{

	private MapControl mMapControl = null;
	private DynamicView mDynView = null;
	private MapView mMapView = null;
	private Gesture mGesture;
	private QueryResultPopup mPopup = null;
	private EditText mKeyQuery = null;
	private Context context = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		initUI();

		prepareData();
//        mMapControl.getMap().setWorkspace(MyApplication.getInstance().getOpenedWorkspace());
//        mMapControl.getMap().open(MyApplication.getInstance().getOpenedWorkspace().getMaps().get(0));
//        mMapControl.getMap().refresh();

//        mDynView = new DynamicView(this, mMapControl.getMap());
//        mDynView.setHitTestTolerance(BitmapFactory.decodeResource(getResources(), R.drawable.ic_btn_poi).getWidth());
//        mMapView.addDynamicView(mDynView);
//
//        mGesture = new Gesture(mMapView,mDynView);
//		mPopup = new QueryResultPopup(mMapView,mDynView);
//
//		mGesture.setSearchAroundListener(new SearchAroundListener() {
//			@Override
//			public void searchGeometry(Geometry geoRegion) {
//				query(geoRegion, DatasetType.POINT);
//			}
//		});
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
				new DefaultDataConfig().autoConfig();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						MyApplication.getInstance().openWorkspace();
						mMapControl.getMap().setWorkspace(MyApplication.getInstance().getOpenedWorkspace());
						mMapControl.getMap().open(MyApplication.getInstance().getOpenedWorkspace().getMaps().get(0));

						mMapControl.getMap().refresh();
//						mMapControl.getMap().setFullScreenDrawModel(true);
						mDynView = new DynamicView(context, mMapControl.getMap());
						mDynView.setHitTestTolerance(BitmapFactory.decodeResource(getResources(), R.drawable.ic_btn_poi).getWidth());
						mMapView.addDynamicView(mDynView);

						mGesture = new Gesture(mMapView,mDynView);
						mPopup = new QueryResultPopup(mMapView,mDynView);

						mGesture.setSearchAroundListener(new SearchAroundListener() {
							@Override
							public void searchGeometry(Geometry geoRegion) {
								query(geoRegion, DatasetType.POINT);
							}
						});

					}
				});
			}
		}.start();
	}

	/**
	 * 初始化界面
	 */
	private void initUI(){
		mMapView =  (MapView) findViewById(R.id.mapview);
		mMapControl = mMapView.getMapControl();

		mKeyQuery = (EditText) findViewById(R.id.et_query);
		mKeyQuery.setOnTouchListener(textOnTouchListener);
		findViewById(R.id.btnZoomIn).setOnClickListener(this);
		findViewById(R.id.btnZoomOut).setOnClickListener(this);
		findViewById(R.id.btnViewEntire).setOnClickListener(this);
		findViewById(R.id.btn_querypt).setOnClickListener(this);
		findViewById(R.id.btn_queryline).setOnClickListener(this);
		findViewById(R.id.btn_queryregion).setOnClickListener(this);
		findViewById(R.id.btn_query).setOnClickListener(this);
		findViewById(R.id.btn_list).setOnClickListener(this);
		findViewById(R.id.btn_clear).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_querypt:
				clearTrackingLayer();
				mMapView.removeAllCallOut();
				mDynView.clear();
				mDynView.refresh();
				mPopup.colsePoup();
				closeKeyBoard();

				mGesture.draw();
				mGesture.setDrawnListener(new DrawnListener() {
					@Override
					public void drawnGeometry(Geometry geoRegion) {
						query(geoRegion, DatasetType.POINT);
						geoRegion.dispose();
					}
				});
				break;
			case R.id.btn_queryline:
				clearTrackingLayer();
				mMapView.removeAllCallOut();
				mDynView.clear();
				mDynView.refresh();
				mPopup.colsePoup();
				closeKeyBoard();

				mGesture.draw();
				mGesture.setDrawnListener(new DrawnListener() {
					@Override
					public void drawnGeometry(Geometry geoRegion) {
						query(geoRegion, DatasetType.LINE);
						geoRegion.dispose();
					}
				});
				break;
			case R.id.btn_queryregion:
				clearTrackingLayer();
				mMapView.removeAllCallOut();
				mDynView.clear();
				mDynView.refresh();
				mPopup.colsePoup();
				closeKeyBoard();

				mGesture.draw();
				mGesture.setDrawnListener(new DrawnListener() {
					@Override
					public void drawnGeometry(Geometry geoRegion) {
						query(geoRegion, DatasetType.REGION);
						geoRegion.dispose();
					}
				});
				break;
			case R.id.btn_query:
				clearTrackingLayer();
				mMapView.removeAllCallOut();
				mDynView.clear();
				mDynView.refresh();
				mPopup.colsePoup();

				String key = mKeyQuery.getText().toString();
				query(key);
				closeKeyBoard();
				break;
			case R.id.btn_list:
				mPopup.show();
				break;
			case R.id.btn_clear:
				mDynView.clear();
				mDynView.refresh();
				mMapView.removeAllCallOut();
				mPopup.dismiss();
				clearTrackingLayer();
				mMapControl.getMap().refresh();
				break;
			case R.id.btnZoomIn:
				//double curScale = mMapControl.getMap().getScale();
				mMapControl.getMap().zoom(2);
				mMapControl.getMap().refresh();
				break;
			case R.id.btnZoomOut:
				//curScale = mMapControl.getMap().getScale();
				mMapControl.getMap().zoom(0.5);
				mMapControl.getMap().refresh();
				break;
			case R.id.btnViewEntire:
				mMapControl.getMap().viewEntire();
				mMapControl.getMap().refresh();
				break;

			default:
				break;
		}
	}

	/**
	 * 按区域查询对象
	 * @param georegion  查询区域
	 * @param type       数据集类型
	 */
	private void query(Geometry georegion,DatasetType type){
		mPopup.clear();
		Layers layers = mMapControl.getMap().getLayers();
		DynamicStyle style = new DynamicStyle();
		style.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.ic_poi));
		for(int i=layers.getCount()-1;i>=0;i--){

			Dataset dataset = layers.get(i).getDataset();

			if(dataset.getType().equals(type)){
				DatasetVector dv = (DatasetVector)dataset;
				QueryParameter param = new QueryParameter();
				param.setSpatialQueryObject(georegion);
				param.setSpatialQueryMode(SpatialQueryMode.CONTAIN);
				param.setCursorType(CursorType.STATIC);
				Recordset recordset = dv.query(param);
				mPopup.addResult(recordset);
			}
		}
		mMapView.invalidate();
		mPopup.show();

		addQueryRegion(georegion);
	}

	/**
	 * 按关键字查询
	 * @param key    关键字
	 */
	private void query(String key){
		if(key.length()==0){
			Toast.makeText(this, "没有输入关键字", Toast.LENGTH_LONG).show();
			return;
		}

		mPopup.clear();

		Layers layers = mMapControl.getMap().getLayers();
		for(int i=layers.getCount()-1;i>=0;i--){
			Dataset dataset = layers.get(i).getDataset();
			if(dataset.getType().equals(DatasetType.POINT)){
				DatasetVector dv = (DatasetVector)dataset;
				QueryParameter param = new QueryParameter();
				param.setAttributeFilter("name like \"%"+key+"%\"");
				Recordset recordset = dv.query(param);
				mPopup.addResult(recordset);
			}
		}
		mPopup.show();
	}

	/**
	 * 在跟踪层上添加查询区域
	 * @param georegion
	 */
	private void addQueryRegion(Geometry georegion){
		GeoStyle style = new GeoStyle();
		style.setFillForeColor(new Color(190, 190, 190));
		style.setFillOpaqueRate(30);
		style.setLineColor(new Color(180, 180, 200));
		style.setLineWidth(0.4);
		georegion.setStyle(style);

		mMapControl.getMap().getTrackingLayer().add(georegion, "");
		mMapControl.getMap().refresh();
	}

	/**
	 * 清空跟踪层
	 */
	private void clearTrackingLayer(){
		if (mMapControl.getMap().getTrackingLayer().getCount() < 1) {
			return;
		}

		mMapControl.getMap().getTrackingLayer().clear();
		mMapControl.getMap().refresh();
	}

	private OnTouchListener textOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			mPopup.colsePoup();

			return false;
		}

	};

	/**
	 * 关闭键盘
	 */
	private void closeKeyBoard(){
		//隐藏键盘
		InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(mKeyQuery.getWindowToken(), 0);


	}
}
