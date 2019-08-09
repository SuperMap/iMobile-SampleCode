package com.supermap.imobile.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supermap.imobile.adapter.MyLayerAdapter;
import com.supermap.imobile.bean.MyLayerData;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.Layers;
import com.supermap.mapping.MapControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的图层管理界面
 */
public class MyLayerManageFragment extends Fragment {

    private Context mContext = null;
    private MapControl mMapControl;
    private RecyclerView layerListView;
    private List<MyLayerData> myLayerDataList = new ArrayList<MyLayerData>();
    public static MyLayerAdapter Layeradapter;

    public MyLayerManageFragment() {

    }

    @SuppressLint("ValidFragment")
    public MyLayerManageFragment(MapControl mMapControl) {
        this.mMapControl = mMapControl;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mylayermanage2, container, false);
        initView(rootView);
        initData();


         Layeradapter = new MyLayerAdapter(myLayerDataList, mMapControl);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        layerListView.setLayoutManager(manager);//默认为垂直显示
        layerListView.setAdapter(Layeradapter);//数据适配
        return rootView;
    }

    //加载界面
    private void initView(View view) {
        layerListView = view.findViewById(R.id.layermanage);


    }

    //准备数据
    private void initData() {
        Layers layers = mMapControl.getMap().getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i).getDataset() != null) {
                MyLayerData myLayerData = new MyLayerData(layers.get(i));//将每个图层存入list中
                myLayerDataList.add(myLayerData);
            }

        }
    }
    public  static  void refresh(){
        Layeradapter.refresh();
    }


}
