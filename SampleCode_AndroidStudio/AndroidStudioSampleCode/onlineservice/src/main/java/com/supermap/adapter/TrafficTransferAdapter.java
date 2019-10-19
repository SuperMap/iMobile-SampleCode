package com.supermap.adapter;

import java.util.List;

import com.supermap.instance.TrafficTransfer;
import com.supermap.onlinedemo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrafficTransferAdapter extends BaseAdapter{

	private List<TrafficTransfer> trafficTransfers;
	private Context context;
	public TrafficTransferAdapter(List<TrafficTransfer> trafficTransfers,Context context){
		this.trafficTransfers=trafficTransfers;
		this.context=context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return trafficTransfers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return trafficTransfers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		if(convertView == null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_traffic_transfer_solutions, parent,false);
			viewHolder.tvTrafficInfo=(TextView) convertView.findViewById(R.id.tvTrafficInfo_ttsi);
			viewHolder.tvWalkDistance=(TextView) convertView.findViewById(R.id.tvWalkDistance_ttsi);
			viewHolder.tvTransferCount=(TextView) convertView.findViewById(R.id.tvTransferCount_ttsi);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		TrafficTransfer trafficTransfer=(TrafficTransfer) getItem(position);
		if(trafficTransfer != null){
			viewHolder.tvTrafficInfo.setText(trafficTransfer.getTrafficInfo());
			viewHolder.tvWalkDistance.setText(trafficTransfer.getWalkDistance()+"");
			viewHolder.tvTransferCount.setText(trafficTransfer.getTransferCount()+"");
		}
		return convertView;
	}
	
	class ViewHolder{
		TextView tvTrafficInfo=null;
		TextView tvWalkDistance=null;
		TextView tvTransferCount=null;
	}
	
}
