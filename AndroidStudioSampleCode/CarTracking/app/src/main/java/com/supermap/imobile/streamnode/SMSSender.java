package com.supermap.imobile.streamnode;

import java.util.List;

public class SMSSender extends StreamNode {

    public SMSSender() {
        this.className = "com.supermap.bdt.streaming.sender.SMSSender";
    }

    /**
     * nextNodes : []
     * formatter : {"className":"com.supermap.bdt.streaming.formatter.JsonFormatter","separator":",","isJsonArray":"false"}
     * user : webchinese用户名
     * apiKey : webchinese接口安全秘钥
     * phoneNumbers : ["18812345678","19912345678"]
     * sendLimit : 1000
     */

    private FormatterBean formatter;
    private String user;
    private String apiKey;
    private int sendLimit;
    private List<String> phoneNumbers;

    public FormatterBean getFormatter() {
        return formatter;
    }

    public void setFormatter(FormatterBean formatter) {
        this.formatter = formatter;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(int sendLimit) {
        this.sendLimit = sendLimit;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public static class FormatterBean {
        /**
         * className : com.supermap.bdt.streaming.formatter.JsonFormatter
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
