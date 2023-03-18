package com.supermap.ar.apparmap.bean;

public class BuildingInfo {
    //BUILDING_INFO_FIELD = PROVINCE,CITY,COUNTY,ADMINCODE,NAME,ADDRESS,BEIZHU,Height
    private Integer smID;
    private String province;
    private String city;
    private String county;
    private String adminCode;
    private String name;
    private String address;
    private String memo;
    private String height;
    private boolean isUse;

    public Integer getSmID() {
        return smID;
    }

    public void setSmID(Integer smID) {
        this.smID = smID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    @Override
    public String toString() {
        return "BuildingInfo{" +
                "smID=" + smID +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", adminCode='" + adminCode + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", memo='" + memo + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
