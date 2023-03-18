package com.supermap.osgblayerattributequery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.FieldInfos;
import com.supermap.data.LicenseStatus;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Feature3D;
import com.supermap.realspace.Layer3D;
import com.supermap.realspace.Layer3DOSGBFile;
import com.supermap.realspace.Layer3DType;
import com.supermap.realspace.Layer3Ds;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.SceneControl.SceneControlInitedCallBackListenner;
import com.supermap.realspace.Selection3D;

/**
 * Title:查询OSGB图层属性的范例代码程序
 *
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 *
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 *
 * 1、范例简介：示范用户Layer3DOSGBFile图层查询属性。
 * 2、示例数据：将SampleData/CBD_android/中的数据拷贝到安装目录\SuperMap\data\ 3、关键类型/成员:
 * SceneControl.getScene 方法 Scene.open 方法
 * Layer3DOSGBFile.getAllFieldValueOfLastSelectedObject(); 方法
 *
 *
 * 4、使用步骤： (1)运行程序，按照默认的参数。 (2)点击单体化建筑，弹出属性信息。
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
    String workspacePath = "/sdcard/SuperMap/data/CBD_android/CBD_android.sxwu";
    // 三维场景名称
    String sceneName = "CBD_android";
    WorkspaceConnectionInfo info;
    WorkspaceType workspaceTypetemp = null;
    boolean isLicenseAvailable = false;
    QueryInfoBubblePopupWindow queryInfoBubblePopupWindow;
    private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Environment.setLicensePath(sdcard + "/SuperMap/license/");
        CameraPermissionHelper.requestCameraPermission(this);
        Environment.initialization(this);
        info = new WorkspaceConnectionInfo();
        // 组件功能必须在 Environment 初始化之后才能调用
        setContentView(R.layout.activity_main);
        sceneControl = (SceneControl) findViewById(R.id.sceneControl);
        queryInfoBubblePopupWindow = new QueryInfoBubblePopupWindow(MainActivity.this);
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

        sceneControl.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Layer3Ds layer3ds = sceneControl.getScene().getLayers();
                // 返回给定的三维图层集合中三维图层对象的总数。
                int count = layer3ds.getCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        Layer3D layer = layer3ds.get(i);
                        // 遍历count之后，得到三维图层对象
                        // 返回三维图层的选择集。
                        if (layer == null) {
                            continue;
                        }
                        final Selection3D selection = layer.getSelection();
                        if (selection == null) {
                            continue;
                        }
                        if (layer.getName() == null) {
                            continue;
                        }
                        // 获取选择集中对象的总数
                        if (selection.getCount() > 0) {

                            // 返回选择集中指定几何对象的系统 ID
                            // 本地数据获取
                            queryInfoBubblePopupWindow.m_QueryInfoData.clear();
                            queryInfoBubblePopupWindow.show(sceneControl, motionEvent.getX(), motionEvent.getY());
                        }
                        FieldInfos fieldInfos = layer.getFieldInfos();
                        vect(selection, layer, fieldInfos, queryInfoBubblePopupWindow);

                    }

                }

                return false;
            }
        });

        // 单体化弹框消失事件
        // queryInfoBubblePopupWindow.set
        queryInfoBubblePopupWindow.mDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                Layer3Ds layer3ds = sceneControl.getScene().getLayers();
                int count = layer3ds.getCount();
                for (int i = 0; i < count; i++) {
                    Selection3D selection3d = layer3ds.get(i).getSelection();
                    selection3d.clear();
                }

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

    // 属性查询时的矢量数据
    public static void vect(Selection3D selection, Layer3D layer, FieldInfos fieldInfos,
                            QueryInfoBubblePopupWindow queryInfoBubble) {
        Feature3D feature = null;
        Layer3DOSGBFile layer3d = null;
        if (layer.getType() == Layer3DType.OSGBFILE) {
            layer3d = (Layer3DOSGBFile) layer;
        } else if (layer.getType() == Layer3DType.VECTORFILE) {
            feature = selection.toFeature3D();
        }
        int count = fieldInfos.getCount();
        if (count > 0) {
            Object[] str = layer3d.getAllFieldValueOfLastSelectedObject();
            if (str == null) {
                return;
            }
            for (int j = 0; j < count; j++) {
                String name = fieldInfos.get(j).getName();
                String strValue;
                Object value;
                if (feature == null) {
                    value = str[j];
                } else {
                    value = feature.getFieldValue(name);
                }
                if (value.equals("NULL")) {
                    strValue = "";
                } else {
                    strValue = value.toString();
                }

                queryInfoBubble.additem(name + ":", strValue);

            }
        }

    }
}
