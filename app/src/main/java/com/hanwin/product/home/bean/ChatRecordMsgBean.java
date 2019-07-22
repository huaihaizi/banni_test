package com.hanwin.product.home.bean;

import com.hanwin.product.common.model.BaseRespMsg;

/**
 * Created by zhaopf on 2018/10/24 0024.
 */

public class ChatRecordMsgBean extends BaseRespMsg{
    private ChatPageInfo data;

    public ChatPageInfo getData() {
        return data;
    }

    public void setData(ChatPageInfo data) {
        this.data = data;
    }
}
