package com.hanwin.product.viewutils;

import com.hanwin.product.common.model.BaseBean;

import java.util.List;

/**
 * 城市bean
 * Created by zhaopf on 2018/6/27 0027.
 */

public class CityBean extends BaseBean{
    private String parentId;
    private String name;
    private List<CountyBean> children;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CountyBean> getChildren() {
        return children;
    }

    public void setChildren(List<CountyBean> children) {
        this.children = children;
    }
}
