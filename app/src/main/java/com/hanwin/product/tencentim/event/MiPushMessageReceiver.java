package com.hanwin.product.tencentim.event;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.hanwin.product.WelcomeActivity;
import com.hanwin.product.tencentim.bean.CustomeModel;
import com.hanwin.product.utils.JSONUtil;
import com.google.gson.reflect.TypeToken;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushToken;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 小米推送接收
 */
public class MiPushMessageReceiver extends PushMessageReceiver {

    private final String TAG = "MiPushMessageReceiver";
    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

//    private long mBussId = 4573;//正式证书id

    private long mBussId = 4662;//测试证书id


    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.e(TAG,"onReceivePassThroughMessage is called. " + message.toString());
        Log.e(TAG, getSimpleDate() + " " + message.getContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
        Log.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(TAG,"onNotificationMessageClicked is called. " + message.toString());
        Log.v(TAG, getSimpleDate() + " " + message.getContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
//        MiPushClient.clearNotification(context);

        JSONObject jsonObject = new JSONObject(message.getExtra());
        Log.e(TAG, "mipush, recv push msg: " + jsonObject.toString());

        MessageReceiverBean messageReceiverBean = JSONUtil.fromJson(jsonObject.toString(), new TypeToken<MessageReceiverBean>() {
        }.getType());
        CustomeModel customeModel = JSONUtil.fromJson(messageReceiverBean.getExt(), new TypeToken<CustomeModel>() {
        }.getType());

        Log.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
        Intent mIntent = new Intent(context, WelcomeActivity.class);
        mIntent.putExtra("customeModel",customeModel);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e(TAG,"onNotificationMessageArrived is called. " + message.toString());
        Log.e(TAG, getSimpleDate() + " " + message.getContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        Log.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG, "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);

        Log.e(TAG, "cmd: " + command + " | arg1: " + cmdArg1 + " | arg2: " + cmdArg2
                + " | result: " + message.getResultCode() + " | reason: " + message.getReason());

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }

        Log.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG, "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);

        Log.e(TAG, "cmd: " + command + " | arg: " + cmdArg1
                + " | result: " + message.getResultCode() + " | reason: " + message.getReason());

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;

                TIMOfflinePushToken param = new TIMOfflinePushToken(mBussId,mRegId);
                TIMManager.getInstance().setOfflinePushToken(param, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "setOfflinePushToken failed, code: " + i + "|msg: " + s);
                    }
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "setOfflinePushToken success");
                    }
                });
            }
        }

        Log.e(TAG, "regId: " + mRegId + " | topic: " + mTopic + " | alias: " + mAlias
                + " | account: " + mAccount + " | starttime: " + mStartTime + " | endtime: " + mEndTime);

    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

}
