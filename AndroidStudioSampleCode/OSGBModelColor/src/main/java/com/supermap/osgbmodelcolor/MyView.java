package com.supermap.osgbmodelcolor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.example.osgbmodelcolor.R;

public class MyView {

	Context currentContext;
	View mView;
	LinearLayout ll_back;
	ColorPickerView colorPickerView;
	public PopupWindow mDialog;
	Button btn_confirm,btn_cancel;
	// 时长进度条
	public SeekBar mSeekbar = null;
	
	public MyView(Activity context){
		this.currentContext=context;
		 mView = LayoutInflater.from(context).inflate(R.layout.popupwindow_myview, null);
		 colorPickerView=(ColorPickerView)mView.findViewById(R.id.cpv);
		 btn_confirm=(Button)mView.findViewById(R.id.btn_confirm);
		 btn_cancel=(Button)mView.findViewById(R.id.btn_cancel);
		 mSeekbar = (SeekBar)mView.findViewById(R.id.seekbar);
		 ColorDrawable dw = new ColorDrawable(-00000);
		 DisplayMetrics dm = new DisplayMetrics();
		 ((Activity) this.currentContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		 int screenWidth = dm.widthPixels;
		 int screenHeight = dm.heightPixels;
		
		 mDialog = new PopupWindow(mView, screenWidth, screenHeight, true);
		 mDialog.setFocusable(false);
		 mDialog.setTouchable(true);
		 mDialog.setBackgroundDrawable(dw);
		//mDialog.setAnimationStyle(R.style.DialogCenterAnimation);

		 mDialog.update();

	}
	public void dismiss() {
		mDialog.dismiss();
	}

	public void show(View parent) {

		if (!mDialog.isShowing()) {
			mDialog.showAsDropDown(parent);
		} else {
			mDialog.dismiss();
		}

	}
	
}
