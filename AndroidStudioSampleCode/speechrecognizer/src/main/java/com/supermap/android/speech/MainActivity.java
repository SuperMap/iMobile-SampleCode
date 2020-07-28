package com.supermap.android.speech;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapView;
import com.supermap.mapping.speech.IntelligentSpeechListener;
import com.supermap.mapping.speech.SpeechManager;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:语音识别示范代码
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范如何运用语音识别，需要用户自行放入讯飞的语音识别SDK。
 * 2、示例数据：数据目录："/sdcard/SampleData/Beijing/"
 *            地图数据：beijing.smwu,clip1.udb,clip1.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *   SpeechManager.init(); //语音组件初始化
 *   SpeechManager.getInstance();//获取语音识别的单例对象
 *   SpeechManager.setVAD_BOS_Time();//设置前段点超时
 *   SpeechManager.setVAD_EOS_Time();//后端后点超时
 *   SpeechManager.setIsPunctuation();//返回结果有无标点
 *   SpeechManager.setAudioPath();//设置录音文件保存路径
 *   SpeechManager.isListening();//获取当前是否在语音会话中
 *   SpeechManager.cancel();//取消当前语音会话
 *   SpeechManager.startListening();//开始语音会话
 *   SpeechManager.stopListening();//结束语音会话(不再接收新的语音输入)
 *   SpeechManager.destroy();//销毁语音识别对象
 *
 * 4、使用步骤：
 *  (1)点击"点UI的语音识别"，会弹出语音识别对话框，此时开始说话即可开始语音识别。
 *  (2)点击"不带UI的语音识别"按钮，开始没有提示界面的语音识别。
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private SpeechManager mSpeechManager = null;

	private TextView mtv_info = null;
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
        Environment.setLicensePath(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperMap/License");
		Environment.initialization(this);

		setContentView(R.layout.activity_main);
        initView();

        //语音组件初始化
        SpeechManager.init(MainActivity.this, getString(R.string.app_id)); //先初始化

        // 当需要进行语音识别的时候再获取单例对象
        mSpeechManager = SpeechManager.getInstance(MainActivity.this);
        setParameters();

        openWorkspace();
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
     * 打开工作空间，显示地图
     */
    private boolean openWorkspace() {
        final String dataPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SampleData/Beijing/beijing.smwu";
        Workspace workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(dataPath);
        info.setType(WorkspaceType.SMWU);
        boolean isOpen = workspace.open(info);
        if (!isOpen) {
            return false;
        }
        MapView mapView = (MapView) findViewById(R.id.mapView);
        Map map = mapView.getMapControl().getMap();
        map.setWorkspace(workspace);
        map.open(workspace.getMaps().get(0));

//        m_Map.viewEntire();
        map.setViewBounds(map.getBounds());
        map.refresh();

        return true;
    }

	//设置语音识别参数
    private void setParameters() {
        mSpeechManager.setVAD_BOS_Time(3000);//前段点超时
        mSpeechManager.setVAD_EOS_Time(2000);//后端点超时
        mSpeechManager.setIsPunctuation(true);//返回结果有无标点
        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperMap/Speech.wav";
        mSpeechManager.setAudioPath(path);//设置录音文件保存路径
    }


    /**
	 * 初始化主界面控件
	 */
	private void initView() {
	    mtv_info = (TextView) findViewById(R.id.tv_info);
		((Button) findViewById(R.id.btn_ui_speech)).setOnClickListener(onClickListener);
		((Button)findViewById(R.id.btn_speech)).setOnClickListener(onClickListener);
	}

    // 按钮单击监听事件
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_ui_speech:
                    mtv_info.setHint("");
                    if (mSpeechManager.isListening()) {
                        mSpeechManager.cancel();
                    }

                    // 带UI界面的语音识别
                    showSpeechSearchDialog();

                    break;
				case R.id.btn_speech:
					// 不带UI的语音识别
					if (mSpeechManager != null) {
                        mSpeechManager.startListening(mListener);
                    }
					break;
			default:
				break;
			}
		}
	};

	private IntelligentSpeechListener mListener = new IntelligentSpeechListener() {

        @Override
        public void onBeginOfSpeech() {
            Log.e(TAG, "onBeginOfSpeech");
            mtv_info.setHint("请开始说话...");
        }

        @Override
        public void onEndOfSpeech() {
            Log.e(TAG, "onEndOfSpeech");
        }

        @Override
        public void onVolumeChanged(int volume) {
//            Log.e(TAG, "onVolumeChanged: " + volume);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, "onError: " + error);
            mtv_info.setHint("");
            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(String info, boolean isLast) {
            Log.e(TAG, "onResult: " + info + ", isLast: " + isLast);

            String str = mtv_info.getText().toString();
            mtv_info.setText(str + info);

            if (isLast) {
                Toast.makeText(MainActivity.this, "本次会话结束", Toast.LENGTH_SHORT).show();
            }
        }
    };

	//显示语音对话框
	private void showSpeechSearchDialog() {
        SpeechDialog dialog = new SpeechDialog(this, R.style.SpeechDialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
	}
}



