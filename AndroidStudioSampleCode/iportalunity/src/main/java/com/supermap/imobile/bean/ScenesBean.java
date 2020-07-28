package com.supermap.imobile.bean;

import java.util.List;

public class ScenesBean {

    /**
     * total : 3
     * totalPage : 1
     * pageSize : 9
     * searchParameter : {"permissionType":null,"orderType":"ASC","keywords":null,"returnSubDir":null,"shareToMe":null,"orderBy":null,"isNotInDir":false,"pageSize":9,"dirIds":null,"filterFields":null,"departmentIds":null,"createStart":null,"tags":null,"currentUser":null,"createEnd":null,"groupIds":null,"userNames":["imobile"],"currentPage":1,"resourceIds":null}
     * currentPage : 1
     * content : [{"thumbnail":"http://rdc.ispeco.com/services/../resources/thumbnail/scene/scene1338289994.png","authorizeSetting":[{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":[],"entityType":"USER","entityName":"imobile","entityId":null}],"description":"科技园浏览","updateTime":1555571908825,"userName":"imobile","title":null,"url":null,"content":null,"tags":null,"visitCount":1,"createTime":1555571908825,"name":"科技园","nickname":"supermap_imobile","layers":null,"id":1338289994},{"thumbnail":"http://rdc.ispeco.com/services/../resources/thumbnail/scene/scene1380008998.png","authorizeSetting":[{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":[],"entityType":"USER","entityName":"imobile","entityId":null}],"description":"夜晚","updateTime":1555311211229,"userName":"imobile","title":null,"url":null,"content":null,"tags":null,"visitCount":7,"createTime":1555311211229,"name":"Night地球","nickname":"supermap_imobile","layers":null,"id":1380008998},{"thumbnail":"http://rdc.ispeco.com/services/../resources/thumbnail/scene/scene1473592234.png","authorizeSetting":[{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":[],"entityType":"USER","entityName":"imobile","entityId":null}],"description":"CBD浏览","updateTime":1555570797780,"userName":"imobile","title":null,"url":null,"content":null,"tags":null,"visitCount":2,"createTime":1555570797780,"name":"CBD","nickname":"supermap_imobile","layers":null,"id":1473592234}]
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
         * permissionType : null
         * orderType : ASC
         * keywords : null
         * returnSubDir : null
         * shareToMe : null
         * orderBy : null
         * isNotInDir : false
         * pageSize : 9
         * dirIds : null
         * filterFields : null
         * departmentIds : null
         * createStart : null
         * tags : null
         * currentUser : null
         * createEnd : null
         * groupIds : null
         * userNames : ["imobile"]
         * currentPage : 1
         * resourceIds : null
         */

        private Object permissionType;
        private String orderType;
        private Object keywords;
        private Object returnSubDir;
        private Object shareToMe;
        private Object orderBy;
        private boolean isNotInDir;
        private int pageSize;
        private Object dirIds;
        private Object filterFields;
        private Object departmentIds;
        private Object createStart;
        private Object tags;
        private Object currentUser;
        private Object createEnd;
        private Object groupIds;
        private int currentPage;
        private Object resourceIds;
        private List<String> userNames;

        public Object getPermissionType() {
            return permissionType;
        }

        public void setPermissionType(Object permissionType) {
            this.permissionType = permissionType;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public Object getKeywords() {
            return keywords;
        }

        public void setKeywords(Object keywords) {
            this.keywords = keywords;
        }

        public Object getReturnSubDir() {
            return returnSubDir;
        }

        public void setReturnSubDir(Object returnSubDir) {
            this.returnSubDir = returnSubDir;
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

        public boolean isIsNotInDir() {
            return isNotInDir;
        }

        public void setIsNotInDir(boolean isNotInDir) {
            this.isNotInDir = isNotInDir;
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

        public Object getCreateStart() {
            return createStart;
        }

        public void setCreateStart(Object createStart) {
            this.createStart = createStart;
        }

        public Object getTags() {
            return tags;
        }

        public void setTags(Object tags) {
            this.tags = tags;
        }

        public Object getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(Object currentUser) {
            this.currentUser = currentUser;
        }

        public Object getCreateEnd() {
            return createEnd;
        }

        public void setCreateEnd(Object createEnd) {
            this.createEnd = createEnd;
        }

        public Object getGroupIds() {
            return groupIds;
        }

        public void setGroupIds(Object groupIds) {
            this.groupIds = groupIds;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public Object getResourceIds() {
            return resourceIds;
        }

        public void setResourceIds(Object resourceIds) {
            this.resourceIds = resourceIds;
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
         * thumbnail : http://rdc.ispeco.com/services/../resources/thumbnail/scene/scene1338289994.png
         * authorizeSetting : [{"permissionType":"DELETE","aliasName":"supermap_imobile","entityRoles":[],"entityType":"USER","entityName":"imobile","entityId":null}]
         * description : 科技园浏览
         * updateTime : 1555571908825
         * userName : imobile
         * title : null
         * url : null
         * content : null
         * tags : null
         * visitCount : 1
         * createTime : 1555571908825
         * name : 科技园
         * nickname : supermap_imobile
         * layers : null
         * id : 1338289994
         */

        private String thumbnail;
        private String description;
        private long updateTime;
        private String userName;
        private Object title;
        private Object url;
        private Object content;
        private Object tags;
        private int visitCount;
        private long createTime;
        private String name;
        private String nickname;
        private Object layers;
        private int id;
        private List<AuthorizeSettingBean> authorizeSetting;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public Object getTitle() {
            return title;
        }

        public void setTitle(Object title) {
            this.title = title;
        }

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }

        public Object getTags() {
            return tags;
        }

        public void setTags(Object tags) {
            this.tags = tags;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public void setVisitCount(int visitCount) {
            this.visitCount = visitCount;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Object getLayers() {
            return layers;
        }

        public void setLayers(Object layers) {
            this.layers = layers;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<AuthorizeSettingBean> getAuthorizeSetting() {
            return authorizeSetting;
        }

        public void setAuthorizeSetting(List<AuthorizeSettingBean> authorizeSetting) {
            this.authorizeSetting = authorizeSetting;
        }

        public static class AuthorizeSettingBean {
            /**
             * permissionType : DELETE
             * aliasName : supermap_imobile
             * entityRoles : []
             * entityType : USER
             * entityName : imobile
             * entityId : null
             */

            private String permissionType;
            private String aliasName;
            private String entityType;
            private String entityName;
            private Object entityId;
            private List<?> entityRoles;

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

            public List<?> getEntityRoles() {
                return entityRoles;
            }

            public void setEntityRoles(List<?> entityRoles) {
                this.entityRoles = entityRoles;
            }
        }
    }
}
