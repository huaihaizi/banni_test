package com.hanwin.product.tencentim.bean;

import java.io.Serializable;

/**
 * Created by zhaopf on 2018/8/10 0010.
 */

public class CustomeModel implements Serializable{
    private String callUserId;//对方的id
    private String callUserName;
    private String mySelfId;//主叫的id
    private String type;//0 邀请  1 对方接听  2 取消  3 拒绝  4 对方挂断
    private int roomId;//房间id
    private String mySelfdisplayName;//主叫的名字
    private String mySelfImageHead;//主叫的头像
    private String orderId;//订单id
    private String translationContent;//翻译内容
    private String startTime;//一句话的开始时间
    private String endTime;//一句话的结束时间

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTranslationContent() {
        return translationContent;
    }

    public void setTranslationContent(String translationContent) {
        this.translationContent = translationContent;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMySelfdisplayName() {
        return mySelfdisplayName;
    }

    public void setMySelfdisplayName(String mySelfdisplayName) {
        this.mySelfdisplayName = mySelfdisplayName;
    }

    public String getMySelfImageHead() {
        return mySelfImageHead;
    }

    public void setMySelfImageHead(String mySelfImageHead) {
        this.mySelfImageHead = mySelfImageHead;
    }

    public String getMySelfId() {
        return mySelfId;
    }

    public void setMySelfId(String mySelfId) {
        this.mySelfId = mySelfId;
    }

    public String getCallUserId() {
        return callUserId;
    }

    public void setCallUserId(String callUserId) {
        this.callUserId = callUserId;
    }

    public String getCallUserName() {
        return callUserName;
    }

    public void setCallUserName(String callUserName) {
        this.callUserName = callUserName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
