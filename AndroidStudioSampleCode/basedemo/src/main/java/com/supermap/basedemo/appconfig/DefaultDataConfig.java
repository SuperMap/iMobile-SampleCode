package com.supermap.basedemo.appconfig;

import com.supermap.basedemo.file.Decompressor;
import com.supermap.basedemo.file.FileManager;
import com.supermap.basedemo.file.MyAssetManager;

import java.io.File;
import java.io.InputStream;



public class DefaultDataConfig {

	private final String MapData = "Data";
	public static final String MapDataPath = MyApplication.SDCARD+"SuperMap/Demos/BaseDemo/";
	public static String LicPath = MyApplication.SDCARD+"SuperMap/License/";
	private final String LicName = "Trial License.slm";
	
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
	    String license    = LicPath + LicName;
		
		File licenseFile = new File(license);
		if(!licenseFile.exists())
			configLic();
		
		File dir = new File(mapDataPah);
		if(!dir.exists()){
			FileManager.getInstance().mkdirs(mapDataPah);
			configMapData();
		}else{
			if(FileManager.getInstance().isFileExsit(mapDataPah+DefaultDataManager.mDefaultServer)){
				return;
			}
			
			boolean hasMapData = false;
			File[] datas = dir.listFiles();
			for(File data:datas){
				if(data.getName().endsWith("SMWU")||data.getName().endsWith("smwu")
					||data.getName().endsWith("SXWU")||data.getName().endsWith("sxwu"))
				{
					//如果默认的数据被删除，那就加载第一个工作空间
					MyApplication.getInstance().getDefaultDataManager().setWorkspaceServer(data.getAbsolutePath());
					hasMapData = true;
					break;
				}
			}
			if(!hasMapData)
			{
				configMapData();
			}
		}
	}
	
	/**
	 * 配置许可文件
	 */
	private void configLic()
	{
		InputStream is = MyAssetManager.getInstance().open(LicName);
		if(is!=null)
		   FileManager.getInstance().copy(is, LicPath+LicName);
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
