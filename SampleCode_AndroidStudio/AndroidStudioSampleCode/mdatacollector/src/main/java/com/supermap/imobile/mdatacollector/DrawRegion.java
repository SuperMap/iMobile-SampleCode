package com.supermap.imobile.mdatacollector;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.Datasources;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.MapControl;

public class DrawRegion {
	
	private Layer  editLayer;
	private MapControl mMapControl;
	private PopupWindow popupWn;
	private boolean isInitedDrawingLayer = false;
	private NetworkAccess mNetworkAccess;
	private char transmission;
	
	public DrawRegion(MapControl mapControl, NetworkAccess networkAccess){
		mMapControl = mapControl;
		mNetworkAccess = networkAccess;
	}
	
	/**
	 *  Draw a region
	 * @return   初始化成功或失败(true or false)
	 */
	private boolean initDrawingLayer(){
		Workspace workspace = mMapControl.getMap().getWorkspace();
		String datasourceName = "TempUDB";
		
		String tempPath = Environment.getTemporaryPath() + "/TempUDB.udb";

		File dataFile = new File(tempPath);
		
		// 设计图层风格
		GeoStyle geoStyle_R = new GeoStyle();
	    geoStyle_R.setFillForeColor(new com.supermap.data.Color(149, 23,17));
	    geoStyle_R.setFillBackOpaque(true);
	    geoStyle_R.setFillOpaqueRate(40);
	    geoStyle_R.setLineWidth(0.1);
	    geoStyle_R.setLineColor(new com.supermap.data.Color(40,40,40));
	    LayerSettingVector layerSettingVector = new LayerSettingVector();
	    layerSettingVector.setStyle(geoStyle_R);
		
		//不存在则加
		if(workspace.getDatasources().indexOf(datasourceName)==-1 || dataFile.exists() == false){
			
			if(workspace.getDatasources().indexOf(datasourceName)!=-1 && dataFile.exists() == false){
				workspace.getDatasources().close(datasourceName);
				
			}
			Datasources datasources= workspace.getDatasources();
			DatasourceConnectionInfo info = new DatasourceConnectionInfo();
			
			if(dataFile.exists()){
				dataFile.delete();
				
			}
			String tempPathUDD = Environment.getTemporaryPath() + "/TempUDB.udd";
			
			File dataFileUDD = new File(tempPathUDD);
			if(dataFileUDD.exists()){
				dataFileUDD.delete();
				
			}
			
			info.setServer(Environment.getTemporaryPath()+"/TempUDB.udb");
			info.setEngineType(EngineType.UDB);
			info.setAlias(datasourceName);
			Datasource udb = datasources.create(info);
			if(udb == null){
				Log.e(this.getClass().getName(),"创建UDB失败了");
				return false;
			}
			DatasetVectorInfo vecInfo = new DatasetVectorInfo();
			vecInfo.setName("TempRegion");
			vecInfo.setType(DatasetType.REGION);
			DatasetVector dataset = udb.getDatasets().create(vecInfo);
			if(dataset == null){
				Log.e(this.getClass().getName(),"创建REGION数据集失败了");
				return false;
			}
			Layer drawLayer = mMapControl.getMap().getLayers().add(dataset, true);
			drawLayer.setEditable(true);

			String mapXML = mMapControl.getMap().toXML();
			workspace.getMaps().setMapXML(0, mapXML);
		}
		
		editLayer = mMapControl.getMap().getLayers().get(0);
		
		editLayer.setAdditionalSetting(layerSettingVector);
		return true;
	}
	
	public void enabledDrawing(boolean enable){
		if(enable){
			if (!isInitedDrawingLayer) {
				isInitedDrawingLayer = initDrawingLayer();
			}
			if (isInitedDrawingLayer) {
				editLayer.setEditable(true);
				mMapControl.setAction(Action.DRAWPLOYGON);
			}
		} else {
			editLayer.setEditable(false);
			mMapControl.setAction(Action.SELECT);
		}
	}
	
	private OnClickListener onClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_popcancel:
				popupWn.dismiss();
//				mMapControl.deleteCurrentGeometry();
//				mMapControl.getMap().refresh();
//				enabledDrawing(true);
				break;
			case R.id.btn_popconfirm:
				mNetworkAccess.netWorkTransfering(transmission, getBounds());
//				mMapControl.deleteCurrentGeometry();
//				mMapControl.getMap().refresh();
				popupWn.dismiss();
				enabledDrawing(false);
				break;
			default:
				break;
			}
		}
	};
	
	private View popupView;
	public void showPopupWn(Context context, char trans){
		if(popupWn == null){
			popupWn = new PopupWindow();
			popupWn.setWidth(LayoutParams.WRAP_CONTENT);
			popupWn.setHeight(LayoutParams.WRAP_CONTENT);
			
			popupView = LayoutInflater.from(context).inflate(R.layout.popupview, null);
			popupWn.setContentView(popupView);
			popupWn.setBackgroundDrawable(new BitmapDrawable());
			((Button)popupView.findViewById(R.id.btn_popconfirm)).setOnClickListener(onClickListener);
			((Button)popupView.findViewById(R.id.btn_popcancel)).setOnClickListener(onClickListener);
			popupWn.setOnDismissListener(new OnDismissListener(){

				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					popupWn.dismiss();
					mMapControl.deleteCurrentGeometry();
					mMapControl.getMap().refresh();
					enabledDrawing(true);
				}});
		}
		transmission = trans;
		popupWn.setOutsideTouchable(true);
		popupWn.showAtLocation(popupView, Gravity.CENTER, 0, 0);
	}
	
	private Rectangle2D getBounds(){
		Rectangle2D bounds = null;
		Geometry geometry = mMapControl.getCurrentGeometry();
		PrjCoordSys prj = mMapControl.getMap().getPrjCoordSys();

        Point2Ds pts = ((GeoRegion)geometry).getPart(0);
        if(prj.getType()!= PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
        	PrjCoordSys desPrj = new PrjCoordSys();
        	desPrj.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
        	CoordSysTranslator.convert(pts, prj, desPrj, new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
        	
        }
        Point2D origin=null, end=null;
        for(int i=0;i<pts.getCount();i++)
        {
            Point2D point = pts.getItem(i);
            
            if(0==i)
            {
            	origin = new Point2D(point);
                end =  new Point2D(point);
                continue;
            }
            
            origin.setX(point.getX()<origin.getX() ? point.getX() : origin.getX());
            origin.setY(point.getY()<origin.getY() ? point.getY() : origin.getY());
            end.setX(point.getX()<end.getX() ? end.getX() : point.getX());
            end.setY(point.getY()<end.getY() ? end.getY() : point.getY()) ;
            
            bounds = new Rectangle2D(origin, end);
        }
        
		return bounds;
	}
}


