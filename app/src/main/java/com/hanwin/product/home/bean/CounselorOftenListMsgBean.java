package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2018/8/22 0022.
 */

public class CounselorOftenListMsgBean extends BaseRespMsg{
    private OftenPageInfo data;

    public OftenPageInfo getData() {
        return data;
    }

    public void setData(OftenPageInfo data) {
        this.data = data;
    }
}
