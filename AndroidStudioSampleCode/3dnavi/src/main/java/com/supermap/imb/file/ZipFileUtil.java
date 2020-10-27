package com.supermap.imb.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipFileUtil {

	public ZipFileUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void unZipFile(String archive, String decompressDir) throws IOException, FileNotFoundException, ZipException{
		BufferedInputStream bufIn = null;
		ZipFile zipFile = new ZipFile(archive, "GBK");
		Enumeration<?> enumeration = zipFile.getEntries();
		
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
			String entryName = zipEntry.getName();
			String entryPath = decompressDir +"/" + entryName;
			if(zipEntry.isDirectory()){
				System.out.println("Decompressing directory -- " + entryName);
				File decompDirFile = new File(entryPath);
				if(!decompDirFile.exists()){
					decompDirFile.mkdirs();
				}
			}else {
				System.out.println("Decompressing file -- " + entryName);
				String fileDir = entryPath.substring(0, entryPath.lastIndexOf("/"));
				File fileDirFile = new File(fileDir);
				if(!fileDirFile.exists()){
					fileDirFile.mkdirs();
				}
				BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
				bufIn = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				byte[] readBuf = new byte[2048];
				int readCount = bufIn.read(readBuf);
				while (readCount != -1){
					bufOut.write(readBuf, 0, readCount);
					readCount = bufIn.read(readBuf);
				}
				bufOut.close();
			}
		}
		zipFile.close();
	}
}
