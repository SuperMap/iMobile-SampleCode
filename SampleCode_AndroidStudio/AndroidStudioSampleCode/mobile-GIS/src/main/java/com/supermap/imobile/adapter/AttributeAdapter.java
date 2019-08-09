package com.supermap.imobile.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.Recordset;
import com.supermap.imobile.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性表适配器类，用于适配记录集的属性集合
 */
public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.ViewHolder> {

    private FieldInfos mFieldInfos;//字段集
    private Recordset mRecordset;//记录
    List<FieldInfo> mFieldInfoList = new ArrayList<FieldInfo>();//字段列表

    private int tempPos = -1;//上一次点击的位置，默认值为 -1，即为未点击过
    private boolean isSelect = false;//是否选中，默认为false

    OnItemListener mOnItemListener = null;

    int defItem = -100;//设置当前点击的位置，默认为-100

    /**
     * 构造函数
     *
     * @param mRecordset 记录集
     */
    public AttributeAdapter(Recordset mRecordset) {

        this.mRecordset = mRecordset;

        initData();//数据准备
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_attribute, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        //根据选中的item位置，设置不同样式风格
        if (defItem == i) {
            if (defItem != tempPos) {//如果当前点击的item不等于上一次点击位置，则设置不同样式
                viewHolder.layout.setBackgroundColor(android.graphics.Color.parseColor("#53ccc3"));
                tempPos = defItem;//临时位置=当前点击item位置
                isSelect = true;//设置已被选中
            } else if (defItem == tempPos) {//如果点击item==上一次点击的位置，则取消当前点击的item背景色，取消选中项
                viewHolder.layout.setBackgroundColor(android.graphics.Color.parseColor("#008577"));
                tempPos = -1;
                isSelect = false;//设置未被选中
            }

        } else {
            viewHolder.layout.setBackgroundColor(android.graphics.Color.parseColor("#008577"));
        }
        viewHolder.tv_field.setText(mFieldInfoList.get(i).getCaption());
        Object value = mRecordset.getFieldValue(mFieldInfoList.get(i).getCaption());//根据字段名称，查询字段值
        //如果字段值为空，则填入“”
        if (value == null) {
            viewHolder.tv_fieldValue.setText("");
        } else {
            viewHolder.tv_fieldValue.setText(String.valueOf(value));
        }


        //绑定监听
        viewHolder.tv_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemListener.onClick(i, mFieldInfoList.get(i).getCaption());
            }
        });
        //绑定监听
        viewHolder.tv_fieldValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemListener.onClick(i, mFieldInfoList.get(i).getCaption());
            }
        });
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    /**
     * 判断是否当前是否有item被选中
     *
     * @return
     */
    public boolean getItemSelect() {
        return isSelect;
    }

    @Override
    public int getItemCount() {
        return mFieldInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_field;//字段名称
        TextView tv_fieldValue; //字段值
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_field = itemView.findViewById(R.id.tv_field);
            tv_fieldValue = itemView.findViewById(R.id.tv_fieldValue);
            layout = itemView.findViewById(R.id.layout_attribute);
        }
    }

    /**
     * 准备数据
     *
     * @Note 系统字段不进行显示及操作
     */
    private void initData() {
        mFieldInfos = mRecordset.getFieldInfos();
        for (int i = 0; i < mRecordset.getFieldCount(); i++) {
            if (mFieldInfos.get(i).getName().equals("SmSdriW")
                    || mFieldInfos.get(i).getName().equals("SmSdriN")
                    || mFieldInfos.get(i).getName().equals("SmSdriE")
                    || mFieldInfos.get(i).getName().equals("SmSdriS")
                    || mFieldInfos.get(i).getName().equals("SmPerimeter")
                    || mFieldInfos.get(i).getName().equals("SmArea")
                    || mFieldInfos.get(i).getName().equals("SmLength")
                    || mFieldInfos.get(i).getName().equals("SmUserID")
                    || mFieldInfos.get(i).getName().equals("SmTopoError")
                    || mFieldInfos.get(i).getName().equals("SmGeometrySize")
                    || mFieldInfos.get(i).getName().equals("SmGeoPosition")
                    || mFieldInfos.get(i).getName().equals("SmX")
                    || mFieldInfos.get(i).getName().equals("SmY")
                    || mFieldInfos.get(i).getName().equals("SmLibTileID")
                    || mFieldInfos.get(i).getName().equals("SmPermeter")) {
//                return;
            } else {
                mFieldInfoList.add(mFieldInfos.get(i));
            }
        }
    }


    /**
     * 设置监听事件
     *
     * @param mOnItemListener
     */
    public void setOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    /**
     * 位置监听
     */
    public interface OnItemListener {
        /**
         * 回到函数
         *
         * @param pos     当前点击位置
         * @param Caption 字段别名，后买那需要通过字段别名判断当前点击位置
         */
        void onClick(int pos, String Caption);
    }



    /**
     * 设置当前点击位置
     *
     * @param position
     */
    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }
}
