package com.supermap.datavisualizationdemo.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileManager {
	private static FileManager sInstance = null;
	
	private FileManager()
	{
		
	}
	
	public boolean isFileExsit(String name){
		File file = new File(name);
		if(file.isFile() && file.exists()){
			return true;
		}
		return false;
	}
	public boolean isDirExsit(String dir){
		File file = new File(dir);
		if(file.isDirectory() && file.exists()){
			return true;
		}
		return false;
	}
	
	public static FileManager getInstance(){
		if(sInstance == null){
			sInstance = new FileManager();
		}
		return sInstance;
	}
	
	public boolean mkdirs(String path){
		File dir = new File(path);
		if(dir.isDirectory())
		{
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			return dir.exists();
		}
		return false;
	}
	
	public File[] opendir(String path){
		File dir = new File(path);
		if(dir.isDirectory())
		{
			return dir.listFiles(); 
		}
		return null;
	}
	
	public boolean copy(String from,String to)
	{
		File fromFile = new File(from);
		File toFile = new File(to);
		if(fromFile.isFile() && fromFile.exists())
		{
			try {
				FileInputStream fis = new FileInputStream(fromFile);
				return copyFile(fis, toFile, true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		
		return false;
	}
	
	public boolean copy(InputStream from ,String to)
	{
		File toFile = new File(to);
		return copyFile(from, toFile,true);
	}
	
	public boolean deleteFile(String file)
	{
		return deleteFile(new File(file));
	}
	
	public boolean deleteFile(File file)
	{
		if(file.isFile() && file.exists())
		{
			return file.delete();
		}
		return false;
	}
	
	/**
	 * ɾ���ļ��л�ɾ���ļ����µ��ļ�
	 * @param dir
	 * @return
	 */
	public boolean deleteDir(String dir)
	{
		return deleteDir(new File(dir));
	}
	
	public boolean deleteDir(File dir)
	{
		if(dir.exists() && dir.isDirectory())
		{
			return delete(dir);
		}
		return false;
	}
	
	/**
	 *
	 * @param file
	 * @return
	 */
	private boolean delete(File file)
	{
		if(file.exists() && file.isDirectory())
		{
			File[] files = file.listFiles();
			for(File f:files)
			{
				delete(f);
			}

			return file.delete();
			
		}else if(file.exists() && file.isFile())
		{
			return file.delete();
		}
		return false;
	}
	
	private boolean copyFile(InputStream src,File des,boolean rewrite){

    	if(!des.getParentFile().exists()){
    		des.getParentFile().mkdirs();
    	}
    	if(des.exists()){
    		if(rewrite){
    			des.delete();
    		}else{
    			return false;
    		}
    	}
    	
    	try{
    		InputStream fis = src;
    		FileOutputStream fos = new FileOutputStream(des);
    		//1kb
    		byte[] bytes = new byte[1024];
    		int readlength = -1;
    		while((readlength = fis.read(bytes))>0){
    			fos.write(bytes, 0, readlength);
    		}
    		fos.flush();
    		fos.close();
    		fis.close();
    	}catch(Exception e){
    		return false;
    	}
    	return true;
    }
}
