package com.supermap.ar;

import android.annotation.SuppressLint;
import android.content.Context;


import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("SdCardPath")
public class CustomWorldHelper {
	public static final int LIST_TYPE_EXAMPLE_1 = 1;

	public static World sharedWorld = null;

	public static World generateMyObjects(Context context)
	{
		if(sharedWorld != null)
		{
			return sharedWorld;
		}

		sharedWorld = new World(context);

		//you can use interest icon for more fun, whereas single button.
		//sharedWorld.setDefaultImage(R.drawable.ar_default_unknow_icon);

		//设置 当前位置                       /维度-经度
		sharedWorld.setGeoPosition(39.9918067479341,116.512255196021); //for supermap 7th.

		int size = ((StaticViewGeoObjectActivity)context).arrName.size();

		ArrayList<GeoObject> goArray = new ArrayList<GeoObject>();
		GeoObject original = new GeoObject();

		original.setGeoPosition(39.9918067479341,116.512255196021); //fors supermap 7th.



		for(int i = 0;i<size;i++)
		{
			//创建POI
			GeoObject go1 = new GeoObject(i + 10);
			go1.setGeoPosition(((StaticViewGeoObjectActivity)context).arrY.get(i),
					((StaticViewGeoObjectActivity)context).arrX.get(i));

			go1.setName(((StaticViewGeoObjectActivity)context).arrName.get(i));

			DecimalFormat df = new DecimalFormat("0.00");

			go1.setDistanceFromUser(Double.parseDouble(df.format(Distance.calculateDistanceMeters(original,go1))));//添加距离信息

			goArray.add(go1);

		}

		//将创建的POI放到增强现实中

		for(int i = 0; i<goArray.size();i++)
		{
			sharedWorld.addArObject(goArray.get(i));
		}
		return sharedWorld;
	}

	private static double calcDistance(GeoObject src1, GeoObject src2)
	{
		return Math.sqrt((src2.getLatitude()-src1.getLatitude())*(src2.getLatitude()-src1.getLatitude())
				+ (src2.getLongitude() - src1.getLongitude())*(src2.getLongitude() - src1.getLongitude()));
	}

	public static double getDistance(double lat1, double lng1, double lat2,
									 double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000d) / 10000d;
		s = s * 1000;
		return s;

	}

	private static double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
}
