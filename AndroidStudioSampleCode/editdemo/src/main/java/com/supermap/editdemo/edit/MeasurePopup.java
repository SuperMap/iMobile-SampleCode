package com.supermap.editdemo.edit;

import java.text.DecimalFormat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.supermap.data.Point;
import com.supermap.editdemo.R;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MeasureListener;

public class MeasurePopup extends PopupWindow implements OnClickListener {

	private MapControl     mMapControl       = null;
	private LayoutInflater mInflater         = null;
	private View           mContentView      = null;
	private EditText       edt_measureresult = null;

	/**
	 * 构造函数
	 * @param mapcontrol
	 */
	public MeasurePopup(MapControl mapcontrol) {
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
		mContentView = mInflater.inflate(R.layout.measurebar, null);
		mContentView.findViewById(R.id.btn_measurelength).setOnClickListener(this);
		mContentView.findViewById(R.id.btn_measurearea).setOnClickListener(this);
		edt_measureresult = (EditText)mContentView.findViewById(R.id.edt_measureresult);
		mMapControl.addMeasureListener(new MeasureListener() {

			@Override
			public void lengthMeasured(double arg0, Point arg1) {

				DecimalFormat df = new DecimalFormat("0.00");
				if (arg0<1000) {
					edt_measureresult.setText(" "+df.format(arg0)+"米");
				} else {
					edt_measureresult.setText(" "+df.format(arg0/1000)+"公里");
				}
			}

			@Override
			public void areaMeasured(double arg0, Point arg1) {

				DecimalFormat df = new DecimalFormat("0.00");
				if (arg0<1000000) {
					edt_measureresult.setText(" "+df.format(arg0)+"平方米");
				} else {
					edt_measureresult.setText(" "+df.format(arg0/1000000)+"平方公里");
				}
			}

			@Override
			public void angleMeasured(double arg0, Point arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 显示测量工具栏
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
		edt_measureresult.setText("");
		super.dismiss();
	}
	@Override
	public void onClick(View v) {
		RadioButton radio = (RadioButton)v;
		Layers lys = mMapControl.getMap().getLayers();
		switch (radio.getId()) {
			case R.id.btn_measurelength:
				if(radio.isChecked()){
					mMapControl.setAction(Action.MEASURELENGTH);
					((EditText)mContentView.findViewById(R.id.edt_measureresult)).setText("");
				}
				break;
			case R.id.btn_measurearea:
				if(radio.isChecked()){
					mMapControl.setAction(Action.MEASUREAREA);
					((EditText)mContentView.findViewById(R.id.edt_measureresult)).setText("");
				}
				break;

			default:
				break;
		}
	}

	/**
	 * 重置按钮
	 */
	private void reset(){
		edt_measureresult.setText("");
		((RadioButton)mContentView.findViewById(R.id.btn_measurelength)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_measurearea)).setChecked(false);;
		((RadioButton)mContentView.findViewById(R.id.btn_receivefocus4)).setChecked(true);
	}

	/**
	 * 撤销
	 */
	public void cancel() {
		reset();
	}
}
