package com.supermap.imobile.streamingservice;

import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 流数据服务
 *
 */
public class StreamingService {
    private static final String TAG = "StreamingService";

    //iServer服务管理首页地址 eg: http://localhost:8090/iserver/manager
    private String ManagerUrL = "";

    private String TOKEN = "";

    private OkHttpClient mOkHttpClient = null;//单例

    private MediaType mContentType = MediaType.parse("application/json; charset=utf-8");

    //网络请求回调
    private OnResponseListener mOnResponseListener;

    public void addOnResponseListener(OnResponseListener listener) {
        mOnResponseListener = listener;
    }

    //请求日志
    private HttpLoggingInterceptor mlogInterceptor = null;

    private StreamingService() {
        //在OkHttp里加入HttpLoggingInterceptor会调用了wirteTo方法，导致会再次执行UploadFileRequestBody.writeto方法重复写入数据。
        mlogInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        mlogInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS); // 不要设置成BODY，会导致writeTo调用两次
    }

    private static StreamingService mStreamingService = null;

    public static StreamingService getInstance() {
        if (mStreamingService == null) {
            mStreamingService = new StreamingService();
        }
        return mStreamingService;
    }

    /**
     * 初始化OkHttp网络请求对象
     */
    private void initOkHttp() {
        if (mOkHttpClient == null) {
            //如果是第一次,初始化一个okHttpClient对象
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addNetworkInterceptor(mlogInterceptor)
                    .build();
        }
    }

    /**
     * 网络请求回调，注意：子线程中回调。
     */
    private Callback callBack = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            if (mOnResponseListener != null) {
                mOnResponseListener.onFailed(e);
            }
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (mOnResponseListener != null) {
                mOnResponseListener.onResponse(response);
            }
        }
    };

    public String getManagerUrL() {
        return ManagerUrL;
    }

    public void setManagerUrL(String url) {
        if (url != null && url.endsWith("/")) {
            ManagerUrL = url.substring(0, url.length() - 1);
        } else {
            ManagerUrL = url;
        }
    }

    public String getToken() {
        return TOKEN;
    }

    public void setToken(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    /**
     * 请求token
     */
//    public void requestToken() {
//        try {
//            initOkHttp();
//
//            String url = "http://192.168.0.28:8090/iserver/manager/streaming.json?token=Qrau6OSwUrQhUocr74-BQEkZQoCIJxTO4sHkOy5YFDuO-Fj0C6VhE1oGfQPmXxFPvMOBpYvQHnEz4oPA3A2kTg..";
//
//            JSONObject jsonRequst = new JSONObject();
//            jsonRequst.put("serviceName", "liushujufuwu");
//            jsonRequst.put("configJsonPath", "D:/SuperMap/SuperMapiServer9D/samples/streamingmodels/AQI/AQI.streaming");
//            jsonRequst.put("token", "Qrau6OSwUrQhUocr74-BQEkZQoCIJxTO4sHkOy5YFDuO-Fj0C6VhE1oGfQPmXxFPvMOBpYvQHnEz4oPA3A2kTg..");
//
//            RequestBody body = RequestBody.create(mContentType, jsonRequst.toString());
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(body)
//                    .build();
//
//            mOkHttpClient.newCall(request).enqueue(callBack);
//        } catch (Exception e) {
//            if (mOnResponseListener != null) {
//                mOnResponseListener.onFailed(e);
//            }
//            Log.e(TAG, e.getMessage());
//        }
//
//    }

    /**
     * 创建流数据服务
     * @param serviceName 服务名称是惟一的，重复的名称会创建失败
     * @param config
     * @param type
     */
    public void createStreamingService(String serviceName, String config, String type) {
        try {
            initOkHttp();

            String url = ManagerUrL +  "/streaming.json?token=" + TOKEN;
            JSONObject jsonRequst = new JSONObject();
            jsonRequst.put("serviceName", serviceName);
            if (type.equals("configJsonPath")) {
                //streaming文件地址
                jsonRequst.put("configJsonPath", config);
            } else if (type.equals("configJsonContent")) {
                //json字符串
                jsonRequst.put("configJsonContent", config);
            }
            jsonRequst.put("token", TOKEN);

            Log.e(TAG, jsonRequst.toString());

            RequestBody body = RequestBody.create(mContentType, jsonRequst.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            mOkHttpClient.newCall(request).enqueue(callBack);
        } catch (Exception e) {
            if (mOnResponseListener != null) {
                mOnResponseListener.onFailed(e);
            }
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 修改已有的流数据服务
     * @param serviceName
     * @param configJsonContent
     * @param configJsonPath
     */
    public void updateStreamingService(String serviceName, String configJsonContent, String configJsonPath) {
        try {
            initOkHttp();

            String url = ManagerUrL + "/services/" + serviceName +  ".json?token=" + TOKEN;

            JSONObject jsonRequst = new JSONObject();
//            jsonRequst.put("isStreamingService", true);
//            jsonRequst.put("interfaceTypes", null);
//            jsonRequst.put("streamingApplicationRunningInfo", null);
//            jsonRequst.put("isSet", false);
//            jsonRequst.put("instances", null);
//            jsonRequst.put("isClusterService", false);
//            jsonRequst.put("type", "com.supermap.processing.jobserver.StreamingServiceServer");
//            jsonRequst.put("interfaceNames", null);
//            jsonRequst.put("isDataflowService", false);
//            jsonRequst.put("alias", null);
//            jsonRequst.put("isDistributedanalystService", false);
//            jsonRequst.put("status", "Submitted");
            jsonRequst.put("name", serviceName);
            jsonRequst.put("configJsonPath", configJsonPath);
            jsonRequst.put("configJsonContent", configJsonContent);

            Log.e(TAG, jsonRequst.toString());
            RequestBody body = RequestBody.create(mContentType, jsonRequst.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            mOkHttpClient.newCall(request).enqueue(callBack);
        } catch (Exception e) {
            if (mOnResponseListener != null) {
                mOnResponseListener.onFailed(e);
            }
            Log.e(TAG, e.getMessage());
        }
    }

}
