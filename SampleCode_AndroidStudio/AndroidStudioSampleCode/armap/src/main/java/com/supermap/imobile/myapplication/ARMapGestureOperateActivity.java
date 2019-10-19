package com.supermap.imobile.myapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.supermap.ar.CameraView;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

/**
 * <p>
 * Title:视频地图的手势操作
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
 * 1、范例简介：示范视频地图的手势操作功能
 * 2、示例数据：数据目录："/sdcard/SampleData/AR/"
 *            地图数据：supermapindoor.smwu,supermap.udb,supermap.udd
 *            许可目录："/SuperMap/License/"
 * 3、关键类型/成员:
 *	 Map.setIsArmap() 				//设置地图为AR模式
 *   Map.setARMapAlpha()			//透明度
 *   Map.setARScrollEnable()		//设置是否支持抬升
 *   Map.setARScrollValue()         //设置抬升值
 *   MapControl.enableRotateTouch() //设置是否支持旋转
 *   MapControl.enableSlantTouch()  //设置是否支持俯仰
 *
 * 4、使用步骤：
 * （1）单指，上下抬升地图；左右旋转地图
 * （2）双指，上下地图俯仰；左右旋转地图
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
public class ARMapGestureOperateActivity extends FragmentActivity implements
		View.OnTouchListener {

    final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private MapControl m_mapcontrol = null;
    private Workspace m_workspace;
    private MapView m_mapView = null;

    private Switch mFixedPntRotateModeSwitch;      //固定点旋转

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.simple_camera_armap_gesture);
        initView();
        initMap(); //初始化地图并且获取POI数据
    }
    private void initView() {
        //添加相机, 这里使用AR包内的CameraView，可自定义
        FrameLayout.LayoutParams cameraViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((RelativeLayout)findViewById(R.id.rl_all)).addView(new CameraView(this), 0,cameraViewParams);

        mFixedPntRotateModeSwitch = (Switch) findViewById(R.id.fixedPntRotateSwitch);
        mFixedPntRotateModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    m_mapcontrol.getMap().setARRotateByCenterEnable(true);
                } else {
                    m_mapcontrol.getMap().setARRotateByCenterEnable(false);
                }
            }
        });
    }

    public static double rotateValueOfARMap = 0.0f;
    public static double elevateValueOfARMap = 0.0f;
    private GestureDetector.OnGestureListener mGestrueListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) { return true; }

        @Override
        public void onShowPress(MotionEvent e) { }

        @Override
        public boolean onSingleTapUp(MotionEvent e) { return true; }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            unlockMap();
            if (e2.getPointerCount() > 1){
                return true;
            }
            endDrawTime = System.currentTimeMillis();
            if(endDrawTime - startDrawTime > 20)
            {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    rotateValueOfARMap += distanceX/3  ;
                } else {
                    elevateValueOfARMap += distanceY * 5;

                    m_mapcontrol.getMap().setARRotateCenter(m_mapcontrol.getMap().getCenter());
                    m_mapcontrol.getMap().setARScrollValue((float) elevateValueOfARMap);
                }
                m_mapcontrol.getMap().setAngle(m_mapcontrol.getMap().getAngle() + distanceX/3);
            }

            startDrawTime = endDrawTime;
            m_mapcontrol.getMap().refresh();
            return true;
        }
        @Override
        public void onLongPress(MotionEvent e) {

        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    //锁定地图不让滑动
    private void lockMap() {

        Map map = m_mapView.getMapControl().getMap();
        Rectangle2D viewBounds = map.getViewBounds();
        map.setLockedViewBounds(viewBounds);
        map.setViewBoundsLocked(true);
    }

    //地图滑动
    private void unlockMap() {

        Map map = m_mapView.getMapControl().getMap();
        Rectangle2D viewBounds = map.getViewBounds();
        map.setLockedViewBounds(viewBounds);
        map.setViewBoundsLocked(false);
    }

	private void initMap() {
		m_workspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(sdcard+"/SampleData/AR/supermapindoor.smwu");
		info.setType(WorkspaceType.SMWU);
		if (m_workspace.open(info)) {

			m_mapView = (MapView) findViewById(R.id.projectionMapView);
			m_mapcontrol = m_mapView.getMapControl();
			m_mapcontrol.getMap().setWorkspace(m_workspace);

			String mapName = m_workspace.getMaps().get(0);    //supermap
			m_mapcontrol.getMap().open(mapName);
			m_mapcontrol.getMap().setAlphaOverlay(true);
			m_mapcontrol.setMapOverlay(true);               //设置透明的
            m_mapcontrol.getMap().setCenter(new Point2D(116.512230,39.991812)); //超图七楼

		}

        m_mapView.addOverlayMap(m_mapcontrol);

		//设置AR地图模式
        m_mapcontrol.getMap().setIsArmap(true);
        m_mapcontrol.getMap().setARMapAlpha(0.5f);//透明度
        m_mapcontrol.getMap().setARScrollEnable(true);
        m_mapcontrol.enableRotateTouch(true); //设置是否支持旋转
        m_mapcontrol.enableSlantTouch(true);  //设置是否支持俯仰

		//设置手势监听器
        m_mapcontrol.setGestureDetector(new GestureDetector(m_mapcontrol.getContext(), mGestrueListener));
        m_mapcontrol.getMap().setSlantAngle(30);  //不是固定旋转的话 给他一个初始角度值。
	}


    long startDrawTime = System.currentTimeMillis();
    long endDrawTime = System.currentTimeMillis();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchEvent(event);
    }
}

