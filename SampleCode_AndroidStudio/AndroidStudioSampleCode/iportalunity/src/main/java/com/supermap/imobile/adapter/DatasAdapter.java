package com.supermap.imobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.supermap.imobile.bean.DataBaseBean;
import com.supermap.imobile.bean.DatasBean;
import com.supermap.imobile.control.DatasControl;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatasAdapter extends BaseAdapter {

    private ArrayList<DataBaseBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;

    public DatasAdapter(Context context, DatasBean datasBean) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        formate(datasBean);
    }

    public void setData(DatasBean datasBean) {
        formate(datasBean);
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
            convertView = mLayoutInflater.inflate(R.layout.item_datas_listview, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_thumbnail);
            holder.filename = (TextView) convertView.findViewById(R.id.tv_filename);
            holder.tags = (TextView) convertView.findViewById(R.id.tv_tags);
            holder.size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.lastModfiedTime = (TextView) convertView.findViewById(R.id.tv_lastmodfied_time);
            holder.owner = (TextView) convertView.findViewById(R.id.tv_owner);
            holder.downloadCount = (TextView) convertView.findViewById(R.id.tv_downloadCount);
            holder.download = (Button) convertView.findViewById(R.id.btn_download);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DataBaseBean baseBean = mList.get(position);

        Glide.with(mContext)
                .load(baseBean.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.map)
                .into(holder.imageView);
        holder.filename.setText(baseBean.getFileName());
        holder.tags.setText(baseBean.getTags());
        String fileSize = Utils.readableFileSize(baseBean.getSize());
        holder.size.setText(fileSize);
        String lastModifiesTime = Utils.parseTime(baseBean.getLastModfiedTime());
        holder.lastModfiedTime.setText(lastModifiesTime);
        holder.owner.setText(baseBean.getOwner());
        holder.downloadCount.setText("" + baseBean.getDownloadCount());
        holder.download.setText(baseBean.getType());

        holder.download.setOnClickListener(v -> {
            if (isDownLoading) {
                Toast.makeText(mContext, "请等待当前任务完成", Toast.LENGTH_SHORT).show();
            } else {
                String filePath = Utils.getIPortalPath() +  "资源中心/" + baseBean.getType() + "/" + baseBean.getFileName();
                DatasControl.getInstance().downLoadData(filePath, baseBean.getID());
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView filename;
        public TextView size;
        public TextView tags;
        public TextView owner;
        public TextView lastModfiedTime;
        public TextView downloadCount;
        public Button download;
    }

    private void formate(DatasBean datasBean) {
        mList.clear();
        int pageSize = datasBean.getPageSize();
        int total = datasBean.getTotal();
        int counts = 0;
        if (total >= pageSize) {
            counts = pageSize;
        } else {
            counts = total;
        }
        for (int i = 0; i < counts; i++) {
            DatasBean.ContentBean contentBean = datasBean.getContent().get(i);
            String fileName = contentBean.getFileName();
            String type = contentBean.getType();
            List<String> tagsList = contentBean.getTags();
            int size = contentBean.getSize();
            long lastModfiedTime = contentBean.getLastModfiedTime();

            long createTime = contentBean.getCreateTime();
            String thumbnail = contentBean.getThumbnail();
            int downloadCount = contentBean.getDownloadCount();
            String serviceStatus = contentBean.getServiceStatus();
            int ID = contentBean.getId();
            String owner = contentBean.getNickname();

            String tags = "";
            if (tagsList != null) {
                for (int j = 0; j < tagsList.size(); j++) {
                    if (tags.isEmpty()) {
                        tags += tagsList.get(j);
                    } else {
                        tags += tags + "," + tagsList.get(j);
                    }
                }
            }

            DataBaseBean baseBean = new DataBaseBean.Builder(fileName, type, size, lastModfiedTime)
                    .setCreateTime(createTime)
                    .setThumbnail(thumbnail)
                    .setDownloadCount(downloadCount)
                    .setServiceStatus(serviceStatus)
                    .setID(ID)
                    .setOwner(owner)
                    .setTags(tags)
                    .build();
            mList.add(baseBean);
        }
        //排序
//        Collections.sort(mList, new SortByTime());
    }

    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            DataBaseBean b1 = (DataBaseBean) o1;
            DataBaseBean b2 = (DataBaseBean) o2;
            if (b1.getLastModfiedTime() < b2.getLastModfiedTime()) {
                return 1;
            }
            return -1;
        }
    }

    private boolean isDownLoading = false;
    public void isDownloading(boolean isDownLoading) {
        this.isDownLoading = isDownLoading;
    }

}
