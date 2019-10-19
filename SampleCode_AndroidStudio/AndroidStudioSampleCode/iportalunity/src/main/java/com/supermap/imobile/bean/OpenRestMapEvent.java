package com.supermap.imobile.bean;

/**
 * 数据下载消息模型
 */
public class OpenRestMapEvent {

    private String URL = "";

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    private String Mode = "";


    public String getUrl() {
        return URL;
    }

    public void setUrl(String url) {
        this.URL = url;
    }

    public OpenRestMapEvent(Builder builder) {
        this.URL = builder.URL;
        this.Mode = builder.Mode;
    }

    public static class Builder {

        private String URL = "";
        private String Mode = "";

        public Builder() {
        }

        public Builder setUrl(String url) {
            this.URL = url;
            return this;
        }

        public Builder setMode(String mode) {
            this.Mode = mode;
            return this;
        }

        public OpenRestMapEvent build() {
            return new OpenRestMapEvent(this);
        }
    }
}
