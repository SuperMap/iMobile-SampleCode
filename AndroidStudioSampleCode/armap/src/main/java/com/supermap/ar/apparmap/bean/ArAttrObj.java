package com.supermap.ar.apparmap.bean;

import com.supermap.ar.areffect.ARViewElement;
import com.supermap.data.Point2D;

public class ArAttrObj {

    private ARViewElement arViewElement;
    private Point2D geoPosition;
    private String titile;
    private String content;

    public ARViewElement getArViewElement() {
        return arViewElement;
    }

    public void setArViewElement(ARViewElement arViewElement) {
        this.arViewElement = arViewElement;
    }

    public Point2D getGeoPosition() {
        return geoPosition;
    }

    public void setGeoPosition(Point2D geoPosition) {
        this.geoPosition = geoPosition;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
