package com.supermap.Util;

import com.supermap.plot.AnimationManager;

public class AnimationGroup extends com.supermap.plot.AnimationGroup {
    static  AnimationGroup animationGroup=null;
    com.supermap.plot.AnimationGroup group;
    public static AnimationGroup getInstance(){
        if (animationGroup==null){
            animationGroup= new  AnimationGroup();
        }
        return animationGroup;
    }
    public com.supermap.plot.AnimationGroup getGroup(){
        if (group==null){
            group=AnimationManager.getInstance().addAnimationGroup("PlotGroup");
        }
        return group;
    }
}
