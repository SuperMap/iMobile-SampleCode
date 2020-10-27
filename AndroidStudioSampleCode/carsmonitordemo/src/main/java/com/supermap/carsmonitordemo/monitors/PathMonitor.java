package com.supermap.carsmonitordemo.monitors;

import java.util.HashMap;

import com.supermap.carsmonitordemo.communication.CarData;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;

/**
 * 轨迹监控
 * @author Congle
 *
 */
public class PathMonitor {
	private DisplayManager mDisplayManager = null;
	private HashMap<String, Point2Ds> mPaths = new HashMap<String, Point2Ds>();
	private HashMap<String,Integer> mPathIDs = new HashMap<String,Integer>();

	/**
	 * 构造函数
	 * @param mgr
	 */
	public PathMonitor(DisplayManager mgr){
		mDisplayManager = mgr;
	}

	/**
	 * 监控车辆
	 * @param cardata
	 */
	public void monitor(CarData cardata){
		double x = cardata.getX();
		double y = cardata.getY();
		String carno = cardata.getCarNo();

		if(mPaths.containsKey(carno)){
			Point2D pt = new Point2D(x, y);
			mPaths.get(carno).add(pt);
			//更新显示轨迹
			if(mPathIDs.containsKey(carno)){
				int id = mPathIDs.get(carno);
//				mDisplayManager.updateLine(id, pt);
				showPath(true,carno);
			}
		}else{
			Point2Ds pts = new Point2Ds();
			pts.add(new Point2D(x, y));
			mPaths.put(carno, pts);
		}

		mDisplayManager.Translate(cardata);
	}

	/**
	 * 设置轨迹是否显示
	 * @param isShow	是否显示
	 * @param carNo		车牌号
	 */
	public void showPath(boolean isShow,String carNo){
		if(isShow){
			if (mPaths.containsKey(carNo)) {

				//已经添加过，要先移除
				if (mPathIDs.containsKey(carNo)) {
					int id = mPathIDs.get(carNo);
					mDisplayManager.removeLine(id);

					mPathIDs.remove(carNo);
				}

				Point2Ds pts = mPaths.get(carNo);
				int id = mDisplayManager.addLine(pts);
				mPathIDs.put(carNo, id);
			}
		}else{
			if (mPathIDs.containsKey(carNo)) {
				int id = mPathIDs.get(carNo);
				mDisplayManager.removeLine(id);

				mPathIDs.remove(carNo);
			}
		}
		mDisplayManager.refresh();
	}

	/**
	 * 判断路径是否已经显示
	 * @param carNo
	 * @return
	 */
	public boolean isShowPath(String carNo){
		if(mPathIDs.containsKey(carNo)){
			return true;
		} else {
			return false;
		}
	}

}
