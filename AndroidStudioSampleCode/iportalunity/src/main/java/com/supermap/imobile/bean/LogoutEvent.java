package com.supermap.imobile.bean;

public class LogoutEvent {

    private String Mode = "";

    private boolean isSuccess = false;//请求成功

    public boolean isSuccess() {
        return isSuccess;
    }
    public String getMode() { return Mode; }

    public LogoutEvent(Builder builder) {
        this.isSuccess = builder.isSuccess;
        this.Mode = builder.Mode;

    }

    public static class Builder{

        private String Mode = "";

        private boolean isSuccess = false;
        private String error = null;

        public Builder() {
        }

        public Builder isSucess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }
        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setMode(String Mode) {
            this.Mode = Mode;
            return this;
        }

        public LogoutEvent build() {
            return new LogoutEvent(this);
        }
    }
}
