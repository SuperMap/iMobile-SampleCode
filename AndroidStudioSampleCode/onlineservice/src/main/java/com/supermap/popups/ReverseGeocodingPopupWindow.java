package com.supermap.popups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.onlinedemo.R;
import com.supermap.onlineservices.GeocodingData;

public class ReverseGeocodingPopupWindow extends PopupWindow{

	public ReverseGeocodingPopupWindow(Context context,int width,int height,GeocodingData geocodingData){
		LayoutInflater inflater=LayoutInflater.from(context);
		View view=inflater.inflate(R.layout.popup_reverse_geocoding, null);
		setWidth(width);
		setHeight(height);
		initPopupView(view,geocodingData);
		setContentView(view);
		setAnimationStyle(R.style.popup_callout);
	}

	private void initPopupView(View view,GeocodingData geocodingData) {
		
		TextView tvName,tvProvince,tvCity,tvCityCode
				,tvDistance,tvNumber,tvStreet,tvCounty,tvFormatedAddress;
		
		tvName=(TextView) view.findViewById(R.id.tvNameRG);
		tvFormatedAddress=(TextView) view.findViewById(R.id.tvFormatedAddressRG);
		tvProvince=(TextView) view.findViewById(R.id.tvProvinceRG);
		tvCity=(TextView) view.findViewById(R.id.tvCityRG);
		tvCityCode=(TextView) view.findViewById(R.id.tvCityCodeRG);
		tvCounty=(TextView) view.findViewById(R.id.tvCountyRG);
		tvDistance=(TextView) view.findViewById(R.id.tvDistanceRG);
		tvNumber=(TextView) view.findViewById(R.id.tvNumberRG);
		tvStreet=(TextView) view.findViewById(R.id.tvStreetRG);
		
		tvName.setText(geocodingData.getName());
		tvFormatedAddress.setText(geocodingData.getFormatedAddress());
		tvProvince.setText(geocodingData.getAddress().getProvince());
		tvCity.setText(geocodingData.getAddress().getCity());
		tvCityCode.setText(geocodingData.getAddress().getCityCode());
		tvCounty.setText(geocodingData.getAddress().getCounty());
		tvDistance.setText(geocodingData.getAddress().getStreetNumber().getDistance());
		tvNumber.setText(geocodingData.getAddress().getStreetNumber().getNumber());
		tvStreet.setText(geocodingData.getAddress().getStreetNumber().getStreet());
		
	}
	
}

