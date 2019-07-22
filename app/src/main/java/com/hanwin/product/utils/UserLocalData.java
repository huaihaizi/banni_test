package com.hanwin.product.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.hanwin.product.User;
import com.hanwin.product.viewutils.ProvinceBean;

import java.util.ArrayList;
import java.util.List;


public class UserLocalData {
    public static void putUser(Context context, User user){
        String user_json =  JSONUtil.toJSON(user);
        PreferencesUtils.putString(context, Contants.USER_JSON,user_json);
    }

    public static void putToken(Context context, String token){
        PreferencesUtils.putString(context, Contants.TOKEN,token);
    }

    public static User getUser(Context context){
        String user_json= PreferencesUtils.getString(context,Contants.USER_JSON);
        if(!TextUtils.isEmpty(user_json)){
            return  JSONUtil.fromJson(user_json,User.class);
        }
        return  new User();
    }

    public static String getToken(Context context){
        return  PreferencesUtils.getString( context,Contants.TOKEN);
    }

    public static void clearUser(Context context){
        PreferencesUtils.putString(context, Contants.USER_JSON,"");
    }

    public static void clearToken(Context context){
        PreferencesUtils.putString(context, Contants.TOKEN,"");
    }

    public static void clearScreeObject(Context context){
        PreferencesUtils.putString(context, Contants.PROPERTY_JSON,"");
    }

    /**
     * 存省市区
     * @param context
     * @param provincelist
     */
    public static void setProvince(Context context, List<ProvinceBean> provincelist){
        String provinceJson =  JSONUtil.toJSON(provincelist);
        PreferencesUtils.putString(context, "PROVINCE",provinceJson);
    }

    /**
     * 取省市区
     * @param context
     * @return
     */
    public static List<ProvinceBean> getProvince(Context context){
        String provinceJson= PreferencesUtils.getString(context,"PROVINCE");
        if(!TextUtils.isEmpty(provinceJson)){
            return  JSONUtil.fromJson(provinceJson, new TypeToken<List<ProvinceBean>>() {
            }.getType());
        }
        return  new ArrayList<>();
    }
}
