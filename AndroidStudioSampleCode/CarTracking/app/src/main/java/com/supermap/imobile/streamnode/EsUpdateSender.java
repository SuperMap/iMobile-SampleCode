package com.supermap.imobile.streamnode;

public class EsUpdateSender extends StreamNode {

    public EsUpdateSender() {
        this.className = "com.supermap.bdt.streaming.sender.EsUpdateSender";
    }

    /**
     * nextNodes : []
     * url : ES服务地址
     * port : ES服务端口
     * index : ES节点名称
     * typ : ES类型名称
     * idFieldName : ID字段名
     */

    private String url;
    private String port;
    private String index;
    private String typ;
    private String idFieldName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }
}
