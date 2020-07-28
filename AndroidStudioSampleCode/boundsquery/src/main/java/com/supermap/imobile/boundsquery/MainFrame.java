package com.supermap.imobile.boundsquery;

import com.supermap.data.Color;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:范围查询
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
 * 1、范例简介：示范如何对数据进行查询，并在MapControl中展示出来
 * 2、示例数据：安装目录/SampleData/GeometryInfo/World.smwu
 * 3、关键类型/成员: 
 *      Map.findSelection 方法
 *      Map.refresh 方法
 *      Map.getLayers 方法
 *      Selecttion.toRecordset 方法
 *      Selecttion.fromRecordset 方法
 *      DatasetVector.query 方法
 *      MapControl.getMap 方法
 *      MapControl.setOnTouchListener 事件监听
 *      MapControl.setSize 方法
 *      GeoStyle.setLineColor 方法
 *      GeoStyle.setLineSymbolID 方法
 *      
 * 4、使用步骤：
 *   (1)点击下拉框选择要查询的图层
 *   (2)点击【查询】按钮进行相关的查询，查询结果在地图中高亮显示
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

public class MainFrame extends Activity implements OnTouchListener,OnClickListener{
    /** Called when the activity is first created. */
	
	private MapView mapView;
	private MapControl m_mapControl;
	private Workspace m_woWorkspace; 
	private Button m_btnZoomin;
	private Button m_btnZoomout;
	private Button m_btnQuery;
	private Spinner m_spnSelectLayer;
	private ArrayAdapter<String> adtSelectLayer;
	private String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestPermissions();
        // 初始化环境,设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        Environment.initialization(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);

        openMap();  
        initView();
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
    // 打开地图
    private boolean openMap(){
    	
    	// 获取当前设备的显示屏幕的相关参数
        final Display display = getWindowManager().getDefaultDisplay(); 
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        if(openWorkspace()){ 
        	
            // 将地图显示空间和 工作空间关联    
        	mapView = (MapView)findViewById(R.id.map_View);
        	m_mapControl=mapView.getMapControl();
        	m_mapControl.getMap().setWorkspace(m_woWorkspace);       	
        	m_mapControl.getMap().setMapDPI(dm.densityDpi);
            m_mapControl.setOnTouchListener(this);

            // 打开工作空间中地图
            String mapName = m_woWorkspace.getMaps().get(1);
            boolean isOpenMap = m_mapControl.getMap().open(mapName);
            if(isOpenMap){
                // 刷新地图，涉及地图的任何操作都需要调用该接口进行刷新
                m_mapControl.getMap().refresh();
            }  
            return true;
        }
        return false;
    }
        
    // 用于打开示范数据
    private boolean openWorkspace(){
    	
        m_woWorkspace = new Workspace();
        WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
        m_info.setServer(sdcard+"/SampleData/GeometryInfo/World.smwu");
        m_info.setType(WorkspaceType.SMWU);    
        return m_woWorkspace.open(m_info);
        
    } 
    
    // 初始化控件，绑定监听器
	private void initView(){
		m_btnZoomin = (Button)findViewById(R.id.zoomin);
		m_btnZoomout = (Button)findViewById(R.id.zoomout);
		// 初始化放大、缩小按钮监听
		m_btnZoomin.setOnClickListener(this);
		m_btnZoomout.setOnClickListener(this);
		
		m_btnQuery = (Button)findViewById(R.id.btn_searchGeo);
		m_btnQuery.setOnClickListener(new queryBtnlistener());
		
		
		
		m_spnSelectLayer = (Spinner)findViewById(R.id.spn_select_layer);
		adtSelectLayer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		Layers layers =  m_mapControl.getMap().getLayers();
		int nCount =layers.getCount();
		for (int i = 0; i < nCount; i++) {
			String strLayerName = layers.get(i).getName();
			adtSelectLayer.add(strLayerName);
		}
		
		//设置下拉列表风格,将adapter添加到spinner中
		adtSelectLayer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_spnSelectLayer.setAdapter(adtSelectLayer); 
		m_spnSelectLayer.setOnItemSelectedListener(new spinnerSelectedListener());
	}
	
	private Rectangle2D getBounds(){
		Point2D ptLeftBottom = m_mapControl.getMap().pixelToMap(new Point(0,1000));
		Point2D ptRightTop = m_mapControl.getMap().pixelToMap(new Point(1000,0));
		Rectangle2D bounds = new Rectangle2D(ptLeftBottom, ptRightTop);
		return bounds;
	}
	
	// 查询按钮
	class queryBtnlistener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub	
			Layer layer = null;
			
			String strLayerName = m_spnSelectLayer.getSelectedItem().toString();
			if (strLayerName.equals("")) {
				return;
			}else {
				layer = m_mapControl.getMap().getLayers().get(strLayerName);
			}
			
			DatasetVector datasetvector = (DatasetVector)layer.getDataset();
	        
			// 清空选择集
	        for (int i = 0; i < m_mapControl.getMap().getLayers().getCount(); i++) {
				Layer ly = m_mapControl.getMap().getLayers().get(i);
				ly.getSelection().clear();
			}         
	        
	        Rectangle2D bounds = getBounds();
	        System.out.println(bounds.toString());
	        System.out.println(layer.getName());
	        // 查询，返回查询结果记录集
	        Recordset recordset = datasetvector.query(bounds, CursorType.STATIC);
	        if (recordset.getRecordCount()<1) {
				Toast.makeText(MainFrame.this, "未搜索到对象" , Toast.LENGTH_SHORT).show();
				m_mapControl.getMap().refresh();
				recordset.dispose();
				return;
			}else{
				Toast.makeText(MainFrame.this, "Count"+recordset.getRecordCount(), Toast.LENGTH_LONG).show();
			}
	         
	        layer.getSelection().fromRecordset(recordset);
	        
	        // 设置选择集的风格
	        GeoStyle style = new GeoStyle();
	        style.setLineColor((new Color(0,0,222)));
	        style.setLineSymbolID(0);
	        style.setLineWidth(0.5);
	        style.setMarkerSymbolID(351);
	        style.setMarkerSize(new com.supermap.data.Size2D(5,5));
	        style.setFillForeColor(new Color(244,50,50));
	        style.setFillOpaqueRate(70);
	        
	        layer.getSelection().setStyle(style);
	 
	        recordset.moveFirst();
	        Geometry geometry = recordset.getGeometry();
	        m_mapControl.getMap().setCenter(geometry.getInnerPoint());
	        m_mapControl.getMap().refresh();
			
	        // 释放资源
	        recordset.dispose();
	        geometry.dispose();	
		}
	}
	
	//下拉列表
	class spinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		m_mapControl.onMultiTouch(event);
		return true;
	}

	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.zoomin:                      // 放大
			m_mapControl.getMap().zoom(2);
			m_mapControl.getMap().refresh();
			break;
		case R.id.zoomout:                     // 缩小
			m_mapControl.getMap().zoom(0.5);
			m_mapControl.getMap().refresh();
			break;
		default:
			break;
		}
	}
}