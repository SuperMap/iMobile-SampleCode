package com.supermap.carsmonitordemo.communication;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supermap.carsmonitordemo.carmonitor.MonitorActivity;
import com.supermap.carsmonitordemo.monitors.DisplayManager;


/**
 * 消息接收器
 * @author Congle
 *
 */
public class MessageReciver extends BroadcastReceiver{

	DisplayManager mDisplayManager;

	/**
	 * 构造函数
	 * @param displayManager
	 */
	public MessageReciver(DisplayManager displayManager){
		mDisplayManager = displayManager;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (MonitorActivity.startMonitor) {
			ArrayList<CarData> datas = intent
					.getParcelableArrayListExtra("cardata");

			for (int i = 0; i < datas.size(); i++) {
				CarData cardata = datas.get(i);

				mDisplayManager.monitor(cardata);                 // 显示车辆

			}
		}
	}
}
