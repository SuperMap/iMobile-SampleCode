package com.supermap.flymanager;

import java.util.Timer;
import java.util.TimerTask;

import com.example.flymanager.R;
import com.supermap.realspace.FlyManager;
import com.supermap.supermapiearth.basepopupwindow.BasePopupWindow;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @Titile:FlyBottomPopupWindow.java
 * @Descript:飞行底部弹窗界面
 * @Company: beijingchaotu
 * @Created on: 2017年3月10日 下午8:25:00
 * @Author: lzw
 * @version: V1.0
 */
public class FlyBottomPopupWindow extends BasePopupWindow {

	Activity currentContext;
	public ImageView ivClose;
	public ImageView iv_imageView2;
	public ImageView iv_imageView3;
	public ImageView iv_imageView1;
	public TextView tv_close;
	public static TextView tv_location;
	//private boolean isTable=false;
	static FlyBottomPopupWindow mFlyBottomPopupWindow;
	FlyManager flyManager;
	Timer flyProgressTimer;
	Handler flyProgressHandler;
	TimerTask flyProgressTimerTask;
	public static boolean isFlying=false;
	public static boolean flyShow=false;
	public static synchronized FlyBottomPopupWindow getInstance(Activity context,int w,int h) {
		if (mFlyBottomPopupWindow == null) {
			mFlyBottomPopupWindow = new FlyBottomPopupWindow(context,w,h);
		}
		return mFlyBottomPopupWindow;
	}
	private FlyBottomPopupWindow(Activity activity,int w,int h) {
        super(activity,w,h);
		this.currentContext = activity;
		iv_imageView1 = (ImageView)findViewById(R.id.iv_imageView1);
		iv_imageView2 = (ImageView)findViewById(R.id.iv_imageView2);
		iv_imageView3 = (ImageView)findViewById(R.id.iv_imageView3);
		tv_close = (TextView)findViewById(R.id.tv_close);
		tv_location = (TextView)findViewById(R.id.tv_progre);
		iv_imageView1.setBackgroundResource(R.drawable.play);
		//点击窗口以外的区域窗口不消失
		this.setDismissWhenTouchOuside(false);
		
	}
	@Override
	public View onCreatePopupView() {
			return LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_bottom, null);
	}
	
}
