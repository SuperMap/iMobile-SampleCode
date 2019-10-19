package com.supermap.imobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.supermap.imobile.control.MyDatasControl;
import com.supermap.imobile.bean.DataBaseBean;
import com.supermap.imobile.bean.DatasBean;
import com.supermap.imobile.iportalservices.MainActivity;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MyDatasAdapter extends BaseAdapter {

    private ArrayList<DataBaseBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;

    public MyDatasAdapter(Context context, DatasBean datasBean) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        formate(datasBean);
    }

    public void setData(DatasBean datasBean) {
        formate(datasBean);
    }

    public void deleteItem(int ID) {
        Iterator<DataBaseBean> it = mList.iterator();
        while(it.hasNext()){
            DataBaseBean bean = it.next();
            if(bean.getID() == ID){
                it.remove();
                break;
            }
        }
        MainActivity mainActivity = (MainActivity) mContext;
        mainActivity.runOnUiThread(() -> notifyDataSetChanged());
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
            convertView = mLayoutInflater.inflate(R.layout.item_mydatas_listview, null);
            holder = new ViewHolder();
            holder.more = convertView.findViewById(R.id.more);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_thumbnail);
            holder.filename = (TextView) convertView.findViewById(R.id.tv_filename);
            holder.type = (TextView) convertView.findViewById(R.id.tv_type);
            holder.size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.lastModfiedTime = (TextView) convertView.findViewById(R.id.tv_lastmodfied_time);
            holder.serviceStatus = (TextView) convertView.findViewById(R.id.tv_service_status);
            holder.MD5 = (TextView) convertView.findViewById(R.id.tv_MD5);
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
        holder.type.setText(baseBean.getType());
        String fileSize = Utils.readableFileSize(baseBean.getSize());
        holder.size.setText(fileSize);
        String lastModifiesTime = Utils.parseTime(baseBean.getLastModfiedTime());
        holder.lastModfiedTime.setText(lastModifiesTime);
        if (baseBean.getServiceStatus().equals("PUBLISHED")) {
            holder.serviceStatus.setText("已发布");
        } else {
            holder.serviceStatus.setText("未发布");
        }
        holder.MD5.setText(baseBean.getMD5());
        holder.download.setText(baseBean.getType());

        holder.download.setOnClickListener(v -> {
            if (isDownLoading) {
                Toast.makeText(mContext, "请等待当前任务完成", Toast.LENGTH_SHORT).show();
            } else {
                String filePath = Utils.getIPortalPath()+  "我的资源/" + baseBean.getType() + "/" + baseBean.getFileName();
                MyDatasControl.getInstance().downLoadData(filePath, baseBean.getID());
            }
        });

        holder.more.setOnClickListener(v ->{
            showPublishServicesDialog(baseBean.getID());
        });

        holder.delete.setOnClickListener(v ->{
            showDeleteDialog(baseBean.getID());
        });

        return convertView;
    }

    public static class ViewHolder {
        public ImageButton more;
        public ImageButton delete;
        public ImageView imageView;
        public TextView filename;
        public TextView size;
        public TextView type;
        public TextView serviceStatus;
        public TextView lastModfiedTime;
        public TextView MD5;
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
            int size = contentBean.getSize();
            long lastModfiedTime = contentBean.getLastModfiedTime();

            long createTime = contentBean.getCreateTime();
            String thumbnail = contentBean.getThumbnail();
            int downloadCount = contentBean.getDownloadCount();
            String serviceStatus = contentBean.getServiceStatus();
            int ID = contentBean.getId();
            String MD5 = contentBean.getMD5();

            DataBaseBean baseBean = new DataBaseBean.Builder(fileName, type, size, lastModfiedTime)
                    .setCreateTime(createTime)
                    .setThumbnail(thumbnail)
                    .setDownloadCount(downloadCount)
                    .setServiceStatus(serviceStatus)
                    .setID(ID)
                    .setMD5(MD5)
                    .build();
            mList.add(baseBean);
        }
        //排序
        Collections.sort(mList, new SortByTime());
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

    //是否删除
    private void showDeleteDialog(int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否删除此数据?");
        builder.setIcon(R.mipmap.user);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MyDatasControl.getInstance().deleteItem(ID);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

    //是否发布服务
    private void showPublishServicesDialog(int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否发布服务?");
        builder.setIcon(R.mipmap.user);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MyDatasControl.getInstance().publishServices(ID);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();
    }

}
