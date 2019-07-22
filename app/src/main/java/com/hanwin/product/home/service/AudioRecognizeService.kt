package com.hanwin.product.home.service

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.hanwin.product.listener.AudioRecognizeListener
import com.hanwin.product.utils.Contants
import com.tencent.aai.AAIClient
import com.tencent.aai.audio.data.AudioRecordDataSource
import com.tencent.aai.auth.LocalCredentialProvider
import com.tencent.aai.config.ClientConfiguration
import com.tencent.aai.exception.ClientException
import com.tencent.aai.exception.ServerException
import com.tencent.aai.listener.AudioRecognizeResultListener
import com.tencent.aai.listener.AudioRecognizeStateListener
import com.tencent.aai.model.AudioRecognizeRequest
import com.tencent.aai.model.AudioRecognizeResult
import com.tencent.aai.model.type.AudioRecognizeConfiguration
import com.tencent.aai.model.type.AudioRecognizeTemplate

/**
 * TODO:语音识别服务
 * @acthor weiang
 * 2019/7/5 6:29 PM
 */
class AudioRecognizeService : Service() {
    var aaiClient: AAIClient? = null
    var audioRecognizeRequest: AudioRecognizeRequest? = null
    //实时翻译监听
    var recognizeListener: AudioRecognizeListener? = null

    var audioRecognizeConfiguration: AudioRecognizeConfiguration? = null

    var audioRecognizeResultListener: AudioRecognizeResultListener? = null

    var audioRecognizeStateListener: AudioRecognizeStateListener? = null

    var isAudioRecognizeStart = false

    lateinit var activity: Activity

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    fun setAudioRecognizeListener(listener: AudioRecognizeListener?) {
        recognizeListener = listener
    }

    /**
     * TODO:初始化语音识别
     * @acthor weiang
     * 2019/7/8 11:28 AM
     */
    public fun initVoiceRecognition(activity: Activity) {
        // 为了方便用户测试，sdk提供了本地签名，但是为了secretKey的安全性，正式环境下请自行在第三方服务器上生成签名。
        val credentialProvider = LocalCredentialProvider(Contants.secretKey)
        // 1、初始化AAIClient对象。
        aaiClient = AAIClient(activity, Contants.appid, Contants.projectid, Contants.secretId, credentialProvider)
        // 2、初始化语音识别请求。
        // 用户配置
        ClientConfiguration.setServerProtocolHttps(false) // 是否启用https，默认启用
        ClientConfiguration.setMaxAudioRecognizeConcurrentNumber(2) // 语音识别的请求的最大并发数
        ClientConfiguration.setMaxRecognizeSliceConcurrentNumber(10) // 单个
        audioRecognizeRequest = AudioRecognizeRequest.Builder()
                .pcmAudioDataSource(AudioRecordDataSource()) // 设置语音源为麦克风输入
                //.templateName(templateName) // 设置模板
                .template(AudioRecognizeTemplate(1, 0, 0)) // 设置自定义模板
                .build()
        // 自定义识别配置
        audioRecognizeConfiguration = AudioRecognizeConfiguration.Builder()
                .enableAudioStartTimeout(false) // 是否使能起点超时停止录音
                .enableAudioEndTimeout(false) // 是否使能终点超时停止录音
                .enableSilentDetect(true) // 是否使能静音检测，true表示不检查静音部分
                .minAudioFlowSilenceTime(1000) // 语音流识别时的间隔时间
                .maxAudioFlowSilenceTime(10000) // 语音终点超时时间
                .maxAudioStartSilenceTime(10000) // 语音起点超时时间
                .minVolumeCallbackTime(80) // 音量回调时间
                .sensitive(2f)
                .build()

        // 3、初始化语音识别结果监听器。
        audioRecognizeResultListener = object : AudioRecognizeResultListener {

            override fun onSliceSuccess(audioRecognizeRequest: AudioRecognizeRequest, audioRecognizeResult: AudioRecognizeResult, i: Int) {
                // 返回语音分片的识别结果
                Log.d("onSuccess", "1 " + i + "VoiceId == " + audioRecognizeResult.voiceId + "text == " + audioRecognizeResult.text)
                if (recognizeListener != null && audioRecognizeResult != null && !TextUtils.isEmpty(audioRecognizeResult.text)) {
                    recognizeListener?.onSliceSuccess(audioRecognizeResult.text, audioRecognizeResult.voiceId)
                }
            }

            override fun onSegmentSuccess(audioRecognizeRequest: AudioRecognizeRequest, audioRecognizeResult: AudioRecognizeResult, i: Int) {
                // 返回语音流的识别结果
                Log.d("onSuccess", "2 " + i + "VoiceId == " + audioRecognizeResult.voiceId + "text == " + audioRecognizeResult.text)
                if (recognizeListener != null && audioRecognizeResult != null && !TextUtils.isEmpty(audioRecognizeResult.text)) {
                    recognizeListener?.onSegmentSuccess(audioRecognizeResult.text, audioRecognizeResult.voiceId)
                }

            }

            override fun onSuccess(audioRecognizeRequest: AudioRecognizeRequest?, s: String?) {

            }

            override fun onFailure(audioRecognizeRequest: AudioRecognizeRequest?, e: ClientException, e1: ServerException?) {
                Log.d("onSuccess", "onError" + "VoiceId == " + e.message + "text == " + e1?.message)

            }
        }

        audioRecognizeStateListener = object : AudioRecognizeStateListener {
            override fun onVoiceFlowStart(p0: AudioRecognizeRequest?, p1: Int) {
                //Log.d("onSuccess", "---------onVoiceFlowStart------------>:")
            }

            override fun onVoiceFlowFinish(p0: AudioRecognizeRequest?, p1: Int) {
                //Log.d("onSuccess", "---------onVoiceFlowFinish------------>:")
            }

            override fun onVoiceFlowFinishRecognize(p0: AudioRecognizeRequest?, p1: Int) {
                //Log.d("onSuccess", "---------onVoiceFlowFinishRecognize------------>:")
            }

            override fun onVoiceVolume(p0: AudioRecognizeRequest?, p1: Int) {
                //Log.d("onSuccess", "---------onVoiceVolume------------>:")
            }

            override fun onVoiceFlowStartRecognize(p0: AudioRecognizeRequest?, p1: Int) {
                //Log.d("onSuccess", "---------onVoiceFlowStartRecognize------------>:")
            }

            override fun onStartRecord(p0: AudioRecognizeRequest?) {
                Log.d("onSuccess", "---------onStartRecord------------>:")
            }

            override fun onStopRecord(p0: AudioRecognizeRequest?) {
                Log.d("onSuccess", "---------onStopRecord------------>:")
                if (isAudioRecognizeStart) {
                    startRecognize(activity)
                }
            }
        }
    }


    /**
     * TODO:开始语音转换
     * @acthor weiang
     * 2019/7/8 11:51 AM
     */
    fun startRecognize(activity: Activity) {
        this.activity = activity
        initVoiceRecognition(activity)
        isAudioRecognizeStart = true
        // 4、启动语音识别
        Thread(Runnable {
            if (aaiClient != null) {
                aaiClient?.startAudioRecognize(audioRecognizeRequest, audioRecognizeResultListener, audioRecognizeStateListener, null, null)
            }
        }).start()
    }

    /**
     * TODO:暂停语音转换
     * @acthor weiang
     * 2019/7/8 11:51 AM
     */
    fun stopRecognize() {
        isAudioRecognizeStart = false
        // 1、获得请求的id
        if (audioRecognizeRequest != null) {
            val requestId = audioRecognizeRequest?.getRequestId()
            // 2、调用cancel方法
            if (aaiClient != null) {
                aaiClient?.stopAudioRecognize(requestId!!)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        isAudioRecognizeStart = false
        if (aaiClient != null) {
            val requestId = audioRecognizeRequest?.getRequestId()
            aaiClient?.cancelAudioRecognize(requestId!!)
            aaiClient?.release()
        }
    }

}
