package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dandan.jsonhandleview.library.JsonViewLayout;
import com.supermap.data.Workspace;
import com.supermap.imobile.streamingapp.CardItem;
import com.supermap.imobile.streamingapp.RapidFloatingActionContentCardListView;
import com.supermap.imobile.streamnode.GeoFilter;
import com.supermap.imobile.streamnode.HttpReceiver;
import com.supermap.imobile.streamnode.WebSocketClientSender;
import com.supermap.imobile.streamingapp.R;
import com.supermap.imobile.streamingservice.DataFlowService;
import com.supermap.imobile.streamingservice.OnResponseListener;
import com.supermap.imobile.streamingservice.StreamingModelFactory;
import com.supermap.imobile.streamingservice.StreamingService;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 界面一
 */
public class FirstFragment extends Fragment implements View.OnClickListener, RapidFloatingActionContentCardListView.OnRapidFloatingActionContentCardListViewListener {

    private static final String TAG = "FirstFragment";
    private final String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private Context mContext = null;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    private JsonViewLayout jsonViewLayout = null;
//    private JsonRecyclerView jsonRecyclerView = null;

    private MapView mapView;
    private MapControl mapControl;
    private Workspace workspace;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        initView(rootView);
        initListening(rootView);
//        openMap(rootView);
        return rootView;

    }

    private void openMap(View rootView) {
//        mapView = rootView.findViewById(R.id.mapview1);
//        mapControl = mapView.getMapControl();
//        workspace = new Workspace();
//        mapControl.getMap().setWorkspace(workspace);
//        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
//        info.setEngineType(EngineType.UDB);
//        info.setServer(SDCARD + "/SampleData/湖北/PopulationHubei.udb");
//        Datasource datasource = workspace.getDatasources().open(info);
//        mapControl.getMap().getLayers().add(datasource.getDatasets().get(1),false);

//        final String dataPath = SDCARD + "/SampleData/湖北/Population.smwu";
//        Workspace workspace = new Workspace();
//        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
//        info.setServer(dataPath);
//        info.setType(WorkspaceType.SMWU);
//        boolean isOpen = workspace.open(info);
//        if (!isOpen) {
//            return;
//        }
//        mapView = rootView.findViewById(R.id.mapview1);
//        Map map = mapView.getMapControl().getMap();
//        map.setWorkspace(workspace);
//        map.open(workspace.getMaps().get(1));
//        map.setViewBounds(map.getBounds());
//
//        map.refresh();
    }

    private void initView(View rootView) {
        rfaLayout = (RapidFloatingActionLayout) rootView.findViewById(R.id.card_list_sample_rfal);
        rfaButton = (RapidFloatingActionButton) rootView.findViewById(R.id.card_list_sample_rfab);

        RapidFloatingActionContentCardListView rfaContent = new RapidFloatingActionContentCardListView(mContext);
        rfaContent.setOnRapidFloatingActionContentCardListViewListener(this);

        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem().setName("流处理模型JSON").setResId(R.mipmap.head_test_a));
        cardItems.add(new CardItem().setName("创建流数据服务").setResId(R.mipmap.head_test_b));
        cardItems.add(new CardItem().setName("更新流数据服务").setResId(R.mipmap.head_test_c));
        rfaContent.setList(cardItems);


        rfaLayout.setIsContentAboveLayout(false);
        rfaLayout.setDisableContentDefaultAnimation(true);

        rfabHelper = new RapidFloatingActionHelper(
                mContext,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();


        jsonViewLayout = rootView.findViewById(R.id.jsonView);
//        jsonViewLayout.bindJson("your json strings." || JSONObject || JSONArray);

//        jsonRecyclerView = rootView.findViewById(R.id.rv_json);
//        jsonRecyclerView.bindJson("your json strings." || JSONObject || JSONArray);
    }

    private DataFlowService dataFlowOnline = null;
    private  StreamingService streamingService = null;
    private StreamingModelFactory streamingModelFactory = null;

    private void initListening(View rootView) {
        streamingService = StreamingService.getInstance();
        streamingService.addOnResponseListener(new OnResponseListener() {
            @Override
            public void onFailed(Exception exception) {
                String message = exception.getMessage();
                Log.e(TAG, "onFailed: " + message);
            }

            @Override
            public void onResponse(Response response) {
                String s = "";
                try {
                    s = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onResponse: " + s);
            }
        });

        streamingService.setManagerUrL("http://192.168.0.28:8090/iserver/manager/");
        streamingService.setToken("Qrau6OSwUrQhUocr74-BQEkZQoCIJxTO4sHkOy5YFDuO-Fj0C6VhE1oGfQPmXxFPvMOBpYvQHnEz4oPA3A2kTg..");

        streamingModelFactory = StreamingModelFactory.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Toast.makeText(mContext, "自定义操作", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onRFACCardListItemClick(int position) {
        switch (position) {
            case 0:
                try {
                    JSONObject  nodeDic = new JSONObject();
                    nodeDic.put("AQIReceiver", streamingModelFactory.buildNodeDic(createAQIReceiver()));
                    nodeDic.put("WebSocketClientSender", streamingModelFactory.buildNodeDic(createWebSocketClientSender()));

                    JSONObject streamingModel= streamingModelFactory.createStreamingModel(nodeDic);

                    jsonViewLayout.bindJson(streamingModel);
//                    jsonViewLayout.expandAll();

//                    jsonRecyclerView.bindJson(streamingModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                //两种创建服务的方式
                //一、通过已有的流处理模型文件创建
                // streamingService.createStreamingService("liushujufuwu-zzb", "D:/SuperMap/SuperMapiServer9D/samples/streamingmodels/AQI/AQI.streaming", "configJsonPath");

                //二、通过流处理模型配置内容（服务器会自动根据内容生成配置文件）
//                streamingModelFactory.setInterval(5000);
//                try {
//                    JSONObject  nodeDic = new JSONObject();
//                    nodeDic.put("AQIReceiver", streamingModelFactory.buildNodeDic(createAQIReceiver()));
//                    nodeDic.put("WebSocketClientSender", streamingModelFactory.buildNodeDic(createWebSocketClientSender()));
//
//                    JSONObject streamingModel= streamingModelFactory.createStreamingModel(nodeDic);
//
//                    streamingService.createStreamingService("liushujufuwu-zzb", streamingModel.toString(), "configJsonContent");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                break;
            case 2:
                //更新服务
//                try {
//                    JSONObject  nodeDic = new JSONObject();
//                    nodeDic.put("AQIReceiver", streamingModelFactory.buildNodeDic(createAQIReceiver()));
//                    nodeDic.put("WebSocketClientSender", streamingModelFactory.buildNodeDic(createWebSocketClientSender()));
//
//                    JSONObject streamingModel= streamingModelFactory.createStreamingModel(nodeDic);
//
//                    String configJsonPath = "D:\\SuperMap\\SuperMapiServer9D\\webapps\\iserver\\WEB-IN|F\\iserver-streaming-setting-new\\liushujufuwu-zzb.streaming";
//                    String configJsonContent = streamingModel.toString();
//                    streamingService.updateStreamingService("liushujufuwu-zzb", configJsonContent, configJsonPath);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                break;
        }
        Toast.makeText(mContext, "clicked " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    private HttpReceiver createAQIReceiver() {
        HttpReceiver httpReceiver = new HttpReceiver();
        httpReceiver.setName("AQIReceiver");
        httpReceiver.setUrl("http://www.supermapol.com/iserver/services/aqi/restjsr/aqi/pm2_5.json?bounds=-113.90625001585,-52.029966847235,113.90625001585,69.175579762077&to=910111");

        HttpReceiver.ReaderBean readerBean = new HttpReceiver.ReaderBean();
        readerBean.setClassName("com.supermap.bdt.streaming.formatter.JsonFormatter");
        readerBean.setIsJsonArray("true");
        readerBean.setArrayExpression("airQualityList");
        httpReceiver.setReader(readerBean);

        List<String> nextNodes = new ArrayList<>();
        nextNodes.add("WebSocketClientSender");
        httpReceiver.setNextNodes(nextNodes);

        HttpReceiver.MetadataBean metadataBean = new HttpReceiver.MetadataBean();
        HttpReceiver.MetadataBean.FieldInfosBean fieldInfosBean01 = new HttpReceiver.MetadataBean.FieldInfosBean();
        fieldInfosBean01.setName("X");
        fieldInfosBean01.setSource("location.x");
        fieldInfosBean01.setNType("DOUBLE");
        HttpReceiver.MetadataBean.FieldInfosBean fieldInfosBean02 = new HttpReceiver.MetadataBean.FieldInfosBean();
        fieldInfosBean02.setName("Y");
        fieldInfosBean02.setSource("location.y");
        fieldInfosBean02.setNType("DOUBLE");
        HttpReceiver.MetadataBean.FieldInfosBean fieldInfosBean03 = new HttpReceiver.MetadataBean.FieldInfosBean();
        fieldInfosBean03.setName("positionName");
        fieldInfosBean03.setSource("positionName");
        fieldInfosBean03.setNType("TEXT");
        HttpReceiver.MetadataBean.FieldInfosBean fieldInfosBean04 = new HttpReceiver.MetadataBean.FieldInfosBean();
        fieldInfosBean04.setName("aqi");
        fieldInfosBean04.setSource("aqi");
        fieldInfosBean04.setNType("DOUBLE");
        List<HttpReceiver.MetadataBean.FieldInfosBean> fieldInfosBeans = new ArrayList<>();
        fieldInfosBeans.add(fieldInfosBean01);
        fieldInfosBeans.add(fieldInfosBean02);
        fieldInfosBeans.add(fieldInfosBean03);
        fieldInfosBeans.add(fieldInfosBean04);
        metadataBean.setFieldInfos(fieldInfosBeans);
        metadataBean.setEpsg(3857);
        metadataBean.setFeatureType("POINT");
        metadataBean.setIdFieldName("AirQuality");
        metadataBean.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");

        httpReceiver.setMetadata(metadataBean);

        return httpReceiver;
    }

    private WebSocketClientSender createWebSocketClientSender() {
        WebSocketClientSender webSocketClientSender = new WebSocketClientSender();

        webSocketClientSender.setName("WebSocketClientSender");

        List<String> prevNodes = new ArrayList<>();
        prevNodes.add("AQIReceiver");
        webSocketClientSender.setPrevNodes(prevNodes);

        webSocketClientSender.setPath("ws://192.168.0.28:8800/iserver/services/dataflow/dataflow/broadcast?token=Qrau6OSwUrQhUocr74-BQEkZQoCIJxTO4sHkOy5YFDuO-Fj0C6VhE1oGfQPmXxFPvMOBpYvQHnEz4oPA3A2kTg..");

        WebSocketClientSender.FormatterBean formatterBean = new WebSocketClientSender.FormatterBean();
        formatterBean.setClassName("com.supermap.bdt.streaming.formatter.GeoJsonFormatter");
        webSocketClientSender.setFormatter(formatterBean);

        return webSocketClientSender;
    }

    private GeoFilter getGeofilter() {
        GeoFilter geoFilter = new GeoFilter();
        geoFilter.setClassName("com.supermap.bdt.streaming.filter.GeoFilter");
        geoFilter.setCaption("地理过滤器");
        geoFilter.setName("GeoFilter");

        List<String> nextNodes = new ArrayList();
        nextNodes.add("GeoFilter");
        geoFilter.setNextNodes(nextNodes);

        List<String> prevNodes = new ArrayList();
        prevNodes.add("Sender");
        geoFilter.setPrevNodes(prevNodes);

        geoFilter.setDescription("");

        GeoFilter.ConnectionBean.InfoBean infoBean01 = new GeoFilter.ConnectionBean.InfoBean();
        infoBean01.setServer("数据源文件路径01");
        List<String> datasets01 = new ArrayList<>();
        datasets01.add("数据集名称01");
        datasets01.add("数据集名称02");
        infoBean01.setDatasetNames(datasets01);

        GeoFilter.ConnectionBean.InfoBean infoBean02 = new GeoFilter.ConnectionBean.InfoBean();
        infoBean02.setServer("数据源文件路径02");
        List<String> datasets02 = new ArrayList<>();
        datasets02.add("数据集名称01");
        datasets02.add("数据集名称02");
        infoBean02.setDatasetNames(datasets02);

        List<GeoFilter.ConnectionBean.InfoBean> infoBeans = new ArrayList<>();
        infoBeans.add(infoBean01);
        infoBeans.add(infoBean02);

        GeoFilter.ConnectionBean connectionBean = new GeoFilter.ConnectionBean();
        connectionBean.setType("数据源类型");
        connectionBean.setInfo(infoBeans);

        geoFilter.setConnection(connectionBean);
        geoFilter.setMode("inside");

        return geoFilter;
    }

}
