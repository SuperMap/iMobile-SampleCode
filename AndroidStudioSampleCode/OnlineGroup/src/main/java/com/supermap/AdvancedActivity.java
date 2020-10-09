package com.supermap;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


import com.supermap.onlineservices.OnlineCallBack;
import com.supermap.onlineservices.OnlineService;
import com.supermap.onlineservices.utils.AccountInfoType;
import com.supermap.onlineservices.utils.GroupOrderBy;
import com.supermap.onlineservices.utils.JoinTypes;
import com.supermap.onlineservices.utils.OrderType;
import com.supermap.supermap.R;


import pub.devrel.easypermissions.EasyPermissions;

public class AdvancedActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AdvancedActivity";


    Fragment mCooperationFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEnvironment();
        setContentView(R.layout.activity_sample_advanced);
        showCooperationFragment();

    }


    private void initEnvironment() {
        requestPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void showCooperationFragment() {
        if (mCooperationFragment == null) {
            mCooperationFragment = CooperationFragment.newInstance("CooperationFragment");
            getSupportFragmentManager().beginTransaction().add(R.id.advanced_frame_container, mCooperationFragment).commit();
        }
        hideAllFragment();
        getSupportFragmentManager().beginTransaction().show(mCooperationFragment).commit();
    }
    private void hideAllFragment() {
        if (mCooperationFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(mCooperationFragment).commit();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        //更新当前用户信息
        OnlineService.getAccountInfo(new OnlineService.AccountInfoCallback() {
            @Override
            public void accountInfoSuccess(String nickName) {
                OnlineService.getAccountInfoByType(nickName, AccountInfoType.values()[0], new OnlineService.AccountInfoByTypeCallback() {
                    @Override
                    public void accountInfoByTypeSuccess(String nickName, String userId) {
                        GroupManager.setCurrentUserId(userId);
                        GroupManager.setCurrentUserName(nickName);
                    }

                    @Override
                    public void accountInfoByTypeFailed(String errInfo) {
                        Log.e(TAG, errInfo);
                    }
                });
            }

            @Override
            public void accountInfoFailed(String errInfo) {

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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*************************************************************************************/
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
