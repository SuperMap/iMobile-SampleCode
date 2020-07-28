package com.supermap.imobile.bean;

/**
 * 联网请求数据模型
 */
public class GetDataEvent {

    private String Mode = "";

    private boolean isStart = false;//开始请求
    private boolean isSuccess = false;//请求成功
    private String error = null;//发生异常
    private int ID = -1;

    public int getID() {
        return ID;
    }
    public boolean isStart() {
        return isStart;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
    public String getError() {
        return error;
    }
    public String getMode() { return Mode; }

    public GetDataEvent(Builder builder) {
        this.isStart = builder.isStart;
        this.isSuccess = builder.isSuccess;
        this.error = builder.error;
        this.Mode = builder.Mode;

    }

    public static class Builder{

        private String Mode = "";

        private boolean isStart = false;
        private boolean isSuccess = false;
        private String error = null;
        private int ID = -1;

        public Builder() {
        }

        public Builder isSucess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }
        public Builder setStart(boolean start) {
            this.isStart = start;
            return this;
        }
        public Builder setError(String error) {
            this.error = error;
            return this;
        }
        public Builder setID(int ID) {
            this.ID = ID;
            return this;
        }

        public Builder setMode(String Mode) {
            this.Mode = Mode;
            return this;
        }

        public GetDataEvent build() {
            return new GetDataEvent(this);
        }
    }
}
