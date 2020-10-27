package com.supermap.navidemo.configuration;

import com.supermap.navidemo.app.MyApplication;
import com.supermap.navidemo.filemanager.Decompressor;
import com.supermap.navidemo.filemanager.FileManager;
import com.supermap.navidemo.filemanager.MyAssetManager;

import java.io.File;
import java.io.InputStream;




public class DefaultDataConfiguration {
	
	private final       String DataDir     = "NaviData";
	public static final String MapDataPath = MyApplication.SDCARD + "SuperMap/Demos/NaviData/";
	
	private final       String LicenseName = "Trial License.slm";
	public static String LicensePath = MyApplication.SDCARD + "SuperMap/License/" ;
	public static final String DefaultWorkspace = MapDataPath + "SuperMapCloud.smwu";
	public DefaultDataConfiguration () 
	{
		
	}
	
	/**
	 * ��������
	 */
	public void autoConfig () 
	{
		
		File licenseDir = new File (LicensePath);
		File mapDataDir = new File (MapDataPath);
		
		if(!licenseDir.exists()){
			FileManager.getInstance().mkdirs(LicensePath);
			configLicense();
		}else {
			boolean isLicenseExists = FileManager.getInstance().isFileExist(LicensePath + LicenseName);
			if(isLicenseExists == false)
			{
				configLicense();
			}
		}
		
		if (!mapDataDir.exists())
		{
			FileManager.getInstance().mkdirs(MapDataPath);
			configMapData();
		}else {
			boolean isWorkspaceFileExists = FileManager.getInstance().isFileExist(MapDataPath + "NaviMap/RoadNameIndex.ndf");
			
			if(isWorkspaceFileExists == false)
			{
				configMapData();
			}
		}
	}
	
    /**
     * ��������ļ�
     */
	private void configLicense ()
	{
		InputStream is = MyAssetManager.getInstance().open(LicenseName);
		if(is != null){
		    FileManager.getInstance().copy(is, LicensePath + LicenseName);
		}
	}
	
	/**
	 * ���õ�ͼ����
	 */
	private void configMapData () 
	{
		String[] datas = MyAssetManager.getInstance().openDir(DataDir);
		for (String data : datas)
		{
			InputStream is = MyAssetManager.getInstance().open(DataDir + "/" + data);        // data is a zip file under DataDir
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

