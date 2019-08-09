package com.supermap.imobile.geometryinfo;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:查询对象信息
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
 * 1、范例简介：示范如何查询几何对象的信息
 * 2、示例数据：安装目录/sdcard/SampleData/GeometryInfo/World.smwu
 * 3、关键类型/成员: 
 *      CallOut .setContentView() 	方法
 *		CallOut.setStyle(); 		方法
 *		CallOut.setLocation(); 		方法			
 *		CallOut.setBackground();	方法
 *		MapView.removeAllCallOut();	方法
 *		MapView.addCallout();		方法
 *		MapView.showCallOut();		方法
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
 *   (1)点击可选择按钮，在地图上长按选择对象作为查询对象
 *   (2)点击相应的按钮进行相关的查询，查询结果在地图中以选择集的方式展现出来
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
	private MapView m_mapView;
	private MapControl m_mapControl; // 地图显示控件	
	private Workspace m_woWorkspace; // 工作空间
	
	private ZoomControls m_Zoom;
	private ImageButton m_btnSelect;
	
	private TextView m_txtCountry;
	private TextView m_txtCapital;
	private TextView m_txtPop;
	private TextView m_txtContinent;
	
	private View m_DetailLayout;
	private PopupWindow pwDetailInfo;
	
	private String mStrCountry;
	private String mStrCapital;
	private String mStrPop;
	private String mStrContinent;
	
	private Button m_btnQuery;
	private Spinner m_spnSelectContinent;
	private ArrayAdapter<String> adtSelectContinent;
	private static final String[] strContinentName = {"亚洲","欧洲","非洲","南美洲","北美洲","南极洲","大洋洲"};
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestPermissions();
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
        	m_mapView = (MapView)findViewById(R.id.mapview);
        	m_mapControl = m_mapView.getMapControl();
        	// 手势监听器
        	m_mapControl.setGestureDetector(new GestureDetector(new MapGestureListener()));
				   
        	m_mapControl.getMap().setWorkspace(m_woWorkspace);      	
        	m_mapControl.getMap().setMapDPI(dm.densityDpi);
            m_mapControl.setOnTouchListener(this);

            // 打开工作空间中地图的第2幅地图 
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
        
    // 用于打开示范数据/sdcard/SampleData/GeometryInfo/World.smwu
    private boolean openWorkspace(){
        m_woWorkspace = new Workspace();

        WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
        m_info.setServer(sdcard+"/SampleData/GeometryInfo/World.smwu");
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
		m_btnSelect.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_mapControl.setAction(Action.SELECT);
			}
		});
		
		m_spnSelectContinent = (Spinner)findViewById(R.id.spn_select_continent);
		adtSelectContinent = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strContinentName);
		adtSelectContinent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_spnSelectContinent.setAdapter(adtSelectContinent);

		m_btnQuery = (Button)findViewById(R.id.btn_search);
		m_btnQuery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {			
				Query(); // 查询
			}
		});	
		
		LayoutInflater lfCallOut = getLayoutInflater();		
		m_DetailLayout = lfCallOut.inflate(R.layout.detailinfo, null);	
		
		pwDetailInfo = new PopupWindow(m_DetailLayout,380, LayoutParams.WRAP_CONTENT);	

		m_txtCountry = (TextView)m_DetailLayout.findViewById(R.id.txt_country);
		m_txtCapital = (TextView)m_DetailLayout.findViewById(R.id.txt_capital);
		m_txtPop = (TextView)m_DetailLayout.findViewById(R.id.txt_pop);
		m_txtContinent = (TextView)m_DetailLayout.findViewById(R.id.txt_Continent);
	}

	public boolean onTouch(View v, MotionEvent event) {
		m_mapControl.onMultiTouch(event);
		return true;
	}
	
	// 属性查询
	private void Query(){
		String strContinent = m_spnSelectContinent.getSelectedItem().toString();
		String strFilter = "CONTINENT = '" + strContinent + "'";
			
		// 获得第10个图层
		Layer layer = m_mapControl.getMap().getLayers().get(9);		
		DatasetVector datasetvector = (DatasetVector)layer.getDataset();

        // 设置查询参数
        QueryParameter parameter = new QueryParameter();
        parameter.setAttributeFilter(strFilter);
        parameter.setCursorType(CursorType.STATIC);
        
        // 查询，返回查询结果记录集
        Recordset recordset = datasetvector.query(parameter);
		
		if (recordset.getRecordCount()<1) {
			Toast.makeText(MainFrame.this, "未搜索到对象", Toast.LENGTH_SHORT).show();
			m_mapControl.getMap().refresh();
			return;
		}
		
        Point2D ptInner;
        recordset.moveFirst();  
        Geometry geometry = recordset.getGeometry();   
        
        m_mapView.removeAllCallOut(); // 移除所有Callout
		  	
        while (!recordset.isEOF()) { 
        	geometry = recordset.getGeometry();
			ptInner = geometry.getInnerPoint();
			
        	LayoutInflater lfCallOut = getLayoutInflater();
    		View calloutLayout = lfCallOut.inflate(R.layout.callout2, null);
    		
    		Button btnSelected = (Button)calloutLayout.findViewById(R.id.btnSelected);
    		btnSelected.setText(geometry.getID() + "");
    		btnSelected.setTag(geometry.getID());
    		btnSelected.setOnClickListener(new detailClickListener());	
			
    		CallOut callout = new CallOut(MainFrame.this);
			callout.setContentView(calloutLayout);				// 设置显示内容
			callout.setCustomize(true);							// 设置自定义背景图片
			callout.setLocation(ptInner.getX(), ptInner.getY());// 设置显示位置	
			m_mapView.addCallout(callout);

			recordset.moveNext();			
		}
        
        m_mapView.showCallOut();								// 显示标注	
        m_mapControl.getMap().setCenter(geometry.getInnerPoint());
        m_mapControl.getMap().refresh();
        
        // 释放资源
        recordset.dispose();    
        geometry.dispose();	
	}
	
	// ID查询
	private void QuerybyID(String id){
		String strFilter = "SMID = '" + id + "'";
		
		// 获得第10个图层
		Layer layer = m_mapControl.getMap().getLayers().get(9);		
		DatasetVector datasetvector = (DatasetVector)layer.getDataset();

        // 设置查询参数
        QueryParameter parameter = new QueryParameter();
        parameter.setAttributeFilter(strFilter);
        parameter.setCursorType(CursorType.STATIC);
        
        // 查询，返回查询结果记录集
        Recordset recordset = datasetvector.query(parameter);
		
		if (recordset.getRecordCount()<1) {
			return;
		}
        
        recordset.moveFirst();  
        
		mStrCountry = recordset.getFieldValue("COUNTRY").toString();	        
        mStrCapital = recordset.getFieldValue("CAPITAL").toString();
        mStrContinent = recordset.getFieldValue("CONTINENT").toString();
        mStrPop = recordset.getFieldValue("POP_1994").toString();
        
        // 释放资源
        recordset.dispose();    
	}
	
	private ImageButton btn_Close;
	
	class detailClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (btn_Close != null)
				btn_Close.performClick();
			
			String strID = v.getTag().toString();
			System.out.println(strID);
			QuerybyID(strID);
			
			
			m_txtCountry.setText(mStrCountry);
			m_txtCapital.setText(mStrCapital);
			m_txtPop.setText(mStrPop);
			m_txtContinent.setText(mStrContinent);
			
//			pwDetailInfo.showAtLocation(m_mapControl, Gravity.NO_GRAVITY, 8, 86);
			pwDetailInfo.showAsDropDown(v, 60, -60);
			btn_Close = (ImageButton)m_DetailLayout.findViewById(R.id.btn_close);
	
			btn_Close.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pwDetailInfo.dismiss();
				}
			});
		}	
	}
	
	// 手势监听器
	class MapGestureListener extends SimpleOnGestureListener{
		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Recordset rt = null;

			// 获得第10个图层
			Layer ly = m_mapControl.getMap().getLayers().get(9);
			rt  = ly.getSelection().toRecordset();
		
			if (rt != null) {			
				if (rt.getRecordCount()<1) {
					return;
				}
				mStrCountry = rt.getFieldValue("COUNTRY").toString();	        
		        mStrCapital = rt.getFieldValue("CAPITAL").toString();
		        mStrContinent = rt.getFieldValue("CONTINENT").toString();
		        mStrPop = rt.getFieldValue("POP_1994").toString();
		        
				Geometry geometry = rt.getGeometry();
				Point2D ptInner = geometry.getInnerPoint();

				LayoutInflater lfCallOut = getLayoutInflater();
				View calloutLayout = lfCallOut.inflate(R.layout.callout, null);
				
				TextView txtBubbleTitle = (TextView)calloutLayout.findViewById(R.id.edtBubbleTitle);
				TextView txtBubbleText = (TextView)calloutLayout.findViewById(R.id.edtBubbleText);
				txtBubbleTitle.setText(mStrCountry);
		        txtBubbleText.setText(mStrCapital);
		        
				CallOut callout = new CallOut(MainFrame.this);
				callout.setContentView(calloutLayout);				// 设置显示内容
				callout.setStyle(CalloutAlignment.BOTTOM);			// 设置对齐方式
				callout.setLocation(ptInner.getX(), ptInner.getY());// 设置显示位置
				
				//callout.setBackground(android.graphics.Color.argb(255, 120, 230, 255),
				//			android.graphics.Color.argb(255, 200, 246, 255));// 自定义颜色
				m_mapView.removeAllCallOut();
				m_mapView.addCallout(callout);
				m_mapView.showCallOut();							// 显示标注	
			}
			
			super.onLongPress(e);
		}
	}
}