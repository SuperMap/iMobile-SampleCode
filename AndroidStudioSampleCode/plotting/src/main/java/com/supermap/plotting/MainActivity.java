package com.supermap.plotting;

/**
 * <p>
 * Title:态势标绘
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
 *   	展示态势标绘功能的使用。
 *   
 * 2、Demo数据：
 * 		数据目录："../SampleData/PlottingData/"
 *      地图数据："SuperMapCloud.smwu", "Edit.udb", "Edit.udd"
 *      许可目录："../SuperMap/License/"
 *      
 * 3、关键类型/成员: 
 *    addPlotLibrary();				方法
 *    setPlotSymbol();				方法
 *    setAction();					方法
 *    getCurrentGeometry();			方法
 *    deleteCurrentGeometry();		方法
 *    submit();						方法
 *    undo();						方法		
 *    redo();						方法
 *    cancel();						方法
 *
 * 4、功能展示
 *   (1)绘制点标绘符号
 *   (2)绘制箭头标绘符号
 *   (3)标绘符号的编辑、删除
 *   (4)符号绘制中撤销、重做
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.supermap.data.*;
import com.supermap.mapping.*;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity {

	private MapControl m_mapControl = null;
	private Workspace m_workSpace = null;
	private MapView m_mapView = null;

	private GridView m_gridViewPoint = null;	//点标绘符号的GridView
	private GridView m_gridViewLine = null;		//线标绘符号的GridView
	private List<Map<String, Object>> m_dataListPoint = null;	//点标绘符号的资源列表
	private List<Map<String, Object>> m_dataListLine = null;	//线标绘符号的资源列表
	//private SimpleAdapter m_simpleAdaper = null;
	
	private long libID_JB = -1;	//点符号库ID
	private long libID_TY = -1;	//线符号库ID
	
	//点符号图片资源，此方法需把资源文件加入工程，用于在GridView中显示点符号
    private int[] iconPoint = { R.drawable.point_10, R.drawable.point_10100,
			R.drawable.point_14, R.drawable.point_30304, R.drawable.point_30307,
			R.drawable.point_30308, R.drawable.point_30502, R.drawable.point_30709,
			R.drawable.point_40503, R.drawable.point_56, R.drawable.point_70100,
			R.drawable.point_80102 , R.drawable.point_80106, R.drawable.point_80201,
			R.drawable.point_80400};
	//点符号的名称，也是其ID
    private String[] iconNamePoint = { "10", "10100", "14", "30304", "30307", "30308", "30502",
			"30709", "40503", "56", "70100", "80102", "80106", "80201", "80400" };
	//线符号图片资源，此方法需把资源文件加入工程，用于在GridView中显示线符号
	private int[] iconLine = { R.drawable.line_1000, R.drawable.line_1001,
				R.drawable.line_1002, R.drawable.line_1003, R.drawable.line_1004,
				R.drawable.line_1005, R.drawable.line_1006, R.drawable.line_1007,
				R.drawable.line_1008, R.drawable.line_1009 };
	//线符号的名称，也是其ID
	private String[] iconNameLine = { "1000", "1001", "1002", "1003", "1004", "1005", "1006",
				"1007", "1008", "1009" };
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
        String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        String dataPath = sdcard + "/SampleData/PlottingData/";	//数据路径，运行本程序，需要将指定的数据放到这个位置
        
        //设置许可文件路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
             
        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);

        setContentView(R.layout.activity_main);
          
      	//打开工作空间
  		m_workSpace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(dataPath + "SuperMapCloud.smwu");
        info.setType(WorkspaceType.SMWU);
        boolean result = m_workSpace.open(info);  
        if (!result) {
			
        	Toast.makeText(this, "工作空间打开失败！", Toast.LENGTH_LONG).show();
			
			m_workSpace.close();
			m_workSpace = null;
			
			return;
		}
        
        //将地图显示控件和工作空间关联
        m_mapView = (MapView)findViewById(R.id.Map_view);
        m_mapControl =  m_mapView.getMapControl();
        m_mapControl.getMap().setWorkspace(m_workSpace);
        
        //打开工作空间中的地图。参数0表示第一张地图
        String mapName = m_workSpace.getMaps().get(0);
        result = m_mapControl.getMap().open(mapName);
        if (!result) {
        	
        	Toast.makeText(this, "地图打开失败！", Toast.LENGTH_LONG).show();
        	
        	m_mapControl.dispose();
        	m_workSpace.close();
        	m_mapControl = null;
        	m_mapView = null;
        	m_workSpace = null;
        	
			return;
		}
        
        //加载点符号库
        String libraryJB = dataPath + "Symbol/JB.plot";
        libID_JB = m_mapControl.addPlotLibrary(libraryJB);
        if(libID_JB < 0)
        {
        	Toast.makeText(this, "加载点符号库失败！", Toast.LENGTH_LONG).show();
        	return;
        }
        //加载线符号库
        String libraryTY = dataPath + "Symbol/TY.plot";
        libID_TY = m_mapControl.addPlotLibrary(libraryTY);
        if(libID_TY < 0)
        {
        	Toast.makeText(this, "加载线符号库失败！", Toast.LENGTH_LONG).show();
        	return;
        }
        
        //设置GridView不可见
        m_gridViewPoint = (GridView)findViewById(R.id.gridviewPoint);
        m_gridViewPoint.setVisibility(View.INVISIBLE);
        m_gridViewLine = (GridView)findViewById(R.id.gridviewLine);
        m_gridViewLine.setVisibility(View.INVISIBLE);
        
        //获取点符号数据并配置适配器
        m_dataListPoint = new ArrayList<Map<String, Object>>();
      	getPointData();	//获取数据
      	String [] from ={"image","text"};
      	int [] to = {R.id.image,R.id.text};
      	SimpleAdapter simpleAdaperPoint = new SimpleAdapter(this, m_dataListPoint, R.layout.item, from, to);//新建适配器
      	m_gridViewPoint.setAdapter(simpleAdaperPoint);//配置适配器
      	
      	//获取线符号数据并配置适配器
      	m_dataListLine = new ArrayList<Map<String, Object>>();
       	getLineData();	//获取数据
       	SimpleAdapter simpleAdaperLine = new SimpleAdapter(this, m_dataListLine, R.layout.item, from, to);
       	m_gridViewLine.setAdapter(simpleAdaperLine);//配置适配器
       
       	
       	m_gridViewPoint.setOnItemClickListener(new OnItemClickListener(){
       		@Override
       		public void onItemClick(AdapterView<?> parent, View view, int position,
       		long id) {

       			String name = iconNamePoint[position];//根据点击的位置，获取符号名称
       			int symbolCode = Integer.parseInt(name);//将符号名称转为整数型
       			m_mapControl.setPlotSymbol(libID_JB, symbolCode);//设置需要绘制的态势标绘符号
       			m_mapControl.setAction(Action.CREATEPLOT);//设置地图状态为态势标绘状态
       		}
       	});	
       	
       	m_gridViewLine.setOnItemClickListener(new OnItemClickListener(){
       		@Override
       		public void onItemClick(AdapterView<?> parent, View view, int position,
       		long id) {

       			String name = iconNameLine[position];//根据点击的位置，获取符号名称
       			int symbolCode = Integer.parseInt(name);//将符号名称转为整数型
       			m_mapControl.setPlotSymbol(libID_TY, symbolCode);//设置需要绘制的态势标绘符号
       			m_mapControl.setAction(Action.CREATEPLOT);//设置地图状态为态势标绘状态
       		}
       	});	
       	
       	//设置编辑图层，态势标绘需要标绘在CAD类型的图层
       	Layers layers = m_mapControl.getMap().getLayers();
       	Layer layer = layers.get("CAD@Edit");
        layer.setEditable(true);  
        
		m_mapControl.addGeometrySelectedListener(geoSelectedListener);//添加对象选中监听器。
		
    }
	
	//对象选中监听器
	private GeometrySelectedListener geoSelectedListener = new GeometrySelectedListener() {
		
		@Override
		public void geometrySelected(GeometrySelectedEvent arg0) {
			// TODO Auto-generated method stub
			m_mapControl.setAction(Action.VERTEXEDIT);//选中对象后，地图切换到编辑对象节点的状态
		}

		@Override
		public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void geometryMultiSelectedCount(int i) {

		}
	};
	
	//获取点标绘数据
	public List<Map<String, Object>> getPointData(){		
		
		for(int i=0;i<iconPoint.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", iconPoint[i]);
			map.put("text", iconNamePoint[i]);
			m_dataListPoint.add(map);
		}	
		return m_dataListPoint;
	}
	
	//获取线标绘数据
	public List<Map<String, Object>> getLineData(){		
		
		for(int i=0;i<iconLine.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", iconLine[i]);
			map.put("text", iconNameLine[i]);
			m_dataListLine.add(map);
		}
			
		return m_dataListLine;
	}
	
  //“点标绘”按钮
  public void btnPoint_Click(View view){
  	
	  if(m_gridViewPoint.getVisibility() == view.VISIBLE){
		  m_gridViewPoint.setVisibility(view.GONE);
	  }
	  else{
		  m_gridViewPoint.setVisibility(view.VISIBLE);
	  }
	  if(m_gridViewLine.getVisibility() == view.VISIBLE){
		  m_gridViewLine.setVisibility(view.GONE);
	  }
  }
  //“箭头标绘”按钮
  public void btnSymbol_Click(View view){
	  
	  if(m_gridViewLine.getVisibility() == view.VISIBLE){
		  m_gridViewLine.setVisibility(view.GONE);
	  }
	  else{
		  m_gridViewLine.setVisibility(view.VISIBLE);
	  }
	  if(m_gridViewPoint.getVisibility() == view.VISIBLE){
		  m_gridViewPoint.setVisibility(view.GONE);
	  }
  }
  //“编辑符号”按钮
  public void btnEdit_Click(View view){
	  
	  m_mapControl.setAction(Action.SELECT);
  }
  //“删除符号”按钮
  public void btnDelete_Click(View view){
	  
	  Geometry geo = m_mapControl.getCurrentGeometry();
		if(geo != null){
			geo.dispose();
			m_mapControl.deleteCurrentGeometry();
			m_mapControl.submit();
			m_mapControl.setAction(Action.PAN);
			m_mapControl.getMap().refresh();
		} else { 

			m_mapControl.setAction(Action.SELECT);
			Toast.makeText(this, "没有选择对象！", Toast.LENGTH_LONG).show();
		}
  }
  //“重做”按钮
  public void btnRedo_Click(View view){
  	
	  m_mapControl.redo();
	  m_mapControl.getMap().refresh();
  }
  //“撤销”按钮
  public void btnUndo_Click(View view){
  	
	  m_mapControl.undo();
	  m_mapControl.getMap().refresh();
  }
  //“取消”按钮
  public void btnCancel_Click(View view){
  	
	  m_mapControl.cancel();
	  m_mapControl.submit();
  }
  //“提交”按钮
  public void btnSubmit_Click(View view){
	  
	  Geometry geometry = m_mapControl.getCurrentGeometry();//获取当前绘制的对象
		
	  if(geometry != null){
		  
		  geometry.dispose();	//释放对象所占用的资源。
		  
		  Layer layer = m_mapControl.getEditLayer();//获取当前编辑图层
		  if(layer != null){
			  
			  Dataset dataset = layer.getDataset();//获取编辑图层对应的数据集对象
			  DatasetType type = dataset.getType();//获取数据集的类型
			  if(type != DatasetType.CAD){//标绘符号只能标绘在CAD图层中
				  Toast.makeText(this, "当前编辑图层不是CAD图层，无法提交标绘符号！", Toast.LENGTH_LONG).show();
			  } else {
				  m_mapControl.submit();//提交操作，更新数据集中的正在编辑的对象
				  m_mapControl.setAction(Action.PAN);//地图切换到漫游状态
			  }
		  }
		  
	  } else {
		  
		  Toast.makeText(this, "没有编辑中的几何对象！", Toast.LENGTH_LONG).show();
	  }
  }
  //“清空”按钮
  public void btnClear_Click(View view){
  	
	  m_mapControl.cancel();//取消操作
	  
	  Layer layer = m_mapControl.getEditLayer();//获取当前编辑图层
	  DatasetVector datasetVector = (DatasetVector)layer.getDataset();//获取编辑图层对应的数据集对象
	  Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
	  if (recordset != null){
		  recordset.deleteAll();
		  recordset.update();
		  recordset.dispose();
		  recordset = null;
	  }
	  m_mapControl.getMap().refresh();
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
