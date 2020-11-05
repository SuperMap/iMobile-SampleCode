package com.supermap.bookmarks;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.BookMark;
import com.supermap.mapping.BookMarks;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
/**
 * <p>
 * Title:地图书签
 * </p>
 *
 * <p>
 * Description:示范新建地图书签，保存地图书签，展示地图书签
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范新建地图书签，保存地图书签，展示地图书签
 * 2、示例数据：安装目录/SampleData/City/Changchun.smwu
 * 3、关键类型/成员:
 *      BookMarks
 *
 * 4、使用步骤：
 *   (1）浏览地图，点击右下角按钮，新建地图书签。
 *   (2)保存当前地图标签
 *   (3)查看地图标签
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends AppCompatActivity {

  private MapView mMapView;
  private MapControl mapControl;
  private Workspace workspace;
  private List<String> mBookmarksSpinnerList;
  private ArrayAdapter<String> mDataAdapter;
  private BookMarks bookMarks;
  private String rootPath =android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
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
    FloatingActionButton addBookmarkFab;

    Spinner bookmarksSpinner;

    mMapView = (MapView) findViewById(R.id.mapView);
    mapControl = mMapView.getMapControl();
    workspace = new Workspace();
    mapControl.getMap().setWorkspace(workspace);
    WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
    info.setServer(rootPath+"/SampleData/City/Changchun.smwu");
    info.setType(WorkspaceType.SMWU);
    workspace.open(info);
    mapControl.getMap().open(workspace.getMaps().get(0));
    mapControl.getMap().refresh();

    bookMarks = mapControl.getMap().getBookMarks();

    // inflate the floating action button
    addBookmarkFab = (FloatingActionButton) findViewById(R.id.addbookmarkFAB);

    // show the dialog for acquiring bookmark name from the user
    addBookmarkFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(v.getContext());
      }
    });

    // add some default bookmarks to the map
    addDefaultBookmarks();

    // populate the spinner list with default bookmark names
    bookmarksSpinner = (Spinner) findViewById(R.id.bookmarksspinner);
    mBookmarksSpinnerList = new ArrayList<>();
    mBookmarksSpinnerList.add(bookMarks.get(0).getName());
    mBookmarksSpinnerList.add(bookMarks.get(1).getName());
    mBookmarksSpinnerList.add(bookMarks.get(2).getName());

    // initialize the adapter for the bookmarks spinner
    mDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mBookmarksSpinnerList);
    mDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    bookmarksSpinner.setAdapter(mDataAdapter);

    // when an item is selected in the spinner set the mapview viewpoint to the selected bookmark's viewpoint
    bookmarksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mapControl.getMap().setScale(bookMarks.get(position).getMapScale());
        mapControl.getMap().setCenter(bookMarks.get(position).getMapCenter());
        mapControl.getMap().refresh();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
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

  /**
   * adds the default bookmarks to the maps BookmarkList
   */
  private void addDefaultBookmarks() {
    BookMark bookMark = new BookMark(mapControl.getMap().getName(),mapControl.getMap().getCenter(),mapControl.getMap().getScale());
    BookMark bookMark1 = new BookMark("default1",new Point2D(5019.376169680683,-4070.4633243491717),0.002000556352354867);
    BookMark bookMark2 = new BookMark("default2",new Point2D(7394.166031982048,-4364.5094736472265),3.768621327735616E-4);
    bookMarks.add(bookMark);
    bookMarks.add(bookMark1);
    bookMarks.add(bookMark2);
  }

  /**
   * add a new bookmark at the location being displayed in the MapView's current Viewpoint
   *
   * @param Name of the new bookmark
   */
  private void addBookmark(String Name) {

    BookMark bookMark = new BookMark(Name,mapControl.getMap().getCenter(),mapControl.getMap().getScale());
    bookMarks.add(bookMark);
    mBookmarksSpinnerList.add(Name);
    mDataAdapter.notifyDataSetChanged();
  }

  /**
   * shows dialog that prompts user to add a name for the new Bookmark
   */
  private void showDialog(Context context) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getResources().getString(R.string.alert_dialog_title));

    // Set up the input
    final EditText input = new EditText(this);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    // Set up the buttons
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // get the input from EditText
        String bookmarkName = input.getText().toString();
        // check if EditText is not empty & bookmark name has not been used
        if (bookmarkName.length() > 0 && !mBookmarksSpinnerList.contains(bookmarkName)) {
          addBookmark(bookmarkName);
        } else {
          // display toast explaining bookmark not set
          Toast.makeText(getApplicationContext(), getResources().getString(R.string.bookmark_not_saved),
              Toast.LENGTH_LONG).show();
          dialog.cancel();
        }
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(@NonNull DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });

    builder.show();

  }
}