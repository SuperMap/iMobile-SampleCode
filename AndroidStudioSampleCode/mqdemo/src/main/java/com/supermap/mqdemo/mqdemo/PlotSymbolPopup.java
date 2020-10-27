package com.supermap.mqdemo.mqdemo;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.supermap.data.CoordSysTranslator;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.GeoText;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.demo.mqdemo.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.RefreshListener;
import com.supermap.mapping.dyn.DynamicLine;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicPolygon;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicText;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.mapping.dyn.ZoomAnimator;

public class PlotSymbolPopup extends PopupWindow implements OnClickListener {

	private MapControl        m_MapControl     = null;
	private Layers            m_Layers         = null;
	private Map               m_Map            = null;

	private LayoutInflater    m_LayoutInflater = null;
	private View              m_ContentView    = null;
	private GridView          mResultGrid     = null;

	private DynamicView       m_DynamicView    = null;
	private MainActivity      m_MainActivity     = null;
	private java.util.Map<String, Bitmap> m_SymbolBmps     = null;
	private ArrayList<String> m_SymbolIDs      = null;
	private double            m_Density        = 0;
	private long               m_LibraryIndex   = 0;

	public PlotSymbolPopup (MapControl mapControl, java.util.Map<String, Bitmap> symbolBmps, ArrayList<String> symbolIDs, long libraryIndex, MainActivity activity){
		super(activity);

		m_LayoutInflater = LayoutInflater.from(mapControl.getContext());
		m_MapControl = mapControl;
		m_MainActivity = activity;
		m_SymbolBmps = symbolBmps;
		m_SymbolIDs = symbolIDs;
		m_LibraryIndex = libraryIndex;

		//获取上下文显示布局
		DisplayMetrics dm = m_MapControl.getContext().getResources().getDisplayMetrics();
		m_Density = dm.scaledDensity;

		initView();
		initSpotGrid();

		//响应返回键操作
		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}

	public void resetView(java.util.Map<String, Bitmap> symbolBmps, ArrayList<String> symbolIDs, long libraryIndex){

		m_SymbolBmps = symbolBmps;
		m_SymbolIDs = symbolIDs;
		m_LibraryIndex = libraryIndex;

//		initView();
		initSpotGrid();

	}

	private void initView() {
		// TODO Auto-generated method stub

		m_ContentView = m_LayoutInflater.inflate(R.layout.plot_symbol, null);
		setContentView(m_ContentView);

		mResultGrid = (GridView) m_ContentView.findViewById(R.id.GridView1);
	}

	private void initSpotGrid(){
		mResultGrid.setAdapter(new PictureAdapter());
		this.setOutsideTouchable(true);
	}

	//自定义适配器
	class PictureAdapter extends BaseAdapter{

		public PictureAdapter()
		{
		}

		@Override
		public int getCount()
		{
			return m_SymbolBmps.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolderPicture viewHolder = null;
			if (convertView == null){
				convertView = m_LayoutInflater.inflate(R.layout.symbol_item, null);

				viewHolder = new ViewHolderPicture();
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_picture_image);
				viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_picture_title);

				convertView.setTag(viewHolder);
			}
			else{
				viewHolder = (ViewHolderPicture)convertView.getTag();
			}
			try{
				if(m_SymbolBmps == null)
					return null;

				if(position < m_SymbolIDs.size() && position < m_SymbolBmps.size()){
					final String strBmpName = m_SymbolIDs.get(position);
					Bitmap bmp = m_SymbolBmps.get(strBmpName);

					if(bmp == null)
						return null;

					BitmapDrawable bmDrawable = new BitmapDrawable(bmp);

					//获取图片控件ImageView
					ImageView imageView = viewHolder.imageView;

//						//设置ImageView随分辨率的固定大小
//						LayoutParams params = imageView.getLayoutParams();
//						params.height= (int)(64 * m_Density/*m_MainActivity.getDensity()*/);
//						params.width = (int)(64 * m_Density/*m_MainActivity.getDensity()*/);
//						imageView.setLayoutParams(params);

//						//绑定图片到ImageView控件,考虑适配低版本(使用了低版本API)
					int nSDKVersion = android.os.Build.VERSION.SDK_INT;
					if(nSDKVersion < android.os.Build.VERSION_CODES.JELLY_BEAN){
						imageView.setBackgroundDrawable(bmDrawable);
					}
					else{
						imageView.setBackground(bmDrawable);
					}

					viewHolder.textView.setText(strBmpName);

					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							int nBmpCode = Integer.valueOf(strBmpName);

//								//取消编辑未提交的编辑对象
//								m_MapControl.cancel();
							//设置地图状态为标绘状态
							m_MapControl.setAction(Action.CREATEPLOT);
							System.out.println("m_LibraryIndex: " + m_LibraryIndex);

							//指定标绘符号进行绘制
							m_MapControl.setPlotSymbol(m_LibraryIndex, nBmpCode);  // 绘制通用标绘符号

							dismiss();
						}
					});

				}

			}
			catch (Exception e) {
				//打印错误日志
				e.printStackTrace();
			}

			return convertView;
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
//		case R.id.btn_tour_ok:
//			conform();
//			break;
//		case R.id.btn_tour_cancel:
//			cancel();
//			break;
			default:
				break;
		}
	}

	public void dismiss(){
		super.dismiss();
	}

	/**
	 * 取消
	 */
	private void cancel() {
		dismiss();
	}

	/**
	 * 确认
	 */
	private void conform() {
		dismiss();
	}

	private static class ViewHolder{
		CheckBox Check;
	}

	private static class ViewHolderPicture{
		TextView textView;
		ImageView imageView;
	}

	/**
	 * 清空查询结果
	 */
	public void clear(){
		if(m_DynamicView != null){
			m_DynamicView.clear();
		}

		setContentView(m_ContentView);
		setWidth(MyApplication.dp2px(350));
		setHeight(MyApplication.dp2px(530));

		mResultGrid.setAdapter(new PictureAdapter());
		this.setOutsideTouchable(true);

		dismiss();
	}

	/**
	 * 清空图层
	 */
	private void clearLayers() {
		int count = m_Layers.getCount();
		for(;count>1;){
			m_Layers.remove(0);
			count = m_Layers.getCount();
		}
	}

	/**
	 * 显示
	 */
	public void show(){
		DisplayMetrics dm = m_MapControl.getContext().getResources().getDisplayMetrics();
		showAt(0, 0, 400 * (int)dm.density, 300 * (int)dm.density);
	}

	private void showAt(int x,int y, int width, int height)
	{
		DisplayMetrics dm = m_MapControl.getContext().getResources().getDisplayMetrics();
//		setWidth(MyApplication.dp2px(width));
//		setHeight(MyApplication.dp2px(height));
		setWidth(width);
		setHeight(height);
		showAtLocation(m_MapControl.getRootView(), Gravity.CENTER|Gravity.CENTER, (int)dm.density*x, (int)dm.density*y);
	}

}