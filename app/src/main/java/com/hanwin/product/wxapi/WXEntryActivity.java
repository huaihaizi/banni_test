package com.hanwin.product.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.activity.LoginActivity;
import com.hanwin.product.home.activity.ThirdLoginActivity;
import com.hanwin.product.home.bean.LoginBean;
import com.hanwin.product.home.bean.LoginMsgBean;
import com.hanwin.product.tencentim.event.RegisterUtils;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.tencentim.view.ILoginView;
import com.hanwin.product.utils.ActivityManager;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.NetworkUtil;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.DialogUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by zhaopf on 2019/5/20.
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler, ILoginView {
    public static final String TAG = WXEntryActivity.class.getSimpleName();
    public static String code;
    private LoginHelper loginHelper;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginHelper = new LoginHelper(this);
        try {
            Intent intent = getIntent();
            BaseApplication.api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        BaseApplication.api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                SendAuth.Resp authResp = (SendAuth.Resp) resp;
                String code = authResp.code;
                Map<String, Object> params = new HashMap<>();
                params.put("code", code);
                send(params);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消";
                ToastUtils.show(BaseApplication.getInstance(), "用户取消授权");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "拒绝";
                ToastUtils.show(BaseApplication.getInstance(), "用户拒绝授权");
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "未知错误";
                finish();
                break;
            default:
                result = "发送返回";
                finish();
                break;
        }
    }

    private void send(Map<String, Object> params) {
        dialogUtil = new DialogUtil();
        dialogUtil.showLoadingDialog(this, "");
        mHttpHelper.post(Contants.BASE_URL + "sign_language/wxchatLogin", params, new SpotsCallBack<LoginMsgBean>(this, "msg") {
            @Override
            public void onSuccess(Response response, LoginMsgBean loginMsgBean) {
                if (loginMsgBean != null) {
                    if (loginMsgBean.getCode() >= 0) {
                        user = loginMsgBean.getData();
                        login(user.getUid(), user.getSignature());
                    } else {
                        dialogUtil.dialogLoading.dismiss();
                        ToastUtils.show(WXEntryActivity.this, loginMsgBean.getMsg());
                    }
                }
            }
        });
    }


    /**
     * 登录腾讯im
     *
     * @param uid
     * @param sig
     */
    private void login(String uid, String sig) {
        loginHelper.loginSDK(uid, sig);
    }

    @Override
    public void onLoginSuccess() {
        dialogUtil.dialogLoading.dismiss();
        Log.e("登录成功 ==== ", "ok");
        ToastUtils.show(this, "登录成功");
        BaseApplication.getInstance().putUser(user, user.getSessionToken());
        RegisterUtils.initPushMessage();
        finish();
        if (!Contants.isActivtiesLogin) {
            MainActivity.start(this, null);
        } else {
            ActivityManager.getInstance().finsihActivity("ThirdLoginActivity");
        }
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        dialogUtil.dialogLoading.dismiss();
        Log.e("登录失败 ==== ", errCode + "   " + errMsg);
        ToastUtils.show(this, "登录失败");
    }
}
