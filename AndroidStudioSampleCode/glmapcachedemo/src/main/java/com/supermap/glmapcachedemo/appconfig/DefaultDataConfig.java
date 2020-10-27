package com.supermap.glmapcachedemo.appconfig;

import java.io.File;
import java.io.InputStream;

import com.supermap.glmapcachedemo.file.Decompressor;
import com.supermap.glmapcachedemo.file.FileManager;
import com.supermap.glmapcachedemo.file.MyAssetManager;

public class DefaultDataConfig {

	private final String MapData = "Data";
	public static final String MapDataPath = MyApplication.SDCARD+"SuperMap/Demos/GLMapCacheDemo/";
	public static String LicPath = MyApplication.SDCARD+"SuperMap/License/";

	// 指定一个重要的数据文件名，用于检查是否已经拷贝过数据
	public static final String mDefaultServer = "VectorCache.xml";

	/**
	 * 构造函数
	 */
	public DefaultDataConfig()
	{

	}

	/**
	 * 配置数据
	 */
	public void autoConfig(){
		//如果有数据了则认为用户已经清理数据盘
		String mapDataPah = MapDataPath+"/";


		File dir = new File(mapDataPah);
		if(!dir.exists()){
			FileManager.getInstance().mkdirs(mapDataPah);
			configMapData();
		}else{
			if(!FileManager.getInstance().isFileExsit(mapDataPah+mDefaultServer)){
				configMapData();
			}
		}

	}

	/**
	 * 配置地图数据
	 */
	private void configMapData(){
		String[] datas = MyAssetManager.getInstance().opendDir(MapData);
		for(String data:datas){
			InputStream is = MyAssetManager.getInstance().open(MapData+"/"+data);
			String zip = MapDataPath+"/"+data;
			boolean result = FileManager.getInstance().copy(is, zip);
			if(result){
				Decompressor.UnZipFolder(zip, MapDataPath);
				//删除压缩包
				File zipFile = new File(zip);
				zipFile.delete();
			}
		}


	}
}
