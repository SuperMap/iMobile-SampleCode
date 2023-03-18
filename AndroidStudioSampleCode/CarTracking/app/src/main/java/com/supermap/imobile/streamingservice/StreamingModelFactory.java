package com.supermap.imobile.streamingservice;

import com.google.gson.Gson;
import com.supermap.imobile.streamnode.StreamNode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 流处理模型
 *
 * //    SingleTextFileReceiver: "单文本文件接收器",
 * //    SocketReceiver: "Socket客户端接收器",
 * //    MultiSocketReceiver: "Socket多客户端接收器",
 * //    SocketServerReceiver: "Socket服务接收器",
 * //    WebSocketReceiver: "WebSocket接收器",
 * //    TextFileReceiver: "文本文件接收器",
 * //    KafkaReceiver: "Kafka接收器",
 * //    HttpReceiver: "Http接收器",
 * //    JMSReceiver: "JMS接收器",
 * //
 * //    WebSocketClientSender: "WebSocket发送器",
 * //    FileSender: "文件发送器",
 * //    JMSSender: "JMS消息发送器",
 * //    SMSSender: "短信消息发送器",
 * //    SocketClientSender: "Socket客户端发送器",
 * //    SocketServerSender: "Socket服务端发送器",
 * //    EsAppendSender: "Elasticsearch添加发送器",
 * //    EsUpdateSender: "Elasticsearch更新发送器",
 * //
 * //    FeatureInsertMapper: "字段添加转换器",
 * //    StaticRDDJoinMapper: "静态资源扩展",
 * //    FeatureDeleteMapper: "字段删除转换器",
 * //    FeatureMapMapper: "字段映射转换器",
 * //    FeatureCalculateMapper: "字段运算转换器",
 * //    GeoTaggerMapper: "地理围栏转换器",
 * //
 * //    FeatureFilter: "逻辑运算过滤器",
 * //    GeoFilter: "地理过滤器"
 */
public class StreamingModelFactory {
    private static final String TAG = "StreamingModelFactory";

    private String checkPointDir = "tmp"; //设置 Streaming 的CheckPoint功能的保存目录。String 类型。
    private int interval = 5000; // 设置 Streaming 运行的间隔时间，单位为毫秒。int 类型。
    private int version = 9000;

    private static StreamingModelFactory mStreamingModelFactory = null;

    public static StreamingModelFactory getInstance() {
        if (mStreamingModelFactory == null) {
            mStreamingModelFactory = new StreamingModelFactory();
        }
        return mStreamingModelFactory;
    }

    public String getCheckPointDir() {
        return checkPointDir;
    }

    public void setCheckPointDir(String checkPointDir) {
        this.checkPointDir = checkPointDir;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public JSONObject buildNodeDic(StreamNode streamNode) {
        try {
            Gson gson = new Gson();
            String geoFilterJson = gson.toJson(streamNode);
            return new JSONObject(geoFilterJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建流处理模型
     * @return
     */
    public JSONObject createStreamingModel(JSONObject nodeDic) {
        try {
            JSONObject root = new JSONObject();
            JSONObject sparkParameter = new JSONObject();
            JSONObject stream = new JSONObject();

            sparkParameter.put("checkPointDir", checkPointDir);
            sparkParameter.put("interval", interval);

            stream.put("nodeDic",nodeDic);

            root.put("sparkParameter", sparkParameter);
            root.put("stream", stream);
            root.put("version", version);

            return root;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
