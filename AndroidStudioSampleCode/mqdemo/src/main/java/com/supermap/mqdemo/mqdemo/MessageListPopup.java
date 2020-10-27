/**
 *
 */
package com.supermap.mqdemo.mqdemo;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.demo.mqdemo.R;


/**
 * @author zhengyl
 *
 */
public class MessageListPopup extends PopupWindow implements OnClickListener{

	private LayoutInflater m_LayoutInflater = null;
	private View 								m_ContentView = null;
	private View 								m_mainView = null;


	private ListView 							m_lvMessageList;
	private Button								m_btnCancel;

	private List<String>						m_listMessage;
	private List<String>						m_listClientId;
	private Map<String, String>					m_mapMessages;

	private ShowSingleMessage						m_ShowSingleMessage = null;

	public MessageListPopup(View mainView, Context context, MainActivity mainActivity) {
		super(mainActivity);

		m_LayoutInflater = LayoutInflater.from(context);
		m_mainView = mainView;
		m_ShowSingleMessage = new ShowSingleMessage(mainView, context, mainActivity);

		initView();


		setBackgroundDrawable(new ColorDrawable(Color.WHITE));
	}

	private void initView() {
		m_listMessage = new LinkedList<String>();
		m_listClientId = new LinkedList<String>();

		m_ContentView = m_LayoutInflater.inflate(R.layout.message_list, null);
		setContentView(m_ContentView);

//		m_btnCancel = (Button)m_ContentView.findViewById(R.id.message_list_cancel);
		m_btnCancel = (Button)m_ContentView.findViewById(R.id.btn_hide);
		m_btnCancel.setOnClickListener(this);


		m_lvMessageList = (ListView)m_ContentView.findViewById(R.id.lv_message_list);
		m_lvMessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				String time = date.toString();
				m_ShowSingleMessage.refreshList(m_listMessage, m_listClientId, time, position);
				m_ShowSingleMessage.setFocusable(true);
				m_ShowSingleMessage.show();

			}
		});

	}

	@Override
	public void onClick(View v) {
		if (v == m_btnCancel) {
			super.dismiss();
			this.setFocusable(false);
		}
	}

	public void show() {
		setWidth(ViewGroup.LayoutParams.FILL_PARENT);
		setHeight(ViewGroup.LayoutParams.FILL_PARENT);
		showAtLocation(m_mainView, Gravity.LEFT | Gravity.TOP, 0, 0);
	}

	public void refreshList(List<String> listMessages, List<String> listClientIds) {
//		m_listMessage.clear();
//		m_listClientId.clear();
		m_listMessage = listMessages;
		m_listClientId = listClientIds;

		GroupAdapter groupAdapter = new GroupAdapter(this.m_ContentView.getContext(), m_listMessage, m_listClientId);
		m_lvMessageList.setAdapter(groupAdapter);

	}

	public class GroupAdapter extends BaseAdapter {
		private Context 		m_context;
		private List<String> 	m_listMessages;
		private List<String>	m_listClientId;

		public GroupAdapter(Context context, List<String> listMessages, List<String> listClientId) {
			this.m_context = context;
			this.m_listMessages = listMessages;
			this.m_listClientId = listClientId;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_listMessages.size();
		}

		@Override
		public Object getItem(int position) {
			if (m_listMessages.size() == 0) {
				return null;
			} else {
				return m_listMessages.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder viewHolder = null;

			if (convertView == null) {
				convertView = m_LayoutInflater.inflate(R.layout.messageitem, null);
				viewHolder = new ViewHolder();

				convertView.setTag(viewHolder);
				viewHolder.textMessage = (TextView)convertView.findViewById(R.id.message_describe);
				viewHolder.textClientId = (TextView)convertView.findViewById(R.id.message_sender);

			} else {
				viewHolder= (ViewHolder)convertView.getTag();
			}
			viewHolder.textMessage.setText(this.m_listMessages.get(position));
			viewHolder.textClientId.setText(this.m_listClientId.get(position));
			return convertView;
		}


	}
	static class ViewHolder {
		TextView textMessage;
		TextView textClientId;
	}

}
