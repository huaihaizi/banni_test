package com.hanwin.product.home.bean;

import java.io.Serializable;

/**
 * Created by admin on 2019/1/15.
 */

public class TransFieldBean implements Serializable{
    private String id;
    private String userId;
    private String transDirect;
    private String elseDirectDesc;
    private String createTime;
    private String updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransDirect() {
        return transDirect;
    }

    public void setTransDirect(String transDirect) {
        this.transDirect = transDirect;
    }

    public String getElseDirectDesc() {
        return elseDirectDesc;
    }

    public void setElseDirectDesc(String elseDirectDesc) {
        this.elseDirectDesc = elseDirectDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
