package com.supermap.imobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.supermap.imobile.bean.ServicesBean;
import com.supermap.imobile.bean.ServiceBaseBean;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ServicesAdapter extends BaseAdapter {

    private ArrayList<ServiceBaseBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;

    public ServicesAdapter(Context context, ServicesBean servicesBean) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        formate(servicesBean);
    }

    public void setData(ServicesBean servicesBean) {
        formate(servicesBean);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_service_listview, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_service_title);
            holder.type = (TextView) convertView.findViewById(R.id.tv_service_type);
            holder.count = (TextView) convertView.findViewById(R.id.tv_visit_count);
            holder.updateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
            holder.createTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.description = (TextView) convertView.findViewById(R.id.tv_description);
            holder.url = (TextView) convertView.findViewById(R.id.tv_url);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ServiceBaseBean baseBean = mList.get(position);

        Glide.with(mContext)
                .load(baseBean.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.map)
                .into(holder.imageView);
        holder.title.setText(baseBean.getTitle());
        holder.type.setText(baseBean.getType());
        holder.count.setText(String.valueOf(baseBean.getCount()));
        holder.updateTime.setText(Utils.parseTime(baseBean.getUpdateTime()));
        holder.createTime.setText(Utils.parseTime(baseBean.getCreateTime()));
        holder.description.setText(baseBean.getDescription());
        holder.url.setText(baseBean.getURL());

        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView type;
        public TextView count;
        public TextView updateTime;
        public TextView createTime;
        public TextView description;
        public TextView url;
    }

    private void formate(ServicesBean servicesBean) {
        mList.clear();
        int pageSize = servicesBean.getPageSize();
        int total = servicesBean.getTotal();
        int counts = 0;
        if (total >= pageSize) {
            counts = pageSize;
        } else {
            counts = total;
        }
        for (int i = 0; i < counts; i++) {
            ServicesBean.ContentBean contentBean = servicesBean.getContent().get(i);
            int ID = contentBean.getId();
            String title = contentBean.getResTitle();
            String type = contentBean.getType();
            String thumbnail = contentBean.getThumbnail();
            int count = contentBean.getVisitCount();
            long updateTime = contentBean.getUpdateTime();
            long createTime = contentBean.getCreateTime();
            String description = contentBean.getDescription();
            boolean enable = contentBean.isEnable();
            ServiceBaseBean baseBean = new ServiceBaseBean.Builder(title,ID,type,thumbnail)
                    .isEnable(enable)
                    .setCount(count)
                    .setcreateTime(createTime)
                    .setupdateTime(updateTime)
                    .setdescription(description)
                    .build();
            mList.add(baseBean);
        }
        //排序
        Collections.sort(mList, new SortByTime());
    }

    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            ServiceBaseBean b1 = (ServiceBaseBean) o1;
            ServiceBaseBean b2 = (ServiceBaseBean) o2;
            if (b1.getUpdateTime() < b2.getUpdateTime()){
                return 1;
            }
            return -1;
        }
    }

}
