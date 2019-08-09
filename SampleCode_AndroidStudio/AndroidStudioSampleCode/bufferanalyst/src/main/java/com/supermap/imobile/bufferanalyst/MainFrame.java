package com.supermap.imobile.bufferanalyst;

import com.supermap.analyst.BufferAnalystGeometry;
import com.supermap.analyst.BufferAnalystParameter;
import com.supermap.analyst.BufferEndType;
import com.supermap.data.Color;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasets;
import com.supermap.data.Environment;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ZoomControls;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:缓冲区分析
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
 * 1、范例简介：示范如何查询输入的缓冲区范围内的数据，并在MapControl中展示出来
 * 2、示例数据：安装目录/sdcard/SampleData/City/Changchun.smwu
 * 3、关键类型/成员: 
 *      QueryParameter.setSpatialQueryObject 方法
 *      QueryParameter.setSpatialQueryMode 方法
 *      SpatialQueryMode.CONTAIN 常量
 *      SpatialQueryMode.INTERSECT 常量
 *      SpatialQueryMode.DISJOINT 常量
 *      Map.findSelection 方法
 *      Selecttion.toRecordset 方法
 *      Selecttion.fromRecordset 方法
 *      DatasetVector.query 方法
 * 4、使用步骤：
 *   (1)在输入框中输入缓冲区范围（必须为数字）
 *   (2)点击选择按钮，长按选择要创建缓冲区分析的对象
 *   (3)点击相应的按钮进行相关的查询，查询结果在地图中以选择集的方式展现出来
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

public class MainFrame extends Activity implements OnTouchListener{
    /** Called when the activity is first created. */
	
	private MapControl m_mapControl; // 地图显示控件	
	private Workspace m_woWorkspace; // 工作空间
	private MapView m_mapView;
	private ZoomControls m_Zoom;
	private ImageButton m_btnSelect;
	private ImageButton m_btnPan;
	private Button m_btnQuery;
	private EditText m_edtDistance;
	String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
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
        
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
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
        	m_mapView = (MapView)findViewById(R.id.map_View);
        	m_mapControl=m_mapView.getMapControl();
        	m_mapControl.getMap().setWorkspace(m_woWorkspace);      	
        	m_mapControl.getMap().setMapDPI(dm.densityDpi);
            m_mapControl.setOnTouchListener(this);

            // 打开工作空间中地图的第1幅地图 
            String mapName = m_woWorkspace.getMaps().get(0);
            boolean isOpenMap = m_mapControl.getMap().open(mapName);
            if(isOpenMap){
                // 刷新地图，涉及地图的任何操作都需要调用该接口进行刷新
                m_mapControl.getMap().refresh();
                
                Layer roadLayer = m_mapControl.getMap().getLayers().get("RoadLine1@changchun");
                if(roadLayer == null)
                	return false;
                else
                	roadLayer.setSelectable(true);
                
                //设置其他图层不可编辑
                for(int i = 0; i < m_mapControl.getMap().getLayers().getCount(); i++)
                {
                	Layer tempLayer = m_mapControl.getMap().getLayers().get(i);
                	if(roadLayer != tempLayer)
                	{
                		tempLayer.setSelectable(false);
                	}
                }
            }  
            return true;
        }
        return false;
    }
        
    // 用于打开示范数据/sdcard/SampleData/City/Changchun.smwu
    private boolean openWorkspace(){
        m_woWorkspace = new Workspace();

        WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
        m_info.setServer(sdcard+"/SampleData/City/Changchun.smwu");
        m_info.setType(WorkspaceType.SMWU);
        
        return m_woWorkspace.open(m_info);
    } 
    
 // 初始化控件，绑定监听器
	private void initView(){
		
		m_Zoom = (ZoomControls)findViewById(R.id.zoomControls1);
		m_Zoom.setOnZoomOutClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_mapControl.getMap().zoom(0.5);
				m_mapControl.getMap().refresh();
			}
		});
		m_Zoom.setOnZoomInClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_mapControl.getMap().zoom(2);
				m_mapControl.getMap().refresh();
			}
		});
		
		m_btnSelect = (ImageButton)findViewById(R.id.btn_selectGeo);
		m_btnSelect.setOnClickListener(new selectBtnlistener());
		
		m_btnPan = (ImageButton)findViewById(R.id.btn_pan);
		m_btnPan.setOnClickListener(new panBtnlistener());
		
		m_btnQuery = (Button)findViewById(R.id.btn_searchGeo);
		m_btnQuery.setOnClickListener(new queryBtnlistener());
		
		m_edtDistance = (EditText)findViewById(R.id.edt_range);
	}

	// 缓冲区查询按钮
	class queryBtnlistener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Recordset queryRecordset = null;
			Layer ly = m_mapControl.getMap().getLayers().get("RoadLine1@changchun");
			if(ly.getSelection() != null)
			{
				queryRecordset = ly.getSelection().toRecordset();
			}
            
			TrackingLayer tLayer = m_mapControl.getMap().getTrackingLayer();
			tLayer.clear();
			GeoRegion geometryBuffer;
            
			if (queryRecordset != null && queryRecordset.getRecordCount() != 0) {
				// 为对象建立缓冲区，并将分析结果存储在结果数据集中
				Datasets datasets = m_woWorkspace.getDatasources().get(0).getDatasets();
				String dtName = datasets.getAvailableDatasetName("da");
				DatasetVectorInfo dd = new DatasetVectorInfo(dtName, DatasetType.REGION);
				DatasetVector d = datasets.create(dd);
				Recordset rr = d.getRecordset(false, CursorType.DYNAMIC);
		        while (!queryRecordset.isEOF()) {
		        	// 设置缓冲区分析参数
		    	    BufferAnalystParameter bufferAnalystParam = new BufferAnalystParameter();
		    	    bufferAnalystParam.setEndType(BufferEndType.ROUND);
		    	    int distance = 5;
		    	    try{
		    	    	distance = Integer.valueOf(m_edtDistance.getText().toString());
		    	    }catch (Exception e) {
		    	    	distance = 5;
					}
		    	    distance = distance<=0?5:distance;
		    	    bufferAnalystParam.setLeftDistance(distance);
		    	    bufferAnalystParam.setRightDistance(distance);
		    	    
		            Geometry geoForBuffer = queryRecordset.getGeometry();
		            PrjCoordSys prj = queryRecordset.getDataset().getPrjCoordSys();
		            geometryBuffer = BufferAnalystGeometry.createBuffer(geoForBuffer, bufferAnalystParam, prj);
		            
		            // 设置风格
			        GeoStyle style = new GeoStyle();
			        style.setLineColor((new Color(50,244,50)));
			        style.setLineSymbolID(0);
			        style.setLineWidth(0.5);
			        style.setMarkerSymbolID(351);
			        style.setMarkerSize(new com.supermap.data.Size2D(5,5));
			        style.setFillForeColor(new Color(244,50,50));
			        style.setFillOpaqueRate(70);
		            
		            geometryBuffer.setStyle(style);
		            
		            tLayer.add(geometryBuffer, "");
		            queryRecordset.moveNext();
		            
					rr.addNew((Geometry)geometryBuffer);
					rr.update();
		        }
				rr.dispose();
			} else {
				Toast.makeText(MainFrame.this, "没有选中对象" , Toast.LENGTH_SHORT).show();
			}

			
	        m_mapControl.getMap().refresh();
			
	        // 释放资源
	        queryRecordset.dispose();	
		}
	}
	
	// 点选按钮
	class selectBtnlistener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			m_mapControl.setAction(Action.SELECT);
		}
	}
	
	// 平移按钮
	class panBtnlistener implements OnClickListener{
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			m_mapControl.setAction(Action.PAN);
		}
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		m_mapControl.onMultiTouch(event);
		return true;
	}
}