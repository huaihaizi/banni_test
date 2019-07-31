package com.hanwin.product.tencentim.event;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.tencentim.util.PushUtil;
import com.huawei.android.pushagent.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMUserStatusListener;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by zhaopf on 2018/11/8.
 */

public class RegisterUtils {
    /**
     * 初始化消息监听和第三方推送 、全局离线推送配置、用户状态变更监听器
     */
    public static void initPushMessage() {
        try {
            //初始化程序后台后消息推送
            PushUtil.getInstance();
            //初始化消息监听
            MessageEvent.getInstance();
            String deviceMan = android.os.Build.MANUFACTURER;
            //注册小米和华为推送
//            if (deviceMan.equals("Xiaomi") && shouldMiInit()) {
//                MiPushClient.registerPush(BaseApplication.getInstance(), "2882303761517855687", "5981785544687");
//            } else if (deviceMan.equals("HUAWEI")) {
//                PushManager.requestToken(BaseApplication.getInstance());
//            } else {
//                MiPushClient.registerPush(BaseApplication.getInstance(), "2882303761517855687", "5981785544687");
//            }
            LoggerInterface newLogger = new LoggerInterface() {
                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
                    Log.d("initPushMessage == ", content, t);
                }

                @Override
                public void log(String content) {
                    Log.d("initPushMessage == ", content);
                }
            };
            Logger.setLogger(BaseApplication.getInstance(), newLogger);

            //魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题 appid  appkey
            if (MzSystemUtils.isBrandMeizu(BaseApplication.getInstance())) {
                com.meizu.cloud.pushsdk.PushManager.register(BaseApplication.getInstance(), "1001688", "429d32dff95e400db791913604a72c78");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("eee", e.getMessage());
        }
    }


    /**
     * 判断小米推送是否已经初始化
     */
    private static boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) BaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = BaseApplication.getInstance().getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
