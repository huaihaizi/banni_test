package com.hanwin.product.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.OkHttpUtils;
import com.tencent.liteav.demo.httputils.StringCallback;
import com.tencent.qcloudtts.RealtimeTTS.RealtimeTtsController;
import com.tencent.qcloudtts.callback.QCloudPlayerCallback;
import com.tencent.qcloudtts.exception.TtsNotInitializedException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.jpush.android.api.JPushInterface;

/**
 * TODO:应用全局工具类
 *
 * @acthor weiang
 * 2019/6/17 5:14 PM
 */
public class AppUtils {

    //普通用户
    public static final int PUSH_TAG_USER_TYPE = 101;
    //手语师
    public static final int PUSH_TAG_CONSTANTOR_TYPE = 102;
    //游客
    public static final int PUSH_TAG_AUDI_TYPE = 103;

    private static final String SERVER_URL = "https://aai.cloud.tencent.com/tts";
    private static final String DOMAIN_NAME = "aai.cloud.tencent.com/tts";
    private static final String SECRET_ID = "AKIDCOK86BB1oX6TzFuJe1kWuXI7n10IlX2y";
    private static final String SECRET_KEY = "hFaVL1q1IZkFbhk06LPXHNlzepVLKxYy";

    /**
     * TODO:判断登录
     *
     * @acthor weiang
     * 2019/6/17 6:00 PM
     */
    public static boolean isLogin() {
        boolean result = false;
        if (BaseApplication.getInstance() != null && BaseApplication.getInstance().getUser() != null && !TextUtils.isEmpty(BaseApplication.getInstance().getUser().getSessionToken())) {
            result = true;
        }
        return result;
    }

    /**
     * TODO:腾讯语音合成
     *
     * @acthor weiang
     * 2019/7/12 1:58 PM
     */
    public static void tencentSpeek(String contentText, QCloudPlayerCallback qCloudPlayerCallback) {
        try {
            BaseApplication.controller.startTts(contentText, null, qCloudPlayerCallback);
        } catch (TtsNotInitializedException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO:腾讯语音合成
     * @acthor weiang
     * 2019/7/12 1:58 PM
     */
    public static void tencentStopSpeek() {
        try {
            BaseApplication.controller.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * TODO:百度语音合成
     *
     * @acthor weiang
     * 2019/7/9 5:02 PM
     */
    public static void baiduSpeek(String contentText, SpeechSynthesizerListener speechSynthesizerListener) {
        BaseApplication.mSpeechSynthesizer.setSpeechSynthesizerListener(speechSynthesizerListener);
        BaseApplication.mSpeechSynthesizer.speak(contentText);
    }

    /**
     * TODO:判断是否是手语师
     *
     * @acthor weiang
     * 2019/6/17 6:00 PM
     */
    public static boolean isSignLanguageTeacher() {
        boolean result = false;
        if (BaseApplication.getInstance() != null && BaseApplication.getInstance().getUser() != null && !TextUtils.isEmpty(BaseApplication.getInstance().getUser().getRole())) {
            result = "signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole());
        }
        return result;
    }

    /**
     * TODO:获取用ID
     *
     * @acthor weiang
     * 2019/6/18 10:15 AM
     */
    public static String getUserId() {
        String userId = "";
        if (BaseApplication.getInstance() != null && BaseApplication.getInstance().getUser() != null && !TextUtils.isEmpty(BaseApplication.getInstance().getUser().getUserId())) {
            userId = BaseApplication.getInstance().getUser().getUserId();
        }
        return userId;
    }


    /**
     * TODO:获取用户名
     *
     * @acthor weiang
     * 2019/6/18 10:15 AM
     */
    public static String getUserName() {
        String userName = "";
        if (BaseApplication.getInstance() != null && BaseApplication.getInstance().getUser() != null && !TextUtils.isEmpty(BaseApplication.getInstance().getUser().getUserName())) {
            userName = BaseApplication.getInstance().getUser().getUserName();
        }
        return userName;
    }


    /**
     * TODO:设置极光推送tag
     *
     * @acthor weiang
     * 2019/6/17 6:01 PM
     */
    public static void setJiPushTags(Context context) {
        String tag = "";
        if (isSignLanguageTeacher()) {
            tag = Contants.PUSH_TAG_BANNI_CONSTANTOR;
        } else {
            tag = Contants.PUSH_TAG_BANNI_USER;
        }
        Set<String> tagSet = new TreeSet<>();
        tagSet.add(tag);
        JPushInterface.setAliasAndTags(context, "", null, null);
        JPushInterface.setAlias(context, 0, "banni_" + getUserId());
        JPushInterface.setTags(context, 0, tagSet);
    }

    /**
     * TODO：弹出键盘
     *
     * @acthor weiang
     * 2019/7/11 1:58 PM
     */
    public static void showKeyboard(Context context, EditText editText) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO：关闭键盘
     *
     * @acthor weiang
     * 2019/7/11 1:58 PM
     */
    public static void closeKeyboard(Activity context, EditText editText) {
        try {
            View view = context.getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //腾讯云语音识别
    private static final class WorkingThread extends Thread {
        private TreeMap<String, String> requestMap;
        private String mRequestBody;

        WorkingThread(TreeMap<String, String> requestMap) {
            this.requestMap = requestMap;
            mRequestBody = "{\n" +
                    "\t\"Action\":\"" + requestMap.get("Action") + "\",\n" +
                    "\t\"Codec\":\"" + requestMap.get("Codec") + "\",\n" +
                    "\t\"Text\":\"" + requestMap.get("Text") + "\",\n" +
                    "\t\"SessionId\":\"" + requestMap.get("SessionId") + "\",\n" +
                    "\t\"Timestamp\":" + requestMap.get("Timestamp") + ",\n" +
                    "\t\"Expired\":" + requestMap.get("Expired") + ",\n" +
                    "\t\"SecretId\":\"" + requestMap.get("SecretId") + "\",\n" +
                    "\t\"AppId\":" + requestMap.get("AppId") + "\n" +
                    "}";
            System.out.println(mRequestBody);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            super.run();
            InputStream inputStream = null;
            try {
                inputStream = obtainResponseStreamWithJava(mRequestBody, requestMap);
                processProtocolBufferStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private static InputStream obtainResponseStreamWithJava(String postJsonBody, TreeMap<String, String> requestMap) throws IOException {
        //发送POST请求
        URL url = new URL(SERVER_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String authorization = generateSign(requestMap);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authorization);
        conn.connect();
        OutputStream out = conn.getOutputStream();
        out.write(postJsonBody.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
        return conn.getInputStream();
    }


    private static String generateSign(TreeMap<String, String> params) {
        String paramStr = "POST" + DOMAIN_NAME + "?";
        StringBuilder builder = new StringBuilder(paramStr);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(String.format(Locale.CHINESE, "%s=%s", entry.getKey(), String.valueOf(entry.getValue())))
                    .append("&");
        }
        //去掉最后一个&
        builder.deleteCharAt(builder.lastIndexOf("&"));
        String sign = "";
        String source = builder.toString();
        System.out.println(source);
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA1");
            mac.init(keySpec);
            mac.update(source.getBytes());
            sign = Base64.getEncoder().encodeToString(mac.doFinal());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println("生成签名串：" + sign);
        return sign;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void processProtocolBufferStream(final InputStream inputStream) throws FileNotFoundException {
        AudioTrack audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder().setSampleRate(22050)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                1024,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();
        File file = newFile(FileUtil.getAdtoSdPath() + "/voice/", "test.pcm");
        FileOutputStream fos = new FileOutputStream(file, true);
        boolean fillSuccess;
        while (true) {
            byte[] pcmData = new byte[1024];
            try {
                fillSuccess = fill(inputStream, pcmData);
                fos.write(pcmData);
                audioTrack.write(pcmData, 0, pcmData.length);
                if (!fillSuccess) {
                    fos.flush();
                    fos.close();
                    audioTrack.stop();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从 InputStream 读取内容到 buffer, 直到 buffer 填满
     *
     * @return 如果 InputStream 内容不足以填满 buffer, 则返回 false.
     * @throws IOException 可能抛出的异常
     */
    private static boolean fill(InputStream in, byte[] buffer) throws IOException {
        int length = buffer.length;
        int hasRead = 0;
        while (true) {
            int offset = hasRead;
            int count = length - hasRead;
            int currentRead = in.read(buffer, offset, count);
            if (currentRead >= 0) {
                hasRead += currentRead;
                if (hasRead == length) {
                    return true;
                }
            }
            if (currentRead == -1) {
                return false;
            }
        }

    }

    public static File newFile(String filePath, String fileName) {
        if (filePath == null || filePath.length() == 0
                || fileName == null || fileName.length() == 0) {
            return null;
        }
        try {
            //判断目录是否存在，如果不存在，递归创建目录
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //组织文件路径
            StringBuilder sbFile = new StringBuilder(filePath);
            if (!filePath.endsWith("/")) {
                sbFile.append("/");
            }
            sbFile.append(fileName);

            //创建文件并返回文件对象
            File file = new File(sbFile.toString());
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
