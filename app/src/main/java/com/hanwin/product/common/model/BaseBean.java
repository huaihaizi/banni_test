package com.hanwin.product.common.model;

import java.io.Serializable;

/**
 * Created by zhaopf on 15/9/24.
 */
public class BaseBean implements Serializable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
