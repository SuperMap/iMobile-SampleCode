package com.supermap.onlinedemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.adapter.TrafficTransferAdapter;
import com.supermap.data.Point2D;
import com.supermap.instance.TrafficTransfer;
import com.supermap.myapplication.MyApplication;
import com.supermap.onlineservices.CoordinateType;
import com.supermap.onlineservices.LineItem;
import com.supermap.onlineservices.LineItems;
import com.supermap.onlineservices.SolutionItem;
import com.supermap.onlineservices.TrafficTransferOnline;
import com.supermap.onlineservices.TrafficTransferOnline.TransferCallback;
import com.supermap.onlineservices.TrafficTransferOnlineData;
import com.supermap.onlineservices.TrafficTransferParameter;

public class TrafficInfoActivity extends Activity implements OnItemClickListener{
	
	private List<TrafficTransfer> trafficTransferList=new ArrayList<TrafficTransfer>();
	private Intent intent;
	
	private boolean trafficTransferFlag=false;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		设置为无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_traffic_info);
//		得到当前活动的意图
		intent=getIntent();
//		公交换乘
		trafficTransfer();
		final ProgressDialog dialog=new ProgressDialog(this);
		dialog.setMessage("加载中...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				runOnUiThread(new Runnable(){
					public void run(){
						if(trafficTransferFlag){
							dialog.dismiss();
						}else{
							Toast.makeText(TrafficInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
							dialog.dismiss();
							finish();
						}
					}
				});
			}
		}).start();
		dialog.show();
	}
//	公交换乘
	private void trafficTransfer(){
		if(intent == null){
			intent=getIntent();
		}
		double startX=intent.getDoubleExtra("startX", 0);
		double startY=intent.getDoubleExtra("startY", 0);
		double endX=intent.getDoubleExtra("endX", 0);
		double endY=intent.getDoubleExtra("endY", 0);
		String startName=intent.getStringExtra("startName");
		String endName=intent.getStringExtra("endName");
		
		TrafficTransferParameter ttParameter=new TrafficTransferParameter();
//		设置目标坐标类型
		ttParameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//		设置起点名称
		ttParameter.setStartName(startName);
//		设置终点名称
		ttParameter.setDestinationName(endName);
//		设置查询城市
		ttParameter.setQueryCity("成都");
//		设置交通类型
		ttParameter.setTrafficType(1);
//		设置返回的结果数
		ttParameter.setResultCount(3);
		
		TrafficTransferOnline trafficTransferOnline=new TrafficTransferOnline();
//		必须调用
//		设置钥匙
		trafficTransferOnline.setKey(MyApplication.KEY);
//		设置回调，用于判断交通转换是否成功
		trafficTransferOnline.setTransferCallback(new TransferCallback() {
			@Override
			public void transferSuccess(TrafficTransferOnlineData data) {
				trafficTransferFlag=true;
//				交通方案路线信息
				solutionItemsInfo(data);
			}

			@Override
			public void transferFailed(String errorInfo) {
				Log.e("TrafficTransfer", errorInfo);
			}
		});
		Point2D startPoint=new Point2D(startX, startY);
		Point2D destPoint=new Point2D(endX, endY);
//		进行交通转换
		trafficTransferOnline.trafficTransfer(startPoint, destPoint, ttParameter);
	}

	private void solutionItemsInfo(TrafficTransferOnlineData data){
		if(data == null){
			Toast.makeText(this, "trafficTransferOnlineData==null", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		TextView tvStartName=(TextView) findViewById(R.id.tvStartName_ati);
		TextView tveNDName=(TextView) findViewById(R.id.tvEndName_ati);
		
//		获取TrafficTransferOnlineData中有多少个SolutionItem
		List<SolutionItem> solutionItems=data.getSolutionItems();
		int solutionItemCount=solutionItems.size();
		for(int i=0;i<solutionItemCount;i++){
			SolutionItem solutionItem=solutionItems.get(i);
			//设置起点名字
			tvStartName.setText(solutionItem.getStartInfo().getName());
			//设置终点名字
			tveNDName.setText(solutionItem.getDestinationInfo().getName());
			
			List<LineItems> lineItemsList=solutionItem.getLinesItems();
			//获取有多少个lineItems方案	
			int lineItemsCount=lineItemsList.size();
			//存储当前分段乘坐的公交（地铁）线路名称
			List<String> lineNameList=new ArrayList<String>();
			//记录转换次数
			List<Double> transferCountList=new ArrayList<Double>();
			int count=0;
			for(int a=0; a<lineItemsCount; a++){
				List<LineItem> lineItemList=lineItemsList.get(a).getLineItems();
//				获取有多少个lineItem方案
				int lineItemCount=lineItemList.size();
				for(int b=0;b<lineItemCount;b++){
//					获取具体的LineItem
					LineItem lineItem=lineItemList.get(b);
//					获取当前线路行走的距离
					Double walkDistance=Double.valueOf(lineItem.getWalkDistance());
//					获取当前线路的名称
					String strLineName=lineItem.getLineName();
					if(a>0){
//						当a>0时，先获取原先线路方案的个数
						if(b==0){
							count=lineNameList.size();
						}
//						在原线路方案上添加新的路线消息，得到新的线路方案
						for(int c=0;c<count;c++){
							lineNameList.add(lineNameList.get(c)+"|"+strLineName);
							double number=transferCountList.get(c).doubleValue()+walkDistance.doubleValue();
							transferCountList.add(Double.valueOf(number));
						}
//						去掉原先的线路方案，得到最后的线路方案
						if(b+1 == lineItemCount){
							for(int d=0;d<count;d++){
								lineNameList.remove(0);
								transferCountList.remove(0);
							}
						}
					}else{
//						当a=0时，添加线路方案
						lineNameList.add(strLineName);
						transferCountList.add(walkDistance);
					}
				}
				
			}
			for(int f=0;f<lineNameList.size();f++){
//				获取从起点到公交站的步行距离
				double startWalkDistance=solutionItem.getStartInfo().getWalkDistance();
//				获取从公交站步行到终点的距离
				double endWalkDistance=solutionItem.getDestinationInfo().getWalkDistance();
				
				TrafficTransfer trafficTransfer=new TrafficTransfer();
				trafficTransfer.setWalkDistance(startWalkDistance+endWalkDistance+
						transferCountList.get(f).doubleValue());
				trafficTransfer.setTrafficInfo(lineNameList.get(f));
				trafficTransfer.setTransferCount(solutionItem.getTransferCount());
				trafficTransferList.add(trafficTransfer);
			}

		}
		
		LinearLayout llListView=(LinearLayout) findViewById(R.id.llListView_ati);
		ListView listView=(ListView)llListView.findViewById(R.id.listView_ltt);
		TrafficTransferAdapter ttApater=new TrafficTransferAdapter(trafficTransferList, this);
		listView.setAdapter(ttApater);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent backIntent=new Intent();
		backIntent.putExtra("position", position);
//		返回数据到上一个活动
		setResult(RESULT_OK, backIntent);
		finish();
	}

}
