package com.supermap.scenemicrocontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Title:场景微操作功能的范例代码程序
 *
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 *
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 *
 * 1、范例简介：示范用户场景微操作功能。 2、示例数据：将SampleData/MaSai/中的数据拷贝到安装目录\SuperMap\data\
 * 3、关键类型/成员: SceneControl.getScene 方法 Scene.open 方法
 *  Scene.setRollEye(double value);Scene.setPitch(double value);
 *  Scene.pan(dobule longla, double lan);
 *
 * 4、使用步骤：
 *  (1)运行程序
 *  (2)点击上下左右按钮 ，实现平移效果。
 *  (4)点击上下俯视按钮，实现俯视仰视效果。
 *  (5)点击左右选择按钮，实现左右旋转效果。
 *
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 *
 *
 * Company: 北京超图软件股份有限公司
 *
 */
public class MainActivity extends Activity {
	private Workspace workspace;
	private Scene scene;
	private SceneControl sceneControl;
	// 离线三维场景数据名称
	private String workspacePath = "/sdcard/SuperMap/data/MaSai/MaSai.sxwu";
	// 三维场景名称
	private String sceneName = "MaSai";
	private WorkspaceConnectionInfo info;
	private WorkspaceType workspaceTypetemp = null;
	private boolean isLicenseAvailable = false;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	private Button panUp, panDown, panRight, panLeft, pitchUp, pitchDown, rollLeft, rollRight;

	private Timer timerRollMax;
	private TimerTask timerTaskRollMax;
	private Handler handlerRollMax;

	private Timer timerRollMin;
	private TimerTask timerTaskRollMin;
	private Handler handlerRollMin;

	private Timer timerPitchMax;
	private TimerTask timerTaskPitchMax;
	private Handler handlerPitchMax;

	private Timer timerPitchMin;
	private TimerTask timerTaskPitchMin;
	private Handler handlerPitchMin;

	private Timer timerPanUp;
	private TimerTask timerTaskPanUp;
	private Handler handlerPanUp;

	private Timer timerPanDown;
	private TimerTask timerTaskPanDown;
	private Handler handlerPanDown;

	private Timer timerPanLeft;
	private TimerTask timerTaskPanLeft;
	private Handler handlerPanLeft;

	private Timer timerPanRight;
	private TimerTask timerTaskPanRight;
	private Handler handlerPanRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Environment.setLicensePath(sdcard + "/SuperMap/license/");
		Environment.initialization(this);
		CameraPermissionHelper.requestCameraPermission(this);
		info = new WorkspaceConnectionInfo();
		// 组件功能必须在 Environment 初始化之后才能调用
		setContentView(R.layout.activity_main);
		panUp = (Button) findViewById(R.id.btn_panup);
		panDown = (Button) findViewById(R.id.btn_pandown);
		panLeft = (Button) findViewById(R.id.btn_panleft);
		panRight = (Button) findViewById(R.id.btn_panright);
		pitchUp = (Button) findViewById(R.id.btn_pitchup);
		pitchDown = (Button) findViewById(R.id.btn_pitchdown);
		rollLeft = (Button) findViewById(R.id.btn_rollleft);
		rollRight = (Button) findViewById(R.id.btn_rollright);
		sceneControl = (SceneControl) findViewById(R.id.sceneControl);
		// 获取当前许可的状态，返回true 许可可用，返回false 许可不可用，不可用情况下无法打开本地场景。
		isLicenseAvailable = isLicenseAvailable();
		// 获取场景控件，在许可可用情况下打开本地场景。
		// 在非按钮事件、非触摸事件中,需要在此接口中写有关scene的方法，防止场景控件绘制失败。
		sceneControl.sceneControlInitedComplete(new SceneControlInitedCallBackListenner() {

			@Override
			public void onSuccess(String success) {
				if (isLicenseAvailable) {
					openLocalScene();
				}

			}
		});

		panUp.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					panUpLongTouch(-90);
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerPanUp.cancel();
				}

				return false;
			}
		});

		panDown.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					panDownLongTouch(90);
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerPanDown.cancel();
				}

				return false;
			}
		});
		panLeft.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					panLeftLongTouch(180);
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerPanLeft.cancel();
				}

				return false;
			}
		});
		panRight.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					panRightLongTouch(0);
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerPanRight.cancel();
				}

				return false;
			}
		});

		pitchUp.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					pitchMaxLongTouch();
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerTaskPitchMax.cancel();
				}

				return false;
			}
		});
		pitchDown.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					pitchMinLongTouch();
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerPitchMin.cancel();
				}

				return false;
			}
		});
		rollLeft.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					rollMinLongTouch();
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerRollMin.cancel();
				}

				return false;
			}
		});
		rollRight.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					rollMaxLongTouch();
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

					timerRollMax.cancel();
				}

				return false;
			}
		});

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

	// 判断许可是否可用
	@SuppressLint("ShowToast")
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

	// 旋转按钮长按事件
	public void rollMaxLongTouch() {

		timerRollMax = new Timer();

		handlerRollMax = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						sceneControl.getScene().setRollEye(0.25);
						break;

					default:
						break;
				}

			}

		};

		timerTaskRollMax = new TimerTask() {

			@Override
			public void run() {

				handlerRollMax.sendEmptyMessage(0);

			}
		};

		timerRollMax.schedule(timerTaskRollMax, 10, 10);
	}

	// 旋转按钮长按事件
	public void rollMinLongTouch() {

		timerRollMin = new Timer();

		handlerRollMin = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						sceneControl.getScene().setRollEye(-0.25);
						break;

					default:
						break;
				}

			}

		};

		timerTaskRollMin = new TimerTask() {

			@Override
			public void run() {

				handlerRollMin.sendEmptyMessage(0);

			}
		};

		timerRollMin.schedule(timerTaskRollMin, 10, 10);
	}

	// 俯仰按钮长按事件
	public void pitchMinLongTouch() {

		timerPitchMin = new Timer();

		handlerPitchMin = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						sceneControl.getScene().setPitch(-0.25);
						break;

					default:
						break;
				}

			}

		};

		timerTaskPitchMin = new TimerTask() {

			@Override
			public void run() {

				handlerPitchMin.sendEmptyMessage(0);

			}
		};

		timerPitchMin.schedule(timerTaskPitchMin, 10, 10);
	}

	// 俯仰长按事件
	public void pitchMaxLongTouch() {

		timerPitchMax = new Timer();

		handlerPitchMax = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						sceneControl.getScene().setPitch(0.25);
						break;

					default:
						break;
				}

			}

		};

		timerTaskPitchMax = new TimerTask() {

			@Override
			public void run() {

				handlerPitchMax.sendEmptyMessage(0);

			}
		};

		timerPitchMax.schedule(timerTaskPitchMax, 10, 10);
	}

	public void panUpLongTouch(final double value) {

		timerPanUp = new Timer();

		handlerPanUp = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						pan(value + 90);
						break;

					default:
						break;
				}

			}

		};

		timerTaskPanUp = new TimerTask() {

			@Override
			public void run() {

				handlerPanUp.sendEmptyMessage(0);

			}
		};
		timerPanUp.schedule(timerTaskPanUp, 10, 10);
	}
	public void panDownLongTouch(final double value) {

		timerPanDown = new Timer();

		handlerPanDown = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						pan(value + 90);
						break;

					default:
						break;
				}

			}

		};

		timerTaskPanDown = new TimerTask() {

			@Override
			public void run() {

				handlerPanDown.sendEmptyMessage(0);

			}
		};
		timerPanDown.schedule(timerTaskPanDown, 10, 10);
	}
	public void panLeftLongTouch(final double value) {

		timerPanLeft = new Timer();

		handlerPanLeft = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						pan(value + 90);
						break;

					default:
						break;
				}

			}

		};

		timerTaskPanLeft = new TimerTask() {

			@Override
			public void run() {

				handlerPanLeft.sendEmptyMessage(0);

			}
		};
		timerPanLeft.schedule(timerTaskPanLeft, 10, 10);
	}
	public void panRightLongTouch(final double value) {

		timerPanRight = new Timer();

		handlerPanRight= new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						pan(value + 90);
						break;

					default:
						break;
				}

			}

		};

		timerTaskPanRight = new TimerTask() {

			@Override
			public void run() {

				handlerPanRight.sendEmptyMessage(0);

			}
		};
		timerPanRight.schedule(timerTaskPanRight, 10, 10);
	}

	public void pan(double angle) {
		double l = 0.00001;
		double heading = sceneControl.getScene().getCamera().getHeading();
		double longla = l * Math.sin((heading + angle) * Math.PI / 180);
		double lan = l * Math.cos((heading + angle) * Math.PI / 180);
		sceneControl.getScene().pan(longla, lan);
	}

}
