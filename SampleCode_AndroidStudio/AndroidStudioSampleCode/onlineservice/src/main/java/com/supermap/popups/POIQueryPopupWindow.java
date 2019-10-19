package com.supermap.popups;

import com.supermap.onlinedemo.R;
import com.supermap.onlineservices.POIInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class POIQueryPopupWindow extends PopupWindow {
	
	private LayoutInflater inflater;
	
	public POIQueryPopupWindow(Context context,int width,int height,POIInfo poiInfo){
		setHeight(height);
		setWidth(width);
		inflater=LayoutInflater.from(context);
		View view=inflater.inflate(R.layout.popup_poi_query,null);
		initPopupView(view,poiInfo);
		setContentView(view);
		setAnimationStyle(R.style.popup_callout);
	}
	
	private void initPopupView(View view,POIInfo poiInfo){
		TextView tvAddress,tvX,tvY,tvConfidence,tvName,tvTelephone;
		tvAddress= (TextView) view.findViewById(R.id.tvAddress);
		tvX= (TextView) view.findViewById(R.id.tvX);
		tvY= (TextView) view.findViewById(R.id.tvY);
		tvConfidence= (TextView) view.findViewById(R.id.tvConfidence);
		tvName= (TextView) view.findViewById(R.id.tvName);
		tvTelephone= (TextView) view.findViewById(R.id.tvTelephone);
		
		tvAddress.setText(poiInfo.getAddress());
		tvX.setText(poiInfo.getLocation().getX()+"");
		tvY.setText(poiInfo.getLocation().getY()+"");
		tvConfidence.setText(poiInfo.getConfidence());
		tvName.setText(poiInfo.getName());
		tvTelephone.setText(poiInfo.getTelephone());
	}
	
}
