package com.hanwin.product.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.dagger.BaseApplicationComponent;
import com.hanwin.product.common.dagger.DaggerBaseApplicationComponent;
import com.hanwin.product.tencentim.util.Foreground;
import com.hanwin.product.utils.BitmapCache;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.UserLocalData;
import com.hanwin.product.viewutils.ProvinceBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.session.SessionWrapper;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.qcloudtts.RealtimeTTS.RealtimeTtsController;
import com.tencent.qcloudtts.VoiceSpeed;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhaopf on 2018/6/20.
 */

public class BaseApplication extends Application {
    private final String TAG = this.getClass().getSimpleName();
    private static BaseApplicationComponent appComponent;
    private static BaseApplication mInstance;
    private User user = new User();
    private List<ProvinceBean> provinceBeanList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static List<BaseActivity> activityAVList;
    //微信appid
    public static String APP_ID = "wxada661dc1b780af6";
    public static IWXAPI api;

    public static SpeechSynthesizer mSpeechSynthesizer;

    public static RealtimeTtsController controller;

    public static BaseApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        activityAVList = new ArrayList<BaseActivity>();
        buildComponentAndInject();
        initOkGo();
        initUser();
        //微信注册初始化
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        boolean s = api.registerApp(APP_ID);
        Log.e("BaseApplication === ", "" + s);

        //初始化内存缓存目录
        File cacheDir = new File(this.getCacheDir(), "volley");
        /** 初始化RequestQueue,其实这里你可以使用Volley.newRequestQueue来创建一个RequestQueue,直接使用构造函数可以定制我们需要的RequestQueue,比如线程池的大小等等 */
        mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), new BasicNetwork(new HurlStack()), 3);
        BitmapCache mCache = new BitmapCache();
        //初始化ImageLoader
        mImageLoader = new ImageLoader(mRequestQueue, mCache);
        //如果调用Volley.newRequestQueue,那么下面这句可以不用调用
        mRequestQueue.start();
        initBaiduText2Voice();
        initTencentText2Voice();
        Foreground.init(this);
        // 判断仅在主线程进行初始化
        if (SessionWrapper.isMainProcess(this)) {
            //初始化 SDK 基本配置
            TIMSdkConfig config = new TIMSdkConfig(Contants.SDKAPPID);
            //初始化 SDK
            TIMManager.getInstance().init(getApplicationContext(), config);
            Log.d("hhh", "----------TIMManager.getInstance().getVersion()--->:" + TIMManager.getInstance().getVersion());
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                    }
                }
            });

            TIMUserConfig userConfig = new TIMUserConfig();
            userConfig.setUserStatusListener(new TIMUserStatusListener() {
                @Override
                public void onForceOffline() {
                    //被踢下线
                    BaseActivity.againLogin();
//                  EventBus.getDefault().post(new MessageEventBusBean());
                }

                @Override
                public void onUserSigExpired() {
                    //票据过期
                    BaseActivity.againLogin();
//                  EventBus.getDefault().post(new MessageEventBusBean());
                }
            });
            TIMManager.getInstance().setUserConfig(userConfig);
        }
        //友盟统计初始化
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        // 设置组件化的Log开关
        UMConfigure.setLogEnabled(true);
        //极光推送初始化
        JPushInterface.setDebugMode(true);
        // 初始化 JPush
        JPushInterface.init(this);
    }

    /**
     * TODO:初始化文字转换语音
     *
     * @acthor weiang
     * 2019/7/10 10:07 AM
     */
    public void initBaiduText2Voice() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this.getApplicationContext());
        mSpeechSynthesizer.setAppId(Contants.BAIDU_APP_ID);
        mSpeechSynthesizer.setApiKey(Contants.BAIDU_API_KEY, Contants.BAIDU_SECRET_KEY);
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }


    /**
     * TODO:初始化文字转换语音
     *
     * @acthor weiang
     * 2019/7/10 10:07 AM
     */
    public void initTencentText2Voice() {
        controller = new RealtimeTtsController();
        controller.init(Long.valueOf(Contants.appid), Contants.secretId, Contants.secretKey);
        //设置语速
        controller.setVoiceSpeed(0);
        //设置音色
        controller.setVoiceType(0);
        //设置音量
        controller.setVoiceVolume(5);
        //设置语言
        controller.setVoiceLanguage(1);
        //设置ProjectId
        controller.setProjectId(0);
    }


    /**
     * TODO:初始化okgo
     *
     * @acthor weiang
     * 2019/7/15 11:05 AM
     */
    private void initOkGo() {
        OkGo.getInstance()
                .setRetryCount(0);
        OkGo.getInstance().getOkHttpClient().newBuilder().retryOnConnectionFailure(true);
        HttpHeaders commonHeaders = new HttpHeaders();
        commonHeaders.put(HttpHeaders.HEAD_KEY_CONNECTION, HttpHeaders.HEAD_VALUE_CONNECTION_CLOSE);
        OkGo.getInstance().addCommonHeaders(commonHeaders);
    }


    private void initUser() {
        this.user = UserLocalData.getUser(this);

    }


    public User getUser() {
        return user;
    }


    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    public void clearUser() {
        this.user = new User();
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
        JPushInterface.setAliasAndTags(this, "", null, null);
    }

    public void setToken(String token) {
        UserLocalData.putToken(this, token);
    }

    public String getToken() {
        String token = UserLocalData.getToken(this) + "";
        return token;
    }

    /**
     * 存省市区
     *
     * @param provinceBeanLis
     */
    public void setProvince(List<ProvinceBean> provinceBeanLis) {
        UserLocalData.setProvince(this, provinceBeanLis);
    }

    /**
     * 取省市
     *
     * @return
     */
    public List<ProvinceBean> getProvinceBeanList() {
        List<ProvinceBean> provinceList = UserLocalData.getProvince(this);
        return provinceList;
    }

    private Intent intent;

    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }


    public static BaseApplicationComponent component() {
        return appComponent;
    }

    public static void buildComponentAndInject() {
        appComponent = DaggerBaseApplicationComponent.Initializer.init(mInstance);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * 添加activity到list中
     */
    public static void addAVActivity(BaseActivity activity) {
        if (activityAVList != null && activity != null) {
            activityAVList.add(activity);
        }
    }

    /**
     * 退出list中的activity
     */
    public static void quitAVActivity() {
        if (activityAVList != null && !activityAVList.isEmpty()) {
            for (Activity a : activityAVList) {
                a.finish();
            }
        }
    }


}
