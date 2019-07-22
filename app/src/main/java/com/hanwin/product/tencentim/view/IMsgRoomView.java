package com.hanwin.product.tencentim.view;

import com.tencent.imsdk.TIMMessage;

/**
 * Created by Administrator on 2018/4/15.
 */

public interface IMsgRoomView {
    //创建房间成功
    void onCreatRoomSuccess();

    //创建房间失败
    void onCreatRoomFailed(String module, int errCode, String errMsg);

    // 进入房间成功
    void onEnterRoomSuccess();

    // 进房间失败
    void onEnterRoomFailed(String module, int errCode, String errMsg);

    // 退出房间成功
    void onQuitRoomSuccess();

    // 退出房间失败
    void onQuitRoomFailed(String module, int errCode, String errMsg);

    // 房间断开
    void onRoomDisconnect(int errCode, String errMsg);

    //发送自定义消息成功
    void onSendCustomerMsgSuccess(TIMMessage message);

    //发送自定义消息失败
    void onSendCustomerMsgError(TIMMessage message);

    void onVedioRequestErr(int result, String errMsg);

    //根据不同情况展示或显示布局
    void showLayout(int type);
}
