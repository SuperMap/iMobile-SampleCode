package com.supermap.imobile.streamnode;

public class EsAppendSender extends StreamNode {

    public EsAppendSender() {
        this.className = "com.supermap.bdt.streaming.sender.EsAppendSender";
    }

    /**
     * nextNodes : []
     * formatter : {"className":"com.supermap.bdt.streaming.formatter.GeoJsonFormatter","separator":",","isJsonArray":"false"}
     * url : ES服务地址
     * queueName : 队列名
     * directoryPath : 目录名
     */

    private FormatterBean formatter;
    private String url;
    private String queueName;
    private String directoryPath;

    public FormatterBean getFormatter() {
        return formatter;
    }

    public void setFormatter(FormatterBean formatter) {
        this.formatter = formatter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public static class FormatterBean {
        /**
         * className : com.supermap.bdt.streaming.formatter.GeoJsonFormatter
         * separator : ,
         * isJsonArray : false
         */

        private String className;
        private String separator;
        private String isJsonArray;
        private String arrayExpression;

        public String getArrayExpression() {
            return arrayExpression;
        }

        public void setArrayExpression(String arrayExpression) {
            this.arrayExpression = arrayExpression;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public String getIsJsonArray() {
            return isJsonArray;
        }

        public void setIsJsonArray(String isJsonArray) {
            this.isJsonArray = isJsonArray;
        }
    }
}
