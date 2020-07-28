package com.supermap.imobile.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.imobile.myapplication.R;
import com.supermap.imobile.utils.ColorPickerView;

import java.util.regex.Pattern;

/**
 * 颜色选择设置对话框,可根据颜色盘选择颜色，或可以自由根据rgb值设置指定颜色
 */
public class ColorPickDialog extends Dialog implements View.OnClickListener {

    Context mContext;
    View contenview;//视图区
    ColorPickerView mColorPickerView;//颜色刻度盘
    TextView tv_rgb;
    EditText edit_rgb;
    TextView tv_colorStr;

    Button btn_red;
    Button btn_white;
    Button btn_gray;
    Button btn_cyan;
    Button btn_blue;
    Button btn_black;
    Button btn_cancle;//取消
    Button btn_confirm;//确定

    OnPickColorListener mPickColorListener;
    LinearLayout layot_backcolor;//背景区

    final int[] colorValue=new int[3];//颜色存储


    /**
     * @param context
     */
    public ColorPickDialog(Context context) {
        super(context);
        mContext=context;
        contenview=LayoutInflater.from(mContext).inflate(R.layout.colorpick,null);
        this.setContentView(contenview);


        initView();//初始界面

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clolor_Red:
                edit_rgb.setText("214,33,17");
                tv_colorStr.setText("#D62111");

                layot_backcolor.setBackgroundColor(Color.argb(255, 214, 33, 17));
                break;
            case R.id.btn_clolor_White:
                edit_rgb.setText("255,255,255");
                tv_colorStr.setText("#FFFFFF");

                layot_backcolor.setBackgroundColor(Color.argb(255, 255, 255, 255));
                break;
            case R.id.btn_clolor_Gray:
                edit_rgb.setText("128,128,128");
                tv_colorStr.setText("#808080");

                layot_backcolor.setBackgroundColor(Color.argb(255, 128, 128, 128));
                break;
            case R.id.btn_clolor_Cyan:
                edit_rgb.setText("0,255,255");
                tv_colorStr.setText("#00FFFF");

                layot_backcolor.setBackgroundColor(Color.argb(255, 0, 255, 255));
                break;
            case R.id.btn_clolor_Blue:
                edit_rgb.setText("0,0,255");
                tv_colorStr.setText("#0000FF");

                layot_backcolor.setBackgroundColor(Color.argb(255, 0, 0, 255));
                break;
            case R.id.btn_clolor_Black:
                edit_rgb.setText("0,0,0");
                tv_colorStr.setText("#000000");

                layot_backcolor.setBackgroundColor(Color.argb(255, 0, 0, 0));
                break;
            case R.id.btn_cancle:
                this.dismiss();
                break;
            case R.id.btn_confirm:
                String[] color = edit_rgb.getText().toString().split("\\,");
                if (color.length != 3) {
                    Toast.makeText(mContext, "请输入正确的数字，如：255,255,255", Toast.LENGTH_SHORT).show();
                } else {
                    if (isInteger(color[0]) && isInteger(color[1]) && isInteger(color[2])) {
                        colorValue[0] = Integer.parseInt(color[0]);
                        colorValue[1] = Integer.parseInt(color[1]);
                        colorValue[2] = Integer.parseInt(color[2]);
                        this.dismiss();
                        mPickColorListener.getColor(colorValue[0],colorValue[1],colorValue[2]);
                    } else {
                        Toast.makeText(mContext, "请输入正确的数字，如：255,255,255", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }

    }

    /**
     * 判断其是否为数字
     * @param str
     * @return
     */
    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 初始化界面
     */
    private void initView() {

        mColorPickerView=(ColorPickerView)findViewById(R.id.colorDisk);
        tv_rgb=(TextView) findViewById(R.id.tv_rgb);
        edit_rgb=(EditText) findViewById(R.id.edit_rgb);
        tv_colorStr=(TextView)findViewById(R.id.tv_colorStr);
        btn_red=(Button) findViewById(R.id.btn_clolor_Red);
        btn_white=(Button)findViewById(R.id.btn_clolor_White);
        btn_gray=(Button)findViewById(R.id.btn_clolor_Gray);
        btn_cyan=(Button)findViewById(R.id.btn_clolor_Cyan);
        btn_blue=(Button)findViewById(R.id.btn_clolor_Blue);
        btn_black=(Button)findViewById(R.id.btn_clolor_Black);
        btn_cancle=(Button)findViewById(R.id.btn_cancle);
        btn_confirm=(Button)findViewById(R.id.btn_confirm);
        layot_backcolor=(LinearLayout)findViewById(R.id.backcolor);

        btn_red.setOnClickListener(this);
        btn_white.setOnClickListener(this);
        btn_gray.setOnClickListener(this);
        btn_cyan.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_black.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        mColorPickerView.setOnColorBackListener(new ColorPickerView.OnColorBackListener() {
            @Override
            public void onColorBack(int a, int r, int g, int b) {

                edit_rgb.setText(r + "," + g + "," + b);
                tv_colorStr.setText(mColorPickerView.getColorStr());

                layot_backcolor.setBackgroundColor(Color.argb(255, r, g, b));


            }
        });
    }

    /**
     * @return
     */
    public int[] getColorValue(){
        return colorValue;
    }

    /**
     * 设置选择监听，当点击确定时，回调信息为颜色值
     * @param listener
     */
    public void setPickColorListener(OnPickColorListener listener){
        mPickColorListener=listener;
    }


    /**
     * 颜色选择监听
     */
    public interface OnPickColorListener{
        void getColor(int r,int g,int b);
    }

}
