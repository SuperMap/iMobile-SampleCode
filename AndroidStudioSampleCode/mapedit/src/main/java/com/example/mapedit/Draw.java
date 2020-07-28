package com.example.mapedit;

import com.supermap.data.Color;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Size2D;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.TrackingLayer;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Draw {
	private MapControl mMapControl;
	private Map mMap;
	private Layers layers;
	public Draw(MapControl mapControl){
		this.mMapControl=mapControl;
		mMap = mMapControl.getMap();
		layers=mMapControl.getMap().getLayers();
		Log.e("++++++++", String.valueOf(layers.getCount()));
	}

	@SuppressLint("ClickableViewAccessibility")
	public void supLine() {
		mMapControl.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						if (isCanDoTouchMoveOp) {
							isCanDoTouchMoveOp = false;
							break;
						}
						if (mCurrentDownPoint != null
								&& Math.abs(mCurrentDownPoint.getX() - x) < 20
								&& Math.abs(mCurrentDownPoint.getY() - y) < 20) {
							break;
						}
						if (System.currentTimeMillis() - mLastTime < 100) {
							break;
						}
					case MotionEvent.ACTION_DOWN:
						isCanDoTouchMoveOp = true;
						mCurrentDownPoint = new Point(x, y);
						mLastTime = System.currentTimeMillis();
						Log.d("zhn", "ACTION_DOWN：" + mCurrentDownPoint.getX() + "-" + mCurrentDownPoint.getY());
						Point2D point6D = pixelToMap(mCurrentDownPoint);
						drawGeom(point6D);
						break;
					case MotionEvent.ACTION_UP:
						Log.d("zhn", "ACTION_UP");
						break;
					case MotionEvent.ACTION_CANCEL:
						mCurrentDownPoint = null;
						Log.d("zhn", "ACTION_CANCEL");
						break;
				}
				return true;
			}
		});
	}

	private long mLastTime;
	private boolean isCanDoTouchMoveOp;
	private Point mCurrentDownPoint;

	public void clearCurrentPoint(){
		mCurrentDownPoint = null;
		rPoints.clear();
		mPoints.clear();
	}

	private Point2D pixelToMap(Point point) {
		return mMap.pixelToMap(point);
//        return new Point6D(point2D.getX(), point2D.getY(), 0);
	}


	private List<Point2D> mPoints = new ArrayList<>();
	private List<Point2D> rPoints = new ArrayList<>();

	/*绘制*/
	private void drawGeom(final Point2D point2D) {
		/*
		 * 关于绘制图形，这里的使用轨迹层绘制，对于线数据绘制，使用线段来处理，不要一条绘制，要一段一段的绘制
		 * 、
		 * */
		TrackingLayer trackingLayer = mMap.getTrackingLayer();
		Point2D lastPoint = null;
		if (mPoints.size() > 0) {
			lastPoint = mPoints.get(mPoints.size() - 1);
		}
		mPoints.add(point2D);
		if (lastPoint != null) {
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(lastPoint);
			point2Ds.add(point2D);
			GeoLine geoLine = new GeoLine(point2Ds);
			GeoStyle lineStyle = geoLine.getStyle();
			if (lineStyle == null) lineStyle = new GeoStyle();
			lineStyle.setLineWidth(0.5);
			lineStyle.setLineColor(new Color(231, 8, 73));
			geoLine.setStyle(lineStyle);
			int add = trackingLayer.add(geoLine, "line" + mPoints.size());
			Log.d("zhn","");
		}
		GeoPoint geoPoint = new GeoPoint(point2D);
		GeoStyle pointStyle = geoPoint.getStyle();
		if (pointStyle == null)
			pointStyle = new GeoStyle();
		pointStyle.setMarkerSize(new Size2D(6, 6));
		pointStyle.setMarkerSymbolID(0);
		pointStyle.setLineColor(new Color(231, 8, 73));
		geoPoint.setStyle(pointStyle);
		int add1 = trackingLayer.add(geoPoint, "point" + mPoints.size());
		drawGuideLine();
	}

	public void undo() {
		if (mPoints != null && mPoints.size() > 0) {
			/*怎样回退采集要素信息，*/
			/*怎样删除最后一个点*/

			TrackingLayer trackingLayer = mMap.getTrackingLayer();
			int lintIndex = trackingLayer.GetEvent("line" + mPoints.size());
			int pointIndex = trackingLayer.GetEvent("point" + mPoints.size());
			Point2D point2D = mPoints.get(mPoints.size() - 1);
			boolean remove = mPoints.remove(point2D);
			boolean add = rPoints.add(point2D);
			if (pointIndex != -1)
				trackingLayer.remove(pointIndex);
			if (lintIndex != -1)
				trackingLayer.remove(lintIndex);
			drawGuideLine();
			Log.d("zhn", "");
		}

	}

	public void redo() {
		/*怎么恢复点和线，*/
		if (rPoints != null && rPoints.size() > 0) {
			Point2D point2D = rPoints.get(rPoints.size() - 1);
			boolean remove = rPoints.remove(point2D);
			if (remove) {
				/*重新绘制图形*/
				drawGeom(point2D);
			}
		}

	}

	private static final String GuideLineTag = "gather_guide_line";

	/***绘制引导线*/
	public void drawGuideLine() {
//        if (mGeometryType == GeometryType.GeoPolygon) {
		if (mPoints.size() > 2) {
			Point2D first = mPoints.get(0);
			Point2D last = mPoints.get(mPoints.size() - 1);
			TrackingLayer trackingLayer = mMap.getTrackingLayer();
			GeoLine geoLine = new GeoLine();
			GeoStyle style = new GeoStyle();
			style.setLineSymbolID(1);
			style.setLineWidth(0.5);
			geoLine.setStyle(style);
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(new Point2D(first.getX(), first.getY()));
			point2Ds.add(new Point2D(last.getX(), last.getY()));
			geoLine.addPart(point2Ds);
			int gather_guide_line = trackingLayer.indexOf(GuideLineTag);
			if (gather_guide_line != -1) {
				trackingLayer.set(gather_guide_line, geoLine);
			} else {
				trackingLayer.add(geoLine, GuideLineTag);
			}
		} else {
			clearGuideLine();
		}
//        }
	}

	/***清除辅助线*/
	private void clearGuideLine() {
		TrackingLayer trackingLayer = mMap.getTrackingLayer();
		int gather_guide_line = trackingLayer.indexOf(GuideLineTag);
		if (gather_guide_line != -1) {
			trackingLayer.remove(gather_guide_line);
		}
	}
	

	public void addLine() {
		mMapControl.setAction(Action.CREATEPOLYLINE);
		layers.get("Line@edit").setEditable(true);
	}
	public void addRegion() {
		mMapControl.setAction(Action.CREATEPOLYGON);
		layers.get("Region@edit").setEditable(true);
		mMapControl.setOnTouchListener(null);
		clearGuideLine();
		mMap.getTrackingLayer().clear();
	}
	public void drawLine() {
		mMapControl.setAction(Action.DRAWLINE);
		layers.get("Line@edit").setEditable(true);
		mMapControl.setOnTouchListener(null);
		clearGuideLine();
		mMap.getTrackingLayer().clear();
	}
	public void drawRegion() {
		mMapControl.setAction(Action.DRAWPLOYGON);
		layers.get("Region@edit").setEditable(true);
		mMapControl.setOnTouchListener(null);
		clearGuideLine();
		mMap.getTrackingLayer().clear();
	}
	

}
