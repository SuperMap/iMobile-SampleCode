package com.supermap.imobile.streamnode;

import java.util.List;

public class GeoFilter extends StreamNode {

    public GeoFilter() {
        this.className = "com.supermap.bdt.streaming.filter.GeoFilter";
    }

    /**
     * className : com.supermap.bdt.streaming.filter.GeoFilter
     * caption : 地理过滤器
     * name : GeoFilter
     * nextNodes : []
     * prevNodes : []
     * description : 节点描述
     * connection : {"type":"数据源类型","info":[{"server":"数据源文件路径01","datasetNames":["数据集名称01","数据集名称02"]},{"server":"数据源文件路径02","datasetNames":["数据集名称01","数据集名称02"]}]}
     * mode : inside
     */

    private ConnectionBean connection;
    private String mode;

    public ConnectionBean getConnection() {
        return connection;
    }

    public void setConnection(ConnectionBean connection) {
        this.connection = connection;
    }

    public String getMode() {
        return mode;
    }

    /**
     * 空间关系
     * @param mode inside outside
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    public static class ConnectionBean {
        /**
         * type : 数据源类型
         * info : [{"server":"数据源文件路径01","datasetNames":["数据集名称01","数据集名称02"]},{"server":"数据源文件路径02","datasetNames":["数据集名称01","数据集名称02"]}]
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
             * server : 数据源文件路径01
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