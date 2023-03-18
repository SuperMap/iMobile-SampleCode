package com.supermap.flymanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flymanager.R;
import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

/**
 * Title:飞行管理范例程序
 * 
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户使用imobile的根据飞行路线飞行功能。
 * 2、示例数据：将SampleData/CBD_android/中的数据拷贝到安装目录\SuperMap\data\ 
 * 3、使用步骤：
 * (1)运行程序
 * (2)点击框内飞行路线，开始飞行
 * (3)点击暂停，停止飞行，点击开始，继续飞行
 * (4)点击重置，停止飞行，点击开始，重新飞行
 * (5)点击全屏，全屏飞行，点击屏幕，恢复界面气泡。
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 * 
 * 
 * Company: 北京超图软件股份有限公司
 * 
 */
public class MainActivity extends Activity {

	private FlyPopupWindow flyPopupWindow;
	private SceneControl sceneControl;
	private TextView tv_dialog;
	private Workspace workspace;
	private Scene scene;
	// 离线三维场景数据名称
	private String workspacePath = "/sdcard/SuperMap/data/CBD_android/CBD_android.sxwu";
	// 三维场景名称
	private String sceneName = "CBD_android";
	private WorkspaceConnectionInfo info;
	private WorkspaceType workspaceTypetemp = null;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	private boolean isLicenseAvailable = false;
	private boolean isFullscreen = false;
	private boolean isClose = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//动态权限
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.initialization(this);
		setContentView(R.layout.activity_main);
		Environment.setLicensePath(sdcard + "/SuperMap/license/");
		info = new WorkspaceConnectionInfo();
		sceneControl = (SceneControl) findViewById(R.id.sceneControl);
		tv_dialog = (TextView) findViewById(R.id.tv_dialog);
		// 获取当前许可的状态，返回true 许可可用，返回false 许可不可用，不可用情况下无法打开本地场景。
		isLicenseAvailable = isLicenseAvailable();
		flyPopupWindow = FlyPopupWindow.getInstance(MainActivity.this, dp2px(this, 190), dp2px(this, 365));
		flyPopupWindow.setDismissWhenTouchOuside(false);
		sceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {

			@Override
			public void onSuccess(String success) {
				if (isLicenseAvailable) {
					openLocalScene();
				}
				flyPopupWindow.fly(sceneControl);
				flyPopupWindow.showAsDropDownBottom(tv_dialog, 0, 0);

			}
		});
		// 飞行界面-"item点击"监听事件
		flyPopupWindow.listView_fly.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				flyPopupWindow.listflyItem(sceneControl, position);
				flyPopupWindow.dismiss();
				flyPopupWindow.mFlyBottomPopupWindow.showAsDropDownBottom(tv_dialog, 0, 0);

			}
		});

		// 飞行界面-"暂停"监听事件
		flyPopupWindow.mFlyBottomPopupWindow.iv_imageView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flyPopupWindow.flyPause(sceneControl);
			}
		});

		// 飞行界面-"停止"监听事件
		flyPopupWindow.mFlyBottomPopupWindow.iv_imageView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flyPopupWindow.flyStop(sceneControl);

			}
		});

		// 飞行界面-全局试图显隐监听事件
		flyPopupWindow.mFlyBottomPopupWindow.iv_imageView3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isFullscreen) {
					flyPopupWindow.mFlyBottomPopupWindow.dismiss();
					isFullscreen = true;
				}

			}
		});
		// 飞行界面-关闭监听事件
		flyPopupWindow.mFlyBottomPopupWindow.tv_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isClose = true;
				flyPopupWindow.flyStop(sceneControl);
				flyPopupWindow.mFlyBottomPopupWindow.dismiss();
				flyPopupWindow.showAsDropDownBottom(tv_dialog, 0, 0);
				flyPopupWindow.reset(sceneControl);
			}

		});

		sceneControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					if (!flyPopupWindow.mFlyBottomPopupWindow.isShowing() && isFullscreen) {
						flyPopupWindow.mFlyBottomPopupWindow.showAsDropDownBottom(tv_dialog, 0, 0);
						isFullscreen = false;
					}
				}
				return false;
			}
		});
	}

	public int dp2px(Activity context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
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
		boolean successed = scene.open(sceneName);
		if (successed) {
			Toast.makeText(MainActivity.this, "打开场景成功", Toast.LENGTH_LONG);

		}
	}

	// 判断许可是否可用
	private boolean isLicenseAvailable() {
		LicenseStatus licenseStatus = Environment.getLicenseStatus();
		if (!licenseStatus.isLicenseExsit()) {
			Toast.makeText(MainActivity.this, "许可不存在，场景打开失败，请加入许可", Toast.LENGTH_LONG).show();
			return false;
		} else if (!licenseStatus.isLicenseValid()) {
			Toast.makeText(MainActivity.this, "许可过期，场景打开失败，请更换有效许可", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}
