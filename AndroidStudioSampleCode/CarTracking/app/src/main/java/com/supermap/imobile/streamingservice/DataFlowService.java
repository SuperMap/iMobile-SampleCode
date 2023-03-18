package com.supermap.imobile.streamingservice;

import android.os.Handler;
import android.os.Message;
import okhttp3.*;

/**
 * 数据流服务
 */

public class DataFlowService {
    private String TAG = "DataFlowService";

    private static DataFlowService mDataFlowService = null;
    private WebSocket mWebSocket = null;
    private String mWsAddress = "";

    private static OnDataFlowListener mOnDataFlowListener = null;

    private static final int FAILED = 0;
    private static final int OPENED = 1;
    private static final int GEOJSON = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case GEOJSON:
                    mOnDataFlowListener.onDataFlowReceiveGeoJson((String) message.obj);
                    break;
                case OPENED:
                    mOnDataFlowListener.onDataFlowDidOpened((String) message.obj);
                    break;
                case FAILED:
                    mOnDataFlowListener.onDataFlowDidFailed((String) message.obj);
                    break;
            }
        }
    };

    private DataFlowService() {
    }

    /**
     * 获取数据流服务单例
     * @return
     */
    public static DataFlowService getInstance() {
        if (mDataFlowService == null) {
            mDataFlowService = new DataFlowService();
        }
        return mDataFlowService;
    }

    /**
     * @param wsAddress
     */
    public void setAddress(String wsAddress) {
        this.mWsAddress = wsAddress;
    }

    public void connect() {
        new Thread(new Runnable() {
            public void run() {
                wsConnect();
            }
        }).start();
    }

    public void close() {
        new Thread(new Runnable() {
            public void run() {
                if (mWebSocket != null) {
                    mWebSocket.close(1000, "Normal Closure");
                }
            }
        }).start();
    }

    private void wsConnect() {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(mWsAddress).build();
        mWebSocket = httpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    if (mOnDataFlowListener != null) {
                        ResponseBody body = response.body();
                        String string = body.string();
                        handleMessage(OPENED, string);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String json) {
                super.onMessage(webSocket, json);
                try {
                    if (mOnDataFlowListener != null) {
                        handleMessage(GEOJSON, json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                try {
                    String message = t.getMessage();
                    String message1 = t.getCause().getMessage();
                    if (mOnDataFlowListener != null) {
                        ResponseBody body = response.body();
                        String string = body.string();
                        handleMessage(FAILED, string);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void handleMessage(int handlerValue, Object object) {
        Message message = Message.obtain();
        message.what = handlerValue;
        message.obj = object;
        handler.sendMessage(message);
    }

    public interface OnDataFlowListener {
        void onDataFlowReceiveGeoJson(String geoJson);

        void onDataFlowDidFailed(String err);

        void onDataFlowDidOpened(String info);
    }

    public void setOnDataFlowListener(OnDataFlowListener listener) {
        this.mOnDataFlowListener = listener;
    }

}
