package com.hanwin.product.home.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.BaseFragment;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.home.activity.ForgetPasswordActivity;
import com.hanwin.product.home.bean.LoginMsgBean;
import com.hanwin.product.tencentim.event.RegisterUtils;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.utils.AppUtils;
import com.hanwin.product.tencentim.view.ILoginView;
import com.hanwin.product.utils.ActivityManager;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.RegexUtil;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.DialogUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by zhaopf on 2018/6/23 0023.
 */

public class LoginFragment extends BaseFragment implements ILoginView {
    @Bind(R.id.edit_phone_number)
    EditText edit_phone_number;
    @Bind(R.id.edit_password)
    EditText edit_password;

    @Bind(R.id.text_forget_password)
    TextView text_forget_password;

    private LoginHelper loginHelper;
    private User user;
    private DialogUtil dialogUtil;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void init() {
        initView();
        initData();
    }

    private void initView() {
        text_forget_password.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        text_forget_password.getPaint().setAntiAlias(true);//抗锯齿
    }

    private void initData() {
        loginHelper = new LoginHelper(this);
    }

    @OnClick(R.id.text_forget_password)
    public void text_forget_password(){
        Intent intent = new Intent(getActivity(), ForgetPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    public void login(){
        String accoount = edit_phone_number.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        Map<String, Object> params = new HashMap<>();
        if (TextUtils.isEmpty(accoount)) {
            ToastUtils.show(getActivity(), "请输入手机号码");
        } else if (!RegexUtil.isMobileNO(accoount)) {
            ToastUtils.show(getActivity(), "请输入正确的手机号码");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.show(getActivity(), "请输入密码");
        } else {
            params.put("userName", accoount);
            params.put("password", password);
            params.put("jpushId", "");
            getCode(params);
        }
    }

    public void getCode(Map<String, Object> params) {
        dialogUtil = new DialogUtil();
        dialogUtil.showLoadingDialog(getActivity(),"");
        mHttpHelper.post(Contants.BASE_URL + "sign_language/tencent_login", params, new SpotsCallBack<LoginMsgBean>(getActivity(),"login") {
            @Override
            public void onSuccess(Response response, LoginMsgBean loginMsgBean) {
                if (loginMsgBean != null) {
                    if (loginMsgBean.getCode() >= 0) {
                        user = loginMsgBean.getData();
                        login(user.getUid(),user.getSignature());
                    } else {
                        dialogUtil.dialogLoading.dismiss();
                        ToastUtils.show(getActivity(), loginMsgBean.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 登录腾讯im
     * @param uid
     * @param sig
     */
    private void login( String uid, String sig){
        loginHelper.loginSDK(uid,sig);
//        loginHelper.loginSDK(Contants.userId,Contants.userSig);
    }

    @Override
    public void onLoginSuccess() {
        dialogUtil.dialogLoading.dismiss();
        Log.e("登录成功 ==== ","ok");
        ToastUtils.show(getActivity(), "登录成功");
        BaseApplication.getInstance().putUser(user,user.getSessionToken());
        RegisterUtils.initPushMessage();
          AppUtils.setJiPushTags(getContext());
        if(!Contants.isActivtiesLogin){
            MainActivity.start(getActivity(),null);
        }else{
            ActivityManager.getInstance().finsihActivity("ThirdLoginActivity");
        }
        getActivity().finish();
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        dialogUtil.dialogLoading.dismiss();
        Log.e("登录失败 ==== ",errCode + "   "+errMsg);
        ToastUtils.show(getActivity(), "登录失败");
    }
}
