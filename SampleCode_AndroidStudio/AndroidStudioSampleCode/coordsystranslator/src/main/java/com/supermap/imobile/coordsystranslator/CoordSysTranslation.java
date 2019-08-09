package com.supermap.imobile.coordsystranslator;

import android.widget.TextView;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.GeoCoordSys;
import com.supermap.data.GeoPoint;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.PrjParameter;
import com.supermap.data.Projection;
import com.supermap.data.ProjectionType;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;

public class CoordSysTranslation {
	
	private Workspace mWorkspace = null;
	
	private MapControl mMapControlSrc = null;
	
	private MapControl mMapControlDes = null;
	
	private TextView mTextViewSrc = null;

	private TextView mTextViewDes = null;
	
	private Map mMapSrc = null;
	
	private Map mMapDes = null;
	
	private Datasource mDatasource = null;
	
	private Datasets mDatasets = null;
	
	private Dataset mDataset = null;
	
	private DatasetVector mDatasetTemp = null;
	
	private String mTempDatasetName = "Temp_Dataset_Name";
	
	private PrjCoordSys mTargetPrjCoordSys = null;
	
	private CoordSysTransParameter mCoordSysTransParameter = null;
	
	/**
	 * 根据workspace和map构造
	 */
	public CoordSysTranslation(Workspace workspace, MapControl mapControlSrc,MapControl mapControlDes,TextView txtSrc, TextView txtDes){
		mWorkspace = workspace;
		mMapControlSrc = mapControlSrc;
		mMapControlDes = mapControlDes;
		mTextViewSrc = txtSrc;
		mTextViewDes = txtDes;
		mMapSrc = mMapControlSrc.getMap();
		mMapDes = mMapControlDes.getMap();
		initialize();
	}

	/**
	 * 执行初始化 
	 */
	private void initialize() {
			// 打开工作空间和地图
		try{
			mMapSrc.open(mWorkspace.getMaps().get(0));
			mDatasource = mWorkspace.getDatasources().get(0);
			mDatasets = mDatasource.getDatasets();
			mDataset = (DatasetVector)mDatasets.get(0);
			if(mDatasets.contains(mTempDatasetName)){
				mDatasetTemp = (DatasetVector) mDatasets.get(mTempDatasetName);
			}else{
				DatasetVectorInfo datasetInfo = new DatasetVectorInfo();
				datasetInfo.setName(mTempDatasetName);
				datasetInfo.setType(DatasetType.POINT);
				mDatasetTemp = mDatasets.create(datasetInfo);
			}
			prjCoordSysPrint(mDataset,mTextViewSrc);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行投影转换
	 */
	public void transform(int type){
		try{
			mapDesReset();
			Point2Ds points = new Point2Ds();
			Recordset recordset = ((DatasetVector)mDataset).getRecordset(false, CursorType.STATIC);
			recordset.moveFirst();
			GeoPoint point;
			Geometry geo;
			while(!recordset.isEOF()){
				geo = recordset.getGeometry();
				if(geo instanceof GeoPoint){
					point = (GeoPoint)geo;
					points.add(new Point2D(point.getX(), point.getY()));
				}
				recordset.moveNext();
			}
			//传入点串,原投影和目标投影
			CoordSysTranslator.convert(points,mDataset.getPrjCoordSys(),getTargetPrjCoordSys(type), getcoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			mDatasetTemp.setPrjCoordSys(getTargetPrjCoordSys(type));
			recordset = mDatasetTemp.getRecordset(false, CursorType.DYNAMIC);
			for(int i=points.getCount()-1;i>=0;i--){
				point = new GeoPoint(points.getItem(i));
				recordset.addNew(point);
				recordset.update();
			}
			recordset.close();
			
			mMapDes.getLayers().add(mDatasetTemp, true);
			mMapDes.setCenter(mMapDes.getBounds().getCenter());
			mMapDes.setScale(mMapSrc.getScale());
			mMapDes.refresh();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 按照不同的投影类型，初始化投影坐标系
	 */
	private PrjCoordSys getTargetPrjCoordSys(int type){
		PrjParameter parameter;
		Projection projection;
			switch(type){
			case 1:
				mTargetPrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_USER_DEFINED);
				projection = new Projection(ProjectionType.PRJ_GAUSS_KRUGER);
				mTargetPrjCoordSys.setProjection(projection);
				parameter = new PrjParameter();
				parameter.setCentralMeridian(110);
				parameter.setStandardParallel1(20);
				parameter.setStandardParallel2(40);
				mTargetPrjCoordSys.setPrjParameter(parameter);
				break;
				
			case 2:
				mTargetPrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_USER_DEFINED);
				projection = new Projection(ProjectionType.PRJ_TRANSVERSE_MERCATOR);
				mTargetPrjCoordSys.setProjection(projection);
				parameter = new PrjParameter();
				parameter.setCentralMeridian(110);
				parameter.setStandardParallel1(0);
				mTargetPrjCoordSys.setPrjParameter(parameter);
				break;
				
			case 3:
				mTargetPrjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_USER_DEFINED);
				projection = new Projection(ProjectionType.PRJ_LAMBERT_CONFORMAL_CONIC);
				mTargetPrjCoordSys.setProjection(projection);
				parameter = new PrjParameter();
				parameter.setCentralMeridian(110);
				parameter.setStandardParallel1(30);
				mTargetPrjCoordSys.setPrjParameter(parameter);
				break;
				
			default:
				break;
			}
		return mTargetPrjCoordSys;
	}
	
	/**
	 * 初始化投影转换参数
	 */
	private CoordSysTransParameter getcoordSysTransParameter(){
		if (mCoordSysTransParameter == null) {
			mCoordSysTransParameter = new CoordSysTransParameter();
		}
		
		return mCoordSysTransParameter;
	}
	
	/**
	 * 重置目标地图窗口和目标投影信息显示窗口
	 */
	private void mapDesReset(){
		try{
			mMapDes.getLayers().clear();
			mMapControlDes.postInvalidate();
			Recordset recordset = mDatasetTemp.getRecordset(false, CursorType.DYNAMIC);
			recordset.deleteAll();
			recordset.update();
			recordset.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 输出投影信息 
	 */
	private void prjCoordSysPrint(Dataset dataset,TextView textView) {
		try{
			PrjCoordSys prjCoordSys = dataset.getPrjCoordSys();
			PrjParameter prjParameter = prjCoordSys.getPrjParameter();
			GeoCoordSys geoCoordSys = prjCoordSys.getGeoCoordSys();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("当前投影信息如下：\n");
			sb.append("投影名称："+prjCoordSys.getName().toString()+"\n");
			sb.append("投影类型："+prjCoordSys.getProjection().getName().toString()+"\n");
			sb.append("中央经线："+prjParameter.getCentralMeridian()+"\n");
			sb.append("原点维度："+prjParameter.getCentralParallel()+"\n");
			sb.append("第一标准纬线："+prjParameter.getStandardParallel1()+"\n");
			sb.append("第二标准纬线："+prjParameter.getStandardParallel2()+"\n");
			sb.append("水平偏移量："+prjParameter.getFalseEasting()+"\n");
			sb.append("垂直偏移量："+prjParameter.getFalseNorthing()+"\n");
			sb.append("比例因子："+prjParameter.getScaleFactor()+"\n");
			sb.append("方位角："+prjParameter.getAzimuth()+"\n");
			sb.append("第一点经线："+prjParameter.getFirstPointLongitude()+"\n");
			sb.append("第二点经线："+prjParameter.getSecondPointLongitude()+"\n");
			
			sb.append("地理坐标系："+geoCoordSys.getName()+"\n");
			sb.append("大地参照系："+geoCoordSys.getGeoDatum().getName()+"\n");
			sb.append("参考椭球体："+geoCoordSys.getGeoDatum().getGeoSpheroid().getName()+"\n");
			sb.append("椭球长半轴："+geoCoordSys.getGeoDatum().getGeoSpheroid().getAxis()+"\n");
			sb.append("椭球扁率："+geoCoordSys.getGeoDatum().getGeoSpheroid().getFlatten()+"\n");
			sb.append("本初子午线："+geoCoordSys.getGeoPrimeMeridian().getLongitudeValue()+"\n");
		
			textView.setText(sb.toString());
			textView.postInvalidate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showCoorsysInfo(){
		prjCoordSysPrint(mDatasetTemp,mTextViewDes);
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		mDatasets.delete(mTempDatasetName);
	}
}
