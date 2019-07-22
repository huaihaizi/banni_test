package com.hanwin.product.home.bean;

import java.io.Serializable;

/**
 * Created by admin on 2019/1/15.
 */

public class SignDialectBean implements Serializable {
    private String id;
    private String userId;
    private String dialect;// 0：通用手语 1：方言手语
    private String dialectDesc;
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

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDialectDesc() {
        return dialectDesc;
    }

    public void setDialectDesc(String dialectDesc) {
        this.dialectDesc = dialectDesc;
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
