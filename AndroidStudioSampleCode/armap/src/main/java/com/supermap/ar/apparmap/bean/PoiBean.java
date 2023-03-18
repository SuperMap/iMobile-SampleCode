package com.supermap.ar.apparmap.bean;

import com.supermap.ar.Point3D;
import com.supermap.ar.areffect.AREffectElement;
import com.supermap.ar.areffect.ARGltfElement;

public class PoiBean {
    public Point3D point;
    public AREffectElement element;
    public ARGltfElement gltfElementPoi;
    public ARGltfElement gltfElementTxt;

    public boolean isUp = false;
    public boolean isBusy = false;

    public PoiBean(Point3D point, AREffectElement element, ARGltfElement gltfElementPoi, ARGltfElement gltfElementTxt) {
        this.point = point;
        this.element = element;
        this.gltfElementPoi = gltfElementPoi;
        this.gltfElementTxt = gltfElementTxt;
    }
}
