package com.supermap.onlinedemo;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

/**
 * 用于获取屏幕的坐标
 */

public class Gesture {
	
	private Context context;
	private MapControl mapControl;
	private MapView mapView;
	public Gesture(Context context,MapView mapView){
		this.context=context;
		this.mapView=mapView;
		mapControl=mapView.getMapControl();
		mapControl.setGestureDetector(new GestureDetector(context,gesture));
	}
	private SimpleOnGestureListener gesture=new SimpleOnGestureListener(){
		@Override
		public void onLongPress(MotionEvent event){
			getPoints(event);
		}
	};
	
	private void getPoints(MotionEvent event){
		float x=event.getX();
		float y=event.getY();
		Point2D point2D=mapControl.getMap().pixelToMap(new Point((int)x,(int)y));
    	CallOut callOut = new CallOut(context);
    	callOut.setStyle(CalloutAlignment.BOTTOM);             
    	final View view = LayoutInflater.from(
				context).inflate(
				R.layout.callout, null);
    	callOut.setContentView(view);
    	callOut.setCustomize(true);                            
    	callOut.setLocation(point2D.getX(), point2D.getY());   
    	mapView.addCallout(callOut);
    	Toast.makeText(context, "x="+point2D.getX()+" y="+point2D.getY(), Toast.LENGTH_SHORT).show();
    	System.out.println("x="+point2D.getX()+" y="+point2D.getY());
    	mapControl.getMap().refresh();
	}
}
