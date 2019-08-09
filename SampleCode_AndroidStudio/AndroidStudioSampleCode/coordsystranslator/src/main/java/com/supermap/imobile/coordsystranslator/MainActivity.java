package com.supermap.imobile.coordsystranslator;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:投影转换
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为 SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：打开一个地图，通过按键实现地图投影转换，以及对比投影转换前后效果。
 * 2、示例数据：安装目录/sdcard/SampleData/CoordSysTranslator/CoordSysTranslation.smwu;
 * 3、关键类型/成员: MapController.setSize 方法 MapController.getMap 方法
 * MapController.setOnTouchListener 事件监听 CoordSysTranslator.convert 方法
 * 
 * 4、使用步骤： (1)点击界面上方的投影转换方式按钮设置投影转换的类型 (2)界面中的两张图形即为投影转换前后的对比结果图形
 * (3)滑动界面下方的滚动条可对转换前后的投影坐标系进行验证
 * ------------------------------------------------
 * ------------------------------
 * ============================================================================>
 * </p>
 * 
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 * 
 */
public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	private MapView mSrcMapView=null;
	private MapView mDesMapView=null;
	private MapControl mSrcMapControl = null;
	private MapControl mDesMapControl = null;
	private TextView mSrcInfo = null;
	private TextView mDesInfo = null;
	private Workspace mWorkspace = null;
	private WorkspaceConnectionInfo mInfo = null;
	private CoordSysTranslation mTranslation = null;

	// 投影转换的类型
	private int type = 0;

	private String sdcard = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath().toString();
	private Display display = null;
	private DisplayMetrics dm = null;
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
		Environment.setLicensePath(sdcard + "/SuperMap/license/");
		Environment.initialization(this);
		setContentView(R.layout.main);
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

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		init();
	}

	private void init() {
		mWorkspace = new Workspace();
		mInfo = new WorkspaceConnectionInfo();
		mInfo.setServer(sdcard
				+ "/SampleData/CoordSysTranslator/CoordSysTranslation.smwu");
		mInfo.setType(WorkspaceType.SMWU);
		mWorkspace.open(mInfo);

		mSrcMapView = (MapView) findViewById(R.id.src);
		mDesMapView = (MapView) findViewById(R.id.des);

		mSrcMapControl=mSrcMapView.getMapControl();
		mDesMapControl=mDesMapView.getMapControl();
		
		mSrcInfo = (TextView) findViewById(R.id.srcTxt);
		mDesInfo = (TextView) findViewById(R.id.desTxt);

		display = getWindowManager().getDefaultDisplay();

		dm = new DisplayMetrics();

		display.getMetrics(dm);

		screenLayoutStyle();


		mTranslation = new CoordSysTranslation(mWorkspace, mSrcMapControl,
				mDesMapControl, mSrcInfo, mDesInfo);

		Button btnGauss = (Button) findViewById(R.id.gauss);
		btnGauss.setOnClickListener(this);
		Button btnUTM = (Button) findViewById(R.id.transverse);
		btnUTM.setOnClickListener(this);
		Button btnLam = (Button) findViewById(R.id.lambert);
		btnLam.setOnClickListener(this);

		// mSrcMapControl.getMap().open(mWorkspace.getMaps().get(0));
		// mDesMapControl.getMap().open(mWorkspace.getMaps().get(1));
	}

	/**
	 * 绘制屏幕布局样式
	 */
	private void screenLayoutStyle() {
		int width = display.getWidth() / 2;
		int height = (int) (display.getHeight() * 0.75);

		// 重新设置MapControl的大小
		LayoutParams srcparams = (LayoutParams) mSrcMapView
				.getLayoutParams();
		srcparams.width = width;
		srcparams.height = height;
		mSrcMapView.setLayoutParams(srcparams);

		LayoutParams desparams = (LayoutParams) mDesMapView
				.getLayoutParams();
		desparams.width = width;
		desparams.height = height;
		mDesMapView.setLayoutParams(desparams);

		// 重新设置文本框的宽度
		srcparams = (LayoutParams) mSrcInfo.getLayoutParams();
		srcparams.width = width;
		mSrcInfo.setLayoutParams(srcparams);
		desparams = (LayoutParams) mDesInfo.getLayoutParams();
		desparams.width = width;
		mDesInfo.setLayoutParams(desparams);

		mSrcInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
		mDesInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

		mSrcMapControl.getMap().setMapDPI(dm.densityDpi);
		mDesMapControl.getMap().setMapDPI(dm.densityDpi);

		mSrcMapControl.getMap().setWorkspace(mWorkspace);
		mDesMapControl.getMap().setWorkspace(mWorkspace);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gauss:
			type = 1;
			break;
		case R.id.transverse:
			type = 2;
			break;
		case R.id.lambert:
			type = 3;
			break;
		default:
			break;
		}
		mDesInfo.setText("");
		mDesInfo.invalidate();
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("投影转换中...");
		progress.setCanceledOnTouchOutside(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTranslation.transform(type);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mTranslation.showCoorsysInfo();
						progress.dismiss();
					}
				});
			}
		}).start();
		progress.show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSrcMapControl.getMap().close();
		mSrcMapControl.dispose();
		mDesMapControl.getMap().close();
		mDesMapControl.dispose();

		try {
			mWorkspace.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mWorkspace.close();
		mInfo.dispose();
		mWorkspace.dispose();
	}

	/**
	 * 重写父类方法
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		screenLayoutStyle();
	}
}