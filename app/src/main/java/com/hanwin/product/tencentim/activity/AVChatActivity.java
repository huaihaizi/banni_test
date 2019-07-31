package com.hanwin.product.tencentim.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.tencentim.bean.MixStreamBean;
import com.hanwin.product.tencentim.bean.TranslateWordBean;
import com.hanwin.product.tencentim.event.AVChatSoundPlayer;
import com.hanwin.product.tencentim.observable.AVChatTimeoutObserver;
import com.hanwin.product.tencentim.observable.Observer;
import com.hanwin.product.tencentim.util.AudioByteDataSource;
import com.hanwin.product.tencentim.util.PushUtil;
import com.hanwin.product.utils.AppUtils;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.FileUtil;
import com.hanwin.product.utils.MD5Utils;
import com.hanwin.product.utils.TimeUtils;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.utils.Utils;
import com.hanwin.product.utils.recorder.AudioRecorder;
import com.hanwin.product.utils.recorder.RecordStreamListener;
import com.tencent.aai.AAIClient;
import com.tencent.aai.audio.data.AudioRecordDataSource;
import com.tencent.aai.auth.AbsCredentialProvider;
import com.tencent.aai.auth.LocalCredentialProvider;
import com.tencent.aai.config.ClientConfiguration;
import com.tencent.aai.exception.ClientException;
import com.tencent.aai.exception.ServerException;
import com.tencent.aai.listener.AudioRecognizeResultListener;
import com.tencent.aai.model.AudioRecognizeRequest;
import com.tencent.aai.model.AudioRecognizeResult;
import com.tencent.aai.model.type.AudioRecognizeConfiguration;
import com.tencent.aai.model.type.AudioRecognizeTemplate;
import com.tencent.aai.model.type.EngineModelType;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Response;


public class AVChatActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.text_reject)
    TextView text_reject;//拒绝
    @Bind(R.id.text_hang_up)
    ImageView text_hang_up;//竖屏挂断
    @Bind(R.id.text_hang_up1)
    ImageView text_hang_up1;//横屏挂断
    @Bind(R.id.text_answer)
    TextView text_answer;//接听
    @Bind(R.id.text_cancel)
    ImageView text_cancel;//取消视频请求
    @Bind(R.id.image_switch_camera)
    ImageView image_switch_camera;//竖屏切换摄像头
    @Bind(R.id.image_switch_camera1)
    ImageView image_switch_camera1;//横屏切换摄像头
    @Bind(R.id.image_full_screen)
    ImageView image_full_screen;//全屏
    @Bind(R.id.image_reset_full_screen)
    ImageView image_reset_full_screen;//收回全屏
    @Bind(R.id.av_trans_bg)
    TextView av_trans_bg;//字幕背景

    @Bind(R.id.lin_btn)
    LinearLayout lin_btn;//下面三个按钮，包含切换摄像头，挂断，全屏
    @Bind(R.id.lin_leftbtn)
    LinearLayout lin_leftbtn;

    @Bind(R.id.big_view)
    TXCloudVideoView big_view;
    @Bind(R.id.small_view)
    TXCloudVideoView small_view;

    @Bind(R.id.image_head)
    ImageView image_head;//头像
    @Bind(R.id.text_name)
    TextView text_name;//名字
    @Bind(R.id.text_state)
    TextView text_state;

    @Bind(R.id.avchat_time)
    Chronometer avchat_time;//计时器

    @Bind(R.id.rel_lay)
    RelativeLayout rel_lay;//头像 名字 部分
    @Bind(R.id.lin_in_coming)
    LinearLayout lin_in_coming;//呼入等待界面

    @Bind(R.id.lin_signlanguage)
    LinearLayout lin_signlanguage;//手语翻译
    @Bind(R.id.lin_consumer)
    LinearLayout lin_consumer;//客户
    @Bind(R.id.signlanguage_image)
    ImageView signlanguage_image;
    @Bind(R.id.consumer_image)
    ImageView consumer_image;

    @Bind(R.id.show_text)
    TextView show_text;
    @Bind(R.id.show_text1)
    TextView show_text1;

    String name = "";

    private String receiverId; // 手语师的account
    private String displayName; // 手语师的名字
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private String roomId;//加入房间的id
    private String orderId = System.currentTimeMillis() + "";//业务订单id
    private String imageHead;//手语师的头像
    public static AVChatActivity instance;
    private Vibrator vibrator;

    Timer timer = new Timer();//定时器
    private AAIClient aaiClient;
    private AudioRecognizeRequest audioRecognizeRequest;

    private TRTCCloudDef.TRTCParams trtcParams;     /// TRTC SDK 视频通话房间进入所必须的参数
    private TRTCCloud trtcCloud;              /// TRTC SDK 实例对象
    private TRTCCloudListener trtcListener;    /// TRTC SDK 回调监听

    User user = BaseApplication.getInstance().getUser();
    private Handler handler;
    private AVChatSoundPlayer avChatSoundPlayer;

    private long startTime;//一句话开始时间
    private long endTime;//一句话结束时间
    private long videoStartTime;//视频开始时间
    private String voiceId = "";//一句话id

    private String textResultSring = "";

    private AudioByteDataSource audioByteDataSource;
    byte[] newdata = new byte[0];
    List<byte[]> listbyte = new ArrayList();

    OrientationEventListener orientationEventListener;
    // 这是记录当前角度
    int rotationFlag = 90;
    //默认是竖屏状态，由于由于系统默认的是横屏，所以需要旋转90
    int rotationRecord = 90;
    private boolean isVerticalScreen = true;

    private AudioRecorder audioRecorder;

    @SuppressLint("HandlerLeak")
    Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: {
                    if (TextUtils.isEmpty(textResultSring)) {
                        return;
                    }
                    if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                        signlanguage_image.setVisibility(View.VISIBLE);
                        show_text.setText(":  " + textResultSring);
                    } else {
                        consumer_image.setVisibility(View.VISIBLE);
                        show_text1.setText(":  " + textResultSring);
                    }
                }
                default:
            }
        }
    };


    // 拨打电话
    public static void outgoingCall(Context context, String orderId, String roomId, String account, String displayName, String imageHead) {
        if (!TextUtils.isEmpty(roomId)) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, AVChatActivity.class);
            intent.putExtra("account", account);
            intent.putExtra("roomId", roomId);
            intent.putExtra("displayName", displayName);
            intent.putExtra("orderId", orderId);
            intent.putExtra("mIsInComingCall", false);
            intent.putExtra("imageHead", imageHead);
            Log.e("outgoingCall ======= ", "receiverId == " + account + "roomId ==== " + roomId);
            context.startActivity(intent);
        } else {
            ToastUtils.show(BaseApplication.getInstance(), "呼叫失败，请稍后重试");
        }
    }

    // 接听来电
    public static void incomingCall(Context context, String orderId, String roomId, String account, String displayName, String imageHead) {
        if (!TextUtils.isEmpty(roomId)) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, AVChatActivity.class);
            intent.putExtra("account", account);
            intent.putExtra("roomId", roomId);
            intent.putExtra("displayName", displayName);
            intent.putExtra("orderId", orderId);
            intent.putExtra("mIsInComingCall", true);
            intent.putExtra("imageHead", imageHead);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_msg_room);
        ButterKnife.bind(this);
        //在oncreate中添加
        instance = this;
        Contants.isInRoom = true;
        if (Contants.list != null && Contants.list.size() > 0) {
            Contants.list.clear();
        }
        avChatSoundPlayer = new AVChatSoundPlayer();
        //创建 TRTC SDK 实例
        trtcListener = new TRTCCloudListenerImpl(this);
        trtcCloud = TRTCCloud.sharedInstance(this);
        trtcCloud.setListener(trtcListener);
        Log.e("sdkversion == ", TRTCCloud.getSDKVersion());
        initView();
//      rotationUIListener();
    }


    private void initView() {
        receiverId = getIntent().getStringExtra("account");
        roomId = getIntent().getStringExtra("roomId");
        displayName = getIntent().getStringExtra("displayName");
        orderId = getIntent().getStringExtra("orderId");
        mIsInComingCall = getIntent().getBooleanExtra("mIsInComingCall", false);
        imageHead = getIntent().getStringExtra("imageHead");
        text_answer.setOnClickListener(this);
        text_reject.setOnClickListener(this);
        text_hang_up.setOnClickListener(this);
        text_hang_up1.setOnClickListener(this);
        text_cancel.setOnClickListener(this);
        image_switch_camera.setOnClickListener(this);
        image_switch_camera1.setOnClickListener(this);
        image_full_screen.setOnClickListener(this);
        image_reset_full_screen.setOnClickListener(this);

        //显示头像
        String imageHeadUrl = "";
        if (!TextUtils.isEmpty(imageHead) && imageHead.startsWith("http:")) {
            imageHeadUrl = imageHead;
        } else {
            imageHeadUrl = Contants.BASE_IMAGE + imageHead;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.image_head_man);
        Glide.with(this).load(imageHeadUrl)
                .apply(options)
                .into(image_head);

        if (!TextUtils.isEmpty(BaseApplication.getInstance().getUser().getName())) {
            name = BaseApplication.getInstance().getUser().getName();
        } else {
            if (!TextUtils.isEmpty(BaseApplication.getInstance().getUser().getNickName())) {
                name = BaseApplication.getInstance().getUser().getNickName();
            }
        }
        text_name.setText(displayName);

        handler = new Handler(getMainLooper());

        Log.e("initView ======= ", "receiverId == " + receiverId + "roomId ==== " + roomId);
        if (!mIsInComingCall) {
            lin_in_coming.setVisibility(View.GONE);
            text_state.setText(R.string.avchat_wait_recieve);
            enterRoom();
        } else {
            text_state.setText(R.string.avchat_video_call_request);
            lin_in_coming.setVisibility(View.VISIBLE);
            text_hang_up.setVisibility(View.GONE);
            text_cancel.setVisibility(View.GONE);
            //来电铃声
            avChatSoundPlayer.play(AVChatSoundPlayer.RingerTypeEnum.RING);
            //初始化震动服务
            vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            long[] patter = {1000, 1000, 2000, 50};
            vibrator.vibrate(patter, 0);
        }
        dismissKeyguard();
        AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, true, mIsInComingCall);
        clearNotification();

        audioRecorder = new AudioRecorder();
        audioRecorder.createDefaultAudio("banni_test");
        audioRecorder.setListener(new RecordStreamListener() {
            @Override
            public void recordOfByte(byte[] data, int begin, int end) {
                if (trtcCloud != null) {
                    TRTCCloudDef.TRTCAudioFrame trtcAudioFrame = new TRTCCloudDef.TRTCAudioFrame();
                    trtcAudioFrame.data = data;
                    trtcAudioFrame.sampleRate = 48000;
                    trtcAudioFrame.timestamp = System.currentTimeMillis();
                    Log.d("radio", "---------data-()--->:" + Arrays.toString(data));
                    trtcCloud.sendCustomAudioData(trtcAudioFrame);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //接听
            case R.id.text_answer:
                text_answer.setEnabled(false);
                avChatSoundPlayer.stop();
                //接听对方视频
                PushUtil.getInstance().sendCustomerMsg(receiverId, user.getUid(), name, user.getAvatar(), Integer.parseInt(roomId), orderId, "1", "", "", "");
                enterRoom();
                // 取消震动
                if (vibrator != null) {
                    vibrator.cancel();
                }
                break;
            case R.id.text_reject:
                //拒绝
                text_reject.setEnabled(false);
                PushUtil.getInstance().sendCustomerMsg(receiverId, "", "", "", 0, orderId, "3", "", "", "");
                avChatSoundPlayer.stop();
                finish();
                break;
            case R.id.text_hang_up:
            case R.id.text_hang_up1:
                //挂断
                text_hang_up.setEnabled(false);
                //发送广播
                sendBroadcast("hangUp", orderId, receiverId);
                PushUtil.getInstance().sendCustomerMsg(receiverId, "", "", "", 0, orderId, "4", "", "", "");
                finish();
                break;
            //取消
            case R.id.text_cancel:
                sendBroadcast("cancel", orderId, receiverId);
                PushUtil.getInstance().sendCustomerMsg(receiverId, "", "", "", 0, orderId, "2", "", "", "");
                finish();
                break;
            //切换摄像头
            case R.id.image_switch_camera:
            case R.id.image_switch_camera1:
                switchCamera();
                break;
            //横屏
            case R.id.image_full_screen:
                isVerticalScreen = false;
                setLandscape();
                break;
            //竖屏
            case R.id.image_reset_full_screen:
                isVerticalScreen = true;
                setPortrait();
                break;
            default:
        }
    }

    /**
     * 设置视频通话的视频参数：需要 TRTCSettingDialog 提供的分辨率、帧率和流畅模式等参数
     */
    private void setTRTCCloudParam() {
        TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
        encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
        encParam.videoFps = 15;
        encParam.videoBitrate = 600;
        encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        trtcCloud.setVideoEncoderParam(encParam);
        TRTCCloudDef.TRTCNetworkQosParam qosParam = new TRTCCloudDef.TRTCNetworkQosParam();
        qosParam.controlMode = TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER;
        qosParam.preference = TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_CLEAR;
        trtcCloud.setNetworkQosParam(qosParam);
        // 美颜
        trtcCloud.setBeautyStyle(TRTCCloudDef.TRTC_BEAUTY_STYLE_SMOOTH, 5, 5, 5);

        //TODO:开启本地音频采集
        trtcCloud.enableCustomAudioCapture(true);
    }

    /**
     * 加入视频房间：需要 TRTCNewViewActivity 提供的  TRTCParams 函数
     */
    private void enterRoom() {
        try {
            trtcParams = new TRTCCloudDef.TRTCParams(Contants.SDKAPPID, user.getUid(), user.getSignature(), Integer.parseInt(roomId), "", "");
            // 预览前配置默认参数
            setTRTCCloudParam();
            // 开启视频采集预览
            small_view.setUserId(trtcParams.userId);
            small_view.setVisibility(View.INVISIBLE);
            trtcCloud.startLocalPreview(true, big_view);
            trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);

            //TODO:开启本地音频采集
            trtcCloud.setAudioFrameListener(new TRTCCloudListener.TRTCAudioFrameListener() {
                @Override
                public void onCapturedAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame) {
                    Log.d("hhh", "--------onCapturedAudioFrame-------------->:" + trtcAudioFrame.data);
                }

                @Override
                public void onPlayAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame, String s) {

                }

                @Override
                public void onMixedPlayAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame) {

                }
            });
            //(String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat
            audioRecorder.startRecord();
            //trtcCloud.startLocalAudio();
            //进房
            trtcCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出视频房间
     */
    private void exitRoom() {
        if (trtcCloud != null) {
            trtcCloud.exitRoom();
        }
    }

    /**
     * 切换摄像头
     */
    private void switchCamera() {
        trtcCloud.switchCamera();
        if (!isVerticalScreen) {
            trtcCloud.setVideoEncoderRotation(TRTCCloudDef.TRTC_VIDEO_ROTATION_0);
        }
    }


    /**
     * 重力感应监听回调
     */
    private void rotationUIListener() {
        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int rotation) {
                if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
                    // 竖屏
                    if (rotationFlag != 0) {
                        rotationRecord = 90;
                        rotationFlag = 0;
                        Log.e("rotationUIListener === ", "竖屏");
                        setPortrait();
                    }
                } else if (((rotation >= 230) && (rotation <= 310))) {
                    // 左横屏
                    if (rotationFlag != 90) {
                        rotationRecord = 0;
                        rotationFlag = 90;
                        Log.e("rotationUIListener === ", "左横屏");
                        setLandscape();
                    }
                } else if (rotation > 30 && rotation < 95) {
                    // 右横屏
                    if (rotationFlag != 270) {
                        rotationRecord = 180;
                        rotationFlag = 270;
                        Log.e("rotationUIListener === ", "右横屏");
                    }
                }
            }
        };
        orientationEventListener.enable();
    }

    /**
     * 设置横屏参数
     */
    private void setLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setLandscapeOrPortrait(false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(small_view.getLayoutParams());
        //动态设置小屏位置、大小参数
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.width = 320;
        lp.height = 180;
        lp.setMargins(0, 40, 40, 0);
        small_view.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(av_trans_bg.getLayoutParams());
        //动态设置字幕背景位置、大小参数
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp1.height = lin_consumer.getHeight() + lin_signlanguage.getHeight() + 100;
        lp1.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        av_trans_bg.setLayoutParams(lp1);
        lin_btn.setVisibility(View.GONE);
        lin_leftbtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置竖屏参数
     */
    private void setPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setLandscapeOrPortrait(true);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(small_view.getLayoutParams());
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.width = 180;
        lp.height = 320;
        lp.setMargins(0, 40, 40, 0);
        small_view.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(av_trans_bg.getLayoutParams());
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp1.height = lin_consumer.getHeight() + lin_signlanguage.getHeight() + lin_btn.getHeight() + 100;
        lp1.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        av_trans_bg.setLayoutParams(lp1);
        lin_btn.setVisibility(View.VISIBLE);
        lin_leftbtn.setVisibility(View.GONE);
    }

    /***
     * 设置视频横竖屏采集的参数
     * @param isPortrait true 竖屏  false 横屏
     */
    private void setLandscapeOrPortrait(boolean isPortrait) {
        try {
            TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
            encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
            encParam.videoFps = 15;
            encParam.videoBitrate = 600;
            if (isPortrait) {
                encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
                trtcCloud.setVideoEncoderParam(encParam);
            } else {
                encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE;
                trtcCloud.setVideoEncoderParam(encParam);
                trtcCloud.setVideoEncoderRotation(TRTCCloudDef.TRTC_VIDEO_ROTATION_0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //超时回调
    Observer<Integer> timeoutObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            // 来电超时，自己未接听
            if (mIsInComingCall) {
                avChatSoundPlayer.stop();
                finish();
            } else {
                //去电超时
                sendBroadcast("timeout", orderId, receiverId);
                PushUtil.getInstance().sendCustomerMsg(receiverId, "", "", "", 0, orderId, "6", "", "", "");
                AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, false, mIsInComingCall);//取消
                if (timer != null) {
                    timer.cancel();
                }
                exitRoom();//退出房间
            }
        }
    };

    /**
     * 根据不同的情况，进行提示
     *
     * @param type
     */
    public void showToast(int type, final String orderID, String sender, final String translationContent, final String startTime, final String endTime) {
        if (type == 1) {
//            ToastUtils.show(BaseApplication.getInstance(), "");
        } else if (type == 2) {
            ToastUtils.show(BaseApplication.getInstance(), "对方已取消视频请求");
            avChatSoundPlayer.stop();
            instance.finish();
        } else if (type == 3) {
            sendBroadcast("reject", orderId, receiverId);
            ToastUtils.show(BaseApplication.getInstance(), "对方已拒绝");
            instance.finish();
        } else if (type == 4) {
            //当挂断后发送一个广播
            sendBroadcast("hangUp", orderId, receiverId);
            ToastUtils.show(BaseApplication.getInstance(), "对方已挂断，通话结束");
            instance.finish();
        } else if (type == 5) {
            //对方在忙线 关闭当前界面
            sendBroadcast("busy", orderId, receiverId);
            ToastUtils.show(BaseApplication.getInstance(), "对方正在通话中");
            instance.finish();
        } else if (type == 6) {
            avChatSoundPlayer.stop();
            finish();
        } else if (type == 7) {
            //通知对方在忙线
            PushUtil.getInstance().sendCustomerMsg(sender, "", "", "", 0, orderId, "5", "", "", "");
        } else if (type == 10) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(translationContent)) {
                        if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                            consumer_image.setVisibility(View.VISIBLE);
                            show_text1.setText(translationContent);
                            dealStr(show_text1);
                        } else {
                            signlanguage_image.setVisibility(View.VISIBLE);
                            show_text.setText(translationContent);
                            dealStr(show_text);
                        }
                    }
                }
            });
        } else if (type == 11) {
            if (!TextUtils.isEmpty(translationContent)) {
                try {
                    TranslateWordBean translateWordBean = new TranslateWordBean();
                    if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                        translateWordBean.setName("客户");
                    } else {
                        translateWordBean.setName("手语师");
                    }
                    translateWordBean.setStartTime(startTime + "");
                    translateWordBean.setContent(translationContent);
                    translateWordBean.setEndTime(endTime + "");
                    translateWordBean.setVideoStartTime(videoStartTime + "");
                    FileUtil.writejSON2File(translateWordBean, orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送广播
     *
     * @param actionType 注册广播过滤类型
     * @param orderNo
     * @param account
     */
    private void sendBroadcast(String actionType, String orderNo, String account) {
        Intent intent = new Intent();
        if ("receiveCall".equals(actionType)) {
            intent.putExtra("chatId", roomId);
        }
        intent.putExtra("account", account);
        intent.putExtra("orderNo", orderNo);
        intent.setAction(actionType);
        sendBroadcast(intent);
    }

    /**
     * 权限检测
     *
     * @return
     */
    protected boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        (String[]) permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //禁止返回键返回
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (avchat_time != null) {
                avchat_time.stop();   //定时器停止
            }
            //取消震动
            if (vibrator != null) {
                vibrator.cancel();
            }
            if (timer != null) {
                timer.cancel();
            }
            exitRoom();
            //销毁 trtc 实例
            if (trtcCloud != null) {
                trtcCloud.setListener(null);
                trtcCloud.setAudioFrameListener(null);//音频数据回调，listener = null 则停止回调数据
                send(true, "", "", roomId);
            }
            trtcCloud = null;
            TRTCCloud.destroySharedInstance();
            avChatSoundPlayer.stop();
            //移除超时监听
            AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, false, mIsInComingCall);
            Contants.isInRoom = false;
            cancelAudioRecognize();
        }
    }

    /**
     * 定时任务
     */
    class RequestTimerTask extends TimerTask {
        private Context mcontext;
        private String receiverId1;
        private String roomId1;
        private String orderNo;

        public RequestTimerTask(Context context, String receiverId, String roomId, String orderNo) {
            this.mcontext = context;
            this.receiverId1 = receiverId;
            this.roomId1 = roomId;
            this.orderNo = orderNo;
        }

        @Override
        public void run() {
            Log.e("timer ======= ", "隔了5s了" + "。receiverId == " + receiverId + "roomId ==== " + roomId);
            PushUtil.getInstance().sendCustomerMsg(receiverId, user.getUid(), name, user.getAvatar(), Integer.parseInt(roomId), orderId, "0", "", "", "");//发起视频请求，等待接听
        }
    }


    // 设置窗口flag，亮屏并且解锁/覆盖在锁屏界面上
    private void dismissKeyguard() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    // SDK内部状态回调
    class TRTCCloudListenerImpl extends TRTCCloudListener {
        private WeakReference<AVChatActivity> mContext;

        public TRTCCloudListenerImpl(AVChatActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        // 加入房间
        @Override
        public void onEnterRoom(long elapsed) {
            AVChatActivity activity = mContext.get();
            if (activity != null) {
                if (!"signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                    PushUtil.getInstance().setInitialize();
                    timer = new Timer();
                    timer.schedule(new RequestTimerTask(instance, receiverId, roomId, orderId), 0 * 1000, 5 * 1000);
                } else {

                }
            }
        }

        // 离开房间
        @Override
        public void onExitRoom(int reason) {
            AVChatActivity activity = mContext.get();
            if (activity != null) {
                activity.finish();
            }
        }

        // ERROR 大多是不可恢复的错误，需要通过 UI 提示用户
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Log.e(TAG, "sdk callback onError");
            AVChatActivity activity = mContext.get();
            if (activity != null) {
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }
        }

        // WARNING 大多是一些可以忽略的事件通知，SDK内部会启动一定的补救机制
        @Override
        public void onWarning(int warningCode, String warningMsg, Bundle extraInfo) {
            Log.e(TAG, "sdk callback onWarning");
        }

        // 有新的用户加入了当前视频房间
        @Override
        public void onUserEnter(String userId) {
            if (timer != null) {
                timer.cancel();
            }
            //移除超时监听
            AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, false, mIsInComingCall);
            rel_lay.setVisibility(View.GONE);//头像布局
            text_cancel.setVisibility(View.GONE);//呼出等待取消按钮
            lin_in_coming.setVisibility(View.GONE);//呼入等待接听拒绝按钮
            lin_btn.setVisibility(View.VISIBLE);//三个按钮显示
            text_hang_up.setVisibility(View.VISIBLE);
            image_switch_camera.setVisibility(View.VISIBLE);
            av_trans_bg.setVisibility(View.VISIBLE);
            avchat_time.setVisibility(View.VISIBLE);
            avchat_time.setBase(SystemClock.elapsedRealtime());//计时器清零
            avchat_time.start();//计时器开始计时

            lin_signlanguage.setVisibility(View.VISIBLE);
            lin_consumer.setVisibility(View.VISIBLE);

            //开始时间
            try {
                videoStartTime = TimeUtils.getCurrentTimeInLong();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //语音识别
            String deviceMan = android.os.Build.MANUFACTURER;
            if (deviceMan.equals("Xiaomi")) {
                voiceRecognition();
            }
            if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                sendBroadcast("receiveCall", orderId, receiverId);
                enableTranscoding();//开启混流
            }
        }

        // 有用户屏蔽了画面
        @Override
        public void onUserVideoAvailable(final String userId, boolean available) {
            AVChatActivity activity = mContext.get();
            if (activity != null && activity.trtcCloud != null) {
                if (available) {
                    if (big_view != null) {
                        small_view.setVisibility(View.VISIBLE);
                        // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                        activity.trtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
                        activity.trtcCloud.setDebugViewMargin(userId, new TRTCCloud.TRTCViewMargin(0.0f, 0.0f, 0.1f, 0.0f));
                        activity.trtcCloud.stopLocalPreview();
                        activity.trtcCloud.startLocalPreview(true, small_view);
                        activity.trtcCloud.startRemoteView(userId, big_view);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                big_view.setUserId(userId + TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                            }
                        });
                    }
                } else {
                    activity.trtcCloud.stopRemoteView(userId);
                }
            }
        }

        // 有用户离开了当前视频房间
        @Override
        public void onUserExit(String userId, int reason) {
            AVChatActivity activity = mContext.get();
            if (activity != null && activity.trtcCloud != null) {
                activity.trtcCloud.stopRemoteView(userId);
                activity.finish();
            }

        }
    }

    /**
     * 语音识别功能
     */
    private void voiceRecognition() {
//        // 为了方便用户测试，sdk提供了本地签名，但是为了secretKey的安全性，正式环境下请自行在第三方服务器上生成签名。
        AbsCredentialProvider credentialProvider = new LocalCredentialProvider(Contants.secretKey);
        try {
            // 1、初始化AAIClient对象。
            aaiClient = new AAIClient(this, Contants.appid, Contants.projectid, Contants.secretId, credentialProvider);
            // 2、初始化语音识别请求。
            // 用户配置
            ClientConfiguration.setServerProtocolHttps(false); // 是否启用https，默认启用
            ClientConfiguration.setMaxAudioRecognizeConcurrentNumber(2); // 语音识别的请求的最大并发数
            ClientConfiguration.setMaxRecognizeSliceConcurrentNumber(10); // 单个
            audioRecognizeRequest = new AudioRecognizeRequest.Builder()
                    .pcmAudioDataSource(new AudioRecordDataSource()) // 设置语音源为麦克风输入
                    //.templateName(templateName) // 设置模板
                    .template(new AudioRecognizeTemplate(EngineModelType.EngineModelType16K, 0, 0)) // 设置自定义模板
                    .build();
            // 自定义识别配置
            final AudioRecognizeConfiguration audioRecognizeConfiguration = new AudioRecognizeConfiguration.Builder()
                    .enableAudioStartTimeout(false) // 是否使能起点超时停止录音
                    .enableAudioEndTimeout(false) // 是否使能终点超时停止录音
                    .enableSilentDetect(true) // 是否使能静音检测，true表示不检查静音部分
                    .minAudioFlowSilenceTime(1000) // 语音流识别时的间隔时间
                    .maxAudioFlowSilenceTime(10000) // 语音终点超时时间
                    .maxAudioStartSilenceTime(10000) // 语音起点超时时间
                    .minVolumeCallbackTime(80) // 音量回调时间
                    .sensitive(2)
                    .build();

            // 3、初始化语音识别结果监听器。
            final AudioRecognizeResultListener audioRecognizeResultListener = new AudioRecognizeResultListener() {
                @Override
                public void onSliceSuccess(AudioRecognizeRequest audioRecognizeRequest, final AudioRecognizeResult audioRecognizeResult, int i) {
                    // 返回语音分片的识别结果
                    Log.e("onSuccess 111 ===  ", "i == " + i + "VoiceId == " + audioRecognizeResult.getVoiceId() + "text == " + audioRecognizeResult.getText());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(audioRecognizeResult.getText())) {
                                PushUtil.getInstance().sendCustomerMsg(receiverId, "", "", "", 0, orderId, "10", audioRecognizeResult.getText(), "", "");
                                if (!voiceId.equals(audioRecognizeResult.getVoiceId())) {
                                    try {
                                        startTime = TimeUtils.getCurrentTimeInLong();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    voiceId = audioRecognizeResult.getVoiceId();
                                }

                                if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                                    signlanguage_image.setVisibility(View.VISIBLE);
                                    show_text.setText(audioRecognizeResult.getText());
                                    dealStr(show_text);
                                } else {
                                    consumer_image.setVisibility(View.VISIBLE);
                                    show_text1.setText(audioRecognizeResult.getText());
                                    dealStr(show_text1);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onSegmentSuccess(AudioRecognizeRequest audioRecognizeRequest, final AudioRecognizeResult audioRecognizeResult, int i) {
                    // 返回语音流的识别结果
                    Log.e("onSuccess 222 ===  ", "i == " + i + "VoiceId == " + audioRecognizeResult.getVoiceId() + "text == " + audioRecognizeResult.getText());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(audioRecognizeResult.getText())) {
                                TranslateWordBean translateWordBean = new TranslateWordBean();
                                if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                                    signlanguage_image.setVisibility(View.VISIBLE);
                                    show_text.setText(audioRecognizeResult.getText());
                                    dealStr(show_text);
                                } else {
                                    consumer_image.setVisibility(View.VISIBLE);
                                    show_text1.setText(audioRecognizeResult.getText());
                                    dealStr(show_text1);
                                }

                                try {
                                    if (voiceId.equals(audioRecognizeResult.getVoiceId())) {
                                        endTime = TimeUtils.getCurrentTimeInLong();
                                    }
                                    PushUtil.getInstance().sendCustomerMsg(receiverId, "", "", "", 0, orderId, "10", audioRecognizeResult.getText(), "", "");
                                    PushUtil.getInstance().sendCustomerMsg(receiverId, "", user.getName(), "", 0, orderId, "11", audioRecognizeResult.getText(), startTime + "", endTime + "");
                                    translateWordBean.setStartTime(startTime + "");
                                    translateWordBean.setContent(audioRecognizeResult.getText());
                                    translateWordBean.setEndTime(endTime + "");
                                    translateWordBean.setVideoStartTime(videoStartTime + "");
                                    FileUtil.writejSON2File(translateWordBean, orderId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onSuccess(AudioRecognizeRequest audioRecognizeRequest, String s) {
                    // 返回所有的识别结果
                }

                @Override
                public void onFailure(AudioRecognizeRequest audioRecognizeRequest, ClientException e, ServerException e1) {
                    // 识别失败
                    Log.e("onFailure ===  ", "e == " + e + "e1 == " + e1);
                }
            };

            // 4、启动语音识别
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (aaiClient != null) {
                        aaiClient.startAudioRecognize(audioRecognizeRequest, audioRecognizeResultListener, audioRecognizeConfiguration);
                    }
                }
            }).start();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消语音识别
     */
    private void cancelAudioRecognize() {
        // 1、获得请求的id
        if (audioRecognizeRequest != null) {
            final int requestId = audioRecognizeRequest.getRequestId();
            // 2、调用cancel方法
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (aaiClient != null) {
                        aaiClient.stopAudioRecognize(requestId);
                        aaiClient.cancelAudioRecognize(requestId);
                        aaiClient.release();
                    }
                }
            }).start();
        }

        if (aaiClient != null) {
            aaiClient.release();
        }
    }

    //开启云端混流转码
    public void enableTranscoding() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                send(false, BaseApplication.getInstance().getUser().getUid(), receiverId, roomId);
            }
        }).start();
    }

    public void send(final boolean isCancel, String user1, String user2, String roomId) {
        Log.e("user1 =============  ", user1);
        Log.e("user2 =============  ", user2);
        String key = "60af2665882f761d212ea7ae89f12334";//API鉴权key
        Long t = null; //有效时间
        try {
            t = System.currentTimeMillis() / 1000 + 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sign = MD5Utils.encode(key + t); //安全签名
        //鉴权url
        String url = "http://fcgi.video.qcloud.com/common_access?appid=1255423799&interface=Mix_StreamV2&t=" + t + "&sign=" + sign;

        String intf = "";
        if (isCancel) {
            intf = "mix_streamv2.cancel_mix_stream";
        } else {
            intf = "mix_streamv2.start_mix_stream_advanced";
        }

        String channid1 = "39191_" + MD5Utils.encode(roomId + "_" + user1 + "_main");
        String channid2 = "39191_" + MD5Utils.encode(roomId + "_" + user2 + "_main");

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonObject2.put("app_id", 1255423799);
        jsonObject2.put("interface", intf);
        jsonObject2.put("mix_stream_session_id", orderId);
        jsonObject2.put("mix_stream_template_id", 390);
        jsonObject2.put("output_stream_id", orderId);
        jsonObject2.put("output_stream_type", 1);

        if (!isCancel) {
            JSONObject j11 = new JSONObject();
            JSONObject j12 = new JSONObject();
            //image_layer为1的作为混流的背景
            //若画布不指定宽高, 则使用image_layer最小的流的宽高，作为画布的宽高
            j11.put("input_stream_id", "canvas1");
            j12.put("image_layer", 1);
            j12.put("input_type", 3);
            j12.put("color", "0x000000");
            j11.put("layout_params", j12);

            JSONObject j21 = new JSONObject();
            JSONObject j22 = new JSONObject();
            j21.put("input_stream_id", channid1);
            j22.put("image_layer", 2);
            j21.put("layout_params", j22);

            JSONObject j31 = new JSONObject();
            JSONObject j32 = new JSONObject();
            j31.put("input_stream_id", channid2);
            j32.put("image_layer", 3);
            j31.put("layout_params", j32);

            jsonArray.add(j11);
            jsonArray.add(j21);
            jsonArray.add(j31);

            jsonObject2.put("input_stream_list", jsonArray);
        }

        jsonObject1.put("interfaceName", "Mix_StreamV2");
        jsonObject1.put("para", jsonObject2);

        jsonObject.put("timestamp", TimeUtils.getCurrentTimeInLong() + "");
        jsonObject.put("eventId", TimeUtils.getCurrentTimeInLong() + "");
        jsonObject.put("interface", jsonObject1);

        Log.e("jsonObject == ", jsonObject.toString());
        Map<String, Object> params = JSONObject.parseObject(jsonObject.toJSONString(), new TypeReference<Map<String, Object>>() {
        });
        mHttpHelper.postraw(url, jsonObject.toJSONString(), new SpotsCallBack<MixStreamBean>(this, "hhh") {
            @Override
            public void onSuccess(Response response, MixStreamBean mixStreamBean) {
                if (mixStreamBean != null) {
                    if (mixStreamBean.getCode() >= 0) {
                        if (isCancel) {
                            Log.e("mix ==== ", "取消混流成功");
                        } else {
                            Log.e("mix ==== ", "添加混流成功");
                        }
                    } else {
                        Log.e("mix ==== ", "混流失败" + mixStreamBean.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 处理字幕，只显示最后的两行
     *
     * @param tv
     */
    private void dealStr(final TextView tv) {
        ViewTreeObserver observer = tv.getViewTreeObserver(); //tv为TextView控件
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout layout = tv.getLayout();
                int line = tv.getLineCount();
                String text = tv.getText().toString();
                if (line > 2) {
                    int start = layout.getLineStart(line - 1);
                    int end = layout.getLineEnd(line - 1);
                    int start1 = layout.getLineStart(line - 2);
                    int end1 = layout.getLineEnd(line - 2);
                    String re = text.substring(start, end);
                    String re1 = text.substring(start1, end1);
                    tv.setText(re1 + re);
                }
            }
        });
    }
}
