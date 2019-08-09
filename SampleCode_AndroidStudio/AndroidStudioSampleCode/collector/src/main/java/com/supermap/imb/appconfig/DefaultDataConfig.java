package com.supermap.imb.appconfig;

import java.io.File;
import java.io.InputStream;

import com.supermap.android.file.Decompressor;
import com.supermap.android.file.FileManager;
import com.supermap.android.file.MyAssetManager;

public class DefaultDataConfig {

	public static final String LicensePath = MyApplication.SDCARD + "SuperMap/License/";
	
	private final       String DataDir     = "CollectorData";
	public static final String MapDataPath = MyApplication.SDCARD + "SampleData/CollectorData/";
    private static final String DefaultServer = "gpscollector.udb";
    
	public static String WorkspacePath = MapDataPath + DefaultServer;
	//public static String WorkspacePath = MyApplication.SDCARD + "/SuperMap/data/changchun.smwu";
	
	/**
	 * 构造函数
	 */
	public DefaultDataConfig () 
	{
		
	}
	
	/**
	 * 配置数据
	 */
	public void autoConfig () 
	{
		File mapDataDir = new File (MapDataPath);
		
		if (!mapDataDir.exists())
		{
			FileManager.getInstance().mkdirs(MapDataPath);
			configMapData();
		}else {
			boolean isWorkspaceFileExists = FileManager.getInstance().isFileExist(MapDataPath + DefaultServer);
			
//			if(isWorkspaceFileExists == false)
//			{
				configMapData();
//			}
		}
	}
	
	/**
	 * 配置地图数据
	 */
	private void configMapData () 
	{
		String[] datas = MyAssetManager.getInstance().openDir(DataDir);
		for (String data : datas)
		{
			InputStream is = MyAssetManager.getInstance().open(DataDir + "/" + data);
			if(is != null){
				String zip = MapDataPath + "/" + data;

				boolean result = FileManager.getInstance().copy(is, zip);
				if (result)
					Decompressor.UnZipFolder(zip, MapDataPath);

				File ziFile = new File(zip);
				ziFile.delete();
			}
		}
	}
	
	
}

