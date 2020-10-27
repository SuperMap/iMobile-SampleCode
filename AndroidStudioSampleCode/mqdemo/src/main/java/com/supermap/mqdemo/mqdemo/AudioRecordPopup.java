/**
 * 
 */
package com.supermap.mqdemo.mqdemo;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.PopupWindow;

import com.supermap.demo.mqdemo.R;
import com.supermap.mdatacollector.MDataCollector;


/**
 * @author zhengyl
 *
 */
public class AudioRecordPopup extends PopupWindow implements OnClickListener{

	private LayoutInflater m_LayoutInflater = null;
	private View 								m_ContentView = null;
	private View 								m_mainView = null;
	
	private MDataCollector m_MDataCollector = null;

	private Button								btnStartRecord;
	private Button								btnStopRecord;
	private Button								btnBackUp;
	private Button								btnOk;
	
	private Chronometer								mTimer = null;

	private MultiMediaPopup						m_MultiMedia = null;
	
	public AudioRecordPopup(View mainView, Context context, MainActivity mainActivity) {
		super(mainActivity);
		
		m_LayoutInflater = LayoutInflater.from(context);
		m_mainView = mainView;
		
		initView();
		
		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}
	
	private void dismissPopupWindow() {
		mTimer.setBase(SystemClock.elapsedRealtime());
		this.dismiss();
	}
	
	private void initView() {
		m_ContentView = m_LayoutInflater.inflate(R.layout.audio_capture, null);
		setContentView(m_ContentView);
		
		btnStartRecord = (Button)m_ContentView.findViewById(R.id.start_record);
		btnStopRecord = (Button)m_ContentView.findViewById(R.id.stop_record);
		btnOk = (Button)m_ContentView.findViewById(R.id.audio_record_ok);
		btnBackUp = (Button)m_ContentView.findViewById(R.id.audio_cancel);
		
		btnStartRecord.setOnClickListener(this);
		btnStopRecord.setOnClickListener(this);
		btnBackUp.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		
		mTimer = (Chronometer)m_ContentView.findViewById(R.id.chronometer1);
		
	}

	public void setParent(MultiMediaPopup multiMedia) {
		m_MultiMedia = multiMedia;
	}
	
	public void show(View parent) {
		setWidth(ViewGroup.LayoutParams.FILL_PARENT);
		setHeight(ViewGroup.LayoutParams.FILL_PARENT);

		showAtLocation(m_mainView, Gravity.LEFT | Gravity.TOP, 0, 0);
	}
	
	public void refreshList() {

		mTimer.setBase(SystemClock.elapsedRealtime());
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_record:
		{
			m_MDataCollector.startCaptureAudio();
			updateRecordTime(R.id.start_record);
		}
			break;
		case R.id.stop_record:
		{
			m_MDataCollector.stopCaptureAudio();
			updateRecordTime(R.id.stop_record);
		}
			break;
		case R.id.audio_record_ok:
		{
//			try {
//				m_MDataCollector.uploadMediaFiles(m_MultiMedia.m_Rect);	
//			} catch(Exception ex) {
//				ex.printStackTrace();
//			}
			m_MultiMedia.sendMultiMediaFiles();
			dismissPopupWindow();
		}
			break;
		case R.id.audio_cancel:
			dismissPopupWindow();
		default:
			break;
		}
	}
	
	private void updateRecordTime(int id) {
		if (id == R.id.start_record) {
			mTimer.setBase(SystemClock.elapsedRealtime());
			mTimer.start();
		} else if (id == R.id.stop_record) {
			mTimer.stop();
		}
	}
	
	public void setMDataCollector(MDataCollector collector) {
		m_MDataCollector = collector;
	}
}
