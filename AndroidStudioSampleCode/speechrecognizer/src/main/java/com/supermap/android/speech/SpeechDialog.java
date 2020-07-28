package com.supermap.android.speech;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.mapping.speech.IntelligentSpeechListener;
import com.supermap.mapping.speech.SpeechManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 语音识别对话框
 */
public class SpeechDialog extends Dialog {

    private static final String TAG = "SpeechDialog";

    private SpeechManager mSpeechManager = null;

    private DiffuseView mDiffuseView;

    // 采集信息
    private TextView mTV_Done = null;

    private Context mContext = null;

    public SpeechDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_speech_search);

        init();
    }

    private void init() {
        mTV_Done = (TextView) findViewById(R.id.tv_sci_setting_path_done);

        mDiffuseView = (DiffuseView) findViewById(R.id.diffuseView);
        mDiffuseView.setCoreColor(Color.parseColor("#0091FF"));//设置中心圆颜色
        mDiffuseView.setColor(Color.parseColor("#50B3FC"));//设置扩散圆颜色
        mDiffuseView.setOnClickListener(mDiffuseViewListener);

        start();

        // 当需要进行语音识别的时候再获取单例对象
        mSpeechManager = SpeechManager.getInstance(mContext);

        startSpeech();
    }

    private void startSpeech(){
        //设置参数
        setParameters();

        //开始识别
        mSpeechManager.startListening(mListener);
    }

    private void setParameters() {
        mSpeechManager.setVAD_BOS_Time(3000);//前段点超时
        mSpeechManager.setVAD_EOS_Time(2000);//后端点超时

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperMap/Speech.wav";
        Log.e(TAG, "Path: " + path);

        mSpeechManager.setAudioPath(path);

//        mSpeechManager.setAudioPath(Environment.getExternalStorageDirectory() + "/msc/Iat.wav");
    }

    View.OnClickListener mDiffuseViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDiffuseView.isDiffuse()) {
                stop();

                mSpeechManager.cancel();
            } else {
                start();

                startSpeech();

//                mSpeechManager.startListening(mListener);
            }
        }
    };

    private IntelligentSpeechListener mListener = new IntelligentSpeechListener() {

        @Override
        public void onBeginOfSpeech() {
            Log.e(TAG, "onBeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.e(TAG, "onEndOfSpeech");
        }

        @Override
        public void onVolumeChanged(int volume) {
//            Log.e(TAG, "onVolumeChanged: " + volume);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, "onError: " + error);
            stop();
            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(String info, boolean isLast) {
            Log.e(TAG, "onResult: " + info + ", isLast: " + isLast);

            String str = mTV_Done.getText().toString();
            mTV_Done.setText(str + info);

            if (isLast) {
                stop();
            }
        }
    };

    //开始动画
    public void start() {
        if (!mDiffuseView.isDiffuse()) {
            mDiffuseView.start();
        }
        mTV_Done.setText(null);
        mTV_Done.setHint("请开始说话...");
    }

    //结束动画
    public void stop() {
        if (mDiffuseView.isDiffuse()) {
            mDiffuseView.stop();
        }
        mTV_Done.setHint("请点击按钮开始说话...");
    }

    @Override
    public void dismiss() {
        super.dismiss();

        stop();
        if (mSpeechManager.isListening()) {
            mSpeechManager.cancel();
        }
    }

}
