package com.supermap.carsmonitordemo.monitors;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.supermap.carsmonitordemo.communication.CarData;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRectangle;
import com.supermap.data.GeoText;
import com.supermap.data.Geometrist;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.TextAlignment;
import com.supermap.data.TextPart;
import com.supermap.data.TextStyle;
import com.supermap.mapping.GeometryEvent;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mapping.dyn.DynamicPoint;


/**
 * 范围监控，负责管理范围和越界判断
 * @author Congle
 *
 */
public class DomainMonitor {
	private DisplayManager mDisplayManager = null;

	private Handler        mUIHandler      = null;
	private TextView       mWarningInfo    = null;
	private TrackingLayer  mTrackingLayer  = null;

	private ArrayList<MonitorDomain> m_MonitorDomainList = null;

	/**
	 * 构造函数
	 * @param mgr
	 */
	public DomainMonitor(DisplayManager mgr){
		mDisplayManager = mgr;
		mTrackingLayer  = mgr.getTrackingLayer();
		mUIHandler      = new Handler();
		m_MonitorDomainList = new ArrayList<MonitorDomain>();

	}

	/**
	 * 增加检测区列表对象
	 * @param event
	 */
	public void monitorDomainList(GeometryEvent event){

		if(event == null){
			return;
		}

		Recordset recordset = null;
		DatasetVector datasetVector = null;

		datasetVector = (DatasetVector) event.getLayer().getDataset();
		if(datasetVector == null){
			return;
		}
		recordset = datasetVector.query(new int[]{event.getID()}, CursorType.DYNAMIC);

		Geometry geometry = recordset.getGeometry();                     // 获取监控区域
		// 将监控区域添加到监控区域列表
		if(geometry != null){
			MonitorDomain monitorDomain = new MonitorDomain();
			monitorDomain.setMonitorGeometry(geometry);
			monitorDomain.setMointorName("监测区 " + m_MonitorDomainList.size());
			GeoText geoText = createGeoText(geometry, monitorDomain.getMonitorName());
			if(geoText != null && mTrackingLayer != null){
				mTrackingLayer.add(geoText, "");
			}
			m_MonitorDomainList.add(monitorDomain);
		}else {

		}
		recordset.dispose();
	}

	/**
	 * 监控车辆
	 * @param cardata
	 */
	public void monitor(CarData cardata){
		if(cardata == null){
			return ;
		}

		DynamicPoint car = mDisplayManager.queryCar(cardata);

		if(car == null){
			return;
		}
		Rectangle2D rectangle2d = car.getBounds();
		if(rectangle2d == null){
			return;
		}
		Rectangle2D rectangle2d2 = new Rectangle2D(rectangle2d.getLeft(), rectangle2d.getBottom(), rectangle2d.getRight()*1.1, rectangle2d.getTop()*1.1);
		GeoRectangle carRectangle = new GeoRectangle(rectangle2d2, 0);
		Point2Ds point2Ds = car.getGeoPoints();

		Point2D point2d = point2Ds.getItem(0);

		GeoPoint geoPoint = new GeoPoint(point2d);

		int id = car.getID();

		if(carRectangle != null && mDisplayManager != null && m_MonitorDomainList.size()>0){

			for (MonitorDomain monitorDomain : m_MonitorDomainList) {
				Geometry monitorDomainGeo = monitorDomain.getMonitorGeometry();
				if(monitorDomainGeo == null ){
					break;
				}
				boolean isContainCar = Geometrist.canContain(monitorDomainGeo, geoPoint);
				boolean isContainCarID = monitorDomain.getMointorIDs().contains(id);

				if(isContainCar){
					//车在监控区域内，而该区域未记录该车的ID，则是进入
					if(!isContainCarID){

						entryWarning(cardata, monitorDomain);
						//加入该监控区域
						monitorDomain.getMointorIDs().add(id);
					}
				} else{
					// 车不在监控区域内，而该区域有该车的ID记录，则是离开
					if(isContainCarID){

						leaveWarning(cardata, monitorDomain);
						monitorDomain.getMointorIDs().remove(id);
					}
				}
			}

		}
	}


	/**
	 * 清除监控区域
	 */
	public void clearMonitorDomain() {
		// 情况监控区
		if(m_MonitorDomainList != null){
			m_MonitorDomainList.clear();
		}

		mDisplayManager.clearEditLayer();
		mDisplayManager.refresh();
	}

	/**
	 * 绑定警告信息显示器
	 */
	public void attacthWarningDisplay(TextView textView){
		mWarningInfo = textView;
	}

	/**
	 * 车辆进入监控区提示
	 * @param cardata
	 * @param domain
	 */
	private void entryWarning(CarData cardata, MonitorDomain domain){
		if(cardata == null || domain == null){
			return;
		}

		DynamicPoint car = mDisplayManager.queryCar(cardata);

		if(car != null && mWarningInfo!= null){
			mDisplayManager.flashing(cardata,5);
			String carNo = cardata.getCarNo();
			mWarningInfo.setTextColor(Color.RED);
			mWarningInfo.setBackgroundColor(Color.argb(180, 155, 155, 155));
			mWarningInfo.setTextSize(35.0f);
			mWarningInfo.setVisibility(View.VISIBLE);
			String time = new Date(System.currentTimeMillis()).toLocaleString();
			mWarningInfo.setText(domain.getMonitorName() + ": \n" + time+"\n"+carNo+"进入 监测区");
			mWarningInfo.invalidate();
			mUIHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mWarningInfo.setVisibility(View.GONE);
				}
			}, 3000);

			//警报音效
			SoundMonitor.getSoundMonitor().warn();
			SoundMonitor.getSoundMonitor().shake();
		}
	}

	/**
	 * 车辆离开监控区提示
	 * @param cardata
	 * @param domain
	 */
	private void leaveWarning(CarData cardata, MonitorDomain domain){
		if(cardata == null || domain == null){
			return;
		}
		DynamicPoint car = mDisplayManager.queryCar(cardata);

		if(car != null && mWarningInfo!= null){
			String carNo = cardata.getCarNo();
			mWarningInfo.setTextColor(Color.RED);
			mWarningInfo.setBackgroundColor(Color.argb(180, 155, 155, 155));
			mWarningInfo.setTextSize(35.0f);
			mWarningInfo.setVisibility(View.VISIBLE);
			String time = new Date(System.currentTimeMillis()).toLocaleString();
			mWarningInfo.setText(domain.getMonitorName() + ": \n" + time+"\n"+carNo+"离开监测区");
			mWarningInfo.invalidate();
			mUIHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mWarningInfo.setVisibility(View.GONE);
				}
			}, 5000);

			//鸣笛音效
			SoundMonitor.getSoundMonitor().whistle();
			SoundMonitor.getSoundMonitor().shake();
		}
	}


	/**
	 *  监控区域类
	 *
	 */
	private class MonitorDomain {
		private Geometry     monitorGeometry    = null;
		private Set<Integer> monitorIDs         = null;
		private String       monitorName        = null;
		public MonitorDomain(){
			monitorIDs = new HashSet<Integer> ();
		}
		public boolean setMonitorGeometry (Geometry geometry ) {
			if(geometry != null) {
				monitorGeometry = geometry;
			}else{
				return false;
			}

			return true;
		}

		public void addMonitorCarID (Integer id) {
			monitorIDs.add(id);
		}

		public Geometry getMonitorGeometry() {
			return monitorGeometry;
		}
		public Set<Integer> getMointorIDs () {
			return monitorIDs;
		}

		public void setMointorName(String monitorname){
			monitorName = monitorname;
		}

		public String getMonitorName(){
			return monitorName;
		}
	}

	/**
	 * 获取几何对象文本
	 * @param geometry
	 * @param content
	 * @return
	 */
	public GeoText createGeoText(Geometry geometry, String content) {
		GeoText geoText = null;
		if(geometry != null){
			TextStyle textStyle = new TextStyle();
			//textStyle.setRotation(30.0);
			//textStyle.setShadow(true);
			textStyle.setAlignment(TextAlignment.TOPCENTER);
			textStyle.setBackColor(new com.supermap.data.Color(0x53ccc3));
			textStyle.setForeColor(new com.supermap.data.Color(0x000000));
			textStyle.setBackOpaque(true);
			textStyle.setBold(true);
			textStyle.setFontName("宋体");
			textStyle.setFontHeight(10.0);
			textStyle.setFontWidth(10.0);
			textStyle.setSizeFixed(true);
			//textStyle.setItalic(true);
			//textStyle.setOutline(true);
			//textStyle.setStrikeout(true);
			//textStyle.setUnderline(true);
			textStyle.setWeight(50);

			Point2D point2d = geometry.getInnerPoint();
			TextPart textPart = new TextPart(content, point2d);
			geoText = new GeoText(textPart, textStyle);
		}
		return geoText;
	}
}
