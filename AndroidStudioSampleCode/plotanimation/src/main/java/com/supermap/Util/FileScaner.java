package com.supermap.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/20.
 */
public class FileScaner {

    private final String TAG="FileScaner";

    public FileScaner(){

    }

    public List<Map<String, String>> getPictureList(File file){
        if(null == file)
            return  null;
        if(!file.exists()){
            return null;
        }
       List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
        getPictureList(file, fileList);
        
        return fileList;
    }

    private void getPictureList(File path, List<Map<String, String>> fileList){
        if(path.isDirectory()){
            File[] files = path.listFiles();
            if(null == files){
                return ;
            }

            for (int i = 0; i<files.length; i++){
                getPictureList(files[i], fileList);
            }
        } else {
            String filePath = path.getAbsolutePath();
            String fileName = filePath.substring(filePath.lastIndexOf("/") +1);
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", fileName.substring(0, fileName.indexOf(".")));
            map.put("path", filePath);
            fileList.add(map);
        }
    }

	public List<? extends Map<String, String>> getPictureList(String path) {
		File file = new File(path);
		
		return getPictureList(file);
	}

	public static void deleteFiles(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files == null || files.length == 0) {
				file.delete();
				return;
			}
			for (File file2 : files) {
				deleteFiles(file2);
			}
			file.delete();
		}

	}
}
