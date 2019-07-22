package com.hanwin.product.home.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.utils.ActivityManager;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.wxapi.WXEntryActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ThirdLoginActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_right)
    TextView text_right;

    @Bind(R.id.check_imageview)
    ImageView check_imageview;
    @Bind(R.id.text_register_clause)
    TextView text_register_clause;

    @Bind(R.id.rel_wechat_login)
    RelativeLayout rel_wechat_login;

    private boolean isSelect = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_login);
        ButterKnife.bind(this);
        initView();
        initData();

    }

    private void initView() {
        text_right.setText("注册");
        int type = getIntent().getIntExtra("type",0);
        if(type == 1){
            Contants.isActivtiesLogin = true;
        }else{
            Contants.isActivtiesLogin = false;
        }
        text_register_clause.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        text_register_clause.getPaint().setAntiAlias(true);//抗锯齿
        if (!BaseApplication.api.isWXAppInstalled()) {
            rel_wechat_login.setVisibility(View.GONE);
        }
    }


    private void initData() {

    }


    /**
     * 微信登录
     * @param view
     */
    @OnClick(R.id.rel_wechat_login)
    public void wechatLogin(View view) {
        if(isSelect) {
            if (BaseApplication.api == null) {
                BaseApplication.api = WXAPIFactory.createWXAPI(this, BaseApplication.APP_ID, true);
            }
            if (!BaseApplication.api.isWXAppInstalled()) {
                ToastUtils.show(BaseApplication.getInstance(), "您手机尚未安装微信，请安装后再登录");
                return;
            }
            BaseApplication.api.registerApp(BaseApplication.APP_ID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
            BaseApplication.api.sendReq(req);
        }else{
            ToastUtils.show(this, "请阅读并勾选同意用户协议");
        }
    }

    /**
     * 现有账号登录
     * @param view
     */
    @OnClick(R.id.text_account_login)
    public void accountLogin(View view) {
        if(isSelect){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("item",0);
            startActivity(intent);
        }else{
            ToastUtils.show(this, "请阅读并勾选同意用户协议");
        }
    }

    /**
     * 注册
     * @param view
     */
    @OnClick(R.id.text_right)
    public void register(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("item",1);
        startActivity(intent);
    }

    @OnClick(R.id.check_imageview)
    public void check_imageview() {
        if (!isSelect) {
            isSelect = true;
            check_imageview.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_checked));
        } else {
            isSelect = false;
            check_imageview.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_normal));
        }
    }

    /**
     * 用户注册协议
     */
    @OnClick(R.id.text_register_clause)
    public void text_register_clause() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "用户注册协议");
        intent.putExtra("url", "https://jz.hanwin.com.cn/baseApp/bannixieyi.html");
        intent.putExtra("type", "0");
        startActivity(intent);
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        BaseApplication.getInstance().clearUser();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BaseApplication.getInstance().clearUser();
        finish();
    }
}
