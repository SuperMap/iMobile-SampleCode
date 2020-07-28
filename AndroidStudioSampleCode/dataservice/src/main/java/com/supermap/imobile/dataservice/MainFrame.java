package com.supermap.imobile.dataservice;

import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.services.DataDownloadService;
import com.supermap.services.DataUploadService;
import com.supermap.services.FeatureSet;
import com.supermap.services.ResponseCallback;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:数据服务模块示范代码
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
 * 1、范例简介：示范如何使用数据服务模块
 * 2、示例数据：sdcard + "/SampleData/GeometryInfo/World.udb
 * 3、关键类型/成员: 
 * 		DataDownloadService
 * 		DataUploadService
 * 4、使用步骤：
 *   (1)点击下载按钮，下载数据集，提示下载成功或失败
 *   (2)点击删除按钮，删除数据集部分对象，提示删除成功或失败
 *   (3)点击上传按钮，上传数据集对象，提示上传成功或失败
 *   (4)点击更新按钮，更新数据集，提示更新成功或失败
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */

public class MainFrame extends Activity implements OnClickListener{
	
	private MapView m_mapView;
	private MapControl m_mapControl; // 地图显示控件		

	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	private final String filepath = sdcard + "/SampleData/GeometryInfo/World.udb";
	private Workspace m_workspace = null;
	private Datasource m_datasource = null;
	private Activity m_Activity = null;
	private String mUrl = null;
	private String mUrlDatasets = null;
	private String datasetName = null;
	private String datasourceName = null;
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
        // 设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        // 初始化环境
        Environment.initialization(this);
        
        setContentView(R.layout.main);
        mUrl = "http://192.168.18.140:8090/iportal";
        mUrlDatasets = "http://192.168.18.140:8090/iportal/services/data-multimedia/rest/data/datasources/multimedia/datasets/";
        datasetName = "Capitals";
        datasourceName = "multimedia";
        openDatasource();
        
        openMap();
        
        //初始化窗体
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

	// 打开工作空间
    private boolean openDatasource(){
		
		m_workspace = new Workspace();
		DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
		connectionInfo.setEngineType(EngineType.UDB);
		connectionInfo.setServer(filepath);
		
		m_datasource = m_workspace.getDatasources().open(connectionInfo);
		
		if(m_datasource != null)
			return true;
		
		return false;
    }
    
    private boolean openMap(){  	
    	 // 将地图显示空间和 工作空间关联    
    	m_mapView = (MapView)findViewById(R.id.mapview);
    	m_mapControl = m_mapView.getMapControl();
    			   
    	m_mapControl.getMap().setWorkspace(m_workspace);
    	
    	if(m_datasource != null){
    		m_mapControl.getMap().getLayers().add(m_datasource.getDatasets().get("Countries"), true);
    		m_mapControl.getMap().getLayers().add(m_datasource.getDatasets().get("Capitals"), true);
    		m_mapControl.getMap().refresh();
    		return true;
    	}
    	else{
			Toast toast = Toast.makeText(m_Activity, "打开地图失败", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
    	}
        return false;
    }
    
    //初始化窗体
    private void initView(){
        ((Button) findViewById(R.id.Button01)).setOnClickListener(this);
        ((Button) findViewById(R.id.Button02)).setOnClickListener(this);
        ((Button) findViewById(R.id.Button03)).setOnClickListener(this);	
        ((Button) findViewById(R.id.Button04)).setOnClickListener(this);
        
        m_Activity = this;
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
        //上传数据集
		case R.id.Button01:
			uploadService();
			break;
		//下载数据集
		case R.id.Button02:
			downloadService();
			break;
	    //更新数据集
		case R.id.Button03:
		    updateService();
			break;
		//删除数据集对象
		case R.id.Button04:
			deleteDataset();
			break;
		default:
			break;
		}
	}

	
	//下载数据集
	public void downloadService(){
		if(m_datasource == null)
			return ;

		m_datasource.getDatasets().delete("Lakes_Table");
		m_datasource.getDatasets().delete("Lakes");
		final ProgressDialog progress = new ProgressDialog(MainFrame.this);
		
		String urlServer  = mUrl;
		String urlDataset = mUrlDatasets + datasetName;
		DataDownloadService downloadService = new DataDownloadService(urlServer);
		downloadService.setResponseCallback(new ResponseCallback(){

			@Override
			public void requestFailed(String errorMsg) {
				// TODO Auto-generated method stub
				Log.e("Download", errorMsg);
				progress.dismiss();

				Toast toast = Toast.makeText(m_Activity, "下载失败", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}

			@Override
			public void requestSuccess() {
				Log.i("Download", "requestSuccess");
			}

			@Override
			public void receiveResponse(FeatureSet result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dataServiceFinished(String arg0) {
				// TODO Auto-generated method stub

				Toast toast = Toast.makeText(m_Activity, "下载成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				progress.dismiss();
				m_mapControl.getMap().refresh();
			}

			@Override
			public void addFeatureSuccess(int arg0) {
				// TODO Auto-generated method stub
				
			}

		});
		//显示下载服务执行的进度条，回调里面销毁
		progress.setMessage("下载中...");
		progress.show();
		
		downloadService.downloadDataset(urlDataset, m_datasource);
	}
	
	//更新数据集
	public void updateService(){

		if(m_datasource == null)
			return;
		
		final ProgressDialog progress = new ProgressDialog(MainFrame.this);
		
		String urlServer  = mUrl;
		String urlDataset = mUrlDatasets + datasetName;
		DataDownloadService downloadService = new DataDownloadService(urlServer);
		downloadService.setResponseCallback(new ResponseCallback(){

			@Override
			public void requestFailed(String errorMsg) {
				// TODO Auto-generated method stub
				Log.e("Update: ", errorMsg);
				progress.dismiss();
				
				Toast toast = Toast.makeText(m_Activity, "更新失败", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}

			@Override
			public void requestSuccess() {
				// TODO Auto-generated method stub
				System.out.println("更新完成");
			}

			@Override
			public void receiveResponse(FeatureSet result) {
				// TODO Auto-generated method stub
			}

			@Override
			public void dataServiceFinished(String arg0) {
				// TODO Auto-generated method stub

				Toast toast = Toast.makeText(m_Activity, "更新成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				progress.dismiss();
				m_mapControl.getMap().refresh();
			}

			@Override
			public void addFeatureSuccess(int arg0) {
				// TODO Auto-generated method stub
				
			}

		});
		//显示更新服务执行的进度条，回调里面销毁
		progress.setMessage("更新中...");
		progress.show();
		
		downloadService.updateDataset(urlDataset, (DatasetVector)m_datasource.getDatasets().get(datasetName));
	}
	
	//上传数据集
	public void uploadService(){

		final ProgressDialog progress = new ProgressDialog(MainFrame.this);
		
		modifiedDataset(2);
		
		DatasetVector dataset = (DatasetVector)m_datasource.getDatasets().get(datasetName);
		String urlServer  = mUrl;
		String urlDataset = mUrlDatasets + datasetName;
		DataUploadService uploadService = new DataUploadService(urlServer);
		uploadService.setResponseCallback(new ResponseCallback(){

			@Override
			public void requestFailed(String errorMsg) {
				// TODO Auto-generated method stub
				Log.e("Upload: ", errorMsg);
				progress.dismiss();

				Toast toast = Toast.makeText(m_Activity, "上传失败", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}

			@Override
			public void requestSuccess() {
			}

			@Override
			public void receiveResponse(FeatureSet result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dataServiceFinished(String arg0) {
				// TODO Auto-generated method stub

				Toast toast = Toast.makeText(m_Activity, "上传成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				progress.dismiss();
				m_mapControl.getMap().refresh();
			}

			@Override
			public void addFeatureSuccess(int arg0) {
				// TODO Auto-generated method stub
				
			}

		});
		//显示上传服务执行的进度条，回调里面销毁
		progress.setMessage("上传中...");
		progress.show();
		
		uploadService.commitDataset(urlDataset, dataset);
	}
	
	//修改已经关联到服务器的数据集
	protected void modifiedDataset(int type){
		//获取关联数据集
		DatasetVector dataset = (DatasetVector)m_datasource.getDatasets().get(datasetName);
		if(dataset == null)
			return;
		
		//获取记录集进行编辑
		Recordset recordset = dataset.getRecordset(false, CursorType.DYNAMIC);
		switch(type){
		case 1:
			if(!recordset.isEmpty()){
				while(!recordset.isEOF()){
					recordset.edit();
					recordset.setFieldValue("SMUSERID", 20);
					recordset.update();
					recordset.moveNext();
				}
			}
			recordset.update();
			recordset.dispose();
			break;
			
		case 2:
			if(!recordset.isEmpty()){
				GeoPoint point1 = new GeoPoint (7056.451, 34690.675);
				recordset.addNew(point1);
				recordset.update();
				recordset.moveNext();
				GeoPoint point2  = new GeoPoint (6254.780, 54220.603);
				recordset.addNew(point2);
				recordset.update();
			}
			
		default:
				break;
		}

	}
	
	//删除数据集部分对象
	protected void deleteDataset(){
		//设置该图层可编辑
		m_mapControl.getMap().getLayers().get(0).setEditable(true);
		//获取图层数据集
    	Dataset dataset = m_mapControl.getMap().getLayers().get(0).getDataset();
    	
    	DatasetVector datasetVector = (DatasetVector)dataset;

    	Recordset recordset = datasetVector.query("SmUserID<100", CursorType.DYNAMIC);
    	int count = recordset.getRecordCount();
	    if(recordset != null && recordset.getRecordCount() > 0){

	    	boolean bDelete = recordset.deleteAll();
	    	if(bDelete == true){

				Toast toast = Toast.makeText(m_Activity, "删除成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();

				m_mapControl.getMap().refresh();
	    	}
	    	else{

				Toast toast = Toast.makeText(m_Activity, "删除失败", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}
	    	
	    	//释放自己开辟的对象
	    	recordset.dispose();
	    }
	    else{
			Toast toast = Toast.makeText(m_Activity, "删除失败", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
	    }
	    
	}
	
}









