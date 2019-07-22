package com.hanwin.product.home.bean;

/**
 * Created by zhaopf on 2019/6/12.
 */

public class ActivitiesBean {
    private String imgUrl;
    private String floatUrl;
    private String imgUrlType;
    private String linkUrl;
    private String userName;
    private String title;
    private String startTime;
    private String endTime;

    public String getActiveStartTime() {
        return activeStartTime;
    }

    public void setActiveStartTime(String activeStartTime) {
        this.activeStartTime = activeStartTime;
    }

    public String getActiveEndTime() {
        return activeEndTime;
    }

    public void setActiveEndTime(String activeEndTime) {
        this.activeEndTime = activeEndTime;
    }

    private String activeStartTime;
    private String activeEndTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFloatUrl() {
        return floatUrl;
    }

    public void setFloatUrl(String floatUrl) {
        this.floatUrl = floatUrl;
    }

    public String getImgUrlType() {
        return imgUrlType;
    }

    public void setImgUrlType(String imgUrlType) {
        this.imgUrlType = imgUrlType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
