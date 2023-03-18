package com.supermap.markingpointsonkmllayer;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.AltitudeMode;
import com.supermap.data.Environment;
import com.supermap.data.GeoPlacemark;
import com.supermap.data.GeoPoint3D;
import com.supermap.data.GeoStyle3D;
import com.supermap.data.Point3D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Feature3D;
import com.supermap.realspace.Feature3DSearchOption;
import com.supermap.realspace.Feature3Ds;
import com.supermap.realspace.Layer3D;
import com.supermap.realspace.Layer3DType;
import com.supermap.realspace.PixelToGlobeMode;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Title:地标标注范例程序
 * 
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户使用地标标注，编辑的功能。 
 * 2.示例数据：将SampleData/CBD_android/中的数据拷贝到安装目录\SuperMap\data\ 
 *
 * 3、使用步骤：   (1)运行程序，长按弹出标注点，标注点收藏 绕点旋转，编辑等功能。
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 * 
 * 
 * Company: 北京超图软件股份有限公司
 * 
 */
public class MainActivity extends Activity {
	// 控件类
	private Menu menu;
	private GestureDetector gestureDetector;
	private SceneControl sceneControl;
	private RelativeLayout rl_bootom;
	private String dataPath = "";
	private TextView tv_info;
	private boolean isCollect = false;
	private ImageView iv_collect, iv_editor, iv_circle, iv_close;
	private Feature3D currentFeature3D;
	private Feature3Ds feature3ds;
	private ArrayList<Feature3D> feArrayList;
	private String layerKMlPath = android.os.Environment.getExternalStorageDirectory()
			+ "/SuperMap/initKML/default.kml";
	private EditorInfo editorInfo;
	
	private Workspace workspace;
	private Scene scene;
	// 离线三维场景数据名称
		String workspacePath = "/sdcard/SuperMap/data/CBD_android/CBD_android.sxwu";
		// 三维场景名称
		String sceneName = "CBD_android";
		WorkspaceConnectionInfo info;
		WorkspaceType workspaceTypetemp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//动态权限
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.initialization(this);
		setContentView(R.layout.activity_main);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(dm);
		editorInfo = new EditorInfo(this, dm.widthPixels, dm.heightPixels);
		dataPath = getApplicationContext().getFilesDir().getAbsolutePath();
		sceneControl = (SceneControl) findViewById(R.id.sceneControl);
		rl_bootom = (RelativeLayout) findViewById(R.id.rl_bottomMenu);
		iv_collect = (ImageView) findViewById(R.id.iv_collect);
		iv_editor = (ImageView) findViewById(R.id.iv_editor);
		iv_circle = (ImageView) findViewById(R.id.iv_circle);
		iv_close = (ImageView) findViewById(R.id.iv_close);
		tv_info = (TextView) findViewById(R.id.tv_info);
		feArrayList = new ArrayList<Feature3D>();
		info = new WorkspaceConnectionInfo();
		gestureDetector = new GestureDetector(MainActivity.this, new gestureListener());
		sceneControl.setGestureDetector(gestureDetector);
		// 标注收藏点击监听
		iv_collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!isCollect) {
					iv_collect.setBackgroundResource(R.drawable.icon_collect);
					isCollect = true;
				} else {
					iv_collect.setBackgroundResource(R.drawable.icon_not_collect);
					isCollect = false;

				}
			}
		});
		// 绕点旋转点击监听
		iv_circle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (currentFeature3D != null) {
					GeoPlacemark geo = (GeoPlacemark) currentFeature3D.getGeometry();
					GeoPoint3D gp = (GeoPoint3D) geo.getGeometry();
					double x = gp.getX();
					double y = gp.getY();
					double z = gp.getZ();
					GeoPoint3D geoPoint3D = new GeoPoint3D(x, y, z + 100);
					sceneControl.getScene().flyCircle(geoPoint3D, 2);
				}

			}
		});
		// 关闭点击监听
		iv_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clearNotCollectFavorit();
				isCollect = false;
				rl_bootom.setVisibility(View.INVISIBLE);
			}
		});
		// 编辑点击监听
		iv_editor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editorInfo.showAtLocation(iv_editor, Gravity.CENTER, 0, 0);
				editorInfo.et_info.setText(tv_info.getText().toString().trim());
				editorInfo.et_name.setText("未命名");
			}
		});
		// 编辑-返回点击监听
		editorInfo.iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				editorInfo.dismiss();
				String name = editorInfo.et_name.getText().toString().trim();
				String info = editorInfo.et_info.getText().toString().trim();
				GeoPlacemark gmk = (GeoPlacemark) currentFeature3D.getGeometry();
				gmk.setName(name);
				currentFeature3D.setName(name);
				currentFeature3D.setDescription(info);
				currentFeature3D.updateData();
				tv_info.setText(info);
			}
		});
		// 编辑-场景可见点击监听
		editorInfo.toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
				if (value) {
					currentFeature3D.setVisible(true);
					value = false;
				} else {
					currentFeature3D.setVisible(false);
					value = true;
				}

			}
		});
		// 场景初始化回调监听
		sceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {

			@Override
			public void onSuccess(String arg0) {
				createFile(layerKMlPath);
				sceneControl.getScene().getLayers().addLayerWith(layerKMlPath, Layer3DType.KML, true, "Favorite_KML");
				Layer3D layer3d = sceneControl.getScene().getLayers().get("Favorite_KML");
				if (layer3d != null) {
					feature3ds = layer3d.getFeatures();
					Feature3D[] feature3d = feature3ds.getFeatureArray(Feature3DSearchOption.ALLFEATURES);
					feArrayList.clear();
					Collections.addAll(feArrayList, feature3d);
					int count = feArrayList.size();
					menu.clear();
					for (int i = 0; i < count; i++) {
						// menu.add(feArrayList.get(i).getName()+"
						// "+feArrayList.get(i).getDescription());
						menu.add(1, i, Menu.NONE,
								feArrayList.get(i).getName() + " " + feArrayList.get(i).getDescription());
					}

				}
				
				openLocalScene();

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		return true;
	}

	// 动态添加 item.getItemId()始终未0，可以用其它控件就不用获取position直接用feArrayList就行。
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		GeoPlacemark geo = (GeoPlacemark) feArrayList.get(id).getGeometry();
		GeoPoint3D gp = (GeoPoint3D) geo.getGeometry();
		Point3D pot = new Point3D(gp.getX(), gp.getY(), gp.getZ() + 100);
		sceneControl.getScene().flyToPoint(pot);
		return super.onOptionsItemSelected(item);
	}

	// 手势监听-长按
	class gestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {

			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			// sceneControl.setAction(Action3D.PAN3D);
			LongTouch(event);
			rl_bootom.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return true;
		}

	}

	// 长按弹出兴趣点
	private void LongTouch(MotionEvent event) {
		// double x = event.getX() - 100;
		// double y = event.getY() - 56;
		double x = event.getX() - 28.1;
		double y = event.getY() - 0.5;
		final android.graphics.Point pnt = new android.graphics.Point();
		pnt.set((int) x, (int) y);
		Feature3D param = getaMarkFileFeature(pnt);
	}

	// 长按添加地标和poi时添加地标
	public Feature3D getaMarkFileFeature(Point pnt) {

		Point3D pnt3d = sceneControl.getScene().pixelToGlobe(pnt, PixelToGlobeMode.TERRAINANDMODEL);
		String favoritName = "未命名";
		final GeoStyle3D pointStyle3D = new GeoStyle3D();
		GeoPoint3D geoPoint = new GeoPoint3D(pnt3d);
		pointStyle3D.setMarkerFile(dataPath + "/config/Resource/icon_green.png");
		pointStyle3D.setAltitudeMode(AltitudeMode.ABSOLUTE);
		geoPoint.setStyle3D(pointStyle3D);
		Feature3Ds feature3Ds = null;
		sceneControl.getScene().getLayers().addLayerWith(layerKMlPath, Layer3DType.KML, true, "Favorite_KML");
		Layer3D layer3d = sceneControl.getScene().getLayers().get("Favorite_KML");
		if (layer3d != null) {
			feature3Ds = layer3d.getFeatures();
		}
		Feature3D feature3D = new Feature3D();
		GeoPlacemark geoPlacemark = new GeoPlacemark(favoritName, geoPoint);
		feature3D.setGeometry(geoPlacemark);
		tv_info.setText("(" + pnt3d.getX() + "," + pnt3d.getY() + ")");
		if (currentFeature3D != null) {
			if (!isCollect) {
				feature3Ds.remove(currentFeature3D);
				currentFeature3D = null;
			} else {

				String description = tv_info.getText().toString();
				if (description != "" && !description.isEmpty() && description != null) {
					currentFeature3D.setDescription(description);
				}
				currentFeature3D.setName(favoritName);
				currentFeature3D.updateData();
				feature3Ds.toKMLFile(layerKMlPath);
				// mCurrentFeature3D.updateData();
				iv_collect.setBackgroundResource(R.drawable.icon_collect);
				// isCollect = false;
				int size = menu.size();
				menu.add(1, size, Menu.NONE, favoritName + description);
				feArrayList.add(currentFeature3D);
			}
		} else {
			iv_collect.setBackgroundResource(R.drawable.icon_not_collect);
		}
		if (feature3Ds != null) {
			currentFeature3D = feature3Ds.add(feature3D);
		}
		return currentFeature3D;

	}

	// bottomMenu 长按或者poi查询的时候弹出的框 关闭时候要清除未收藏的feature,以及收藏了保存到对应KML文件中
	public void clearNotCollectFavorit() {

		// 一种是长按时的情况 当前为mCurrentFeature3D
		Feature3Ds feature3Ds = null;
		// 为null是空球的情况。
		Layer3D layer3d = sceneControl.getScene().getLayers().get("Favorite_KML");
		if (layer3d != null) {
			feature3Ds = layer3d.getFeatures();
		}
		// 长按时的情况 未收藏 为空的时候
		if (currentFeature3D != null) {

			if (isCollect) {
				currentFeature3D.setDescription(tv_info.getText().toString());
				GeoPlacemark geo = (GeoPlacemark) currentFeature3D.getGeometry();
				currentFeature3D.setName(geo.getName());
				String name = geo.getName();
				currentFeature3D.updateData();
				feature3Ds.toKMLFile(layerKMlPath);
				int size = menu.size();
				menu.add(1, size, Menu.NONE, name + tv_info.getText().toString());
				feArrayList.add(currentFeature3D);
				Toast.makeText(MainActivity.this, "保存了" + name + "新地标", Toast.LENGTH_SHORT).show();

			} else {
				feature3Ds.remove(currentFeature3D);
				Toast.makeText(MainActivity.this, "未保存", Toast.LENGTH_SHORT).show();
			}
			currentFeature3D = null;
			isCollect = false;
		}

	}

	// 生成文件
	public static File createFile(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	// 打开一个本地场景
		private void openLocalScene() {

			// 新建一个工作空间对象
			if (workspace == null) {
				workspace = new Workspace();
			}
			// 根据工作空间类型，设置服务路径和类型信息。
			workspaceTypetemp = WorkspaceType.SXWU;
			info.setServer(workspacePath);
			info.setType(workspaceTypetemp);
			// 场景关联工作空间
			if (workspace.open(info)) {
				scene = sceneControl.getScene();
				scene.setWorkspace(workspace);
			}
			// 打开场景
			boolean successed = sceneControl.getScene().open(sceneName);
			if (successed) {
				Toast.makeText(MainActivity.this, "打开场景成功", Toast.LENGTH_LONG);
			}
		}
}
