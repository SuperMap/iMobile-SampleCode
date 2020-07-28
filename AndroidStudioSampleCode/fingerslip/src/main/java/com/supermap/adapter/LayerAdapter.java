package com.supermap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.data.DatasetType;
import com.supermap.fingerslipdemo.MainActivity;
import com.supermap.fingerslipdemo.R;
import com.supermap.mapping.Layer;



import java.util.List;

public class LayerAdapter extends BaseAdapter{
    String name;
    List<Layer> layerList;
    Context context;
    MainActivity mainActivity;
    DatasetType type=null;
    public LayerAdapter(List<Layer> list, MainActivity activity){
        this.layerList=list;
        this.context=activity.getApplicationContext();
        this.mainActivity=activity;
    }
    @Override
    public int getCount() {
        return layerList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_layer,null);
            viewHolder.tv_layername=(TextView) convertView.findViewById(R.id.layername);
            viewHolder.imageView_type=(ImageView)convertView.findViewById(R.id.image_type);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        name=layerList.get(position).getCaption();
        viewHolder.tv_layername.setText(name);
        if (layerList.get(position).getTheme()!=null){
            viewHolder.imageView_type.setBackgroundResource(R.drawable.layer_theme);
        }
        else {
            type=layerList.get(position).getDataset().getType();
            if (type==DatasetType.POINT){
                viewHolder.imageView_type.setBackgroundResource(R.drawable.layer_point);
            }
            else  if (type==DatasetType.LINE){
                viewHolder.imageView_type.setBackgroundResource(R.drawable.layer_line);
            }
            else  if (type==DatasetType.REGION){
                viewHolder.imageView_type.setBackgroundResource(R.drawable.layer_region);
            }
            else  if (type==DatasetType.TEXT){
                viewHolder.imageView_type.setBackgroundResource(R.drawable.layer_text);
            }
        }
        viewHolder.tv_layername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layerList.get(position).getTheme()==null){
                if (layerList.get(position).getDataset().getType()== DatasetType.POINT){
                    layerList.get(position).setEditable(true);
                    mainActivity.showSymbolMarkerFragment();
                    notifyDataSetChanged();
                }
                else  if (layerList.get(position).getDataset().getType()== DatasetType.LINE){
                    layerList.get(position).setEditable(true);
                    mainActivity.showSymbolLineFragment();
                }
                else  if (layerList.get(position).getDataset().getType()== DatasetType.REGION){
                    layerList.get(position).setEditable(true);
                    mainActivity.showSymbolFillFragment();
                }
                }
                else  {
                    Toast.makeText(context,"专题图图层无法设置",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }
    static class ViewHolder{
        TextView tv_layername=null;
        ImageView imageView_type=null;
    }
}
