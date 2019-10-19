package com.supermap.imobile.bean;

import java.util.List;

public class RestMapsBean {

    /**
     * resourceConfigID : map
     * supportedMediaTypes : ["application/xml","text/xml","application/json","application/fastjson","application/rjson","text/html","application/jsonp","application/x-java-serialized-object","application/ajax","application/kml","application/ifx","application/flex","application/flash","application/flash3d","application/ijs","application/javascript","application/html5","application/ol3","application/vt","application/vectortile","application/isl","application/silverlight","application/smc","application/supermapcloud","application/tdt","application/tianditu","application/ilt","application/leaflet","application/mbgl","application/webgl3d"]
     * path : http://192.168.169.121/iserver/services/map_population-and-economy2/rest/maps/2014%E5%B9%B4%E4%BA%BA%E5%8F%A3%E5%AF%86%E5%BA%A6%E4%B8%93%E9%A2%98%E5%9B%BE
     * name : 2014年人口密度专题图
     * resourceType : StaticResource
     */

    private String resourceConfigID;
    private String path;
    private String name;
    private String resourceType;
    private List<String> supportedMediaTypes;

    public String getResourceConfigID() {
        return resourceConfigID;
    }

    public void setResourceConfigID(String resourceConfigID) {
        this.resourceConfigID = resourceConfigID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<String> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    public void setSupportedMediaTypes(List<String> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }
}
