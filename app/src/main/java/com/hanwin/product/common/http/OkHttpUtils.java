package com.hanwin.product.common.http;

import com.hanwin.product.utils.AppUtils;
import com.hanwin.product.utils.TencentCloudAPITC3;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;

import java.util.Map;
import java.util.TreeMap;

/**
 * TODO 网络请求封装
 *
 * @acthor weiang
 * 2019/3/18 3:03 PM
 */
public class OkHttpUtils {

    //post请求方法
    public static void post(String tag, String url, Map<String, String> map, AbsCallback callback) {
        OkGo.post(getUrl(url)).params(map)
                .tag(tag)
                .headers(getTencentHeaders(map))
                .execute(callback);
    }

    //get请求方法
    public static void get(String tag, String url, Map<String, String> map, AbsCallback callback) {
        OkGo.get(getUrl(url)).params(map)
                .tag(tag)
                .headers(getTencentHeaders(map))
                .execute(callback);
    }

    /**
     * url拼接
     */
    private static String getUrl(String url) {
        //return ApiConstants.BASE_URL + url;
        return url;
    }

    /**
     * TODO:设置header
     *
     * @acthor weiang
     * 2019/4/26 11:12 AM
     */
    private static HttpHeaders getTencentHeaders(Map<String, String> map) {
        HttpHeaders headers = new HttpHeaders();
        String signature = TencentCloudAPITC3.getSignature((TreeMap<String, String>) map);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization",signature);
        return headers;
    }


    /**
     * TODO:设置header
     *
     * @acthor weiang
     * 2019/4/26 11:12 AM
     */
    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();

//        "Content-Type":"application/json",
//                "Authorization":"HRCKlbwPhWtVvfGn914qE5O1rwc="
        headers.put("Content-Type", "application/json");
        headers.put("Authorization","HRCKlbwPhWtVvfGn914qE5O1rwc=");

        return headers;
    }

}
