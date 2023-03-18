package com.supermap.imobile.streamnode;

public class WebSocketClientSender extends StreamNode {

    public WebSocketClientSender() {
        this.className = "com.supermap.bdt.streaming.sender.WebSocketClientSender";
    }

    /**
     * nextNodes : []
     * formatter : {"className":"com.supermap.bdt.streaming.formatter.GeoJsonFormatter","separator":",","isJsonArray":"false"}
     * path : ws-----------------
     */

    private FormatterBean formatter;
    private String path;

    public FormatterBean getFormatter() {
        return formatter;
    }

    public void setFormatter(FormatterBean formatter) {
        this.formatter = formatter;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
