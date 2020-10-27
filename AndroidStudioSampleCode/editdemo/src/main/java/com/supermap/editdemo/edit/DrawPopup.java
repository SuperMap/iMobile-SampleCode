package com.supermap.editdemo.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.supermap.editdemo.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;

public class DrawPopup extends PopupWindow implements OnClickListener{
	private MapControl mMapControl = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;

	/**
	 * 构造函数
	 * @param mapcontrol
	 */
	public DrawPopup(MapControl mapcontrol) {
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
		mContentView = mInflater.inflate(R.layout.addbar, null);
		mContentView.findViewById(R.id.btn_addline).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_addpoint).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_addregion).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_drawline).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_drawregion).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_freedraw).setOnClickListener(this);
	}

	/**
	 * 显示绘制工具栏
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
		RadioButton radio = (RadioButton)v;
		Layers lys = mMapControl.getMap().getLayers();
		switch (radio.getId()) {
			case R.id.btn_addpoint:
				if(radio.isChecked()){
					mMapControl.setAction(Action.CREATEPOINT);
					lys.get("Point@edit").setEditable(true);
				}
				break;
			case R.id.btn_addline:
				if(radio.isChecked()){
					mMapControl.setAction(Action.CREATEPOLYLINE);
					lys.get("Line@edit").setEditable(true);
				}
				break;
			case R.id.btn_addregion:
				if(radio.isChecked()){
					mMapControl.setAction(Action.CREATEPOLYGON);
					lys.get("Region@edit").setEditable(true);

				}
				break;
			case R.id.btn_drawline:
				if(radio.isChecked()){
					mMapControl.setAction(Action.DRAWLINE);
					lys.get("Line@edit").setEditable(true);
				}
				break;
			case R.id.btn_drawregion:
				if(radio.isChecked()){
					mMapControl.setAction(Action.DRAWPLOYGON);
					lys.get("Region@edit").setEditable(true);
				}
				break;
			case R.id.btn_freedraw:
				if(radio.isChecked()){
					mMapControl.setAction(Action.FREEDRAW);
					lys.get("CAD@edit").setEditable(true);
				}
				break;

			default:
				break;
		}

	}

	/**
	 * 重置按钮状态
	 */
	private void reset(){
		((RadioButton)mContentView.findViewById(R.id.btn_addline)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_addpoint)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_addregion)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_drawline)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_drawregion)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_freedraw)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_receivefocus2)).setChecked(true);
	}

	/**
	 * 撤销
	 */
	public void cancel(){
		reset();
	}

}
