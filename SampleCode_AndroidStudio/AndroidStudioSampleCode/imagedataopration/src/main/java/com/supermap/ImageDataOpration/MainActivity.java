package com.supermap.ImageDataOpration;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.supermap.analyst.spatialanalyst.RasterClip;
import com.supermap.analyst.spatialanalyst.RasterClipFileType;
import com.supermap.data.DataConversion;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetImage;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoRegion;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.ImageStretchOption;
import com.supermap.mapping.ImageStretchType;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingImage;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:遥感影像数据处理
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
 * 1、Demo简介： 对遥感影像数据操作。
 * 2、Demo数据：数据目录："SampleData/DataImage/tifData" 
 * 地图数据："World.smwu", "World.udb","World.udd" 
 * 许可目录："/SuperMap/License/" 
 * 3、关键类型/成员: 
 * DataConversion.importTIF(); 方法 
 * DataConversion.exportTIF(); 方法
 * RasterClip.clip(); 方法 
 * RasterClip.split(); 方法
 * LayerSettingImage.setDisplayBandIndexes();方法
 * LayerSettingImage.getDisplayBandIndexes();方法
 * LayerSettingImage.setImageStretchOption();方法
 * ImageStretchOption.setStretchType(); 方法
 * 
 * 4、功能展示:
 * (1)遥感影像数据导入
 * (2)遥感影像数据导出
 * (3)波段显示
 * (4)拉伸类型
 * (5)裁切数据
 * (6)切割数据
 * (7)裁剪数据为影像数据
 * --------------------------------------------
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
	private String sdCard = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath();
	private Workspace mWorkspace;
	private MapView mMapView;
	private MapControl mMapControl;
	private Button btnExport;
	private Button btnImport;
	private Button btnCutting;
	private Button btnCutImage;
	private Button btnCutDataset;
	private Button btnband;
	private Button btnstretching;
	private String tifImportPath = sdCard
			+ "/SampleData/DataImage/tifData/WorldEarth.tif";
	private String tifExportPath = sdCard
			+ "/SampleData/DataImage/tifData/tifTest.tif";
	private String cutImagePath = sdCard
			+ "/SampleData/DataImage/tifData/cutImageTest.tif";
	private Datasource udbDatasource;
	private Dataset dataset;
	private Layer mLayer;
	private boolean resultBoolean;
	private LayerSettingImage layerSettingImage;
	private String[] StringArrays;
	private int[] intArrays = new int[] {0, 1, 2};
	private Point2D point1;
	private Point2D point2;
	private Point2D point3;
	private Point2D point4;
	private Point2D point5;
	private Point2D point6;
	private Point2D point7;
	private double pointWidth;
	private double poinTop;
	private double poinBottom;
	private double poinLeft;
	private Dataset resultdataset;
	private Point2D[] point2d;
	private Point2Ds point2Ds;
	private GeoRegion region;
	private GeoLine Line;
	private boolean[] areaStates;
	private ListView listview;

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
		Environment.initialization(this);
//		Environment.setOpenGLMode(false);
		Environment.setLicensePath(sdCard + "/SuperMap/license");
		//设置横屏
		if( getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		setContentView(R.layout.activity_main);
		//初始化视图控件
		initView();
		//打开地图
		openMap();
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
	 * 打开地图
	 */
	private void openMap() {
		mWorkspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(sdCard + "/SampleData/DataImage/tifData/TIF.smwu");
		info.setType(WorkspaceType.SMWU);
		mWorkspace.open(info);
		// 将地图显示控件和工作空间关联
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapControl = mMapView.getMapControl();
		mMapControl.getMap().setWorkspace(mWorkspace);
		mMapControl.getMap().open("World");
		mMapControl.getMap().refresh();
		
		// 因为地图已经打开,所以udb也相应打开,所以通过获取地图,然后获取数据源的方式,拿到数据源
		udbDatasource = mMapControl.getMap().getWorkspace().getDatasources()
				.get("tif");
		
		//当前编辑的图层
		mLayer = mMapControl.getMap().getLayers().get(0);
		mLayer.setVisible(true);
		mLayer.setEditable(true);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		btnImport = (Button) findViewById(R.id.btn_import);
		btnExport = (Button) findViewById(R.id.btn_export);
		btnCutting = (Button) findViewById(R.id.btn_cutting);
		btnCutImage = (Button) findViewById(R.id.btn_Cut_Image);
		btnCutDataset = (Button) findViewById(R.id.btn_Cut_Dataset);
		btnband = (Button) findViewById(R.id.btn_band);
		btnstretching = (Button) findViewById(R.id.btn_stretching);
		btnImport.setOnClickListener(this);
		btnExport.setOnClickListener(this);
		btnCutting.setOnClickListener(this);
		btnCutImage.setOnClickListener(this);
		btnCutDataset.setOnClickListener(this);
		btnband.setOnClickListener(this);
		btnstretching.setOnClickListener(this);
		
		findViewById(R.id.btnStretch2).setOnClickListener(this);
		findViewById(R.id.btnStretch3).setOnClickListener(this);
		findViewById(R.id.btnStretch4).setOnClickListener(this);
		findViewById(R.id.btnStretch5).setOnClickListener(this);
		findViewById(R.id.btnStretch6).setOnClickListener(this);
		findViewById(R.id.btnStretch7).setOnClickListener(this);
		
		findViewById(R.id.btnClip2).setOnClickListener(this);
		findViewById(R.id.btnClip3).setOnClickListener(this);
		findViewById(R.id.btnClip4).setOnClickListener(this);
		
		findViewById(R.id.btnControlDisplay2).setOnClickListener(this);
		findViewById(R.id.btnControlDisplay3).setOnClickListener(this);
		findViewById(R.id.btnControlDisplay4).setOnClickListener(this);
		
		//save the Setting params
		findViewById(R.id.btnSetting4).setOnClickListener(this);
	}

	/**
	 * 按钮点击选择
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_import: // 导入影像数据
			//importImageData();
			break;
		case R.id.btn_export: // 导出数据集
			//exportDataset();
			break;
		case R.id.btn_band:// 设置波段样式
			//bandStyle();
			break;
		case R.id.btn_stretching:// 设置拉伸类型
			//stretchingType();
			
			if( findViewById(R.id.tvSetting2).getVisibility() == View.INVISIBLE )
			{
				findViewById(R.id.tvSetting2).setVisibility(View.VISIBLE);
				findViewById(R.id.tvSetting3).setVisibility(View.VISIBLE);
				findViewById(R.id.EditText2).setVisibility(View.VISIBLE);
				findViewById(R.id.EditText3).setVisibility(View.VISIBLE);
				findViewById(R.id.btnSetting4).setVisibility(View.VISIBLE);
			}
			else
			{
				findViewById(R.id.tvSetting2).setVisibility(View.INVISIBLE);
				findViewById(R.id.tvSetting3).setVisibility(View.INVISIBLE);
				findViewById(R.id.EditText2).setVisibility(View.INVISIBLE);
				findViewById(R.id.EditText3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnSetting4).setVisibility(View.INVISIBLE);
			}
			break;
		//保存设置参数
		case R.id.btnSetting4:
			
			break;
		case R.id.btn_cutting:// 切割数据集
			//cuttingDataset();
			
			if( findViewById(R.id.btnClip2).getVisibility() == View.VISIBLE )
			{
				findViewById(R.id.btnClip2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnClip3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnClip4).setVisibility(View.INVISIBLE);
			}
			
			if( findViewById(R.id.btnStretch2).getVisibility() == View.VISIBLE )
			{
				findViewById(R.id.btnStretch2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch4).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch5).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch6).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch7).setVisibility(View.INVISIBLE);
			}
			
			if( findViewById(R.id.btnControlDisplay2).getVisibility() == View.INVISIBLE )
			{
				findViewById(R.id.btnControlDisplay2).setVisibility(View.VISIBLE);
				findViewById(R.id.btnControlDisplay3).setVisibility(View.VISIBLE);
				findViewById(R.id.btnControlDisplay4).setVisibility(View.VISIBLE);
				mMapControl.getMap().refresh();
			}
			else
			{
				findViewById(R.id.btnControlDisplay2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnControlDisplay3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnControlDisplay4).setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.btnControlDisplay2:
			bandStyle(0);
			
			break;
		case R.id.btnControlDisplay3:
			bandStyle(1);
			
			break;
		case R.id.btnControlDisplay4:
			bandStyle(2);
			
			break;
		case R.id.btn_Cut_Dataset:// 裁剪为数据集
			//cutDataset();
			if( findViewById(R.id.btnStretch2).getVisibility() == View.VISIBLE )
			{
				findViewById(R.id.btnStretch2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch4).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch5).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch6).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch7).setVisibility(View.INVISIBLE);
			}
			
			if( findViewById(R.id.btnControlDisplay2).getVisibility() == View.VISIBLE )
			{
				findViewById(R.id.btnControlDisplay2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnControlDisplay3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnControlDisplay4).setVisibility(View.INVISIBLE);
			}

			if( findViewById(R.id.btnClip2).getVisibility() == View.INVISIBLE )
			{
				findViewById(R.id.btnClip2).setVisibility(View.VISIBLE);
				findViewById(R.id.btnClip3).setVisibility(View.VISIBLE);
				findViewById(R.id.btnClip4).setVisibility(View.VISIBLE);
				mMapControl.getMap().refresh();
			}
			else
			{
				findViewById(R.id.btnClip2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnClip3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnClip4).setVisibility(View.INVISIBLE);
			}
			break;
		//裁剪到数据集
		case R.id.btnClip2:
			cutDataset();
			
			break;
		//裁剪到文件
		case R.id.btnClip3:
			cutImage();
			
			break;
		//影像切割
		case R.id.btnClip4:
			cuttingDataset();
			
			break;
		case R.id.btn_Cut_Image:// 裁剪为影像数据
			//cutImage();
			
			if( findViewById(R.id.btnClip2).getVisibility() == View.VISIBLE )
			{
				findViewById(R.id.btnClip2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnClip3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnClip4).setVisibility(View.INVISIBLE);
			}
			
			if( findViewById(R.id.btnControlDisplay2).getVisibility() == View.VISIBLE )
			{
				findViewById(R.id.btnControlDisplay2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnControlDisplay3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnControlDisplay4).setVisibility(View.INVISIBLE);
			}

			if( findViewById(R.id.btnStretch2).getVisibility() == View.INVISIBLE )
			{
				findViewById(R.id.btnStretch2).setVisibility(View.VISIBLE);
				findViewById(R.id.btnStretch3).setVisibility(View.VISIBLE);
				findViewById(R.id.btnStretch4).setVisibility(View.VISIBLE);
				findViewById(R.id.btnStretch5).setVisibility(View.VISIBLE);
				findViewById(R.id.btnStretch6).setVisibility(View.VISIBLE);
				findViewById(R.id.btnStretch7).setVisibility(View.VISIBLE);
				mMapControl.getMap().refresh();
			}
			else
			{
				findViewById(R.id.btnStretch2).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch3).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch4).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch5).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch6).setVisibility(View.INVISIBLE);
				findViewById(R.id.btnStretch7).setVisibility(View.INVISIBLE);
			}
			break;
		//高斯拉伸
		case R.id.btnStretch2:
			// 设置拉伸样式 高斯拉伸
			setGaussian();
			
			break;
		//最值拉伸
		case R.id.btnStretch3:
			setMinimummaximum();
			
			break;
		//标准差拉伸
		case R.id.btnStretch4:
			setStandarddeviation();
			
			break;
		//直方图匹配
		case R.id.btnStretch5:
			setHistogramspecification();
			
			break;
		//直方图均衡化
		case R.id.btnStretch6:
			setHistogramequalization();
		//无拉伸
		case R.id.btnStretch7:
			setNone();
			
			break;
			
		default:
			break;
		}
	}
	/**
	 * 判断数据集是否为空
	 */
	private boolean notNull(){
		dataset = udbDatasource.getDatasets().get("WorldEarth");
		if(dataset== null){
			Toast.makeText(getApplicationContext(), "请先导入数据", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	/**
	 * 拉伸类型选择
	 */
	private void stretchingType() {
		if(notNull()!=true){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("选择一种拉伸类型");
		// 指定下拉列表的显示数据
		final String[] cities = { "高斯拉伸", "直方图均衡", "直方图匹配", "最值拉伸", "无拉伸",
				"标准差拉伸" };
		// 设置一个下拉的列表选择项
		builder.setItems(cities, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 设置拉伸样式 高斯拉伸
					setGaussian();
					break;
				case 1:
					// 设置拉伸样式 直方图均衡
					setHistogramequalization();
					break;

				case 2:
					// 设置拉伸样式直方图匹配
					setHistogramspecification();
					break;

				case 3:
					// 设置拉伸样式 最值拉伸
					setMinimummaximum();
					break;

				case 4:
					// 设置拉伸样式 无拉伸
					setNone();
					break;

				case 5:
					// 设置拉伸样式 标准差拉伸
					setStandarddeviation();
					break;
				default:
					break;
				}
			}
		}).setNegativeButton("取消", null).create();
		builder.show();
	}

	
	/**
	 * 波段显示样式
	 */
	private void bandStyle(final int colorIndex) {
//		if(!notNull()){
//			return;
//		}
		listview = new ListView(getApplicationContext());
		StringArrays = new String[] { "Band1", "Band2", "Band3" };
		areaStates = new boolean[] { false, false, false };
		AlertDialog builder = new AlertDialog.Builder(this)
				.setTitle("选择显示波段")
				.setMultiChoiceItems(StringArrays, areaStates,
						new OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						for (int i = 0; i < intArrays.length; i++) {
							if (listview.getCheckedItemPositions().get(i)) {
								intArrays[colorIndex] = i;
							}
						}
						layerSettingImage = (LayerSettingImage) mLayer
								.getAdditionalSetting();
						layerSettingImage.setDisplayBandIndexes(intArrays);
						
						mMapControl.getMap().refresh();
						Toast.makeText(getApplicationContext(), "设置显示波段成功",
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("取消", null).create();
		listview = builder.getListView();
		builder.show();
	}

	/**
	 * 切割数据集
	 */
	private void cuttingDataset() {
//		if(!notNull()){
//			return;
//		}
		cutLine();
		Datasets datasets = udbDatasource.getDatasets();
		dataset = mLayer.getDataset();
		if(dataset.getName().equals("LeftDataName"))
			return; // 已经切割过
		
		if(datasets.contains("LeftDataName")){
			datasets.delete("LeftDataName");
		}
		if (datasets.contains("RightDataName")) {
			datasets.delete("RightDataName");
		}
		
		resultBoolean = RasterClip.split(dataset, Line, udbDatasource,
				"LeftDataName", "RightDataName");
		if (resultBoolean) {
			mMapControl.getMap().close();
			mLayer = mMapControl.getMap().getLayers().add(datasets.get("LeftDataName"), true);
			mMapControl.getMap().refresh();
			Toast.makeText(getApplicationContext(), "切割成功", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getApplicationContext(), "切割失败", Toast.LENGTH_SHORT)
					.show();
		}
		
		mMapControl.getMap().getTrackingLayer().clear();
	}

	/**
	 * 切割线
	 */
	private void cutLine() {
		point6 = new Point2D();
		point7 = new Point2D();
		pointWidth = mLayer.getDataset().getBounds().getWidth() / 2;//udbDatasource.getDatasets().get("WorldEarth").getBounds().getWidth() / 2;
		poinTop = mLayer.getDataset().getBounds().getTop();//udbDatasource.getDatasets().get("WorldEarth").getBounds().getTop();
		poinBottom = mLayer.getDataset().getBounds().getBottom();//udbDatasource.getDatasets().get("WorldEarth").getBounds().getBottom();
		poinLeft = mLayer.getDataset().getBounds().getLeft();//udbDatasource.getDatasets().get("WorldEarth").getBounds().getLeft();
		point6.setX(pointWidth + poinLeft);
		point6.setY(poinTop);
		point7.setX(pointWidth + poinLeft);
		point7.setY(poinBottom);
		point2d = new Point2D[] { point6, point7 };
		point2Ds = new Point2Ds();
		point2Ds.addRange(point2d);
		Line = new GeoLine();
		Line.addPart(point2Ds);
		
		mMapControl.getMap().getTrackingLayer().add(Line, "line");
		mMapControl.getMap().refresh();
	}

	/**
	 * 裁剪为影像数据
	 */
	private void cutImage() {
//		if(!notNull()){
//			return;
//		}
		
		DatasetImage image = (DatasetImage)mLayer.getDataset();//(DatasetImage) udbDatasource.getDatasets().get("WorldEarth");
		regionSet();
		resultBoolean = RasterClip.clip(image, region, true, cutImagePath,
				RasterClipFileType.TIF);
		if (resultBoolean) {
			Toast.makeText(getApplicationContext(), "裁剪影像数据成功: " + cutImagePath,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "裁剪影像数据失败",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 裁剪为数据集
	 */
	private void cutDataset() {
//		if(!notNull()){
//			return;
//		}
		regionSet();
		Datasets datasets = udbDatasource.getDatasets();
		dataset = mLayer.getDataset();//udbDatasource.getDatasets().get("WorldEarth");
		if(dataset.getName().equals("cutDataset"))
			return; // 已经裁剪过了
		
		if(datasets.contains("cutDataset")){
			datasets.delete("cutDataset");
		}
		
		resultdataset = RasterClip.clip(dataset, region, true, true,
				udbDatasource, "cutDataset");
		if (resultdataset != null) {
			mMapControl.getMap().getLayers().remove("WorldEarth@WorldEarth");
			dataset = udbDatasource.getDatasets().get("cutDataset");
			//addMap();
			mLayer = mMapControl.getMap().getLayers().add(dataset, true);
			mLayer.setVisible(true);
			mLayer.setEditable(true);
			mMapControl.getMap().refresh();
			
			Toast.makeText(getApplicationContext(), "裁剪为数据集成功",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "裁剪为数据集失败",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 裁剪矩形区域
	 */
	private void regionSet() {
		point1 = new Point2D();
		point2 = new Point2D();
		point3 = new Point2D();
		point4 = new Point2D();
		point5 = new Point2D();
		point1.setX(20.34);
		point1.setY(20.34);
		point2.setX(100.34);
		point2.setY(20.34);
		point3.setX(100.34);
		point3.setY(100.34);
		point4.setX(20.34);
		point4.setY(100.34);
		point5.setX(20.34);
		point5.setY(20.34);
		point2d = new Point2D[] { point1, point2, point3, point4, point5 };
		point2Ds = new Point2Ds();
		point2Ds.addRange(point2d);
		region = new GeoRegion();
		region.addPart(point2Ds);
	}

	/**
	 * 导出数据源
	 */
	private void exportDataset() {
		if(!notNull()){
			return;
		}
		try {
			resultBoolean = DataConversion.exportTIF(tifExportPath, dataset);
			if (resultBoolean == true) {
				dataset = udbDatasource.getDatasets().get("WorldEarth");
				addMap();
				Toast.makeText(getApplicationContext(), "影像数据导出成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "影像数据导出失败",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 导入影像数据
	 */
	private void importImageData() {
		try {
			Datasets datasets =udbDatasource.getDatasets();
			if(datasets.contains("WorldEarth")){
				datasets.delete("WorldEarth");
			}
			
			resultBoolean = DataConversion.importTIF(tifImportPath,
					udbDatasource);
			if (resultBoolean == true) {
				dataset = udbDatasource.getDatasets().get("WorldEarth");
				addMap();
				Toast.makeText(getApplicationContext(), "影像数据导入成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "影像数据导入失败",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将数据集添加到地图图层显示
	 */
	private void addMap() {
		mLayer = mMapControl.getMap().getLayers().add(dataset, true);
		mLayer.setVisible(true);
		mLayer.setEditable(true);
		mMapControl.getMap().refresh();
	}

	/**
	 * 设置高斯拉伸
	 */
	public void setGaussian() {
		ImageStretchOption imageStretchOption = new ImageStretchOption();
		// 获取图层显示模式
		layerSettingImage = (LayerSettingImage) mLayer.getAdditionalSetting();
		// 设置拉伸样式 高斯拉伸
		imageStretchOption.setStretchType(ImageStretchType.GAUSSIAN);
		// 设置图层拉伸样式
		layerSettingImage.setImageStretchOption(imageStretchOption);
		// 刷新图层;
		mMapControl.getMap().refresh();
		Toast.makeText(getApplicationContext(), "设置拉伸样式成功", Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 设置直方图均衡
	 */
	public void setHistogramequalization() {
		ImageStretchOption imageStretchOption = new ImageStretchOption();
		layerSettingImage = (LayerSettingImage) mLayer.getAdditionalSetting();
		imageStretchOption
				.setStretchType(ImageStretchType.HISTOGRAMEQUALIZATION);
		layerSettingImage.setImageStretchOption(imageStretchOption);
		mMapControl.getMap().refresh();
		Toast.makeText(getApplicationContext(), "设置拉伸样式成功", Toast.LENGTH_SHORT)
				.show();

	}

	/**
	 * 设置直方图匹配
	 */
	public void setHistogramspecification() {
		ImageStretchOption imageStretchOption = new ImageStretchOption();
		layerSettingImage = (LayerSettingImage) mLayer.getAdditionalSetting();
		imageStretchOption
				.setStretchType(ImageStretchType.HISTOGRAMSPECIFICATION);
		layerSettingImage.setImageStretchOption(imageStretchOption);
		mMapControl.getMap().refresh();
		Toast.makeText(getApplicationContext(), "设置拉伸样式成功", Toast.LENGTH_SHORT)
				.show();

	}

	/**
	 * 设置最值拉伸
	 */
	public void setMinimummaximum() {
		ImageStretchOption imageStretchOption = new ImageStretchOption();
		layerSettingImage = (LayerSettingImage) mLayer.getAdditionalSetting();
		imageStretchOption.setStretchType(ImageStretchType.MINIMUMMAXIMUM);
		layerSettingImage.setImageStretchOption(imageStretchOption);
		mMapControl.getMap().refresh();
		Toast.makeText(getApplicationContext(), "设置拉伸样式成功", Toast.LENGTH_SHORT)
				.show();

	}

	/**
	 * 设置无拉伸
	 */
	public void setNone() {
		ImageStretchOption imageStretchOption = new ImageStretchOption();
		layerSettingImage = (LayerSettingImage) mLayer.getAdditionalSetting();
		imageStretchOption.setStretchType(ImageStretchType.NONE);
		layerSettingImage.setImageStretchOption(imageStretchOption);
		mMapControl.getMap().refresh();
		Toast.makeText(getApplicationContext(), "设置拉伸样式成功", Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 设置标准差拉伸
	 */
	public void setStandarddeviation() {
		ImageStretchOption imageStretchOption = new ImageStretchOption();
		layerSettingImage = (LayerSettingImage) mLayer.getAdditionalSetting();
		imageStretchOption.setStretchType(ImageStretchType.STANDARDDEVIATION);
		layerSettingImage.setImageStretchOption(imageStretchOption);
		mMapControl.getMap().refresh();
		Toast.makeText(getApplicationContext(), "设置拉伸样式成功", Toast.LENGTH_SHORT)
				.show();

	}

}
