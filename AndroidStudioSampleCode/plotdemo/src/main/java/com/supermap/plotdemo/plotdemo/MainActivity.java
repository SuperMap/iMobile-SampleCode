package com.supermap.plotdemo.plotdemo;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.Toast;

import com.supermap.data.Color;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.GeoCircle;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicPolygon;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.plotdemo.R;


public class MainActivity extends Activity implements OnClickListener, OnTouchListener{

	private MapControl mMapControl;
	private Workspace  mWorkspace;
	private Map        mMap;
	private long libID_JY = -1;
	private long libID_TY = -1;
	private long libID    = -1;
	
	private EditPopup  editPopup   = null;
	private Builder    clearDialog = null;
	private Activity   mActivity   = null;
	private DatasetVector datasetCAD = null;
	
	private String TAG = "MainActivity";
	private String rootPath = null;
	private RadioButton focusButton = null;
	private ProgressDialog mProgress = null;
	private String toastMsg = null;
    private GridView mGridView = null;
    private ImageAdapter mImageAdapter1 = null;
    private ImageAdapter mImageAdapter2 = null;
    private List<? extends java.util.Map<String, String>> list1 = null;
    private List<? extends java.util.Map<String, String>> list2 = null;
	private boolean mExitEnable = false;
	private MyApplication myApp = null;
	
	boolean isTouchMapUp = true;
	
	private TencentLocTool mTencentLocation = null;
	private DynamicView m_locateDynamicView = null;
	
	private int m_LocationPolygonID = 0;
	private int m_LocationID = 0;
//	private Navigation mNavigation = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        myApp = MyApplication.getInstance();
        myApp.registerActivity(this);
        setContentView(R.layout.activity_main1);
        rootPath = MyApplication.SDCARD;

//        mTencentLocation = new TencentLocation(this);

		TencentLocTool.getInstance().init(this);
		mTencentLocation =TencentLocTool.getInstance();
        initView();
        
        prepareData();
    }
    
    @Override
    protected void onDestroy(){
    	mProgress.dismiss();
    	closeMap();
    	super.onDestroy();
    }
    /*
     * 准备地图数据
     */
	private void prepareData(){
		mProgress = new ProgressDialog(this);
		mProgress.setCancelable(false);
		mProgress.setMessage("数据加载中...");
		mProgress.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
		mProgress.show();
		// 子线程中配置地图数据，初始化地图
		new Thread(){
			@Override
			public void run() {
				super.run();
				//配置数据
				new DefaultDataConfig().autoConfig();
				initSymbolLibrary();           // 1.初始化符号库
				openMap();                     // 2.打开地图
				
				// 对UI的操作要在主线程进行, 显示错误信息
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 初始化其他视图
						initAlertDialog();
				        initPopupWidow();
				        initDrawingStyle();
				        if(m_locateDynamicView == null){
							m_locateDynamicView = new DynamicView(mMapControl.getContext(), mMapControl.getMap());
							((MapView) findViewById(R.id.v_MapView)).addDynamicView(m_locateDynamicView);
						}
						mProgress.dismiss();
						showTostInfo(toastMsg, true);
					}
				});
			}
		}.start();
	}
	
    /**
     * 初始化提示框
     */
	private void initAlertDialog() {
        clearDialog = new Builder(this);
        
        // 设置清空操作提示对话框
        clearDialog.setTitle("是否清空所有符号?");
        clearDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clearDataset();
			}
		});
        clearDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * Initilize popup window
	 */
    private void initPopupWidow() {
    	String pathSymbolIconJY = DefaultDataConfig.MapDataPath + this.getResources().getString(R.string.path_symbol_icon_JY);
        String pathSymbolIconTY = DefaultDataConfig.MapDataPath + this.getResources().getString(R.string.path_symbol_icon_TY);
      
         editPopup = new EditPopup(mMapControl, this);
         OnDismissListener onDismissListener = new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				if(((RadioButton)findViewById(R.id.btn_EditSymbol)).isChecked())
					focusButton.setChecked(true);             // when popup window is dismissed
			}
		};

         editPopup.setOnDismissListener(onDismissListener);

         FileScaner fileScaner = new FileScaner();
 	    
         list1 = fileScaner.getPictureList(pathSymbolIconJY);
 		 list2 = fileScaner.getPictureList(pathSymbolIconTY);
 		
 		GridView gridView1 = (GridView) findViewById(R.id.gridview1);
        GridView gridView2 = (GridView) findViewById(R.id.gridview2);
        OnItemClickListener itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				java.util.Map<String, String> symbol = null;
				if(libID == libID_JY)
				 symbol = list1.get(position);
				if(libID == libID_TY)
					 symbol = list2.get(position);
				String name = symbol.get("name");
				try {
					int symbolCode = Integer.parseInt(name);
					if (symbolCode >= 0) {
						mMapControl.setPlotSymbol(libID, symbolCode);
						mMapControl.setAction(Action.CREATEPLOT);
					}
				} catch (Exception e) {
					
				}
				((RadioButton)findViewById(R.id.btn_ArrowSymbol)).setChecked(false);
				((RadioButton)findViewById(R.id.btn_DotSymbol)).setChecked(false);
				findViewById(R.id.gridview1).setVisibility(View.GONE);
				findViewById(R.id.gridview2).setVisibility(View.GONE);
			}
		};
         if(list1 != null){
            mImageAdapter1 = new ImageAdapter(this, list1);
            gridView1.setAdapter(mImageAdapter1);
            gridView1.setOnItemClickListener(itemClickListener);
         }
         if(list2 != null){
            mImageAdapter2 = new ImageAdapter(this, list2);
            gridView2.setAdapter(mImageAdapter2);
            gridView2.setOnItemClickListener(itemClickListener);
         }
         
         
         
	}

    /**
     * Initialize symbol library
     */
	private void initSymbolLibrary() {
         String pathJY = DefaultDataConfig.MapDataPath + this.getResources().getString(R.string.path_libJY);
         String pathTY = DefaultDataConfig.MapDataPath + this.getResources().getString(R.string.path_libTY);
         libID_JY = mMapControl.addPlotLibrary(pathJY);
         libID_TY = mMapControl.addPlotLibrary(pathTY);
         
         if(libID_JY < 0)
        	 toastMsg += " 加载JY符号库失败";
         if(libID_TY < 0)
        	 toastMsg += " 加载TY符号库失败";
        
	}

	/**
	 * Open  workspace and map
	 * @return
	 */
	private boolean openMap() {
    	if(mMap == null){
    		mMap = mMapControl.getMap();
    		mWorkspace = mMap.getWorkspace();
    	}
		WorkspaceConnectionInfo workspaceInfo = new WorkspaceConnectionInfo();
		String wkPath = rootPath + this.getResources().getString(R.string.workspace_path0);
		wkPath= DefaultDataConfig.WorkspacePath;
		workspaceInfo.setServer(wkPath);
		if(wkPath.endsWith("smwu") || wkPath.endsWith("SMWU")){
			workspaceInfo.setType(WorkspaceType.SMWU);
		} else if(wkPath.endsWith("sxwu") || wkPath.endsWith("SXWU")){
			workspaceInfo.setType(WorkspaceType.SXWU);
		}
		
		boolean isOpened = mWorkspace.open(workspaceInfo);
		if(!isOpened){
			toastMsg += " 打开工作空间失败";
			mMap.close();
			mWorkspace.close();
			mWorkspace = null;
			mMap = null;
			return isOpened;
		}
		
		isOpened = mMap.open(mWorkspace.getMaps().get(0));
		if(!isOpened){
			toastMsg += " 打开地图失败";
			mMap.close();
			mWorkspace.close();
			mWorkspace = null;
			mMap = null;
			return false;
		} else {
			toastMsg = null;
		}
//		mMap.setFullScreenDrawModel(true);
		mMap.refresh();
		datasetCAD = (DatasetVector)mWorkspace.getDatasources().get("Edit").getDatasets().get("CAD");
		if(datasetCAD == null)
			toastMsg += " 没有从工作空间中获得名为CAD的数据集";
		return isOpened;
	}

	/**
     * Initialize buttons in main view
     */
	private void initView() {
		mMapControl = (MapControl) ((MapView) findViewById(R.id.v_MapView)).getMapControl();
//		mNavigation = mMapControl.getNavigation();
//		mNavigation.setEncryption(new SuperMapPatent());
		
		findViewById(R.id.btn_ArrowSymbol).setOnClickListener(this);
		findViewById(R.id.btn_Cancel).setOnClickListener(this);
		findViewById(R.id.btn_Clear).setOnClickListener(this);
		findViewById(R.id.btn_DotSymbol).setOnClickListener(this);
		findViewById(R.id.btn_EditSymbol).setOnClickListener(this);
		findViewById(R.id.btn_Redo).setOnClickListener(this);
		findViewById(R.id.btn_Submit).setOnClickListener(this);
		findViewById(R.id.btn_Undo).setOnClickListener(this);
		findViewById(R.id.btn_ViewEntire).setOnClickListener(this);
		findViewById(R.id.btn_ZoomIn).setOnClickListener(this);
		findViewById(R.id.btn_ZoomOut).setOnClickListener(this);
		findViewById(R.id.btn_Locate).setOnClickListener(this);
		
		focusButton = (RadioButton) findViewById(R.id.btn_receivefocus);
		
		mMapControl.setOnTouchListener(this);
	}
	/**
	 * Called when a view has been clicked.
	 */
	@Override
	public void onClick(View v) {
		
		if(!isTouchMapUp){
			focusButton.setChecked(true);
			return;
		}
			
		if(mMapControl == null || mMap == null)
			return;
		if(v.getId() != R.id.btn_EditSymbol && editPopup.isShowing())
			editPopup.dismiss();

		switch(v.getId()){
		case R.id.btn_DotSymbol:
			// 连续点击关闭符号list
			if(findViewById(R.id.gridview1).getVisibility() == View.VISIBLE) {
				findViewById(R.id.gridview1).setVisibility(View.GONE);
				focusButton.setChecked(true);
				break;
			}
			if(mImageAdapter1 == null){
				showTostInfo("点标绘的图片资源不存在", true);
				focusButton.setChecked(true);
				break;
			}

			libID = libID_JY;
			findViewById(R.id.gridview1).setVisibility(View.VISIBLE);
			findViewById(R.id.gridview2).setVisibility(View.GONE);
			break;
		
		case R.id.btn_ArrowSymbol:
			// 连续点击关闭符号list
			if(findViewById(R.id.gridview2).getVisibility() == View.VISIBLE) {
				findViewById(R.id.gridview2).setVisibility(View.GONE);
				focusButton.setChecked(true);
				break;
			}
			if(mImageAdapter2 == null){
				showTostInfo("箭头标绘的图片资源不存在", true);
				focusButton.setChecked(true);
				break;
			}

			libID = libID_TY;
			findViewById(R.id.gridview2).setVisibility(View.VISIBLE);
			findViewById(R.id.gridview1).setVisibility(View.GONE);
			break;
			
		case R.id.btn_EditSymbol:
			if(editPopup.isShowing()){
				editPopup.dismiss();
				focusButton.setChecked(true);
			}else {
				editPopup.show(v);
			}
			break;
			
		case R.id.btn_Cancel:
			mMapControl.cancel();
			mMapControl.submit();
			focusButton.setChecked(true);
			break;
			
		case R.id.btn_Clear:
			clearDialog.create().show();
			focusButton.setChecked(true);
			mMap.refresh();

			break;
			
		case R.id.btn_Redo:
			mMapControl.redo();
			mMap.refresh();
			focusButton.setChecked(true);
			break;
			
		case R.id.btn_Submit:
			if(checkSubmit()){
				boolean istrue = mMapControl.submit();
				mMapControl.setAction(Action.PAN);
			}
			focusButton.setChecked(true);
			break;
		case R.id.btn_Undo:
			mMapControl.undo();
			mMap.refresh();
			focusButton.setChecked(true);

			break;
			
		case R.id.btn_ViewEntire:
			mMap.viewEntire();

			break;
			
		case R.id.btn_ZoomIn:
			mMap.zoom(2);
			mMap.refresh();
			break;
			
		case R.id.btn_ZoomOut:
			mMap.zoom(0.5);
			mMap.refresh();
			break;
			
		case R.id.btn_Locate:
			locating();

			break;
		default:
			break;
		}
		if(v.getId()!= R.id.btn_ArrowSymbol && v.getId() != R.id.btn_DotSymbol){
			findViewById(R.id.gridview1).setVisibility(View.GONE);
		    findViewById(R.id.gridview2).setVisibility(View.GONE);
		}
	}

	/**
	 * 显示定位
	 */
	private void locating() {
		// TODO Auto-generated method stub
		if(mTencentLocation == null){
			return ;
		}
		
		Point2D pos = mTencentLocation.getGPSPoint();
//		pos = mNavigation.encryptGPS(pos.getX(), pos.getY()); // 纠偏
//		if(pos.getX() < 0.000001 && pos.getY() < 0.000001){
//			return ;
//		}
		
		// 将pos转换为地图上的点
		PrjCoordSys prj = mMap.getPrjCoordSys();
		if (prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds points = new Point2Ds();
			points.add(pos);
			
			PrjCoordSys destPrj = new PrjCoordSys();
			destPrj.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			CoordSysTranslator.convert(points, destPrj, prj, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			
			pos.setX(points.getItem(0).getX());
			pos.setY(points.getItem(0).getY());
		}
		drawCircleOnDyn(pos, 0, mTencentLocation.getAccuracy());
		mMap.setCenter(pos);
		mMap.refresh();
		m_locateDynamicView.refresh();
	}

	private boolean checkSubmit() {
		boolean submitEnable = false;
		Geometry geometry = mMapControl.getCurrentGeometry();
		
		if(geometry != null){
			submitEnable = true;
			geometry.dispose();
		} else {
		    showTostInfo("没有编辑中的几何对象", true);
		}
		
		Layer layer = mMapControl.getEditLayer();
		if(layer != null){
			Dataset dataset = layer.getDataset();
			DatasetType type = dataset.getType();
			if(type != DatasetType.CAD){
				showTostInfo("当前编辑图层不是CAD图层，无法提交标绘符号,editlayer type is " + type, true);
			} else {
				submitEnable = true;
			}
		}
		return submitEnable;
	}

	/**
	 * Clear the dataset which is being editted.
	 */
	private void clearDataset() {
		if(datasetCAD == null)
			return ;
		mMapControl.cancel();
		Recordset recordset = datasetCAD.getRecordset(false, CursorType.DYNAMIC);
		recordset.deleteAll();
		recordset.update();
		recordset.dispose();
		recordset = null;
		mMap.refresh();
	}

	/**
	 * Toast a message
	 * @param msg
	 * @param isError
	 */
	void showTostInfo(String msg, boolean isError){
		if(msg == null)
			return;
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		if(isError)
			Log.e(TAG, msg);
		msg = null;
	}

	// 点击地图时隐藏符号选择窗口
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v.getId() != R.id.btn_ArrowSymbol && v.getId() != R.id.btn_DotSymbol) {
				if (findViewById(R.id.gridview1).getVisibility() == View.VISIBLE) {
					findViewById(R.id.gridview1).setVisibility(View.GONE);
					focusButton.setChecked(true);
				}

				if (findViewById(R.id.gridview2).getVisibility() == View.VISIBLE) {
					findViewById(R.id.gridview2).setVisibility(View.GONE);
					focusButton.setChecked(true);
				}
				
				if (editPopup != null ? editPopup.isShowing() : false) {
					editPopup.dismiss();
					focusButton.setChecked(true);
				}
			}
			
			isTouchMapUp = false;
			resetView(false);
		}
		if (event.getAction() == MotionEvent.ACTION_UP){
			isTouchMapUp = true;
			resetView(true);
		}
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!mExitEnable){
				Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
				mExitEnable = true;
			}else if (mMapControl != null){
				
				myApp.exit();
			}			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void closeMap() {
		// TODO Auto-generated method stub
		if(mMap != null){
			
			mMap.close();
			try {
				mWorkspace.save();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mWorkspace.close();
			mMap.dispose();
			mMapControl.dispose();
			mMap = null;
			mMapControl = null;
			mWorkspace = null;
		}
	}
	
	private void initDrawingStyle(){
		double lineWidth = 0.6;
		mMapControl.setStrokeColor(mMapControl.getResources().getColor(R.color.blue));
		mMapControl.setStrokeWidth(lineWidth);
	}

	private void resetView(boolean clickable) {
		
		findViewById(R.id.btn_ArrowSymbol).setClickable(clickable);
		findViewById(R.id.btn_Cancel).setClickable(clickable);
		findViewById(R.id.btn_Clear).setClickable(clickable);
		findViewById(R.id.btn_DotSymbol).setClickable(clickable);
		findViewById(R.id.btn_EditSymbol).setClickable(clickable);
		findViewById(R.id.btn_Redo).setClickable(clickable);
		findViewById(R.id.btn_Submit).setClickable(clickable);
		findViewById(R.id.btn_Undo).setClickable(clickable);
		findViewById(R.id.btn_ViewEntire).setClickable(clickable);
		findViewById(R.id.btn_ZoomIn).setClickable(clickable);
		findViewById(R.id.btn_ZoomOut).setClickable(clickable);
	}
	
	private void drawCircleOnDyn(Point2D point2D, float azimuth, double q){

		if (point2D.getX() == 0 || point2D.getY() == 0) {
			MyApplication.getInstance().ShowInfo("定位点为空");
			return ;
		}
		m_locateDynamicView.removeElement(m_LocationID);
		m_locateDynamicView.removeElement(m_LocationPolygonID);
		
		//构造精度范围
		if (q == 0) {
			q = 60;
		}
		GeoCircle geoCircle = new GeoCircle(point2D, q);
		GeoRegion geoRegion = geoCircle.convertToRegion(50*4);
		//绘制精度范围
		DynamicPolygon dynPolygon = new DynamicPolygon();
		dynPolygon.fromGeometry(geoRegion);
		DynamicStyle style = new DynamicStyle();
		style.setBackColor(android.graphics.Color.rgb(128, 128, 255));
		style.setLineColor(android.graphics.Color.rgb(128,255,255));//224, 224, 224
		
		style.setAlpha(65);//95
//		style.setSize(3.0f);//6.0f
		dynPolygon.setStyle(style);

		m_locateDynamicView.addElement(dynPolygon);
		m_LocationPolygonID = dynPolygon.getID();
		drawPoint(point2D, azimuth);
	}
	
	public void drawPoint(Point2D point2D, float azimuth){
		DynamicPoint dynPoint = new DynamicPoint();
		dynPoint.addPoint(point2D);
		
		DynamicStyle dynStyle = new DynamicStyle();
		dynStyle.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.location));
		dynStyle.setAngle(azimuth);
		dynPoint.setStyle(dynStyle);
		
		m_locateDynamicView.addElement(dynPoint);
		m_LocationID = dynPoint.getID();
		m_locateDynamicView.refresh();
	}
}
