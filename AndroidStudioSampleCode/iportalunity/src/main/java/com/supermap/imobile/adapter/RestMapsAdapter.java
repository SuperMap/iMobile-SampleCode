package com.supermap.imobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.supermap.imobile.bean.OpenRestMapEvent;
import com.supermap.imobile.bean.RestMapsBean;
import com.supermap.imobile.iportalservices.R;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class RestMapsAdapter extends BaseAdapter {

    private ArrayList<RestMapsBean> mList = new ArrayList();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;
    private String restUrl = null;

    public RestMapsAdapter(Context context, ArrayList<RestMapsBean> restMapsBean, String restUrl) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = restMapsBean;
        this.restUrl = restUrl;
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
            convertView = mLayoutInflater.inflate(R.layout.item_restmaps_listview, null);
            holder = new ViewHolder();
            holder.entireImage = convertView.findViewById(R.id.entireImage);
            holder.tv_resourceConfigID = (TextView) convertView.findViewById(R.id.tv_resourceConfigID);
            holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);

            holder.tv_resourceType = (TextView) convertView.findViewById(R.id.tv_resourceType);
            holder.tv_restname = (TextView) convertView.findViewById(R.id.tv_restname);
            holder.more = (ImageButton) convertView.findViewById(R.id.more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RestMapsBean restMapsBean = mList.get(position);

        String entireImageUrl = restUrl + "/maps/" + restMapsBean.getName() + "/entireImage.png";
        Glide.with(mContext)
                .load(entireImageUrl)
                .centerCrop()
                .placeholder(R.drawable.map)
                .into(holder.entireImage);
        holder.tv_resourceConfigID.setText("" + restMapsBean.getResourceConfigID());
//        holder.tv_path.setText("" + restUrl + "/maps/" + restMapsBean.getName());
        holder.tv_path.setText("" + restMapsBean.getPath());
        holder.tv_resourceType.setText("" + restMapsBean.getResourceType());
        holder.tv_restname.setText("" + restMapsBean.getName());

        holder.more.setOnClickListener(v -> {
        });

        CharSequence text = holder.tv_path.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) text;
            URLSpan urls[] = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            for (URLSpan urlSpan : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(urlSpan.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(urlSpan),
                        sp.getSpanEnd(urlSpan),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            }
            holder.tv_path.setText(style);
        }

        return convertView;
    }

    private class MyURLSpan extends ClickableSpan {

        private String url;

        public MyURLSpan(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View arg0) {
            OpenRestMapEvent openRestMapEvent = new OpenRestMapEvent.Builder()
                    .setMode("MapViewFragment")
                    .setUrl(this.url)
                    .build();
            EventBus.getDefault().post(openRestMapEvent);
        }

    }

    public static class ViewHolder {
        public ImageView entireImage;
        public TextView tv_resourceConfigID;
        public TextView tv_path;
        public TextView tv_resourceType;
        public TextView tv_restname;
        public ImageButton more;
    }

}
