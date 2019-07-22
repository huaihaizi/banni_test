package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

import java.util.List;

/**
 * Created by admin on 2019/6/12.
 */

public class ActivitiesMsgBean extends BaseRespMsg{
    private List<ActivitiesBean> data;

    public List<ActivitiesBean> getData() {
        return data;
    }

    public void setData(List<ActivitiesBean> data) {
        this.data = data;
    }
}
