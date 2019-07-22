package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by admin on 2019/5/30.
 */

public class ServiceTimesMsgBean extends BaseRespMsg{
    private ServiceTimesBean data;

    public ServiceTimesBean getData() {
        return data;
    }

    public void setData(ServiceTimesBean data) {
        this.data = data;
    }
}
