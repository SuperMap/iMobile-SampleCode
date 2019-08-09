package com.supermap.android.file;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import android.R.integer;

/*
 * Java util's Zip tool
 * 
 * @author once
 * 
 */

public class ZipUtils {
	private static final int BUFF_SIZE = 1024 * 1024;   // 1MB buffer
	
	/*
	 * Compress files or directories
	 * 
	 * @param:  resFileList;  files need to be compressed
	 * @param:  zipFile;      compressed file
	 * @throws: IOException;  when compressing
	 * 
	 */
	public static void zipFile (Collection<File> resFileLIst, File zipFile) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
		
		for (File resFile : resFileLIst) {
			zipFile(resFile, zipout, "");
		}
		zipout.close();
	}
	
	/*
	 * Compress files or directories
	 * 
	 * @param:  resFileList;  files need to be compressed
	 * @param:  zipFile;      compressed file
	 * @param:  comment;      comment of zipfile
	 * @throws: IOException;  when compressing
	 * 
	 */
	public static void zipFile (Collection<File> resFileLIst, File zipFile, String comment) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
		
		for (File resFile : resFileLIst) {
			zipFile(resFile, zipout, "");
		}
		zipout.setComment(comment);
		zipout.close();
	}
	
	/*
	 *Uncompress files or directories
	 *
	 *@param:  zipFile;        file needs to be uncompressed
	 *@param:  foldePath;      storage path for uncompressed files
	 *@throws: ZipException;   form error
	 *@throws: IOException;    IO error
	 */
	public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
		File desDir = new File(folderPath);
		if(!desDir.exists()) {
			desDir.mkdirs();
		}
		
		ZipFile zf = new ZipFile(zipFile);
		for(Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			InputStream in = zf.getInputStream(entry);
			String str = folderPath + File.separator + entry.getName();
			str = new String(str.getBytes("8859_1"), "GB2312");
			File desFile = new File(str);
			if(!desFile.exists()) {
				File fileParentDir = desFile.getParentFile();
				if(!fileParentDir.exists()) {
					fileParentDir.mkdirs();
				}
				desFile.createNewFile();
			}
			
			OutputStream out = new FileOutputStream(desFile);
			byte buffer[] = new byte[BUFF_SIZE];
			int realLength;
			while ((realLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, realLength);
			}
			
			in.close();
			out.close();
		}
	}
	
	/*
	 * Uncompress files whose name contains input words
	 * 
	 * @param:  zipFile;        file needs to be uncompressed
	 * @param:  folderPath;     storage path of uncompressed file
	 * @param:  nameContains;   input filename which should be matched
	 * @throws: ZipException;   form error
	 * @throws: IOException;    IO error
	 * 
	 */
	public static ArrayList<File> upZipSelectedFile(File zipFile, String folderPath, String nameContains) throws ZipException, IOException {
	
		ArrayList<File> fileList = new ArrayList<File>();
		
		File desDir = new File(folderPath);
		if(!desDir.exists()){
			desDir.exists();
		}
		
		ZipFile zf = new ZipFile(zipFile);
		for(Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			if(entry.getName().contains(nameContains)) {
				InputStream in = zf.getInputStream(entry);
				String str = folderPath  + File.separator + entry.getName();
				str = new String(str.getBytes("8859_1"), "GB2312");
				File desFile = new File(str);
				if(!desFile.exists()) {
					File fileParentDir = desFile.getParentFile();
					if(!fileParentDir.exists()) {
						fileParentDir.mkdirs();
					}
					desFile.createNewFile();
				}
				
				OutputStream out = new FileOutputStream(desFile);
				byte buffer[] = new byte[BUFF_SIZE];
				int realLength;
				while ((realLength = in.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
				}
				
				in.close();
				out.close();
				fileList.add(desFile);
			}
		}
		
		return fileList;
	}
	
	/*
	 *Get compressed file's Enumeration Object for compressed file's entries
	 *
	 * @param:    zipFile;              compressed file
	 * @throws:   ZipException;         form error
	 * @throws:   IOException;          IO error
	 * @return:   Enumeration Object;   compressed file's entries
	 * 
	 */
	public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException, IOException {
		ZipFile zf = new ZipFile(zipFile);
		return zf.entries();
	} 
	
	/*
	 * Get comment of compressed file
	 * 
	 * @param:    entry;   compressed file Object
	 * @return:   String comment;  compressed file's comment
	 * @throws:   UnsupportedEncodingException
	 * 
	 */
	public static String getEntryComment(ZipEntry entry) throws UnsupportedEncodingException {
		String comment = new String(entry.getComment().getBytes("GB2312"), "8859_1");
		return comment;
	}
	
	/*
	 * Get compressed file's name
	 * 
	 * @param:    entry;  compressed file Object
	 * @throws:   UnsupportedEncodingException
	 * @return:   String fileName;   compressed file's name
	 * 
	 */
	public static String getEntryName(ZipEntry entry) throws UnsupportedEncodingException {
		String name = new String(entry.getName().getBytes("GB2312"), "8859_1");
		return name;
	}
	
	
	/*
	 * Compress Function : compress a file
	 * 
	 * @param:  resFile;      the file need to be compress
	 * @param:  zipout;       compressed file
	 * @param:  rootpath;     storage path of compressed file
	 * @throws: FileNotFoundException, IOExcecption;   when compressing
	 * 
	 * 
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath) throws FileNotFoundException, IOException {
		rootpath = rootpath + ((rootpath.trim().length() == 0) ? "" : File.separator) + resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		}else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
		
	}
	
	
	
}