package com.supermap.ar.apparmap.Element;

import com.supermap.ar.Point3D;
import com.supermap.ar.areffect.preset.Shape;

import java.util.List;

/**
 * 封装的建筑物和顶面数据类
 */
public class MyBuildingInfo {
    private List<Shape> myList;
    private List<Point3D> topPoints;

    public MyBuildingInfo(List<Shape> myList, List<Point3D> topPoints) {
        this.myList = myList;
        this.topPoints = topPoints;
    }

    public List<Shape> getMyList() {
        return myList;
    }

    public void setMyList(List<Shape> myList) {
        this.myList = myList;
    }

    public List<Point3D> getTopPoints() {
        return topPoints;
    }

    public void setTopPoints(List<Point3D> topPoints) {
        this.topPoints = topPoints;
    }
}
