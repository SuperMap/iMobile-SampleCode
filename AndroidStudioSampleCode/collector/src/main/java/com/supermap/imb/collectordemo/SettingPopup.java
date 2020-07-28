package com.supermap.imb.collectordemo;

import com.supermap.data.GeoStyle;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.collector.Collector;
import com.supermap.mapping.collector.CollectorElement;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SettingPopup extends PopupWindow implements OnClickListener {

	private MapControl        m_MapControl     = null;
	private Layers            m_Layers         = null;
	private Map               m_Map            = null;

	private LayoutInflater    m_LayoutInflater = null;
	private View              m_ContentView    = null;
	
	private TextView mTip = null;
	private MainActivity    m_MainActivity     = null;

	/**
	 * 采集类
	 */
	private Collector m_Collector = null;

	/**
	 * 线颜色
	 */
	private com.supermap.data.Color   m_LineColor = new com.supermap.data.Color(82,198,223);
	
	/**
	 * 面颜色
	 */
	private com.supermap.data.Color   m_FillForeColor = new com.supermap.data.Color(160,255,90);
	
	/**
	 * 线宽度
	 */
	private double                    m_LineWidth = 1.0f;
	
	public SettingPopup (MapControl mapControl, Collector collector, MainActivity activity){
		super(activity);
    	
		m_LayoutInflater = LayoutInflater.from(mapControl.getContext());
		m_MapControl     = mapControl;
		m_Collector      = collector;
		m_MainActivity   = activity;
		
		initView();
		
		//响应返回键操作
		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		
		m_ContentView = m_LayoutInflater.inflate(R.layout.settingcollector, null);
		setContentView(m_ContentView);
		
		

		((Button) m_ContentView.findViewById(R.id.btnLineStyle)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btnPolygonStyle)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btnLineWidth)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btnSave)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.btnCBack)).setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		//线风格
		case R.id.btnLineStyle:
			if(m_Collector != null){
				int[] colors = {0,90,160,190,220,245};
				int valueR = colors[(int)Math.round(Math.random()*(colors.length-1))];
				int valueB = colors[(int)Math.round(Math.random()*(colors.length-1))];
				m_LineColor = new com.supermap.data.Color(valueR, 110, valueB);

				String strR = String.valueOf(valueR);
				String strB = String.valueOf(valueB);
				((EditText) m_ContentView.findViewById(R.id.EditText4)).setText("R= " + strR + ",G=" + 110 + ",B=" + strB);
			}
			break;
		//面风格
		case R.id.btnPolygonStyle:
			if(m_Collector != null){
				int[] colors = {0,90,160,190,220,245};
				int valueR = colors[(int)Math.round(Math.random()*(colors.length-1))];
				int valueB = colors[(int)Math.round(Math.random()*(colors.length-1))];
				m_FillForeColor = new com.supermap.data.Color(valueR, 255, valueB);

				String strR = String.valueOf(valueR);
				String strB = String.valueOf(valueB);
				((EditText) m_ContentView.findViewById(R.id.EditText5)).setText("R=" + strR + ",G=255" + ",B=" + strB);
			}
			break;
		//线宽度
		case R.id.btnLineWidth:
			if(m_Collector != null){
				double[] widths = {0.6,1.0,1.2,1.5,1.6,1.8,1.9};
				double width = widths[(int)Math.round(Math.random()*(widths.length-1))];
				m_LineWidth = width;
				
				String strWidth = String.valueOf(width);
				((EditText) m_ContentView.findViewById(R.id.EditText6)).setText(strWidth);
			}
			break;
		//保存
		case R.id.btnSave:
			if(m_Collector != null){
				
				//设置显示风格
				GeoStyle geoStyle = m_Collector.getStyle();//new GeoStyle();
				//线宽
				geoStyle.setLineWidth(m_LineWidth);
				//线颜色
				geoStyle.setLineColor(m_LineColor);
				//面颜色
				geoStyle.setFillForeColor(m_FillForeColor);
				//设置风格
				m_Collector.setStyle(geoStyle);
				
				//添加名称和说明
				CollectorElement element = m_Collector.getElement();
				if(element != null){
					String strName = ((EditText) m_ContentView.findViewById(R.id.EditText2)).getText().toString();
					String strNotes = ((EditText) m_ContentView.findViewById(R.id.EditText3)).getText().toString();
					
					//名称
					element.setName(strName);
					//说明
					element.setNotes(strNotes);
				}
				
				//隐藏设置窗口
				this.dismiss();
			}
			break;
			
		//返回
		case R.id.btnCBack:
			this.dismiss();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 清空查询结果
	 */
	public void clear(){
	}
	
    /**
     * 显示
     */
   public void show(){
		showAt(0, 0, m_MapControl.getWidth(), m_MapControl.getHeight());
	}
	
	private void showAt(int x,int y, int width, int height)
	{
		setWidth(width);
		setHeight(height);
		
		showAtLocation(m_MapControl.getRootView(), Gravity.LEFT|Gravity.CENTER, dp2px(x), dp2px(y));
	}
	
	public void dismiss(){
		super.dismiss();
	}

	public int dp2px (int dp) 
	{
		int density = (int) (dp*m_MapControl.getContext().getResources().getDisplayMetrics().density);
		return density;
	}
	
}