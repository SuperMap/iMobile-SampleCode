package com.supermap.themedemo.main;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.themedemo.app.MyApplication;
import com.supermap.themedemo.theme.BarTheme;
import com.supermap.themedemo.theme.PieTheme;
import com.supermap.data.Color;
import com.supermap.data.ColorGradientType;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Recordset;
import com.supermap.data.TextStyle;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.ThemeLabel;
import com.supermap.mapping.ThemeLabelItem;
import com.supermap.mapping.ThemeRange;
import com.supermap.mapping.ThemeRangeItem;
import com.supermap.mapping.ThemeUnique;
import com.supermap.mapping.ThemeUniqueItem;
import com.supermap.themedemo.R;
/**
 * <p>
 * Title:专题图
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile 演示Demo的代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile 演示Demo说明---------------------------
 *
 * 1、Demo简介：
 *   展示标签专题图、分段设色专题图、单值专题图、饼状图和柱状图的制作。
 * 2、Demo数据：数据目录："/SuperMap/Demos/Data/ThemeData/Statistics.smwu"
 *           地图数据："Statistics.smwu", "Statistics.udb", "Statistics.udd"
 *           许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *		ThemeLabel.setLabelExpression();     方法
 *		ThemeLabel.setRangeExpression();     方法
 *		ThemeLabel.addToHead(); 		                 方法
 *		ThemeLabelItem.setStyle();	                               方法
 *		ThemeLabelItem.setVisible();         方法
 *
 *      ThemeRange.addToTail();              方法
 *		ThemeRangeItem.setCaption();	                方法
 *      ThemeRangeItem.setEnd();             方法
 *      ThemeRangeItem.setStart();           方法
 *      ThemeRangeItem.setVisible();         方法
 *      ThemeRangeItem.setStyle();           方法
 *
 *      DynPieChart.addChartData();          方法
 *      DynPieChart.setChartTitle();         方法
 *      DynPieChart.setAxesColor();          方法
 *      DynPieChart.setShowLegend();         方法
 *      DynPieChart.setShowLabels();         方法
 *      DynPieChart.addPoint();              方法
 *      DynPieChart.setStyle();              方法
 *
 *		DynBarChart.setChartTile();          方法
 *		DynBarChart.setAxesColor();          方法
 *		DynBarChart.setChartSize();          方法
 *		DynBarChart.setShowGrid();           方法
 *
 * 4、功能展示
 *   标签专题图、分段设色专题图、单值专题图、饼状图和柱状图。
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class MainActivity extends Activity {

	// 定义按钮控件
	private ImageButton btn_label    = null;
	private ImageButton btn_range    = null;
	private ImageButton btn_unique   = null;
	private ImageButton btn_pie      = null;
	private ImageButton btn_bar      = null;
	private ImageButton btn_clear    = null;
	private ImageButton btn_entire   = null;
	private ImageButton btn_zoomOut  = null;
	private ImageButton btn_zoomIn   = null;
	private TextView    tv_chartName = null;

	// 定义地图控件
	Workspace     workspace  = null;
	MapView       mapView    = null;
	MapControl    mapControl = null;
	private DynamicView m_DynamicLayer     = null;
	private Layer       m_UnifiedMapLayer  = null;
	private Layer       m_RangeLayer       = null;
	private Layer       m_UniqueLayer      = null;

	// 定义布尔变量
	boolean isPieCreated    = false;
	boolean isBarCreated    = false;
	boolean isExitEnable     = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.out.println("onCreate");
		//在onCreate中调用初始化方法，否则组件功能不能正常
		Environment.initialization(this);
		setContentView(R.layout.main);

		openWorkspace();

		// 获取并添加动态层到地图
		m_DynamicLayer  = new DynamicView(this, mapView.getMapControl().getMap());
		mapView.addDynamicView(m_DynamicLayer);

		initView();
	}

	/**
	 * 初始化控件，绑定监听器
	 */
	private void initView(){
		btn_label = (ImageButton) findViewById(R.id.btn_label);
		btn_label.setOnClickListener(new ButtonOnClickListener());

		btn_range = (ImageButton) findViewById(R.id.btn_range);
		btn_range.setOnClickListener(new ButtonOnClickListener());

		btn_unique = (ImageButton) findViewById(R.id.btn_unique);
		btn_unique.setOnClickListener(new ButtonOnClickListener());

		btn_pie = (ImageButton) findViewById(R.id.btn_pie);
		btn_pie.setOnClickListener(new ButtonOnClickListener());

		btn_bar = (ImageButton) findViewById(R.id.btn_bar);
		btn_bar.setOnClickListener(new ButtonOnClickListener());

		btn_clear = (ImageButton) findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(new ButtonOnClickListener());

		btn_entire = (ImageButton) findViewById(R.id.btn_entire);
		btn_entire.setOnClickListener(new ButtonOnClickListener());

		btn_zoomOut = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn_zoomOut.setOnClickListener(new ButtonOnClickListener());

		btn_zoomIn = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn_zoomIn.setOnClickListener(new ButtonOnClickListener());

		tv_chartName = (TextView) findViewById(R.id.chartName);

	}

	/**
	 * 按钮点击事件监听类
	 *
	 */
	class ButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_label:
					showUnifiedMap();	               // 显示统一风格标签专题图
					break;

				case R.id.btn_range:
					showThemeRangeMap();               // 显示分段设色专题图
					break;

				case R.id.btn_unique:
					showThemeUniqueMap();              // 显示单值专题图
					break;

				case R.id.btn_pie:
					showPieTheme();                    // 显示饼图
					break;

				case R.id.btn_bar:
					showBarTheme();                    // 显示柱状图
					break;

				case R.id.btn_clear:
					clearTheme();                      // 清除专题图
					break;

				case R.id.btn_entire:
					mapControl.getMap().viewEntire();
					m_DynamicLayer.refresh();
					mapControl.getMap().refresh();
					break;

				case R.id.btn_zoomOut:
					mapControl.getMap().zoom(0.5);
					m_DynamicLayer.refresh();
					mapControl.getMap().refresh();
					break;

				case R.id.btn_zoomIn:
					mapControl.getMap().zoom(2);
					m_DynamicLayer.refresh();
					mapControl.getMap().refresh();
					break;

				default:
					break;
			}

		}
	}

	/**
	 * 打开工作空间
	 */
	private void openWorkspace() {

		mapView = (MapView) findViewById(R.id.mapView);
		mapControl = mapView.getMapControl();

		workspace = MyApplication.getInstance().getOpenedWorkspace();
		mapControl.getMap().setWorkspace(workspace);
		mapControl.getMap().open(workspace.getMaps().get(0));
//		mapControl.getMap().setFullScreenDrawModel(true);
		mapControl.getMap().zoom(2);
		mapControl.getMap().getLayers().get("Provinces_R@Data#1").setVisible(false);
	}

	private void showInfo(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}


	/**
	 * 显示统一风格标签专题图
	 */
	private void showUnifiedMap() {
		m_DynamicLayer.clear();                           // 用于清除可能存在的饼图或柱状图，防止覆盖
		m_DynamicLayer.refresh();
		if (m_UnifiedMapLayer == null) {
			creatUnifiedMap();                             // 创建统一风格标签专题图
			mapControl.getMap().refresh();
		} else {
			if (!(m_UnifiedMapLayer.isVisible())) {
				m_UnifiedMapLayer.setVisible(true);
				mapControl.getMap().refresh();
			}
		}

	}

	/**
	 * 制作统一风格标签专题图
	 */
	private void creatUnifiedMap() {
		ThemeLabel themeLabelMap = new ThemeLabel();
		themeLabelMap.setLabelExpression("NAME");
		themeLabelMap.setRangeExpression("id");

		// 为标签专题图的标签设置统一样式
		ThemeLabelItem themeLabelItem1 = new ThemeLabelItem();
		themeLabelItem1.setVisible(true);
		TextStyle textStyle1 = new TextStyle();
		textStyle1.setForeColor(new Color(0, 10, 10));
		textStyle1.setFontName("宋体");
		themeLabelItem1.setStyle(textStyle1);

		// 添加标签专题图子项到标签专题图对象中
		themeLabelMap.addToHead(themeLabelItem1);

		Dataset dataset = workspace.getDatasources().get(0).getDatasets().get(0);
		if (dataset != null) {
			m_UnifiedMapLayer = mapControl.getMap().getLayers().add(dataset,themeLabelMap, true);
		}
	}

	/**
	 * 显示单值专题图
	 */
	private void showThemeUniqueMap() {
		m_DynamicLayer.clear();                        // 用于清除可能存在的饼图或柱状图，防止覆盖
		m_DynamicLayer.refresh();
		// 先关闭可能已显示的分段设色专题图
		if(m_RangeLayer != null && m_RangeLayer.isVisible())
			m_RangeLayer.setVisible(false);

		if(m_UniqueLayer == null){
			creatThemeUniqueMap();                      // 创建单值专题图

		}else {
			if (!(m_UniqueLayer.isVisible())) {
				m_UniqueLayer.setVisible(true);
				mapControl.getMap().refresh();
			}
		}
	}

	/**
	 * 构造单值专题图
	 */
	public void creatThemeUniqueMap() {
		ThemeUnique themeUnique = new ThemeUnique();

		// 将得到的专题图添加到地图
		Dataset dataset = workspace.getDatasources().get(0).getDatasets()
				.get("Provinces_R");
		if(dataset == null){

			return;
		}

		// 根据数据集中的ID来创建单值专题图
		themeUnique = ThemeUnique.makeDefault((DatasetVector) dataset, "ID",
				ColorGradientType.TERRAIN);

		// 设置各个子项显示风格
		int nCount = themeUnique.getCount();
		for (int i = 0; i < nCount; i++) {
			ThemeUniqueItem Item = themeUnique.getItem(i);
			Item.getStyle().setLineColor(new Color(100, 100, 100));
		}

		m_UniqueLayer = mapControl.getMap().getLayers().add(dataset, themeUnique, true);
		if (m_UnifiedMapLayer != null)
			mapControl.getMap().getLayers().moveDown(0);               // 确保标签专题图在最上层
		tv_chartName.setText("全国行政区域单值专题图");
		mapControl.getMap().refresh();
	}

	/**
	 * 显示分段设色专题图
	 */
	public void showThemeRangeMap(){
		m_DynamicLayer.clear();                         // 用于清除可能存在的饼图或柱状图，防止覆盖
		m_DynamicLayer.refresh();
		// 先关闭可能已经显示的单值专题图
		if(m_UniqueLayer != null && m_UniqueLayer.isVisible())
			m_UniqueLayer.setVisible(false);

		// 分段设色专题图只创建一次,之后只是设置显示与否
		if(m_RangeLayer == null){
			creatThemeRangeMap();                        // 创建分段设色专题图

		}else {
			if (!(m_RangeLayer.isVisible())) {
				m_RangeLayer.setVisible(true);
				mapControl.getMap().refresh();
			}
		}
	}

	/**
	 * 制作分段专题图
	 */
	public void creatThemeRangeMap() {

		Dataset dataset = workspace.getDatasources().get(0).getDatasets()
				.get("Provinces_R");

		if(dataset == null){
			return;
		}

		// 根据2000年的GDP("GDP_2000")来设置分段专题图
		tv_chartName.setText("2000年各省份的GDP分布");
		ThemeRange themeRangeMap = new ThemeRange();
		themeRangeMap.setRangeExpression("GDP_2000");      // 设置分段专题图表达式字段

		// 填充样式设置
		GeoStyle geoStyle = new GeoStyle();
		geoStyle.setLineColor(new Color(255, 255, 255));
		geoStyle.setLineWidth(0.3);

		// GDP小于五百亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem1 = new ThemeRangeItem();
		themeRangeItem1.setCaption("GDP小于五百亿");
		themeRangeItem1.setEnd(500);
		themeRangeItem1.setStart(0);
		themeRangeItem1.setVisible(true);
		geoStyle.setFillForeColor(new Color(209, 182, 210));
		themeRangeItem1.setStyle(geoStyle);

		// GDP小于一千亿大于五百亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem2 = new ThemeRangeItem();
		themeRangeItem2.setCaption("GDP小于一千亿大于五百亿");
		themeRangeItem2.setEnd(1000);
		themeRangeItem2.setStart(500);
		themeRangeItem2.setVisible(true);
		geoStyle.setFillForeColor(new Color(205, 167, 183));
		themeRangeItem2.setStyle(geoStyle);

		// GDP小于一千五亿大于一千亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem3 = new ThemeRangeItem();
		themeRangeItem3.setCaption("GDP小于一千五亿大于一千亿");
		themeRangeItem3.setEnd(1500);
		themeRangeItem3.setStart(1000);
		themeRangeItem3.setVisible(true);
		geoStyle.setFillForeColor(new Color(183, 128, 151));
		themeRangeItem3.setStyle(geoStyle);

		// GDP小于二千 亿大于一千五亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem4 = new ThemeRangeItem();
		themeRangeItem4.setCaption("GDP小于二千亿大于一千五亿");
		themeRangeItem4.setEnd(2000);
		themeRangeItem4.setStart(1500);
		themeRangeItem4.setVisible(true);
		geoStyle.setFillForeColor(new Color(164, 97, 136));
		themeRangeItem4.setStyle(geoStyle);

		// GDP小于二千五亿 大于二千亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem5 = new ThemeRangeItem();
		themeRangeItem5.setCaption("GDP小于二千五亿 大于二千亿");
		themeRangeItem5.setEnd(2500);
		themeRangeItem5.setStart(2000);
		themeRangeItem5.setVisible(true);
		geoStyle.setFillForeColor(new Color(94, 53, 77));
		themeRangeItem5.setStyle(geoStyle);

		// GDP小于三千 亿大于二千五亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem6 = new ThemeRangeItem();
		themeRangeItem6.setCaption("GDP小于三千 亿大于二千五亿");
		themeRangeItem6.setEnd(3000);
		themeRangeItem6.setStart(2500);
		themeRangeItem6.setVisible(true);
		geoStyle.setFillForeColor(new Color(255, 0, 0));
		themeRangeItem6.setStyle(geoStyle);

		// GDP小于三千五 亿大于三千亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem7 = new ThemeRangeItem();
		themeRangeItem7.setCaption("GDP小于三千五 亿大于三千亿");
		themeRangeItem7.setEnd(3500);
		themeRangeItem7.setStart(3000);
		themeRangeItem7.setVisible(true);
		geoStyle.setFillForeColor(new Color(0, 183, 239));
		themeRangeItem7.setStyle(geoStyle);

		// GDP小于四千 亿大于三千五亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem8 = new ThemeRangeItem();
		themeRangeItem8.setCaption("GDP小于四千 亿大于三千五亿");
		themeRangeItem8.setEnd(4000);
		themeRangeItem8.setStart(3500);
		themeRangeItem8.setVisible(true);
		geoStyle.setFillForeColor(new Color(244, 219, 24));
		themeRangeItem8.setStyle(geoStyle);

		// GDP小于四千五亿 大于四千亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem9 = new ThemeRangeItem();
		themeRangeItem9.setCaption("GDP小于四千五亿 大于四千亿");
		themeRangeItem9.setEnd(4500);
		themeRangeItem9.setStart(4000);
		themeRangeItem9.setVisible(true);
		geoStyle.setFillForeColor(new Color(26, 168, 38));
		themeRangeItem9.setStyle(geoStyle);

		// GDP小于五千 亿大于四千五亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem10 = new ThemeRangeItem();
		themeRangeItem10.setCaption("GDP小于五千 亿大于四千五亿");
		themeRangeItem10.setEnd(5000);
		themeRangeItem10.setStart(4500);
		themeRangeItem10.setVisible(true);
		geoStyle.setFillForeColor(new Color(115, 35, 175));
		themeRangeItem10.setStyle(geoStyle);

		// GDP大于五千亿的分段专题图子项的设置
		ThemeRangeItem themeRangeItem11 = new ThemeRangeItem();
		themeRangeItem11.setCaption("GDP大于五千亿");
		themeRangeItem11.setEnd(Double.MAX_VALUE);
		themeRangeItem11.setStart(5000);
		themeRangeItem11.setVisible(true);
		geoStyle.setFillForeColor(new Color(56, 98, 183));
		themeRangeItem11.setStyle(geoStyle);

		// 添加专题图子项到分段专题图对象中
		themeRangeMap.addToHead(themeRangeItem1);
		themeRangeMap.addToTail(themeRangeItem2);
		themeRangeMap.addToTail(themeRangeItem3);
		themeRangeMap.addToTail(themeRangeItem4);
		themeRangeMap.addToTail(themeRangeItem5);
		themeRangeMap.addToTail(themeRangeItem6);
		themeRangeMap.addToTail(themeRangeItem7);
		themeRangeMap.addToTail(themeRangeItem8);
		themeRangeMap.addToTail(themeRangeItem9);
		themeRangeMap.addToTail(themeRangeItem10);
		themeRangeMap.addToTail(themeRangeItem11);

		// 显示
		m_RangeLayer = mapControl.getMap().getLayers()
				.add(dataset, themeRangeMap, true);
		if (m_UnifiedMapLayer != null)
			mapControl.getMap().getLayers().moveDown(0);      // 确保标签专题图在最上层
		mapControl.getMap().refresh();
	}

	/**
	 * 显示饼图
	 */
	public void showPieTheme() {
		m_DynamicLayer.clear();            // 用于清除可能存在的饼图或柱状图，防止覆盖
		Datasource datasource = workspace.getDatasources().get(0);
		Dataset dataset = datasource.getDatasets().get(0);
		DatasetVector datasetVector = (DatasetVector) dataset;

		Recordset recordset = datasetVector.getRecordset(false,
				CursorType.STATIC);
		PieTheme popAgeTheme = new PieTheme(m_DynamicLayer,
				recordset);
		popAgeTheme.creat(60,60);

		m_DynamicLayer.refresh();
	}

	/**
	 * 显示柱状图
	 */
	public void showBarTheme() {
		m_DynamicLayer.clear();            // 用于清除可能存在的饼图或柱状图，防止覆盖
		Datasource datasource3 = workspace.getDatasources().get(0);
		Dataset dataset3 = datasource3.getDatasets().get(0);
		DatasetVector datasetVector3 = (DatasetVector) dataset3;

		Recordset recordset3 = datasetVector3.getRecordset(false,
				CursorType.STATIC);
		BarTheme barTheme = new BarTheme(m_DynamicLayer, recordset3);
		barTheme.creat(100, 80);

		m_DynamicLayer.refresh();
	}

	/**
	 * 清空专题图
	 */
	public void clearTheme() {

		isBarCreated = false;
		isPieCreated = false;

		Layers m_Layers = mapControl.getMap().getLayers();

		// 清除标签、分段、单值专题图
		if(m_UnifiedMapLayer !=null)
			m_Layers.remove(m_UnifiedMapLayer.getName());
		if(m_RangeLayer !=null)
			m_Layers.remove(m_RangeLayer.getName());
		if(m_UniqueLayer !=null)
			m_Layers.remove(m_UniqueLayer.getName());

		m_UnifiedMapLayer  = null;
		m_RangeLayer       = null;
		m_UniqueLayer      = null;
		// 清空动态层, 即清除饼图或柱状图
		if(m_DynamicLayer != null ){
			m_DynamicLayer.clear();
			m_DynamicLayer.refresh();
		}
		mapControl.getMap().refresh();


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!isExitEnable){
				Toast.makeText(this, "再按一次退出程序！", 1500).show();
				isExitEnable = true;
			}else{
				mapControl.getMap().close();
				this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
