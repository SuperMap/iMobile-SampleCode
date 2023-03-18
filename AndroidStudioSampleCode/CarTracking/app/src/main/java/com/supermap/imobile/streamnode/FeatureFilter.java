package com.supermap.imobile.streamnode;

public class FeatureFilter extends StreamNode {

    public FeatureFilter() {
        this.className = "com.supermap.bdt.streaming.filter.FeatureFilter";
    }

    /**
     * nextNodes : []
     * prevNodes : []
     * filter : 过滤表达式
     */

    private String filter;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
