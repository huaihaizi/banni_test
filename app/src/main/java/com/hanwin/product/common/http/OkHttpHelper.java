package com.hanwin.product.common.http;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.utils.Contants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpHelper {

    public static final int TOKEN_MISSING = 401;// token 丢失
    public static final int TOKEN_ERROR = 402; // token 错误
    public static final int TOKEN_EXPIRE = 403; // token 过期


    public static final String TAG = "OkHttpHelper";

    private static OkHttpHelper mInstance;
    private OkHttpClient mHttpClient;
    private Gson mGson;

    private Handler mHandler;

    static {
        mInstance = new OkHttpHelper();
    }

    public OkHttpHelper() {

        mHttpClient = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        mGson = new Gson();
//        mGson = new GsonBuilder().registerTypeAdapterFactory(new DateNullAdapterFactory<>()).create();
        mHandler = new Handler(Looper.getMainLooper());

    }

    public static OkHttpHelper getInstance() {
        return mInstance;
    }


    public void get(String url, Map<String, Object> param, BaseCallback callback) {
        Request request = buildGetRequest(url, param);
        request(request, callback);
    }

    public void get(String url, BaseCallback callback) {
        get(url, null, callback);
    }


    public void post(String url, Map<String, Object> param, BaseCallback callback) {
        Log.e("OkHttpHelper ==", "请求参数 ：" + mGson.toJson(param).toString());
        Request request = buildPostRequest(url, param);
        request(request, callback);
    }

    public void postraw(String url, String json, BaseCallback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.e("OkHttpHelper ==", "请求参数 ：" + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        request(request, callback);
    }

    public void postFile(String url, Map<String, String> map, File file, BaseCallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        String TYPE = "application/octet-stream";
        RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
        RequestBody requestBody = builder.setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        request(request, callback);
    }

    public void request(final Request request, final BaseCallback callback) {
        callback.onBeforeRequest(request);
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(callback, request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callbackResponse(callback, response);
                if (response.isSuccessful()) {
                    String resultStr = response.body().string();
                    Log.e("OkHttpHelper ==", "resultStr返回数据： " +resultStr.toString());
                    if (callback.mType == String.class) {
                        callbackSuccess(callback, response, resultStr);
                    } else {
                        if (response.request().url().toString().contains(Contants.BASE_URL)) {
                            try {//尝试解析为基础数据
                                BaseRespMsg respMsg = mGson.fromJson(resultStr, BaseRespMsg.class);
                                if (respMsg != null) {
                                    if (respMsg.getCode() == BaseRespMsg.TO_LOGIN) {
//                                        Intent intent = new Intent(callback.mContext, LoginActivity.class);
//                                        callback.mContext.startActivity(intent);
//                                        BaseApplication.getInstance().setToken("");
//                                        Intent intent1 = new Intent();
//                                        intent1.setAction("is_exit_app");
//                                        callback.mContext.sendBroadcast(intent1);
                                    } else {
                                        Object obj = mGson.fromJson(resultStr, callback.mType);
                                        callbackSuccess(callback, response, obj);
                                    }
                                    return;
                                }
                            } catch (IllegalStateException e) {
                                callback.onError(response, response.code(), e);
                            }
                        }
                        try {
                            Object obj = mGson.fromJson(resultStr, callback.mType);
                            callbackSuccess(callback, response, obj);
                        } catch (com.google.gson.JsonParseException e) { // Json解析的错误
                            callback.onError(response, response.code(), e);
                        } catch (Exception e) {
                            callback.onError(response, response.code(), e);
                        }
                    }
                } else if (response.code() == TOKEN_ERROR || response.code() == TOKEN_EXPIRE || response.code() == TOKEN_MISSING) {
                    callbackTokenError(callback, response);
                } else {
                    callbackError(callback, response, null);
                }
            }
        });
    }


    private void callbackTokenError(final BaseCallback callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onTokenError(response, response.code());
            }
        });
    }

    private void callbackSuccess(final BaseCallback callback, final Response response, final Object obj) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, obj);
            }
        });
    }


    private void callbackError(final BaseCallback callback, final Response response, final Exception e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, response.code(), e);
            }
        });
    }


    private void callbackFailure(final BaseCallback callback, final Request request, final IOException e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }


    private void callbackResponse(final BaseCallback callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }


    private Request buildPostRequest(String url, Map<String, Object> params) {

        return buildRequest(url, HttpMethodType.POST, params);
    }

    private Request buildGetRequest(String url, Map<String, Object> param) {

        return buildRequest(url, HttpMethodType.GET, param);
    }

    private Request buildRequest(String url, HttpMethodType methodType, Map<String, Object> params) {
        Request.Builder builder = new Request.Builder().url(url);
        if (methodType == HttpMethodType.POST) {
            RequestBody body = builderFormData(params);
            builder.post(body);
        } else if (methodType == HttpMethodType.GET) {
            url = buildUrlParams(url, params);
            builder.url(url);
            builder.get();
        }
        return builder.build();
    }


    private String buildUrlParams(String url, Map<String, Object> params) {

        if (params == null)
            params = new HashMap<>(1);
//获取token
        String token = BaseApplication.getInstance().getToken();
        if (!TextUtils.isEmpty(token))
            params.put("token", token);


        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + (entry.getValue() == null ? "" : entry.getValue().toString()));
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }

        if (url.indexOf("?") > 0) {
            url = url + "&" + s;
        } else {
            url = url + "?" + s;
        }

        return url;
    }

    private RequestBody builderFormData(Map<String, Object> params) {


//        FormEncodingBuilder builder = new FormEncodingBuilder();
        FormBody.Builder builder = new FormBody.Builder();


        if (params != null) {


            for (Map.Entry<String, Object> entry : params.entrySet()) {

                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            //获取token

//            String token = BaseApplication.getInstance().getToken();
//            if (!TextUtils.isEmpty(token))
//                builder.add("token", token);
        }

        return builder.build();

    }

    enum HttpMethodType {

        GET,
        POST,

    }


}
