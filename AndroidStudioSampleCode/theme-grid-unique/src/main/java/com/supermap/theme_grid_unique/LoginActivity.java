package com.supermap.theme_grid_unique;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.supermap.data.Environment;
import com.supermap.data.LicenseStatus;
import com.supermap.onlineservices.DownloadFile;
import com.supermap.onlineservices.OnlineService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";
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

        Environment.initialization(this);
        if (!checkLicenseValid()) {
            loginAction(this);

        }else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

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
     * 登录
     */
    public void loginAction(Context context) {

        String licensePath = android.os.Environment.getExternalStorageDirectory().getPath() + "/Supermap/License/";

        File file = new File(licensePath +"iMobileTrialLicense.slm");
        if (file.exists()) {
            file.delete();
        }
        OnlineService onlineService = new OnlineService(context);
        onlineService.login("imobile1234", "123456", new OnlineService.LoginCallback() {
            @Override
            public void loginSuccess() {

                Log.e(TAG,"onlineService loginSuccess");
                onlineService.downloadFile(context, "iMobileTrialLicense", licensePath +"iMobileTrialLicense.zip", new DownloadFile.DownloadListener() {
                    @Override
                    public void getProgress(int i) {

                    }
                    @Override
                    public void onComplete() {
                        Log.e(TAG,"onlineService onComplete");
                        onlineService.logout();
                        //解压文件
                        unzipFile(licensePath +"iMobileTrialLicense.zip",licensePath );
                        File file = new File(licensePath +"iMobileTrialLicense.zip");
                        if (file.exists()) {
                            file.delete();
                        }

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG,"onlineService onFailure");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                });
            }
            @Override
            public void loginFailed(String s) {
                Log.e(TAG,"onlineService loginFailed");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        return;

    }
    //检查许可是否合法
    private boolean checkLicenseValid() {
        LicenseStatus licenseStatus = Environment.getLicenseStatus();
        return licenseStatus.isLicenseValid();
    }
    public static boolean isUnZiped = false;
    /**
     * 解压一个压缩文档 到指定位置
     * @param zipFile 压缩包的名字
     * @param targetDir 指定的路径
     */
    public static void unzipFile(String zipFile, String targetDir){
        Log.v("XZip", "UnZipFolder(String, String)");
        java.util.zip.ZipInputStream inZip;
        try {

            inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFile));

            java.util.zip.ZipEntry zipEntry;
            String szName = "";

            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();

                if (zipEntry.isDirectory()) {

                    File folder = new File(targetDir + File.separator + szName);
                    folder.mkdirs();

                } else {

                    File file = new File(targetDir + File.separator + szName);
                    file.createNewFile();
                    // get the output stream of the file
                    java.io.FileOutputStream out = new java.io.FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = inZip.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();

            isUnZiped = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
