package com.supermap.carsmonitordemo.communication;

import java.util.ArrayList;

public class NaviDataManager {

	private int m_Cursor = 0; //游标

	private boolean isAdvance = true; //是否向前读取

	private ArrayList<CarData> m_arrryCarData;	//记录导航数据

	/**
	 * 构造函数
	 */
	public NaviDataManager() {
		m_arrryCarData = new ArrayList<CarData>();
	}

	/**
	 * 初始化导航数据
	 * @param carNo     车辆牌照
	 * @param dataPath 	轨迹文件
	 */
	public void init(String carNo, String dataPath) {
		ArrayList<String> arrryString = new ArrayList<String>();	//记录每行数据
		OperateFile.readFile(dataPath, arrryString);

		String strPhoneNo = createPhoneNo();
		String strCarName = createCarName();
		for (int i = 0; i < arrryString.size(); i++) {
			ArrayList<Double> arrryDouble = new ArrayList<Double>();	//记录每行数据

			OperateFile.resolveData(arrryString.get(i), arrryDouble);
			// 设置车辆信息
			CarData data = new CarData();
			data.setCarName(strCarName);
			data.setCarNo(carNo);
			data.setPhoneNo(strPhoneNo);
			data.setX(arrryDouble.get(0));
			data.setY(arrryDouble.get(1));

			m_arrryCarData.add(data);
		}
		int random = (int) (Math.random()*m_arrryCarData.size());
		m_Cursor = random;
	}

	/**
	 * 创建电话号码
	 * @return
	 */
	private String createPhoneNo(){
		StringBuilder sb = new StringBuilder();
		sb.append("186");

		//8个数字
		for(int i=0;i<8;i++){
			int value = (int) (Math.random()*9);
			sb.append(value);
		}

		return sb.toString();
	}

	/**
	 * 创建车辆名称
	 * @return   返回车辆名称字符串
	 */
	private String createCarName(){
		String carName = "未知车型";
		int value = (int) (Math.random()*10);
		//8个数字
		switch (value){
			case 0:
				carName = "拉博基尼";
				break;
			case 1:
				carName = "奥迪";
				break;
			case 2:
				carName = "大众";
				break;
			case 3:
				carName = "凯迪拉克";
				break;
			case 4:
				carName = "雪佛兰";
				break;
			case 5:
				carName = "悍马";
				break;
			case 6:
				carName = "法拉利";
				break;
			default:
				carName = "未知车型";
				break;
		}

		return carName;
	}

	public int getSize() {
		return m_arrryCarData.size();
	}

	/**
	 * 读取一条数据，正向读完转反向读取
	 * @return
	 */
	public CarData getData() {

		if (m_arrryCarData.size() < 1) {
			return null;
		}

		if (m_arrryCarData.size() == 1) {
			return m_arrryCarData.get(0);
		}

		// 改变读取方向
		if (m_Cursor == m_arrryCarData.size()-1) {
			isAdvance = false;
		} else if (m_Cursor == 0) {
			isAdvance = true;
		}

		if (isAdvance) {
			return m_arrryCarData.get(m_Cursor++);
		} else {
			return m_arrryCarData.get(m_Cursor--);
		}
	}

}