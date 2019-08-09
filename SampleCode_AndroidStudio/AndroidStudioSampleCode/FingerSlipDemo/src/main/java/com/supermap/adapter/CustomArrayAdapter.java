package com.supermap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.fingerslipdemo.R;



/** An array adapter that knows how to render views when given CustomData classes */
public class CustomArrayAdapter extends ArrayAdapter<CustomData> {

    private LayoutInflater mInflater;

    public CustomArrayAdapter(Context context, CustomData[] values) {
        super(context, R.layout.custom_data_view, values);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.custom_data_view, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.textView = convertView.findViewById(R.id.textView);
            holder.imageView = convertView.findViewById(R.id.imageview);
            holder.linearLayout = convertView.findViewById(R.id.ll_menu_item);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // Populate the text
        holder.textView.setText(getItem(position).getText());

        holder.imageView.setBackgroundResource(getItem(position).getDrawableId());

        boolean selected = getItem(position).isSelected();
        if (selected) {
            holder.linearLayout.setBackgroundResource(R.drawable.selector_menu_selected);
            holder.textView.setTextColor(Color.parseColor("#6495ED"));
        } else {
            holder.linearLayout.setBackgroundResource(R.drawable.shape_menu_normal);
            holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
        }

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;
    }


}
