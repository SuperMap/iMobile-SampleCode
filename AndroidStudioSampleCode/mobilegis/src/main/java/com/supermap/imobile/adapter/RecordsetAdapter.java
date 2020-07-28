package com.supermap.imobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.supermap.data.FieldInfos;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.imobile.myapplication.R;

public class RecordsetAdapter extends BaseAdapter {
    private Recordset mRecordset ;
    private Context mContext;
    private Point2D point ;
    private FieldInfos mFieldInfos ;
    private int attributeCount = 0;
    public RecordsetAdapter(Recordset recordset,Context context) {
        mRecordset = recordset;
        mContext=context;
        point = mRecordset.getGeometry().getInnerPoint();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int index) {
        return mRecordset.getFieldValue(index);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup arg2) {
        Holder holder = null;

        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_callout_item, null);
            holder = new Holder();
            holder.filed = (TextView) convertView.findViewById(R.id.tv_field);
            holder.filedValue = (TextView) convertView.findViewById(R.id.tv_fieldvalue);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }


        attributeCount ++;
        if (index == 0) {
            holder.filed.setText("坐标X: ");
            if(point != null){
                holder.filedValue.setText(point.getX() + "");
            }else{
                holder.filedValue.setText("");
            }
        }

        if (index == 1) {

            holder.filed.setText("坐标Y: ");
            if (point != null) {
                holder.filedValue.setText(point.getY() + "");
            } else {
                holder.filedValue.setText("");
            }

        }


        if(index >1){

            holder.filed.setText("");

            holder.filedValue.setText("");

        }

        return convertView;
    }

    class Holder{
        TextView filed;
        TextView filedValue;
    }
}
