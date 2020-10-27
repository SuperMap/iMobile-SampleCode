package com.supermap.basedemo.appconfig;


import com.supermap.basedemo.R;

public class DefaultDataManager extends DataManager{

	public static final String mDefaultServer = "Changchun.smwu";
	
	/**
	 * 构造函数
	 */
	DefaultDataManager()
	{
		setWorkspaceServer(DefaultDataConfig.MapDataPath+mDefaultServer);
	}
	
	/**
	 * 获取显示图像资源
	 * @param name   显示对象的名称
	 * @return       资源id
	 */
	public int getMapTypeResource(String name){
		if(name.equals("超图云服务")){
			return R.drawable.ic_map_cloud;
		}else if(name.contains("Rest")){
			return R.drawable.ic_map_rest;
		}else if(name.equals("谷歌地图")){
			return R.drawable.ic_map_google;
		}else if(name.equals("百度地图")){
			return R.drawable.ic_map_baidu;
		}else if(name.equals("天地图")){
				return R.drawable.ic_map_tianditu;
		}else if(name.contains("SIT地图")){
			return R.drawable.ic_map_sit;
		}else if(name.contains("DEM地图")){
			return R.drawable.ic_map_dem;
		}else if(name.contains("SCI地图")){
			return R.drawable.ic_map_sci;
		}else if(name.contains("CAD地图")){
			return R.drawable.ic_map_cad;
		}else if(name.contains("iServerRest地图")){
			return R.drawable.ic_map_rest;
		}else if(name.contains("WMS地图")){
			return R.drawable.ic_map_sci;
		}else if(name.contains("Bing地图")){
			return R.drawable.ic_map_sci;
		}else if(name.contains("OpenStreetMap")){
			return R.drawable.ic_map_sci;
		}
		return R.drawable.ic_map_vector;
	
	}
	
	
}
