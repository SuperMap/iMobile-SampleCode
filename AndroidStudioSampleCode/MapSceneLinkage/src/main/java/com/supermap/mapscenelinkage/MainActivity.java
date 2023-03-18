package com.supermap.mapscenelinkage;

 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.mapscenelinkage.R;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.realspace.Camera;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;
/**
 * Title:二三维联动范例程序
 * 
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户二三维联动功能。
 * 2、示例数据：将SampleData/GeometryInfo/中的数据拷贝到安装目录/SampleData/GeometryInfo/World.smwu
 * 3、关键类型/成员:
 *
 * 4、使用步骤：
 *  (1)运行程序 
 *  (2)点击二维按钮，显示二维地图 
 *  (3)点击三维按钮，显示三维
 *  (4)点击二三维按钮，触摸地图/球 ，实现二三维联动 
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 * 
 * 
 * Company: 北京超图软件股份有限公司
 * 
 */
public class MainActivity extends Activity {

	private SceneControl sceneControl;
	private MapView  mapview;
	private MapControl mapControl;
	private Workspace woWorkspace;
	private String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	private static double EarthRadius = 6378137;
	private GestureDetector gestureDetector;
	private Button btn_map, btn_scene, btn_linkage;
	private RelativeLayout rl_map, rl_scene;
	double width = 0, height;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//动态权限
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.initialization(getApplicationContext());
		Environment.setOpenGLMode(true);
		setContentView(R.layout.activity_main);

		btn_map = (Button) findViewById(R.id.btn_map);
		btn_scene = (Button) findViewById(R.id.btn_scene);
		btn_linkage = (Button) findViewById(R.id.btn_linkage);
		mapview = (MapView) findViewById(R.id.map_view);
		mapControl=mapview.getMapControl();
		sceneControl = (SceneControl) findViewById(R.id.sceneControl);
		rl_map = (RelativeLayout) findViewById(R.id.rl_map);
		rl_scene = (RelativeLayout) findViewById(R.id.rl_scene);
		setSceneLayout();
		gestureDetector = new GestureDetector(MainActivity.this, new gestureListener());
		sceneControl.setGestureDetector(gestureDetector);

		mapControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case (MotionEvent.ACTION_MOVE):
					Camera camera = new Camera(mapControl.getMap().getCenter().getX(),
							mapControl.getMap().getCenter().getY(),
							CalculateAltitudeFromBounds(mapControl.getMap().getViewBounds()));
					camera.setHeading(mapControl.getMap().getAngle());
					sceneControl.getScene().setCamera(camera);
					break;

				default:
					break;
				}

				return false;
			}
		});

		sceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {

			@Override
			public void onSuccess(String arg0) {
				openMap();
			}
		});

		// 二维
		btn_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setMapLayout();
			}
		});
		// 三维
		btn_scene.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setSceneLayout();

			}
		});
		// 二三维
		btn_linkage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setLayout();
			}
		});
	}

	// 打开地图
	private boolean openMap() {

		// 获取当前设备的显示屏幕的相关参数
		final Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		if (openWorkspace()) {

			// 将地图显示空间和 工作空间关联
			mapControl.getMap().setWorkspace(woWorkspace);
			mapControl.getMap().setMapDPI(dm.densityDpi);
			// m_mapControl.setOnTouchListener(this);

			// 打开工作空间中地图
			String mapName = woWorkspace.getMaps().get(1);
			boolean isOpenMap = mapControl.getMap().open(mapName);
			if (isOpenMap) {
				// 刷新地图，涉及地图的任何操作都需要调用该接口进行刷新
				mapControl.getMap().refresh();
			}
			return true;
		}
		return false;
	}

	// 用于打开示范数据
	private boolean openWorkspace() {

		woWorkspace = new Workspace();
		WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
		m_info.setServer(sdcard + "/SuperMap/data/GeometryInfo/World.smwu");
		m_info.setType(WorkspaceType.SMWU);
		return woWorkspace.open(m_info);

	}

	/// <summary>
	/// 根据给定的场景高度计算地图中显示范围的宽度
	/// </summary>
	/// <param name="altitude">场景高度</param>
	/// <returns>地图显示范围尺寸</returns>
	private Size2D CalculateSizeFromAltitude(Double altitude) {
		Size2D size = new Size2D(0, 0);

		try {
			// 当场景高度大于可全幅显示整球的高度时
			if (altitude >= EarthRadius) {
				double ratio = (altitude + EarthRadius) / 2;
				double longitudeWidth = 120 * ratio / EarthRadius;
				double latitudeWidth = 120 * ratio / EarthRadius;

				size = new Size2D(longitudeWidth, latitudeWidth);
			}
			// 当场景高度小于可全幅显示整球的高度时，即无法看到整球时
			else {
				double tan30 = Math.tan(3.1415926 / 6);
				// 设置二元一次方程组的a、b、c值
				double a = (Math.pow(tan30, 2) + 1) * Math.pow(EarthRadius, 2);
				double b = -2 * (EarthRadius + altitude) * EarthRadius * Math.pow(tan30, 2);
				double c = Math.pow(tan30, 2) * Math.pow((EarthRadius + altitude), 2) - Math.pow(EarthRadius, 2.0);
				// 解一元二次方程，取锐角，因此余弦值较大
				double cosd = (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / 2 / a;
				double d = Math.acos(cosd);
				double widthd = 2 * d * EarthRadius;
				double width = (widthd / 3.1415926 / EarthRadius) * 180;

				size = new Size2D(width, width);
			}
		} catch (Exception ex) {

		}
		return size;
	}

	/// <summary>
	/// 根据给定的地图范围计算场景的高度
	/// </summary>
	/// <param name="bounds">地图范围</param>
	/// <returns>场景高度</returns>
	private double CalculateAltitudeFromBounds(Rectangle2D bounds) {
		double altitude = EarthRadius;
		try {
			if (bounds.getWidth() >= 120) {
				double mapWidth = bounds.getWidth();
				altitude = EarthRadius * mapWidth / 60 - EarthRadius;
			} else if (bounds.getWidth() != 0) {
				double angle1 = bounds.getWidth() / 2 / 180 * 3.1415926;
				double height = Math.sin(angle1) * EarthRadius;
				double a = height / Math.tan(angle1);
				double b = height / Math.tan(3.1415926 / 6);
				altitude = a + b - EarthRadius;
			}
		} catch (Exception ex) {

		}
		return altitude;
	}

	class gestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Camera mCamera = sceneControl.getScene().getCamera();
			Point2D point = new Point2D(mCamera.getLongitude(), mCamera.getLatitude());
			Size2D size = CalculateSizeFromAltitude(mCamera.getAltitude());
			Rectangle2D bounds = new Rectangle2D(point, size);
			mapControl.getMap().setViewBounds(bounds);
			mapControl.getMap().setAngle(mCamera.getTilt());
			;
			mapControl.getMap().refresh();
			sceneControl.getScene().refresh();
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			// TODO Auto-generated method stub
			Camera mCamera = sceneControl.getScene().getCamera();
			Point2D point = new Point2D(mCamera.getLongitude(), mCamera.getLatitude());
			Size2D size = CalculateSizeFromAltitude(mCamera.getAltitude());
			Rectangle2D bounds = new Rectangle2D(point, size);
			mapControl.getMap().setViewBounds(bounds);
			mapControl.getMap().refresh();
			sceneControl.getScene().refresh();
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			// TODO Auto-generated method stub
			Camera mCamera = sceneControl.getScene().getCamera();
			Point2D point = new Point2D(mCamera.getLongitude(), mCamera.getLatitude());
			Size2D size = CalculateSizeFromAltitude(mCamera.getAltitude());
			Rectangle2D bounds = new Rectangle2D(point, size);
			mapControl.getMap().setViewBounds(bounds);
			mapControl.getMap().refresh();
			sceneControl.getScene().refresh();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private void setLayout() {
		DisplayMetrics dp = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dp);
		width = dp.widthPixels;
		height = dp.heightPixels;
		double x = (dp.widthPixels - dp2px(50)) / 2;
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_map.getLayoutParams();
		params.width = (int) x;
		params.height = (int) height;
		rl_map.setLayoutParams(params);
		RelativeLayout.LayoutParams params1 = new LayoutParams((int) x, (int) height);
		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		rl_scene.setLayoutParams(params1);
	}

	// mapView需要设置一个宽度
	private void setSceneLayout() {
		DisplayMetrics dp = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dp);
		width = dp.widthPixels;
		height = dp.heightPixels;
		double y = 1;
		double x = (dp.widthPixels - dp2px(50));
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_map.getLayoutParams();
		params.width = (int) y;
		params.height = (int) height;
		rl_map.setLayoutParams(params);
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) rl_scene.getLayoutParams();
		params1.height = (int) height;
		params1.width = (int) x;
		rl_scene.setLayoutParams(params1);
	}

	private void setMapLayout() {
		ViewGroup.LayoutParams lp = rl_map.getLayoutParams();
		lp.width = (int) width;
		lp.height = (int) height;
		rl_map.setLayoutParams(lp);
	}

	// 转化函数
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				this.getResources().getDisplayMetrics());
	}
}
