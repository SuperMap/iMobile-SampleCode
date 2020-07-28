package com.supermap.imobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.supermap.imobile.control.MyDatasControl;
import com.supermap.imobile.bean.DownloadDataEvent;
import com.supermap.imobile.bean.GetDataEvent;
import com.supermap.imobile.bean.UploadDataEvent;
import com.supermap.iportalservices.IPortalService;
import com.supermap.imobile.iportalservices.R;
import com.supermap.imobile.utils.Utils;
import com.yalantis.phoenix.PullToRefreshView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 我的数据界面
 */
public class MyDatasFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "MyDatasFragment";
    private Context mContext = null;

    private MyDatasControl mMyDatasControl = null;
    private ListView mDatasListView = null;
    private PullToRefreshView mPullToRefreshView = null;
    private FloatingActionButton floatingActionButton = null;

    private RelativeLayout layout_progress;
    private TextView tv_fileName;
    private ProgressBar progressBar;
    private TextView progesssValue;
    private Button btn_cancel_upload;
    private Button btn_cancel_download;
    private LinearLayout ll_progesssbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mydatas, container, false);
        initView(rootView);
        initListening(rootView);
        initControl();
        return rootView;
    }

    private void initView(View rootView) {
        mDatasListView = rootView.findViewById(R.id.list_mydatas);
        mPullToRefreshView = rootView.findViewById(R.id.pull_to_refresh);
        floatingActionButton = rootView.findViewById(R.id.fab);

        layout_progress = rootView.findViewById(R.id.rl_download_upload);
        tv_fileName = rootView.findViewById(R.id.tv_filename);
        progressBar = rootView.findViewById(R.id.progesss);
        progesssValue = rootView.findViewById(R.id.progesss_value1);
        btn_cancel_upload = rootView.findViewById(R.id.btn_cancel_upload);
        btn_cancel_download = rootView.findViewById(R.id.btn_cancel_download);
        ll_progesssbar = rootView.findViewById(R.id.ll_progesssbar);
    }

    /**
     * 设置进度显示在对应的位置
     */
    public void setPosition() {
//        int width = ((Activity)mContext).getWindowManager().getDefaultDisplay().getWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width  = metrics.widthPixels; //得到屏幕的宽度
        int px = Utils.dip2px(mContext, 6 * 2); //因为布局中设置了左右margin
        width = width - px;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) progesssValue.getLayoutParams();
        int progress = progressBar.getProgress();
        int tW = progesssValue.getWidth();
        if (width * progress / 100 + tW * 0.3 > width) {
            params.leftMargin = (int) (width - tW * 1.1);
        } else if (width * progress / 100 < tW * 0.7) {
            params.leftMargin = 0;
        } else {
            params.leftMargin = (int) (width * progress / 100 - tW * 0.7);
        }
        progesssValue.setLayoutParams(params);

    }

    //控制服务模块联网请求和界面刷新
    private void initControl() {
        MyDatasControl.createInstance(mContext, mDatasListView, mPullToRefreshView, ll_progesssbar);
        mMyDatasControl = MyDatasControl.getInstance();

        mPullToRefreshView.setRefreshing(true);
        mMyDatasControl.getMyDatas();
    }

    private void initListening(View rootView) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "请选择要上传的文件", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });
        btn_cancel_download.setOnClickListener(this);
        btn_cancel_upload.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                String path = Utils.getPath(mContext, uri);

                boolean isValid = Utils.verifyDataPath(path);
                if (isValid) {
                    mMyDatasControl.uploadData(path);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetDataEvent(GetDataEvent event) {
        if (event.isSuccess()) {
            mPullToRefreshView.setRefreshing(false);
            Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
        } else if (event.getError() != null) {
            mPullToRefreshView.setRefreshing(false);
            Toast.makeText(mContext, "刷新失败：" + event.getError(), Toast.LENGTH_SHORT).show();
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadDataEvent(UploadDataEvent event) {
        if (event.isUpLoading()) {
            layout_progress.setVisibility(View.VISIBLE);
            btn_cancel_upload.setVisibility(View.VISIBLE);
            btn_cancel_download.setVisibility(View.GONE);

            tv_fileName.setText("文件路径：" + event.getFilePath());
            progressBar.setProgress(event.getProgress());
            progesssValue.setText(new StringBuffer().append(event.getProgress()).append("%"));
            progesssValue.post(() -> setPosition());
        } else if (event.isSuccess()){
            layout_progress.setVisibility(View.GONE);
            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
        } else if (event.getError() != null) {
            Toast.makeText(mContext, event.getError(), Toast.LENGTH_SHORT).show();
            layout_progress.setVisibility(View.GONE);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadDataEvent(DownloadDataEvent event) {
        if (!event.getMode().equals(TAG)) {
            return;
        }
        Log.e(TAG, "onDownloadDataEvent");
        if (event.isDownLoading()) {
            layout_progress.setVisibility(View.VISIBLE);
            btn_cancel_download.setVisibility(View.VISIBLE);
            btn_cancel_upload.setVisibility(View.GONE);

            tv_fileName.setText("文件路径：" + event.getFilePath());
            progressBar.setProgress(event.getProgress());
            progesssValue.setText(new StringBuffer().append(event.getProgress()).append("%"));
            progesssValue.post(() -> setPosition());
        } else if (event.isSuccess()) {
            Toast.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show();
            layout_progress.setVisibility(View.GONE);
        } else if (event.getError() != null) {
            Toast.makeText(mContext, event.getError(), Toast.LENGTH_SHORT).show();
            layout_progress.setVisibility(View.GONE);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_download:
                //取消下载
                IPortalService.getInstance().cancelDownload();
                layout_progress.setVisibility(View.GONE);
                Toast.makeText(mContext, "取消下载", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_cancel_upload:
                //取消上传
                IPortalService.getInstance().cancelUpload();
                layout_progress.setVisibility(View.GONE);
                Toast.makeText(mContext, "取消上传", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
