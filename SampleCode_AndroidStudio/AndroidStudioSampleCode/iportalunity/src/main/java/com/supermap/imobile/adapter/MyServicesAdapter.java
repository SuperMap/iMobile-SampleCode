package com.supermap.imobile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.supermap.imobile.bean.ServiceBaseBean;
import com.supermap.imobile.bean.ServicesBean;
import com.supermap.iportalservices.IPortalService;
import com.supermap.iportalservices.OnResponseListener;
import com.supermap.imobile.iportalservices.MainActivity;
import com.supermap.imobile.iportalservices.MapViewActivity;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.utils.Utils;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyServicesAdapter extends BaseAdapter {

    private static final String TAG = "MyServicesAdapter";
    private ArrayList<ServiceBaseBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;

    public MyServicesAdapter(Context context, ServicesBean servicesBean) {
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
            convertView = mLayoutInflater.inflate(R.layout.item_myservice_listview, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_service_title);
            holder.type = (TextView) convertView.findViewById(R.id.tv_service_type);
            holder.count = (TextView) convertView.findViewById(R.id.tv_visit_count);
            holder.updateTime = (TextView) convertView.findViewById(R.id.tv_update_time);
            holder.createTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.description = (TextView) convertView.findViewById(R.id.tv_description);
            holder.url = (TextView) convertView.findViewById(R.id.tv_url);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_thumbnail);
            holder.more = (ImageButton) convertView.findViewById(R.id.more);
            holder.share = (ImageButton) convertView.findViewById(R.id.share);
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

        holder.more.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MapViewActivity.class);
            intent.putExtra("restUrl",baseBean.getURL());
            mContext.startActivity(intent);
        });

        holder.share.setOnClickListener(v -> {
            showShareServicesDialog(baseBean.getID());
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
        public ImageButton share;
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

    //共享服务
    private void showShareServicesDialog(int ID) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage("是否公开服务?");
//        builder.setIcon(R.mipmap.user);
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
//                    @Override
//                    public void onFailed(Exception exception) {
//                        if (exception != null) {
//                            Log.e(TAG, "" + exception.getMessage());
//                        }
//                        MainActivity mainActivity = (MainActivity) mContext;
//                        mainActivity.runOnUiThread(() -> {
//                            Toast.makeText(mainActivity, "共享失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//                    }
//
//                    @Override
//                    public void onResponse(Response response) {
//                        String responseBody = null;
//                        try {
//                            responseBody = response.body().string();
//                            JSONObject root = new JSONObject(responseBody);
//                            boolean succeed = root.getBoolean("succeed");
//                            MainActivity mainActivity = (MainActivity) mContext;
//                            mainActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (succeed) {
//                                        Toast.makeText(mainActivity, "共享成功", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(mainActivity, "共享失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                IPortalService.getInstance().setServicesShareConfig(ID);
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//            }
//        });
//        builder.show();
    }

}
