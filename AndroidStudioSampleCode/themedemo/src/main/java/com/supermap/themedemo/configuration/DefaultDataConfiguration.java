package com.supermap.themedemo.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;


import com.supermap.themedemo.app.MyApplication;
import com.supermap.themedemo.filemanager.FileManager;
import com.supermap.themedemo.filemanager.MyAssetManager;
import com.supermap.themedemo.filemanager.ZipUtils;


public class DefaultDataConfiguration {
	private final       String DataDir     = "Data";
	public static final String MapDataPath = MyApplication.SDCARD + "SuperMap/Demos/ThemeDemo/";
	public static String LicensePath = MyApplication.SDCARD + "SuperMap/License/" ;
	
	private final        String LicenseName   = "Trial License.slm";
	private static final String DefaultServer = "Statistics.smwu";
	
	public static String WorkspacePath = MapDataPath + DefaultServer;
	
	/**
	 * ���캯��
	 */
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
			boolean isWorkspaceFileExists = FileManager.getInstance().isFileExist(MapDataPath + DefaultServer);
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
		if(is != null)
		    FileManager.getInstance().copy(is, LicensePath + LicenseName);
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
			String zip = MapDataPath + "/" + data;
			boolean result = FileManager.getInstance().copy(is, zip);
			if (result)
			{
				try {
					File zipFile = new File(zip);
					ZipUtils.upZipFile(zipFile, MapDataPath);
					zipFile.delete();
				}catch (ZipException e){
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

