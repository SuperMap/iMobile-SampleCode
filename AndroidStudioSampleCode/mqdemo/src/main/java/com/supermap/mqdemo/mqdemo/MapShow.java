/**
 *
 */
package com.supermap.mqdemo.mqdemo;

import android.content.Context;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Action;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;

/**
 * @author zhengyl
 *
 */
public class MapShow {
	private Context mContext;

	private Workspace mWorkspace = null;

	private WorkspaceConnectionInfo mInfo = null;

	private MapControl mMapControl = null;

	private Map mMap = null;

	private String mServer = null;

	public MapShow(Context context,MapControl mapcontrol){
		mContext = context;

		mWorkspace = new Workspace();

		mInfo = new WorkspaceConnectionInfo();

		mMapControl = mapcontrol;
	}

	public void setMapData(String server){
		this.mServer = server;
	}

	public Workspace getWorkspace() {
		return mWorkspace;
	}

	private boolean openWorkspace(){
		if(mServer == null){
			throw new IllegalStateException("You did not set map data");
		}

		mInfo.setServer(mServer);

		int pos = mServer.lastIndexOf(".");
		String ext = mServer.substring(pos);
		if (ext.equalsIgnoreCase(".SMWU")) {
			mInfo.setType(WorkspaceType.SMWU);
		} else if (ext.equalsIgnoreCase(".SXWU")) {
			mInfo.setType(WorkspaceType.SXWU);
		}

		boolean isopen = false;
		try {
			isopen = mWorkspace.open(mInfo);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return isopen;
	}

	private void initMapControl(){
		Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();

		display.getMetrics(dm);

		mMap = mMapControl.getMap();

		mMap.setMapDPI(dm.densityDpi);

		mMap.setWorkspace(mWorkspace);

		//设置地图操作的监听器
		mMapControl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return mMapControl.onMultiTouch(event);
			}
		});

		// 支持放大镜选择
//		mMapControl.setMagnifierEnabled(true);
	}

	/**
	 * 打开地图
	 */
	public void showMap(){
		if(openWorkspace()){
			initMapControl();
			String mapName = mWorkspace.getMaps().get(1);
//			mMap.setFullScreenDrawModel(true);
			mMap.open(mapName);
			mMapControl.setAction(Action.SELECT);
			mMap.refresh();
		}

	}

	public Point2D getCenter() {
		return mMap.getCenter();
	}

	public void dispose(){
		mMap.close();
		mMapControl.dispose();

		try {
			mWorkspace.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mWorkspace.close();
		mInfo.dispose();
		mWorkspace.dispose();
	}

	public Rectangle2D getBounds() {
		return mMap.getBounds();
	}

	public Point2D getPoint(Point2D pntGPS) {
		if (pntGPS.getX() < 0.00001 || pntGPS.getY() < 0.00001) {
//			Toast toast = Toast.makeText(mContext, "当前坐标无效", Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
//			toast.show();
			Looper.prepare();
			MyApplication.getInstance().showInfo("当前坐标无效");
			Looper.loop();
			return null;
		}

//		if (!getBounds().contains(pntGPS)) {
//			
//		} else
		{
			PrjCoordSys prj = mMap.getPrjCoordSys();
			if (prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
				Point2Ds points = new Point2Ds();
				points.add(pntGPS);

				PrjCoordSys destPrj = new PrjCoordSys();
				destPrj.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				CoordSysTranslator.convert(points, destPrj, prj, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

				pntGPS.setX(points.getItem(0).getX());
				pntGPS.setY(points.getItem(0).getY());
			}
		}


		return pntGPS;
	}

	public Point2D convertMapToDatasetPoint(Point2D pntSrc, PrjCoordSysType typeDest) {
		PrjCoordSys prj = mMap.getPrjCoordSys();
		if (prj.getType() != typeDest) {
			Point2Ds points = new Point2Ds();
			points.add(pntSrc);

			PrjCoordSys destPrj = new PrjCoordSys();
			destPrj.setType(typeDest);
			CoordSysTranslator.convert(points, prj, destPrj, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

			pntSrc.setX(points.getItem(0).getX());
			pntSrc.setY(points.getItem(0).getY());
		}
		return pntSrc;
	}

	public android.content.res.Resources getResources() {
		return mMapControl.getResources();
	}

	public void panTo(Point2D pnt) {
		mMapControl.panTo(pnt, 50);
	}

	public Map getMap(){
		return mMap;
	}

	public MapControl getMapControl() {
		return mMapControl;
	}
}
