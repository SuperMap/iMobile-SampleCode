package com.supermap.popups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.onlinedemo.R;
import com.supermap.onlineservices.GeocodingData;

public class GeocodingPopupWindow extends PopupWindow{

	public GeocodingPopupWindow(Context context,int width,int height,GeocodingData geocodingData){
		LayoutInflater inflater=LayoutInflater.from(context);
		View view=inflater.inflate(R.layout.popup_geocoding, null);
		setWidth(width);
		setHeight(height);
		initPopupView(view,geocodingData);
		setContentView(view);
		setAnimationStyle(R.style.popup_callout);
	}

	private void initPopupView(View view,GeocodingData geocodingData) {
		
		TextView tvX,tvY,tvFormateAddress,tvName,tvProvince,tvCity,tvCityCode
				,tvCounty,tvDistance,tvNumber,tvStreet,tvConfidence;
		
		tvX=(TextView) view.findViewById(R.id.tvX_G);
		tvY=(TextView) view.findViewById(R.id.tvY_G);
		tvName=(TextView) view.findViewById(R.id.tvName_G);
		tvFormateAddress=(TextView) view.findViewById(R.id.tvFormateAddress_G);
		tvProvince=(TextView) view.findViewById(R.id.tvProvince_G);
		tvCity=(TextView) view.findViewById(R.id.tvCity_G);
		tvCityCode=(TextView) view.findViewById(R.id.tvCityCode_G);
		tvCounty=(TextView) view.findViewById(R.id.tvCounty_G);
		tvDistance=(TextView) view.findViewById(R.id.tvDistance_G);
		tvNumber=(TextView) view.findViewById(R.id.tvNumber_G);
		tvStreet=(TextView) view.findViewById(R.id.tvStreet_G);
		tvConfidence=(TextView) view.findViewById(R.id.tvConfidence_G);
		
		tvX.setText(geocodingData.getLocation().getX()+"");
		tvY.setText(geocodingData.getLocation().getY()+"");
		tvName.setText(geocodingData.getName());
		tvFormateAddress.setText(geocodingData.getFormatedAddress());
		tvProvince.setText(geocodingData.getAddress().getProvince());
		tvCity.setText(geocodingData.getAddress().getCity());
		tvCityCode.setText(geocodingData.getAddress().getCityCode());
		tvCounty.setText(geocodingData.getAddress().getCounty());
		tvDistance.setText(geocodingData.getAddress().getStreetNumber().getDistance());
		tvNumber.setText(geocodingData.getAddress().getStreetNumber().getNumber());
		tvStreet.setText(geocodingData.getAddress().getStreetNumber().getStreet());
		tvConfidence.setText(geocodingData.getConfidence()+"");
		
	}
	
}
