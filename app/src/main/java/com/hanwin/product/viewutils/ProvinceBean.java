package com.hanwin.product.viewutils;

import com.hanwin.product.common.model.BaseBean;

import java.util.List;

/**
 * 省bean
 * Created by zhaopf on 2018/6/24 0024.
 */

public class ProvinceBean extends BaseBean {
    private String parentId;
    private String name;
    private List<CityBean> children;

    public List<CityBean> getChildren() {
        return children;
    }

    public void setChildren(List<CityBean> children) {
        this.children = children;
    }

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
}
