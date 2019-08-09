package com.example.mapedit;

import com.supermap.mapping.Action;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;

import android.util.Log;

public class Draw {
	private MapControl mMapControl;
	private Layers layers;
	public Draw(MapControl mapControl){
		this.mMapControl=mapControl;
		layers=mMapControl.getMap().getLayers();
		Log.e("++++++++", String.valueOf(layers.getCount()));
	}
	
	//
	public void addLine() {
		mMapControl.setAction(Action.CREATEPOLYLINE);
		layers.get("Line@edit").setEditable(true);
	}
	public void addRegion() {
		mMapControl.setAction(Action.CREATEPOLYGON);
		layers.get("Region@edit").setEditable(true);
	}
	public void drawLine() {
		mMapControl.setAction(Action.DRAWLINE);
		layers.get("Line@edit").setEditable(true);
	}
	public void drawRegion() {
		mMapControl.setAction(Action.DRAWPLOYGON);
		layers.get("Region@edit").setEditable(true);
	}
	

}
