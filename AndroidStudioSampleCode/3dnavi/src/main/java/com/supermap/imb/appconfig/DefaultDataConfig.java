package com.supermap.imb.appconfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import com.supermap.imb.file.FileManager;
import com.supermap.imb.file.MyAssetManager;
import com.supermap.imb.file.ZipFileUtil;


public class DefaultDataConfig {

	private final String MapData = "Data";
	public static final String MapDataPath = MyApplication.SDCARD+"SuperMap/Demos/3DNaviDemo/";
	public static String LicPath = MyApplication.SDCARD+"SuperMap/License/";

	// 指定一个重要的数据文件名，用于检查是否已经拷贝过数据
	public static final String mDefaultServer = "凯德Mall/凯德Mall.sxwu";

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
				try {
					ZipFileUtil.unZipFile(zip, MapDataPath);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ZipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//删除压缩包
				File zipFile = new File(zip);
				zipFile.delete();
			}
		}


	}
}
