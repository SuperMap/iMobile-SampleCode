package com.supermap.hysometricsettingdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.supermap.data.Color;
import com.supermap.data.ColorDictionary;
import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.HypsometricSetting;
import com.supermap.realspace.HypsometricSettingDisplayMode;
import com.supermap.realspace.Layer3D;
import com.supermap.realspace.Layer3DOSGBFile;
import com.supermap.realspace.Layer3Ds;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Title:分层设色范例程序
 * 
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户在Layer3DOSGBFile图层分层设色。 2、示例数据：将SampleData/MaSai/中的数据拷贝到安装目录\SuperMap\data\ 3、关键类型/成员:
 * SceneControl.getScene 方法 Scene.open 方法  Layer3DOSGBFile.setHypsometricSetting(HypsometricSetting,
 * hypsometricSetting) 方法
 * 
 *
 * 4、使用步骤：   (1)运行程序，按照默认的参数，实现了分层设色动态淹没的效果。
 *           (2)可以手动拖动进度条更改透明度。
 *           (3)可以设置最大水位。
 *           (4)可以设置动画时间。
 *           (5)可以更改循环模式，循环或者单次。
 *           (6) 播放和停止。
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 * 
 * 
 * Company: 北京超图软件股份有限公司
 * 
 */

@SuppressLint("SdCardPath") public class MainActivity extends Activity {

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
	private String workspacePath = "/sdcard/SuperMap/data/MaSai/MaSai.sxwu";
	// 本地数据的场景名称
	private String sceneName = "MaSai";
	//声明相关控件和参数。
	private int flag = 0;
	private double Opacity = 30;
	private double maxWaterLevel = 0.0;
	private double mTime = 0.0;
	private SeekBar mSeekbar;
	private TextView play, stop;
	private EditText et_maxWaterLevel, et_time;
	private ToggleButton mTogBtn;
	private boolean playmode = true;
	private Timer mTimer;
	private Handler mHandler;
	private TimerTask mTimerTask;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//动态权限
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.setLicensePath(sdcard+"/SuperMap/license/");
		Environment.initialization(this);
		setFullScreen();
		setContentView(R.layout.activity_main);
		mSceneControl = (SceneControl) findViewById(R.id.sceneControl);
		mSeekbar = (SeekBar) findViewById(R.id.seekbar);
		play = (TextView) findViewById(R.id.tv_play);
		stop = (TextView) findViewById(R.id.tv_stop);
		mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn);
		et_maxWaterLevel = (EditText) findViewById(R.id.et_maxWaterLevel);
		et_time = (EditText) findViewById(R.id.et_time);

		// 透明度seekbar监听器
		mSeekbar.setOnSeekBarChangeListener(new myListener());
		// 接口回调中触发分层设色。
		mSceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {
			
			@Override
			public void onSuccess(String success) {
				if (isLicenseAvailable()) {
					// 在许可可用的情况下打开场景打开指定本地场景
					openLocalScene();
					if (isOpenSceneSuccess) {
						// 加载动态淹没
						setupHypsometricSetting();
					}

				}
			}
		});

		// 停止
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
			}
		});
		// 播放
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setupHypsometricSetting();

			}
		});
      //动画模式
		mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					playmode = true;
					setupHypsometricSetting();
				} else {
					playmode = false;

				}
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

	/**
	 * @author：Supermap
	 * @注释 ：判断当前许可是否可用
	 */
	public boolean isLicenseAvailable() {
		LicenseStatus licenseStatus = Environment.getLicenseStatus();
		boolean a = licenseStatus.isLicenseValid();
		if (!licenseStatus.isLicenseExsit()) {
			Toast.makeText(MainActivity.this, "许可不存在，场景打开失败，请加入许可",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (!licenseStatus.isLicenseValid()) {
			Toast.makeText(MainActivity.this, "许可过期，场景打开失败，请更换有效许可",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// 透明度监听
	class myListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			// 将当前进度值赋值给透明度值。
			Opacity = progress;
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

	// 加载分层设色动态淹没
	private void setupHypsometricSetting() {

		if (mTimer == null) {

			mTimer = new Timer();
		}

		mHandler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					String temp = et_maxWaterLevel.getText().toString();
					if ("".equals(temp)) {
						maxWaterLevel = 0;
					} else {
						maxWaterLevel = Double.valueOf(temp);
					}

					String tempTime = et_time.getText().toString();
					if ("".equals(tempTime)) {
						mTime = 1;
					} else {
						mTime = Double.valueOf(tempTime);
					}
					double value = (flag * maxWaterLevel) / (20 * mTime);
					double key = (value > maxWaterLevel) ? maxWaterLevel
							: value;

					ColorDictionary colorDictionary = new ColorDictionary();
					colorDictionary.setColor(0, new Color(0, 150, 255));
					colorDictionary.setColor(key, new Color(50, 150, 255));

					HypsometricSetting mhypsometricSetting = new HypsometricSetting();
					mhypsometricSetting
							.setDisplayMode(HypsometricSettingDisplayMode.HypsometricSettingDisplayModeFace);
					mhypsometricSetting.setMinVisibleValue(0);
					mhypsometricSetting.setMaxVisibleValue(key);
					mhypsometricSetting.setOpacity(Opacity);
					mhypsometricSetting.setColorDictionary(colorDictionary);
					Layer3Ds layer3ds = mSceneControl.getScene().getLayers();
					Layer3D layer3d = layer3ds.get(0);
					Layer3DOSGBFile layer3dosgbfile = (Layer3DOSGBFile) layer3d;
					layer3dosgbfile.setHypsometricSetting(mhypsometricSetting);

					flag++;
					if (key == maxWaterLevel) {
						if (playmode) {
							flag = 0;
						} else {
							if (mTimer != null) {
								mTimer.cancel();
								mTimer = null;
							}

						}

					}
					break;

				default:
					break;
				}

			}

		};

		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				mHandler.sendEmptyMessage(0);

			}
		};

		if (mTimer != null) {
			mTimer.schedule(mTimerTask, 500, 50);
		}

		System.gc();
	}

	//充满屏幕
	private void setFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	// pause状态下 关闭timer
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

	}

}
