package com.supermap.imobile.bean;

/**
 * 数据上传消息模型
 */
public class UploadDataEvent {

    private boolean isStart = false;//开始上传
    private boolean isUpLoading = false;//正在上传中
    private boolean isSuccess = false;//上传成功
    private String error = null;//发生异常
    private int progress = 0;//上传进度
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

    public boolean isUpLoading() {
        return isUpLoading;
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

    public UploadDataEvent(Builder builder) {
        this.isStart = builder.isStart;
        this.isUpLoading = builder.isUploading;
        this.isSuccess = builder.isSuccess;
        this.error = builder.error;
        this.progress = builder.progress;
        this.filePath = builder.filePath;
        this.ID = builder.ID;
    }

    public static class Builder {

        private boolean isStart = false;
        private boolean isUploading = false;
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

        public Builder isUploading(boolean isDownloading) {
            this.isUploading = isDownloading;
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

        public UploadDataEvent build() {
            return new UploadDataEvent(this);
        }
    }
}
