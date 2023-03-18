package com.supermap.ar.apparmap.bean;

import com.supermap.ar.Point3D;

import java.util.HashMap;
import java.util.List;

public class ArObj {

    private List<List<Point3D>> pointsList;
    private Integer smID;

    public List<List<Point3D>> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<List<Point3D>> pointsList) {
        this.pointsList = pointsList;
    }

    public Integer getSmID() {
        return smID;
    }

    public void setSmID(Integer smID) {
        this.smID = smID;
    }
}
