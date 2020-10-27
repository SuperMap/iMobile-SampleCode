package com.supermap.querydemo.appconfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;
import com.supermap.querydemo.file.FileManager;
import com.supermap.querydemo.file.MyAssetManager;
import com.supermap.querydemo.file.ZipUtils;

public class DefaultDataConfig {

	private final       String QueryData     = "Data";
	public static final String QueryDataPath = MyApplication.SDCARD+"SuperMap/Demos/QueryDemo/";
	public static String LicPath       = MyApplication.SDCARD+"SuperMap/License/";
	private final       String LicName       = "Trial License.slm";

	private static final String DefaultServer = "Changchun.smwu";

	public static String WorkspacePath = QueryDataPath+DefaultServer;


	public DefaultDataConfig()
	{

	}

	/**
	 * 配置数据
	 */
	public void autoConfig(){
		//如果有数据了则认为用户已经清理数据盘
		String mapDataPah = QueryDataPath;
		String license    = LicPath + LicName;

		File licenseFile = new File(license);
		if(!licenseFile.exists())
			configLic();


		File dir = new File(mapDataPah);
		if(!dir.exists()){
			FileManager.getInstance().mkdirs(mapDataPah);
			//configLic();
			if(!FileManager.getInstance().isFileExsit(mapDataPah+DefaultServer)){
				configMapData();
			}
		}else{
			//理论上用户不可能这么干，但是万一他root了呢？
			if(FileManager.getInstance().isFileExsit(mapDataPah+DefaultServer)){

				return;
			}
			boolean hasMapData = false;
			File[] datas = dir.listFiles();
			for(File data:datas){
				if(data.getName().endsWith("SMWU")||data.getName().endsWith("smwu")
						||data.getName().endsWith("SXWU")||data.getName().endsWith("sxwu"))
				{
					//如果默认的数据被删除，那就加载第一个工作空间
					WorkspacePath = data.getAbsolutePath();
					hasMapData = true;
					break;
				}
			}
			if(!hasMapData)
			{
				//configLic();
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
		if(is != null)
			FileManager.getInstance().copy(is, LicPath+LicName);
	}

	/**
	 * 配置地图数据
	 */
	private void configMapData(){
		String[] datas = MyAssetManager.getInstance().opendDir(QueryData);
		for(String data:datas){
			InputStream is = MyAssetManager.getInstance().open(QueryData+"/"+data);
			String zip = QueryDataPath+"/"+data;
			boolean result = FileManager.getInstance().copy(is, zip);
			if(result){
				try {
					File zipFile = new File(zip);
					ZipUtils.upZipFile(zipFile, QueryDataPath);
					//删除压缩包
					zipFile.delete();
				} catch (ZipException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
