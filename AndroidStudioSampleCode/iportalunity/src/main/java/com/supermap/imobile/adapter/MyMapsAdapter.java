package com.supermap.imobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.supermap.imobile.bean.MapBaseBean;
import com.supermap.imobile.bean.MapsBean;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.iportalservices.WebMapActivity;
import com.supermap.imobile.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyMapsAdapter extends BaseAdapter {

    private ArrayList<MapBaseBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;

    public MyMapsAdapter(Context context, MapsBean mapsBean) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        formate(mapsBean);
    }

    public void setData(MapsBean mapsBean) {
        formate(mapsBean);
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
            convertView = mLayoutInflater.inflate(R.layout.item_mymaps_listview, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_maptitle);
            holder.type = (TextView) convertView.findViewById(R.id.tv_maptype);
            holder.count = (TextView) convertView.findViewById(R.id.tv_visit_count);
            holder.updateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
            holder.createTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.description = (TextView) convertView.findViewById(R.id.tv_description);
            holder.url = (TextView) convertView.findViewById(R.id.tv_url);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_thumbnail);
            holder.more = (ImageButton) convertView.findViewById(R.id.more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MapBaseBean baseBean = mList.get(position);

        Glide.with(mContext)
                .load(baseBean.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.map)
                .into(holder.imageView);
        holder.title.setText(baseBean.getMapTitle());
        holder.type.setText(baseBean.getMapType());
        holder.count.setText(String.valueOf(baseBean.getCount()));
        holder.updateTime.setText(Utils.parseTime(baseBean.getUpdateTime()));
        holder.createTime.setText(Utils.parseTime(baseBean.getCreateTime()));
        holder.description.setText(baseBean.getDescription());
        holder.url.setText(baseBean.getURL());

        holder.more.setOnClickListener(v ->{
            Intent intent = new Intent(mContext, WebMapActivity.class);
            intent.putExtra("MapId",baseBean.getID());
            intent.putExtra("Thumbnail",baseBean.getThumbnail());
            mContext.startActivity(intent);
        });

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
        public ImageButton more;
    }

    private void formate(MapsBean mapsBean) {
        mList.clear();
        int pageSize = mapsBean.getPageSize();
        int total = mapsBean.getTotal();
        int counts = 0;
        if (total >= pageSize) {
            counts = pageSize;
        } else {
            counts = total;
        }
        for (int i = 0; i < counts; i++) {
            MapsBean.ContentBean contentBean = mapsBean.getContent().get(i);
            int ID = contentBean.getId();
            String mapTitle = contentBean.getTitle();
            String mapType = contentBean.getSourceType();
            String thumbnail = contentBean.getThumbnail();
            int count = contentBean.getVisitCount();
            long updateTime = contentBean.getUpdateTime();
            long createTime = contentBean.getCreateTime();
            String description = contentBean.getDescription();
            MapBaseBean baseBean = new MapBaseBean.Builder(mapTitle,ID,mapType,thumbnail)
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
            MapBaseBean b1 = (MapBaseBean) o1;
            MapBaseBean b2 = (MapBaseBean) o2;
            if (b1.getUpdateTime() < b2.getUpdateTime()){
                return 1;
            }
            return -1;
        }
    }

}
