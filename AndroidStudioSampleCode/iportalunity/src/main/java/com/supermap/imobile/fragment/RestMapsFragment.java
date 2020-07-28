package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.supermap.imobile.adapter.RestMapsAdapter;
import com.supermap.imobile.bean.RestMapsBean;
import com.supermap.iportalservices.IPortalService;
import com.supermap.iportalservices.OnResponseListener;
import com.supermap.imobile.iportalservices.MapViewActivity;
import com.supermap.imobile.iportalservices.R;
import okhttp3.Response;

import java.util.ArrayList;

/**
 * 打开地图
 */
public class RestMapsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RestMapsFragment";
    private Context mContext = null;
    private ListView listView = null;
    private RestMapsAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restmaps, container, false);
        initView(rootView);
        return rootView;
    }

    private String restUrl = null;
    private void initView(View rootView) {
        listView = rootView.findViewById(R.id.list_restmaps);

        Bundle arguments = getArguments();
        restUrl = arguments.getString("restUrl");
        IPortalService.getInstance().addOnResponseListener(new OnResponseListener() {
            @Override
            public void onFailed(Exception exception) {

            }

            @Override
            public void onResponse(Response response) {
                String responseBody = null;
                ArrayList<RestMapsBean> restMapsBeanList = new ArrayList<>();
                try {
                    responseBody = response.body().string();

                    //Json的解析类对象
                    JsonParser parser = new JsonParser();
                    //将JSON的String 转成一个JsonArray对象
                    JsonArray jsonArray = parser.parse(responseBody).getAsJsonArray();

                    Gson gson = new Gson();
                    //加强for循环遍历JsonArray
                    for (JsonElement user : jsonArray) {
                        //使用GSON，直接转成Bean对象
                        RestMapsBean restMapsBean = gson.fromJson(user, RestMapsBean.class);
                        restMapsBeanList.add(restMapsBean);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    showToast(e.getMessage());
                    return;
                }
                if (adapter == null) {
                    adapter = new RestMapsAdapter(mContext, restMapsBeanList, restUrl);
                }
                ((MapViewActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            }
        });
        IPortalService.getInstance().getRestMaps(restUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void showToast(final String msg){
        if (mContext != null) {
            ((MapViewActivity)mContext).runOnUiThread(() -> Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show());
        }
    }

}
