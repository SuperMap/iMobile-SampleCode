package com.supermap.imobile.streamnode;

public class FeatureInsertMapper extends StreamNode {

    public FeatureInsertMapper() {
        this.className = "com.supermap.bdt.streaming.map.FeatureInsertMapper";
    }

    /**
     * nextNodes : []
     * prevNodes : []
     * insertIndex : 11
     * fieldName : 添加字段名称
     * nType : BOOLEAN
     * expression : 运算表达式
     */

    private int insertIndex;
    private String fieldName;
    private String nType;
    private String expression;

    public int getInsertIndex() {
        return insertIndex;
    }

    public void setInsertIndex(int insertIndex) {
        this.insertIndex = insertIndex;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getNType() {
        return nType;
    }

    public void setNType(String nType) {
        this.nType = nType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
