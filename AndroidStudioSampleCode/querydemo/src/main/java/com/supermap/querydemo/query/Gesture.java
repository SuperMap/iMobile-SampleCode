package com.supermap.querydemo.query;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.supermap.data.GeoCircle;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometry;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.mapping.dyn.ZoomAnimator;
import com.supermap.querydemo.R;

// 手势处理类
public class Gesture{
	public interface DrawnListener{
		public void drawnGeometry(Geometry geoRegion);
	}

	public interface SearchAroundListener{
		public void searchGeometry(Geometry geoRegion);
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
	 * 设置查询监听
	 * @param l
	 */
	public void setSearchAroundListener(SearchAroundListener l){
		mSearchAroundListener = l;
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
	 * @param dynView
	 */
	public Gesture(MapView mapView,DynamicView dynView) {
		mMapView = mapView;
		this.mMapControl = mMapView.getMapControl();
		mDynView = dynView;

		mMapControl.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_MOVE){
//					mMapView.removeAllCallOut();
				}
				if(mDrawEnable && mDrawnListener!=null){
					if(event.getAction()==MotionEvent.ACTION_UP){
						Geometry geometry = mMapControl.getCurrentGeometry();
						if(geometry != null){
							GeoLine line = (GeoLine) geometry;
							GeoRegion region = new GeoRegion();
							for(int i=0;i<line.getPartCount();i++){
								if (line.getPart(i).getCount()>2) {
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
				return mMapControl.onMultiTouch(event);
			}
		});

		setDrawStyle();

		//长按进行周边查询
		GestureDetector gestureDetector = new GestureDetector(mMapControl.getContext(), new GestureDetector.SimpleOnGestureListener() {
			@Override
			public void onLongPress(MotionEvent e) {
				super.onLongPress(e);
				//一旦长按就把状态设置为PAN
				mMapControl.setAction(Action.PAN);
				mDynView.clear();
				mMapView.removeAllCallOut();
				DynamicPoint dynBounds = new DynamicPoint();
				final Point2D center = mMapControl.getMap().pixelToMap(new Point((int)e.getX(), (int)e.getY()));
				dynBounds.addPoint(center);
				DynamicStyle style = new DynamicStyle();
				style.setBackground(BitmapFactory.decodeResource(mMapControl.getResources(), R.drawable.ic_bounds_query));
				dynBounds.setStyle(style);
				dynBounds.addAnimator(new ZoomAnimator(2.0f, 750));
				dynBounds.addAnimator(new ZoomAnimator(0.5f, 750));
				mDynView.addElement(dynBounds);
				mDynView.startAnimation();
				mDynView.refresh();
				mDynView.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(mSearchAroundListener!=null){
							//计算一个像素长度为150的到地理长度
							Point pt = new Point(0, 0);
							Point pt1 = new Point(0, 100);
							Point2D pt2d = mMapControl.getMap().pixelToMap(pt);
							Point2D pt2d1 = mMapControl.getMap().pixelToMap(pt1);
							double x = pt2d.getX()-pt2d1.getX();
							double y = pt2d.getY()-pt2d1.getY();
							double r = Math.sqrt(x*x+y*y);
							GeoCircle circle = new GeoCircle(center, r);
							mDynView.clear();
							mDynView.refresh();
							mSearchAroundListener.searchGeometry(circle);
							circle.dispose();
						}
					}
				}, 1500);
			}
		});

		mMapControl.setGestureDetector(gestureDetector);
	}

	/**
	 * 设置绘制对象的风格
	 */
	private void setDrawStyle(){
		mMapControl.setStrokeColor(Color.argb(255, 255, 75, 45));
	}
}
