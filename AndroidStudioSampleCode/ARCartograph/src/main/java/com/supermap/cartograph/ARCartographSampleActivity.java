package com.supermap.cartograph;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.supermap.ar.arcartograph.ARRulerCallBack;
import com.supermap.ar.arcartograph.ARCartographView;
import com.supermap.ar.arcartograph.ArRulerCallBack;


public class ARCartographSampleActivity extends AppCompatActivity implements View.OnClickListener, ArRulerCallBack, ARCartographView.SceneDepthListener {

    private ARCartographView mARCartographView;
    private ImageView mAddImage;
    private ImageView mDeleteImage;
    private Group mPromptGroup;
    private ImageView mRulerPrompt;
    private TextView mPromptText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_ruler);


        initPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (mARCartographView != null) {
            mARCartographView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mARCartographView != null) {
            mARCartographView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mARCartographView.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            initPermission();
        }
    }


    private void init() {
        mARCartographView = findViewById(R.id.gl_ruler_show);

        mAddImage = findViewById(R.id.iv_ruler_add);
        mDeleteImage = findViewById(R.id.iv_ruler_delete);

        mPromptText = findViewById(R.id.tv_ruler_prompt);

        mRulerPrompt = findViewById(R.id.iv_ruler_prompt);


        mAddImage.setOnClickListener(this);

        mDeleteImage.setOnClickListener(this);

        mARCartographView.setArRulerCallBack(this);
        mARCartographView.setSceneDepthListener(this);


        ((findViewById(R.id.btnSetFeatureVisibility))).setOnClickListener(this);
        ((findViewById(R.id.btnMeasureArea))).setOnClickListener(this);
        ((findViewById(R.id.btnMeasureLength))).setOnClickListener(this);

    }

    private void initPermission() {
        if (!PermissionHelper.hasCameraPermission(this)) {
            PermissionHelper.requestCameraPermission(this);
        } else {
            init();
        }
    }


    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void showSnackBar(String msg) {
        SnackBarHelper.getInstance().showMessage(this, msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_ruler_add:
                if (mARCartographView.isHitTest()) {
                    //添加一条记录
                    mARCartographView.addRuler();
                }
                break;

            case R.id.btnMeasureLength:
                //设置当前测量模式为长度测量
                mARCartographView.setMeasreMode(ARCartographView.MeasureMode.MEASURE_LENGTH);
                break;


            case R.id.btnMeasureArea:
                //设置当前测量模式为面积测量
                mARCartographView.setMeasreMode(ARCartographView.MeasureMode.MEASURE_AREA);
                break;


            case R.id.iv_ruler_delete:
                //删除一条测量
                mARCartographView.deleteRuler();
                break;


            case R.id.btnSetFeatureVisibility:
                //设置特诊点是否可见
                mARCartographView.setFeaturePointVisible(mARCartographView.isFeaturePointVisible() == false ? true : false);
                break;


            case R.id.vGesture:
                //
                mARCartographView.finishMeasure(mARCartographView.isFinishMeasure() == false?true:false);
                break;
        }
    }


    @Override
    public void showPrompt(final boolean isShow) {
        showPrompt(isShow, "");
    }

    @Override
    public void showPrompt(final boolean isShow, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mPromptGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
                mRulerPrompt.setVisibility(isShow ? View.VISIBLE : View.GONE);
                mPromptText.setVisibility(isShow ? View.VISIBLE : View.GONE);
//                mPromptText.setText(msg);
            }
        });
    }

    @Override
    public void SceneDepthCallBack(float sceneDepth) {
        final float depth = sceneDepth;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.txtShowDepth)).setText("Scene Depth: " + String.format("%.5f", depth));
            }
        });
    }
}
