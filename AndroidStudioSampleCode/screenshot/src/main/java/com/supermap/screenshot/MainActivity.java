package com.supermap.screenshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapView;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private final int requestCode = 2;
  private final String[] permission = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
  private MapView mapView1;
  String rootPath =android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
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
      Environment.setLicensePath(rootPath + "/SuperMap/license");
      Environment.initialization(this);

      setContentView(R.layout.activity_main);

      Environment.setOpenGLMode(true);
      Environment.setWebCacheDirectory(rootPath + "/SuperMap/data/webCache");

        mapView1 = (MapView) findViewById(R.id.mapview);
        Workspace workspace1 = new Workspace();
        mapView1.getMapControl().getMap().setWorkspace(workspace1);
        WorkspaceConnectionInfo info1 = new WorkspaceConnectionInfo();
        info1.setServer(rootPath+"/SampleData/Hunan/Hunan.smwu");
        info1.setType(WorkspaceType.SMWU);
        workspace1.open(info1);
        mapView1.getMapControl().getMap().open(workspace1.getMaps().get(0));
        mapView1.getMapControl().getMap().refresh();
  }

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
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // handle menu item selection

    int itemId = item.getItemId();
    if (itemId == R.id.CaptureMap) {
      // Check permissions to see if failure may be due to lack of permissions.
      boolean permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, permission[0]) ==
          PackageManager.PERMISSION_GRANTED;

      if (!permissionCheck) {
        // If permissions are not already granted, request permission from the user.
        ActivityCompat.requestPermissions(MainActivity.this, permission, requestCode);
      } else {
        captureScreenshotAsync();
      }
    }

    return true;
  }

  /**
   * capture the map as an image
   */
  private void captureScreenshotAsync() {
          Bitmap bitmap=Bitmap.createBitmap(mapView1.getMapControl().getMapWidth(),mapView1.getMapControl().getHeight(), Bitmap.Config.ARGB_8888);
          mapView1.getMapControl().outputMap(bitmap);
          // play the camera shutter sound
          MediaActionSound sound = new MediaActionSound();
          sound.play(MediaActionSound.SHUTTER_CLICK);
          Log.d(TAG, "Captured the image!!");
          // save the exported bitmap to an image file
          SaveImageTask saveImageTask = new SaveImageTask();
          saveImageTask.execute(bitmap);
  }

  /**
   * save the bitmap image to file and open it
   *
   * @param bitmap
   * @throws IOException
   */
  private File saveToFile(Bitmap bitmap) throws IOException {
    File root;
    File file = null;
    String fileName = "map-export-image" + System.currentTimeMillis() + ".jpg";
    root = android.os.Environment.getExternalStorageDirectory();
    File fileDir = new File(root.getAbsolutePath() + "/SuperMap Export/");
    boolean isDirectoryCreated = fileDir.exists();
    if (!isDirectoryCreated) {
      isDirectoryCreated = fileDir.mkdirs();
    }
    if (isDirectoryCreated) {
      file = new File(fileDir, fileName);
      // write the bitmap to PNG file
      FileOutputStream fos = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    }
    return file;

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      captureScreenshotAsync();
    } else {
      Toast.makeText(MainActivity.this, getResources().getString(R.string.storage_permission_denied), Toast
          .LENGTH_SHORT).show();
    }
  }

  /**
   * AsyncTask class to save the bitmap as an image
   */
  private class SaveImageTask extends AsyncTask<Bitmap, Void, File> {

    @Override
    protected void onPreExecute() {
      // display a toast message to inform saving the map as an image
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.map_export_message), Toast.LENGTH_SHORT)
          .show();
    }

    /**
     * save the file using a worker thread
     */
    @Override
    protected File doInBackground(Bitmap... mapBitmap) {

      try {
        return saveToFile(mapBitmap[0]);
      } catch (Exception e) {
        Log.e(TAG, getResources().getString(R.string.map_export_failure) + e.getMessage());
      }

      return null;

    }

    /**
     * Perform the work on UI thread to open the exported map image
     */
    @Override
    protected void onPostExecute(File file) {
      // Open the file to view
      Intent i = new Intent();
      i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      i.setAction(Intent.ACTION_VIEW);
//      i.setDataAndType(
//          FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", file),
//          "image/png");
      startActivity(i);
    }
  }

  public static class ScreenshotFileProvider extends FileProvider {}
}