package com.hanwin.product.viewutils;

import com.hanwin.product.common.model.BaseBean;

/**
 * 县区bean
 * Created by zhaopf on 2018/6/27 0027.
 */

public class CountyBean extends BaseBean{
    private String parentId;
    private String name;
    private String children;

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

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }
}
