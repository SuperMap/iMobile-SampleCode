package com.supermap.imobile.streamnode;

import java.util.List;

public class StaticRDDJoinMapper extends StreamNode {

    public StaticRDDJoinMapper() {
        this.className = "com.supermap.bdt.streaming.map.StaticRDDJoinMapper";
    }

    /**
     * nextNodes : []
     * prevNodes : []
     * csvFile : CSV文件路径
     * idFields : ["补充字段名称01","补充字段名称02"]
     */

    private String csvFile;
    private List<String> idFields;

    public String getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }

    public List<String> getIdFields() {
        return idFields;
    }

    public void setIdFields(List<String> idFields) {
        this.idFields = idFields;
    }
}
