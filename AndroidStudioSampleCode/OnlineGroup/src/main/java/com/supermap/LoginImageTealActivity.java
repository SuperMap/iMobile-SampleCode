package com.supermap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;


import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.onlineservices.OnlineService;
import com.supermap.supermap.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginImageTealActivity extends AppCompatActivity {

    private static final String TAG = "LoginImageTealActivity";

    private ProgressBar mProgress_bar;
    private FloatingActionButton mFloatingActionButton;
    private View mParent_view;

    private TextInputEditText mUsername = null;
    private TextInputEditText mPassword = null;

    private AppCompatCheckBox mCB_RemberMe = null;
    private AppCompatCheckBox mCB_ShowPassword = null;

    private SharedPreferences mSharedPreferences;
    private final String LOGIN_CONFIG = "Login_Config";
    private final String REMEMBER_ME = "RememberMe";
    private final String SHOW_PASSWORD = "ShowPassword";
    private final String USERNAME = "Username";
    private final String PASSWORD = "Password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_image_teal);

        requestPermissions();

        mSharedPreferences = this.getSharedPreferences(LOGIN_CONFIG, Context.MODE_PRIVATE);

        initView();
        initListener();
    }

    /*
     *获取时间
     */
    private String getTime(long time) {
        //得到long类型当前时间
//        long l = System.currentTimeMillis();
        //new日期对
//        Date date = new Date(l);
        Date date = new Date(time);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        return dateFormat.format(date);
    }

    //检查许可是否合法
    private boolean checkLicenseValid() {
        LicenseStatus licenseStatus = Environment.getLicenseStatus();
        if (!licenseStatus.isLicenseExsit()) {
            return false;
        }
        if (licenseStatus.isTrailLicense()) {
            Log.e(TAG, "试用许可");
        }
        Date expireDate = licenseStatus.getExpireDate();
        String time = getTime(expireDate.getTime());
        Log.e(TAG, "许可过期时间：" + time);

        return licenseStatus.isLicenseValid();
    }
    private static final String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath();
    private void initEnvironment() {
        Log.e(TAG, "initEnvironment");
        //初始化
        Environment.setLicensePath(SDCARD+"/SuperMap/License/");
//        Environment.initialization(this);
//        Environment.setOpenGLMode(true);


    }



    private void initListener() {
        ((View) findViewById(R.id.sign_up_for_account)).setOnClickListener(view -> Snackbar.make(mParent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show());

        mFloatingActionButton.setOnClickListener(v -> loginAction());

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.e(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.e(TAG, "onTextChanged");
                if (mCB_RemberMe.isChecked()) {
                    mSharedPreferences.edit().putString(USERNAME, Objects.requireNonNull(mUsername.getText()).toString()).apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.e(TAG, "afterTextChanged");
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (mCB_ShowPassword.isChecked()) {
                   mSharedPreferences.edit().putString(PASSWORD, Objects.requireNonNull(mPassword.getText()).toString()).apply();
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCB_RemberMe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mSharedPreferences.edit().putBoolean(REMEMBER_ME, true).apply();
                mSharedPreferences.edit().putString(USERNAME, Objects.requireNonNull(mUsername.getText()).toString()).apply();
                mSharedPreferences.edit().putString(PASSWORD, Objects.requireNonNull(mPassword.getText()).toString()).apply();
            } else {
                mSharedPreferences.edit().putBoolean(REMEMBER_ME, false).apply();
                mSharedPreferences.edit().remove(USERNAME).apply();
                mSharedPreferences.edit().remove(PASSWORD).apply();
            }
        });

        mCB_ShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mSharedPreferences.edit().putBoolean(SHOW_PASSWORD, true).apply();
            } else {
                mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mSharedPreferences.edit().putBoolean(SHOW_PASSWORD, false).apply();
            }
            mPassword.setSelection(mPassword.length());
        });
    }

    private void initView() {
        mParent_view = findViewById(android.R.id.content);
        mProgress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mCB_RemberMe = findViewById(R.id.cb_remember_me);
        mCB_ShowPassword = findViewById(R.id.cb_show_password);

        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);

        boolean showPassword = mSharedPreferences.getBoolean(SHOW_PASSWORD, false);
        if (showPassword) {
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mCB_ShowPassword.setChecked(true);
        } else {
            mCB_ShowPassword.setChecked(false);
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        boolean remember = mSharedPreferences.getBoolean(REMEMBER_ME, false);
        if (remember) {
            String username = mSharedPreferences.getString(USERNAME, "");
            String password = mSharedPreferences.getString(PASSWORD, "");
            mUsername.setText(username);
            mPassword.setText(password);

            mUsername.setSelection(mUsername.length());
            mPassword.setSelection(mPassword.length());

            mCB_RemberMe.setChecked(true);
        } else {
            mCB_RemberMe.setChecked(false);
        }
    }

    boolean mNetAvailable = false;

    /**
     * 登录
     */
    private void loginAction() {
        mProgress_bar.setVisibility(View.VISIBLE);
        mFloatingActionButton.setAlpha(0f);
        mFloatingActionButton.setEnabled(false);

        if (!checkLicenseValid()) {
            //许可无效

            mProgress_bar.setVisibility(View.GONE);
            mFloatingActionButton.setAlpha(1f);
            mFloatingActionButton.setEnabled(true);
            return;
        }



        //检查网络是否可用
        new Thread(() -> {

            runOnUiThread(() -> {

                    String un = Objects.requireNonNull(mUsername.getText()).toString();
                    String pw = Objects.requireNonNull(mPassword.getText()).toString();

                    if (un.isEmpty()) {
                        Snackbar.make(mParent_view, "请填写用户名,电话号码或邮箱", Snackbar.LENGTH_SHORT).show();
                        mProgress_bar.setVisibility(View.GONE);
                        mFloatingActionButton.setAlpha(1f);
                        mFloatingActionButton.setEnabled(true);
                        return;
                    }
                    if (pw.isEmpty()) {
                        Snackbar.make(mParent_view, "请填写密码", Snackbar.LENGTH_SHORT).show();
                        mProgress_bar.setVisibility(View.GONE);
                        mFloatingActionButton.setAlpha(1f);
                        mFloatingActionButton.setEnabled(true);
                        return;
                    }

                    new Handler().postDelayed(() -> {
                        if (StringTools.isMobilePhoneNumber(un)) {
                            //电话号码
                            OnlineService.loginByPhoneNumber(un, pw, new OnlineService.LoginCallback() {
                                @Override
                                public void loginSuccess() {
                                    Intent intent=new Intent(LoginImageTealActivity.this,AdvancedActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void loginFailed(String error) {

                                }
                            });
                        } else {
                            //用户名或邮箱
                            OnlineService.login(un, pw, new OnlineService.LoginCallback() {
                                @Override
                                public void loginSuccess() {
                                    Intent intent=new Intent(LoginImageTealActivity.this,AdvancedActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void loginFailed(String error) {

                                }
                            });
                        }

                    }, 500);

            });
        }).start();

    }

    private void enableButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress_bar.setVisibility(View.GONE);
                mFloatingActionButton.setAlpha(1f);
                mFloatingActionButton.setEnabled(true);
            }
        });
    }

    /***********************************************************************************/
    /**
     * 需要申请的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

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
            initEnvironment();
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
                    Manifest.permission.CAMERA,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.CHANGE_WIFI_STATE);
            //没有授权，编写申请权限代码
            Log.e(TAG, "requestPermissions");
        } else {
            Log.e(TAG, "hasPermissions");
            //已经授权，执行操作代码

            initEnvironment();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        Log.e(TAG, "onRequestPermissionsResult" + requestCode + ", permissions: " + permissions.toString() +", grantResults: " + grantResults.toString());

        initEnvironment();
    }

    /***********************************************************************************/


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 退出应用
     */
    public void exit() {
        finish();
        Process.killProcess(Process.myPid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否退出应用?");
            builder.setPositiveButton("退出", (arg0, arg1) -> {
                //quit the application
                exit();
            });
            builder.setNegativeButton("取消", (arg0, arg1) -> {
            });
            builder.setCancelable(false);
            builder.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
