package com.supermap.imobile.streamingapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

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
            Manifest.permission.CAMERA,
    };

    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        requestPermissions();
    }

    private void initView() {
        mProgressView = findViewById(R.id.login_progress);
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
            login();
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
                    Manifest.permission.CHANGE_WIFI_STATE);
            //没有授权，编写申请权限代码
        } else {
            //已经授权，执行操作代码
            login();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 登录
     */
    private void login() {
        showProgress(true);

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            });
        }).start();

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /*动态权限回调***************************************************************************************************/
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        Toast.makeText(this, "用户授权成功", Toast.LENGTH_SHORT).show();
        login();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        Toast.makeText(this, "用户授权失败", Toast.LENGTH_SHORT).show();
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }

        //处理权限名字字符串
        StringBuffer sb = new StringBuffer();
        for (String str : perms){
            sb.append(str);
            sb.append("\n");
        }
        sb.replace(sb.length() - 2,sb.length(),"");

//        switch (requestCode){
//            case 0:
//                Toast.makeText(this, "已拒绝权限" + perms.get(0), Toast.LENGTH_SHORT).show();
//                break;
//            case 1:
//                Toast.makeText(this, "已拒绝WRITE_EXTERNAL_STORAGE和WRITE_EXTERNAL_STORAGE权限"+ perms.get(0), Toast.LENGTH_SHORT).show();
//                break;
//        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            Toast.makeText(this, "已拒绝权限" + sb + "并不再询问" , Toast.LENGTH_SHORT).show();
            new AppSettingsDialog
                    .Builder(this)
                    .setRationale("此功能需要：" + sb + "权限，否则无法正常使用，是否打开设置？")
                    .setPositiveButton("是")
                    .setNegativeButton("否")
                    .build()
                    .show();
        }
    }
}

