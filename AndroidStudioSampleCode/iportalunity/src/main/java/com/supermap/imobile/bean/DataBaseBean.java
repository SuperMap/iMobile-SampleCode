package com.supermap.imobile.bean;

public class DataBaseBean {

    private String fileName;
    private String type;
    private int size;
    private long lastModfiedTime;

    private String thumbnail;
    private int downloadCount;
    private String serviceStatus;
    private long createTime;
    private int ID;
    private String MD5;
    private String owner;//所有者
    private String tags;//标签

    public String getFileName() {
        return fileName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModfiedTime() {
        return lastModfiedTime;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public int getID() {
        return ID;
    }

    public String getMD5() {
        return MD5;
    }

    public String getOwner() {
        return owner;
    }

    public String getTags() {
        return tags;
    }

    public DataBaseBean(Builder builder) {
        this.fileName = builder.fileName;
        this.lastModfiedTime = builder.lastModfiedTime;
        this.thumbnail = builder.thumbnail;
        this.type = builder.type;
        this.size = builder.size;
        this.createTime = builder.createTime;
        this.downloadCount = builder.downloadCount;
        this.serviceStatus = builder.serviceStatus;
        this.ID = builder.ID;
        this.MD5 = builder.MD5;
        this.owner = builder.owner;
        this.tags = builder.tags;
    }

    public static class Builder {
        private String fileName;
        private String type;
        private int size;
        private long lastModfiedTime;

        private long createTime;
        private String thumbnail;
        private int downloadCount;
        private String serviceStatus;

        private int ID;
        private String MD5;
        private String owner;
        private String tags;

        public Builder(String fileName, String type, int size, long lastModfiedTime) {
            this.fileName = fileName;
            this.type = type;
            this.size = size;
            this.lastModfiedTime = lastModfiedTime;
        }

        public Builder setCreateTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder setDownloadCount(int downloadCount) {
            this.downloadCount = downloadCount;
            return this;
        }

        public Builder setServiceStatus(String serviceStatus) {
            this.serviceStatus = serviceStatus;
            return this;
        }

        public Builder setID(int ID) {
            this.ID = ID;
            return this;
        }

        public Builder setMD5(String MD5) {
            this.MD5 = MD5;
            return this;
        }

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder setTags(String tags) {
            this.tags = tags;
            return this;
        }

        //构造器入口
        public DataBaseBean build() {
            return new DataBaseBean(this);
        }

    }

}
