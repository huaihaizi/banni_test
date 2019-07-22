package com.hanwin.product.home.bean;

import java.util.List;

/**
 * Created by zhaopf on 2018/10/17 0017.
 */

public class ChatRecordBean {
    private String userName;
    private String name;
    private String gender;
    private String avatar;
    private String isHide;
    private String nickName;
    private boolean isSeeMore;//f
    private List<ChatRecordTimeBean> createLenTime;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ChatRecordTimeBean> getCreateLenTime() {
        return createLenTime;
    }

    public void setCreateLenTime(List<ChatRecordTimeBean> createLenTime) {
        this.createLenTime = createLenTime;
    }

    public boolean isSeeMore() {
        return isSeeMore;
    }

    public void setSeeMore(boolean seeMore) {
        isSeeMore = seeMore;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
