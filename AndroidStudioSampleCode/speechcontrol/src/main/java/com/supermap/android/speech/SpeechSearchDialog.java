package com.supermap.android.speech;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.supermap.mapping.speech.POIInfo;
import com.supermap.mapping.speech.SpeechControl;
import com.supermap.mapping.speech.SpeechControlListener;
import com.supermap.mapping.speech.SpeechMode;

import java.util.ArrayList;

/**
 * 语音对话框
 */

public class SpeechSearchDialog extends Dialog {
    private static final String TAG = "SpeechSearchDialog";

    private DiffuseView mDiffuseView;

    // 采集信息
    private TextView mTV_Done = null;

    private Context mContext = null;

    private SpeechControl mSpeechControl = null;

    public SpeechSearchDialog(Context context, int theme, SpeechControl speechControl) {
        super(context, theme);
        mContext = context;
        mSpeechControl = speechControl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.dialog_speech_search);

        init();
    }

    private void init() {
        mTV_Done = (TextView) findViewById(R.id.tv_sci_setting_path_done);
        mTV_Done.setMovementMethod(ScrollingMovementMethod.getInstance());

        mDiffuseView = (DiffuseView) findViewById(R.id.diffuseView);
        mDiffuseView.setCoreColor(Color.parseColor("#0091FF"));//设置中心圆颜色
        mDiffuseView.setColor(Color.parseColor("#50B3FC"));//设置扩散圆颜色
        mDiffuseView.setOnClickListener(mDiffuseViewListener);

        start();
        mSpeechControl.startListening(mSpeechControlListener);

    }

    //语音控制监听
    private SpeechControlListener mSpeechControlListener = new SpeechControlListener() {
        @Override
        public void onBeginOfSpeech() {
//			MyApplication.getInstance().showInfo("开始说话");
        }

        @Override
        public void onEndOfSpeech() {
//			MyApplication.getInstance().showInfo("结束说话");
            stop();
        }

        @Override
        public void onVolumeChanged(int volume) {
            // 当前音量值，范围[0-30]
//			showTip("当前正在说话，音量大小：" + volume);
//			Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onError(String error) {
            MyApplication.getInstance().showInfo(error);
            stop();
        }

        @Override
        public void onResult(String info, boolean isLast) {
            String speechInfo = mTV_Done.getText().toString() + info;
            mTV_Done.setText(speechInfo);

            if (isLast) {
                stop();
                dismiss();
            }
        }

        @Override
        public void onPOIShow(ArrayList<POIInfo> poiList) {
            Log.e(TAG , "onPOIShow");
            MainActivity mainActivity = (MainActivity) mContext;
            mainActivity.showSlideLayout();

            mainActivity.setPoiList(poiList);
            mainActivity.mSearchResultsListViewAdapter.setList(poiList);
            mainActivity.mSearchResultsListViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPOIClick(ArrayList<POIInfo> poiList, POIInfo info, int position) {
            Log.e(TAG , "onPOIClick");
            MainActivity mainActivity = (MainActivity) mContext;
            mainActivity.showSlideLayout();

            mainActivity.setPoiList(poiList);
            mainActivity.mSearchResultsListViewAdapter.setList(poiList);
            mainActivity.mSearchResultsListViewAdapter.notifyDataSetChanged();
            mainActivity.moveToItem(position);
        }

        @Override
        public void onSpeechModeState(SpeechMode.SpeechModeType type) {
            Log.e(TAG , "onSpeechModeState：" + type );
            MainActivity mainActivity = (MainActivity) mContext;

            if (type == SpeechMode.SpeechModeType.EDIT) {
                mainActivity.mLL_draw.setVisibility(View.VISIBLE);
            } else if (type == SpeechMode.SpeechModeType.NORMAL) {
                mainActivity.mLL_draw.setVisibility(View.GONE);
            }
        }
    };

    private View.OnClickListener mDiffuseViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDiffuseView.isDiffuse()) {
                stop();

                mSpeechControl.cancel();
            } else {
                start();

                mSpeechControl.startListening(mSpeechControlListener);
            }
        }
    };


    private void start() {
        if (!mDiffuseView.isDiffuse()) {
            mDiffuseView.start();
        }
        mTV_Done.setText(null);
        mTV_Done.setHint("请开始说话...");
        //Log.e("TAG", "SpeechDialog-start");
    }

    private void stop() {
        if (mDiffuseView.isDiffuse()) {
            mDiffuseView.stop();
        }
        mTV_Done.setHint("请点击按钮开始说话...");
        //Log.e("TAG", "SpeechDialog-stop");
    }

    @Override
    public void dismiss() {
        super.dismiss();

        stop();
        if (mSpeechControl.isListening()) {
            mSpeechControl.cancel();
        }
    }

}
