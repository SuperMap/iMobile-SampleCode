package com.supermap.android.speech;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.supermap.data.Point2D;
import com.supermap.mapping.speech.POIInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 展示POI结果的ListView适配器
 */

public class SearchResultsListViewAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<POIInfo> mList = new ArrayList<>();

    public SearchResultsListViewAdapter (Context context) {
        mContext = context;

        for (int i = 0; i < 10; i++) {
            mList.add(new POIInfo("name", new Point2D(3232,435)));
        }
    }

    public SearchResultsListViewAdapter (Context context, ArrayList<POIInfo> list) {
        mContext = context;
        mList = list;
    }

    public void setList(ArrayList<POIInfo> list) {
        mList.clear();
        mList.addAll(list);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.search_results_listview_item, null);
            viewHolder.ll_01 =  (LinearLayout)convertView.findViewById(R.id.ll_01);
            viewHolder.ll_02 =  (LinearLayout)convertView.findViewById(R.id.ll_02);
            viewHolder.textView_count = (TextView)convertView.findViewById(R.id.count);
            viewHolder.textView_name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.textView_distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.imageView_location = (ImageView) convertView.findViewById(R.id.location);
            viewHolder.imageView_navi = (ImageView) convertView.findViewById(R.id.navi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.textView_count.setText((position + 1 )+ ".");

        //默认的当前位置
        final Point2D point2D_center = new Point2D(new Point2D(116.422429, 39.935264));

        final POIInfo poiInfo = mList.get(position);

        if (poiInfo.getIsSelected()) {
            viewHolder.ll_01.setBackgroundColor(Color.parseColor("#50B3FC"));
            viewHolder.ll_02.setBackgroundColor(Color.parseColor("#50B3FC"));
        } else {
            viewHolder.ll_01.setBackgroundColor(Color.parseColor("#FFFFFF"));
            viewHolder.ll_02.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        viewHolder.textView_name.setText(poiInfo.getPoiname());

        double abs_x = Math.abs(point2D_center.getX() - poiInfo.getPoint2D().getX());
        double abs_y = Math.abs(point2D_center.getY() - poiInfo.getPoint2D().getY());
        double length = Math.sqrt(Math.pow(abs_x, 2)+Math.pow(abs_y, 2));

        double dUnit = 2.694945898329385 / 30 * Math.pow(10, -4); //1米
        double distance = length / dUnit;
        int parseInt = Integer.parseInt(new DecimalFormat("0").format(distance));
        viewHolder.textView_distance.setText("距离当前位置约" + parseInt + "米");

        final MainActivity mainActivity = (MainActivity) mContext;

        viewHolder.imageView_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                poiInfo.setSelected(true);
                for (int i = 0; i < mList.size(); i++) {
                    if (i != position) {
                        mList.get(i).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
                mainActivity.locationPOI(poiInfo.getPoint2D());
                mainActivity.showSelectedPointByCallout(poiInfo.getPoint2D(), "SelectedCallout", R.drawable.tmc_poi_hl);
            }
        });

        viewHolder.imageView_navi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mainActivity.startNavi(point2D_center ,poiInfo.getPoint2D());
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView textView_count;
        public TextView textView_name;
        public ImageView imageView_location;
        public ImageView imageView_navi;
        public TextView textView_distance;
        public LinearLayout ll_01;
        public LinearLayout ll_02;

        public ViewHolder() {
            super();
        }

    }
}
