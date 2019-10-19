package com.supermap.imobile.bean;

import java.util.List;

public class MyAccountBean {

    /**
     * ownRoles : ["PORTAL_USER","DATA_CENTER"]
     * joinTime : null
     * departmentId : 25
     * roles : ["PORTAL_USER","DATA_CENTER"]
     * description : null
     * userGroups : []
     * password : null
     * extendAttrs : null
     * isLocked : false
     * nickname : supermap_imobile
     * name : imobile
     * passwordQuestion : {"pwdAnswer":null,"pwdQuestion":"school"}
     * id : null
     * passwordLastModified : {"date":2,"hours":9,"seconds":14,"month":3,"nanos":0,"year":119,"minutes":55,"time":1554170114000}
     * email :
     * departmentNames : ["端产品研发移动产品部","端产品研发中心","研究院"]
     */

    private Object joinTime;
    private int departmentId;
    private Object description;
    private Object password;
    private Object extendAttrs;
    private boolean isLocked;
    private String nickname;
    private String name;
    private PasswordQuestionBean passwordQuestion;
    private Object id;
    private PasswordLastModifiedBean passwordLastModified;
    private String email;
    private List<String> ownRoles;
    private List<String> roles;
    private List<?> userGroups;
    private List<String> departmentNames;

    public Object getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Object joinTime) {
        this.joinTime = joinTime;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public Object getExtendAttrs() {
        return extendAttrs;
    }

    public void setExtendAttrs(Object extendAttrs) {
        this.extendAttrs = extendAttrs;
    }

    public boolean isIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PasswordQuestionBean getPasswordQuestion() {
        return passwordQuestion;
    }

    public void setPasswordQuestion(PasswordQuestionBean passwordQuestion) {
        this.passwordQuestion = passwordQuestion;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public PasswordLastModifiedBean getPasswordLastModified() {
        return passwordLastModified;
    }

    public void setPasswordLastModified(PasswordLastModifiedBean passwordLastModified) {
        this.passwordLastModified = passwordLastModified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getOwnRoles() {
        return ownRoles;
    }

    public void setOwnRoles(List<String> ownRoles) {
        this.ownRoles = ownRoles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<?> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<?> userGroups) {
        this.userGroups = userGroups;
    }

    public List<String> getDepartmentNames() {
        return departmentNames;
    }

    public void setDepartmentNames(List<String> departmentNames) {
        this.departmentNames = departmentNames;
    }

    public static class PasswordQuestionBean {
        /**
         * pwdAnswer : null
         * pwdQuestion : school
         */

        private Object pwdAnswer;
        private String pwdQuestion;

        public Object getPwdAnswer() {
            return pwdAnswer;
        }

        public void setPwdAnswer(Object pwdAnswer) {
            this.pwdAnswer = pwdAnswer;
        }

        public String getPwdQuestion() {
            return pwdQuestion;
        }

        public void setPwdQuestion(String pwdQuestion) {
            this.pwdQuestion = pwdQuestion;
        }
    }

    public static class PasswordLastModifiedBean {
        /**
         * date : 2
         * hours : 9
         * seconds : 14
         * month : 3
         * nanos : 0
         * year : 119
         * minutes : 55
         * time : 1554170114000
         */

        private int date;
        private int hours;
        private int seconds;
        private int month;
        private int nanos;
        private int year;
        private int minutes;
        private long time;

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getNanos() {
            return nanos;
        }

        public void setNanos(int nanos) {
            this.nanos = nanos;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
