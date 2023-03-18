package com.supermap.imobile.streamnode;

import java.util.List;

public class GeoTaggerMapper extends StreamNode {

    public GeoTaggerMapper() {
        this.className = "com.supermap.bdt.streaming.map.GeoTaggerMapper";
    }

    /**
     * nextNodes : []
     * prevNodes : []
     * connection : {"type":"数据源类型","info":[{"server":"数据源文件路径","datasetNames":["数据集名称01","数据集名称02"]},{"server":"数据源文件路径","datasetNames":["数据集名称01","数据集名称02"]}]}
     * fenceName : 名称字段名
     * fenceID : ID字段名
     * withinFieldName : 进入地理围栏字段名
     * statusFieldName : 状态字段名
     */

    private ConnectionBean connection;
    private String fenceName;
    private String fenceID;
    private String withinFieldName;
    private String statusFieldName;

    public ConnectionBean getConnection() {
        return connection;
    }

    public void setConnection(ConnectionBean connection) {
        this.connection = connection;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getFenceID() {
        return fenceID;
    }

    public void setFenceID(String fenceID) {
        this.fenceID = fenceID;
    }

    public String getWithinFieldName() {
        return withinFieldName;
    }

    public void setWithinFieldName(String withinFieldName) {
        this.withinFieldName = withinFieldName;
    }

    public String getStatusFieldName() {
        return statusFieldName;
    }

    public void setStatusFieldName(String statusFieldName) {
        this.statusFieldName = statusFieldName;
    }

    public static class ConnectionBean {
        /**
         * type : 数据源类型
         * info : [{"server":"数据源文件路径","datasetNames":["数据集名称01","数据集名称02"]},{"server":"数据源文件路径","datasetNames":["数据集名称01","数据集名称02"]}]
         */

        private String type;
        private List<InfoBean> info;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<InfoBean> getInfo() {
            return info;
        }

        public void setInfo(List<InfoBean> info) {
            this.info = info;
        }

        public static class InfoBean {
            /**
             * server : 数据源文件路径
             * datasetNames : ["数据集名称01","数据集名称02"]
             */

            private String server;
            private List<String> datasetNames;

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public List<String> getDatasetNames() {
                return datasetNames;
            }

            public void setDatasetNames(List<String> datasetNames) {
                this.datasetNames = datasetNames;
            }
        }
    }
}
