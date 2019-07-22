package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

import java.util.List;

/**
 * Created by zhaopf on 2018/8/22 0022.
 */

public class CounselorListMsgBean extends BaseRespMsg{
    private PageInfo data;

    public PageInfo getData() {
        return data;
    }

    public void setData(PageInfo data) {
        this.data = data;
    }
}
