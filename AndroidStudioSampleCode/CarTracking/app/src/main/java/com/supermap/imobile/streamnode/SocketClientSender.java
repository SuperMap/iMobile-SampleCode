package com.supermap.imobile.streamnode;

public class SocketClientSender extends StreamNode {

    public SocketClientSender() {
        this.className = "com.supermap.bdt.streaming.sender.SocketClientSender";
    }

    /**
     * nextNodes : []
     * formatter : {"className":"com.supermap.bdt.streaming.formatter.CSVFormatter","separator":",","isJsonArray":"false"}
     * ip : Socket服务IP
     * port : 5050
     */

    private FormatterBean formatter;
    private String ip;
    private int port;

    public FormatterBean getFormatter() {
        return formatter;
    }

    public void setFormatter(FormatterBean formatter) {
        this.formatter = formatter;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static class FormatterBean {
        /**
         * className : com.supermap.bdt.streaming.formatter.CSVFormatter
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
