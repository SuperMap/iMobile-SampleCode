package com.supermap.mqdemo.mqdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.demo.mqdemo.R;
import com.supermap.plot.GeoGraphicObject;
import com.supermap.data.Geometry;
import com.supermap.data.Recordset;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;

public class PlotTypePopup extends PopupWindow implements OnClickListener {

	private MapControl      m_MapControl       = null;

	private LayoutInflater  m_LayoutInflater   = null;
	private View            m_ContentView      = null;
	private MainActivity    m_MainActivity     = null;
	private PlotSymbolPopup m_PlotSymbolPopup  = null;

	public int m_queryType = 0;//0代表当前位置查询，1代表选中查询
	public Object m_QueryObject = null;

	private View m_MainView = null;

	public PlotTypePopup (MapControl mapControl, View mainView, MainActivity activity){
		super(activity);

		m_LayoutInflater = LayoutInflater.from(mapControl.getContext());
		m_MapControl     = mapControl;
		m_MainActivity   = activity;
		m_MainView		 = mainView;

		initView();

//		//响应返回键操作
		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		this.setFocusable(false);
	}

	private void initView() {
		m_ContentView = m_LayoutInflater.inflate(R.layout.plot_bar, null);

		setContentView(m_ContentView);

		((Button) m_ContentView.findViewById(R.id.btn_plot)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_line)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_region)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_scrawl)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_arrow)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btn_clear)).setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
			case R.id.btn_plot:
				m_MapControl.setAction(Action.CREATEPLOT);

				String path = DefaultDataConfiguration.MapDataPath + "SymbolIcon/点标号/";

				File file = new File(path);

				m_SymbolBmps.clear();
				m_SymbolIDs.clear();
				getFileBitmaps(file);

				if(m_PlotSymbolPopup == null){
					m_PlotSymbolPopup = new PlotSymbolPopup(m_MapControl, m_SymbolBmps, m_SymbolIDs, m_MainActivity.getLibIDJB(), m_MainActivity);
				}
				else{
					m_PlotSymbolPopup.resetView(m_SymbolBmps, m_SymbolIDs, m_MainActivity.getLibIDJB());
				}
				m_PlotSymbolPopup.show();

				break;
			case R.id.btn_line:

				m_MapControl.setAction(Action.DRAWLINE);
				break;
			case R.id.btn_region:
				m_MapControl.setAction(Action.DRAWPLOYGON);
				break;
			case R.id.btn_scrawl:
				m_MapControl.setAction(Action.FREEDRAW);
				break;
			case R.id.btn_arrow:
				m_MapControl.setAction(Action.CREATEPLOT);

				String path2 = DefaultDataConfiguration.MapDataPath + "SymbolIcon/箭头标号/";

				File file2 = new File(path2);

				m_SymbolBmps.clear();
				m_SymbolIDs.clear();
				getFileBitmaps(file2);

				if(m_PlotSymbolPopup == null){
					m_PlotSymbolPopup = new PlotSymbolPopup(m_MapControl, m_SymbolBmps, m_SymbolIDs, (int)m_MainActivity.getLibIDTY(), m_MainActivity);
				}
				else{
					m_PlotSymbolPopup.resetView(m_SymbolBmps, m_SymbolIDs, (int)m_MainActivity.getLibIDTY());
				}
				m_PlotSymbolPopup.show();

				break;
			case R.id.btn_clear:
				//获取当前编辑图层
				Layer layer = m_MapControl.getMap().getLayers().get(0);
				//由图层获取关联数据集的记录集
				Recordset rc = ((DatasetVector) layer.getDataset()).getRecordset(false, CursorType.DYNAMIC);
				//编辑删除操作
				rc.deleteAll();
				//更新记录集
				rc.update();
				//刷新地图显示操作结果
				m_MapControl.getMap().refresh();
				m_MainActivity.clearLists();

				break;
			default:
				break;
		}
		this.dismiss();
	}

	private java.util.Map<String, Bitmap> m_SymbolBmps = new HashMap<String, Bitmap>();
	private ArrayList<String> m_SymbolIDs = new ArrayList<String>();
	//	ArrayList<Bitmap> m_SymbolBmps = new ArrayList<Bitmap>();
	private boolean getFileBitmaps(File file){

		if(file.exists())
		{
			if (file.isDirectory()) {
				File[] fileList = file.listFiles();
				for (File f : fileList) {
					getFileBitmaps(f);
				}
			} else {
				//是存在的文件才会转换到流
				if(file.isFile()){
					try{
						InputStream is = new FileInputStream(file);
						//编码输入流得到图片位图
						Bitmap bmp = BitmapFactory.decodeStream(is);

						if(bmp != null){
							//获取这张图片的文件名称
							String strBmpFullName = file.getName();
							String[] strBmpArrayName = strBmpFullName.split("\\.");

							if(strBmpArrayName.length != 2)
								return false;

							//获得真实的标绘符号的编码
							String strBmpName = strBmpArrayName[0];

							//添加这张图片到内存映射表中
							m_SymbolBmps.put(strBmpName, bmp);
							m_SymbolIDs.add(strBmpName);

//		        			m_SymbolBmps.add(bmp);
//		        			String bmpCodePath = file.getPath();
//		        			String strBmpName = bmpCodePath.substring(bmpCodePath.length() - bmpCodePath.lastIndexOf("/"));

							return true;
						}
					}
					catch(Exception e){
					}
				}
			}

			return false;
		}

		return false;
	}

	Recordset m_GeoMesRecordset = null;
	/**
	 * 接收到的串添加到数据库并关联地图显示
	 * @Parma String
	 */
	private boolean addRecivedGeometry(String geoMsg){
		if(m_GeoMesRecordset == null){
			return false;
		}

		//构造接收消息内容为几何对象
		GeoGraphicObject geo = new GeoGraphicObject();
		geo.fromXML(geoMsg);

		//添加记录到当前接收记录集
		boolean bAdd = m_GeoMesRecordset.addNew(geo);
		m_GeoMesRecordset.update();

		if (bAdd) {
			m_MapControl.getMap().refresh();

			return true;
		} else {
			MyApplication.getInstance().showError("添加记录失败");
			return false;
		}
	}

	/**
	 * 获取用于网络传输的XML串,只支持一个对象
	 * @Parma geometry
	 */
	private String getSendGeoXML(Geometry geometry){
		if(geometry == null){
			return null;
		}

		String geoXML = geometry.toXML();
		if(geoXML == null){
			return null;
		}

		//获得一个通过StringBuilder构造的字符串
		String geoBuilderXML = new StringBuilder(geoXML).toString();

		return geoBuilderXML;
	}

	/**
	 * 获取当前正在编辑的几何对象,便于随时发送消息
	 */
	private Geometry getCurrentGeoMetry(){
		Geometry geo = m_MapControl.getCurrentGeometry();

		if(geo != null){
			return geo;
		}

		return null;
	}

	/**
	 * 获取网络传输的xml串,支持多个对象同时传输,编码和解析待定
	 */
	private String getSendXML(){
		return null;
	}

	/**
	 * 添加从网络获取的xml串,支持多个对象同时传输,编码和解析待定
	 */
	private boolean addRecivedXML(String geoXMLs){
		return false;
	}

	/**
	 * 显示
	 */
	public void show(View parent){
		DisplayMetrics dm = m_MapControl.getContext().getResources().getDisplayMetrics();
//		showAt(0, 0, 400 * (int)dm.density, 90 * (int)dm.density);
		setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

		Rect outRect = new Rect();
		m_MainActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);

		showAtLocation(m_MainView, Gravity.LEFT | Gravity.TOP, 8, (int)((120 * dm.density)/2)+ 10 + outRect.top);
	}

	private void showAt(int x,int y, int width, int height)
	{
		this.setFocusable(false);
		DisplayMetrics dm = m_MapControl.getContext().getResources().getDisplayMetrics();
		setWidth(width);
		setHeight(height);
		showAtLocation(m_MapControl.getRootView(), Gravity.CENTER|Gravity.CENTER/*Gravity.LEFT|Gravity.TOP*/, (int)dm.density*x, (int)dm.density*y);
	}

	public void dismiss(){
		super.dismiss();
		this.setFocusable(false);
	}
}