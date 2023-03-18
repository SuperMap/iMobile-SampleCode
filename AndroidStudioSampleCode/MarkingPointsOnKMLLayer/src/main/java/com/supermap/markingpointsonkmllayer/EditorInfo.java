package com.supermap.markingpointsonkmllayer;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.supermap.supermapiearth.basepopupwindow.BasePopupWindow;

/**
 * 
 * @Titile:EditorInfo.java
 * @Descript:标注编辑界面
 * @Company: beijingchaotu
 * @Created on: 2017年10月10日 下午8:29:34
 * @Author: lzw
 * @version: V1.0
 */
public class EditorInfo extends BasePopupWindow {

	public Activity currentContext;

	public EditText et_name,et_info;
	
	ToggleButton toggleButton;

	ImageView iv_back;
    
	RelativeLayout rl_editor;

	public TextView tv_cancel;

	static EditorInfo editorInfo;

	public EditorInfo(Activity context,int w,int h) {
		super(context,w,h);
		this.currentContext = context;
		et_name = (EditText)findViewById(R.id.et_name);
		et_info = (EditText)findViewById(R.id.et_info);
		iv_back = (ImageView)findViewById(R.id.iv_back);
		rl_editor=(RelativeLayout)findViewById(R.id.rl_editor);
		toggleButton = (ToggleButton)findViewById(R.id.mTogBtn);
		rl_editor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				currentContext.runOnUiThread(new  Runnable() {
					public void run() {
						InputMethodManager inputMethodManager=(InputMethodManager)currentContext.getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(rl_editor.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
					}
				});
			}
		});
	}

	public Context getText() {
		return currentContext;
	}

	@Override
	public View onCreatePopupView() {
		return LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_editorinfo, null);
	}
}
