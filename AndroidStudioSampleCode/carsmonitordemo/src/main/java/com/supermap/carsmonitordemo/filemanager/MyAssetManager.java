package com.supermap.carsmonitordemo.filemanager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.AssetManager;


public class MyAssetManager {
	private static WeakReference<AssetManager> sAssetManager= null;
	
	private static Boolean sInited = false;
	private static MyAssetManager sInstance = null;
	
	private MyAssetManager ()
	{
		
	}
	
	public static void init (Context context)
	{
		if (!sInited)
		{
			AssetManager assets = context.getAssets();
			sAssetManager = new WeakReference<AssetManager> (assets);
			sInstance = new MyAssetManager();
			sInited = true;
		}
	}
	
	public static MyAssetManager getInstance ()
	{
		if (!sInited)
		{
			throw new IllegalArgumentException("Please call init() firstly");
		}
		
		return sInstance;
	}
	
	public InputStream open (String fileName)
	{
		if (sInited)
		{
			try{
				return sAssetManager.get().open(fileName, AssetManager.ACCESS_STREAMING);
			}catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/*
	 * Get all files in the directory
	 * 
	 * @param:   dir;        directory
	 * @return:  String[];   file list
	 * 
	 */
	public String[] openDir (String dir)
	{
		if (sInited)
		{
			try{
				return sAssetManager.get().list(dir);
			}catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
}

















