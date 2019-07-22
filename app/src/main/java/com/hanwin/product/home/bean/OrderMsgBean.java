package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2018/10/23 0023.
 */

public class OrderMsgBean extends BaseRespMsg{
    private OrderBean data;

    public OrderBean getData() {
        return data;
    }

    public void setData(OrderBean data) {
        this.data = data;
    }
}
