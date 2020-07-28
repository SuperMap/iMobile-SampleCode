package com.supermap;

import java.util.List;

public class GroupListBean {



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


        private String orderType;
        private Object keywords;
        private String orderBy;
        private int pageSize;
        private Object filterFields;
        private boolean returnCreate;
        private Object tags;
        private Object currentUser;
        private boolean returnJoined;
        private boolean isEnabled;
        private Object userNames;
        private Object isPublic;
        private int currentPage;
        private boolean returnCanJoin;
        private List<String> joinTypes;

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

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public Object getFilterFields() {
            return filterFields;
        }

        public void setFilterFields(Object filterFields) {
            this.filterFields = filterFields;
        }

        public boolean isReturnCreate() {
            return returnCreate;
        }

        public void setReturnCreate(boolean returnCreate) {
            this.returnCreate = returnCreate;
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

        public boolean isReturnJoined() {
            return returnJoined;
        }

        public void setReturnJoined(boolean returnJoined) {
            this.returnJoined = returnJoined;
        }

        public boolean isIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        public Object getUserNames() {
            return userNames;
        }

        public void setUserNames(Object userNames) {
            this.userNames = userNames;
        }

        public Object getIsPublic() {
            return isPublic;
        }

        public void setIsPublic(Object isPublic) {
            this.isPublic = isPublic;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public boolean isReturnCanJoin() {
            return returnCanJoin;
        }

        public void setReturnCanJoin(boolean returnCanJoin) {
            this.returnCanJoin = returnCanJoin;
        }

        public List<String> getJoinTypes() {
            return joinTypes;
        }

        public void setJoinTypes(List<String> joinTypes) {
            this.joinTypes = joinTypes;
        }
    }

    public static class ContentBean {


        private String creator;
        private String icon;
        private String resourceSharer;
        private String description;
        private long updateTime;
        private String groupName;
        private long createTime;
        private boolean isEnabled;
        private String nickname;
        private boolean isPublic;
        private int id;
        private boolean isNeedCheck;
        private List<String> tags;

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getResourceSharer() {
            return resourceSharer;
        }

        public void setResourceSharer(String resourceSharer) {
            this.resourceSharer = resourceSharer;
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

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public boolean isIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public boolean isIsPublic() {
            return isPublic;
        }

        public void setIsPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isIsNeedCheck() {
            return isNeedCheck;
        }

        public void setIsNeedCheck(boolean isNeedCheck) {
            this.isNeedCheck = isNeedCheck;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}
