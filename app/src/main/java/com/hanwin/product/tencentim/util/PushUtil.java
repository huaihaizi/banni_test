package com.hanwin.product.tencentim.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.tencentim.activity.AVChatActivity;
import com.hanwin.product.tencentim.bean.CustomMessage;
import com.hanwin.product.tencentim.bean.CustomeModel;
import com.hanwin.product.tencentim.bean.Message;
import com.hanwin.product.tencentim.bean.MessageFactory;
import com.hanwin.product.tencentim.event.MessageEvent;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.JSONUtil;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageOfflinePushSettings;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.Observable;
import java.util.Observer;

/**
 * 在线消息通知展示
 */
public class PushUtil implements Observer {

    private static final String TAG = PushUtil.class.getSimpleName();

    private static int pushNum = 0;

    private final int pushId = 1;
    private String ISSaveOrderId;//将视频过的订单号保存
    private static PushUtil instance = new PushUtil();
    private int count = 0;

    private PushUtil() {
        MessageEvent.getInstance().addObserver(this);
    }

    public static PushUtil getInstance() {
        return instance;
    }

    /**
     * 初始化
     */
    public void setInitialize(){
        count = 0;
    }

    private void PushNotify(TIMMessage msg) {
        if (msg == null || msg.isSelf()) {
            //自己发的消息不显示
            return;
        } else if (Foreground.get().isForeground() || MessageFactory.getMessage(msg) instanceof CustomMessage) {
            //处理视频请求的消息
            operationAVMessage(msg);
        } else {
            String senderStr, contentStr;
            Message message = MessageFactory.getMessage(msg);
            if (message == null) return;
            senderStr = message.getSender();
            NotificationManager mNotificationManager = (NotificationManager) BaseApplication.getInstance().getSystemService(BaseApplication.getInstance().NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BaseApplication.getInstance());
            Intent notificationIntent = new Intent(BaseApplication.getInstance(), MainActivity.class);
            notificationIntent.putExtra("identify", senderStr);
            notificationIntent.putExtra("type", msg.getConversation().getType());
            notificationIntent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(BaseApplication.getInstance(), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentTitle(senderStr)//设置通知栏标题
                    .setContentText(senderStr + "发来一条视频请求")
                    .setContentIntent(intent) //设置通知栏点击意图
//                .setNumber(++pushNum) //设置通知集合的数量
//                    .setTicker(senderStr + ":" + contentStr) //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
            Notification notify = mBuilder.build();
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(pushId, notify);
        }
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) BaseApplication.getInstance().getSystemService(BaseApplication.getInstance().NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;
                if (msg != null) {
                    PushNotify(msg);
                }
            }
        }
    }

    /**
     * 处理自定义 “视频请求”的消息
     *
     * @param msg
     */
    public void operationAVMessage(TIMMessage msg) {
        SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences("ORDERID", Context.MODE_PRIVATE);
        ISSaveOrderId = preferences.getString("ISSaveOrderId", "");
        TIMElem elem = msg.getElement(0);
        //获取当前元素的类型
        TIMElemType elemType = elem.getType();
        if (elemType == TIMElemType.Custom) {
            TIMCustomElem timCustomElem = (TIMCustomElem) elem;
            String a = new String(timCustomElem.getData());
            CustomeModel customeModel = JSONUtil.fromJson(a, new TypeToken<CustomeModel>() {
            }.getType());
            if (customeModel != null) {
                String type = customeModel.getType();
                String orderId = customeModel.getOrderId();
                String roomID = customeModel.getRoomId() + "";
                String displayName = customeModel.getMySelfdisplayName();
                String imageHead = customeModel.getMySelfImageHead();
                String translationContent = customeModel.getTranslationContent();
                String startTime = customeModel.getStartTime();
                String endTime = customeModel.getEndTime();
                //将进入视频界面的订单 id 保存
                SharedPreferences pref = BaseApplication.getInstance().getSharedPreferences("ORDERID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ISSaveOrderId", orderId);
                editor.commit();
                Log.e("push === ", "type = " + type);
                if ("0".equals(type)) {//对方邀请
                    if (!orderId.equals(ISSaveOrderId)) {
                        Log.e("push === ", "ISSaveOrderId = " + ISSaveOrderId);
                        Log.e("push === ", "orderId = " + orderId);
                        if (Contants.isInRoom) {
                            //判断是否已经在房间里，如果在通知对方在忙
                            if (AVChatActivity.instance != null) {
                                AVChatActivity.instance.showToast(7, orderId, msg.getSender(),"","","");
                            }
                        } else {
                            try {
                                //判断是否息屏
                                PowerManager pm = (PowerManager) BaseApplication.getInstance().getSystemService(Context.POWER_SERVICE);
                                boolean screenOn = pm.isScreenOn();
                                //如果灭屏
                                if (!screenOn) {
                                    //点亮屏幕
                                    PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
                                    mWakeLock.acquire();
                                    mWakeLock.release();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            AVChatActivity.incomingCall(BaseApplication.getInstance(), orderId, roomID, msg.getSender(), displayName, imageHead);
                            Log.d("hhh", "----" + (Looper.getMainLooper() == Looper.myLooper()));
                        }
                    }
                } else {
                    // 2 对方取消  3 拒绝   4 挂断  5  忙线  6  超时
                    if (AVChatActivity.instance != null) {
                        AVChatActivity.instance.showToast(Integer.parseInt(type), orderId, displayName, translationContent,startTime,endTime);
                    }
                }
            }
        }
    }


    /***
     * 发送自定义消息
     * @param calledUserId  发送消息给对方的id 被叫
     * @param mySelfId   主叫的id
     * @param displayName   主叫的名字
     * @param imageHead   主叫的头像
     * @param enterRoomId   房间的id
     * @param orderId   业务订单id
     * @param type   1 .发起呼叫 2.接听 3.拒绝  4.挂断（退出房间）
     */
    public void sendCustomerMsg(String calledUserId, String mySelfId, String displayName, String imageHead, int enterRoomId, String orderId, String type, String translationContent,String startTime,String endTime) {
        final TIMMessage message = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        CustomeModel customeModel = new CustomeModel();
        customeModel.setType(type);
        customeModel.setRoomId(enterRoomId);
        customeModel.setCallUserId(calledUserId);
        customeModel.setMySelfId(mySelfId);
        customeModel.setMySelfdisplayName(displayName);
        customeModel.setMySelfImageHead(imageHead);
        customeModel.setOrderId(orderId);
        customeModel.setTranslationContent(translationContent);
        customeModel.setStartTime(startTime);
        customeModel.setEndTime(endTime);

        elem.setData(JSON.toJSONBytes(customeModel));
        elem.setExt(JSON.toJSONBytes(customeModel));
        //自定义 byte[]
        TIMMessageOfflinePushSettings settings = new TIMMessageOfflinePushSettings();
        //设置在 Android 设备上收到消息时的离线配置
        TIMMessageOfflinePushSettings.AndroidSettings androidSettings = new TIMMessageOfflinePushSettings.AndroidSettings();
        androidSettings.setSound(Uri.parse("android.resource://" + BaseApplication.getInstance().getPackageName() + "/" + R.raw.avchat_notify));
        settings.setAndroidSettings(androidSettings);
        TIMMessageOfflinePushSettings.IOSSettings iosSettings = new TIMMessageOfflinePushSettings.IOSSettings();
        //设置 iOS 设备收到离线消息时的提示音
        iosSettings.setSound("10.caf");
        settings.setIosSettings(iosSettings);
        if ("0".equals(type)) {
            if (count > 0) {
                settings.setEnabled(false);//取消消息离线推送
            } else {
                Log.e("Push","count ===== " + count);
                settings.setEnabled(true);
            }
            count++;
            elem.setDesc(displayName + "向您视频请求");
        } else {
            elem.setDesc("请求超时取消视频请求");
            settings.setEnabled(false);//取消消息离线推送
        }
        message.setOfflinePushSettings(settings);
        message.addElement(elem);

        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, calledUserId);
        conversation.sendOnlineMessage(message, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.e("发送消息 ==== ", "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e("发送消息 === ", "SendMsg ok");
            }
        });
    }
}
