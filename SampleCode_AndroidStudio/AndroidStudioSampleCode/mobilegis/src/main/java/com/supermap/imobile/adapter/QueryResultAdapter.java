package com.supermap.imobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Recordset;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

import java.util.Vector;

public class QueryResultAdapter extends BaseExpandableListAdapter {
    Vector<Recordset> mResultData=new Vector<>();
    MapControl mMapControl;
    Context mContext;
    TrackingLayer mTrackingLayer;
    MapView mMapView;

    public QueryResultAdapter(Vector<Recordset> mResultData, MapControl mapControl){
        this.mResultData=mResultData;
        this.mMapControl=mapControl;
        this.mContext=mapControl.getContext();
        this.mTrackingLayer=mapControl.getMap().getTrackingLayer();
        this.mMapView=mapControl.getMap().getMapView();
    }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_queryresult_item, null);
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

            convertView.setOnClickListener(new View.OnClickListener() {
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


//						mDynView.clear();
                    mMapView.removeAllCallOut();

                    final CallOut calloutLocation = new CallOut(mMapView.getContext());
                    calloutLocation.setStyle(CalloutAlignment.BOTTOM);
                    calloutLocation.setLocation(geometry.getInnerPoint().getX(), geometry.getInnerPoint().getY());
                    calloutLocation.setCustomize(true); // 设置自定义背景
                    ImageView imageView = new ImageView(mMapControl.getContext());

                    // 显示起点
                    imageView.setBackgroundResource(R.drawable.ic_btn_poi);
                    calloutLocation.setContentView(imageView);
                    calloutLocation.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            CallOut checkedCallOut = mMapView.getCallOut("POI");
                            if(checkedCallOut !=null)
                                return;

                            CallOut callout = new CallOut(mMapView.getContext());
                            callout.setStyle(CalloutAlignment.BOTTOM);
                            callout.setBackground(Color.WHITE, Color.WHITE);
                            callout.setLocation(calloutLocation.getLocationX(), calloutLocation.getLocationY());
                            View calloutView = LayoutInflater.from(mContext).inflate(R.layout.callout, null);
                            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.WRAP_CONTENT);
//								calloutView.setLayoutParams(params);
                            TextView name = (TextView) calloutView.findViewById(R.id.tv_name);

                            name.setText(obj==null?"null":obj.toString());

                            calloutView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    mMapView.removeCallOut("POI");
                                    mMapView.refresh();
                                }
                            });
                            ListView list = (ListView) calloutView.findViewById(R.id.list_record);
                            list.setAdapter(new RecordsetAdapter(recordset,mMapControl.getContext()));
                            callout.setContentView(calloutView);

                            mMapView.addCallout(callout,"POI");
                            mMapView.refresh();

                        }});

                    mMapView.addCallout(calloutLocation, "Locate");

//						mDynView.refresh();

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_queryresult_group, null);
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
