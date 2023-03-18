package com.supermap.osgblayerattributequery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.osgblayerattributequery.QueryInfoBubblePopupWindow.QueryInfoAdapter.QueryInfoViewHolder;
import com.supermap.realspace.DownloadManager;

import java.util.ArrayList;

/**
 *
 * @Titile:QueryInfoBubblePopupWindow.java
 * @Descript:气泡界面
 * @Company: beijingchaotu
 * @Created on: 2017年8月2日 下午8:30:46
 * @Author: sueprmap
 * @version: V1.0
 */
public class QueryInfoBubblePopupWindow  {

    RelativeLayout rl_bubble;
    public PopupWindow mDialog;
    QueryInfoViewHolder queryInfoViewHolder = null;
    View mView = null;
    private ListView mQueryInfoList = null;
    //private QueryInfoAdapter mQueryInfoAdapter = null;
    public ArrayList<QueryInfo> m_QueryInfoData;
    private Context context;
    int width;
    int height;

    public QueryInfoBubblePopupWindow(Context mcontext) {
        context = mcontext;
        m_QueryInfoData = new ArrayList<QueryInfo>();
        // mQueryInfoList = new ListView(context);
        mView = LayoutInflater.from(context).inflate(R.layout.popupwindow_queryinfobubble, null);
        rl_bubble=(RelativeLayout)mView.findViewById(R.id.rl_bubble);
        mQueryInfoList = (ListView) mView.findViewById(R.id.lst_queryinfo);
        //rl_bubble = (RelativeLayout) findViewById(R.id.rl_bubble);
        //	mQueryInfoList = (ListView) findViewById(R.id.lst_queryinfo);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int height = dp2px(200);
        mDialog = new PopupWindow(mView, screenWidth, height, true);
        mDialog.setFocusable(true);
        mDialog.setOutsideTouchable(true);
        mDialog.setBackgroundDrawable(new ColorDrawable(android.R.color.white));
        mDialog.getBackground().setAlpha(0);
        mDialog.setAnimationStyle(R.style.popupwindow_bubble);
        mDialog.update();

    }

    public void additem(String name, String value) {
        QueryInfo info = new QueryInfo();

        info.name = name;
        info.value = value;
        m_QueryInfoData.add(info);
    }


    public void show(View parent, float x, float y) {
        mQueryInfoList.setAdapter(new QueryInfoAdapter(context));
        float density = context.getResources().getDisplayMetrics().density;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDialog.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }

    class QueryInfo {
        String name;
        String value;
    }

    class QueryInfoAdapter extends BaseAdapter {

        private Context m_Context;
        private LayoutInflater m_inflater;

        public DownloadManager m_DownloadManager;
        // private Map<String, LayerItemViewHolder> m_holderMap= new
        // HashMap<String, LayerItemViewHolder>();

        public QueryInfoAdapter(Context context) {
            m_Context = context;
            this.m_inflater = LayoutInflater.from(m_Context);
        }

        // 定义item中包含的控件类型
        public final class QueryInfoViewHolder {
            public TextView txt_queryInfoName;
            public TextView txt_queryInfoValue;
        }

        // 定义一个获得文字信息的方法
        void setTextViewStyle(TextView textView) {

            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(30, 0, 10, 0);
            textView.setTextSize(15);
            textView.setTextColor(Color.BLACK);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return m_QueryInfoData.size();
        }

        @Override
        public QueryInfo getItem(int itemPosition) {
            // TODO Auto-generated method stub
            return m_QueryInfoData.get(itemPosition);
        }

        @Override
        public long getItemId(int itemPosition) {
            // TODO Auto-generated method stub
            return itemPosition;
        }

        @Override
        public View getView(int itemPosition, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub

            // 从列表中获取对应ID的图层数据
            final QueryInfo itemData = m_QueryInfoData.get(itemPosition);

            String name = itemData.name;
            String value = itemData.value;
            queryInfoViewHolder = new QueryInfoViewHolder();
            if (convertView == null) {
                // //将item的View设置为从自定义的页面布局中反射获取
                convertView = m_inflater.inflate(R.layout.popupwindow_queryinfobubble_item, null);
                queryInfoViewHolder.txt_queryInfoName = (TextView) convertView.findViewById(R.id.txt_queryinfoname);
                queryInfoViewHolder.txt_queryInfoValue = (TextView) convertView.findViewById(R.id.txt_queryinfovalue);

                convertView.setTag(queryInfoViewHolder);
            } else {
                queryInfoViewHolder = (QueryInfoViewHolder) convertView.getTag();

            }

            queryInfoViewHolder.txt_queryInfoName.setText(name);

            // setTextViewStyle(queryInfoViewHolder.txt_queryInfoName);

            queryInfoViewHolder.txt_queryInfoValue.setText(value);

            // setTextViewStyle(queryInfoViewHolder.txt_queryInfoValue);
            queryInfoViewHolder.txt_queryInfoName.setTextColor(Color.BLACK);
            return convertView;
        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                this.context.getResources().getDisplayMetrics());
    }

}
