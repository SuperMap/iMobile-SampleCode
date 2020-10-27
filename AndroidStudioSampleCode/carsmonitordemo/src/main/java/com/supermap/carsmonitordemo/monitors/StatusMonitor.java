package com.supermap.carsmonitordemo.monitors;

import android.graphics.Color;


import com.supermap.carsmonitordemo.communication.CarData;

/**
 * @author Congle
 *
 */
public class StatusMonitor {
	private DisplayManager mDisplayManager = null;
	
	/**
	 * ���캯��
	 * @param mgr
	 */
	public StatusMonitor(DisplayManager mgr){
		mDisplayManager = mgr;
	}
	
	/**
	 * ��س���״̬
	 * @param cardata
	 */
	public void monitor(CarData cardata){
		int status = cardata.getState();
		switch (status) {
		case 1:
			mDisplayManager.updateElementStyle(cardata, Color.GREEN);
			break;
		case 2:
			mDisplayManager.updateElementStyle(cardata, Color.RED);
			mDisplayManager.flashing(cardata,1);
			break;
		case 3:
			mDisplayManager.updateElementStyle(cardata, Color.YELLOW);
			break;
		}
	}
}
