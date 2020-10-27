package com.supermap.carsmonitordemo.communication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Environment;

public class OperateFile {

	/**
	 * 读取车辆轨迹数据
	 * @param filePath
	 * @param arrry
	 * @return
	 */
	static public boolean readFile(String filePath,ArrayList<String> arrry){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			//获取指定文件输出流
			try {
				FileInputStream fis = new FileInputStream(filePath);
				//将指定输出流包装成BufferedReader
				BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));

				String line = null;
				while ((line = buffer.readLine()) != null) {
					arrry.add(line);
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	static public void resolveData(String line, ArrayList<Double> arrryItem){
		int start = 0;
		int end = 0;
		while (true) {
			if ((end = line.indexOf("	", start)) != -1) {
				String subStr = line.substring(start, end);
				double data = Double.parseDouble(subStr);
				arrryItem.add(data);
				start = end+1;
			} else {
				//不读最后一个数据
				String subStr = line.substring(start);
				double data = Double.parseDouble(subStr);
				arrryItem.add(data);
				break;				//结束循环
			}
		}
	}
}
