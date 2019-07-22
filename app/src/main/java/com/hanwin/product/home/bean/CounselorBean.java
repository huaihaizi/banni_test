package com.hanwin.product.home.bean;


import com.hanwin.product.common.model.BaseBean;

import java.util.List;

/**
 * Created by zhaopf on 2018/8/22 0022.
 */

public class CounselorBean extends BaseBean{
    private String userId;
    private String userName;
    private String name;
    private String gender;
    private String workingYears;
    private String avatar;
    private String onlineStatus;
    private String keywordsNum;
    private String address;
    private String goodAtDirection;
    private String isStar;
    private String certificationRelSign;
    private String respCode;
    private String successCase;
    private List<WorkExperienceBean> experience;
    private List<EducationBackBean> education;
    private String picUrl;
    private List<String> livePhoto;
    private String multiAverage;
    private String transDirect;
    private String realAthenNameSign;
    private String isHide;
    private String uid;
    private TransFieldBean transField;
    private SignDialectBean signDialect;
    //活动的数据
    private String img_url;
    private String user_name;
    private String img_url_type;
    private String title;
    private String link_url;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImg_url_type() {
        return img_url_type;
    }

    public void setImg_url_type(String img_url_type) {
        this.img_url_type = img_url_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public TransFieldBean getTransField() {
        return transField;
    }

    public void setTransField(TransFieldBean transField) {
        this.transField = transField;
    }

    public SignDialectBean getSignDialect() {
        return signDialect;
    }

    public void setSignDialect(SignDialectBean signDialect) {
        this.signDialect = signDialect;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }

    public String getRealAthenNameSign() {
        return realAthenNameSign;
    }

    public void setRealAthenNameSign(String realAthenNameSign) {
        this.realAthenNameSign = realAthenNameSign;
    }

    public List<WorkExperienceBean> getExperience() {
        return experience;
    }

    public void setExperience(List<WorkExperienceBean> experience) {
        this.experience = experience;
    }

    public String getMultiAverage() {
        return multiAverage;
    }

    public void setMultiAverage(String multiAverage) {
        this.multiAverage = multiAverage;
    }

    public String getTransDirect() {
        return transDirect;
    }

    public void setTransDirect(String transDirect) {
        this.transDirect = transDirect;
    }

    public List<WorkExperienceBean> getWorkExperience() {
        return experience;
    }

    public void setWorkExperience(List<WorkExperienceBean> experience) {
        this.experience = experience;
    }

    public List<EducationBackBean> getEducation() {
        return education;
    }

    public void setEducation(List<EducationBackBean> education) {
        this.education = education;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<String> getLivePhoto() {
        return livePhoto;
    }

    public void setLivePhoto(List<String> livePhoto) {
        this.livePhoto = livePhoto;
    }

    public String getSuccessCase() {
        return successCase;
    }

    public void setSuccessCase(String successCase) {
        this.successCase = successCase;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWorkingYears() {
        return workingYears;
    }

    public void setWorkingYears(String workingYears) {
        this.workingYears = workingYears;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getKeywordsNum() {
        return keywordsNum;
    }

    public void setKeywordsNum(String keywordsNum) {
        this.keywordsNum = keywordsNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGoodAtDirection() {
        return goodAtDirection;
    }

    public void setGoodAtDirection(String goodAtDirection) {
        this.goodAtDirection = goodAtDirection;
    }

    public String getIsStar() {
        return isStar;
    }

    public void setIsStar(String isStar) {
        this.isStar = isStar;
    }

    public String getCertificationRelSign() {
        return certificationRelSign;
    }

    public void setCertificationRelSign(String certificationRelSign) {
        this.certificationRelSign = certificationRelSign;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }
}
