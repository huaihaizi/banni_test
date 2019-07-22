package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2018/9/4 0004.
 */

public class CounselorDetailMsgBean extends BaseRespMsg{
    private CounselorDetailBean data;

    public CounselorDetailBean getData() {
        return data;
    }

    public void setData(CounselorDetailBean data) {
        this.data = data;
    }
}
