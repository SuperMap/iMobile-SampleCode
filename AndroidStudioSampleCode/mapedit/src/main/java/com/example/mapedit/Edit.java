package com.example.mapedit;

import java.util.ArrayList;

import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.MapControl;

public class Edit {
	private MapControl mMapControl;
	public Edit (MapControl mapControl) {
		this.mMapControl=mapControl;
		mMapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
			
			@Override
			public void geometrySelected(GeometrySelectedEvent event) {
				// TODO Auto-generated method stub
				mMapControl.appointEditGeometry(event.getGeometryID(), event.getLayer());
				
			}
			
			@Override
			public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void geometryMultiSelectedCount(int i) {

			}
		});
	}
	public void editnode() {
		mMapControl.setAction(Action.VERTEXEDIT);
	}
	public void addnode() {
		mMapControl.setAction(Action.VERTEXADD);
	}
	public void deletnode() {
		mMapControl.setAction(Action.VERTEXDELETE);
	}
}
