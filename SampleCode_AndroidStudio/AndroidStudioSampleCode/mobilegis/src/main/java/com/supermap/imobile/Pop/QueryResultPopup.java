package com.supermap.imobile.Pop;


import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.data.Recordset;
import com.supermap.imobile.adapter.QueryResultAdapter;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

import java.util.Vector;

/**
 * 查询结果类
 */
public class QueryResultPopup extends PopupWindow{
	private MapControl mMapControl = null;
	private MapView mMapView = null;
	private LayoutInflater mInflater = null;
	private View mContentView = null;
	private ExpandableListView mResultList = null;
	private Vector<Recordset> mResultData = new Vector<Recordset>();
	private TextView mTip = null;
//	private DynamicView mDynView = null;
	private TrackingLayer mTrackingLayer = null;
	private PopShowListener listener;
	int width;

	/**
	 * 构造函数
	 * @param mapView
	 */
	public QueryResultPopup(MapView mapView,int width,int height) {
		mMapView = mapView;
		mMapControl = mapView.getMapControl();
//		mDynView = dynView;
		mInflater = LayoutInflater.from(mMapControl.getContext());
		mTrackingLayer = mMapView.getMapControl().getMap().getTrackingLayer();
		loadView();

		setContentView(mContentView);

		setWidth(width);//设置宽度
		setHeight(height/2);//设置高度
		ColorDrawable dw = new ColorDrawable(0x30000000);
		setBackgroundDrawable(dw);
		setAnimationStyle(R.style.AnimationRightFade);//设置弹出样式

		mResultList.setAdapter(new QueryResultAdapter(mResultData,mMapControl));
		this.setOutsideTouchable(false);
		this.width=width;
	}

	/**
	 * 初始化界面
	 */
	private void loadView(){
		mContentView = mInflater.inflate(R.layout.popup_queryresult, null);
		mContentView.findViewById(R.id.btn_hide).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
				listener.Popshow(false);
			}
		});

		mResultList = (ExpandableListView) mContentView.findViewById(R.id.list_queryresult);
		mTip = (TextView) mContentView.findViewById(R.id.tv_tip);
	}

	public void setPopshowListener(PopShowListener listener){
	    this.listener=listener;
    }
	public interface PopShowListener{
	    public void Popshow(boolean isshow);
    }
    public void clearData(){
		mResultData.clear();
	}
	/**
	 * 显示查询结果
	 */
	public void show(){


		if(mResultData.size()>0){
			mTip.setVisibility(View.GONE);
			mResultList.setVisibility(View.VISIBLE);
		}else{
			mTip.setVisibility(View.VISIBLE);
			mResultList.setVisibility(View.GONE);
		}

		// 为解决子项反复添加问题
		for(int i=mResultList.getExpandableListAdapter().getGroupCount()-1;i>=0;i--){
			mResultList.collapseGroup(i);
		}

		for(int i=mResultList.getExpandableListAdapter().getGroupCount()-1;i>=0;i--){
			mResultList.expandGroup(i);
		}
		showAtLocation(mMapControl.getRootView(), Gravity.BOTTOM, 0, 0);
        listener.Popshow(true);
	}

	/**
	 * 清空查询结果
	 */
	public void clear(){
//		mDynView.clear();
		mMapView.removeAllCallOut();

		mResultData.clear();
		loadView();
		setContentView(mContentView);
		setWidth(350);
		setHeight(530);

		mResultList.setAdapter(new QueryResultAdapter(mResultData,mMapControl));
		this.setOutsideTouchable(true);

		dismiss();
	}

	/**
	 * 增加查询结果
	 * @param recordset
	 */
	public void addResult(Recordset recordset){
		int count = recordset.getRecordCount();
		if(recordset.getRecordCount() > 0){
			boolean hasContain = false;
			for(int i=mResultData.size()-1;i>=0;i--){
				if(mResultData.get(i).getDataset().getName().equals(recordset.getDataset().getName())){
					hasContain = true;
					break;
				}
			}
			if(!hasContain){
				mResultData.add(recordset);
			}

		}
	}

	public void colsePoup(){
		dismiss();
	}
}
