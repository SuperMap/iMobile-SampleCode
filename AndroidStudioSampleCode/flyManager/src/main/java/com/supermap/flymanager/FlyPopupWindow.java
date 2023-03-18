package com.supermap.flymanager;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flymanager.R;
import com.supermap.realspace.Action3D;
import com.supermap.realspace.FlyManager;
import com.supermap.realspace.FlyStatus;
import com.supermap.realspace.Routes;
import com.supermap.realspace.SceneControl;
import com.supermap.supermapiearth.basepopupwindow.BasePopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @Titile:flyPopupWindow.java
 * @Descript:飞行界面
 * @Company: beijingchaotu
 * @Created on: 2017年3月11日 上午11:33:59
 * @Author: lzw
 * @version: V1.0
 */
public class FlyPopupWindow extends BasePopupWindow {

	Activity mContext;
	public FlyBottomPopupWindow mFlyBottomPopupWindow;
	public ListView listView_fly;
	ImageButton back2;
	static FlyPopupWindow mFlyPopupWindow;
	static ArrayList<String> flyRouteNames;
	static FlyManager flyManager;
	static String flyRoute;
	static Routes routes;
	public static boolean isFlying = false;
	public static boolean isStop = false;
	boolean isPopFlyShowing = false;
	//boolean isTablet = false;
	String localSceneDirPath = getExSdCardPath() + "/SuperMap/data/CBD_android/";
	String sampleSceneDirPath =getExSdCardPath() + "/SuperMap/LocalData/";

	static class FlyRoutesViewHolder {
		public ImageView imageView;
		public TextView textView;

	}

	static Timer flyProgressTimer;
	static TimerTask flyProgressTimerTask;
	static Handler flyProgressHandler;

	public static synchronized FlyPopupWindow getInstance(Activity context, int w, int h) {
		if (mFlyPopupWindow == null) {
			mFlyPopupWindow = new FlyPopupWindow(context, w, h);
		}
		return mFlyPopupWindow;
	}

	private FlyPopupWindow(Activity context, int w, int h) {
		super(context, w, h);
		this.mContext = context;
		//isTablet = Utils.isTablet(context);
		mFlyBottomPopupWindow = FlyBottomPopupWindow.getInstance(mContext,
				 dp2px(mContext, 350),dp2px(mContext, 120));
//		mFlyBottomPopupWindow = FlyBottomPopupWindow.getInstance(mContext,
//				isTablet ? Utils.dp2px(mContext, 450) : Utils.dp2px(mContext, 350),
//						isTablet ? Utils.dp2px(mContext, 180) : Utils.dp2px(mContext, 120));
		listView_fly = (ListView) findViewById(R.id.listView_fly);
		back2 = (ImageButton) findViewById(R.id.back2);
		flyRouteNames = new ArrayList<String>();
		back2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
	}

	/**
	 * @author：Supermap
	 * @注释 ：fly
	 */
	public void fly(final SceneControl sceneControl) {

		flyRouteNames.clear();
		flyManager = sceneControl.getScene().getFlyManager();

		String currentSceneName = sceneControl.getScene().getName();
		flyRoute = getFlyRoutePath(localSceneDirPath, currentSceneName);
		if (flyRoute == null) {
			showToastInfo(mContext, "该场景下无飞行路线");
			return;
		} else {
			routes = flyManager.getRoutes();
			boolean hasRoutes = routes.fromFile(flyRoute);
			if (hasRoutes) {
				int numOfRoutes = routes.getCount();
				for (int i = 0; i < numOfRoutes; i++) {
					flyRouteNames.add(routes.getRouteName(i));
				}
			} else {
				showToastInfo(mContext, "该场景下无飞行路线");
			}
		}

		BaseAdapter adapter_fly = new BaseAdapter() {
			FlyRoutesViewHolder viewHolder;

			@Override
			public View getView(int position, View convertView, ViewGroup arg2) {
				if (convertView == null) {
					viewHolder = new FlyRoutesViewHolder();
					convertView = mContext.getLayoutInflater().inflate(R.layout.popupwindow_fly_item, null);
					viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_fly);
					viewHolder.textView = (TextView) convertView.findViewById(R.id.textView_fly);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (FlyRoutesViewHolder) convertView.getTag();
				}

				viewHolder.imageView.setBackgroundResource(R.drawable.icon_fly);
				viewHolder.textView.setText(flyRouteNames.get(position));
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return flyRouteNames.size();
			}
		};

		listView_fly.setAdapter(adapter_fly);
	}

	// 实时更新飞行模式下飞行进度条
	public void refreashFlyProgress() {

		flyProgressTimer = new Timer();

		flyProgressHandler = new Handler() {
			double duration = flyManager.getDuration();
			int a = flyManager.getRoutes().getCurrentRouteIndex();
			String name = flyManager.getRoutes().getRouteName(a);

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:

					if (flyManager.getStatus() == FlyStatus.PLAY) {
						double progress = flyManager.getProgress();
						double percent = progress / duration * 100;
						int tempPercent = (int) Math.ceil(percent);
						String textInfo =tempPercent + "%";
						mFlyBottomPopupWindow.tv_location.setTextSize(25);
						mFlyBottomPopupWindow.tv_location.setText(textInfo);

					} else if (flyManager.getStatus() == FlyStatus.STOP) {

						flyProgressTimerTask.cancel();

					}
					break;

				default:
					break;
				}

			}

		};

		flyProgressTimerTask = new TimerTask() {

			@Override
			public void run() {
				flyProgressHandler.sendEmptyMessage(0);
			}
		};

		flyProgressTimer.schedule(flyProgressTimerTask, 500, 10);

	}

	public void flyPause(SceneControl sceneControl) {
		if (flyManager != null) {

			if (!isFlying) {
				if (isStop) {
					flyManager = sceneControl.getScene().getFlyManager();
					routes = flyManager.getRoutes();
					routes.fromFile(flyRoute);
					isStop = false;
				}
				mFlyBottomPopupWindow.iv_imageView1.setBackgroundResource(R.drawable.play);
				sceneControl.setAction(Action3D.PAN3D);
				flyManager.play();
				refreashFlyProgress();
				isFlying = true;
			} else {

				mFlyBottomPopupWindow.iv_imageView1.setBackgroundResource(R.drawable.pause);
				flyManager.pause();
				sceneControl.setAction(Action3D.PANSELECT3D);
				isFlying = false;
			}
		}

	}

	public void flyStop(SceneControl sceneControl) {
		if (flyManager != null) {
			flyManager.stop();
			sceneControl.setAction(Action3D.PANSELECT3D);
			isFlying = false;
			isStop = true;
		}
	}

	public void listflyItem(SceneControl sceneControl, int position) {
		routes.setCurrentRoute(position);
		sceneControl.setAction(Action3D.PAN3D);
		flyManager.play();
		refreashFlyProgress();
		isFlying = true;
		dismiss();
		isPopFlyShowing = true;
	}

	@Override
	public View onCreatePopupView() {
		return LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_fly, null);
	}
	
	public  int dp2px(Activity context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	public  String getExSdCardPath() {
			String exCardPath = "";
			// 判断sd卡是否存在,若存在，则读取 vold.fstab文件 找到外置各个卡挂载点
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

				return android.os.Environment.getExternalStorageDirectory() + "";
			}

			return null;

		}
	
	// 根据场景名称获取同名的飞行路线，需要确认飞行文件的存放位置
	public  String getFlyRoutePath(String localDataPath, String sceneName) {
		String flyRoutePath = "";

		if (new File(localDataPath).exists()) {
			flyRoutePath = localDataPath + sceneName + ".fpf";
			if (new File(flyRoutePath).exists()) {
				return flyRoutePath;
			}
		}
		return null;
	}
	
	public  void showToastInfo(Activity context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}
	
	public void reset(SceneControl sceneControl){
		flyManager = sceneControl.getScene().getFlyManager();
		String currentSceneName = sceneControl.getScene().getName();
		flyRoute = getFlyRoutePath(localSceneDirPath, currentSceneName);
		routes = flyManager.getRoutes();
		boolean hasRoutes = routes.fromFile(flyRoute);
	}

}
