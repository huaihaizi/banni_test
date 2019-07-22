package com.hanwin.product.utils;

import android.util.Log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;

/**
 * TODO:腾讯语音合成签名生成工具类
 *
 * @acthor weiang
 * 2019/7/16 11:02 AM
 */
public class TencentCloudAPITC3 {
    private final static Charset UTF8 = StandardCharsets.UTF_8;
    private final static String SECRET_ID = "AKIDCOK86BB1oX6TzFuJe1kWuXI7n10IlX2y";
    private final static String SECRET_KEY = "hFaVL1q1IZkFbhk06LPXHNlzepVLKxYy";
    private final static String CT_JSON = "application/json; charset=utf-8";

    /**
     * TODO:获取签名
     *
     * @param params
     * @acthor weiang
     * 2019/7/16 11:05 AM
     */
    public static String getSignature(TreeMap<String, String> params) {
        String signature = "";
        try {
            signature = sign(getStringToSign(params), SECRET_KEY, "HmacSHA1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }


    public static String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(UTF8), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes(UTF8));
        //DatatypeConverter.printBase64Binary(hash)
        //String signature =  DatatypeConverter.printBase64Binary(hash);
        String signature = android.util.Base64.encodeToString(hash, 16);
        Log.d("hhh", "-------------signature-------------->:" + signature);
        signature = signature.replaceAll("\n", "");
        return signature;
    }

//    public static byte[] hmac256(byte[] key, String msg) throws Exception {
//        Mac mac = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
//        mac.init(secretKeySpec);
//        return mac.doFinal(msg.getBytes(UTF8));
//    }
//
//    public static String sha256Hex(String s) throws Exception {
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        byte[] d = md.digest(s.getBytes(UTF8));
//        return DatatypeConverter.printHexBinary(d).toLowerCase();
//    }

    public static String getStringToSign(TreeMap<String, String> params) {
        StringBuilder s2s = new StringBuilder("GETcvm.tencentcloudapi.com/?");
        // 签名时要求对参数进行字典排序，此处用TreeMap保证顺序
        for (String k : params.keySet()) {
            s2s.append(k).append("=").append(params.get(k).toString()).append("&");
        }
        return s2s.toString().substring(0, s2s.length() - 1);
    }


//    public static String getUrl(TreeMap<String, String> params) throws UnsupportedEncodingException {
//        StringBuilder url = new StringBuilder("https://cvm.tencentcloudapi.com/?");
//        // 实际请求的url中对参数顺序没有要求
//        for (String k : params.keySet()) {
//            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
//            url.append(k).append("=").append(URLEncoder.encode(params.get(k).toString(), String.valueOf(UTF8))).append("&");
//        }
//        return url.toString().substring(0, url.length() - 1);
//    }

}
