package com.supermap.plotdemo.plotdemo;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import java.util.ArrayList;

import com.supermap.data.Geometry;
import com.supermap.mapping.Action;
import com.supermap.mapping.GeometrySelectedEvent;
import com.supermap.mapping.GeometrySelectedListener;
import com.supermap.mapping.MapControl;
import com.supermap.plotdemo.R;

public class EditPopup extends PopupWindow {

	private MapControl mMapControl;
	private LayoutInflater mLayoutInflater;
	private View mContentView;
	private Context mContext;
	private Builder deleteDialog = null;
	private boolean isDeleteClicked = false;
	
	public EditPopup(MapControl mapControl, Context context) {
		if (mapControl != null) {

			mContext = context;
			mMapControl = mapControl;
			mLayoutInflater = LayoutInflater.from(context);
			initPopupView();

			setContentView(mContentView);
			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);// setWidth(LayoutParams.WRAP_CONTENT);
//			setOutsideTouchable(true);
//			setBackgroundDrawable(new BitmapDrawable());
			initAlertDialog();
			mMapControl.addGeometrySelectedListener(geoSelectedListener);
		}
	}

	private void initAlertDialog() {
		deleteDialog = new Builder(mContext);
        // 设置删除提示对话框
        deleteDialog.setTitle("是否删除该符号?");
        deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mMapControl.deleteCurrentGeometry();
				mMapControl.submit();
				mMapControl.setAction(Action.PAN);
				mMapControl.getMap().refresh();
			}
		});
        deleteDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				dismiss();
			}
		});
	}
	
	/**
	 * 编辑、删除按钮点击监听
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_edit:
                 mMapControl.setAction(Action.SELECT);
                 ((MainActivity)mContext).showTostInfo("请选择要编辑的对象", false);
				break;

			case R.id.btn_delete:
				if(checkeDelete()){
                   deleteDialog.create().show();
				}else{
					isDeleteClicked = true;    // 点击时，没有选中对象
				}
				break;
			default:
				break;
			}
			dismiss();
		}

		
	};
	
	private boolean checkeDelete() {
		boolean deleteEnable = false;
		Geometry geo = mMapControl.getCurrentGeometry();
		if(geo != null){
			deleteEnable = true;
			geo.dispose();
		} else { 
			((MainActivity)mContext).showTostInfo("请先选中要删除的对象", false);
			mMapControl.setAction(Action.SELECT);
		}
		return deleteEnable;
	}
	
	// 几何对象选择监听，当有几何对象被选择时，设置为节点编辑状态
	private GeometrySelectedListener geoSelectedListener = new GeometrySelectedListener() {
		
		@Override
		public void geometrySelected(GeometrySelectedEvent arg0) {
			// TODO Auto-generated method stub
			mMapControl.setAction(Action.VERTEXEDIT);
			if(isDeleteClicked){
				deleteDialog.create().show();       // 点击删除按钮后，再选中对象，直接弹出删除及时
				isDeleteClicked = false;
			}
		}

		@Override
		public void geometryMultiSelected(ArrayList<GeometrySelectedEvent> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void geometryMultiSelectedCount(int i) {

		}
	};
	
	/**
	 * Initialize view
	 */
	private void initPopupView() {
		// Inflate a view
		mContentView = mLayoutInflater.inflate(R.layout.edit_bar, null);
		mContentView.findViewById(R.id.btn_edit).setOnClickListener(onClickListener);
		mContentView.findViewById(R.id.btn_delete).setOnClickListener(onClickListener);
	}


	/**
	 * Display the popup window
	 * 
	 * @param anchorView
	 */
	public void show(View anchorView) {
		if (anchorView != null) {
			Resources res = ((MainActivity)mContext).getResources();
			float edit_w = res.getDimension(R.dimen.btn_menu_bar1_width);
			float edit_bar_w = res.getDimension(R.dimen.edit_bar_w);
			int xoff = (int)(edit_w - edit_bar_w)/2;
			showAsDropDown(anchorView,xoff,0);
		} else {

		}
	}

	/**
	 * Dispose of the popup window
	 */
	@Override
	public void dismiss() {
		super.dismiss();
	}
}
