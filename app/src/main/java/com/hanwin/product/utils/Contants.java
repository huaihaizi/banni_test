package com.hanwin.product.utils;

import com.hanwin.product.tencentim.bean.TranslateWordBean;
import com.hanwin.product.viewutils.ProvinceBean;

import java.util.ArrayList;
import java.util.List;

public class Contants {

    public static final String USER_JSON = "user_json";
    public static final String TOKEN = "token";
    public static final String PROPERTY_JSON = "property_json";

//    public static String BASE_URL = "http://jz.hanwin.com.cn/";//生产环境

    //    public static String BASE_URL = "http://210.22.136.174:8888/";//外网测试接口地址
//    public static String BASE_URL = "http://192.168.20.33:9123/";//开发环境
    public static String BASE_URL = "http://47.103.64.77:9123/";//测试环境
//    public static String BASE_URL = "http://192.168.10.33:9123/";
//    public static String BASE_URL = "http://192.168.20.35:8888/baseProject/";//内网测试接口地址
//    public static String BASE_URL = "http://192.168.20.31:8888/baseProject/";//本机内网测试接口地址
//    public static String BASE_URL = "http://192.168.20.7:8888/baseProject/";//内网测试接口地址

//    public static String BASE_WEB_URL = "http://47.103.64.77:8032/";//webview基础地址

    public static String BASE_WEB_URL = "http://jz.hanwin.com.cn/";//webview基础地址

    //webview传递地址
    public static String TRANSMIT_WEB_URL = BASE_URL.substring(0, BASE_URL.length() - 1);

    public static String BASE_IMAGE = "https://oss-jz.oss-cn-beijing.aliyuncs.com/";//图片地址地址
    public static String JOIN_SIGN_LANGUAGE = "http://jz.hanwin.com.cn/SLTranslator/";//加入手语师
    public static String SKILL_INFO = "http://jz.hanwin.com.cn/join_consultant/skill-info.html";//技能信息
    public static String SERVICE_TIME = "http://jz.hanwin.com.cn/join_consultant/time-slot.html";//服务时间

    public static List<ProvinceBean> provinceList = new ArrayList<>();
    public static String tokenkey = "";

    //普通用户
    public static final String PUSH_TAG_BANNI_USER = "banni_user";
    //手语师
    public static final String PUSH_TAG_BANNI_CONSTANTOR = "banni_constantor";
    //游客
    public static final String PUSH_TAG_BANNI_AUDI = "banni_audi";

    //正式的
    public static int SDKAPPID = 1400158389;//Tencent 应用id
    public static int ACCOUNTTYPE = 36862;//Tencent

    //测试
//    public static int SDKAPPID = 1400166235;//Tencent 应用id
//    public static int ACCOUNTTYPE = 36862;//Tencent

    //语音
    public static int appid = 1255423799;
    public static int projectid = 0;
    public static String secretId = "AKIDCOK86BB1oX6TzFuJe1kWuXI7n10IlX2y";
    public static String secretKey = "hFaVL1q1IZkFbhk06LPXHNlzepVLKxYy";

    //设置APPID/AK/SK
    public static final String BAIDU_APP_ID = "16752068";
    public static final String BAIDU_API_KEY = "w5pufMahNW3jCwkB8h2MdpXh";
    public static final String BAIDU_SECRET_KEY = "09Qri7ySksGdG7qF0vSouoIGCLxhDCIu";

    public static List<TranslateWordBean> list = new ArrayList<>();//字幕暂存list
    public static boolean isInRoom = false;//是否在视频房间里
    public static boolean flag = false;//在非登录的情况下，然后登录后未实名认证设为true
    public static boolean isActivtiesLogin = false;//是否是活动界面跳转登录

}
