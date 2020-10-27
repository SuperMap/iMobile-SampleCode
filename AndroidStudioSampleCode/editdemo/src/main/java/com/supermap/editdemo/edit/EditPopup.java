package com.supermap.editdemo.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import java.util.ArrayList;

import com.supermap.editdemo.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.ActionChangedListener;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.MapControl;

public class EditPopup extends PopupWindow implements OnClickListener{
	private MapControl mMapControl = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;

	private RadioButton mBtnSelect = null;
	private RadioButton mBtnAddNode = null;
	private RadioButton mBtnEditNode = null;
	private RadioButton mBtnDeleteNode = null;
	private RadioButton mBtnDeleteObj = null;

	// 只负责接受焦点
	private RadioButton mBtnReceiveFocus = null;

	/**
	 * 构造函数
	 * @param mapcontrol
	 */
	public EditPopup(MapControl mapcontrol) {
		mMapControl = mapcontrol;
		mInflater = LayoutInflater.from(mMapControl.getContext());
		loadView();
		setContentView(mContentView);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 初始化界面控件
	 */
	private void loadView(){
		mContentView = mInflater.inflate(R.layout.editbar, null);
		mBtnSelect = (RadioButton) mContentView.findViewById(R.id.btn_select);
		mBtnSelect.setOnClickListener(this);
		mBtnAddNode = (RadioButton) mContentView.findViewById(R.id.btn_addnode);
		mBtnAddNode.setOnClickListener(this);
		mBtnEditNode = (RadioButton) mContentView.findViewById(R.id.btn_editnode);
		mBtnEditNode.setOnClickListener(this);
		mBtnDeleteNode = (RadioButton) mContentView.findViewById(R.id.btn_deletenode);
		mBtnDeleteNode.setOnClickListener(this);
		mBtnDeleteObj = (RadioButton) mContentView.findViewById(R.id.btn_delteobject);
		mBtnDeleteObj.setOnClickListener(this);
		mBtnReceiveFocus =  (RadioButton) mContentView.findViewById(R.id.btn_receivefocus3);
		reset();


		mMapControl.addGeometrySelectedListener(new GeometrySelectedListener() {
			@Override
			public void geometrySelected(GeometrySelectedEvent event) {
				mMapControl.appointEditGeometry(event.getGeometryID(), event.getLayer());
				enableEdit();
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
							mBtnEditNode.setChecked(true);
							mBtnSelect.setChecked(false);
						}
					});
				}

			}
		});
	}

	/**
	 * 显示编辑工具栏
	 */
	public void show(View anchorView){
		reset();
		showAsDropDown(anchorView, 0, -2);
	}

	/**
	 * 关闭工具栏
	 */
	public void dismiss(){
		//当菜单条消失时将地图设置为PAN
		mMapControl.setAction(Action.PAN);
		super.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_select:
				mMapControl.setAction(Action.SELECT);
				break;
			case R.id.btn_addnode:
				mMapControl.setAction(Action.VERTEXADD);
				break;
			case R.id.btn_editnode:
				mMapControl.setAction(Action.VERTEXEDIT);
				break;
			case R.id.btn_deletenode:
				mMapControl.setAction(Action.VERTEXDELETE);
				break;
			case R.id.btn_delteobject:
				AlertDialog.Builder builer = new AlertDialog.Builder(mMapControl.getContext());
				builer.setTitle("删除当前编辑对象");
				builer.setMessage("确定要删除这个对象?");
				builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mMapControl.deleteCurrentGeometry();
						reset();
					}
				});
				builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builer.create().show();
				break;
			default:
				break;
		}

	}

	/**
	 * 重置按钮状态
	 */
	private void reset(){
		mBtnSelect.setChecked(false);
		mBtnAddNode.setChecked(false);
		mBtnEditNode.setChecked(false);
		mBtnDeleteNode.setChecked(false);
		mBtnDeleteObj.setChecked(false);
		mBtnReceiveFocus.setChecked(true);

		mBtnAddNode.setEnabled(false);
		mBtnEditNode.setEnabled(false);
		mBtnDeleteNode.setEnabled(false);
		mBtnDeleteObj.setEnabled(false);
	}

	/**
	 * 设置按钮状态
	 */
	private void enableEdit(){
		mBtnAddNode.setEnabled(true);
		mBtnEditNode.setEnabled(true);
		mBtnDeleteNode.setEnabled(true);
		mBtnDeleteObj.setEnabled(true);
	}

	/**
	 * 撤销
	 */
	public void cancel() {
		reset();
	}
}
