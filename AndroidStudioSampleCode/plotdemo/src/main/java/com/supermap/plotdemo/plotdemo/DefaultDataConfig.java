package com.supermap.plotdemo.plotdemo;

import com.supermap.plotdemo.util.FileManager;
import com.supermap.plotdemo.util.MyAssetManager;
import com.supermap.plotdemo.util.ZipFileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;



public class DefaultDataConfig {

	private final String DataDir = "Data";
	public static final String MapDataPath = MyApplication.SDCARD + "SuperMap/Demos/PlotDemo/";

	private final String LicenseName = "SuperMap iMobile Trial.slm";
	public static String LicensePath = MyApplication.SDCARD + "SuperMap/License/";
	private static final String DefaultServer = "Workspace/SuperMapCloud.smwu";

	public static String WorkspacePath = MapDataPath + DefaultServer;

	/**
	 * 构造函数
	 */
	public DefaultDataConfig() {

	}

	/**
	 * 配置数据
	 */
	public void autoConfig() {

		File licenseDir = new File(LicensePath);
		File mapDataDir = new File(MapDataPath);

		if (!licenseDir.exists()) {
			FileManager.getInstance().mkdirs(LicensePath);
			configLicense();
		} else {
			boolean isLicenseExists = FileManager.getInstance().isFileExist(LicensePath + LicenseName);
			if (isLicenseExists == false) {
				configLicense();
			}
		}

		if (!mapDataDir.exists()) {
			FileManager.getInstance().mkdirs(MapDataPath);
			configMapData();
		} else {

			 boolean isWorkspaceFileExists =
			 FileManager.getInstance().isFileExist(MapDataPath + DefaultServer);
			
			 if(isWorkspaceFileExists == false)
			 {
				configMapData();
			 }
		}
	}

	/**
	 * 配置许可文件
	 */
	private void configLicense() {
		File dirFile = new File(LicensePath);
		File [] files = dirFile.listFiles();
		for (int i=0; i<files.length; i++) {
			if(files[i].getName().endsWith(".slm"))    // 无论原有许可是否有效，只有存在许可文件就不拷贝许可，以免影响原有许可
				return;
		}
		InputStream is = MyAssetManager.getInstance().open(LicenseName);
		if (is != null) {
			FileManager.getInstance().copy(is, LicensePath + LicenseName);
		}
	}

	/**
	 * 配置地图数据
	 */
	private void configMapData() {

		String[] datas = MyAssetManager.getInstance().openDir(DataDir);
		for (String data : datas) {
			
			InputStream is = MyAssetManager.getInstance().open( DataDir + "/" + data); // data is a zip file under DataDir
			if (is != null) {
				String zip = MapDataPath + "/" + data;

				boolean result = FileManager.getInstance().copy(is, zip);
				if (result)
					// Decompressor.UnZipFolder(zip, MapDataPath);
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

				File ziFile = new File(zip);
				ziFile.delete();
			}
		}
	}

}
