package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.supermap.imobile.control.MyMapsControl;
import com.supermap.imobile.iportalservices.R;
import com.yalantis.phoenix.PullToRefreshView;

/**
 * 我的地图界面
 */
public class MyMapsFragment extends Fragment {
    private static final String TAG = "MyMapsFragment";

    private Context mContext = null;

    private MyMapsControl mMyMapsControl = null;
    private ListView mMapsListView = null;
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
        View rootView = inflater.inflate(R.layout.fragment_mymaps, container, false);
        initView(rootView);
        initListening(rootView);
        initControl();
        return rootView;
    }

    private void initView(View rootView) {
        mMapsListView = rootView.findViewById(R.id.list_mymaps);
        mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
    }

    //控制服务模块联网请求和界面刷新
    private void initControl() {
        mMyMapsControl = new MyMapsControl(mContext, mMapsListView, mPullToRefreshView);
        mMyMapsControl.getMyMaps();
    }

    private void initListening(View rootView) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

//                try {
//                    IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
//                        @Override
//                        public void onFailed(Exception exception) {
//
//                        }
//
//                        @Override
//                        public void onResponse(Response response) {
//                            try {
//                                String string = response.body().string();
//                                Log.e(TAG, string);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    IPortalService.getInstance().logout();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }


}
