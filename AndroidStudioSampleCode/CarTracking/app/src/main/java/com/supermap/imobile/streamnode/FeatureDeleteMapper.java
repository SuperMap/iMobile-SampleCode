package com.supermap.imobile.streamnode;

import java.util.List;

public class FeatureDeleteMapper extends StreamNode {

    public FeatureDeleteMapper() {
        this.className = "com.supermap.bdt.streaming.map.FeatureDeleteMapper";
    }

    private List<String> deleteFieldNames;

    public List<String> getDeleteFieldNames() {
        return deleteFieldNames;
    }

    public void setDeleteFieldNames(List<String> deleteFieldNames) {
        this.deleteFieldNames = deleteFieldNames;
    }
}
