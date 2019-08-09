package com.supermap.imobile.service;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.services.FeatureSet;
import com.supermap.services.QueryMode;
import com.supermap.services.QueryOption;
import com.supermap.services.QueryService;
import com.supermap.services.ResponseCallback;
import com.supermap.services.ServiceQueryParameter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.app.ProgressDialog;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:服务模块示范代码
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码 
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 * 
 * 1、范例简介：示范如何使用服务模块
 * 2、示例数据：http://support.supermap.com.cn:8090/iserver/services/map-china400/rest/maps/China
 * 3、关键类型/成员: 
 * 		QueryService.query();			 				方法
 *		QueryService.setResposeCallback(); 				方法
 *		ServiceQueryParameter.setQueryServiceName(); 	方法
 *		ServiceQueryParameter.setQueryMapName(); 		方法
 *		ServiceQueryParameter.setQueryLayerName();	 	方法
 *		ServiceQueryParameter.setExpectRecordCount();   方法
 *		ServiceQueryParameter.setQueryRecordStart();    方法
 *		ServiceQueryParameter.setQueryOption();   		方法
 *		ServiceQueryParameter.setAttributeFilter(); 	方法
 * 4、使用步骤：
 *   (1)点击设置按钮，设置服务器地址与端口号。
 *   (2)输入要查询的服务名称、地图名称、图层名称(本范例程序中填入的服务名称和地图名与底图一致)。
 *   (3)输入查询语句，点击查询，查询结果会在地图上显示。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

public class MainFrame extends Activity {
	
	private MapView m_mapView;
	private MapControl m_mapControl; // 地图显示控件	
	private Workspace m_woWorkspace; // 工作空间	
	private ZoomControls m_Zoom;
	private Button m_btnSetting;
	private Button m_btnQuery;
	
	private PopupWindow m_popup;	
	private View mainLayout = null;
	
	private EditText m_edtServerName;
	private EditText m_edtMapName;
	private EditText m_edtLayerName;
	private EditText m_edtSql;
	
	private String m_strServer = "";
	
	private SharedPreferences m_preferences;
	private SharedPreferences.Editor m_editor;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestPermissions();
        // 设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        // 初始化环境
        Environment.initialization(this);
        
        setContentView(R.layout.main);
        openMap();        
        initView();
    }
    
    // 打开地图
    private boolean openMap(){
    	m_woWorkspace = new Workspace();
    	
    	 // 将地图显示空间和 工作空间关联    
    	m_mapView = (MapView)findViewById(R.id.mapview);
    	m_mapControl = m_mapView.getMapControl();
    			   
    	m_mapControl.getMap().setWorkspace(m_woWorkspace);
    	
    	DatasourceConnectionInfo dsInfo = new DatasourceConnectionInfo();
    	dsInfo.setServer("http://support.supermap.com.cn:8090/iserver/services/map-china400/rest/maps/China");
    	dsInfo.setEngineType(EngineType.Rest);
    	dsInfo.setAlias("ChinaRest");
    	
    	Datasource ds = m_woWorkspace.getDatasources().open(dsInfo);
    	if(ds != null){
    		
    		m_mapControl.getMap().getLayers().add(ds.getDatasets().get(0), true);
    		m_mapControl.getMap().refresh();
    		return true;
    	}
        Log.e(this.getClass().getName(), "打开数据源失败了");
       
        return true;
    }
    
        
    
    // 初始化控件，绑定监听器
	private void initView(){	
		m_preferences = getSharedPreferences("server", MODE_PRIVATE);
		m_editor = m_preferences.edit();
		
		m_edtServerName = (EditText)findViewById(R.id.edt_serverName);
		m_edtMapName = (EditText)findViewById(R.id.edt_mapName);
		m_edtLayerName = (EditText)findViewById(R.id.edt_layerName);
		m_edtSql = (EditText)findViewById(R.id.edt_sql);
		
		m_Zoom = (ZoomControls)findViewById(R.id.zoomControls1);
		m_Zoom.setOnZoomOutClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				m_mapControl.getMap().zoom(0.5);
				m_mapControl.getMap().refresh();
			}
		});
		m_Zoom.setOnZoomInClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				m_mapControl.getMap().zoom(2);
				m_mapControl.getMap().refresh();
			}
		});
		
		LayoutInflater lfCallOut = getLayoutInflater();
		mainLayout = lfCallOut.inflate(R.layout.setting, null);	
		m_popup = new PopupWindow(mainLayout,450,300);
		m_popup.setFocusable(true);
		
		//设置按钮
		m_btnSetting = (Button)findViewById(R.id.btn_setting);
		m_btnSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText edtServer = (EditText)mainLayout.findViewById(R.id.edt_server);
				EditText edtPort = (EditText)mainLayout.findViewById(R.id.edt_port);
				edtServer.setText(m_preferences.getString("server", "http://support.supermap.com.cn"));
				edtPort.setText(m_preferences.getString("port", "8090"));
				m_popup.showAtLocation(findViewById(R.id.mapview), Gravity.CENTER, 0, 0);
			}
		});
		
		
		//确定按钮	
		Button btnOk = (Button)mainLayout.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText edtServer = (EditText)mainLayout.findViewById(R.id.edt_server);
				EditText edtPort = (EditText)mainLayout.findViewById(R.id.edt_port);
				// 保存数据
				m_editor.putString("server", edtServer.getText().toString());
				m_editor.putString("port", edtPort.getText().toString());
				m_editor.commit();
				m_popup.dismiss();
			}
		});
		//关闭按钮
		Button btnClose = (Button)mainLayout.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_popup.dismiss();
			}
		});

		m_btnQuery = (Button)findViewById(R.id.btn_query);
		m_btnQuery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String strServer =  m_preferences.getString("server", "http://support.supermap.com.cn");
				String strPort = m_preferences.getString("port", "8090");
				m_strServer = strServer + ":" + strPort;
				Query(); // 查询
			}
		});	
	}
	
	// 查询
	private void Query() {
		m_mapView.removeAllCallOut();
		
		final ProgressDialog progress = new ProgressDialog(MainFrame.this);
		
		QueryService service = new QueryService(m_strServer);
		ServiceQueryParameter parameter = new ServiceQueryParameter();
		parameter.setQueryMapName(m_edtMapName.getText().toString());
		parameter.setQueryServiceName(m_edtServerName.getText().toString());
		parameter.setQueryLayerName(m_edtLayerName.getText().toString());		
		
		//设置查询参数
		parameter.setExpectRecordCount(100);
		parameter.setQueryRecordStart(0);
		parameter.setQueryOption(QueryOption.GEOMETRY);
		parameter.setAttributeFilter(m_edtSql.getText().toString());
		
		service.setResponseCallback(new ResponseCallback() {
			@Override
			public void requestSuccess() {
				//销毁进度条显示框
				progress.dismiss();
				
				Toast.makeText(MainFrame.this, "查询成功", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void requestFailed(String arg0) {
				//销毁进度条显示框
				progress.dismiss();
				
				Toast.makeText(MainFrame.this, "查询失败", Toast.LENGTH_LONG).show();
				System.out.println("错误信息 " + arg0);
			}
			
			@Override
			public void receiveResponse(FeatureSet arg0) {
				if (arg0 instanceof FeatureSet) {
					FeatureSet featureSet = (FeatureSet)arg0;

					int nCount = 0;
					featureSet.moveFirst();
					while (!featureSet.isEOF()) {
						
						Geometry geo = featureSet.getGeometry();
						if (geo == null) {
							featureSet.moveNext();
							continue;
						}
						nCount++;
						Point2D pt = featureSet.getGeometry().getInnerPoint();
						LayoutInflater lfCallOut = getLayoutInflater();
			    		View calloutLayout = lfCallOut.inflate(R.layout.callout, null);
					
			    		CallOut callout = new CallOut(MainFrame.this);
						callout.setContentView(calloutLayout);				// 设置显示内容
						callout.setCustomize(true);							// 设置自定义背景图片
						callout.setLocation(pt.getX(), pt.getY());			// 设置显示位置	
						m_mapView.addCallout(callout);
						featureSet.moveNext();
					}
					System.out.println("count is " + nCount);
					System.out.println("featureSet count is " + featureSet.getFeatureCount());
				}			
			}
//			

			@Override
			public void dataServiceFinished(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addFeatureSuccess(int arg0) {
				
			}
		});
		
		//显示服务查询进度条，回调里面销毁
		progress.setMessage("服务查询中...");
		progress.show();
		// 查询
		service.query(parameter, QueryMode.SqlQuery);
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