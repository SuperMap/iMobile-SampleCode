package com.supermap.mqdemo.mqdemo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.util.Log;

public class Decompressor {

	public static boolean isUnZiped = false;
	/**
	 * 解压一个压缩文档 到指定位置
	 * @param zipFileString 压缩包的名字
	 * @param outPathString 指定的路径
	 * @throws Exception
	 */
	public static void UnZipFolder(String zipFile, String targetDir){
		android.util.Log.v("XZip", "UnZipFolder(String, String)");
		java.util.zip.ZipInputStream inZip;
		try {

			inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFile));

			java.util.zip.ZipEntry zipEntry;
			String szName = "";

			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();

				if (zipEntry.isDirectory()) {

					java.io.File folder = new java.io.File(targetDir + java.io.File.separator + szName);
					folder.mkdirs();

				} else {

					java.io.File file = new java.io.File(targetDir + java.io.File.separator + szName);
					file.createNewFile();
					// get the output stream of the file
					java.io.FileOutputStream out = new java.io.FileOutputStream(file);
					int len;
					byte[] buffer = new byte[1024];
					while ((len = inZip.read(buffer)) != -1) {
						out.write(buffer, 0, len);
						out.flush();
					}
					out.close();
				}
			}
			inZip.close();

			isUnZiped = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 解压缩功能.
	 * 将zipFile文件解压到folderPath目录下.
	 * @throws Exception
	 */
	public int upZipFile(File zipFile, String folderPath) throws ZipException,IOException {
		//public static void upZipFile() throws Exception{
		ZipFile zfile=new ZipFile(zipFile);
		Enumeration zList=zfile.entries();
		ZipEntry ze=null;
		byte[] buf=new byte[1024];
		while(zList.hasMoreElements()){
			ze=(ZipEntry)zList.nextElement();
			if(ze.isDirectory()){
				System.out.println("ze.getName(): " + ze.getName());//Log.d("upZipFile", "ze.getName() = "+ze.getName());

				String dirstr = folderPath + ze.getName();
				//dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				System.out.println("str: " + dirstr);//Log.d("upZipFile", "str = "+dirstr);

				File f=new File(dirstr);
				f.mkdir();
				continue;
			}
			OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
			InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
			int readLen=0;
			while ((readLen=is.read(buf, 0, 1024))!=-1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		return 0;
	}
	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * @param baseDir 指定根目录
	 * @param absFileName 相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName){
		String[] dirs=absFileName.split("/");
		File ret=new File(baseDir);
		String substr = null;
		if(dirs.length>1){
			for (int i = 0; i < dirs.length-1;i++) {
				substr = dirs[i];
				try {
					//substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret=new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = "+ret);
			if(!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length-1];
			try {
				//substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = "+substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret=new File(ret, substr);
			Log.d("upZipFile", "2ret = "+ret);
			return ret;
		}
		return ret;
	}

}
