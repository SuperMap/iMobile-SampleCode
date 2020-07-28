package com.supermap.imobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.AR.ARMode;
import com.supermap.mapping.AR.ArControl2;
import com.supermap.mapping.MapControl;

/**
 * 我的地图界面
 */
public class MyMapsFragment extends Fragment {

    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getPath() + "/";
    public  ArControl2 mArControl2;
    private MapControl mMapcontrol = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mymaps, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMyMap();
    }


    private void initMyMap() {
        Workspace m_workspace = new Workspace();
        WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
        info.setServer(SDCARD + "SampleData/AR/supermapindoor.smwu");
        info.setType(WorkspaceType.SMWU);
        if (m_workspace.open(info)) {
            mArControl2 = (ArControl2) getView().findViewById(R.id.test_arcontrol);
            mMapcontrol = mArControl2.mapControl;
            mMapcontrol.getMap().setWorkspace(m_workspace);
            String mapName = m_workspace.getMaps().get(0);
            mMapcontrol.getMap().open(mapName);
            if (!mMapcontrol.getMap().IsArmap()) {
                mMapcontrol.getMap().setIsArmap(true);
            }

            mArControl2.setARMode(ARMode.AR_NORMAL);
            mArControl2.hideCamera();

        } else {
            System.out.println("Open Workspace failed");
        }
        mMapcontrol.getMap().refresh();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mArControl2.closeFollowingMode();
    }
}
