package com.supermap.AImeasure;

/**
 * <p>
 * Title:AI测图
 * </p>
 *javagl
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   	展示AI测图功能。
 *
 * 2、Demo数据：
 *      许可目录："../SuperMap/License/"
 *
 * 3、关键类型/成员:
 *    mARMeasureView.setFlagType();			         方法
 *    mARMeasureView.setOnLengthChangedListener();	 方法
 *    mARMeasureView.addPoint();				     方法
 *    mARMeasureView.startMeasure();			     方法
 *    mARMeasureView.stopMeasure();				     方法
 *    mARMeasureView.undo();		                 方法
 *
 * 4、功能展示
 *   (1)平视找到平面；
 *   (2)添加测图标记；
 *   (3)完成测图。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */

import com.supermap.ar.highprecision.ARMeasureView;
import com.supermap.ar.highprecision.OnLengthChangedListener;
import com.supermap.data.Environment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import java.text.DecimalFormat;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,
        OnLengthChangedListener {


    final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();

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
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CAMERA);
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


   ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private ARMeasureView mARMeasureView = null;
    private ImageButton mBtnRecord;         //记录
    private ImageButton mBtnCancelRecord;   //撤销
    private ImageButton mBtnSupport;        //辅助图标显示，十字或者旗帜等。
    private Button      mBtnChangeModel;    //切换模型

    private TextView mTxtTotalLength;
    private TextView mTxtCurrentLengh;

    private boolean mModelFlag = false;
    private boolean mIsStartMeasure = false;
    DecimalFormat dF = new  java.text.DecimalFormat("0.000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions() ;
        //设置许可文件路径
        Environment.setLicensePath(sdcard + "/SuperMap/license/");

        //组件功能必须在 Environment 初始化之后才能调用
        Environment.initialization(this);

        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {

        mARMeasureView = findViewById(R.id.layoutHPMeasureview);

        //设置距离变化监听
        mARMeasureView.setOnLengthChangedListener(this);

        //保存当前记录
        mBtnRecord = findViewById(R.id.btnRecord);
        mBtnRecord.setOnClickListener(this);

        //撤销上一记录
        mBtnCancelRecord = findViewById(R.id.btnCancelRecord);
        mBtnCancelRecord.setOnClickListener(this);


        //设置中心辅助
        mBtnSupport = findViewById(R.id.btnSupport);
        mBtnSupport.setOnClickListener(this);


        //切换模型
        mBtnChangeModel = findViewById(R.id.btnChangeModel);
        mBtnChangeModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mModelFlag == false){
                    mARMeasureView.setFlagType(ARMeasureView.FlagType.RED_FLAG);
                    mModelFlag = true;
                }else{
                    mARMeasureView.setFlagType(ARMeasureView.FlagType.PIN_BOWLING);
                    mModelFlag = false;
                }

            }
        });


        //这里输出距离量算信息
        mTxtTotalLength  = findViewById(R.id.txtTotalLength);
        mTxtCurrentLengh = findViewById(R.id.txtCurrentLength);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mARMeasureView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mARMeasureView.onPause();

    }
    @Override
    public void onCurrentLengthChanged(float v) {
        mTxtCurrentLengh.setText("CurrentLength:"+dF.format(v)+"m");
    }

    @Override
    public void onTotalLengthChanged(float v) {
        mTxtTotalLength.setText("TotalLength"+dF.format(v)+"m");
    }

    @Override
    public void onCurrentToLastPntDstChanged(float v) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRecord:
//                mMeasureView.addNewRecord();
                mARMeasureView.addPoint();
                break;

            case R.id.btnChangeModel:
                if( mModelFlag == false){
                    mARMeasureView.setFlagType(ARMeasureView.FlagType.RED_FLAG);
                    mModelFlag = true;
                }else{
                    mARMeasureView.setFlagType(ARMeasureView.FlagType.PIN_BOWLING);
                    mModelFlag = false;
                }
                break;

            case R.id.btnCancelRecord:
//                mARMeasureView.cleanAll();
                mARMeasureView.undo();
                break;

            case R.id.btnSupport:
                if(mIsStartMeasure == false){
                    mARMeasureView.startMeasure();
                    mIsStartMeasure = true;
                }else{
                    mARMeasureView.stopMeasure();
                    mIsStartMeasure = false;
                }
                break;

        }
    }


}
