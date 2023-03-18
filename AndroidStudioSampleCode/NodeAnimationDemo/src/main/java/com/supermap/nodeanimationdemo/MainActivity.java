package com.supermap.nodeanimationdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.supermap.data.AltitudeMode;
import com.supermap.data.Color;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine3D;
import com.supermap.data.GeoModel;
import com.supermap.data.GeoPlacemark;
import com.supermap.data.GeoPoint3D;
import com.supermap.data.GeoStyle3D;
import com.supermap.data.LicenseStatus;
import com.supermap.data.NodeAnimation;
import com.supermap.data.NodeAnimationPlayMode;
import com.supermap.data.Point3D;
import com.supermap.data.Point3Ds;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Feature3D;
import com.supermap.realspace.Layer3D;
import com.supermap.realspace.Layer3DType;
import com.supermap.realspace.PixelToGlobeMode;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

import java.io.File;
import java.util.ArrayList;

/**
 * Title:节点动画范例程序
 * 
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户在。 2、示例数据：将SampleData/CBD_android/中的数据拷贝到\SuperMap\data\;
 * xiaoche-b.SGM和Textures模型纹理放在:安装目录\SuperMap\data\CBD_android\files\3、关键类型/成员:
 * SceneControl.getScene 方法 Scene.open 方法
 * 
 * 4、使用步骤：   (1)运行程序,点击 “点击创建路线”，在屏幕中 触摸屏幕自定义配置动画路线。
 *           (2)配置时长，默认有初始化时长。
 *           (3)设置循环模式 默认为动画循环 取消默认为 单次动画。
 *           (4)点击开始，小车按照自定义路线运行。
 *           (5)点击清除，然后重复上述步骤。
 * -----------------------------------------------------------------------------
 * -
 * ============================================================================>
 * 
 * 
 * Company: 北京超图软件股份有限公司
 * 
 */
@SuppressLint("SdCardPath")
public class MainActivity extends Activity {

	// 三维场景控件
	private SceneControl mSceneControl;
	// 三维场景
	private Scene mScene;
	// 工作空间相关类
	private Workspace m_workspace;
	private WorkspaceConnectionInfo info;
	private WorkspaceType workspaceTypetemp = null;
	private boolean isOpenSceneSuccess = false;
	// 本地数据地址
	private String workspacePath = "/sdcard/SuperMap/data/CBD_android/CBD_android.sxwu";
	// 本地数据的场景名称
	@SuppressLint("SdCardPath")
	private String sceneName = "CBD_android";
	// 声明一个全局的几何对象
	private Feature3D feature3d = null;
	// 声明一个全局的节点动画轨迹对象
	private GeoLine3D geoline3d = null;
	// 声明一个全局的路径，用于创建本地KML
	private String Path = null;
	// 声明指定模型对应的的位置
	private Point3D position;
	// 开始，清楚
	private TextView start, clear, createRoute;
	// 循环模式
	private ToggleButton mTogBtn;
	// 动画模式
	NodeAnimationPlayMode nodeAnimationPlayMode;
	// 时长进度条
	private SeekBar mSeekbar = null;

	// 是否是自定义创建路线
	private boolean isCreateRoute = false;
	// 节点动画类
	private NodeAnimation nodeAnimation = null;
	// 定义一个全局变量 存储Point3D
	private ArrayList<Point3D> myPoint3DArrayList;
	// 时长
	private double mTime = 30;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.setLicensePath(sdcard+"/SuperMap/license/");
		Environment.initialization(getApplicationContext());
		setFullScreen();
		setContentView(R.layout.activity_main);
		mSceneControl = (SceneControl) findViewById(R.id.sceneControl);
		GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new gestureListener());
		mSceneControl.setGestureDetector(gestureDetector);
		mSeekbar = (SeekBar) findViewById(R.id.seekbar);
		createRoute = (TextView) findViewById(R.id.tv_createRoute);
		start = (TextView) findViewById(R.id.tv_start);
		clear = (TextView) findViewById(R.id.tv_clear);
		myPoint3DArrayList = new ArrayList<Point3D>();
		// 默认动画循环模式为 循环模式
		nodeAnimationPlayMode = NodeAnimationPlayMode.LOOP;

		mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn);
		// seekbar监听器
		mSeekbar.setOnSeekBarChangeListener(new myListener());
		// 接口回调中触发节点动画。
		mSceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {
			
			@Override
			public void onSuccess(String success) {
				if (isLicenseAvailable()) {
					// 在许可可用的情况下打开场景打开指定本地场景
					openLocalScene();
					if (isOpenSceneSuccess) {
						// step one 创建本地KML，并且添加KML图层
						addKML();
						// step two 添加三维模型
						addModel();

						Point3D flypoint = new Point3D(116.471928672833, 39.9147239405117, 120);
						mSceneControl.getScene().flyToPoint(flypoint, 5000);
					}
				}

			}
		});
		// 创建路线
		createRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				isCreateRoute = true;
			}
		});
		// 动画模式
		mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 循环
					nodeAnimationPlayMode = NodeAnimationPlayMode.LOOP;
				} else {
					// 单次
					nodeAnimationPlayMode = NodeAnimationPlayMode.ONCE;
				}
			}

		});
		// 开始
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isCreateRoute = false;
				// initializeLine() ;
				startnodeAnimation();
			}
		});

		// 清除
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

		        mSceneControl.getScene().getTrackingLayer().clear();
				myPoint3DArrayList.clear();
				if(nodeAnimation!=null){
					nodeAnimation.setEnabled(false);
					nodeAnimation.setLength(0.0);
				}
				geoline3d = null;
			}
		});
	}

	/**
	 * @author：Supermap
	 * @注释 ：打开一个本地场景数据
	 */
	private void openLocalScene() {

		// 新建一个工作空间对象
		if (m_workspace == null) {
			m_workspace = new Workspace();
		}
		// 根据工作空间类型，设置服务路径和类型信息。
		if (info == null) {
			info = new WorkspaceConnectionInfo();
		}

		workspaceTypetemp = WorkspaceType.SXWU;
		info.setServer(workspacePath);
		info.setType(workspaceTypetemp);
		mScene = mSceneControl.getScene();
		// 场景关联工作空间
		if (m_workspace.open(info)) {
			mScene.setWorkspace(m_workspace);
		}
		// 打开场景
		isOpenSceneSuccess = mScene.open(sceneName);

	}

	// 时长
	class myListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			mTime = progress;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	}

	// 点击创建路线之后，在点击屏幕 获取路线。
	Point3D pnt3d;

	class gestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
			// TODO Auto-generated method stub

			return true;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			// TODO Auto-generated method stub
			// Log.v("MyGesture", "onFling()");
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			// TODO Auto-generated method stub
			if (isCreateRoute) {
				// 根据手指点击之后微调屏幕坐标
				double x = event.getX() - 28.1;
				double y = event.getY() - 0.5;
				final android.graphics.Point pnt = new android.graphics.Point();
				pnt.set((int) x, (int) y);

				pnt3d = mSceneControl.getScene().pixelToGlobe(pnt, PixelToGlobeMode.TERRAINANDMODEL);

				GeoStyle3D geoPoint3dStyle = new GeoStyle3D();
				geoPoint3dStyle.setFillForeColor((new Color(0, 255, 0)));
				geoPoint3dStyle.setAltitudeMode(AltitudeMode.ABSOLUTE);
				GeoPoint3D geoPoint3D = new GeoPoint3D(pnt3d);
				geoPoint3D.setStyle3D(geoPoint3dStyle);
				mSceneControl.getScene().getTrackingLayer().add(geoPoint3D, "point");
				myPoint3DArrayList.add(pnt3d);

				int count = myPoint3DArrayList.size();
				Point3D[] points = new Point3D[count];
				for (int i = 0; i < count; i++) {
					points[i] = myPoint3DArrayList.get(i);
				}
				Point3Ds point3ds = new Point3Ds(points);
				if (point3ds.getCount() > 1) {
					GeoStyle3D lineStyle3D = new GeoStyle3D();
					lineStyle3D.setLineColor(new Color(0, 255, 0));
					lineStyle3D.setAltitudeMode(AltitudeMode.ABSOLUTE);
					geoline3d = new GeoLine3D(point3ds);
					geoline3d.setStyle3D(lineStyle3D);
					mSceneControl.getScene().getTrackingLayer().add(geoline3d, "geoline");

				}
			}
			return false;
		}
	}

	/**
	 * @author：Supermap
	 * @注释 ：创建本地KML图层，并且添加KML图层
	 */
	private void addKML() {

		Path = "/sdcard/SuperMap/data/CBD_android/files/";
		makeFilePath(Path, "NodeAnimation.kml");
		mSceneControl.getScene().getLayers().addLayerWith(Path + "NodeAnimation.kml", Layer3DType.KML, true,
				"NodeAnimation");

	}

	/**
	 * @author：Supermap
	 * @注释 ：添加模型
	 */
	private void addModel() {

		GeoModel geoModel = new GeoModel();
		position = new Point3D(116.471928672833, 39.9147239405117, 5);
		geoModel.fromFile(Path + "xiaoche-b.SGM", position);
		geoModel.setRotationZ(90.0);
		Layer3D layer3d = mSceneControl.getScene().getLayers().get("NodeAnimation");
		GeoStyle3D geostyle3d = new GeoStyle3D();
		geostyle3d.setAltitudeMode(AltitudeMode.ABSOLUTE);
		GeoPlacemark geoPlacemark = new GeoPlacemark("UntitledFeature3D", geoModel);
		geoPlacemark.setStyle3D(geostyle3d);
		feature3d = layer3d.getFeatures().add(geoPlacemark);

	}

	/**
	 * @author：Supermap
	 * @注释 ：开启节点动画
	 */
	private void startnodeAnimation() {
		GeoPlacemark placemark = (GeoPlacemark) feature3d.getGeometry();
		GeoModel myGeoModel = (GeoModel) placemark.getGeometry();
		nodeAnimation = myGeoModel.getNodeAnimation();
		nodeAnimation.setPlayMode(nodeAnimationPlayMode);

		if (geoline3d != null) {
			nodeAnimation.setLength(mTime);
			Log.v("lzw", "startnodeAnimation mTime=" + mTime);
			nodeAnimation.setEnabled(true);
			nodeAnimation.setTrack(geoline3d);
		} else {
			nodeAnimation.setEnabled(false);

		}

	}

	/**
	 * @author：Supermap
	 * @注释 ：生成文件
	 */
	private File makeFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * @author：Supermap
	 * @注释 ：生成文件夹
	 */
	private void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			Log.i("error:", e + "");
		}

	}

	/**
	 * @author：Supermap
	 * @注释 ：判断当前许可是否可用
	 */
	private boolean isLicenseAvailable() {
		LicenseStatus licenseStatus = Environment.getLicenseStatus();
		boolean a = licenseStatus.isLicenseValid();
		if (!licenseStatus.isLicenseExsit()) {
			Toast.makeText(MainActivity.this, "许可不存在，场景打开失败，请加入许可", Toast.LENGTH_LONG).show();
			return false;
		} else if (!licenseStatus.isLicenseValid()) {
			Toast.makeText(MainActivity.this, "许可过期，场景打开失败，请更换有效许可", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// 充满 屏幕
	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

}
