package com.supermap.android.file;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
	/*
	 * mark that it is initialized or not
	 */
	private static boolean sInited = false;
	private static MySharedPreferences sInstace = null;
	
	/*
	 * SharedPreferences in System
	 */
	private SharedPreferences mSharedPreferences = null;
	private boolean isXMLOpened = false;
	private static WeakReference<Context> sContext = null;
	
	private MySharedPreferences () {
		
	}
	
	public static void init (Context context) 
	{
		if (!sInited)
		{
			sContext = new WeakReference<Context> (context);
			sInstace = new MySharedPreferences();
			sInited  = true;
		}
	}
	
	public static MySharedPreferences getInstance() 
	{
	    if(!sInited)
	    {
	    	throw new IllegalArgumentException("Please call init() firstly");
	    }
	    
	    return sInstace;
	}
	
	
	public void open (String xml)
	{
		mSharedPreferences = sContext.get().getSharedPreferences(xml, Context.MODE_PRIVATE);
		isXMLOpened = true;
	}
	
	public void put (String key, int value)
	{
		if (isXMLOpened)
		{
			mSharedPreferences.edit().putInt(key, value);
			mSharedPreferences.edit().commit();
		}
	}
	
	public void put (String key, Boolean value)
	{
		if (isXMLOpened)
		{
			mSharedPreferences.edit().putBoolean(key, value);
			mSharedPreferences.edit().commit();
		}
	}
	
	public void put (String key, String value)
	{
		if (isXMLOpened)
		{
			mSharedPreferences.edit().putString(key, value);
			mSharedPreferences.edit().commit();
		}
	}
	
	public void put (String key, float value)
	{
		if (isXMLOpened)
		{
			mSharedPreferences.edit().putFloat(key, value);
			mSharedPreferences.edit().commit();
		}
	}
	
	public void put (String key, long value)
	{
		if (isXMLOpened)
		{
			mSharedPreferences.edit().putLong(key, value);
			mSharedPreferences.edit().commit();
		}
	}
	
	public boolean getBoolean (String key)
	{
		if (isXMLOpened)
		{
			return mSharedPreferences.getBoolean(key, false);
		}
		
		return false;
	}
	
	public int getInt (String key)
	{
		if (isXMLOpened)
		{
			return mSharedPreferences.getInt(key, 0);
		}
		
		return 0;
	}
	
	public float getFloat (String key)
	{
		if (isXMLOpened)
		{
			return mSharedPreferences.getFloat(key, 0);
		}
		
		return 0;
	}
	
	public long getLong (String key)
	{
		if (isXMLOpened)
		{
			return mSharedPreferences.getLong(key, 0);
		}
		
		return 0;
	}
	
	public String getString (String key)
	{
		if (isXMLOpened)
		{
			return mSharedPreferences.getString(key, "");
		}
		
		return "";
	}
	
	public void clear () 
	{
		if (isXMLOpened)
		{
			mSharedPreferences.edit().clear();
			mSharedPreferences.edit().commit();
		}
	}
}














