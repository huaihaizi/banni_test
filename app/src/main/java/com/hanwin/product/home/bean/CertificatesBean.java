package com.hanwin.product.home.bean;


import com.hanwin.product.common.model.BaseBean;

/**
 * Created by zhaopf on 2018/8/31 0031.
 */

public class CertificatesBean extends BaseBean {
    private int picId;
    private int userId;
    private String picUrl;
    private int type;
    private String description;

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public int getPicId() {
        return picId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
