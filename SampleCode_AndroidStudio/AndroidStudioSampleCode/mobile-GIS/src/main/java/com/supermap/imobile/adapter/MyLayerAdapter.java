package com.supermap.imobile.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.data.DatasetType;
import com.supermap.imobile.Pop.LayerSettingPop;
import com.supermap.imobile.bean.MyLayerData;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;

import java.util.ArrayList;
import java.util.List;


//图层数据适配类
public class MyLayerAdapter extends RecyclerView.Adapter<MyLayerAdapter.ViewHolder> {

    //图层数据集合
    public List<MyLayerData> myLayerDataList = new ArrayList<MyLayerData>();
    Context mContext;
    MapControl mMapControl;

    /**
     *
     * @param myLayerDataList
     * @param mMapControl
     */
    public MyLayerAdapter(List<MyLayerData> myLayerDataList, MapControl mMapControl) {
        this.myLayerDataList = myLayerDataList;
        this.mMapControl = mMapControl;
        mContext = mMapControl.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layer, viewGroup, false);//设置显示item子项布局
        final ViewHolder holder = new ViewHolder(view);// 绑定子view
        //添加每个按钮点击事件
        holder.imageView_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();//获取当前点击位置
                MyLayerData data = myLayerDataList.get(position);//获取当前点击位置数据
                Layer layer = data.getLayer();//获取当前点击数据图层
                if (layer.isVisible()) {//判断是否可见，若是已经可见，则设置为不可见
                    holder.imageView_visible.setBackgroundResource(R.drawable.btn_visible_pressed);
                    layer.setVisible(false);//设置图层不可见
                } else {
                    holder.imageView_visible.setBackgroundResource(R.drawable.btn_visible_current);
                    layer.setVisible(true);//设置图层可见
                }
            }
        });

        holder.imageView_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                MyLayerData data = myLayerDataList.get(position);
                Layer layer = data.getLayer();
                if (layer.getTheme() != null) {
                }// 如果图层为专题图，不可进行选择
                else {
                    if (layer.isSelectable()) {
                        holder.imageView_select.setBackgroundResource(R.drawable.btn_select_pressed);
                        layer.setSelectable(false);
                    } else {
                        holder.imageView_select.setBackgroundResource(R.drawable.btn_select_current);
                        layer.setSelectable(true);
                    }
                }

            }
        });

        holder.imageView_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                MyLayerData data = myLayerDataList.get(position);
                Layer layer = data.getLayer();
                if (layer.getTheme() != null) {
                }// 如果图层为专题图，不可进行编辑
                else if (layer.getCaption().equals("UserPoint@Changchun")
                        ||layer.getCaption().equals("User_Line@Changchun")
                        ||layer.getCaption().equals("UserText@Changchun")
                        ||layer.getCaption().equals("UserCAD@Changchun")
                        ||layer.getCaption().equals("UserRegion@Changchun")){
                    if (layer.isEditable()) {
                        holder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_pressed);
                        layer.setEditable(false);
                    } else {
                        holder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_current);
                        layer.setEditable(true);
                        for (int i = 0; i < myLayerDataList.size(); i++) {
                            MyLayerData tmpdata = myLayerDataList.get(i);
                            if (tmpdata != data) {
                                tmpdata.getLayer().setEditable(false);//除当前编辑图层外，其余图层都为不可编辑图层
                            }
                        }
                        notifyDataSetChanged();//刷新数据

                    }
                }


            }
        });
        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LayerSettingPop(holder);
                int position = holder.getAdapterPosition();
                MyLayerData data = myLayerDataList.get(position);
                Layer layer = data.getLayer();
                LayerSettingPop layerSettingPop = new LayerSettingPop(mMapControl, position);
                layerSettingPop.adapter = MyLayerAdapter.this;
                if (layer.getTheme() != null) {
                    layerSettingPop.hideStylelayout();
                    layerSettingPop.ShowPop(holder.btn_more);
                } else {//只有当数据集为点、线、面时，可是设置设置图层颜色
                    if (layer.getDataset().getType().equals(DatasetType.POINT)
                            || layer.getDataset().getType().equals(DatasetType.LINE)
                            || layer.getDataset().getType().equals(DatasetType.REGION)) {
                        layerSettingPop.ShowPop(holder.btn_more);
                    } else {
                        layerSettingPop.hideStylelayout();
                        layerSettingPop.ShowPop(holder.btn_more);

                    }
                }
            }

        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //数据初始化时界面视图
        MyLayerData myLayerData = myLayerDataList.get(i);
        Layer layer = myLayerData.getLayer();
        //是否可见
        if (layer.isVisible()) {
            viewHolder.imageView_visible.setBackgroundResource(R.drawable.btn_visible_current);
        } else {
            viewHolder.imageView_visible.setBackgroundResource(R.drawable.btn_visible_pressed);
        }
        //是否可选择
        if (layer.isSelectable()) {
            viewHolder.imageView_select.setBackgroundResource(R.drawable.btn_select_current);
        } else {
            if (layer.getTheme() == null)
                viewHolder.imageView_select.setBackgroundResource(R.drawable.btn_select_pressed);
            else {
                viewHolder.imageView_select.setBackgroundResource(R.drawable.btn_slect_theme);
            }
        }
        //是否编辑,只有user几个图层可进行编辑
        if (layer.isEditable()) {
            if (layer.getCaption().equals("UserPoint@Changchun")
                    ||layer.getCaption().equals("User_Line@Changchun")
                    ||layer.getCaption().equals("UserText@Changchun")
                    ||layer.getCaption().equals("UserCAD@Changchun")
                    ||layer.getCaption().equals("UserRegion@Changchun")){
            viewHolder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_current);}
            else {
                viewHolder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_theme);
            }
        } else {
            if (layer.getTheme() == null){
                if (layer.getCaption().equals("UserPoint@Changchun")
                        ||layer.getCaption().equals("User_Line@Changchun")
                        ||layer.getCaption().equals("UserText@Changchun")
                        ||layer.getCaption().equals("UserCAD@Changchun")
                        ||layer.getCaption().equals("UserRegion@Changchun")){
                viewHolder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_pressed);}
            else {
                    viewHolder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_theme);
                }}
            else {
                viewHolder.imageView_edit.setBackgroundResource(R.drawable.btn_edit_theme);
            }
        }

        //设置图层类型
        if (layer.getTheme() != null) {
            viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_theme);
        } else {
            if (layer.getDataset() != null) {
                DatasetType type = layer.getDataset().getType();
                if (type.equals(DatasetType.POINT)) {
                    viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_point);
                } else if (type.equals(DatasetType.LINE)) {
                    viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_line);
                } else if (type.equals(DatasetType.REGION)) {
                    viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_region);
                } else if (type.equals(DatasetType.CAD)) {
                    viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_cad);
                } else if (type.equals(DatasetType.TEXT)) {
                    viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_text);
                } else if (type.equals(DatasetType.NETWORK)) {
                    viewHolder.imageView_datatype.setBackgroundResource(R.drawable.layer_network);
                }
            }
        }
        //设置图层名称
        viewHolder.tv_dataname.setText(layer.getCaption());
        //
        viewHolder.btn_more.setBackgroundResource(R.drawable.btn_more);

    }

    @Override
    public int getItemCount() {

        return myLayerDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView_visible;//可见性
        ImageView imageView_select;//
        ImageView imageView_edit;
        ImageView imageView_datatype;
        ImageButton btn_more;
        TextView tv_dataname;


        public ViewHolder(View View) {
            super(View);
            //设置子项对应控件布局
            imageView_visible = View.findViewById(R.id.image_visible);
            imageView_select = View.findViewById(R.id.image_select);
            imageView_edit = View.findViewById(R.id.image_edit);
            imageView_datatype = View.findViewById(R.id.image_datatype);
            btn_more = View.findViewById(R.id.btn_more);
            tv_dataname = View.findViewById(R.id.tv_dataname);

        }
    }

    public  void refresh(){
        notifyDataSetChanged();//刷新数据
    }

}
