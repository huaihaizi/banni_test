package com.hanwin.product.tencentim.event;

import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.message.TIMMessageRevokedListener;

import java.util.List;
import java.util.Observable;

/**
 * 消息通知事件，上层界面可以订阅此事件
 */
public class MessageEvent extends Observable implements TIMMessageListener, TIMMessageRevokedListener {


    private volatile static MessageEvent instancew;

    private MessageEvent(){

        //注册消息监听器
        TIMManager.getInstance().addMessageListener(this);
    }

    public static MessageEvent getInstance(){
        if (instancew == null) {
            synchronized (MessageEvent.class) {
                if (instancew == null) {
                    instancew = new MessageEvent();
                }
            }
        }
        return instancew;
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
//        if(list != null && list.size() > 0){
//            setChanged();
//            notifyObservers(list.get(list.size() - 1));
//        }
        for (TIMMessage item:list){
            setChanged();
            notifyObservers(item);
        }
        return false;
    }

    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message){
        setChanged();
        notifyObservers(message);
    }

    /**
     * 清理消息监听
     */
    public void clear(){
        instancew = null;
    }


    @Override
    public void onMessageRevoked(TIMMessageLocator timMessageLocator) {
        setChanged();
        notifyObservers(timMessageLocator);
    }
}
