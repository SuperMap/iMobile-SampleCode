package com.example.mapedit;

import java.util.ArrayList;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.LicenseType;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment.SavedState;
import android.content.DialogInterface;
import android.icu.text.AlphabeticIndex.Record;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity {

	String RootPath=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	private MapView mMapView;
	private MapControl mMapControl;
	private Workspace mWorkspace;
	private LinearLayout drawLayout,editLayout,selectLayout;
	private Draw mDraw;
	private Edit mEdit;
	private ArrayList<GeometrySelectedEvent> mulselect;
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
		requestPermissions() ;
		Environment.setLicensePath(RootPath+"/SuperMap/License/");
		Environment.initialization(this);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}
	
	private void initView() {
		mMapView=(MapView) findViewById(R.id.mapview);
		mMapControl=mMapView.getMapControl();
		
		
		drawLayout=(LinearLayout) findViewById(R.id.rg_expand_draw);
		editLayout=(LinearLayout) findViewById(R.id.rg_expand_edit);
		selectLayout=(LinearLayout) findViewById(R.id.ll_multiselect_menu);
		
//		drawLayout.setVisibility(View.GONE);
//		editLayout.setVisibility(View.GONE);
//		selectLayout.setVisibility(View.GONE);
		
		findViewById(R.id.draw).setOnClickListener(listener);
		findViewById(R.id.rb_creatLine).setOnClickListener(listener);
		findViewById(R.id.rb_supLine).setOnClickListener(listener);
		findViewById(R.id.rb_creatRegion).setOnClickListener(listener);
		findViewById(R.id.rb_drawLine).setOnClickListener(listener);
		findViewById(R.id.rb_drawRegion).setOnClickListener(listener);
		findViewById(R.id.edit).setOnClickListener(listener);
		findViewById(R.id.editnode).setOnClickListener(listener);
		findViewById(R.id.addnode).setOnClickListener(listener);
		findViewById(R.id.deletnode).setOnClickListener(listener);
		findViewById(R.id.rb_undo).setOnClickListener(listener);
		findViewById(R.id.rb_redo).setOnClickListener(listener);
		findViewById(R.id.btn_delete).setOnClickListener(listener);
		findViewById(R.id.btn_save).setOnClickListener(listener);
		findViewById(R.id.btn_multiselect).setOnClickListener(listener);
		findViewById(R.id.btn_begin_multiselect).setOnClickListener(listener);
		findViewById(R.id.btn_clear_select).setOnClickListener(listener);
	}
	
	private void initData() {
		new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {					
						mWorkspace=new Workspace();			
						WorkspaceConnectionInfo info=new WorkspaceConnectionInfo();				
						info.setServer(RootPath+"/SampleData/MapEdit/changchun.smwu");
						info.setType(WorkspaceType.SMWU);				
						mWorkspace.open(info);				
						mMapControl.getMap().setWorkspace(mWorkspace);				
						String mapname=mWorkspace.getMaps().get(0);
						mMapControl.getMap().open(mapname);							
						mMapControl.getMap().refresh();
						mDraw=new Draw(mMapControl);
						mEdit=new Edit(mMapControl);
					}
				});
			}
		}).start();
		
		registerListener();

	}
	private void registerListener() {
		// TODO Auto-generated method stub
		
		mMapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
			
			@Override
			public void geometrySelected(GeometrySelectedEvent event) {
				// TODO Auto-generated method stub
				mMapControl.appointEditGeometry(event.getGeometryID(), event.getLayer());
				
			}
			
			@Override
			public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> events) {
				// TODO Auto-generated method stub
				mulselect=events;

			}

			@Override
			public void geometryMultiSelectedCount(int i) {

			}
		});
		
	}
	OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.draw:
				if (drawLayout.getVisibility()==View.GONE) {
					drawLayout.setVisibility(View.VISIBLE);
					editLayout.setVisibility(View.GONE);
					selectLayout.setVisibility(View.GONE);
				}
				else {
					drawLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.rb_creatLine:
				mDraw.addLine();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.rb_supLine:
				mDraw.supLine();
				break;
			case R.id.rb_creatRegion:
				mDraw.addRegion();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.rb_drawLine:
				mDraw.drawLine();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.rb_drawRegion:
				mDraw.drawRegion();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.edit:
				if (editLayout.getVisibility()==View.GONE) {
					drawLayout.setVisibility(View.GONE);
					editLayout.setVisibility(View.VISIBLE);
					selectLayout.setVisibility(View.GONE);
					
					mMapControl.setAction(Action.SELECT);

				}
				else {
					editLayout.setVisibility(View.GONE);
					mMapControl.setAction(Action.PAN);
				}
				break;
			case R.id.editnode:
				mEdit.editnode();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.addnode:
				mEdit.addnode();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.deletnode:
				mEdit.deletnode();
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.rb_undo:
				mMapControl.undo();
				mDraw.undo();
				break;
			case R.id.rb_redo:
				mMapControl.redo();
				mDraw.redo();
				break;
			case R.id.btn_delete:
				if (mMapControl.getAction().equals(Action.MULTI_SELECT)||mMapControl.getCurrentGeometry()!=null) {
					deletObject();
				}
				else {
					Toast.makeText(MainActivity.this, "请选择删除对象",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_save:
				if (mMapControl.getAction().equals(Action.PAN)||mMapControl.getAction().equals(Action.NULL)) {
					mMapControl.setOnTouchListener(null);
					mMapControl.setAction(Action.PAN);
					mDraw.clearCurrentPoint();
					Toast.makeText(MainActivity.this, "请选择编辑对象", Toast.LENGTH_SHORT).show();
				}
				else {
					boolean aa=mMapControl.submit();
					if (!aa) {
						Log.i("++++++", "save fail");
					}
					mMapControl.setAction(Action.PAN);
					mMapControl.getMap().refresh();
				}
				break;
			case R.id.btn_multiselect:
				if (selectLayout.getVisibility()==View.GONE) {
					drawLayout.setVisibility(View.GONE);
					editLayout.setVisibility(View.GONE);
					selectLayout.setVisibility(View.VISIBLE);
					
					mMapControl.setAction(Action.SELECT);

				}
				else {
					selectLayout.setVisibility(View.GONE);
					mMapControl.setAction(Action.PAN);
				}
				break;
			case R.id.btn_begin_multiselect:
				mMapControl.setAction(Action.MULTI_SELECT);
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.btn_finish_multiselect:
				mMapControl.setAction(Action.SELECT);
				mMapControl.setOnTouchListener(null);
				mDraw.clearCurrentPoint();
				break;
			case R.id.btn_clear_select:
				DatasetVector dataset=(DatasetVector) mMapControl.getMap().getLayers().get("Line@edit").getDataset();
				Recordset rd=dataset.getRecordset(false, CursorType.DYNAMIC);
				rd.edit();
				rd.deleteAll();
				rd.update();
				rd.dispose();
				DatasetVector dataset1=(DatasetVector) mMapControl.getMap().getLayers().get("Region@edit").getDataset();
				Recordset rd1=dataset1.getRecordset(false, CursorType.DYNAMIC);
				rd1.edit();
				rd1.deleteAll();
				rd1.update();
				rd1.dispose();
				mMapControl.setOnTouchListener(null);
				mMapControl.getMap().getTrackingLayer().clear();
				mMapControl.setAction(Action.PAN);
				mDraw.clearCurrentPoint();
				break;
			default:
				break;
			}
			
		}
	};
	private void deletObject() {
		AlertDialog dialog=new AlertDialog.Builder(this)
				.setTitle("删除当前编辑对象")
				.setMessage("确定要删除这个对象")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (mMapControl.getAction().equals(Action.MULTI_SELECT)) {
							for (int i = 0; i < mulselect.size(); i++) {
								mMapControl.appointEditGeometry(mulselect.get(i).getGeometryID(), mulselect.get(i).getLayer());
								mMapControl.deleteCurrentGeometry();
							}
						}
						else {
							mMapControl.deleteCurrentGeometry();
						}
						
						mMapControl.setAction(Action.PAN);
					}
				} )
				.setNegativeButton("取消", null)
				.show();
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
}
