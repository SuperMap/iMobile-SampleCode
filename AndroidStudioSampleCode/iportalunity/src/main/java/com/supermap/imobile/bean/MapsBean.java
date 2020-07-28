package com.supermap.imobile.bean;

import java.util.List;

/**
 *
 字段	            类型          	    说明
 content	        List<ViewerMap>  	页面内容。
 currentPage	    int	                当前第几页。
 pageSize	        int	                每页大小。
 searchParameter	SearchParameter  	当前页搜索参数。
 total	            int	                总记录数。
 totalPage	        int	                总页数。
 */
public class MapsBean {

    /**
     * total : 2
     * totalPage : 1
     * pageSize : 9
     * searchParameter : {"orderType":"ASC","updateEnd":-1,"keywords":null,"sourceTypes":null,"shareToMe":null,"orderBy":null,"pageSize":9,"dirIds":null,"filterFields":null,"departmentIds":null,"mapStatus":null,"checkStatus":null,"epsgCode":null,"createEnd":-1,"groupIds":null,"permitInstances":null,"resourceIds":null,"permissionType":null,"visitEnd":-1,"excludeIds":null,"returnSubDir":null,"isNotInDir":null,"suggest":null,"visitStart":-1,"createStart":-1,"tags":null,"updateStart":-1,"currentUser":null,"userNames":["imobile"],"currentPage":1}
     * currentPage : 1
     * content : [{"extent":{"top":2.003750834E7,"left":-2.003750834E7,"bottom":-2.003750834E7,"leftBottom":{"x":-2.003750834E7,"y":-2.003750834E7},"right":2.003750834E7,"rightTop":{"x":2.003750834E7,"y":2.003750834E7}},"controls":null,"extentString":"{\"top\":2.003750834E7,\"left\":-2.003750834E7,\"bottom\":-2.003750834E7,\"leftBottom\":{\"x\":-2.003750834E7,\"y\":-2.003750834E7},\"right\":2.003750834E7,\"rightTop\":{\"x\":2.003750834E7,\"y\":2.003750834E7}}","description":"","verifyReason":null,"units":null,"title":"OPSTMAP","resolution":0,"checkStatus":"SUCCESSFUL","visitCount":12,"centerString":"{\"x\":1.2961020197800126E7,\"y\":4908169.077572179}","epsgCode":3857,"nickname":"supermap_imobile","layers":[{"wmtsOption":null,"styleString":"null","title":"OpenStreetMap","type":null,"subLayersString":"null","WMTSOptionString":"null","features":null,"boundsString":"null","prjCoordSys":null,"id":null,"cartoCSS":null,"datasourceName":null,"prjCoordSysString":"null","identifier":null,"layerType":null,"featuresString":"null","WMTSOption":null,"themeSettings":"{\"extent\":[-20037508.34,-20037508.34,20037508.34,20037508.34],\"setThumbnail\":\"${iportal_static_root}/static/dataviz/static/imgs/map/osm.png\",\"maxZoom\":19,\"epsgCode\":\"EPSG:3857\",\"sourceType\":\"OSM\",\"name\":\"OpenStreetMap\",\"minZoom\":1,\"units\":\"m\",\"url\":\"http://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png\",\"zIndex\":0}","isVisible":true,"subLayers":null,"url":null,"zindex":null,"scalesString":"null","scales":null,"name":"dv_v5_rest","bounds":null,"mapId":null,"style":null,"markersString":"null","opacity":1,"markers":null}],"id":173022304,"searchSetting":null,"setThumbnail":"http://rdc.ispeco.com/services/../resources/thumbnail/map/map173022304.png","level":8,"center":{"x":1.2961020197800126E7,"y":4908169.077572179},"authorizeSetting":[{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":["PORTAL_USER","DATA_CENTER"],"entityType":"USER","entityName":"imobile","entityId":null}],"updateTime":1554780675350,"userName":"imobile","tags":null,"checkUser":null,"checkUserNick":null,"checkTime":null,"sourceType":"MAPVIEWER","setCreateTime":1554780675350,"controlsString":"","isDefaultBottomMap":false,"status":null},{"extent":{"top":7087311.00490398,"left":8009146.115071949,"bottom":382872.01868254057,"leftBottom":{"x":8009146.115071949,"y":382872.01868254057},"right":1.5037846241523674E7,"rightTop":{"x":1.5037846241523674E7,"y":7087311.00490398}},"controls":null,"extentString":"{\"top\":7087311.00490398,\"left\":8009146.115071949,\"bottom\":382872.01868254057,\"leftBottom\":{\"x\":8009146.115071949,\"y\":382872.01868254057},\"right\":1.5037846241523674E7,\"rightTop\":{\"x\":1.5037846241523674E7,\"y\":7087311.00490398}}","description":"全国人口密度空间分布","verifyReason":null,"units":null,"title":"Map-全国人口密度空间分布","resolution":0,"checkStatus":"SUCCESSFUL","visitCount":12,"centerString":"{\"x\":1.1664316093234548E7,\"y\":3172129.414416621}","epsgCode":3857,"nickname":"supermap_imobile","layers":[{"wmtsOption":null,"styleString":"null","title":"PopulationDistribution","type":null,"subLayersString":"null","WMTSOptionString":"null","features":null,"boundsString":"null","prjCoordSys":null,"id":null,"cartoCSS":null,"datasourceName":null,"prjCoordSysString":"null","identifier":null,"layerType":null,"featuresString":"null","WMTSOption":null,"themeSettings":"{\"layerType\":\"TILE\",\"sourceType\":\"SUPERMAP_REST\",\"epsgCode\":\"EPSG:3857\",\"name\":\"PopulationDistribution\",\"url\":\"${iportalProxyServiceRoot}:8080/iserver/services/map-Population/rest/maps/PopulationDistribution\",\"zIndex\":0}","isVisible":true,"subLayers":null,"url":"http://rdc.ispeco.com:8080/iserver/services/map-Population/rest/maps/PopulationDistribution","zindex":null,"scalesString":"null","scales":null,"name":"dv_v5_rest","bounds":null,"mapId":null,"style":null,"markersString":"null","opacity":1,"markers":null}],"id":519700682,"searchSetting":null,"setThumbnail":"http://rdc.ispeco.com/services/../services/../resources/thumbnail/map/map519700682.png","level":4,"center":{"x":1.1664316093234548E7,"y":3172129.414416621},"authorizeSetting":[{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":["PORTAL_USER","DATA_CENTER"],"entityType":"USER","entityName":"imobile","entityId":null}],"updateTime":1554725330484,"userName":"imobile","tags":null,"checkUser":null,"checkUserNick":null,"checkTime":null,"sourceType":"MAPVIEWER","setCreateTime":1554725294140,"controlsString":"","isDefaultBottomMap":false,"status":null}]
     */

    private int total;
    private int totalPage;
    private int pageSize;
    private SearchParameterBean searchParameter;
    private int currentPage;
    private List<ContentBean> content;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public SearchParameterBean getSearchParameter() {
        return searchParameter;
    }

    public void setSearchParameter(SearchParameterBean searchParameter) {
        this.searchParameter = searchParameter;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class SearchParameterBean {
        /**
         * orderType : ASC
         * updateEnd : -1
         * keywords : null
         * sourceTypes : null
         * shareToMe : null
         * orderBy : null
         * pageSize : 9
         * dirIds : null
         * filterFields : null
         * departmentIds : null
         * mapStatus : null
         * checkStatus : null
         * epsgCode : null
         * createEnd : -1
         * groupIds : null
         * permitInstances : null
         * resourceIds : null
         * permissionType : null
         * visitEnd : -1
         * excludeIds : null
         * returnSubDir : null
         * isNotInDir : null
         * suggest : null
         * visitStart : -1
         * createStart : -1
         * tags : null
         * updateStart : -1
         * currentUser : null
         * userNames : ["imobile"]
         * currentPage : 1
         */

        private String orderType;
        private int updateEnd;
        private Object keywords;
        private Object sourceTypes;
        private Object shareToMe;
        private Object orderBy;
        private int pageSize;
        private Object dirIds;
        private Object filterFields;
        private Object departmentIds;
        private Object mapStatus;
        private Object checkStatus;
        private Object epsgCode;
        private int createEnd;
        private Object groupIds;
        private Object permitInstances;
        private Object resourceIds;
        private Object permissionType;
        private int visitEnd;
        private Object excludeIds;
        private Object returnSubDir;
        private Object isNotInDir;
        private Object suggest;
        private int visitStart;
        private int createStart;
        private Object tags;
        private int updateStart;
        private Object currentUser;
        private int currentPage;
        private List<String> userNames;

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public int getUpdateEnd() {
            return updateEnd;
        }

        public void setUpdateEnd(int updateEnd) {
            this.updateEnd = updateEnd;
        }

        public Object getKeywords() {
            return keywords;
        }

        public void setKeywords(Object keywords) {
            this.keywords = keywords;
        }

        public Object getSourceTypes() {
            return sourceTypes;
        }

        public void setSourceTypes(Object sourceTypes) {
            this.sourceTypes = sourceTypes;
        }

        public Object getShareToMe() {
            return shareToMe;
        }

        public void setShareToMe(Object shareToMe) {
            this.shareToMe = shareToMe;
        }

        public Object getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(Object orderBy) {
            this.orderBy = orderBy;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public Object getDirIds() {
            return dirIds;
        }

        public void setDirIds(Object dirIds) {
            this.dirIds = dirIds;
        }

        public Object getFilterFields() {
            return filterFields;
        }

        public void setFilterFields(Object filterFields) {
            this.filterFields = filterFields;
        }

        public Object getDepartmentIds() {
            return departmentIds;
        }

        public void setDepartmentIds(Object departmentIds) {
            this.departmentIds = departmentIds;
        }

        public Object getMapStatus() {
            return mapStatus;
        }

        public void setMapStatus(Object mapStatus) {
            this.mapStatus = mapStatus;
        }

        public Object getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(Object checkStatus) {
            this.checkStatus = checkStatus;
        }

        public Object getEpsgCode() {
            return epsgCode;
        }

        public void setEpsgCode(Object epsgCode) {
            this.epsgCode = epsgCode;
        }

        public int getCreateEnd() {
            return createEnd;
        }

        public void setCreateEnd(int createEnd) {
            this.createEnd = createEnd;
        }

        public Object getGroupIds() {
            return groupIds;
        }

        public void setGroupIds(Object groupIds) {
            this.groupIds = groupIds;
        }

        public Object getPermitInstances() {
            return permitInstances;
        }

        public void setPermitInstances(Object permitInstances) {
            this.permitInstances = permitInstances;
        }

        public Object getResourceIds() {
            return resourceIds;
        }

        public void setResourceIds(Object resourceIds) {
            this.resourceIds = resourceIds;
        }

        public Object getPermissionType() {
            return permissionType;
        }

        public void setPermissionType(Object permissionType) {
            this.permissionType = permissionType;
        }

        public int getVisitEnd() {
            return visitEnd;
        }

        public void setVisitEnd(int visitEnd) {
            this.visitEnd = visitEnd;
        }

        public Object getExcludeIds() {
            return excludeIds;
        }

        public void setExcludeIds(Object excludeIds) {
            this.excludeIds = excludeIds;
        }

        public Object getReturnSubDir() {
            return returnSubDir;
        }

        public void setReturnSubDir(Object returnSubDir) {
            this.returnSubDir = returnSubDir;
        }

        public Object getIsNotInDir() {
            return isNotInDir;
        }

        public void setIsNotInDir(Object isNotInDir) {
            this.isNotInDir = isNotInDir;
        }

        public Object getSuggest() {
            return suggest;
        }

        public void setSuggest(Object suggest) {
            this.suggest = suggest;
        }

        public int getVisitStart() {
            return visitStart;
        }

        public void setVisitStart(int visitStart) {
            this.visitStart = visitStart;
        }

        public int getCreateStart() {
            return createStart;
        }

        public void setCreateStart(int createStart) {
            this.createStart = createStart;
        }

        public Object getTags() {
            return tags;
        }

        public void setTags(Object tags) {
            this.tags = tags;
        }

        public int getUpdateStart() {
            return updateStart;
        }

        public void setUpdateStart(int updateStart) {
            this.updateStart = updateStart;
        }

        public Object getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(Object currentUser) {
            this.currentUser = currentUser;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public List<String> getUserNames() {
            return userNames;
        }

        public void setUserNames(List<String> userNames) {
            this.userNames = userNames;
        }
    }

    public static class ContentBean {
        /**
         * extent : {"top":2.003750834E7,"left":-2.003750834E7,"bottom":-2.003750834E7,"leftBottom":{"x":-2.003750834E7,"y":-2.003750834E7},"right":2.003750834E7,"rightTop":{"x":2.003750834E7,"y":2.003750834E7}}
         * controls : null
         * extentString : {"top":2.003750834E7,"left":-2.003750834E7,"bottom":-2.003750834E7,"leftBottom":{"x":-2.003750834E7,"y":-2.003750834E7},"right":2.003750834E7,"rightTop":{"x":2.003750834E7,"y":2.003750834E7}}
         * description :
         * verifyReason : null
         * units : null
         * title : OPSTMAP
         * resolution : 0
         * checkStatus : SUCCESSFUL
         * visitCount : 12
         * centerString : {"x":1.2961020197800126E7,"y":4908169.077572179}
         * epsgCode : 3857
         * nickname : supermap_imobile
         * layers : [{"wmtsOption":null,"styleString":"null","title":"OpenStreetMap","type":null,"subLayersString":"null","WMTSOptionString":"null","features":null,"boundsString":"null","prjCoordSys":null,"id":null,"cartoCSS":null,"datasourceName":null,"prjCoordSysString":"null","identifier":null,"layerType":null,"featuresString":"null","WMTSOption":null,"themeSettings":"{\"extent\":[-20037508.34,-20037508.34,20037508.34,20037508.34],\"setThumbnail\":\"${iportal_static_root}/static/dataviz/static/imgs/map/osm.png\",\"maxZoom\":19,\"epsgCode\":\"EPSG:3857\",\"sourceType\":\"OSM\",\"name\":\"OpenStreetMap\",\"minZoom\":1,\"units\":\"m\",\"url\":\"http://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png\",\"zIndex\":0}","isVisible":true,"subLayers":null,"url":null,"zindex":null,"scalesString":"null","scales":null,"name":"dv_v5_rest","bounds":null,"mapId":null,"style":null,"markersString":"null","opacity":1,"markers":null}]
         * id : 173022304
         * searchSetting : null
         * setThumbnail : http://rdc.ispeco.com/services/../resources/thumbnail/map/map173022304.png
         * level : 8
         * center : {"x":1.2961020197800126E7,"y":4908169.077572179}
         * authorizeSetting : [{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":["PORTAL_USER","DATA_CENTER"],"entityType":"USER","entityName":"imobile","entityId":null}]
         * updateTime : 1554780675350
         * userName : imobile
         * tags : null
         * checkUser : null
         * checkUserNick : null
         * checkTime : null
         * sourceType : MAPVIEWER
         * setCreateTime : 1554780675350
         * controlsString :
         * isDefaultBottomMap : false
         * status : null
         */

        private ExtentBean extent;
        private Object controls;
        private String extentString;
        private String description;
        private Object verifyReason;
        private Object units;
        private String title;
        private int resolution;
        private String checkStatus;
        private int visitCount;
        private String centerString;
        private int epsgCode;
        private String nickname;
        private int id;
        private Object searchSetting;
        private String thumbnail;
        private int level;
        private CenterBean center;
        private long updateTime;
        private String userName;
        private Object tags;
        private Object checkUser;
        private Object checkUserNick;
        private Object checkTime;
        private String sourceType;
        private long createTime;
        private String controlsString;
        private boolean isDefaultBottomMap;
        private Object status;
        private List<LayersBean> layers;
        private List<AuthorizeSettingBean> authorizeSetting;

        public ExtentBean getExtent() {
            return extent;
        }

        public void setExtent(ExtentBean extent) {
            this.extent = extent;
        }

        public Object getControls() {
            return controls;
        }

        public void setControls(Object controls) {
            this.controls = controls;
        }

        public String getExtentString() {
            return extentString;
        }

        public void setExtentString(String extentString) {
            this.extentString = extentString;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getVerifyReason() {
            return verifyReason;
        }

        public void setVerifyReason(Object verifyReason) {
            this.verifyReason = verifyReason;
        }

        public Object getUnits() {
            return units;
        }

        public void setUnits(Object units) {
            this.units = units;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getResolution() {
            return resolution;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        public String getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(String checkStatus) {
            this.checkStatus = checkStatus;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public void setVisitCount(int visitCount) {
            this.visitCount = visitCount;
        }

        public String getCenterString() {
            return centerString;
        }

        public void setCenterString(String centerString) {
            this.centerString = centerString;
        }

        public int getEpsgCode() {
            return epsgCode;
        }

        public void setEpsgCode(int epsgCode) {
            this.epsgCode = epsgCode;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getSearchSetting() {
            return searchSetting;
        }

        public void setSearchSetting(Object searchSetting) {
            this.searchSetting = searchSetting;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public CenterBean getCenter() {
            return center;
        }

        public void setCenter(CenterBean center) {
            this.center = center;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Object getTags() {
            return tags;
        }

        public void setTags(Object tags) {
            this.tags = tags;
        }

        public Object getCheckUser() {
            return checkUser;
        }

        public void setCheckUser(Object checkUser) {
            this.checkUser = checkUser;
        }

        public Object getCheckUserNick() {
            return checkUserNick;
        }

        public void setCheckUserNick(Object checkUserNick) {
            this.checkUserNick = checkUserNick;
        }

        public Object getCheckTime() {
            return checkTime;
        }

        public void setCheckTime(Object checkTime) {
            this.checkTime = checkTime;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getControlsString() {
            return controlsString;
        }

        public void setControlsString(String controlsString) {
            this.controlsString = controlsString;
        }

        public boolean isIsDefaultBottomMap() {
            return isDefaultBottomMap;
        }

        public void setIsDefaultBottomMap(boolean isDefaultBottomMap) {
            this.isDefaultBottomMap = isDefaultBottomMap;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public List<LayersBean> getLayers() {
            return layers;
        }

        public void setLayers(List<LayersBean> layers) {
            this.layers = layers;
        }

        public List<AuthorizeSettingBean> getAuthorizeSetting() {
            return authorizeSetting;
        }

        public void setAuthorizeSetting(List<AuthorizeSettingBean> authorizeSetting) {
            this.authorizeSetting = authorizeSetting;
        }

        public static class ExtentBean {
            /**
             * top : 2.003750834E7
             * left : -2.003750834E7
             * bottom : -2.003750834E7
             * leftBottom : {"x":-2.003750834E7,"y":-2.003750834E7}
             * right : 2.003750834E7
             * rightTop : {"x":2.003750834E7,"y":2.003750834E7}
             */

            private double top;
            private double left;
            private double bottom;
            private LeftBottomBean leftBottom;
            private double right;
            private RightTopBean rightTop;

            public double getTop() {
                return top;
            }

            public void setTop(double top) {
                this.top = top;
            }

            public double getLeft() {
                return left;
            }

            public void setLeft(double left) {
                this.left = left;
            }

            public double getBottom() {
                return bottom;
            }

            public void setBottom(double bottom) {
                this.bottom = bottom;
            }

            public LeftBottomBean getLeftBottom() {
                return leftBottom;
            }

            public void setLeftBottom(LeftBottomBean leftBottom) {
                this.leftBottom = leftBottom;
            }

            public double getRight() {
                return right;
            }

            public void setRight(double right) {
                this.right = right;
            }

            public RightTopBean getRightTop() {
                return rightTop;
            }

            public void setRightTop(RightTopBean rightTop) {
                this.rightTop = rightTop;
            }

            public static class LeftBottomBean {
                /**
                 * x : -2.003750834E7
                 * y : -2.003750834E7
                 */

                private double x;
                private double y;

                public double getX() {
                    return x;
                }

                public void setX(double x) {
                    this.x = x;
                }

                public double getY() {
                    return y;
                }

                public void setY(double y) {
                    this.y = y;
                }
            }

            public static class RightTopBean {
                /**
                 * x : 2.003750834E7
                 * y : 2.003750834E7
                 */

                private double x;
                private double y;

                public double getX() {
                    return x;
                }

                public void setX(double x) {
                    this.x = x;
                }

                public double getY() {
                    return y;
                }

                public void setY(double y) {
                    this.y = y;
                }
            }
        }

        public static class CenterBean {
            /**
             * x : 1.2961020197800126E7
             * y : 4908169.077572179
             */

            private double x;
            private double y;

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }
        }

        public static class LayersBean {
            /**
             * wmtsOption : null
             * styleString : null
             * title : OpenStreetMap
             * type : null
             * subLayersString : null
             * WMTSOptionString : null
             * features : null
             * boundsString : null
             * prjCoordSys : null
             * id : null
             * cartoCSS : null
             * datasourceName : null
             * prjCoordSysString : null
             * identifier : null
             * layerType : null
             * featuresString : null
             * WMTSOption : null
             * themeSettings : {"extent":[-20037508.34,-20037508.34,20037508.34,20037508.34],"setThumbnail":"${iportal_static_root}/static/dataviz/static/imgs/map/osm.png","maxZoom":19,"epsgCode":"EPSG:3857","sourceType":"OSM","name":"OpenStreetMap","minZoom":1,"units":"m","url":"http://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png","zIndex":0}
             * isVisible : true
             * subLayers : null
             * url : null
             * zindex : null
             * scalesString : null
             * scales : null
             * name : dv_v5_rest
             * bounds : null
             * mapId : null
             * style : null
             * markersString : null
             * opacity : 1
             * markers : null
             */

            private Object wmtsOption;
            private String styleString;
            private String title;
            private Object type;
            private String subLayersString;
            private String WMTSOptionString;
            private Object features;
            private String boundsString;
            private Object prjCoordSys;
            private Object id;
            private Object cartoCSS;
            private Object datasourceName;
            private String prjCoordSysString;
            private Object identifier;
            private Object layerType;
            private String featuresString;
            private Object WMTSOption;
            private String themeSettings;
            private boolean isVisible;
            private Object subLayers;
            private Object url;
            private Object zindex;
            private String scalesString;
            private Object scales;
            private String name;
            private Object bounds;
            private Object mapId;
            private Object style;
            private String markersString;
            private int opacity;
            private Object markers;

            public Object getWmtsOption() {
                return wmtsOption;
            }

            public void setWmtsOption(Object wmtsOption) {
                this.wmtsOption = wmtsOption;
            }

            public String getStyleString() {
                return styleString;
            }

            public void setStyleString(String styleString) {
                this.styleString = styleString;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Object getType() {
                return type;
            }

            public void setType(Object type) {
                this.type = type;
            }

            public String getSubLayersString() {
                return subLayersString;
            }

            public void setSubLayersString(String subLayersString) {
                this.subLayersString = subLayersString;
            }

            public String getWMTSOptionString() {
                return WMTSOptionString;
            }

            public void setWMTSOptionString(String WMTSOptionString) {
                this.WMTSOptionString = WMTSOptionString;
            }

            public Object getFeatures() {
                return features;
            }

            public void setFeatures(Object features) {
                this.features = features;
            }

            public String getBoundsString() {
                return boundsString;
            }

            public void setBoundsString(String boundsString) {
                this.boundsString = boundsString;
            }

            public Object getPrjCoordSys() {
                return prjCoordSys;
            }

            public void setPrjCoordSys(Object prjCoordSys) {
                this.prjCoordSys = prjCoordSys;
            }

            public Object getId() {
                return id;
            }

            public void setId(Object id) {
                this.id = id;
            }

            public Object getCartoCSS() {
                return cartoCSS;
            }

            public void setCartoCSS(Object cartoCSS) {
                this.cartoCSS = cartoCSS;
            }

            public Object getDatasourceName() {
                return datasourceName;
            }

            public void setDatasourceName(Object datasourceName) {
                this.datasourceName = datasourceName;
            }

            public String getPrjCoordSysString() {
                return prjCoordSysString;
            }

            public void setPrjCoordSysString(String prjCoordSysString) {
                this.prjCoordSysString = prjCoordSysString;
            }

            public Object getIdentifier() {
                return identifier;
            }

            public void setIdentifier(Object identifier) {
                this.identifier = identifier;
            }

            public Object getLayerType() {
                return layerType;
            }

            public void setLayerType(Object layerType) {
                this.layerType = layerType;
            }

            public String getFeaturesString() {
                return featuresString;
            }

            public void setFeaturesString(String featuresString) {
                this.featuresString = featuresString;
            }

            public Object getWMTSOption() {
                return WMTSOption;
            }

            public void setWMTSOption(Object WMTSOption) {
                this.WMTSOption = WMTSOption;
            }

            public String getThemeSettings() {
                return themeSettings;
            }

            public void setThemeSettings(String themeSettings) {
                this.themeSettings = themeSettings;
            }

            public boolean isIsVisible() {
                return isVisible;
            }

            public void setIsVisible(boolean isVisible) {
                this.isVisible = isVisible;
            }

            public Object getSubLayers() {
                return subLayers;
            }

            public void setSubLayers(Object subLayers) {
                this.subLayers = subLayers;
            }

            public Object getUrl() {
                return url;
            }

            public void setUrl(Object url) {
                this.url = url;
            }

            public Object getZindex() {
                return zindex;
            }

            public void setZindex(Object zindex) {
                this.zindex = zindex;
            }

            public String getScalesString() {
                return scalesString;
            }

            public void setScalesString(String scalesString) {
                this.scalesString = scalesString;
            }

            public Object getScales() {
                return scales;
            }

            public void setScales(Object scales) {
                this.scales = scales;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getBounds() {
                return bounds;
            }

            public void setBounds(Object bounds) {
                this.bounds = bounds;
            }

            public Object getMapId() {
                return mapId;
            }

            public void setMapId(Object mapId) {
                this.mapId = mapId;
            }

            public Object getStyle() {
                return style;
            }

            public void setStyle(Object style) {
                this.style = style;
            }

            public String getMarkersString() {
                return markersString;
            }

            public void setMarkersString(String markersString) {
                this.markersString = markersString;
            }

            public int getOpacity() {
                return opacity;
            }

            public void setOpacity(int opacity) {
                this.opacity = opacity;
            }

            public Object getMarkers() {
                return markers;
            }

            public void setMarkers(Object markers) {
                this.markers = markers;
            }
        }

        public static class AuthorizeSettingBean {
            /**
             * permissionType : DELETE
             * aliasName : supermap_imobile
             * entityRoles : ["PORTAL_USER","DATA_CENTER"]
             * entityType : USER
             * entityName : imobile
             * entityId : null
             */

            private String permissionType;
            private String aliasName;
            private String entityType;
            private String entityName;
            private Object entityId;
            private List<String> entityRoles;

            public String getPermissionType() {
                return permissionType;
            }

            public void setPermissionType(String permissionType) {
                this.permissionType = permissionType;
            }

            public String getAliasName() {
                return aliasName;
            }

            public void setAliasName(String aliasName) {
                this.aliasName = aliasName;
            }

            public String getEntityType() {
                return entityType;
            }

            public void setEntityType(String entityType) {
                this.entityType = entityType;
            }

            public String getEntityName() {
                return entityName;
            }

            public void setEntityName(String entityName) {
                this.entityName = entityName;
            }

            public Object getEntityId() {
                return entityId;
            }

            public void setEntityId(Object entityId) {
                this.entityId = entityId;
            }

            public List<String> getEntityRoles() {
                return entityRoles;
            }

            public void setEntityRoles(List<String> entityRoles) {
                this.entityRoles = entityRoles;
            }
        }
    }
}
