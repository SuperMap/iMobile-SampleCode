package com.supermap.imobile.dynamicshow;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.supermap.data.Environment;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.dyn.DynamicPolymerizer;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicElement;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.RotateAnimator;
import com.supermap.mapping.dyn.TranslateAnimator;
import com.supermap.mapping.dyn.ZoomAnimator;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * <p>
 * Title:动态层显示
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为SuperMap iMobile for Android 的示范代码 
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 * 
 * 1、范例简介：示范如何使用动态层
 * 2、示例数据：安装目录/sdcard/SampleData/GeometryInfo/World.smwu
 * 3、关键类型/成员: 
 *      DynamicView.addElement 				方法
 *      DynamicView.query 					方法
 *      DynamicView.clear 					方法
 *      DynamicElement.addPoint	 			方法
 *      DynamicElement.addAnimator 			方法
 *      DynamicElement.setStyle 			方法
 *      DynamicElement.setOnClickListenner	方法
 *      DynamicElement.getID	 			方法
 *          
 * 4、使用步骤：
 *   (1)点击添加点，添加动态点对象
 *   (2)点击动画按钮，产生动画
 *   (3)点击清空动态层，清空动态元素
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
	private MapView m_mapview = null;
	private Workspace m_workspace =  null;
	private MapControl m_mapControl = null;
	
	private Button m_btnPt;
	private Button m_btnAnim;
	private Button m_btnClear;
		
	private DynamicView m_dynamicLayer = null;
	private ArrayList<Integer> m_idList;
	
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	/**
	 * 需要申请的权限数组
	 */
	protected String[] needPermissions = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_WIFI_STATE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.CHANGE_WIFI_STATE,
	};
//	private DynamicView m_dynamicLayer2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestPermissions();
        // 初始化环境,设置许可路径
        Environment.setLicensePath(sdcard+"/SuperMap/license/");
        //在onCreate中调用初始化方法，否则组件功能不能正常
        Environment.initialization(this);	
		setContentView(R.layout.activity_main);
		
		openWorkspace();		
		initView();
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		m_dynamicLayer = new DynamicView(this, m_mapControl.getMap());
		m_mapview.addDynamicView(m_dynamicLayer);
		
		m_btnPt = (Button)findViewById(R.id.btn_pt);	
		m_btnPt.setOnClickListener(new Btnlistener());
		
		m_btnAnim = (Button)findViewById(R.id.btn_animation);
		m_btnAnim.setOnClickListener(new Btnlistener());

		m_btnClear = (Button)findViewById(R.id.btn_clear);
		m_btnClear.setOnClickListener(new Btnlistener());
		
		m_idList = new ArrayList<Integer>();

		m_dynamicLayer.setPolymerize(true);
		m_dynamicLayer.setEndPolymerListener(new DynamicView.EndPolymerListener() {
			@Override
			public void endPolymerElements(List<DynamicElement> dynamicPolymerizers) {
				//不能做耗时操作
				for (int i = 0; i < dynamicPolymerizers.size(); i++) {
					DynamicPolymerizer ele = (DynamicPolymerizer) dynamicPolymerizers.get(i);

					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.airplane);
					Bitmap m_bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
					DynamicStyle style = new DynamicStyle();

//                            style.setScale(Integer.parseInt(ele.getText())%2);
					style.setAngle(Integer.parseInt(ele.getText()) * 10);
					style.setBackground(m_bitmap);


					ele.setStyle(style);
				}
			}
		});
		m_dynamicLayer.setOnPolymerClickListenner(new DynamicView.OnPolymerClickListener() {

			@Override
			public void onClick(Point2D mapPoint, List<DynamicElement> dynamicElements) {


//                        addCallout(mapPoint.getX(),mapPoint.getY());

				Toast toast = Toast.makeText(MainActivity.this, "聚合个数：" + dynamicElements.size(), Toast.LENGTH_SHORT);
				toast.show();
			}
		});

//		Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.airplane);
//		Bitmap m_bitmap = Bitmap.createScaledBitmap(bitmap1, 80, 80, true);
//		DynamicStyle style = new DynamicStyle();
//		style.setAngle(60);
//		style.setBackground(m_bitmap);
//
////				Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red);
////				Bitmap m_bitmap2 = Bitmap.createScaledBitmap(bitmap2, 80, 80, true);
////				DynamicStyle style2 = new DynamicStyle();
////				style2.setBackground(m_bitmap2);
//
//
//		m_dynamicLayer.setPolymerizeStyle(style);
////				m_dynamicLayer2.setPolymerizeSelectedStyle(style2);
//		m_dynamicLayer.setPolymerizeTextColor(android.graphics.Color.TRANSPARENT);
//
//		m_dynamicLayer.refresh();

		m_mapview.getDynParams().setIsRealTimeCacheDraw(false);

	}
	
	/**
	 * 打开工作空间
	 */
	private void openWorkspace() {
		m_mapview = (MapView) findViewById(R.id.mapview);
		m_mapControl = m_mapview.getMapControl();
		
		m_workspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();

		info.setServer(sdcard+"/SampleData/GeometryInfo/World.smwu");
		
		info.setType(WorkspaceType.SMWU);
		boolean isOpen = m_workspace.open(info);
		if(!isOpen){
			showInfo("Workspace open failed!");
		}
		
		m_mapControl.getMap().setWorkspace(m_workspace);		
		m_mapControl.getMap().open(m_workspace.getMaps().get(1));
	}

	/**
	 * 检测权限
	 * return true:已经获取权限
	 * return false: 未获取权限，主动请求权限
	 */

	public boolean checkPermissions(String[] permissions) {
		return EasyPermissions.hasPermissions(this, permissions);
	}

	/**
	 * 申请动态权限
	 */
	private void requestPermissions() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return;
		}
		if (!checkPermissions(needPermissions)) {
			EasyPermissions.requestPermissions(
					this,
					"为了应用的正常使用，请允许以下权限。",
					0,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.ACCESS_WIFI_STATE,
					Manifest.permission.ACCESS_NETWORK_STATE,
					Manifest.permission.CHANGE_WIFI_STATE);
			//没有授权，编写申请权限代码
		} else {
			//已经授权，执行操作代码
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}
	private void showInfo(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 获取随机点
	 * @return
	 */
	private Point2D getPoint() {
		double x = Math.random() * 360 - 180;
		double y = Math.random() * 180 - 90;
		
		Point2D pt = new Point2D(x, y);
		return pt;
	}
	
	private double calculatesAngle(Point2D ptStart, Point2D ptEnd) {
		double Y = ptEnd.getY() - ptStart.getY();
		double X = ptEnd.getX() - ptStart.getX();
		double angle2 = Math.atan2(Y, X);

		return 90 - angle2*180/3.1415;
	}
	
	class Btnlistener implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.airplane2);
			switch (v.getId()) {
			case R.id.btn_pt:	
				for (int i = 0; i < 100; i++) {								
					DynamicPoint element = new DynamicPoint();
					Point2D pt = getPoint();
					element.addPoint(pt);
					DynamicStyle style = new DynamicStyle();
					style.setLineColor(Color.rgb(0, 0, 200));
					style.setAngle((float)(Math.random() * 360 - 180));

					style.setBackground(bitmap);
					element.setStyle(style);
					element.setOnClickListenner(new DynamicElement.OnClickListener() {
						
						@Override
						public void onClick(DynamicElement element) {
							element.addAnimator(new ZoomAnimator(2.f, 600));
							element.addAnimator(new ZoomAnimator(0.5f, 600));
							element.startAnimation();
						}
					});
					
					m_idList.add(element.getID());
					m_dynamicLayer.addElement(element);
				}
				
				m_dynamicLayer.refresh();

				break;
			case R.id.btn_animation:
				
//				for (int i = 0; i < m_idList.size(); i++) {
//					DynamicElement element = m_dynamicLayer.query(m_idList.get(i));
//
//					element.addAnimator(new ZoomAnimator(2, 1000));
//					element.addAnimator(new ZoomAnimator(0.5f, 1000));
//
//					Point2D pt = getPoint();
//					double angle = calculatesAngle(element.getBounds().getCenter(), pt);
//					float angle2 = element.getStyle().getAngle();
//
//					element.addAnimator(new RotateAnimator((float)(angle-angle2), 1000));
//					element.addAnimator(new TranslateAnimator(pt, 1000));
//
//					Point2D pt2 = getPoint();
//					double angle3 = calculatesAngle(pt, pt2);
//
//					element.addAnimator(new RotateAnimator((float)(angle3-angle), 1000));
//					element.addAnimator(new TranslateAnimator(pt2, 1000));
//				}
//
//				m_dynamicLayer.startAnimation();

				break;

			case R.id.btn_clear:
				m_dynamicLayer.clear();
				m_dynamicLayer.refresh();
				m_idList.clear();
				break;
			default:
				break;
			}
		}
	}
}
