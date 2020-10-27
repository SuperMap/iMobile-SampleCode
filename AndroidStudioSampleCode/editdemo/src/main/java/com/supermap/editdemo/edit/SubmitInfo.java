package com.supermap.editdemo.edit;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import java.util.ArrayList;

import com.supermap.editdemo.R;
import com.supermap.editdemo.appconfig.MyApplication;
import com.supermap.mapping.Action;
import com.supermap.mapping.ActionChangedListener;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.MapControl;

public class SubmitInfo extends PopupWindow implements OnClickListener{
	private MapControl mMapControl = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;

	private Button btn_submitInfo_confirm = null;

	/**
	 * 构造函数
	 * @param mapcontrol
	 */
	public SubmitInfo(MapControl mapcontrol) {
		mMapControl = mapcontrol;
		mInflater = LayoutInflater.from(mMapControl.getContext());
		loadView();
		setContentView(mContentView);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 初始化界面
	 */
	private void loadView(){
		mContentView = mInflater.inflate(R.layout.submit_info, null);
		btn_submitInfo_confirm = (Button) mContentView.findViewById(R.id.btn_submitInfo_confirm);
		btn_submitInfo_confirm.setOnClickListener(this);

		reset();

		mMapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
			@Override
			public void geometrySelected(GeometrySelectedEvent event) {
				mMapControl.appointEditGeometry(event.getGeometryID(), event.getLayer());

			}

			@Override
			public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void geometryMultiSelectedCount(int i) {

			}
		});

		mMapControl.addActionChangedListener(new ActionChangedListener() {
			@Override
			public void actionChanged(Action newAction, Action oldAction) {
				if(newAction.equals(Action.VERTEXEDIT)&&oldAction.equals(Action.SELECT)){
					Activity act = (Activity)mMapControl.getContext();
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {

						}
					});
				}

			}
		});
	}

	/**
	 * 显示提交对话框
	 */
	public void show(){
		reset();
		//showAsDropDown(((Activity)mMapControl.getContext()).findViewById(R.id.btn_submit), -(MyApplication.dp2px(114)), -4, Gravity.CENTER|Gravity.LEFT);
		showAsDropDown(((Activity)mMapControl.getContext()).findViewById(R.id.btn_submit), 0, -2);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss(){
		//当菜单条消失时将地图设置为PAN
		mMapControl.setAction(Action.PAN);
		super.dismiss();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_submitInfo_confirm:
				dismiss();
				break;

			default:
				break;
		}

	}

	/**
	 * 重置
	 */
	private void reset(){

	}

	/**
	 * 撤销
	 */
	public void cancel() {
		reset();
	}
}
