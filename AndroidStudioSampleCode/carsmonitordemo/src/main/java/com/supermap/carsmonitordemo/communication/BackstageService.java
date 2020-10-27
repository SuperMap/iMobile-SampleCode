package com.supermap.carsmonitordemo.communication;

import java.io.File;
import java.util.ArrayList;



import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.supermap.carsmonitordemo.app.MyApplication;
import com.supermap.carsmonitordemo.carmonitor.MonitorActivity;
import com.supermap.carsmonitordemo.configuration.DefaultDataConfiguration;

/**
 * 后台服务
 * @author Congle
 *
 */
public class BackstageService extends Service{

	private ArrayList<NaviDataManager> m_NaviDataMgrs;

	private static int Counter = 0;
	private String rootPath = android.os.Environment.getExternalStorageDirectory().toString();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		initNaviData();                            //初始化轨迹数据
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(mRunnable);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		mHandler.postDelayed(mRunnable, 1000);
		return super.onStartCommand(intent, flags, startId);
	}

	Handler mHandler = new Handler();

	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(MonitorActivity.startMonitor)

				sendData();                                   //发送数据

			mHandler.postDelayed(mRunnable, 400);
		}
	};

	/**
	 * 发送车辆信息
	 */
	private void sendData(){

		Intent intent = new Intent();
		intent.setAction(MyApplication.BroadcastAction);
		intent.setPackage(getPackageName());
		intent.putParcelableArrayListExtra("cardata", getCarDatas());

		sendBroadcast(intent);
	}

	/**
	 * 初始化轨迹数据
	 */
	private void initNaviData() {
		File file = new File(DefaultDataConfiguration.MapDataPath + "/Track/");
		File[] files = file.listFiles();

		ArrayList<String> paths = new ArrayList<String>();

		// 获取所有字文件
		for (int i=0;i<files.length;i++){
			paths.add(files[i].getPath());
		}

		m_NaviDataMgrs = new ArrayList<NaviDataManager>();
		int pathsCount = paths.size();
		//for (int i = 0; i < pathsCount*3; i++) {
		for (int i = 0; i < pathsCount; i++) {
			NaviDataManager naviDataMgr = new NaviDataManager();
			naviDataMgr.init(createCarNo(),paths.get(i%pathsCount));

			m_NaviDataMgrs.add(naviDataMgr);
		}
	}

	/**
	 * 创建车牌号
	 * @return   返回车牌字符串
	 */
	private String createCarNo(){
		StringBuilder sb = new StringBuilder();
		sb.append("京");
		char c = '0';
		//第一个字母只能是 ABCEFGP0
		int value = (int) (Math.random()*8);
		switch (value) {
			case 0:
				c = 'A';
				break;
			case 1:
				c = 'B';
				break;
			case 2:
				c = 'C';
				break;
			case 3:
				c = 'E';
				break;
			case 4:
				c = 'F';
				break;
			case 5:
				c = 'G';
				break;
			case 6:
				c = 'P';
				break;
			case 7:
				c = '0';
				break;
			default:
				c = 'A';
				break;
		}
		sb.append(c);
		sb.append(' ');
		//5个数字
		for(int i=0;i<5;i++){
			c = '0';
			value = (int) (Math.random()*11);
			if(value<10){
				c += value;
			}else{
				c = 'B';
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 从文件取汽车数据
	 * @return
	 */
	private ArrayList<CarData> getCarDatas() {
		ArrayList<CarData> carDatas = new ArrayList<CarData>();

		for (int i = 0; i < m_NaviDataMgrs.size(); i++) {
			NaviDataManager navigationData = m_NaviDataMgrs.get(i);

			CarData data = navigationData.getData();
			if(Counter%10 == 0){
				int state = (int) (Math.random()*100);
				if(state>90){
					state = 3;
				}else if(state>80){
					state = 2;
				}else{
					state = 1;
				}
				data.setState(state);
			}

			if (data != null) {
				carDatas.add(data);
			}
		}

		Counter++;
		return carDatas;
	}
}
