package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2018/8/28 0028.
 */

public class VersionMsgBean extends BaseRespMsg{
    private VersionBean data;

    public VersionBean getData() {
        return data;
    }

    public void setData(VersionBean data) {
        this.data = data;
    }
}
