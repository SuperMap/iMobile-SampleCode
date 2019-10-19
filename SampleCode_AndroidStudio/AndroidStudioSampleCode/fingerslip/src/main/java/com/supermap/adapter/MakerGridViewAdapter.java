package com.supermap.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.supermap.data.*;
import com.supermap.fingerslipdemo.R;
import com.supermap.mapping.MapView;


/**
 *
 * Created by zzb on 2018/10/16.
 */

public class MakerGridViewAdapter extends BaseAdapter {

    private MapView mapView;
    private int count;
    private LayoutInflater inflater;
    private Context context;

    private SymbolGroup Group_M;

    @Override
    public int getCount() {
        return count;
    }

    public MakerGridViewAdapter(MapView mapView, Context context) {
        this.mapView = mapView;
        this.context = context;
        inflater = ((Activity)context).getLayoutInflater();

        Workspace workspace = mapView.getMapControl().getMap().getWorkspace();
        Resources m_resources = workspace.getResources();
        SymbolMarkerLibrary symbol_M = m_resources.getMarkerLibrary();
        Group_M = symbol_M.getRootGroup();
        count = Group_M.getCount();
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
        ViewHolder viewHolder =null;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_symbol_mark_gridview, null);
            viewHolder.imageView = convertView.findViewById(R.id.imageview);
            viewHolder.textView = convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Symbol symbol  = Group_M.get(position);
        Bitmap backBitmap = Bitmap.createBitmap(dip2px(context, 50), dip2px(context, 50), Bitmap.Config.ARGB_8888);
        symbol.draw(backBitmap);

        viewHolder.imageView.setImageBitmap(backBitmap);
        viewHolder.textView.setText(symbol.getName());

//        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), backBitmap);
//        viewHolder.imageView.setBackground(drawable);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 。
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
