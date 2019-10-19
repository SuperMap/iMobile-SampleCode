package com.supermap.imobile.bean;

import com.supermap.iportalservices.IPortalService;

public class MapBaseBean {

    private String mapTitle;
    private String mapType; //地图的来源类型
    private String thumbnail; //地图的缩略图路径。
    private int count; //访问次数
    private long updateTime;
    private long createTime;
    private String description;
    int ID = -1;//地图的ID

    public MapBaseBean(Builder builder) {
        this.ID = builder.ID;
        this.mapTitle = builder.mapTitle;
        this.mapType = builder.mapType;
        this.thumbnail = builder.thumbnail;
        this.count = builder.count;
        this.updateTime = builder.updateTime;
        this.createTime = builder.createTime;
        this.description = builder.description;
    }

    public int getID() {
        return ID;
    }

    public String getMapTitle() {
        return mapTitle;
    }

    public String getMapType() {
        return mapType;
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

    public String getURL() {
        String URL = "http://" + IPortalService.getInstance().getIPortalServiceHost() +  "/apps/viewer/" + ID;
        return URL;
    }

    public static class Builder {
        private String mapTitle;
        private String mapType; //地图的来源类型
        private String thumbnail; //地图的缩略图路径。
        private int count; //访问次数
        private long updateTime;
        private long createTime;
        private String description;
        private int ID = -1;//地图的ID

        public Builder(String mapTitle, int ID, String mapType, String thumbnail) {
            this.mapTitle = mapTitle;
            this.ID = ID;
            this.mapType = mapType;
            this.thumbnail = thumbnail;
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
        public MapBaseBean build() {
            return new MapBaseBean(this);
        }
    }

}
