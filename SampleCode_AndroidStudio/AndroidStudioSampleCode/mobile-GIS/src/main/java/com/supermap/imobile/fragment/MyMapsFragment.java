package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.view.LayerListView;

/**
 * 我的地图界面
 */
public class MyMapsFragment extends Fragment {

    private Context mContext = null;
    LayerListView layerListView=null;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mymaps, container, false);
        return rootView;
    }



}
