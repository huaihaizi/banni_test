package com.hanwin.product.viewutils;

import com.hanwin.product.common.model.BaseRespMsg;

import java.util.List;

/**
 * Created by zhaopf on 2018/6/24 0024.
 */

public class ProvinceCityMsgBean extends BaseRespMsg{
    private List<ProvinceBean> data;

    public List<ProvinceBean> getData() {
        return data;
    }

    public void setData(List<ProvinceBean> data) {
        this.data = data;
    }
}
