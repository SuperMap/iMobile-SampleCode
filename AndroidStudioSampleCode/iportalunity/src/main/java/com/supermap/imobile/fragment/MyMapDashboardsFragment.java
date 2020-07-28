package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.supermap.imobile.control.MyMapDashboardsControl;
import com.supermap.imobile.iportalservices.R;
import com.yalantis.phoenix.PullToRefreshView;

/**
 * 我的大屏界面
 */
public class MyMapDashboardsFragment extends Fragment {

    private Context mContext = null;

    private MyMapDashboardsControl mMyMapDashboardsControl = null;
    private ListView mListView = null;
    private PullToRefreshView mPullToRefreshView;
    private FloatingActionButton floatingActionButton = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mymapdashboards, container, false);
        initView(rootView);
        initListening(rootView);
        initControl();
        return rootView;
    }

    private void initView(View rootView) {
        mListView = rootView.findViewById(R.id.list_mymapdashboards);
        mPullToRefreshView = rootView.findViewById(R.id.pull_to_refresh);
        floatingActionButton = rootView.findViewById(R.id.fab);
    }

    //控制服务模块联网请求和界面刷新
    private void initControl() {
        mPullToRefreshView.setRefreshing(true);
        mMyMapDashboardsControl = new MyMapDashboardsControl(mContext, mListView, mPullToRefreshView);
        mMyMapDashboardsControl.getMyMapDashboards();
    }

    private void initListening(View rootView) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


}
