package com.hanwin.product.home.bean;

import com.hanwin.product.User;
import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2018/8/21 0021.
 */

public class LoginMsgBean extends BaseRespMsg{
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
