package com.supermap.imobile.visualization;

import android.app.Application;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyApplication extends Application {
    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    @Override
    public void onCreate() {
        super.onCreate();
        initData();//数据准备

    }

    private void initData() {
        File mapDataDir = new File(SDCARD + "sampledata/ThematicMaps");
        if (!mapDataDir.exists()) {
            mapDataDir.mkdir();
        }

        try {
            CopyFiles("ThematicMaps", SDCARD + "sampledata/ThematicMaps");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean iscopy = false;

    private boolean CopyFiles(String oldPath, String newPath) throws IOException {
        AssetManager mAssetManger = this.getAssets();
        String[] fileNames = mAssetManger.list(oldPath);// 获取assets目录下的所有文件及有文件的目录名
        if (fileNames.length > 0) {//如果是目录,如果是具体文件则长度为0
            File file = new File(newPath);
            file.mkdirs();//如果文件夹不存在，则创建
            for (String fileName : fileNames) {
                if (oldPath == "")   //assets中的oldPath是相对路径，不能够以“/”开头
                    CopyFiles(fileName, newPath + "/" + fileName);
                else
                    CopyFiles(oldPath + "/" + fileName, newPath + "/" + fileName);
            }
        } else {//如果是文件
            File file = new File(newPath);
            if (file.exists()) {
                return false;
            } else {
                InputStream is = mAssetManger.open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }

        }
        return true;
    }
}
