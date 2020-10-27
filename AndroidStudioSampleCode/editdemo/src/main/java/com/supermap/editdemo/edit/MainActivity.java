package com.supermap.editdemo.edit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RadioButton;

import com.supermap.data.GeometryType;
import com.supermap.data.Point;
import com.supermap.editdemo.R;
import com.supermap.editdemo.appconfig.DefaultDataConfig;
import com.supermap.editdemo.appconfig.MyApplication;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.MeasureListener;
/**
 * <p>
 * Title:地图编辑
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
 *   展示添加、删除对象，编辑节点，量算等地图数据编辑的操作。
 * 2、Demo数据：数据目录："SuperMap/Demos/Data/EditData/"
 *           地图数据："changchun.smwu", "changchun.udb", "changchun.udd", "edit.udb", "edit.udd"
 *           许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *    Layer.setEditable();                          方法
 *    MapControl.setAction();                       方法
 *    MapControl.submit();                          方法
 *    MapControl.addGeometrySelectedListener();     方法
 *    MapControl.addActionChangedListener();        方法
 *    MapControl.addMeasureListener();              方法
 *    MapControl.setStrokeColor();                  方法
 *    MapControl.setStrokeWidth();                  方法
 *    MapControl.undo();                            方法
 *    MapControl.redo();                            方法
 *
 * 4、功能展示
 *   (1)添加点、线、面、自由线、自由面，涂鸦；
 *   (2)编辑、添加、删除节点，删除对象；
 *   (3)距离，面积量算。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends Activity implements OnClickListener, OnTouchListener{
	private MapControl mMapControl = null;
	private MeasurePopup mMeasurePopup = null;
	private DrawPopup mDrawPopup = null;
	private EditPopup mEditPopup = null;
	private SettingPopup mSettingPopup = null;
	private SubmitInfo   mSubmitInfo   = null;
	// 只负责接受焦点
	private RadioButton mBtnReceiveFocus = null;
	private View  anchorView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initUI();
		prepareData();

		mMapControl.setMagnifierEnabled(true);
		mMapControl.setOnTouchListener(this);
		mMeasurePopup = new MeasurePopup(mMapControl);
		mDrawPopup = new DrawPopup(mMapControl);
		mEditPopup = new EditPopup(mMapControl);
		mSettingPopup = new SettingPopup(mMapControl);
		mSubmitInfo   = new SubmitInfo(mMapControl);
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
//						mMapControl.getMap().setFullScreenDrawModel(true);
						mMapControl.getMap().refresh();
					}
				});
			}
		}.start();
	}
	/**
	 * 初始化界面
	 */
	private void initUI(){
		MapView mapView =  (MapView) findViewById(R.id.mapView);
		mMapControl = mapView.getMapControl();

		findViewById(R.id.btn_add).setOnClickListener(this);
		findViewById(R.id.btn_edit).setOnClickListener(this);
		findViewById(R.id.btn_undo).setOnClickListener(this);
		findViewById(R.id.btn_redo).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_submit).setOnClickListener(this);
		findViewById(R.id.btn_setting).setOnClickListener(this);
		findViewById(R.id.btnZoomIn).setOnClickListener(this);
		findViewById(R.id.btnZoomOut).setOnClickListener(this);
		findViewById(R.id.btnViewEntire).setOnClickListener(this);
		findViewById(R.id.btn_measure).setOnClickListener(this);

		mBtnReceiveFocus = (RadioButton)findViewById(R.id.btn_receivefocus1);
		anchorView = findViewById(R.id.btn_add);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_add:
				mMeasurePopup.dismiss();
				mEditPopup.dismiss();
				mSettingPopup.dismiss();
				mSubmitInfo.dismiss();

				if(mDrawPopup.isShowing()){
					mBtnReceiveFocus.setChecked(true);
					mDrawPopup.dismiss();
				}else{
					mDrawPopup.show(anchorView);
				}
				break;
			case R.id.btn_edit:
				mMeasurePopup.dismiss();
				mDrawPopup.dismiss();
				mSettingPopup.dismiss();
				mSubmitInfo.dismiss();

				if(mEditPopup.isShowing()){
					mBtnReceiveFocus.setChecked(true);
					mEditPopup.dismiss();
				}else{
					mEditPopup.show(anchorView);
				}
				break;
			case R.id.btn_undo:
				mMapControl.undo();
				break;
			case R.id.btn_redo:
				mMapControl.redo();
				break;
			case R.id.btn_cancel:
				mDrawPopup.cancel();
				mEditPopup.cancel();
				mMapControl.cancel();
				mMeasurePopup.cancel();
				mSubmitInfo.dismiss();

				break;
			case R.id.btn_submit:

				boolean isEdittable = isEditting();
				if(isEdittable){
					mMapControl.submit();

					mMeasurePopup.cancel();
					mEditPopup.cancel();
					mDrawPopup.cancel();
					mMapControl.cancel();
				}else{
					mMeasurePopup.dismiss();
					mEditPopup.dismiss();
					mSettingPopup.dismiss();
					mDrawPopup.dismiss();
					reset();
					mSubmitInfo.show();
				}
				break;
			case R.id.btn_setting:
				mMeasurePopup.dismiss();
				mDrawPopup.dismiss();
				mEditPopup.dismiss();
				mSubmitInfo.dismiss();

				if(mSettingPopup.isShowing()){
					mBtnReceiveFocus.setChecked(true);
					mSettingPopup.dismiss();
				}else{
					mSettingPopup.show(anchorView);
				}
				break;
			case R.id.btnZoomIn:
				mMapControl.getMap().zoom(2);
				mMapControl.getMap().refresh();
				break;
			case R.id.btnZoomOut:
				mMapControl.getMap().zoom(0.5);
				mMapControl.getMap().refresh();
				break;
			case R.id.btnViewEntire:
				mMapControl.getMap().viewEntire();
				mMapControl.getMap().refresh();
				break;
			case R.id.btn_measure:
				mDrawPopup.dismiss();
				mEditPopup.dismiss();
				mSettingPopup.dismiss();
				mSubmitInfo.dismiss();

				if(mMeasurePopup.isShowing()){
					mBtnReceiveFocus.setChecked(true);
					mMeasurePopup.dismiss();
				}else{
					mMeasurePopup.show(anchorView);
				}
				break;
			default:
				break;
		}
	}

	MeasureListener measureListener = new MeasureListener() {

		@Override
		public void lengthMeasured(double arg0, Point arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void areaMeasured(double arg0, Point arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void angleMeasured(double arg0, Point arg1) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		mMapControl.onMultiTouch(event);

		int action = event.getAction();

		// 绘制完点对象，自动提交
		if(action == MotionEvent.ACTION_UP){
			if (mMapControl.getCurrentGeometry() != null && mMapControl.getCurrentGeometry().getType()==GeometryType.GEOPOINT) {

				mMapControl.submit();

				return true;
			}
		}

		return true;

	}

	/**
	 * 判断是否在编辑
	 * @return
	 */
	private boolean isEditting() {

		Action action= mMapControl.getAction();
		if(action.equals(Action.PAN) || action.equals( Action.NULL)){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 重置按钮
	 */
	protected void reset(){
		((RadioButton)findViewById(R.id.btn_add)).setChecked(false);
		((RadioButton)findViewById(R.id.btn_edit)).setChecked(false);
		((RadioButton)findViewById(R.id.btn_setting)).setChecked(false);
		((RadioButton)findViewById(R.id.btn_measure)).setChecked(false);
	}
}