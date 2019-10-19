package com.supermap.imobile.control;

import android.content.Context;
import android.widget.ListView;
import com.google.gson.Gson;
import com.supermap.imobile.adapter.DatasAdapter;
import com.supermap.imobile.bean.DownloadDataEvent;
import com.supermap.imobile.bean.GetDataEvent;
import com.supermap.imobile.bean.DatasBean;
import com.supermap.iportalservices.IPortalService;
import com.supermap.iportalservices.OnResponseListener;
import com.supermap.iportalservices.ProgressResponseBody;
import com.supermap.imobile.iportalservices.MainActivity;
import com.yalantis.phoenix.PullToRefreshView;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 我的数据
 */
public class DatasControl {
    private static final String TAG = "MyDatasControl";

    private MainActivity mMainActivity = null;
    private ListView mListView = null;
    private PullToRefreshView mPullToRefreshView = null;
    private DatasAdapter adapter = null;

    private String filePath = null;//下载的文件路径

    private DatasControl(Context context, ListView listView, PullToRefreshView pullToRefreshView) {
        mMainActivity = (MainActivity) context;
        mListView = listView;
        mPullToRefreshView = pullToRefreshView;
        mPullToRefreshView.setOnRefreshListener(refreshListener);
    }

    private static DatasControl DatasControl = null;

    public static void createInstance(Context context, ListView listView, PullToRefreshView pullToRefreshView) {
        if (DatasControl == null) {
            DatasControl = new DatasControl(context, listView, pullToRefreshView);
        }
    }

    public static DatasControl getInstance() {
        return DatasControl;
    }

    /**
     * 下拉刷新
     */
    PullToRefreshView.OnRefreshListener refreshListener = new PullToRefreshView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            try {
                IPortalService.getInstance().addOnResponseListener(updateListViewlistener);
                HashMap<String, String> searchParameter = new HashMap<>();
                searchParameter.put("pageSize", "20");
                searchParameter.put("orderBy", "LASTMODIFIEDTIME");
                searchParameter.put("orderType", "DESC");;
                IPortalService.getInstance().getDatas(searchParameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 更新数据
     */
    public void getDatas() {
        try {
            IPortalService.getInstance().addOnResponseListener(updateListViewlistener);
            HashMap<String, String> searchParameter = new HashMap<>();
            searchParameter.put("pageSize", "20");
            searchParameter.put("orderBy", "LASTMODIFIEDTIME");
            searchParameter.put("orderType", "DESC");
            IPortalService.getInstance().getDatas(searchParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //下载回调
    private OnResponseListener downloadListener = new OnResponseListener() {
        @Override
        public void onFailed(Exception exception) {
            DownloadDataEvent downloadDataEvent = new DownloadDataEvent.Builder()
                    .isSuccess(false)
                    .setError(exception.getMessage())
                    .setMode("DatasResourceFragment")
                    .build();
            EventBus.getDefault().post(downloadDataEvent);
        }

        @Override
        public void onResponse(Response response) {
            if (response.isSuccessful()) {
                try {
                    ResponseBody body = response.body();
                    InputStream is = body.byteStream();

                    File file = new File(filePath);
                    file.getParentFile().mkdirs();
                    FileOutputStream fileout = new FileOutputStream(file);
                    /**
                     * 根据实际运行效果 设置缓冲区大小
                     */
                    byte[] buffer = new byte[1024];
                    int ch = 0;
                    while ((ch = is.read(buffer)) != -1) {
                        fileout.write(buffer, 0, ch);
                    }
                    is.close();
                    fileout.flush();
                    fileout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                DownloadDataEvent downloadDataEvent = new DownloadDataEvent.Builder()
                        .isSuccess(false)
                        .setError("下载失败-" + response.code() + ": " + response.request().url())
                        .setMode("DatasResourceFragment")
                        .build();
                EventBus.getDefault().post(downloadDataEvent);
            }
        }
    };

    //下载进度回调
    private ProgressResponseBody.ProgressListener downloadProgressListener = (bytesRead, contentLength, done) -> {
        double value = (float) bytesRead / contentLength * 100;
        DownloadDataEvent downloadDataEvent = new DownloadDataEvent.Builder()
                .isDownloading(true)
                .isSuccess(false)
                .setProgress((int) Math.round(value))
                .setFilePath(filePath)
                .setMode("DatasResourceFragment")
                .build();
        EventBus.getDefault().post(downloadDataEvent);
        adapter.isDownloading(true);
        if (done) {
            try {
                //防止数据量很小的时候，界面一闪而过或者不出现
                Thread.sleep(1000);
                EventBus.getDefault().post(new DownloadDataEvent.Builder()
                        .isSuccess(true)
                        .isDownloading(false)
                        .setFilePath(filePath)
                        .setMode("DatasResourceFragment")
                        .build());
                adapter.isDownloading(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * 下载数据
     */
    public void  downLoadData(String path, int ID) {
        try {
            filePath = path;

            IPortalService.getInstance().addOnResponseListener(downloadListener);
            IPortalService.getInstance().downloadData(ID, downloadProgressListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 联网刷新界面回调
     */
    private OnResponseListener updateListViewlistener = new OnResponseListener() {
        @Override
        public void onFailed(final Exception exception) {
            EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError(exception.getMessage()).build());
        }

        @Override
        public void onResponse(final Response response) {
            String responseBody = null;
            DatasBean datasBean = null;
            try {
                responseBody = response.body().string();

                JSONObject root = new JSONObject(responseBody);
                if (root.has("error")) {
                    JSONObject error = new JSONObject(root.getString("error"));
                    final String errorMsg = error.getString("errorMsg");
                    final String code = error.getString("code");
                    EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError(code + ": " + errorMsg).build());
                    return;
                } else {
                    Gson gson = new Gson();
                    datasBean = gson.fromJson(responseBody, DatasBean.class);
                    if (datasBean == null) {
                        EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError("刷新失败").build());
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (adapter == null) {
                adapter = new DatasAdapter(mMainActivity, datasBean);
            } else {
                adapter.setData(datasBean);
            }
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.setAdapter(adapter);
                    EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(true).build());
                }
            });
        }
    };

}
