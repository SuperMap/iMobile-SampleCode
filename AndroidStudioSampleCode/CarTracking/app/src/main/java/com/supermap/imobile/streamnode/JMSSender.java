package com.supermap.imobile.streamnode;

public class JMSSender extends StreamNode {

    public JMSSender() {
        this.className = "com.supermap.bdt.streaming.sender.JMSSender";
    }

    /**
     * nextNodes : []
     * formatter : {"className":"com.supermap.bdt.streaming.formatter.GeoJsonFormatter","separator":",","isJsonArray":"false"}
     * url : JMS消息服务地址
     * port : 5666
     * queueName : 消息队列名称
     * jdniName : JDNI名称
     * userName : 用户名
     * password : 密码
     */

    private FormatterBean formatter;
    private String url;
    private int port;
    private String queueName;
    private String jdniName;
    private String userName;
    private String password;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getJdniName() {
        return jdniName;
    }

    public void setJdniName(String jdniName) {
        this.jdniName = jdniName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
