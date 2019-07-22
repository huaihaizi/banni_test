package com.hanwin.product.tencentim.bean;


import com.tencent.imsdk.TIMMessage;

/**
 * 消息工厂
 */
public class MessageFactory {

    private MessageFactory() {}


    /**
     * 消息工厂方法
     */
    public static Message getMessage(TIMMessage message){
        switch (message.getElement(0).getType()){
            case Text:
            case Custom:
                return new CustomMessage(message);
            default:
                return null;
        }
    }



}
