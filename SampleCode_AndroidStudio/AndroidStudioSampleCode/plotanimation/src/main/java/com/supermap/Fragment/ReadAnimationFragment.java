package com.supermap.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.supermap.mapping.MapControl;
import com.supermap.plot.AnimationManager;
import com.supermap.plotanimation.MainActivity;
import com.supermap.plotanimation.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ValidFragment")
public class ReadAnimationFragment extends Fragment implements View.OnClickListener {
    //
    View rootView;
    //
    Button btn_readxml;
    Button btn_play;
    Button btn_pause;
    Button btn_reset;
    Button btn_stop;
    //
    MapControl mapControl;
    //
    Timer timer;
    @SuppressLint("ValidFragment")
    public ReadAnimationFragment(MapControl mapControl){
        this.mapControl=mapControl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_readanimation, container, false);
        initView();
        return rootView;
    }
    private void initView(){
        btn_readxml=(Button)rootView.findViewById(R.id.btn_readxml);
        btn_play=(Button)rootView.findViewById(R.id.btn_play);
        btn_pause=(Button)rootView.findViewById(R.id.btn_pause);
        btn_reset=(Button)rootView.findViewById(R.id.btn_reset);
        btn_stop=(Button)rootView.findViewById(R.id.btn_stop);

        //
        btn_readxml.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_readxml:
                readxml();
                break;
            case R.id.btn_play:
                playAniamtion();
                break;
            case R.id.btn_pause:
                AnimationManager.getInstance().pause();
                break;
            case R.id.btn_reset:
                AnimationManager.getInstance().reset();
                break;
            case R.id.btn_stop:
                stopAniamtion();
                break;
        }
    }
    private void readxml(){
        File file=new File(MainActivity.RootPath+"SampleData/Fujian/plot/TourLineFile.xml");
        mapControl.setAnimations();
        boolean isread= AnimationManager.getInstance().getAnimationFromXML(file.getAbsolutePath());
        if (isread){
            shownToast("读取xml成功");
        }
        else {
            shownToast("读取xml失败");
        }

    }
    private void shownToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
    private void playAniamtion(){
        if (timer==null){
       timer= new Timer();
       timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AnimationManager.getInstance().excute();
            }
        },0,500);
        }
        AnimationManager.getInstance().play();
    }
    private void stopAniamtion(){
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        AnimationManager.getInstance().stop();
    }
}
