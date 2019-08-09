package com.supermap.imobile.mdatacollector;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyApplication extends Application {
		
		private static MyApplication sInstance = null;
        public static String RootPath ;
		private ArrayList<Activity> mActivities = new ArrayList<Activity>();
		
		@Override
		public void onCreate() {
			super.onCreate();
			RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
			sInstance = this;
		
		}
		
		/**
		 * 获取当前应用对象
		 * @return   返回MyApplication
		 */
		public static MyApplication getInstance() {
			return sInstance;
		}
		
		/**
		 * Toast显示信息
		 * @param strMsg   需要显示的字符串
		 */
		public  void showInfo(final String strMsg){
			Context context = getApplicationContext();
			View toastView = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
			((TextView) toastView.findViewById(R.id.toast_Content)).setText(strMsg);
			Toast toast = new Toast(context);
			toast.setGravity(Gravity.CENTER, 0, 0);
			
			toast.setView(toastView);
			toast.show();
		}
		
		/**
		 * 注册Activity
		 * @param act  Activity对象
		 */
		public void registerActivity(Activity act){
			mActivities.add(act);
		}
		
		/**
		 * 退出应用
		 */
		public void exit(){
		
			for(Activity act:mActivities){
				act.finish();
			}
			Process.killProcess(Process.myPid());
		}
		
		
}

