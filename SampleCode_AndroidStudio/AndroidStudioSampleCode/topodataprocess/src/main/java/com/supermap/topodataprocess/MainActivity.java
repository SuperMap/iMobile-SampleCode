package com.supermap.topodataprocess;
/**
 * <p>
 * Title:拓扑数据处理
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
 *   	展示拓扑编辑和拓扑捕捉功能。
 *   
 * 2、Demo数据：
 * 		数据目录："../SampleData/TopoDataprocessData/"
 *      地图数据："TopoDataprocess.smwu", "TopoDataprocess.udb", "TopoDataprocess.udd"
 *      许可目录："../SuperMap/License/"
 *      
 * 3、关键类型/成员: 
 *    m_mapControl.setAction();						方法
 *    snapSetting.openDefault();					方法
 *    snapSetting.openAll();						方法
 *    snapClose.closeAll();							方法
 *    m_mapControl.setSnapSetting(snapSetting);		方法
 *    m_mapControl.undo();							方法
 *    m_mapControl.redo();							方法		
 *    TopoBuild.topoBuildRegion();					方法
 *
 * 4、功能展示
 *   (1)擦除、分割、合并、组合、求交、补充岛洞、填充岛洞
 *   (2)默认捕捉、全部捕捉、拓扑构面、回退、重做
 *   (3)绘制点、绘制线、绘制面、
 *   (4)选择对象、平移对象
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

import com.supermap.analyst.TopoBuild;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.SnapSetting;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity {
	
	private MapControl m_mapControl = null;
	private Workspace m_wokspace = null;
	private MapView m_mapView = null;
	
	final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	
	boolean m_bShowEditButtons = false;
	boolean m_bShowSnapButtons = false;
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
			requestPermissions();
        	//设置许可文件路径
        	Environment.setLicensePath(sdcard + "/SuperMap/license/");
      		//组件功能必须在 Environment 初始化之后才能调用
      		Environment.initialization(this);
            //关闭OpenGL模式 
      		Environment.setOpenGLMode(false);
      		
      		setContentView(R.layout.activity_main);
              
      		 //打开工作空间
            m_wokspace = new Workspace();
            WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
            info.setServer(sdcard+"/SampleData/TopoDataprocessData/TopoDataprocess.smwu");
            info.setType(WorkspaceType.SMWU);
            m_wokspace.open(info);
                  
            //将地图显示控件和工作空间关联
            m_mapView = (MapView)findViewById(R.id.Map_view);
            m_mapControl =  m_mapView.getMapControl();
            m_mapControl.getMap().setWorkspace(m_wokspace);
            
            //打开工作空间中的地图。参数0表示第一张地图
            String mapName = m_wokspace.getMaps().get(0);
            m_mapControl.getMap().open(mapName);
            
            //刷新地图        
            m_mapControl.getMap().refresh();
            
            //设置按钮不显示
            ShowEditButtons(false);
            ShowSnapButtons(false);
    }
    
    //“放大”按钮
    public void btnZoomIn_Click(View view){
    	
    	m_mapControl.getMap().zoom(2);
		m_mapControl.getMap().refresh();
    }
    //“缩小”按钮
    public void btnZoomOut_Click(View view){
    	
    	m_mapControl.getMap().zoom(0.5);
		m_mapControl.getMap().refresh();
    }
    //“全幅”按钮
    public void btnFullScreen_Click(View view){
    	
    	m_mapControl.getMap().viewEntire();
    }
    
    //"拓扑捕捉"按钮
    public void btnSnap_Click(View view){
    	
    	ShowSnapButtons(!m_bShowSnapButtons);
    }
    //"拓扑编辑"按钮
    public void btnEdit_Click(View view){
    	
    	ShowEditButtons(!m_bShowEditButtons);
    }
    //"绘制点"按钮
    public void btnDrawPoint_Click(View view){
    	
    	m_mapControl.setAction(Action.CREATEPOINT);
    	m_mapControl.getMap().getLayers().get("Point@TopoDataprocess").setEditable(true);
    }
  //"绘制线"按钮
    public void btnDrawLine_Click(View view){
    	
    	m_mapControl.setAction(Action.CREATEPOLYLINE);
    	m_mapControl.getMap().getLayers().get("Line@TopoDataprocess").setEditable(true);
    }
    //"绘制面"按钮
    public void btnDrawRegion_Click(View view){
    	
    	m_mapControl.setAction(Action.CREATEPOLYGON);
    	m_mapControl.getMap().getLayers().get("Region@TopoDataprocess").setEditable(true);
    }
    //"提交"按钮
    public void btnCommit_Click(View view){
    	
    	m_mapControl.submit();
    	m_mapControl.setAction(Action.PAN);
    }
    //"选择"按钮
    public void btnSelect_Click(View view){
    	
    	m_mapControl.setAction(Action.SELECT);
    }
    //"清空"按钮
    public void btnClear_Click(View view){
    	
    	m_mapControl.deleteCurrentGeometry();
    	m_mapControl.setAction(Action.SELECT);
    }
    //"平移"按钮
    public void btnMove_Click(View view){
    	
    	m_mapControl.setAction(Action.MOVE_GEOMETRY);
    	m_mapControl.getMap().refresh();
    }
  
    //"拓扑捕捉-默认捕捉"按钮
    public void btnSnapDefault_Click(View view){
    	
    	SnapSetting snapSetting = new SnapSetting();
		//打开默认捕捉模式
		snapSetting.openDefault();
		//捕捉设置
		m_mapControl.setSnapSetting(snapSetting);
    }
    //"拓扑捕捉-全面捕捉"按钮
    public void btnSnapAll_Click(View view){
    	
    	SnapSetting snapSetting = new SnapSetting();
    	//打开全部捕捉模式
		snapSetting.openAll();
		//捕捉设置
		m_mapControl.setSnapSetting(snapSetting);
    }
    //"拓扑捕捉-拓扑构面"按钮
    public void btnSnapToRegion_Click(View view){
    	
    	//获取线数据集
    	Dataset datasetLine = m_mapControl.getMap().getLayers().get("Line@TopoDataprocess").getDataset();
    	DatasetVector datasetRegion = (DatasetVector)m_mapControl.getMap().getLayers().get("Region@TopoDataprocess").getDataset();
		//线数据集拓扑生成面数据集
		boolean bTopoBuild = TopoBuild.topoBuildRegion(datasetLine, datasetRegion);
		if(bTopoBuild == true){
			System.out.println("succeed!");
		}
		
		m_mapControl.getMap().refresh();
    }
    //"拓扑捕捉-关闭捕捉"按钮
    public void btnSnapClose_Click(View view){
    	
    	//创建一个捕捉设置
		SnapSetting snapClose = new SnapSetting();
		//关闭所有捕捉模式
		snapClose.closeAll();
		//捕捉设置
		m_mapControl.setSnapSetting(snapClose);
    }
    //"拓扑捕捉-回退"按钮
    public void btnSnapUndo_Click(View view){
    	
    	m_mapControl.undo();
    }
    //"拓扑捕捉-重做"按钮
    public void btnSnapRedo_Click(View view){
    	
    	m_mapControl.redo();
    }
    
    //"拓扑编辑-面擦除"按钮
    public void btnEditRegionDivide_Click(View view){
    	
    	m_mapControl.setAction(Action.ERASE_REGION);
    }
    //"拓扑编辑-面分割"按钮
    public void btnEditRegionLineDivide_Click(View view){
    	
    	m_mapControl.setAction(Action.SPLIT_BY_LINE);
    }
    //"拓扑编辑-面合并"按钮
    public void btnEditRegionUnion_Click(View view){
    	
		m_mapControl.getMap().getLayers().get("Region@TopoDataprocess").setEditable(true);
    	m_mapControl.setAction(Action.UNION_REGION);
    }
    //"拓扑编辑-面组合"按钮
    public void btnEditRegionCompose_Click(View view){
    	
		m_mapControl.getMap().getLayers().get("Region@TopoDataprocess").setEditable(true);
    	m_mapControl.setAction(Action.COMPOSE_REGION);
    }
    //"拓扑编辑-面求交"按钮
    public void btnEditRegionIntersect_Click(View view){
		m_mapControl.getMap().getLayers().get("Region@TopoDataprocess").setEditable(true);
    	m_mapControl.setAction(Action.INTERSECT_REGION);
    }
    //"拓扑编辑-补充岛洞"按钮
    public void btnEditRegionIslandDivide_Click(View view){
    	
    	m_mapControl.setAction(Action.PATCH_HOLLOW_REGION);
    }
    //"拓扑编辑-填充岛洞"按钮
    public void btnEditRegionIslandFill_Click(View view){
    	
    	m_mapControl.setAction(Action.FILL_HOLLOW_REGION);
    }
    //"拓扑编辑-平移"按钮
    public void btnEditMoveGeometry_Click(View view){
    	
    	m_mapControl.setAction(Action.MOVE_GEOMETRY);
    }
    
    public void ShowEditButtons(boolean bShow){
    	
    	if(bShow){
    		findViewById(R.id.btnEditRegionDivide).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnEditRegionLineDivide).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnEditRegionUnion).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnEditRegionCompose).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnEditRegionIntersect).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnEditRegionIslandDivide).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnEditRegionIslandFill).setVisibility(View.VISIBLE);
    		
    		if(m_bShowSnapButtons){
    			ShowSnapButtons(false);
    		}
    	}
    	else{
    		findViewById(R.id.btnEditRegionDivide).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnEditRegionLineDivide).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnEditRegionUnion).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnEditRegionCompose).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnEditRegionIntersect).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnEditRegionIslandDivide).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnEditRegionIslandFill).setVisibility(View.INVISIBLE);
    	}
    	
    	m_bShowEditButtons = bShow;
    }
    
    public void ShowSnapButtons(boolean bShow){
    	
    	if(bShow){
    		findViewById(R.id.btnSnapDefault).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnSnapAll).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnSnapToRegion).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnSnapClose).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnSnapUndo).setVisibility(View.VISIBLE);
    		findViewById(R.id.btnSnapRedo).setVisibility(View.VISIBLE);
    		
    		if(m_bShowEditButtons){
    			ShowEditButtons(false);
    		}
    	}
    	else{
    		findViewById(R.id.btnSnapDefault).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnSnapAll).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnSnapToRegion).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnSnapClose).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnSnapUndo).setVisibility(View.INVISIBLE);
    		findViewById(R.id.btnSnapRedo).setVisibility(View.INVISIBLE);
    	}
    	
    	m_bShowSnapButtons = bShow;
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
