package com.supermap.querydemo.query;

import java.util.Vector;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.querydemo.appconfig.MyApplication;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mapping.dyn.DynamicElement;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.querydemo.R;

public class QueryResultPopup extends PopupWindow{
	private MapControl mMapControl = null;
	private MapView mMapView = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;
	private ExpandableListView mResultList = null;
	private Vector<Recordset> mResultData = new Vector<Recordset>();
	private TextView mTip = null;
	private DynamicView mDynView = null;
	private TrackingLayer mTrackingLayer = null;

	/**
	 * 构造函数
	 * @param mapView
	 * @param dynView
	 */
	public QueryResultPopup(MapView mapView,DynamicView dynView) {
		mMapView = mapView;
		mMapControl = mapView.getMapControl();
		mDynView = dynView;
		mInflater = LayoutInflater.from(mMapControl.getContext());
		mTrackingLayer = mMapView.getMapControl().getMap().getTrackingLayer();
		loadView();

		setContentView(mContentView);
		setWidth(MyApplication.dp2px(350));
		setHeight(MyApplication.dp2px(530));

		mResultList.setAdapter(new QueryResultAdapter());
		this.setOutsideTouchable(true);
	}

	/**
	 * 初始化界面
	 */
	private void loadView(){
		mContentView = mInflater.inflate(R.layout.popup_queryresult, null);
		mContentView.findViewById(R.id.btn_hide).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		mResultList = (ExpandableListView) mContentView.findViewById(R.id.list_queryresult);
		mTip = (TextView) mContentView.findViewById(R.id.tv_tip);
	}

	/**
	 * 显示查询结果
	 */
	public void show(){


		if(mResultData.size()>0){
			mTip.setVisibility(View.GONE);
			mResultList.setVisibility(View.VISIBLE);
		}else{
			mTip.setVisibility(View.VISIBLE);
			mResultList.setVisibility(View.GONE);
		}

		// 为解决子项反复添加问题
		for(int i=mResultList.getExpandableListAdapter().getGroupCount()-1;i>=0;i--){
			mResultList.collapseGroup(i);
		}

		for(int i=mResultList.getExpandableListAdapter().getGroupCount()-1;i>=0;i--){
			mResultList.expandGroup(i);
		}
		showAtLocation(mMapControl.getRootView(), Gravity.LEFT|Gravity.TOP, MyApplication.dp2px(100), MyApplication.dp2px(100));
	}

	/**
	 * 清空查询结果
	 */
	public void clear(){
		mDynView.clear();
		mMapView.removeAllCallOut();

		mResultData.clear();
		loadView();
		setContentView(mContentView);
		setWidth(MyApplication.dp2px(350));
		setHeight(MyApplication.dp2px(530));

		mResultList.setAdapter(new QueryResultAdapter());
		this.setOutsideTouchable(true);

		dismiss();
	}

	/**
	 * 增加查询结果
	 * @param recordset
	 */
	public void addResult(Recordset recordset){
		int count = recordset.getRecordCount();
		if(recordset.getRecordCount() > 0){
			boolean hasContain = false;
			for(int i=mResultData.size()-1;i>=0;i--){
				if(mResultData.get(i).getDataset().getName().equals(recordset.getDataset().getName())){
					hasContain = true;
					break;
				}
			}
			if(!hasContain){
				mResultData.add(recordset);
			}

		}
	}

	private class QueryResultAdapter extends BaseExpandableListAdapter{
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			Recordset recordset = mResultData.get(groupPosition);
			recordset.moveTo(childPosition);
			return recordset;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			TextView holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.list_queryresult_item, null);
				holder = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder = (TextView) convertView.getTag();
			}
			try {
				final Recordset recordset = mResultData.get(groupPosition);
				recordset.moveTo(childPosition);
				String fieldName = "Name";//这个字段一定存在
				Geometry geometry = recordset.getGeometry();
				if(geometry.getType() == GeometryType.GEOREGION){
					fieldName = "SMID";
				}
				//得先找一下哪个字段合适
				FieldInfos finfos = recordset.getFieldInfos();

				for(int i=0;i<finfos.getCount();i++){
					if((finfos.get(i).getName().contains("name")||finfos.get(i).getName().contains("NAME"))&& finfos.get(i).getType() == FieldType.TEXT){
						fieldName = finfos.get(i).getName();

						break;
					}
				}

				final Object obj = recordset.getFieldValue(fieldName);
				holder.setText(obj==null?"null":obj.toString());

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						recordset.moveTo(childPosition);

						Geometry geometry = recordset.getGeometry();
						if (geometry.getType() == GeometryType.GEOREGION) {
							GeoStyle geostyle = new GeoStyle();
							geostyle.setFillForeColor(new com.supermap.data.Color(255, 0, 0));
							geostyle.setFillOpaqueRate(30);
							geostyle.setLineColor(new com.supermap.data.Color(180, 180, 200));
							geostyle.setLineWidth(0.4);
							geometry.setStyle(geostyle);
							int index = mTrackingLayer.indexOf("dypt");
							if (index >= 0)
								mTrackingLayer.remove(index);
							mTrackingLayer.add(geometry, "dypt");
							mMapControl.getMap().refresh();
						}

						if (geometry.getType() == GeometryType.GEOLINE) {
							GeoStyle geostyle = new GeoStyle();
							geostyle.setLineColor(new com.supermap.data.Color(255, 0, 0));
							geostyle.setLineWidth(1);
							geometry.setStyle(geostyle);
							int index = mTrackingLayer.indexOf("dypt");
							if (index >= 0)
								mTrackingLayer.remove(index);
							mTrackingLayer.add(geometry, "dypt");
							mMapControl.getMap().refresh();
						}


						mDynView.clear();
						mMapView.removeAllCallOut();

						final CallOut calloutLocation = new CallOut(mMapView.getContext());
						calloutLocation.setStyle(CalloutAlignment.BOTTOM);
						calloutLocation.setLocation(geometry.getInnerPoint().getX(), geometry.getInnerPoint().getY());
						calloutLocation.setCustomize(true); // 设置自定义背景
						ImageView imageView = new ImageView(mMapControl.getContext());

						// 显示起点
						imageView.setBackgroundResource(R.drawable.ic_btn_poi);
						calloutLocation.setContentView(imageView);
						calloutLocation.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								CallOut checkedCallOut = mMapView.getCallOut("POI");
								if(checkedCallOut !=null)
									return;

								CallOut callout = new CallOut(mMapView.getContext());
								callout.setStyle(CalloutAlignment.LEFT);
								callout.setBackground(Color.WHITE, Color.WHITE);
								callout.setLocation(calloutLocation.getLocationX(), calloutLocation.getLocationY());
								View calloutView = mInflater.inflate(R.layout.callout, null);
								TextView name = (TextView) calloutView.findViewById(R.id.tv_name);

								name.setText(obj==null?"null":obj.toString());

								calloutView.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										mMapView.removeCallOut("POI");
										mMapView.refresh();
									}
								});
								ListView list = (ListView) calloutView.findViewById(R.id.list_record);
								list.setAdapter(new RecordsetAdapter(recordset));
								callout.setContentView(calloutView);

								mMapView.addCallout(callout,"POI");
								mMapView.refresh();

							}});

						mMapView.addCallout(calloutLocation, "Locate");

						mDynView.refresh();

						mMapControl.panTo(geometry.getInnerPoint(), 300);

					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mResultData.get(groupPosition).getRecordCount();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mResultData.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mResultData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			TextView holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.list_queryresult_group, null);
				holder = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder = (TextView) convertView.getTag();
			}
			try{
				String dataset = mResultData.get(groupPosition).getDataset().getName();
				holder.setText(dataset);
			}catch(Exception e){
				System.out.println(e);
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

	private class RecordsetAdapter extends BaseAdapter{
		private Recordset mRecordset = null;
		private FieldInfos mFieldInfos = null;
		private int attributeCount = 0;
		private Point2D point = null;
		public RecordsetAdapter(Recordset recordset) {
			mRecordset = recordset;
			mFieldInfos = mRecordset.getFieldInfos();
			point = mRecordset.getGeometry().getInnerPoint();
		}

		@Override
		public int getCount() {
			//return mRecordset==null?0:mRecordset.getFieldCount();
			return 3;
		}

		@Override
		public Object getItem(int index) {
			return mRecordset.getFieldValue(index);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup arg2) {
			Holder holder = null;

			if(convertView==null){
				convertView = mInflater.inflate(R.layout.list_callout_item, null);
				holder = new Holder();
				holder.filed = (TextView) convertView.findViewById(R.id.tv_field);
				holder.filedValue = (TextView) convertView.findViewById(R.id.tv_fieldvalue);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}


			attributeCount ++;
			if (index == 0) {
				holder.filed.setText("坐标X: ");
				if(point != null){
					holder.filedValue.setText(point.getX() + "");
				}else{
					holder.filedValue.setText("");
				}
			}

			if (index == 1) {

				holder.filed.setText("坐标Y: ");
				if (point != null) {
					holder.filedValue.setText(point.getY() + "");
				} else {
					holder.filedValue.setText("");
				}

			}


			if(index >1){

				holder.filed.setText("");

				holder.filedValue.setText("");

			}

			return convertView;
		}

		class Holder{
			TextView filed;
			TextView filedValue;
		}
	}
	public void colsePoup(){
		dismiss();
	}
}
