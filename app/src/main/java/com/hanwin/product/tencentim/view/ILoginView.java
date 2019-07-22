package com.hanwin.product.tencentim.view;

/**
 * Created by zhaopf on 2018/11/8.
 */

public interface  ILoginView {

    // 登录成功
    void onLoginSuccess();

    // 登录失败
    void onLoginFailed(String module, int errCode, String errMsg);
}
