package com.supermap.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.supermap.Util.AnimationGroup;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.mapping.Action;
import com.supermap.mapping.MapControl;
import com.supermap.plot.AnimationDefine;
import com.supermap.plot.AnimationGO;
import com.supermap.plot.AnimationGrow;
import com.supermap.plot.AnimationManager;
import com.supermap.plot.AnimationScale;
import com.supermap.plot.GeoGraphicObject;
import com.supermap.plot.GraphicObjectType;
import com.supermap.plotanimation.MainActivity;
import com.supermap.plotanimation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

public class AnimationSettingDialog extends Dialog implements View.OnClickListener {
    //
    TextView tv_animation_title;
    //
    EditText edit_duration;
    EditText edit_starttime;
    EditText edit_endscale;
    EditText edit_startscale;
    //
    RadioGroup radiogroup_playindex;
    //
    RadioButton radiobtn_playwithoter;
    RadioButton radiobtn_playafteroter;
    RadioButton radiobtn_playfirst;
    //
    Button btn_cancle;
    Button btn_save;
    //
    LinearLayout layou_scaleanimation;
    //
    double durationtime=0;
    double starttime=0;
    double startlocation=0;
    double startscale=0;
    double endscale=0;
    //
    MapControl mapControl;
    Geometry geometry;
    //
    public List<AnimationGO> animationlist;
    //
    AnimationListListener animationListListener;

    public AnimationSettingDialog(MapControl mapControl, Geometry geometry, List<AnimationGO> animationlist) {
        super(mapControl.getContext());
        this.setContentView(R.layout.dialog_ainiamtion);
        this.geometry=geometry;
        this.mapControl=mapControl;
        this.animationlist=animationlist;
        initView();
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        //
        tv_animation_title=(TextView)findViewById(R.id.tv_animation_title);
        edit_duration=(EditText)findViewById(R.id.edit_duration);
        edit_starttime=(EditText)findViewById(R.id.edit_starttime);
        edit_endscale=(EditText)findViewById(R.id.edit_endscale);
        edit_startscale=(EditText)findViewById(R.id.edit_startscale);
        radiogroup_playindex=(RadioGroup)findViewById(R.id.radiogroup_playindex);
        radiobtn_playwithoter=(RadioButton)findViewById(R.id.radiobtn_playwithoter);
        radiobtn_playafteroter=(RadioButton)findViewById(R.id.radiobtn_playafteroter);
        radiobtn_playfirst=(RadioButton)findViewById(R.id.radiobtn_playfirst);
        btn_cancle=(Button)findViewById(R.id.btn_cancle);
        btn_save=(Button)findViewById(R.id.btn_save);
        layou_scaleanimation=(LinearLayout) findViewById(R.id.layou_scaleanimation);
        //
        btn_cancle.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        radiogroup_playindex.setOnCheckedChangeListener(checkedChangeListener);

        if (checkGeometryTtype().equals(GraphicObjectType.SYMBOL_DOT)){
            tv_animation_title.setText("比例动画");
        }
        else if (checkGeometryTtype().equals(GraphicObjectType.SYMBOL_ALGO)){
            tv_animation_title.setText("生长动画");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                saveAnimationsetting();
                dismiss();
                break;
            case R.id.btn_cancle:
                dismiss();
                break;
        }
        mapControl.setAction(Action.PAN);
    }
    RadioGroup.OnCheckedChangeListener checkedChangeListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int count=animationlist.size();
            switch (i) {
                case R.id.radiobtn_playwithoter:
                    if (count==0){
                        starttime=0;
                    }
                    else {
                        AnimationGO tempAnimation=animationlist.get(count-1);
                        starttime=tempAnimation.getStartTime();
                    }
                    break;
                case R.id.radiobtn_playafteroter:
                    if (count==0){
                        starttime=0;
                    }
                    else {
                        AnimationGO tempAnimation=animationlist.get(count-1);
                        starttime=tempAnimation.getStartTime()+tempAnimation.getDuration();
                    }
                    break;
                case R.id.radiobtn_playfirst:
                    starttime=0;
                    break;
            }

        }
    };
    private GraphicObjectType checkGeometryTtype(){
        GeoGraphicObject geoGraphicObject=(GeoGraphicObject)geometry;
        return geoGraphicObject.getSymbolType();
    }
    private void saveAnimationsetting(){
        durationtime= Double.parseDouble(edit_duration.getText().toString());
        starttime= Double.parseDouble(edit_starttime.getText().toString());
        startscale=Double.parseDouble(edit_startscale.getText().toString());
        endscale=Double.parseDouble(edit_endscale.getText().toString());

        if (checkGeometryTtype().equals(GraphicObjectType.SYMBOL_DOT)){
            AnimationScale animationScale= (AnimationScale) AnimationManager.getInstance().createAnimation(AnimationDefine.AnimationType.ScaleAnimation);
            animationScale.setDuration(durationtime);
            animationScale.setStartTime(starttime);
            animationScale.setStartScaleFactor(startscale);
            animationScale.setEndScaleFactor(endscale);
            animationScale.setName(String.valueOf(geometry.getID()));
            animationScale.setGeometry((GeoGraphicObject) geometry,mapControl.getHandle(),mapControl.getEditLayer().getName());
            AnimationGroup.getInstance().getGroup().addAnimation(animationScale);
            animationlist.add(animationScale);
        }
        else if (checkGeometryTtype().equals(GraphicObjectType.SYMBOL_ALGO)){
            AnimationGrow animationGrow=(AnimationGrow)AnimationManager.getInstance().createAnimation(AnimationDefine.AnimationType.GrowAnimation);
            animationGrow.setDuration(durationtime);
            animationGrow.setStartTime(starttime);
            animationGrow.setStartLocation(startscale);
            animationGrow.setEndLocation(endscale);
            animationGrow.setName(String.valueOf(geometry.getID()));
            animationGrow.setGeometry((GeoGraphicObject) geometry,mapControl.getHandle(),mapControl.getEditLayer().getName());
            AnimationGroup.getInstance().getGroup().addAnimation(animationGrow);
            animationlist.add(animationGrow);
        }
        animationListListener.getList(animationlist);
    }
    public void addAnimationListListener(AnimationListListener listListener){
        this.animationListListener=listListener;
    }
    public interface AnimationListListener{
        void getList(List<AnimationGO> list);
    }
}
