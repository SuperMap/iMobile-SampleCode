package com.supermap.onlinedemo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.adapter.LineSolutionAdapter;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.instance.LineSolution;
import com.supermap.instance.TrafficTransfer;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;
import com.supermap.myapplication.MyApplication;
import com.supermap.onlineservices.CoordinateType;
import com.supermap.onlineservices.LineItem;
import com.supermap.onlineservices.LineItems;
import com.supermap.onlineservices.SolutionItem;
import com.supermap.onlineservices.TrafficTransferOnline;
import com.supermap.onlineservices.TrafficTransferOnline.TransferCallback;
import com.supermap.onlineservices.TrafficTransferOnlineData;
import com.supermap.onlineservices.TrafficTransferParameter;
/**
 * <p>
 * Title: 公交换乘
 * </p>
 *
 *	<p>
 *============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码 
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *	Description: 
 *		1、范例简介：设置相关地理坐标与站名，点击搜索，进行路线选择
 *		2、范例数据：地图引擎：SuperMapCloud
 *          地图数据：超图云地图数据 http://supermapcloud.com
 *          许可目录："/SuperMap/license/"
 *      3、关键类型/成员:
 *					TrafficTransferParameter.setCoordinateType()					方法
 * 					TrafficTransferParameter.setStartName()							方法
 * 					TrafficTransferParameter.setDestinationName()					方法
 * 					TrafficTransferParameter.setQueryCity()							方法
 * 					TrafficTransferParameter.setTrafficType()						方法
 * 					TrafficTransferParameter.setResultCount()						方法
 * 					
 * 					TrafficTransferOnline.setKey()									方法
 * 					TrafficTransferOnline.setTransferCallback()						方法
 * 					TrafficTransferOnline.trafficTransfer()							方法
 * 
 * 					SolutionItem.getStartInfo().getWalkDistance()					方法
 * 					SolutionItem.getDestinationInfo().getWalkDistance()				方法
 * 					SolutionItem.getTransferCount()									方法
 * 					SolutionItem.getStartInfo().getName()							方法
 * 					SolutionItem.getDestinationInfo().getName()						方法
 * 
 * 					LineItem.getStartStopName()										方法
 * 					LineItem.getEndStopName()										方法
 * 					LineItem.getLineDirection()										方法
 * 					LineItem.getPassStopCount()										方法
 * 					LineItem.getWalkDistance()										方法
 * 					LineItem.getLineName()											方法
 *     4、步骤：
 *     		(1) 设置起点、终点
 *     		(2) 点击搜索
 *     		(3) 跳转到新的界面，进行路线选择
 *     		(4) 选择后，可查看具体信息，点击具体信息，地图显示起点、终点的位置
 *     5、注意事项：
 *     		(1) 查找成功，在地图上未看到图标，请缩小地图
 *	</p>
 *	
 *	<p>
 *	Company: 北京超图软件股份有限公司
 *	</p>
 */
public class TrafficTransferActivity extends Activity implements OnClickListener,OnItemClickListener{
	private Workspace workspace;
	private Dataset dataset;
	private Datasource datasource;
	private MapView mapView;
	private MapControl mapControl;
	private TrackingLayer trackingLayer;
	
	private	double startX=-1;
	private	double startY=-1;
	private	double endX=-1;
	private	double endY=-1;
	private	String startName="";
	private	String endName="";
	private EditText etStartX,etStartY,etStartName,etEndX,etEndY,etEndName;
	private Button btnStart, btnEnd, btnSearch, btnIn, btnOut, btnClear,btnEntire;
	
	private List<LineSolution>  lineSolutionList=new ArrayList<LineSolution>();
	private List<TrafficTransfer> trafficTransferList=new ArrayList<TrafficTransfer>();
	private int backPosition=0;
	private List<Point2Ds> point2DsList=new ArrayList<Point2Ds>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		设置为无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_traffic_transfer);
//		判断打开地图是否成功
		if(openMap()){
			initView();
		}
		new Gesture(this, mapView);
	}

	private boolean openMap(){
		mapView=(MapView) findViewById(R.id.mapView_att);
		mapControl=mapView.getMapControl();
//		添加跟踪图层
		trackingLayer=mapControl.getMap().getTrackingLayer();
		
		workspace=new Workspace();
		DatasourceConnectionInfo dcInfo=new DatasourceConnectionInfo();
//		打开超图云地图
		dcInfo.setEngineType(EngineType.SuperMapCloud);
		dcInfo.setServer("http://t2.supermapcloud.com/");
//		给地图设置工作空间
		mapControl.getMap().setWorkspace(workspace);
		datasource=workspace.getDatasources().open(dcInfo);
		if(datasource != null){
			dataset=datasource.getDatasets().get(0);
//			添加图层
			mapControl.getMap().getLayers().add(dataset,true);
			//设置地图初始的显示范围，放地图出图时就显示的是成都
			mapControl.getMap().setScale(1/114746.09);
			mapControl.getMap().setCenter(new Point2D(11586694.1130399,3589094.2752003));
			mapControl.getMap().refresh();
			return true;
		}
		return false;
	}

	private void initView(){
		btnSearch = (Button) findViewById(R.id.btnSearch_att);
		btnStart = (Button) findViewById(R.id.btnStart_att);
		btnEnd = (Button) findViewById(R.id.btnEnd_att);
		btnIn = (Button) findViewById(R.id.btnIn_att);
		btnOut = (Button) findViewById(R.id.btnOut_att);
		btnClear = (Button) findViewById(R.id.btnClear_att);
		btnEntire = (Button) findViewById(R.id.btnEntire_att);
		
		btnSearch.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		btnEnd.setOnClickListener(this);
		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnEntire.setOnClickListener(this);
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(workspace != null){
			trackingLayer.clear();
			mapControl.getMap().close();
			workspace.dispose();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnStart_att:
			final LinearLayout startView=(LinearLayout) findViewById(R.id.include_att);
			startView.setVisibility(View.VISIBLE);

			etStartX=(EditText) startView.findViewById(R.id.etX_i);
			etStartX.setText("104.068480");

			etStartY=(EditText) startView.findViewById(R.id.etY_i);
			etStartY.setText("30.537340");

			TextView tvStartAlias=(TextView) startView.findViewById(R.id.tvAlias_i);
			tvStartAlias.setText("起点站名");

			etStartName=(EditText) startView.findViewById(R.id.etName_i);
			etStartName.setText("超图软件");

			Button btnFinish=(Button) startView.findViewById(R.id.btnFinish_i);
			btnFinish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startX=Double.parseDouble(etStartX.getText().toString());
					startY=Double.parseDouble(etStartY.getText().toString());
					startName=etStartName.getText().toString();
					if(startX <= 0|| startY <= 0 || startName == null || startName == ""){
						Toast.makeText(TrafficTransferActivity.this, "请设置正确的起点值", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(TrafficTransferActivity.this, "finish", Toast.LENGTH_SHORT).show();
					startView.setVisibility(View.INVISIBLE);
				}
			});

			break;
		case R.id.btnEnd_att:
			final LinearLayout endView=(LinearLayout) findViewById(R.id.include_att);
			endView.setVisibility(View.VISIBLE);

			etEndX=(EditText) endView.findViewById(R.id.etX_i);
			etEndX.setText("104.073324");

			etEndY=(EditText) endView.findViewById(R.id.etY_i);
			etEndY.setText("30.696837");

			TextView tvEndAlias=(TextView) endView.findViewById(R.id.tvAlias_i);
			tvEndAlias.setText("终点站名");

			etEndName=(EditText) endView.findViewById(R.id.etName_i);
			etEndName.setText("成都火车站");

			Button btnEndFinish=(Button) endView.findViewById(R.id.btnFinish_i);
			btnEndFinish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					endX=Double.parseDouble(etEndX.getText().toString());
					endY=Double.parseDouble(etEndY.getText().toString());
					endName=etEndName.getText().toString();
					if(endX <= 0 || endY <= 0 || endName == null || endName == ""){
						Toast.makeText(TrafficTransferActivity.this, "请设置正确的终点值", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(TrafficTransferActivity.this, "finish", Toast.LENGTH_SHORT).show();
					endView.setVisibility(View.INVISIBLE);
				}
			});
			break;
		case R.id.btnSearch_att:
			if(etStartX == null ){
				Toast.makeText(this, "请设置正确的起点", Toast.LENGTH_SHORT).show();
				return;
			}
			if(etEndX == null){
				Toast.makeText(this, "请设置正确的终点", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intentTrafficInfo=new Intent(TrafficTransferActivity.this,TrafficInfoActivity.class);
			intentTrafficInfo.putExtra("startX", startX);
			intentTrafficInfo.putExtra("startY", startY);
			intentTrafficInfo.putExtra("endX", endX);
			intentTrafficInfo.putExtra("endY", endY);
			intentTrafficInfo.putExtra("startName", startName);
			intentTrafficInfo.putExtra("endName", endName);
			startActivityForResult(intentTrafficInfo, 1);
			break;
		case R.id.btnIn_att:
			mapControl.getMap().zoom(2);
			mapControl.getMap().refresh();

			break;
		case R.id.btnOut_att:
			mapControl.getMap().zoom(0.5);
			mapControl.getMap().refresh();
			break;
		case R.id.btnClear_att:
			if(trackingLayer != null){
				trackingLayer.clear();
			}
			if(mapView != null){
				mapView.removeAllCallOut();
			}
			mapControl.getMap().refresh();
			break;
		case R.id.btnEntire_att:
			if (mapControl != null) {
				mapControl.getMap().viewEntire();
				mapControl.getMap().refresh();
			}
			break;
		default:
			break;
		}
	}

	private void trafficTransfer(){
		TrafficTransferOnline trafficTransferOnline=new TrafficTransferOnline();
		Point2D startPoint=new Point2D(startX, startY);
		Point2D destPoint=new Point2D(endX, endY);
		TrafficTransferParameter ttParameter=new TrafficTransferParameter();
//		设置坐标类型
		ttParameter.setCoordinateType(CoordinateType.NAVINFO_AMAP_MERCATOR);
//		设置起点名称
		ttParameter.setStartName(startName);
//		设置终点名称
		ttParameter.setDestinationName(endName);
//		设置公交换乘服务查询范围
		ttParameter.setQueryCity("成都");
//		设置公交换乘策略。0表示正常模式，1表示不走地铁。
		ttParameter.setTrafficType(1);
//		设置最大换乘方案个数
		ttParameter.setResultCount(3);
//		必须调用
//		设置钥匙
		trafficTransferOnline.setKey(MyApplication.KEY);
//		设置回调函数，检查公交查询是否成功
		trafficTransferOnline.setTransferCallback(new TransferCallback() {
			@Override
			public void transferSuccess(TrafficTransferOnlineData data) {
//				方案信息
				solutionItemsInfo(data,backPosition);
			}

			@Override
			public void transferFailed(String errorInfo) {
				Log.e("TrafficTransfer", errorInfo);
			}
		});
//		进行公交换乘
		trafficTransferOnline.trafficTransfer(startPoint, destPoint, ttParameter);
	}

	/**
	 * 方案信息
	 * @param data
	 * @param backPosition 该参数为查询返回后的位置
	 */
	private void solutionItemsInfo(TrafficTransferOnlineData data,int backPosition){
		if(data == null){
			Toast.makeText(this, "trafficTransferOnlineData==null", Toast.LENGTH_SHORT).show();
			return ;
		}
		Toast.makeText(TrafficTransferActivity.this, "Success ", Toast.LENGTH_SHORT).show();

		LinearLayout llPrarentListView=(LinearLayout) TrafficTransferActivity.this.findViewById(R.id.llParentListView_att);
		llPrarentListView.setVisibility(View.VISIBLE);
		TextView tvStartName=(TextView) llPrarentListView.findViewById(R.id.tvStartName_att);
		TextView tveNDName=(TextView) llPrarentListView.findViewById(R.id.tvEndName_att);

		List<SolutionItem> solutionItems=data.getSolutionItems();
		int solutionItemCount=solutionItems.size();
		for(int i=0;i<solutionItemCount;i++){
			SolutionItem solutionItem=solutionItems.get(i);
			//	设置起点名字
			tvStartName.setText(solutionItem.getStartInfo().getName());
			//	设置终点名字
			tveNDName.setText(solutionItem.getDestinationInfo().getName());

			List<LineItems> lineItemsList=solutionItem.getLinesItems();
			//	获取有多少个lineItems方案	
			int lineItemsCount=lineItemsList.size();
			//	存储线路方案
			List<String> lineNameList=new ArrayList<String>();
			////	记录转换次数
			//	List<Double> transferCountList=new ArrayList<Double>();
			//	记录每个线路转换的起点
			List<String> startNameList=new ArrayList<String>();
			//	记录每个线路转换的终点
			List<String> endNameList=new ArrayList<String>();
			//	记录每个线路转换的方向
			List<String> directionList=new ArrayList<String>();
			//	用字符串记录每个线路转换的站数
			List<String> passStopCountList=new ArrayList<String>();
			//	用字符串记录每个线路换站走的距离
			List<String> strWalkDistanceList=new ArrayList<String>();
			//	用于记录动态变化的线路方案总数
			int count=0;
			for(int a=0; a<lineItemsCount; a++){
				List<LineItem> lineItemList=lineItemsList.get(a).getLineItems();
				//	获取有多少个lineItem方案
				int lineItemCount=lineItemList.size();
				for(int b=0;b<lineItemCount;b++){
//					获取当前线路
					LineItem lineItem=lineItemList.get(b);
//					得到当前分段乘坐的公交（地铁）线路名称
					String strLineName=lineItem.getLineName();
//					得到当前分段起始公交（地铁）站名称
					String strStartStopName=lineItem.getStartStopName();
//					得到当前分段终点公交（地铁）站名称
					String strEndStopName=lineItem.getEndStopName();
//					得到分段线路的方向
					String strDirection=lineItem.getLineDirection();
//					得到总经历的站次数
					String strPassStopCount=String.valueOf(lineItem.getPassStopCount());
//					得到从起始点到公交（地铁）起点的步行距离
					String strWalkDistance=String.valueOf(lineItem.getWalkDistance());
					if(a>0){
						// 当a>0时，先获取原先线路方案的个数
						if(b==0){
							count=lineNameList.size();
						}
						//	在原线路方案上添加新的路线消息，得到新的线路方案
						for(int c=0;c<count;c++){
							lineNameList.add(lineNameList.get(c)+"|"+strLineName);
							startNameList.add(startNameList.get(c)+"|"+strStartStopName);
							endNameList.add(endNameList.get(c)+"|"+strEndStopName);
							directionList.add(directionList.get(c)+"|"+strDirection);
							passStopCountList.add(passStopCountList.get(c)+"|"+strPassStopCount);
							strWalkDistanceList.add(strWalkDistanceList.get(c)+"|"+strWalkDistance);
						}
						//	去掉原先的线路方案，得到最后的线路方案
						if(b+1 == lineItemCount){
							for(int d=0;d<count;d++){
								lineNameList.remove(0);
								startNameList.remove(0);
								endNameList.remove(0);
								directionList.remove(0);
								passStopCountList.remove(0);
								strWalkDistanceList.remove(0);
							}
						}
					}else{
						//	当a=0时，添加线路方案
						lineNameList.add(strLineName);
						startNameList.add(strStartStopName);
						endNameList.add(strEndStopName);
						directionList.add(strDirection);
						passStopCountList.add(strPassStopCount);
						strWalkDistanceList.add(strWalkDistance);
					}
				}

			}
			for(int f=0;f<lineNameList.size();f++){
				TrafficTransfer trafficTransfer=new TrafficTransfer();
				trafficTransfer.setTransferCount(solutionItem.getTransferCount());
				trafficTransferList.add(trafficTransfer);

				Point2Ds points=new Point2Ds();
//				添加起点地理坐标
				points.add(solutionItem.getStartInfo().getLocation());
//				添加终点地理坐标
				points.add(solutionItem.getDestinationInfo().getLocation());
				point2DsList.add(points);

			}
			for(int g=0;g<startNameList.size();g++){
//				进行分离字符串
				Pattern pattern=Pattern.compile("[|]");
				for(int h=0;h<pattern.split(startNameList.get(g)).length;h++){
					LineSolution lineSolution=new LineSolution();
					lineSolution.setStartName(pattern.split(startNameList.get(g))[h]);
					lineSolution.setEndName(pattern.split(endNameList.get(g))[h]);
					lineSolution.setDirection(pattern.split(directionList.get(g))[h]);
					lineSolution.setTrafficInfo(pattern.split(lineNameList.get(g))[h]);
					lineSolution.setPassStopCount(Integer.parseInt(pattern.split(passStopCountList.get(g))[h]));
					lineSolution.setWalkDistance(pattern.split(strWalkDistanceList.get(g))[h]);
					lineSolutionList.add(lineSolution);
				}

			}

		}
		TextView tvLocation=(TextView) findViewById(R.id.tvLocation_att);
		tvLocation.setVisibility(View.VISIBLE);

		LinearLayout llListView=(LinearLayout) llPrarentListView.findViewById(R.id.llListView_att);
		llListView.setVisibility(View.VISIBLE);
		ListView listView=(ListView) llListView.findViewById(R.id.listView_ltt);

		List<LineSolution> lineSolutions=new ArrayList<LineSolution>();
		int startSelectionPosition=0;
		for(int a=0;a<backPosition;a++){
			startSelectionPosition+=(trafficTransferList.get(a).getTransferCount()+1);
		}
		int endSelectionPosition=startSelectionPosition+trafficTransferList.get(backPosition).getTransferCount()+1;
		for(int j=startSelectionPosition;j<endSelectionPosition;j++){
			lineSolutions.add(lineSolutionList.get(j));
		}
		if(lineSolutions.size() != 0){
			LineSolutionAdapter adapter=new LineSolutionAdapter(this, lineSolutions);
			listView.setDividerHeight(2);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(TrafficTransferActivity.this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LinearLayout llPrarentListView=(LinearLayout) TrafficTransferActivity.this.findViewById(R.id.llParentListView_att);
		llPrarentListView.setVisibility(View.GONE);
		if(point2DsList.size()!=0){
			Point2Ds point2Ds=point2DsList.get(backPosition);
//			添加CallOut到地图上
			addCallOutOnMapView(point2Ds.getItem(0), R.drawable.start_point);
			addCallOutOnMapView(point2Ds.getItem(1), R.drawable.end_point);
		}
	}

	/**
	 * 在地图上添加CallOut对象
	 * @param point2Ds
	 * @param poiInfos
	 */
	@SuppressWarnings("deprecation")
	private void addCallOutOnMapView(Point2D point2D,int resourceId){
		CallOut callout = new CallOut(this);
		callout.setCustomize(true);
		final View showView = LayoutInflater.from(this).inflate(R.layout.callout, null);
		Button btnInfos = (Button) showView.findViewById(R.id.btnInfos_c);
		btnInfos.setBackground(getResources().getDrawable(resourceId));
		btnInfos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(TrafficTransferActivity.this, "popup", Toast.LENGTH_SHORT).show();
			}
		});
		callout.setLocation(point2D.getX(), point2D.getY());
		callout.setContentView(showView);
		callout.setStyle(CalloutAlignment.BOTTOM);
		mapView.addCallout(callout);
		mapControl.getMap().refresh();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(resultCode){
		case RESULT_OK:
			if(requestCode == 1){
				System.out.println("BackDataSuccess");
				backPosition=data.getIntExtra("position", 0);
				trafficTransfer();
			}
			break;
		}
	}

}
