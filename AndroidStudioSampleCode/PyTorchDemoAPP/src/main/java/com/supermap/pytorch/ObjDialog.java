package com.supermap.pytorch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 语音对话框
 */

public class ObjDialog extends Dialog {
    private static final String TAG = "ObjDialog";

    // 采集信息
    private ImageView imageView = null;
    private TextView mTV_type = null;
    private TextView mTV_day = null;
    private TextView mTV_time = null;

    private Context mContext = null;

    Bitmap id = null;
    String type = null;
    String day = null;
    String time = null;

    public ObjDialog(Context context, int theme, Bitmap id, String type, String day, String time) {
        super(context, theme);
        mContext = context;
        this.id = id;
        this.type = type;
        this.day = day;
        this.time = time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.dialog_speech_search);

        init();
    }

    private void init() {
        imageView = findViewById(R.id.img);
        mTV_type = findViewById(R.id.type);
        mTV_day = findViewById(R.id.day);
        mTV_time = findViewById(R.id.time);

        imageView.setImageBitmap(this.id);
        mTV_type.setText(this.type);
        mTV_day.setText(this.day);
        mTV_time.setText(this.time);
    }

}
