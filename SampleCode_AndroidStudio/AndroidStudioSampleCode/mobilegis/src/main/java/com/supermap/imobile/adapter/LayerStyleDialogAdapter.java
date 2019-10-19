package com.supermap.imobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.supermap.imobile.myapplication.R;

import java.util.List;

/**
 * 图层风格对话框适配器，
 */
public class LayerStyleDialogAdapter extends BaseAdapter {
    Context mContext;
    private List<String> mContentList;//内容列表

    /**
     * @param mContentLists
     * @param mContext
     */
    public LayerStyleDialogAdapter(List<String> mContentLists, Context mContext) {
        this.mContentList = mContentLists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public Object getItem(int i) {
        return mContentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layerstyle_spinner_item, null);//设置对应显示item子项布局
            holder.txtView = convertView.findViewById(R.id.layerstyle_spinner_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 填充数据
        String str = mContentList.get(position);
        if (str.startsWith("#")) {
            holder.txtView.setText("");
            holder.txtView.setBackgroundColor(android.graphics.Color.parseColor(str));
        } else {
            holder.txtView.setText(str);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView txtView;
    }
}
