package com.supermap.imobile.bean;

/**
 * 数据下载消息模型
 */
public class DownloadDataEvent {

    private String Mode = "";

    private boolean isStart = false;//开始下载
    private boolean isDownLoading = false;//正在下载
    private boolean isSuccess = false;//下载成功
    private String error = null;//发生异常
    private int progress = 0;//下载进度
    private String filePath = null;//本地文件路径
    private int ID = -1;//数据唯一ID

    //    private boolean isPause = false;
    //    private boolean isCancel = false;

    public String getFilePath() {
        return filePath;
    }

    public int getProgress() {
        return progress;
    }

    public int getID() {
        return ID;
    }

    public boolean isDownLoading() {
        return isDownLoading;
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

    public DownloadDataEvent(Builder builder) {
        this.isStart = builder.isStart;
        this.isDownLoading = builder.isDownLoading;
        this.isSuccess = builder.isSuccess;
        this.error = builder.error;
        this.progress = builder.progress;
        this.filePath = builder.filePath;
        this.ID = builder.ID;
        this.Mode = builder.Mode;
    }

    public static class Builder {

        private String Mode = "";

        private boolean isStart = false;
        private boolean isDownLoading = false;
        private boolean isSuccess = false;
        private String error = null;
        private String filePath = null;
        private int progress = 0;
        private int ID = -1;

        public Builder() {
        }

        public Builder isSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public Builder isDownloading(boolean isDownloading) {
            this.isDownLoading = isDownloading;
            return this;
        }

        public Builder isStart(boolean start) {
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

        public Builder setProgress(int progress) {
            this.progress = progress;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setMode(String Mode) {
            this.Mode = Mode;
            return this;
        }

        public DownloadDataEvent build() {
            return new DownloadDataEvent(this);
        }
    }
}
