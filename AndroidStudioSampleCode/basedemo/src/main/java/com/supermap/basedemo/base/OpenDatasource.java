package com.supermap.basedemo.base;

import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;

public class OpenDatasource {

	public static Datasource open_WMS130_World(Workspace mWorkspace, String name){
		// 初始化测试数据
		if(mWorkspace == null)
			return null;
		
		DatasourceConnectionInfo dInfo = new DatasourceConnectionInfo();
		dInfo.setServer("http://support.supermap.com.cn:8090/iserver/services/map-world/wms130/World");//http://support.supermap.com.cn:8090/iserver/services/map-world/wms130/%E4%B8%96%E7%95%8C%E5%9C%B0%E5%9B%BE_Day/wms
		dInfo.setWebVersion("1.3.0");
		dInfo.setWebFormat("image/png");
		dInfo.setWebCoordinate("EPSG:3857");
		dInfo.setWebVisibleLayers("0.10,0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1,0.0");
		dInfo.setWebBBox(new Rectangle2D(-1.003750834278E7, -1.003750834279E7, 1.003750834278E7, 1.003750834279E7));
		dInfo.setEngineType(EngineType.OGC);
		dInfo.setDriver("WMS");
		if(name == null || name.isEmpty()){
			dInfo.setAlias("WMS");
		}else {
			dInfo.setAlias(name);
		}

		Datasource ds = mWorkspace.getDatasources().open(dInfo);
		dInfo.dispose();
		
		return ds;
		
	}
}
