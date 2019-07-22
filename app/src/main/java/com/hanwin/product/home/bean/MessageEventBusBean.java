package com.hanwin.product.home.bean;

/**
 * Created by zhaopf on 2019/3/29.
 */

public class MessageEventBusBean {
    private String orderId;//订单id
    private String receiverId;//被呼叫的账号
    private String actionType;//1.receiveCall 接听  2.hangUp 挂断  3.timeout 超时未接听  4.busy  忙线  5.reject 拒绝 6.cancel 取消呼叫

    public MessageEventBusBean() {
    }

    public MessageEventBusBean(String actionType, String orderId, String receiverId) {
        this.orderId = orderId;
        this.receiverId = receiverId;
        this.actionType = actionType;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getActionType() {
        return actionType;
    }
}
