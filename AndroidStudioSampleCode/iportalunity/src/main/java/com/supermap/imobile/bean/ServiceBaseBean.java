package com.supermap.imobile.bean;

import com.supermap.iportalservices.IPortalService;

public class ServiceBaseBean {

    private String title;
    private String type;
    private String thumbnail; //缩略图路径。
    private int count; //访问次数
    private long updateTime;
    private long createTime;
    private String description;
    private boolean enable = false;//是否可用
    int ID = -1;

    public ServiceBaseBean(Builder builder) {
        this.ID = builder.ID;
        this.title = builder.title;
        this.type = builder.type;
        this.thumbnail = builder.thumbnail;
        this.count = builder.count;
        this.updateTime = builder.updateTime;
        this.createTime = builder.createTime;
        this.description = builder.description;
        this.enable = builder.enable;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getCount() {
        return count;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getURL() {
        return "http://" + IPortalService.getInstance().getIPortalServiceHost() + "/iserver/services/" + title + "/rest";
//        return "http://rdc.ispeco.com/iserver/services/"+ title + "/rest";
    }

    public static class Builder {
        private String title;
        private String type;
        private String thumbnail; //地图的缩略图路径。
        private int count; //访问次数
        private long updateTime;
        private long createTime;
        private String description;
        private int ID = -1;
        private boolean enable = false;

        public Builder(String title, int ID, String type, String thumbnail) {
            this.title = title;
            this.ID = ID;
            this.type = type;
            this.thumbnail = thumbnail;
        }

        public Builder isEnable(boolean enable){
            this.enable = enable;
            return this;
        }

        public Builder setCount(int count){
            this.count = count;
            return this;
        }

        public Builder setupdateTime(long updateTime){
            this.updateTime = updateTime;
            return this;
        }

        public Builder setcreateTime(long createTime){
            this.createTime = createTime;
            return this;
        }

        public Builder setdescription(String description){
            this.description = description;
            return this;
        }

        //构造器入口
        public ServiceBaseBean build() {
            return new ServiceBaseBean(this);
        }
    }

}
