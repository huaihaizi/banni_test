package com.hanwin.product.tencentim.presenter;

import com.hanwin.product.tencentim.view.ILoginView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;

/**
 *  登录模块
 * Created by zhaopf on 2018/11/8.
 */

public class LoginHelper {
    private ILoginView loginView;

    public LoginHelper(ILoginView view){
        loginView = view;
    }

    //登录
    public void loginSDK(String userId, String userSig){
        TIMManager.getInstance().login(userId, userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                loginView.onLoginFailed("login", code, desc);
            }

            @Override
            public void onSuccess() {
                loginView.onLoginSuccess();
            }
        });
    }

    //登出
    public void logout(){
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                loginView.onLoginFailed("login", code, desc);
            }

            @Override
            public void onSuccess() {
                //登出成功
                loginView.onLoginSuccess();
            }
        });
    }
}
