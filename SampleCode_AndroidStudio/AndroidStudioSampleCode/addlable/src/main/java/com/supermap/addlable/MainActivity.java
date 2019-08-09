package com.supermap.addlable;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.GeoText;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.TextAlignment;
import com.supermap.data.TextPart;
import com.supermap.data.TextStyle;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.toolkit.ColorPickerView;
import com.supermap.toolkit.TextStickerView;
import java.util.ArrayList;
import java.util.regex.Pattern;


import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:文本自定义编辑、颜色自定义选择
 * 通过自定义控件，设置文本大小，颜色，旋转角度
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
 * 1、范例简介：示范如何通过自定义控件，设置文本大小，颜色，旋转角度
 * 2、示例数据：数据目录："/sdcard/SampleData/AddLable/"
 *            地图数据：AddLable.smwu,AddLable.udb,AddLable.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *
 *
 *   canvas.drawText();//绘制文字
 *   canvas.drawBitmap();//绘制控件外形
 *   canvas.rotate();//旋转
 *
 * 4、使用步骤：
 * （1）点击添加标注，设置文本内容
 * （2）点击颜色，设置需要的颜色
 * （3）旋转、缩放、平移编辑文本控件，最后保存到数据集中
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
public class MainActivity extends AppCompatActivity {

    String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    MapView mapView;
    Workspace workspace;
    MapControl mapControl;
    String mtext;
    Button colorpickbtn;
    TextStickerView textStickerView;
    int red, green, blue, alpha;
    Layer lablelayer;

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
        Environment.setLicensePath(RootPath + "/SuperMap/License/");
        Environment.initialization(this);
        setContentView(R.layout.activity_main);
        initView();
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
    protected void initView() {
        openMap();
        textStickerView = findViewById(R.id.textstickerview);
        findViewById(R.id.addlable).setOnClickListener(listener);
        findViewById(R.id.save).setOnClickListener(listener);
        findViewById(R.id.delet).setOnClickListener(listener);
        findViewById(R.id.select).setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addlable:
                    addlable();
                    break;
                case R.id.save:
                    savelable();
                    break;
                case R.id.colorpick:
                    colorpick();
                    break;
                case R.id.delet:
                    deletText();
                    break;
                case R.id.select:
                    mapControl.setAction(Action.SELECT);
                    if (!lablelayer.isEditable()) {
                        lablelayer.setSelectable(true);
                        lablelayer.setEditable(true);
                    }
                    mapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
                        @Override
                        public void geometrySelected(GeometrySelectedEvent Event) {
                            mapControl.appointEditGeometry(Event.getGeometryID(), Event.getLayer());
                        }

                        @Override
                        public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arrayList) {

                        }
                    });
                    break;
            }
        }
    };

    private void openMap() {
        mapView = findViewById(R.id.mapview);
        mapControl = mapView.getMapControl();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(RootPath + "/SampleData/AddLable/AddLable.smwu");
        info.setType(WorkspaceType.SMWU);
        workspace = new Workspace();
        workspace.open(info);
        mapControl.getMap().setWorkspace(workspace);
        String mapname = workspace.getMaps().get(0);
        mapControl.getMap().open(mapname);
        lablelayer = mapControl.getMap().getLayers().get(0);
    }

    //保存标注
    private void savelable() {
        textStickerView.setVisibility(View.GONE);
        DatasetVector textdataset = (DatasetVector) lablelayer.getDataset();
        Recordset recordset = textdataset.getRecordset(false, CursorType.DYNAMIC);
        recordset.moveFirst();
        GeoText geoText = new GeoText();
        TextPart textPart = new TextPart();
        textPart.setText(textStickerView.getText());

        textPart.setRotation(-textStickerView.getRotateAngle());
        Point2D point2D = mapControl.getMap().pixelToMap(textStickerView.getpoint());
        textPart.setAnchorPoint(point2D);
        geoText.addPart(textPart);
        //设置风格
        TextStyle textStyle = new TextStyle();
        textStyle.setForeColor(new com.supermap.data.Color(textStickerView.getTextColor()));
        textStyle.setAlignment(TextAlignment.MIDDLECENTER);



        geoText.setTextStyle(textStyle);

        recordset.addNew(geoText);
        recordset.update();

        textPart.dispose();
        geoText.dispose();
        recordset.close();
        recordset.dispose();
        mapControl.getMap().refresh();
        //
        textStickerView.resetView();
    }

    //
    private void addlable() {
        addTextLable();
    }

    private void addTextLable() {
        View SetTextView = LayoutInflater.from(this).inflate(R.layout.layout_settext, null);
        final EditText editText = SetTextView.findViewById(R.id.edittext);
        colorpickbtn = SetTextView.findViewById(R.id.colorpick);
        colorpickbtn.setOnClickListener(listener);

        final AlertDialog alerdialog = new AlertDialog.Builder(MainActivity.this)
                .setView(SetTextView)
                .setTitle("Please Add Your Lable")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .show();
        alerdialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtext = editText.getText().toString();
                if (!mtext.isEmpty()) {
                    textStickerView.setText(mtext);
                    textStickerView.setTextColor(Color.argb(100, red, green, blue));
                    textStickerView.setVisibility(View.VISIBLE);
                    alerdialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "标注不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void colorpick() {
        final View contenview = LayoutInflater.from(this).inflate(R.layout.colorpick, null);
        final ColorPickerView colorPickerView = (ColorPickerView) contenview.findViewById(R.id.colorDisk);
        final TextView tv_rgb = (TextView) contenview.findViewById(R.id.tv_rgb);
        final EditText edit_rgb = (EditText) contenview.findViewById(R.id.edit_rgb);
        final TextView tv_colorStr = (TextView) contenview.findViewById(R.id.tv_colorStr);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.btn_clolor_Red:
                        edit_rgb.setText("214,33,17");
                        tv_colorStr.setText("#D62111");

                        contenview.setBackgroundColor(Color.argb(100, 214, 33, 17));
                        break;
                    case R.id.btn_clolor_White:
                        edit_rgb.setText("255,255,255");
                        tv_colorStr.setText("#FFFFFF");

                        contenview.setBackgroundColor(Color.argb(100, 255, 255, 255));
                        break;
                    case R.id.btn_clolor_Gray:
                        edit_rgb.setText("128,128,128");
                        tv_colorStr.setText("#808080");

                        contenview.setBackgroundColor(Color.argb(100, 128, 128, 128));
                        break;
                    case R.id.btn_clolor_Cyan:
                        edit_rgb.setText("0,255,255");
                        tv_colorStr.setText("#00FFFF");

                        contenview.setBackgroundColor(Color.argb(100, 0, 255, 255));
                        break;
                    case R.id.btn_clolor_Blue:
                        edit_rgb.setText("0,0,255");
                        tv_colorStr.setText("#0000FF");

                        contenview.setBackgroundColor(Color.argb(100, 0, 0, 255));
                        break;
                    case R.id.btn_clolor_Black:
                        edit_rgb.setText("0,0,0");
                        tv_colorStr.setText("#000000");

                        contenview.setBackgroundColor(Color.argb(100, 0, 0, 0));
                        break;
                    default:
                        break;
                }
            }
        };

        contenview.findViewById(R.id.btn_clolor_Red).setOnClickListener(listener);
        contenview.findViewById(R.id.btn_clolor_White).setOnClickListener(listener);
        contenview.findViewById(R.id.btn_clolor_Cyan).setOnClickListener(listener);
        contenview.findViewById(R.id.btn_clolor_Gray).setOnClickListener(listener);
        contenview.findViewById(R.id.btn_clolor_Blue).setOnClickListener(listener);
        contenview.findViewById(R.id.btn_clolor_Black).setOnClickListener(listener);


        final AlertDialog colorpickdialog = new AlertDialog.Builder(MainActivity.this)
                .setView(contenview)
                .setTitle("Pick A Color")
                .setCancelable(false)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .show();
        colorpickdialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] color = edit_rgb.getText().toString().split("\\,");
                if (color.length != 3) {
                    Toast.makeText(MainActivity.this, "请输入正确的数字，如：255,255,255", Toast.LENGTH_SHORT).show();
                } else {
                    if (isInteger(color[0]) && isInteger(color[1]) && isInteger(color[2])) {
                        red = Integer.parseInt(color[0]);
                        green = Integer.parseInt(color[1]);
                        blue = Integer.parseInt(color[2]);
                        colorpickbtn.setBackgroundColor(Color.argb(100, red, green, blue));
                        colorpickdialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "请输入正确的数字，如：255,255,255", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        colorPickerView.setOnColorBackListener(new ColorPickerView.OnColorBackListener() {
            @Override
            public void onColorBack(int a, int r, int g, int b) {

                edit_rgb.setText(r + "," + g + "," + b);
                tv_colorStr.setText(colorPickerView.getColorStr());

                contenview.setBackgroundColor(Color.argb(100, r, g, b));

            }
        });

    }

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    private void deletText() {
        if (mapControl.getCurrentGeometry()!=null) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("确定要删除这个对象?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mapControl.deleteCurrentGeometry();
                            lablelayer.setEditable(false);
                            mapControl.setAction(Action.PAN);

                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        else {
            Toast.makeText(MainActivity.this,"请选择需要删除对象",Toast.LENGTH_SHORT).show();
        }
    }
}
