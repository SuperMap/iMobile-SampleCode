package com.supermap.imobile.control;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.supermap.iportalservices.*;
import com.supermap.imobile.adapter.MyDatasAdapter;
import com.supermap.imobile.bean.*;
import com.supermap.imobile.iportalservices.MainActivity;
import com.yalantis.phoenix.PullToRefreshView;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 我的数据
 */
public class MyDatasControl {
    private static final String TAG = "MyDatasControl";

    private MainActivity mMainActivity = null;
    private ListView mListView = null;
    private PullToRefreshView mPullToRefreshView = null;
    private MyDatasAdapter adapter = null;
    private LinearLayout ll_progesssbar = null;

    private String filePath = null;//上传或下载的文件路径

    private int deleteID = -1;

    private MyDatasControl(Context context, ListView listView, PullToRefreshView pullToRefreshView, LinearLayout ll_progesssbar) {
        mMainActivity = (MainActivity) context;
        mListView = listView;
        mPullToRefreshView = pullToRefreshView;
        mPullToRefreshView.setOnRefreshListener(refreshListener);
        this.ll_progesssbar = ll_progesssbar;
    }

    private static MyDatasControl myDatasControl = null;
    public static void createInstance(Context context, ListView listView, PullToRefreshView pullToRefreshView, LinearLayout ll_progesssbar) {
        if (myDatasControl == null) {
            myDatasControl = new MyDatasControl(context, listView, pullToRefreshView, ll_progesssbar);
        }
    }

    public static MyDatasControl getInstance() {
        return myDatasControl;
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
                IPortalService.getInstance().getMyDatas(searchParameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 更新数据
     */
    public void getMyDatas() {
        try {
            IPortalService.getInstance().addOnResponseListener(updateListViewlistener);
            HashMap<String, String> searchParameter = new HashMap<>();
            searchParameter.put("pageSize", "20");
            IPortalService.getInstance().getMyDatas(searchParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //下载回调
    private OnResponseListener downloadListener  = new OnResponseListener() {
        @Override
        public void onFailed(Exception exception) {
            DownloadDataEvent downloadDataEvent = new DownloadDataEvent.Builder()
                    .isSuccess(false)
                    .setError(exception.getMessage())
                    .setMode("MyDatasFragment")
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
                        .setMode("MyDatasFragment")
                        .setError("下载失败-" + response.code() + ": " + response.request().url())
                        .build();
                EventBus.getDefault().post(downloadDataEvent);
            }
        }
    };

    //下载进度回调
    private ProgressResponseBody.ProgressListener downloadProgressListener = (bytesRead, contentLength, done) -> {
        double value = (float)bytesRead / contentLength * 100;
        DownloadDataEvent downloadDataEvent = new DownloadDataEvent.Builder()
                .isDownloading(true)
                .isSuccess(false)
                .setProgress((int) Math.round(value))
                .setFilePath(filePath)
                .setMode("MyDatasFragment")
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
                        .setMode("MyDatasFragment")
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
    public void downLoadData(String path, int ID) {
        try {
            filePath = path;

            IPortalService.getInstance().addOnResponseListener(downloadListener);
            IPortalService.getInstance().downloadMyData(ID, downloadProgressListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 联网上传数据（获取ID->上传）
     */
    public void uploadData(String path) {
        try {
            filePath = path;
            String fileName = new File(path).getName();
            String tags = "用户数据";
//            String type = "WORKSPACE";

            //获取ID
            IPortalService.getInstance().addOnResponseListener(getIDListener);
            IPortalService.getInstance().getMyDataID(fileName, tags, DataItemType.WORKSPACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ID回调
     */
    private OnResponseListener getIDListener = new OnResponseListener() {
        @Override
        public void onFailed(final Exception e) {
            Log.e("getMyDataID", e.getMessage());
        }

        @Override
        public void onResponse(final Response response) {
            if (response.isSuccessful()) {
                String responseBody = null;
                try {
                    responseBody = response.body().string();

                    JSONObject root = new JSONObject(responseBody);
                    if (root.has("childID")) {
                        int childID = root.getInt("childID");
                        if (filePath != null && !filePath.isEmpty()) {
                            //上传
                            IPortalService.getInstance().addOnResponseListener(uploadListener);
                            IPortalService.getInstance().uploadData(filePath, childID, uploadProgressListener);
                        }
                    } else {
                        EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError("获取数据ID失败").build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError(e.getMessage()).build());
                }
            } else {
                EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError("获取数据ID失败").build());
            }
        }
    };

    //上传进度(子线程)
    private ProgressRequestBody.ProgressListener uploadProgressListener = (length, size) -> {
        double value = (float)size / length * 100;
        UploadDataEvent uploadDataEvent = new UploadDataEvent.Builder()
                .isUploading(true)
                .isSuccess(false)
                .setFilePath(filePath)
                .setProgress((int) Math.round(value))
                .build();
        EventBus.getDefault().post(uploadDataEvent);
        if (length == size) {
            try {
                Thread.sleep(1000);
                EventBus.getDefault().post(new UploadDataEvent.Builder().isSuccess(true).isUploading(false).build());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 数据上传回调
     */
    private OnResponseListener uploadListener = new OnResponseListener() {
        @Override
        public void onFailed(final Exception e) {
            UploadDataEvent uploadDataEvent = new UploadDataEvent.Builder()
                    .isSuccess(false)
                    .setError("上传数据失败" + e.getMessage())
                    .build();
            EventBus.getDefault().post(uploadDataEvent);
        }

        @Override
        public void onResponse(final Response response) {
            if (!response.isSuccessful()) {
                UploadDataEvent uploadDataEvent = new UploadDataEvent.Builder()
                        .isSuccess(false)
                        .setError(response.message())
                        .build();
                EventBus.getDefault().post(uploadDataEvent);
            }
        }
    };

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
                    EventBus.getDefault().post(new GetDataEvent.Builder().isSucess(false).setError(code +": " + errorMsg).build());
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
                adapter = new MyDatasAdapter(mMainActivity, datasBean);
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

    //删除数据
    public void deleteItem(int ID) {
        try {
            mMainActivity.runOnUiThread(() -> {
                ll_progesssbar.setVisibility(View.VISIBLE);
            });
            deleteID = ID;
            IPortalService.getInstance().addOnResponseListener(deleteListener);
            IPortalService.getInstance().deleteMyContentItem(MyContentType.MY_DATA, ID);
        } catch (Exception e) {
            e.printStackTrace();
            mMainActivity.runOnUiThread(() -> {
                ll_progesssbar.setVisibility(View.VISIBLE);
            });
        }
    }

    //发布服务
    public void publishServices(int ID) {
//        try {
//            mMainActivity.runOnUiThread(() -> {
//                ll_progesssbar.setVisibility(View.VISIBLE);
//            });
//            IPortalService.getInstance().addOnResponseListener(publishListener);
//            HashMap<String, String> parameter = new HashMap<>();
//            parameter.put("serviceType", "RESTMAP,RESTDATA");
//            IPortalService.getInstance().publishServices(ID, parameter);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mMainActivity.runOnUiThread(() -> {
//                ll_progesssbar.setVisibility(View.VISIBLE);
//            });
//        }
    }

    private OnResponseListener deleteListener = new OnResponseListener() {
        @Override
        public void onFailed(Exception exception) {
            if (exception != null) {
                Log.e(TAG, "" + exception.getMessage());
            }
            mMainActivity.runOnUiThread(() -> {
                Toast.makeText(mMainActivity, "删除失败", Toast.LENGTH_SHORT).show();
                ll_progesssbar.setVisibility(View.GONE);
            });
        }

        @Override
        public void onResponse(Response response) {
            if (response.isSuccessful()) {
                try {
                    String string = response.body().string();
                    Log.e(TAG, "delete: " + string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (deleteID != -1) {
                    adapter.deleteItem(deleteID);
                }
                mMainActivity.runOnUiThread(() -> {
                    Toast.makeText(mMainActivity, "删除成功", Toast.LENGTH_SHORT).show();
                    ll_progesssbar.setVisibility(View.GONE);
                });
            } else {
                mMainActivity.runOnUiThread(() -> {
                    Toast.makeText(mMainActivity, "删除失败", Toast.LENGTH_SHORT).show();
                    ll_progesssbar.setVisibility(View.GONE);
                });;
            }
        }
    };

    private OnResponseListener publishListener = new OnResponseListener() {
        @Override
        public void onFailed(Exception exception) {
            if (exception != null) {
                Log.e(TAG, "" + exception.getMessage());
            }
            mMainActivity.runOnUiThread(() -> {
                Toast.makeText(mMainActivity, "发布失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                ll_progesssbar.setVisibility(View.GONE);
            });
        }

        @Override
        public void onResponse(Response response) {
            try {
                String responseBody = response.body().string();
                JSONObject root = new JSONObject(responseBody);
                boolean succeed = root.getBoolean("succeed");

                mMainActivity.runOnUiThread(() -> {
                    if (succeed) {
                        Toast.makeText(mMainActivity, "发布成功", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String error = root.getString("error");
                            Toast.makeText(mMainActivity, "发布失败: " + error, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mMainActivity, "发布失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    ll_progesssbar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                mMainActivity.runOnUiThread(() -> {
                    Toast.makeText(mMainActivity, "发布失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ll_progesssbar.setVisibility(View.GONE);
                });
            }
        }
    };

}
