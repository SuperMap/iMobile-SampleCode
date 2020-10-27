/**
 *
 */
package com.supermap.mqdemo.mqdemo;

import com.supermap.demo.mqdemo.R;
import com.supermap.mdatacollector.MDataCollector;
import com.supermap.services.DataDownloadService;
import com.supermap.services.DataUploadService;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * @author zhengyl
 *
 */
public class TextMessagePopup extends PopupWindow  implements OnClickListener{

	private LayoutInflater m_LayoutInflater = null;
	private View 								m_ContentView = null;
	private View 								m_mainView = null;

	private MainActivity						m_MainActivity = null;

	private EditText							mMessageContent;

	public TextMessagePopup(View mainView, Context context, MainActivity mainActivity) {
		super(mainActivity);

		m_LayoutInflater = LayoutInflater.from(context);
		m_mainView = mainView;
		m_MainActivity = mainActivity;

		initView();
		// 避免软键盘遮挡
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}


	private void initView() {
		m_ContentView = m_LayoutInflater.inflate(R.layout.text_message_popup, null);
		setContentView(m_ContentView);

		((Button) m_ContentView.findViewById(R.id.cancel)).setOnClickListener(this);
		((Button) m_ContentView.findViewById(R.id.send)).setOnClickListener(this);

		mMessageContent = (EditText)m_ContentView.findViewById(R.id.et_text_message_content);
	}


	public void show() {
		setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

		showAtLocation(m_mainView, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0,0);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.cancel:
			{
				mMessageContent.getText().clear();
				this.dismiss();
			}
			break;
			case R.id.send:
			{
				String sTextMessage = mMessageContent.getText().toString();
				mMessageContent.clearFocus();
				mMessageContent.getText().clear();

				if (sTextMessage.isEmpty()) {
					MyApplication.getInstance().showInfo("消息内容不能为空！");
				} else {
					if (m_MainActivity != null && m_MainActivity.m_MessageQueue != null) {
						String msg = "{content_type=3}" + sTextMessage;
						boolean bSend = m_MainActivity.m_MessageQueue.sendMessageByType(msg, 3);
						if (bSend) {
							MyApplication.getInstance().showInfo("消息发送成功！");
						} else {
							MyApplication.getInstance().showInfo("消息发送失败，请重新发送！");
						}
					}
				}


				this.dismiss();
			}
			break;
			default:
				this.dismiss();
				break;
		}
	}

}
