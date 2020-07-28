package com.supermap.imobile.control;

import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.supermap.imobile.adapter.MyMapsAdapter;
import com.supermap.imobile.bean.MapsBean;
import com.supermap.imobile.iportalservices.MainActivity;
import com.supermap.iportalservices.IPortalService;
import com.supermap.iportalservices.OnResponseListener;
import com.yalantis.phoenix.PullToRefreshView;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 我的地图
 */
public class MyMapsControl {
    private static final String TAG = "MyMapsControl";

    private MainActivity mMainActivity = null;
    private ListView mListView = null;
    private PullToRefreshView mPullToRefreshView;
    private MyMapsAdapter adapter = null;

    public MyMapsControl(Context context, ListView listView, PullToRefreshView pullToRefreshView) {
        mMainActivity = (MainActivity) context;
        mListView = listView;
        mPullToRefreshView = pullToRefreshView;
        mPullToRefreshView.setOnRefreshListener(refreshListener);
    }

    /**
     * 联网请求更新数据
     */
    public void getMyMaps() {
        try {
            mPullToRefreshView.setRefreshing(true);
            IPortalService.getInstance().addOnResponseListener(listener);
            HashMap<String, String> searchParameter = new HashMap<>();
            searchParameter.put("pageSize", "20");
            IPortalService.getInstance().getMyMaps(searchParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉刷新
     */
    PullToRefreshView.OnRefreshListener refreshListener = new PullToRefreshView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            try {
                IPortalService.getInstance().addOnResponseListener(listener);
                HashMap<String, String> searchParameter = new HashMap<>();
                searchParameter.put("pageSize", "20");
                IPortalService.getInstance().getMyMaps(searchParameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 联网回调
     */
    private OnResponseListener listener = new OnResponseListener() {
        @Override
        public void onFailed(final Exception exception) {
            mMainActivity.runOnUiThread(() -> {
                Toast.makeText(mMainActivity,"刷新失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                mPullToRefreshView.setRefreshing(false);
            });
        }

        @Override
        public void onResponse(final Response response) {
            String responseBody = null;
            MapsBean mapsBean = null;
            try {
                responseBody = response.body().string();

                JSONObject root = new JSONObject(responseBody);
                if (root.has("error")) {
                    JSONObject error = new JSONObject(root.getString("error"));
                    final String errorMsg = error.getString("errorMsg");
                    final String code = error.getString("code");
                    showToast(code + "：" + errorMsg);
                    setRefresh(false);
                    return;
                } else {
                    Gson gson = new Gson();
                    mapsBean = gson.fromJson(responseBody, MapsBean.class);
                    if (mapsBean == null) {
                        showToast("刷新失败");
                        setRefresh(false);
                        return;
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                showToast(e.getMessage());
                mMainActivity.runOnUiThread(() -> mPullToRefreshView.setRefreshing(false));
                return;
            }
            if (adapter == null) {
                adapter = new MyMapsAdapter(mMainActivity, mapsBean);
            } else {
                adapter.setData(mapsBean);
            }
            mMainActivity.runOnUiThread(() -> {
                mListView.setAdapter(adapter);
                mPullToRefreshView.setRefreshing(false);
                Toast.makeText(mMainActivity, "刷新成功", Toast.LENGTH_SHORT).show();
            });
        }
    };

    private void showToast(final String msg){
        if (mMainActivity != null) {
            mMainActivity.runOnUiThread(() -> Toast.makeText(mMainActivity, msg, Toast.LENGTH_SHORT).show());
        }
    }

    private void setRefresh(final boolean refresh){
        if (mMainActivity != null) {
            mMainActivity.runOnUiThread(() -> mPullToRefreshView.setRefreshing(refresh));
        }
    }
}
