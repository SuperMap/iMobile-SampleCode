package com.supermap.dataconversion;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:数据导入导出
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为 SuperMap
 * iMobile 演示Demo的代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile
 * 演示Demo说明---------------------------
 * 
 * 1、Demo简介：数据导入地图数据集,导出数据集为数据类型。
 *  2、Demo数据：
 * 数据目录："SampleData/DataConversion/" 
 * 地图数据："World.smwu", "World.udb","World.udd"
 * 许可目录："/SuperMap/License/" 
 * 3、关键类型/成员: 
 * DataConversion.importDWG(); 方法
 * DataConversion.importDXF(); 方法 
 * DataConversion.importMIF(); 方法 
 * DataConversion.importTIF(); 方法 
 * DataConversion.importSHP(); 方法 
 * DataConversion.exportDWG(); 方法
 * DataConversion.exportDXF(); 方法
 * DataConversion.exportMIF(); 方法
 * DataConversion.exportSHP(); 方法
 * DataConversion.exportTIF(); 方法
 * 
 * 4、功能展示 (1)数据类型导入； (2)数据导出为数据集。 --------------------------------------------
 * ----------------------------------
 * ============================================================================>
 * </p>
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	private Workspace mWorkspace = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;
	private Button btnImport = null;
	private Button btnExport = null;
	private static String sdCard = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static String importDWG = sdCard
			+ "/SampleData/DataConversion/dwgImport.dwg";
	public static String importDXF = sdCard
			+ "/SampleData/DataConversion/dxfImport.dxf";
	public static String importTIF = sdCard
			+ "/SampleData/DataConversion/tifImport.tif";
	public static String importMIF = sdCard
			+ "/SampleData/DataConversion/mifImport.mif";
	public static String importSHP = sdCard
			+ "/SampleData/DataConversion/shpImport.shp";
	public static String importIMG = sdCard
			+ "/SampleData/DataConversion/imgImport.img";
	public static String exportDWG = sdCard
			+ "/SampleData/DataConversion/dwgExport.dwg";
	public static String exportDXF = sdCard
			+ "/SampleData/DataConversion/dxfExport.dxf";
	public static String exportTIF = sdCard
			+ "/SampleData/DataConversion/tifExport.tif";
	public static String exportMIF = sdCard
			+ "/SampleData/DataConversion/mifExport.mif";
	public static String exportSHP = sdCard
			+ "/SampleData/DataConversion/shpExport.shp";
	public static String exportIMG = sdCard
			+ "/SampleData/DataConversion/imgExport.img";

	private ImportData mImportData = null;
	private ExportData mExportData = null;
	// 只负责接受焦点
	private View anchorView = null;
	private Button btnZoomIn;
	private Button btnZoomOut;
	private Button btnViewEntire;
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
		// 设置许可路径
		Environment.setLicensePath(sdCard + "/SuperMap/license/");
		// 初始化环境
		Environment.initialization(this);
		Environment.setOpenGLMode(false);
		setContentView(R.layout.activity_main);
		initView();
		openMap();
		mImportData = new ImportData(mMapControl, MainActivity.this);
		mExportData = new ExportData(mMapControl, MainActivity.this);
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

	/**
	 * 初始化控件
	 */
	private void initView() {
		MapView mapView = (MapView) findViewById(R.id.mapView);
		mMapControl = mapView.getMapControl();
		btnImport = (Button) findViewById(R.id.btn_import);
		btnExport = (Button) findViewById(R.id.btn_export);
		btnZoomIn = (Button) findViewById(R.id.btnZoomIn);
		btnZoomOut = (Button) findViewById(R.id.btnZoomOut);
		btnViewEntire = (Button) findViewById(R.id.btnViewEntire);
		btnImport.setOnClickListener(this);
		btnExport.setOnClickListener(this);
		btnZoomIn.setOnClickListener(this);
		btnZoomOut.setOnClickListener(this);
		btnViewEntire.setOnClickListener(this);
		anchorView = findViewById(R.id.btn_import);

	}

	/**
	 * 打开地图
	 */
	private void openMap() {
		mWorkspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(sdCard + "/SampleData/DataConversion/World.smwu");
		info.setType(WorkspaceType.SMWU);
		mWorkspace.open(info);
		// 将地图显示控件和工作空间关联
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapControl = mMapView.getMapControl();
		mMapControl.getMap().setWorkspace(mWorkspace);
	}

	/**
	 * 选择需要点击的按钮
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_import:
			if (mImportData.isShowing()) {
				mImportData.dismiss();
			} else {
				mImportData.showImport(anchorView);
			}
			break;
		case R.id.btn_export:
			if (mExportData.isShowing()) {
				mExportData.dismiss();
			} else {
				mExportData.showExport(anchorView);
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
		default:
			break;
		}

	}

}
