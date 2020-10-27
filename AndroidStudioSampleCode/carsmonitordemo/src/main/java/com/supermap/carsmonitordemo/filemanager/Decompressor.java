package com.supermap.carsmonitordemo.filemanager;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Decompressor {

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


		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

}
