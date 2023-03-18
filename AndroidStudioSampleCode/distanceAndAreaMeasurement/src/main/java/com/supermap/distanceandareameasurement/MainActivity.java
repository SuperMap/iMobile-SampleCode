package com.supermap.distanceandareameasurement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import com.supermap.data.Color;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint3D;
import com.supermap.data.GeoStyle3D;
import com.supermap.data.GeoText3D;
import com.supermap.data.Point3D;
import com.supermap.data.Size2D;
import com.supermap.data.TextPart3D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.realspace.Action3D;
import com.supermap.realspace.Layer3D;
import com.supermap.realspace.Layer3DOSGBFile;
import com.supermap.realspace.Layer3Ds;
import com.supermap.realspace.Scene;
import com.supermap.realspace.SceneControl;
import com.supermap.realspace.Sightline;
import com.supermap.realspace.Tracking3DEvent;
import com.supermap.realspace.Tracking3DListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Title:距离面积量算范例程序
 * 
 * Description:
 * ============================================================================>
 * ------------------------------版权声明---------------------------- 此文件为SuperMap
 * iMobile for Android 的示范代码 版权所有：北京超图软件股份有限公司
 * ----------------------------------------------------------------
 * ---------------------SuperMap iMobile for Android
 * 示范程序说明------------------------
 * 
 * 1、范例简介：示范用户使用imobile的距离面积功能。
 * 
 *
 * 4、使用步骤： (1)运行程序，点击距离或者面积按钮，分别进行对应的量算功能。
 * -----------------------------------------------------------------------------
 * - ==========================================================================
 * ==>
 * 
 * 
 * Company: 北京超图软件股份有限公司
 * 
 */
public class MainActivity extends Activity {

	// 控件类
	private TextView tv_distance, tv_area, tv_info, tv_clear;
	private SceneControl sceneControl;
	private Handler totalLengthHandler;
	private int AnalysisTypeArea = 1;
	private Sightline sightline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//动态权限
		CameraPermissionHelper.requestCameraPermission(this);
		Environment.initialization(this);
		setContentView(R.layout.activity_main);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_area = (TextView) findViewById(R.id.tv_area);
		sceneControl = (SceneControl) findViewById(R.id.sceneControl);
		tv_info = (TextView) findViewById(R.id.tv_info);
		tv_clear = (TextView) findViewById(R.id.tv_clear);
		totalLengthHandler = new MeasureHandler();
		sceneControl.addTrackingListener(mTracking3dListener);
		// 距离量算
		tv_distance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				closeAnalysis();
				AnalysisTypeArea = 0;
				tv_distance.setBackgroundResource(R.drawable.tv_left_press);
				tv_area.setBackgroundResource(R.drawable.tv_right_press);
				startMeasureAnalysis();
			}
		});

		// 面积量算
		tv_area.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeAnalysis();
				AnalysisTypeArea = 1;
				tv_distance.setBackgroundResource(R.drawable.tv_left);
				tv_area.setBackgroundResource(R.drawable.tv_right);
				startSurearea();
			}
		});
		// 清除量算
		tv_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeAnalysis();
			}
		});
	}

	// 开启距离测量分析
	public void startMeasureAnalysis() {
		sceneControl.setAction(Action3D.MEASUREDISTANCE3D);
	}

	// 开启测量面积分析
	public void startSurearea() {
		sceneControl.setAction(Action3D.MEASUREAREA3D);

	}

	// 关闭所有情况下的分析
	public void closeAnalysis() {
		sceneControl.setAction(Action3D.PANSELECT3D);
		tv_info.setText("");
	}

	public void startPerspectiveAnalysis() {
		if (sightline == null) {
			sightline = new Sightline(sceneControl.getScene());
		}
		sceneControl.setAction(Action3D.CREATEPOINT3D);
	}

	/**
	 * 用于分析时候监听交互
	 * 
	 * @author：Supermap
	 * @注释 ：三维场景窗口的跟踪图层中交互绘制事件的监听器。
	 */
	private Tracking3DListener mTracking3dListener = new Tracking3DListener() {

		@Override
		public void tracking(Tracking3DEvent event) {

			initAnalySis(sceneControl, event);

		}
	};

	public void initAnalySis(SceneControl sceneControl, Tracking3DEvent event) {

		if (sightline != null && sceneControl.getAction() == Action3D.CREATEPOINT3D) {

			Point3D p3D = new Point3D(event.getX(), event.getY(), event.getZ());

			if (sightline.getvViewerPosition().getX() == 0) {
				sightline.setViewerPosition(p3D);
				sightline.build();
				// 加点
				Point3D point3d = new Point3D(event.getX(), event.getY(), event.getZ());
				GeoPoint3D geoPoint3D = new GeoPoint3D(point3d);
				GeoStyle3D geoStyle3D = new GeoStyle3D();
				geoPoint3D.setStyle3D(geoStyle3D);
				sceneControl.getScene().getTrackingLayer().add(geoPoint3D, "point");
			} else {
				sightline.addTargetPoint(p3D);
				// 加点
				Point3D point3d = new Point3D(event.getX(), event.getY(), event.getZ());
				GeoPoint3D geoPoint3D = new GeoPoint3D(point3d);
				GeoStyle3D geoStyle3D = new GeoStyle3D();
				geoPoint3D.setStyle3D(geoStyle3D);
				sceneControl.getScene().getTrackingLayer().add(geoPoint3D, "point");
			}

		}

		else if (sceneControl.getAction() == Action3D.MEASUREDISTANCE3D) {
			measureDistance(event);
		} else if (sceneControl.getAction() == Action3D.MEASUREAREA3D) {
			measureSurearea(event);
		}

	}

	// 测量距离
	private void measureDistance(Tracking3DEvent event) {
		// 加点
		// 更新总距离长度
		double totalLength = event.getTotalLength();
		double totalcurrentlength = event.getCurrentLength();
		double x = event.getX();
		Log.v("lzw", "totalLength=" + totalLength + ";" + "totalcurrentlength=" + totalcurrentlength);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putDouble("length", totalLength);
		msg.setData(bundle);
		totalLengthHandler.sendMessage(msg);

	}

	// 测量面积
	private void measureSurearea(Tracking3DEvent event) {
		// 加点
		// 更新测量面积
		double TotalArea = event.getTotalArea();
		double totalcurrentlength = event.getCurrentLength();
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putDouble("Area", TotalArea);
		msg.setData(bundle);
		totalLengthHandler.sendMessage(msg);
		Log.v("lzw", "TotalArea=" + TotalArea + ";" + "totalcurrentlength=" + totalcurrentlength);
	}

	// 结束通视分析
	public void endPerspectiveAnalysis(SceneControl sceneControl) {

		sceneControl.getScene().getTrackingLayer().clear();
		sightline.clearResult();
		sightline.dispose();
		sightline = null;
		sceneControl.setAction(Action3D.PANSELECT3D);

	}

	// 处理测量距离、面积、通视分析的数据更新到textView上展示。
	class MeasureHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (AnalysisTypeArea == 0) {
				double msgLength = Math.round(msg.getData().getDouble("length"));
				if (msgLength < 1000) {
					tv_info.setText(" 共 " + msgLength + " 米");
				} else {
					tv_info.setText(" 共 " + Math.round(msgLength / 1000) + "公里");
				}

			} else if (AnalysisTypeArea == 1) {

				double msgLength = Math.round(msg.getData().getDouble("Area"));
				if (msgLength < 1000) {
					tv_info.setText(" 共 " + msgLength + " 米");
				} else {
					tv_info.setText(" 共 " + Math.round(msgLength / 1000) + "公里");
				}
			}
		}
	}

}
