package com.supermap.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.supermap.ai.AIRecognition;
import com.supermap.ai.AIDetectView;
import com.supermap.ai.AIDetectViewInfo;
import com.supermap.ai.AISize;
import com.supermap.data.Environment;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * <p>
 * Title:AI模块示范代码
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
 * 1、范例简介：示范如何AI功能进行物体的检测识别。
 * 2、示例数据：许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *       AIdetectView.resumeDetect();//开始AI识别
 *       AIdetectView.pauseDetect();//停止AI识别
 *       AIdetectView.setisPolymerize(); //设置AI是否聚合模式
 *       AIdetectView.ScreenCapture(); //AI截图
 *       AIdetectView.startCountTrackedObjs();//AI开始跟踪计数
 *       AIdetectView.stopCountTrackedObjs();//AI停止跟踪计数
 *       AIdetectView.DetectListener();//AI识别结果回调监听

 *
 * 4、使用步骤：
 *      (1)点击选择分类，选择希望进行识别的物件分类。
 *      (2)点击开始识别，对摄像头内的物体进行识别检测。
 *      (3)点击停止识别，停止对摄像头内的物体进行识别检测。
 *      (4)点击聚合模式，将检测目标聚合到一起。
 *      (5)点击开始计数，对摄像头内的目标进行跟踪计数。
 *      (6)点击停止计数，停止对摄像头内的目标进行跟踪计数。
 *      (7)点击AI截图，对当前的AI识别内容进行截图
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
public class MainActivity extends FragmentActivity{
    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath() + "/";
    private AIDetectView mAIdetectView = null;
    private AlertDialog alertDialog3; //多选框
    Vector<String> strToUse;
    Vector<String> strToSet;
    Vector<String> strToSetTmp;
    private ListView mListview = null;
    private SeekBar m_seekBarPoly;
    private Button mBtnCountInfo;
    private Handler mHandler;
    Runnable runnableUi;
    private int mTrackedCount = 0;
    private ImageView mPreviewImage;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        Environment.setLicensePath(SDCARD + "SuperMap/license/");
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
    private void initView() {
        mPreviewImage = (ImageView)findViewById(R.id.previewBitmap);
        mHandler = new Handler();
        runnableUi = new Runnable() {
            @Override
            public void run() {
                String str = "Count:" + mTrackedCount;
                mBtnCountInfo.setText(str);
//                if(mAIdetectView.getPreviewBitmap()!=null)
//                {
//                    mPreviewImage.setImageBitmap(mAIdetectView.getPreviewBitmap());
//                }
            }
        };
        mBtnCountInfo =(Button)findViewById(R.id.countInfo);
        mAIdetectView = (AIDetectView) findViewById(R.id.test_arcontrol);
        AIDetectViewInfo aiDetectViewInfo = new AIDetectViewInfo();
        aiDetectViewInfo.assetManager = getAssets();
        aiDetectViewInfo.modeFile = "detect.tflite";
        aiDetectViewInfo.lableFile = "labelmap.txt";
        aiDetectViewInfo.inputSize = 300;
        aiDetectViewInfo.isQuantized = true;
        mAIdetectView.setDetectInfo(aiDetectViewInfo);
        mAIdetectView.init();

        strToSet = new Vector<String>();
        strToSetTmp = new Vector<String>();
        Vector<String> strDetectArray;
        try {
            strToUse = mAIdetectView.getAllDetectArrayProvide();  //获取可用模型类别，获取完之后加入复选框列表由用户选择
        } catch (final IOException e) {
            Toast toast =
                    Toast.makeText(
                            this, "Get all provide detect array failed", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Vector<String> strToUsetmp = new Vector<String>();
        strToUsetmp.add("mouse");

        mAIdetectView.setDetectArrayToUse(strToUsetmp);
        mAIdetectView.setDetectInterval(0);

        mAIdetectView.setDetectedListener(new AIDetectView.DetectListener() {   //设置识别结果回调监听
            /**
             * 流量统计结果回调监听
             * @param result
             */
            @Override
            public void onDectetComplete(Map<String, Integer> result) {

            }

            /**
             * 目标检测结果回调监听
             * @param recognitions
             */
            @Override
            public synchronized void onProcessDetectResult(List<AIRecognition> recognitions) {

            }

            /**
             * 计数结果回调监听
             * @param count
             */
            @Override
            public void onTrackedCountChanged(int count)
            {
                mTrackedCount = count;
                mHandler.post(runnableUi);
            }

            @Override
            public void onAISizeChanged(AISize aiSize) {

            }
        });

        /**
         * 滑动条,用于设置AI聚合模式下聚合度阈值
         */
        m_seekBarPoly = (SeekBar) findViewById(R.id.progress);
        m_seekBarPoly.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                mAIdetectView.setPolymerizeThreshold(progress*10,progress*10);
                int widthTmp = (progress*10<mAIdetectView.getPolySize().getWidth())?
                        progress*10:mAIdetectView.getPolySize().getWidth();
                int heightTmp = (progress*10<mAIdetectView.getPolySize().getHeight())?
                        progress*10:mAIdetectView.getPolySize().getHeight();
                mAIdetectView.setPolymerizeThreshold(widthTmp,heightTmp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //当前进度
        mAIdetectView.setPolymerizeThreshold(400,400);
        m_seekBarPoly.setProgress(40);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 选择分类多选框初始化
     * @param view
     */
    public void showMutilAlertDialog(View view){
        strToSet = mAIdetectView.getDetectArrayToUse();
        strToSetTmp.clear();
        for (String str: strToSet)
        {
            strToSetTmp.add(str);
        }

        final int length = strToUse.size();
        final String[] items = new String[length+1];
        final boolean[] itemsChecked = new boolean[length+1];
        if (strToSetTmp.size() == strToUse.size())
        {
            itemsChecked[0] = true;
        }
        else
        {
            itemsChecked[0] = false;
        }
        items[0] = "全选";
        for (int i = 0;i < length;i++)
        {
            items[i+1] = strToUse.elementAt(i);
            if (strToSet.contains(items[i+1]))
            {
                itemsChecked[i+1] = true;
            }
            else
            {
                itemsChecked[i+1] = false;
            }
        }
        final android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("设置分类");

        /**
         *第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */
        alertBuilder.setMultiChoiceItems(items, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                if(i==0)
                {
                    for(int j=0;j<length+1;j++)
                    {
                        mListview.setItemChecked(j,isChecked);
                        itemsChecked[j]=isChecked;
                    }
                    if (isChecked)
                    {
                        strToSetTmp.clear();
                        for (String str: strToUse)
                        {
                            strToSetTmp.add(str);
                        }
                    }
                    else
                    {
                        strToSetTmp.clear();
                    }
                }
                else {
                    if (isChecked) {
                        if (!strToSetTmp.contains(items[i])) {
                            strToSetTmp.add(items[i]);
                        }
                        if (strToSetTmp.size() == strToUse.size())
                        {
                            mListview.setItemChecked(0,isChecked);
                            itemsChecked[0]=isChecked;
                        }
                        Toast.makeText(MainActivity.this, "选择" + items[i], Toast.LENGTH_SHORT).show();
                    } else {
                        mListview.setItemChecked(0,isChecked);
                        itemsChecked[0]=isChecked;
                        if (strToSetTmp.contains(items[i])) {
                            strToSetTmp.remove(items[i]);
                        }
                        Toast.makeText(MainActivity.this, "取消选择" + items[i], Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAIdetectView.setDetectArrayToUse(strToSetTmp);
                dialogInterface.dismiss();
                Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                strToSetTmp.clear();
                dialogInterface.dismiss();
            }
        });

        alertDialog3 = alertBuilder.create();
        mListview = alertDialog3.getListView();
        alertDialog3.show();
    }

    /**
     * 开始识别
     * @param view
     */
    public void startDetect(View view)
    {
        mAIdetectView.resumeDetect();
    }

    /**
     * 停止识别
     * @param view
     */
    public void stopDetect(View view)
    {
        mAIdetectView.pauseDetect();
    }


    /**
     * 聚合模式
     * @param view
     */
    public void changePolyStatus(View view)
    {
        if (mAIdetectView.isPolymerize())
        {
            mAIdetectView.setPolymerize(false);
            m_seekBarPoly.setVisibility(View.INVISIBLE);
        }
        else
        {
            mAIdetectView.setPolymerize(true);
            m_seekBarPoly.setVisibility(View.VISIBLE);
        }
    }

    /**
     * AI截图
     * @param view
     */
    public void screenCapture(View view)
    {
        mPreviewImage.setImageBitmap(mAIdetectView.getScreenCapture());
    }

    /**
     * 开始跟踪计数
     * @param view
     */
    public void startTrackCount(View view)
    {
        mAIdetectView.startCountTrackedObjs();
        mBtnCountInfo.setVisibility(View.VISIBLE);
    }

    /**
     * 停止跟踪计数
     * @param view
     */
    public void stopTrackCount(View view)
    {
        mAIdetectView.stopCountTrackedObjs();
        mBtnCountInfo.setVisibility(View.INVISIBLE);
    }
}


