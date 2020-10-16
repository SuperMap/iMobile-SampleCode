package com.supermap.imobile.myapplication;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.supermap.ar.ArSensorManager;
import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

/**
 * <p>
 * Title:视频地图运动跟踪
 * </p>
 *
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------版权声明----------------------------
 * 此文件为 SuperMap iMobile for Android 的示范代码
 * 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android 示范程序说明------------------------
 *
 * 1、范例简介：示范视频地图运动跟踪
 * 2、示例数据：数据目录："/sdcard/SampleData/AR/"
 *            地图数据：supermapindoor.smwu,supermap.udb,supermap.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *				Map.setProjectMatrix()		方法
 *				Map.setTransformMatrix()	方法
 * 4、使用步骤：
 * 	使用assets/hiro.png的Mark，摄像头识别mark后，添加地图数据。
 *
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p>
 *
 * <p>
 * Company: 北京超图软件股份有限公司
 * </p>
 *
 */
public class ARSimple extends ARActivity {

	private SensorManager mSensorManager = null;
	private MapControl m_mapcontrol = null;
	private Workspace m_workspace = null;

	private ARTrackingManager mARTrackingManager = new ARTrackingManager(this);

    private Button mARSceneControlButton = null;
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath() + "/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.arsimple_main);
		mainLayout = (FrameLayout)this.findViewById(R.id.mainLayout);

		//控制是否注册
		mARSceneControlButton = (Button)findViewById(R.id.ar_scene_button);  //AR场景控制器
		mARSceneControlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mARTrackingManager != null){
					//控制是否注册模式，注册模式下会保留当前位置的状态信息，不再进行跟踪
					boolean isRegisterMode = mARTrackingManager.getIsRegisterMode();
					if(isRegisterMode == true){
						mARTrackingManager.setIsRegisterMode(false);
						mARSceneControlButton.setText("开启");

					}else{
						mARTrackingManager.setIsRegisterMode(true);
						mARSceneControlButton.setText("关闭");
					}

				}
			}
		});

		new Thread() {
			@Override
			public void run() {

					while(true) {
						if(m_mapcontrol !=null){

							m_mapcontrol.getMap().setARMapType(5);
							timeNewPaint = System.currentTimeMillis();

							if(mARTrackingManager.getTransformMatrix() != null && mARTrackingManager.getProjectionMatrix() != null){
								m_mapcontrol.getMap().setProjectMatrix(mARTrackingManager.getProjectionMatrix());
								m_mapcontrol.getMap().setTransformMatrix(mARTrackingManager.getTransformMatrix());
							}

							if ( Math.abs(timeOldPaint - timeNewPaint) > 200 ){
								m_mapcontrol.getMap().refresh();
								timeOldPaint = timeNewPaint;
							}
						}

					}

			}
		}.start();
	}
	long timeOldPaint = 0;
	long timeNewPaint = 0;

	//初始化地图
	private void initMap() {
		final ARActivity arControl2 = this;
		new Handler().postDelayed(new Runnable(){
			public void run(){

				//打开工作空间
				m_workspace = new Workspace();
				WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
				info.setServer(SDCARD + "SampleData/AR/supermapindoor.smwu");
				info.setType(WorkspaceType.SMWU);
				if (m_workspace.open(info)) {
					m_mapcontrol = arControl2.mapControl;
					m_mapcontrol.getMap().setWorkspace(m_workspace);
					String mapName = m_workspace.getMaps().get(0);

					m_mapcontrol.getMap().open(mapName);
					if (!m_mapcontrol.getMap().IsArmap()) {
						m_mapcontrol.getMap().setIsArmap(true);
					}

					m_mapcontrol.getMap().viewEntire(); //如果viewEntire, 则top and right. else left bottom.
					arControl2.beginAr();

					//映射到marker中心
					double pntX = mapControl.getMap().getViewBounds().getRight();
					double pntY = mapControl.getMap().getViewBounds().getTop();
					Point2D pntRightTop = new Point2D(pntX,pntY);
					mapControl.getMap().setCenter(pntRightTop);

				} else {
					System.out.println("Open Workspace failed");
				}
				m_mapcontrol.getMap().refresh();

			}
		}, 3000);

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		initMap();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		ArSensorManager.resume(mSensorManager);
	}
	@Override
	protected void onPause()
	{
		super.onPause();

		ArSensorManager.pause(mSensorManager);
	}
	@Override
	protected void onRestart()
	{
		super.onRestart();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	/**
	 * 2. 创建渲染器
	 */
	@Override
	protected ARRenderer supplyRenderer() {
		//获取一个传感器管理器，注册到ArSensorManager中。
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//启动AR运动追踪
		mARTrackingManager.init();
		return mARTrackingManager.getSimpleRenderer();

	}
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);
	}
}