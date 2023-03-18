package com.supermap.imobile.streamnode;

public class FeatureCalculateMapper extends StreamNode {

    public FeatureCalculateMapper() {
        this.className = "com.supermap.bdt.streaming.map.FeatureCalculateMapper";
    }

    /**
     * nextNodes : []
     * prevNodes : []
     * fieldName : 目标字段名称
     * expression : 字段的运算表达式
     */

    private String fieldName;
    private String expression;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
