/**
 *
 */
package com.supermap.mqdemo.mqdemo;

import java.util.LinkedList;
import java.util.List;


import com.supermap.demo.mqdemo.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author zhengyl
 *
 */
public class ShowSingleMessage extends PopupWindow  implements OnClickListener{

	private LayoutInflater m_LayoutInflater = null;
	private View 								m_ContentView = null;
	private View 								m_mainView = null;

	private ListView 							m_lvMessageList;
	private Button								m_btnCancel;

	private TextView							m_textSender = null;
	private TextView							m_textTime = null;
	private TextView							m_editTextMessage = null;



	public ShowSingleMessage(View mainView, Context context, MainActivity mainActivity) {
		super(mainActivity);

		m_LayoutInflater = LayoutInflater.from(context);
		m_mainView = mainView;
		initView();


		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}

	private void initView() {

		m_ContentView = m_LayoutInflater.inflate(R.layout.single_message_show, null);
		setContentView(m_ContentView);

		m_btnCancel = (Button)m_ContentView.findViewById(R.id.single_message_cancel);
		m_btnCancel.setOnClickListener(this);

		m_textSender = (TextView)m_ContentView.findViewById(R.id.single_message_sender2);
		m_textTime 	= (TextView)m_ContentView.findViewById(R.id.single_send_time_2);
		m_editTextMessage 	= (TextView)m_ContentView.findViewById(R.id.single_message);
	}

	@Override
	public void onClick(View v) {
		if (v == m_btnCancel) {
			super.dismiss();
			this.setFocusable(false);
			dismiss();
		}
	}

	public void show() {
		setWidth(ViewGroup.LayoutParams.FILL_PARENT);
		setHeight(ViewGroup.LayoutParams.FILL_PARENT);
		showAtLocation(m_mainView, Gravity.LEFT | Gravity.TOP, 0, 0);
	}

	public void refreshList(List<String> listMessages, List<String> listClientIds, String time, int position) {
		if (listClientIds.size() == 0 || listMessages.size() == 0 || position >= listMessages.size()) {
			return ;
		}
		String message = listMessages.get(position);
		String clientId = listClientIds.get(position);

		m_textSender.setText(clientId);
		m_textTime.setText(time);
		m_editTextMessage.setText(message);

	}

}
