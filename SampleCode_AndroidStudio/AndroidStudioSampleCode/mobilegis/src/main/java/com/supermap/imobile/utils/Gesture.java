package com.supermap.imobile.utils;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.supermap.data.GeoLine;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicView;

// 手势处理类
public class Gesture{
	public interface DrawnListener{
		public void drawnGeometry(Geometry geoRegion);
	}

	public interface SearchAroundListener{
		 void searchGeometry(Geometry geoRegion);
	}

	private MapControl mMapControl = null;
	private MapView mMapView = null;
	private DrawnListener mDrawnListener = null;
	private boolean mDrawEnable = false;
	private DynamicView mDynView = null;
	private SearchAroundListener mSearchAroundListener = null;

	/**
	 * 开启地图绘制线功能
	 */
	public void draw(){
		mMapControl.setAction(Action.DRAWLINE);
		mDrawEnable = true;
	}


	/**
	 * 设置绘制监听
	 * @param drawn
	 */
	public void setDrawnListener(DrawnListener drawn){
		mDrawnListener = drawn;
	}

	/**
	 * 构造函数
	 * @param mapView

	 */
	public Gesture(MapView mapView) {
		mMapView = mapView;
		this.mMapControl = mMapView.getMapControl();
//		mDynView = dynView;
//
		mMapControl.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_MOVE){
//					mMapView.removeAllCallOut();
				}
				if(mDrawEnable && mDrawnListener!=null){
					if(event.getAction()==MotionEvent.ACTION_UP){
						Geometry geometry = mMapControl.getCurrentGeometry();
						if(geometry != null) {
							if (geometry.getType()== GeometryType.GEOLINE){
							GeoLine line = (GeoLine) geometry;
							GeoRegion region = new GeoRegion();
							for (int i = 0; i < line.getPartCount(); i++) {
								if (line.getPart(i).getCount() > 2) {
									region.addPart(line.getPart(i));
								}
							}
							mDrawnListener.drawnGeometry(region);
							mMapControl.deleteCurrentGeometry();
							mMapControl.setAction(Action.PAN);

							//释放
							geometry.dispose();
							region.dispose();
						}
						}
					}
				}
				return mMapControl.onMultiTouch(event);
			}
		});

		setDrawStyle();
	}

	/**
	 * 设置绘制对象的风格
	 */
	private void setDrawStyle(){
		mMapControl.setStrokeColor(Color.argb(255, 255, 75, 45));
	}
}
