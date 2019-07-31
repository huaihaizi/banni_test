package com.hanwin.product.tencentim.event;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.hanwin.product.WelcomeActivity;
import com.hanwin.product.tencentim.bean.CustomeModel;
import com.hanwin.product.utils.JSONUtil;
import com.huawei.android.pushagent.api.PushEventReceiver;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushToken;

import java.util.List;

/**
 * 华为推送接收
 */
public class HwPushMessageReceiver extends PushEventReceiver {
    private final String TAG = "HwPushMessageReceiver";
    private long mBussId = 4574;//正式的
//    private long mBussId = 4663;//测试的

    @Override
    public void onToken(Context context, String token, Bundle extras){
        String belongId = extras.getString("belongId");
        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        Log.e(TAG, content);
        TIMOfflinePushToken param = new TIMOfflinePushToken(mBussId,token);
        try {
            Log.e(TAG, "-------------param = new TIMOfflinePushToken(mBussId,token)-1->:");
            TIMManager.getInstance().setOfflinePushToken(param, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "-------onError=------>;"+s);
                }

                @Override
                public void onSuccess() {
                    Log.e(TAG, "-------onSuccess=------>;");
                }
            });
            Log.e(TAG, "-------------param = new TIMOfflinePushToken(mBussId,token)-2->:");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            Log.e(TAG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }

            String content = extras.getString(BOUND_KEY.pushMsgKey);
            Log.e(TAG, "hwpush, recv push msg: " + content);

            List<MessageReceiverBean> messageReceiverBean = JSONUtil.fromJson(content, new TypeToken<List<MessageReceiverBean>>() {
            }.getType());
            Log.e(TAG, "messageReceiverBean: " + messageReceiverBean.get(0).getExt().toString());
            CustomeModel customeModel = JSONUtil.fromJson(messageReceiverBean.get(0).getExt(), new TypeToken<CustomeModel>() {
            }.getType());
            Log.e(TAG, "customeModel: " + customeModel.getMySelfdisplayName());

            Intent mIntent = new Intent(context, WelcomeActivity.class);//华为推送，点击通知栏事件处理
            mIntent.putExtra("customeModel",customeModel);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        } else if (Event.PLUGINRSP.equals(event)) {
            final int TYPE_LBS = 1;
            final int TYPE_TAG = 2;
            int reportType = extras.getInt(BOUND_KEY.PLUGINREPORTTYPE, -1);
            boolean isSuccess = extras.getBoolean(BOUND_KEY.PLUGINREPORTRESULT, false);
            String message = "";
            if (TYPE_LBS == reportType) {
                message = "LBS report result :";
            } else if(TYPE_TAG == reportType) {
                message = "TAG report result :";
            }
            Log.e(TAG, message + isSuccess);
        }
        super.onEvent(context, event, extras);
    }
}
