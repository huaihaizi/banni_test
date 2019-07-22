package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2019/6/11.
 */

public class FloatImageMsgBean extends BaseRespMsg{
    private FloatImageBean data;

    public FloatImageBean getData() {
        return data;
    }

    public void setData(FloatImageBean data) {
        this.data = data;
    }
}
