package com.supermap.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.supermap.instance.LineSolution;
import com.supermap.onlinedemo.R;

public class LineSolutionAdapter extends BaseAdapter{

	private Context context;
	private List<LineSolution> list; 
	
	public LineSolutionAdapter(Context context,List<LineSolution> list){
		this.context=context;
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_line_solution, parent,false);
			viewHolder.tvStartName=(TextView) convertView.findViewById(R.id.tvStartName_als);
			viewHolder.tvEndName=(TextView) convertView.findViewById(R.id.tvEndName_als);
			viewHolder.tvWalkDistance=(TextView) convertView.findViewById(R.id.tvWalkDistance_als);
			viewHolder.tvPassStopCount=(TextView) convertView.findViewById(R.id.tvPassStopCount_als);
			viewHolder.tvDirection=(TextView) convertView.findViewById(R.id.tvDirection_als);
			viewHolder.tvTrafficInfo=(TextView) convertView.findViewById(R.id.tvTrafficTool_als);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		LineSolution lineSolution=(LineSolution) getItem(position);
		viewHolder.tvStartName.setText("起点："+lineSolution.getStartName());
		viewHolder.tvEndName.setText("终点："+lineSolution.getEndName());
		viewHolder.tvWalkDistance.setText("步行距离："+lineSolution.getWalkDistance()+" m");
		viewHolder.tvPassStopCount.setText(lineSolution.getPassStopCount()+" 站");
		viewHolder.tvDirection.setText(lineSolution.getDirection());
		viewHolder.tvTrafficInfo.setText(lineSolution.getTrafficInfo());
		return convertView;
	}

	
	class ViewHolder{
		TextView tvStartName,tvEndName,tvWalkDistance,
					tvPassStopCount,tvDirection,tvTrafficInfo;
	}
	
}
