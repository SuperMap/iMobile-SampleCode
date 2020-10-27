package com.supermap.basedemo.base;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import com.supermap.basedemo.R;
import com.supermap.basedemo.appconfig.DataManager;
import com.supermap.basedemo.appconfig.DefaultDataConfig;
import com.supermap.basedemo.appconfig.DefaultDataManager;
import com.supermap.basedemo.appconfig.MyApplication;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
/**
 * <p>
 * Title:地图显示
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
 * 1、Demo简介：
 *   展示离线的矢量数据、影像数据，在线地图等地图数据的显示和浏览。
 * 2、Demo数据：数据目录："/SuperMap/Demos/Data/BaseDemo/"
 *              地图数据："changchun.smwu", "changchun.udb", "changchun.udd"
 *              许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *   Workspace.getMaps().get();                方法
 *   Workspace.getDatasources().get();         方法
 *   Datasource.getDatasets().get();           方法
 *   MapControl.getMap();                      方法
 *   MapCOntrol.zoomTo();                      方法
 *   Map.open();                               方法
 *   Map.getLayers().add();                    方法
 *   Map.refresh();                            方法
 *   Map.close();                              方法
 *
 * 4、功能展示
 *   (1)离线的矢量数据、影像数据，在线地图展示；
 *   (2)打开工作空间；
 *   (3)打开数据源。
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
	
	private static RadioButton mBtnOpenMap = null;
	private static RadioButton mBtnOpenWorkspace = null;
	private static RadioButton mBtnOpenDatasource = null;
	// 只负责接受焦点
	private RadioButton mBtnReceiveFocus = null;
	
	private MapView            mMapView = null;
	private static MapControl  mMapControl = null;
	private DefaultDataManager mDefaultDataManager = null;
	private DataManager mUserDataManager = null;
    private MyMapPopup         mMyMapPopup = null;
    private UserDatasourcePopup mUserDatasourcePopup = null;
    private UserWorkspacePopup  mUserWorkspacePopup = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapControl = mMapView.getMapControl();
		
		prepareData();
		initUI();
	}

	/**
	 * 初始化UI
	 */
	private void initUI(){
		mBtnOpenMap        = (RadioButton) findViewById(R.id.btn_map_open);
		mBtnOpenWorkspace  = (RadioButton) findViewById(R.id.btn_wks_open);
		mBtnOpenDatasource = (RadioButton) findViewById(R.id.btn_ds_open);
		mBtnOpenMap.setOnClickListener(this);
		mBtnOpenWorkspace.setOnClickListener(this);
		mBtnOpenDatasource.setOnClickListener(this);
		
		mBtnReceiveFocus = (RadioButton) findViewById(R.id.btn_receivefocus);
		
		findViewById(R.id.btnZoomIn).setOnClickListener(this);
		findViewById(R.id.btnZoomOut).setOnClickListener(this);
		findViewById(R.id.btnViewEntire).setOnClickListener(this);
	}

	/**
	 * 加载数据
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
				mDefaultDataManager = MyApplication.getInstance().getDefaultDataManager();
				mUserDataManager = MyApplication.getInstance().getUserDataManager();
				mDefaultDataManager.open();
		    	progress.dismiss();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						if(mDefaultDataManager.isDataOpen()){
							mMapControl.getMap().setWorkspace(mDefaultDataManager.getWorkspace());
							// 对长春市地图增加整屏刷新
							if(mDefaultDataManager.getDisplayMapName().equals("长春市区图")){
								mMapControl.getMap().setFullScreenDrawModel(true);
							}else{
								mMapControl.getMap().setFullScreenDrawModel(false);
							}
							//判断mapname是否为空
							if(mDefaultDataManager.getDisplayMapName()== null){
								return;
							}
							mMapControl.getMap().open(mDefaultDataManager.getDisplayMapName());
							mMapControl.getMap().refresh();
						}else {
							MyApplication.getInstance().ShowError("工作空间打开失败！");
						}
						mMyMapPopup          = new MyMapPopup(mMapControl);
						mUserDatasourcePopup = new UserDatasourcePopup(mMapControl);
						mUserWorkspacePopup  = new UserWorkspacePopup(mMapControl);
						progress.dismiss();
					}
				});
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_map_open:
			mBtnOpenMap.setChecked(true);
			mBtnOpenWorkspace.setChecked(false);
			mBtnOpenDatasource.setChecked(false);
			
			mMyMapPopup.show();
			mUserDatasourcePopup.dismiss();
			mUserWorkspacePopup.dismiss();
			break;
		case R.id.btn_wks_open:
			mBtnOpenMap.setChecked(false);
			mBtnOpenWorkspace.setChecked(true);
			mBtnOpenDatasource.setChecked(false);
			
			mUserWorkspacePopup.show();
			mMyMapPopup.dismiss();
			mUserDatasourcePopup.dismiss();
			break;
		case R.id.btn_ds_open:
			mBtnOpenMap.setChecked(false);
			mBtnOpenWorkspace.setChecked(false);
			mBtnOpenDatasource.setChecked(true);

			mUserDatasourcePopup.show();
			mMyMapPopup.dismiss();
			mUserWorkspacePopup.dismiss();
			break;
		case R.id.btnZoomIn:
			double curScale = mMapControl.getMap().getScale();
			mMapControl.getMap().zoom(2);
			mMapControl.getMap().refresh();
			break;
		case R.id.btnZoomOut:
			curScale = mMapControl.getMap().getScale();
			mMapControl.getMap().zoom(0.5);
			mMapControl.getMap().refresh();
			break;
		case R.id.btnViewEntire:
			//全幅的时候就停止地图的动画
			mMapControl.cancelAnimation();
			mMapControl.getMap().viewEntire();
			mMapControl.getMap().refresh();
			break;
		default:
			break;
		}
	}

	/**
	 * 重置按钮状态
	 */
	public static void reset(){
		mBtnOpenMap.setChecked(false);
		mBtnOpenWorkspace.setChecked(false);
		mBtnOpenDatasource.setChecked(false);

	}
}
